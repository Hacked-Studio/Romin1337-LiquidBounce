/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.modules.render.AntiBlind;
import net.ccbluex.liquidbounce.features.module.modules.render.Crosshair;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.features.module.modules.misc.AutoHypixel;
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer;
import net.ccbluex.liquidbounce.utils.ClassUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.lwjgl.opengl.GL11;

@Mixin(GuiIngame.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiInGame extends MixinGui {

    @Shadow
    protected abstract void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player);

    @Shadow @Final
    protected static ResourceLocation widgetsTexPath;

    @Shadow public GuiPlayerTabOverlay overlayPlayerList;

    @Inject(method = "showCrosshair", at = @At("HEAD"), cancellable = true) 
    private void injectCrosshair(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final Crosshair crossHair = (Crosshair) LiquidBounce.moduleManager.getModule(Crosshair.class);
        if (crossHair.getState() && crossHair.noVanillaCH.get())
            callbackInfoReturnable.setReturnValue(false);
    }

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    private void renderScoreboard(ScoreObjective scoreObjective, ScaledResolution scaledResolution, CallbackInfo callbackInfo) {
        if (scoreObjective != null) AutoHypixel.gameMode = ColorUtils.stripColor(scoreObjective.getDisplayName());

        final AntiBlind antiBlind = (AntiBlind) LiquidBounce.moduleManager.getModule(AntiBlind.class);
        if ((antiBlind.getState() && antiBlind.getScoreBoard().get()) || LiquidBounce.moduleManager.getModule(HUD.class).getState())
            callbackInfo.cancel();
    }

    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    private void renderBossHealth(CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = (AntiBlind) LiquidBounce.moduleManager.getModule(AntiBlind.class);
        if (antiBlind.getState() && antiBlind.getBossHealth().get())
            callbackInfo.cancel();
    }

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        final HUD hud = (HUD) LiquidBounce.moduleManager.getModule(HUD.class);

        //GlStateManager.pushMatrix();
        GlStateManager.translate(0F, -RenderUtils.yPosOffset, 0F);

        if(Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer && hud.getState()) {
            final Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer entityPlayer = (EntityPlayer) mc.getRenderViewEntity();

            boolean blackHB = hud.getState() && hud.getBlackHotbarValue().get();
            int middleScreen = sr.getScaledWidth() / 2;
            float posInv = hud.getAnimPos(entityPlayer.inventory.currentItem * 20F);

            GlStateManager.resetColor();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(widgetsTexPath);

            float f = this.zLevel;
            this.zLevel = -90.0F;
            GlStateManager.resetColor();

            if (blackHB) {
                RenderUtils.originalRoundedRect(middleScreen - 91, sr.getScaledHeight() - 2, middleScreen + 91, sr.getScaledHeight() - 22, 3F, Integer.MIN_VALUE);
                RenderUtils.originalRoundedRect(middleScreen - 91 + posInv, sr.getScaledHeight() - 2, middleScreen - 91 + posInv + 21, sr.getScaledHeight() - 22, 3F, Integer.MAX_VALUE);
            } else {
                this.drawTexturedModalRect(middleScreen - 91F, sr.getScaledHeight() - 22, 0, 0, 182, 22);
                this.drawTexturedModalRect(middleScreen - 91F + posInv - 1, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
            }

            this.zLevel = f;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            if (blackHB) {
                for (int j = 0; j < 9; ++j) {
                    int k = sr.getScaledWidth() / 2 - 91 + j * 20 + 2;
                    int l = sr.getScaledHeight() - 16 - 3;
                    this.renderHotbarItem(j, k, l - 1, partialTicks, entityPlayer);
                }
            } else {
                for (int j = 0; j < 9; ++j) {
                    int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
                    int l = sr.getScaledHeight() - 16 - 3;
                    this.renderHotbarItem(j, k, l, partialTicks, entityPlayer);
                }
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();

            GlStateManager.translate(0F, RenderUtils.yPosOffset, 0F);
            LiquidBounce.eventManager.callEvent(new Render2DEvent(partialTicks));
            AWTFontRenderer.Companion.garbageCollectionTick();
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void renderTooltipPost(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        //GlStateManager.popMatrix();

        if (!ClassUtils.hasClass("net.labymod.api.LabyModAPI")) {
            LiquidBounce.eventManager.callEvent(new Render2DEvent(partialTicks));
            AWTFontRenderer.Companion.garbageCollectionTick();
        }
    }

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    private void renderPumpkinOverlay(final CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = (AntiBlind) LiquidBounce.moduleManager.getModule(AntiBlind.class);

        if(antiBlind.getState() && antiBlind.getPumpkinEffect().get())
            callbackInfo.cancel();
    }
}