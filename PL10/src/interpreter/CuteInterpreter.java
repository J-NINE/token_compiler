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
   
   //Func or Bin type 판별해서 적절한 함수 호출 
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
			Node lNode = this.lookupTable( ((IdNode)opNode).idString );	//변수명으로 변수값
			
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
         nodeValue = runList(((ListNode)value));      // value가 list인 경우, list처리하여 저장
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
      
      if(node1 instanceof IdNode && lookupTable(((IdNode)node1).idString) != null) {   //lookuptable에 들어 있다면
         if(operator.funcType != FunctionNode.FunctionType.DEFINE)   //새로운 재정의가 아니라면
            node1 = lookupTable(((IdNode)node1).idString);         //lookuptable탐색해서
      }
      
      if(node2 instanceof IdNode && lookupTable(((IdNode)node2).idString) != null) {
         if(operator.funcType != FunctionNode.FunctionType.DEFINE) {
            node2 = lookupTable(((IdNode)node2).idString);
         }
      }

      /////////////////////////////

      switch (operator.funcType) {
      //FunctionType is not visible 
      //public enum으로 수정해 줌
      
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
               ///////List전체가 나옴
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
            //List와 Id의 경우 모두 구현 완료
            result = new BooleanNode(false);
            
            if(node1 instanceof QuoteNode) {
               node1 = runQuote(operand);   //QuoteNode포함한 list
            }
               //get next next Node
            if(node2 instanceof QuoteNode) {
               node2 = runQuote(operand.cdr());   //QuoteNode포함한 list 
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
         case NULL_Q:      //구현완료
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
             * runQuote는 인자로 받은 ListNode의 car()을 QuoteNode로 생각, 해당 QuoteNode.nodeInside()를 return
             * operand.car()이 QuoteNode이므로 runQuote(operand)로 nodeInside return 
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
         case NOT:   //구현완료
            //operand의 #T #F 판별
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
               
               node1 = ifcon.car();   //조건-> boolean
               //Node condition = runExpr(first);   //ifcon
               Node condition = runExpr(node1);
               if(condition instanceof IdNode && lookupTable(((IdNode)condition).idString) != null) {   //lookuptable에 들어 있다면
                  if(operator.funcType != FunctionNode.FunctionType.DEFINE)   //새로운 재정의가 아니라면
                     result = lookupTable(((IdNode)condition).idString);         //lookuptable탐색해서
               }
               
               if(((BooleanNode)condition).value)
                  result = ifcon.cdr().car();
               else
                  result = elsecon.cdr().car();
               
               if(result instanceof IdNode && lookupTable(((IdNode)result).idString) != null) {   //lookuptable에 들어 있다면   
                  if(operator.funcType != FunctionNode.FunctionType.DEFINE)   //새로운 재정의가 아니라면
                     result = lookupTable(((IdNode)result).idString);         //lookuptable탐색해서
               }
               /////////////////////////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
               //아랫줄을 주석처리하면  ( cond ( a ' ( 1 2 ) ) ( #F 6 ) ) 얘가 제대로 돌아감
               //안하면 안돌아감
               //근데 cond에서는 해야지 출력양식이 맞음
               //result = new QuoteNode((ListNode)result);
               ////////////////////////////////////////////////////!!!!!!!!!!!!!!!!
            }
            
            break;
            ///////////////item2
         case DEFINE:
            if(node1 instanceof IdNode){      //DEFINE의 경우 앞의 노드가 Id가 아닐경우 오류라고 가정(3에서 바꿀 예정).
               this.insertTable(((IdNode) node1).idString, node2);   //a라는 id로 b에 저장.
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
   
   //값을 넣으면 새로운 IntNode생성
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

   //operator 다음 node 두 개 계산해서 새로운 intNode의 value로 설정 후 return      
   private Node runBinary(ListNode list) {
      
      BinaryOpNode operator = (BinaryOpNode)list.car();   //list의 car()를 binaryNode로 type casting
      Node result = null;                        //final return Node
      
      /*2nd solution*/
      //!!!!!!!!!!!!!! 중첩될 수록 중복 계산
      //연산자가 나올 때마다 전역변수 calcval의 값을 바꾼다
      //다음 노드가 Int인지 list인지 확인해서 호출

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
            
            //!!!!!!!!!!!!type자체 인식 불가
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
         Node parseTree = cuteParser.parseExpr();                              //일반 Node를 typeNode로 만들어 줌 
         
         CuteInterpreter i = new CuteInterpreter();                              //new interpreter
         Node resultNode = i.runExpr(parseTree);                                 //list일때만 해당 작업 실행
         System.out.print("- ");
         NodePrinter.getPrinter(System.out).prettyPrint(resultNode);
         System.out.println();
      }
      
      
      
   }
}