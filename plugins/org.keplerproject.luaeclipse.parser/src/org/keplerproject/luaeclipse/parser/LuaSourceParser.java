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
package org.keplerproject.luaeclipse.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.keplerproject.luaeclipse.internal.parser.NodeFactory;
import org.keplerproject.luaeclipse.internal.parser.error.LuaParseError;

/**
 * The Class LuaSourceParser provides a DLTK AST for Lua source code, when an
 * error occur during parsing it provide the previous version of AST.
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class LuaSourceParser extends AbstractSourceParser {

    public static Object mutex = new Object();

    /**
     * Sources cache, allow to keep previous version of source per file in mind.
     * When syntax errors occurs it's then possible to use previous version of
     * source, in order to obtain a consistent AST.
     */
    private static Map<String, String> _cache = null;
    static {
	_cache = Collections.synchronizedMap(new HashMap<String, String>());
    }

    /**
     * @since 2.0
     */
    @Override
    public IModuleDeclaration parse(IModuleSource input,
	    IProblemReporter reporter) {
	char[] source = input.getContentsAsCharArray();
	String fileName = input.getFileName();
	return parse(fileName.toCharArray(), source, reporter);
    }

    /**
     * Provides DLTK compliant AST from Metalua analysis
     * 
     * @return {@link ModuleDeclaration}, in case of syntax errors, the previous
     *         valid AST is given
     * @see org.eclipse.dltk.ast.parser.ISourceParser#parse(char[], char[],
     *      org.eclipse.dltk.compiler.problem.IProblemReporter)
     */
    public synchronized ModuleDeclaration parse(char[] file, char[] source,
	    IProblemReporter reporter) {

	// Analyze code
	ModuleDeclaration ast;
	String code = new String(source);
	NodeFactory factory = null;
	String fileName = new String(file);
	synchronized (mutex) {
	    factory = new NodeFactory(code);
	}

	// Search for problem
	if (factory.errorDetected()) {

	    // Report it
	    LuaParseError analyzer = factory.analyser();
	    IProblem problem = buildProblem(file, analyzer);
	    reporter.reportProblem(problem);

	    // Fetch previous stable source from cache
	    if (_cache.containsKey(fileName)) {
		synchronized (mutex) {
		    factory = new NodeFactory(_cache.get(fileName));
		    ast = factory.getRoot();
		}
	    } else {
		// When there is no source code cached, start from scratch
		ast = new ModuleDeclaration(source.length);
	    }

	} else {
	    // Cache current AST in order to use it in case of error
	    _cache.put(fileName, code);
	    synchronized (mutex) {
	    	ast = factory.getRoot();
	    }
	}
	return ast;
    }

    /**
     * Parses Lua error string and founds its position: offset, line, column
     */
    private IProblem buildProblem(char[] fileName, LuaParseError analyzer) {
	int col = analyzer.getErrorColumn();
	int offset = analyzer.getErrorOffset();
	int line = analyzer.getErrorLine();
	int id = 1;
	String[] args = {};

	// Consider all problems as errors
	int severity = ProblemSeverities.Error;

	// Retrieve Lua error string
	String error = analyzer.getErrorString();

	// Convert file name
	String file = new String(fileName);

	IProblem problem = new DefaultProblem(file, error, id, args, severity,
		offset, offset, line, col);
	return problem;
    }

}
