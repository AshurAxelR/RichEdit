package com.xrbpowered.zoomui.richedit;

import java.util.ArrayList;
import java.util.regex.Matcher;

import com.xrbpowered.zoomui.richedit.TokeniserContext.MatcherRule;

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
		
		MatcherRule match = null;
		for(MatcherRule rule : context.rules) {
			Matcher m = rule.getMatcher();
			m.region(index, end);
			if(m.lookingAt()) {
				match = rule;
				break;
			}
		}
		if(match!=null) {
			StyleToken t = match.tokenProvider.evaluateToken(index, match.getMatcher().group());
			index = match.getMatcher().end();
			return t;
		}
		else
			return null;
	}
	
	protected TokeniserContext switchContext(TokeniserContext context, String str) {
		if(context!=null)
			context.init(str);
		return context;
	}
	
	public ArrayList<StyleToken> processLine(String str, int start, int end, TokeniserContext context) {
		ArrayList<StyleToken> tokens = new ArrayList<>();
		
		index = start;
		if(context==null)
			context = defaultContext;
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
