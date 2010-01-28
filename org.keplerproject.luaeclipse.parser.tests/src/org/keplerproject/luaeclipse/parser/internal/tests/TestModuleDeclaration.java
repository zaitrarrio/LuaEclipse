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

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.keplerproject.luaeclipse.parser.LuaSourceParser;
import org.keplerproject.luaeclipse.parser.internal.tests.utils.DummyReporter;

public class TestModuleDeclaration extends TestCase {

	/**
	 * Mainly tests if ASTs are smartly cached
	 */
	public void testIncompleteParse() {

		LuaSourceParser parser = new LuaSourceParser();
		DummyReporter reporter = new DummyReporter();
		ModuleDeclaration start = null;
		ModuleDeclaration fuzzy = null;

		// Local variable declaration
		start = parser.parse("none".toCharArray(), "local var".toCharArray(),
				reporter);

		// Fuzzy state between two stables ones
		fuzzy = parser.parse("none".toCharArray(), "local var=".toCharArray(),
				reporter);

		// Check if faulty ASTs are ignored, the previous AST should be given
		assertTrue("AST from cache should have be provided.", start == fuzzy);

		// Now make a valid local assignment declaration
		ModuleDeclaration stable = parser.parse("none".toCharArray(),
				"local var=1".toCharArray(), reporter);

		// Check if new valid AST is cached
		assertNotSame(
				"Stable AST from cache should have been replaced by a newer one.",
				start, stable);
	}

	/**
	 * Mainly tests if ASTs are smartly cached
	 */
	public void testIncorrectParse() {

		LuaSourceParser parser = new LuaSourceParser();
		DummyReporter reporter = new DummyReporter();
		ModuleDeclaration start = null;
		ModuleDeclaration fuzzy = null;

		// Regular local variable declaration
		start = parser.parse("none".toCharArray(), "local var".toCharArray(),
				reporter);

		// Incomplete local variable declaration with assignment
		fuzzy = parser.parse("none".toCharArray(), "local var=".toCharArray(),
				reporter);

		// Check if faulty ASTs are ignored, the previous AST should be given
		assertTrue("AST from cache should have be provided.", start == fuzzy);

		// Wrong code anything that could follow will be an error
		fuzzy = parser.parse("none".toCharArray(), "local var = = "
				.toCharArray(), reporter);

		// Check if faulty ASTs are ignored, the first AST should be given
		assertTrue("AST from cache should have be provided.", start == fuzzy);

		// Try a deeper mistake
		fuzzy = parser.parse("none".toCharArray(), "local var = = 1"
				.toCharArray(), reporter);

		// Check if faulty ASTs are ignored, the fist AST should be given
		assertTrue("AST from cache should have be provided.", start == fuzzy);

		// Now make a valid local assignment declaration
		ModuleDeclaration stable = parser.parse("none".toCharArray(),
				"local var = 1".toCharArray(), reporter);

		// Check if new valid AST is cached
		assertNotSame(
				"Stable AST from cache should have been replaced by a newer one.",
				start, stable);
	}
}
