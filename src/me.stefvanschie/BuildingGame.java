package me.stefvanschie;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
public class BuildingGame extends JavaPlugin
{
	int first = 0;
	int second = 0;
	int third = 0;
	Player firstplayer;
	Player secondplayer;
	Player thirdplayer;
	String arena;
	int place;
	int counter = 0;
	HashMap<Player, String> players = new HashMap<Player, String>();
	HashMap<Integer, Player> playernumbers = new HashMap<Integer, Player>();
	HashMap<Player, Integer> votes = new HashMap<Player, Integer>();
	//files
	File arenasFile = new File("arenas.yml");
	File configFile = new File("config.yml");
	YamlConfiguration arenas = YamlConfiguration.loadConfiguration(arenasFile);
	YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
	@Override
	public void onEnable()
	{
		//files
		arenas = new YamlConfiguration();
		config = new YamlConfiguration();
		arenasFile = new File(getDataFolder(), "arenas.yml");
		configFile = new File(getDataFolder(), "config.yml");
		if (!arenasFile.exists())
		{
			try
			{
				arenasFile.createNewFile();
			}
			catch (IOException e)
			{
				getLogger().warning("Building Game's arenas.yml had some problems.");
				getLogger().warning("If you don't see the arenas.yml folder, please restart your server!");
			}
			arenasFile.getParentFile().mkdirs();
		}
		else if (!configFile.exists())
		{
			try
			{
				configFile.createNewFile();
			}
			catch (IOException e)
			{
				getLogger().warning("Building Game's config.yml had some problems.");
				getLogger().warning("If you don't see the config.yml folder, please restart your server!");
			}
			configFile.getParentFile().mkdirs();
			generateSettings();
		}
		saveYamls();
		loadYamls();
		//other things
		getLogger().info("Building Game has been enabled succesfully!");
		saveYamls();
	}
	@Override
	public void onDisable()
	{
		getLogger().info("Building Game has been disabled succesfully!");
		saveYamls();
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("bg"))
		{
			if (args[0].equalsIgnoreCase("setmainspawn") && sender instanceof Player)
			{
				if (player.hasPermission("setmainspawn"))
				{
					arenas.set("main-spawn.world", player.getLocation().getWorld().getName());
					arenas.set("main-spawn.x", player.getLocation().getBlockX());
					arenas.set("main-spawn.y", player.getLocation().getBlockY());
					arenas.set("main-spawn.z", player.getLocation().getBlockZ());
					saveYamls();
					player.sendMessage(ChatColor.GREEN + "Buildinggame main spawn has been set!");
				}
				else if (!player.hasPermission("setmainspawn"))
				{
					player.sendMessage(ChatColor.RED + "You don't have the required permission for that!");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.setmainspawn.permission");
				}
			}
			else if (args[0].equalsIgnoreCase("createarena"))
			{
				if (player.hasPermission("createarena"))
				{
					if (args.length == 2)
					{
						arenas.set(args[1], null);
						saveYamls();
						player.sendMessage(ChatColor.GREEN + "Arena " + args[1] + " created!");
					}
					else if (args.length < 2)
					{
						player.sendMessage(ChatColor.RED + "Please specify the arena name!");
					}
					else if (args.length > 2)
					{
						player.sendMessage(ChatColor.RED + "Please only specify the arena name!");
					}
					else
					{
						player.sendMessage(ChatColor.RED + "An unexpected error occured! Error: bg.createarena.args.length");
					}
				}
				else if (!player.hasPermission("createarena"))
				{
					player.sendMessage(ChatColor.RED + "You don't have the required permission for that!");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "An unexpected error occurred. Error: bg.createarena.permission");
				}
			}
			else if (args[0].equalsIgnoreCase("setspawn") && sender instanceof Player)
			{
				if (player.hasPermission("setspawn"))
				{
					if (args.length == 2)
					{
						counter++;
						arenas.set(args[1] + "." + counter + ".world", player.getLocation().getWorld().getName());
						arenas.set(args[1] + "." + counter + ".x", player.getLocation().getBlockX());
						arenas.set(args[1] + "." + counter + ".y", player.getLocation().getBlockY());
						arenas.set(args[1] + "." + counter + ".z", player.getLocation().getBlockZ());
						arenas.set(args[1] + ".maxplayers", counter);
						saveYamls();
						player.sendMessage(ChatColor.GREEN + "Spawn " + counter + " set!");
					}
					else if (args.length > 2)
					{
						player.sendMessage(ChatColor.RED + "Please only specify the arenaname");
					}
					else if (args.length < 2)
					{
						player.sendMessage(ChatColor.RED + "Please specify the arenaname");
					}
					else
					{
						player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.setspawn.args.length");
					}
				}
				else if (!player.hasPermission("setspawn"))
				{
					player.sendMessage(ChatColor.RED + "You don't have the permission for that");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.setspawn.permission");
				}
			}
			else if (args[0].equalsIgnoreCase("join") && sender instanceof Player)
			{
				if (args.length == 2)
				{
					if (player.hasPermission("join"))
					{
						if (arenas.contains(args[1]))
						{
							if (players.size() < arenas.getInt(args[1] + ".maxplayers"))
							{
								player.sendMessage(ChatColor.GOLD + "You have joined the game");
								for (Player p : players.keySet())
								{
									p.sendMessage(ChatColor.GOLD + "" + player.getName() + " joined the game!");
								}
								playernumbers.put(players.size() + 1, player);
								players.put(player, args[1]);
								if (players.size() == arenas.getInt(args[1] + ".maxplayers"))
								{
									List<String> subjects = new ArrayList<String>(); 
									subjects = config.getStringList("subjects");
									Random rndm = new Random();
									int subjectint = rndm.nextInt(subjects.size());
									String subject = subjects.get(subjectint);
									int places = 1;         
									for (Player pl : players.keySet())
									{
										if (players.get(pl) == args[1])
										{
											String worldstr = arenas.getString(args[1] + "." + places + ".world");
											World world = getServer().getWorld(worldstr);
											int x = arenas.getInt(args[1] + "." + places + ".x");
											int y = arenas.getInt(args[1] + "." + places + ".y");
											int z = arenas.getInt(args[1] + "." + places + ".z");
											Location location = new Location(world, x, y, z);
											pl.teleport(location);
											places++;
											pl.sendMessage(ChatColor.GOLD + "The game has started!");
											pl.sendMessage(ChatColor.GOLD + "The subject is " + subject);
										}
									}
									timer(args[1]);
								}
							}
							else if (players.size() >= getConfig().getInt(args[1] + ".maxplayers"))
							{
								player.sendMessage(ChatColor.RED + "This arena is currently full");
							}
							else
							{
								player.sendMessage(ChatColor.RED + "An unexpected error occured: Error: bg.join.playersingame");
							}
						}
						else if (!config.contains(args[1]))
						{
							player.sendMessage(ChatColor.RED + "This arena does not exists!");
						}
						else
						{
							player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.join.config.contains");
						}
					}
					else if (!player.hasPermission("join"))
					{
						player.sendMessage(ChatColor.RED + "You don't have the required permission for that!");
					}
					else
					{
						player.sendMessage(ChatColor.RED + "An unexpected error occured: Error: bg.join.permission");
					}
				}
				else if (args.length < 2)
				{
					player.sendMessage(ChatColor.RED + "Please specify the arena name!");
				}
				else if (args.length > 2)
				{
					player.sendMessage(ChatColor.RED + "Please only specify the arena name!");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.join.args.length");
				}
			}
			else if (args[0].equalsIgnoreCase("leave") && sender instanceof Player)
			{
				if (player.hasPermission("leave"))
				{
					String worldstr = arenas.getString("main-spawn.world");
					World world = getServer().getWorld(worldstr);
					Location location = new Location(world, arenas.getInt("main-spawn.x"), arenas.getInt("main-spawn.y"), arenas.getInt("main-spawn.z"));
					if (players.containsKey(player))
					{
						String arena = players.get(player);
						players.remove(player);
						playernumbers.remove(player);
						votes.remove(player);
						player.teleport(location);
						player.sendMessage(ChatColor.GOLD + "You have left the game!");
						for (Player pl : players.keySet())
						{
							if (players.get(pl) == arena)
							{
								pl.sendMessage(ChatColor.GOLD + "" + player.getName() + " has left the arena!");
							}
						}
					}
					else
					{
						player.sendMessage(ChatColor.GOLD + "You're not in a game!");
					}
				}
				else if (!player.hasPermission("leave"))
				{
					player.sendMessage(ChatColor.RED + "You don't have the required permission for that!");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "An unexpected error occured: Error: bg.leave.permission");
				}
			}
			else if (args[0].equalsIgnoreCase("vote"))
			{
				if (player.hasPermission("bg.vote"))
				{
					if (players.containsKey(player))
					{
						int args1 = 0;
						try
						{
							args1 = Integer.parseInt(args[1]);
						}
						catch (NumberFormatException nfe)
						{
							player.sendMessage(ChatColor.RED + "This value must be an integer!");
						}
						if (args1 >= 1 && args1 <= 10)
						{
							Player pl = playernumbers.get(place);
							votes.put(pl, args1 + votes.get(pl));
							player.sendMessage(ChatColor.GOLD + "You gave" + playernumbers.get(place).getName() + "'s plot a " + args[2]);
						}
						else if (!(args1 >= 1 && args1 <= 10))
						{
							player.sendMessage(ChatColor.RED + "Choose a number between 1 and 10");
						}
						else
						{
							player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.vote.number");
						}
					}
					else if (!players.containsKey(player))
					{
						player.sendMessage(ChatColor.RED + "You're not in a game");
					}
					else
					{
						player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.vote.cotains");
					}
				}
				else if (!player.hasPermission("bg.vote"))
				{
					player.sendMessage(ChatColor.RED + "You don't have the required permission for that!");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.vote.permission");
				}
			}
			else if (args[0].equalsIgnoreCase("help"))
			{
				player.sendMessage(ChatColor.DARK_GRAY + "---------------------" + ChatColor.GOLD + "BuildingGame" + ChatColor.DARK_GRAY + "---------------------");
				player.sendMessage(ChatColor.GOLD + "/bg setmainspawn" + ChatColor.DARK_GRAY + " - Sets the main spawn location for the buildinggame");
				player.sendMessage(ChatColor.GOLD + "/bg createarena <arenaname>" + ChatColor.DARK_GRAY + " - Create a new arena");
				player.sendMessage(ChatColor.GOLD + "/bg setspawn <arenaname>" + ChatColor.DARK_GRAY + " - Set a new spawn location");
				player.sendMessage(ChatColor.GOLD + "/bg join <arenaname>" + ChatColor.DARK_GRAY + " - Join an arena");
				player.sendMessage(ChatColor.GOLD + "/bg leave" + ChatColor.DARK_GRAY + " - Leave your game");
				player.sendMessage(ChatColor.GOLD + "/bg vote <1-10>" + ChatColor.DARK_GRAY + " - Vote on a player's building");
			}
			else if (args[0].equalsIgnoreCase("setting"))
			{
				if (args[1].equalsIgnoreCase("timer"))
				{
					if (player.hasPermission("bg.setting.timer"))
					{
						if (isInt(args[2]))
						{
							config.set("timer", args[2]);
							saveYamls();
							player.sendMessage(ChatColor.GREEN + "The timer setting has been set to " + args[2]);
						}
						else if (!isInt(args[2]))
						{
							player.sendMessage(ChatColor.RED + "This setting can only be an integer!");
						}
						else
						{
							player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.setting.timer.integer");
						}
					}
					else if (!player.hasPermission("bg.setting.timer"))
					{
						player.sendMessage(ChatColor.RED + "You don't have the required permission for that!");
					}
					else
					{
						player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.setting.timer.permission");
					}
				}
				else if (args[1].equalsIgnoreCase("votetimer"))
				{
					if (player.hasPermission("bg.setting.votetimer"))
					{
						if (isInt(args[2]))
						{
							config.set("votetimer", args[2]);
							saveYamls();
							player.sendMessage(ChatColor.GREEN + "The votetimer setting has been set to " + args[2]);
						}
						else if (!isInt(args[2]))
						{
							player.sendMessage(ChatColor.RED + "This setting can only be an integer!");
						}
						else
						{
							player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.setting.votetimer.isInt");
						}
					}
					else if (!player.hasPermission("bg.setting.votetimer"))
					{
						player.sendMessage(ChatColor.RED + "You don't have the required permission for that!");
					}
					else
					{
						player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.setting.votetimer.permission");
					}
				}
				else if (args[1].equalsIgnoreCase("subjects"))
				{
					if (args[2].equalsIgnoreCase("add"))
					{
						if (player.hasPermission("bg.setting.subjects.add"))
						{
							List<String> subjects = new ArrayList<String>();
							subjects = config.getStringList("subjects");
							String subject = "";
							for(int i = 4; i < args.length; i++)
							{ 
								if (subject != "")
								{
									subject = subject + "_"; 
								}
								subject = subject + args[i];
							}
							subjects.add(subject);
						}
						else if (!player.hasPermission("bg.setting.subjects.add"))
						{
							player.sendMessage(ChatColor.RED + "You don't have the required permission for that!");
						}
						else
						{
							player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.setting.subjects.add.permission");
						}
					}
					else if (args[2].equalsIgnoreCase("remove"))
					{
						if (player.hasPermission("bg.setting.subjects.remove"))
						{
							List<String> subjects = new ArrayList<String>();
							subjects = config.getStringList("subjects");
							String subject = "";
							for(int i = 4; i < args.length; i++)
							{ 
								if (subject != "")
								{
									subject = subject + "_"; 
								}
								subject = subject + args[i];
							}
							subjects.remove(subject);
						}
						else if (!player.hasPermission("bg.setting.subjects.remove"))
						{
							player.sendMessage(ChatColor.RED + "You don't have the required permission for that!");
						}
						else
						{
							player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.setting.subjects.remove.permission");
						}
					}
					else
					{
						player.sendMessage(ChatColor.RED + "That option is not available");
					}
				}
				else
				{
					player.sendMessage(ChatColor.RED + "That setting does not exist");
				}
			}
			else if (args[0].equalsIgnoreCase("reload"))
			{
				if (args.length == 2)
				{
					if (args[1].equalsIgnoreCase("config"))
					{
						if (player.hasPermission("bg.reload.config"))
						{
							YamlConfiguration.loadConfiguration(configFile);
							player.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
						}
						else if (!player.hasPermission("bg.reload.config"))
						{
							player.sendMessage(ChatColor.RED + "You don't have the required permission for that!");
						}
						else
						{
							player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.reload.config.permission");
						}
					}
					else if (args[1].equalsIgnoreCase("arenas"))
					{
						if (player.hasPermission("bg.reload.arenas"))
						{
							YamlConfiguration.loadConfiguration(arenasFile);
							player.sendMessage(ChatColor.GREEN + "Arenas reloaded!");
						}
						else if (player.hasPermission("bg.reload.arenas"))
						{
							player.sendMessage(ChatColor.RED + "You don't have the required permission for that!");
						}
						else
						{
							player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.reload.arenas.permission");
						}
					}
					else
					{
						player.sendMessage(ChatColor.RED + "That's not a correct file");
					}
				}
				else if (args.length != 2)
				{;
					if (player.hasPermission("bg.reload") || (player.hasPermission("bg.reload.config") && player.hasPermission("bg.reload.arenas")))
					{
						YamlConfiguration.loadConfiguration(configFile);
						YamlConfiguration.loadConfiguration(arenasFile);
						player.sendMessage(ChatColor.GREEN + "All the files have been reloaded!");
					}
					else if (!player.hasPermission("bg.reload") || !player.hasPermission("bg.reload.config") || !player.hasPermission("bg.reload.arenas"))
					{
						player.sendMessage(ChatColor.RED + "You don't have the required permission for that!");
					}
					else
					{
						player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.reload.permission");
					}
				}
				else
				{
					player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.reload.args.length");
				}
			}
			else if (args[0].equalsIgnoreCase("generatesettings"))
			{
				if (player.hasPermission("bg.generatesettings"))
				{
					generateSettings();
				}
				else if (!player.hasPermission("bg.generatesettings"))
				{
					player.sendMessage(ChatColor.RED + "You don't have the required permission for that!");
				}
				else
				{
					player.sendMessage(ChatColor.RED + "An unexpected error occured. Error: bg.generatesettings.permission");
				}
			}
			else
			{
				player.sendMessage(ChatColor.DARK_GRAY + "---------------------" + ChatColor.GOLD + "BuildingGame" + ChatColor.DARK_GRAY + "---------------------");
				player.sendMessage(ChatColor.GOLD + "/bg setmainspawn" + ChatColor.DARK_GRAY + " - Sets the main spawn location for the buildinggame");
				player.sendMessage(ChatColor.GOLD + "/bg createarena <arenaname>" + ChatColor.DARK_GRAY + " - Create a new arena");
				player.sendMessage(ChatColor.GOLD + "/bg setspawn <arenaname>" + ChatColor.DARK_GRAY + " - Set a new spawn location");
				player.sendMessage(ChatColor.GOLD + "/bg join <arenaname>" + ChatColor.DARK_GRAY + " - Join an arena");
				player.sendMessage(ChatColor.GOLD + "/bg leave" + ChatColor.DARK_GRAY + " - Leave your game");
				player.sendMessage(ChatColor.GOLD + "/bg vote <1-10>" + ChatColor.DARK_GRAY + " - Mark your building as done");
			}
		}
		return false;
	}
	public void saveYamls()
	{
	    try
	    {
	        arenas.save(arenasFile);
	        config.save(configFile);
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }
	}
	public void loadYamls()
	{
	    try
	    {
	        arenas.load(arenasFile);
	        config.load(configFile);
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	    }
	}
	public static boolean isInt(String s) {
	    try
	    {
	        Integer.parseInt(s);
	    }
	    catch (NumberFormatException nfe)
	    {
	        return false;
	    }
	    return true;
	}
	public void generateSettings()
	{
		List<String> setsubjects = new ArrayList<String>();
		setsubjects.add("carrot");
		setsubjects.add("frog");
		setsubjects.add("superhero");
		setsubjects.add("octopus");
		setsubjects.add("maze");
		setsubjects.add("dog house");
		setsubjects.add("spiderman");
		setsubjects.add("baseball");
		setsubjects.add("birthday");
		setsubjects.add("cannon");
		configFile.getParentFile().mkdirs();
		config.set("timer", 300);
		config.set("votetimer", 15);
		config.set("subjects", setsubjects);
		saveYamls();
	}
	boolean firstrun = true;
	int seconds;
	public void timer(final String arena)
	{
		if (firstrun == true)
		{
			seconds = config.getInt("timer");
		}
		Bukkit.getScheduler().runTaskLater(this, new Runnable()
		{
			@Override
        	public void run()
			{
				firstrun = false;
				if (seconds == 60 || seconds == 30 || seconds == 15 || (seconds >= 1 && seconds <= 10))
				{
					for (Player pl : players.keySet())
					{
						if (players.get(pl) == arena)
						{
							pl.sendMessage(ChatColor.GOLD + "You have " + seconds + " seconds left!");
						}
					}
					seconds--;
					timer(arena);
				}
				else if (seconds == 0)
				{
					World world = getServer().getWorld(arenas.getString(arena + ".1.world"));
					int x = arenas.getInt(arena + ".1.x");
					int y = arenas.getInt(arena + ".1.y");
					int z = arenas.getInt(arena + ".1.z");
					Location location = new Location(world, x, y, z);
					for (Player pl : players.keySet())
					{
						if (players.get(pl) == arena)
						{
							votes.put(pl, 0);
							pl.teleport(location);
							pl.sendMessage(ChatColor.GOLD + "" + playernumbers.get(1).getName() + "'s plot.");
						}
					}
					firstrun = true;
					voting(arena);
				}
				else
				{
					seconds--;
					timer(arena);
				}
			}
		}, 20L);
	}
	int voteseconds;
	boolean everyrun = true;
	boolean once = true;
	public void voting(final String arena)
	{
		if (everyrun == true)
		{
			voteseconds = config.getInt("votetimer");
		}
		if (once == true)
		{
			place = 1;
		}
		Bukkit.getScheduler().runTaskLater(this, new Runnable()
		{
			@Override
        	public void run()
			{
				once = false;
				everyrun = false;
				if (voteseconds == 0)
				{
					everyrun = true;
					place++;
					if (place > config.getInt(arena))
					{
						for (Player pl : players.keySet())
						{
							if (players.get(pl) == arena)
							{
								if (votes.get(pl) > first)
								{
									third = second;
									thirdplayer = secondplayer;
									second = first;
									secondplayer = firstplayer;
									first = votes.get(pl);
									firstplayer = pl;
								}
								else if (votes.get(pl) < first && votes.get(pl) > second)
								{
									third = second;
									thirdplayer = secondplayer;
									second = votes.get(pl);
									secondplayer = pl;
								}
								else if (votes.get(pl) < second && votes.get(pl) > third)
								{
									third = votes.get(pl);
									thirdplayer = pl;
								}
							}
						}
						String worldstr = arenas.getString("main-spawn.world");
						World world = getServer().getWorld(worldstr);
						int x = arenas.getInt("main-spawn.x");
						int y = arenas.getInt("main-spawn.y");
						int z = arenas.getInt("main-spawn.z");
						Location location = new Location(world, x, y, z);
						for (Player pl : players.keySet())
						{
							if (players.get(pl) == arena)
							{
								pl.teleport(location);
								pl.sendMessage(ChatColor.GOLD + "Game done!");
								if (firstplayer == pl)
								{
									pl.sendMessage(ChatColor.GREEN + "You went first with " + first + " points!");
								}
								else if (secondplayer == pl)
								{
									pl.sendMessage(ChatColor.GREEN + "You went second with " + second + " points!");
								}
								else if (thirdplayer == pl)
								{
									pl.sendMessage(ChatColor.GREEN + "You went third with " + third + " points!");
								}
								first = 0;
								second = 0;
								third = 0;
								votes.remove(pl);
								playernumbers.remove(pl);
								players.remove(pl);
							}
						}
						once = true;
						everyrun = true;
						voteseconds = config.getInt("votetimer");
					}
					else
					{
						String worldstr = arenas.getString("main-spawn.world");
						World world = getServer().getWorld(worldstr);
						int x = arenas.getInt("main-spawn.x");
						int y = arenas.getInt("main-spawn.y");
						int z = arenas.getInt("main-spawn.z");
						Location location = new Location(world, x, y, z);
						for (Player pl : players.keySet())
						{
							if (players.get(pl) == arena)
							{
								pl.teleport(location);
							}
						}
						voting(arena);
					}
				}
				else
				{
					voteseconds--;
					voting(arena);
				}
			}
		}, 20L);
	}
}
