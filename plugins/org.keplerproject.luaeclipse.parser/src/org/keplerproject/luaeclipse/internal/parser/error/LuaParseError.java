package org.keplerproject.luaeclipse.internal.parser.error;

public abstract class LuaParseError {
	private String _errorString;
	private Integer _errorLine;
	private Integer _errorOffset;
	private Integer _errorCol;

	public LuaParseError(String errorMessage) {
		_errorString = errorMessage;
	}

	public String getErrorString() {
		return _errorString;
	}

	public Integer getErrorLine() {
		return _errorLine;
	}

	public Integer getErrorOffset() {
		return _errorOffset;
	}

	public Integer getErrorColumn() {
		return _errorCol;
	}

	protected abstract void initPositions();

	protected void setErrorLine(Integer errorLine) {
		_errorLine = errorLine;
	}

	protected void setErrorOffset(Integer errorOffset) {
		_errorOffset = errorOffset;
	}

	protected void setErrorColumn(Integer errorCol) {
		_errorCol = errorCol;
	}
}
