package cherry.game;

import static cherry.game.Tile.FULL_H;
import static cherry.game.Tile.FULL_W;
import static cherry.game.Tile.HALF_H;
import static cherry.game.Tile.HALF_W;
import static cherry.game.Tile.localToPixel;
import static cherry.game.Tile.pixelToLocal;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import blue.core.Engine;
import blue.core.Input;
import blue.game.Sprite;
import blue.geom.Vector2;
import cherry.core.Scene;

public class Editor extends Scene {		
	protected static final Color
		debug_background = new Color(0f, 0f, 0f, .5f),
		debug_foreground = new Color(1f, 1f, 1f, .5f);
	protected final Vector2.Mutable
		mouse = new Vector2.Mutable();
	protected float
		scale = .5f;
	
	protected final View 
		camera = new View();
	
	protected Room
		room;
	protected Sprite
		sprite0,
		sprite1;
	protected boolean
		show_grid = true,
		show_tiles = true,
		show_walls = true;
	protected Brush
		brush;
	
	protected java.awt.FileDialog
		file_dialog;
	
	public Editor() {
		file_dialog = new java.awt.FileDialog((java.awt.Dialog)null);
	}
	
	@Override
	public void onAttach() {
		camera.set_camera(Engine.canvas().mid());
		camera.tween.set(.001f, .001f);
		room = new Room();
		
		sprite0 = Sprite.fromName("sprite0", null);
		sprite1 = Sprite.fromName("sprite1", null);
		
		sprite0.setAlpha(.5f);
		sprite1.setAlpha(.5f);
		
		brush = new Brush();
	}
	
	@Override
	public void onRender(RenderContext context) {
		context.push();
			context.mov(
					(int)camera.camera_t.x(),
					(int)camera.camera_t.y()
					);
			context.sca(camera.camera_s);
			
			Vector2				
				v0 = pixelToLocal(camera.mouseToPixel(0, 0)), //top l view in local coords				
				v1 = camera.mouseToPixel(0, 0),               //top l view in pixel coords				
				v2 = camera.mouseToPixel(                     //bot r view in pixel coords
						context.canvas_w,
						context.canvas_h
						);
			int
				x0 = (int)v0.x() - 6,
				y0 = (int)v0.y()    ,
				dx = (int)(v2.x() - v1.x()) / FULL_W + 6, //view width  in tiles
				dy = (int)(v2.y() - v1.y()) / FULL_H + 6; //view height in tiles
			
			for(float y = 0; y < dy; y += .5f)
				for(float x = y % 1f; x < dx; x += 1f) {
					int
						i = x0 + (int)(y + x),
						j = y0 + (int)(y - x);
					if(
								i >= 0 && i < room.w() &&
								j >= 0 && j < room.h()
								) {
						Vector2 pixel = localToPixel(i, j);
						
						if(show_grid) {
							Sprite sprite = null;
							switch((i + j) & 1) {
								case 0: sprite = sprite0; break;
								case 1: sprite = sprite1; break;
							}
							if(sprite != null) {
								sprite .center(pixel.x(), pixel.y() + HALF_H);
								context.render(sprite);
							}
						}
							
						Cell cell = room.cell(i, j);					
						if(show_tiles && cell.tile != null) {
							cell.tile.sprite.center(pixel.x(), pixel.y() + FULL_H);						
							context.render(cell.tile.sprite);
						}					
						if(show_walls && cell.wall != null) {
							cell.wall.sprite.center(pixel.x(), pixel.y());						
							context.render(cell.wall.sprite);
						}
					}
				}		

		brush.render_cursor(context);
		context.pop();		
		brush.render_hotbar(context);
		
		context.font(new Font("Monospaced", Font.PLAIN, 16));
		FontMetrics fm = context.g.getFontMetrics();
		
		Cell cell = room.cell(brush.i(), brush.j());
		String[] info = {
			brush.mode_a + " [" + brush.i() + ", " + brush.j() + "]",
			"Tile: " + (cell != null && cell.tile != null ? cell.tile.string : null),
			"Wall: " + (cell != null && cell.wall != null ? cell.wall.string : null)
		};
		int
			info_w = 0,
			info_h = 0,
			fm_h = fm.getAscent() + fm.getDescent() + fm.getLeading();
		for(int i = 0; i < info.length; i ++) {
			int w = fm.stringWidth(info[i]);
			if(w > info_w)
				info_w = w;
			info_h += fm_h;
		}
		info_h += fm_h;
		int
			x = context.canvas_w - info_w,
			y = 0;
		
		context.color(debug_background);
		context.rect(
				x, y,
				info_w,
				info_h,
				true
				);
		context.color(debug_foreground);
		for(int i = 0; i < info.length; i ++)
			context.text(info[i], x, y += fm_h);
	}	
	
	@Override
	public void onUpdate(UpdateContext context) {
		camera.tween(context.fixed_dt);
		if(Input.isBtnDn(Input.BTN_1))
			brush._place(room);
		if(Input.isBtnDn(Input.BTN_3))
			brush._break(room);
		brush.hotbar.tween(context.fixed_dt);
	}	
	
	@Override
	public void onKeyDn(int key) {
		if(
				Input.isKeyDn(Input.KEY_L_CTRL) || 
				Input.isKeyDn(Input.KEY_R_CTRL) ){
			switch(key) {
				case Input.KEY_N: onNew(); break;
				case Input.KEY_O: onLoad(); break;
				case Input.KEY_S: onSave(); break;			
			}
		} else if(
				Input.isKeyDn(Input.KEY_L_ALT) || 
				Input.isKeyDn(Input.KEY_R_ALT) ){
			switch(key) {
				case Input.KEY_BQUOTE: show_grid = !show_grid; break;
				case Input.KEY_1: show_tiles = !show_tiles; break;
				case Input.KEY_2: show_walls = !show_walls; break;
			}			
		} else if(		
				Input.isKeyDn(Input.KEY_L_SHIFT) || 
				Input.isKeyDn(Input.KEY_R_SHIFT) ){
			switch(key) {
				case Input.KEY_EQUALS: room.increment(); break;
				case Input.KEY_MINUS : room.decrement(); break;
				
				case Input.KEY_W:
				case Input.KEY_UP_ARROW: room.translate(-1, -1); break;
				
				case Input.KEY_S:
				case Input.KEY_DN_ARROW: room.translate( 1,  1); break;
				
				case Input.KEY_A:
				case Input.KEY_L_ARROW : room.translate(-1,  1); break;
				
				case Input.KEY_D:
				case Input.KEY_R_ARROW : room.translate( 1, -1); break;

				case Input.KEY_Q: room.rotate_l(); break;
				case Input.KEY_E: room.rotate_r(); break;

				case Input.KEY_X: room.mirror_x(); break;
				case Input.KEY_Z: room.mirror_y(); break;
			}
		} else
			switch(key) {
				case Input.KEY_EQUALS: zoom_increment(); break;
				case Input.KEY_MINUS : zoom_decrement(); break;
				
				case Input.KEY_W: 
				case Input.KEY_UP_ARROW: camera.mov_target(0,  FULL_H * camera.camera_s.y()); break;
				
				case Input.KEY_S: 
				case Input.KEY_DN_ARROW: camera.mov_target(0, -FULL_H * camera.camera_s.y()); break;
				
				case Input.KEY_A: 
				case Input.KEY_L_ARROW : camera.mov_target( FULL_W * camera.camera_s.x(), 0); break;
				
				case Input.KEY_D: 
				case Input.KEY_R_ARROW : camera.mov_target(-FULL_W * camera.camera_s.x(), 0); break;
				
				case Input.KEY_Q: brush.last_brush(); break;
				case Input.KEY_E: brush.next_brush(); break;
				
				case Input.KEY_1: brush.set_mode(Brush.Mode.TILE); break;
				case Input.KEY_2: brush.set_mode(Brush.Mode.WALL); break;
				case Input.KEY_TAB: brush.toggle_mode(); break;
			}		
	}	
	
	@Override
	public void onMouseMoved(Vector2 mouse) {
		Vector2 pixel = camera.mouseToPixel(mouse);
		brush.set_pixel(pixel.x(), pixel.y());		
		
		if(Input.isBtnDn(Input.BTN_2)) {
			float
				dx = mouse.x() - this.mouse.x(),
				dy = mouse.y() - this.mouse.y();
			camera.mov_target(dx, dy);
		}
		
		this.mouse.set(mouse);
	}
	
	public void zoom_increment() {
		if(scale <  4f)
			scale *= 1.25f;
	}
	
	public void zoom_decrement() {
		if(scale > .5f)
			scale /= 1.25f;
	}
	
	@Override
	public void onWheelMoved(float wheel) {
		if(wheel != 0) {
			Vector2 mouse = Input.getMouse();			
			if(wheel < 0) zoom_increment();
			if(wheel > 0) zoom_decrement();
			
			camera.sca_target(mouse, scale, scale);
		}
	}	
	
	public void onNew() {
		room.resize(
				Room.DEFAULT_ROOM_W, 
				Room.DEFAULT_ROOM_H
				);
		room.clear();
	}
	
	public void onLoad() {
		file_dialog.setMode(java.awt.FileDialog.LOAD);
		file_dialog.setVisible(true);
		file_dialog.dispose();
		
		String path = file_dialog.getFile();
		if(path != null)
			room.load(path);
	}
	
	public void onSave() {
		file_dialog.setMode(java.awt.FileDialog.SAVE);
		file_dialog.setVisible(true);
		file_dialog.dispose();
		
		String path = file_dialog.getFile();
		if(path != null)
			room.save(path);
	}
	
	public static class Brush {
		public static enum Mode {
			TILE,
			WALL;
		}
		
		protected final Vector2.Mutable
			local = new Vector2.Mutable(),
			pixel = new Vector2.Mutable();
		protected Tile[]
			tile_brushes,
			wall_brushes;
		protected int
			tile_index,
			wall_index;
		protected Tile
			tile_brush,
			wall_brush;
		protected Mode
			mode_a,
			mode_b;
		
		protected Sprite
			tile_cursor,
			wall_cursor;
		protected View
			hotbar;
		
		public Brush() {		
			tile_cursor = Sprite.fromName("tile_cursor", null);
			wall_cursor = Sprite.fromName("wall_cursor", null);
			load_tile_brushes();
			load_wall_brushes();
			mode_a = Mode.TILE;
			mode_b = Mode.WALL;
			
			hotbar = new View();
			hotbar.tween.set(.001f, .001f);
		}
		
		public void set_mode(Mode mode_c) {
			if(mode_a != mode_c) {
				mode_b = mode_a;
				mode_a = mode_c;
			}
			switch(mode_a) {
				case TILE: hotbar.set_target(-tile_index * FULL_W, 0); break;
				case WALL: hotbar.set_target(-wall_index * FULL_W, 0); break;
			}
		}
		
		public void toggle_mode() {
			set_mode(mode_b);
		}
		
		public void next_brush() {
			switch(mode_a) {
				case TILE:
					tile_index = (tile_index + 1 + tile_brushes.length) % tile_brushes.length;
					tile_brush = tile_brushes[tile_index];
					break;
				case WALL:
					wall_index = (wall_index + 1 + wall_brushes.length) % wall_brushes.length;
					wall_brush = wall_brushes[wall_index];
					break;
			}
			switch(mode_a) {
				case TILE: hotbar.set_target(-tile_index * FULL_W, 0); break;
				case WALL: hotbar.set_target(-wall_index * FULL_W, 0); break;
			}
		}
		
		public void last_brush() {
			switch(mode_a) {
				case TILE:
					tile_index = (tile_index - 1 + tile_brushes.length) % tile_brushes.length;
					tile_brush = tile_brushes[tile_index];
					break;
				case WALL:
					wall_index = (wall_index - 1 + wall_brushes.length) % wall_brushes.length;
					wall_brush = wall_brushes[wall_index];
					break;
			}
			switch(mode_a) {
				case TILE: hotbar.set_target(-tile_index * FULL_W, 0); break;
				case WALL: hotbar.set_target(-wall_index * FULL_W, 0); break;
			}
		}
		
		public void _place(Room room) {
			switch(mode_a) {
				case TILE: room.set_tile(i(), j(), tile_brush); break;
				case WALL: room.set_wall(i(), j(), wall_brush); break;
			}
		}
		
		public void _break(Room room) {
			switch(mode_a) {
				case TILE: room.set_tile(i(), j(), null); break;
				case WALL: room.set_wall(i(), j(), null); break;
			}
		}
		
		public void render_cursor(RenderContext context) {					
			switch(mode_a) {
				case TILE:
					tile_cursor.center(x(), y() + HALF_H);					
					context.render(tile_cursor);
					break;					
				case WALL:
					wall_cursor.center(x(), y()         );
					context.render(wall_cursor);
					break;
			}
		}
		
		public void render_hotbar(RenderContext context) {
			context.color(debug_background);
			context.rect(
					0, context.canvas_h - FULL_H * 2,
					   context.canvas_w , FULL_H * 2,
					true);
			context.color(debug_foreground);
			context.rect(
					context.canvas_w / 2 - HALF_W, 
					context.canvas_h - 2 * FULL_H,
					FULL_W, 2 * FULL_H,
					true);
			
			context.push();						
				context.mov(
						context.canvas_w / 2          + (int)hotbar.camera_t.x(),
						context.canvas_h - 2 * FULL_H + (int)hotbar.camera_t.y()
						);
			
			switch(mode_a) {
				case TILE:
					for(int i = 0; i < tile_brushes.length; i ++) {
						tile_brushes[i].sprite.center(i * FULL_W, FULL_H);
						context.render(tile_brushes[i].sprite);
					}
					break;					
				case WALL:
					for(int i = 0; i < wall_brushes.length; i ++) {
						wall_brushes[i].sprite.center(i * FULL_W, FULL_H);
						context.render(wall_brushes[i].sprite);
					} 
					break;
			}
			context.pop();
		}
		
		public float x() { return pixel.x(); }
		public float y() { return pixel.y(); }
		public int i() { return (int)local.x(); }
		public int j() { return (int)local.y(); }
		
		public void set_local(float i, float j) {
			i = (int)i;
			j = (int)j;
			Vector2 pixel = localToPixel(i, j);
			
			this.local.set(i , j);
			this.pixel.set(pixel);
		}
		
		public void set_local(Vector2 local) {
			set_local(local.x(), local.y());
		}
		
		public void set_pixel(float x, float y) {
			Vector2 local = pixelToLocal(x, y);
			int
				i = (int)local.x(),
				j = (int)local.y();
			Vector2 pixel = localToPixel(i, j);
			
			this.local.set(i , j);
			this.pixel.set(pixel);
		}
		
		public void set_pixel(Vector2 pixel) {
			set_pixel(pixel.x(), pixel.y());
		}		
		
		public void load_tile_brushes() {
			tile_brushes = new Tile[Tile.TILE_INDEX.size()];
			Tile.TILE_INDEX.values().toArray(tile_brushes);
			if(tile_brushes.length > 0)
				tile_brush = tile_brushes[tile_index = 0];
		}
		
		public void load_wall_brushes() {
			wall_brushes = new Tile[Tile.WALL_INDEX.size()];
			Tile.WALL_INDEX.values().toArray(wall_brushes);
			if(wall_brushes.length > 0)
				wall_brush = wall_brushes[wall_index = 0];
		}
	}
}
