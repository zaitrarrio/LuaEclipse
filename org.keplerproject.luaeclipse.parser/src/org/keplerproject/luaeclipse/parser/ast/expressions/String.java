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

/**
 * @author	Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 * @date $Date: 2009-07-23 12:07:30 +0200 (jeu., 23 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: String.java 2161 2009-07-23 10:07:30Z kkinfoo $
 */
package org.keplerproject.luaeclipse.parser.ast.expressions;

import org.eclipse.dltk.ast.expressions.StringLiteral;
import org.keplerproject.luaeclipse.internal.parser.Index;

// TODO: Auto-generated Javadoc
/**
 * The Class String.
 */
public class String extends StringLiteral implements Index {

	private long id;

	/**
	 * Instantiates a new string.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public String(int start, int end, java.lang.String value) {
		super(start, end, value);
	}

	public long getID() {
		return id;
	}

	public void setID(long id) {
		this.id = id;
	}
}