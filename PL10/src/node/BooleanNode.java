package node;

public class BooleanNode implements ValueNode{
	//public���� ����-> interpreter���� ���� ȣ��
	public Boolean value;	//PL06������ value
	
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
