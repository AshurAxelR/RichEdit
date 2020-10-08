package com.xrbpowered.zoomui.richedit;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xrbpowered.zoomui.richedit.StyleToken.Style;

public abstract class TokeniserContext implements StyleTokenProvider {

	protected class MatcherRule {
		public Pattern pattern;
		public StyleTokenProvider tokenProvider; 
		
		public Matcher matcher = null;
		
		public MatcherRule(Pattern pattern, StyleTokenProvider tokenProvider) {
			this.pattern = pattern;
			this.tokenProvider = tokenProvider;
		}
	}
	
	protected ArrayList<MatcherRule> rules = new ArrayList<>();
	protected TokeniserContext nextLineContext;
	
	public TokeniserContext() {
		nextLineContext = this;
	}

	protected StyleTokenProvider token(final Style style, final TokeniserContext nextContext) {
		return new StyleTokenProvider() {
			@Override
			public StyleToken evaluateToken(int index, int match) {
				return new StyleToken(index, style, nextContext);
			}
		};
	}
	
	protected void add(Pattern pattern, StyleTokenProvider tokenProvider) {
		rules.add(new MatcherRule(pattern, tokenProvider));
	}

	protected void add(String regex, StyleTokenProvider tokenProvider) {
		rules.add(new MatcherRule(Pattern.compile(regex), tokenProvider));
	}

	protected void add(Pattern pattern, final Style style, final TokeniserContext nextContext) {
		rules.add(new MatcherRule(pattern, token(style, nextContext)));
	}

	protected void add(Pattern pattern, final Style style) {
		add(pattern, style, this);
	}

	protected void addPlain(Pattern pattern) {
		add(pattern, null, this);
	}

	protected void add(String regex, final Style style, final TokeniserContext nextContext) {
		add(Pattern.compile(regex), style, nextContext);
	}

	protected void add(String regex, final Style style) {
		add(Pattern.compile(regex), style, this);
	}

	protected void addPlain(String regex) {
		add(Pattern.compile(regex), null, this);
	}

	public void init(String str) {
		for(MatcherRule rule : rules) {
			if(rule.matcher==null)
				rule.matcher = rule.pattern.matcher(str);
			else
				rule.matcher.reset(str);
		}
	}
	
	@Override
	public StyleToken evaluateToken(int index, int match) {
		return rules.get(match).tokenProvider.evaluateToken(index, match);
	}
	
	public int ruleCount() {
		return rules.size();
	}
	
	public Matcher matcher(int match) {
		return rules.get(match).matcher;
	}
	
	public String raw(int match) {
		return rules.get(match).matcher.group();
	}
	
	public TokeniserContext nextLineContext() {
		return nextLineContext;
	}

}
