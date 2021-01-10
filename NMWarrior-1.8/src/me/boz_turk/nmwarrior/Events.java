package me.boz_turk.nmwarrior;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class Events implements Listener {
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent e) {
		if (e.getEntity().getKiller() instanceof Player && e.getEntity().getPlayer() instanceof Player) {
			Player victim = e.getEntity().getPlayer();
			Player attacker = e.getEntity().getKiller();
			
			if (attacker.getWorld().getName().equalsIgnoreCase("Orman") || attacker.getWorld().getName().equalsIgnoreCase("world_the_end") || attacker.getWorld().getName().equalsIgnoreCase("world_nether")){
				
				if (Main.dbdurum == true) {
					long ontime = me.boz_turk.nmontime.Main.getOntime(victim);
					if (ontime >= 18000) {
						try {
							PreparedStatement victimsql = Main.getConnection()
									.prepareStatement("SELECT * FROM " + Main.table + " WHERE Nick= '"+victim.getName()+"'");
							
							ResultSet victimsqlresult = victimsql.executeQuery();
							
							long victimpoint = 0;
						
							if (victimsqlresult.next()) {
								victimpoint = victimsqlresult.getLong("Point");
								
								PreparedStatement victimupdate = Main.getConnection()
										.prepareStatement("UPDATE "+ Main.table +" SET `Point`="+(victimpoint - (victimpoint/15))+" WHERE Nick = '"+victim.getName()+"'");
								victimupdate.executeUpdate();
								
							}
							PreparedStatement attackersql = Main.getConnection()
									.prepareStatement("SELECT * FROM " + Main.table + " WHERE Nick= '"+attacker.getName()+"'");
							
							ResultSet attackersqlresult = attackersql.executeQuery();
							
							if (attackersqlresult.next()) {
								if (victimpoint >= 90) {
									
									long lastpoint = attackersqlresult.getLong("Point") + (victimpoint/15);
								
									PreparedStatement attackerupdate = Main.getConnection()
											.prepareStatement("UPDATE "+ Main.table +" SET `Point`="+lastpoint+" WHERE Nick = '"+attacker.getName()+"'");
									attackerupdate.executeUpdate();
									
									
								}
							}
						} 
						catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
						
				}
			}
		}
	}
	
	
	@EventHandler
    public void onPlayerClickInventory(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
        if(e.getView().getTitle().contains("§8[§a!§8]§7=§8[§a")){
        	e.setCancelled(true);
        	if(e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) return;
        	ItemStack item = e.getCurrentItem();
        	if (item.getItemMeta().getLore() != null) {
	        	ArrayList<String> lorelist = (ArrayList<String>) item.getItemMeta().getLore();
	        	String buttonID = lorelist.get(lorelist.size() - 1).replace("§0ID:", "");
	        	if (buttonID.substring(0, 5).contains("eQp6t")) {
	        		String[] pageID = buttonID.replace("§0Page:", "").split("\\|");
	        		savasciMenu(p, Integer.valueOf(pageID[1])+1);	
	        		p.playSound(p.getLocation(), Sound.CLICK, 9000.0F, 1F);
	        	}
	        	if (buttonID.substring(0, 5).contains("M5EqG")) {
	        		String[] pageID = buttonID.replace("§0Page:", "").split("\\|");
	        		savasciMenu(p, Integer.valueOf(pageID[1])-1);	
	        		p.playSound(p.getLocation(), Sound.CLICK, 9000.0F, 1F);
	        	}
	        	
        	}
        	
    	}
    }
	
	
	public static void savasciMenu(Player player, int page) {
			
			Inventory inv = Bukkit.createInventory(null, 18, "§8[§a!§8]§7=§8[§a Savascitop");
		
		ItemStack item1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
	    SkullMeta item1Meta = (SkullMeta) item1.getItemMeta();
		
		
		try {
			PreparedStatement playersql;
			playersql = Main.getConnection()
					.prepareStatement("SELECT * FROM " + Main.table + " order by Point desc LIMIT "+(page - 1) * 10 +", "+10*page);
			
			ResultSet playersqlresult = playersql.executeQuery();
			
			 if (page > 1) {
			    	ItemStack item5 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				    SkullMeta item5Meta = (SkullMeta) item5.getItemMeta();
			  		String url = "http://textures.minecraft.net/texture/37aee9a75bf0df7897183015cca0b2a7d755c63388ff01752d5f4419fc645";
			  		
			  		item5Meta.setDisplayName("§8[§aÖnceki Sayfa§8]");
			  		
				    item5Meta.setLore(Arrays.asList("§8»§7 Mevcut Sayfa: %pageid%".replace("%pageid%",""+page),"§0ID:M5EqG|§0Page:"+page));
				    
			        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
			        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
			        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
			        Field profileField = null;
			        try
			        {
			            profileField = item5Meta.getClass().getDeclaredField("profile");
			            profileField.setAccessible(true);
			            profileField.set(item5Meta, profile);
			        }
			        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e)
			        {
			            e.printStackTrace();
			        }
			        item5.setItemMeta(item5Meta);
			        inv.setItem(16, item5);
			    }
			
			int reelslot = 0;
			 
			for(int i = (page-1)*9; i < 10*page; i++){
			  	
				if (playersqlresult.next()) {
					if (reelslot != 9) {
						
						long point = playersqlresult.getLong("Point");
						
						
						item1Meta.setOwner(playersqlresult.getString("Nick"));
			    	    item1Meta.setDisplayName("§8[§a"+ (i+1) +"§8]§7=§8[§a"+ playersqlresult.getString("Nick") +"§8]");
			    	    item1Meta.setLore(Arrays.asList("§7Puan: §a"+point));
			    	    item1.setItemMeta(item1Meta);
			    	    inv.setItem(reelslot, item1);
						
			    	    point = 0;
			    	    
				        reelslot++;
					}
					else {
		    			
		    			ItemStack item6 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		    		    SkullMeta item6Meta = (SkullMeta) item6.getItemMeta();
		    	  		String url = "http://textures.minecraft.net/texture/682ad1b9cb4dd21259c0d75aa315ff389c3cef752be3949338164bac84a96e";
		    	  		
		    	  		item6Meta.setDisplayName("§8[§aSonraki Sayfa§8]");
		    	  		
		    		    item6Meta.setLore(Arrays.asList("§8»§7 Mevcut Sayfa: %pageid%".replace("%pageid%",""+page),"§0ID:eQp6t|§0Page:"+page));
		    		    
		    	        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		    	        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
		    	        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
		    	        Field profileField = null;
		    	        try
		    	        {
		    	            profileField = item6Meta.getClass().getDeclaredField("profile");
		    	            profileField.setAccessible(true);
		    	            profileField.set(item6Meta, profile);
		    	        }
		    	        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e)
		    	        {
		    	            e.printStackTrace();
		    	        }
		    	        item6.setItemMeta(item6Meta);
		    	        inv.setItem(17, item6);
		    		}
				}
			
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	  
		player.closeInventory();
		player.openInventory(inv);
	
	}

}
