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

import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.koneki.ldt.editor.Activator;

/**
 * 
 * @author Kevin KIN-FOO <kkinfoo@sierrawireless.com>
 * 
 */
public class LuaCompletionEngine extends ScriptCompletionEngine {

	@Override
	public void complete(IModuleSource module, int position, int k) {
		this.requestor.beginReporting();
		this.actualCompletionPosition = position;
		String start = getWordStarting(module.getSourceContents(), position, 10);
		this.offset = actualCompletionPosition - start.length();
		this.setSourceRange(position - start.length(), position);
		// Some keywords are not listed hereby, they are defined in default template
		String[] keywords = new String[] { "and", "break", "do", "else", "elseif", "end", "false", "in", "nil", "not", "or", "return", "then",
				"true", };
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
				Activator.logError("Unable to initialize completion engine", e); //$NON-NLS-1$
			}
		} finally {
			this.requestor.endReporting();
		}
	}

	private void createProposal(String name, IModelElement element) {
		CompletionProposal proposal = null;
		try {
			if (element == null) {
				proposal = this.createProposal(CompletionProposal.KEYWORD, this.actualCompletionPosition);
			} else {
				switch (element.getElementType()) {
				case IModelElement.METHOD:
					proposal = this.createProposal(CompletionProposal.METHOD_DECLARATION, this.actualCompletionPosition);
					IMethod method = (IMethod) element;
					proposal.setFlags(method.getFlags());
					proposal.setParameterNames(method.getParameterNames());
					break;
				case IModelElement.FIELD:
					proposal = this.createProposal(CompletionProposal.FIELD_REF, this.actualCompletionPosition);
					proposal.setFlags(((IField) element).getFlags());
					break;
				case IModelElement.TYPE:
					proposal = this.createProposal(CompletionProposal.TYPE_REF, this.actualCompletionPosition);
					proposal.setFlags(((IType) element).getFlags());
					break;
				case IModelElement.LOCAL_VARIABLE:
					proposal = this.createProposal(CompletionProposal.LOCAL_VARIABLE_REF, this.actualCompletionPosition);
					proposal.setFlags(Flags.AccPrivate);
					break;
				default:
					proposal = this.createProposal(CompletionProposal.KEYWORD, this.actualCompletionPosition);
					proposal.setFlags(Flags.AccDefault);
					break;
				}
			}
			proposal.setName(name);
			proposal.setCompletion(name);
			proposal.setReplaceRange(offset, offset + name.length() - 1);
			proposal.setRelevance(20);
			proposal.setModelElement(element);
			this.requestor.accept(proposal);
		} catch (ModelException e) {
			Activator.logWarning("Problem during completion processing.", e); //$NON-NLS-1$
		}
	}

	private String getWordStarting(String content, int position, int maxLen) {
		if (position <= 0 || position > content.length())
			return Util.EMPTY_STRING;
		final int original = position;
		while (position > 0
				&& maxLen > 0
				&& ((content.charAt(position - 1) == ':') || (content.charAt(position - 1) == '\'') || (content.charAt(position - 1) == '"') || isLessStrictIdentifierCharacter(content
						.charAt(position - 1)))) {
			--position;
			--maxLen;
		}
		return content.substring(position, original);
	}

	private static boolean isLessStrictIdentifierCharacter(char ch) {
		boolean isLowercaseLetter = ch >= 'a' && ch <= 'z';
		boolean isUppercaseLetter = ch >= 'A' && ch <= 'Z';
		return isLowercaseLetter || isUppercaseLetter || ch == '_';
	}
}
