package got.common.entity.westeros.dorne;

import got.common.GOTLevelData;
import got.common.database.*;
import got.common.entity.other.GOTTradeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class GOTEntityDorneMarketTrader extends GOTEntityDorneMan implements GOTTradeable {
	public GOTEntityDorneMarketTrader(World world) {
		super(world);
		canBeMarried = false;
		addTargetTasks(false);
	}

	@Override
	public boolean canTradeWith(EntityPlayer entityplayer) {
		return GOTLevelData.getData(entityplayer).getAlignment(getFaction()) >= 0.0f && isFriendly(entityplayer);
	}

	@Override
	public float getAlignmentBonus() {
		return 2.0f;
	}

	@Override
	public String getSpeechBank(EntityPlayer entityplayer) {
		if (isFriendly(entityplayer)) {
			return "westeros/dorne/marketTrader/friendly";
		}
		return "westeros/dorne/marketTrader/hostile";
	}

	@Override
	public void onPlayerTrade(EntityPlayer entityplayer, GOTTradeEntries.TradeType type, ItemStack itemstack) {
		GOTLevelData.getData(entityplayer).addAchievement(GOTAchievement.TRADE);
	}
}