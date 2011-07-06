/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.internal.parser;

import java.util.List;

import org.eclipse.dltk.ast.ASTListNode;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.expressions.CallArgumentsList;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.expressions.Literal;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.koneki.ldt.parser.ast.LuaModuleDeclaration;
import org.eclipse.koneki.ldt.parser.ast.expressions.BinaryExpression;
import org.eclipse.koneki.ldt.parser.ast.expressions.Boolean;
import org.eclipse.koneki.ldt.parser.ast.expressions.Call;
import org.eclipse.koneki.ldt.parser.ast.expressions.Dots;
import org.eclipse.koneki.ldt.parser.ast.expressions.Function;
import org.eclipse.koneki.ldt.parser.ast.expressions.Identifier;
import org.eclipse.koneki.ldt.parser.ast.expressions.Index;
import org.eclipse.koneki.ldt.parser.ast.expressions.Invoke;
import org.eclipse.koneki.ldt.parser.ast.expressions.Nil;
import org.eclipse.koneki.ldt.parser.ast.expressions.Number;
import org.eclipse.koneki.ldt.parser.ast.expressions.Pair;
import org.eclipse.koneki.ldt.parser.ast.expressions.Parenthesis;
import org.eclipse.koneki.ldt.parser.ast.expressions.String;
import org.eclipse.koneki.ldt.parser.ast.expressions.Table;
import org.eclipse.koneki.ldt.parser.ast.expressions.UnaryExpression;
import org.eclipse.koneki.ldt.parser.ast.statements.Break;
import org.eclipse.koneki.ldt.parser.ast.statements.Chunk;
import org.eclipse.koneki.ldt.parser.ast.statements.Do;
import org.eclipse.koneki.ldt.parser.ast.statements.ElseIf;
import org.eclipse.koneki.ldt.parser.ast.statements.ForInPair;
import org.eclipse.koneki.ldt.parser.ast.statements.ForNumeric;
import org.eclipse.koneki.ldt.parser.ast.statements.If;
import org.eclipse.koneki.ldt.parser.ast.statements.Local;
import org.eclipse.koneki.ldt.parser.ast.statements.LocalRec;
import org.eclipse.koneki.ldt.parser.ast.statements.Repeat;
import org.eclipse.koneki.ldt.parser.ast.statements.Return;
import org.eclipse.koneki.ldt.parser.ast.statements.Set;
import org.eclipse.koneki.ldt.parser.ast.statements.While;

/**
 * TODO Comment this class
 */
public class DLTKObjectCreator {
	public static CallArgumentsList createCallArgumentsList() {
		return new CallArgumentsList();
	}

	public static CallArgumentsList createCallArgumentsList(final int start, final int end) {
		return new CallArgumentsList(start, end);
	}

	public static LuaModuleDeclaration createLuaModuleDeclaration(final int length) {
		return new LuaModuleDeclaration(length);
	}

	public static LuaModuleDeclaration createLuaModuleDeclaration(final int length, final boolean rebuild) {
		return new LuaModuleDeclaration(length, rebuild);
	}

	public static BinaryExpression createBinaryExpression(final int start, final int end, Expression left, final java.lang.String kind,
			Expression right) {
		return new BinaryExpression(start, end, left, kind, right);
	}

	public static BinaryExpression createBinaryExpression(final int start, final int end, Expression left, final int kind, Expression right) {
		return new BinaryExpression(start, end, left, kind, right);
	}

	public static Boolean createBoolean(final int start, final int end, final boolean value) {
		return new Boolean(start, end, value);
	}

	public static Call createCall(final int start, final int end, Expression name) {
		return new Call(start, end, name);
	}

	public static Call createCall(final int start, final int end, Expression name, CallArgumentsList callList) {
		return new Call(start, end, name, callList);
	}

	public static Dots createDots(final int start, final int end) {
		return new Dots(start, end);
	}

	public static Identifier createIdentifier(final int start, final int end, java.lang.String name) {
		return new Identifier(start, end, name);
	}

	public static Function createFunction(final int start, final int end, Chunk parameters, Chunk body) {
		return new Function(start, end, parameters, body);
	}

	public static Index create(Expression key, Declaration value) {
		return new Index(key, value);
	}

	public static Index create(Expression key, Expression value) {
		return new Index(key, value);
	}

	public static Invoke createInvoke(final int start, final int end, Expression module, String string) {
		return new Invoke(start, end, module, string);
	}

	public static Invoke createInvoke(final int start, final int end, Expression module, String string, CallArgumentsList callList) {
		return new Invoke(start, end, module, string, callList);
	}

	public static Nil createNil(final int start, final int end) {
		return new Nil(start, end);
	}

	public static Number createNumber(final int start, final int end, final double value) {
		return new Number(start, end, value);
	}

	public static Pair createPair(Literal name, Statement statement) {
		return new Pair(name, statement);
	}

	public static Pair createPair(SimpleReference name, Statement statement) {
		return new Pair(name, statement);
	}

	public static Parenthesis createParenthesis(final int start, final int end, Expression expression) {
		return new Parenthesis(start, end, expression);
	}

	public static String createString(final int start, final int end, final java.lang.String value) {
		return new String(start, end, value);
	}

	public static Table createTable(final int start, final int end) {
		return new Table(start, end);
	}

	public static Table createTable(final int start, final int end, ASTListNode args) {
		return new Table(start, end, args);
	}

	public static UnaryExpression createUnaryExpression(final int start, final int end, final java.lang.String kind, Statement statement) {
		return new UnaryExpression(start, end, kind, statement);
	}

	public static UnaryExpression createUnaryExpression(final int start, final int end, final int kind, Statement statement) {
		return new UnaryExpression(start, end, kind, statement);
	}

	public static Break createBreak(final int start, final int end) {
		return new Break(start, end);
	}

	public static Chunk createChunk(final int start, final int end) {
		return new Chunk(start, end);
	}

	public static Chunk createChunk(final int start, final int end, List<Statement> statements) {
		return new Chunk(start, end, statements);
	}

	public static Do createDo(final int start, final int end, Chunk chunk) {
		return new Do(start, end, chunk);
	}

	public static ElseIf createElseIf(final int start, final int end, Expression condition, Chunk nominal) {
		return new ElseIf(start, end, condition, nominal);
	}

	public static ElseIf createElseIf(final int start, final int end, Expression condition, Chunk nominal, Chunk alternative) {
		return new ElseIf(start, end, condition, nominal, alternative);
	}

	public static ForInPair createForInPair(final int start, final int end, Chunk identifiers, Chunk expression, Chunk chunk) {
		return new ForInPair(start, end, identifiers, expression, chunk);
	}

	public static ForNumeric createForNumeric(final int start, final int end, Identifier variable, Expression from, Expression to, Chunk chunk) {
		return new ForNumeric(start, end, variable, from, to, chunk);
	}

	public static ForNumeric createForNumeric(final int start, final int end, Identifier variable, Expression from, Expression to,
			Expression optional, Chunk chunk) {
		return new ForNumeric(start, end, variable, from, to, optional, chunk);
	}

	public static If createIf(final int start, final int end, Expression condition, Chunk nominal) {
		return new If(start, end, condition, nominal);
	}

	public static If createIf(final int start, final int end, Expression condition, Chunk nominal, Chunk alternative) {
		return new If(start, end, condition, nominal, alternative);
	}

	public static Local createLocal(final int start, final int end, Chunk identifiers) {
		return new Local(start, end, identifiers);
	}

	public static Local createLocal(final int start, final int end, Chunk identifiers, Chunk inits) {
		return new Local(start, end, identifiers, inits);
	}

	public static LocalRec createLocalRec(final int start, final int end, Chunk identifiers) {
		return new LocalRec(start, end, identifiers);
	}

	public static LocalRec createLocalRec(final int start, final int end, Chunk identifiers, Chunk inits) {
		return new LocalRec(start, end, identifiers, inits);
	}

	public static Repeat createRepeat(final int start, final int end, Chunk chunk, Expression condition) {
		return new Repeat(start, end, chunk, condition);
	}

	public static Return createReturn(final int start, final int end) {
		return new Return(start, end);
	}

	public static Return createReturn(final int start, final int end, Chunk values) {
		return new Return(start, end, values);
	}

	public static Return createReturn(final int start, final int end, List<Statement> list) {
		return new Return(start, end, list);
	}

	public static Set createSet(final int start, final int end, Chunk left, Chunk right) {
		return new Set(start, end, left, right);
	}

	public static While createWhile(final int start, final int end, Expression expression, Chunk chunk) {
		return new While(start, end, expression, chunk);
	}
}
