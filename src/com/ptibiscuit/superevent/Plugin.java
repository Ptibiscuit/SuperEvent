package com.ptibiscuit.superevent;

import com.ptibiscuit.framework.Helper;
import com.ptibiscuit.framework.JavaPluginEnhancer;
import com.ptibiscuit.framework.PermissionHelper;
import com.ptibiscuit.superevent.api.EventType;
import com.ptibiscuit.superevent.listeners.PlayerManager;
import com.ptibiscuit.superevent.models.Event;
import com.ptibiscuit.superevent.models.Team;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class Plugin extends JavaPluginEnhancer {

	private ArrayList<Event> events = new ArrayList<Event>();
	private HashMap<String, String> eventtypes = new HashMap<String, String>();
	private PlayerManager pm = new PlayerManager();
	private static Plugin plugin;

	@Override
	public void onConfigurationDefault(FileConfiguration c) {
		c.set("events", new HashMap<String, Object>());
		c.set("config.when_a_player_join.display_to_team", true);
		c.set("config.when_a_player_join.display_to_event", false);
	}

	@Override
	public void onLangDefault(Properties p) {
		p.setProperty("event_created", "L'event " + ChatColor.GOLD + "\"+event\"" + ChatColor.WHITE + " a été crée.");
		p.setProperty("cant_find_event", "Ce tag d'event n'existe pas.");
		p.setProperty("cant_find_team", "Ce tag de team n'existe pas.");
		p.setProperty("must_be_player", "Vous devez être un joueur pour faire ceci.");
		p.setProperty("team_added", "La team " + ChatColor.GOLD + "\"+team\"" + ChatColor.WHITE + " a bien été ajouté !");
		p.setProperty("top_list_event", "Liste des events diponibles:");
		p.setProperty("elem_list_event", "- " + ChatColor.GOLD + "+event" + ChatColor.WHITE + ": +nbre_teams teams");
		p.setProperty("top_list_team", "Liste des groupes de l'event " + ChatColor.GOLD + "\"+name\"" + ChatColor.WHITE + ":");
		p.setProperty("elem_list_team", "- " + ChatColor.GOLD + "+team" + ChatColor.WHITE + ": +nbre_players joueurs");
		p.setProperty("top_list_player", "Liste des joueurs du group " + ChatColor.GOLD + "\"+name\"" + ChatColor.WHITE + ":");
		p.setProperty("elem_list_player", "- +player");
		p.setProperty("tag_already_taken", "Ce tag est déjà utilisé");
		p.setProperty("spawn_fix", "Spawn de la team " + ChatColor.GOLD + "\"+team\"" + ChatColor.WHITE + " fixée !");
		p.setProperty("join_team", "Vous avez rejoins la team " + ChatColor.GOLD + "\"+name\"" + ChatColor.WHITE + ".");
		p.setProperty("event_started", "Les joueurs ont été teleportés !");
		p.setProperty("player_joined", "+player a rejoint l'équipe +team !");
		p.setProperty("player_quitted", "+player a quitté l'event !");
		p.setProperty("already_started", "L'event a déjà commencé.");
		p.setProperty("no_id", "Id d'item inconnu.");
		p.setProperty("already_stopped", "L'event n'a pas commencé.");
		p.setProperty("event_stopped", "L'event a été arreté.");
		p.setProperty("cant_start", "Impossible de démarrer l'event.");
		p.setProperty("start_event", "L'event commence !");
		p.setProperty("stop_event", "L'event est fini !");
		p.setProperty("team_deleted", "Le groupe " + ChatColor.GOLD + "\"+team\"" + ChatColor.WHITE + " a été supprimé.");
		p.setProperty("event_deleted", "L'event " + ChatColor.GOLD + "\"+event\"" + ChatColor.WHITE + " a été supprimé.");
		p.setProperty("message_sent_event", ChatColor.GRAY + "[" + ChatColor.GOLD + "+event" + ChatColor.GRAY + "]" + ChatColor.WHITE + "+message");
		p.setProperty("message_sent_team", ChatColor.GRAY + "[" + ChatColor.GOLD + "+event" + ChatColor.WHITE + "->" + ChatColor.YELLOW + "+team" + ChatColor.GRAY + "]" + ChatColor.WHITE + "+message");
		p.setProperty("already_in_team", "Vous êtes déjà dans une team.");
		p.setProperty("quit_team", "Vous avez quitté votre team.");
		p.setProperty("not_in_team", "Vous n'êtes pas dans une équipe.");
		p.setProperty("hes_not_in_team", "Ce joueur n'est pas dans une équipe.");
		p.setProperty("been_healed", "Vous avez été healé !");
		p.setProperty("object_recieved", "Vous avez reçu un objet !");
		p.setProperty("performed_command", "Vous avez fait la commande " + ChatColor.YELLOW + "\"+command\"" + ChatColor.YELLOW + "");
		p.setProperty("player_kicked", "+player a été kické de l'event.");
		p.setProperty("been_kicked", "Vous avez été kické de l'event.");
		p.setProperty("cant_find_player", "Ce joueur n'existe pas.");
		p.setProperty("player_kicked", "+player a été kické.");
		p.setProperty("heal_group", "Vous avez healé le groupe +group de l'event +event.");
		p.setProperty("heal_event", "Vous avez healé l'event +event.");
		p.setProperty("give_event", "Vous avez donné un objet à l'event +event.");
		p.setProperty("give_group", "Vous avez donné un objet au groupe +group de l'event +event.");
		p.setProperty("command_event", "Vous avez forcé la commande " + ChatColor.YELLOW + "\"/+command\"" + ChatColor.WHITE + " l'event +event.");
		p.setProperty("command_group", "Vous avez forcé la commande " + ChatColor.YELLOW + "\"+command\"" + ChatColor.WHITE + " au groupe +group de l'event +event.");
		p.setProperty("cant_do", "Vous n'avez pas la permission de faire ça.");
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
		plugin = this;
		this.setup(ChatColor.RED + "[SuperEvent]", "superevent", true);
		this.getMyLogger().startFrame();

		this.getMyLogger().addInFrame("SuperEvent by Ptibiscuit");
		//this.getMyLogger().addCompleteLineInFrame();

		// Pour les eventype, on doit d'abord charger les classes qu'il nous faut.
		ConfigurationSection ts = this.getConfig().getConfigurationSection("types");
		if (ts != null) {
			for (Entry<String, Object> entry : ts.getValues(false).entrySet()) {
				String classNew = entry.getValue().toString();
				this.eventtypes.put(entry.getKey(), classNew);
			}
		}

		/*
		 * Architecture du fichier: ------------------------- events: tag: name:
		 * type: tag: options: {DEFINED BY THE DEVELOPPER} teams: tag: spawn:
		 */
		ConfigurationSection cs = this.getConfig().getConfigurationSection("events");
		if (cs.getValues(false) != null) {
			for (Entry<String, Object> entry : cs.getValues(false).entrySet()) {
				ConfigurationSection data = (ConfigurationSection) entry.getValue();
				String tag = entry.getKey();
				ArrayList<Team> teams = new ArrayList<Team>();
				ConfigurationSection csTeams = data.getConfigurationSection("teams");
				if (csTeams != null) {
					for (Entry<String, Object> entryTeam : csTeams.getValues(false).entrySet()) {
						ConfigurationSection dataTeam = (ConfigurationSection) entryTeam.getValue();
						String tagTeam = entryTeam.getKey();
						Location spawnTeam = null;
						if (dataTeam.getString("spawn") != null) {
							spawnTeam = Helper.convertStringToLocation(dataTeam.getString("spawn"));
						}

						Team t = new Team(tagTeam, spawnTeam, new HashMap<String, Location>());
						teams.add(t);
					}
				}
				Event e = new Event(tag, teams);
				/*
				ConfigurationSection csType = data.getConfigurationSection("type");
				if (csType != null) {
					String urlType =
					this.getEventTypeByTag(csType.get("tag").toString());
					if (urlType != null) { EventType eventType =
						this.generateEventTypeByUrl(urlType); eventType.setEvent(e);
						eventType.setConfig(data.getConfigurationSection("type.options"));
						eventType.onEnable();
					} else {
						this.getMyLogger().addInFrame("Can't load the type of " + tag + ", a problem, maybe ?", false);
					}
				}
				*/
				this.events.add(e);
			}
		}
		PluginManager pgm = this.getServer().getPluginManager();
		pgm.registerEvents(pm, this);
		this.getMyLogger().displayFrame(false);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (label.equalsIgnoreCase("secreate")) {
				if (!this.permissionHandler.has(sender, "event.create", true)) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e == null) {

					this.createEvent(new Event(args[0], new ArrayList<Team>()));
					this.sendMessage(sender, this.getSentence("event_created").replace("+event", args[0]));
				} else {
					this.sendPreMessage(sender, "tag_already_taken");
				}
			} else if (label.equalsIgnoreCase("sekick")) {
				if (!this.permissionHandler.has(sender, "kick", true)) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Player p = this.getServer().getPlayer(args[0]);
				if (p != null) {
					Team t = this.getTeamByPlayer(args[0]);
					if (t != null) {
						Event e = this.getEventByTeam(t);
						e.kick(p);
						this.sendPreMessage(sender, "player_kicked");
					} else {
						this.sendPreMessage(p, "hes_not_in_team");
					}
				} else {
					this.sendPreMessage(sender, "cant_find_player");
				}
			} else if (label.equalsIgnoreCase("seheal")) {
				if (!this.permissionHandler.has(sender, "event.heal", true)) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}
				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					e.heal();
					this.sendMessage(sender, this.getSentence("heal_event").replace("+event", e.getTag()));
				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("sehealgroup")) {
				if (!(this.permissionHandler.has(sender, "group.heal", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					Team t = e.getTeamByTag(args[1]);
					if (t != null) {
						t.heal();
						this.sendMessage(sender, this.getSentence("heal_group").replace("+event", e.getTag()).replace("+group", t.getTag()));
					} else {
						this.sendPreMessage(sender, "cant_find_team");
					}
				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("secommand")) {
				if (!(this.permissionHandler.has(sender, "event.command", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					String commandMessage = "";
					for (int i = 1; i < args.length; i++) {
						commandMessage += args[i] + " ";
					}
					commandMessage = commandMessage.trim();
					e.performCommand(commandMessage);
					this.sendMessage(sender, this.getSentence("command_event").replace("+command", commandMessage).replace("+event", e.getTag()));
				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("secommandgroup")) {
				if (!(this.permissionHandler.has(sender, "group.command", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					Team t = e.getTeamByTag(args[1]);
					if (t != null) {
						String commandMessage = "";
						for (int i = 2; i < args.length; i++) {
							commandMessage += args[i] + " ";
						}
						commandMessage = commandMessage.trim();
						t.performCommand(commandMessage);
						this.sendMessage(sender, this.getSentence("command_group").replace("+group", t.getTag()).replace("+command", commandMessage).replace("+event", e.getTag()));
					} else {
						this.sendPreMessage(sender, "cant_find_team");
					}
				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("segive")) {
				if (!(this.permissionHandler.has(sender, "event.give", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					ItemStack is = null;
					String[] type = args[1].split(":");
					Material material = Material.matchMaterial(type[0]);
					if (material != null) {
						if (args.length == 2) {

							if (type.length == 1) {
								is = new ItemStack(Integer.parseInt(type[0]), 1);
							} else {
								is = new ItemStack(Integer.parseInt(type[0]), 1, Short.parseShort(type[1]));
							}
						} else {
							int amount = Integer.parseInt(args[2]);
							if (amount < 1) {
								amount = 1;
							}
							if (amount > 64) {
								amount = 64;
							}

							if (type.length == 1) {
								is = new ItemStack(Integer.parseInt(type[0]), amount);
							} else {
								is = new ItemStack(Integer.parseInt(type[0]), amount, Short.parseShort(type[1]));
							}
						}
						e.give(is);
						this.sendMessage(sender, this.getSentence("give_event").replace("+event", e.getTag()));
					} else {
						this.sendPreMessage(sender, "no_id");
					}

				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("segivegroup")) {
				if (!(this.permissionHandler.has(sender, "group.give", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					Team t = e.getTeamByTag(args[1]);
					if (t != null) {
						ItemStack is = null;
						String[] type = args[1].split(":");
						Material material = Material.matchMaterial(type[0]);
						if (material != null) {
							if (args.length == 2) {
								if (type.length == 1) {
									is = new ItemStack(Integer.parseInt(type[0]), 1);
								} else {
									is = new ItemStack(Integer.parseInt(type[0]), 1, Short.parseShort(type[1]));
								}
							} else {
								int amount = Integer.parseInt(args[2]);
								if (amount < 1) {
									amount = 1;
								}
								if (amount > 64) {
									amount = 64;
								}

								if (type.length == 1) {
									is = new ItemStack(Integer.parseInt(type[0]), amount);
								} else {
									is = new ItemStack(Integer.parseInt(type[0]), amount, Short.parseShort(type[1]));
								}
							}
							e.give(is);
							this.sendMessage(sender, this.getSentence("give_group").replace("+group", t.getTag()).replace("+event", e.getTag()));
						} else {
							this.sendPreMessage(sender, "no_id");
						}
					} else {
						this.sendPreMessage(sender, "cant_find_team");
					}
				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("selist")) {
				if (args.length == 0) {
					if (!(this.permissionHandler.has(sender, "list", true))) {
						this.sendPreMessage(sender, "cant_do");
						return true;
					}

					this.sendPreMessage(sender, "top_list_event");
					for (Event e : events) {
						this.sendMessage(sender, this.getSentence("elem_list_event").replace("+event", e.getTag()).replace("+nbre_teams", String.valueOf(e.getTeams().size())));
					}
				} else if (args.length == 1) {
					if (!(this.permissionHandler.has(sender, "event.list", true))) {
						this.sendPreMessage(sender, "cant_do");
						return true;
					}

					Event e = this.getEventByTag(args[0]);
					if (e != null) {
						this.sendMessage(sender, this.getSentence("top_list_team").replace("+name", e.getTag()));
						for (Team t : e.getTeams()) {
							this.sendMessage(sender, this.getSentence("elem_list_team").replace("+team", t.getTag()).replace("+nbre_players", String.valueOf(t.getPlayers().size())));
						}
					} else {
						this.sendPreMessage(sender, "cant_find_event");
					}
				} else if (args.length == 2) {
					if (!(this.permissionHandler.has(sender, "group.list", true))) {
						this.sendPreMessage(sender, "cant_do");
						return true;
					}

					Event e = this.getEventByTag(args[0]);
					if (e != null) {
						Team t = e.getTeamByTag(args[1]);
						if (t != null) {
							this.sendMessage(sender, this.getSentence("top_list_player").replace("+name", t.getTag()));
							for (String namePlayer : t.getPlayers().keySet()) {
								Player player = this.getServer().getPlayer(namePlayer);
								this.sendMessage(sender, this.getSentence("elem_list_player").replace("+player", player.getDisplayName()));
							}
						} else {
							this.sendPreMessage(sender, "cant_find_team");
						}
					} else {
						this.sendPreMessage(sender, "cant_find_event");
					}
				}
			} else if (label.equalsIgnoreCase("sestart")) {
				if (!(this.permissionHandler.has(sender, "event.start", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					if (!e.isActive()) {
						if (e.canStart()) {
							e.startEvent();
							this.sendPreMessage(sender, "event_started");
						} else {
							this.sendPreMessage(sender, "cant_start");
						}
					} else {
						this.sendPreMessage(sender, "already_started");
					}
				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("sestop")) {
				if (!(this.permissionHandler.has(sender, "event.stop", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					if (e.isActive()) {
						e.stopEvent();
						this.sendPreMessage(sender, "event_stopped");
					} else {
						this.sendPreMessage(sender, "already_stopped");
					}
				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("sedel")) {
				if (!(this.permissionHandler.has(sender, "event.delete", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					this.deleteEvent(e);
					this.sendMessage(sender, this.getSentence("event_deleted").replace("+event", e.getTag()));
				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("sesay")) {
				if (!(this.permissionHandler.has(sender, "event.say", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					String message = "";
					for (int i = 1; i < args.length; i++) {
						message += " " + args[i];
					}
					message = message.trim();

					e.sendMessage(this.getSentence("message_sent_event").replace("+event", e.getTag()).replace("+message", message));
					this.sendMessage(sender, this.getSentence("message_sent_event").replace("+event", e.getTag()).replace("+message", message));
				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("sesaygroup")) {
				if (!this.permissionHandler.has(sender, "group.say", true)) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					Team t = e.getTeamByTag(args[1]);
					if (t != null) {
						String message = "";
						for (int i = 2; i < args.length; i++) {
							message += " " + args[i];
						}
						message = message.trim();

						t.sendMessage(this.getSentence("message_sent_team").replace("+team", t.getTag()).replace("+event", e.getTag()).replace("+message", message));
						this.sendMessage(sender, this.getSentence("message_sent_team").replace("+team", t.getTag()).replace("+event", e.getTag()).replace("+message", message));
					} else {
						this.sendPreMessage(sender, "cant_find_team");
					}
				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("sedelgroup")) {
				if (!(this.permissionHandler.has(sender, "group.give", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					Team t = e.getTeamByTag(args[1]);
					if (t != null) {
						e.removeTeam(t);
						this.sendMessage(sender, this.getSentence("team_deleted").replace("+team", t.getTag()));
					} else {
						this.sendPreMessage(sender, "cant_find_team");
					}
				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("sesettype")) {
				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					// On regarde d'abord si l'eventtype veut bien de cet event
				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			}
			////////////////////////////////////////// THE WALL
			if (!(sender instanceof Player)) {
				this.sendPreMessage(sender, "must_be_player");
				return true;
			}
			Player p = (Player) sender;
			/////////////////////////////////////////// THE WALL

			if (label.equalsIgnoreCase("seaddgroup")) {
				if (!(this.permissionHandler.has(sender, "event.addteam", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					Team t = e.getTeamByTag(args[1]);
					if (t == null) {
						e.addTeam(new Team(args[1], p.getLocation(), new HashMap<String, Location>()));
						this.sendMessage(p, this.getSentence("team_added").replace("+team", args[1]));
					} else {
						this.sendPreMessage(sender, "cant_find_team");
					}
				} else {
					this.sendPreMessage(sender, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("sesetspawn")) {
				if (!(this.permissionHandler.has(sender, "group.spawn.set", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Event e = this.getEventByTag(args[0]);
				if (e != null) {
					Team t = e.getTeamByTag(args[1]);
					if (t != null) {
						t.setSpawn(p.getLocation());
						this.sendMessage(sender, getSentence("spawn_fix").replace("+team", t.getTag()));
					} else {
						this.sendPreMessage(p, "cant_find_team");
					}
				} else {
					this.sendPreMessage(p, "cant_find_event");
				}
			} else if (label.equalsIgnoreCase("sejoin")) {
				if (!(this.permissionHandler.has(sender, "join", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Team test = this.getTeamByPlayer(p.getName());
				if (test == null) {
					Event e = this.getEventByTag(args[0]);
					if (e != null) {
						if (!e.isActive()) {
							Team t = null;
							if (args.length >= 2) {
								t = e.getTeamByTag(args[1]);
								if (t == null) {
									this.sendPreMessage(p, "cant_find_team");
									return true;
								}
							} else {
								t = e.getTeamWithMinimPlayer();
							}
							e.playerJoin(t, p);
							this.sendMessage(sender, this.getSentence("join_team").replace("+name", t.getTag()));
						} else {
							this.sendPreMessage(sender, "already_started");
						}
					} else {
						this.sendPreMessage(sender, "cant_find_event");
					}
				} else {
					this.sendPreMessage(sender, "already_in_team");
				}
			} else if (label.equalsIgnoreCase("sequit")) {
				if (!(this.permissionHandler.has(sender, "quit", true))) {
					this.sendPreMessage(sender, "cant_do");
					return true;
				}

				Team t = this.getTeamByPlayer(p.getName());
				if (t != null) {
					Event e = this.getEventByTeam(t);
					e.playerQuit(p);
					this.sendPreMessage(sender, "quit_team");
				} else {
					this.sendPreMessage(sender, "not_in_team");
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}

	public void createEvent(Event e) {
		this.events.add(e);

		// TODO Rajouter dans le fichier de configuration
		this.getConfig().set("events." + e.getTag() + ".teams", new ArrayList<Team>());
		this.saveConfig();
	}

	public EventType generateEventTypeByUrl(String url) {
		try {
			Class eventTypeClass = Class.forName(url);
			Object o = eventTypeClass.newInstance();
			EventType eventType = (EventType) o;
			return eventType;
		} catch (InstantiationException ex) {
			Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public String getEventTypeByTag(String tag) {
		return this.eventtypes.get(tag);
	}

	public void addTeamForEvent(Event e, Team t) {
		e.getTeams().add(t);
	}

	public Event getEventByTag(String tag) {
		for (Event e : this.events) {
			if (e.getTag().equalsIgnoreCase(tag)) {
				return e;
			}
		}
		return null;
	}

	public Event getEventByTeam(Team t) {
		for (Event e : this.events) {
			if (e.getTeams().contains(t)) {
				return e;
			}
		}
		return null;
	}

	public Team getTeamByPlayer(String player) {
		for (Event e : this.events) {
			Team t = e.getTeamByPlayer(player);
			if (t != null) {
				return t;
			}
		}
		return null;
	}

	public void deleteEvent(Event e) {
		e.stopEvent();
		this.events.remove(e);
		this.getConfig().set("events." + e.getTag(), null);
		this.saveConfig();
	}

	public static Plugin getPlugin() {
		return plugin;
	}
}
