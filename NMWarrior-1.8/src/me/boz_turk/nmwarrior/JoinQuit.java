package me.boz_turk.nmwarrior;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class JoinQuit implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    
    if (Main.dbdurum == true) {	
        
        try {
    		PreparedStatement value = Main.getConnection()
    		.prepareStatement("SELECT * FROM "+ Main.table +" WHERE Nick = '"+player.getName()+"'");
    		
    		ResultSet results = value.executeQuery();
    		
    		if (!results.next()) {
    			PreparedStatement insert = Main.getConnection()
    			.prepareStatement("INSERT INTO " + Main.table + "(ID, Nick, Point) VALUE (null,?,?)");
    			insert.setString(1, player.getName()); //Nick
    			insert.setLong(2, 100); //point
    			
    			insert.executeUpdate();
    		}
    	} catch (SQLException e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    	}
    }
    
  }

}
