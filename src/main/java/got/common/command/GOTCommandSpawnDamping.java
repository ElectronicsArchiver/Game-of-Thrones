package got.common.command;

import java.util.*;

import got.common.GOTSpawnDamping;
import net.minecraft.command.*;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class GOTCommandSpawnDamping extends CommandBase {
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "set", "calc", "reset");
		}
		if (args.length == 2 && ("set".equals(args[0]) || "calc".equals(args[0]))) {
			ArrayList<String> types = new ArrayList<>();
			for (EnumCreatureType type : EnumCreatureType.values()) {
				types.add(type.name());
			}
			types.add(GOTSpawnDamping.TYPE_NPC);
			return CommandBase.getListOfStringsMatchingLastWord(args, types.toArray(new String[0]));
		}
		return null;
	}

	@Override
	public String getCommandName() {
		return "spawnDamping";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "got.command.spawnDamping.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return false;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length >= 1) {
			String option = args[0];
			if ("reset".equals(option)) {
				GOTSpawnDamping.resetAll();
				CommandBase.func_152373_a(sender, this, "got.command.spawnDamping.reset");
				return;
			}
			if (args.length >= 2) {
				String type = args[1];
				if (!type.equals(GOTSpawnDamping.TYPE_NPC) && EnumCreatureType.valueOf(type) == null) {
					throw new WrongUsageException("got.command.spawnDamping.noType", type);
				}
				if ("set".equals(option) && args.length >= 3) {
					float damping = (float) CommandBase.parseDoubleBounded(sender, args[2], 0.0, 1.0);
					GOTSpawnDamping.setSpawnDamping(type, damping);
					CommandBase.func_152373_a(sender, this, "got.command.spawnDamping.set", type, Float.valueOf(damping));
					return;
				}
				if ("calc".equals(option)) {
					World world = sender.getEntityWorld();
					int dim = world.provider.dimensionId;
					String dimName = world.provider.getDimensionName();
					float damping = GOTSpawnDamping.getSpawnDamping(type);
					int players = world.playerEntities.size();
					int expectedChunks = 196;
					int baseCap = GOTSpawnDamping.getBaseSpawnCapForInfo(type, world);
					int cap = GOTSpawnDamping.getSpawnCap(type, baseCap, players);
					int capXPlayers = cap * players;
					ChatComponentTranslation msg = new ChatComponentTranslation("got.command.spawnDamping.calc", dim, dimName, type, Float.valueOf(damping), players, expectedChunks, cap, baseCap, capXPlayers);
					msg.getChatStyle().setColor(EnumChatFormatting.GREEN);
					sender.addChatMessage(msg);
					return;
				}
			}
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}
