package got.common.command;

import java.util.List;

import got.common.GOTDate;
import net.minecraft.command.*;
import net.minecraft.util.ChatComponentTranslation;

public class GOTCommandDate extends CommandBase {
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "get", "set", "add");
		}
		return null;
	}

	@Override
	public String getCommandName() {
		return "date";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "got.command.date.usage";
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
		if (args.length >= 1 && "get".equals(args[0])) {
			int date = GOTDate.AegonCalendar.currentDay;
			String dateName = GOTDate.AegonCalendar.getDate().getDateName(false);
			ChatComponentTranslation message = new ChatComponentTranslation("got.command.date.get", date, dateName);
			sender.addChatMessage(message);
			return;
		}
		if (args.length >= 2) {
			int newDate = GOTDate.AegonCalendar.currentDay;
			if ("set".equals(args[0])) {
				newDate = CommandBase.parseInt(sender, args[1]);
			} else if ("add".equals(args[0])) {
				int date = CommandBase.parseInt(sender, args[1]);
				newDate += date;
			}
			if (Math.abs(newDate) > 1000000) {
				throw new WrongUsageException("got.command.date.outOfBounds");
			}
			GOTDate.setDate(newDate);
			String dateName = GOTDate.AegonCalendar.getDate().getDateName(false);
			CommandBase.func_152373_a(sender, this, "got.command.date.set", newDate, dateName);
			return;
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}
