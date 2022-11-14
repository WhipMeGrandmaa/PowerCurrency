package me.whipmegrandma.powercurrency.command.power;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

public final class PowerEditSubCommand extends SimpleSubCommand {

	public PowerEditSubCommand(SimpleCommandGroup parent) {
		super(parent, "edit");

		this.setPermission("powercurrency.command.edit");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		Player player = getPlayer();

		ItemStack cauldron = ItemCreator.of(CompMaterial.CAULDRON, "&9Power Shop Menu", "", "PLACE ME").tag("Power", "Shop").glow(true).make();

		player.getInventory().addItem(cauldron);
		Common.tell(player, "Given a power cauldron. Place this down wherever you want a shop.");
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
