package org.eclipse.koneki.ldt.ui.properties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

public class LuaPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	protected Control createContents(Composite parent) {
		return new Composite(parent, SWT.NONE);
	}
}
