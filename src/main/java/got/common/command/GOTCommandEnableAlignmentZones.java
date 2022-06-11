package got.common.command;

import java.util.List;

import got.common.GOTLevelData;
import net.minecraft.command.*;

public class GOTCommandEnableAlignmentZones extends CommandBase {
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return CommandBase.getListOfStringsMatchingLastWord(args, new String[] { "enable", "disable" });
		}
		return null;
	}

	@Override
	public String getCommandName() {
		return "alignmentZones";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "got.command.alignmentZones.usage";
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
			String flag = args[0];
			if ("enable".equals(flag)) {
				GOTLevelData.setEnableAlignmentZones(true);
				CommandBase.func_152373_a(sender, (ICommand) this, "got.command.alignmentZones.enable", new Object[0]);
				return;
			}
			if ("disable".equals(flag)) {
				GOTLevelData.setEnableAlignmentZones(false);
				CommandBase.func_152373_a(sender, (ICommand) this, "got.command.alignmentZones.disable", new Object[0]);
				return;
			}
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}
