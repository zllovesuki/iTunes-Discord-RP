package com.github.kevinmussi.itunesrp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.kevinmussi.itunesrp.core.DiscordHelper;
import com.github.kevinmussi.itunesrp.core.ScriptDiscordBridge;
import com.github.kevinmussi.itunesrp.core.ScriptHelper;
import com.github.kevinmussi.itunesrp.data.OperativeSystem;

public final class Main {
	
	private static final Logger LOGGER =
			Logger.getLogger(Main.class.getName() + "Logger");
	
	public static final OperativeSystem OS = OperativeSystem.getOS();
	
	private Main() {
		super();
	}
	
	public static void main(String[] args) throws InterruptedException {
		LOGGER.log(Level.INFO, "Application started running.");
		
		// Get the OperativeSystem object
		if(OS == OperativeSystem.OTHER) {
			LOGGER.log(Level.SEVERE, "Your operative system is not supported!");
			return;
		}
                
        // Change the encoding to use emojis on Windows
        if(OS == OperativeSystem.WINDOWS) {
        	System.setProperty("file.encoding", "UTF-8");
	        try {
	            java.lang.reflect.Field charset = null;
	            charset = java.nio.charset.Charset.class.getDeclaredField("defaultCharset");
	            charset.setAccessible(true);
	            charset.set(null, null);
	        } catch (NoSuchFieldException|IllegalAccessException e) {
	            LOGGER.log(Level.SEVERE, "An error occurred: ", e);
	            return;
	        }
        }
                
		// Create the ScriptHelper
		ScriptHelper scriptHelper;
		try {
			scriptHelper = new ScriptHelper(OS);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Something went wrong: ", e);
			return;
		}
		
		// Create the ScriptDiscordBridge
		ScriptDiscordBridge bridge = new ScriptDiscordBridge();
		
		// The bridge observes the script helper to receive updates
		// about the songs playing (in form of a String object).
		scriptHelper.addObserver(bridge);
		
		// Create the DiscordHelper passing the MainFrame to it
		DiscordHelper discordHelper = new DiscordHelper();
		
		// The Discord helper observes the bridge to receive updates
		// about the songs playing (in form of a Track object).
		bridge.addObserver(discordHelper);
		
		// The script helper observes the Discord helper to be notified
		// when the script must be executed or stopped.
		discordHelper.setCommanded(scriptHelper);

		Runtime.getRuntime().addShutdownHook(new Thread(discordHelper::disconnect));

		discordHelper.connect();

	}
	
}
