package com.ptibiscuit.superevent.api;

import com.ptibiscuit.superevent.Plugin;
import com.ptibiscuit.superevent.models.Event;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public abstract class EventType {
	private Event event;
	private ConfigurationSection config;
	
	public void onCommand(Player sender, String label, String[] args) {
		// Do nothing ...
	}
	public abstract void onEnable();
	public abstract boolean canTakeEvent(CommandSender cs, Event e);

	public void setConfig(ConfigurationSection config) {
		this.config = config;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public void saveConfig()
	{
		Plugin.getPlugin().saveConfig();
	}
	
	public ConfigurationSection getConfig() {
		return config;
	}

	public Event getEvent() {
		return event;
	}
	
	
}
