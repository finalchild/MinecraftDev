/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2017 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mcp.at.psi.mixins.impl

import com.demonwav.mcdev.platform.mcp.at.AtElementFactory
import com.demonwav.mcdev.platform.mcp.at.psi.mixins.AtClassNameMixin
import com.demonwav.mcdev.util.*
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiClass

abstract class AtClassNameImplMixin(node: ASTNode) : ASTWrapperPsiElement(node), AtClassNameMixin {

    override val classNameValue
        get() = findQualifiedClass(project, classNameText)

    override val classNameText: String
        get() = classNameElement.text

    override fun setClassName(className: String) {
        replace(AtElementFactory.createClassName(project, className))
    }
}