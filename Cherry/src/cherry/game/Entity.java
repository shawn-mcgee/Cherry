package cherry.game;

import blue.core.Renderable;
import blue.core.Updateable;
import blue.geom.Region2;

public class Entity implements Renderable, Updateable {
	public final Region2.Mutable
		bounds = new Region2.Mutable();
	
	@Override
	public void onRender(RenderContext context) {
		
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		
	}
	
}
