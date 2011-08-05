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
package org.eclipse.koneki.ldt.ui.internal.interpreter;

import java.io.IOException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.IDeployment;
import org.eclipse.dltk.internal.launching.AbstractInterpreterInstallType;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.ui.Activator;
import org.osgi.framework.Bundle;

public class LuaInterpreterInstallType extends AbstractInterpreterInstallType {

	private static final String[] INTERPRETER_NAMES = { "lua5.1", "lua" }; //$NON-NLS-1$ //$NON-NLS-2$

	@Override
	protected IPath createPathFile(IDeployment deployment) throws IOException {
		Bundle bundle = Activator.getDefault().getBundle();
		return deployment.add(bundle, "script/path.lua"); //$NON-NLS-1$
	}

	@Override
	protected IInterpreterInstall doCreateInterpreterInstall(String id) {
		return new LuaInterpreterInstall(this, id);
	}

	@Override
	protected ILog getLog() {
		return Activator.getDefault().getLog();
	}

	@Override
	protected String getPluginId() {
		return Activator.PLUGIN_ID;
	}

	@Override
	protected String[] getPossibleInterpreterNames() {
		return INTERPRETER_NAMES;
	}

	@Override
	public String getName() {
		return "Lua"; //$NON-NLS-1$
	}

	@Override
	public String getNatureId() {
		return LuaNature.LUA_NATURE;
	}

}
