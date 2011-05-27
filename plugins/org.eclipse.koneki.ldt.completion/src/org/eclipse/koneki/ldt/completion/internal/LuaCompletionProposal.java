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
package org.eclipse.koneki.ldt.completion.internal;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposal;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.koneki.ldt.ui.Activator;
import org.eclipse.swt.graphics.Image;

public class LuaCompletionProposal extends ScriptCompletionProposal {

    public LuaCompletionProposal(String replacementString,
	    int replacementOffset, int replacementLength, Image image,
	    String displayString, int relevance) {
	super(replacementString, replacementOffset, replacementLength, image,
		displayString, relevance);
    }

    public LuaCompletionProposal(String replacementString,
	    int replacementOffset, int replacementLength, Image image,
	    String displayString, int relevance, boolean isInDoc) {
	super(replacementString, replacementOffset, replacementLength, image,
		displayString, relevance, isInDoc);
    }

    @Override
    protected boolean isSmartTrigger(char trigger) {
	return trigger == '.';
    }

    protected boolean insertCompletion() {
	IPreferenceStore preference = Activator.getDefault()
		.getPreferenceStore();
	return preference
		.getBoolean(PreferenceConstants.CODEASSIST_INSERT_COMPLETION);
    }
}
