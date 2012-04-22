package com.ptibiscuit.superevent.api;

import com.ptibiscuit.superevent.models.Event;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventTypeExample extends EventType {
	
	@Override
	public void onCommand(Player sender, String label, String[] args) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onEnable() {
		
	}

	@Override
	public boolean canTakeEvent(CommandSender cs, Event e) {
		return true;
	}
	
}
