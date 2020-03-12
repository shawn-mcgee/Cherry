package cherry.game;

import static cherry.game.Tile.localToPixel;
import static cherry.game.Tile.pixelToLocal;
import static cherry.game.Tile.snap;

import java.util.HashMap;
import java.util.Map;

import blue.core.Renderable;
import blue.core.Updateable;
import blue.geom.Vector;
import blue.geom.Vector2;
import blue.util.Util;

public class Entity implements Renderable, Updateable {
	public static final String
		MAXIMUM_HEALTH = "maximum_health",
		CURRENT_HEALTH = "current_health",
		MOVEMENT_SPEED = "movement_speed",
		SIZE           = "size";
	protected static final float
		SIN = (float)(Math.sin(Math.toRadians(45))),
		COS = (float)(Math.cos(Math.toRadians(45))),
		EPSILON = .001f,
		FIXED_DT = 1f / 60f;
	public static enum Facing {
		NORTH(-COS, -SIN),
		NORTH_EAST(0, -1),
		EAST ( COS, -SIN),
		SOUTH_EAST( 1, 0),
		SOUTH( COS,  SIN),
		SOUTH_WEST(0,  1),
		WEST (-COS,  SIN),
		NORTH_WEST(-1, 0);
		
		public final Vector2
			vector;
		
		Facing(float x, float y) {
			vector = new Vector2(x, y);
		}
	}
	
	protected Room
		room;
	protected Cell
		cell;
	
	public final Vector2.Mutable
		local = new Vector2.Mutable(),
		pixel = new Vector2.Mutable();
	public Facing
		facing = Facing.SOUTH;
	
	protected final Map<Class<?>, Map<String, Property<?>>>
		map = new HashMap<>();
	
	protected final IntegerProperty
		maximum_health = new IntegerProperty(1, 0, 1).attach(this, MAXIMUM_HEALTH),
		current_health = new IntegerProperty(1, 0, 1).attach(this, CURRENT_HEALTH);
	protected final FloatProperty
		movement_speed = new FloatProperty( 2f, 0f, 6f).attach(this, MOVEMENT_SPEED),
		size           = new FloatProperty(.2f, 0f,.5f).attach(this, SIZE          );
	
	public <T> Map<String, Property<?>> get(Class<T> type) {
		Map<String, Property<?>> m = map.get(type);
		if(m == null) 
				map.put(type, m = new HashMap<>());
		return m;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Property<T> get(Class<T> type, String name) {
		return (Property<T>)get(type).get(name);
	}
	
	public <T> void add(Class<T> type, String name, Property<T> prop) {
		get(type).put(name, prop);
	}
	
	public <T> void del(Class<T> type, String name, Property<T> prop) {
		if(prop != null)
			get(type).remove(name, prop);
		else
			get(type).remove(name);
	}
	
	public FloatProperty getFloatProperty(String name) {
		return (FloatProperty)get(Float.class, name);
	}
	
	public IntegerProperty getIntegerProperty(String name) {
		return (IntegerProperty)get(Integer.class, name);
	}
	
	public BooleanProperty getBooleanProperty(String name) {
		return (BooleanProperty)get(Boolean.class, name);
	}
	
	public void setPixel(Vector2 pixel) {
		setPixel(pixel.x(), pixel.y());
	}
	
	public void setPixel(float x, float y) {
		pixel.set(x, y);
		local.set(pixelToLocal(x, y));
	}
	
	public void setLocal(Vector2 local) {
		setLocal(local.x(), local.y());
	}
	
	public void setLocal(float i, float j) {
		local.set(i, j);
		pixel.set(localToPixel(i, j));
	}
	
	public Entity setRoom(Room room) {
		if(this.room != null)
			onLeaveRoom();
		this.room = room;
		if(this.room != null)
			onEnterRoom();
		return this;
	}
	
	public Entity setCell(Cell cell) {
		if(this.cell != null)
			onLeaveCell();
		this.cell = cell;
		if(this.cell != null)
			onEnterCell();
		return this;
	}
	
	public Room getRoom() {
		return this.room;
	}
	
	public Cell getCell() {
		return this.cell;
	}
	
	public void onEnterRoom() { }
	public void onLeaveRoom() { }
	public void onEnterCell() { }
	public void onLeaveCell() { }
	
	@Override
	public void onRender(RenderContext context) { }
	@Override
	public void onUpdate(UpdateContext context) { }
	
	
	
	public void move() {
		float
			speed = this.movement_speed.value(),
			dx = speed * facing.vector.x() * FIXED_DT,
			dy = speed * facing.vector.y() * FIXED_DT,
			size = this.size.value();
		
		int
			i0 = snap(local.x() - size + EPSILON),
			i1 = snap(local.x() + size - EPSILON),
			j0 = snap(local.y() - size + EPSILON),
			j1 = snap(local.y() + size - EPSILON),
			a = snap(local.x() + dx + Util.sign(dx) * size),
			b = snap(local.y() + dy + Util.sign(dy) * size);
		
		if(dx != 0) {
			Tile
				tile0 = room.tile(a, j0),
				tile1 = room.tile(a, j1);
			if(tile0 == null || tile1 == null)
				dx = a - local.x() + (dx >= 0 ? - size : size + 1);
		}
		
		if(dy != 0) {
			Tile
				tile0 = room.tile(i0, b),
				tile1 = room.tile(i1, b);
			if(tile0 == null || tile1 == null)
				dy = b - local.y() + (dy >= 0 ? - size : size + 1);
		}
		
		Vector.m_add( local,  dx,  dy);
		pixel.set(localToPixel(local));
		
		if(dx != 0 || dy != 0)
			cell.m_flag();
	}
	
	public static interface Property<T> {
		public T value();
	}
	
	public static class FloatProperty implements Property<Float> {
		public float
			value,
			delta,
			min,
			max;
		
		public FloatProperty() {
			this(0f, 0f, 1f);
		}
		
		public FloatProperty(float value) {
			this(value, 0f, 1f);
		}
		
		public FloatProperty(float value, float min, float max) {
			this.value = value;
			this.min = min;
			this.max = max;
		}

		@Override
		public Float value() {
			return Util.clamp(value + delta, min, max);
		}
		
		protected FloatProperty attach(Entity self, String name) {
			self.add(Float.class, name, this);
			return this;
		}
	}
	
	public static class IntegerProperty implements Property<Integer> {
		public int
			value,
			delta,
			cache,
			min,
			max;
		
		public IntegerProperty() {
			this(0, 0, 1);
		}
		
		public IntegerProperty(int value) {
			this(value, 0, 1);
		}
		
		public IntegerProperty(int value, int min, int max) {
			this.value = value;
			this.min = min;
			this.max = max;
		}
		
		@Override
		public Integer value() {
			return Util.clamp(value + delta, min, max);
		}
		
		protected IntegerProperty attach(Entity self, String name) {
			self.add(Integer.class, name, this);
			return this;
		}
	}
	
	public static class BooleanProperty implements Property<Boolean> {
		public boolean
			value;
		
		public BooleanProperty() {
			this(false);
		}
		
		public BooleanProperty(boolean value) {
			this.value = value;
		}
		
		@Override
		public Boolean value() {
			return value;
		}
		
		protected BooleanProperty attach(Entity self, String name) {
			self.add(Boolean.class, name, this);
			return this;
		}
	}	
}
