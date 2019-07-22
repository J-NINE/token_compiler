package parser;

import java.io.PrintStream;
import java.util.StringTokenizer;

import node.ListNode;
import node.Node;
import node.QuoteNode;

public class NodePrinter {
	PrintStream ps;
	public static NodePrinter getPrinter(PrintStream ps) {
		return new NodePrinter(ps);
	}

	private NodePrinter(PrintStream ps) {
		this.ps = ps;
	}
	
	// ListNode, QuoteNode, Node�� ���� printNode �Լ��� ���� overload ��������

	//ListNode
	private void printNode(ListNode listNode) {
		//empty or end ListNode
		if (listNode == ListNode.EMPTYLIST) {
			ps.print("( ! ) ");
			return;
		}
		if (listNode == ListNode.ENDLIST) {
			return;
		}

		//��� ���� �ʴٸ�
		//list�� ���� ���
		//headNode�� ������־�� �Ѵ� 
		printNode(listNode.car());
		
		//������Ʈ ����
		if(listNode.cdr().equals(ListNode.EMPTYLIST)) {
			ps.print("( )");
		}
		
		printNode(listNode.cdr());
	}
	
	//QuoteNode
	//����?
	//private void printNode(QuoteNode quoteNode) {
		private void printNode(QuoteNode quoteNode) {
			if(quoteNode.nodeInside() == null)
				return;
			ps.print("'");
			printNode(quoteNode.nodeInside());
		}
	//}
	
	//Node
	//prettyPrint���� �켱������ �۵��ϴ� �κ�
	private void printNode(Node node) {
		if (node == null)
			return;
		//��� ���� �ʴٸ�
		
		//node�� Listnode���
		if(node instanceof ListNode) {
			ps.print("(");
			printNode((ListNode)node);
			ps.print(")");
		} else if (node instanceof QuoteNode){
			//�ڵ忡�� �߰���
			printNode((QuoteNode) node);
		}else {
			/*
			String temp = node.toString();
			StringTokenizer st = new StringTokenizer(temp,  " ");
			
			st.nextToken();
			ps.print(" " + st.nextToken());
			*/
			ps.print(node);
		}
		
	}
	// ���� �κ��� �־��� ��� ���Ŀ� �°� �ڵ带 �ۼ��Ͻÿ�. }
	public void prettyPrint(Node node){
		printNode(node);
	}
}
	

