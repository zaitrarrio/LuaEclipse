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
package org.keplerproject.luaeclipse.metalua;

import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;

/**
 * Enables to run Metalua code and source files quickly.
 * 
 * It works with an unique inner {@link LuaState} instance as loading Metalua
 * could be pretty time costly.
 * 
 * @author Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 */
public class Metalua {

    /** Provides a new LuaState with Metalua capabilities */
    public synchronized static LuaState newState() throws LuaException {
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
     * Indicate if code contains syntax errors
     * 
     * @param code
     *            to run
     * @return true is code is correct, otherwise false
     */
    public static boolean isValid(final String code) {

	// Try to load code without run it
	boolean status;
	LuaState state;
	try {
	    state = newState();
	} catch (LuaException e) {
	    return false;
	}
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
