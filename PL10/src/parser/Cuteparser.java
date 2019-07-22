package parser;

import java.io.FileNotFoundException;
import java.util.Iterator;

import node.BinaryOpNode;
import node.BooleanNode;
import node.FunctionNode;
import node.IdNode;
import node.IntNode;
import node.ListNode;
import node.Node;
import node.QuoteNode;
import parser.Scanner;
import parser.Token;
import parser.TokenType;

public class Cuteparser {
	private Iterator<Token> tokens;
	private static Node END_OF_LIST = new Node(){};
	
	//token���� ����
	public Cuteparser(String file) {
		try {
			tokens = Scanner.scan(file);
		} catch (Exception e) {
			
		}
	}
	
	private Token getNextToken() {
		if (!tokens.hasNext())
			return null;
		return tokens.next();
	}
	
	
	public Node parseExpr() {
		//nextToken�� ���ͼ�
		Token t = getNextToken();
		//���� ���
		if (t == null) {
			System.out.println("No more token");
			return null;
		}
		
		//���� ���
		TokenType tType = t.type();
		String tLexeme = t.lexme();
		
		//tokenType�� ���� typeNode���� ��ȯ
		switch (tType) {
			case ID:
				return new IdNode(tLexeme);
			case INT:
				if (tLexeme == null)
					System.out.println("???");
				return new IntNode(tLexeme);
				// ���� ������ �κ�
			case DIV:
			case EQ:
			case MINUS:
			case GT:
			case PLUS:
			case TIMES:
			case LT:
				return new BinaryOpNode(tType);
			// ���� ������ �κ�
			case ATOM_Q:
			case CAR:
			case CDR:
			case COND:
			case CONS:
			case DEFINE:
			case EQ_Q:
			case LAMBDA:
			case NOT:
			case NULL_Q:
				return new FunctionNode(tType);
			// ���� ������ �κ�
			case FALSE:
				return BooleanNode.FALSE_NODE;
			case TRUE:
				return BooleanNode.TRUE_NODE;
			case L_PAREN:
				return parseExprList();
			case R_PAREN:
				return END_OF_LIST ;
			//QuoteNode�� �� ��쿡 �����ȴ�
			case APOSTROPHE:
				return new QuoteNode(parseExpr());
			case QUOTE:
				return new QuoteNode(parseExpr());
			default:
				System.out.println("Parsing Error!");
				return null;	
		}
	}
	
	// ���� ������ �κ�
	private ListNode parseExprList() {
		Node head = parseExpr();
		if (head == null)
			return null;
		if (head == END_OF_LIST) // if next token is RPAREN
			return ListNode.ENDLIST;
			
		ListNode tail = parseExprList();
			
		if (tail == null)
			return null;
		return ListNode.cons(head, tail);
	}
}




	

