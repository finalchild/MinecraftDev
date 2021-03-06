/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2017 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.mcp.at.completion

import com.intellij.codeInsight.completion.PrefixMatcher
import com.intellij.codeInsight.lookup.LookupElement

class SrgPrefixMatcher(prefix: String) : PrefixMatcher(prefix) {
    override fun prefixMatches(name: String) = true
    override fun cloneWithPrefix(prefix: String) = SrgPrefixMatcher(prefix)

    override fun prefixMatches(element: LookupElement): Boolean {
        if (element !is AtMcpLookupItem) {
            return false
        }

        return element.getPrettyText().contains(myPrefix, ignoreCase = true)
    }
}
