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


/**
 * @author	Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 * @date $Date: 2009-07-23 12:07:30 +0200 (jeu., 23 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: TestUnaryOperations.java 2161 2009-07-23 10:07:30Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.internal.tests;

import junit.framework.TestCase;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.koneki.ldt.parser.LuaSourceParser;
import org.eclipse.koneki.ldt.parser.internal.tests.utils.DummyReporter;


/**
 * The Class TestUnaryOperations checks implementation of Lua's unary operators
 * in the parser.
 */
public class TestUnaryOperations extends TestCase {

	/** The reporter. */
	private IProblemReporter reporter;

	/** The file name. */
	private char[] fileName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() {
		// No tests on about file name
		fileName = "none".toCharArray();

		// Dummy problem reporter
		this.reporter = new DummyReporter();
	}

	/**
	 * Test length.
	 */
	public void testLength() {

		char[] source = "size = #{}".toCharArray();
		ModuleDeclaration module = new LuaSourceParser().parse(fileName,
				source, this.reporter);
		assertFalse("Length operator is not supported.", module.isEmpty());
	}

	/**
	 * Test not.
	 */
	public void testNot() {

		char[] source = "_not = not True".toCharArray();
		ModuleDeclaration module = new LuaSourceParser().parse(fileName,
				source, this.reporter);
		assertFalse("Logical negation is not supported.", module.isEmpty());
	}

	/**
	 * Test unary minus.
	 */
	public void testUnaryMinus() {

		char[] source = "unary = -6".toCharArray();
		ModuleDeclaration module = new LuaSourceParser().parse(fileName,
				source, this.reporter);
		assertFalse("Unary minus is not supported.", module.isEmpty());
	}
}
