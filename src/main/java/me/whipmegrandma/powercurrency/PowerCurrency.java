package me.whipmegrandma.powercurrency;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.mineacademy.fo.plugin.SimplePlugin;

public final class PowerCurrency extends SimplePlugin {

	@Override
	protected void onPluginStart() {
	}

	@Override
	protected void onReloadablesStart() {

	}

	@EventHandler
	public void onRightClick(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.COW)
			event.getRightClicked().getWorld().createExplosion(event.getRightClicked().getLocation(), 5);
	}

	public static PowerCurrency getInstance() {
		return (PowerCurrency) SimplePlugin.getInstance();
	}
}
