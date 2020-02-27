package cherry.game;

import java.awt.Color;

import blue.core.Engine;
import blue.core.Input;
import blue.core.Scene;
import blue.geom.Vector;
import blue.geom.Vector2;

public class Game extends Scene {
	public final Room
		ROOM_1 = new Room(128, 128, "Room 1"),
		ROOM_2 = new Room(128,  32, "Room 2"),
		ROOM_3 = new Room( 32, 128, "Room 3"),
		ROOM_4 = new Room( 64,  64, "Room 4");
	public final Vector2.Mutable
		camera_t = new Vector2.Mutable(0f, 0f),//camera tx, ty
		camera_s = new Vector2.Mutable(1f, 1f),//camera sx, sy
		mouse = new Vector2.Mutable();
	public Room
		room;
	
	public Game() {
		ROOM_1.link_north(ROOM_3);
		ROOM_3.link_north(ROOM_4);
		
		ROOM_1.link_west(ROOM_4);		
		ROOM_3.link_east(ROOM_2);
		ROOM_2.link_east(ROOM_4);
		
		room = ROOM_1;
	}
	
	@Override
	public void onResize() {
		camera_t.set(Engine.canvas().mid());
	}
	
	@Override
	public void onRender(RenderContext context) {
		context.push();				
			context.mov(camera_t);//translate view
			context.sca(camera_s);//scale     view
			room.onRender(context);
			
			Vector2 mouse = mouseToPixel(Input.getMouse());
			
			if(mouse.y() < room.bounds.y1() && room.north.room != null) {
				context.color(Color.MAGENTA);
				context.circle(room.north.door, 8, true);
			}
			if(mouse.y() > room.bounds.y2() && room.south.room != null) {
				context.color(Color.MAGENTA);
				context.circle(room.south.door, 8, true);
			}
			if(mouse.x() < room.bounds.x1() && room.west.room != null) {
				context.color(Color.MAGENTA);
				context.circle(room.west.door, 8, true);
			}
			if(mouse.x() > room.bounds.x2() && room.east.room != null) {
				context.color(Color.MAGENTA);
				context.circle(room.east.door, 8, true);
			}
		context.pop();		
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		
			
		
	}
	
	@Override
	public void onBtnDn(int btn) {
		if(btn == Input.BTN_1) {
			Vector2 mouse = mouseToPixel(Input.getMouse());
			
			if(mouse.y() < room.bounds.y1() && room.north.room != null) {
				float
					dx = (room.north.door.x() - room.north.room.south.door.x()) * camera_s.x(),
					dy = (room.north.door.y() - room.north.room.south.door.y()) * camera_s.y();
				Vector.m_add(camera_t, dx, dy);
				room = room.north.room;
				return;
			}
			if(mouse.y() > room.bounds.y2() && room.south.room != null) {
				float
					dx = (room.south.door.x() - room.south.room.north.door.x()) * camera_s.x(),
					dy = (room.south.door.y() - room.south.room.north.door.y()) * camera_s.y();
				Vector.m_add(camera_t, dx, dy);
				room = room.south.room;
				return;
			}
			if(mouse.x() < room.bounds.x1() && room.west.room != null) {
				float
					dx = (room.west.door.x() - room.west.room.east.door.x()) * camera_s.x(),
					dy = (room.west.door.y() - room.west.room.east.door.y()) * camera_s.y();
				Vector.m_add(camera_t, dx, dy);
				room = room.west.room;
				return;
			}
			if(mouse.x() > room.bounds.x2() && room.east.room != null) {
				float
					dx = (room.east.door.x() - room.east.room.west.door.x()) * camera_s.x(),
					dy = (room.east.door.y() - room.east.room.west.door.y()) * camera_s.y();
				Vector.m_add(camera_t, dx, dy);
				room = room.east.room;
			}
		}
	}
	
	@Override
	public void onMouseMoved(Vector2 mouse) {
		if(Input.isBtnDn(Input.BTN_3)) {
			float
				dx = mouse.x() - this.mouse.x(),
				dy = mouse.y() - this.mouse.y();
			Vector.m_add(camera_t, dx, dy);
		}
		this.mouse.set(mouse);
	}
	
	@Override
	public void onWheelMoved(float wheel) {			
		if(wheel != 0) {
			Vector2 mouse = Input.getMouse();
			float
				x0 = mouse.x(),
				y0 = mouse.y(),
				_sx = camera_s.x(),
				_sy = camera_s.y(),
				x1 = (x0 - camera_t.x()) / _sx,
				y1 = (y0 - camera_t.y()) / _sy;
			
			if(wheel < 0) {
				if(_sx < 4)
					_sx *= 1.25f;
				if(_sy < 4)
					_sy *= 1.25f;
			}
			if(wheel > 0) {
				if(_sx > 1)
					_sx /= 1.25f;
				if(_sy > 1)
					_sy /= 1.25f;
			}
			
			float
				x2 = (x1 * _sx) + camera_t.x(),
				y2 = (y1 * _sy) + camera_t.y();			
			
			camera_s.set(_sx, _sy);		
			Vector.m_add(
					camera_t,
					x0 - x2,
					y0 - y2
					);
		}
	}
	
	public final Vector2 mouseToPixel(float x, float y) {
		return new Vector2(
			(x - camera_t.x()) / camera_s.x(),
			(y - camera_t.y()) / camera_s.y()
			);
	}
	
	public final Vector2 pixelToMouse(float x, float y) {
		return new Vector2(
			(x * camera_s.x()) + camera_t.x(),
			(y * camera_s.y()) + camera_t.y()
			);
	}
	
	public Vector2 mouseToPixel(Vector mouse) {
		return mouseToPixel(mouse.x(), mouse.y());
	}
	
	public Vector2 pixelToMouse(Vector pixel) {
		return pixelToMouse(pixel.x(), pixel.y());
	}
}
