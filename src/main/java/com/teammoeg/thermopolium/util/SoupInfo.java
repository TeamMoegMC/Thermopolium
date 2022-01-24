package com.teammoeg.thermopolium.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;

public class SoupInfo {
	public List<FloatemStack> stacks;
	public List<EffectInstance> effects;
	public int healing;
	public float saturation;
	public ResourceLocation base;
	
	public SoupInfo(List<FloatemStack> stacks, List<EffectInstance> effects, int healing, float saturation,ResourceLocation base) {
		super();
		this.stacks = stacks;
		this.effects = effects;
		this.healing = healing;
		this.saturation = saturation;
		this.base = base;
	}
	public SoupInfo() {
		this(new ArrayList<>(),new ArrayList<>(),0,0,new ResourceLocation("minecraft:water"));
	}
	public float getDensity() {
		return stacks.stream().map(FloatemStack::getCount).reduce(0f,Float::sum);
	}
	public SoupInfo(CompoundNBT nbt) {
		stacks=nbt.getList("items",10).stream().map(e->(CompoundNBT)e).map(FloatemStack::new).collect(Collectors.toList());
		effects=nbt.getList("effects",10).stream().map(e->(CompoundNBT)e).map(EffectInstance::read).collect(Collectors.toList());
		healing=nbt.getInt("heal");
		saturation=nbt.getFloat("saturation");
		base=new ResourceLocation(nbt.getString("base"));
	}
	public void adjustParts(float oparts,float parts) {
		for(FloatemStack fs:stacks) {
			fs.setCount(fs.getCount()*oparts/parts);
		}
		
		for(EffectInstance es:effects) {
			es.duration=(int) (es.duration*oparts/parts);
		}
		healing=(int) (healing*oparts/parts);
		saturation=saturation*oparts/parts;
	}
	public CompoundNBT save() {
		return new CompoundNBT();
	}
	public SoupInfo(ResourceLocation base) {
		this(new ArrayList<>(),new ArrayList<>(),0,0,base);
	}
	public static String getRegName(CompoundNBT nbt) {
		return nbt.getString("base");
	}
	public boolean canAddItem(int count) {
		return true;
	}
	public void addItem(ItemStack is) {
		for(FloatemStack i:stacks) {
			if(i.equals(is)) {
				i.count+=is.getCount();
				break;
			}
		}
	}
}
