package com.xrbpowered.zoomui.richedit;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xrbpowered.zoomui.richedit.StyleToken.Style;

public abstract class TokeniserContext {

	protected static class MatcherRule {
		public Pattern pattern;
		public StyleTokenProvider tokenProvider; 
		
		private Matcher matcher = null;
		
		public MatcherRule(Pattern pattern, StyleTokenProvider tokenProvider) {
			this.pattern = pattern;
			this.tokenProvider = tokenProvider;
		}
		
		public void init(String str) {
			if(matcher==null)
				matcher = pattern.matcher(str);
			else
				matcher.reset(str);
		}
		
		public Matcher getMatcher() {
			return matcher;
		}
	}
	
	protected ArrayList<MatcherRule> rules = new ArrayList<>();
	protected TokeniserContext nextLineContext;
	
	public TokeniserContext() {
		nextLineContext = this;
	}

	public void add(Pattern pattern, StyleTokenProvider tokenProvider) {
		rules.add(new MatcherRule(pattern, tokenProvider));
	}

	public void add(String regex, StyleTokenProvider tokenProvider) {
		rules.add(new MatcherRule(Pattern.compile(regex), tokenProvider));
	}

	public void add(Pattern pattern, final Style style, final TokeniserContext nextContext) {
		rules.add(new MatcherRule(pattern, StyleTokenProvider.token(style, nextContext)));
	}

	public void add(Pattern pattern, final Style style) {
		add(pattern, style, this);
	}

	public void addPlain(Pattern pattern) {
		add(pattern, null, this);
	}

	public void add(String regex, final Style style, final TokeniserContext nextContext) {
		add(Pattern.compile(regex), style, nextContext);
	}

	public void add(String regex, final Style style) {
		add(Pattern.compile(regex), style, this);
	}

	public void addPlain(String regex) {
		add(Pattern.compile(regex), null, this);
	}

	public void init(String str) {
		for(MatcherRule rule : rules)
			rule.init(str);
	}
	
	public TokeniserContext nextLineContext() {
		return nextLineContext;
	}

}
