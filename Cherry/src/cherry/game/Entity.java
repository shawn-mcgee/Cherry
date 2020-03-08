package cherry.game;

import static cherry.game.Tile.localToPixel;

import java.awt.Color;

import blue.core.Renderable;
import blue.core.Updateable;
import blue.geom.Vector;
import blue.geom.Vector2;
import blue.util.Util;

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
		size = .25f;
	
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
		
		if(segment_a != null) {
			context.color(Color.GREEN);
			context.line(
					localToPixel(segment_a.v1),
					localToPixel(segment_a.v2));
		}
		
		if(segment_b != null) {
			context.color(Color.BLUE);
			context.line(
					localToPixel(segment_b.v1),
					localToPixel(segment_b.v2));
		}
		
		if(point != null) {
			context.color(Color.RED);
			context.line(
					localToPixel(local),
					localToPixel(point)
					);
		}
	}
	

	Segment
		segment_a = null,
		segment_b = null;
	Vector2
		point = null;
	
	@Override
	public void onUpdate(UpdateContext context) {
		float
			dx = speed.x() * context.fixed_dt,
			dy = speed.y() * context.fixed_dt;
		int
			i = (int)local.x(),
			j = (int)local.y();
		
		if(dx < 0) {
			if(room.get_wall(i - 1, j) != null) {
				segment_a = new Segment(i    , j    , i    , j + 1);
				System.out.println("case 1");
			} else if(dy < 0 && room.get_wall(i - 1, j - 1) != null) {
				segment_a = new Segment(i - 1, j    , i    , j    );
				System.out.println("case 2");
			} else if(dy > 0 && room.get_wall(i - 1, j + 1) != null) {
				segment_a = new Segment(i - 1, j + 1, i    , j + 1);
				System.out.println("case 3");
			}
		} else if(dx > 0) {
			if(room.get_wall(i + 1, j) != null) {
				segment_a = new Segment(i + 1, j    , i + 1, j + 1);
				System.out.println("case 4");
			} else if(dy < 0 && room.get_wall(i + 1, j - 1) != null) {
				segment_a = new Segment(i + 1, j    , i + 2, j    );
				System.out.println("case 5");
			} else if(dy > 0 && room.get_wall(i + 1, j + 1) != null) {
				segment_a = new Segment(i + 1, j + 1, i + 2, j + 1);
				System.out.println("case 6");
			}
		}
		
		if(dy < 0) {
			if(room.get_wall(i, j - 1) != null)
				segment_b = new Segment(i    , j    , i + 1, j    );
			else if(dx < 0 && room.get_wall(i - 1, j - 1) != null)
				segment_b = new Segment(i    , j - 1, i    , j    );
			else if(dx > 0 && room.get_wall(i + 1, j + 1) != null)
				segment_b = new Segment(i + 1, j - 1,  + 1, j     );			
		} else if(dy > 0) {
			if(room.get_wall(i, j + 1) != null)
				segment_b = new Segment(i    , j + 1, i + 1, j + 1);
			else if(dx < 0 && room.get_wall(i - 1, j + 1) != null)
				segment_b = new Segment(i    , j + 1, i    , j + 2);
			else if(dx > 0 && room.get_wall(i + 1, j + 1) != null)
				segment_b = new Segment(i + 1, j + 1, i + 1, j + 2);
		}
		
		Vector2
			point = null;
		float
			delta = Float.POSITIVE_INFINITY;
		
		if(segment_a != null) {
			Vector2 
				c = Vector.add(local, dx, dy);	
		    float 
		    	t = Vector.s_pro(Vector.sub(c, segment_a.v1), segment_a.dv);
		    Vector2 
		    	p = Vector.add(segment_a.v1, Vector.mul(segment_a.dv, Util.clamp(t, 0f, 1f))); 
		    float 
		    	d = Vector.dot(Vector.sub(c, p));
		    if(d < delta) {
		    	point = p;
		    	delta = d;
		    	
		    	this.point = p;
		    }
		}
		
		if(segment_b != null) {
			Vector2
				c = Vector.add(local, dx, dy);
		    float 
		    	t = Vector.s_pro(Vector.sub(c, segment_b.v1), segment_b.dv);
		    Vector2
		    	p = Vector.add(segment_b.v1, Vector.mul(segment_b.dv, Util.clamp(t, 0f, 1f)));
		    float 
		    	d = Vector.dot(Vector.sub(c, p));
		    if(d < delta) {
		    	point = p;
		    	delta = d;
		    	
		    	this.point = p;
		    }
		}
		
		if(point != null && delta < size * size) {
			float a0 = (dx * dx) + (dy * dy);			
			if(a0 != 0) {
				float 
					b0 = 2 * dx * (local.x() - point.x()) + 2 * dy * (local.y() - point.y()),
					c0 = 
						(point.x() * point.x()) - (2 * (point.x() * local.x())) + (local.x() * local.x()) + 
						(point.y() * point.y()) - (2 * (point.y() * local.y())) + (local.y() * local.y()) -
						size * size;
				float
					b1 = b0 / a0,
					c1 = c0 / a0,
					q0 = b1 / 2 ,
					q1 = (float)Math.sqrt(q0 * q0 - c1),
					t0 = -q0 - q1,
					t1 = -q0 + q1;
				System.out.println(t0 + ", " + t1);
				dx *= t0;
				dy *= t0;
			}			
		}
		
		Vector.m_add(local, dx, dy);
		pixel.set(localToPixel(local));
	}
	
	private static class Segment {
		public final Vector2
			v1,
			v2,
			dv;
		
		public Segment(int x1, int y1, int x2, int y2) {
			this.v1 = new Vector2(x1, y1);
			this.v2 = new Vector2(x2, y2);
			this.dv = new Vector2(x2 - x1, y2 - y1);
		}
	}
	
	
		
		
		
		
		
		
		
		
//		int
//			i = (int)(local.x() + dx + (dx != 0f ? dx > 0f ? size : - size: 0f)),
//			j = (int)(local.y() + dy + (dy != 0f ? dy > 0f ? size : - size: 0f));
//		Tile
//			wall_dx = room.get_wall(i, j()),
//			wall_dy = room.get_wall(i(), j);
//		
//		if(wall_dx != null && wall_dy == null)
//			dx = i - local.x() + (dx <= 0 ? size + 1 : - size);
//		if(wall_dy != null && wall_dx == null)
//			dy = j - local.y() + (dy <= 0 ? size + 1 : - size);
		
}
