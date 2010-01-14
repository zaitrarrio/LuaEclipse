/**
 * 
 */
package org.keplerproject.ldt.debug.ui.breakpoints;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IRunToLineTarget;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.ui.texteditor.ITextEditor;
import org.keplerproject.ldt.ui.editors.LuaEditor;

/**
 * @author jasonsantos
 */
public class LuaEditorAdapterFactory implements IAdapterFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
	 *      java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof LuaEditor) {

			ITextEditor editorPart = (ITextEditor) adaptableObject;
			IResource resource = (IResource) editorPart.getEditorInput()
					.getAdapter(IResource.class);
			if (resource != null) {
				String ext = resource.getFileExtension();
				if (ext != null && ext.equals("lua")) {
					if (adapterType.equals(IToggleBreakpointsTarget.class))
						return new LuaBreakpointTargetAdapter();
					if (adapterType.equals(IRunToLineTarget.class))
						return new LuaRunToLineTargetAdapter();
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return new Class[] { IToggleBreakpointsTarget.class };
	}
}
