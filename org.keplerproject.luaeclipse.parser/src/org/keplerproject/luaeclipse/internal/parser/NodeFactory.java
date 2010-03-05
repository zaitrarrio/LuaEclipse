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
import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.Argument;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.expressions.CallArgumentsList;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.statements.Statement;
import org.keplerproject.luaeclipse.internal.parser.error.LuaParseError;
import org.keplerproject.luaeclipse.internal.parser.error.LuaParseErrorAnalyzer;
import org.keplerproject.luaeclipse.parser.LuaExpressionConstants;
import org.keplerproject.luaeclipse.parser.ast.declarations.FunctionDeclaration;
import org.keplerproject.luaeclipse.parser.ast.declarations.LocalVariableDeclaration;
import org.keplerproject.luaeclipse.parser.ast.declarations.TableDeclaration;
import org.keplerproject.luaeclipse.parser.ast.expressions.BinaryExpression;
import org.keplerproject.luaeclipse.parser.ast.expressions.Boolean;
import org.keplerproject.luaeclipse.parser.ast.expressions.Call;
import org.keplerproject.luaeclipse.parser.ast.expressions.Dots;
import org.keplerproject.luaeclipse.parser.ast.expressions.Function;
import org.keplerproject.luaeclipse.parser.ast.expressions.Identifier;
import org.keplerproject.luaeclipse.parser.ast.expressions.Index;
import org.keplerproject.luaeclipse.parser.ast.expressions.Invoke;
import org.keplerproject.luaeclipse.parser.ast.expressions.Nil;
import org.keplerproject.luaeclipse.parser.ast.expressions.Number;
import org.keplerproject.luaeclipse.parser.ast.expressions.Pair;
import org.keplerproject.luaeclipse.parser.ast.expressions.Parenthesis;
import org.keplerproject.luaeclipse.parser.ast.expressions.String;
import org.keplerproject.luaeclipse.parser.ast.expressions.Table;
import org.keplerproject.luaeclipse.parser.ast.expressions.UnaryExpression;
import org.keplerproject.luaeclipse.parser.ast.statements.Break;
import org.keplerproject.luaeclipse.parser.ast.statements.Chunk;
import org.keplerproject.luaeclipse.parser.ast.statements.ElseIf;
import org.keplerproject.luaeclipse.parser.ast.statements.ForInPair;
import org.keplerproject.luaeclipse.parser.ast.statements.ForNumeric;
import org.keplerproject.luaeclipse.parser.ast.statements.If;
import org.keplerproject.luaeclipse.parser.ast.statements.Local;
import org.keplerproject.luaeclipse.parser.ast.statements.LocalRec;
import org.keplerproject.luaeclipse.parser.ast.statements.LuaStatementConstants;
import org.keplerproject.luaeclipse.parser.ast.statements.Repeat;
import org.keplerproject.luaeclipse.parser.ast.statements.Return;
import org.keplerproject.luaeclipse.parser.ast.statements.Set;
import org.keplerproject.luaeclipse.parser.ast.statements.While;

/**
 * A factory for creating Node objects.
 * 
 * From Lua source this class is capable of producing a DLTK AST using the
 * Metalua library.
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class NodeFactory implements LuaExpressionConstants,
	LuaStatementConstants {

    /** Enables to waked Lua AST from parsed code. */
    private MetaluaASTWalker lua;

    /** Root of all the nodes produced by this instance of {@link NodeFactory}. */
    private ModuleDeclaration root;

    /**
     * Initialize factory with current Lua context, assumes that an AST named
     * "ast" already exits in Lua context.
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
    public LuaParseError analyser() {
	return lua.analyzer();
    }

    private Chunk addDeclarations(Chunk ids, Chunk values, int mod) {
	Chunk result = new Chunk(values.sourceStart(), values.sourceEnd());
	for (int c = 0; c < values.getChilds().size(); c++) {
	    Statement s = (Statement) values.getChilds().get(c);
	    Expression ref = (Expression) ids.getChilds().get(c);
	    SimpleReference name = NameFinder.getReference(ref);
	    if (s instanceof Function) {
		s = declareFunction(name, (Function) s, mod);
	    } else if (s instanceof Table) {
		s = declareTable(name, (Table) s, mod);
	    }
	    result.addStatement(s);
	}
	return result;
    }

    private Statement addDeclaration(Expression e, int mod) {
	if (e instanceof Index) {
	    Index index = (Index) e;
	    Declaration declaration = null;
	    SimpleReference ref = NameFinder.getReference(index.getLeft());
	    if (index.getRight() instanceof Function) {
		Function function = (Function) index.getRight();
		declaration = declareFunction(ref, function, mod);
	    } else if (index.getRight() instanceof Table) {
		Table table = (Table) index.getRight();
		declaration = declareTable(ref, table, mod);
	    }
	    /*
	     * Wrap declaration in an expression
	     */
	    if (declaration != null) {
		Chunk chunk = new Chunk(declaration.sourceStart(), declaration
			.sourceEnd());
		chunk.addStatement(declaration);
		if (e instanceof Pair) {
		    return new Pair(index.sourceStart(), index.sourceEnd(),
			    index.getLeft(), chunk);
		}
		return new Index(index.sourceStart(), index.sourceEnd(), index
			.getLeft(), chunk);
	    }
	}
	return e;
    }

    private FunctionDeclaration declareFunction(SimpleReference name,
	    Function f, int mod) {
	FunctionDeclaration declaration = new FunctionDeclaration(name, f
		.sourceStart(), f.sourceEnd());
	declaration.setModifier(mod);
	/*
	 * Add arguments
	 */
	for (Object o : f.getArguments().getStatements()) {
	    Expression e = (Expression) o;
	    SimpleReference ref = NameFinder.getReference(e);
	    Argument arg = new Argument(ref, e.sourceStart(), e.sourceEnd(),
		    null, mod);
	    declaration.addArgument(arg);
	}
	// Store body
	declaration.acceptBody(f, true);
	return declaration;
    }

    private Statement declareLocale(SimpleReference name, Expression e, int mod) {
	LocalVariableDeclaration local = new LocalVariableDeclaration(name, e
		.sourceStart(), e.sourceEnd());
	local.setModifier(mod);
	return local;
    }

    private TableDeclaration declareTable(SimpleReference name, Table t, int mod) {
	Chunk body = new Chunk(t.sourceStart(), t.sourceEnd());
	for (Object o : t.getChilds()) {
	    Expression expr = (Expression) o;
	    expr = (Expression) addDeclaration(expr, mod);
	    body.addStatement(expr);
	}
	TableDeclaration dec = new TableDeclaration(name, t.sourceStart(), t
		.sourceEnd());
	dec.setModifier(mod);
	dec.setBody(body);
	return dec;
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
     * Retrieve a node for the given ID from {@linkplain MetaluaASTWalker}
     * instance. This node comes with all its child nodes. In order to do so,
     * this function is called recursively.
     * 
     * @param long id ID of the node in Lua indexed AST
     * 
     * @return DLTK compliant node from Lua AST node for ID
     */
    public Statement getNode(final long id) {

	// Used for binaries expressions
	Statement left, right;
	Chunk chunk, altChunk;
	Expression expression, altExpression;
	String string;

	// Child node IDs will help for recursive node instantiation
	List<Long> childNodes = lua.children(id);

	// Define position in code
	int childCount = childNodes.size();
	int end = lua.getEndPosition(id);
	int start = lua.getStartPosition(id);

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
	    node = new Table(start, end);

	    // Fill with values
	    for (Long child : childNodes) {
		Statement s = addDeclaration((Expression) getNode(child),
			Declaration.AccPublic);
		((Table) node).addStatement(s);
	    }
	    break;
	/*
	 * Pairs
	 */
	case LuaExpressionConstants.E_PAIR:
	    left = (Expression) getNode(childNodes.get(0));
	    right = (Expression) getNode(childNodes.get(1));
	    node = new Pair(start, end, (Expression) left, (Expression) right);
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
	    expression = (Expression) getNode(childNodes.get(0));
	    node = new UnaryExpression(start, end, kindOfNode, expression);
	    break;
	/*
	 * Binary Operations
	 */
	case LuaExpressionConstants.E_BIN_OP:
	    assert childCount > 1 : "Too many expressions "
		    + "in binary operation: " + childCount;
	    // Determine king of expression
	    int kind = MetaluaASTWalker.opid(lua.getValue(id));

	    // Compute both sides of '='
	    left = (Expression) getNode(childNodes.get(0));
	    right = (Expression) getNode(childNodes.get(1));
	    node = new BinaryExpression(start, end, (Expression) left, kind,
		    (Expression) right);
	    break;
	/*
	 * Assignment
	 */
	case LuaExpressionConstants.E_ASSIGN:
	    // Deal with assignment
	    assert childCount == 2 : "Invalid number of parameters "
		    + "for a 'Set' instruction :" + childCount;
	    chunk = (Chunk) getNode(childNodes.get(0));
	    altChunk = (Chunk) getNode(childNodes.get(1));
	    /*
	     * In case of function assigned to variables, use variables names as
	     * function name. Mainly useful for having valid value on outline.
	     */
	    altChunk = addDeclarations(chunk, altChunk, Declaration.AccPublic);
	    node = new Set(start, end, chunk, altChunk);
	    break;
	/*
	 * Identifiers
	 */
	case LuaExpressionConstants.E_IDENTIFIER:
	    assert childCount == 0 : "Id has child nodes: " + childCount;
	    node = new Identifier(start, end, lua.getValue(id));
	    break;
	/*
	 * "Do" and Chunks statements
	 */
	case LuaStatementConstants.S_BLOCK:
	    node = new Chunk(start, end);
	    chunk = (Chunk) node;
	    // Inflate block
	    for (Long childID : childNodes) {
		chunk.addStatement(getNode(childID));
	    }
	    break;
	/*
	 * Functions
	 */
	case Declaration.D_METHOD:
	    assert childCount == 2 : "Wrong child nodes count for a function: "
		    + childCount;
	    chunk = (Chunk) getNode(childNodes.get(0));
	    altChunk = (Chunk) getNode(childNodes.get(1));
	    node = new Function(start, end, chunk, altChunk);
	    break;
	/*
	 * Return
	 */
	case LuaStatementConstants.S_RETURN:
	    // Define return statement and values
	    node = new Return(start, end);
	    for (long returnIndex : childNodes) {
		expression = (Expression) getNode(returnIndex);
		((Return) node).addExpression(expression);
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
	    assert childCount == 1 : "Too many expressions between parenthesis.";
	    expression = (Expression) getNode(childNodes.get(0));
	    node = new Parenthesis(start, end, expression);
	    break;
	/*
	 * While
	 */
	case S_WHILE:
	    assert childCount == 2 : "Wrong parameters count to build while statement: "
		    + childCount;
	    expression = (Expression) getNode(childNodes.get(0));
	    chunk = (Chunk) getNode(childNodes.get(1));
	    node = new While(start, end, expression, chunk);
	    break;
	/*
	 * Repeat
	 */
	case S_UNTIL:
	    assert childCount == 2 : "Wrong parameters count to build repeat statement: "
		    + childCount;
	    chunk = (Chunk) getNode(childNodes.get(0));
	    expression = (Expression) getNode(childNodes.get(1));
	    node = new Repeat(start, end, chunk, expression);
	    break;
	/*
	 * Local variable declaration
	 */
	case ASTNode.D_VAR_DECL:
	    assert childCount == 2 : "Wrong count of parameters "
		    + "for local declaration: " + childCount;

	    // Handle assignment at declaration
	    chunk = (Chunk) getNode(childNodes.get(0));
	    if (lua.nodeHasLineInfo(childNodes.get(1))) {
		altChunk = (Chunk) getNode(childNodes.get(1));
		altChunk = addDeclarations(chunk, altChunk,
			Declaration.AccPrivate);
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
	     * We're dealing with a mutant statement. A regular `If has 3 child
	     * nodes. Besides, it could have one more option for the "else"
	     * part. Furthermore, "elseif" nodes could indefinitely follow an if
	     * statement.
	     */
	    assert childCount > 1 : "Not enough clauses for if statement: "
		    + childCount;

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
		 * Elseif nodes goes by pair: Expression then Chunk. That's why
		 * we'll use a range of 2.
		 */
		for (int pair = 2; pair < childCount - 1; pair += 2) {

		    // Cast Expression then Chunk
		    expression = (Expression) getNode(childNodes.get(pair));
		    chunk = (Chunk) getNode(childNodes.get(pair + 1));

		    // Append ElseIf nodes' expression and chunk
		    ((ElseIf) (node)).addExpressionAndRelatedChunk(expression,
			    chunk);
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
	    assert childCount > 3 : "Not enough parameter to built numeric for: "
		    + childCount;
	    // Extract common informations
	    Identifier variable = (Identifier) getNode(childNodes.get(0));
	    expression = (Expression) getNode(childNodes.get(1));
	    altExpression = (Expression) getNode(childNodes.get(2));
	    chunk = (Chunk) getNode(childNodes.get(childCount - 1));

	    // Deal with optional expression
	    if (childCount > 4) {
		Expression optionnal = (Expression) getNode(childNodes.get(3));
		node = new ForNumeric(start, end, variable, expression,
			altExpression, optionnal, chunk);
	    } else {
		// Regular numeric for
		node = new ForNumeric(start, end, variable, expression,
			altExpression, chunk);
	    }
	    break;
	case S_FOREACH:
	    assert childCount > 2 : "Not enough parameter to built for each: "
		    + childCount;
	    chunk = (Chunk) getNode(childNodes.get(0));
	    altChunk = (Chunk) getNode(childNodes.get(1));
	    Chunk lastChunk = (Chunk) getNode(childNodes.get(2));
	    node = new ForInPair(start, end, chunk, altChunk, lastChunk);
	    break;
	/*
	 * Call to function
	 */
	case E_CALL:
	    // Allocate function with its name
	    assert childCount > 0 : "No name given for function call.";
	    altExpression = (Expression) getNode(childNodes.get(0));

	    // Append parameters for call
	    if (childCount > 1) {
		CallArgumentsList args = new CallArgumentsList();
		for (int parameter = 1; parameter < childCount; parameter++) {
		    expression = (Expression) getNode(childNodes.get(parameter));
		    args.addNode(expression);

		    // Define parameter list position in code
		    if (parameter == 1) {
			args.setStart(expression.matchStart());
		    } else if (parameter == (childCount - 1)) {
			args.setEnd(expression.matchStart()
				+ expression.matchLength());
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
	    assert childCount == 2 : "Wrong parameter count for index: "
		    + childCount;
	    expression = (Expression) getNode(childNodes.get(0));
	    altExpression = (Expression) getNode(childNodes.get(1));
	    node = new Index(start, end, expression, altExpression);
	    break;
	/*
	 * Local recursion
	 */
	case D_FUNC_DEC:
	    assert childCount == 2 : "Too many parameters for local declaration "
		    + "of recursive function: " + childCount;

	    // Handle assignment at declaration
	    chunk = (Chunk) getNode(childNodes.get(0));
	    if (lua.nodeHasLineInfo(childNodes.get(1))) {
		altChunk = (Chunk) getNode(childNodes.get(1));
		altChunk = addDeclarations(chunk, altChunk,
			Declaration.AccPrivate);
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
	    assert childCount > 1 : "No name defined for invocation.";
	    expression = (Expression) getNode(childNodes.get(0));
	    string = (String) getNode(childNodes.get(1));
	    if (childCount > 2) {
		CallArgumentsList args = new CallArgumentsList();
		for (int parameter = 2; parameter < childCount; parameter++) {
		    Expression e = (Expression) getNode(childNodes
			    .get(parameter));
		    args.addNode(e);

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
	}

	/*
	 * Check if gap of 2 characters in Lua string is allowed
	 */
	boolean correctRange = (start - 1) <= (end + 1);
	//
	// There is an exception for functions that can be declared as
	//
	// name = function ( var )
	// someCode()
	// end
	//
	// For DLTK the node containing "name" is in the "node" of the function,
	// in Lua it's not the case
	//
	correctRange = correctRange || (kindOfNode == S_BLOCK);
	assert correctRange : "Wrong code offsets for node: " + id
		+ ". Begins at " + start + ", ends at " + end;
	// if (node instanceof
	// org.keplerproject.luaeclipse.internal.parser.Index) {
	// org.keplerproject.luaeclipse.internal.parser.Index index;
	// index = (org.keplerproject.luaeclipse.internal.parser.Index) node;
	// index.setID(id);
	// }
	return node;
    }

    /**
     * Gets the root of DLTK AST, starts a top down parsing from the first AST
     * node.
     * 
     * @see ModuleDeclaration
     * @return ModuleDeclaration root of any DLTK compliant AST
     */
    public ModuleDeclaration getRoot() {
	// Proceed source parsing only when there are no errors
	if (!errorDetected()) {
	    // Start top down parsing
	    root.addStatement(getNode(1));
	}
	return root;
    }
}
