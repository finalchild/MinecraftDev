/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2017 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mixin.util

import com.demonwav.mcdev.util.equivalentTo
import com.demonwav.mcdev.util.findMatchingMethods
import com.demonwav.mcdev.util.memberReference
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMember
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.compiled.ClsMethodImpl
import com.intellij.psi.util.PsiUtil
import com.intellij.util.containers.stream
import org.jetbrains.annotations.Contract
import java.util.stream.Collectors
import java.util.stream.Stream

@Contract(pure = true)
fun findMethods(psiClass: PsiClass): Stream<PsiMethod>? {
    val targets = psiClass.mixinTargets
    return when (targets.size) {
        0 -> null
        1 -> targets.single().methods.stream()
                .filter({!it.isConstructor})
        else -> targets.stream()
                .flatMap { target -> target.methods.stream() }
                .filter({!it.isConstructor})
                .collect(Collectors.groupingBy(PsiMethod::memberReference))
                .values.stream()
                .filter { it.size >= targets.size }
                .map { it.first() }
    }?.filter { m ->
        // Filter methods which are already in the Mixin class
        psiClass.findMatchingMethods(m, false).isEmpty()
    }
}

@Contract(pure = true)
fun findMethodsDeep(psiClass: PsiClass, start: PsiClass): Stream<ShadowTarget> {
    return start.streamMixinHierarchy()
        .flatMap { mixin ->
            findMethods(mixin)
                ?.filterAccessible(psiClass, mixin)
                ?.map { ShadowTarget(mixin, it) } ?: Stream.empty()
        }
}

@Contract(pure = true)
fun findFields(psiClass: PsiClass): Stream<PsiField>? {
    val targets = psiClass.mixinTargets
    return when (targets.size) {
        0 -> null
        1 -> targets.single().fields.stream()
        else -> targets.stream()
                .flatMap { target -> target.fields.stream() }
                .collect(Collectors.groupingBy(PsiField::memberReference))
                .values.stream()
                .filter { it.size >= targets.size }
                .map { it.first() }
    }?.filter {
        // Filter fields which are already in the Mixin class
        psiClass.findFieldByName(it.name, false) == null
    }
}

@Contract(pure = true)
fun findFieldsDeep(psiClass: PsiClass, start: PsiClass): Stream<ShadowTarget> {
    return start.streamMixinHierarchy()
        .flatMap { mixin ->
            findFields(mixin)
                ?.filterAccessible(psiClass, mixin)
                ?.map { ShadowTarget(mixin, it) } ?: Stream.empty()
        }
}

@Contract(pure = true)
fun PsiMethod.findSource(): PsiMethod {
    val body = body
    if (body != null) {
        return this
    }

    // Attempt to find the source if we have a compiled method
    return (this as? ClsMethodImpl)?.sourceMirrorMethod ?: this
}

data class ShadowTarget(val mixin: PsiClass, val member: PsiMember)

private fun <T : PsiMember> Stream<T>.filterAccessible(psiClass: PsiClass, target: PsiClass): Stream<T> {
    return if (psiClass equivalentTo target) this else filter {
        PsiUtil.getAccessLevel(it.modifierList!!) >= PsiUtil.ACCESS_LEVEL_PROTECTED
    }
}

private fun PsiClass.streamMixinHierarchy(): Stream<PsiClass> {
    val builder = Stream.builder<PsiClass>()

    var currentClass: PsiClass = this
    do {
        builder.add(currentClass)
        currentClass = currentClass.superClass ?: break
    } while (currentClass.isMixin)

    return builder.build()
}
