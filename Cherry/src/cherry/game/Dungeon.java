package cherry.game;

import static cherry.game.Tile.FULL_H;
import static cherry.game.Tile.FULL_W;
import static cherry.game.Tile.localToPixel;
import static cherry.game.Tile.pixelToLocal;
import static cherry.game.Tile.snap;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import blue.core.Engine;
import blue.core.Scene;
import blue.geom.Vector2;

public class Dungeon extends Scene {
	protected final View
		camera = new View();
	protected Room
		room;
	
	protected Player
		player;
	
	@Override
	public void onAttach() {
		room = new Room();
		room.load("room.txt");
		camera.set_camera(Engine.canvas().mid());
		
		Random random = new Random();
		for(int i = 0; i < 0; i ++) {			
			Minion minion = new Minion();
			int
				x,
				y;
			do {
				x = random.nextInt(room.w());
				y = random.nextInt(room.h());
			} while(room.tile(x, y) == null || room.wall(x, y) != null || room.cell(x, y).count() > 1);
			
			minion.setLocal(x + random.nextFloat(), y + random.nextFloat());
			room.add(minion);
		}
		
		room.add(player = new Player());
		camera.tween.set(.1f, .1f);
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
			x0 = (int)v0.x() - 4,
			y0 = (int)v0.y()    ,
			dx = (int)(v2.x() - v1.x()) / FULL_W + 4, //view width  in tiles
			dy = (int)(v2.y() - v1.y()) / FULL_H + 4; //view height in tiles
		
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
						
					Cell cell = room.cell(i, j);					
					if(cell.tile != null) {
						cell.tile.sprite.center(pixel.x(), pixel.y() + FULL_H);						
						context.render(cell.tile.sprite);
					}
				}
			}
		
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
						
					Cell cell = room.cell(i, j);					
					if(cell.wall != null) {
						cell.wall.sprite.center(pixel.x(), pixel.y());					
						context.render(cell.wall.sprite);
					}
					
					if(cell.s_flag)
						handle_s_flag(cell);
					for(Entity e: cell) {
						context.render(e);
						
						context.color(Color.BLUE);
						context.line(e.pixel, cell.pixel);
					}
				}
			}
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		//Update Entities
		for(int j = 0; j < room.h; j ++)
			for(int i = 0; i < room.w; i ++) {
				room.grid[i][j].detach();
				room.grid[i][j].attach();
				for(Entity e: room.grid[i][j])
					context.update(e);
			}
		
//		for(Entity e: room)
//			context.update(e);
		
		//Handle m_flag
		for(int j = 0; j < room.h; j ++)
			for(int i = 0; i < room.w; i ++)
				if(room.grid[i][j].m_flag)
					handle_m_flag(room.grid[i][j]);

		camera.set_target(context.canvas_w / 2 - player.pixel.x(), context.canvas_h / 2 - player.pixel.y());
		camera.tween(context.fixed_dt);
	}
	
	public void handle_m_flag(Cell cell) {
		int
			i = snap(cell.local.x()),
			j = snap(cell.local.y());
		for(Entity e: cell) {
			int
				m = snap(e.local.x()),
				n = snap(e.local.y());
			if(i != m || j != n) {
				Cell _cell = room.cell(m, n);
				if(_cell != null) {
					 cell.detach(e);
					_cell.attach(e);
				}
			} else 
				cell.s_flag();
		}
		cell.m_flag = false;
	}
	
	public void handle_s_flag(Cell cell) {
		Collections.sort(cell.list, z_order);
		cell.s_flag = false;
	}
	
	protected static final Comparator<Entity>
		z_order = (Entity a, Entity b) -> {
			if(a.pixel.y() < b.pixel.y()) return -1;
			if(a.pixel.y() > b.pixel.y()) return  1;
			if(a.pixel.x() < b.pixel.x()) return -1;
			if(a.pixel.x() > b.pixel.x()) return  1;
			return 0;
		};
}
