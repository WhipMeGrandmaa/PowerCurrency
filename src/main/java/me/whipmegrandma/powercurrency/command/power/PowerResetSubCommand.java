package me.whipmegrandma.powercurrency.command.power;

import me.whipmegrandma.powercurrency.database.PowerDatabase;
import me.whipmegrandma.powercurrency.manager.PowerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public final class PowerResetSubCommand extends SimpleSubCommand {

	public PowerResetSubCommand(SimpleCommandGroup parent) {
		super(parent, "reset");

		this.setUsage("<username>");
		this.setPermission("power.command.reset");
		this.setMinArguments(1);
	}


	@Override
	protected void onCommand() {

		String param = args[0];

		PowerDatabase.getInstance().pollCache(param, data -> {

			if (data == null) {
				Common.tell(sender, param + " has never joined the server before.");

				return;
			}

			String name = data.getKey();
			Player receiver = Bukkit.getPlayerExact(param);

			if (receiver == null) {
				Common.tell(sender, "The power of " + name + " has been reset.");
				PowerDatabase.getInstance().setCache(param, 0);

				return;
			}

			if (!receiver.equals(getPlayer())) {
				Common.tell(sender, "The power of " + name + " has been reset.");
				Common.tell(receiver, "Your power has been reset.");
			} else
				Common.tell(sender, "Your power has been reset.");

			PowerManager.setPower(receiver, 0);

		});

	}

	@Override
	protected List<String> tabComplete() {
		return args.length == 1 ? completeLastWordPlayerNames() : NO_COMPLETE;
	}
}
