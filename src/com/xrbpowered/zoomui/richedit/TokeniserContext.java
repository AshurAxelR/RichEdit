package com.xrbpowered.zoomui.richedit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TokeniserContext {

	protected Pattern[] patterns;
	protected Matcher[] matchers = null;
	
	public TokeniserContext(Pattern[] patterns) {
		this.patterns = patterns;
	}
	
	protected abstract StyleToken evaluateToken(int index, int match);
	
	public void init(String str) {
		if(matchers==null) {
			matchers = new Matcher[patterns.length];
			for(int i=0; i<matchers.length; i++)
				matchers[i] = patterns[i].matcher(str);
		}
		else {
			for(int i=0; i<matchers.length; i++)
				matchers[i].reset(str);
		}
	}
	
	protected String raw(int match) {
		return matchers[match].group();
	}
	
	public TokeniserContext nextLineContext() {
		return this;
	}
	
	public static abstract class SingleLine extends TokeniserContext {
		public SingleLine(Pattern[] patterns) {
			super(patterns);
		}
		
		public TokeniserContext nextLineContext() {
			return null;
		}
	}
}
