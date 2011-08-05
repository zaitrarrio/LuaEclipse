/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.editor.internal.navigation;

import java.io.IOException;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.koneki.ldt.editor.Activator;
import org.eclipse.koneki.ldt.editor.lang.Messages;
import org.eclipse.swt.graphics.Image;

public class LuaLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		return null;
	}

	@Override
	public Image getImage(Object element) {
		IMember member = getMember(element);
		if (member == null)
			return null;
		try {
			if (member.exists()) {
				int flags = member.getFlags();
				// Special icon for private type
				if (member instanceof IType && (flags & Modifiers.AccPrivate) != 0) {
					// Special icon
					return getPrivateMethodIcon();
				}
			}
		} catch (ModelException e) {
			Activator.logError(Messages.LuaCompletionProvidersFlags, e);
		} catch (IOException e) {
			Activator.logError(Messages.LuaCompletionProvidersImage, e);
		}
		// DLTK default behavior
		return null;
	}

	private IMember getMember(Object element) {
		if (element instanceof IMember) {
			return (IMember) element;
		}
		return null;
	}

	private Image getPrivateMethodIcon() throws IOException {
		return Activator.getImage("/img/class_obj.gif"); //$NON-NLS-1$
	}

}
