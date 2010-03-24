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
package org.keplerproject.luaeclipse.internal.templates;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplateCompletionProcessor;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;

/**
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 * 
 */
public class LuaTemplateCompletionProcessor extends
	ScriptTemplateCompletionProcessor {

    private static char[] IGNORE = new char[] {'.',':'};
    
    public LuaTemplateCompletionProcessor(
	    ScriptContentAssistInvocationContext context) {
	super(context);
    }

    @Override
    protected String getContextTypeId() {
	// TODO Auto-generated method stub
	return LuaUniversalTemplateContextType.CONTEXT_TYPE_ID;
    }
    protected char[] getIgnore() {
	return IGNORE;
	}
    @Override
    protected ScriptTemplateAccess getTemplateAccess() {
	// TODO Auto-generated method stub
	return LuaTemplateAccess.getInstance();
    }

}
