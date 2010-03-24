package org.keplerproject.luaeclipse.internal.templates;

import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.keplerproject.luaeclipse.ui.LuaUILanguageToolkit;

public class LuaTemplateUILanguageToolkit extends LuaUILanguageToolkit {

    public ScriptSourceViewerConfiguration createSourceViewerConfiguration() {

	return new LuaTemplateSourceViewerConfiguration(getTextTools()
		.getColorManager(), getPreferenceStore(), null,
		getPartitioningId(), false);
    }
}
