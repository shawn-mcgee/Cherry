package cherry.core;

import static cherry.game.Tile.FULL_H;
import static cherry.game.Tile.FULL_W;
import static cherry.game.Tile.HALF_H;
import static cherry.game.Tile.HALF_W;
import static cherry.game.Tile.localToPixel;
import static cherry.game.Tile.pixelToLocal;

import java.awt.Color;

import blue.core.Input;
import blue.game.Sprite;
import blue.geom.Layout;
import blue.geom.Vector2;
import cherry.game.Cell;
import cherry.game.Room;
import cherry.game.Tile;

public class Editor extends Scene {	
	protected static final Color
		debug_background = new Color(0f, 0f, 0f, .5f),
		debug_foreground = new Color(1f, 1f, 1f, .5f);
	
	protected final LevelView
		level_view = new LevelView(this);
	protected final BrushView
		brush_view = new BrushView(this);
	
	protected Room
		room;
	protected Brush
		brush;
	
	public Editor() {
		root.add(level_view);
		root.add(brush_view);
		room  = new Room() ;
		brush = new Brush(this);
	}
	
	public static class LevelView extends View {
		public final Editor
			parent;
		protected Sprite
			sprite0,
			sprite1;
		protected boolean
			show_grid  = true,
			show_tiles = true,
			show_walls = true;
		
		protected final Vector2.Mutable
			mouse = new Vector2.Mutable();
		protected float
			scale = .5f;
		
		public LevelView(Editor parent) {
			this.parent = parent;
			this.layout.set(new Layout(.5, 0, .5, 0, 1, "#-128", 0, 0, 1, 1));
		}
		
		@Override
		public void onAttach() {
			sprite0 = Sprite.fromName("sprite0", null);
			sprite1 = Sprite.fromName("sprite1", null);
			sprite0.setAlpha(.5f);
			sprite1.setAlpha(.5f);
		}
		
		@Override
		public void onRender(RenderContext context) {
			Vector2				
				v0 = pixelToLocal(mouseToPixel(0, 0)), //top l view in local coords				
				v1 = mouseToPixel(0, 0),               //top l view in pixel coords				
				v2 = mouseToPixel(                     //bot r view in pixel coords
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
								i >= 0 && i < parent.room.w() &&
								j >= 0 && j < parent.room.h()
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
							
						Cell cell = parent.room.cell(i, j);					
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
		}
		
		@Override
		public void onMouseMoved(Vector2 mouse) {
			Vector2 pixel = mouseToPixel(mouse);
			parent.brush.set_pixel(pixel.x(), pixel.y());		
			
			if(Input.isBtnDn(Input.BTN_2)) {
				float
					dx = mouse.x() - this.mouse.x(),
					dy = mouse.y() - this.mouse.y();
				mov_target(dx, dy);
			}
			
			this.mouse.set(mouse);
		}
		
		@Override
		public void onBtnDn(int btn) {
			gainFocus();
		}
	}
	
	public static class BrushView extends View {
		public final Editor
			parent;
		
		public BrushView(Editor parent) {
			this.parent = parent;
			this.layout.set(.5, 1, .5, 1, 1, "#128", 0, 0, 1, 1);
		}
		
		@Override
		public void onRender(RenderContext context) {
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
						context.canvas_w / 2          + (int)camera_t.x(),
						context.canvas_h - 2 * FULL_H + (int)camera_t.y()
						);
			
			switch(parent.brush.mode_a) {
				case TILE:
					for(int i = 0; i < parent.brush.tile_brushes.length; i ++) {
						parent.brush.tile_brushes[i].sprite.center(i * FULL_W, FULL_H);
						context.render(parent.brush.tile_brushes[i].sprite);
					}
					break;					
				case WALL:
					for(int i = 0; i < parent.brush.wall_brushes.length; i ++) {
						parent.brush.wall_brushes[i].sprite.center(i * FULL_W, FULL_H);
						context.render(parent.brush.wall_brushes[i].sprite);
					} 
					break;
			}
			context.pop();
		}
		
		@Override
		public void onWheelMoved(float wheel) {
			if(wheel < 0) {
				parent.brush.next_brush();
			}
			if(wheel > 0) {
				parent.brush.last_brush();
			}
		}
		
		@Override
		public void onBtnDn(int btn) {
			gainFocus();
		}
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
		
		protected final Editor
			parent;
		
		public Brush(Editor parent) {		
			tile_cursor = Sprite.fromName("tile_cursor", null);
			wall_cursor = Sprite.fromName("wall_cursor", null);
			load_tile_brushes();
			load_wall_brushes();
			mode_a = Mode.TILE;
			mode_b = Mode.WALL;
			
			this.parent = parent;
		}
		
		public void set_mode(Mode mode_c) {
			if(mode_a != mode_c) {
				mode_b = mode_a;
				mode_a = mode_c;
			}
			switch(mode_a) {
				case TILE: parent.brush_view.set_target(-tile_index * FULL_W, 0); break;
				case WALL: parent.brush_view.set_target(-wall_index * FULL_W, 0); break;
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
				case TILE: parent.brush_view.set_target(-tile_index * FULL_W, 0); break;
				case WALL: parent.brush_view.set_target(-wall_index * FULL_W, 0); break;
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
				case TILE: parent.brush_view.set_target(-tile_index * FULL_W, 0); break;
				case WALL: parent.brush_view.set_target(-wall_index * FULL_W, 0); break;
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
