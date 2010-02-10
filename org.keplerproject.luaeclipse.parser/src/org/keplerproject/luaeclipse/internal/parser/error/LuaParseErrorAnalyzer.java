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
package org.keplerproject.luaeclipse.internal.parser.error;

/**
 * 
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 * 
 */
public class LuaParseErrorAnalyzer extends LuaParseError {

	public LuaParseErrorAnalyzer(String errorMessage) {
		super(errorMessage);
	}

	private Integer extractIntFromErrorString(final String startTag,
			final char endTag) {
		return extractIntFromErrorString(startTag, endTag, 0);

	}

	private Integer extractIntFromErrorString(final String startTag,
			final char endTag, final int shift) {
		String errorMessage = shift > 0 ? getErrorString().substring(shift)
				: getErrorString();

		int offsetStart = errorMessage.indexOf(startTag) + startTag.length();
		int offsetEnd = errorMessage.indexOf(endTag);
		String offset = errorMessage.substring(offsetStart, offsetEnd);
		return Integer.parseInt(offset);

	}

	@Override
	protected void initPositions() {
		// Error column
		String tag = " column ";
		int shift = getErrorString().indexOf(tag);
		setErrorColumn(extractIntFromErrorString(tag, ',', shift));
		
		// Error line
		setErrorLine(extractIntFromErrorString(" line ", ','));

		// Offset
		shift = getErrorString().indexOf('>');
		setErrorOffset(extractIntFromErrorString(" char ", ':', shift));
	}
}
