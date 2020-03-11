package cherry.game;

import static cherry.game.Tile.localToPixel;

import java.awt.Color;

import blue.core.Renderable;
import blue.core.Updateable;
import blue.geom.Vector;
import blue.geom.Vector2;
import blue.util.Copyable;
import blue.util.Util;

public class Entity implements Renderable, Updateable {
	protected static final float
		SIN = (float)(Math.sin(Math.toRadians(45))),
		COS = (float)(Math.cos(Math.toRadians(45)));
	protected static final Vector2
		NORTH = new Vector2(0, -1),
		WEST  = new Vector2(-1, 0),
		SOUTH = new Vector2(0,  1),
		EAST  = new Vector2( 1, 0),		
		NORTH_WEST = new Vector2(-COS, -SIN),
		SOUTH_WEST = new Vector2(-COS,  SIN),
		SOUTH_EAST = new Vector2( COS,  SIN),
		NORTH_EAST = new Vector2( COS, -SIN);
	
	public Room
		room;
	public Cell
		cell;
	
	public final Property[]
		property;
	
	public final Vector2.Mutable
		local = new Vector2.Mutable(),
		pixel = new Vector2.Mutable(),
		facing = new Vector2.Mutable();
	
	public Entity() {
		property = new Property[Property.COUNT];
		for(int i = 0; i < Property.COUNT; i ++)
			property[i] = Property.DEFAULT[i].copy();
	}
	
	@Override
	public void onRender(RenderContext context) {
		context.stroke(2);
		context.color(Color.RED);	
		
		float size = property[Property.SIZE].total();		
		Vector2
			a = localToPixel(local.x() - size, local.y() - size),
			b = localToPixel(local.x() + size, local.y() - size),
			c = localToPixel(local.x() + size, local.y() + size),
			d = localToPixel(local.x() - size, local.y() + size);
		
		context.line(a, b);
		context.line(b, c);
		context.line(c, d);
		context.line(d, a);
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		float
			speed = property[Property.MOVEMENT_SPEED].total(),
			dx = speed * facing.x() * context.fixed_dt,
			dy = speed * facing.y() * context.fixed_dt,
			size = property[Property.SIZE].total();
		Tile
			tile_x0 = null,
			tile_x1 = null,
			tile_y0 = null,
			tile_y1 = null,
			wall_x0 = null,
			wall_x1 = null,
			wall_y0 = null,
			wall_y1 = null;		
		int
			a = (int)(local.x() + dx + Util.sign(dx) * size),
			b = (int)(local.y() + dy + Util.sign(dy) * size);
		
		if (dx < 0) {
			tile_x0 = room.get_tile((int)(local.x() + dx - size), (int)(local.y() - size));
			tile_x1 = room.get_tile((int)(local.x() + dx - size), (int)(local.y() + size));
			wall_x0 = room.get_wall((int)(local.x() + dx - size), (int)(local.y() - size));
			wall_x1 = room.get_wall((int)(local.x() + dx - size), (int)(local.y() + size));		
			if(tile_x0 == null || tile_x1 == null || wall_x0 != null || wall_x1 != null)
				dx = (int)(a + 1 - local.x() + size);
		} else if (dx > 0) {
			tile_x0 = room.get_tile((int)(local.x() + dx + size), (int)(local.y() - size));
			tile_x1 = room.get_tile((int)(local.x() + dx + size), (int)(local.y() + size));
			wall_x0 = room.get_wall((int)(local.x() + dx + size), (int)(local.y() - size));
			wall_x1 = room.get_wall((int)(local.x() + dx + size), (int)(local.y() + size));
			if(tile_x0 == null || tile_x1 == null || wall_x0 != null || wall_x1 != null)
				dx = (int)(a - local.x() - size);
		}
		
		if (dy < 0) {
			tile_y0 = room.get_tile((int)(local.x() - size), (int)(local.y() + dy - size));
			tile_y1 = room.get_tile((int)(local.x() + size), (int)(local.y() + dy - size));
			wall_y0 = room.get_wall((int)(local.x() - size), (int)(local.y() + dy - size));
			wall_y1 = room.get_wall((int)(local.x() + size), (int)(local.y() + dy - size));
			if(tile_y0 == null || tile_y1 == null || wall_y0 != null || wall_y1 != null)
				dy = (int)(b + 1 - local.y() + size);
		} else if (dy > 0) {
			tile_y0 = room.get_tile((int)(local.x() - size), (int)(local.y() + dy + size));
			tile_y1 = room.get_tile((int)(local.x() + size), (int)(local.y() + dy + size));
			wall_y0 = room.get_wall((int)(local.x() - size), (int)(local.y() + dy + size));
			wall_y1 = room.get_wall((int)(local.x() + size), (int)(local.y() + dy + size));
			if(tile_y0 == null || tile_y1 == null || wall_y0 != null || wall_y1 != null)
				dy = (int)(b - local.y() - size);
		}
		
		Vector.m_add(local, dx, dy);
		pixel.set(localToPixel(local));
	}
	
	public static int snap(float x) {
		return (int)(x - x % 1f);
	}
	
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
	
	public static class Property implements Copyable<Property> {
		public static final int
			MAXIMUM_HEALTH = 0,
			CURRENT_HEALTH = 1,
			MOVEMENT_SPEED = 2,
			SIZE           = 3,
			COUNT = 4;
		public static final Property
			DEFAULT_MAXIMUM_HEALTH = new Property(   1, 0, 0,   1),
			DEFAULT_CURRENT_HEALTH = new Property(   1, 0, 0,   1),
			DEFAULT_MOVEMENT_SPEED = new Property(   2, 0, 0,   6),
			DEFAULT_SIZE           = new Property(.25f, 0, 0, .5f);
		public static final Property[]
			DEFAULT = {
				DEFAULT_MAXIMUM_HEALTH,
				DEFAULT_CURRENT_HEALTH,
				DEFAULT_MOVEMENT_SPEED,
				DEFAULT_SIZE
			};		
		private float
			value,
			delta,
			total,
			min,
			max;
		
		public Property(Property p) {
			this(p.value, p.delta, p.min, p.max);
		}
		
		public Property(float value, float delta, float min, float max) {
			this.value = value;
			this.delta = delta;
			this.min = min;
			this.max = max;
			
			this.total = Util.clamp(value + delta, min, max);
		}
		
		public float value(float value) {			
			return this.total = Util.clamp((this.value = value) + this.delta, this.min, this.max);			
		}
		
		public float delta(float delta) {
			return this.total = Util.clamp(this.value + (this.delta = delta), this.min, this.max);
		}
		
		public float value() {
			return value;
		}
		
		public float delta() {
			return delta;
		}
		
		public float total() {
			return total;
		}

		@Override
		public Property copy() {
			return new Property(this);
		}
	}
	
	public static class Modifier {
		
	}
	
	public static class Status {
		
	}
}
