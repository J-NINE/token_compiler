package parser;

class Char {
	private final char value;
	private final CharacterType type;

	enum CharacterType {
		LETTER, DIGIT, SPECIAL_CHAR, WS, END_OF_STREAM,
	}
	
	static Char of(char ch) {
		return new Char(ch, getType(ch));
	}
	
	private Char(char ch, CharacterType type) {
		this.value = ch;
		this.type = type;
	}
	
	char value() {
		return this.value;
	}
	
	CharacterType type() {
		return this.type;
	}
	
	static Char end() {
		return new Char(Character.MIN_VALUE, CharacterType.END_OF_STREAM);
	}

	//특문의 CharacterType을 SPECIAL로 설정한다면
	//CharStream의 nextChar()에서 바로 transit()호출해선 -처리로 들어감
	private static CharacterType getType(char ch) {
		int code = (int)ch;
		if ( (code >= (int)'A' && code <= (int)'Z')
			|| (code >= (int)'a' && code <= (int)'z')) {	//?를 letter로 선언
			return CharacterType.LETTER;
		}
		
		if ( Character.isDigit(ch) ) {
			return CharacterType.DIGIT;
		}
		
		//모든 특문을 SPECIAL_CHAR로 설정해 봄
		switch ( ch ) {
			case '-': 
			case '(':
			case ')':
			case '+':
			case '*':
			case '/':
			case '<':
			case '=':
			case '>':
			case '\'':
			case '#':
				return CharacterType.SPECIAL_CHAR;
			case '?':	//?는 State.ID로 분류되어야하므로 LETTER처리
				return CharacterType.LETTER;
		}
		
		if ( Character.isWhitespace(ch) ) {
			return CharacterType.WS;
		}
		
		throw new IllegalArgumentException("input=" + ch);
	}
}
