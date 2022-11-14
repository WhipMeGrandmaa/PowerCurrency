package me.whipmegrandma.powercurrency.command.power;

import me.whipmegrandma.powercurrency.database.PowerDatabase;
import me.whipmegrandma.powercurrency.manager.PowerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public final class PowerSetSubCommand extends SimpleSubCommand {

	public PowerSetSubCommand(SimpleCommandGroup parent) {
		super(parent, "set");

		this.setUsage("<username> <power>");
		this.setPermission("powercurrency.command.set");
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
			Player receiver = Bukkit.getPlayerExact(param);

			if (receiver == null) {
				Common.tell(sender, "The power of " + name + " has been set to " + power + ".");

				PowerDatabase.getInstance().setCache(param, power);

				return;
			}

			if (!receiver.equals(getPlayer())) {
				Common.tell(sender, "The power of " + name + " has been set to " + power + ".");
				Common.tell(receiver, "Your power has been set to " + power + ".");
			} else
				Common.tell(sender, "Your power has been set to " + power + ".");

			PowerManager.setPower(receiver, power);
		});

	}

	@Override
	protected List<String> tabComplete() {
		return args.length == 1 ? completeLastWordPlayerNames() : NO_COMPLETE;
	}
}
