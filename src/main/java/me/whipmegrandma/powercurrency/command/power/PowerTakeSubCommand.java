package me.whipmegrandma.powercurrency.command.power;

import me.whipmegrandma.powercurrency.database.PowerDatabase;
import me.whipmegrandma.powercurrency.manager.PowerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public final class PowerTakeSubCommand extends SimpleSubCommand {

	public PowerTakeSubCommand(SimpleCommandGroup parent) {
		super(parent, "take");

		this.setUsage("<username> <power>");
		this.setPermission("power.command.take");
		this.setMinArguments(2);
	}


	@Override
	protected void onCommand() {

		String param = args[0];
		int power = findNumber(1, "The amount must be a number!");
		checkBoolean(power >= 0, "The amount must be a positive whole number!");

		PowerDatabase.getInstance().pollCache(param, data -> {

			if (data == null) {
				Common.tell(sender, param + " has never joined the server before.");

				return;
			}

			String name = data.getKey();
			int balance = data.getValue();

			PowerDatabase.getInstance().setCache(param, data.getValue() - power);

			Player receiver = Bukkit.getPlayerExact(param);

			if (receiver == null) {
				Common.tell(sender, power + " power has been taken from " + name + ".");

				return;
			}

			if (!receiver.equals(getPlayer())) {
				Common.tell(sender, power + " power has been taken from " + name + ".");
				Common.tell(receiver, power + " power has been taken from you.");
			} else
				Common.tell(sender, power + " power has been taken from you.");

			PowerManager.setPower(receiver, data.getValue() - power);
		});

	}

	@Override
	protected List<String> tabComplete() {
		return args.length == 1 ? completeLastWordPlayerNames() : NO_COMPLETE;
	}
}
