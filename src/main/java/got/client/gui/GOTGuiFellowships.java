package got.client.gui;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.math.IntMath;
import com.mojang.authlib.GameProfile;

import got.common.*;
import got.common.database.GOTTitle;
import got.common.fellowship.GOTFellowshipClient;
import got.common.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

public class GOTGuiFellowships extends GOTGuiMenuBase {
	public static ResourceLocation iconsTextures = new ResourceLocation("got:textures/gui/fellowships.png");
	public static int entrySplit = 5;
	public static int entryBorder = 10;
	public static int selectBorder = 2;
	public Page page = Page.LIST;
	public List<GOTFellowshipClient> allFellowshipsLeading = new ArrayList<>();
	public List<GOTFellowshipClient> allFellowshipsOther = new ArrayList<>();
	public List<GOTFellowshipClient> allFellowshipInvites = new ArrayList<>();
	public GOTFellowshipClient mouseOverFellowship;
	public GOTFellowshipClient viewingFellowship;
	public UUID mouseOverPlayer;
	public boolean mouseOverPlayerRemove;
	public boolean mouseOverPlayerOp;
	public boolean mouseOverPlayerDeop;
	public boolean mouseOverPlayerTransfer;
	public UUID removingPlayer;
	public UUID oppingPlayer;
	public UUID deoppingPlayer;
	public UUID transferringPlayer;
	public boolean mouseOverInviteAccept;
	public boolean mouseOverInviteReject;
	public GOTPacketFellowshipAcceptInviteResult.AcceptInviteResult acceptInviteResult;
	public String acceptInviteResultFellowshipName;
	public GuiButton buttonCreate;
	public GuiButton buttonCreateThis;
	public GOTGuiButtonFsOption buttonInvitePlayer;
	public GuiButton buttonInviteThis;
	public GOTGuiButtonFsOption buttonDisband;
	public GuiButton buttonDisbandThis;
	public GuiButton buttonLeave;
	public GuiButton buttonLeaveThis;
	public GOTGuiButtonFsOption buttonSetIcon;
	public GuiButton buttonRemove;
	public GuiButton buttonTransfer;
	public GOTGuiButtonFsOption buttonRename;
	public GuiButton buttonRenameThis;
	public GuiButton buttonBack;
	public GuiButton buttonInvites;
	public GOTGuiButtonFsOption buttonPVP;
	public GOTGuiButtonFsOption buttonHiredFF;
	public GOTGuiButtonFsOption buttonMapShow;
	public GuiButton buttonOp;
	public GuiButton buttonDeop;
	public List<GOTGuiButtonFsOption> orderedFsOptionButtons = new ArrayList<>();
	public GuiTextField textFieldName;
	public GuiTextField textFieldPlayer;
	public GuiTextField textFieldRename;
	public int scrollWidgetWidth = 9;
	public int scrollWidgetHeight = 8;
	public int scrollBarX = xSize + 2 + 1;
	public GOTGuiScrollPane scrollPaneLeading = new GOTGuiScrollPane(scrollWidgetWidth, scrollWidgetHeight);
	public GOTGuiScrollPane scrollPaneOther = new GOTGuiScrollPane(scrollWidgetWidth, scrollWidgetHeight);
	public GOTGuiScrollPane scrollPaneMembers = new GOTGuiScrollPane(scrollWidgetWidth, scrollWidgetHeight);
	public GOTGuiScrollPane scrollPaneInvites = new GOTGuiScrollPane(scrollWidgetWidth, scrollWidgetHeight);
	public int displayedFellowshipsLeading;
	public int displayedFellowshipsOther;
	public int displayedMembers;
	public int displayedInvites;
	public int tickCounter;

	public void acceptInvitation(GOTFellowshipClient invite) {
		GOTPacketFellowshipRespondInvite packet = new GOTPacketFellowshipRespondInvite(invite, true);
		GOTPacketHandler.networkWrapper.sendToServer(packet);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button == buttonCreate) {
				page = Page.CREATE;
			} else if (button == buttonCreateThis) {
				String name = textFieldName.getText();
				if (checkValidFellowshipName(name) == null) {
					name = StringUtils.trim(name);
					GOTPacketFellowshipCreate packet = new GOTPacketFellowshipCreate(name);
					GOTPacketHandler.networkWrapper.sendToServer(packet);
				}
				page = Page.LIST;
			} else if (button == buttonInvitePlayer) {
				page = Page.INVITE;
			} else if (button == buttonInviteThis) {
				String name = textFieldPlayer.getText();
				if (checkValidPlayerName(name) == null) {
					name = StringUtils.trim(name);
					GOTPacketFellowshipInvitePlayer packet = new GOTPacketFellowshipInvitePlayer(viewingFellowship, name);
					GOTPacketHandler.networkWrapper.sendToServer(packet);
				}
				page = Page.FELLOWSHIP;
			} else if (button == buttonDisband) {
				page = Page.DISBAND;
			} else if (button == buttonDisbandThis) {
				GOTPacketFellowshipDisband packet = new GOTPacketFellowshipDisband(viewingFellowship);
				GOTPacketHandler.networkWrapper.sendToServer(packet);
				page = Page.LIST;
			} else if (button == buttonLeave) {
				page = Page.LEAVE;
			} else if (button == buttonLeaveThis) {
				GOTPacketFellowshipLeave packet = new GOTPacketFellowshipLeave(viewingFellowship);
				GOTPacketHandler.networkWrapper.sendToServer(packet);
				page = Page.LIST;
			} else if (button == buttonSetIcon) {
				GOTPacketFellowshipSetIcon packet = new GOTPacketFellowshipSetIcon(viewingFellowship);
				GOTPacketHandler.networkWrapper.sendToServer(packet);
			} else if (button == buttonRemove) {
				GOTPacketFellowshipDoPlayer packet = new GOTPacketFellowshipDoPlayer(viewingFellowship, removingPlayer, GOTPacketFellowshipDoPlayer.PlayerFunction.REMOVE);
				GOTPacketHandler.networkWrapper.sendToServer(packet);
				page = Page.FELLOWSHIP;
			} else if (button == buttonOp) {
				GOTPacketFellowshipDoPlayer packet = new GOTPacketFellowshipDoPlayer(viewingFellowship, oppingPlayer, GOTPacketFellowshipDoPlayer.PlayerFunction.OP);
				GOTPacketHandler.networkWrapper.sendToServer(packet);
				page = Page.FELLOWSHIP;
			} else if (button == buttonDeop) {
				GOTPacketFellowshipDoPlayer packet = new GOTPacketFellowshipDoPlayer(viewingFellowship, deoppingPlayer, GOTPacketFellowshipDoPlayer.PlayerFunction.DEOP);
				GOTPacketHandler.networkWrapper.sendToServer(packet);
				page = Page.FELLOWSHIP;
			} else if (button == buttonTransfer) {
				GOTPacketFellowshipDoPlayer packet = new GOTPacketFellowshipDoPlayer(viewingFellowship, transferringPlayer, GOTPacketFellowshipDoPlayer.PlayerFunction.TRANSFER);
				GOTPacketHandler.networkWrapper.sendToServer(packet);
				page = Page.FELLOWSHIP;
			} else if (button == buttonRename) {
				page = Page.RENAME;
			} else if (button == buttonRenameThis) {
				String name = textFieldRename.getText();
				if (checkValidFellowshipName(name) == null) {
					name = StringUtils.trim(name);
					GOTPacketFellowshipRename packet = new GOTPacketFellowshipRename(viewingFellowship, name);
					GOTPacketHandler.networkWrapper.sendToServer(packet);
				}
				page = Page.FELLOWSHIP;
			} else if (button == buttonBack) {
				keyTyped('E', 1);
			} else if (button == buttonInvites) {
				page = Page.INVITATIONS;
			} else if (button == buttonPVP) {
				GOTPacketFellowshipToggle packet = new GOTPacketFellowshipToggle(viewingFellowship, GOTPacketFellowshipToggle.ToggleFunction.PVP);
				GOTPacketHandler.networkWrapper.sendToServer(packet);
			} else if (button == buttonHiredFF) {
				GOTPacketFellowshipToggle packet = new GOTPacketFellowshipToggle(viewingFellowship, GOTPacketFellowshipToggle.ToggleFunction.HIRED_FF);
				GOTPacketHandler.networkWrapper.sendToServer(packet);
			} else if (button == buttonMapShow) {
				GOTPacketFellowshipToggle packet = new GOTPacketFellowshipToggle(viewingFellowship, GOTPacketFellowshipToggle.ToggleFunction.MAP_SHOW);
				GOTPacketHandler.networkWrapper.sendToServer(packet);
			} else {
				super.actionPerformed(button);
			}
		}
	}

	public void alignOptionButtons() {
		List<GuiButton> activeOptionButtons = new ArrayList<>();
		for (GuiButton button : orderedFsOptionButtons) {
			if (button.visible) {
				activeOptionButtons.add(button);
			}
		}
		if (buttonLeave.visible) {
			activeOptionButtons.add(buttonLeave);
		}
		int midX = guiLeft + xSize / 2;
		int numActive = activeOptionButtons.size();
		if (numActive > 0) {
			int gap = 8;
			int allWidth = 0;
			for (GuiButton button : activeOptionButtons) {
				if (allWidth > 0) {
					allWidth += gap;
				}
				allWidth += button.width;
			}
			int x = midX - allWidth / 2;
			for (GuiButton activeOptionButton : activeOptionButtons) {
				GuiButton button = activeOptionButton;
				button.xPosition = x;
				x += button.width;
				x += gap;
			}
		}
	}

	public void buttonSound() {
		buttonBack.func_146113_a(mc.getSoundHandler());
	}

	public String checkValidFellowshipName(String name) {
		if (!StringUtils.isWhitespace(name)) {
			if (GOTLevelData.getData(mc.thePlayer).anyMatchingFellowshipNames(name, true)) {
				return StatCollector.translateToLocal("got.gui.fellowships.nameExists");
			}
			return null;
		}
		return "";
	}

	public String checkValidPlayerName(String name) {
		if (!StringUtils.isWhitespace(name)) {
			if (viewingFellowship.containsPlayerUsername(name)) {
				return StatCollector.translateToLocalFormatted("got.gui.fellowships.playerExists", name);
			}
			return null;
		}
		return "";
	}

	public void clearMouseOverFellowship() {
		mouseOverFellowship = null;
	}

	public int countOnlineMembers(GOTFellowshipClient fs) {
		int i = 0;
		List<GameProfile> allPlayers = fs.getAllPlayerProfiles();
		for (GameProfile player : allPlayers) {
			if (isPlayerOnline(player)) {
				i++;
			}
		}
		return i;
	}

	public void displayAcceptInvitationResult(UUID fellowshipID, String name, GOTPacketFellowshipAcceptInviteResult.AcceptInviteResult result) {
		if (page == Page.ACCEPT_INVITE_RESULT) {
			if (result == GOTPacketFellowshipAcceptInviteResult.AcceptInviteResult.JOINED) {
				page = Page.FELLOWSHIP;
				viewingFellowship = GOTLevelData.getData(mc.thePlayer).getClientFellowshipByID(fellowshipID);
			} else {
				acceptInviteResult = result;
				acceptInviteResultFellowshipName = name;
			}
		}
	}

	public void drawFellowshipEntry(GOTFellowshipClient fs, int x, int y, int mouseX, int mouseY, boolean isInvite) {
		drawFellowshipEntry(fs, x, y, mouseX, mouseY, isInvite, xSize);
	}

	public void drawFellowshipEntry(GOTFellowshipClient fs, int x, int y, int mouseX, int mouseY, boolean isInvite, int selectWidth) {
		int selectX0 = x - 2;
		int selectX1 = x + selectWidth + 2;
		int selectY0 = y - 2;
		int selectY1 = y + fontRendererObj.FONT_HEIGHT + 2;
		if (mouseX >= selectX0 && mouseX <= selectX1 && mouseY >= selectY0 && mouseY <= selectY1) {
			drawRect(selectX0, selectY0, selectX1, selectY1, 1442840575);
			mouseOverFellowship = fs;
		}
		boolean isMouseOver = mouseOverFellowship == fs;
		drawFellowshipIcon(fs, x, y, 0.5F);
		String fsName = fs.getName();
		int maxLength = 110;
		if (fontRendererObj.getStringWidth(fsName) > maxLength) {
			String ellipsis = "...";
			while (fontRendererObj.getStringWidth(fsName + ellipsis) > maxLength) {
				fsName = fsName.substring(0, fsName.length() - 1);
			}
			fsName = fsName + ellipsis;
		}
		GameProfile owner = fs.getOwnerProfile();
		boolean ownerOnline = isPlayerOnline(owner);
		fontRendererObj.drawString(fsName, x + 15, y, 16777215);
		fontRendererObj.drawString(owner.getName(), x + 130, y, ownerOnline ? 16777215 : isMouseOver ? 12303291 : 7829367);
		if (isInvite) {
			int iconWidth = 8;
			int iconAcceptX = x + xSize - 18;
			int iconRejectX = x + xSize - 8;
			boolean accept = false;
			boolean reject = false;
			if (isMouseOver) {
				mouseOverInviteAccept = mouseX >= iconAcceptX && mouseX <= iconAcceptX + iconWidth && mouseY >= y && mouseY <= y + iconWidth;
				accept = mouseOverInviteAccept;
				mouseOverInviteReject = mouseX >= iconRejectX && mouseX <= iconRejectX + iconWidth && mouseY >= y && mouseY <= y + iconWidth;
				reject = mouseOverInviteReject;
			}
			mc.getTextureManager().bindTexture(iconsTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(iconAcceptX, y, 16, 16 + (accept ? 0 : iconWidth), iconWidth, iconWidth);
			drawTexturedModalRect(iconRejectX, y, 8, 16 + (reject ? 0 : iconWidth), iconWidth, iconWidth);
		} else {
			String memberCount = String.valueOf(fs.getPlayerCount());
			String onlineMemberCount = String.valueOf(countOnlineMembers(fs)) + " | ";
			fontRendererObj.drawString(memberCount, x + xSize - fontRendererObj.getStringWidth(memberCount), y, isMouseOver ? 12303291 : 7829367);
			fontRendererObj.drawString(onlineMemberCount, x + xSize - fontRendererObj.getStringWidth(memberCount) - fontRendererObj.getStringWidth(onlineMemberCount), y, 16777215);
		}
	}

	public void drawFellowshipIcon(GOTFellowshipClient fsClient, int x, int y, float scale) {
		ItemStack fsIcon = fsClient.getIcon();
		if (fsIcon != null) {
			GL11.glDisable(3042);
			GL11.glDisable(3008);
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glDisable(2896);
			GL11.glEnable(32826);
			GL11.glEnable(2896);
			GL11.glEnable(2884);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPushMatrix();
			GL11.glScalef(scale, scale, 1.0F);
			renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), fsIcon, Math.round(x / scale), Math.round(y / scale));
			GL11.glPopMatrix();
			GL11.glDisable(2896);
		}
	}

	public void drawPlayerEntry(GameProfile player, int x, int y, int titleOffset, int mouseX, int mouseY) {
		UUID playerUuid = player.getId();
		String playerUsername = player.getName();
		int selectX0 = x - 2;
		int selectX1 = x + xSize + 2;
		int selectY0 = y - 2;
		int selectY1 = y + fontRendererObj.FONT_HEIGHT + 2;
		if (mouseX >= selectX0 && mouseX <= selectX1 && mouseY >= selectY0 && mouseY <= selectY1) {
			drawRect(selectX0, selectY0, selectX1, selectY1, 1442840575);
			mouseOverPlayer = playerUuid;
		}
		boolean isMouseOver = playerUuid.equals(mouseOverPlayer);
		String titleName = null;
		GOTTitle.PlayerTitle title = viewingFellowship.getTitleFor(playerUuid);
		if (title != null) {
			titleName = title.getFormattedTitle(mc.thePlayer);
		}
		if (titleName != null) {
			fontRendererObj.drawString(titleName, x, y, 16777215);
		}
		boolean online = isPlayerOnline(player);
		fontRendererObj.drawString(playerUsername, x + titleOffset, y, online ? 16777215 : isMouseOver ? 12303291 : 7829367);
		boolean isOwner = viewingFellowship.getOwnerUuid().equals(playerUuid);
		boolean isAdmin = viewingFellowship.isAdmin(playerUuid);
		if (isOwner) {
			mc.getTextureManager().bindTexture(iconsTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(x + titleOffset + fontRendererObj.getStringWidth(playerUsername + " "), y, 0, 0, 8, 8);
		} else if (isAdmin) {
			mc.getTextureManager().bindTexture(iconsTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(x + titleOffset + fontRendererObj.getStringWidth(playerUsername + " "), y, 8, 0, 8, 8);
		}
		boolean owned = viewingFellowship.isOwned();
		boolean adminned = viewingFellowship.isAdminned();
		if (!isOwner && (owned || adminned)) {
			int iconWidth = 8;
			int iconRemoveX = x + xSize - 28;
			int iconOpDeopX = x + xSize - 18;
			int iconTransferX = x + xSize - 8;
			if (adminned) {
				iconRemoveX = x + xSize - 8;
			}
			boolean remove = false;
			boolean opDeop = false;
			boolean transfer = false;
			if (isMouseOver) {
				mouseOverPlayerRemove = mouseX >= iconRemoveX && mouseX <= iconRemoveX + iconWidth && mouseY >= y && mouseY <= y + iconWidth;
				remove = mouseOverPlayerRemove;
				if (owned) {
					if (isAdmin) {
						mouseOverPlayerDeop = mouseX >= iconOpDeopX && mouseX <= iconOpDeopX + iconWidth && mouseY >= y && mouseY <= y + iconWidth;
						opDeop = mouseOverPlayerDeop;
					} else {
						mouseOverPlayerOp = mouseX >= iconOpDeopX && mouseX <= iconOpDeopX + iconWidth && mouseY >= y && mouseY <= y + iconWidth;
						opDeop = mouseOverPlayerOp;
					}
					mouseOverPlayerTransfer = mouseX >= iconTransferX && mouseX <= iconTransferX + iconWidth && mouseY >= y && mouseY <= y + iconWidth;
					transfer = mouseOverPlayerTransfer;
				}
			}
			mc.getTextureManager().bindTexture(iconsTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(iconRemoveX, y, 8, 16 + (remove ? 0 : iconWidth), iconWidth, iconWidth);
			if (owned) {
				if (isAdmin) {
					drawTexturedModalRect(iconOpDeopX, y, 32, 16 + (opDeop ? 0 : iconWidth), iconWidth, iconWidth);
				} else {
					drawTexturedModalRect(iconOpDeopX, y, 24, 16 + (opDeop ? 0 : iconWidth), iconWidth, iconWidth);
				}
				drawTexturedModalRect(iconTransferX, y, 0, 16 + (transfer ? 0 : iconWidth), iconWidth, iconWidth);
			}
		}
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		GOTPlayerData playerData = GOTLevelData.getData(mc.thePlayer);
		boolean viewingOwned = viewingFellowship != null && viewingFellowship.isOwned();
		boolean viewingAdminned = viewingFellowship != null && viewingFellowship.isAdminned();
		mouseOverFellowship = null;
		mouseOverPlayer = null;
		mouseOverPlayerRemove = false;
		mouseOverPlayerOp = false;
		mouseOverPlayerDeop = false;
		mouseOverPlayerTransfer = false;
		if (page != Page.REMOVE) {
			removingPlayer = null;
		}
		if (page != Page.OP) {
			oppingPlayer = null;
		}
		if (page != Page.DEOP) {
			deoppingPlayer = null;
		}
		if (page != Page.TRANSFER) {
			transferringPlayer = null;
		}
		mouseOverInviteAccept = false;
		mouseOverInviteReject = false;
		if (page != Page.ACCEPT_INVITE_RESULT) {
			acceptInviteResult = null;
			acceptInviteResultFellowshipName = null;
		}
		boolean creationEnabled = GOTConfig.isFellowshipCreationEnabled(mc.theWorld);
		boolean canPlayerCreateNew = playerData.canCreateFellowships(true);
		buttonCreate.visible = page == Page.LIST;
		buttonCreate.enabled = buttonCreate.visible && creationEnabled && canPlayerCreateNew;
		buttonCreateThis.visible = page == Page.CREATE;
		String checkValidName = checkValidFellowshipName(textFieldName.getText());
		buttonCreateThis.enabled = buttonCreateThis.visible && checkValidName == null;
		buttonInvitePlayer.visible = buttonInvitePlayer.enabled = page == Page.FELLOWSHIP && (viewingOwned || viewingAdminned);
		boolean canInvite = page == Page.INVITE && !isFellowshipMaxSize(viewingFellowship);
		buttonInviteThis.visible = canInvite;
		String checkValidPlayer = "";
		if (canInvite) {
			checkValidPlayer = checkValidPlayerName(textFieldPlayer.getText());
			buttonInviteThis.enabled = buttonInviteThis.visible && checkValidPlayer == null;
		}
		buttonDisband.visible = buttonDisband.enabled = page == Page.FELLOWSHIP && viewingOwned;
		buttonDisbandThis.visible = buttonDisbandThis.enabled = page == Page.DISBAND;
		buttonLeave.visible = buttonLeave.enabled = page == Page.FELLOWSHIP && !viewingOwned;
		buttonLeaveThis.visible = buttonLeaveThis.enabled = page == Page.LEAVE;
		buttonSetIcon.visible = buttonSetIcon.enabled = page == Page.FELLOWSHIP && (viewingOwned || viewingAdminned);
		buttonRemove.visible = buttonRemove.enabled = page == Page.REMOVE;
		buttonTransfer.visible = buttonTransfer.enabled = page == Page.TRANSFER;
		buttonRename.visible = buttonRename.enabled = page == Page.FELLOWSHIP && viewingOwned;
		buttonRenameThis.visible = page == Page.RENAME;
		String checkValidRename = checkValidFellowshipName(textFieldRename.getText());
		buttonRenameThis.enabled = buttonRenameThis.visible && checkValidRename == null;
		buttonBack.visible = buttonBack.enabled = page != Page.LIST;
		buttonInvites.visible = buttonInvites.enabled = page == Page.LIST;
		buttonPVP.visible = buttonPVP.enabled = page == Page.FELLOWSHIP && (viewingOwned || viewingAdminned);
		if (buttonPVP.enabled) {
			buttonPVP.setIconUV(64, viewingFellowship.getPreventPVP() ? 80 : 48);
		}
		buttonHiredFF.visible = buttonHiredFF.enabled = page == Page.FELLOWSHIP && (viewingOwned || viewingAdminned);
		if (buttonHiredFF.enabled) {
			buttonHiredFF.setIconUV(80, viewingFellowship.getPreventHiredFriendlyFire() ? 80 : 48);
		}
		buttonMapShow.visible = buttonMapShow.enabled = page == Page.FELLOWSHIP && viewingOwned;
		if (buttonMapShow.enabled) {
			buttonMapShow.setIconUV(96, viewingFellowship.getShowMapLocations() ? 48 : 80);
		}
		buttonOp.visible = buttonOp.enabled = page == Page.OP;
		buttonDeop.visible = buttonDeop.enabled = page == Page.DEOP;
		alignOptionButtons();
		setupScrollBars(i, j);
		drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.drawScreen(i, j, f);
		String s = StatCollector.translateToLocal("got.gui.fellowships.title");
		drawCenteredString(s, guiLeft + xSize / 2, guiTop - 30, 16777215);
		if (page == Page.LIST) {
			int x = guiLeft;
			int y = scrollPaneLeading.paneY0;
			s = StatCollector.translateToLocal("got.gui.fellowships.leading");
			drawCenteredString(s, guiLeft + xSize / 2, y, 16777215);
			y += fontRendererObj.FONT_HEIGHT + 10;
			List<GOTFellowshipClient> sortedLeading = sortFellowshipsForDisplay(allFellowshipsLeading);
			int[] leadingMinMax = scrollPaneLeading.getMinMaxIndices(sortedLeading, displayedFellowshipsLeading);
			for (int index = leadingMinMax[0]; index <= leadingMinMax[1]; index++) {
				GOTFellowshipClient fs = sortedLeading.get(index);
				drawFellowshipEntry(fs, x, y, i, j, false);
				y += fontRendererObj.FONT_HEIGHT + 5;
			}
			y = scrollPaneOther.paneY0;
			s = StatCollector.translateToLocal("got.gui.fellowships.member");
			drawCenteredString(s, guiLeft + xSize / 2, y, 16777215);
			y += fontRendererObj.FONT_HEIGHT + 10;
			List<GOTFellowshipClient> sortedOther = sortFellowshipsForDisplay(allFellowshipsOther);
			int[] otherMinMax = scrollPaneOther.getMinMaxIndices(sortedOther, displayedFellowshipsOther);
			for (int k = otherMinMax[0]; k <= otherMinMax[1]; k++) {
				GOTFellowshipClient fs = sortedOther.get(k);
				drawFellowshipEntry(fs, x, y, i, j, false);
				y += fontRendererObj.FONT_HEIGHT + 5;
			}
			String invites = String.valueOf(playerData.getClientFellowshipInvites().size());
			int invitesX = buttonInvites.xPosition - 2 - fontRendererObj.getStringWidth(invites);
			int invitesY = buttonInvites.yPosition + buttonInvites.height / 2 - fontRendererObj.FONT_HEIGHT / 2;
			fontRendererObj.drawString(invites, invitesX, invitesY, 16777215);
			if (buttonInvites.func_146115_a()) {
				renderIconTooltip(i, j, StatCollector.translateToLocal("got.gui.fellowships.invitesTooltip"));
			}
			if (buttonCreate.func_146115_a()) {
				if (!creationEnabled) {
					s = StatCollector.translateToLocal("got.gui.fellowships.creationDisabled");
					this.drawCenteredString(s, guiLeft + xSize / 2, buttonCreate.yPosition + buttonCreate.height + 4, 16777215);
				} else if (!canPlayerCreateNew) {
					s = StatCollector.translateToLocal("got.gui.fellowships.createLimit");
					this.drawCenteredString(s, guiLeft + xSize / 2, buttonCreate.yPosition + buttonCreate.height + 4, 16777215);
				}
			}
			if (scrollPaneLeading.hasScrollBar) {
				scrollPaneLeading.drawScrollBar();
			}
			if (scrollPaneOther.hasScrollBar) {
				scrollPaneOther.drawScrollBar();
			}
		} else if (page == Page.CREATE) {
			s = StatCollector.translateToLocal("got.gui.fellowships.createName");
			drawCenteredString(s, guiLeft + xSize / 2, textFieldName.yPosition - 4 - fontRendererObj.FONT_HEIGHT, 16777215);
			textFieldName.drawTextBox();
			if (checkValidName != null) {
				drawCenteredString(checkValidName, guiLeft + xSize / 2, textFieldName.yPosition + textFieldName.height + fontRendererObj.FONT_HEIGHT, 16711680);
			}
		} else if (page == Page.FELLOWSHIP) {
			int x = guiLeft;
			int y = guiTop + 10;
			s = StatCollector.translateToLocalFormatted("got.gui.fellowships.nameAndPlayers", viewingFellowship.getName(), Integer.valueOf(viewingFellowship.getPlayerCount()));
			drawCenteredString(s, guiLeft + xSize / 2, y, 16777215);
			y += fontRendererObj.FONT_HEIGHT;
			y += 5;
			if (viewingFellowship.getIcon() != null) {
				drawFellowshipIcon(viewingFellowship, guiLeft + xSize / 2 - 8, y, 1.0F);
			}
			boolean preventPVP = viewingFellowship.getPreventPVP();
			boolean preventHiredFF = viewingFellowship.getPreventHiredFriendlyFire();
			boolean mapShow = viewingFellowship.getShowMapLocations();
			int iconPVPX = guiLeft + xSize - 36;
			int iconHFFX = guiLeft + xSize - 16;
			int iconMapX = guiLeft + xSize - 56;
			int iconY = y;
			int iconSize = 16;
			mc.getTextureManager().bindTexture(iconsTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(iconPVPX, iconY, 64, preventPVP ? 80 : 48, iconSize, iconSize);
			drawTexturedModalRect(iconHFFX, iconY, 80, preventHiredFF ? 80 : 48, iconSize, iconSize);
			drawTexturedModalRect(iconMapX, iconY, 96, mapShow ? 48 : 80, iconSize, iconSize);
			if (i >= iconPVPX && i < iconPVPX + iconSize && j >= iconY && j < iconY + iconSize) {
				renderIconTooltip(i, j, StatCollector.translateToLocal(preventPVP ? "got.gui.fellowships.pvp.prevent" : "got.gui.fellowships.pvp.allow"));
			}
			if (i >= iconHFFX && i < iconHFFX + iconSize && j >= iconY && j < iconY + iconSize) {
				renderIconTooltip(i, j, StatCollector.translateToLocal(preventHiredFF ? "got.gui.fellowships.hiredFF.prevent" : "got.gui.fellowships.hiredFF.allow"));
			}
			if (i >= iconMapX && i < iconMapX + iconSize && j >= iconY && j < iconY + iconSize) {
				renderIconTooltip(i, j, StatCollector.translateToLocal(mapShow ? "got.gui.fellowships.mapShow.on" : "got.gui.fellowships.mapShow.off"));
			}
			y += iconSize;
			y += 10;
			int titleOffset = 0;
			for (UUID playerUuid : viewingFellowship.getAllPlayerUuids()) {
				GOTTitle.PlayerTitle title = viewingFellowship.getTitleFor(playerUuid);
				if (title != null) {
					String titleName = title.getFormattedTitle(mc.thePlayer);
					int thisTitleWidth = fontRendererObj.getStringWidth(titleName + " ");
					titleOffset = Math.max(titleOffset, thisTitleWidth);
				}
			}
			drawPlayerEntry(viewingFellowship.getOwnerProfile(), x, y, titleOffset, i, j);
			y += fontRendererObj.FONT_HEIGHT + 10;
			List<GameProfile> membersSorted = sortMembersForDisplay(viewingFellowship);
			int[] membersMinMax = scrollPaneMembers.getMinMaxIndices(membersSorted, displayedMembers);
			for (int index = membersMinMax[0]; index <= membersMinMax[1]; index++) {
				GameProfile member = membersSorted.get(index);
				drawPlayerEntry(member, x, y, titleOffset, i, j);
				y += fontRendererObj.FONT_HEIGHT + 5;
			}
			for (Object bObj : buttonList) {
				GuiButton button = (GuiButton) bObj;
				if (button instanceof GOTGuiButtonFsOption && button.visible && button.func_146115_a()) {
					s = button.displayString;
					drawCenteredString(s, guiLeft + xSize / 2, button.yPosition + button.height + 4, 16777215);
				}
			}
			if (scrollPaneMembers.hasScrollBar) {
				scrollPaneMembers.drawScrollBar();
			}
		} else if (page == Page.INVITE) {
			if (isFellowshipMaxSize(viewingFellowship)) {
				int x = guiLeft + xSize / 2;
				int y = guiTop + 30;
				s = StatCollector.translateToLocalFormatted("got.gui.fellowships.invite.maxSize", viewingFellowship.getName(), Integer.valueOf(GOTConfig.getFellowshipMaxSize(mc.theWorld)));
				List<String> lines = fontRendererObj.listFormattedStringToWidth(s, xSize);
				for (String line : lines) {
					drawCenteredString(line, x, y, 16777215);
					y += fontRendererObj.FONT_HEIGHT;
				}
			} else {
				s = StatCollector.translateToLocalFormatted("got.gui.fellowships.inviteName", viewingFellowship.getName());
				drawCenteredString(s, guiLeft + xSize / 2, textFieldPlayer.yPosition - 4 - fontRendererObj.FONT_HEIGHT, 16777215);
				textFieldPlayer.drawTextBox();
				if (checkValidPlayer != null) {
					drawCenteredString(checkValidPlayer, guiLeft + xSize / 2, textFieldPlayer.yPosition + textFieldPlayer.height + fontRendererObj.FONT_HEIGHT, 16711680);
				}
			}
		} else if (page == Page.DISBAND) {
			int x = guiLeft + xSize / 2;
			int y = guiTop + 30;
			s = StatCollector.translateToLocalFormatted("got.gui.fellowships.disbandCheck1", viewingFellowship.getName());
			drawCenteredString(s, x, y, 16777215);
			y += fontRendererObj.FONT_HEIGHT;
			s = StatCollector.translateToLocal("got.gui.fellowships.disbandCheck2");
			drawCenteredString(s, x, y, 16777215);
			y += fontRendererObj.FONT_HEIGHT * 2;
			s = StatCollector.translateToLocal("got.gui.fellowships.disbandCheck3");
			drawCenteredString(s, x, y, 16777215);
			y += fontRendererObj.FONT_HEIGHT;
		} else if (page == Page.LEAVE) {
			int x = guiLeft + xSize / 2;
			int y = guiTop + 30;
			s = StatCollector.translateToLocalFormatted("got.gui.fellowships.leaveCheck1", viewingFellowship.getName());
			drawCenteredString(s, x, y, 16777215);
			y += fontRendererObj.FONT_HEIGHT;
			s = StatCollector.translateToLocal("got.gui.fellowships.leaveCheck2");
			drawCenteredString(s, x, y, 16777215);
			y += fontRendererObj.FONT_HEIGHT * 2;
		} else if (page == Page.REMOVE) {
			int x = guiLeft + xSize / 2;
			int y = guiTop + 30;
			s = StatCollector.translateToLocalFormatted("got.gui.fellowships.removeCheck", viewingFellowship.getName(), viewingFellowship.getUsernameFor(removingPlayer));
			List<String> lines = fontRendererObj.listFormattedStringToWidth(s, xSize);
			for (String line : lines) {
				drawCenteredString(line, x, y, 16777215);
				y += fontRendererObj.FONT_HEIGHT;
			}
		} else if (page == Page.OP) {
			int x = guiLeft + xSize / 2;
			int y = guiTop + 30;
			s = StatCollector.translateToLocalFormatted("got.gui.fellowships.opCheck1", viewingFellowship.getName(), viewingFellowship.getUsernameFor(oppingPlayer));
			List<String> lines = fontRendererObj.listFormattedStringToWidth(s, xSize);
			for (String line : lines) {
				drawCenteredString(line, x, y, 16777215);
				y += fontRendererObj.FONT_HEIGHT;
			}
			y += fontRendererObj.FONT_HEIGHT;
			s = StatCollector.translateToLocalFormatted("got.gui.fellowships.opCheck2", viewingFellowship.getName(), viewingFellowship.getUsernameFor(oppingPlayer));
			lines = fontRendererObj.listFormattedStringToWidth(s, xSize);
			for (String line : lines) {
				drawCenteredString(line, x, y, 16777215);
				y += fontRendererObj.FONT_HEIGHT;
			}
		} else if (page == Page.DEOP) {
			int x = guiLeft + xSize / 2;
			int y = guiTop + 30;
			s = StatCollector.translateToLocalFormatted("got.gui.fellowships.deopCheck", viewingFellowship.getName(), viewingFellowship.getUsernameFor(deoppingPlayer));
			List<String> lines = fontRendererObj.listFormattedStringToWidth(s, xSize);
			for (String line : lines) {
				drawCenteredString(line, x, y, 16777215);
				y += fontRendererObj.FONT_HEIGHT;
			}
		} else if (page == Page.TRANSFER) {
			int x = guiLeft + xSize / 2;
			int y = guiTop + 30;
			s = StatCollector.translateToLocalFormatted("got.gui.fellowships.transferCheck1", viewingFellowship.getName(), viewingFellowship.getUsernameFor(transferringPlayer));
			List<String> lines = fontRendererObj.listFormattedStringToWidth(s, xSize);
			for (String line : lines) {
				drawCenteredString(line, x, y, 16777215);
				y += fontRendererObj.FONT_HEIGHT;
			}
			y += fontRendererObj.FONT_HEIGHT;
			s = StatCollector.translateToLocal("got.gui.fellowships.transferCheck2");
			drawCenteredString(s, x, y, 16777215);
			y += fontRendererObj.FONT_HEIGHT;
		} else if (page == Page.RENAME) {
			s = StatCollector.translateToLocalFormatted("got.gui.fellowships.renameName", viewingFellowship.getName());
			drawCenteredString(s, guiLeft + xSize / 2, textFieldRename.yPosition - 4 - fontRendererObj.FONT_HEIGHT, 16777215);
			textFieldRename.drawTextBox();
			if (checkValidRename != null) {
				drawCenteredString(checkValidRename, guiLeft + xSize / 2, textFieldRename.yPosition + textFieldRename.height + fontRendererObj.FONT_HEIGHT, 16711680);
			}
		} else if (page == Page.INVITATIONS) {
			int x = guiLeft;
			int y = guiTop + 10;
			s = StatCollector.translateToLocal("got.gui.fellowships.invites");
			drawCenteredString(s, guiLeft + xSize / 2, y, 16777215);
			y += fontRendererObj.FONT_HEIGHT + 10;
			if (allFellowshipInvites.isEmpty()) {
				y += fontRendererObj.FONT_HEIGHT;
				s = StatCollector.translateToLocal("got.gui.fellowships.invitesNone");
				drawCenteredString(s, guiLeft + xSize / 2, y, 16777215);
			} else {
				int[] invitesMinMax = scrollPaneInvites.getMinMaxIndices(allFellowshipInvites, displayedInvites);
				for (int index = invitesMinMax[0]; index <= invitesMinMax[1]; index++) {
					GOTFellowshipClient fs = allFellowshipInvites.get(index);
					drawFellowshipEntry(fs, x, y, i, j, true);
					y += fontRendererObj.FONT_HEIGHT + 5;
				}
			}
			if (scrollPaneInvites.hasScrollBar) {
				scrollPaneInvites.drawScrollBar();
			}
		} else if (page == Page.ACCEPT_INVITE_RESULT) {
			int x = guiLeft + xSize / 2;
			int y = guiTop + 30;
			if (acceptInviteResult == null) {
				int waitingDots = IntMath.mod(tickCounter / 10, 3);
				s = "";
				for (int l = 0; l < waitingDots; l++) {
					s = s + ".";
				}
				drawCenteredString(s, guiLeft + xSize / 2, y, 16777215);
			} else if (acceptInviteResult == GOTPacketFellowshipAcceptInviteResult.AcceptInviteResult.JOINED) {
				s = "Joining... (you shouldn't be able to see this message)";
				drawCenteredString(s, guiLeft + xSize / 2, y, 16777215);
			} else {
				if (acceptInviteResult == GOTPacketFellowshipAcceptInviteResult.AcceptInviteResult.DISBANDED) {
					s = StatCollector.translateToLocalFormatted("got.gui.fellowships.invited.disbanded", acceptInviteResultFellowshipName);
				} else if (acceptInviteResult == GOTPacketFellowshipAcceptInviteResult.AcceptInviteResult.TOO_LARGE) {
					s = StatCollector.translateToLocalFormatted("got.gui.fellowships.invited.maxSize", acceptInviteResultFellowshipName, Integer.valueOf(GOTConfig.getFellowshipMaxSize(mc.theWorld)));
				} else if (acceptInviteResult == GOTPacketFellowshipAcceptInviteResult.AcceptInviteResult.NONEXISTENT) {
					s = StatCollector.translateToLocalFormatted("got.gui.fellowships.invited.notFound");
				} else {
					s = "If you can see this message, something has gone wrong!";
				}
				List<String> lines = fontRendererObj.listFormattedStringToWidth(s, xSize);
				for (String line : lines) {
					drawCenteredString(line, x, y, 16777215);
					y += fontRendererObj.FONT_HEIGHT;
				}
			}
		}
	}

	public GOTFellowshipClient getMouseOverFellowship() {
		return mouseOverFellowship;
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int k = Mouse.getEventDWheel();
		if (k != 0) {
			k = Integer.signum(k);
			if (page == Page.LIST) {
				if (scrollPaneLeading.hasScrollBar && scrollPaneLeading.mouseOver) {
					int l = allFellowshipsLeading.size() - displayedFellowshipsLeading;
					scrollPaneLeading.mouseWheelScroll(k, l);
				}
				if (scrollPaneOther.hasScrollBar && scrollPaneOther.mouseOver) {
					int l = allFellowshipsOther.size() - displayedFellowshipsOther;
					scrollPaneOther.mouseWheelScroll(k, l);
				}
			}
			if (page == Page.FELLOWSHIP && scrollPaneMembers.hasScrollBar && scrollPaneMembers.mouseOver) {
				int l = viewingFellowship.getMemberUuids().size() - displayedMembers;
				scrollPaneMembers.mouseWheelScroll(k, l);
			}
			if (page == Page.INVITATIONS && scrollPaneInvites.hasScrollBar && scrollPaneInvites.mouseOver) {
				int l = allFellowshipInvites.size() - displayedInvites;
				scrollPaneInvites.mouseWheelScroll(k, l);
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		if (mc.thePlayer != null) {
			refreshFellowshipList();
		}
		int midX = guiLeft + xSize / 2;
		guiTop -= 20;
		buttonList.add(buttonCreate = new GuiButton(0, midX - 100, guiTop + 230, 200, 20, StatCollector.translateToLocal("got.gui.fellowships.create")));
		buttonList.add(buttonCreateThis = new GuiButton(1, midX - 100, guiTop + 170, 200, 20, StatCollector.translateToLocal("got.gui.fellowships.createThis")));
		buttonList.add(buttonInvitePlayer = new GOTGuiButtonFsOption(2, midX, guiTop + 232, 0, 48, StatCollector.translateToLocal("got.gui.fellowships.invite")));
		buttonList.add(buttonInviteThis = new GuiButton(3, midX - 100, guiTop + 170, 200, 20, StatCollector.translateToLocal("got.gui.fellowships.inviteThis")));
		buttonList.add(buttonDisband = new GOTGuiButtonFsOption(4, midX, guiTop + 232, 16, 48, StatCollector.translateToLocal("got.gui.fellowships.disband")));
		buttonList.add(buttonDisbandThis = new GuiButton(5, midX - 100, guiTop + 170, 200, 20, StatCollector.translateToLocal("got.gui.fellowships.disbandThis")));
		buttonList.add(buttonLeave = new GuiButton(6, midX - 60, guiTop + 230, 120, 20, StatCollector.translateToLocal("got.gui.fellowships.leave")));
		buttonList.add(buttonLeaveThis = new GuiButton(7, midX - 100, guiTop + 170, 200, 20, StatCollector.translateToLocal("got.gui.fellowships.leaveThis")));
		buttonList.add(buttonSetIcon = new GOTGuiButtonFsOption(8, midX, guiTop + 232, 48, 48, StatCollector.translateToLocal("got.gui.fellowships.setIcon")));
		buttonList.add(buttonRemove = new GuiButton(9, midX - 100, guiTop + 170, 200, 20, StatCollector.translateToLocal("got.gui.fellowships.remove")));
		buttonList.add(buttonTransfer = new GuiButton(10, midX - 100, guiTop + 170, 200, 20, StatCollector.translateToLocal("got.gui.fellowships.transfer")));
		buttonList.add(buttonRename = new GOTGuiButtonFsOption(11, midX, guiTop + 232, 32, 48, StatCollector.translateToLocal("got.gui.fellowships.rename")));
		buttonList.add(buttonRenameThis = new GuiButton(12, midX - 100, guiTop + 170, 200, 20, StatCollector.translateToLocal("got.gui.fellowships.renameThis")));
		buttonList.add(buttonPVP = new GOTGuiButtonFsOption(15, midX, guiTop + 232, 64, 48, StatCollector.translateToLocal("got.gui.fellowships.togglePVP")));
		buttonList.add(buttonHiredFF = new GOTGuiButtonFsOption(16, midX, guiTop + 232, 80, 48, StatCollector.translateToLocal("got.gui.fellowships.toggleHiredFF")));
		buttonList.add(buttonMapShow = new GOTGuiButtonFsOption(17, midX, guiTop + 232, 96, 48, StatCollector.translateToLocal("got.gui.fellowships.toggleMapShow")));
		buttonList.add(buttonOp = new GuiButton(18, midX - 100, guiTop + 170, 200, 20, StatCollector.translateToLocal("got.gui.fellowships.op")));
		buttonList.add(buttonDeop = new GuiButton(19, midX - 100, guiTop + 170, 200, 20, StatCollector.translateToLocal("got.gui.fellowships.deop")));
		guiTop += 30;
		buttonList.add(buttonBack = new GuiButton(13, guiLeft - 10, guiTop, 20, 20, "<"));
		buttonList.add(buttonInvites = new GOTGuiButtonFsInvites(14, guiLeft + xSize - 16, guiTop, ""));
		orderedFsOptionButtons.clear();
		orderedFsOptionButtons.add(buttonInvitePlayer);
		orderedFsOptionButtons.add(buttonDisband);
		orderedFsOptionButtons.add(buttonRename);
		orderedFsOptionButtons.add(buttonSetIcon);
		orderedFsOptionButtons.add(buttonMapShow);
		orderedFsOptionButtons.add(buttonPVP);
		orderedFsOptionButtons.add(buttonHiredFF);
		textFieldName = new GuiTextField(fontRendererObj, midX - 80, guiTop + 40, 160, 20);
		textFieldName.setMaxStringLength(40);
		textFieldPlayer = new GuiTextField(fontRendererObj, midX - 80, guiTop + 40, 160, 20);
		textFieldRename = new GuiTextField(fontRendererObj, midX - 80, guiTop + 40, 160, 20);
		textFieldRename.setMaxStringLength(40);
	}

	public boolean isFellowshipMaxSize(GOTFellowshipClient fellowship) {
		if (fellowship != null) {
			int limit = GOTConfig.getFellowshipMaxSize(mc.theWorld);
			return limit >= 0 && fellowship.getPlayerCount() >= limit;
		}
		return false;
	}

	@Override
	public void keyTyped(char c, int i) {
		if (page == Page.CREATE && textFieldName.textboxKeyTyped(c, i) || page == Page.INVITE && textFieldPlayer.textboxKeyTyped(c, i)) {
			return;
		}
		if (page == Page.RENAME && textFieldRename.textboxKeyTyped(c, i)) {
			return;
		}
		if (page != Page.LIST) {
			if (i == 1 || i == mc.gameSettings.keyBindInventory.getKeyCode()) {
				if (page == Page.INVITE || page == Page.DISBAND || page == Page.LEAVE || page == Page.REMOVE || page == Page.OP || page == Page.DEOP || page == Page.TRANSFER || page == Page.RENAME) {
					page = Page.FELLOWSHIP;
				} else if (page == Page.ACCEPT_INVITE_RESULT) {
					if (acceptInviteResult != null) {
						page = Page.INVITATIONS;
					}
				} else {
					page = Page.LIST;
				}
			}
		} else {
			super.keyTyped(c, i);
		}
	}

	@Override
	public void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		if (page == Page.LIST && mouseOverFellowship != null) {
			buttonSound();
			page = Page.FELLOWSHIP;
			viewingFellowship = mouseOverFellowship;
		}
		if (page == Page.CREATE) {
			textFieldName.mouseClicked(i, j, k);
		}
		if (page == Page.INVITE) {
			textFieldPlayer.mouseClicked(i, j, k);
		}
		if (page == Page.RENAME) {
			textFieldRename.mouseClicked(i, j, k);
		}
		if (page == Page.FELLOWSHIP && mouseOverPlayer != null && mouseOverPlayerRemove) {
			buttonSound();
			page = Page.REMOVE;
			removingPlayer = mouseOverPlayer;
		}
		if (page == Page.FELLOWSHIP && mouseOverPlayer != null && mouseOverPlayerOp) {
			buttonSound();
			page = Page.OP;
			oppingPlayer = mouseOverPlayer;
		}
		if (page == Page.FELLOWSHIP && mouseOverPlayer != null && mouseOverPlayerDeop) {
			buttonSound();
			page = Page.DEOP;
			deoppingPlayer = mouseOverPlayer;
		}
		if (page == Page.FELLOWSHIP && mouseOverPlayer != null && mouseOverPlayerTransfer) {
			buttonSound();
			page = Page.TRANSFER;
			transferringPlayer = mouseOverPlayer;
		}
		if (page == Page.INVITATIONS && mouseOverFellowship != null && mouseOverInviteAccept) {
			buttonSound();
			acceptInvitation(mouseOverFellowship);
			mouseOverFellowship = null;
			page = Page.ACCEPT_INVITE_RESULT;
		}
		if (page == Page.INVITATIONS && mouseOverFellowship != null && mouseOverInviteReject) {
			buttonSound();
			rejectInvitation(mouseOverFellowship);
			mouseOverFellowship = null;
		}
	}

	public void refreshFellowshipList() {
		allFellowshipsLeading.clear();
		allFellowshipsOther.clear();
		List<GOTFellowshipClient> fellowships = new ArrayList<>(GOTLevelData.getData(mc.thePlayer).getClientFellowships());
		for (GOTFellowshipClient fs : fellowships) {
			if (fs.isOwned()) {
				allFellowshipsLeading.add(fs);
			} else {
				allFellowshipsOther.add(fs);
			}
		}
		allFellowshipInvites.clear();
		allFellowshipInvites.addAll(GOTLevelData.getData(mc.thePlayer).getClientFellowshipInvites());
	}

	public void rejectInvitation(GOTFellowshipClient invite) {
		GOTPacketFellowshipRespondInvite packet = new GOTPacketFellowshipRespondInvite(invite, false);
		GOTPacketHandler.networkWrapper.sendToServer(packet);
	}

	public void renderIconTooltip(int x, int y, String s) {
		float z = zLevel;
		int stringWidth = 200;
		List desc = fontRendererObj.listFormattedStringToWidth(s, stringWidth);
		func_146283_a(desc, x, y);
		GL11.glDisable(2896);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		zLevel = z;
	}

	public void setupScrollBars(int i, int j) {
		if (page == Page.LIST) {
			displayedFellowshipsLeading = allFellowshipsLeading.size();
			displayedFellowshipsOther = allFellowshipsOther.size();
			scrollPaneLeading.hasScrollBar = false;
			scrollPaneOther.hasScrollBar = false;
			while (displayedFellowshipsLeading + displayedFellowshipsOther > 12) {
				if (displayedFellowshipsOther >= displayedFellowshipsLeading) {
					displayedFellowshipsOther--;
					scrollPaneOther.hasScrollBar = true;
				} else {
					displayedFellowshipsLeading--;
					scrollPaneLeading.hasScrollBar = true;
				}
			}
			scrollPaneLeading.paneX0 = guiLeft;
			scrollPaneLeading.scrollBarX0 = guiLeft + scrollBarX;
			scrollPaneLeading.paneY0 = guiTop + 10;
			scrollPaneLeading.paneY1 = scrollPaneLeading.paneY0 + fontRendererObj.FONT_HEIGHT + 10 + (fontRendererObj.FONT_HEIGHT + 5) * displayedFellowshipsLeading;
			scrollPaneLeading.mouseDragScroll(i, j);
			scrollPaneOther.paneX0 = guiLeft;
			scrollPaneOther.scrollBarX0 = guiLeft + scrollBarX;
			scrollPaneOther.paneY0 = scrollPaneLeading.paneY1 + 5;
			scrollPaneOther.paneY1 = scrollPaneOther.paneY0 + fontRendererObj.FONT_HEIGHT + 10 + (fontRendererObj.FONT_HEIGHT + 5) * displayedFellowshipsOther;
			scrollPaneOther.mouseDragScroll(i, j);
		}
		if (page == Page.FELLOWSHIP) {
			displayedMembers = viewingFellowship.getMemberUuids().size();
			scrollPaneMembers.hasScrollBar = false;
			if (displayedMembers > 11) {
				displayedMembers = 11;
				scrollPaneMembers.hasScrollBar = true;
			}
			scrollPaneMembers.paneX0 = guiLeft;
			scrollPaneMembers.scrollBarX0 = guiLeft + scrollBarX;
			scrollPaneMembers.paneY0 = guiTop + 10 + fontRendererObj.FONT_HEIGHT + 5 + 16 + 10 + fontRendererObj.FONT_HEIGHT + 10;
			scrollPaneMembers.paneY1 = scrollPaneMembers.paneY0 + (fontRendererObj.FONT_HEIGHT + 5) * displayedMembers;
		} else {
			scrollPaneMembers.hasScrollBar = false;
		}
		scrollPaneMembers.mouseDragScroll(i, j);
		if (page == Page.INVITATIONS) {
			displayedInvites = allFellowshipInvites.size();
			scrollPaneInvites.hasScrollBar = false;
			if (displayedInvites > 15) {
				displayedInvites = 15;
				scrollPaneInvites.hasScrollBar = true;
			}
			scrollPaneInvites.paneX0 = guiLeft;
			scrollPaneInvites.scrollBarX0 = guiLeft + scrollBarX;
			scrollPaneInvites.paneY0 = guiTop + 10 + fontRendererObj.FONT_HEIGHT + 10;
			scrollPaneInvites.paneY1 = scrollPaneInvites.paneY0 + (fontRendererObj.FONT_HEIGHT + 5) * displayedInvites;
			scrollPaneInvites.mouseDragScroll(i, j);
		}
	}

	public List<GOTFellowshipClient> sortFellowshipsForDisplay(List<GOTFellowshipClient> list) {
		List<GOTFellowshipClient> sorted = new ArrayList<>(list);
		Collections.sort(sorted, (fs1, fs2) -> {
			int count1 = fs1.getPlayerCount();
			int count2 = fs2.getPlayerCount();
			if (count1 == count2) {
				return fs1.getName().toLowerCase().compareTo(fs2.getName().toLowerCase());
			}
			return -Integer.compare(count1, count2);
		});
		return sorted;
	}

	public List<GameProfile> sortMembersForDisplay(GOTFellowshipClient fs) {
		List<GameProfile> members = new ArrayList<>(fs.getMemberProfiles());
		Collections.sort(members, (player1, player2) -> {
			boolean admin1 = fs.isAdmin(player1.getId());
			boolean admin2 = fs.isAdmin(player2.getId());
			boolean online1 = GOTGuiFellowships.isPlayerOnline(player1);
			boolean online2 = GOTGuiFellowships.isPlayerOnline(player2);
			if (online1 == online2) {
				if (admin1 == admin2) {
					return player1.getName().toLowerCase().compareTo(player2.getName().toLowerCase());
				}
				if (admin1 && !admin2) {
					return -1;
				}
				if (!admin1 && admin2) {
					return 1;
				}
			} else {
				if (online1 && !online2) {
					return -1;
				}
				if (!online1 && online2) {
					return 1;
				}
			}
			return 0;
		});
		return members;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		tickCounter++;
		refreshFellowshipList();
		textFieldName.updateCursorCounter();
		if (page != Page.CREATE) {
			textFieldName.setText("");
		}
		textFieldPlayer.updateCursorCounter();
		if (page != Page.INVITE || isFellowshipMaxSize(viewingFellowship)) {
			textFieldPlayer.setText("");
		}
		textFieldRename.updateCursorCounter();
		if (page != Page.RENAME) {
			textFieldRename.setText("");
		}
	}

	public static boolean isPlayerOnline(GameProfile player) {
		EntityClientPlayerMP mcPlayer = Minecraft.getMinecraft().thePlayer;
		List list = mcPlayer.sendQueue.playerInfoList;
		for (Object obj : list) {
			GuiPlayerInfo info = (GuiPlayerInfo) obj;
			if (info.name.equalsIgnoreCase(player.getName())) {
				return true;
			}
		}
		return false;
	}

	public enum Page {
		LIST, CREATE, FELLOWSHIP, INVITE, DISBAND, LEAVE, REMOVE, OP, DEOP, TRANSFER, RENAME, INVITATIONS, ACCEPT_INVITE_RESULT;
	}
}
