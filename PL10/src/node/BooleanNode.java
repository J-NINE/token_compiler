package node;

public class BooleanNode implements ValueNode{
	//public으로 변경-> interpreter에서 직접 호출
	public Boolean value;	//PL06까지는 value
	
	@Override
	
	public String toString(){
		return value ? "#T" : "#F";
	}
	
	public static BooleanNode FALSE_NODE = new BooleanNode(false);
	public static BooleanNode TRUE_NODE = new BooleanNode(true);
	
	//private until PL06
	public BooleanNode(Boolean b){
		value = b;
	}
	
	
}
