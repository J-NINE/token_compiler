package node;

import java.util.HashMap;
import java.util.Map;

import parser.TokenType;

public class FunctionNode implements Node{
	public enum FunctionType {
		DEFINE { TokenType tokenType() {return TokenType.DEFINE;} },
		LAMBDA { TokenType tokenType() {return TokenType.LAMBDA;} },
		COND { TokenType tokenType() {return TokenType.COND;} },
		QUOTE { TokenType tokenType() {return TokenType.QUOTE;} },
		NOT { TokenType tokenType() {return TokenType.NOT;} },
		CDR { TokenType tokenType() {return TokenType.CDR;} },
		CAR { TokenType tokenType() {return TokenType.CAR;} },
		CONS { TokenType tokenType() {return TokenType.CONS;} },
		EQ_Q { TokenType tokenType() {return TokenType.EQ_Q;} },
		NULL_Q { TokenType tokenType() {return TokenType.NULL_Q;} },
		ATOM_Q { TokenType tokenType() {return TokenType.ATOM_Q;} };
		
		
		private static Map<TokenType, FunctionType> fromTokenType = new HashMap<TokenType, FunctionType>();
		static {
			for (FunctionType fType : FunctionType.values()){
				fromTokenType.put(fType.tokenType(), fType);
			}
		}
		static FunctionType getFuncType(TokenType tType){
			return fromTokenType.get(tType);
		}
		abstract TokenType tokenType();
	}
	
	
	
	public FunctionType funcType;	//PL06에서는 value
	@Override
	public String toString() {
		return funcType.name();
		
	}
	public void setValue(TokenType tType) {
		//tokenType을 받아 Func로 바꿔야 한다
		FunctionType fType = FunctionType.getFuncType(tType);
		funcType = fType;
	}
	
	public FunctionNode(TokenType tType) {
		// TODO Auto-generated constructor stub
		setValue(tType);
	}
}