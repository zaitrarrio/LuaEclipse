package org.keplerproject.luaeclipse.completion;

import org.eclipse.dltk.ui.text.completion.ScriptCompletionProcessor;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.ui.IEditorPart;
import org.keplerproject.luaeclipse.core.LuaNature;

public class LuaCompletionProcessor extends ScriptCompletionProcessor {

	public LuaCompletionProcessor(IEditorPart editor,
			ContentAssistant assistant, String partition) {
		super(editor, assistant, partition);
	}

	@Override
	protected String getNatureId() {
		return LuaNature.LUA_NATURE;
	}
}
