package cherry.game;

import static cherry.game.Tile.localToPixel;

import java.awt.Color;

import blue.core.Renderable;
import blue.core.Updateable;
import blue.geom.Vector;
import blue.geom.Vector2;

public class Entity implements Renderable, Updateable {
	public static final int
		FULL_W = (int)Math.sqrt(Tile.FULL_W * Tile.FULL_W + Tile.FULL_W * Tile.FULL_W)    ,
		FULL_H = (int)Math.sqrt(Tile.FULL_W * Tile.FULL_W + Tile.FULL_W * Tile.FULL_W) / 2,
		HALF_W = FULL_W / 2,
		HALF_H = FULL_H / 2;
	public Room
		room;
	public Cell
		cell;
	
	public final Vector2.Mutable
		local = new Vector2.Mutable(),
		pixel = new Vector2.Mutable(),
		speed = new Vector2.Mutable();
	public float
		size = .5f;
	
	public float x() {
		return pixel.x();
	}
	
	public float y() {
		return pixel.y();
	}
	
	public int i() {
		return (int)local.x();
	}
	
	public int j() {
		return (int)local.y();
	}
	
	@Override
	public void onRender(RenderContext context) {
		context.stroke(2);
		context.color(Color.RED);
		context.oval(
				pixel.x() - size * HALF_W,
				pixel.y() - size * HALF_H,
				size * FULL_W ,
				size * FULL_H,
				false
				);
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		float
			dx = speed.x() * context.fixed_dt,
			dy = speed.y() * context.fixed_dt;
		int
			i = (int)(local.x() + dx + (dx != 0f ? dx > 0f ? size : - size: 0f)),
			j = (int)(local.y() + dy + (dy != 0f ? dy > 0f ? size : - size: 0f));
		Tile
			wall_dx = room.get_wall(i, j()),
			wall_dy = room.get_wall(i(), j);
		
		if(wall_dx != null)
			dx = i - local.x() + (dx <= 0 ? size + 1 : - size);
		if(wall_dy != null)
			dy = j - local.y() + (dy <= 0 ? size + 1 : - size);
		
		Vector.m_add(local, dx, dy);		
		pixel.set(localToPixel(local));		
	}
}
