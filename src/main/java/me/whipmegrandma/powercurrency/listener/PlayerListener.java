package me.whipmegrandma.powercurrency.listener;

import me.whipmegrandma.powercurrency.database.PowerDatabase;
import me.whipmegrandma.powercurrency.manager.PowerManager;
import me.whipmegrandma.powercurrency.manager.PowerShopCauldronManager;
import me.whipmegrandma.powercurrency.menu.SellMenu;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.CompParticle;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.remain.Remain;

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

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItemInHand();
		Location location = event.getBlock().getLocation();

		String data = CompMetadata.getMetadata(item, "Power");

		if (!"Shop".equals(data))
			return;

		PowerShopCauldronManager.getInstance().add(location);

		CompParticle.FLASH.spawn(location);
		CompSound.ENTITY_ZOMBIE_VILLAGER_CURE.play(location);
		Remain.sendTitle(player, "Successfully &aplaced &fa", "power shop cauldron!");
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Location location = block.getLocation();

		if (!PowerShopCauldronManager.getInstance().has(location))
			return;

		PowerShopCauldronManager.getInstance().remove(location);

		CompParticle.FLASH.spawn(location);
		CompSound.ENTITY_ZOMBIE_VILLAGER_CURE.play(location);
		Remain.sendTitle(player, "Successfully &cremoved &fa", "power shop cauldron!");
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {

		if (!Remain.isInteractEventPrimaryHand(event))
			return;

		Player player = event.getPlayer();
		Block block = event.getClickedBlock();


		if (block == null)
			return;


		Location location = block.getLocation();

		if (!PowerShopCauldronManager.getInstance().has(location) || player.getGameMode() == GameMode.CREATIVE)
			return;


		SellMenu menu = SellMenu.findMenu("Main_Menu");
		Valid.checkNotNull(menu, "'Main_Menu' is not set in sellmenu.yml.");
		
		if (!PlayerUtil.hasPerm(player, "powercurrency.shop.sell"))
			return;

		menu.displayTo(player);
	}
}
