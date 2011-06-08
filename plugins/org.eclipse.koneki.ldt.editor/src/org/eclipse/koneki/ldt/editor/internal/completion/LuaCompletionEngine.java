/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *     Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 *******************************************************************************/
package org.eclipse.koneki.ldt.editor.internal.completion;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;

/**
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 * 
 */
public class LuaCompletionEngine extends ScriptCompletionEngine {

    IScriptProject project;
    IProgressMonitor progressMonitor;
    private CompletionRequestor requestor;
    private int actualCompletionPosition;
    private int offset;

    @Override
    public void complete(IModuleSource module, int position, int k) {
	this.actualCompletionPosition = position;
	this.offset = k;
	String[] keywords = new String[] { "and", "break", "do", "else",
		"elseif", "end", "false", "for", "function", "if", "in",
		"local", "nil", "not", "or", "repeat", "return", "then",
		"true", "until", "while" };
	for (int j = 0; j < keywords.length; j++) {
	    createProposal(keywords[j], null);
	}

	// Completion for model elements.
	try {
	    module.getModelElement().accept(new IModelElementVisitor() {
		public boolean visit(IModelElement element) {
		    if (element.getElementType() > IModelElement.SOURCE_MODULE) {
			createProposal(element.getElementName(), element);
		    }
		    return true;
		}
	    });
	} catch (ModelException e) {
	    if (DLTKCore.DEBUG) {
		e.printStackTrace();
	    }
	}
    }

    private void createProposal(String name, IModelElement element) {
	CompletionProposal proposal = null;
	try {
	    if (element == null) {
		proposal = this.createProposal(CompletionProposal.KEYWORD,
			this.actualCompletionPosition);
	    } else {
		switch (element.getElementType()) {
		case IModelElement.METHOD:
		    proposal = this.createProposal(
			    CompletionProposal.METHOD_DECLARATION,
			    this.actualCompletionPosition);
		    proposal.setFlags(((IMethod) element).getFlags());
		    break;
		case IModelElement.FIELD:
		    proposal = this.createProposal(
			    CompletionProposal.FIELD_REF,
			    this.actualCompletionPosition);
		    proposal.setFlags(((IField) element).getFlags());
		    break;
		case IModelElement.TYPE:
		    proposal = this.createProposal(CompletionProposal.TYPE_REF,
			    this.actualCompletionPosition);
		    proposal.setFlags(((IType) element).getFlags());
		    break;
		default:
		    proposal = this.createProposal(CompletionProposal.KEYWORD,
			    this.actualCompletionPosition);
		    break;
		}
	    }
	    proposal.setName(name);
	    proposal.setCompletion(name);
	    proposal.setReplaceRange(actualCompletionPosition - offset,
		    actualCompletionPosition - offset);
	    proposal.setRelevance(20);
	    proposal.setModelElement(element);
	    this.requestor.accept(proposal);
	} catch (Exception e) {
	}
    }

}
