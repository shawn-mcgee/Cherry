package cherry;

import blue.core.Engine;
import blue.util.Version;

public class Cherry {
	public static final Version
		VERSION = new Version("Cherry", 0, 0, 1);
	
	public static void main(String[] args) {
		Engine.getConfiguration().set(Engine.WINDOW_TITLE, VERSION);
		System.out.println(VERSION);
		
		Engine.init();
	}
}
