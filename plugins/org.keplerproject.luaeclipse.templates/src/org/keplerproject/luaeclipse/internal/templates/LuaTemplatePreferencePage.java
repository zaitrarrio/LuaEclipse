package org.keplerproject.luaeclipse.internal.templates;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.keplerproject.luaeclipse.editor.Activator;
import org.keplerproject.luaeclipse.editor.internal.text.ILuaPartitions;
import org.keplerproject.luaeclipse.editor.internal.text.LuaSourceViewerConfiguration;
import org.keplerproject.luaeclipse.editor.internal.text.LuaTextTools;

public class LuaTemplatePreferencePage extends ScriptTemplatePreferencePage {

    @Override
    protected ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
	return new LuaSourceViewerConfiguration(getTextTools()
		.getColorManager(), getPreferenceStore(), null,
		ILuaPartitions.LUA_PARTITIONING);
    }

    @Override
    protected ScriptTemplateAccess getTemplateAccess() {
	return LuaTemplateAccess.getInstance();
    }

    @Override
    protected void setPreferenceStore() {
	setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    private LuaTextTools getTextTools() {
	return Activator.getDefault().getTextTools();
    }
}
