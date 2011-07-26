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
import org.eclipse.koneki.ldt.parser.ast.LuaModuleDeclaration;
import org.eclipse.koneki.ldt.parser.internal.tests.utils.DummyReporter;

public class TestModuleDeclaration extends TestCase {

	/**
	 * Mainly tests if ASTs are smartly cached
	 */
	public void testIncompleteParse() {

		ISourceParser parser = new LuaSourceParserFactory().createSourceParser();
		DummyReporter reporter = new DummyReporter();
		LuaModuleDeclaration regular = null;
		LuaModuleDeclaration fuzzy = null;

		// Local variable declaration
		regular = (LuaModuleDeclaration) parser.parse(new ModuleSource("local var"), reporter); //$NON-NLS-1$

		// Fuzzy state between two stables ones
		fuzzy = (LuaModuleDeclaration) parser.parse(new ModuleSource("local var="), reporter); //$NON-NLS-1$

		// Check if faulty ASTs are ignored, the previous AST should be given
		assertSame("While errors occur previous AST is given.", regular, fuzzy);//$NON-NLS-1$
		assertFalse("Error at variable declaration", regular.hasError()); //$NON-NLS-1$

		// Now make a valid local assignment declaration
		ModuleDeclaration stable = (ModuleDeclaration) parser.parse(new ModuleSource("local var=1"), reporter); //$NON-NLS-1$

		// Check if new valid AST is cached
		assertNotSame("Stable AST from cache should have been replaced by a new one.", regular, stable); //$NON-NLS-1$
	}

	/**
	 * Mainly tests if ASTs are smartly cached
	 */
	public void testIncorrectParse() {

		ISourceParser parser = new LuaSourceParserFactory().createSourceParser();
		DummyReporter reporter = new DummyReporter();
		LuaModuleDeclaration regular = null;
		LuaModuleDeclaration fuzzy = null;

		// Regular local variable declaration
		regular = (LuaModuleDeclaration) parser.parse(new ModuleSource("local var"), reporter); //$NON-NLS-1$

		// Incomplete local variable declaration with assignment
		fuzzy = (LuaModuleDeclaration) parser.parse(new ModuleSource("local var="), reporter); //$NON-NLS-1$

		/*
		 * Check if faulty ASTs are ignored, the source previous AST is used to generate a new one
		 */
		assertSame("Faulty code does not return previous AST.", regular, fuzzy); //$NON-NLS-1$

		// Wrong code, anything that could follow will be considered an error
		fuzzy = (LuaModuleDeclaration) parser.parse(new ModuleSource("local var = = "), reporter); //$NON-NLS-1$

		// Check if faulty a new AST is generated from cached source
		assertSame("AST from cache should have be provided.", regular, fuzzy); //$NON-NLS-1$

		// Try a deeper mistake
		fuzzy = (LuaModuleDeclaration) parser.parse(new ModuleSource("local var = = 1"), reporter); //$NON-NLS-1$

		// Check if faulty ASTs are ignored, the fist AST should be given
		assertSame("AST from cache shoul have been provided as well.", regular, fuzzy); //$NON-NLS-1$
	}
}
