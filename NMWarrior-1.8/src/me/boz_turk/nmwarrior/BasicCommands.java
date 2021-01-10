package me.boz_turk.nmwarrior;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


public class BasicCommands implements CommandExecutor {
	
	@SuppressWarnings("unused")
	private Main plugin;
  
	public BasicCommands(Main pl) { this.plugin = pl; }

	static String error_prefix = Main.error_prefix;
	static String info_prefix = Main.info_prefix;

	public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("savasci")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (Main.dbdurum == true) {
						try {
							PreparedStatement value = Main.getConnection()
									.prepareStatement("SELECT * FROM "+ Main.table +" WHERE Nick = '"+p.getName()+"'");
							ResultSet results = value.executeQuery();
							if (results.next()) {
								sender.sendMessage("§8[§eBilgi§8]§7=§8[ §7Savasci puanınız: §e"+results.getLong("Point"));	
						    }
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else {
						sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Veritabanı sorunu oluştu lütfen yetkililere bildiriniz");
					}
				}
				else {
					sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Bu komutu sadece oyuncular kullanabilir");
				}
			}
			else if (args.length == 1) {
				if (Main.dbdurum == true) {
					try {
						PreparedStatement value = Main.getConnection()
								.prepareStatement("SELECT * FROM "+ Main.table +" WHERE Nick = '"+args[0]+"'");
						ResultSet results = value.executeQuery();
						if (results.next()) {
							sender.sendMessage("§8[§eBilgi§8]§7=§8[ §e"+args[0]+"§7'nin savasci puani: §e"+results.getLong("Point"));	
					    }
						else {
							sender.sendMessage("§8[§eBilgi§8]§7=§8[ §7Herhangi bir kayıt bulunamadı");	
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Veritabanı sorunu oluştu lütfen yetkililere bildiriniz");
				}
			}
			else {
				sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Kullanım: §c/savasci [<nick>]");
			} 
		}
		else if (cmd.getName().equalsIgnoreCase("savascitop")) {
			if (args.length == 0) {
				if (Main.dbdurum == true) {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						Events.savasciMenu(p, 1);
					}
					else {
						sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Bu komutu sadece oyuncular kullanabilir");
					}
				}
				else {
					sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Veritabanı sorunu oluştu lütfen yetkililere bildiriniz");
				}
			}
			else {
				sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Kullanım: §c/savascitop");
			} 
		}
		
		else if (cmd.getName().equalsIgnoreCase("savascistand")) { 
			if (sender.hasPermission("batcomputer.adminsohbet")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (args.length == 1) {
						  File configfile = new File("plugins/NMWarrior/config.yml");
				          FileConfiguration configdata = YamlConfiguration.loadConfiguration(configfile);
						  
						  String locid = p.getLocation().getWorld().getName()+"|"+p.getLocation().getX()+"|"+p.getLocation().getY()+"|"+p.getLocation().getZ()+"|"+p.getLocation().getYaw()+"|"+p.getLocation().getPitch();
						  configdata.set("stand_"+args[0], locid);
						  try {
							configdata.save(configfile);
							p.sendMessage(info_prefix+"Armorstand işaretlendi");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					  }
					  else {
						  p.sendMessage(error_prefix+"Kullanım: §c/savascistand <1,2,3>");
					  }
				}
				else {
					sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Bu komutu sadece oyuncular kullanabilir");
				} 
			}
			else {
				sender.sendMessage(error_prefix+"Bu komutu için gerekli yetkiye sahip değilsin");
			}
		  }
	    return true;
	  }
}
