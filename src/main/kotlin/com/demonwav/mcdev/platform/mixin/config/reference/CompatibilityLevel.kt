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

import com.demonwav.mcdev.platform.mixin.util.MixinConstants.Classes.COMPATIBILITY_LEVEL
import com.demonwav.mcdev.util.ReferenceResolver
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiEnumConstant
import com.intellij.util.ArrayUtil

object CompatibilityLevel : ReferenceResolver() {

    override fun resolveReference(context: PsiElement): PsiElement? {
        val value = (context as JsonStringLiteral).value
        val compatibilityLevel = findCompatibilityLevel(context)
        return compatibilityLevel?.findFieldByName(value, false)?.takeIf { it is PsiEnumConstant }
    }

    override fun collectVariants(context: PsiElement): Array<Any> {
        val compatibilityLevel = findCompatibilityLevel(context) ?: return ArrayUtil.EMPTY_OBJECT_ARRAY
        val list = ArrayList<LookupElementBuilder>()
        for (field in compatibilityLevel.fields) {
            if (field !is PsiEnumConstant) {
                continue
            }

            list.add(LookupElementBuilder.create(field.name!!))
        }

        return list.toArray()
    }

    private fun findCompatibilityLevel(context: PsiElement) =
        JavaPsiFacade.getInstance(context.project).findClass(COMPATIBILITY_LEVEL, context.resolveScope)
}
