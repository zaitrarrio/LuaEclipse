package org.eclipse.koneki.ldt.ui.search;

import java.util.ArrayList;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.search.ModelElementOccurrencesFinder;
import org.eclipse.koneki.ldt.internal.parser.IOccurrenceHolder;
import org.eclipse.koneki.ldt.parser.LuaSelectionEngine;
import org.eclipse.koneki.ldt.parser.ast.expressions.Identifier;

public class LuaModelElementOccurrencesFinder extends ModelElementOccurrencesFinder {

	private Declaration declaration;

	@Override
	public String initialize(ISourceModule module, IModuleDeclaration root, int offset, int length) {
		ASTNode node = LuaSelectionEngine.findMinimalDeclaration((ModuleDeclaration) root, offset, offset + length);
		if (node instanceof Identifier) {
			Identifier id = (Identifier) node;
			if (id.hasDeclaration()) {
				declaration = id.getDeclaration();
				return null;
			}
		} else if (node instanceof Declaration) {
			declaration = (Declaration) node;
			return null;
		}
		return "No valuable information on this node";//$NON-NLS-1$
	}

	public OccurrenceLocation[] getOccurrences() {
		if (declaration == null || !(declaration instanceof IOccurrenceHolder)) {
			return new OccurrenceLocation[0];
		}
		// Highlight declaration itself
		ArrayList<OccurrenceLocation> list = new ArrayList<OccurrenceLocation>();
		list.add(new OccurrenceLocation(declaration.getNameStart(), declaration.getNameEnd() - declaration.getNameStart(), declaration.getName()));
		IOccurrenceHolder holder = (IOccurrenceHolder) declaration;

		// Highlight occurrences
		for (ASTNode node : holder.getOccurrences()) {
			list.add(new OccurrenceLocation(node.sourceStart(), node.matchLength(), node.toString()));
		}
		return list.toArray(new OccurrenceLocation[list.size()]);
	}
}
