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

package org.keplerproject.luaeclipse.metalua.tests.internal.cases;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.keplerproject.luaeclipse.metalua.Metalua;
import org.keplerproject.luaeclipse.metalua.tests.Suite;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;
import org.osgi.framework.Bundle;

/**
 * Make sure that calls to Metalua work
 * 
 * @author kkinfoo
 * 
 */
public class TestMetalua extends TestCase {
    private static Bundle BUNDLE;
    static {
	BUNDLE = Platform.getBundle(Suite.PLUGIN_ID);
    }

    private LuaState state = null;

    public void setUp() {
	try {
	    this.state = Metalua.newState();
	} catch (LuaException e) {
	    assert false : "Unable to load Metalua " + e.getMessage();
	}
    }

    /** Make sure that syntax errors are catchable by Lua exception */
    public void testHandleErrors() {
	boolean error = false;
	String message = new String();
	try {
	    LuaState s = Metalua.newState();
	    error = s.LdoString("for") != 0;
	} catch (LuaException e) {
	    error = true;
	    message = e.getMessage();
	}
	assertTrue(message, error);
    }

    /** Run from source */
    public void testRunLuaCode() {

	boolean success = true;
	String message = new String();

	// Proofing valid code
	success = state.LdoString("var = 1+1") == 0;
	assertTrue("Single assignment does not work: " + message, success);

	// Proofing wrong code
	success = state.LdoString("var local = 'trashed'") == 0;
	assertFalse("Wrong code is accepted", success);
    }

    /** Run Lua source file */
    public void testRunLuaFile() {

	boolean success = false;
	String message = new String();
	String fileLocation = null;

	// Proofing valid file
	try {
	    fileLocation = path("/scripts/assignment.lua");
	    success = state.LdoFile(fileLocation) == 0;
	} catch (IOException e) {
	    message = e.getMessage();
	}
	assertTrue("File location is not defined.", fileLocation != null);
	assertTrue("Single assignment from '" + fileLocation
		+ "' does not work: " + message, success);

	// Proofing wrong file
	success = state.LdoFile("/scripts/john.doe") == 0;
	assertFalse("Inexistant file call works.", success);
    }

    /** Run from source */
    public void testRunMetaluaCode() {
	// Proofing valid code
	boolean success;
	success = state.LdoString("ast = mlc.luastring_to_ast('var = 1 + 2')") == 0;
	assertTrue("Single assignment does not work.", success);
    }

    /** Run Metalua source file */
    public void testRunMetaluaFile() {
	boolean success = true;
	String message = new String();
	String fileLocation = null;

	// Proofing valid file
	try {
	    fileLocation = path("/scripts/introspection.mlua");
	    success = state.LdoFile(fileLocation) == 0;
	} catch (IOException e) {
	    message = e.getMessage();
	    success = false;
	}
	assertTrue("File location is not defined.", fileLocation != null);
	assertTrue(
		"Code from '" + fileLocation + "' does not work: " + message,
		success);
    }

    public void testSourcesPath() {
	String path = Metalua.path();
	assertFalse("Metalua sources path is not definded.", path.length() == 0);
	File directory = new File(path);
	assertTrue("Metalua sources path does not redirect to directory.",
		directory.isDirectory());
    }

    /** Ensure access to portable file locations */
    private String path(String uri) throws IOException {

	String sourcePath = FileLocator.getBundleFile(BUNDLE).getPath();
	sourcePath += new File(BUNDLE.getEntry(uri).getFile());

	return sourcePath;
    }
}
