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

	/** Make sure that syntax errors are catchable by Lua exception */
	public void testHandleErrors() {
		boolean error = false;
		String message = new String();
		try {
			Metalua.run("for");
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
		try {
			Metalua.run("var = 1+1");
		} catch (LuaException e) {
			message = e.getMessage();
			success = false;
		}
		assertTrue("Single assignment does not work: " + message, success);

		// Proofing wrong code
		try {
			Metalua.run("var local = 'trashed'");
			success = true;
		} catch (LuaException e) {
			success = false;
		}
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
			Metalua.runFile(fileLocation);
			success = true;
		} catch (LuaException e) {
			message = e.getMessage();
		} catch (IOException e) {
			message = e.getMessage();
		}
		assertTrue("File location is not defined.", fileLocation != null);
		assertTrue("Single assignment from '" + fileLocation
				+ "' does not work: " + message, success);

		// Proofing wrong file
		try {
			Metalua.runFile("/scripts/john.doe");
			success = true;
		} catch (LuaException e) {
			message = e.getMessage();
			success = false;
		}
		assertFalse("Inexistant file call works.", success);
	}

	/** Run from source */
	public void testRunMetaluaCode() {

		boolean success = true;
		String message = "";

		// Proofing valid code
		try {
			Metalua.run("ast = mlc.luastring_to_ast ( \"var = 1 + 2\" )");
		} catch (LuaException e) {
			message = e.getMessage();
			success = false;
		}
		assertTrue("Single assignment does not work: " + message, success);
	}

	/** Run Metalua source file */
	public void testRunMetaluaFile() {
		boolean success = true;
		String message = new String();
		String fileLocation = null;

		// Proofing valid file
		try {
			fileLocation = path("/scripts/introspection.mlua");
			Metalua.runFile(fileLocation);
		} catch (LuaException e) {
			message = e.getMessage();
			success = false;
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
		String path = Metalua.sourcesPath();
		assertFalse("Metalua sources path is not definded.", path.isEmpty());
		File directory = new File(path);
		assertTrue("Metalua sources path does not redirect to directory.",
				directory.isDirectory());
	}

	/** Ensure access to portable file locations */
	private String path(String uri) throws IOException {

		// Stop when plug-in's root can't be located
		// try {
		// URL url = BUNDLE.getEntry(uri);
		// String path = FileLocator.toFileURL(url).getFile();
		// return new File(path).getPath();
		// } catch (NullPointerException e) {
		// throw new IOException(uri + " not found.");
		// }
		String sourcePath = FileLocator.getBundleFile(BUNDLE).getPath();
		sourcePath += new File(BUNDLE.getEntry(uri).getFile());

		return sourcePath;
	}
}
