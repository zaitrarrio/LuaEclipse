package org.keplerproject.luaeclipse.internal.parser;


public class LuaParseErrorNotifier extends LuaParseErrorAnalyzer {

	public LuaParseErrorNotifier(String errorMessage) {
		super("", errorMessage);
		initPositions(1, 1, 1);
	}
}
