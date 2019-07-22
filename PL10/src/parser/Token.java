package parser;

import java.util.HashMap;
import java.util.Map;


public class Token {
	

	private final TokenType type;
	private final String lexme;
	
	
	
	//ID와 ?만 구분 -> ID로 판별된 token에 대한 2차 filter
	//아직 사용하는 곳 없음
	static Token ofName(String lexme) {
		TokenType type = KEYWORDS.get(lexme);
		
		//lexme가 keyword라면
		if ( type != null ) {
			return new Token(type, lexme);
		}
		
		//keyword가 아니라면 ?가 있는지 ID인지 구분
		else if ( lexme.endsWith("?") ) {
			//?두개이상
			if ( lexme.substring(0, lexme.length()-1).contains("?") ) {
				throw new ScannerException("invalid ID=" + lexme);
			}
			//QUESTION인 경우
			return new Token(TokenType.QUESTION, lexme);
		}
		else if ( lexme.contains("?") ) {
			throw new ScannerException("invalid ID=" + lexme);
		}
		
		//ID인 경우
		else {
			return new Token(TokenType.ID, lexme);
		}
	}
	
	
	Token(TokenType type, String lexme) {
		this.type = type;
		this.lexme = lexme;
	}
	public TokenType type() {
		return this.type;
	}
	
	public String lexme() {	
		return this.lexme;
	}
	
	@Override
	public String toString() {
	return String.format("%s(%s)", type, lexme);
	}
	
	//keyword 처리해줄것
	private static final Map<String,TokenType> KEYWORDS = new HashMap<>();
	static {
		KEYWORDS.put("define", TokenType.DEFINE);
		KEYWORDS.put("lambda", TokenType.LAMBDA);
		KEYWORDS.put("cond", TokenType.COND);
		KEYWORDS.put("quote", TokenType.QUOTE);
		KEYWORDS.put("not", TokenType.NOT);
		KEYWORDS.put("cdr", TokenType.CDR);
		KEYWORDS.put("car", TokenType.CAR);
		KEYWORDS.put("cons", TokenType.CONS);
		KEYWORDS.put("eq?", TokenType.EQ_Q);
		KEYWORDS.put("null?", TokenType.NULL_Q);
		KEYWORDS.put("atom?", TokenType.ATOM_Q);
	}
	
}
