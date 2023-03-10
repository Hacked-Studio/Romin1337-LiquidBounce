/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.ListValue

import net.minecraft.util.ResourceLocation

@ModuleInfo(name = "Cape", description = "LiquidBounce+ capes.", category = ModuleCategory.RENDER)
class Cape : Module() {

    val styleValue = ListValue("style", arrayOf("Dark", "Darker", "Light", "Special1", "Special2","MineCon2011","MineCon2012","MineCon2013","MineCon2015","MineCon2016"), "Dark")

    private val capeCache = hashMapOf<String, CapeStyle>()

    fun getCapeLocation(value: String): ResourceLocation {
        if (capeCache[value.toUpperCase()] == null) {
            try {
                capeCache[value.toUpperCase()] = CapeStyle.valueOf(value.toUpperCase())
            } catch (e: Exception) {
                capeCache[value.toUpperCase()] = CapeStyle.DARK
            }
        }
        return capeCache[value.toUpperCase()]!!.location
    }

    enum class CapeStyle(val location: ResourceLocation) {
        DARK(ResourceLocation("liquidbounce+/cape/dark.png")),
        DARKER(ResourceLocation("liquidbounce+/cape/darker.png")),
        LIGHT(ResourceLocation("liquidbounce+/cape/light.png")),
        SPECIAL1(ResourceLocation("liquidbounce+/cape/special1.png")),
        SPECIAL2(ResourceLocation("liquidbounce+/cape/special2.png")),
        MINECON2011(ResourceLocation("liquidbounce+/cape/2011.png")),
        MINECON2012(ResourceLocation("liquidbounce+/cape/2012.png")),
        MINECON2013(ResourceLocation("liquidbounce+/cape/2013.png")),
        MINECON2015(ResourceLocation("liquidbounce+/cape/2015.png")),
        MINECON2016(ResourceLocation("liquidbounce+/cape/2016.png"))
    }

    override val tag: String
        get() = styleValue.get()

}