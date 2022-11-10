package me.whipmegrandma.powercurrency.manager;

import me.whipmegrandma.powercurrency.database.PowerDatabase;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompSound;

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

	public static int getPower(Player player) {
		return powerManager.get(player.getUniqueId());
	}

	public static void setPower(Player player, int power) {
		powerManager.put(player.getUniqueId(), power);

		PowerDatabase.getInstance().saveCache(player);
	}

	public static boolean buy(Player player, int amount) {
		UUID uuid = player.getUniqueId();

		int power = powerManager.get(uuid);
		int powerUpdated = power - amount;

		if (powerUpdated < 0) {

			CompSound.ENTITY_VILLAGER_NO.play(player);

			return false;
		}

		CompSound.ENTITY_VILLAGER_CELEBRATE.play(player);

		powerManager.put(uuid, powerUpdated);
		PowerDatabase.getInstance().saveCache(player);

		return true;
	}

	public static boolean sell(Player player, CompMaterial material, int price) {
		UUID uuid = player.getUniqueId();
		ItemStack[] items = player.getInventory().getContents();

		int times = 0;

		for (ItemStack item : items)
			if (item != null && material.toMaterial() == item.getType()) {
				times += item.getAmount();
			}

		if (times == 0) {
			CompSound.ENTITY_VILLAGER_NO.play(player);

			return false;
		}

		PlayerUtil.take(player, material, times);

		CompSound.ENTITY_VILLAGER_CELEBRATE.play(player);

		int power = powerManager.get(uuid);
		int powerUpdated = power + (price * times);

		powerManager.put(uuid, powerUpdated);

		PowerDatabase.getInstance().saveCache(player);

		return true;
	}
}
