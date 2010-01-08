package org.keplerproject.luaeclipse.editor.internal.text;

import org.eclipse.core.runtime.ILog;
import org.eclipse.dltk.ui.text.folding.AbstractASTFoldingStructureProvider;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.keplerproject.luaeclipse.core.LuaNature;
import org.keplerproject.luaeclipse.editor.Activator;

public class LuaASTFoldingStructureProvider extends
		AbstractASTFoldingStructureProvider {

	@Override
	protected String getCommentPartition() {
		return ILuaPartitions.LUA_COMMENT;
	}

	@Override
	protected ILog getLog() {
		return Activator.getDefault().getLog();
	}

	@Override
	protected String getNatureId() {
		return LuaNature.LUA_NATURE;
	}

	@Override
	protected String getPartition() {
		return ILuaPartitions.LUA_PARTITIONING;
	}

	@Override
	protected IPartitionTokenScanner getPartitionScanner() {
		return Activator.getDefault().getTextTools().getPartitionScanner();
	}

	@Override
	protected String[] getPartitionTypes() {
		return ILuaPartitions.LUA_PARTITION_TYPES;
	}

}
