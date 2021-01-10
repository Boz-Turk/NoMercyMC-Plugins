package me.boz_turk.nmontime;

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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;



public class Events implements Listener {
	
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
	        	if (buttonID.substring(0, 5).contains("hDqCu")) {
	        		String[] pageID = buttonID.replace("§0Page:", "").split("\\|");
	        		ontimeMenu(p, Integer.valueOf(pageID[1])+1);	
	        		p.playSound(p.getLocation(), Sound.CLICK, 9000.0F, 1F);
	        	}
	        	if (buttonID.substring(0, 5).contains("qSnoJ")) {
	        		String[] pageID = buttonID.replace("§0Page:", "").split("\\|");
	        		ontimeMenu(p, Integer.valueOf(pageID[1])-1);
	        		p.playSound(p.getLocation(), Sound.CLICK, 9000.0F, 1F);
	        	}        	
        	}
        	
    	}
    }
	
	public static void ontimeMenu(Player player, int page) {
		Inventory inv = Bukkit.createInventory(null, 18, "§8[§a!§8]§7=§8[§a Ontimetop");
		ItemStack item1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
	    SkullMeta item1Meta = (SkullMeta) item1.getItemMeta();
		
		try {
			PreparedStatement playersql;
			playersql = Main.getConnection()
					.prepareStatement("SELECT * FROM " + Main.table + " order by Ontime desc LIMIT "+(page - 1) * 10 +", "+10*page);
			
			ResultSet playersqlresult = playersql.executeQuery();
			
			 if (page > 1) {
			    	ItemStack item5 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				    SkullMeta item5Meta = (SkullMeta) item5.getItemMeta();
			  		String url = "http://textures.minecraft.net/texture/37aee9a75bf0df7897183015cca0b2a7d755c63388ff01752d5f4419fc645";
			  		
			  		item5Meta.setDisplayName("§8[§aÖnceki Sayfa§8]");
			  		
				    item5Meta.setLore(Arrays.asList("§8»§7 Mevcut Sayfa: %pageid%".replace("%pageid%",""+page),"§0ID:qSnoJ|§0Page:"+page));
				    
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
						
						long n = playersqlresult.getLong("Ontime");
				        long nDay = n/86400;
				        long nHours = (n%86400)/3600;
				        long nMin = ((n%86400)%3600) /60;
				        long nSec =(((n%86400)%3600)%60);
				        
				        String ontime_sure = "";
						
						if (nDay > 0) {
							ontime_sure = ontime_sure + "§a"+nDay+" §7Gün ";
						}
						if (nHours > 0) {
							ontime_sure = ontime_sure + "§a"+nHours+" §7Saat ";
						}
						if (nMin > 0) {
							ontime_sure = ontime_sure + "§a"+nMin+" §7Dakika ";
						}
						if (nSec > 0) {
							ontime_sure = ontime_sure + "§a"+nSec+" §7Saniye ";
						}
						
						item1Meta.setOwner(playersqlresult.getString("Nick"));
			    	    item1Meta.setDisplayName("§8[§a"+ (i+1) +"§8]§7=§8[§a"+ playersqlresult.getString("Nick") +"§8]");
			    	    item1Meta.setLore(Arrays.asList("§7Ontime: "+ontime_sure));
			    	    item1.setItemMeta(item1Meta);
			    	    inv.setItem(reelslot, item1);
						
						n = 0;
				        nDay = 0;
				        nHours = 0;
				        nMin = 0;
				        nSec = 0;
				        
				        ontime_sure = null;
				        reelslot++;
					}
					else {
		    			
		    			ItemStack item6 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		    		    SkullMeta item6Meta = (SkullMeta) item6.getItemMeta();
		    	  		String url = "http://textures.minecraft.net/texture/682ad1b9cb4dd21259c0d75aa315ff389c3cef752be3949338164bac84a96e";
		    	  		
		    	  		item6Meta.setDisplayName("§8[§aSonraki Sayfa§8]");
		    	  		
		    		    item6Meta.setLore(Arrays.asList("§8»§7 Mevcut Sayfa: %pageid%".replace("%pageid%",""+page),"§0ID:hDqCu|§0Page:"+page));
		    		    
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
