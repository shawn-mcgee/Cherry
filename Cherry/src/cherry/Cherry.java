package cherry;

import blue.core.Engine;
import blue.core.Stage;
import blue.util.Version;
import cherry.game.Editor;
import cherry.game.Loader;

public class Cherry {
	public static final Version
		VERSION = new Version("Cherry", 0, 1, 3);
	
	public static void main(String[] args) {	
		System.out.println(VERSION);
		
		Stage.loadConfiguration("engine.cfg");
		
		Stage.setProperty(Stage.WINDOW_TITLE, VERSION);
		Stage.setProperty(Stage.THREAD_FPS  , 0      );
		Stage.setProperty(Stage.DEBUG       , Stage.METRICS);
		
		
		Loader.load();
		
		Engine.init();
		
		Stage.setScene(new Editor());
		//Stage.setScene(new Dungeon());
	}	
}
