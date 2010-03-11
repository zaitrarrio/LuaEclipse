package org.keplerproject.luaeclipse.editor.internal.navigation;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IType;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.keplerproject.luaeclipse.editor.Activator;
import org.osgi.framework.Bundle;

public class LuaLabelProvider extends LabelProvider {
    private static Image image = null;

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
	if (image == null) {
	    Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
	    URL url = bundle.getResource("/img/class_obj.gif");
	    String file = FileLocator.resolve(url).getPath();
	    image = new Image(Display.getDefault(), file);
	}
	return image;
    }

}
