/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.ui.documentation;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationProvider;
import org.eclipse.koneki.ldt.luadoc.ILuaEntry;
import org.eclipse.koneki.ldt.luadoc.LuadocEntry;
import org.eclipse.koneki.ldt.luadoc.LuadocGenerator;

/**
 * TODO Comment this class
 */
public class LuaDocumentationProvider implements IScriptDocumentationProvider {

	/**
	 * @see org.eclipse.dltk.ui.documentation.IScriptDocumentationProvider#getInfo(java.lang.String)
	 */
	@Override
	public Reader getInfo(String arg0) {
		String path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		Map<String, ILuaEntry> generate = LuadocGenerator.getInstance().generate(path); //$NON-NLS-1$

		ILuaEntry iLuaEntry = generate.get(arg0);
		if (iLuaEntry != null) {
			String html = ((LuadocEntry) iLuaEntry).getHtml();
			return new StringReader(html);
		}
		return new StringReader(arg0);
	}

	/**
	 * @see org.eclipse.dltk.ui.documentation.IScriptDocumentationProvider#getInfo(org.eclipse.dltk.core.IMember, boolean, boolean)
	 */
	@Override
	public Reader getInfo(IMember arg0, boolean arg1, boolean arg2) {
		return new StringReader("Element " + arg0.getElementName() + " tostring " + arg0.toString());
	}

}
