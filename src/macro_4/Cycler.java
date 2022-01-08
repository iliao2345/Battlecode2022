package macro_4;

public class Cycler<T> {
	public CyclerNode<T> node = null;
	public int length = 0;
	
	public Cycler() {
	}
	
	public void pop() {
		node = node.pop();
		length--;
	}
	
	public void push(T data) {
		node = new CyclerNode(data, node);
		length++;
	}
}
