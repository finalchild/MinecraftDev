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
import com.intellij.codeInsight.completion.JavaLookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.AnnotatedElementsSearch
import com.intellij.util.ArrayUtil

object MixinClass : ReferenceResolver() {

    override fun resolveReference(context: PsiElement): PsiElement? {
        val className = buildQualifiedClassName(context)
        return JavaPsiFacade.getInstance(context.project).findClass(className, context.resolveScope)
    }

    override fun collectVariants(context: PsiElement): Array<Any> {
        val mixinAnnotation = JavaPsiFacade.getInstance(context.project).findClass(MIXIN, context.resolveScope) ?:
            return ArrayUtil.EMPTY_OBJECT_ARRAY

        val packageName = findPackage(context)?.let { "$it." }
        val list = ArrayList<LookupElementBuilder>()
        for (mixin in AnnotatedElementsSearch.searchPsiClasses(mixinAnnotation, context.resolveScope)) {
            val lookupString = if (packageName != null) {
                val qualifiedName = mixin.qualifiedName!!
                if (!qualifiedName.startsWith(packageName)) {
                    continue
                }

                qualifiedName.removePrefix(packageName)
            } else {
                mixin.qualifiedName!!
            }

            list.add(JavaLookupElementBuilder.forClass(mixin, lookupString)
                .withPresentableText(mixin.name!!))
        }

        return list.toArray()
    }

    private fun buildQualifiedClassName(context: PsiElement): String {
        val value = (context as JsonStringLiteral).value
        return findPackage(context)?.let { "$it.$value" } ?: value
    }

    private fun findPackage(context: PsiElement): String? {
        // Literal -> Array -> Property -> Object
        val obj = context.parent?.parent?.parent as? JsonObject ?: return null
        return (obj.findProperty("package")?.value as? JsonStringLiteral)?.value
    }
}
