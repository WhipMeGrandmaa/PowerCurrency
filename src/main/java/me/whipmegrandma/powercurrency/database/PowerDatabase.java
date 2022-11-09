package me.whipmegrandma.powercurrency.database;

import lombok.Getter;
import me.whipmegrandma.manager.PowerManager;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.database.SimpleDatabase;
import org.mineacademy.fo.model.Tuple;

import java.sql.ResultSet;
import java.util.HashMap;
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

			} catch (Throwable t) {
				Common.error(t, "Unable to save power for " + player.getName());
			}
		});
	}

	public void pollCache(String playerName, Consumer<Tuple<String, Integer>> callThisOnLoad) {
		this.checkLoadedAndSync();

		Common.runAsync(() -> {

			try {

				ResultSet resultSet = this.query("SELECT * FROM {table} WHERE Name = '" + playerName + "' COLLATE NOCASE");

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

	public void pollAllCache(Consumer<HashMap<String, Integer>> callThisOnLoad) {
		this.checkLoadedAndSync();

		Common.runAsync(() -> {

			try {
				HashMap<String, Integer> map = new HashMap<>();

				this.selectAll("{table}", data -> {
					String name = data.getString("Name");
					Integer power = data.getInt("Power");

					map.put(name, power);
				});

				Common.runLater(() -> callThisOnLoad.accept(map));
				map.clear();

			} catch (Throwable t) {
				Common.error(t, "Unable to load all data.");
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