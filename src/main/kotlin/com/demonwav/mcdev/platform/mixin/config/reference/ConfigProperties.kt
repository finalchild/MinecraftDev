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

import com.demonwav.mcdev.platform.mixin.util.MixinConstants.Classes.MIXIN_CONFIG
import com.demonwav.mcdev.platform.mixin.util.MixinConstants.Classes.SERIALIZED_NAME
import com.demonwav.mcdev.util.ReferenceResolver
import com.demonwav.mcdev.util.constantStringValue
import com.demonwav.mcdev.util.findAnnotation
import com.demonwav.mcdev.util.ifEmpty
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiFile
import com.intellij.util.ArrayUtil

object ConfigProperties : ReferenceResolver() {

    override fun resolveReference(context: PsiElement): PsiElement? {
        val configClass = findConfigClass(context) ?: return null
        return findProperty(configClass, (context as JsonStringLiteral).value)
    }

    override fun collectVariants(context: PsiElement): Array<Any> {
        val configClass = findConfigClass(context) ?: return ArrayUtil.EMPTY_OBJECT_ARRAY

        val list = ArrayList<LookupElementBuilder>()
        forEachProperty(configClass) { _, name ->
            list.add(LookupElementBuilder.create(name))
        }
        return list.toArray()
    }

    private fun findProperty(configClass: PsiClass, name: String): PsiField? {
        forEachProperty(configClass) { field, fieldName ->
            if (fieldName == name) {
                return field
            }
        }

        return null
    }

    private inline fun forEachProperty(configClass: PsiClass, func: (PsiField, String) -> Unit) {
        for (field in configClass.fields) {
            val name = field.findAnnotation(SERIALIZED_NAME)?.findDeclaredAttributeValue(null)?.constantStringValue ?: continue
            func(field, name)
        }
    }

    private fun findConfigClass(context: PsiElement): PsiClass? {
        val mixinConfig = JavaPsiFacade.getInstance(context.project).findClass(MIXIN_CONFIG, context.resolveScope) ?: return null

        val property = context.parent as JsonProperty

        val path = ArrayList<String>()

        var current = property.parent
        while (current != null && current !is PsiFile) {
            if (current is JsonProperty) {
                path.add(current.name)
            }
            current = current.parent
        }

        path.ifEmpty { return mixinConfig }

        // Walk to correct class
        var currentClass = mixinConfig
        for (i in path.lastIndex downTo 0) {
            currentClass = (findProperty(currentClass, path[i])?.type as? PsiClassType)?.resolve() ?: return null
        }
        return currentClass
    }
}
