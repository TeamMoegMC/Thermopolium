package com.teammoeg.thermopolium.items;



import java.util.List;

import com.teammoeg.thermopolium.Contents;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.util.SoupInfo;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
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
	@Override
	public boolean hasContainerItem() {
		return super.hasContainerItem();
	}
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return super.getUseAction(stack);
	}
	@Override
	public int getUseDuration(ItemStack stack) {
		return super.getUseDuration(stack);
	}
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	@Override
	public boolean isFood() {
		return super.isFood();
	}
	@Override
	public Food getFood() {
		return super.getFood();
	}
	public static SoupInfo getInfo(ItemStack stack) {
		return new SoupInfo(stack.getOrCreateChildTag("soup"));
	}
	public static void setInfo(ItemStack stack,SoupInfo si) {
		stack.getOrCreateTag().put("soup",si.save());
	}
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		return super.onItemUseFinish(stack, worldIn, entityLiving);
	}
	public StewItem(String name, Properties properties) {
		super(properties);
		setRegistryName(Main.MODID, name);
		Contents.registeredItems.add(this);
	}
}
