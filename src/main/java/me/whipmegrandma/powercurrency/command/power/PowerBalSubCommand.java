package me.whipmegrandma.powercurrency.command.power;

import me.whipmegrandma.powercurrency.database.PowerDatabase;
import me.whipmegrandma.powercurrency.manager.PowerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.*;

public final class PowerBalSubCommand extends SimpleSubCommand {

	public PowerBalSubCommand(SimpleCommandGroup parent) {
		super(parent, "bal");

		this.setUsage("<username>");
		this.setPermission("powercurrency.command.balance");
	}


	@Override
	protected void onCommand() {
		CommandSender sender = getSender();

		if (args.length == 0) {
			checkConsole();

			this.leaderBoard();

		} else if (args.length == 1) {

			String receiver = args[0];

			Player player = Bukkit.getPlayerExact(receiver);

			if (player != null && player.equals(getPlayer())) {
				this.leaderBoard();

				return;
			}

			if (player != null) {
				Common.tell(sender, "Power of " + player.getName() + ": " + PowerManager.getPower(player));

				return;
			}

			PowerDatabase.getInstance().pollCache(receiver, data -> {

				if (data == null) {
					Common.tell(sender, receiver + " has never joined the server before.");

					return;
				}

				String name = data.getKey();
				int power = data.getValue();

				Common.tell(sender, "Power of " + name + ": " + power);
			});

		} else {

			if (sender instanceof Player)
				Common.dispatchCommandAsPlayer((Player) sender, "/p bal help");
			else
				Common.dispatchCommand(sender, "/p bal help");
		}
	}

	private void leaderBoard() {
		PowerDatabase.getInstance().pollAllCache(allCache -> {

			int balance = PowerManager.getPower((Player) sender);

			Common.tell(sender, "Power: " + balance,
					"Leaderboard place: " + this.leaderBoardPosition((Player) sender, allCache));
		});
	}

	private int leaderBoardPosition(Player player, HashMap<String, Integer> map) {

		List<Map.Entry<String, Integer>> sorted = new ArrayList<>(map.entrySet());

		Collections.sort(sorted, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

		int position = 1;

		for (Map.Entry<String, Integer> entrySet : sorted) {
			String playerName = player.getName();
			String playerNameMap = entrySet.getKey();

			if (playerName.equalsIgnoreCase(playerNameMap))
				return position;

			position++;
		}

		return position;
	}

	@Override
	protected List<String> tabComplete() {
		return args.length == 1 ? completeLastWordPlayerNames() : NO_COMPLETE;
	}
}
