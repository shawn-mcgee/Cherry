package cherry.game;

import blue.core.Input;
import blue.game.Sprite;
import blue.geom.Vector2;

public class Player extends Entity {
	protected static final float
		SPEED = 2f,
		SIN = (float)(SPEED * Math.sin(Math.toRadians(45))),
		COS = (float)(SPEED * Math.cos(Math.toRadians(45)));
	protected static final Vector2
		NORTH = new Vector2(0, -SPEED),
		WEST  = new Vector2(-SPEED, 0),
		SOUTH = new Vector2(0,  SPEED),
		EAST  = new Vector2( SPEED, 0),
		
		NORTH_WEST = new Vector2(-COS, -SIN),
		SOUTH_WEST = new Vector2(-COS,  SIN),
		SOUTH_EAST = new Vector2( COS,  SIN),
		NORTH_EAST = new Vector2( COS, -SIN);
	
	Sprite
		tile_cursor = Sprite.fromName("tile_cursor", null);
	
	public Player() {
		local.set(1.5f, 1.5f);
	}
	
	@Override
	public void onRender(RenderContext context) {
		tile_cursor.center(cell.x(), cell.y() + Tile.HALF_H);
		context.render(tile_cursor);
		super.onRender(context);
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
				speed.set(0f, 0f); break;
			case 1:  //w
			case 11: //w + a + d
				speed.set(NORTH_WEST); break;
			case 2:  //a
			case 7:  //w + a + s
				speed.set(SOUTH_WEST); break;
			case 4:  //s
			case 14: //a + s + d
				speed.set(SOUTH_EAST); break;
			case 8:  //d
			case 13: //w + s + d
				speed.set(NORTH_EAST); break;
			case 3:  //w + a
				speed.set(WEST ); break;
			case 6:  //a + s
				speed.set(SOUTH); break;
			case 12: //s + d
				speed.set(EAST ); break;
			case 9:  //w + d
				speed.set(NORTH); break;		
		}		
		super.onUpdate(context);
	}
}
