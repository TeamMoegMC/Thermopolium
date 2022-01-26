package com.teammoeg.thermopolium.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teammoeg.thermopolium.Contents;
import com.teammoeg.thermopolium.blocks.StewPotTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.fluids.FluidStack;

public class StewPotRenderer extends TileEntityRenderer<StewPotTileEntity>
{

	public StewPotRenderer(TileEntityRendererDispatcher rendererDispatcherIn)
	{
		super(rendererDispatcherIn);
		System.out.println("constructed");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(StewPotTileEntity te, float partialTicks, MatrixStack matrixStack,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
		if(!te.getWorld().isBlockLoaded(te.getPos()))
			return;
		BlockState state = te.getBlockState();
		if(state.getBlock()!=Contents.SCBlocks.stew_pot)
			return;
		matrixStack.push();
		FluidStack fs = te.getTank().getFluid();
		if(fs!=null&&!fs.isEmpty()&&fs.getFluid()!=null)
		{
			float yy = fs.getAmount()/(float)te.getTank().getCapacity()*.5f+.3125f;
			matrixStack.translate(0,yy,0);
			matrixStack.rotate(new Quaternion(90, 0, 0, true));
			IVertexBuilder builder = buffer.getBuffer(RenderType.getTranslucent());
			TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).getSprite(fs.getFluid().getAttributes().getStillTexture(fs));
			int col = fs.getFluid().getAttributes().getColor(fs);
			int iW = sprite.getWidth();
			int iH = sprite.getHeight();
			if(iW > 0&&iH > 0)
				RenderUtils.drawRepeatedSprite(builder,matrixStack,.125f,.125f,.75f,.75f, iW, iH,
						sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(),
						(col >> 16&255)/255.0f, (col >> 8&255)/255.0f, (col&255)/255.0f,1f,combinedLightIn,combinedOverlayIn);
		}

		matrixStack.pop();
	}


}