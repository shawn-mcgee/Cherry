package cherry.game;

import static cherry.game.Tile.HALF_H;
import static cherry.game.Tile.localToPixel;
import static cherry.game.Tile.pixelToLocal;

import java.awt.Font;
import java.awt.FontMetrics;

import blue.core.Engine;
import blue.core.Input;
import blue.core.Scene;
import blue.game.Sprite;
import blue.geom.Vector2;

public class Editor extends Scene {
	private static final long 
		serialVersionUID = 1L;
	private static final int
		TILE_MODE = 0,
		WALL_MODE = 1;
	protected final Camera 
		camera = new Camera();
	
	protected Room
		room;
	protected Sprite
		sprite0,
		sprite1,
		wall_cursor,
		tile_cursor;	
	protected boolean
		show_grid = true,
		show_tiles = true,
		show_walls = true;
	
	protected final Vector2.Mutable
		mouse = new Vector2.Mutable();
	protected float
		scale = 1f;
	
	protected Tile[]
		tiles,
		walls;
	protected int
		tile_index,
		wall_index;
	
	protected final Vector2.Mutable
		brush_local = new Vector2.Mutable(),
		brush_pixel = new Vector2.Mutable();
	protected int
		brush_mode;
	protected String
		brush_string;
	protected Sprite
		brush_sprite;
	
	public Editor() {
		//do nothing
	}
	
	@Override
	public void onAttach() {		
		room = new Room();
		
		sprite0 = Sprite.fromName("sprite0", null);
		sprite1 = Sprite.fromName("sprite1", null);
		tile_cursor = Sprite.fromName("tile_cursor", null);
		wall_cursor = Sprite.fromName("wall_cursor", null);
		
		sprite0.setAlpha(.5f);
		sprite1.setAlpha(.5f);
		
		camera.tween.set(.001f, .001f);
		
		brush_string = "marble_slab_2";
		brush_sprite = Sprite.fromName(brush_string, null);
	}
	
	@Override
	public void onResize() {
		camera.set_camera(Engine.canvas().mid());
	}
	
	@Override
	public void onRender(RenderContext context) {
		context.push();
			context.mov(
					(int)camera.camera_t.x(),
					(int)camera.camera_t.y()
					);
			context.sca(camera.camera_s);
			
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
							sprite.center(pixel.x(), pixel.y());
							context.render(sprite);
						}
					}
					
					Cell cell = room.get_cell(i, j);					
					if(show_tiles && cell.tile != null) {
						cell.tile.sprite.center(pixel.x(), pixel.y() + HALF_H);
						context.render(cell.tile.sprite);
					}					
					if(show_walls && cell.wall != null) {
						cell.wall.sprite.center(pixel.x(), pixel.y() - HALF_H);
						context.render(cell.wall.sprite);
					}
				}
			
			Sprite cursor = null;
			switch(brush_mode) {
				case TILE_MODE: 
					cursor = tile_cursor; 
					cursor.center(brush_pixel.x(), brush_pixel.y()         );
					break;					
				case WALL_MODE: 
					cursor = wall_cursor;
					cursor.center(brush_pixel.x(), brush_pixel.y() - HALF_H);
					break;
			}
			if(cursor != null)
				context.render(cursor);
			
		context.pop();
		
		context.font(new Font("Monospaced", Font.PLAIN, 16));
		FontMetrics fm = context.g.getFontMetrics();
		
		String[] info = {
			"show_grid: " + show_grid,
			"show_tiles: " + show_tiles,
			"show_walls: " + show_walls
		};
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		context.update(camera);
		int
			i = (int)brush_local.x(),
			j = (int)brush_local.y();
		
		if(Input.isBtnDn(Input.BTN_1)) {
			switch(brush_mode) {
				case TILE_MODE: room.set_tile(i, j, brush_string); break;
				case WALL_MODE: room.set_wall(i, j, brush_string); break;
			}
		}
		if(Input.isBtnDn(Input.BTN_3)) {
			switch(brush_mode) {
				case TILE_MODE: room.set_tile(i, j, null); break;
				case WALL_MODE: room.set_wall(i, j, null); break;
			}
		}
	}
	
	@Override
	public void onKeyDn(int key) {
		switch(key) {
			case Input.KEY_G: show_grid  = !show_grid;  break;
			case Input.KEY_T: show_tiles = !show_tiles; break;
			case Input.KEY_W: show_walls = !show_walls; break;
			case Input.KEY_TAB:
				brush_mode ^= 1;
		}		
	}	
	
	@Override
	public void onMouseMoved(Vector2 mouse) {
		Vector2 pixel = camera.mouseToPixel(mouse);
		set_brush_pixel(pixel.x(), pixel.y() + HALF_H);
		
		
		if(Input.isBtnDn(Input.BTN_2)) {
			float
				dx = mouse.x() - this.mouse.x(),
				dy = mouse.y() - this.mouse.y();
			camera.mov_target(dx, dy);
		}
		
		this.mouse.set(mouse);
	}
	
	@Override
	public void onWheelMoved(float wheel) {
		if(wheel != 0) {
			Vector2 mouse = Input.getMouse();			
			if(wheel < 0 && scale <  4f)
				scale *= 1.25f;
			if(wheel > 0 && scale > .5f)
				scale /= 1.25f;
			
			camera.sca_target(mouse, scale, scale);
		}
	}
	
	public void set_brush_local(float i, float j) {
		i = (int)i;
		j = (int)j;
		Vector2 pixel = localToPixel(i, j);
		
		this.brush_local.set(i , j);
		this.brush_pixel.set(pixel);
	}
	
	public void set_brush_local(Vector2 local) {
		set_brush_local(local.x(), local.y());
	}
	
	public void set_brush_pixel(float x, float y) {
		Vector2 local = pixelToLocal(x, y);
		int
			i = (int)local.x(),
			j = (int)local.y();
		Vector2 pixel = localToPixel(i, j);
		
		this.brush_local.set(i , j);
		this.brush_pixel.set(pixel);
	}
	
	public void set_brush_pixel(Vector2 pixel) {
		set_brush_pixel(pixel.x(), pixel.y());
	}
}
