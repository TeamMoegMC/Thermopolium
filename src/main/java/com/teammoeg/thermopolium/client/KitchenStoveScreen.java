/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Thermopolium.
 *
 * Thermopolium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Thermopolium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Thermopolium. If not, see <https://www.gnu.org/licenses/>.
 */

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
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		this.renderBackground(matrixStack);
		Minecraft.getInstance().getTextureManager().bind(TEXTURE);

		this.blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		if (te.processMax > 0 && te.process > 0) {
			int h = (int) (20 * (1-te.process / (float) te.processMax));
			this.blit(matrixStack, leftPos + 73, topPos + 7 + h, 176,h, 31,20 - h);
		}
	}

}
