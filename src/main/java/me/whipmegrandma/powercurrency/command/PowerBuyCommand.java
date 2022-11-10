package me.whipmegrandma.powercurrency.command;

import me.whipmegrandma.powercurrency.menu.BuyMenu;
import org.bukkit.command.CommandSender;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;

import java.util.List;

@AutoRegister
public final class PowerBuyCommand extends SimpleCommand {
	
	public PowerBuyCommand() {
		super("powerbuy|pbuy");

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
