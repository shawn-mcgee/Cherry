package cherry.game;

import java.util.LinkedList;
import java.util.List;

import blue.core.Updateable;
import blue.geom.Vector2;
import blue.util.Util;

public class Room implements Updateable {
	public static final int
		DEFAULT_ROOM_W = 16,
		DEFAULT_ROOM_H = 16;
	protected int
		w,
		h;
	protected Cell[][]
		grid;
	
	protected List<Entity>
		entities;
	
	public Room() {
		this(
				DEFAULT_ROOM_W,
				DEFAULT_ROOM_H
				);
	}
	
	public Room(int w, int h) {
		this.w = w > 0 ? w : 1;
		this.h = h > 0 ? h : 1;
		grid = new Cell[this.w][this.h];
		for(int i = 0; i < this.w; i ++)
			for(int j = 0; j < this.h; j ++)
				grid[i][j] = new Cell(i, j, this);
		this.entities = new LinkedList<>();
	}
	
	public void add_entity(Entity entity) {
		Cell cell = get_cell(entity.i(), entity.j());
		if(cell != null) {
			entities.add   (entity);
			cell.add_entity(entity);
			entity.room = this;
		}
	}
	
	public void del_entity(Entity entity) {
		Cell cell = get_cell(entity.i(), entity.j());
		if(cell != null) {
			entities.remove(entity);
			cell.del_entity(entity);
			entity.room = null;
		}
	}
	
	public int w() { return w; }
	public int h() { return h; }
	
	@Override
	public void onUpdate(UpdateContext context) {
		for(int j = 0; j < h; j ++)
			for(int i = 0; i < w; i ++)
				context.update(grid[i][j]);
		for(int j = 0; j < h; j ++)
			for(int i = 0; i < w; i ++)
				if(grid[i][j].m_flag)
					grid[i][j].handle_m_flag();
		for(int j = 0; j < h; j ++)
			for(int i = 0; i < w; i ++)
				if(grid[i][j].s_flag)
					grid[i][j].handle_s_flag();
	}
	
	public void set_tile(int i, int j, Tile tile) {
		Cell cell = get_cell(i, j);
		if(cell != null) {
			if(tile != null) {
				if(cell.tile != null)
					if(cell.tile.string.equals(tile.string))
						return;
				cell.tile = tile;
			} else
				cell.tile = null;
		}
	}
	
	public void set_wall(int i, int j, Tile wall) {
		Cell cell = get_cell(i, j);
		if(cell != null) {
			if(wall != null) {
				if(cell.wall != null)
					if(cell.wall.string.equals(wall.string))
						return;
				cell.wall = wall;
			} else
				cell.wall = null;
		}
	}
	
	public Cell get_cell(int i, int j) {
		if(
				i >= 0 && i < w &&
				j >= 0 && j < h ) {
			return grid[i][j];
		} else
			return null;
	}
	
	public Tile get_tile(int i, int j) {
		if(
				i >= 0 && i < w &&
				j >= 0 && j < h ) {
			return grid[i][j].tile;
		} else
			return null;
	}
	
	public Tile get_wall(int i, int j) {
		if(
				i >= 0 && i < w &&
				j >= 0 && j < h ) {
			return grid[i][j].wall;
		} else
			return null;
	}
	
	public void clear() {
		for(int i = 0; i < w; i ++)
			for(int j = 0; j < h; j ++)
				grid[i][j].clear();
	}
	
	public void resize(int w, int h) {
		int
			_w = w > 0 ? w : 1,
			_h = h > 0 ? h : 1;		
		if(_w != this.w || _h != this.h) {
			int
				min_w = Math.min(_w, this.w),
				min_h = Math.min(_h, this.h);
			Cell[][]
				grid = new Cell[_w][_h];
			for(int i = 0; i < _w; i ++)
				for(int j = 0; j < _h; j ++) {
					if(i < min_w && j < min_h)
						grid[i][j] = this.grid[i][j];
					else
						grid[i][j] = new Cell(i, j, this);
				}
			this.w = _w;
			this.h = _h;
			this.grid = grid;
		}
	}
	
	public void increment() {
		resize(this.w + 1, this.h + 1);
	}
	
	public void decrement() {
		resize(this.w - 1, this.h - 1);
	}
	
	public void translate(int dx, int dy) {
		Cell[][] 
			grid = new Cell[this.w][this.h];
		for(int i = 0; i < this.w; i ++)
			for(int j = 0; j < this.h; j ++) {
				int
					a = (i + dx + this.w) % this.w,
					b = (j + dy + this.h) % this.h;			
				grid[a][b] = this.grid[i][j];
			}
		this.grid = grid;
	}
	
	public void rotate_r() {
		int
			_w = this.w,
			_h = this.h;
		Cell[][] 
			grid = new Cell[_h][_w];
		for(int i = 0; i < _w; i ++)
			for(int j = 0; j < _h; j ++) {
				grid[_h - j - 1][i] = this.grid[i][j];
			}
		this.w = _h;
		this.h = _w;
		this.grid = grid;
	}
	
	public void rotate_l() {
		int
			_w = this.w,
			_h = this.h;
		Cell[][]
			grid = new Cell[_h][_w];
		for(int i = 0; i < _w; i ++)
			for(int j = 0; j < _h; j ++) {
				grid[j][_w - i - 1] = this.grid[i][j];
			}
		this.w = _h;
		this.h = _w;
		this.grid = grid;
	}
	
	public void mirror_y() {
		Cell[][]
			grid = new Cell[this.w][this.h];
		for(int i = 0; i < this.w; i ++)
			for(int j = 0; j < this.h; j ++) {
				grid[this.w - i - 1][j] = this.grid[i][j];
			}
		this.grid = grid;
	}
	
	public void mirror_x() {
		Cell[][]
			grid = new Cell[this.w][this.h];
		for(int i = 0; i < this.w; i ++)
			for(int j = 0; j < this.h; j ++) {
				grid[i][this.h - j - 1] = this.grid[i][j];
			}
		this.grid = grid;
	}
	
	public void save(String path) {
		List<String> list = new LinkedList<>();
		for(int i = 0; i < this.w; i ++)
			for(int j = 0; j < this.h; j ++) {
				String
					cell = i + ", " + j + ":",
					data = "";
				Tile tile = this.get_tile(i, j);
				if(tile != null)
					data += (data.isEmpty() ? "" : ", ") + "tile=" + tile.string;
				Tile wall = this.get_wall(i, j);
				if(wall != null)
					data += (data.isEmpty() ? "" : ", ") + "wall=" + wall.string;
				
				if(!data.isEmpty()) {
					list.add(cell + data);
				}
			}
		Util.printToFile(path, false, list);
	}
	
	public void load(String path) {
		List<String> list = Util.parseFromFile(path, new LinkedList<>());		
		this.clear();
		
		for(String line: list) {
			if(!line.trim().isEmpty()) {
				int
					k = line.indexOf(':');
				if(k >= 0) {
					String
						cell = line.substring(0, k),
						data = line.substring(++ k);
					Vector2 v = Vector2.parseVector2(cell);
					int
						i = (int)v.x(),
						j = (int)v.y();			
					
					String[] args0 = data.split("\\,");
					String
						tile = null,
						wall = null;					
					for(String arg0: args0) {
						String[] args1 = arg0.split("\\=");
						if(args1.length > 1) {
							String
								var = args1[0].trim(),
								val = args1[1].trim();
							switch(var) {
								case "tile": tile = val; break;
								case "wall": wall = val; break;
							}					
						}				
					}
					
					if(tile != null || wall != null) {	
						if(i >= this.w || j >= this.w)
							resize( 
									Math.max(this.w, i + 1), 
									Math.max(this.h, j + 1)
									);
						if(tile != null)
							this.set_tile(i, j, Tile.load_as_tile(tile));
						if(wall != null)
							this.set_wall(i, j, Tile.load_as_wall(wall));
					}
				}
			}
		}
	}
}
