package me.whipmegrandma.powercurrency.command.power;

import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;

@AutoRegister
public final class PowerMainCommand extends SimpleCommandGroup {

	public PowerMainCommand() {
		super("power|p");
	}

	@Override
	protected void registerSubcommands() {
		this.registerSubcommand(new ReloadCommand());
		this.registerSubcommand(new PowerBalSubCommand(this));
	}
}
