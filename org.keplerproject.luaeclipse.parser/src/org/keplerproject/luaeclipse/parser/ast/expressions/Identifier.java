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
 * @date $Date: 2009-06-18 16:46:07 +0200 (jeu., 18 juin 2009) $
 * $Author: kkinfoo $
 * $Id: Identifier.java 1887 2009-06-18 14:46:07Z kkinfoo $
 */
package org.keplerproject.luaeclipse.parser.ast.expressions;

import org.eclipse.dltk.ast.expressions.ExpressionConstants;
import org.eclipse.dltk.ast.expressions.Literal;
import org.eclipse.dltk.utils.CorePrinter;
import org.keplerproject.luaeclipse.internal.parser.Index;

import java.lang.String;


// TODO: Auto-generated Javadoc
/**
 * Used to define variables' names.
 * 
 * @author kkinfoo
 */
public class Identifier extends Literal implements LeftHandSide, Index {

	private long id;

	/**
	 * Instantiates a new identifier.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param value
	 *            the value
	 */
	public Identifier(int start, int end, String value) {
		super(start, end);
		fLiteralValue = value;
	}

	/**
	 * Instantiates a new identifier.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 */
	public Identifier(int start, int end) {
		this(start, end, "");
	}

	/**
	 * Gets the kind.
	 * 
	 * @return Parser's token value
	 */
	@Override
	public int getKind() {
		return ExpressionConstants.E_IDENTIFIER;
	}

	public long getID() {
		return id;
	}
	public void printNode(CorePrinter output){
		output.formatPrintLn(getValue());
	}

	public void setID(long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anwrt.ldt.parser.ast.expressions.LeftHandSide#isLeftHandSide()
	 */
	public boolean isLeftHandSide() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ast.expressions.Literal#toString()
	 */
	public String toString() {
		return fLiteralValue;
	}

}
