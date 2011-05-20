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

import com.naef.jnlua.LuaException;

/**
 * @author Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 */
public class LuaParseErrorFactory {
	public static LuaParseError get(final LuaException source) {
	try {
			return new LuaParseErrorAnalyzer(source.getMessage());
	} catch (Exception e) {
			return new LuaParseErrorNotifier(source.getMessage());
	}

    }
}
