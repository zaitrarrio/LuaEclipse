package org.keplerproject.luaeclipse.internal.completion;

import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposal;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;
import org.keplerproject.luaeclipse.ui.Activator;

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
