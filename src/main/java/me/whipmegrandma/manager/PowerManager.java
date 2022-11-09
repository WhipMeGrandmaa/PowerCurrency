package me.whipmegrandma.manager;

import me.whipmegrandma.powercurrency.database.PowerDatabase;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PowerManager {

	private static HashMap<UUID, Integer> powerManager = new HashMap<>();

	public static void join(Player player, Integer power) {
		powerManager.put(player.getUniqueId(), power);
	}

	public static void quit(Player player) {
		powerManager.remove(player.getUniqueId());
	}

	public static void setPower(Player player, int power) {
		powerManager.put(player.getUniqueId(), power);

		PowerDatabase.getInstance().saveCache(player);
	}

	public static int getPower(Player player) {
		return powerManager.get(player.getUniqueId());
	}
}
