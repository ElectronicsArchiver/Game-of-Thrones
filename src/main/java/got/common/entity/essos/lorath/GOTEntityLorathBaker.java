package got.common.entity.essos.lorath;

import got.common.database.*;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GOTEntityLorathBaker extends GOTEntityLorathTrader {
	public GOTEntityLorathBaker(World world) {
		super(world);
		canBeMarried = false;
	}

	@Override
	public GOTTradeEntries getBuyPool() {
		return GOTTradeEntries.COMMON_BAKER_BUY;
	}

	@Override
	public GOTTradeEntries getSellPool() {
		return GOTTradeEntries.COMMON_BAKER_SELL;
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		data = super.onSpawnWithEgg(data);
		npcItemsInv.setMeleeWeapon(new ItemStack(GOTRegistry.rollingPin));
		npcItemsInv.setIdleItem(new ItemStack(GOTRegistry.oliveBread));
		return data;
	}
}
