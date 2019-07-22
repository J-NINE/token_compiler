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
	
	// ListNode, QuoteNode, Node에 대한 printNode 함수를 각각 overload 형식으로

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

		//비어 있지 않다면
		//list의 내용 출력
		//headNode를 출력해주어야 한다 
		printNode(listNode.car());
		
		//공리스트 방지
		if(listNode.cdr().equals(ListNode.EMPTYLIST)) {
			ps.print("( )");
		}
		
		printNode(listNode.cdr());
	}
	
	//QuoteNode
	//이중?
	//private void printNode(QuoteNode quoteNode) {
		private void printNode(QuoteNode quoteNode) {
			if(quoteNode.nodeInside() == null)
				return;
			ps.print("'");
			printNode(quoteNode.nodeInside());
		}
	//}
	
	//Node
	//prettyPrint에서 우선적으로 작동하는 부분
	private void printNode(Node node) {
		if (node == null)
			return;
		//비어 있지 않다면
		
		//node가 Listnode라면
		if(node instanceof ListNode) {
			ps.print("(");
			printNode((ListNode)node);
			ps.print(")");
		} else if (node instanceof QuoteNode){
			//코드에서 추가함
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
	// 이후 부분을 주어진 출력 형식에 맞게 코드를 작성하시오. }
	public void prettyPrint(Node node){
		printNode(node);
	}
}
	

