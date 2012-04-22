package com.ptibiscuit.superevent.models;

import com.ptibiscuit.framework.Helper;
import com.ptibiscuit.superevent.Plugin;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Event {
	private ArrayList<Team> teams = new ArrayList<Team>();
	private String tag;
	private boolean state = EventState.INACTIVE;
	
	public Event(String tag, ArrayList<Team> teams) {
		this.tag = tag;
		this.teams = teams;
	}

	public String getTag() {
		return tag;
	}

	public ArrayList<Team> getTeams() {
		return teams;
	}
	
	public void playerJoin(Player p)
	{
		// On va séléctionner la team avec le moins de joueur.
		this.playerJoin(this.getTeamWithMinimPlayer(), p);
	}
	
	public Team getTeamWithMinimPlayer()
	{
		Team lowerTeam = null;
		for (Team t : this.teams)
		{
			if (lowerTeam != null)
			{
				if (lowerTeam.getPlayers().size() > t.getPlayers().size())
					lowerTeam = t;
			}
			else
			{
				lowerTeam = t;
			}
		}
		return lowerTeam;
	}
	
	public void playerJoin(Team t, Player p)
	{
		this.sendMessage(Plugin.getPlugin().getSentence("player_joined").replace("+team", t.getTag()).replace("+player", p.getDisplayName()));
		t.joinTeam(p.getName());
	}
	
	public void playerQuit(Player p)
	{
		Team t = this.getTeamByPlayer(p.getName());
		t.playerQuit(p.getName());
		this.sendMessage(Plugin.getPlugin().getSentence("player_quitted").replace("+player", p.getDisplayName()));
	}
	
	public void sendMessage(String m)
	{
		for (Team t : this.teams)
		{
			t.sendMessage(m);
		}
	}
	
	public void heal()
	{
		for (Team t : this.teams)
		{
			t.heal();
		}
	}
	
	public void performCommand(String command)
	{
		for (Team t : this.teams)
		{
			t.performCommand(command);
		}
	}
	
	public void give(ItemStack is)
	{
		for (Team t : this.teams)
		{
			t.give(is);
		}
	}
	
	public void kick(Player p)
	{
		Team t = this.getTeamByPlayer(p.getName());
		t.playerQuit(p.getName());
		Plugin.getPlugin().sendPreMessage(p, "been_kicked");
		this.sendMessage(Plugin.getPlugin().getSentence("player_kicked").replace("+player", p.getDisplayName()));
	}
	
	public Team getTeamByPlayer(String player)
	{
		for (Team t : this.teams)
		{
			 if (t.getPlayers().containsKey(player))
				 return t;
		}
		return null;
	}
	
	public void stopEvent()
	{
		if (state == EventState.ACTIVE)
		{
			for (Team t : this.teams)
			{
				t.sendMessage(Plugin.getPlugin().getSentence("stop_event"));
				t.takeBackPlayers();
			}
			this.state = EventState.INACTIVE;
		}
	}
	
	public Team getTeamByTag(String tag)
	{
		for (Team t : this.teams)
		{
			if (t.getTag().equalsIgnoreCase(tag))
				return t;
		}
		return null;
	}
	
	public void startEvent()
	{
		// Premièrement, on fais tp tous les joueurs.
		for (Team t : this.teams)
		{
			t.sendMessage(Plugin.getPlugin().getSentence("start_event"));
			t.teleportPlayers();
		}
		
		this.state = EventState.ACTIVE;
	}
	
	public boolean isActive() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}
	
	public boolean canStart()
	{
		for (Team t : this.teams)
			if (t.getSpawn() == null)
				return false;
		
		return true;
	}
	
	public void addTeam(Team t)
	{
		this.teams.add(t);
		Plugin.getPlugin().getConfig().set("events." + this.getTag() + ".teams." + t.getTag(), new HashMap<String, Object>());
		ConfigurationSection c = Plugin.getPlugin().getConfig().getConfigurationSection("events." + this.getTag() + ".teams." + t.getTag());
		if (t.getSpawn() != null)
			c.set("spawn", Helper.convertLocationToString(t.getSpawn()));
		else
			c.set("spawn", "");
		Plugin.getPlugin().saveConfig();
	}
	
	public void removeTeam(Team t)
	{
		this.teams.remove(t);
		FileConfiguration c = Plugin.getPlugin().getConfig();
		c.set("events." + this.tag + ".teams." + t.getTag(), null);
		Plugin.getPlugin().saveConfig();
	}
}
