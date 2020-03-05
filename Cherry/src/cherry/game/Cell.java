package cherry.game;

import static cherry.game.Tile.localToPixel;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import blue.core.Renderable;
import blue.core.Updateable;
import blue.geom.Vector2;

public class Cell implements Renderable, Updateable {
	public Room
		room;
	public Vector2
		local,
		pixel;
	public Tile
		tile,
		wall;
	public List<Entity>
		entities;
	
	public boolean
		m_flag,
		s_flag;
	
	public Cell(int i, int j, Room room) {
		this.room = room;
		this.local = new  Vector2(i, j);
		this.pixel = localToPixel(i, j);
		this.entities = new LinkedList<>();
	}
	
	public void add_entity(Entity entity) {
		entities.add   (entity);
		entity.cell = this;
	}
	
	public void del_entity(Entity entity) {
		entities.remove(entity);
		entity.cell = null;
	}
	
	public int i() { return (int)local.x(); }
	public int j() { return (int)local.y(); }
	
	public float x() { return pixel.x(); }
	public float y() { return pixel.y(); }
	
	public void clear() {
		entities.clear();
		tile = null;
		wall = null;
	}

	@Override
	public void onRender(RenderContext context) {
		//do nothing
	}

	@Override
	public void onUpdate(UpdateContext context) {
		for(Entity entity: entities) {
			context.update(entity);
			if(
					i() != entity.i() ||
					j() != entity.j() )
				m_flag= true;
		}
	}
	
	public void handle_m_flag() {
		for(Entity entity: entities)
			if(
					i() != entity.i() ||
					j() != entity.j() ){
				Cell cell = room.get_cell(
						entity.i(), 
						entity.j()
						);
				if(cell != null) {
					this.entities.remove(entity);
					cell.entities.add   (entity);
					
					entity.cell = cell                    ;
					cell.s_flag = cell.entities.size() > 1;
				}
			}
		m_flag = false;
	}
	
	protected static final Comparator<Entity>
		z_order = (Entity a, Entity b) -> {
			if(a.y() + a.size < b.y() + b.size) return -1;
			if(a.y() + a.size > b.y() + b.size) return  1;
			if(a.x() < b.x()) return -1;
			if(a.x() > b.x()) return  1;
			return 0;
		};
	
	public void handle_s_flag() {
		Collections.sort(
				entities,
				z_order
				);
		s_flag = false;
	}
}
