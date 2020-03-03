package cherry;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import blue.core.Engine;
import blue.game.Sprite;
import blue.geom.Vector;
import blue.util.Util;
import blue.util.Version;
import cherry.game.Editor;
import cherry.game.Tile;

public class Cherry {
	public static final Version
		VERSION = new Version("Cherry", 0, 1, 0);
	
	public static void main(String[] args) {		
		System.out.println(VERSION);
		
		Engine.getConfiguration().load("engine.cfg");
		
		Engine.getConfiguration().set(Engine.CANVAS_FOREGROUND, Vector.fromColor4i(Color.BLACK));
		Engine.getConfiguration().set(Engine.CANVAS_BACKGROUND, Vector.fromColor4i(Color.BLACK));
		Engine.getConfiguration().set(Engine.WINDOW_TITLE, VERSION);
		Engine.getConfiguration().set(Engine.DEBUG, true);
		
		load_sprites("sprites/index");
		index_tiles("tiles/index");
		index_walls("walls/index");
		
		Engine.init();
		
		Engine.setScene(new Editor());
	}
	
	public static final void load_sprites(String index) {
		System.out.print("[load_sprites '" + index + "']...");
		List<String> args0 = Util.parseFromFile(index, new LinkedList<String>());
		for(String arg0: args0) {
			if(!arg0.trim().isEmpty()) {
				String[] args1 = arg0.split("\\,");
				String
					name = null,
					path = null;
				int
					w = 0,
					h = 0;
				
				for(String arg1: args1) {
					String[] args2 = arg1.split("\\=");
					if(args2.length > 1) {
						String
							var = args2[0].trim(),
							val = args2[1].trim();
						switch(var) {
							case "name": name = val; break;
							case "path": path = val; break;
							case "w": w = Util.stringToInt(val); break;
							case "h": h = Util.stringToInt(val); break;
						}					
					}				
				}
				
				Sprite.load(name, path, w, h);
			}
		}
		System.out.println("done");
	}
	
	public static final void index_tiles(String index) {
		System.out.print("[index_tiles '" + index + "']...");
		List<String> list = Util.parseFromFile(index, new LinkedList<String>());
		for(String string: list)
			Tile.load_tile(string);
		System.out.println("done");
	}
	
	public static final void index_walls(String index) {
		System.out.print("[index_walls '" + index + "']...");
		List<String> list = Util.parseFromFile(index, new LinkedList<String>());
		for(String string: list)
			Tile.load_wall(string);
		System.out.println("done");
	}
}
