/*******************************************************************************
 * Copyright (c) 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.koneki.ldt.parser.ast.statements;

import org.eclipse.dltk.ast.statements.Block;

/**
 * TODO Comment this class
 */
public class Do extends Block {

	public Do(int start, int end, Chunk chunk) {
		super(start, end, chunk.getStatements());
	}
}
