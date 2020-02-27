package cherry;

import java.awt.Color;

import blue.core.Engine;
import blue.geom.Vector;
import blue.util.Version;
import cherry.game.Game;

public class Cherry {
	public static final Version
		VERSION = new Version("Cherry", 0, 0, 1);
	
	public static void main(String[] args) {
		Engine.getConfiguration().set(Engine.WINDOW_TITLE, VERSION);
		
		Engine.getConfiguration().set(Engine.CANVAS_FOREGROUND, Vector.fromColor4i(Color.BLACK));
		Engine.getConfiguration().set(Engine.CANVAS_BACKGROUND, Vector.fromColor4i(Color.BLACK));
		Engine.getConfiguration().set(Engine.WINDOW_DEVICE, 1);
		
		Engine.getConfiguration().set(Engine.DEBUG, true);
		
		System.out.println(VERSION);
		
		Engine.init();
		
		Engine.setScene(new Game());
	}
}
