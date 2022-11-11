package me.whipmegrandma.powercurrency;

import me.whipmegrandma.powercurrency.database.PowerDatabase;
import me.whipmegrandma.powercurrency.hook.PlaceholderAPIHook;
import me.whipmegrandma.powercurrency.manager.PowerShopCauldronManager;
import me.whipmegrandma.powercurrency.menu.BuyMenu;
import me.whipmegrandma.powercurrency.menu.SellMenu;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.plugin.SimplePlugin;

public final class PowerCurrency extends SimplePlugin {

	@Override
	protected void onPluginStart() {
		PowerDatabase.getInstance().connect("jdbc:sqlite:" + FileUtil.getOrMakeFile("database.sqlite").getAbsolutePath());
	}

	@Override
	protected void onReloadablesStart() {
		PowerShopCauldronManager.onEnable();

		BuyMenu.loadMenus();
		SellMenu.loadMenus();

		if (HookManager.isPlaceholderAPILoaded()) {

			new PlaceholderAPIHook().register();
			Common.log("Enabled support for PlaceholderAPI.");

		} else
			Common.log("Disabling support for PlaceholderAPI. Please download PlaceholderAPI here https://www.spigotmc.org/resources/placeholderapi.6245/");

	}

}
