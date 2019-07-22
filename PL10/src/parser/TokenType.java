package parser;


public enum TokenType {
	INT,
	ID, QUESTION,
	TRUE, FALSE, NOT,
	PLUS, MINUS, TIMES, DIV,
	LT, GT, EQ, APOSTROPHE,
	L_PAREN, R_PAREN,
	DEFINE, LAMBDA, COND, QUOTE,
	CAR, CDR, CONS,
	ATOM_Q, NULL_Q, EQ_Q;
	
	//State.START.transit()내부 switch 변경해서 
	static TokenType fromSpecialCharactor(char ch) {
	
		switch ( ch ) {
		//단일 특수문자 
		//상태전이가 필요없다 -> START에서 바로 MATCHED 판정을 내려주어야 함
		case '(':
			return TokenType.L_PAREN;
		case ')':
			return TokenType.R_PAREN;
		case '+':
			return TokenType.PLUS;
		case '-':
			return TokenType.MINUS;
		case '*':
			return TokenType.TIMES;
		case '/':
			return TokenType.DIV;
		case '<':
			return TokenType.LT;
		case '=':
			return TokenType.EQ;
		case '>':
			return TokenType.GT;	
		case '\'':
			return TokenType.APOSTROPHE;
		//정규 표현식을 참고하여 ch와 매칭되는 keyword를 반환하는 case문 작성

		default:
			throw new IllegalArgumentException("unregistered char: " + ch);
			}

	}
}
