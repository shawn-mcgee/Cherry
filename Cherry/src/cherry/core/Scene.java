package cherry.core;

import blue.core.Engine;
import blue.core.Input;
import blue.geom.Vector2;

public class Scene extends blue.core.Scene {
	public final Node
		root = new Node();
	
	@Override
	public void onRender(RenderContext context) {
		root.render(context);
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		root.update(context);
	}
	
	@Override
	public void onAttach() {
		root.resize(Engine.canvas());
		root.attach();
	}
	
	@Override
	public void onDetach() {
		root.detach();
	}
	
	@Override
	public void onResize() {
		root.resize(Engine.canvas());
	}
	
	@Override
	public void onMouseMoved(Vector2 mouse) { 
		root.mouseMoved(mouse);
	}
	
	@Override
	public void onWheelMoved(float   wheel) { 
		root.wheelMoved(Input.getMouse(), wheel);
	}
	
	@Override
	public void onKeyDn(int key) { 
		root.keyDn(key);
	}
	
	@Override
	public void onKeyUp(int key) { 
		root.keyUp(key);
	}	
	
	@Override
	public void onBtnDn(int btn) { 
		root.btnDn(Input.getMouse(), btn);
	}
	
	@Override
	public void onBtnUp(int btn) { 
		root.btnUp(Input.getMouse(), btn);
	}
}
