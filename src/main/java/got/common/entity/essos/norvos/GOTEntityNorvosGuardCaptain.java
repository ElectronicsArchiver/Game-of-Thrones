package got.common.entity.essos.norvos;

import got.common.GOTLevelData;
import got.common.database.*;
import got.common.entity.ai.GOTEntityAIAttackOnCollide;
import got.common.entity.other.GOTUnitTradeable;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GOTEntityNorvosGuardCaptain extends GOTEntityNorvosGuard implements GOTUnitTradeable {
	public GOTEntityNorvosGuardCaptain(World world) {
		super(world);
		canBeMarried = false;
		addTargetTasks(false);
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25.0);
	}

	@Override
	public boolean canTradeWith(EntityPlayer entityplayer) {
		return GOTLevelData.getData(entityplayer).getAlignment(getFaction()) >= 50.0f && isFriendly(entityplayer);
	}

	@Override
	public EntityAIBase createNorvosAttackAI() {
		return new GOTEntityAIAttackOnCollide(this, 1.4, false);
	}

	@Override
	public float getAlignmentBonus() {
		return 5.0f;
	}

	@Override
	public String getSpeechBank(EntityPlayer entityplayer) {
		if (isFriendly(entityplayer)) {
			if (canTradeWith(entityplayer)) {
				return "standart/civilized/usual_friendly";
			}
			return "standart/civilized/usual_neutral";
		}
		return "standart/civilized/usual_hostile";
	}

	@Override
	public GOTUnitTradeEntries getUnits() {
		return GOTUnitTradeEntries.NORVOS;
	}

	@Override
	public GOTInvasions getWarhorn() {
		return GOTInvasions.NORVOS;
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		data = super.onSpawnWithEgg(data);
		npcItemsInv.setMeleeWeapon(new ItemStack(GOTRegistry.essosPolearm));
		npcItemsInv.setIdleItem(npcItemsInv.getMeleeWeapon());
		setCurrentItemOrArmor(4, null);
		return data;
	}

	@Override
	public void onUnitTrade(EntityPlayer entityplayer) {
		GOTLevelData.getData(entityplayer).addAchievement(GOTAchievement.trade);
	}
}
