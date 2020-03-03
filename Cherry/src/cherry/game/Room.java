package cherry.game;

import java.util.LinkedList;
import java.util.List;

import blue.core.Renderable;
import blue.core.Updateable;
import blue.geom.Vector2;
import blue.util.Util;

public class Room implements Renderable, Updateable {
	public static final int
		DEFAULT_ROOM_W = 16,
		DEFAULT_ROOM_H = 16;
	protected int
		w,
		h;	
	protected Cell[][]
		grid;
	
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
	}
	
	public int w() { return w; }
	public int h() { return h; }
	
	public void clear() {
		for(int i = 0; i < w; i ++)
			for(int j = 0; j < h; j ++)
				grid[i][j].clear();
	}
	
	public void set_tile(int i, int j, String string) {
		Cell cell = get_cell(i, j);
		if(cell != null) {
			if(string != null) {
				if(cell.tile != null)
					if(cell.tile.string.equals(string))
						return;
				cell.tile = Tile.load_tile(string);
			} else
				cell.tile = null;
		}
	}
	
	public void set_wall(int i, int j, String string) {
		Cell cell = get_cell(i, j);
		if(cell != null) {
			if(string != null) {
				if(cell.wall != null)
					if(cell.wall.string.equals(string))
						return;				
				cell.wall = Tile.load_wall(string);
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
	
	@Override
	public void onRender(RenderContext context) {
		for(int j = 0; j < h; j ++)
			for(int i = 0; i < w; i ++)
				grid[i][j].onRender(context);
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		
	}
	
	public static void resize(Room room, int w, int h) {
		int
			_w = w > 0 ? w : 1,
			_h = h > 0 ? h : 1;		
		if(_w != room.w || _h != room.h) {
			int
				min_w = Math.min(_w, room.w),
				min_h = Math.min(_h, room.h);
			Cell[][]
				grid = new Cell[_w][_h];
			for(int i = 0; i < _w; i ++)
				for(int j = 0; j < _h; j ++) {
					if(i < min_w && j < min_h)
						grid[i][j] = room.grid[i][j];
					else
						grid[i][j] = new Cell(i, j, room);
				}
			room.w = _w;
			room.h = _h;
			room.grid = grid;
		}
	}
	
	public static void increment(Room room) {
		resize(room, room.w + 1, room.h + 1);
	}
	
	public static void decrement(Room room) {
		resize(room, room.w - 1, room.h - 1);
	}
	
	public static void offset(Room room, int dx, int dy) {
		Cell[][] 
			grid = new Cell[room.w][room.h];
		for(int i = 0; i < room.w; i ++)
			for(int j = 0; j < room.h; j ++) {
				int
					a = (i + dx + room.w) % room.w,
					b = (j + dy + room.h) % room.h;			
				grid[a][b] = room.grid[i][j];
			}
		room.grid = grid;
	}
	
	public static void rotate_r(Room room) {
		int
			_w = room.w,
			_h = room.h;
		Cell[][] 
			grid = new Cell[_h][_w];
		for(int i = 0; i < _w; i ++)
			for(int j = 0; j < _h; j ++) {
				grid[_h - j - 1][i] = room.grid[i][j];
			}
		room.w = _h;
		room.h = _w;
		room.grid = grid;
	}
	
	public static void rotate_l(Room room) {
		int
			_w = room.w,
			_h = room.h;
		Cell[][]
			grid = new Cell[_h][_w];
		for(int i = 0; i < _w; i ++)
			for(int j = 0; j < _h; j ++) {
				grid[j][_w - i - 1] = room.grid[i][j];
			}
		room.w = _h;
		room.h = _w;
		room.grid = grid;
	}
	
	public static void mirror_y(Room room) {
		Cell[][]
			grid = new Cell[room.w][room.h];
		for(int i = 0; i < room.w; i ++)
			for(int j = 0; j < room.h; j ++) {
				grid[room.w - i - 1][j] = room.grid[i][j];
			}
		room.grid = grid;
	}
	
	public static void mirror_x(Room room) {
		Cell[][]
			grid = new Cell[room.w][room.h];
		for(int i = 0; i < room.w; i ++)
			for(int j = 0; j < room.h; j ++) {
				grid[i][room.h - j - 1] = room.grid[i][j];
			}
		room.grid = grid;
	}
	
	public static void save(Room room, String path) {
		List<String> list = new LinkedList<>();
		for(int i = 0; i < room.w; i ++)
			for(int j = 0; j < room.h; j ++) {
				String
					cell = i + ", " + j + ":",
					data = "";
				Tile tile = room.get_tile(i, j);
				if(tile != null)
					data += (data.isEmpty() ? "" : ", ") + "tile=" + tile.string;
				Tile wall = room.get_wall(i, j);
				if(wall != null)
					data += (data.isEmpty() ? "" : ", ") + "wall=" + wall.string;
				
				if(!data.isEmpty()) {
					list.add(cell + data);
				}
			}
		Util.printToFile(path, false, list);
	}
	
	public static void open(Room room, String path) {
		List<String> list = Util.parseFromFile(path, new LinkedList<>());		
		room.clear();
		
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
						if(i >= room.w || j >= room.w)
							resize(
									room, 
									Math.max(room.w, i + 1), 
									Math.max(room.h, j + 1)
									);
						if(tile != null)
							room.set_tile(i, j, tile);
						if(wall != null)
							room.set_wall(i, j, wall);
					}
				}
			}
		}
	}
}
