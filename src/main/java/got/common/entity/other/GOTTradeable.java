package got.common.entity.other;

import got.common.database.GOTTradeEntries;
import got.common.faction.GOTFaction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface GOTTradeable {
	boolean canTradeWith(EntityPlayer var1);

	GOTTradeEntries getBuyPool();

	GOTFaction getFaction();

	String getNPCName();

	GOTTradeEntries getSellPool();

	void onPlayerTrade(EntityPlayer var1, GOTTradeEntries.TradeType var2, ItemStack var3);

	public interface Smith extends GOTTradeable {
	}

}
