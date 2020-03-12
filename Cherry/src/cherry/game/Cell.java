package cherry.game;

import static cherry.game.Tile.localToPixel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import blue.geom.Vector2;

public class Cell implements Iterable<Entity> {
	public final Vector2
		local,
		pixel;
	public final Room
		room;
	
	protected Tile
		tile,
		wall;
	protected final List<Entity>
		list,
		attach,
		detach;
	
	protected boolean
		m_flag,
		s_flag;
	
	public Cell(int i, int j, Room room) {
		this.room = room;
		this.local = new  Vector2(i, j);
		this.pixel = localToPixel(i, j);
		
		this.list = new LinkedList<>();
		this.attach = new LinkedList<>();
		this.detach = new LinkedList<>();
	}
	
	public void add(Entity e) {
		if(e.cell != this && list.add   (e)) {
			e.setCell(this);
			s_flag();
		}
	}
	
	public void del(Entity e) {
		if(list.remove(e) && e.cell == this) {
			e.setCell(null);
		}
	}
	
	public void attach(Entity e) {
		attach.add(e);
	}
	
	public void detach(Entity e) {
		detach.add(e);
	}
	
	public void attach() {
		for(Entity e: attach)
			add(e);
		attach.clear();
	}
	
	public void detach() {
		for(Entity e: detach)
			del(e);
		detach.clear();
	}
	
	public void m_flag() { m_flag = list.size() > 0; }
	public void s_flag() { s_flag = list.size() > 1; }
	
	public void clear() {
		list.clear();
		tile = null;
		wall = null;
	}

	@Override
	public Iterator<Entity> iterator() {
		return list.iterator();
	}
}
