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
package org.keplerproject.luaeclipse.parser.internal.tests;

import junit.framework.TestCase;

import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.keplerproject.luaeclipse.parser.LuaSourceParser;
import org.keplerproject.luaeclipse.parser.ast.declarations.FunctionDeclaration;
import org.keplerproject.luaeclipse.parser.ast.declarations.TableDeclaration;
import org.keplerproject.luaeclipse.parser.internal.tests.utils.DeclarationVisitor;
import org.keplerproject.luaeclipse.parser.internal.tests.utils.DummyReporter;

/**
 * Checks AST inner declarations behavior
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class TestDeclarations extends TestCase {

    /**
     * Assert there are no duplication of declaration nodes
     */
    public void testFunctionDeclarationCount() {
	DeclarationVisitor visitor = null;
	try {
	    visitor = parse("method = function() end");
	} catch (Exception e) {
	    assertNotNull("Visitor not initialised", visitor);
	    return;
	}
	int declarationCount = visitor.getDeclarations().size();
	int functionDeclarationCount = visitor.getDeclarations(
		FunctionDeclaration.class).size();
	assertTrue("Unable to retrieve declaration.", declarationCount > 0);
	assertEquals("Some declarations are not function ones.",
		functionDeclarationCount, declarationCount);
    }

    /**
     * Check that modifiers are properly set
     */
    public void testPublicFunctionSetDeclarationModifiers() {
	DeclarationVisitor visitor = null;
	try {
	    visitor = parse("method = function() end");
	} catch (Exception e) {
	    assertNotNull("Visitor not initialised", visitor);
	    return;
	}
	Declaration declaration = visitor.getDeclarations(
		FunctionDeclaration.class).get(0);
	assertTrue("Function should be considered as public.", declaration
		.isPublic());
	assertFalse("Function should not be considered as private.",
		declaration.isPrivate());
    }

    /**
     * Check if status of function declaration in a local node is being
     * considered properly
     */
    public void testLocalFunctionSetDeclarationModifiers() {
	DeclarationVisitor visitor = null;
	try {
	    visitor = parse("local method = function() end");
	} catch (Exception e) {
	    assertNotNull("Visitor not initialised", visitor);
	    return;
	}
	Declaration declaration = visitor.getDeclarations(
		FunctionDeclaration.class).get(0);
	assertFalse("Function should not be considered as public.", declaration
		.isPublic());
	assertTrue("Function should be considered as private.", declaration
		.isPrivate());
    }

    /**
     * Check if status of table in a local node is being considered properly
     */

    public void testLocalTableDeclarationModifiers() {
	DeclarationVisitor visitor = null;
	try {
	    visitor = parse("local t={}");
	} catch (Exception e) {
	    assertNotNull("Visitor not initialised", visitor);
	    return;
	}
	Declaration declaration = visitor.getDeclarations(
		TableDeclaration.class).get(0);
	assertFalse("Table should not be considered as public.", declaration
		.isPublic());
	assertTrue("Table should be considered as private.", declaration
		.isPrivate());
    }

    /**
     * Parses AST to extract declarations to test
     */
    private static DeclarationVisitor parse(String code) throws Exception {
	// Parse code
	DeclarationVisitor visitor = new DeclarationVisitor();
	DummyReporter reporter = new DummyReporter();
	LuaSourceParser parser = new LuaSourceParser();

	// Extract declarations
	ModuleDeclaration module = parser.parse("none".toCharArray(), code
		.toCharArray(), reporter);
	module.traverse(visitor);
	return visitor;
    }
}