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
package org.keplerproject.luaeclipse.parser.ast.declarations;

import org.eclipse.dltk.ast.declarations.Argument;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.statements.Statement;
import org.keplerproject.luaeclipse.internal.parser.NameFinder;
import org.keplerproject.luaeclipse.parser.ast.expressions.BinaryExpression;
import org.keplerproject.luaeclipse.parser.ast.expressions.Function;
import org.keplerproject.luaeclipse.parser.ast.expressions.Index;
import org.keplerproject.luaeclipse.parser.ast.expressions.Table;
import org.keplerproject.luaeclipse.parser.ast.statements.BinaryStatement;
import org.keplerproject.luaeclipse.parser.ast.statements.Chunk;

/**
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class DeclarationFetcher {
    private Statement registerChunks(Statement parent, Chunk left, Chunk right) {
	/*
	 * Handle declarations only for Binary statements
	 */
	BinaryStatement bin = null;
	if (parent instanceof BinaryExpression) {
	    bin = (BinaryStatement) parent;
	} else {
	    return parent;
	}
	// Compute accessibility modifier
	// int access;
	// if (parent instanceof Local) {
	// access = Declaration.AccPublic;// Declaration.AccPrivate;
	// } else {
	// access = Declaration.AccPublic;
	// }

	/*
	 * Link every declaration to its initialization
	 */
	int rightSize = right != null ? right.getChilds().size() : 0;
	for (int k = 0; k < rightSize; k++) {
	    Expression l = (Expression) left.getChilds().get(k);
	    Expression r = (Expression) right.getChilds().get(k);
	    Declaration declaration = declarationFromPair(parent, l, r);
	    if (declaration != null) {
		// declaration.setModifier(access);
		bin.addDeclaration(declaration);
	    }

	}
	/*
	 * Continue with local variable without initialization
	 */
	for (int k = rightSize; k < left.getChilds().size(); k++) {

	    Expression l = (Expression) left.getChilds().get(k);
	    SimpleReference ref = NameFinder.getReference(l);
	    LocalVariableDeclaration local = new LocalVariableDeclaration(ref,
		    l.sourceStart(), l.sourceEnd());
	    // if (parent instanceof Local) {
	    // declaration.setModifier(Declaration.AccPrivate);
	    // }
	    local.setModifier(Declaration.D_DECLARATOR);
	    // local.setModifier(access);
	    bin.addDeclaration(local);
	}
	return bin;
    }

    private Declaration declarationFromPair(Statement parent, Expression left,
	    Expression right) {
	SimpleReference name = NameFinder.getReference(left);
	int start = right.sourceStart(), end = right.sourceEnd();
	if (right instanceof Function) {
	    Function function = (Function) right;
	    FunctionDeclaration f = new FunctionDeclaration(name, start, end);
	    f.acceptBody(function);
	    f.setModifier(Declaration.D_METHOD);
	    /*
	     * Register function arguments
	     */
	    for (Object o : function.getArgumentChunk().getStatements()) {
		Expression expr = (Expression) o;
		SimpleReference ref = NameFinder.getReference(expr);
		Argument arg = new Argument(ref, ref.sourceStart(), ref
			.sourceEnd(), expr, Declaration.AccProtected);
		arg.setModifiers(Declaration.D_ARGUMENT);
		f.addArgument(arg);
	    }
	    return f;

	} else if (right instanceof Table) {
	    Table table = (Table) right;
	    TableDeclaration t = new TableDeclaration(name, start, end);
	    t.setModifier(Declaration.D_CLASS);
	    t.setBody(table.getChunk());
	    // if (parent != null) {
	    // t.addSuperClass(parent);
	    // }
	    return t;
	}
	return null;
    }

    // private Statement fetch(Statement node, Chunk left, Chunk right,
    // int modifier) {
    // int leftSize = left.getStatements().size();
    // int rightSize = right == null ? 0 : right.getStatements().size();
    // int start = node.sourceStart();
    // int end = start + node.sourceEnd();
    //
    // /*
    // * Create empty chunk for right side, it will contain statement and
    // * declarations when needed.
    // */
    // int availableValues = leftSize > rightSize ? rightSize : leftSize;
    // Chunk castedRight = new Chunk(left.matchStart(), left.matchLength()
    // + left.matchStart());
    // for (int n = 0; n < availableValues; n++) {
    //
    // // Process needed only for identifiers and indexes
    // Expression declaredVar = (Expression) left.getStatements().get(n);
    // Expression assignedValue = (Expression) right.getStatements()
    // .get(n);
    //
    // // Get variable name
    // String varName = NameFinder.extractName(declaredVar);
    //
    // // Declare Identifier as Arguments
    // int idStart = declaredVar.matchStart() - 1;
    // int idEnd = declaredVar.matchStart() + declaredVar.matchLength();
    //
    // /*
    // * Deal with table declarations, they will be represented as Classes
    // * in outline.
    // */
    // if (assignedValue instanceof Table) {
    // /*
    // * Make table declaration as type
    // */
    // TableDeclaration table = new TableDeclaration(varName, idStart,
    // idEnd, start, end);
    // table.setModifiers(Declaration.D_DECLARATOR);
    // table.setModifier(modifier);
    //
    // // Append current statement to declaration
    // Chunk body = new Chunk(assignedValue.matchStart(),
    // assignedValue.matchStart()
    // + assignedValue.matchLength());
    // body.addStatement(assignedValue);
    // table.setBody(body);
    //
    // // Insert declaration in AST
    // castedRight.addStatement(table);
    // } else if (assignedValue instanceof Function) {
    // /*
    // * Deal with function declarations
    // */
    // FunctionDeclaration function = new FunctionDeclaration(varName,
    // idStart, idEnd, start, end);
    // // Append function body to function declaration
    // function.acceptBody((Function) assignedValue);
    //
    // // Associate function's arguments to function declaration
    // function.acceptArguments(((Function) assignedValue)
    // .getArguments());
    //
    // function.setModifiers(Declaration.D_METHOD);
    // function.setModifier(modifier);
    //
    // // Insert declaration in AST
    // castedRight.addStatement(function);
    // } else if (declaredVar instanceof Index) {
    // /*
    // * Deal with fields, they are index of a table that aren't
    // * functions.
    // */
    // FieldDeclaration field = new FieldDeclaration(varName, idStart,
    // idEnd, declaredVar.matchStart(), declaredVar
    // .matchStart()
    // + declaredVar.matchLength());
    // field.setModifier(Declaration.AccDefault);
    // field.setModifier(modifier);
    // castedRight.addStatement(field);
    // castedRight.addStatement(assignedValue);
    // if (DLTKCore.DEBUG) {
    // System.err.println("attribute : " + varName);
    // }
    // } else {
    // // Add casted if needed statement to chunk
    // castedRight.addStatement(assignedValue);
    // }
    // }
    // if (node instanceof BinaryStatement
    // && ((BinaryStatement) node).getRight() != null) {
    // ((BinaryStatement) node).getRight().addStatement(castedRight);
    // }
    //
    // return node;
    // }

    private Statement register(Index i) {
	Declaration d = declarationFromPair(i, i.getLeft(), i.getRight());
	if (d != null) {
	    i.addDeclaration(d);
	}
	return i;
    }

    private Statement register(BinaryStatement node) {
	return registerChunks((Statement) node, node.getLeft(), node.getRight());
    }

    public Statement register(Statement node) {
	if (node instanceof BinaryStatement) {
	    return register((BinaryStatement) node);
	} else if (node instanceof Index) {
	    return register((Index) node);
	}
	return node;
    }
    //public void registerForPatch( Statement  s)
}
