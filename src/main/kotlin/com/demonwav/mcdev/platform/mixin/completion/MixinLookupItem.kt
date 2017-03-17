/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2017 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mixin.completion

import com.demonwav.mcdev.platform.mixin.action.insertShadows
import com.demonwav.mcdev.platform.mixin.util.ShadowTarget
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.completion.JavaMethodCallElement
import com.intellij.codeInsight.lookup.VariableLookupItem
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiVariable
import com.intellij.psi.impl.source.PostprocessReformattingAspect
import java.util.stream.Stream

class MixinMethodLookupItem(private val shadow: ShadowTarget) : JavaMethodCallElement(shadow.member as PsiMethod) {

    override fun handleInsert(context: InsertionContext) {
        insertShadow(context, shadow)
        super.handleInsert(context)
    }
}

class MixinFieldLookupItem(private val shadow: ShadowTarget) : VariableLookupItem(shadow.member as PsiField) {

    override fun handleInsert(context: InsertionContext) {
        insertShadow(context, shadow)

        // Replace object with proxy object so super doesn't qualify the reference
        `object` = ShadowField(`object`)
        super.handleInsert(context)
    }

    private class ShadowField(variable: PsiVariable) : PsiVariable by variable
}

private fun insertShadow(context: InsertionContext, shadow: ShadowTarget) {
    // Insert @Shadow element
    insertShadows(context.project, shadow.mixin, Stream.of(shadow.member))
    PostprocessReformattingAspect.getInstance(context.project).doPostponedFormatting()
}
