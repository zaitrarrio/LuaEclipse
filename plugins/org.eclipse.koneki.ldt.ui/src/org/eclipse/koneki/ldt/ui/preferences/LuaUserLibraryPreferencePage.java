package org.eclipse.koneki.ldt.ui.preferences;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.preferences.UserLibraryPreferencePage;
import org.eclipse.koneki.ldt.core.LuaLanguageToolkit;

public class LuaUserLibraryPreferencePage extends UserLibraryPreferencePage {

	/**
	 * @see org.eclipse.dltk.ui.preferences.UserLibraryPreferencePage#getLanguageToolkit()
	 */
	@Override
	protected IDLTKLanguageToolkit getLanguageToolkit() {
		return LuaLanguageToolkit.getDefault();
	}

}