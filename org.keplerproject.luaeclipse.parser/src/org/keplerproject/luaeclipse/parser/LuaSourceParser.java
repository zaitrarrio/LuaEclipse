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

/**
 * @author	Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 * @date $Date: 2009-07-29 17:56:04 +0200 (mer., 29 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: LuaSourceParser.java 2190 2009-07-29 15:56:04Z kkinfoo $
 */
package org.keplerproject.luaeclipse.parser;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.keplerproject.luaeclipse.internal.parser.NodeFactory;
import org.keplerproject.luaeclipse.internal.parser.error.LuaParseError;

/**
 * The Class LuaSourceParser provides a DLTK AST, when an error occur during
 * parsing it provide the previous version of AST.
 */
public class LuaSourceParser extends AbstractSourceParser {

	/**
	 * AST cache, allow to keep previous AST in mind when syntax errors occurs
	 */
	private static Map<String, ModuleDeclaration> _cache = null;
	static {
		_cache = new HashMap<String, ModuleDeclaration>();
	}

	/**
	 * Provide DLTK compliant AST
	 * 
	 * @return {@link ModuleDeclaration}, in case of syntax errors, the previous
	 *         valid AST is given
	 * @see org.eclipse.dltk.ast.parser.ISourceParser#parse(char[], char[],
	 *      org.eclipse.dltk.compiler.problem.IProblemReporter)
	 */
	@Override
	public synchronized ModuleDeclaration parse(char[] file, char[] source,
			IProblemReporter reporter) {

		// Analyze code
		NodeFactory factory = new NodeFactory(new String(source));
		String fileName = new String(file);
		ModuleDeclaration ast;

		// Search for problem
		if (factory.errorDetected()) {

			// Report it
			LuaParseError analyzer = factory.analyser();
			IProblem problem = buildProblem(file, analyzer);
			reporter.reportProblem(problem);

			// Fetch previous AST from cache
			if (_cache.containsKey(fileName)) {
				ast = _cache.get(fileName);
			} else {
				// When there is no AST cached, create an empty one
				ast = new ModuleDeclaration(source.length);
			}
			/*
			 * Try partial procedure if there a no cache generate empty
			 * AST,parse the code before error's offset
			 */
			// if (!(analyzer instanceof LuaParseErrorNotifier)) {
			// String partial = new String(source);
			// partial = NodeFactory.makeShortVersionToRun(partial);
			// partial = NodeFactory.makeShortVersionToRun("do end");
			// factory = new NodeFactory(partial);
			// }

		} else {
			// Cache current AST in order to use it in case of error
			ast = factory.getRoot();
			_cache.put(fileName, ast);
		}
		return ast;
	}

	private IProblem buildProblem(char[] fileName, LuaParseError analyzer) {
		int col = analyzer.getErrorColumn();
		int offset = analyzer.getErrorOffset();
		int line = analyzer.getErrorLine();
		int id = 1;
		int severity = ProblemSeverities.Error;
		String[] args = {};
		String error = analyzer.getErrorString();
		String file = new String(fileName);

		IProblem problem = new DefaultProblem(file, error, id, args, severity,
				offset, offset, line, col);
		return problem;
	}

}
