package cherry.util;

import java.util.Collection;
import java.util.Iterator;

public interface Reverse<T> {
	
	public ReverseIterable<T> reverse();
	
	public static class ReverseIterable<T> implements Iterable<T> {
		private final Object[]
			list;
		
		public ReverseIterable(Collection<T> list) {
			this.list = list.toArray();
		}
		
		public ReverseIterable(T[] list) {
			this.list = list;
		}

		@Override
		public ReverseIterator<T> iterator() {
			return new ReverseIterator<T>(this);
		}
	}
	
	
	public static class ReverseIterator<T> implements Iterator<T> {		
		private Object[]
			list;
		private int
			a,
			b;		
		
		public ReverseIterator(ReverseIterable<T> reverse) {
			init(reverse.list);
		}		
		
		public ReverseIterator(Collection<T> list) {
			init(list.toArray());
		}
		
		public ReverseIterator(T[] list) {
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
			return (T)list[-- b];
		}	
	}
}