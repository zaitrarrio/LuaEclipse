/******************************************************************************
 * Copyright (c) 2009 KeplerProject, Sierra Wireless.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 *          - initial API and implementation and initial documentation
 *****************************************************************************/
package org.keplerproject.luaeclipse.parser.ast.statements;

// TODO: Auto-generated Javadoc
/**
 * The Class Break.
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class Break extends SimpleStatement {

    /**
     * Instantiates a new break.
     * 
     * @param start
     *            the start
     * @param end
     *            the end
     */
    public Break(int start, int end) {
	super(start, end, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.dltk.ast.statements.Statement#getKind()
     */
    @Override
    public int getKind() {
	return LuaStatementConstants.S_BREAK;
    }

}
