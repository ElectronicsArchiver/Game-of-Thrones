package got.common.command;

import java.util.List;

import got.common.GOTTime;
import net.minecraft.command.*;

public class GOTCommandTime extends CommandBase {
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "set", "add");
		}
		if (args.length == 2 && "set".equals(args[0])) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "day", "night");
		}
		return null;
	}

	@Override
	public String getCommandName() {
		return "got_time";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "got.command.time.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length >= 2) {
			if ("set".equals(args[0])) {
				long time;
				time = "day".equals(args[1]) ? Math.round(GOTTime.DAY_LENGTH * 0.03) : "night".equals(args[1]) ? Math.round(GOTTime.DAY_LENGTH * 0.6) : (long) CommandBase.parseIntWithMin(sender, args[1], 0);
				GOTTime.setWorldTime(time);
				CommandBase.func_152373_a(sender, this, "got.command.time.set", time);
				return;
			}
			if ("add".equals(args[0])) {
				int time = CommandBase.parseIntWithMin(sender, args[1], 0);
				GOTTime.addWorldTime(time);
				CommandBase.func_152373_a(sender, this, "got.command.time.add", time);
				return;
			}
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}
