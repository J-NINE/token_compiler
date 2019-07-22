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

	//Ư���� CharacterType�� SPECIAL�� �����Ѵٸ�
	//CharStream�� nextChar()���� �ٷ� transit()ȣ���ؼ� -ó���� ��
	private static CharacterType getType(char ch) {
		int code = (int)ch;
		if ( (code >= (int)'A' && code <= (int)'Z')
			|| (code >= (int)'a' && code <= (int)'z')) {	//?�� letter�� ����
			return CharacterType.LETTER;
		}
		
		if ( Character.isDigit(ch) ) {
			return CharacterType.DIGIT;
		}
		
		//��� Ư���� SPECIAL_CHAR�� ������ ��
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
			case '?':	//?�� State.ID�� �з��Ǿ���ϹǷ� LETTERó��
				return CharacterType.LETTER;
		}
		
		if ( Character.isWhitespace(ch) ) {
			return CharacterType.WS;
		}
		
		throw new IllegalArgumentException("input=" + ch);
	}
}
