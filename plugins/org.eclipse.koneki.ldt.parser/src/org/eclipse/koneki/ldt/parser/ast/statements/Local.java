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

/**
 * @author	Kevin KIN-FOO <kkinfoo@anyware-tech.com>
 * @date $Date: 2009-07-29 17:56:04 +0200 (mer., 29 juil. 2009) $
 * $Author: kkinfoo $
 * $Id: Local.java 2190 2009-07-29 15:56:04Z kkinfoo $
 */
package org.eclipse.koneki.ldt.parser.ast.statements;

import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.ast.statements.StatementConstants;
import org.eclipse.dltk.utils.CorePrinter;
import org.eclipse.koneki.ldt.internal.parser.IndexedNode;

// TODO: Auto-generated Javadoc
/**
 * The Class Local.
 */
public class Local extends BinaryStatement implements StatementConstants, IndexedNode {

	private long id;

	/**
	 * Instantiates a new local.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param identifiers
	 *            the identifiers
	 * @param expressions
	 *            the expressions
	 */
	public Local(int start, int end, Chunk identifiers, Chunk expressions) {
		super(start, end, identifiers, D_VAR_DECL, expressions);
	}

	/**
	 * Instantiates a new local.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param identifiers
	 *            the identifiers
	 */
	public Local(int start, int end, Chunk identifiers) {
		this(start, end, identifiers, null);
	}

	public long getID() {
		return id;
	}

	public void setID(long id) {
		this.id = id;
	}

	public void printNode(CorePrinter output) {
		String varList = new String();
		String valueList = new String();
		final String comma = ", "; //$NON-NLS-1$
		Chunk chunk = (Chunk) getLeft();
		for (Object var : chunk.getStatements()) {
			Statement state = (Statement) var;
			varList += state.toString() + comma;
		}
		if (varList.length() > 0) {
			varList = varList.substring(0, varList.length() - 2);
		}
		if (getRight() != null) {
			chunk = (Chunk) getRight();
			for (Object e : chunk.getStatements()) {
				Statement statement = (Statement) e;
				valueList += statement.toString() + comma;
			}
			if (valueList.length() > 0) {
				valueList = " = " + valueList.substring(0, valueList.length() - 2);//$NON-NLS-1$
			}
		}
		output.formatPrintLn("local " + varList + "=" + valueList); //$NON-NLS-1$  //$NON-NLS-2$
	}
}
