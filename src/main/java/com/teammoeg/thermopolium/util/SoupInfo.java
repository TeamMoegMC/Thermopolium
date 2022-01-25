package com.teammoeg.thermopolium.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.teammoeg.thermopolium.data.recipes.StewSerializer;
import com.teammoeg.thermopolium.fluid.SoupFluid;

import net.minecraft.item.Food;
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
	public static List<FloatemStack> getStacks(CompoundNBT nbt) {
		return nbt.getList("items",10).stream().map(e->(CompoundNBT)e).map(FloatemStack::new).collect(Collectors.toList());
	}
	public float getDensity() {
		return stacks.stream().map(FloatemStack::getCount).reduce(0f,Float::sum);
	}
	public SoupInfo(CompoundNBT nbt) {
		stacks=nbt.getList("items",10).stream().map(e->(CompoundNBT)e).map(FloatemStack::new).collect(Collectors.toList());
		effects=nbt.getList("effects",10).stream().map(e->(CompoundNBT)e).map(EffectInstance::read).collect(Collectors.toList());
		healing=nbt.getInt("heal");
		saturation=nbt.getFloat("sat");
		base=new ResourceLocation(nbt.getString("base"));
	}
	public boolean isEmpty() {
		return stacks.isEmpty()&&effects.isEmpty();
	}
	public boolean merge(SoupInfo f,float cparts,float oparts) {
		if(this.getDensity()+f.getDensity()*oparts/cparts>3)return false;
		if(!this.base.equals(f.base))return false;
		for(EffectInstance es:f.effects) {
			boolean added=false;
			for(EffectInstance oes:effects) {
				if(isEffectEquals(oes,es)) {
					oes.duration+=es.duration*oparts/cparts;
					added=true;
					break;
				}
			}
			if(!added) {
				if(effects.size()<3)
					effects.add(es);
			}
		}
		for(FloatemStack fs:f.stacks) {
			this.addItem(new FloatemStack(fs.getStack(),fs.count*oparts/cparts));
		}
		recalculateHAS();
		return true;
	}
	public static boolean isEffectEquals(EffectInstance t1,EffectInstance t2) {
		return t1.getPotion()==t2.getPotion()&&t1.getAmplifier()==t2.getAmplifier();
	}
	public void addEffect(EffectInstance eff,float parts) {
		
		for(EffectInstance oes:effects) {
			if(isEffectEquals(oes,eff)) {
				
				oes.duration+=eff.duration/parts;
				return;
			}
		}
		if(effects.size()<3) {
			EffectInstance copy=new EffectInstance(eff);
			copy.duration/=parts;
			effects.add(copy);
		}
	}
	public void recalculateHAS() {
		float nh=this.getDensity();
		float ns=0;
		for(FloatemStack fs:stacks) {

			Food f=fs.getItem().getFood();
			if(f!=null) {
				nh+=fs.count*f.getHealing();
				ns+=fs.count*f.getSaturation();
			}
		}
		this.healing=(int) Math.ceil(nh);
		this.saturation=ns;
	}
	public void adjustParts(float oparts,float parts) {
		if(oparts==parts)return;
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
		CompoundNBT nbt=new CompoundNBT();
		nbt.put("items",StewSerializer.toNBTList(stacks,FloatemStack::serializeNBT));
		nbt.put("effects",StewSerializer.toNBTList(effects,e->e.write(new CompoundNBT())));
		nbt.putInt("heal",healing);
		nbt.putFloat("sat",saturation);
		nbt.putString("base",base.toString());
		return nbt;
	}
	public SoupInfo(ResourceLocation base) {
		this(new ArrayList<>(),new ArrayList<>(),0,0,base);
	}
	public static String getRegName(CompoundNBT nbt) {
		return nbt.getString("base");
	}
	public void addItem(ItemStack is,float parts) {
		for(FloatemStack i:stacks) {
			if(i.equals(is)) {
				i.count+=is.getCount()/parts;
				return;
			}
		}
		stacks.add(new FloatemStack(is.copy(),is.getCount()/parts));
	}
	public void addItem(FloatemStack is) {
		for(FloatemStack i:stacks) {
			if(i.equals(is.getStack())) {
				i.count+=is.count;
				return;
			}
		}
		stacks.add(is);
	}
}
