package cherry.game;

import blue.core.Renderable;
import blue.core.Updateable;
import blue.geom.Vector2;

public class Unit implements Renderable, Updateable {
	public Room
		room;
	public Cell
		cell;
	
	public final Vector2.Mutable
		location = new Vector2.Mutable(),
		velocity = new Vector2.Mutable();
	public float
		size;
	
	
	@Override
	public void onRender(RenderContext context) {
		
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		
	}
	
}
