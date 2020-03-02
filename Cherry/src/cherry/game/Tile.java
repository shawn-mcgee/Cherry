package cherry.game;

import java.util.HashMap;
import java.util.Map;

import blue.game.Sprite;
import blue.geom.Vector;
import blue.geom.Vector2;
import cherry.Cherry.Dither;

public class Tile {
	private static final Dither
		DITHER = new Dither();
	public static final int
		FULL_W = 64,
		FULL_H = 32,
		HALF_W = FULL_W / 2,
		HALF_H = FULL_H / 2;
	
	public String
		name,
		path;
	public Sprite
		sprite;
	
	private Tile(String name, String path, Sprite sprite) {
		this.name = name;
		this.path = path;
		this.sprite = sprite;
	}
	
	private static final Map<String, Tile>
		NAME_INDEX = new HashMap<>(),
		PATH_INDEX = new HashMap<>();
	
	public static Tile getByName(String name) {
		Tile tile = NAME_INDEX.get(name);
		if(tile == null)
			throw new IllegalArgumentException("ERROR [cherry.game.Tile]: A Tile with name '" + name + "' does not exist.");
		return tile;
	}
	
	public static Tile getByPath(String path) {
		Tile tile = PATH_INDEX.get(path);
		if(tile == null)
			throw new IllegalArgumentException("ERROR [cherry.game.Tile]: A Tile with path '" + path + "' does not exist.");
		return tile;
	}
	
	public static Tile load(String name, String path) {
		if(NAME_INDEX.containsKey(name))
			throw new IllegalArgumentException("A Tile with name '" + name + "' already exists.");
		if(PATH_INDEX.containsKey(path))
			throw new IllegalArgumentException("A Tile with path '" + path + "' already exists.");
		Tile tile = new Tile(name, path, Sprite.load(name, path, FULL_W, FULL_H * 2));
		NAME_INDEX.put(name, tile);
		PATH_INDEX.put(path, tile);
		return tile;
	}	
	
	public static final Vector2 localToPixel(float i, float j) {
		return new Vector2(
				(i - j) * HALF_W,
				(i + j) * HALF_H
				);
	}
	
	public static final Vector2 pixelToLocal(float x, float y) {
		x /= FULL_W;
		y /= FULL_H;
		return new Vector2(
				(y + x),
				(y - x)
				);
	}	
	
	public static final Vector2 localToPixel(Vector ij) {
		return localToPixel(ij.x(), ij.y());
	}
	
	public static final Vector2 pixelToLocal(Vector xy) {
		return pixelToLocal(xy.x(), xy.y());
	}
}
