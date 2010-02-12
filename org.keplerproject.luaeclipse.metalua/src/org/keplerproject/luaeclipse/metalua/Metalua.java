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

/**
 * @author	Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 * @date $Date: 2009-07-17 14:32:31 +0200 (ven., 17 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: Metalua.java 2112 2009-07-17 12:32:31Z kkinfoo $
 */
package org.keplerproject.luaeclipse.metalua;

import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;

// TODO: Auto-generated Javadoc
/**
 * Enables to run Metalua code and source files quickly.
 * 
 * It works with an unique inner {@link LuaState} instance as loading Metalua
 * could be pretty time costly.
 * 
 * @author kkinfoo
 */
public class Metalua {

	/*
	 * Load Metalua
	 */
	/** The state. */
	private static LuaState state;
	static {
		try {
			state = MetaluaStateFactory.newLuaState();
		} catch (LuaException e) {
			Activator.log(e);
		}
	}

	public static LuaState get() {
		return state;
	}
	public static LuaState newState() throws LuaException{
		return MetaluaStateFactory.newLuaState();
	}

	/**
	 * Retrieve error message from a LuaState.
	 * 
	 * @param l
	 *            the l
	 * 
	 * @throws LuaException
	 *             the lua exception
	 */
	public static void raise(LuaState l) throws LuaException {

		// Get message at top of stack
		String msg = l.toString(-1);

		// Clean stack
		l.pop(1);
		throw new LuaException(msg);
	}

	/**
	 * Reloads inner {@link LuaState}
	 * 
	 * If runtime behavior turns weird, call this method to reset it.
	 * 
	 * @throws LuaException
	 *             the lua exception
	 */
	public static void refreshState() throws LuaException {
		state = MetaluaStateFactory.newLuaState();
	}

	/**
	 * Runs Metalua code
	 * 
	 * {@code Metalua.run("print 'hello world'")}
	 * 
	 * @param code
	 *            the code
	 * 
	 * @return True if Lua accept code, false else way
	 * 
	 * @throws LuaException
	 *             the lua exception
	 */
	public static void run(String code) throws LuaException {
		if (state.LdoString(code) != 0) {
			Metalua.raise(state);
			refreshState();
		}
	}

	/**
	 * Runs Metalua code from a file
	 * 
	 * {@code Metalua.runFile("call/me.mlua")}
	 * 
	 * @param fileURI
	 *            the file uri
	 * 
	 * @return True if Lua accept code, false else way
	 * 
	 * @throws LuaException
	 *             the lua exception
	 */
	public static void runFile(String fileURI) throws LuaException {
		run("dofile([[" + fileURI + "]])");
	}

	/**
	 * Indicate if code contains syntax errors
	 * 
	 * @param code
	 *            to run
	 * @return true is code is correct, otherwise false
	 */
	public static boolean isValid(final String code) {

		// Try to load code without run it
		boolean status;
		switch (state.LloadString(code)) {
		case 0:
			status = true;
			break;
		default:
			status = false;
			break;
		}

		// Clear stack
		state.pop(1);
		return status;
	}

	public static String path() {
		return MetaluaStateFactory.sourcesPath();
	}
}
