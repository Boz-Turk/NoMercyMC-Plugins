package me.boz_turk.nmwarrior;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class Main extends JavaPlugin implements Listener {
	
  static Plugin plugin;
  public String prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix"));
  public static String error_prefix = "§8[§cHATA§8]§7=§8[ §7";
  public static String info_prefix = "§8[§eBilgi§8]§7=§8[ §7";
  
  
  File ipData = new File(this.getDataFolder() + File.separator + "ipData.yml");
  File OntimeFile = new File(this.getDataFolder() + File.separator + "ontime" + ".yml");
  
  private static Connection connection;
  public String host, database, username, password;
  public static String table;
  public int port;
  public static boolean dbdurum = false; 
  
  public void onEnable() {
	plugin = this;

    Bukkit.getServer().getPluginManager().registerEvents(this, this);
    if (!(new File(getDataFolder(), "config.yml")).exists()) {
      saveDefaultConfig();
    }
	
    table = getConfig().getString("db_table");
    
    getServer().getConsoleSender().sendMessage("§8[§aNM-Warrior§8]§7=§8[ §aPlugin Aktif!");
    getCommand("savasci").setExecutor(new BasicCommands(this));
    getCommand("savascitop").setExecutor(new BasicCommands(this));
    getCommand("savascistand").setExecutor(new BasicCommands(this));
    
    mysqlSetup(1);
    
    autoTimer();
    registerEvents();
    standRefresh(); 
  }


  
  public void onDisable() { 
	  getServer().getConsoleSender().sendMessage("§8[§aNM-Warrior§8]§7=§8[ §cPlugin Kapali!"); 
  }


  
  public void registerEvents() {
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(new JoinQuit(), this);
    pm.registerEvents(new Events(), this);
  }
 
  
  public void autoTimer() {
	  Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
          public void run()
          {
            Date now = new Date();
            SimpleDateFormat formathourmin = new SimpleDateFormat("mm");
            int timemin = Integer.valueOf(formathourmin.format(now)).intValue();
 	        if (timemin%5==0) {
 	        	standRefresh();
 	        	getServer().getConsoleSender().sendMessage("§8[§eNM-Warrior§8]§7=§8[ §eStandlar yenilendi");
 	        	mysqlSetup(1);
 	        }
          }
        }, 0L, 1200L); //20Tick 1 Saniye yapar
  }
  
 
  public static void standRefresh() {
	  File configfile = new File("plugins/NMWarrior/config.yml");
      FileConfiguration configdata = YamlConfiguration.loadConfiguration(configfile);
      
      
		try {
				PreparedStatement playersql;
				playersql = Main.getConnection()
						.prepareStatement("SELECT * FROM "+table+" ORDER BY Point DESC LIMIT 3");
				ResultSet playersqlresult = playersql.executeQuery();
				
				for(int i=1; i <= 3; i++){
					if (playersqlresult.next()) {
						if (!String.valueOf(configdata.get("stand_"+i)).equalsIgnoreCase("NONE")) {
							String[] str = String.valueOf(configdata.get("stand_"+i)).split("\\|");
							Location loc = new Location(Bukkit.getWorld(str[0]), Double.valueOf(str[1]), Double.valueOf(str[2]), Double.valueOf(str[3]), Float.valueOf(str[4]), Float.valueOf(str[5]));	
							
							ItemStack item1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
						    SkullMeta item1Meta = (SkullMeta) item1.getItemMeta();
							
							for(Entity e : loc.getChunk().getEntities()){
					            if(loc.distanceSquared(e.getLocation()) < 1 ){
					            	if(e instanceof ArmorStand) {
					            		
					            	  item1Meta.setOwner(playersqlresult.getString("Nick"));
					            	  item1.setItemMeta(item1Meta);
					            	  
									  e.setCustomName("§8[§a"+i+"§8]§7=§8[§7"+playersqlresult.getString("Nick")+"§8]");
									  e.setCustomNameVisible(true);
									  ((ArmorStand) e).setHelmet(item1);
					            	}
					            }
					        }
						}
					}
					else {
						if (!String.valueOf(configdata.get("stand_"+i)).equalsIgnoreCase("NONE")) {
							String[] str = String.valueOf(configdata.get("stand_"+i)).split("\\|");
							Location loc = new Location(Bukkit.getWorld(str[0]), Double.valueOf(str[1]), Double.valueOf(str[2]), Double.valueOf(str[3]), Float.valueOf(str[4]), Float.valueOf(str[5]));	
							
							ItemStack item1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
						    SkullMeta item1Meta = (SkullMeta) item1.getItemMeta();
							
							for(Entity e : loc.getChunk().getEntities()){
					            if(loc.distanceSquared(e.getLocation()) < 1 ){
					            	if(e instanceof ArmorStand) {
					        	  	  String url = "https://textures.minecraft.net/texture/cd44f602f7cf987076baa7a6e5a70516d3865eca38a61cc912c4af5f181ec6c1";    	  
					            	  GameProfile profile = new GameProfile(UUID.randomUUID(), null);
				        	          byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
				        	          profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
				        	          Field profileField = null;
				        	          try
				        	          {
				        	            profileField = item1Meta.getClass().getDeclaredField("profile");
				        	            profileField.setAccessible(true);
				        	            profileField.set(item1Meta, profile);
				        	          }
				        	          catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1)
				        	          {
				        	            e1.printStackTrace();
				        	          }
					            	  
					            	  
					            	  
					            	  item1.setItemMeta(item1Meta);
					            	  
									  e.setCustomName("§8[§a"+i+"§8]§7=§8[§7 NMSavasci §8]");
									  e.setCustomNameVisible(true);
									  ((ArmorStand) e).setHelmet(item1);
					            	}
					            }
					        }
						}
					}
				}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
  
  
  public void tableCreate() {
		
		try {		
	        PreparedStatement insert = getConnection()
					.prepareStatement("CREATE TABLE IF NOT EXISTS `" + table + "` ( `ID` INT(11) NOT NULL AUTO_INCREMENT , `Nick` TEXT NOT NULL , `Point` BIGINT , PRIMARY KEY (`ID`))");
			insert.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
  
  public void mysqlSetup(int tekrar) {
		host = getConfig().getString("db_host");
		port = getConfig().getInt("db_port");
		database = getConfig().getString("db_database");
		username = getConfig().getString("db_username");
		password = getConfig().getString("db_password");

		try {
			synchronized (this) {	
				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" 
				+ this.port + "/" + this.database + "?useUnicode=true&characterEncoding=UTF-8", this.username, this.password));
				getServer().getConsoleSender().sendMessage("§8[§aNM-Warrior§8]§7=§8[ §aMYSQL Baglantisi basarili!");
				tableCreate();
				dbdurum = true;
			}
		}catch(SQLException | ClassNotFoundException e) {
			
			if (tekrar < 5) {
				getServer().getConsoleSender().sendMessage("§8[§aNM-Warrior§8]§7=§8[ §eMYSQL Baglantisi basarisiz tekrar deneniyor..");
				Timer myTimer=new Timer();
	            TimerTask gorev =new TimerTask() {
	            	int sayac = 0;
	                @Override
	                public void run() {
	                	if (sayac == 1){
	                    	mysqlSetup((tekrar+1));
	                    	myTimer.cancel();
	                	}
	                	else {
	                		sayac++;
	                	}
	                }
	            };
	            myTimer.schedule(gorev, 0, 5000);
			}
			else {
				getServer().getConsoleSender().sendMessage("§8[§aNM-Warrior§8]§7=§8[ §cMYSQL Baglantisi basarisiz!");
				dbdurum = false;
			}
		}
	}
	
	
	public static Connection getConnection() {
		return connection;
	}
	@SuppressWarnings("static-access")
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
