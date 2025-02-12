package got.common.entity.essos.ghiscar;

import got.common.database.*;
import got.common.entity.ai.GOTEntityAINearestAttackableTargetBasic;
import got.common.faction.GOTFaction;
import got.common.item.other.GOTItemRobes;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.world.World;

public class GOTEntityGhiscarGladiator extends GOTEntityGhiscarMan {
	public static ItemStack[] weaponsIron = { new ItemStack(GOTRegistry.essosSword), new ItemStack(GOTRegistry.essosDagger), new ItemStack(GOTRegistry.essosDaggerPoisoned), new ItemStack(GOTRegistry.essosHammer) };
	public static int[] turbanColors = { 1643539, 6309443, 7014914, 7809314, 5978155 };
	public static int[] leatherDyes = { 10855845, 8026746, 5526612, 3684408, 8350297, 10388590, 4799795, 5330539, 4211801, 2632504 };

	public GOTEntityGhiscarGladiator(World world) {
		super(world);
		canBeMarried = false;
		addTargetTasks(true);
		npcShield = GOTShields.GHISCAR;
		int target = addTargetTasks(false);
		targetTasks.addTask(target + 1, new GOTEntityAINearestAttackableTargetBasic(this, GOTEntityGhiscarGladiator.class, 0, true));
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(npcRangedAccuracy).setBaseValue(0.75);
	}

	@Override
	public void dropFewItems(boolean flag, int i) {
	}

	public ItemStack dyeLeather(ItemStack itemstack) {
		int i = rand.nextInt(leatherDyes.length);
		int color = leatherDyes[i];
		((ItemArmor) itemstack.getItem()).func_82813_b(itemstack, color);
		return itemstack;
	}

	@Override
	public float getAlignmentBonus() {
		return 0.0f;
	}

	@Override
	public GOTFaction getFaction() {
		return GOTFaction.GHISCAR;
	}

	@Override
	public GOTAchievement getKillAchievement() {
		return GOTAchievement.killGladiator;
	}

	@Override
	public String getSpeechBank(EntityPlayer entityplayer) {
		return "standart/special/gladiator";
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		data = super.onSpawnWithEgg(data);
		int i = rand.nextInt(weaponsIron.length);
		npcItemsInv.setMeleeWeapon(weaponsIron[i].copy());
		npcItemsInv.setIdleItem(npcItemsInv.getMeleeWeapon());
		setCurrentItemOrArmor(1, new ItemStack(GOTRegistry.ghiscarBoots));
		setCurrentItemOrArmor(2, new ItemStack(GOTRegistry.ghiscarLeggings));
		setCurrentItemOrArmor(3, new ItemStack(GOTRegistry.ghiscarChestplate));
		ItemStack turban = new ItemStack(GOTRegistry.robesHelmet);
		int robeColor = turbanColors[rand.nextInt(turbanColors.length)];
		GOTItemRobes.setRobesColor(turban, robeColor);
		setCurrentItemOrArmor(4, turban);
		return data;
	}

	@Override
	public void setupNPCGender() {
		familyInfo.setMale(true);
	}
}
