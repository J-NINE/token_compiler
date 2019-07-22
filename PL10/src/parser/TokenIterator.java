package parser;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;



class TokenIterator implements Iterator<Token> {
	private final ScanContext context;
	private Optional<Token> nextToken;
	
	TokenIterator(ScanContext context) {
		this.context = context;
		nextToken = readToNextToken(context);
	}

	@Override
	public boolean hasNext() {
		return nextToken.isPresent();
	}

	@Override
	public Token next() {
		if ( !nextToken.isPresent() ) {
			throw new NoSuchElementException();
		}
		
		Token token = nextToken.get();	//Optional��ü ���� token��������

		//���⼭ State�Ǻ��� ���� ������ hw05���� ������ ���

		
		nextToken = readToNextToken(context);
		
		return token;
	}

	private Optional<Token> readToNextToken(ScanContext context) {
		State current = State.START;
		while ( true ) {
			
			//MATCHED ���°� ���� ������ �ݺ�
			TransitionOutput output = current.transit(context);
			//Token token = output.token().get();
			if ( output.nextState() == State.MATCHED ) {
				//���� token�� id��� ofName�� ������ �ɷ���� �ϴµ� Optimal��ü�� token.lexme������ �Ұ�
				//get()����ؼ� ��<Token> �޾ƿ� �Ŀ� �۾� 
				Token token = output.token().get();
				
				if(current == State.ACCEPT_ID) {	//ID�� ���е� token�� ��쿡��
					//ofName�ҷ��� KEYWORD/QUESTION/ID �Ǵ�
					token = Token.ofName(token.lexme());
					//Optional ��ü�� ��� return
					return Optional.of(token);
				}
				return output.token();
			}
			else if ( output.nextState() == State.FAILED ) {
				throw new ScannerException();
			}
			else if ( output.nextState() == State.EOS ) {
				return Optional.empty();
			}
			
			current = output.nextState();
		}
	}
}
