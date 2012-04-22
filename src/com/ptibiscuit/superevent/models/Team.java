/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptibiscuit.superevent.models;

import com.ptibiscuit.framework.Helper;
import com.ptibiscuit.framework.JavaPluginEnhancer;
import com.ptibiscuit.superevent.Plugin;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Team {
	private HashMap<String, Location> players = new HashMap<String, Location>();
	private Location spawn;
	private String tag;

	public HashMap<String, Location> getPlayers() {
		return players;
	}

	public Location getSpawn() {
		return spawn;
	}

	public String getTag() {
		return tag;
	}

	public void setSpawn(Location spawn) {
		Plugin p = Plugin.getPlugin();
		p.getConfig().set("events" + p.getEventByTeam(this).getTag() + "." + this.tag + ".spawn", Helper.convertLocationToString(spawn));
		p.saveConfig();
		this.spawn = spawn;
	}
	
	public void heal()
	{
		for (String player : this.players.keySet())
		{
			Player p = Bukkit.getPlayer(player);
			p.setHealth(20);
			p.setFoodLevel(20);
		}
		this.sendMessage(Plugin.getPlugin().getSentence("been_healed"));
	}
	
	public void give(ItemStack it)
	{
		for (String player : this.players.keySet())
		{
			Player p = Bukkit.getPlayer(player);
			Helper.surelyGiveObject(p, it);
		}
		this.sendMessage(Plugin.getPlugin().getSentence("object_recieved"));
	}
	
	public void performCommand(String command)
	{
		for (String player : this.players.keySet())
		{
			Player p = Bukkit.getPlayer(player);
			p.performCommand(command);
		}
		this.sendMessage(Plugin.getPlugin().getSentence("performed_command").replace("+command", command));
	}
	
	public void playerQuit(String player)
	{
		if (this.players.get(player) != null)
		{
			Bukkit.getPlayer(player).teleport(this.players.get(player));
		}
		this.players.remove(player);
		
	}

	public void takeBackPlayers()
	{
		for (Entry<String, Location> entry : this.players.entrySet())
		{
			if (entry.getValue() != null)
			{
				Bukkit.getPlayer(entry.getKey()).teleport(entry.getValue());
				this.players.put(entry.getKey(), null);
			}
		}
	}
	
	public void sendMessage(String m)
	{
		for (String name : this.players.keySet())
		{
			Player p = Bukkit.getPlayer(name);
			if (p != null)
				Plugin.getPlugin().sendMessage(p, m);
		}
	}
	
	public void teleportPlayers()
	{
		for (String namePlayer : this.players.keySet())
		{
			Player p = JavaPluginEnhancer.getStaticServer().getPlayer(namePlayer);
			this.players.put(namePlayer, p.getLocation());
			p.teleport(this.spawn);
		}
	}
	
	public void joinTeam(String player)
	{
		// TODO Afficher aux autres joueurs la venue de ce joueur.
		
		this.players.put(player, null);
	}
	
	public Team(String tag, Location spawn, HashMap<String, Location> teams) {
		this.spawn = spawn;
		this.tag = tag;
		this.players = teams;
	}
	
}
