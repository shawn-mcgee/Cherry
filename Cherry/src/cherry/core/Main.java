package cherry.core;

import blue.core.Engine;
import cherry.Cherry;

public class Main {

	public static void main(String[] args) {		
		Engine.init();
		
		Cherry.load_sprites("sprites/index");
		Cherry.index_tiles("tiles/index");
		Cherry.index_walls("walls/index");
		
		Engine.setScene(new Editor());
		
		
	}
}
