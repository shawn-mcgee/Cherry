package cherry;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import blue.core.Engine;
import blue.game.Sprite;
import blue.geom.Vector;
import blue.util.Version;
import cherry.game.Editor;
import cherry.game.Tile;

public class Cherry {
	public static final Version
		VERSION = new Version("Cherry", 0, 0, 1);
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		Engine.getConfiguration().set(Engine.WINDOW_TITLE, VERSION);
		
		Engine.getConfiguration().set(Engine.CANVAS_FOREGROUND, Vector.fromColor4i(Color.BLACK));
		Engine.getConfiguration().set(Engine.CANVAS_BACKGROUND, Vector.fromColor4i(Color.BLACK));
		Engine.getConfiguration().set(Engine.WINDOW_DEVICE, 0);
		
		Engine.getConfiguration().set(Engine.DEBUG, false);
		
		System.out.println(VERSION);
		
		Engine.init();
		
		Tile.load("Debug", "debug.png");
		Engine.setScene(new Editor());
	}
	
	public static class Dither implements Sprite.Effect {		
		@Override
		public void filter(int[] frame_data, int frame_w, int frame_h) {
			for(int i = 0; i < frame_w; i ++)
				for(int j = 0; j < frame_h; j ++)
					if(((i & j) & 1) == 0)
						frame_data[frame_w * j + i] = 0;					
		}		
	}
}
