package got.client.render.other;

import org.lwjgl.opengl.GL11;

import got.client.model.*;
import got.common.database.GOTRegistry;
import got.common.item.other.GOTItemMug;
import got.common.tileentity.GOTTileEntityMug;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;

public class GOTRenderMug extends TileEntitySpecialRenderer {
	public static ResourceLocation mugTexture = new ResourceLocation("got:textures/model/mug.png");
	public static ResourceLocation mugClayTexture = new ResourceLocation("got:textures/model/mugClay.png");
	public static ResourceLocation gobletGoldTexture = new ResourceLocation("got:textures/model/gobletGold.png");
	public static ResourceLocation gobletSilverTexture = new ResourceLocation("got:textures/model/gobletSilver.png");
	public static ResourceLocation gobletCopperTexture = new ResourceLocation("got:textures/model/gobletCopper.png");
	public static ResourceLocation gobletWoodTexture = new ResourceLocation("got:textures/model/gobletWood.png");
	public static ResourceLocation skullTexture = new ResourceLocation("got:textures/model/skullCup.png");
	public static ResourceLocation glassTexture = new ResourceLocation("got:textures/model/wineGlass.png");
	public static ResourceLocation bottleTexture = new ResourceLocation("got:textures/model/glassBottle.png");
	public static ResourceLocation hornTexture = new ResourceLocation("got:textures/model/aleHorn.png");
	public static ResourceLocation hornGoldTexture = new ResourceLocation("got:textures/model/aleHornGold.png");
	public static ModelBase mugotel = new GOTModelMug();
	public static ModelBase gobletModel = new GOTModelGoblet();
	public static ModelBase skullModel = new GOTModelSkullCup();
	public static ModelBase glassModel = new GOTModelWineGlass();
	public static ModelBase bottleModel = new GOTModelGlassBottle();
	public static GOTModelAleHorn hornModel = new GOTModelAleHorn();
	public static RenderBlocks renderBlocks = new RenderBlocks();

	public void renderLiquid(IIcon icon, int uvMin, int uvMax, double yMin, double yMax, float scale) {
		double edge = 0.001;
		double xzMin = (double) uvMin * (double) scale;
		double xzMax = (double) uvMax * (double) scale;
		float dxz = 0.5f - (uvMin + uvMax) / 2.0f * scale;
		yMin = 16.0 - yMin;
		yMax = 16.0 - yMax;
		yMin *= scale;
		yMax *= scale;
		GL11.glPushMatrix();
		GL11.glTranslatef(dxz, -0.5f, dxz);
		renderBlocks.setOverrideBlockTexture(icon);
		GOTRenderBlocks.renderStandardInvBlock(renderBlocks, GOTRegistry.mugBlock, xzMin += edge, yMax -= edge, xzMin, xzMax -= edge, yMin += edge, xzMax);
		renderBlocks.clearOverrideBlockTexture();
		GL11.glPopMatrix();
	}

	public void renderMeniscus(IIcon icon, int uvMin, int uvMax, double width, double height, float scale) {
		float minU = icon.getInterpolatedU(uvMin);
		float maxU = icon.getInterpolatedU(uvMax);
		float minV = icon.getInterpolatedV(uvMin);
		float maxV = icon.getInterpolatedV(uvMax);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-(width *= scale), -(height *= scale), width, minU, maxV);
		tessellator.addVertexWithUV(width, -height, width, maxU, maxV);
		tessellator.addVertexWithUV(width, -height, -width, maxU, minV);
		tessellator.addVertexWithUV(-width, -height, -width, minU, minV);
		tessellator.draw();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		GOTTileEntityMug mug = (GOTTileEntityMug) tileentity;
		ItemStack mugItemstack = mug.getMugItemForRender();
		Item mugItem = mugItemstack.getItem();
		boolean full = !mug.isEmpty();
		GOTItemMug.Vessel vessel = mug.getVessel();
		GL11.glEnable(32826);
		GL11.glDisable(2884);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5f, (float) d1, (float) d2 + 0.5f);
		GL11.glScalef(-1.0f, -1.0f, 1.0f);
		float mugScale = 0.75f;
		GL11.glScalef(mugScale, mugScale, mugScale);
		float scale = 0.0625f;
		switch (mug.getBlockMetadata()) {
		case 0: {
			GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
			break;
		}
		case 1: {
			GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
			break;
		}
		case 2: {
			GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
			break;
		}
		case 3: {
			GL11.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
		}
		}
		if (vessel == GOTItemMug.Vessel.SKULL || vessel == GOTItemMug.Vessel.HORN || vessel == GOTItemMug.Vessel.HORN_GOLD) {
			GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
		}
		if (full) {
			GL11.glDisable(2896);
			GL11.glPushMatrix();
			bindTexture(TextureMap.locationItemsTexture);
			IIcon liquidIcon = mugItem.getIconFromDamage(-1);
			if (vessel == GOTItemMug.Vessel.MUG || vessel == GOTItemMug.Vessel.MUG_CLAY) {
				renderMeniscus(liquidIcon, 6, 10, 2.0, 7.0, scale);
			} else if (vessel == GOTItemMug.Vessel.GOBLET_GOLD || vessel == GOTItemMug.Vessel.GOBLET_SILVER || vessel == GOTItemMug.Vessel.GOBLET_COPPER || vessel == GOTItemMug.Vessel.GOBLET_WOOD) {
				renderMeniscus(liquidIcon, 6, 9, 1.5, 8.0, scale);
			} else if (vessel == GOTItemMug.Vessel.SKULL) {
				renderMeniscus(liquidIcon, 5, 11, 3.0, 9.0, scale);
			} else if (vessel == GOTItemMug.Vessel.GLASS) {
				renderLiquid(liquidIcon, 6, 9, 6.0, 9.0, scale);
			} else if (vessel == GOTItemMug.Vessel.BOTTLE) {
				renderLiquid(liquidIcon, 6, 10, 1.0, 5.0, scale);
			} else if (vessel == GOTItemMug.Vessel.HORN || vessel == GOTItemMug.Vessel.HORN_GOLD) {
				hornModel.prepareLiquid(scale);
				renderMeniscus(liquidIcon, 6, 9, -1.5, 5.0, scale);
			}
			GL11.glPopMatrix();
			GL11.glEnable(2896);
		}
		GL11.glPushMatrix();
		ModelBase model = null;
		if (vessel == GOTItemMug.Vessel.MUG) {
			bindTexture(mugTexture);
			model = mugotel;
		} else if (vessel == GOTItemMug.Vessel.MUG_CLAY) {
			bindTexture(mugClayTexture);
			model = mugotel;
		} else if (vessel == GOTItemMug.Vessel.GOBLET_GOLD) {
			bindTexture(gobletGoldTexture);
			model = gobletModel;
		} else if (vessel == GOTItemMug.Vessel.GOBLET_SILVER) {
			bindTexture(gobletSilverTexture);
			model = gobletModel;
		} else if (vessel == GOTItemMug.Vessel.GOBLET_COPPER) {
			bindTexture(gobletCopperTexture);
			model = gobletModel;
		} else if (vessel == GOTItemMug.Vessel.GOBLET_WOOD) {
			bindTexture(gobletWoodTexture);
			model = gobletModel;
		} else if (vessel == GOTItemMug.Vessel.SKULL) {
			bindTexture(skullTexture);
			model = skullModel;
		} else if (vessel == GOTItemMug.Vessel.GLASS) {
			bindTexture(glassTexture);
			model = glassModel;
			GL11.glEnable(2884);
		} else if (vessel == GOTItemMug.Vessel.BOTTLE) {
			bindTexture(bottleTexture);
			model = bottleModel;
			GL11.glEnable(2884);
		} else if (vessel == GOTItemMug.Vessel.HORN) {
			bindTexture(hornTexture);
			model = hornModel;
		} else if (vessel == GOTItemMug.Vessel.HORN_GOLD) {
			bindTexture(hornGoldTexture);
			model = hornModel;
		}
		if (model != null) {
			model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, scale);
		}
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glDisable(3042);
		GL11.glEnable(2884);
		GL11.glDisable(32826);

	}
}
