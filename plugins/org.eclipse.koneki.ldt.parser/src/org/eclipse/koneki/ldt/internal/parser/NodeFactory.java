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
import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.Argument;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.expressions.CallArgumentsList;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.expressions.Literal;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.koneki.ldt.parser.Activator;
import org.eclipse.koneki.ldt.parser.LuaExpressionConstants;
import org.eclipse.koneki.ldt.parser.ast.declarations.FunctionDeclaration;
import org.eclipse.koneki.ldt.parser.ast.declarations.TableDeclaration;
import org.eclipse.koneki.ldt.parser.ast.expressions.BinaryExpression;
import org.eclipse.koneki.ldt.parser.ast.expressions.Boolean;
import org.eclipse.koneki.ldt.parser.ast.expressions.Call;
import org.eclipse.koneki.ldt.parser.ast.expressions.Dots;
import org.eclipse.koneki.ldt.parser.ast.expressions.Identifier;
import org.eclipse.koneki.ldt.parser.ast.expressions.Index;
import org.eclipse.koneki.ldt.parser.ast.expressions.Invoke;
import org.eclipse.koneki.ldt.parser.ast.expressions.Nil;
import org.eclipse.koneki.ldt.parser.ast.expressions.Number;
import org.eclipse.koneki.ldt.parser.ast.expressions.Pair;
import org.eclipse.koneki.ldt.parser.ast.expressions.Parenthesis;
import org.eclipse.koneki.ldt.parser.ast.expressions.String;
import org.eclipse.koneki.ldt.parser.ast.expressions.UnaryExpression;
import org.eclipse.koneki.ldt.parser.ast.statements.Break;
import org.eclipse.koneki.ldt.parser.ast.statements.Chunk;
import org.eclipse.koneki.ldt.parser.ast.statements.ElseIf;
import org.eclipse.koneki.ldt.parser.ast.statements.ForInPair;
import org.eclipse.koneki.ldt.parser.ast.statements.ForNumeric;
import org.eclipse.koneki.ldt.parser.ast.statements.If;
import org.eclipse.koneki.ldt.parser.ast.statements.Local;
import org.eclipse.koneki.ldt.parser.ast.statements.LocalRec;
import org.eclipse.koneki.ldt.parser.ast.statements.LuaStatementConstants;
import org.eclipse.koneki.ldt.parser.ast.statements.Repeat;
import org.eclipse.koneki.ldt.parser.ast.statements.Return;
import org.eclipse.koneki.ldt.parser.ast.statements.Set;
import org.eclipse.koneki.ldt.parser.ast.statements.While;

/**
 * A factory for creating Node objects.
 * 
 * From Lua source this class is capable of producing a DLTK AST using the Metalua library.
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 */
public class NodeFactory implements LuaExpressionConstants, LuaStatementConstants {

	/** There is a character gap between Lua and Java */
	private static final int languageGap = 1;

	/** Enables to waked Lua AST from parsed code. */
	private MetaluaASTWalker lua;

	/** Root of all the nodes produced by this instance of {@link NodeFactory}. */
	private ModuleDeclaration root;

	/**
	 * Initialize factory with current Lua context, assumes that an AST named "ast" already exits in Lua context.
	 * 
	 * @param MetaluaASTWalker
	 *            Tool making communication with Lua a lot easier
	 * @param int sourceLength the source's length
	 */
	protected NodeFactory(MetaluaASTWalker w, int sourceLength) {
		this.lua = w;
		this.root = new ModuleDeclaration(sourceLength);
	}

	/**
	 * Instantiates a new node factory.
	 * 
	 * @param MetaluaASTWalker
	 *            Tool making communication with Lua a lot easier
	 */
	protected NodeFactory(MetaluaASTWalker helper) {
		this(helper, 0);
	}

	/**
	 * Instantiates a new node factory.
	 * 
	 * @param source
	 *            AST will be generated from this source this source
	 */
	public NodeFactory(final java.lang.String source) {
		this(new MetaluaASTWalker(source), source.length());
	}

	/**
	 * Instantiates a new node factory.
	 * 
	 * @param sourceFile
	 *            the source file
	 */
	public NodeFactory(final File sourceFile) {
		this(new MetaluaASTWalker(sourceFile));
	}

	/**
	 * Report syntax error analyzer if there is any
	 * 
	 * @return {@link LuaParseErrorAnalyzer}
	 */
	public DefaultProblem getProblem() {
		return lua.getProblem();
	}

	/**
	 * Creates an Argument out of a {@link Statement}
	 * 
	 * @param s
	 *            Statement to cast
	 * @param modifiers
	 *            as described in {@link Declaration}
	 * @return casted Argument, if given {@link Statement} is not a regular {@link Identifier} it will be processed as {@link Dots}
	 */
	private Argument argument(Statement s, int modifiers) {
		int start = s.sourceStart(), end = s.sourceEnd();
		SimpleReference name;
		if (s instanceof Identifier) {
			name = (Identifier) s;
		} else {
			// Assume type is `Dots
			name = new SimpleReference(start, end, "..."); //$NON-NLS-1$
		}
		return new Argument(name, start, end, null, modifiers);
	}

	/**
	 * Just indicates if there is any syntax error in parsed code
	 * 
	 * @return True is code contains any syntax error, false else way
	 */
	public boolean errorDetected() {
		return lua.hasSyntaxErrors();
	}

	/**
	 * Retrieve a node for the given ID from {@linkplain MetaluaASTWalker} instance. This node comes with all its child nodes. In order to do so, this
	 * function is called recursively.
	 * 
	 * @param long id ID of the node in Lua indexed AST
	 * 
	 * @return DLTK compliant node from Lua AST node for ID
	 */
	public Statement getNode(final long id) {
		return getNode(id, Declaration.AccPublic);
	}

	public Statement getNode(final long id, int modifier) {

		// Used for binaries expressions
		Statement left, right;
		Chunk chunk, altChunk;
		Expression expression, altExpression;
		String string;
		SimpleReference ref;

		// Child node IDs will help for recursive node instantiation
		List<Long> childNodes = lua.children(id);

		// Define position in code
		int childCount = childNodes.size();
		int start = lua.getStartPosition(id) - languageGap;
		int end = lua.getEndPosition(id);

		/*
		 * Fetch root type
		 */
		Statement node = null;
		int kindOfNode = lua.typeOfNode(id);
		switch (kindOfNode) {
		/*
		 * Numbers
		 */
		case LuaExpressionConstants.NUMBER_LITERAL:
			int value = Integer.valueOf(lua.getValue(id));
			node = new Number(start, end, value);
			break;
		/*
		 * Strings
		 */
		case LuaExpressionConstants.STRING_LITERAL:
			node = new String(start, end, lua.getValue(id));
			break;
		/*
		 * Tables
		 */
		case LuaExpressionConstants.E_TABLE:
			// Define Table
			ref = referenceFromNodeId(id);
			node = new TableDeclaration(ref, start, end);
			((TableDeclaration) node).setModifier(modifier);

			// Fill with values
			chunk = new Chunk(start, end);
			for (Long child : childNodes) {
				chunk.addStatement(getNode(child, modifier));
			}
			((TableDeclaration) node).setBody(chunk);
			break;
		/*
		 * Pairs
		 */
		case LuaExpressionConstants.E_PAIR:
			left = getNode(childNodes.get(0), modifier);
			right = getNode(childNodes.get(1), modifier);
			if (left instanceof Literal) {
				node = new Pair((Literal) left, right, modifier);
			} else {
				node = new Pair((Identifier) left, right, modifier);
			}
			break;
		/*
		 * Logical Values {Nil, True, False}
		 */
		case LuaExpressionConstants.NIL_LITTERAL:
			node = new Nil(start, end);
			break;
		case LuaExpressionConstants.BOOL_TRUE:
			node = new Boolean(start, end, true);
			break;
		case LuaExpressionConstants.BOOL_FALSE:
			node = new Boolean(start, end, false);
			break;
		/*
		 * Unary Operations
		 */
		case LuaExpressionConstants.E_LENGTH:
		case LuaExpressionConstants.E_UN_MINUS:
		case LuaExpressionConstants.E_BNOT:
			node = (Statement) getNode(childNodes.get(0));
			node = new UnaryExpression(start, end, kindOfNode, node);
			break;
		/*
		 * Binary Operations
		 */
		case LuaExpressionConstants.E_BIN_OP:
			assert childCount > 1 : "Too many expressions in binary operation: " //$NON-NLS-1$
					+ childCount;
			// Determine king of expression
			int kind = MetaluaASTWalker.opid(lua.getValue(id));

			// Compute both sides of '='
			left = (Expression) getNode(childNodes.get(0), modifier);
			right = (Expression) getNode(childNodes.get(1), modifier);
			node = new BinaryExpression(start, end, (Expression) left, kind, (Expression) right);
			break;
		/*
		 * Assignment
		 */
		case LuaExpressionConstants.E_ASSIGN:
			// Deal with assignment
			assert childCount == 2 : "Invalid number of parameters for a 'Set' statement :" + childCount; //$NON-NLS-1$
			chunk = (Chunk) getNode(childNodes.get(0), modifier);
			altChunk = (Chunk) getNode(childNodes.get(1), modifier);
			node = new Set(start, end, chunk, altChunk);
			break;
		/*
		 * Identifiers
		 */
		case LuaExpressionConstants.E_IDENTIFIER:
			assert childCount == 0 : "Id has child nodes: " + childCount; //$NON-NLS-1$
			node = new Identifier(start, end, lua.getValue(id));
			break;
		/*
		 * "Do" and Chunks statements
		 */
		case LuaStatementConstants.S_BLOCK:
			chunk = new Chunk(start, end);
			// Inflate block
			for (Long childID : childNodes) {
				chunk.addStatement(getNode(childID, modifier));
			}
			node = chunk;
			break;
		/*
		 * Functions
		 */
		case Declaration.D_METHOD:
			assert childCount == 2 : "Wrong child nodes count for a function: " + childCount; //$NON-NLS-1$
			chunk = (Chunk) getNode(childNodes.get(0));
			altChunk = (Chunk) getNode(childNodes.get(1));
			ref = referenceFromNodeId(id);
			node = new FunctionDeclaration(ref, start, end);
			((FunctionDeclaration) node).setModifier(modifier);
			((FunctionDeclaration) node).acceptBody(altChunk);
			for (Object o : chunk.getStatements()) {
				if (o instanceof Statement) {
					Argument arg = argument((Statement) o, modifier);
					((FunctionDeclaration) node).addArgument(arg);
				}
			}
			break;
		/*
		 * Return
		 */
		case LuaStatementConstants.S_RETURN:
			// Define return statement and values
			node = new Return(start, end);
			for (long returnIndex : childNodes) {
				left = getNode(returnIndex);
				((Return) node).addReturnValue(left);
			}
			break;
		/*
		 * Break
		 */
		case LuaStatementConstants.S_BREAK:
			node = new Break(start, end);
			break;
		/*
		 * Parenthesis
		 */
		case LuaExpressionConstants.E_PAREN:
			assert childCount == 1 : "Too many expressions between parenthesis."; //$NON-NLS-1$
			expression = (Expression) getNode(childNodes.get(0));
			node = new Parenthesis(start, end, expression);
			break;
		/*
		 * While
		 */
		case S_WHILE:
			assert childCount == 2 : "Wrong parameters count to build while statement: " + childCount; //$NON-NLS-1$
			expression = (Expression) getNode(childNodes.get(0));
			chunk = (Chunk) getNode(childNodes.get(1));
			node = new While(start, end, expression, chunk);
			break;
		/*
		 * Repeat
		 */
		case S_UNTIL:
			assert childCount == 2 : "Wrong parameters count to build repeat statement: " + childCount; //$NON-NLS-1$
			chunk = (Chunk) getNode(childNodes.get(0));
			expression = (Expression) getNode(childNodes.get(1));
			node = new Repeat(start, end, chunk, expression);
			break;
		/*
		 * Local variable declaration
		 */
		case ASTNode.D_VAR_DECL:
			assert childCount == 2 : "Wrong count of parameters for local declaration: " + childCount; //$NON-NLS-1$

			// Handle assignment at declaration
			chunk = (Chunk) getNode(childNodes.get(0), Declaration.AccPrivate);
			if (lua.nodeHasLineInfo(childNodes.get(1))) {
				altChunk = (Chunk) getNode(childNodes.get(1), Declaration.AccPrivate);
				// altChunk = addDeclarations(chunk, altChunk,
				// Declaration.AccPrivate, true);
				node = new Local(start, end, chunk, altChunk);
			} else {
				node = new Local(start, end, chunk);
			}
			break;
		/*
		 * If statement
		 */
		case S_IF:
			/*
			 * We're dealing with a mutant statement. A regular `If has 3 child nodes. Besides, it could have one more option for the "else" part.
			 * Furthermore, "elseif" nodes could indefinitely follow an if statement.
			 */
			assert childCount > 1 : "Not enough clauses for if statement: " + childCount; //$NON-NLS-1$

			// Extract if components
			expression = (Expression) getNode(childNodes.get(0));
			chunk = (Chunk) getNode(childNodes.get(1));

			/*
			 * Deal with the multiple "elseif" case
			 */
			if (childCount > 2) {

				// `If node that can handle "elseif"
				node = new ElseIf(start, end, expression, chunk);

				/*
				 * Elseif nodes goes by pair: Expression then Chunk. That's why we'll use a range of 2.
				 */
				for (int pair = 2; pair < childCount - 1; pair += 2) {

					// Cast Expression then Chunk
					expression = (Expression) getNode(childNodes.get(pair));
					chunk = (Chunk) getNode(childNodes.get(pair + 1));

					// Append ElseIf nodes' expression and chunk
					((ElseIf) (node)).addExpressionAndRelatedChunk(expression, chunk);
				}

				// Append else chunk
				if ((childCount % 2) == 1) {
					altChunk = (Chunk) getNode(childNodes.get(childCount - 1));
					((ElseIf) (node)).setAlternative(altChunk);
				}

			} else {
				// Regular `If case
				node = new If(start, end, expression, chunk);
			}
			break;
		/*
		 * For loop
		 */
		case S_FOR:
			assert childCount > 3 : "Not enough parameter to build numeric for: " + childCount; //$NON-NLS-1$
			// Extract common informations
			Identifier variable = (Identifier) getNode(childNodes.get(0), modifier);
			expression = (Expression) getNode(childNodes.get(1), modifier);
			altExpression = (Expression) getNode(childNodes.get(2), modifier);
			chunk = (Chunk) getNode(childNodes.get(childCount - 1), modifier);

			// Deal with optional expression
			if (childCount > 4) {
				Expression optionnal = (Expression) getNode(childNodes.get(3), modifier);
				node = new ForNumeric(start, end, variable, expression, altExpression, optionnal, chunk);
			} else {
				// Regular numeric for
				node = new ForNumeric(start, end, variable, expression, altExpression, chunk);
			}
			break;
		case S_FOREACH:
			assert childCount > 2 : "Not enough parameter to built for each: " + childCount; //$NON-NLS-1$
			chunk = (Chunk) getNode(childNodes.get(0), modifier);
			altChunk = (Chunk) getNode(childNodes.get(1), modifier);
			Chunk lastChunk = (Chunk) getNode(childNodes.get(2), modifier);
			node = new ForInPair(start, end, chunk, altChunk, lastChunk);
			break;
		/*
		 * Call to function
		 */
		case E_CALL:
			// Allocate function with its name
			assert childCount > 0 : "No name given for function call."; //$NON-NLS-1$
			altExpression = (Expression) getNode(childNodes.get(0));

			// Append parameters for call
			if (childCount > 1) {
				CallArgumentsList args = new CallArgumentsList();
				for (int parameter = 1; parameter < childCount; parameter++) {
					left = getNode(childNodes.get(parameter));
					args.addNode(argument(left, modifier));

					// Define parameter list position in code
					if (parameter == 1) {
						args.setStart(left.sourceStart());
					} else if (parameter == (childCount - 1)) {
						args.setEnd(left.sourceEnd());
					}
				}

				node = new Call(start, end, altExpression, args);
			} else {
				node = new Call(start, end, altExpression);
			}
			break;
		/*
		 * Index
		 */
		case E_INDEX:
			// Indexed array and value of index
			assert childCount == 2 : "Wrong parameter count for index: " + childCount; //$NON-NLS-1$
			left = getNode(childNodes.get(0), modifier);
			right = getNode(childNodes.get(1), modifier);
			if (left instanceof Invoke) {
				left = ((Invoke) left).getCallName();
			}
			if (right instanceof Declaration) {
				node = new Index((Expression) left, (Declaration) right);
			} else {
				node = new Index((Expression) left, (Expression) right);
			}
			break;
		/*
		 * Local recursion
		 */
		case D_FUNC_DEC:
			assert childCount == 2 : "Too many parameters for local declaration of recursive function: " + childCount; //$NON-NLS-1$

			// Handle assignment at declaration
			chunk = (Chunk) getNode(childNodes.get(0), Declaration.AccPrivate);
			if (lua.nodeHasLineInfo(childNodes.get(1))) {
				altChunk = (Chunk) getNode(childNodes.get(1), Declaration.AccPrivate);
				node = new LocalRec(start, end, chunk, altChunk);
			} else {
				// Average declaration
				node = new LocalRec(start, end, chunk);
			}
			break;
		/*
		 * Dots
		 */
		case E_DOTS:
			node = new Dots(start, end);
			break;
		/*
		 * Invoke
		 */
		case E_INVOKE:
			assert childCount > 1 : "No name defined for invocation."; //$NON-NLS-1$
			expression = (Expression) getNode(childNodes.get(0), modifier);
			string = (String) getNode(childNodes.get(1), modifier);
			if (childCount > 2) {
				CallArgumentsList args = new CallArgumentsList();
				for (int parameter = 2; parameter < childCount; parameter++) {
					Expression e;
					e = (Expression) getNode(childNodes.get(parameter));
					args.addNode(argument(e, modifier));

					// Define parameter list position in code
					if (parameter == 2) {
						args.setStart(e.matchStart());
					} else if (parameter == (childCount - 1)) {
						args.setEnd(e.matchStart() + e.matchLength());
					}
				}
				node = new Invoke(start, end, expression, string, args);
			} else {
				node = new Invoke(start, end, expression, string);
			}
			break;
		/*
		 * Dealing with errors
		 */
		case E_ERROR:
			lua.buildProblem(id);
			node = new Chunk(start, end);
			break;
		}
		return node;
	}

	/**
	 * Gets the root of DLTK AST, starts a top down parsing from the first AST node.
	 * 
	 * @see ModuleDeclaration
	 * @return ModuleDeclaration root of any DLTK compliant AST
	 */
	public ModuleDeclaration getRoot() {
		// Proceed source parsing only when there are no errors
		if (!errorDetected()) {
			try {
				// Start top down parsing
				root.addStatement(getNode(1));
			} catch (Throwable t) {
				// Avoid any kind of crashing
				Activator.log(t);
				return new ModuleDeclaration(lua.getSource().length());
			}
		}
		return root;
	}

	/**
	 * Compute a name for a given node numeric identifier
	 * 
	 * @param id
	 *            Numeric identifier of node to name
	 * @return Metalua flavored name of given node
	 */
	private SimpleReference referenceFromNodeId(final long id) {
		// Fetch top node of expression
		final long refId = lua.getIdentifier(id);
		java.lang.String representation = new java.lang.String();
		int start = 0, end = 0;
		if (refId > 0) {
			// Get position
			start = lua.getStartPosition(refId) - languageGap;
			end = lua.getEndPosition(refId);
			// Compose name
			representation = lua.stringRepresentation(refId);
		}
		return new SimpleReference(start, end, representation);
	}
}
