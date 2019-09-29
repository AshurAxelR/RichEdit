package com.xrbpowered.zoomui.richedit;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class LineTokeniser {

	public final TokeniserContext defaultContext;
	
	public LineTokeniser(TokeniserContext defaultContext) {
		this.defaultContext = defaultContext;
	}
	
	protected int index;
	
	protected StyleToken getNextToken(String str, int end, TokeniserContext context) {
		if(context==null)
			return null;
		if(index>=end)
			return null;
		
		Matcher[] matchers = context.matchers;
		int match = -1;
		for(int i=0; i<matchers.length; i++) {
			matchers[i].region(index, end);
			if(matchers[i].lookingAt()) {
				match = i;
				break;
			}
		}
		if(match>=0) {
			StyleToken t = context.evaluateToken(index, match);
			index = matchers[match].end();
			return t;
		}
		else
			return null;
	}
	
	protected TokeniserContext switchContext(TokeniserContext context, String str) {
		if(context==null)
			context = defaultContext;
		if(context!=null)
			context.init(str);
		return context;
	}
	
	public ArrayList<StyleToken> processLine(String str, int start, int end, TokeniserContext context) {
		ArrayList<StyleToken> tokens = new ArrayList<>();
		
		index = start;
		context = switchContext(context, str);
		
		boolean stop = false;
		StyleToken prev = null;
		while(!stop) {
			StyleToken t = getNextToken(str, end, context);
			stop = (t==null) || index>end;
			if(stop)
				t = new StyleToken(index, null, context);
			if(prev==null || prev.style!=t.style) {
				t.start -= start;
				tokens.add(t);
			}
			if(context!=t.nextContext)
				context = switchContext(t.nextContext, str);
		}
		return tokens;
	}
	
}
