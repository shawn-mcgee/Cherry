package cherry.util;

import java.util.Collection;
import java.util.Iterator;

public interface Forward<T> {
	
	public ForwardIterable<T> forward();
	
	public static class ForwardIterable<T> implements Iterable<T> {
		private final Object[]
			list;
		
		public ForwardIterable(Collection<T> list) {
			this.list = list.toArray();
		}
		
		public ForwardIterable(T[] list) {
			this.list = list;
		}

		@Override
		public ForwardIterator<T> iterator() {
			return new ForwardIterator<T>(this);
		}
	}
	
	
	public static class ForwardIterator<T> implements Iterator<T> {		
		private Object[]
			list;
		private int
			a,
			b;
		
		public ForwardIterator(ForwardIterable<T> forward) {
			init(forward.list);
		}		
		
		public ForwardIterator(Collection<T> list) {
			init(list.toArray());
		}
		
		public ForwardIterator(T[] list) {
			init(list);
		}
		
		private void init(Object[] list) {
			this.list = list;
			
			this.a =           0;
			this.b = list.length;
		}
	
		@Override
		public boolean hasNext() {
			return a < b;
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			return (T)list[a ++];
		}	
	}
}