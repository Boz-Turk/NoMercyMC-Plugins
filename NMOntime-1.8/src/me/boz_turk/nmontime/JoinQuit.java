package me.boz_turk.nmontime;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class JoinQuit
  implements Listener
{

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    
    if (Main.dbdurum == true) {

        try {
    		PreparedStatement value = Main.getConnection()
    		.prepareStatement("SELECT * FROM "+ Main.table +" WHERE Nick = '"+player.getName()+"'");
    		
    		ResultSet results = value.executeQuery();
    		
    		if (results.next()) {
    			PreparedStatement update = Main.getConnection()
    			.prepareStatement("UPDATE "+ Main.table +" SET `Lastlogin`="+Main.unixtrans(new Date())+" WHERE Nick = '"+player.getName()+"'");
    			
    			update.executeUpdate();
    	    }
    		else {
    			PreparedStatement insert = Main.getConnection()
    			.prepareStatement("INSERT INTO " + Main.table + "(ID, Nick, Ontime, Lastlogin, Lastquit) VALUE (null,?,?,?,?)");
    			insert.setString(1, player.getName()); //Nick
    			insert.setLong(2, 0); //ontime
    			insert.setLong(3, Main.unixtrans(new Date())); //lastjoin
    			insert.setLong(4, 0); //lastquit
    			
    			insert.executeUpdate();
    		}
    	} catch (SQLException e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    	}
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    if (Main.dbdurum == true) {
	    try {
	    	PreparedStatement playersql = Main.getConnection()
			.prepareStatement("SELECT * FROM " + Main.table + " WHERE Nick= '"+player.getName()+"'");
			ResultSet playersqlresult = playersql.executeQuery();
			if (playersqlresult.next()) {
				
				long lj = playersqlresult.getLong("Lastlogin"); //Giriş Zamanı
				long lq = Main.unixtrans(new Date()); //Çıkış zamanı
				long ontime = playersqlresult.getLong("Ontime") + (lq-lj); //Ontime
				
				PreparedStatement update = Main.getConnection()
				.prepareStatement("UPDATE "+ Main.table +" SET Ontime=?, Lastquit=? WHERE Nick=?");
		
				update.setLong(1, ontime); //Ontime
				update.setLong(2, lq); //Lastquit
				update.setString(3, player.getName()); //Lastquit
				
				update.executeUpdate();
			}
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
    }
    
  }
}
