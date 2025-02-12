package got.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GOTGuiButtonCoinExchange extends GuiButton {
	public GOTGuiButtonCoinExchange(int i, int j, int k) {
		super(i, j, k, 32, 17, "");
	}

	@Override
	public void drawButton(Minecraft mc, int i, int j) {
		if (visible) {
			mc.getTextureManager().bindTexture(GOTGuiCoinExchange.guiTexture);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			field_146123_n = i >= xPosition && j >= yPosition && i < xPosition + width && j < yPosition + height;
			int k = getHoverState(field_146123_n);
			int u = 176 + id * width;
			int v = 0 + k * height;
			drawTexturedModalRect(xPosition, yPosition, u, v, width, height);
			mouseDragged(mc, i, j);
		}
	}
}