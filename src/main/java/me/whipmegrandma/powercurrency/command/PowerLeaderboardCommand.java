package me.whipmegrandma.powercurrency.command;

import me.whipmegrandma.powercurrency.menu.PowerLeaderboardMenu;
import org.bukkit.command.CommandSender;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;

import java.util.List;

@AutoRegister
public final class PowerLeaderboardCommand extends SimpleCommand {

	public PowerLeaderboardCommand() {
		super("powerleaderboard|ptop|pleaderboard");

		this.setPermission("power.command.leaderboard");
	}

	@Override
	protected void onCommand() {
		CommandSender sender = getSender();

		PowerLeaderboardMenu menu = PowerLeaderboardMenu.findMenu("Main_Menu");
		checkNotNull(menu, "'Main_Menu' is not set in powerleaderboardmenu.yml.");

		menu.displayTo(getPlayer());
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
