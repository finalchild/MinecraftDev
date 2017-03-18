/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2017 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.bukkit.framework

import com.demonwav.mcdev.asset.PlatformAssets
import com.demonwav.mcdev.facet.MavenLibraryPresentationProvider
import com.intellij.openapi.roots.libraries.LibraryProperties

class BukkitPresentationProvider : MavenLibraryPresentationProvider(BUKKIT_LIBRARY_KIND, "org.bukkit", "bukkit") {
    override fun getIcon(properties: LibraryProperties<*>?) = PlatformAssets.BUKKIT_ICON
}

class PaperPresentationProvider : MavenLibraryPresentationProvider(PAPER_LIBRARY_KIND, "com.destroystokyo.paper", "paper-api") {
    override fun getIcon(properties: LibraryProperties<*>?) = PlatformAssets.PAPER_ICON
}

class SpigotPresentationProvider : MavenLibraryPresentationProvider(SPIGOT_LIBRARY_KIND, "org.spigotmc", "spigot-api") {
    override fun getIcon(properties: LibraryProperties<*>?) = PlatformAssets.SPIGOT_ICON
}
