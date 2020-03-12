package cherry._game;

import java.awt.Color;

import blue.core.Engine;
import blue.core.Input;
import blue.core.Scene;
import blue.geom.Vector;
import blue.geom.Vector2;
import blue.geom.Vector4;
import cherry.game.View;

public class Game extends Scene {
	public final Room
		ROOM_1 = new Room(128, 128, "Room 1"),
		ROOM_2 = new Room(128,  32, "Room 2"),
		ROOM_3 = new Room( 32, 128, "Room 3"),
		ROOM_4 = new Room( 64,  64, "Room 4");
	public final View
		camera = new View();
	public final Vector2.Mutable
		mouse = new Vector2.Mutable();
	public Room
		room;
	
	public static void main(String[] args) {
		Engine.getConfiguration().set(Engine.CANVAS_FOREGROUND, new Vector4(0, 0, 0, 255));
		Engine.init();
		Engine.setScene(new Game());
	}
	
	public Game() {
		ROOM_1.link_north(ROOM_3);
		ROOM_3.link_north(ROOM_4);
		
		ROOM_1.link_west(ROOM_4);		
		ROOM_3.link_east(ROOM_2);
		ROOM_2.link_east(ROOM_4);
		
		room = ROOM_1;
		
		camera.tween.set(.001f, .001f);
	}
	
	@Override
	public void onResize() {
		camera.set_camera(Engine.canvas().mid(), new Vector2(1f, 1f));
	}
	
	@Override
	public void onRender(RenderContext context) {
		context.push();				
			context.mov(camera.camera_t);//translate view
			context.sca(camera.camera_s);//scale     view			
			
			context.color(Color.MAGENTA);
			context.circle(room.bounds.mid(), 8, true);
			
			room.onRender(context, 0, 4);
			
			context.color(Color.MAGENTA);
			
			Vector2 mouse = camera.mouseToPixel(Input.getMouse());
			
			if(mouse.y() < room.bounds.y1() && room.north.room != null)
				context.circle(room.north.door, 8, true);
			if(mouse.y() > room.bounds.y2() && room.south.room != null)
				context.circle(room.south.door, 8, true);
			if(mouse.x() < room.bounds.x1() && room.west.room != null)
				context.circle(room.west.door, 8, true);
			if(mouse.x() > room.bounds.x2() && room.east.room != null)
				context.circle(room.east.door, 8, true);
		context.pop();		
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		camera.tween(context.fixed_dt);		
	}
	
	@Override
	public void onBtnDn(int btn) {
		if(btn == Input.BTN_1) {
			Vector2 mouse = camera.mouseToPixel(Input.getMouse());
			
			if(mouse.y() < room.bounds.y1() && room.north.room != null) {
				float
					dx = (room.north.door.x() - room.north.room.south.door.x()) * camera.camera_s.x(),
					dy = (room.north.door.y() - room.north.room.south.door.y()) * camera.camera_s.y();				
				room = room.north.room;
				camera.mov_camera(dx, dy);
				camera.set_target(Vector.sub(Engine.canvas().mid(), room.bounds.mid()));
				return;
			}
			if(mouse.y() > room.bounds.y2() && room.south.room != null) {
				float
					dx = (room.south.door.x() - room.south.room.north.door.x()) * camera.camera_s.x(),
					dy = (room.south.door.y() - room.south.room.north.door.y()) * camera.camera_s.y();
				room = room.south.room;
				camera.mov_camera(dx, dy);
				camera.set_target(Vector.sub(Engine.canvas().mid(), room.bounds.mid()));
				return;
			}
			if(mouse.x() < room.bounds.x1() && room.west.room != null) {
				float
					dx = (room.west.door.x() - room.west.room.east.door.x()) * camera.camera_s.x(),
					dy = (room.west.door.y() - room.west.room.east.door.y()) * camera.camera_s.y();
				room = room.west.room;
				camera.mov_camera(dx, dy);
				camera.set_target(Vector.sub(Engine.canvas().mid(), room.bounds.mid()));
				return;
			}
			if(mouse.x() > room.bounds.x2() && room.east.room != null) {
				float
					dx = (room.east.door.x() - room.east.room.west.door.x()) * camera.camera_s.x(),
					dy = (room.east.door.y() - room.east.room.west.door.y()) * camera.camera_s.y();
				room = room.east.room;
				camera.mov_camera(dx, dy);
				camera.set_target(Vector.sub(Engine.canvas().mid(), room.bounds.mid()));
			}
		}
	}
	
	@Override
	public void onMouseMoved(Vector2 mouse) {
		if(Input.isBtnDn(Input.BTN_3)) {
			float
				dx = mouse.x() - this.mouse.x(),
				dy = mouse.y() - this.mouse.y();
			camera.mov_target(dx, dy);
		}
		this.mouse.set(mouse);
	}
	
	private float
		scale = 1f;
	
	@Override
	public void onWheelMoved(float wheel) {			
		if(wheel != 0) {
			Vector2 mouse = Input.getMouse();			
			if(wheel < 0 && scale < 4)
				scale *= 1.25f;
			if(wheel > 0 && scale > 1)
				scale /= 1.25f;
			
			camera.sca_target(mouse, scale, scale);
		}
	}
	
	
}
