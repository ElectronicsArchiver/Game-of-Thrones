package got.common.command;

import java.util.*;

import got.common.GOTLevelData;
import got.common.faction.GOTFaction;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class GOTCommandAlignment extends CommandBase {
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		switch (args.length) {
		case 1:
			return CommandBase.getListOfStringsMatchingLastWord(args, "set", "add");
		case 2:
			List<String> list = GOTFaction.getPlayableAlignmentFactionNames();
			list.add("all");
			return CommandBase.getListOfStringsMatchingLastWord(args, list.toArray(new String[0]));
		case 4:
			return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		default:
			break;
		}
		return null;
	}

	@Override
	public String getCommandName() {
		return "alignment";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "got.command.alignment.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return i == 3;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length >= 2) {
			List<GOTFaction> factions = new ArrayList();
			if ("all".equalsIgnoreCase(args[1])) {
				factions = GOTFaction.getPlayableAlignmentFactions();
			} else {
				GOTFaction faction = GOTFaction.forName(args[1]);
				if (faction == null) {
					throw new WrongUsageException("got.command.alignment.noFaction", args[1]);
				}
				factions.add(faction);
			}
			if ("set".equals(args[0])) {
				EntityPlayerMP entityplayer;
				float alignment = (float) CommandBase.parseDoubleBounded(sender, args[2], -2147483647.0, 2147483647.0);
				if (args.length >= 4) {
					entityplayer = CommandBase.getPlayer(sender, args[3]);
				} else {
					entityplayer = CommandBase.getCommandSenderAsPlayer(sender);
					if (entityplayer == null) {
						throw new PlayerNotFoundException();
					}
				}
				for (GOTFaction f : factions) {
					GOTLevelData.getData(entityplayer).setAlignmentFromCommand(f, alignment);
					CommandBase.func_152373_a(sender, this, "got.command.alignment.set", entityplayer.getCommandSenderName(), f.factionName(), Float.valueOf(alignment));
				}
				return;
			}
			if ("add".equals(args[0])) {
				EntityPlayerMP entityplayer;
				float newAlignment;
				float alignment = (float) CommandBase.parseDouble(sender, args[2]);
				if (args.length >= 4) {
					entityplayer = CommandBase.getPlayer(sender, args[3]);
				} else {
					entityplayer = CommandBase.getCommandSenderAsPlayer(sender);
					if (entityplayer == null) {
						throw new PlayerNotFoundException();
					}
				}
				HashMap<GOTFaction, Float> newAlignments = new HashMap<>();
				for (GOTFaction f : factions) {
					newAlignment = GOTLevelData.getData(entityplayer).getAlignment(f) + alignment;
					if (newAlignment < -2147483647.0f) {
						throw new WrongUsageException("got.command.alignment.tooLow", Float.valueOf(-2147483647.0f));
					}
					if (newAlignment > 2147483647.0f) {
						throw new WrongUsageException("got.command.alignment.tooHigh", Float.valueOf(2147483647.0f));
					}
					newAlignments.put(f, newAlignment);
				}
				for (GOTFaction f : factions) {
					newAlignment = newAlignments.get(f);
					GOTLevelData.getData(entityplayer).addAlignmentFromCommand(f, alignment);
					CommandBase.func_152373_a(sender, this, "got.command.alignment.add", Float.valueOf(alignment), entityplayer.getCommandSenderName(), f.factionName());
				}
				return;
			}
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}
