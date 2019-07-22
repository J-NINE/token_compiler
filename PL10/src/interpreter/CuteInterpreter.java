package interpreter;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import node.BinaryOpNode;
import node.BooleanNode;
import node.FunctionNode;
import node.IdNode;
import node.IntNode;
import node.ListNode;
import node.Node;
import node.QuoteNode;
import parser.*;

public class CuteInterpreter {
   
   public int calcval = 0;
   private Node tempNode = null;
   
   private void errorLog(String err) {
      System.out.println(err);
   }
   
   public Node runExpr(Node rootExpr) {
      if(rootExpr == null)
         return null;
      
      if(rootExpr instanceof IdNode) {
         ///////////////////////////
         if(lookupTable(((IdNode)rootExpr).idString) != null){
            rootExpr = lookupTable(((IdNode)rootExpr).idString);
         }
         if(rootExpr instanceof ListNode){
            return runExpr(rootExpr);
         }
         ///////////////////////////
         return rootExpr;
      }
      else if(rootExpr instanceof BooleanNode)
         return rootExpr;
      else if(rootExpr instanceof ListNode) 
         return runList((ListNode) rootExpr);
      else if(rootExpr instanceof IntNode)
         return rootExpr;
      else
         errorLog("run Expr error");
      return null;
   }
   
   //Func or Bin type �Ǻ��ؼ� ������ �Լ� ȣ�� 
   private Node runList(ListNode list) {
      
	   Node opNode = list.car();
	   
      if(list.equals(ListNode.EMPTYLIST))
         return list;
      if(opNode instanceof FunctionNode)
         return runFunction((FunctionNode)list.car(), list.cdr());
      if(opNode instanceof BinaryOpNode) {
         return runBinary(list);
      }
      if(opNode instanceof ListNode) {
    	  tempNode = list.car();
    	  return runList((ListNode)list.car());
      }
      
      if(opNode instanceof IdNode){
			Node lNode = this.lookupTable( ((IdNode)opNode).idString );	//���������� ������
			
			Node lNodeNext = ((FunctionNode)lNode);
			
			if(lNode != null){
				insertTable(((FunctionNode)lNodeNext).funcType.toString(), list.cdr().car());
				
				Node a = this.runList(   (ListNode)  ((ListNode)lNode).cdr().car());
				removeTable(((IdNode)lNodeNext).idString);
				return a;
			}
			
		}
      
      return list;
   }
   
   private static final Map<String, Node> m = new HashMap<String, Node>();
   
   private void insertTable(String id, Node value) {
      Node nodeValue = value;
      
      if(value instanceof ListNode ){
         nodeValue = runList(((ListNode)value));      // value�� list�� ���, listó���Ͽ� ����
      } else if(value instanceof IntNode) {
         nodeValue = runExpr((IntNode)value);
      } else if (value instanceof QuoteNode) {
         ListNode ln = new ListNode() {
            
            @Override
            public ListNode cdr() {
               // TODO Auto-generated method stub
               return null;
            }
            
            @Override
            public Node car() {
               // TODO Auto-generated method stub
               return value;
            }
         };
         nodeValue = runQuote(ln);
         //System.out.println(((ListNode)nodeValue).car() );
      } else if(nodeValue instanceof BooleanNode) {
         nodeValue = (BooleanNode)nodeValue;
      } else {
         nodeValue = runExpr((IdNode)value);
      }
      
      m.put(id, nodeValue);
      
   }
   
   private Node lookupTable(String id) {
      Node result = m.get(id);
      //System.out.println(result.toString());
      return result;
   }
   
   private boolean removeTable(String id){
	   
	   
		Boolean res = false;
		res = m.containsKey(id);
		
		return res;								
	}
   
   private Node makeList(Node head, Node tail) {
      ListNode ln = new ListNode() {
         
         @Override
         public ListNode cdr() {
            // TODO Auto-generated method stub
            return (ListNode)tail;
         }
         
         @Override
         public Node car() {
            // TODO Auto-generated method stub
            return head;
         }
      };
      
      return ln;
   }
   
   
   private Node runFunction(FunctionNode operator, ListNode operand) {
      
      Node result = null;
   
      Node node1 = operand.car();
      Node node2 = (node1 != null)? operand.cdr().car() : null;
      
      if(node1 instanceof IdNode && lookupTable(((IdNode)node1).idString) != null) {   //lookuptable�� ��� �ִٸ�
         if(operator.funcType != FunctionNode.FunctionType.DEFINE)   //���ο� �����ǰ� �ƴ϶��
            node1 = lookupTable(((IdNode)node1).idString);         //lookuptableŽ���ؼ�
      }
      
      if(node2 instanceof IdNode && lookupTable(((IdNode)node2).idString) != null) {
         if(operator.funcType != FunctionNode.FunctionType.DEFINE) {
            node2 = lookupTable(((IdNode)node2).idString);
         }
      }

      /////////////////////////////

      switch (operator.funcType) {
      //FunctionType is not visible 
      //public enum���� ������ ��
      
         case CAR:   
   
            System.out.println("THIS IS CAR");
            if(node1 instanceof QuoteNode) {
               result = runQuote(operand);
               if(result instanceof IdNode && lookupTable(((IdNode)result).idString) != null) {
                  if(operator.funcType != FunctionNode.FunctionType.DEFINE) {
                     result = lookupTable(((IdNode)result).idString);
                  }
               }   
               result = ((ListNode)result).car();
            } else {
               ///////List��ü�� ����
               result = ((ListNode) node1).car();
            }
            break;
            
         case CDR:
            System.out.println("THIS IS CDR");
            if(!(node1 instanceof ListNode)) { 
               errorLog("THIS IS NOT LIST");
               break;
            }

            result = ((ListNode) node1).cdr();
            QuoteNode temp = new QuoteNode(result);
            result = temp;
            break;
   
         case CONS:
            System.out.println("THIS IS CONS");
            
            ListNode listNode = (ListNode) makeList(runExpr(node1), runExpr(node2));
            
            temp = new QuoteNode(listNode);
            
            result = temp;
            break;
         case EQ_Q:
            //List�� Id�� ��� ��� ���� �Ϸ�
            result = new BooleanNode(false);
            
            if(node1 instanceof QuoteNode) {
               node1 = runQuote(operand);   //QuoteNode������ list
            }
               //get next next Node
            if(node2 instanceof QuoteNode) {
               node2 = runQuote(operand.cdr());   //QuoteNode������ list 
            }
            
            if((node1 == null) && (node2 == null))
               break;
            
            if((node1 instanceof IntNode) && (node2 instanceof IntNode)) {
               IntNode idNext = (IntNode)node1;
               IntNode idNext2 = (IntNode)node2;
               if((idNext.value()).equals(idNext2.value())) {
                  result = new BooleanNode(true);
                  
               }
            }
            
            if((node1 instanceof IdNode) && (node2 instanceof IdNode)) {
               IdNode idNext = (IdNode)node1;
               IdNode idNext2 = (IdNode)node2;
               if((idNext.idString).equals(idNext2.idString)) {
                  result = new BooleanNode(true);
               }
            } else if((node1 instanceof ListNode) && (node2 instanceof ListNode)) {
               ListNode listNext = (ListNode)node1;
               ListNode listNext2 = (ListNode)node2;
               //
               while(!(listNext.equals(ListNode.ENDLIST)) && !(listNext2.equals(ListNode.ENDLIST))) {
                  if((listNext.car().toString()).equals(listNext2.car().toString())) {
                     listNext = listNext.cdr();
                     listNext2 = listNext2.cdr();
                  } else
                     break;
               }
               result = new BooleanNode(true);
            }            
            break;
         case NULL_Q:      //�����Ϸ�
            result = new BooleanNode(false);
            
            System.out.println("HERE IS NULL_Q");
            //Node p = operand.car();   //operand.car() -> QuoteNode
            
            Node check = node1;
            ///////////////////////////////////////
            if(check instanceof QuoteNode) {
               check = runQuote(operand);
            }
            ///////////////////////////////////////
            /*
             * runQuote�� ���ڷ� ���� ListNode�� car()�� QuoteNode�� ����, �ش� QuoteNode.nodeInside()�� return
             * operand.car()�� QuoteNode�̹Ƿ� runQuote(operand)�� nodeInside return 
             */
            
            
            
            if(check instanceof ListNode || check instanceof IdNode) {
               ListNode beanHater = (ListNode)check;
               if(beanHater.equals(ListNode.ENDLIST)) {
                  result = new BooleanNode(true);
               }
               
            }

            break;
         case ATOM_Q:
            
            check = runQuote(operand);
            result = new BooleanNode(true);
                  
            if(check instanceof ListNode) {
               ListNode beanHater = (ListNode)check;
               //check NULL LIST
               if(beanHater.equals(ListNode.ENDLIST)) {
                  return result;
               }   
               System.out.println("NOT ATOM");
               result = new BooleanNode(false);
            } else {
               System.out.println("ATOM");
            }
         
            break;
         case NOT:   //�����Ϸ�
            //operand�� #T #F �Ǻ�
            node1 = runExpr(node1);
            
            if(node1 instanceof BooleanNode) {
               BooleanNode carrotHater = (BooleanNode)node1;
               if(carrotHater.value) {
                  result = new BooleanNode(false);
               } else {
                  result = new BooleanNode(true);
               }
            }
            break;
         case COND:
            if((node1 instanceof ListNode) && (node2 instanceof ListNode)) {
               ListNode ifcon = (ListNode)node1;
               ListNode elsecon = (ListNode)node2;
               
               node1 = ifcon.car();   //����-> boolean
               //Node condition = runExpr(first);   //ifcon
               Node condition = runExpr(node1);
               if(condition instanceof IdNode && lookupTable(((IdNode)condition).idString) != null) {   //lookuptable�� ��� �ִٸ�
                  if(operator.funcType != FunctionNode.FunctionType.DEFINE)   //���ο� �����ǰ� �ƴ϶��
                     result = lookupTable(((IdNode)condition).idString);         //lookuptableŽ���ؼ�
               }
               
               if(((BooleanNode)condition).value)
                  result = ifcon.cdr().car();
               else
                  result = elsecon.cdr().car();
               
               if(result instanceof IdNode && lookupTable(((IdNode)result).idString) != null) {   //lookuptable�� ��� �ִٸ�   
                  if(operator.funcType != FunctionNode.FunctionType.DEFINE)   //���ο� �����ǰ� �ƴ϶��
                     result = lookupTable(((IdNode)result).idString);         //lookuptableŽ���ؼ�
               }
               /////////////////////////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
               //�Ʒ����� �ּ�ó���ϸ�  ( cond ( a ' ( 1 2 ) ) ( #F 6 ) ) �갡 ����� ���ư�
               //���ϸ� �ȵ��ư�
               //�ٵ� cond������ �ؾ��� ��¾���� ����
               //result = new QuoteNode((ListNode)result);
               ////////////////////////////////////////////////////!!!!!!!!!!!!!!!!
            }
            
            break;
            ///////////////item2
         case DEFINE:
            if(node1 instanceof IdNode){      //DEFINE�� ��� ���� ��尡 Id�� �ƴҰ�� ������� ����(3���� �ٲ� ����).
               this.insertTable(((IdNode) node1).idString, node2);   //a��� id�� b�� ����.
            } else {
               System.out.println("Syntax Error!");         
            }
            break;
            //////////////item3
         case LAMBDA:
        	 if( tempNode==null ){
					return operator;
				}
				else{
					Node rhs3 = ((ListNode)tempNode).car();
					this.insertTable( ((IdNode)((ListNode)node1).car()).idString , rhs3);
					//tempNode.setNext(null);
					return runExpr(node2);
				}
      default:
         break;
      }
      return result;
   }
   
   //���� ������ ���ο� IntNode����
   private IntNode IntNodeMaker(int number) {
      String text = Integer.toString(number);
      return new IntNode(text);
   }
   
   private Node NodeCase(Node next) {
      if(next instanceof IntNode) {
         IntNode calcOp = (IntNode) next;
         return calcOp;
      } else if(next instanceof ListNode) {
         return runList((ListNode)next);
      }
      return null;
   }

   //operator ���� node �� �� ����ؼ� ���ο� intNode�� value�� ���� �� return      
   private Node runBinary(ListNode list) {
      
      BinaryOpNode operator = (BinaryOpNode)list.car();   //list�� car()�� binaryNode�� type casting
      Node result = null;                        //final return Node
      
      /*2nd solution*/
      //!!!!!!!!!!!!!! ��ø�� ���� �ߺ� ���
      //�����ڰ� ���� ������ �������� calcval�� ���� �ٲ۴�
      //���� ��尡 Int���� list���� Ȯ���ؼ� ȣ��

      //get Next Node
      //calcOp is IntNode at last
      Node next = list.cdr().car();   //first op
      Node next2 = list.cdr().cdr().car();
      
      /////////////////////////////////////////////////
      if(next instanceof IdNode) next = lookupTable(((IdNode)next).idString);
      if(next2 instanceof IdNode) next2 = lookupTable(((IdNode)next2).idString);
      /////////////////////////////////////////////////
      
      IntNode calcOp = (IntNode)NodeCase(next);
      IntNode calcOp2 = (IntNode)NodeCase(next2);
      
      
      switch (operator.value) {
         case PLUS:
            result = IntNodeMaker(calcOp.value() + calcOp2.value());
            break;
         case MINUS:
            result = IntNodeMaker(calcOp.value() - calcOp2.value());
            break;
         case DIV:
            result = IntNodeMaker(calcOp.value() / calcOp2.value());
            break;
         case TIMES: 
            result = IntNodeMaker(calcOp.value() * calcOp2.value());
            
            //!!!!!!!!!!!!type��ü �ν� �Ұ�
         case LT:
            BooleanNode boolnode = null;
            if(calcOp.value() < calcOp2.value())
               boolnode = new BooleanNode(true);
            else
               boolnode = new BooleanNode(false);
            result = boolnode;
            break;
         case GT:
            if(calcOp.value() > calcOp2.value())
               boolnode = new BooleanNode(true);
            else
               boolnode = new BooleanNode(false);
            result = boolnode;
            break;
         case EQ:
            if(calcOp.value().equals(calcOp2.value()))
               boolnode = new BooleanNode(true);
            else
               boolnode = new BooleanNode(false);
            
            result = boolnode;
         default:
            break;
      }
      
      return result;
   }
   
   private Node runQuote(ListNode node) {
      return ((QuoteNode) node.car()).nodeInside();
   }
   
   public static void main(String[] args) {
      ClassLoader cloader = CuteInterpreter.class.getClassLoader();               //load Class
      //File file = new File(cloader.getResource("interpreter/as07.txt").getFile());   //get file
      Scanner scan = new Scanner(System.in);
      String input = "( + 2 3 )";
      
      while(true) {
         System.out.print("> ");
         input = scan.nextLine();
         
         Cuteparser cuteParser = new Cuteparser(input);                           //parser
         Node parseTree = cuteParser.parseExpr();                              //�Ϲ� Node�� typeNode�� ����� �� 
         
         CuteInterpreter i = new CuteInterpreter();                              //new interpreter
         Node resultNode = i.runExpr(parseTree);                                 //list�϶��� �ش� �۾� ����
         System.out.print("- ");
         NodePrinter.getPrinter(System.out).prettyPrint(resultNode);
         System.out.println();
      }
      
      
      
   }
}