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


package org.keplerproject.luaeclipse.ui.interpreter.preferences;

import org.eclipse.dltk.internal.debug.ui.interpreters.AddScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.keplerproject.luaeclipse.core.LuaNature;
import org.keplerproject.luaeclipse.ui.interpreter.preferences.AddLuaInterpreterDialog;


public class LuaInterpretersBlock extends InterpretersBlock {

	@Override
	protected AddScriptInterpreterDialog createInterpreterDialog(
			IInterpreterInstall standin) {
		AddLuaInterpreterDialog dialog = new  AddLuaInterpreterDialog(
				this,
				getShell(),
				ScriptRuntime.getInterpreterInstallTypes(getCurrentNature()),
				standin);
		return dialog;
	}

	@Override
	protected String getCurrentNature() {
		return LuaNature.LUA_NATURE;
	}
}