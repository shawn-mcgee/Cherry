package cherry.game;

import static cherry.game.Tile.*;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import blue.core.Engine;
import blue.core.Event;
import blue.core.Input;
import blue.core.Renderable;
import blue.core.Scene;
import blue.game.Sprite;
import blue.geom.Vector2;
import cherry.core.View;

public class Editor extends Scene {
	private static final long 
		serialVersionUID = 1L;
	protected final View 
		view = new View();
	
	protected Room
		room;
	protected Sprite
		sprite0,
		sprite1;
	protected final Vector2.Mutable
		mouse = new Vector2.Mutable();
	protected float
		scale = 1f;
	protected boolean
		show_grid = true;
	
	protected java.awt.MenuBar
		mb;	
	
	protected Brush
		brush = new Brush();
	
	public Editor() {
		Event.attach(ResizeEvent.class, (event) -> {
			room.resize(event.room_w, event.room_h);
		});
	}
	
	@Override
	public void onAttach() {
		mb = new java.awt.MenuBar();
		java.awt.Menu
			m1 = new java.awt.Menu("File"),
			m2 = new java.awt.Menu("Edit");
		java.awt.MenuItem
			m11 = new java.awt.MenuItem("Save", new java.awt.MenuShortcut(KeyEvent.VK_S)),
			m12 = new java.awt.MenuItem("Load", new java.awt.MenuShortcut(KeyEvent.VK_L)),
			m21 = new java.awt.MenuItem("Resize", new java.awt.MenuShortcut(KeyEvent.VK_R));
		
		m1.add(m11);
		m1.add(m12);
		mb.add(m1);		

		m2.add(m21);
		mb.add(m2);
		
		m2.addActionListener((ae) -> {
//			Component parentComponent,
//	        Object message, String title, int messageType, Icon icon,
//	        Object[] selectionValues, Object initialSelectionValue)
			String size = (String) JOptionPane.showInputDialog(null, "Enter <W, H>", "Resize", JOptionPane.PLAIN_MESSAGE, null, null, (room.room_w() + ", " + room.room_h()));
			if(size != null) {
				Vector2 v = Vector2.parseVector2(size);
				int
					room_w = (int)v.x(),
					room_h = (int)v.y();
				Event.queue(new ResizeEvent(room_w, room_h));
			}
		});
		
		room = new Room();
		sprite0 = Sprite.load("Sprite0", "Sprite0.png", 64, 32);
		sprite1 = Sprite.load("Sprite1", "Sprite1.png", 64, 32);
		
		view.tween.set(.001f, .001f);
		
		brush.tile = Tile.getByName("Debug");
		brush.wall = Tile.getByName("Debug");
		
		Engine.setMenuBar(mb);
	}
	
	@Override
	public void onDetach() {
		Engine.setMenuBar(null);
	}
	
	@Override
	public void onResize() {
		view.set_camera(Engine.canvas().mid());
	}
	
	@Override
	public void onRender(RenderContext context) {
		context.push();
			context.mov(
					(int)view.camera_t.x(),
					(int)view.camera_t.y()
					);
			context.sca(view.camera_s);
			
			for(int j = 0; j < room.room_h(); j ++)
				for(int i = 0; i < room.room_w(); i ++) {
					Vector2 pixel = localToPixel(i, j);
					
					if(show_grid) {
						Sprite sprite = null;
						switch((i + j) & 1) {
							case 0: sprite = sprite0; break;
							case 1: sprite = sprite1; break;
						}
						if(sprite != null) {
							sprite.center(pixel);
							context.render(sprite);
						}
					}
					
					Cell cell = room.get_cell(i, j);
					
					if(cell.tile != null) {
						cell.tile.sprite.center(pixel.x(), pixel.y() + HALF_H);
						context.render(cell.tile.sprite);
					}
					
					if(cell.wall != null) {
						cell.wall.sprite.center(pixel.x(), pixel.y() - HALF_H);
						context.render(cell.wall.sprite);
					}
				}
			
			context.render(brush);
			
			
		context.pop();
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		context.update(view);
		int
			i = (int)brush.local.x(),
			j = (int)brush.local.y();
		
		if(Input.isBtnDn(Input.BTN_1)) {
			switch(brush.mode) {
				case TILE: room.set_tile(i, j, brush.tile.name); break;
				case WALL: room.set_wall(i, j, brush.wall.name); break;
			}
		}
		if(Input.isBtnDn(Input.BTN_3)) {
			switch(brush.mode) {
				case TILE: room.set_tile(i, j, null); break;
				case WALL: room.set_wall(i, j, null); break;
			}
		}
	}
	
	@Override
	public void onKeyDn(int key) {
		switch(key) {
			case Input.KEY_TAB:
				switch(brush.mode) {
					case TILE: brush.mode = Brush.Mode.WALL; break;
					case WALL: brush.mode = Brush.Mode.TILE; break;
				}
				break;
			case Input.KEY_SPACE:
				show_grid = !show_grid;
				break;
				
		}			
	}	
	
	@Override
	public void onMouseMoved(Vector2 mouse) {
		Vector2 pixel = view.mouseToPixel(mouse);
		
		
		brush.set_pixel(new Vector2(pixel.x(), pixel.y() + HALF_H));
		
		
		if(Input.isBtnDn(Input.BTN_2)) {
			float
				dx = mouse.x() - this.mouse.x(),
				dy = mouse.y() - this.mouse.y();
			view.mov_target(dx, dy);
		}
		this.mouse.set(mouse);
	}
	
	@Override
	public void onWheelMoved(float wheel) {
		if(wheel != 0) {
			Vector2 mouse = Input.getMouse();			
			if(wheel < 0 && scale < 4)
				scale *= 1.25f;
			if(wheel > 0 && scale > 1)
				scale /= 1.25f;
			
			view.sca_target(mouse, scale, scale);
		}
	}
	
	public static class Brush implements Renderable {
		public static enum Mode {
			TILE,
			WALL
		}
		public final Vector2.Mutable
			local = new Vector2.Mutable(),
			pixel = new Vector2.Mutable();
		public Mode
			mode = Mode.TILE;
		public Tile[]
			tiles,
			walls;
		public Tile
			tile,
			wall;
		
		
		public Brush() {
		}
		
		public void set_local(Vector2 local) {
			int
				i = (int)local.x(),
				j = (int)local.y();
			Vector2 pixel = localToPixel(i, j);
			
			this.local.set(i , j);
			this.pixel.set(pixel);
		}
		
		public void set_pixel(Vector2 pixel) {
			Vector2 local = pixelToLocal(pixel);
			int
				i = (int)local.x(),
				j = (int)local.y();
			//pixel = localToPixel(i, j);
			
			this.local.set(i , j);
			this.pixel.set(pixel);
		}

		@Override
		public void onRender(RenderContext context) {			
			switch(mode) {
				case TILE:
					if(tile != null) {
						tile.sprite.center(pixel.x(), pixel.y() + HALF_H);
						context.render(tile.sprite);
					}
					break;
				case WALL:
					if(wall != null) {
						wall.sprite.center(pixel.x(), pixel.y() - HALF_H);
						context.render(wall.sprite);
					}
					break;
			}
			
			Vector2 pixel = localToPixel(local);			
			context.color(Color.ORANGE);
			context.rect(
					pixel.x() - HALF_W,
					pixel.y() - HALF_H,
					FULL_W,
					FULL_H,
					false
					);
		}		
	}
	
	private static class ResizeEvent {
		public final int
			room_w,
			room_h;
		
		public ResizeEvent(int room_w, int room_h) {
			this.room_w = room_w;
			this.room_h = room_h;
		}
	}
}
