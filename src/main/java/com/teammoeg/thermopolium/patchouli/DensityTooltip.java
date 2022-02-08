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

package com.teammoeg.thermopolium.patchouli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.teammoeg.thermopolium.data.recipes.CookingRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

public class DensityTooltip implements ICustomComponent {
	boolean allow;
	int x,y,w,h;
	IVariable recipe;
	transient List<ITextComponent> density;
	public DensityTooltip() {
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		recipe=lookup.apply(recipe);
		ResourceLocation out=new ResourceLocation(recipe.asString());
		CookingRecipe cr=CookingRecipe.recipes.get(ForgeRegistries.FLUIDS.getValue(out));
		if(cr!=null) {
			density=new ArrayList<>();
			density.add(new TranslationTextComponent("recipe.thermopolium.density",cr.getDensity()));
		}
	}

	@Override
	public void build(int componentX, int componentY, int pageNum) {
	}

	@Override
	public void render(MatrixStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
		if(context.isAreaHovered(mouseX, mouseY,x,y,w,h))
			context.setHoverTooltipComponents(density);
	}

}
