package com.xrbpowered.zoomui.richedit;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.xrbpowered.zoomui.richedit.StyleToken.Style;

public class InterruptibleContext extends TokeniserContext {

	public static class InterruptionRule {
		public Pattern pattern;
		public Style style;
		public TokeniserContext nextContext;
		
		public InterruptionRule(Pattern pattern, Style style, TokeniserContext nextContext) {
			this.pattern = pattern;
			this.style = style;
			this.nextContext = nextContext;
		}
		
		public InterruptionRule(String regex, Style style, TokeniserContext nextContext) {
			this(Pattern.compile(regex), style, nextContext);
		}
		
		public MatcherRule matcherRule() {
			return new MatcherRule(pattern, StyleTokenProvider.token(style, nextContext));
		}
	}
	
	public static class InterruptionRules {
		protected ArrayList<InterruptionRule> rules = new ArrayList<>();
		public InterruptionRules() {
		}
		public InterruptionRules(InterruptionRules inter) {
			if(inter!=null) {
				for(InterruptionRule r : inter.rules)
					rules.add(r);
			}
		}
		public InterruptionRules(InterruptionRule... inter) {
			for(InterruptionRule r : inter)
				rules.add(r);
		}
		public InterruptionRules(InterruptionRules inter1, InterruptionRules inter2) {
			if(inter1!=null) {
				for(InterruptionRule r : inter1.rules)
					rules.add(r);
			}
			if(inter2!=null) {
				for(InterruptionRule r : inter2.rules)
					rules.add(r);
			}
		}
		public InterruptionRules(InterruptionRules inter1, InterruptionRule... inter2) {
			if(inter1!=null) {
				for(InterruptionRule r : inter1.rules)
					rules.add(r);
			}
			for(InterruptionRule r : inter2)
				rules.add(r);
		}
	}
	
	public InterruptibleContext() {
		this((InterruptionRules)null);
	}

	public InterruptibleContext(InterruptionRules inter) {
		if(inter!=null) {
			for(InterruptionRule r : inter.rules)
				this.rules.add(r.matcherRule());
		}
	}

}
