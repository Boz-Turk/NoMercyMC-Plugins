package me.boz_turk.nmontime;

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
		if (cmd.getName().equalsIgnoreCase("ontime")) {
			if (args.length == 0) {
				
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (Main.dbdurum == true) {
						try {
							PreparedStatement value = Main.getConnection()
									.prepareStatement("SELECT * FROM "+ Main.table +" WHERE Nick = '"+p.getName()+"'");
							
							ResultSet results = value.executeQuery();
							
							if (results.next()) {
								long n = results.getLong("Ontime");
						        long nDay = n/86400;
						        long nHours = (n%86400)/3600;
						        long nMin = ((n%86400)%3600) /60;
						        long nSec =(((n%86400)%3600)%60);
						        
						        String ontime_sure = "";
								
								if (nDay > 0) {
									ontime_sure = ontime_sure + "§e"+nDay+" §7Gün ";
								}
								if (nHours > 0) {
									ontime_sure = ontime_sure + "§e"+nHours+" §7Saat ";
								}
								if (nMin > 0) {
									ontime_sure = ontime_sure + "§e"+nMin+" §7Dakika ";
								}
								if (nSec > 0) {
									ontime_sure = ontime_sure + "§e"+nSec+" §7Saniye ";
								}
								
								if (!ontime_sure.equalsIgnoreCase("")) {
									sender.sendMessage("§8[§eBilgi§8]§7=§8[ §7Oyunda kalma süreniz: "+ontime_sure);
								}
								else {
									sender.sendMessage("§8[§eBilgi§8]§7=§8[ §7Henüz zaman kaydedilmemiş lütfen daha sonra deneyiniz");
								}
								
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
							
							long n = results.getLong("Ontime");
					        long nDay = n/86400;
					        long nHours = (n%86400)/3600;
					        long nMin = ((n%86400)%3600) /60;
					        long nSec =(((n%86400)%3600)%60);
					        
					        String ontime_sure = "";
							
							if (nDay > 0) {
								ontime_sure = ontime_sure + "§e"+nDay+" §7Gün ";
							}
							if (nHours > 0) {
								ontime_sure = ontime_sure + "§e"+nHours+" §7Saat ";
							}
							if (nMin > 0) {
								ontime_sure = ontime_sure + "§e"+nMin+" §7Dakika ";
							}
							if (nSec > 0) {
								ontime_sure = ontime_sure + "§e"+nSec+" §7Saniye ";
							}
							
							if (!ontime_sure.equalsIgnoreCase("")) {
								sender.sendMessage("§8[§eBilgi§8]§7=§8[ §e"+args[0]+ "§7'in oyun süresi: "+ontime_sure);
							}
							else {
								sender.sendMessage("§8[§eBilgi§8]§7=§8[ §7Henüz zaman kaydedilmemiş lütfen daha sonra deneyiniz");
							}
							
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
				sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Kullanım: §c/ontimetop");
			} 
		}
		else if (cmd.getName().equalsIgnoreCase("ontimever")) {
			if (sender.hasPermission("*")) {
				if (args.length == 2) {
					if (Main.dbdurum == true) {
						try {
							PreparedStatement playersql = Main.getConnection().prepareStatement("SELECT * FROM " + Main.table + " WHERE Nick= '"+args[0]+"'");
							ResultSet playersqlresult = playersql.executeQuery();
							if (playersqlresult.next()) {
								PreparedStatement update = Main.getConnection().prepareStatement("UPDATE "+ Main.table +" SET Ontime=? WHERE Nick=?");
								
								update.setLong(1, playersqlresult.getLong("Ontime")+Long.valueOf(args[1])); //Ontime
								update.setString(2, args[0]); //Nick
								
								update.executeUpdate();
							}
							else {
								PreparedStatement insert = Main.getConnection()
								.prepareStatement("INSERT INTO " + Main.table + "(ID, Nick, Ontime) VALUE (null,?,?)");
								insert.setString(1, args[0]); //Nick
								insert.setLong(2, Long.valueOf(args[1])); //ontime
								
								insert.executeUpdate();
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else {
						sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Veritabanı sorunu oluştu!");
					}
					
				}
				else {
					sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Kullanım: §c/ontimever <oyuncu> <saniye>");
				} 
			}	
		}
		
		else if (cmd.getName().equalsIgnoreCase("ontimetop")) {
			if (args.length == 0) {
				if (sender instanceof Player) {
					if (Main.dbdurum == true) {
						Player p = (Player) sender;
						Events.ontimeMenu(p, 1);
					}
					else {
						sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Veritabanı sorunu oluştu lütfen yetkililere bildiriniz");
					}
				}
				else {
					sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Bu komutu sadece oyuncular kullanabilir");
				}
				
			}
			else {
				sender.sendMessage("§8[§cHATA§8]§7=§8[ §7Kullanım: §c/ontimetop");
			} 
		}
		
		else if (cmd.getName().equalsIgnoreCase("ontimestand")) {
			if (sender.hasPermission("batcomputer.adminsohbet")) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					if (args.length == 1) {
						  File configfile = new File("plugins/NMOntime/config.yml");
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
						  p.sendMessage(error_prefix+"Kullanım: §c/ontimestand <1,2,3>");
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
