package node;

public class IntNode implements ValueNode{
	private Integer value;
	
	@Override
	public String toString(){
		return value + " ";
	}
	
	public IntNode(String text) {
		this.value = new Integer(text);
	}
	
	public Integer value() {
		return this.value;
	}

}
