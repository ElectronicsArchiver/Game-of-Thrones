package got.common.entity.essos.yiti;

import got.common.database.*;
import got.common.item.other.GOTItemRobes;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GOTEntityYiTiBrewer extends GOTEntityYiTiMarketTrader {
	public GOTEntityYiTiBrewer(World world) {
		super(world);
		canBeMarried = false;
	}

	@Override
	public GOTTradeEntries getBuyPool() {
		return GOTTradeEntries.COMMON_BREWER_BUY;
	}

	@Override
	public GOTTradeEntries getSellPool() {
		return GOTTradeEntries.COMMON_BREWER_SELL;
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		data = super.onSpawnWithEgg(data);
		npcItemsInv.setIdleItem(new ItemStack(GOTRegistry.mugAraq));
		int robeColor = 12346368;
		ItemStack body = new ItemStack(GOTRegistry.kaftanChestplate);
		ItemStack legs = new ItemStack(GOTRegistry.kaftanLeggings);
		GOTItemRobes.setRobesColor(body, robeColor);
		GOTItemRobes.setRobesColor(legs, robeColor);
		setCurrentItemOrArmor(3, body);
		setCurrentItemOrArmor(2, legs);
		return data;
	}
}
