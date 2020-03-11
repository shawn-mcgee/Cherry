package cherry.ui;

import java.util.Set;
import java.util.TreeSet;

import blue.core.Renderable;
import blue.core.Updateable;
import blue.geom.Box;
import blue.geom.Layout;
import blue.geom.Region2;
import blue.geom.Vector2;
import blue.util.Forward;
import blue.util.Reverse;

public class Node implements Renderable, Updateable, Forward<Node>, Reverse<Node> {
	private final Region2.Mutable
		bounds = new Region2.Mutable();
	private Layout
		layout;
	private Node
		parent;
	
	private final Set<Node>
		tree = new TreeSet<>(),
		attach = new TreeSet<>(),
		detach = new TreeSet<>();
	
	public Box<?> bounds() {
		return bounds;
	}
	
	public Layout layout() {
		return layout;
	}
	
	public Node parent() {
		return parent;
	}
	
	public void setBounds(Box<?> bounds) {
		this.bounds.set(bounds);
		if(this.parent != null)
			resize(this.parent.bounds);
	}
	
	public void setLayout(Layout layout) {
		this.layout = layout;
		if(this.parent != null)
			resize(this.parent.bounds);
	}
	
	public boolean add(Node node) {
		if(node.parent == null && tree.add   (node)) {
			node.parent = this;
			node.attach();
			return true;
		}
		return false;
	}
	
	public boolean del(Node node) {
		if(node.parent == this && tree.remove(node)) {
			node.parent = null;
			node.detach();
			return true;
		}
		return false;
	}
	
	public void attach(Node node) {
		attach.add(node);
	}
	
	public void detach(Node node) {
		detach.add(node);
	}
	
	public void resize(Box<?> parent) {
		if(layout != null) 
			layout.m_region(
					bounds,
					parent
					);
		onResize();
		for(Node node: tree)
			node.resize(bounds);
	}
	
	public void attach() {
		for(Node node: attach)
			add(node);
		attach.clear();
	}
	
	public void detach() {
		for(Node node: detach)
			del(node);
		detach.clear();
	}
	
	public void gainFocus() {
		
	}
	
	public void loseFocus() {
		
	}
	
	public void mouseMoved(Vector2 mouse) {
		if(bounds.contains(mouse, true)) {
			onMouseMoved(mouse);
			for(Node node: tree)
				node.mouseMoved(mouse);
		}
	}
	
	public void wheelMoved(Vector2 mouse, float   wheel) {
		if(bounds.contains(mouse, true)) {
			onWheelMoved(wheel);
			for(Node node: tree)
				node.wheelMoved(mouse, wheel);
		}
	}
	
	public void btnDn(Vector2 mouse, int btn) {
		if(bounds.contains(mouse, true)) {
			onBtnDn(btn);
			for(Node node: tree)
				node.btnDn(mouse, btn);
		}
	}
	
	public void btnUp(Vector2 mouse, int btn) {
		if(bounds.contains(mouse, true)) {
			onBtnUp(btn);
			for(Node node: tree)
				node.btnUp(mouse, btn);
		}
	}
	
	@Override
	public void onRender(RenderContext context) { }
	@Override
	public void onUpdate(UpdateContext context) { }

	public void onResize() { }
	public void onAttach() { }
	public void onDetach() { }
	public void onGainFocus() { }
	public void onLoseFocus() { }
	public void onMouseMoved(Vector2 mouse) { }
	public void onWheelMoved(float   wheel) { }
	public void onKeyDn(int key) { }
	public void onKeyUp(int key) { }
	public void onBtnDn(int btn) { }
	public void onBtnUp(int btn) { }
	
	@Override
	public ForwardIterable<Node> forward() {
		return new ForwardIterable<>(tree);
	}
	
	@Override
	public ReverseIterable<Node> reverse() {
		return new ReverseIterable<>(tree);
	}
}
