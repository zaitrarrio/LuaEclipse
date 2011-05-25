package org.keplerproject.luaeclipse.parser.lua.tests;

import junit.framework.TestSuite;

import org.keplerproject.luaeclipse.parser.lua.internal.tests.TestIndex;

public class TestLuaIntegration extends TestSuite {
	public TestLuaIntegration() {
		super();
		setName("Lua-side integration"); //$NON-NLS-1$
		addTestSuite(TestIndex.class);
	}
}
