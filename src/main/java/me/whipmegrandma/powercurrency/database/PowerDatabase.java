package me.whipmegrandma.powercurrency.database;

import lombok.Getter;
import me.whipmegrandma.powercurrency.manager.PowerLeaderboardManager;
import me.whipmegrandma.powercurrency.manager.PowerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.database.SimpleDatabase;
import org.mineacademy.fo.model.Tuple;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public final class PowerDatabase extends SimpleDatabase {

	@Getter
	private static final PowerDatabase instance = new PowerDatabase();

	private PowerDatabase() {
		this.addVariable("table", "PowerCurrency");
	}

	@Override
	protected void onConnected() {

		this.createTable(TableCreator.of("{table}")
				.add("UUID", "VARCHAR(64)")
				.add("Name", "TEXT")
				.add("Power", "INTEGER")
				.setPrimaryColumn("UUID"));
	}

	public void loadCache(Player player, Consumer<Integer> callThisOnLoad) {
		this.checkLoadedAndSync();

		Common.runAsync(() -> {

			try {

				ResultSet resultSet = this.query("SELECT Power FROM {table} WHERE UUID = '" + player.getUniqueId() + "'");

				if (!resultSet.next()) {
					Common.runLater(() -> callThisOnLoad.accept(0));
					this.saveNewPlayerCache(player);

					return;
				}

				Integer power = resultSet.getInt("Power");

				Common.runLater(() -> callThisOnLoad.accept(power));

			} catch (Throwable t) {
				Common.error(t, "Unable to load power for " + player.getName());
			}
		});
	}

	public void saveCache(Player player) {
		this.checkLoadedAndSync();

		Common.runLaterAsync(() -> {

			try {
				SerializedMap map = SerializedMap.ofArray(
						"UUID", player.getUniqueId(),
						"Name", player.getName(),
						"Power", PowerManager.getPower(player));

				final String columns = Common.join(map.keySet());
				final String values = Common.join(map.values(), ", ", value -> value == null || value.equals("NULL") ? "NULL" : "'" + value + "'");

				this.update("INSERT OR REPLACE INTO {table} (" + columns + ") VALUES (" + values + ");");

				Common.runLater(this::updateLeaderboard);

			} catch (Throwable t) {
				Common.error(t, "Unable to save power for " + player.getName());
			}
		});
	}

	public void saveNewPlayerCache(Player player) {

		try {
			SerializedMap map = SerializedMap.ofArray(
					"UUID", player.getUniqueId(),
					"Name", player.getName(),
					"Power", 0);

			String columns = Common.join(map.keySet());
			String values = Common.join(map.values(), ", ", value -> value == null || value.equals("NULL") ? "NULL" : "'" + value + "'");

			this.update("INSERT OR REPLACE INTO {table} (" + columns + ") VALUES (" + values + ");");

			Common.runLater(this::updateLeaderboard);

		} catch (Throwable t) {
			Common.error(t, "Unable to save data for newly joined player " + player.getName());
		}
	}

	public void pollCache(String playerName, Consumer<Tuple<String, Integer>> callThisOnLoad) {
		this.checkLoadedAndSync();

		Common.runAsync(() -> {

			UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();

			try {

				ResultSet resultSet = this.query("SELECT * FROM {table} WHERE UUID = '" + uuid + "' COLLATE NOCASE");

				if (!resultSet.next()) {
					Common.runLater(() -> callThisOnLoad.accept(null));

					return;
				}

				String name = resultSet.getString("Name");
				Integer power = resultSet.getInt("Power");

				Tuple<String, Integer> data = new Tuple<>(name, power);

				Common.runLater(() -> callThisOnLoad.accept(data));

			} catch (Throwable t) {
				Common.error(t, "Unable to load power for " + playerName);
			}
		});
	}

	public BukkitTask pollAllCache(Consumer<HashMap<String, Integer>> callThisOnLoad) {
		this.checkLoadedAndSync();

		BukkitTask task = Common.runAsync(() -> {

			try {
				HashMap<String, Integer> map = new HashMap<>();

				this.selectAll("{table}", data -> {
					String name = data.getString("Name");
					Integer power = data.getInt("Power");

					map.put(name, power);
				});

				Common.runLater(() -> callThisOnLoad.accept(map));

			} catch (Throwable t) {
				Common.error(t, "Unable to load all data.");
			}
		});
		return task;
	}

	public void updateLeaderboard() {
		this.checkLoadedAndSync();
		PowerLeaderboardManager.clear();

		Common.runAsync(() -> {

			try (ResultSet resultSet = this.query("SELECT Name, Power FROM {table} ORDER BY Power DESC LIMIT 8")) {
				while (resultSet.next())
					try {
						String name = resultSet.getString("Name");
						String power = String.valueOf(resultSet.getInt("Power"));
						Tuple<String, String> data = new Tuple<>(name, power);

						Common.runLater(() -> PowerLeaderboardManager.add(data));

					} catch (final Throwable t) {
						Common.log("Error reading a row from table while polling leaderboard.");

						t.printStackTrace();
						break;
					}

			} catch (final Throwable t) {
				Common.error(t, "Error updating leaderboard.");
			}
		});
	}

	public void setCache(String name, int power) {
		this.checkLoadedAndSync();

		Common.runLaterAsync(() -> {

			UUID uuid = Bukkit.getOfflinePlayer(name).getUniqueId();

			try {
				this.update("UPDATE {table} set Power = '" + power + "' WHERE UUID = '" + uuid + "' COLLATE NOCASE");

				this.updateLeaderboard();
			} catch (Throwable t) {
				Common.error(t, "Unable to set power for " + name);
			}
		});
	}

	private void checkLoadedAndSync() {
		Valid.checkSync("Database calls must happen sync, not async!");

		if (!this.isConnected())
			this.connectUsingLastCredentials();

		Valid.checkBoolean(this.isConnected(), "Not connected to database.");

	}

}
