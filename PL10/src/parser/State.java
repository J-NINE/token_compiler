package parser;

import static parser.TokenType.ID;
import static parser.TokenType.INT;
import static parser.TransitionOutput.GOTO_ACCEPT_ID;
import static parser.TransitionOutput.GOTO_ACCEPT_INT;
import static parser.TransitionOutput.GOTO_EOS;
import static parser.TransitionOutput.GOTO_FAILED;
import static parser.TransitionOutput.GOTO_MATCHED;
import static parser.TransitionOutput.GOTO_SHARP;
import static parser.TransitionOutput.GOTO_SIGN;
import static parser.TransitionOutput.GOTO_START;




enum State {
	START {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch ( ch.type() ) {
				case LETTER:
					context.append(v);
					return GOTO_ACCEPT_ID;
				case DIGIT:
					context.append(v);
					return GOTO_ACCEPT_INT;
				
				//ù��°�� SPECIAL_CHAR�� ���´ٸ� append
				case SPECIAL_CHAR:
					context.append(v);
					
					if(v == '-') {			// START�� -�� ���� SIGN���� �Ǵ�
						return GOTO_SIGN;
					}else if(v == '#'){
						return GOTO_SHARP;
					}else {					//START�� �ٸ� Ư���϶��� �ٷ� GOTO_MATCHED
						return GOTO_MATCHED(TokenType.fromSpecialCharactor(v), context.getLexime());
					}
					
				case WS:
					return GOTO_START;
				case END_OF_STREAM:
					return GOTO_EOS;
				default:
					throw new AssertionError();
			}
		}
	},
	ACCEPT_ID {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch ( ch.type() ) {
				case LETTER:
				case DIGIT:
					context.append(v);
					return GOTO_ACCEPT_ID;
				case SPECIAL_CHAR:
					return GOTO_FAILED;
				case WS:
				case END_OF_STREAM:
					return GOTO_MATCHED(ID, context.getLexime());
				default:
					throw new AssertionError();
			}
		}
	},
	ACCEPT_INT {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			switch ( ch.type() ) {
				case LETTER:
					return GOTO_FAILED;
				case DIGIT:
					context.append(ch.value());
					return GOTO_ACCEPT_INT;
				case SPECIAL_CHAR:
					return GOTO_FAILED;
				case WS:
				case END_OF_STREAM:
					return GOTO_MATCHED(INT, context.getLexime());
				default:
					throw new AssertionError();
			}
		}
	},
	SIGN {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch ( ch.type() ) {
				case LETTER:
					return GOTO_FAILED;
				case DIGIT:
					context.append(v);
					return GOTO_ACCEPT_INT;
				case WS:
					return GOTO_MATCHED(TokenType.fromSpecialCharactor('-'), context.getLexime());
				case END_OF_STREAM:
					return GOTO_FAILED;
				default:
					throw new AssertionError();
			}
		}
	},
	TF{	//True False �����ؼ� MATCHED�� ������
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch(v) {	
				case 'T': //# �ڿ� T�� �� �� -> True
					context.append(v);
					return GOTO_MATCHED(TokenType.TRUE, context.getLexime());
				case 'F': //# �ڿ� F�� �� �� -> False
					context.append(v);
					return GOTO_MATCHED(TokenType.FALSE, context.getLexime());
				default: 
					throw new AssertionError();
			}
			
		
			
		}
	},
	MATCHED {
		@Override
		public TransitionOutput transit(ScanContext context) {
			throw new IllegalStateException("at final state");
		}
	},
	FAILED{
		@Override
		public TransitionOutput transit(ScanContext context) {
			throw new IllegalStateException("at final state");
		}
	},
	EOS {
		@Override
		public TransitionOutput transit(ScanContext context) {
			return GOTO_EOS;
		}
	};
	
	abstract TransitionOutput transit(ScanContext context);
}
