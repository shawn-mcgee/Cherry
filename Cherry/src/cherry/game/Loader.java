package cherry.game;

import java.util.LinkedList;
import java.util.List;

import blue.core.Debug;
import blue.game.Sprite;
import blue.util.Util;

public class Loader {
	public static final String
		SPRITE_INDEX = "sprites/index",
		TILE_INDEX   = "tiles/index",
		WALL_INDEX   = "walls/index";
	
	public static final void load() {
		load_sprites(SPRITE_INDEX);
		index_tiles(TILE_INDEX);
		index_walls(WALL_INDEX);
	}	
	
	public static final void load_sprites(String index) {
		Debug.log(Debug.INFO, Loader.class, "load_sprites '" + index + "'...");
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
	}
	
	public static final void index_tiles(String index) {
		Debug.log(Debug.INFO, Loader.class, "index_tiles '" + index + "'...");
		List<String> list = Util.parseFromFile(index, new LinkedList<String>());
		for(String string: list)
			Tile.grab_tile(string);
	}
	
	public static final void index_walls(String index) {
		Debug.log(Debug.INFO, Loader.class, "index_walls '" + index + "'...");
		List<String> list = Util.parseFromFile(index, new LinkedList<String>());
		for(String string: list)
			Tile.grab_wall(string);
	}
}
