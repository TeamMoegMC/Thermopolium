package com.teammoeg.thermopolium.mixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.teammoeg.thermopolium.items.StewItem;
import com.teammoeg.thermopolium.util.FloatemStack;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.diet.api.DietApi;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.api.IDietResult;
import top.theillusivec4.diet.common.impl.DietApiImpl;
import top.theillusivec4.diet.common.util.DietResult;
//As Diet's author refuse to add such a more flexible api, I have to resort to mixin.
@Mixin(DietApiImpl.class)
public class DietApiImplMixin extends DietApi {
	@Inject(at=@At("HEAD"),require=1,method="get(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Ltop/theillusivec4/diet/api/IDietResult;",cancellable=true,remap=false)
	public void get(PlayerEntity player, ItemStack input,CallbackInfoReturnable<IDietResult> result) {
		if(input.getItem() instanceof StewItem) {
			List<FloatemStack> is=StewItem.getItems(input);
			Map<IDietGroup, Float> groups=new HashMap<>();
			for(FloatemStack s:is) {
				IDietResult dr=DietApiImpl.getInstance().get(player,s.getStack());
				if(dr!=DietResult.EMPTY)
					for(Entry<IDietGroup, Float> me:dr.get().entrySet())
						groups.merge(me.getKey(),me.getValue()*s.getCount(),Float::sum);
			}
			result.setReturnValue(new DietResult(groups));
		}
	}
	@Inject(at=@At("HEAD"),require=1,method="get(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;IF)Ltop/theillusivec4/diet/api/IDietResult;",cancellable=true,remap=false)
	public void get(PlayerEntity player, ItemStack input,int heal,float sat,CallbackInfoReturnable<IDietResult> result) {
		if(input.getItem() instanceof StewItem) {
			List<FloatemStack> is=StewItem.getItems(input);
			Map<IDietGroup, Float> groups=new HashMap<>();
			for(FloatemStack s:is) {
				IDietResult dr=DietApiImpl.getInstance().get(player,s.getStack());
				for(Entry<IDietGroup, Float> me:dr.get().entrySet())
				groups.merge(me.getKey(),me.getValue()*s.getCount(),Float::sum);
			}
			result.setReturnValue(new DietResult(groups));
		}
	}

}
