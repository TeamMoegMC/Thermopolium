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
import net.minecraftforge.fml.client.gui.GuiUtils;

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
			RenderUtils.handleGuiTank(transform,te.getTank(), guiLeft + 105, guiTop + 20, 16, 46);
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

}
