/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.internal.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.DefaultProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.koneki.ldt.metalua.Metalua;
import org.eclipse.koneki.ldt.parser.Activator;
import org.eclipse.koneki.ldt.parser.LuaExpressionConstants;
import org.eclipse.koneki.ldt.parser.ast.statements.LuaStatementConstants;

import com.naef.jnlua.LuaException;
import com.naef.jnlua.LuaState;

/**
 * All Lua tools for parsing Lua AST are available from here.
 * 
 * The aim of this plugin is to enable DLTK to access ASTs generated from {@link Metalua}. In order to do so, when ASTs are build on Metalua's side,
 * they need to be converted in Java objects. Then, they could be parsed by DLTK.
 * 
 * In Lua ASTs are recursive tables, in Java, they are supposed to be Objects. Obviously they are no automatic conversion possible. That's why to
 * convert Lua ASTs in DLTK Java ones, walking Lua data to build Java objects is a solution. Object instantiation is performed by {@link NodeFactory},
 * this just gather Lua AST walking tooling.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class MetaluaASTWalker implements LuaExpressionConstants, LuaStatementConstants {

	/** Instance of Lua, AST is fetched from this object */
	private LuaState _state;

	/** Lua source code to parse */
	private String source;

	/** Indicates if Metalua had problem while parsing code. */
	private DefaultProblem _parseError = null;

	/** Path to Lua ast function script */
	final public static String luaFile = "/scripts/ast_to_table.lua"; //$NON-NLS-1$

	/** Name of variable containing computed AST in {@linkplain LuaState} */
	private final String astVariable = "ast"; //$NON-NLS-1$

	/** Numeric value returned when identifier of a node has not been found */
	public static long NODE_NOT_FOUND = 0;

	/**
	 * Instantiates a new Lua instance ready to parse.
	 * 
	 * More precisely, the constructor instantiate a {@link LuaState} loaded with {@link Metalua}. Furthermore, it loads as well a bunch of Lua
	 * functions. Most of the time, methods of the current classe just call those functions.
	 */
	private MetaluaASTWalker() {
		try {
			/*
			 * Define path to source file
			 */

			// Make sure that file is available on disk
			URL url = Platform.getBundle(Activator.PLUGIN_ID).getEntry(luaFile);

			// Retrieve absolute URI of file
			String path = new File(FileLocator.toFileURL(url).getFile()).getPath();

			/*
			 * Generate a new instance of Lua, in order to avoid several files using the same stack
			 */
			setState(Metalua.newState());

			// Run file
			FileInputStream input = new FileInputStream(new File(path));
			getState().load(input, "parsingUtilities"); //$NON-NLS-1$
			getState().call(0, 0);
		} catch (IOException e) {
			Activator.log(e);
		} catch (LuaException e) {
			Activator.log(e);
		}
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
	 *            Absolute path to source file. Lua and Metalua sources only, not any kind of pre compiled chunks.
	 */
	public MetaluaASTWalker(final File path) {
		this();
		// TODO: Test me
		String statement = astVariable + " = mlc.luafile_to_ast('" + path.getPath() + "')"; //$NON-NLS-1$ //$NON-NLS-2$
		getState().load(statement, "mlc.luafile_to_ast"); //$NON-NLS-1$
		getState().call(0, 0);
		index();
	}

	/**
	 * Converts Metalua `Error node in DLTK {@link IProblem}, result can be fetch from {@link #getProblem()}
	 * 
	 * @param idOfErrorNode
	 *            Numeric identifier of `Error node
	 */
	public void buildProblem(final long idOfErrorNode) {
		// Retrieve AST
		int top = getState().getTop();

		// Retrieve error message
		final String message = getValue(idOfErrorNode);

		// Read error position table
		getState().getGlobal(astVariable);
		assert getState().isTable(-1);
		getState().getField(-1, "lineinfo"); //$NON-NLS-1$
		assert getState().isTable(-1);
		getState().getField(-1, "first"); //$NON-NLS-1$
		assert getState().isTable(-1);
		int[] positions = new int[3];
		for (int index = 0; index < positions.length; index++) {
			// +1 because Lua table starts at 1
			getState().pushNumber(index + 1);
			getState().getTable(-2);
			assert getState().isNumber(-1);
			positions[index] = (int) getState().toNumber(-1);
			getState().pop(1);
		}
		// Clear stack
		getState().pop(getState().getTop() - top);
		final int line = positions[0];
		final int col = positions[1];
		final int offset = positions[2];
		ProblemSeverity type = ProblemSeverity.ERROR;
		String[] args = new String[0];
		IProblemIdentifier id;
		id = DefaultProblemIdentifier.decode((int) idOfErrorNode);
		_parseError = new DefaultProblem(new String(), message, id, args, type, offset, -1, line, col);
	}

	/**
	 * Give children ID list.
	 * 
	 * @param id
	 *            Id of a node contained in the AST Metalua just generate from source code.
	 * 
	 * @return the list< long> IDs of child nodes of node for the given ID
	 */
	public List<Long> children(final long id) {
		return getTableFromLuaFunction(id, "children"); //$NON-NLS-1$
	}

	/** Enable to compare Lua AST walkers */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MetaluaASTWalker)) {
			return false;
		}
		MetaluaASTWalker node = (MetaluaASTWalker) o;
		return getSource().equals(node.getSource()) && getState().equals(node.getState());
	}

	/**
	 * Provides result of a Boolean return Lua function
	 * 
	 * @param id
	 *            Numeric identifier of parameter node
	 * @param functionName
	 *            Name of Lua function to call
	 * @return true when function call succeed,false else way
	 */
	private boolean getBooleanFromLuaFunction(final long id, final String functionName) {
		boolean hasLineInfo;
		int top = getState().getTop();
		getState().getGlobal(functionName);
		getState().pushNumber((double) id);
		try {
			getState().call(1, 1);
			assert getState().isBoolean(-1) : "Boolean sould be on top of stack"; //$NON-NLS-1$
			hasLineInfo = getState().toBoolean(-1);
		} catch (Exception e) {
			hasLineInfo = false;
		}
		getState().pop(getState().getTop() - top);
		return hasLineInfo;
	}

	/**
	 * Retrieve offset for end of node in source from {@link Metalua}
	 * 
	 * @param ID
	 *            of a just parsed node
	 * @return position of end of the source of the current node in parsed code source.
	 */
	public int getEndPosition(final long id) {
		return (int) getLongFromLuaFunction(id, "getEnd"); //$NON-NLS-1$
	}

	/**
	 * Provides the identifier of declaration given node refers to
	 * 
	 * @param id
	 *            Numeric identifier of given node
	 * @return Numeric identifier of declaration or {@link #NODE_NOT_FOUND}
	 */
	public long getDeclaration(final long id) {
		return getLongFromLuaFunction(id, "getDeclaration");//$NON-NLS-1$
	}

	/**
	 * Gives expression identifier for given node
	 * 
	 * @param id
	 *            Node identifier of node waiting for name from another node
	 * @return {@link Expression} node identifier or <code>0</code> when no identifier is found
	 */
	public long getIdentifier(final long id) {
		return getLongFromLuaFunction(id, "getIdentifierId"); //$NON-NLS-1$
	}

	/**
	 * Utility that enable to call a Lua function which return an Integer.
	 * 
	 * @param function
	 *            Name of function to call
	 * @param id
	 *            Numeric identifier of node, parameter of called function
	 * @return Integer result of function or {@link #NODE_NOT_FOUND} in case of failure
	 */
	private long getLongFromLuaFunction(final long id, String function) {
		long value = NODE_NOT_FOUND;
		int top = getState().getTop();
		getState().getGlobal(function);
		getState().pushNumber((double) id);
		assert getState().isNumber(-1) : "Unable to load ID of node."; //$NON-NLS-1$
		assert getState().isFunction(-2) : "Unable to load function to compute end position in source."; //$NON-NLS-1$
		try {
			getState().call(1, 1);
			if (getState().isNumber(-1)) {
				value = (long) getState().toNumber(-1);
				getState().pop(1);
			}
		} catch (LuaException e) {
			Activator.logError("Unable to load node identifier", e); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			Activator.log(e);
		}
		assert getState().getTop() == top : "Lua stack should be balanced"; //$NON-NLS-1$
		return value;
	}

	/**
	 * Numeric identifier of parent node
	 * 
	 * @param id
	 *            numeric identifier of child node
	 * @return numeric identifier of parent node or 0 in case of failure
	 */
	public long getParent(long id) {
		return getLongFromLuaFunction(id, "getParent"); //$NON-NLS-1$
	}

	/**
	 * Return an IProblem when there is a syntax error in parsed code
	 * 
	 * @return Error instanced in {@link #buildProblem(long)}
	 */
	public DefaultProblem getProblem() {
		return _parseError;
	}

	/** Last parsed source */
	public String getSource() {
		return source;
	}

	/**
	 * Retrieve offset for start of node in source from {@link Metalua}
	 * 
	 * @param ID
	 *            of a just parsed node
	 * @return position of start of the source of the current node in parsed code source.
	 */
	public int getStartPosition(final long id) {
		return (int) getLongFromLuaFunction(id, "getStart"); //$NON-NLS-1$
	}

	/** Lua instance loaded with {@linkplain Metalua} used to parse source code */
	protected synchronized LuaState getState() {
		return _state;
	}

	/**
	 * Call a Lua String return function
	 * 
	 * @param id
	 *            Node identifier
	 * @param functionName
	 *            Name of function to call in <code>script/ast_to_table.lua</code>
	 * @return String result of function, empty string on error
	 */
	private String getStringFromLuaFunction(final long id, final String functionName) {

		// Stack should be empty
		int top = getState().getTop();

		// Retrieve Lua function
		getState().getField(LuaState.GLOBALSINDEX, functionName);

		// Pass given ID as parameter
		getState().pushNumber((double) id);

		// Call function
		String name = null;
		try {
			getState().call(1, 1);
			name = getState().toString(-1);
		} catch (LuaException e) {
			Activator.logWarning("Unable to get node name for id: " + id, e); //$NON-NLS-1$
			name = new String();
		}

		// Flush stack
		getState().pop(getState().getTop() - top);
		return name;
	}

	/**
	 * Retrieve table content resulting from call to a Lua function, this function has to return a table and its size like:
	 * 
	 * <pre>
	 * function sample()
	 * 	local table = {}
	 * 	return table,#table
	 * end
	 * </pre>
	 * 
	 * @param id
	 *            Numeric node identifier, used as function parameter
	 * @param functionName
	 *            Name of function to call
	 * @return Sorted List of numeric identifiers
	 */
	private List<Long> getTableFromLuaFunction(final long id, final String functionName) {

		// Work on empty stack
		int top = getState().getTop();
		TreeSet<Long> child = new TreeSet<Long>();

		// Fetch Lua function
		getState().getGlobal(functionName);

		// Provide parameter
		getState().pushNumber((double) id);

		/*
		 * Effective call
		 */
		assert getState().isNumber(-1) : "Number parameter should be in stack."; //$NON-NLS-1$
		assert getState().isFunction(-2) : "Attemp to call a non Lua function."; //$NON-NLS-1$
		try {
			getState().call(1, 2);

			// Check results
			assert getState().isNumber(-1);
			assert getState().isTable(-2);

			// Retrieve children count
			long count = (long) getState().toNumber(-1);
			getState().pop(1);

			// Store children
			assert getState().isTable(-1) : "Can't access children IDs table.";//$NON-NLS-1$
			for (long k = 0; k < count; k++) {
				// Provide requested index of result table
				getState().pushNumber((double) k + 1);

				// Store table value at this index
				assert getState().getTop() == top + 2 : "Stack alea";//$NON-NLS-1$
				getState().getTable(-2);
				Long nodeID = (long) getState().toNumber(-1);
				child.add(nodeID);
				getState().pop(1);
			}
		} catch (LuaException e) {
			Activator.logError("Unable to compute child nodes identifier", e); //$NON-NLS-1$
		}
		// Flush stack
		getState().pop(getState().getTop() - top);
		return new ArrayList<Long>(child);
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
		int top = getState().getTop();

		// Retrieve Lua procedure
		getState().getGlobal("getValue"); //$NON-NLS-1$

		// Provide node ID, push parameter in stack
		getState().pushNumber((double) id);

		// Call Lua function with 1 parameter and 1 result
		try {
			getState().call(1, 1);
			assert getState().getTop() == 1 && getState().isString(-1) : "A problem occured during value retrieval"; //$NON-NLS-1$

			// Bear value in mind
			value = getState().toString(-1);
		} catch (LuaException e) {
			value = new String();
			Activator.logWarning("Unable to get value for node: " + id, e); //$NON-NLS-1$
		}
		// Flush stack
		getState().pop(getState().getTop() - top);
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
		assert getState().getTop() == 0 : "Lua stack should be empty before indexation, stack size: " + getState().getTop(); //$NON-NLS-1$

		// Retrieve procedure index
		getState().getField(LuaState.GLOBALSINDEX, "index"); //$NON-NLS-1$

		// Retrieve current AST
		getState().getField(LuaState.GLOBALSINDEX, astVariable);

		// Analyze error if AST index fails
		try {
			getState().call(1, 1);
			// Remove procedure and parameter from Lua stack
			getState().pop(1);
		} catch (LuaException e) {
			Activator.logError("Unable to index nodes after parsing", e); //$NON-NLS-1$
		}

		// Lua stack should be empty again
		assert getState().getTop() == 0 : "Lua stack should be empty at this point, instead stack size is " //$NON-NLS-1$
				+ getState().getTop();
	}

	/**
	 * Indicates if a node is a declaration
	 * 
	 * @param id
	 *            Numeric identifier of node
	 * @return true is given node is a declaration, false else way
	 */
	public boolean isDeclaration(final long id) {
		return getBooleanFromLuaFunction(id, "isDeclaration"); //$NON-NLS-1$
	}

	/**
	 * Indicates if nodes has line info in Lua AST, most of the time chunks don't.
	 * 
	 * @param id
	 *            of the node to parse from the last parsed source code.
	 * @return {@link Boolean}
	 */
	public boolean nodeHasLineInfo(final long id) {
		return getBooleanFromLuaFunction(id, "hasLineInfo");//$NON-NLS-1$
	}

	/**
	 * Retrieve node type name
	 * 
	 * @param id
	 *            Identifier of node
	 * 
	 * @return Type name of node, such as:
	 *         <ul>
	 *         <li>Index</li>
	 *         <li>Set</li>
	 *         <li>...</li>
	 *         </ul>
	 */
	public String nodeName(final long id) {
		return getStringFromLuaFunction(id, "getTag"); //$NON-NLS-1$
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

		if ("sub".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_MINUS;
		} else if ("mul".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_MULT;
		} else if ("div".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_DIV;
		} else if ("eq".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_EQUAL;
		} else if ("concat".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_CONCAT;
		} else if ("mod".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_MOD;
		} else if ("pow".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_POWER;
		} else if ("lt".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_LT;
		} else if ("le".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_LE;
		} else if ("and".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_LAND;
		} else if ("or".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_LOR;
		} else if ("not".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_BNOT;
		} else if ("len".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_LENGTH;
		} else if ("unm".equals(s)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_UN_MINUS;
		} else {
			// Assume it's an addition
			assert "add".equals(s) : "Unhandled operator: " + s; //$NON-NLS-1$ //$NON-NLS-2$
			return LuaExpressionConstants.E_PLUS;
		}
	}

	/**
	 * The aim here is to perform the following statement in Lua: <code>ast = getast( sourceCode )</code>
	 */
	private boolean parse() {

		// Lua utils
		int top = getState().getTop();

		/*
		 * Load parsing function
		 */
		getState().load("require 'errnode'", "paringFunctionLoad"); //$NON-NLS-1$ //$NON-NLS-2$
		getState().call(0, 0);

		// Retrieve function
		getState().getGlobal("getast"); //$NON-NLS-1$
		assert getState().isFunction(-1) : "Unable to load parsing function"; //$NON-NLS-1$

		// Provide parameter
		getState().pushString(this.source);

		// Build Lua AST
		try {
			getState().call(1, 1);
			assert getState().isTable(-1) : "Lua AST generation failed."; //$NON-NLS-1$

			// Store result in global variable 'ast' in Lua side
			getState().setGlobal(astVariable);
			getState().pop(getState().getTop() - top);
			return true;
		} catch (LuaException e) {
			Activator.logError("Unable to pase AST", e); //$NON-NLS-1$
			return false;
		}
	}

	private synchronized final void setState(LuaState lua) {
		_state = lua;
	}

	/**
	 * Fetch Metalua string representation of expression under a node. <code>
	 * `Index{
	 * 		`Id{tab}, 
	 * 		`String {field}
	 * }</code> will produce <code>tab.field</code>
	 * 
	 * @param id
	 *            Numeric identifier of expression top node
	 * @return Metalua flavored representation of expression
	 */
	public String stringRepresentation(long id) {
		return getStringFromLuaFunction(id, "getIdentifierName"); //$NON-NLS-1$
	}

	/**
	 * Retrieve kind of statement or expression from Lua AST.
	 * 
	 * @param id
	 *            ID of node from the last AST
	 * 
	 * @return Kind of node as represented in {@linkplain LuaExpressionConstants}
	 */
	public int typeOfNode(final long id) {
		/*
		 * Determine type of operator
		 */
		String typeName = nodeName(id);
		if ("Op".equals(typeName)) { //$NON-NLS-1$
			/*
			 * Check if it's an unary operation
			 */
			String value = getValue(id);
			if ("unm".equals(value)) { //$NON-NLS-1$
				return LuaExpressionConstants.E_UN_MINUS;
			} else if ("not".equals(value)) { //$NON-NLS-1$
				return LuaExpressionConstants.E_BNOT;
			} else if ("len".equals(value)) { //$NON-NLS-1$
				return LuaExpressionConstants.E_LENGTH;
			}

			// So, it should be a binary operation
			return LuaExpressionConstants.E_BIN_OP;

		} else if ("Set".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_ASSIGN;
		} else if ("Do".equals(typeName)) { //$NON-NLS-1$
			return LuaStatementConstants.S_BLOCK;
		} else if ("Id".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_IDENTIFIER;
		} else if ("Number".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.NUMBER_LITERAL;
		} else if ("Nil".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.NIL_LITTERAL;
		} else if ("True".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.BOOL_TRUE;
		} else if ("False".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.BOOL_FALSE;
		} else if ("Table".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_TABLE;
		} else if ("Paren".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_PAREN;
		} else if ("Pair".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_PAIR;
		} else if ("String".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.STRING_LITERAL;
		} else if ("Function".equals(typeName)) { //$NON-NLS-1$
			return Declaration.D_METHOD;
		} else if ("Return".equals(typeName)) { //$NON-NLS-1$
			return LuaStatementConstants.S_RETURN;
		} else if ("Break".equals(typeName)) { //$NON-NLS-1$
			return LuaStatementConstants.S_BREAK;
		} else if ("While".equals(typeName)) { //$NON-NLS-1$
			return LuaStatementConstants.S_WHILE;
		} else if ("Repeat".equals(typeName)) { //$NON-NLS-1$
			return LuaStatementConstants.S_UNTIL;
		} else if ("Local".equals(typeName)) { //$NON-NLS-1$
			return ASTNode.D_VAR_DECL;
		} else if ("Fornum".equals(typeName)) { //$NON-NLS-1$
			return LuaStatementConstants.S_FOR;
		} else if ("Forin".equals(typeName)) { //$NON-NLS-1$
			return LuaStatementConstants.S_FOREACH;
		} else if ("If".equals(typeName)) { //$NON-NLS-1$
			return LuaStatementConstants.S_IF;
		} else if ("Call".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_CALL;
		} else if ("Index".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_INDEX;
		} else if ("Localrec".equals(typeName)) { //$NON-NLS-1$
			return LuaStatementConstants.D_FUNC_DEC;
		} else if ("Dots".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_DOTS;
		} else if ("Invoke".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_INVOKE;
		} else if ("Error".equals(typeName)) { //$NON-NLS-1$
			return LuaExpressionConstants.E_ERROR;
		}
		// Typical blocks do not have tags
		return LuaStatementConstants.S_BLOCK;
	}
}
