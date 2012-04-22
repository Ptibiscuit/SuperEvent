/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptibiscuit.superevent.listeners;

import com.ptibiscuit.superevent.Plugin;
import com.ptibiscuit.superevent.models.Event;
import com.ptibiscuit.superevent.models.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerManager implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Team t = Plugin.getPlugin().getTeamByPlayer(e.getPlayer().getName());
		if (t != null)
		{
			// Il faisait partie d'un jeu.
			Event event = Plugin.getPlugin().getEventByTeam(t);
			event.playerQuit(e.getPlayer());
		}
	}
	
}
