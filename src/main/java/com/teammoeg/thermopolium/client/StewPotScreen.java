package com.teammoeg.thermopolium.client;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.blocks.StewPotTileEntity;
import com.teammoeg.thermopolium.container.StewPotContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.client.gui.GuiUtils;
/**
 * Fluid render codes adapted from Immersive Engineering and modified.
 * Related codes fall under their license and open-sourced. 
 * @author BluSunrize
 * @author khjxiaogu
 * */
public class StewPotScreen extends ContainerScreen<StewPotContainer> {
	public static class ImageButton extends Button {
		int xTexStart;
		int yTexStart;
		private final int textureWidth;
		private final int textureHeight;
		int state;

		public ImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn,
				Button.IPressable onPressIn) {
			this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, EMPTY_TOOLTIP, onPressIn);
		}

		public ImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn,
				Button.ITooltip tt, Button.IPressable onPressIn) {
			this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, 256, 256, onPressIn, tt,
					StringTextComponent.EMPTY);
		}

		public ImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int textureWidth,
				int textureHeight, Button.IPressable onPress, ITextComponent title) {
			this(x, y, width, height, xTexStart, yTexStart, textureWidth, textureHeight, onPress, EMPTY_TOOLTIP, title);
		}

		public ImageButton(int p_i244513_1_, int p_i244513_2_, int p_i244513_3_, int p_i244513_4_, int p_i244513_5_,
				int p_i244513_6_, int p_i244513_9_, int p_i244513_10_, Button.IPressable p_i244513_11_,
				Button.ITooltip p_i244513_12_, ITextComponent p_i244513_13_) {
			super(p_i244513_1_, p_i244513_2_, p_i244513_3_, p_i244513_4_, p_i244513_13_, p_i244513_11_, p_i244513_12_);
			this.textureWidth = p_i244513_9_;
			this.textureHeight = p_i244513_10_;
			this.xTexStart = p_i244513_5_;
			this.yTexStart = p_i244513_6_;
		}

		public void setPosition(int xIn, int yIn) {
			this.x = xIn;
			this.y = yIn;
		}

		public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
			int i = 0, j = state * this.height;

			if (this.isHovered()) {
				i += this.width;
			}

			RenderSystem.enableDepthTest();
			blit(matrixStack, this.x, this.y, this.xTexStart + i, this.yTexStart + j, this.width, this.height,
					this.textureWidth, this.textureHeight);
			if (this.isHovered()) {
				this.renderToolTip(matrixStack, mouseX, mouseY);
			}

		}
	}

	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID,
			"textures/gui/cistern_culinary.png");
	StewPotTileEntity te;
	public StewPotScreen(StewPotContainer container, PlayerInventory inv, ITextComponent titleIn) {
		super(container, inv, titleIn);
		this.titleY=4;
		this.titleX=7;
		this.playerInventoryTitleY=this.ySize-92;
		this.playerInventoryTitleX=4;
		te=container.getTile();
	}

	public static TranslationTextComponent start = new TranslationTextComponent(
			"gui." + Main.MODID + ".stewpot.canstart");
	public static TranslationTextComponent started = new TranslationTextComponent("gui." + Main.MODID + ".stewpot.started");
	public static TranslationTextComponent nostart = new TranslationTextComponent(
			"gui." + Main.MODID + ".stewpot.cantstart");
	public static TranslationTextComponent nors = new TranslationTextComponent(
			"gui." + Main.MODID + ".stewpot.noredstone");
	public static TranslationTextComponent rs = new TranslationTextComponent("gui." + Main.MODID + ".stewpot.redstone");
	private ArrayList<ITextComponent> tooltip=new ArrayList<>(2);
	ImageButton btn1;
	ImageButton btn2;
	@Override
	public void init() {
		super.init();
		this.buttons.clear();
		this.addButton(btn1=new ImageButton(guiLeft + 7, guiTop + 48, 20, 12, 176, 83, (b, s, x, y) -> {
			if(btn1.state==0)
				tooltip.add(start);
			else tooltip.add(started);
		}, btn -> {
			if(btn1.state==0)
			te.sendMessage((short) 0,0);
			
		}));
		this.addButton(btn2=new ImageButton(guiLeft + 7, guiTop + 61, 20, 20, 176, 107, (b, s, x, y) -> {
			if(btn2.state==1)
				tooltip.add(nors);
			else
				tooltip.add(rs);
		}, btn -> {
			te.sendMessage((short) 1,btn2.state);
		}));
		
		
	}

	@Override
	public void render(MatrixStack transform, int mouseX, int mouseY, float partial) {
		tooltip.clear();
		btn1.state=te.proctype>0?1:0;
		btn2.state=te.rsstate?1:2;
		super.render(transform, mouseX, mouseY, partial);
		if(te.proctype!=2)
			handleGuiTank(transform,te.getTank(), guiLeft + 105, guiTop + 20, 16, 46);
		GuiUtils.drawHoveringText(transform,tooltip,mouseX, mouseY, width, height,
				-1, font);
		
	}

	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
		this.font.drawText(matrixStack, this.title, this.titleX, this.titleY, 0xffda856b);
		ITextComponent name=this.playerInventory.getDisplayName();
		int w=this.font.getStringWidth(name.getString());
		this.font.drawText(matrixStack,name, this.xSize-w-this.playerInventoryTitleX,this.playerInventoryTitleY,0xffda856b);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partial, int x, int y) {
		this.renderBackground(transform);
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);

		this.blit(transform, guiLeft, guiTop, 0, 0, xSize, ySize);
		if (te.processMax > 0 && te.process > 0) {
			 int h = (int) (29 * (te.process / (float) te.processMax));
			 this.blit(transform, guiLeft + 9, guiTop + 17 + h, 176,54 + h, 16, 29-h);
		}
		if(te.proctype==2) {
			this.blit(transform, guiLeft + 44, guiTop + 16,176,0,54,54);
			this.blit(transform, guiLeft + 102, guiTop + 17,230,0,21,51);
		}
	}

	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= guiLeft + x && mouseY >= guiTop + y && mouseX < guiLeft + x + w && mouseY < guiTop + y + h;
	}
	//Adapted codes start
	private static void handleGuiTank(MatrixStack transform, IFluidTank tank, int x, int y, int w, int h)
	{
		FluidStack fluid=tank.getFluid();
		transform.push();
		
		IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		if(fluid!=null&&fluid.getFluid()!=null)
		{
			int fluidHeight = (int)(h*(fluid.getAmount()/(float)tank.getCapacity()));
			drawRepeatedFluidSpriteGui(buffer, transform, fluid, x, y+h-fluidHeight, w, fluidHeight);
			RenderSystem.color3f(1, 1, 1);
		}
		buffer.finish();
		transform.pop();
	}
	private static RenderType getGui(ResourceLocation texture)
	{
		return RenderType.makeType(
				"gui_"+texture,
				DefaultVertexFormats.POSITION_COLOR_TEX,GL11.GL_QUADS,
				256,
				RenderType.State.getBuilder()
						.texture(new RenderState.TextureState(texture, false, false))
						.alpha(new RenderState.AlphaState(0.5F))
						.build(false)
		);
	}
	private static void buildVertex(IVertexBuilder bu,MatrixStack transform,float r,float g,float b,float a,float p1,float p2,float u0,float u1) {
		bu.pos(transform.getLast().getMatrix(),p1,p2, 0)
		.color(r, g, b, a)
		.tex(u0, u1)
		.overlay(OverlayTexture.NO_OVERLAY)
		.lightmap(LightTexture.packLight(15,15))
		.normal(transform.getLast().getNormal(),1, 1, 1)
		.endVertex();
	}

	public static void drawRepeatedFluidSpriteGui(IRenderTypeBuffer.Impl buffer, MatrixStack transform, FluidStack fluid, float x, float y, float w, float h){
		RenderType renderType = getGui(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
		IVertexBuilder builder = buffer.getBuffer(renderType);
		TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).getSprite(fluid.getFluid().getAttributes().getStillTexture(fluid));
		int col = fluid.getFluid().getAttributes().getColor(fluid);
		int iW = sprite.getWidth();
		int iH = sprite.getHeight();
		if(iW > 0&&iH > 0)
			drawRepeatedSprite(builder, transform, x, y, w, h, iW, iH,
					sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(),
					(col >> 16&255)/255.0f, (col >> 8&255)/255.0f, (col&255)/255.0f,0.8f);
		buffer.finish(renderType);
	}


	public static void drawRepeatedSprite(IVertexBuilder builder, MatrixStack transform, float x, float y, float w,
										  float h, int iconWidth, int iconHeight, float uMin, float uMax, float vMin, float vMax,
										  float r, float g, float b, float alpha)
	{
		int iterMaxW = (int)(w/iconWidth);
		int iterMaxH = (int)(h/iconHeight);
		float leftoverW = w%iconWidth;
		float leftoverH = h%iconHeight;
		float leftoverWf = leftoverW/iconWidth;
		float leftoverHf = leftoverH/iconHeight;
		float iconUDif = uMax-uMin;
		float iconVDif = vMax-vMin;
		for(int ww = 0; ww < iterMaxW; ww++)
		{
			for(int hh = 0; hh < iterMaxH; hh++)
				drawTexturedColoredRect(builder, transform, x+ww*iconWidth, y+hh*iconHeight, iconWidth, iconHeight,
						r, g, b, alpha, uMin, uMax, vMin, vMax);
			drawTexturedColoredRect(builder, transform, x+ww*iconWidth, y+iterMaxH*iconHeight, iconWidth, leftoverH,
					r, g, b, alpha, uMin, uMax, vMin, (vMin+iconVDif*leftoverHf));
		}
		if(leftoverW > 0)
		{
			for(int hh = 0; hh < iterMaxH; hh++)
				drawTexturedColoredRect(builder, transform, x+iterMaxW*iconWidth, y+hh*iconHeight, leftoverW, iconHeight,
						r, g, b, alpha, uMin, (uMin+iconUDif*leftoverWf), vMin, vMax);
			drawTexturedColoredRect(builder, transform, x+iterMaxW*iconWidth, y+iterMaxH*iconHeight, leftoverW, leftoverH,
					r, g, b, alpha, uMin, (uMin+iconUDif*leftoverWf), vMin, (vMin+iconVDif*leftoverHf));
		}
	}
	private static void drawTexturedColoredRect(
			IVertexBuilder builder, MatrixStack transform,
			float x, float y, float w, float h,
			float r, float g, float b, float alpha,
			float u0, float u1, float v0, float v1
	) {
		buildVertex(builder, transform,r,g,b,alpha,x, y+h,u0, v1);
		buildVertex(builder, transform,r,g,b,alpha,x+w, y+h,u1, v1);
		buildVertex(builder, transform,r,g,b,alpha,x+w, y,u1, v0);
		buildVertex(builder, transform,r,g,b,alpha,x, y,u0, v0);
	}
	//Adapted codes ends
}
