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
package org.eclipse.koneki.ldt.editor.lang;

import org.eclipse.osgi.util.NLS;

/**
 * TODO Comment this class
 */
// CHECKSTYLE NLS: OFF
public final class Messages extends NLS {
	public static String LuaCompletionProvidersFlags;
	public static String LuaCompletionProvidersImage;
	public static String LuaEditorMatchingBracketIsOutsideSelectedElement;
	public static String LuaEditorNobracketSelected;
	public static String LuaEditorNoMatchingBracketFound;
	public static String LuaCompletionEngineIniTialization;
	private static final String BUNDLE_NAME = "org.eclipse.koneki.ldt.editor.messages"; //$NON-NLS-1$
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
// CHECKSTYLE NLS: ON
