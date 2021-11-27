package got.common.network;

import java.io.IOException;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.*;
import got.GOT;
import got.common.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;

public class GOTPacketMoneyGet implements IMessage {
	public ItemStack item;

	public GOTPacketMoneyGet() {
	}

	public GOTPacketMoneyGet(ItemStack items) {
		item = items;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		try {
			item = new PacketBuffer(data).readItemStackFromBuffer();
		} catch (IOException e) {
			FMLLog.severe("Error writing money data");
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		try {
			new PacketBuffer(data).writeItemStackToBuffer(item);
		} catch (IOException e) {
			FMLLog.severe("Error reading money data");
			e.printStackTrace();
		}
	}

	public static class Handler implements IMessageHandler<GOTPacketMoneyGet, IMessage> {
		@Override
		public IMessage onMessage(GOTPacketMoneyGet packet, MessageContext context) {
			ItemStack item = packet.item;
			if (GOT.sell.containsKey(item)) {
				EntityPlayerMP player = context.getServerHandler().playerEntity;
				GOTPlayerData pd = GOTLevelData.getData(player);
				int cost = GOT.sell.get(item);
				int index = -1;
				for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
					if (player.inventory.mainInventory[i] == null || player.inventory.mainInventory[i].getItem() != item.getItem() || player.inventory.mainInventory[i].getItemDamage() != item.getItemDamage()) {
						continue;
					}
					index = i;
					break;
				}
				if (index >= 0) {
					if (--player.inventory.mainInventory[index].stackSize <= 0) {
						player.inventory.mainInventory[index] = null;
					}
					new GOTPacketMoneyChange(pd.balance += cost);
					pd.markDirty();
					GOTPacketHandler.networkWrapper.sendTo(packet, player);
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + StatCollector.translateToLocalFormatted("got.gui.money.get", item.getDisplayName())));
				}
			}
			return null;
		}
	}
}