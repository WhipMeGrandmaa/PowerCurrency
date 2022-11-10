package me.whipmegrandma.powercurrency.manager;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.ArrayList;
import java.util.List;

public final class PowerShopCauldronManager extends YamlConfig {

	@Getter
	private static PowerShopCauldronManager instance;

	public static List<Location> powerShopCauldronManager = new ArrayList<>();

	private PowerShopCauldronManager() {

		this.loadConfiguration(NO_DEFAULT, "powershoplocations.yml");
	}

	@Override
	protected void onLoad() {
		powerShopCauldronManager = toLocation();
	}

	@Override
	protected void onSave() {
		this.set("Locations", toConfig(powerShopCauldronManager));
	}

	public boolean has(Location loc) {
		return powerShopCauldronManager.contains(loc);
	}

	public void add(Location loc) {
		powerShopCauldronManager.add(loc);

		this.save();
	}

	public void remove(Location loc) {
		powerShopCauldronManager.remove(loc);

		this.save();
	}

	private List<String> toConfig(List<Location> list) {

		List<String> compiledLocations = new ArrayList<>();

		for (Location loc : list) {
			String string = loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ();

			compiledLocations.add(string);
		}

		return compiledLocations;
	}

	private List<Location> toLocation() {

		List<Location> loadedLocations = new ArrayList<>();

		List<String> string = this.getStringList("Locations");

		for (String loc : string) {

			String[] filter = loc
					.replace(",", "")
					.split(" ");

			World world = Bukkit.getWorld(filter[0]);
			double x = Double.parseDouble(filter[1]);
			double y = Double.parseDouble(filter[2]);
			double z = Double.parseDouble(filter[3]);

			Location location = new Location(world, x, y, z);

			loadedLocations.add(location);
		}

		return loadedLocations;
	}

	public static void onEnable() {
		instance = new PowerShopCauldronManager();
	}
}
