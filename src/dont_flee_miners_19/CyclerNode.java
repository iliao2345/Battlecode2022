package dont_flee_miners_19;

public class CyclerNode<T> {
	public T data;
	public CyclerNode<T> last;
	public CyclerNode<T> next;
	public CyclerNode(T data, CyclerNode<T> other) {  // adds new T to the cycle
		this.data = data;
		if (other!=null) {
			last = other.last;
			next = other;
			last.next = this;
			next.last = this;
		}
		else {
			last = this;
			next = this;
		}
	}
	public CyclerNode<T> pop() {
		if (next==this) {
			return null;
		}
		last.next = next;
		next.last = last;
		return next;
	}
}
