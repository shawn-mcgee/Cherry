package cherry.ui;

import java.util.LinkedList;
import java.util.List;

import blue.core.Renderable.RenderContext;
import blue.core.Updateable.UpdateContext;
import blue.math.Box;
import blue.math.Layout;
import blue.math.Region2;
import blue.math.Vector2;
import cherry.util.Forward;
import cherry.util.Forward.ForwardIterable;
import cherry.util.Reverse;
import cherry.util.Reverse.ReverseIterable;

public class Node implements Forward<Node>, Reverse<Node> {
	protected final Layout.Mutable
		layout = new Layout.Mutable();
	protected final Region2.Mutable
		bounds = new Region2.Mutable();
	
	protected final List<Node>
		list = new LinkedList<>();
	protected ForwardIterable<Node>
		forward;
	protected ReverseIterable<Node>
		reverse;
	
	protected Node
		parent,
		focus ;
	
	public void add(Node node) {
		node.parent = this;
		list.add   (node);
		forward = null;
		reverse = null;
		node.attach();
	}
	
	public void del(Node node) {
		node.parent = null;
		list.remove(node);
		forward = null;
		reverse = null;
		node.detach();
	}
	
	public void render(RenderContext context) {
		onRender(context);
		for(Node node: forward())
			node.render(context);
	}
	
	public void update(UpdateContext context) {
		onUpdate(context);
		for(Node node: reverse())
			node.update(context);
	}
	
	public void resize(Box<?> parent) {
		layout.m_region(parent, bounds);
		onResize();		
		for(Node node: forward())
			node.resize(bounds);		
	}
	
	public void attach() {
		onAttach();
		for(Node node: forward())
			node.attach();
	}
	
	public void detach() {
		for(Node node: forward())
			node.detach();
		onDetach();
	}
	
	public void gainFocus() {
		if(parent != null) {
			if(parent.focus != null) {
				parent.gainFocus() ;
				parent.focus = this;
				
				parent.list.remove(this);
				parent.list.add   (this);
				parent.forward = null;
				parent.reverse = null;
				
				onGainFocus();
			} else {
				if(parent.focus != this) {
					parent.gainFocus() ;
					parent.loseFocus() ;
					parent.focus = this;
					
					parent.list.remove(this);
					parent.list.add   (this);
					parent.forward = null;
					parent.reverse = null;
					
					onGainFocus();
				} else
					loseFocus();
			}
		} else
			loseFocus();
	}
	
	public void loseFocus() {
		if(focus != null) {
			focus.  loseFocus();
			focus.onLoseFocus();
			focus = null;
		}
	}
	
	public void keyDn(int key) {
		if(focus != null)
			focus.keyDn(key);
		else
			onKeyDn(key);
	}
	
	public void keyUp(int key) {
		if(focus != null)
			focus.keyUp(key);
		else
			onKeyUp(key);
	}
	
	public boolean mouseMoved(Vector2 mouse) {
		boolean b = bounds.contains(mouse, true);
		if(b) {
			for(Node node: reverse())
				if(node.mouseMoved(mouse))
					return true;
			onMouseMoved(mouse);
		}
		return b;
	}
	
	public boolean wheelMoved(Vector2 mouse, float wheel) {
		boolean b = bounds.contains(mouse, true);
		if(b) {
			for(Node node: reverse())
				if(node.wheelMoved(mouse, wheel))
					return true;
			onWheelMoved(wheel);
		}
		return b;
	}
	
	public boolean btnDn(Vector2 mouse, int btn) {
		boolean b = bounds.contains(mouse, true);
		if(b) {
			for(Node node: reverse())
				if(node.btnDn(mouse, btn))
					return true;
			onBtnDn(btn);
		}
		return b;
	}
	
	public boolean btnUp(Vector2 mouse, int btn) {
		boolean b = bounds.contains(mouse, true);
		if(b) {
			for(Node node: reverse())
				if(node.btnUp(mouse, btn))
					return true;
			onBtnUp(btn);
		}
		return b;
	}
	
	public void onRender(RenderContext context) { }
	public void onUpdate(UpdateContext context) { }
	public void onResize() { }
	public void onAttach() { }
	public void onDetach() { }
	public void onGainFocus() { }
	public void onLoseFocus() { }
	public void onKeyDn(int key) { }
	public void onKeyUp(int key) { }
	public void onMouseMoved(Vector2 mouse) { }
	public void onWheelMoved(float   wheel) { }
	public void onBtnDn(int btn) { }
	public void onBtnUp(int btn) { }

	@Override
	public ForwardIterable<Node> forward() {
		return forward != null ? forward : (forward = new ForwardIterable<>(list));
	}

	@Override
	public ReverseIterable<Node> reverse() {
		return reverse != null ? reverse : (reverse = new ReverseIterable<>(list));
	}
}
