package org.keplerproject.luaeclipse.parser.ast.declarations;

import org.eclipse.dltk.ast.declarations.Declaration;
import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;

public class TableField extends FieldDeclaration {

	public TableField(String name, int nameStart, int nameEnd, int declStart,
			int declEnd) {
		super(name, nameStart, nameEnd, declStart, declEnd);
	}

	public TableField(SimpleReference name, int declStart, int declEnd) {
		this(name.getName(), name.sourceStart(), name.sourceEnd(), declStart,
				declEnd);
	}

	public int getKind() {
		return Declaration.D_ARGUMENT;
	}
}
