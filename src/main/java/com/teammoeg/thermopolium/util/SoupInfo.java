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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Comparators;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.thermopolium.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.thermopolium.data.recipes.FoodValueRecipe;
import com.teammoeg.thermopolium.data.recipes.SerializeUtil;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SoupInfo {
	public List<FloatemStack> stacks;
	public List<EffectInstance> effects;
	public List<Pair<EffectInstance, Float>> foodeffect = new ArrayList<>();
	public int healing;
	public float saturation;
	public float shrinkedFluid=0;
	public ResourceLocation base;

	public SoupInfo(List<FloatemStack> stacks, List<EffectInstance> effects, int healing, float saturation,
			ResourceLocation base) {
		super();
		this.stacks = stacks;
		this.effects = effects;
		this.healing = healing;
		this.saturation = saturation;
		this.base = base;
	}

	public SoupInfo() {
		this(new ArrayList<>(), new ArrayList<>(), 0, 0, new ResourceLocation("minecraft:water"));
	}

	public static List<FloatemStack> getStacks(CompoundNBT nbt) {
		return nbt.getList("items", 10).stream().map(e -> (CompoundNBT) e).map(FloatemStack::new)
				.collect(Collectors.toList());
	}

	public float getDensity() {
		return stacks.stream().map(FloatemStack::getCount).reduce(0f, Float::sum);
	}

	public SoupInfo(CompoundNBT nbt) {
		stacks = nbt.getList("items", 10).stream().map(e -> (CompoundNBT) e).map(FloatemStack::new)
				.collect(Collectors.toList());
		effects = nbt.getList("effects", 10).stream().map(e -> (CompoundNBT) e).map(EffectInstance::read)
				.collect(Collectors.toList());
		healing = nbt.getInt("heal");
		saturation = nbt.getFloat("sat");
		foodeffect = nbt.getList("feffects", 10).stream().map(e -> (CompoundNBT) e)
				.map(e -> new Pair<>(EffectInstance.read(e.getCompound("effect")), e.getFloat("chance")))
				.collect(Collectors.toList());
		base = new ResourceLocation(nbt.getString("base"));
		shrinkedFluid=nbt.getFloat("afluid");
	}

	public boolean isEmpty() {
		return stacks.isEmpty() && effects.isEmpty();
	}

	public boolean canMerge(SoupInfo f, float cparts, float oparts) {
		return (this.getDensity() * cparts + f.getDensity() * oparts) / (cparts + oparts) <= 3;
	}
	/*
	 *
	 * @param cparts current soup amout
	 * @param oparts new soup amout
	 **/
	public boolean merge(SoupInfo f, float cparts, float oparts) {
		if (!canMerge(f,cparts,oparts))
			return false;
		for (EffectInstance es : f.effects) {
			boolean added = false;
			for (EffectInstance oes : effects) {
				if (isEffectEquals(oes, es)) {
					oes.duration += es.duration * oparts / cparts;
					added = true;
					break;
				}
			}
			if (!added) {
				if (effects.size() < 3) {
					EffectInstance copy=new EffectInstance(es);
					copy.duration=(int) (copy.duration*oparts/cparts);
					effects.add(copy);
				}
			}
		}
		for (Pair<EffectInstance, Float> es : f.foodeffect) {
			boolean added = false;
			for (Pair<EffectInstance, Float> oes : foodeffect) {
				if (es.getSecond()==oes.getSecond()&&isEffectEquals(oes.getFirst(), es.getFirst())) {
					oes.getFirst().duration += es.getFirst().duration * oparts / cparts;
					added = true;
					break;
				}
			}
			if (!added) {
				foodeffect.add(es);
			}
		}
		shrinkedFluid+=f.shrinkedFluid * oparts / cparts;
		for (FloatemStack fs : f.stacks) {
			this.addItem(new FloatemStack(fs.getStack(), fs.count * oparts / cparts));
		}

		completeAll();
		return true;
	}
	public void completeAll() {
		completeData();
		completeEffects();
	}
	public void completeData() {
		stacks.sort(Comparator.comparingInt(e->Item.getIdFromItem(e.stack.getItem())));
		foodeffect.sort(Comparator.<Pair<EffectInstance,Float>>comparingInt(e->Effect.getId(e.getFirst().getPotion())).thenComparing(Pair::getSecond));
	}
	public void completeEffects() {
		effects.sort(Comparator.<EffectInstance>comparingInt(x->Effect.getId(x.getPotion())).thenComparingInt(e->e.getDuration()));
	}
	public static boolean isEffectEquals(EffectInstance t1, EffectInstance t2) {
		return t1.getPotion() == t2.getPotion() && t1.getAmplifier() == t2.getAmplifier();
	}

	public void addEffect(EffectInstance eff, float parts) {

		for (EffectInstance oes : effects) {
			if (isEffectEquals(oes, eff)) {
				oes.duration =Math.max(oes.duration,(int)Math.min(oes.duration+eff.duration / parts,eff.duration*2f));
				return;
			}
		}
		if (effects.size() < 3) {
			EffectInstance copy = new EffectInstance(eff);
			copy.duration /= parts;
			effects.add(copy);
		}
	}

	public void recalculateHAS() {
		foodeffect.clear();
		float nh = 0;
		float ns = 0;
		for (FloatemStack fs : stacks) {
			FoodValueRecipe fvr = FoodValueRecipe.recipes.get(fs.getItem());
			if (fvr != null) {
				nh += fvr.heal * fs.count;
				ns += fvr.sat * fvr.heal * fs.count;
				foodeffect.addAll(fvr.effects);
				continue;
			}
			Food f = fs.getItem().getFood();
			if (f != null) {
				nh += fs.count * f.getHealing();
				ns += fs.count * f.getSaturation() * f.getHealing();
				foodeffect.addAll(f.getEffects());
			}
		}
		FluidFoodValueRecipe ffvr=FluidFoodValueRecipe.recipes.get(this.base);
		if(ffvr!=null) {
			nh+=ffvr.heal*(1+this.shrinkedFluid);
			ns+=ffvr.sat*(1+this.shrinkedFluid)/2;
		}
		float dense = this.getDensity();
		int conv = (int) (MathHelper.clamp((dense - 1) / 2f, 0, 1) * 0.3 * nh);
		this.healing = (int) Math.ceil(nh - conv);
		ns += conv / 2f;
		if(this.healing>0)
			this.saturation = Math.max(0.7f, ns / this.healing);
		else
			this.saturation=0;
	}

	public void adjustParts(float oparts, float parts) {
		if (oparts == parts)
			return;
		for (FloatemStack fs : stacks) {
			fs.setCount(fs.getCount() * oparts / parts);
		}

		for (EffectInstance es : effects) {
			es.duration = (int) (es.duration * oparts / parts);
		}
		float delta=0;
		if(oparts>parts)
			delta=oparts-parts;
		shrinkedFluid=(shrinkedFluid*oparts+delta)/parts;
		healing = (int) (healing * oparts / parts);
		saturation = saturation * oparts / parts;
	}

	public CompoundNBT save() {
		CompoundNBT nbt = new CompoundNBT();
		write(nbt);
		return nbt;
	}

	public SoupInfo(ResourceLocation base) {
		this(new ArrayList<>(), new ArrayList<>(), 0, 0, base);
	}

	public static String getRegName(CompoundNBT nbt) {
		return nbt.getString("base");
	}

	public void addItem(ItemStack is, float parts) {
		for (FloatemStack i : stacks) {
			if (i.equals(is)) {
				i.count += is.getCount() / parts;
				return;
			}
		}
		stacks.add(new FloatemStack(is.copy(), is.getCount() / parts));
	}

	public void addItem(FloatemStack is) {
		for (FloatemStack i : stacks) {
			if (i.equals(is.getStack())) {
				i.count += is.count;
				return;
			}
		}
		stacks.add(is);
	}

	public void write(CompoundNBT nbt) {
		nbt.put("items", SerializeUtil.toNBTList(stacks, FloatemStack::serializeNBT));
		nbt.put("effects", SerializeUtil.toNBTList(effects, e -> e.write(new CompoundNBT())));
		nbt.put("feffects", SerializeUtil.toNBTList(foodeffect, e -> {
			CompoundNBT cnbt = new CompoundNBT();
			cnbt.put("effect", e.getFirst().write(new CompoundNBT()));
			cnbt.putFloat("chance", e.getSecond());
			return cnbt;
		}));
		nbt.putInt("heal", healing);
		nbt.putFloat("sat", saturation);
		nbt.putString("base", base.toString());
		nbt.putFloat("afluid",shrinkedFluid);
	}

}
