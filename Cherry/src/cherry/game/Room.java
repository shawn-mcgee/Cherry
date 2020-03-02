package cherry.game;

import blue.core.Renderable;
import blue.core.Updateable;

public class Room implements Renderable, Updateable {
	public static final int
		DEFAULT_ROOM_W = 16,
		DEFAULT_ROOM_H = 16;
	protected int
		room_w,
		room_h;	
	protected Cell[][]
		grid;
	
	public Room() {
		this(
				DEFAULT_ROOM_W,
				DEFAULT_ROOM_H
				);
	}
	
	public Room(int w, int h) {
		room_w = w > 0 ? w : 1;
		room_h = h > 0 ? h : 1;
		grid = new Cell[room_w][room_h];
		for(int i = 0; i < room_w; i ++)
			for(int j = 0; j < room_h; j ++)
				grid[i][j] = new Cell(i, j, this);
	}
	
	public int room_w() { return room_w; }
	public int room_h() { return room_h; }
	
	public void resize(int w, int h) {
		int
			_w = w > 0 ? w : 1,
			_h = h > 0 ? h : 1;		
		if(_w != room_w || _h != room_h) {
			int
				min_w = Math.min(_w, room_w),
				min_h = Math.min(_h, room_h);
			Cell[][]
				_grid = new Cell[_w][_h];
			for(int i = 0; i < _w; i ++)
				for(int j = 0; j < _h; j ++) {
					if(i < min_w && j < min_h)
						_grid[i][j] = grid[i][j];
					else
						_grid[i][j] = new Cell(i, j, this);
				}
			room_w = _w;
			room_h = _h;
			grid = _grid;
		}
	}
	
	public void set_tile(int i, int j, String name) {
		Cell cell = get_cell(i, j);
		if(cell != null) {
			if(name != null) {
				if(cell.tile != null)
					if(cell.tile.name.equals(name))
						return;
				cell.tile = Tile.getByName(name);
			} else
				cell.tile = null;
		}
	}
	
	public void set_wall(int i, int j, String name) {
		Cell cell = get_cell(i, j);
		if(cell != null) {
			if(name != null) {
				if(cell.wall != null)
					if(cell.wall.name.equals(name))
						return;				
				cell.wall = Tile.getByName(name);	
			} else
				cell.wall = null;
		}
	}
	
	public Cell get_cell(int i, int j) {
		if(
				i >= 0 && i < room_w &&
				j >= 0 && j < room_h ) {
			return grid[i][j];
		} else
			return null;
	}
	
	public Tile get_tile(int i, int j) {
		if(
				i >= 0 && i < room_w &&
				j >= 0 && j < room_h ) {
			return grid[i][j].tile;
		} else
			return null;
	}
	
	public Tile get_wall(int i, int j) {
		if(
				i >= 0 && i < room_w &&
				j >= 0 && j < room_h ) {
			return grid[i][j].wall;
		} else
			return null;
	}
	
	@Override
	public void onRender(RenderContext context) {
		for(int j = 0; j < room_h; j ++)
			for(int i = 0; i < room_w; i ++)
				grid[i][j].onRender(context);
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		
	}	
}
