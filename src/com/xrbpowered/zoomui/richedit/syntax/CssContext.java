package com.xrbpowered.zoomui.richedit.syntax;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.HashSet;

import com.xrbpowered.zoomui.richedit.InterruptibleContext;
import com.xrbpowered.zoomui.richedit.StyleToken;
import com.xrbpowered.zoomui.richedit.StyleTokenProvider;
import com.xrbpowered.zoomui.richedit.StyleToken.Style;
import com.xrbpowered.zoomui.richedit.TokeniserContext;

public class CssContext extends InterruptibleContext {

	public static Style tag = new Style(new Color(0x770055), null, Font.BOLD);
	public static Style selPseudoclass = new Style(new Color(0x777777), null, Font.BOLD);
	public static Style selClass = new Style(new Color(0x0000ff), null, Font.BOLD);
	public static Style selId = new Style(new Color(0x0077ff), null, Font.BOLD);
	public static Style directive = new Style(new Color(0x007777), null, Font.BOLD);
	public static Style propertyName = new Style(new Color(0x0077ff));
	public static Style identifier = new Style(new Color(0x555555));
	public static Style important = new Style(new Color(0xff0000), null, Font.ITALIC);
	public static Style number = new Style(new Color(0x000077));
	public static Style string = new Style(new Color(0x0000ff));
	public static Style stringEscape = new Style(new Color(0x7799ff));
	public static Style comment = new Style(new Color(0x007744));
	public static Style htmlComment = new Style(new Color(0xdddddd));
	public static Style todo = new Style(new Color(0x7799bb), null, Font.BOLD);

	private static class StringContext extends InterruptibleContext {
		public StringContext(InterruptionRules inter, String delim, TokeniserContext next) {
			super(inter);
			nextLineContext = next;
			add(delim, string, next);
			add("\\\\([\\\\\\\"\\']|([0-9A-Fa-f]{1,6}))", stringEscape);
			add(".", string);
		}
	}

	private static class ValueContext extends InterruptibleContext {
		public ValueContext(InterruptionRules inter) {
			super(inter);
			add("\\!important", important);
			add("[A-Za-z_\\-][A-Za-z0-9_\\-]*", identifier);
			addCommon(inter, this);
			addPlain(".");
		}
	}
	
	public CssContext() {
		this(null);
	}

	public CssContext(InterruptionRules inter) {
		super(inter);
		
		TokeniserContext propertyNameContext = new InterruptibleContext(inter) {{
			add("\\:", null, new ValueContext(new InterruptionRules(
				inter,
				new InterruptionRule(";", null, this),
				new InterruptionRule("\\}", null, CssContext.this)
			)));
			add("\\}", null, CssContext.this);
			add("[A-Za-z_\\-][A-Za-z0-9_\\-]*", propertyName);
			addCommon(inter, this);
			addPlain(".");
		}};

		add("[A-Za-z_][A-Za-z0-9_\\-]*", tag);
		add("\\:\\:?[A-Za-z_][A-Za-z0-9_\\-]*", selPseudoclass);
		add("\\.[A-Za-z_][A-Za-z0-9_\\-]*", selClass);
		add("#[A-Za-z_][A-Za-z0-9_\\-]*", selId);
		add("@[A-Za-z_][A-Za-z0-9_\\-]*", new StyleTokenProvider() {
			@Override
			public StyleToken evaluateToken(int start, String raw) {
				TokeniserContext inner = nestedDirectives.contains(raw.toLowerCase()) ? CssContext.this : propertyNameContext;
				return new StyleToken(start, directive, new ValueContext(new InterruptionRules(
					inter,
					new InterruptionRule("\\{", null, inner),
					new InterruptionRule(";", null, CssContext.this)
				)));
			}
		});
		add("\\{", null, propertyNameContext);
		add("\\[", null, new ValueContext(new InterruptionRules(
				inter,
				new InterruptionRule("\\]", null, CssContext.this)
			)));
		add("\\(", null, new ValueContext(new InterruptionRules(
				inter,
				new InterruptionRule("\\)", null, CssContext.this)
			)));
		addCommon(inter, this);
		addPlain(".");
	}
	
	private static void addCommon(InterruptionRules inter, TokeniserContext ctx) {
		ctx.addPlain("\\s+");
		ctx.add("\\/\\*", comment, new TokeniserContext() {{
			add("\\*\\/", comment, ctx);
			add("(TODO)|(FIXME)", todo);
			add(".", comment);
		}});
		ctx.add("\\-?[0-9]+(%|([A-Za-z]+))?", number);
		ctx.add("#[0-9A-Fa-f]+", number);
		ctx.add("\\\"", string, new StringContext(inter, "\\\"", ctx));
		ctx.add("\\\'", string, new StringContext(inter, "\\\'", ctx));
		ctx.add("<!\\-\\-", htmlComment);
		ctx.add("\\-\\->", htmlComment);
	}
	
	private static final HashSet<String> nestedDirectives = new HashSet<>();
	static {
		nestedDirectives.addAll(Arrays.asList(new String[] {
			"@font-feature-values", "@keyframes", "@media", "@supports"
		}));
	}

}
