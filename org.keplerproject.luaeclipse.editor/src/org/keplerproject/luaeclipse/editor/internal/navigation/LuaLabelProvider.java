package org.keplerproject.luaeclipse.editor.internal.navigation;

import java.io.IOException;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IType;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.keplerproject.luaeclipse.editor.Activator;

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
		if (member instanceof IType
			&& (flags & Modifiers.AccPrivate) != 0)
		    return getPrivateMethodIcon(); // return your special icon
	    }
	} catch (Exception e) {
	}
	return null; // will enter default DLTK behavior
    }

    private IMember getMember(Object element) {
	if (element instanceof IMember) {
	    return (IMember) element;
	}
	return null;
    }

    private Image getPrivateMethodIcon() throws IOException {
	return Activator.getImage("/img/class_obj.gif");
    }

}
