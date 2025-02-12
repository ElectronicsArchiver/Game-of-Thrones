package got.common.command;

import java.util.List;

import got.common.GOTLevelData;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class GOTCommandPledgeCooldown extends CommandBase {
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		return null;
	}

	@Override
	public String getCommandName() {
		return "pledgeCooldown";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "got.command.pledgeCooldown.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return i == 1;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length >= 1) {
			EntityPlayerMP entityplayer;
			int cd = CommandBase.parseIntBounded(sender, args[0], 0, 10000000);
			if (args.length >= 2) {
				entityplayer = CommandBase.getPlayer(sender, args[1]);
			} else {
				entityplayer = CommandBase.getCommandSenderAsPlayer(sender);
				if (entityplayer == null) {
					throw new PlayerNotFoundException();
				}
			}
			GOTLevelData.getData(entityplayer).setPledgeBreakCooldown(cd);
			CommandBase.func_152373_a(sender, this, "got.command.pledgeCooldown.set", entityplayer.getCommandSenderName(), cd, GOTLevelData.getHMSTime_Ticks(cd));
			return;
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}
