package com.xrbpowered.zoomui.richedit.syntax;

import java.awt.Color;
import java.awt.Font;
import java.util.regex.Pattern;

import com.xrbpowered.zoomui.richedit.InterruptibleContext.InterruptionRule;
import com.xrbpowered.zoomui.richedit.InterruptibleContext.InterruptionRules;
import com.xrbpowered.zoomui.richedit.StyleToken;
import com.xrbpowered.zoomui.richedit.StyleToken.Style;
import com.xrbpowered.zoomui.richedit.StyleTokenProvider;
import com.xrbpowered.zoomui.richedit.TokeniserContext;

public class XmlContext extends TokeniserContext {

	public static Style comment = new Style(new Color(0x007744));
	public static Style tag = new Style(Color.BLACK, null, Font.BOLD);
	public static Style xmlTag = new Style(Color.WHITE, Color.BLACK, Font.BOLD);
	public static Style cdata = new Style(new Color(0x990000));
	public static Style cdataTag = new Style(new Color(0x990000), null, Font.BOLD);
	public static Style doctype = new Style(Color.WHITE, new Color(0xdddddd));
	public static Style attributeName = new Style(new Color(0x777777));
	public static Style attributeValue = new Style(new Color(0x0000ff));
	public static Style entity = new Style(null, new Color(0xfefde0), Font.ITALIC);
	public static Style todo = new Style(new Color(0x7799bb), null, Font.BOLD);

	private static class TagContext extends TokeniserContext {
		public TagContext(TokeniserContext next) {
			add("\\?\\>", xmlTag, next);
			add("\\/?\\>", tag, next);
			add("[A-Za-z_][A-Za-z0-9_\\:\\-\\.]*", attributeName);
			add("\\\".*?\\\"", attributeValue);
			add("\\\'.*?\\\'", attributeValue);
			addPlain(".");
		}
	}
	
	public XmlContext() {
		TagContext tagContext = new TagContext(this);
		
		TokeniserContext scriptContext = new JavascriptContext(new InterruptionRules(
			new InterruptionRule(Pattern.compile("<\\/script"), tag, tagContext)
		));

		TokeniserContext styleContext = new CssContext(new InterruptionRules(
				new InterruptionRule(Pattern.compile("<\\/style"), tag, tagContext)
			));

		TokeniserContext tagNameContext = new TokeniserContext() {{
			add("[A-Za-z_][A-Za-z0-9_\\:\\-\\.]*", new StyleTokenProvider() {
				@Override
				public StyleToken evaluateToken(int index, int match) {
					return new StyleToken(index, tag,
							raw(match).equalsIgnoreCase("script") ? new TagContext(scriptContext)
								: raw(match).equalsIgnoreCase("style") ? new TagContext(styleContext)
								: tagContext);
				}
			});
			add(".", null, tagContext);
		}};
		
		addPlain("\\s+");
		add("\\<!\\-\\-", comment, new TokeniserContext() {{
			add("\\-\\-\\>", comment, XmlContext.this);
			add("(TODO)|(FIXME)", todo);
			add(".", comment);
		}});
		add("\\<!\\[CDATA\\[", cdataTag, new TokeniserContext() {{
			add("\\]\\]\\>", cdataTag, XmlContext.this);
			add(".", cdata);
		}});
		add("\\<!DOCTYPE", doctype, new TokeniserContext() {{
			add("\\>", doctype, XmlContext.this);
			add(".", doctype);
		}});
		add("\\<\\?", xmlTag, tagNameContext);
		add("\\<\\/[A-Za-z_][A-Za-z0-9_\\:\\-\\.]*", tag, tagContext);
		add("\\<", tag, tagNameContext);
		add("\\&[#\\w]+\\;", entity);
		addPlain(".");
	}

}
