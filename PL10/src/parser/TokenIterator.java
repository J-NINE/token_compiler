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
		
		Token token = nextToken.get();	//Optional객체 안의 token가져오고

		//여기서 State판별로 들어가기 때문에 hw05에서 오류가 뜬다

		
		nextToken = readToNextToken(context);
		
		return token;
	}

	private Optional<Token> readToNextToken(ScanContext context) {
		State current = State.START;
		while ( true ) {
			
			//MATCHED 상태가 나올 때까지 반복
			TransitionOutput output = current.transit(context);
			//Token token = output.token().get();
			if ( output.nextState() == State.MATCHED ) {
				//나온 token이 id라면 ofName을 돌려서 걸러줘야 하는데 Optimal객체라 token.lexme접근이 불가
				//get()사용해서 값<Token> 받아온 후에 작업 
				Token token = output.token().get();
				
				if(current == State.ACCEPT_ID) {	//ID로 구분된 token인 경우에는
					//ofName불러서 KEYWORD/QUESTION/ID 판단
					token = Token.ofName(token.lexme());
					//Optional 객체로 묶어서 return
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
