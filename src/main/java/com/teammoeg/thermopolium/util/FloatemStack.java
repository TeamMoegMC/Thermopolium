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

package com.teammoeg.thermopolium.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class FloatemStack {
	ItemStack stack;
	float count;

	public FloatemStack(ItemStack stack, float count) {
		super();
		this.stack = stack.copy();
		this.stack.setCount(1);
		this.count = count;
	}

	public FloatemStack(CompoundNBT nbt) {
		super();
		this.deserializeNBT(nbt);
	}

	public FloatemStack(ItemStack is) {
		this(is, is.getCount());
	}

	public ItemStack getStack() {
		return stack.getStack();
	}

	public ItemStack getContainerItem() {
		return stack.getContainerItem();
	}

	public boolean hasContainerItem() {
		return stack.hasContainerItem();
	}

	public CompoundNBT serializeNBT() {
		CompoundNBT cnbt = stack.serializeNBT();
		cnbt.putFloat("th_countf", count);
		return cnbt;
	}

	public boolean isEmpty() {
		return count <= 0.001;
	}

	public Item getItem() {
		return stack.getItem();
	}

	public int getEntityLifespan(World world) {
		return stack.getEntityLifespan(world);
	}

	public CompoundNBT write(CompoundNBT nbt) {
		CompoundNBT cnbt = stack.save(nbt);
		cnbt.putFloat("th_countf", count);
		return cnbt;
	}

	public int getMaxStackSize() {
		return stack.getMaxStackSize();
	}

	public boolean isStackable() {
		return stack.isStackable();
	}

	public boolean isDamageable() {
		return stack.isDamageableItem();
	}

	public boolean isDamaged() {
		return stack.isDamaged();
	}

	public int getDamage() {
		return stack.getDamageValue();
	}

	public void setDamage(int damage) {
		stack.setDamageValue(damage);
	}

	public int getMaxDamage() {
		return stack.getMaxDamage();
	}

	public CompoundNBT getShareTag() {
		return stack.getShareTag();
	}

	public void readShareTag(CompoundNBT nbt) {
		stack.readShareTag(nbt);
	}

	public boolean areShareTagsEqual(ItemStack other) {
		return stack.areShareTagsEqual(other);
	}

	public FloatemStack copy() {
		return new FloatemStack(stack.copy(), this.count);
	}

	public boolean isItemEqual(ItemStack other) {
		return stack.sameItem(other);
	}

	public boolean isItemEqualIgnoreDurability(ItemStack stack) {
		return stack.sameItemStackIgnoreDurability(stack);
	}

	public String getTranslationKey() {
		return stack.getDescriptionId();
	}

	public boolean hasTag() {
		return stack.hasTag();
	}

	public CompoundNBT getTag() {
		return stack.getTag();
	}

	public CompoundNBT getOrCreateTag() {
		return stack.getOrCreateTag();
	}

	public CompoundNBT getOrCreateChildTag(String key) {
		return stack.getOrCreateTagElement(key);
	}

	public CompoundNBT getChildTag(String key) {
		return stack.getTagElement(key);
	}

	public void removeChildTag(String p_196083_1_) {
		stack.removeTagKey(p_196083_1_);
	}

	public void setTag(CompoundNBT nbt) {
		stack.setTag(nbt);
	}

	public ITextComponent getDisplayName() {
		return stack.getHoverName();
	}

	public ItemStack setDisplayName(ITextComponent name) {
		return stack.setHoverName(name);
	}

	public void clearCustomName() {
		stack.resetHoverName();
	}

	public boolean hasDisplayName() {
		return stack.hasCustomHoverName();
	}

	public boolean hasEffect() {
		return stack.hasFoil();
	}

	public Rarity getRarity() {
		return stack.getRarity();
	}

	public void setTagInfo(String key, INBT value) {
		stack.addTagElement(key, value);
	}

	public ITextComponent getTextComponent() {
		return stack.getDisplayName();
	}

	public float getCount() {
		return count;
	}

	public void setCount(float count) {
		this.count = count;
	}

	public void grow(float count) {
		this.count += count;
	}

	public void shrink(float count) {
		this.count -= count;
		if (this.count < 0)
			this.count = 0;
	}

	public boolean isFood() {
		return stack.isEdible();
	}

	public boolean equals(ItemStack other) {
		if (this.getItem() != other.getItem()) {
			return false;
		}
		return ItemStack.tagMatches(this.getStack(), other);
	}

	public void deserializeNBT(CompoundNBT nbt) {
		stack = ItemStack.of(nbt);
		this.count = nbt.getFloat("th_countf");
	}
}
