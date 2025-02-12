package got.common.entity.essos.legendary.quest;

import java.util.*;

import got.common.*;
import got.common.database.*;
import got.common.entity.ai.*;
import got.common.entity.other.*;
import got.common.faction.GOTFaction;
import got.common.quest.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GOTEntityJaqenHghar extends GOTEntityHumanBase {
	public GOTEntityJaqenHghar(World world) {
		super(world);
		isImmuneToFrost = true;
		canBeMarried = false;
		addTargetTasks(false);
		setSize(0.6f, 1.8f);
		getNavigator().setAvoidsWater(true);
		getNavigator().setBreakDoors(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new GOTEntityAIHiredRemainStill(this));
		tasks.addTask(2, new GOTEntityAIAttackOnCollide(this, 1.4, false));
		tasks.addTask(3, new GOTEntityAIFollowHiringPlayer(this));
		tasks.addTask(4, new EntityAIOpenDoor(this, true));
		tasks.addTask(5, new EntityAIWander(this, 1.0));
		tasks.addTask(6, new GOTEntityAIEat(this, GOTFoods.ESSOS, 8000));
		tasks.addTask(6, new GOTEntityAIDrink(this, GOTFoods.ESSOS_DRINK, 8000));
		tasks.addTask(7, new EntityAIWatchClosest2(this, EntityPlayer.class, 8.0f, 0.02f));
		tasks.addTask(7, new EntityAIWatchClosest2(this, GOTEntityNPC.class, 5.0f, 0.02f));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityLiving.class, 8.0f, 0.02f));
		tasks.addTask(9, new EntityAILookIdle(this));
	}

	public boolean addMQOfferFor(EntityPlayer entityplayer) {
		GOTMiniQuestWelcome quest;
		GOTPlayerData pd = GOTLevelData.getData(entityplayer);
		if (pd.getMiniQuestsForEntity(this, true).isEmpty() && ((GOTMiniQuest) (quest = new GOTMiniQuestWelcome(null, this))).canPlayerAccept(entityplayer)) {
			questInfo.setPlayerSpecificOffer(entityplayer, quest);
			return true;
		}
		return false;
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.22);
	}

	public void arriveAt(EntityPlayer entityplayer) {
		ArrayList<EntityPlayer> msgPlayers = new ArrayList<>();
		if (entityplayer != null) {
			msgPlayers.add(entityplayer);
		}
		List worldPlayers = worldObj.playerEntities;
		for (Object obj : worldPlayers) {
			EntityPlayer player = (EntityPlayer) obj;
			if (msgPlayers.contains(player)) {
				continue;
			}
			double d = 64.0;
			double dSq = d * d;
			if (getDistanceSqToEntity(player) >= dSq) {
				continue;
			}
			msgPlayers.add(player);
		}
		for (EntityPlayer player : msgPlayers) {
			GOTSpeech.sendSpeechAndChatMessage(player, this, "legendary/jaqen_arrive");
		}
		doJaqenFX();
	}

	public void depart() {
		ArrayList<EntityPlayer> msgPlayers = new ArrayList<>();
		List worldPlayers = worldObj.playerEntities;
		for (Object obj : worldPlayers) {
			EntityPlayer player = (EntityPlayer) obj;
			if (msgPlayers.contains(player)) {
				continue;
			}
			double d = 64.0;
			double dSq = d * d;
			if (getDistanceSqToEntity(player) >= dSq) {
				continue;
			}
			msgPlayers.add(player);
		}
		for (EntityPlayer player : msgPlayers) {
			GOTSpeech.sendSpeechAndChatMessage(player, this, "legendary/jaqen_depart");
		}
		doJaqenFX();
		setDead();
	}

	public void doJaqenFX() {
		playSound("random.pop", 2.0f, 0.5f + rand.nextFloat() * 0.5f);
		worldObj.setEntityState(this, (byte) 16);
	}

	@Override
	public float getAlignmentBonus() {
		return 300.0f;
	}

	@Override
	public GOTFaction getFaction() {
		return GOTFaction.LORATH;
	}

	@Override
	public GOTAchievement getKillAchievement() {
		return GOTAchievement.killJaqenHghar;
	}

	@Override
	public String getSpeechBank(EntityPlayer entityplayer) {
		if (isFriendly(entityplayer)) {
			return "legendary/jaqen_friendly";
		}
		return "legendary/jaqen_hostile";
	}

	@Override
	public int getTotalArmorValue() {
		return 15;
	}

	@Override
	public void onArtificalSpawn() {
		GOTJaqenHgharTracker.addNewWanderer(getUniqueID());
		arriveAt(null);
	}

	@Override
	public void onAttackModeChange(GOTEntityNPC.AttackMode mode, boolean mounted) {
		if (mode == GOTEntityNPC.AttackMode.IDLE) {
			setCurrentItemOrArmor(0, npcItemsInv.getIdleItem());
		} else {
			setCurrentItemOrArmor(0, npcItemsInv.getMeleeWeapon());
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!worldObj.isRemote && !GOTJaqenHgharTracker.isWandererActive(getUniqueID()) && getAttackTarget() == null) {
			depart();
		}
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		data = super.onSpawnWithEgg(data);
		npcItemsInv.setMeleeWeapon(new ItemStack(GOTRegistry.essosSword));
		npcItemsInv.setIdleItem(npcItemsInv.getMeleeWeapon());
		return data;
	}

	@Override
	public void setupNPCGender() {
		familyInfo.setMale(true);
	}

	@Override
	public boolean speakTo(EntityPlayer entityplayer) {
		if (GOTJaqenHgharTracker.isWandererActive(getUniqueID())) {
			if (questInfo.getOfferFor(entityplayer) != null) {
				return super.speakTo(entityplayer);
			}
			if (addMQOfferFor(entityplayer)) {
				GOTJaqenHgharTracker.setWandererActive(getUniqueID());
				String speechBank = "legendary/jaqen_welcome";
				this.sendSpeechBank(entityplayer, speechBank);
				return true;
			}
		}
		return super.speakTo(entityplayer);
	}
}
