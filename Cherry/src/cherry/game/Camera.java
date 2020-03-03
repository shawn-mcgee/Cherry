package cherry.game;

import blue.core.Renderable;
import blue.core.Updateable;
import blue.geom.Vector;
import blue.geom.Vector2;

public class Camera implements Renderable, Updateable {
	public final Vector2.Mutable
		camera_t = new Vector2.Mutable(0f, 0f),
		camera_s = new Vector2.Mutable(1f, 1f),
		target_t = new Vector2.Mutable(0f, 0f),
		target_s = new Vector2.Mutable(1f, 1f),
		tween = new Vector2.Mutable();
	public Renderable
		renderable;

	@Override
	public void onRender(RenderContext context) {
		context.push();
			context.mov(
					(int)(camera_t.x() * 1  ) / 1  ,
					(int)(camera_t.y() * 1  ) / 1  );
			context.sca(
					(int)(camera_s.x() * 100) / 100,
					(int)(camera_s.y() * 100) / 100);
			context.render(renderable);		
		context.pop();
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		if(!camera_t.equals(target_t)) {
			float
				dx = (target_t.x() - camera_t.x()) * (1f - (float)Math.pow(tween.x(), context.fixed_dt)),
				dy = (target_t.y() - camera_t.y()) * (1f - (float)Math.pow(tween.y(), context.fixed_dt));
			Vector.m_add(camera_t, dx, dy);
		}
		if(!camera_s.equals(target_s)) {
			float
				dx = (target_s.x() - camera_s.x()) * (1f - (float)Math.pow(tween.x(), context.fixed_dt)),
				dy = (target_s.y() - camera_s.y()) * (1f - (float)Math.pow(tween.y(), context.fixed_dt));
			Vector.m_add(camera_s, dx, dy);
		}
	}
	
	public void set_camera(Vector2 t) {
		set_camera(t.x(), t.y());
	}
	
	public void set_camera(float tx, float ty) {
		camera_t.set(tx, ty);		
		target_t.set(tx, ty);
	}
	
	public void set_camera(Vector2 t, Vector2 s) {
		set_camera(t.x(), t.y(), s.x(), s.y());
	}
	
	public void set_camera(float tx, float ty, float sx, float sy) {
		camera_t.set(tx, ty);
		camera_s.set(sx, sy);
		
		target_t.set(tx, ty);
		target_s.set(sx, sy);
	}
	
	public void mov_camera(Vector2 t) {
		mov_camera(t.x(), t.y());
	}
	
	public void mov_camera(float tx, float ty) {
		Vector.m_add(camera_t, tx, ty);
		Vector.m_add(target_t, tx, ty);
	}
	
	public void sca_camera(Vector2 s) {
		sca_camera(s.x(), s.y());
	}
	
	public void sca_camera(float sx, float sy) {
		Vector.m_mul(camera_s, sx, sy);
		Vector.m_mul(target_s, sx, sy);
	}	
	
	public void sca_camera(Vector2 v0, Vector2 s) {
		sca_target(v0, s.x(), s.y());
	}
	
	public void sca_camera(Vector2 v0, float sx, float sy) {
		Vector2
			v1 = new Vector2(
					(v0.x() - camera_t.x()) / camera_s.x(),
					(v0.y() - camera_t.y()) / camera_s.y()),
			v2 = new Vector2(
					(v1.x() * sx) + camera_t.x(),
					(v1.y() * sy) + camera_t.y());
		float
			tx = camera_t.x() + v0.x() - v2.x(),
			ty = camera_t.y() + v0.y() - v2.y();	
		
		camera_t.set(tx, ty);
		camera_s.set(sx, sy);

		target_t.set(tx, ty);
		target_s.set(sx, sy);
	}
	
	public void set_target(Vector2 t) {
		set_target(t.x(), t.y());
	}
	
	public void set_target(float tx, float ty) {
		target_t.set(tx, ty);
	}
	
	public void set_target(Vector2 t, Vector2 s) {
		set_target(t.x(), t.y(), s.x(), s.y());
	}
	
	public void set_target(float tx, float ty, float sx, float sy) {
		target_t.set(tx, ty);
		target_s.set(sx, sy);
	}
	
	public void mov_target(Vector2 t) {
		mov_target(t.x(), t.y());
	}
	
	public void mov_target(float tx, float ty) {
		Vector.m_add(target_t, tx, ty);
	}
	
	public void sca_target(Vector2 s) {
		sca_target(s.x(), s.y());
	}
	
	public void sca_target(float sx, float sy) {
		Vector.m_mul(target_s, sx, sy);
	}	
	
	public void sca_target(Vector2 v0, Vector2 s) {
		sca_target(v0, s.x(), s.y());
	}
	
	public void sca_target(Vector2 v0, float sx, float sy) {
		Vector2
			v1 = new Vector2(
					(v0.x() - camera_t.x()) / camera_s.x(),
					(v0.y() - camera_t.y()) / camera_s.y()),
			v2 = new Vector2(
					(v1.x() * sx) + camera_t.x(),
					(v1.y() * sy) + camera_t.y());
		float
			tx = camera_t.x() + v0.x() - v2.x(),
			ty = camera_t.y() + v0.y() - v2.y();		
	
		target_t.set(tx, ty);
		target_s.set(sx, sy);
	}
	
	public final Vector2 mouseToPixel(float x, float y) {
		return new Vector2(
			(x - camera_t.x()) / camera_s.x(),
			(y - camera_t.y()) / camera_s.y()
			);
	}
	
	public final Vector2 pixelToMouse(float x, float y) {
		return new Vector2(
			(x * camera_s.x()) + camera_t.x(),
			(y * camera_s.y()) + camera_t.y()
			);
	}
	
	public Vector2 mouseToPixel(Vector mouse) {
		return mouseToPixel(mouse.x(), mouse.y());
	}
	
	public Vector2 pixelToMouse(Vector pixel) {
		return pixelToMouse(pixel.x(), pixel.y());
	}
}
