package me.whipmegrandma.powercurrency.command.power;

import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;

@AutoRegister
public final class power extends SimpleCommandGroup {

	public power() {
		super("power|p");
	}

	@Override
	protected void registerSubcommands() {
		this.registerSubcommand(new ReloadCommand());
	}
}
