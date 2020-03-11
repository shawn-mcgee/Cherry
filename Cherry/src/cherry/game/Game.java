package cherry.game;

import static cherry.game.Tile.FULL_H;
import static cherry.game.Tile.FULL_W;
import static cherry.game.Tile.localToPixel;
import static cherry.game.Tile.pixelToLocal;

import blue.core.Engine;
import blue.core.Scene;
import blue.geom.Vector2;

public class Game extends Scene {
	protected final Camera
		camera = new Camera();
	protected Room
		room;
	protected Player
		player;
	
	@Override
	public void onAttach() {
		room = new Room();
		room.load("room.txt");
		camera.set_camera(Engine.canvas().mid());
		
		
		room.add_entity(player = new Player());
		camera.tween.set(.1f, .1f);
	}

	@Override
	public void onRender(RenderContext context) {
		//context.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
						
					Cell cell = room.get_cell(i, j);					
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
						
					Cell cell = room.get_cell(i, j);					
					if(cell.wall != null) {
						cell.wall.sprite.center(pixel.x(), pixel.y());					
						context.render(cell.wall.sprite);
					}
					for(Entity entity: cell.entities)
						context.render(entity);
				}
			}
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		context.update(room);

		camera.set_target(context.canvas_w / 2 - player.x(), context.canvas_h / 2 - player.y());
		camera.tween(context.fixed_dt);
	}
}
