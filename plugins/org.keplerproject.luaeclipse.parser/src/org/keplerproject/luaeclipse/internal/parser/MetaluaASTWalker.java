/******************************************************************************
 * Copyright (c) 2009 KeplerProject, Sierra Wireless.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 *          - initial API and implementation and initial documentation
 *****************************************************************************/
package org.keplerproject.luaeclipse.internal.parser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.core.DLTKCore;
import org.keplerproject.luaeclipse.internal.parser.error.LuaParseError;
import org.keplerproject.luaeclipse.internal.parser.error.LuaParseErrorFactory;
import org.keplerproject.luaeclipse.metalua.Metalua;
import org.keplerproject.luaeclipse.parser.Activator;
import org.keplerproject.luaeclipse.parser.LuaExpressionConstants;
import org.keplerproject.luaeclipse.parser.ast.statements.LuaStatementConstants;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;

/**
 * All Lua tools for parsing Lua AST are available from here.
 * 
 * The aim of this plugin is to enable DLTK to access ASTs generated from
 * {@link Metalua}. In order to do so, when ASTs are build on Metalua's side,
 * they need to be converted in Java objects. Then, they could be parsed by
 * DLTK.
 * 
 * In Lua ASTs are recursive tables, in Java, they are supposed to be Objects.
 * Obviously they are no automatic conversion possible. That's why to convert
 * Lua ASTs in DLTK Java ones, walking Lua data to build Java objects is a
 * solution. Object instantiation is performed by {@link NodeFactory}, this just
 * gather Lua AST walking tooling.
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class MetaluaASTWalker implements LuaExpressionConstants,
		LuaStatementConstants {

	/** Instance of Lua, AST is fetched from this object */
	private LuaState _state;

	/**
	 * When retriving ID of child nodes, they must be sorted. That's why this
	 * object is for.
	 */
	private Comparator<Long> comparator;

	/** Lua source code to parse */
	private String source;

	/** Indicates if Metalua had problem while parsing code. */
	private LuaParseError _parseError = null;

	/**
	 * Instantiates a new Lua instance ready to parse.
	 * 
	 * More precisely, the constructor instantiate a {@link LuaState} loaded
	 * with {@link Metalua}. Furthermore, it loads as well a bunch of Lua
	 * functions. Most of the time, methods of the current classe just call
	 * those functions.
	 */
	private MetaluaASTWalker() {

		try {
			/*
			 * Define path to source file
			 */

			// Make sure that file is available on disk
			URL url = Platform.getBundle(Activator.PLUGIN_ID).getEntry(
					"/scripts/ast_to_table.mlua");

			// Retrieve absolute URI of file
			String path = new File(FileLocator.toFileURL(url).getFile())
					.getPath();

			/*
			 * Generate a new instance of Lua, in order to avoid several files
			 * using the same stack
			 */
			setState(Metalua.newState());

			// Run file
			getState().LdoFile(path);
		} catch (IOException e) {
			Activator.log(e);
		} catch (LuaException e) {
			Activator.log(e);
		}
		// Implement comparator in order to be able to sort child node IDs
		this.comparator = new Comparator<Long>() {
			public int compare(Long l1, Long l2) {
				return l1.compareTo(l2);
			}

			@Override
			@SuppressWarnings("unchecked")
			public boolean equals(Object obj) {
				if (obj instanceof Comparator<?>) {
					Comparator<Long> comparator = (Comparator<Long>) obj;
					return this.compare(Long.valueOf(2), Long.valueOf(3)) == comparator
							.compare(Long.valueOf(2), Long.valueOf(3));
				}
				return false;
			}
		};
	}

	/**
	 * Instantiates a new node factory helper.
	 * 
	 * @param source
	 *            the source
	 */
	public MetaluaASTWalker(final String source) {
		this();

		// Bear source in mind
		this.source = source;

		// Detect syntax errors, parse code
		if (this.parse()) {
			// Index AST made when there are no errors
			index();
		}
	}

	/**
	 * Instantiates a new Lua instance, then loads a source file.
	 * 
	 * @param path
	 *            Absolute path to source file. Lua and Metalua sources only,
	 *            not any kind of pre compiled chunks.
	 */
	public MetaluaASTWalker(final File path) {
		this();
		// TODO: Test me
		String statement = "ast = mlc.luafile_to_ast('" + path.getPath() + "')";
		getState().LdoString(statement);
		index();
	}

	/**
	 * @return {@link IProblem} giving information on syntax error
	 */
	public LuaParseError analyzer() {
		assert hasSyntaxErrors() : "No problems without syntax error";
		return _parseError;
	}

	/**
	 * Give children ID list.
	 * 
	 * @param id
	 *            Id of a node contained in the AST Metalua just generate from
	 *            source code.
	 * 
	 * @return the list< long> IDs of child nodes of node for the given ID
	 */
	public List<Long> children(final long id) {

		// Work on empty stack
		assert getState().getTop() == 0;
		List<Long> child = new ArrayList<Long>();

		// Fetch Lua function
		getState().getGlobal("children");

		// Provide parameter
		getState().pushNumber((double) id);

		/*
		 * Effective call
		 */
		assert getState().isNumber(-1) : "Number parameter should be in stack.";
		assert getState().isFunction(-2) : "Attemp to call a non Lua function.";
		if (getState().pcall(1, 2, 0) == 0) {

			// Check results
			assert getState().isNumber(-1);
			assert getState().isTable(-2);

			// Retrieve children count
			long count = (long) getState().toNumber(-1);
			getState().pop(1);

			// Store children
			assert getState().isTable(-1) : "Can't access children IDs table.";
			for (long k = 0; k < count; k++) {
				// Provide requested index of result table
				getState().pushNumber((double) k + 1);

				// Store table value at this index
				assert getState().getTop() == 2 : "Stack alea";
				getState().getTable(-2);
				Long nodeID = (long) getState().toNumber(-1);
				child.add(nodeID);
				getState().pop(1);
			}
		}
		// Flush stack
		getState().setTop(0);

		/*
		 * Sort list
		 */
		Collections.sort(child, this.comparator);
		return child;
	}

	/** Enable to compare Lua AST walkers */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MetaluaASTWalker)) {
			return false;
		}
		MetaluaASTWalker node = (MetaluaASTWalker) o;
		return getSource().equals(node.getSource())
				&& getComparator().equals(node.getComparator())
				&& getState().equals(node.getState());
	}

	/**
	 * Gives comparator used to sort out child nodes IDs, only useful in
	 * {@link #equals(Object)}.
	 */
	protected Comparator<Long> getComparator() {
		return comparator;
	}

	/**
	 * Retrieve offset for end of node in source from {@link Metalua}
	 * 
	 * @param ID
	 *            of a just parsed node
	 * @return position of end of the source of the current node in parsed code
	 *         source.
	 */
	public int getEndPosition(final long id) {
		int position;
		assert getState().getTop() == 0 : "Lua stack should be empty";
		getState().getGlobal("getEnd");
		getState().pushNumber((double) id);
		assert getState().isNumber(-1) : "Unable to load ID of node.";
		assert getState().isFunction(-2) : "Unable to load function to compute end"
				+ " position in source.";
		switch (getState().pcall(1, 1, 0)) {
		case 0:
			assert getState().isNumber(-1) : "Wrong type for end position in code";
			position = (int) getState().toNumber(-1);
			getState().setTop(0);
			assert getState().getTop() == 0 : "Lua stack should be empty";
			return position;
		}
		getState().setTop(0);
		return 0;
	}

	/**
	 * Retrieve offset for start of node in source from {@link Metalua}
	 * 
	 * @param ID
	 *            of a just parsed node
	 * @return position of start of the source of the current node in parsed
	 *         code source.
	 */
	public int getStartPosition(final long id) {
		int position;
		assert getState().getTop() == 0 : "Lua stack should be empty";
		getState().getGlobal("getStart");
		getState().pushNumber((double) id);
		if (getState().pcall(1, 1, 0) == 0) {
			assert getState().isNumber(-1) : "Wrong type for start position in code";
			position = (int) getState().toNumber(-1);
			getState().setTop(0);
			assert getState().getTop() == 0 : "Lua stack should be empty";
			return position;
		}
		getState().setTop(0);
		return 0;
	}

	/** Last parsed source */
	protected String getSource() {
		return source;
	}

	/** Lua instance loaded with {@linkplain Metalua} used to parse source code */
	protected synchronized LuaState getState() {
		return _state;
	}

	/**
	 * Gets the value.
	 * 
	 * @param id
	 *            the id
	 * 
	 * @return the value
	 */
	public String getValue(final long id) {
		String value;
		assert getState().getTop() == 0 : "Lua stack should be empty";

		// Retrieve Lua procedure
		getState().getGlobal("getValue");

		// Provide node ID, push parameter in stack
		getState().pushNumber((double) id);

		// Call Lua function with 1 parameter and 1 result
		if (getState().pcall(1, 1, 0) == 0) {
			assert getState().getTop() == 1 && getState().isString(-1) : "A problem occured during value retrieval";

			// Bear value in mind
			value = getState().toString(-1);
		} else {
			// TODO: Remove silent behaviour
			value = "none";
		}
		// Flush stack
		getState().setTop(0);
		return value;
	}

	/** Indicates if any error occurred. */
	public boolean hasSyntaxErrors() {
		return _parseError != null;
	}

	/**
	 * Generates AST from given source.
	 */
	private void index() {
		/*
		 * Create index in AST
		 */

		// Stack should be empty
		assert getState().getTop() == 0 : "Lua stack should be empty before "
				+ "indexation, stack size: " + getState().getTop();

		// Retrieve procedure index
		getState().getField(LuaState.LUA_GLOBALSINDEX, "index");

		// Retrieve current AST
		getState().getField(LuaState.LUA_GLOBALSINDEX, "ast");

		// Analyze error if AST index fails
		if (getState().pcall(1, 1, 0) == LuaState.LUA_ERRRUN) {
			try {
				Metalua.raise(getState());
			} catch (LuaException e) {
				_parseError = LuaParseErrorFactory.get(e.getMessage());
			}
		} else {
			// Remove procedure and parameter from Lua stack
			getState().pop(1);
		}

		// Lua stack should be empty again
		assert getState().getTop() == 0 : "Lua stack should be empty at this point, "
				+ "instead stack size is " + getState().getTop();
	}

	/**
	 * Indicates if nodes has line info in Lua AST, most of the time chunks
	 * don't.
	 * 
	 * @param id
	 *            of the node to parse from the last parsed source code.
	 * @return {@link Boolean}
	 */
	public boolean nodeHasLineInfo(final long id) {
		boolean hasLineInfo;
		assert getState().getTop() == 0 : "Lua stack should be empty.";
		getState().getGlobal("hasLineInfo");
		getState().pushNumber((double) id);
		if (getState().pcall(1, 1, 0) == 0) {
			assert getState().isBoolean(-1) : "Boolean sould be on top of stack";
			hasLineInfo = getState().toBoolean(-1);
		} else {
			hasLineInfo = false;
		}
		getState().setTop(0);
		return hasLineInfo;
	}

	/**
	 * Operation identifier.
	 * 
	 * @param s
	 *            the s
	 * 
	 * @return Numeric value for type
	 * 
	 * @see
	 */
	public static int opid(final String s) {

		if ("sub".equals(s)) {
			return LuaExpressionConstants.E_MINUS;
		} else if ("mul".equals(s)) {
			return LuaExpressionConstants.E_MULT;
		} else if ("div".equals(s)) {
			return LuaExpressionConstants.E_DIV;
		} else if ("eq".equals(s)) {
			return LuaExpressionConstants.E_EQUAL;
		} else if ("concat".equals(s)) {
			return LuaExpressionConstants.E_CONCAT;
		} else if ("mod".equals(s)) {
			return LuaExpressionConstants.E_MOD;
		} else if ("pow".equals(s)) {
			return LuaExpressionConstants.E_POWER;
		} else if ("lt".equals(s)) {
			return LuaExpressionConstants.E_LT;
		} else if ("le".equals(s)) {
			return LuaExpressionConstants.E_LE;
		} else if ("and".equals(s)) {
			return LuaExpressionConstants.E_LAND;
		} else if ("or".equals(s)) {
			return LuaExpressionConstants.E_LOR;
		} else if ("not".equals(s)) {
			return LuaExpressionConstants.E_BNOT;
		} else if ("len".equals(s)) {
			return LuaExpressionConstants.E_LENGTH;
		} else if ("unm".equals(s)) {
			return LuaExpressionConstants.E_UN_MINUS;
		} else {
			// Assume it's an addition
			assert "add".equals(s) : "Unhandled operator: " + s;
			return LuaExpressionConstants.E_PLUS;
		}
	}

	/**
	 * Node name.
	 * 
	 * @param id
	 *            the id
	 * 
	 * @return the string
	 */
	public String nodeName(final long id) {
		String name = null;

		// Stack should be empty
		assert getState().getTop() == 0 : MetaluaASTWalker.class.toString()
				+ ": Lua stack should be empty";

		// Retrieve Lua function
		getState().getField(LuaState.LUA_GLOBALSINDEX, "getTag");

		// Pass given ID as parameter
		getState().pushNumber((double) id);

		// Call function
		switch (getState().pcall(1, 1, 0)) {
		case 0:
			name = getState().toString(-1);
			break;
		default:
			// TODO: remove silent behaviour
			name = new String();
			break;
		}

		/*
		 * Convert type name in numeric value
		 */
		// if (getState().isString(-1)) {
		// name = getState().toString(-1);
		// } else {
		// assert getState().isNil(-1);
		// }

		// Flush stack
		getState().setTop(0);
		return name;
	}

	/**
	 * The aim here is to perform the following statement in Lua: ast =
	 * mlc.luastring_to_ast('" + source + "')
	 */
	private boolean parse() {
		// Parsing function
		String parseFunction = "luastring_to_ast";

		// Lua utils
		assert getState().getTop() == 0 : "Stack is unbalanced before AST generation.";
		// if (getState().getTop() > 0) {
		// getState().setTop(0);
		// }
		// Retrieve function
		getState().getGlobal("mlc");
		getState().getField(-1, parseFunction);
		getState().remove(-2);
		assert getState().isFunction(-1) : "Unable to load parsing function: "
				+ parseFunction;

		// Provide parameter
		getState().pushString(this.source);

		// Build Lua AST
		switch (getState().pcall(1, 1, 0)) {
		case 0:
			assert getState().getTop() == 1 && getState().isTable(-1) : "Lua AST generation failed.";

			// Store result in global variable 'ast' in Lua side
			getState().setGlobal("ast");
			assert getState().getTop() == 0 : "Lua stack is unbalanced after AST generation";
			return true;
		default:
			// Retrieve error from Lua stack
			String error = getState().toString(-1);

			// Notify Error
			_parseError = LuaParseErrorFactory.get(error);

			// Clean stack
			getState().pop(-1);
			if (DLTKCore.DEBUG) {
			    	Activator.logWarning("Bind error:\n" + error);
			}
			return false;
		}
	}

	private synchronized final void setState (LuaState lua ){
		_state = lua;
	}
	/**
	 * Retrieve kind of statement or expression from Lua AST.
	 * 
	 * @param id
	 *            ID of node from the last AST
	 * 
	 * @return Kind of node as represented in
	 *         {@linkplain LuaExpressionConstants}
	 */
	public int typeOfNode(final long id) {
		/*
		 * Determine type of operator
		 */
		String typeName = nodeName(id);
		if ("Op".equals(typeName)) {
			/*
			 * Check if it's an unary operation
			 */
			String value = getValue(id);
			if ("unm".equals(value)) {
				return LuaExpressionConstants.E_UN_MINUS;
			} else if ("not".equals(value)) {
				return LuaExpressionConstants.E_BNOT;
			} else if ("len".equals(value)) {
				return LuaExpressionConstants.E_BNOT;
			}

			// So, it should be a binary operation
			return LuaExpressionConstants.E_BIN_OP;

		} else if ("Set".equals(typeName)) {
			return LuaExpressionConstants.E_ASSIGN;
		} else if ("Do".equals(typeName)) {
			return LuaStatementConstants.S_BLOCK;
		} else if ("Id".equals(typeName)) {
			return LuaExpressionConstants.E_IDENTIFIER;
		} else if ("Number".equals(typeName)) {
			return LuaExpressionConstants.NUMBER_LITERAL;
		} else if ("Nil".equals(typeName)) {
			return LuaExpressionConstants.NIL_LITTERAL;
		} else if ("True".equals(typeName)) {
			return LuaExpressionConstants.BOOL_TRUE;
		} else if ("False".equals(typeName)) {
			return LuaExpressionConstants.BOOL_FALSE;
		} else if ("Table".equals(typeName)) {
			return LuaExpressionConstants.E_TABLE;
		} else if ("Paren".equals(typeName)) {
			return LuaExpressionConstants.E_PAREN;
		} else if ("Pair".equals(typeName)) {
			return LuaExpressionConstants.E_PAIR;
		} else if ("String".equals(typeName)) {
			return LuaExpressionConstants.STRING_LITERAL;
		} else if ("Function".equals(typeName)) {
			return Declaration.D_METHOD;
		} else if ("Return".equals(typeName)) {
			return LuaStatementConstants.S_RETURN;
		} else if ("Break".equals(typeName)) {
			return LuaStatementConstants.S_BREAK;
		} else if ("While".equals(typeName)) {
			return LuaStatementConstants.S_WHILE;
		} else if ("Repeat".equals(typeName)) {
			return LuaStatementConstants.S_UNTIL;
		} else if ("Local".equals(typeName)) {
			return ASTNode.D_VAR_DECL;
		} else if ("Fornum".equals(typeName)) {
			return LuaStatementConstants.S_FOR;
		} else if ("Forin".equals(typeName)) {
			return LuaStatementConstants.S_FOREACH;
		} else if ("If".equals(typeName)) {
			return LuaStatementConstants.S_IF;
		} else if ("Call".equals(typeName)) {
			return LuaExpressionConstants.E_CALL;
		} else if ("Index".equals(typeName)) {
			return LuaExpressionConstants.E_INDEX;
		} else if ("Localrec".equals(typeName)) {
			return LuaStatementConstants.D_FUNC_DEC;
		} else if ("Dots".equals(typeName)) {
			return LuaExpressionConstants.E_DOTS;
		} else if ("Invoke".equals(typeName)) {
			return LuaExpressionConstants.E_INVOKE;
		}
		// Typical blocks do not have tags
		return LuaStatementConstants.S_BLOCK;
	}

}