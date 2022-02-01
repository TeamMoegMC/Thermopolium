package com.teammoeg.thermopolium.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.blocks.KitchenStoveTileEntity;
import com.teammoeg.thermopolium.container.KitchenStoveContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class KitchenStoveScreen extends ContainerScreen<KitchenStoveContainer> {
	KitchenStoveTileEntity te;
	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID,
			"textures/gui/kitchen_stove.png");
	public KitchenStoveScreen(KitchenStoveContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		te=screenContainer.tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		this.renderBackground(matrixStack);
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);

		this.blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize);
		if (te.processMax > 0 && te.process > 0) {
			int h = (int) (20 * (1-te.process / (float) te.processMax));
			this.blit(matrixStack, guiLeft + 73, guiTop + 7 + h, 176,h, 31,20 - h);
		}
	}

}
