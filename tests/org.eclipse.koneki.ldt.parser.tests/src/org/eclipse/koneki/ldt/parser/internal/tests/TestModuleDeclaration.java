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

package org.eclipse.koneki.ldt.parser.internal.tests;

import junit.framework.TestCase;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.compiler.env.ModuleSource;
import org.eclipse.koneki.ldt.parser.LuaSourceParserFactory;
import org.eclipse.koneki.ldt.parser.internal.tests.utils.DummyReporter;

public class TestModuleDeclaration extends TestCase {

	/**
	 * Mainly tests if ASTs are smartly cached
	 */
	public void testIncompleteParse() {

		ISourceParser parser = new LuaSourceParserFactory().createSourceParser();
		DummyReporter reporter = new DummyReporter();
		ModuleDeclaration start = null;
		ModuleDeclaration fuzzy = null;

		// Local variable declaration
		start = (ModuleDeclaration) parser.parse(new ModuleSource("local var"), reporter); //$NON-NLS-1$

		// Fuzzy state between two stables ones
		fuzzy = (ModuleDeclaration) parser.parse(new ModuleSource("local var="), reporter); //$NON-NLS-1$

		// Check if faulty ASTs are ignored, the previous AST should be given
		assertTrue("Only source of previous AST is cached, even during errors another AST is generated from previous source.", start != fuzzy);//$NON-NLS-1$

		// Now make a valid local assignment declaration
		ModuleDeclaration stable = (ModuleDeclaration) parser.parse(new ModuleSource("local var=1"), reporter); //$NON-NLS-1$

		// Check if new valid AST is cached
		assertNotSame("Stable AST from cache should have been replaced by a new one.", start, stable); //$NON-NLS-1$
	}

	/**
	 * Mainly tests if ASTs are smartly cached
	 */
	public void testIncorrectParse() {

		ISourceParser parser = new LuaSourceParserFactory().createSourceParser();
		DummyReporter reporter = new DummyReporter();
		ModuleDeclaration start = null;
		ModuleDeclaration fuzzy = null;

		// Regular local variable declaration
		start = (ModuleDeclaration) parser.parse(new ModuleSource("local var"), reporter); //$NON-NLS-1$

		// Incomplete local variable declaration with assignment
		fuzzy = (ModuleDeclaration) parser.parse(new ModuleSource("local var="), reporter); //$NON-NLS-1$

		/*
		 * Check if faulty ASTs are ignored, the source previous AST is used to generate a new one
		 */
		assertNotSame("AST is not regenerated from cached source.", start, fuzzy); //$NON-NLS-1$

		// Wrong code, anything that could follow will be considered an error
		fuzzy = (ModuleDeclaration) parser.parse(new ModuleSource("local var = = "), reporter); //$NON-NLS-1$

		// Check if faulty a new AST is generated from cached source
		assertNotSame("AST from cache should have be provided.", start, fuzzy); //$NON-NLS-1$

		// Try a deeper mistake
		fuzzy = (ModuleDeclaration) parser.parse(new ModuleSource("local var = = 1"), reporter); //$NON-NLS-1$

		// Check if faulty ASTs are ignored, the fist AST should be given
		assertNotSame("AST from cache shouldn't have been provided.", start, fuzzy); //$NON-NLS-1$
	}
}
