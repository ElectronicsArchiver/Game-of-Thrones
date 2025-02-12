package got.common.faction;

import java.io.File;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLLog;
import got.common.GOTLevelData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.UsernameCache;

public class GOTFactionBounties {
	public static Map<GOTFaction, GOTFactionBounties> factionBountyMap = new HashMap<>();
	public static boolean needsLoad = true;
	public static int KILL_RECORD_TIME = 3456000;
	public static int BOUNTY_KILLED_TIME = 864000;
	public GOTFaction theFaction;
	public Map<UUID, PlayerData> playerList = new HashMap<>();
	public boolean needsSave = false;

	public GOTFactionBounties(GOTFaction f) {
		theFaction = f;
	}

	public List<PlayerData> findBountyTargets(int killAmount) {
		ArrayList<PlayerData> players = new ArrayList<>();
		for (PlayerData pd : playerList.values()) {
			if (pd.recentlyBountyKilled() || pd.getNumKills() < killAmount) {
				continue;
			}
			players.add(pd);
		}
		return players;
	}

	public PlayerData forPlayer(EntityPlayer entityplayer) {
		return this.forPlayer(entityplayer.getUniqueID());
	}

	public PlayerData forPlayer(UUID id) {
		PlayerData pd = playerList.get(id);
		if (pd == null) {
			pd = new PlayerData(this, id);
			playerList.put(id, pd);
		}
		return pd;
	}

	public void markDirty() {
		needsSave = true;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		playerList.clear();
		if (nbt.hasKey("PlayerList")) {
			NBTTagList playerTags = nbt.getTagList("PlayerList", 10);
			for (int i = 0; i < playerTags.tagCount(); ++i) {
				NBTTagCompound playerData = playerTags.getCompoundTagAt(i);
				UUID id = UUID.fromString(playerData.getString("UUID"));
				if (id == null) {
					continue;
				}
				PlayerData pd = new PlayerData(this, id);
				pd.readFromNBT(playerData);
				playerList.put(id, pd);
			}
		}
	}

	public void update() {
		for (PlayerData pd : playerList.values()) {
			pd.update();
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList playerTags = new NBTTagList();
		for (Map.Entry<UUID, PlayerData> e : playerList.entrySet()) {
			UUID id = e.getKey();
			PlayerData pd = e.getValue();
			if (!pd.shouldSave()) {
				continue;
			}
			NBTTagCompound playerData = new NBTTagCompound();
			playerData.setString("UUID", id.toString());
			pd.writeToNBT(playerData);
			playerTags.appendTag(playerData);
		}
		nbt.setTag("PlayerList", playerTags);
	}

	public static boolean anyDataNeedsSave() {
		for (GOTFactionBounties fb : factionBountyMap.values()) {
			if (!fb.needsSave) {
				continue;
			}
			return true;
		}
		return false;
	}

	public static GOTFactionBounties forFaction(GOTFaction fac) {
		GOTFactionBounties bounties = factionBountyMap.get(fac);
		if (bounties == null) {
			bounties = GOTFactionBounties.loadFaction(fac);
			if (bounties == null) {
				bounties = new GOTFactionBounties(fac);
			}
			factionBountyMap.put(fac, bounties);
		}
		return bounties;
	}

	public static File getBountiesDir() {
		File dir = new File(GOTLevelData.getOrCreateGOTDir(), "factionbounties");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	public static File getFactionFile(GOTFaction f, boolean findLegacy) {
		File defaultFile = new File(GOTFactionBounties.getBountiesDir(), f.codeName() + ".dat");
		if (!findLegacy || defaultFile.exists()) {
			return defaultFile;
		}
		for (String alias : f.listAliases()) {
			File aliasFile = new File(GOTFactionBounties.getBountiesDir(), alias + ".dat");
			if (!aliasFile.exists()) {
				continue;
			}
			return aliasFile;
		}
		return defaultFile;
	}

	public static void loadAll() {
		try {
			factionBountyMap.clear();
			needsLoad = false;
			GOTFactionBounties.saveAll();
		} catch (Exception e) {
			FMLLog.severe("Error loading GOT faction bounty data");
			e.printStackTrace();
		}
	}

	public static GOTFactionBounties loadFaction(GOTFaction fac) {
		File file = GOTFactionBounties.getFactionFile(fac, true);
		try {
			NBTTagCompound nbt = GOTLevelData.loadNBTFromFile(file);
			if (nbt.hasNoTags()) {
				return null;
			}
			GOTFactionBounties fb = new GOTFactionBounties(fac);
			fb.readFromNBT(nbt);
			return fb;
		} catch (Exception e) {
			FMLLog.severe("Error loading GOT faction bounty data for %s", fac.codeName());
			e.printStackTrace();
			return null;
		}
	}

	public static void saveAll() {
		try {
			for (GOTFactionBounties fb : factionBountyMap.values()) {
				if (!fb.needsSave) {
					continue;
				}
				GOTFactionBounties.saveFaction(fb);
				fb.needsSave = false;
			}
		} catch (Exception e) {
			FMLLog.severe("Error saving GOT faction bounty data");
			e.printStackTrace();
		}
	}

	public static void saveFaction(GOTFactionBounties fb) {
		try {
			NBTTagCompound nbt = new NBTTagCompound();
			fb.writeToNBT(nbt);
			GOTLevelData.saveNBTToFile(GOTFactionBounties.getFactionFile(fb.theFaction, false), nbt);
		} catch (Exception e) {
			FMLLog.severe("Error saving GOT faction bounty data for %s", fb.theFaction.codeName());
			e.printStackTrace();
		}
	}

	public static void updateAll() {
		for (GOTFactionBounties fb : factionBountyMap.values()) {
			fb.update();
		}
	}

	public static class PlayerData {
		public GOTFactionBounties bountyList;
		public UUID playerID;
		public String username;
		public List<KillRecord> killRecords = new ArrayList<>();
		public int recentBountyKilled;

		public PlayerData(GOTFactionBounties b, UUID id) {
			bountyList = b;
			playerID = id;
		}

		public String findUsername() {
			if (username == null) {
				GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152652_a(playerID);
				if (profile == null || StringUtils.isBlank(profile.getName())) {
					String name = UsernameCache.getLastKnownUsername(playerID);
					if (name != null) {
						profile = new GameProfile(playerID, name);
					} else {
						profile = new GameProfile(playerID, "");
						MinecraftServer.getServer().func_147130_as().fillProfileProperties(profile, true);
					}
				}
				username = profile.getName();
			}
			return username;
		}

		public int getNumKills() {
			return killRecords.size();
		}

		public void markDirty() {
			bountyList.markDirty();
		}

		public void readFromNBT(NBTTagCompound nbt) {
			killRecords.clear();
			if (nbt.hasKey("KillRecords")) {
				NBTTagList recordTags = nbt.getTagList("KillRecords", 10);
				for (int i = 0; i < recordTags.tagCount(); ++i) {
					NBTTagCompound killData = recordTags.getCompoundTagAt(i);
					KillRecord kr = new KillRecord();
					kr.readFromNBT(killData);
					killRecords.add(kr);
				}
			}
			recentBountyKilled = nbt.getInteger("RecentBountyKilled");
		}

		public boolean recentlyBountyKilled() {
			return recentBountyKilled > 0;
		}

		public void recordBountyKilled() {
			recentBountyKilled = 864000;
			markDirty();
		}

		public void recordNewKill() {
			killRecords.add(new KillRecord());
			markDirty();
		}

		public boolean shouldSave() {
			return !killRecords.isEmpty() || recentBountyKilled > 0;
		}

		public void update() {
			boolean minorChanges = false;
			if (recentBountyKilled > 0) {
				--recentBountyKilled;
				minorChanges = true;
			}
			ArrayList<KillRecord> toRemove = new ArrayList<>();
			for (KillRecord kr : killRecords) {
				kr.timeElapsed--;
				if (kr.timeElapsed <= 0) {
					toRemove.add(kr);
				}
				minorChanges = true;
			}
			if (!toRemove.isEmpty()) {
				killRecords.removeAll(toRemove);
				minorChanges = true;
			}
			if (minorChanges && MinecraftServer.getServer().getTickCounter() % 600 == 0) {
				markDirty();
			}
		}

		public void writeToNBT(NBTTagCompound nbt) {
			NBTTagList recordTags = new NBTTagList();
			for (KillRecord kr : killRecords) {
				NBTTagCompound killData = new NBTTagCompound();
				kr.writeToNBT(killData);
				recordTags.appendTag(killData);
			}
			nbt.setTag("KillRecords", recordTags);
			nbt.setInteger("RecentBountyKilled", recentBountyKilled);
		}

		public static class KillRecord {
			public int timeElapsed = 3456000;

			public void readFromNBT(NBTTagCompound nbt) {
				timeElapsed = nbt.getInteger("Time");
			}

			public void writeToNBT(NBTTagCompound nbt) {
				nbt.setInteger("Time", timeElapsed);
			}
		}

	}

}
