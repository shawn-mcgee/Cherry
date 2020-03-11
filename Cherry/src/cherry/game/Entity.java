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
		
		Segment
			segment_a = null,
			segment_b = null;
		
		if(dx < 0) {
			if(room.get_wall(i - 1, j) != null) {
				segment_a = new Segment(i    , j    , i    , j + 1);
			} else if(dy < 0 && room.get_wall(i - 1, j - 1) != null) {
				segment_a = new Segment(i - 1, j    , i    , j    );
			} else if(dy > 0 && room.get_wall(i - 1, j + 1) != null) {
				segment_a = new Segment(i - 1, j + 1, i    , j + 1);
			}
		} else if(dx > 0) {
			if(room.get_wall(i + 1, j) != null) {
				segment_a = new Segment(i + 1, j    , i + 1, j + 1);
			} else if(dy < 0 && room.get_wall(i + 1, j - 1) != null) {
				segment_a = new Segment(i + 1, j    , i + 2, j    );
			} else if(dy > 0 && room.get_wall(i + 1, j + 1) != null) {
				segment_a = new Segment(i + 1, j + 1, i + 2, j + 1);
			}
		}
		
		if(dy < 0) {
			if(room.get_wall(i, j - 1) != null) {
				segment_b = new Segment(i    , j    , i + 1, j    );
			} else if(dx < 0 && room.get_wall(i - 1, j - 1) != null) {
				segment_b = new Segment(i    , j - 1, i    , j    );
			} else if(dx > 0 && room.get_wall(i + 1, j + 1) != null) {
				segment_b = new Segment(i + 1, j - 1,i + 1, j     );
			}
		} else if(dy > 0) {
			if(room.get_wall(i, j + 1) != null) {
				segment_b = new Segment(i    , j + 1, i + 1, j + 1);
			}
			else if(dx < 0 && room.get_wall(i - 1, j + 1) != null) {
				segment_b = new Segment(i    , j + 1, i    , j + 2);
			}
			else if(dx > 0 && room.get_wall(i + 1, j + 1) != null) {
				segment_b = new Segment(i + 1, j + 1, i + 1, j + 2);
			}
		}
		
		Vector2
			point = null;
		float
			delta = Float.POSITIVE_INFINITY;
		
		
		Segment
			segment_c = new Segment(local.x(), local.y(), local.x() + dx + Util.sign(dx) * size, local.y()),
			segment_d = new Segment(local.x(), local.y(), local.x(), local.y() + dy + Util.sign(dy) * size);
		
		if(segment_a != null) {
			Vector2 p;
			p = point(segment_a, segment_c);
			if(p != null) {
				float d = Vector.dot(Vector.sub(local, p));
				if(d < delta) {
					delta = d;
					point = p;
					
					this.point = p;
				}
			}
			p = point(segment_a, segment_d);
			if(p != null) {
				float d = Vector.dot(Vector.sub(local, p));
				if(d < delta) {
					delta = d;
					point = p;
					
					this.point = p;
				}
			}
			
			this.segment_a = segment_a;
		}
		
		if(segment_b != null){
			Vector2 p;
			p = point(segment_b, segment_c);
			if(p != null) {
				float d = Vector.dot(Vector.sub(local, p));
				if(d < delta) {
					delta = d;
					point = p;
					
					this.point = p;
				}
			}
			p = point(segment_b, segment_d);
			if(p != null) {
				float d = Vector.dot(Vector.sub(local, p));
				if(d < delta) {
					delta = d;
					point = p;
					
					this.point = p;
				}
			}
			
			this.segment_b = segment_b;
		}
		
		if(point != null) {
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
				if(Float.isNaN(t0)) {
					System.err.println("a0: " + a0);
					System.err.println("b0: " + b0);
					System.err.println("c0: " + c0);
					System.err.println("b1: " + b1);
					System.err.println("c1: " + c1);
					System.err.println("q0: " + q0);
					System.err.println("q1: " + q1);
					System.err.println("t: " + t0);
					
					t0 = 0;
				}
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
		
		public Segment(float x1, float y1, float x2, float y2) {
			this.v1 = new Vector2(x1, y1);
			this.v2 = new Vector2(x2, y2);
			this.dv = new Vector2(x2 - x1, y2 - y1);
		}		
	}
	
	public static Vector2 point(Segment a, Segment b) {
		float
			x1 = a.v1.x(),
			x2 = a.v2.x(),
			x3 = b.v1.x(),
			x4 = b.v2.x(),
			y1 = a.v1.y(),
			y2 = a.v2.y(),
			y3 = b.v1.y(),
			y4 = b.v2.y(),
			dx = a.dv.x(),
			dy = a.dv.y();
		int
			w1 = winding(x1, y1, x2, y2, x3, y3),
			w2 = winding(x1, y1, x2, y2, x4, y4);
		
		if(w1 != w2) {
			float
				t = 
				((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) /
				((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1)) ;
			if(t <= 0f)
				return a.v1;
			if(t >= 1f)
				return a.v2;
			return new Vector2(x1 + t * dx, y1 + t * dy);
		}
		
		return null;
	}
	
	public static final int winding(
    		Vector2 v1,
    		Vector2 v2,
    		Vector2 v3
    		) {
    	return winding(v1.x(), v1.y(), v2.x(), v2.y(), v3.x(), v3.y());
    }
	
	public static final int winding(
    		float x1, float y1,
    		float x2, float y2,
    		float x3, float y3
    		) {
    	float w = 
    			(y2 - y1) * (x3 - x2) - 
    			(x2 - x1) * (y3 - y2);    
    	return (w != 0) ? (w > 0)? -1 : 1 : 0;
    }
}
