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
package org.eclipse.koneki.ldt.templates.internal;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.koneki.ldt.core.LuaNature;
import org.eclipse.koneki.ldt.editor.Activator;

/**
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 * 
 */
public class LuaTemplateAccess extends ScriptTemplateAccess {
    private static final String CUSTOM_TEMPLATES_KEY = LuaNature.LUA_NATURE
	    + ".Templates";
    private static LuaTemplateAccess instance;

    public static LuaTemplateAccess getInstance() {
	if (instance == null) {
	    instance = new LuaTemplateAccess();
	}

	return instance;
    }

    @Override
    protected String getContextTypeId() {
	// TODO Auto-generated method stub
	return LuaUniversalTemplateContextType.CONTEXT_TYPE_ID; 
    }

    @Override
    protected String getCustomTemplatesKey() {
	// TODO Auto-generated method stub
	return CUSTOM_TEMPLATES_KEY;
    }

    @Override
    protected IPreferenceStore getPreferenceStore() {
	return Activator.getDefault().getPreferenceStore();
    }

}
