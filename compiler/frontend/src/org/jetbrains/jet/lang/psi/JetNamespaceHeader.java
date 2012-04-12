/*
 * Copyright 2010-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceService;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.JetNodeTypes;
import org.jetbrains.jet.lang.resolve.FqName;

import java.util.List;

/**
 * @author abreslav
 */
public class JetNamespaceHeader extends JetReferenceExpression {
    public JetNamespaceHeader(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    public List<JetSimpleNameExpression> getParentNamespaceNames() {
        List<JetSimpleNameExpression> parentParts = findChildrenByType(JetNodeTypes.REFERENCE_EXPRESSION);
        JetSimpleNameExpression lastPart = (JetSimpleNameExpression)findLastChildByType(JetNodeTypes.REFERENCE_EXPRESSION);
        parentParts.remove(lastPart);
        return parentParts;
    }

    @Nullable
    public JetSimpleNameExpression getLastPartExpression() {
        return (JetSimpleNameExpression)findLastChildByType(JetNodeTypes.REFERENCE_EXPRESSION);
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this, PsiReferenceService.Hints.NO_HINTS);
    }

    @Nullable
    @Override
    public PsiReference getReference() {
        PsiReference[] references = getReferences();
        return references.length == 1 ? references[0] : null;
    }

    @Nullable
    public PsiElement getNameIdentifier() {
        JetSimpleNameExpression lastPart = (JetSimpleNameExpression)findLastChildByType(JetNodeTypes.REFERENCE_EXPRESSION);
        if (lastPart == null) {
            return null;
        }

        return lastPart.getIdentifier();
    }


    private static final Key<FqName> FQ_NAME_KEY = Key.create(FqName.class.getSimpleName());

    @NotNull
    public FqName getFqName() {
        FqName fqName = getUserData(FQ_NAME_KEY);
        if (fqName == null) {
            fqName = getFqNameImpl();
            putUserDataIfAbsent(FQ_NAME_KEY, fqName);
        }
        return fqName;
    }

    @NotNull
    private FqName getFqNameImpl() {
        StringBuilder builder = new StringBuilder();
        for (JetSimpleNameExpression nameExpression : getParentNamespaceNames()) {
            // TODO: we can cache parents right here
            builder.append(nameExpression.getReferencedName());
            builder.append(".");
        }
        builder.append(getName());
        return new FqName(builder.toString());
    }

    @Override
    @NotNull
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    public boolean isRoot() {
        return getName().length() == 0;
    }
}

