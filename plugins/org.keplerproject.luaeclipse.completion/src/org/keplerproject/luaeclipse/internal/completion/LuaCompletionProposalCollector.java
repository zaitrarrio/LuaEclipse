package org.keplerproject.luaeclipse.internal.completion;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposal;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposalCollector;
import org.eclipse.dltk.ui.text.completion.ScriptOverrideCompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.keplerproject.luaeclipse.core.LuaNature;

public class LuaCompletionProposalCollector extends
	ScriptCompletionProposalCollector {

    protected final static char[] VAR_TRIGGER = new char[] { '\t', ' ', '=',
	    ':', '.' };

    public LuaCompletionProposalCollector(ISourceModule module) {
	super(module);
    }

    @Override
    protected ScriptCompletionProposal createOverrideCompletionProposal(
	    IScriptProject scriptProject, ISourceModule compilationUnit,
	    String name, String[] paramTypes, int start, int length,
	    String displayName, String completionProposal) {
	return new ScriptOverrideCompletionProposal(scriptProject,
		compilationUnit, name, paramTypes, start, length, displayName,
		completionProposal);
    }

    @Override
    protected ScriptCompletionProposal createScriptCompletionProposal(
	    String completion, int replaceStart, int length, Image image,
	    String displayString, int i) {
	return new LuaCompletionProposal(completion, replaceStart, length,
		image, displayString, i);
    }

    @Override
    protected ScriptCompletionProposal createScriptCompletionProposal(
	    String completion, int replaceStart, int length, Image image,
	    String displayString, int i, boolean isInDoc) {
	return new LuaCompletionProposal(completion, replaceStart, length,
		image, displayString, i, isInDoc);
    }

    @Override
    protected char[] getVarTrigger() {
	return VAR_TRIGGER;
    }

	@Override
	protected String getNatureId() {
		return LuaNature.LUA_NATURE;
	}

}
