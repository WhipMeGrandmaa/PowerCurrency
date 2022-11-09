package me.whipmegrandma.listener;

import me.whipmegrandma.manager.PowerManager;
import me.whipmegrandma.powercurrency.database.PowerDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mineacademy.fo.annotation.AutoRegister;

@AutoRegister
public final class PlayerListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		PowerDatabase.getInstance().loadCache(player, power -> PowerManager.join(player, power));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		PowerManager.quit(player);

	}
}
