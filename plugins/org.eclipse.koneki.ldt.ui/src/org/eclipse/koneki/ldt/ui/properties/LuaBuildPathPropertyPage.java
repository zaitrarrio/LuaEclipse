package org.eclipse.koneki.ldt.ui.properties;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.preferences.BuildPathsPropertyPage;
import org.eclipse.koneki.ldt.core.LuaLanguageToolkit;
import org.eclipse.ui.IWorkbenchPropertyPage;

public class LuaBuildPathPropertyPage extends BuildPathsPropertyPage implements IWorkbenchPropertyPage {
	public LuaBuildPathPropertyPage() {
	}

	/**
	 * @since 2.0
	 */
	public IDLTKLanguageToolkit getLanguageToolkit() {
		return LuaLanguageToolkit.getDefault();
	}
}
