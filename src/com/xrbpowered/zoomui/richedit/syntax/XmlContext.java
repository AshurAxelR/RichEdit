package com.xrbpowered.zoomui.richedit.syntax;

import java.awt.Color;
import java.awt.Font;

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
	public static Style doctype = new Style(Color.WHITE, new Color(0xaaaaaa));
	public static Style attributeName = new Style(new Color(0x777777));
	public static Style attributeValue = new Style(new Color(0x0000ff));
	public static Style entity = new Style(null, new Color(0xfefde0), Font.ITALIC);
	public static Style todo = new Style(new Color(0x7799bb), null, Font.BOLD);

	public XmlContext() {
		addPlain("\\s+");
		add("\\<!\\-\\-", comment, commentContext);
		add("\\<!\\[CDATA\\[", cdataTag, cdataContext);
		add("\\<!DOCTYPE", doctype, doctypeContext);
		add("\\<\\?", xmlTag, tagNameContext);
		add("\\<\\/?", tag, tagNameContext);
		add("\\&\\w+\\;", entity);
		addPlain(".");
	}
	
	private static TokeniserContext commentContext = new TokeniserContext() {{
		add("\\-\\-\\>", comment);
		add("(TODO)|(FIXME)", todo, this);
		add(".", comment, this);
	}};
	
	private static TokeniserContext cdataContext = new TokeniserContext() {{
		add("\\]\\]\\>", cdataTag);
		add(".", cdata, this);
	}};

	private static TokeniserContext doctypeContext = new TokeniserContext() {{
		add("\\>", doctype);
		add(".", doctype, this);
	}};
	
	private static TokeniserContext scriptContext = new TokeniserContext() {{
		add("<\\/script", tag, tagContext);
		add(".", null, this);
	}};

	private static class TagContext extends TokeniserContext {
		public TagContext(TokeniserContext next) {
			add("\\?\\>", xmlTag, next);
			add("\\/?\\>", tag, next);
			add("[A-Za-z_][A-Za-z0-9_\\:\\-\\.]*", attributeName, this);
			add("\\\".*?\\\"", attributeValue, this);
			add("\\\'.*?\\\'", attributeValue, this);
			add(".", null, this);
		}
	}
	
	private static TagContext tagContext = new TagContext(null);
	private static TagContext scriptTagContext = new TagContext(scriptContext);
	
	private static TokeniserContext tagNameContext = new TokeniserContext() {{
		add("[A-Za-z_][A-Za-z0-9_\\:\\-\\.]*", new StyleTokenProvider() {
			@Override
			public StyleToken evaluateToken(int index, int match) {
				return new StyleToken(index, tag,
						raw(match).equalsIgnoreCase("script") ? scriptTagContext : tagContext);
			}
		});
		add(".", null, tagContext);
	}};
	
}
