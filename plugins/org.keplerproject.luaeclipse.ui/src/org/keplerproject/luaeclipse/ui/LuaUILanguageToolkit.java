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


package org.keplerproject.luaeclipse.ui;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.AbstractDLTKUILanguageToolkit;
import org.eclipse.jface.preference.IPreferenceStore;
import org.keplerproject.luaeclipse.core.LuaLanguageToolkit;


public class LuaUILanguageToolkit extends AbstractDLTKUILanguageToolkit {

	@Override
	public IDLTKLanguageToolkit getCoreToolkit() {
		return LuaLanguageToolkit.getDefault();
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

}
