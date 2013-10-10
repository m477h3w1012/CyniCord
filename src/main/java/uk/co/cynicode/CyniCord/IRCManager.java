package uk.co.cynicode.CyniCord;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uk.co.cynicode.CyniCord.Listeners.IRCChatListener;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;

/**
 * An instantiation of a PircBotX bot
 *
 * @author Matthew Ball
 *
 */
public class IRCManager {

    private PircBotX bot;
    
    private String hostname;
    private String nickname;
    private int port;

    /**
     * Constructor for making a new Bot out of barely anything
     *
     * @param plugin : Used for getting the config options
     * @throws Exception : So much that could go wrong here...
     */
    public IRCManager(CyniCord plugin) throws Exception {

    	port = plugin.getConfig().getInt("CyniCord.irc.port");
    	hostname = plugin.getConfig().getString("CyniCord.irc.hostname");
    	nickname = plugin.getConfig().getString("CyniCord.irc.nickname");
    	
    	CyniCord.printDebug( nickname + "@" + hostname + ":" + port);
    	
        this.bot = new PircBotX();

        CyniCord.printDebug( "1" );
        this.bot.getListenerManager().addListener(new IRCChatListener());
        CyniCord.printDebug( "2" );
        this.bot.setName( nickname );
        this.bot.setLogin("CyniBot");
        try {
        	CyniCord.printDebug( "3" );
            this.bot.connect( hostname, port );
            CyniCord.printDebug( "4" );
            CyniCord.printInfo( "Connected " + nickname + " to IRC server: " + hostname );
            CyniCord.printDebug( "5" );
        } catch (Exception e) {
        	CyniCord.printSevere( "IRC connection has failed..." );
            throw e;
        }

    }

    /**
     * Restart the IRC component of the plugin via various means
     */
    public void restart() {
        CyniCord.printWarning("Restarting the IRC bot...");
        CyniCord.printInfo("Stopping the IRC bot...");
        bot.shutdown();
        CyniCord.printInfo("Starting up the IRC bot again...");
        try {
            bot.reconnect();
            //loadChannels(DataManager.returnAllChannels());
            CyniCord.printInfo("Reconnected successfully");
        } catch (IOException e) {
            CyniCord.printSevere("We could not connect...");
            e.printStackTrace();
        } catch (NickAlreadyInUseException e) {
            CyniCord.printSevere("Our nickname was already in use...");
            e.printStackTrace();
        } catch (IrcException e) {
            CyniCord.printSevere("IRC has failed... call in the drones...");
            e.printStackTrace();
        }
        return;
    }

    /**
     * Kill the IRC component of the plugin ungracefully ... In other words,
     * knock it over the head with a bat.
     */
    public void stop() {
        CyniCord.printInfo("Shutting down IRC...");
        this.bot.shutdown(true);
        CyniCord.printInfo("IRC has been killed.");
    }
}