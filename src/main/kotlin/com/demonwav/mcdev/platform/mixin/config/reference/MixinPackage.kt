/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2017 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mixin.config.reference

import com.demonwav.mcdev.platform.mixin.util.MixinConstants.Annotations.MIXIN
import com.demonwav.mcdev.util.ReferenceResolver
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.search.searches.AnnotatedElementsSearch
import com.intellij.util.ArrayUtil
import com.intellij.util.PlatformIcons

object MixinPackage : ReferenceResolver() {

    override fun resolveReference(context: PsiElement): PsiElement? {
        val value = (context as JsonStringLiteral).value
        return JavaPsiFacade.getInstance(context.project).findPackage(value)
    }

    override fun collectVariants(context: PsiElement): Array<Any> {
        val mixinAnnotation = JavaPsiFacade.getInstance(context.project).findClass(MIXIN, context.resolveScope)
            ?: return ArrayUtil.EMPTY_OBJECT_ARRAY

        val packages = HashSet<String>()
        val list = ArrayList<LookupElementBuilder>()

        for (mixin in AnnotatedElementsSearch.searchPsiClasses(mixinAnnotation, context.resolveScope)) {
            val packageName = (mixin.containingFile as? PsiJavaFile)?.packageName ?: continue
            if (packages.add(packageName)) {
                list.add(LookupElementBuilder.create(packageName).withIcon(PlatformIcons.PACKAGE_ICON))
            }
        }

        return list.toArray()
    }
}
