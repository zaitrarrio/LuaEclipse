package org.keplerproject.luaeclipse.internal.completion;

import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalComputer;
import org.eclipse.dltk.ui.text.completion.ScriptContentAssistInvocationContext;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;

public class LuaCompletionProposalComputer extends
	ScriptCompletionProposalComputer {
    public LuaCompletionProposalComputer() {

    }

    @Override
    protected TemplateCompletionProcessor createTemplateProposalComputer(
	    ScriptContentAssistInvocationContext context) {
	// TODO Auto-generated method stub
	return null;
    }

    protected ScriptCompletionProposalCollector createCollector(
	    ScriptContentAssistInvocationContext context) {
	return new LuaCompletionProposalCollector(context.getSourceModule());
    }
}
