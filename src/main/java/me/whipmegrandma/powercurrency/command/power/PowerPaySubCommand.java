package me.whipmegrandma.powercurrency.command.power;

import me.whipmegrandma.powercurrency.database.PowerDatabase;
import me.whipmegrandma.powercurrency.manager.PowerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public final class PowerPaySubCommand extends SimpleSubCommand {

	public PowerPaySubCommand(SimpleCommandGroup parent) {
		super(parent, "pay");

		this.setUsage("<username> <power>");
		this.setPermission("powercurrency.command.pay");
		this.setMinArguments(2);
	}


	@Override
	protected void onCommand() {

		String param = args[0];
		int power = findNumber(1, "The amount must be a number!");
		checkBoolean(power >= 0, "The amount must be a positive whole number!");

		Integer balanceSender = PowerManager.getPower(getPlayer());

		if (power > balanceSender) {
			Common.tell(getPlayer(), "You do not have sufficient power.");

			return;
		}

		PowerDatabase.getInstance().pollCache(param, data -> {

			if (data == null) {
				Common.tell(sender, param + " has never joined the server before.");

				return;
			}

			String name = data.getKey();
			Integer balanceReceiver = data.getValue();

			Player receiver = Bukkit.getPlayerExact(param);

			if (receiver == null) {
				Common.tell(sender, power + " power has been sent to " + name + ".");

				PowerDatabase.getInstance().setCache(param, balanceReceiver + power);

				PowerManager.setPower(getPlayer(), balanceSender - power);

				return;
			}

			if (!receiver.equals(getPlayer())) {
				Common.tell(sender, power + " power has been sent to " + name + ".");
				Common.tell(receiver, power + " power has been received from " + getPlayer().getName() + ".");
			} else
				Common.tell(sender, "You cannot pay yourself.");

			PowerManager.setPower(receiver, balanceReceiver + power);
			PowerManager.setPower(getPlayer(), balanceSender - power);
		});

	}

	@Override
	protected List<String> tabComplete() {
		return args.length == 1 ? completeLastWordPlayerNames() : NO_COMPLETE;
	}
}
