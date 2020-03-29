package cherry;

import blue.core.Engine;
import blue.util.Version;
import cherry.game.Dungeon;
import cherry.game.Loader;

public class Cherry {
	public static final Version
		VERSION = new Version("Cherry", 0, 1, 1);
	
	public static void main(String[] args) {	
		System.out.println(VERSION);
		
		Engine.loadConfiguration("engine.cfg");
		
		Engine.setProperty(Engine.WINDOW_TITLE, VERSION);
		Engine.setProperty(Engine.ENGINE_FPS, 0);
		Engine.setProperty(Engine.DEBUG, true);
		
		Loader.load();
		
		Engine.init();
		
		//Engine.setScene(new Editor());
		Engine.setScene(new Dungeon());
	}	
}
