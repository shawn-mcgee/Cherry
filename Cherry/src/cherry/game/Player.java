package cherry.game;

import blue.core.Input;

public class Player extends Entity {
	
	public Player() {
		local.set(1.5f, 1.5f);
		
		property[Property.MOVEMENT_SPEED].value(  4f);
		property[Property.SIZE          ].value(.25f);
	}
	
	@Override
	public void onRender(RenderContext context) {
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
				facing.set(0f, 0f); break;
			case 1:  //w
			case 11: //w + a + d
				facing.set(NORTH_WEST); break;
			case 2:  //a
			case 7:  //w + a + s
				facing.set(SOUTH_WEST); break;
			case 4:  //s
			case 14: //a + s + d
				facing.set(SOUTH_EAST); break;
			case 8:  //d
			case 13: //w + s + d
				facing.set(NORTH_EAST); break;
			case 3:  //w + a
				facing.set(WEST ); break;
			case 6:  //a + s
				facing.set(SOUTH); break;
			case 12: //s + d
				facing.set(EAST ); break;
			case 9:  //w + d
				facing.set(NORTH); break;
		}		
		super.onUpdate(context);
	}
}
