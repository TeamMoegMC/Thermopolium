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

package com.teammoeg.thermopolium.items;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.thermopolium.Contents;
import com.teammoeg.thermopolium.Contents.SCItems;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.util.FloatemStack;
import com.teammoeg.thermopolium.util.SoupInfo;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class StewItem extends Item {
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		return super.getContainerItem(itemStack);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return super.getItemStackLimit(stack);
	}

	public ItemStack onItemUseFinish(ItemStack itemstack, World worldIn, LivingEntity entityLiving) {

		SoupInfo si = getInfo(itemstack);
		if (!worldIn.isRemote) {
			for (EffectInstance eff : si.effects) {
				if (eff != null) {
					entityLiving.addPotionEffect(eff);
				}
			}
			Random r=entityLiving.getRNG();
			for(Pair<EffectInstance, Float> ef:si.foodeffect) {
				if(r.nextFloat()<ef.getSecond())
					entityLiving.addPotionEffect(ef.getFirst());
			}
		}
		if (entityLiving instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entityLiving;
			if (!worldIn.isRemote)
				player.getFoodStats().addStats(si.healing, si.saturation);
			if (player.abilities.isCreativeMode)
				return itemstack;

		}
		return new ItemStack(Items.BOWL);
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		playerIn.setActiveHand(handIn);
		return ActionResult.resultConsume(itemstack);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 16;
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	public static SoupInfo getInfo(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundNBT soupTag = stack.getChildTag("soup");
			return soupTag == null ? new SoupInfo(new ResourceLocation(stack.getTag().getString("type")))
					: new SoupInfo(soupTag);
		}
		return new SoupInfo();
	}

	public static void setInfo(ItemStack stack, SoupInfo si) {
		if (!si.isEmpty())
			stack.getOrCreateTag().put("soup", si.save());
	}

	public static List<FloatemStack> getItems(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundNBT nbt = stack.getChildTag("soup");
			if (nbt != null)
				return SoupInfo.getStacks(nbt);
		}
		return ImmutableList.of();
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isInGroup(group)) {
			ItemStack is = new ItemStack(this);
			is.getOrCreateTag().putString("type", fluid.toString());
			items.add(is);
		}
	}

	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	ResourceLocation fluid;
	// fake food to trick mechanics
	public static final Food fakefood = new Food.Builder().hunger(4).saturation(0.2f).setAlwaysEdible().fastToEat().meat()
			.build();

	public StewItem(String name, ResourceLocation fluid, Properties properties) {
		super(properties.food(fakefood));
		setRegistryName(Main.MODID, name);
		Contents.registeredItems.add(this);
		SCItems.stews.add(this);
		this.fluid = fluid;
	}
}
