package cherry._game;

import java.awt.Color;

import blue.core.Renderable;
import blue.core.Updateable;
import blue.geom.Bounds2;
import blue.geom.Vector;
import blue.geom.Vector2;
import blue.util.Random;
import blue.util.Random.LFSR32;

public class Room implements Renderable, Updateable {
	public final Bounds2
		bounds;
	public final Node
		north,
		south,
		east,
		west;
	public final String
		name;
	
	public Room(float w, float h, String name) {
		this.bounds = new Bounds2(0, 0, w, h);
		this.north = new Node(w / 2, 0);
		this.south = new Node(w / 2, h);
		this.east = new Node(w, h / 2);
		this.west = new Node(0, h / 2);
		this.name = name;
	}
	
	public void link_north(Room room) {
		this.north.room = room;
		room.south.room = this;
	}
	
	public void link_south(Room room) {
		this.south.room = room;
		room.north.room = this;
	}
	
	public void link_east(Room room) {
		this.east.room = room;
		room.west.room = this;
	}
	
	public void link_west(Room room) {
		this.west.room = room;
		room.east.room = this;
	}
	
	private static final LFSR32
		UID32 = Random.LFSR32();
	private int
		render_uid,
		update_uid;
	
	@Override
	public void onRender(RenderContext context) {
		onRender(context, UID32.next(), 2);		
	}
	
	public void onRender(RenderContext context, int render_uid, int depth) {
		if(depth > 0) {
			context.stroke(2);
			
			context.color(Color.WHITE);
			context.rect(this.bounds, false);
			context.text(this.name, this.bounds.mid());
			
			this.render_uid = render_uid;
			
			if(north.room != null) {
				context.push();
					float
						dx = north.door.x() - north.room.south.door.x(),
						dy = north.door.y() - north.room.south.door.y();
					context.mov(dx, dy);
					north.room.onRender(context, render_uid, depth - 1);
				context.pop();
			}
			
			if(south.room != null) {
				context.push();
					float
						dx = south.door.x() - south.room.north.door.x(),
						dy = south.door.y() - south.room.north.door.y();
					context.mov(dx, dy);
					south.room.onRender(context, render_uid, depth - 1);
				context.pop();
			}
			
			if(east.room != null) {
				context.push();
					float
						dx = east.door.x() - east.room.west.door.x(),
						dy = east.door.y() - east.room.west.door.y();
					context.mov(dx, dy);
					east.room.onRender(context, render_uid, depth - 1);
				context.pop();
			}
			
			if(west.room != null) {
				context.push();
					float
						dx = west.door.x() - west.room.east.door.x(),
						dy = west.door.y() - west.room.east.door.y();
					context.mov(dx, dy);
					west.room.onRender(context, render_uid, depth - 1);
				context.pop();
			}
		}
	}

	@Override
	public void onUpdate(UpdateContext context) {
		
	}
	
	
	
	public static class Node {
		public final Vector2
			door;
		public Room
			room;
		
		public Node(Vector p) {
			door = new Vector2(p);
		}
		
		public Node(float x, float y) {
			door = new Vector2(x, y);
		}
	}
}
