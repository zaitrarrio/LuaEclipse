package org.eclipse.koneki.ldt.ui.search;

import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.search.ModelElementOccurrencesFinder;

public class LuaModelElementOccurrencesFinder extends ModelElementOccurrencesFinder {

	@Override
	public String initialize(ISourceModule module, IModuleDeclaration root, int offset, int length) {
		return null;
	}

	public OccurrenceLocation[] getOccurrences() {
		OccurrenceLocation[] locations = new OccurrenceLocation[10];
		for (int location = 0; location < locations.length; location++)
			locations[location] = new OccurrenceLocation(10 * location, 5, "Que tu crois");
		return locations;
	}
}
