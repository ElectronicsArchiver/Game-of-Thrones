package got.common.entity.essos.dothraki;

import java.util.List;

import got.common.GOTConfig;
import got.common.database.*;
import got.common.entity.ai.*;
import got.common.entity.other.*;
import got.common.faction.GOTFaction;
import got.common.quest.*;
import got.common.world.biome.essos.GOTBiomeDothrakiSea;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class GOTEntityDothraki extends GOTEntityHumanBase implements IPickpocketable {
	public int draka;

	public GOTEntityDothraki(World world) {
		super(world);
		canBeMarried = true;
		setSize(0.6f, 1.8f);
		getNavigator().setAvoidsWater(true);
		getNavigator().setBreakDoors(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new GOTEntityAIHiredRemainStill(this));
		tasks.addTask(2, createDothrakiAttackAI());
		tasks.addTask(3, new GOTEntityAIFollowHiringPlayer(this));
		tasks.addTask(4, new EntityAIOpenDoor(this, true));
		tasks.addTask(5, new EntityAIWander(this, 1.0));
		tasks.addTask(6, new GOTEntityAIEat(this, GOTFoods.WILD, 8000));
		tasks.addTask(6, new GOTEntityAIDrink(this, GOTFoods.WILD_DRINK, 8000));
		tasks.addTask(7, new EntityAIWatchClosest2(this, EntityPlayer.class, 8.0f, 0.02f));
		tasks.addTask(7, new EntityAIWatchClosest2(this, GOTEntityNPC.class, 5.0f, 0.02f));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityLiving.class, 8.0f, 0.02f));
		tasks.addTask(9, new EntityAILookIdle(this));
		int target = addTargetTasks(true);
		targetTasks.addTask(target + 1, new GOTEntityAIDothrakiSkirmish(this, true));
		spawnRidingHorse = rand.nextInt(10) == 0;
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.22);
		getEntityAttribute(npcRangedAccuracy).setBaseValue(0.75);
		getEntityAttribute(horseAttackSpeed).setBaseValue(2.0);
	}

	public boolean canDothrakiSkirmish() {
		return !questInfo.anyActiveQuestPlayers();
	}

	public EntityAIBase createDothrakiAttackAI() {
		return new GOTEntityAIAttackOnCollide(this, 1.4, false);
	}

	@Override
	public GOTMiniQuest createMiniQuest() {
		return GOTMiniQuestFactory.DOTHRAKI.createQuest(this);
	}

	@Override
	public void dropFewItems(boolean flag, int i) {
		super.dropFewItems(flag, i);
		int bones = rand.nextInt(2) + rand.nextInt(i + 1);
		for (int l = 0; l < bones; ++l) {
			dropItem(Items.bone, 1);
		}
		dropRohanItems(flag, i);
	}

	public void dropRohanItems(boolean flag, int i) {
		if (rand.nextInt(6) == 0) {
			dropChestContents(GOTChestContents.DOTHRAKI, 1, 2 + i);
		}
	}

	@Override
	public float getAlignmentBonus() {
		return 5.0f;
	}

	@Override
	public float getBlockPathWeight(int i, int j, int k) {
		float f = 0.0f;
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(i, k);
		if (biome instanceof GOTBiomeDothrakiSea) {
			f += 20.0f;
		}
		return f;
	}

	@Override
	public GOTMiniQuestFactory getBountyHelpSpeechDir() {
		return GOTMiniQuestFactory.DOTHRAKI;
	}

	@Override
	public boolean getCanSpawnHere() {
		if (super.getCanSpawnHere()) {
			if (liftSpawnRestrictions) {
				return true;
			}
			int i = MathHelper.floor_double(posX);
			int j = MathHelper.floor_double(boundingBox.minY);
			int k = MathHelper.floor_double(posZ);
			if (j > 62 && j < 140 && worldObj.getBlock(i, j - 1, k) == worldObj.getBiomeGenForCoords(i, k).topBlock) {
				return true;
			}
		}
		return false;
	}

	public String getDothrakiSkirmishSpeech() {
		return "standart/special/gladiator";
	}

	@Override
	public GOTFaction getFaction() {
		return GOTFaction.DOTHRAKI;
	}

	@Override
	public String getNPCName() {
		return familyInfo.getName();
	}

	@Override
	public float getPoisonedArrowChance() {
		return 0.5f;
	}

	@Override
	public String getSpeechBank(EntityPlayer entityplayer) {
		if (isFriendly(entityplayer)) {
			if (hiredNPCInfo.getHiringPlayer() == entityplayer) {
				return "standart/wild/hired_soldier";
			}
			return "standart/wild/usual_friendly";
		}
		return "standart/wild/usual_hostile";
	}

	public boolean isDothrakSkirmishing() {
		return draka > 0;
	}

	@Override
	public void onAttackModeChange(GOTEntityNPC.AttackMode mode, boolean mounted) {
		if (mode == GOTEntityNPC.AttackMode.IDLE) {
			if (mounted) {
				setCurrentItemOrArmor(0, npcItemsInv.getIdleItemMounted());
			} else {
				setCurrentItemOrArmor(0, npcItemsInv.getIdleItem());
			}
		} else if (mounted) {
			setCurrentItemOrArmor(0, npcItemsInv.getMeleeWeaponMounted());
		} else {
			setCurrentItemOrArmor(0, npcItemsInv.getMeleeWeapon());
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!worldObj.isRemote && isDothrakSkirmishing()) {
			if (!GOTConfig.enableDothrakiSkirmish) {
				draka = 0;
			} else if (!(getAttackTarget() instanceof GOTEntityDothraki)) {
				--draka;
			}
		}
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		data = super.onSpawnWithEgg(data);
		if (rand.nextInt(3) == 0) {
			npcItemsInv.setMeleeWeapon(new ItemStack(GOTRegistry.nomadBattleaxe));
		} else {
			npcItemsInv.setMeleeWeapon(new ItemStack(GOTRegistry.nomadSword));
		}
		npcItemsInv.setMeleeWeaponMounted(npcItemsInv.getMeleeWeapon());
		if (rand.nextInt(4) == 0) {
			npcItemsInv.setSpearBackup(npcItemsInv.getMeleeWeapon());
			npcItemsInv.setMeleeWeapon(new ItemStack(GOTRegistry.nomadSpear));
		}
		npcItemsInv.setIdleItem(npcItemsInv.getMeleeWeapon());
		npcItemsInv.setIdleItemMounted(npcItemsInv.getMeleeWeaponMounted());
		setCurrentItemOrArmor(1, new ItemStack(GOTRegistry.dothrakiBoots));
		setCurrentItemOrArmor(2, new ItemStack(GOTRegistry.dothrakiLeggings));
		setCurrentItemOrArmor(3, new ItemStack(GOTRegistry.dothrakiChestplate));
		if (rand.nextInt(10) == 0) {
			setCurrentItemOrArmor(4, new ItemStack(GOTRegistry.dothrakiHelmet));
		} else {
			setCurrentItemOrArmor(4, null);
		}
		return data;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		draka = nbt.getInteger("Skirmish");
	}

	public void setDothrakiSkirmishing() {
		int prevSkirmishTick = draka;
		draka = 160;
		if (!worldObj.isRemote && prevSkirmishTick == 0) {
			List nearbyPlayers = worldObj.getEntitiesWithinAABB(EntityPlayer.class, boundingBox.expand(24.0, 24.0, 24.0));
			for (Object nearbyPlayer : nearbyPlayers) {
				EntityPlayer entityplayer = (EntityPlayer) nearbyPlayer;
				GOTSpeech.sendSpeech(entityplayer, this, GOTSpeech.getRandomSpeechForPlayer(this, getDothrakiSkirmishSpeech(), entityplayer));
			}
		}
	}

	@Override
	public void setupNPCGender() {
		familyInfo.setMale(rand.nextBoolean());
	}

	@Override
	public void setupNPCName() {
		familyInfo.setName(GOTNames.getDothrakiName(rand, familyInfo.isMale()));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("Skirmish", draka);
	}
}
