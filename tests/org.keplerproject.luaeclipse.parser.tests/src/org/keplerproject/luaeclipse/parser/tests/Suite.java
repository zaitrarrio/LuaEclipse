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
package org.keplerproject.luaeclipse.parser.tests;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.keplerproject.luaeclipse.parser.LuaSourceParser;
import org.keplerproject.luaeclipse.parser.internal.tests.TestASTValidity;
import org.keplerproject.luaeclipse.parser.internal.tests.TestDeclarations;
import org.keplerproject.luaeclipse.parser.internal.tests.TestExpressions;
import org.keplerproject.luaeclipse.parser.internal.tests.TestLuaBinaryOperations;
import org.keplerproject.luaeclipse.parser.internal.tests.TestLuaSourceParser;
import org.keplerproject.luaeclipse.parser.internal.tests.TestModuleDeclaration;
import org.keplerproject.luaeclipse.parser.internal.tests.TestSourceElementRequestVisitor;
import org.keplerproject.luaeclipse.parser.internal.tests.TestStatements;
import org.keplerproject.luaeclipse.parser.internal.tests.TestUnaryOperations;
import org.keplerproject.luaeclipse.parser.internal.tests.TestVisitor;

/**
 * The Class Suite, groups all {@link TestCase} for {@link LuaSourceParser}
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class Suite extends TestSuite {

    /**
     * Instantiates a new suite registering all {@link TestCase} of the plug-in.
     * 
     */
    public Suite() {
	setName("Lua Source parser");
	addTestSuite(TestASTValidity.class);
	addTestSuite(TestDeclarations.class);
	addTestSuite(TestExpressions.class);
	addTestSuite(TestLuaBinaryOperations.class);
	addTestSuite(TestLuaSourceParser.class);
	addTestSuite(TestModuleDeclaration.class);
	addTestSuite(TestSourceElementRequestVisitor.class);
	addTestSuite(TestStatements.class);
	addTestSuite(TestUnaryOperations.class);
	addTestSuite(TestVisitor.class);
    }
}
