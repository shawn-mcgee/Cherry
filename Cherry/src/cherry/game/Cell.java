package cherry.game;

import static cherry.game.Tile.localToPixel;

import java.util.List;

import blue.core.Renderable;
import blue.core.Updateable;
import blue.geom.Vector2;

public class Cell implements Renderable, Updateable {
	public Room
		room;
	public Vector2
		local,
		pixel;
	public Tile
		tile,
		wall;
	public List<Unit>
		units;
	
	public Cell(int i, int j, Room room) {
		this.room = room;
		this.local = new  Vector2(i, j);
		this.pixel = localToPixel(i, j);
	}
	
	public int i() { return (int)local.x(); }
	public int j() { return (int)local.y(); }
	
	public float x() { return pixel.x(); }
	public float y() { return pixel.y(); }


	@Override
	public void onRender(RenderContext context) {
		//do nothing
	}

	@Override
	public void onUpdate(UpdateContext context) {
		for(Unit unit: units)
			context.update(unit);
	}
}
