package uk.co.cynicode.CyniCord.Listeners;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import uk.co.cynicode.CyniCord.CyniCord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PluginMessageListener implements Listener {
	
	private CyniCord plugin;
	
	public PluginMessageListener( CyniCord plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPluginEvent( PluginMessageEvent event ) {
		try {
			CyniCord.printDebug("Channel recieved : " + event.getTag());
			
			if (!event.getTag().equals("BungeeCord")) {
				CyniCord.printWarning("Listener was given message for channel " + event.getTag());
				return;
			}
			
			CyniCord.printDebug( "CyniChat message recieved" );
			
			DataInputStream in = new DataInputStream( new ByteArrayInputStream( event.getData() ) );
			String instruct = in.readUTF();
			String direct = in.readUTF();
			String subChannel = in.readUTF();
			
			CyniCord.printDebug( instruct );
			CyniCord.printDebug( direct );
			CyniCord.printDebug( subChannel );
			
			if( !subChannel.equals("CyniChat") && !subChannel.equals( "CyniCord" ) ){return;/*Not our problem*/}
			
			CyniCord.printDebug( "Next stage" );
			
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);
			
			CyniCord.printDebug( "We're still going..." );
			
			String servername = ( (Server) event.getSender()).getInfo().getName();
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(msgbytes));
			
			CyniCord.printDebug( "Input streams created..." );
			
			if ( subChannel.equals( "CyniChat" ) ) {
				
				CyniCord.printDebug( "CyniChat" );
				String one = dis.readUTF();
				String two = dis.readUTF();
				
				//EndpointType type = EndpointType.values()[dis.readInt()];
				String fancyPlayerName = dis.readUTF();
				String playerName = dis.readUTF();
				String chatChannel = dis.readUTF();
				String message = dis.readUTF();
				
				CyniCord.printDebug( "Details read..." );
				
				CyniCord.printDebug( "Fancy name : " + fancyPlayerName );
				CyniCord.printDebug( "Player name : " + playerName );
				CyniCord.printDebug( "Channel name : " + chatChannel );
				CyniCord.printDebug( "Message : " + message );
				
				CyniCord.sendMessage( chatChannel, playerName, message );
				
				CyniCord.printDebug( "Message sent..." );
				
			} else {
				
				CyniCord.printDebug( "CyniCord" );
				
				Map<String, String> ircToGameChans = new HashMap<String, String>();
				Map<String, String> ircChanAndPass = new HashMap<String, String>();
				
				while ( !dis.readUTF().equals( "END" ) ) {
					String info = dis.readUTF();
					
					String[] thisChan = info.split("~|~");
					
					ircToGameChans.put( thisChan[1], thisChan[0] );
					ircChanAndPass.put( thisChan[1], thisChan[2] );
					
				}
				
				CyniCord.PBot.addChannels( ircToGameChans, ircChanAndPass);
				
			}
			
		} catch ( IOException e ) {
			CyniCord.printWarning("PluginMessage error...");
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return;
	}
	
}
