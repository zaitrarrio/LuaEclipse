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

package org.keplerproject.luaeclipse.lua.tests;

import org.keplerproject.luaeclipse.lua.internal.tests.ConcurrencyTest;

import junit.framework.TestSuite;

public class Suite extends TestSuite {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.keplerproject.luaeclipse.lua.tests"; //$NON-NLS-1$

	/** Registers all tests to run */
	public Suite() {
		super();
		setName("JNLua"); //$NON-NLS-1$
		addTestSuite(ConcurrencyTest.class);
	}
}
