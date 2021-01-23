package cherry.game;

import static cherry.game.Tile.*;

import java.awt.Color;

import blue.math.Vector2;

public class Minion extends Entity {
	
	
	@Override
	public void render(RenderContext context) {
		context.stroke(2);
		context.color(Color.RED);
		
		float size = this.size.value();
		Vector2
			a = localToPixel(local.x() - size, local.y() - size),
			b = localToPixel(local.x() + size, local.y() - size),
			c = localToPixel(local.x() + size, local.y() + size),
			d = localToPixel(local.x() - size, local.y() + size);
		
		context.line(a, b);
		context.line(b, c);
		context.line(c, d);
		context.line(d, a);
	}
	
	@Override
	public void update(UpdateContext context) {
		move();
	}
	
}
