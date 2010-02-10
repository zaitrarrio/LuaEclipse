package org.keplerproject.luaeclipse.internal.parser.error;

public class LuaParseErrorNotifier extends LuaParseError {

	public LuaParseErrorNotifier(String errorMessage) {
		super(errorMessage);
	}

	@Override
	protected void initPositions() {
		setErrorColumn(1);
		setErrorLine(1);
		setErrorOffset(1);
	}

}
