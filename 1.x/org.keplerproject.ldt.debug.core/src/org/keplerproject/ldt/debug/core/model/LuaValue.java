/**
 * 
 */
package org.keplerproject.ldt.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * @author jasonsantos
 */
public class LuaValue extends LuaDebugElement implements IValue {

	private final String	fType;
	private final String	fValue;

	/**
	 * @param debugTarget
	 * @param value
	 */
	public LuaValue(IDebugTarget debugTarget, String type, String value) {
		super(debugTarget);
		fType = type;
		fValue = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */

	public String getReferenceTypeName() {
		return fType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */

	public String getValueString() {
		return fValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */

	public IVariable[] getVariables() throws DebugException {
		return new IVariable[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */

	public boolean hasVariables() throws DebugException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */

	public boolean isAllocated() throws DebugException {
		return true;
	}

}
