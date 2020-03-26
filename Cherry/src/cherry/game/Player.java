package cherry.game;

import static cherry.game.Tile.localToPixel;

import java.awt.Color;

import blue.core.Input;
import blue.geom.Vector2;

public class Player extends Entity {
	
	public Player() {
		this.setLocal(.5f, .5f);
		
		this.movement_speed.value = 3f;
	}
	
	@Override
	public void onRender(RenderContext context) {
		context.stroke(2);
		context.color(Color.GREEN);	
		
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
	public void onUpdate(UpdateContext context) {
		int
			w = Input.isKeyDn(Input.KEY_W) ? 1 : 0,
			a = Input.isKeyDn(Input.KEY_A) ? 2 : 0,
			s = Input.isKeyDn(Input.KEY_S) ? 4 : 0,
			d = Input.isKeyDn(Input.KEY_D) ? 8 : 0;
		switch(w | a | s | d) {
			case 0:
			case 5: //w + s
			case 10://a + d
			case 15://w + a + s + d
				break;
			case 1:  //w
			case 11: //w + a + d
				turn(Facing.NORTH); move(); break;
			case 2:  //a
			case 7:  //w + a + s
				turn(Facing.WEST);  move(); break;
			case 4:  //s
			case 14: //a + s + d
				turn(Facing.SOUTH); move(); break;
			case 8:  //d
			case 13: //w + s + d
				turn(Facing.EAST);  move(); break;
			case 3:  //w + a
				turn(Facing.NORTH_WEST); move(); break;
			case 6:  //a + s
				turn(Facing.SOUTH_WEST); move(); break;
			case 12: //s + d
				turn(Facing.SOUTH_EAST); move(); break;
			case 9:  //w + d
				turn(Facing.NORTH_EAST); move(); break;
		}		
		super.onUpdate(context);
	}
}
