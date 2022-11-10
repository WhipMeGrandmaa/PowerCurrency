package me.whipmegrandma.powercurrency.command.power;

import me.whipmegrandma.powercurrency.menu.BuyMenu;
import org.bukkit.command.CommandSender;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public final class PowerBuySubCommand extends SimpleSubCommand {

	public PowerBuySubCommand(SimpleCommandGroup parent) {
		super(parent, "buy");

		this.setPermission("power.command.buy");
	}


	@Override
	protected void onCommand() {
		CommandSender sender = getSender();

		BuyMenu menu = BuyMenu.findMenu("Main_Menu");
		checkNotNull(menu, "'Main_Menu' is not set in buymenu.yml.");

		menu.displayTo(getPlayer());
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
