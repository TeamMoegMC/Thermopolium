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
		CompoundNBT cnbt = stack.write(nbt);
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
		return stack.isDamageable();
	}

	public boolean isDamaged() {
		return stack.isDamaged();
	}

	public int getDamage() {
		return stack.getDamage();
	}

	public void setDamage(int damage) {
		stack.setDamage(damage);
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
		return new FloatemStack(stack.copy(),this.count);
	}

	public boolean isItemEqual(ItemStack other) {
		return stack.isItemEqual(other);
	}

	public boolean isItemEqualIgnoreDurability(ItemStack stack) {
		return stack.isItemEqualIgnoreDurability(stack);
	}

	public String getTranslationKey() {
		return stack.getTranslationKey();
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
		return stack.getOrCreateChildTag(key);
	}

	public CompoundNBT getChildTag(String key) {
		return stack.getChildTag(key);
	}

	public void removeChildTag(String p_196083_1_) {
		stack.removeChildTag(p_196083_1_);
	}

	public void setTag(CompoundNBT nbt) {
		stack.setTag(nbt);
	}

	public ITextComponent getDisplayName() {
		return stack.getDisplayName();
	}

	public ItemStack setDisplayName(ITextComponent name) {
		return stack.setDisplayName(name);
	}

	public void clearCustomName() {
		stack.clearCustomName();
	}

	public boolean hasDisplayName() {
		return stack.hasDisplayName();
	}

	public boolean hasEffect() {
		return stack.hasEffect();
	}

	public Rarity getRarity() {
		return stack.getRarity();
	}

	public void setTagInfo(String key, INBT value) {
		stack.setTagInfo(key, value);
	}

	public ITextComponent getTextComponent() {
		return stack.getTextComponent();
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
		return stack.isFood();
	}

	public boolean equals(ItemStack other) {
		if (this.getItem() != other.getItem()) {
			return false;
		}
		return ItemStack.areItemStackTagsEqual(this.getStack(), other);
	}

	public void deserializeNBT(CompoundNBT nbt) {
		stack=ItemStack.read(nbt);
		this.count = nbt.getFloat("th_countf");
	}
}
