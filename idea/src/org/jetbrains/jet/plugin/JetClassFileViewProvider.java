/*
 * Copyright 2000-2012 JetBrains s.r.o.
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

package org.jetbrains.jet.plugin;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.psi.JetFile;

/**
 * @author Evgeny Gerashchenko
 * @since 3/2/12
 */
public class JetClassFileViewProvider extends SingleRootFileViewProvider {
    private String myText;

    public JetClassFileViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile file, String text) {
        super(manager, file, false, JetLanguage.INSTANCE);
        myText = text;
    }

    @NotNull
    @Override
    public CharSequence getContents() {
        return myText;
    }

    @Override
    protected PsiFile createFile(@NotNull Project project, @NotNull VirtualFile file, @NotNull FileType fileType) {
        return createFile(getBaseLanguage());
    }

    @Override
    public boolean isPhysical() {
        return false;
    }

    @NotNull
    public JetFile getJetFile() {
        PsiFile psiInner = getPsi(JetLanguage.INSTANCE);
        assert psiInner instanceof JetFile;
        return (JetFile) psiInner;
    }
}
