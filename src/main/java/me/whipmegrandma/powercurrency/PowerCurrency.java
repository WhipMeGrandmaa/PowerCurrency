package me.whipmegrandma.powercurrency;

import me.whipmegrandma.powercurrency.database.PowerDatabase;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.plugin.SimplePlugin;

public final class PowerCurrency extends SimplePlugin {

	@Override
	protected void onPluginStart() {
		PowerDatabase.getInstance().connect("jdbc:sqlite:" + FileUtil.getOrMakeFile("database.sqlite").getAbsolutePath());
	}

	@Override
	protected void onReloadablesStart() {

	}

}
