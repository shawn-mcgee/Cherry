package cherry.game;

import java.util.Map;
import java.util.TreeMap;

import blue.game.Sprite;
import blue.geom.Vector;
import blue.geom.Vector2;

public class Tile {
	protected static final Map<String, Tile>
		TILE_INDEX = new TreeMap<>(),
		WALL_INDEX = new TreeMap<>();
	public static final int
		FULL_W = 128,
		FULL_H = 64,
		HALF_W = FULL_W / 2,
		HALF_H = FULL_H / 2;
	
	public final String
		string;
	public final Sprite
		sprite;
	
	private Tile(String string, Sprite sprite) {
		this.string = string;
		this.sprite = sprite;
	}
	
	public static final Tile load_as_tile(String string) {
		Tile tile = TILE_INDEX.get(string);
		if(tile == null)
			TILE_INDEX.put(string, tile = new Tile(string, Sprite.fromName(string, null)));
		return tile;
	}
	
	public static final Tile load_as_wall(String string) {
		Tile wall = WALL_INDEX.get(string);
		if(wall == null)
			WALL_INDEX.put(string, wall = new Tile(string, Sprite.fromName(string, null)));
		return wall;
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
