package got.common.entity.essos.ibben;

import got.common.database.GOTRegistry;
import got.common.entity.ai.GOTEntityAIRangedAttack;
import got.common.entity.other.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GOTEntityIbbenAxeThrower extends GOTEntityIbbenWarrior {
	public GOTEntityIbbenAxeThrower(World world) {
		super(world);
		canBeMarried = false;
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float f) {
		ItemStack axeItem = npcItemsInv.getRangedWeapon();
		if (axeItem == null) {
			axeItem = new ItemStack(GOTRegistry.ironThrowingAxe);
		}
		GOTEntityThrowingAxe axe = new GOTEntityThrowingAxe(worldObj, this, target, axeItem, 1.0f, (float) getEntityAttribute(npcRangedAccuracy).getAttributeValue());
		playSound("random.bow", 1.0f, 1.0f / (rand.nextFloat() * 0.4f + 0.8f));
		worldObj.spawnEntityInWorld(axe);
		swingItem();
	}

	@Override
	public EntityAIBase createIbbenAttackAI() {
		return new GOTEntityAIRangedAttack(this, 1.4, 40, 60, 12.0f);
	}

	@Override
	public void onAttackModeChange(GOTEntityNPC.AttackMode mode, boolean mounted) {
		if (mode == GOTEntityNPC.AttackMode.IDLE) {
			setCurrentItemOrArmor(0, npcItemsInv.getIdleItem());
		} else {
			setCurrentItemOrArmor(0, npcItemsInv.getRangedWeapon());
		}
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		data = super.onSpawnWithEgg(data);
		if (rand.nextInt(3) == 0) {
			npcItemsInv.setRangedWeapon(new ItemStack(GOTRegistry.ironThrowingAxe));
		} else {
			npcItemsInv.setRangedWeapon(new ItemStack(GOTRegistry.bronzeThrowingAxe));
		}
		npcItemsInv.setIdleItem(npcItemsInv.getRangedWeapon());
		return data;
	}
}
