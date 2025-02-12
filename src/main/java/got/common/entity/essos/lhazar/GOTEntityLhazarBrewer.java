package got.common.entity.essos.lhazar;

import got.common.database.*;
import got.common.item.other.GOTItemMug;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GOTEntityLhazarBrewer extends GOTEntityLhazarTrader {
	public GOTEntityLhazarBrewer(World world) {
		super(world);
		canBeMarried = false;
	}

	@Override
	public GOTTradeEntries getBuyPool() {
		return GOTTradeEntries.COMMON_BREWER_BUY;
	}

	@Override
	public GOTTradeEntries getSellPool() {
		return GOTTradeEntries.COMMON_BREWER_BUY;
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		data = super.onSpawnWithEgg(data);
		ItemStack drink = new ItemStack(GOTRegistry.mugAraq);
		GOTItemMug.setVessel(drink, GOTFoods.NOMAD_DRINK.getRandomVessel(rand), true);
		npcItemsInv.setIdleItem(drink);
		return data;
	}
}
