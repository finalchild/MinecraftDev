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

import com.demonwav.mcdev.platform.mixin.util.MixinConstants.Classes.MIXIN_PLUGIN
import com.demonwav.mcdev.util.ReferenceResolver
import com.demonwav.mcdev.util.fullQualifiedName
import com.intellij.codeInsight.completion.JavaLookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.util.PsiUtil
import com.intellij.util.ArrayUtil

object MixinPlugin : ReferenceResolver() {

    override fun resolveReference(context: PsiElement): PsiElement? {
        val value = (context as JsonStringLiteral).value
        return JavaPsiFacade.getInstance(context.project).findClass(value, context.resolveScope)
    }

    override fun collectVariants(context: PsiElement): Array<Any> {
        val configInterface = JavaPsiFacade.getInstance(context.project).findClass(MIXIN_PLUGIN, context.resolveScope)
            ?: return ArrayUtil.EMPTY_OBJECT_ARRAY

        val list = ArrayList<LookupElementBuilder>()
        for (configClass in ClassInheritorsSearch.search(configInterface, configInterface.useScope, true, true, false)) {
            if (!PsiUtil.isInstantiatable(configClass)) continue
            list.add(JavaLookupElementBuilder.forClass(configClass, configClass.fullQualifiedName, true)
                .withPresentableText(configClass.name!!))
        }
        return list.toArray()
    }
}
