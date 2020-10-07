package com.xrbpowered.zoomui.richedit.syntax;

import java.awt.Color;
import java.awt.Font;
import java.util.regex.Pattern;

import com.xrbpowered.zoomui.richedit.StyleToken;
import com.xrbpowered.zoomui.richedit.TokeniserContext;
import com.xrbpowered.zoomui.richedit.StyleToken.Style;

public class XmlContext extends TokeniserContext {

	public static Style comment = new Style(new Color(0x007755));
	public static Style tag = new Style(Color.BLACK, null, Font.BOLD);
	public static Style xmlTag = new Style(Color.WHITE, Color.BLACK, Font.BOLD);
	public static Style cdata = new Style(new Color(0x990000));
	public static Style cdataTag = new Style(new Color(0x990000), null, Font.BOLD);
	public static Style doctype = new Style(Color.WHITE, new Color(0xaaaaaa));
	public static Style attributeName = new Style(new Color(0x777777));
	public static Style attributeValue = new Style(new Color(0x0000ff));
	public static Style entity = new Style(null, new Color(0xfefde0), Font.ITALIC);
	public static Style todo = new Style(new Color(0x7799bb), null, Font.BOLD);

	public TokeniserContext commentContext = new TokeniserContext(new Pattern[] {
			Pattern.compile("\\-\\-\\>"),
			Pattern.compile("(TODO)|(FIXME)"),
			Pattern.compile(".")
		}) {
		@Override
		protected StyleToken evaluateToken(int index, int match) {
			switch(match) {
				case 0:
					return new StyleToken(index, comment);
				case 1:
					return new StyleToken(index, todo, this);
				default:
					return new StyleToken(index, comment, this);
			}
		}
	};
	
	public TokeniserContext cdataContext = new TokeniserContext(new Pattern[] {
			Pattern.compile("\\]\\]\\>"),
			Pattern.compile(".")
		}) {
		@Override
		protected StyleToken evaluateToken(int index, int match) {
			switch(match) {
				case 0:
					return new StyleToken(index, cdataTag);
				default:
					return new StyleToken(index, cdata, this);
			}
		}
	};

	public TokeniserContext doctypeContext = new TokeniserContext(new Pattern[] {
			Pattern.compile("\\>"),
			Pattern.compile(".")
		}) {
		@Override
		protected StyleToken evaluateToken(int index, int match) {
			switch(match) {
				case 0:
					return new StyleToken(index, doctype);
				default:
					return new StyleToken(index, doctype, this);
			}
		}
	};
	
	public TokeniserContext scriptContext = new TokeniserContext(new Pattern[] {
			Pattern.compile("<\\/script"),
			Pattern.compile(".")
		}) {
		@Override
		protected StyleToken evaluateToken(int index, int match) {
			switch(match) {
				case 0:
					return new StyleToken(index, tag, tagContext);
				default:
					return new StyleToken(index, null, this);
			}
		}
	};

	public class TagContext extends TokeniserContext {
		public final TokeniserContext next;
		public TagContext(TokeniserContext next) {
			super(new Pattern[] {
				Pattern.compile("\\?\\>"),
				Pattern.compile("\\/?\\>"),
				Pattern.compile("[A-Za-z_][A-Za-z0-9_\\:\\-\\.]*"),
				Pattern.compile("\\\".*?\\\""),
				Pattern.compile("\\\'.*?\\\'"),
				Pattern.compile(".")
			});
			this.next = next;
		}
		@Override
		protected StyleToken evaluateToken(int index, int match) {
			switch(match) {
				case 0:
					return new StyleToken(index, xmlTag, next);
				case 1:
					return new StyleToken(index, tag, next);
				case 2:
					return new StyleToken(index, attributeName, this);
				case 3:
				case 4:
					return new StyleToken(index, attributeValue, this);
				default:
					return new StyleToken(index, null, this);
			}
		}
	}
	
	public TagContext tagContext = new TagContext(null); 
	
	public TokeniserContext tagNameContext = new TokeniserContext(new Pattern[] {
			Pattern.compile("[A-Za-z_][A-Za-z0-9_\\:\\-\\.]*"),
			Pattern.compile(".")
		}) {
		
		@Override
		protected StyleToken evaluateToken(int index, int match) {
			switch(match) {
				case 0:
					return new StyleToken(index, tag,
							raw(match).equalsIgnoreCase("script") ? new TagContext(scriptContext) : tagContext);
				default:
					return new StyleToken(index, null, tagContext);
			}
		}
	};
	
	public XmlContext() {
		super(new Pattern[] {
				Pattern.compile("\\s+"),
				Pattern.compile("\\<!\\-\\-"),
				Pattern.compile("\\<!\\[CDATA\\["),
				Pattern.compile("\\<!DOCTYPE"),
				Pattern.compile("\\<\\?"),
				Pattern.compile("\\<\\/?"),
				Pattern.compile("\\&\\w+\\;"),
				Pattern.compile(".")
			});
	}
	
	@Override
	protected StyleToken evaluateToken(int index, int match) {
		switch(match) {
			case 1:
				return new StyleToken(index, comment, commentContext);
			case 2:
				return new StyleToken(index, cdataTag, cdataContext);
			case 3:
				return new StyleToken(index, doctype, doctypeContext);
			case 4:
				return new StyleToken(index, xmlTag, tagNameContext);
			case 5:
				return new StyleToken(index, tag, tagNameContext);
			case 6:
				return new StyleToken(index, entity);
			default:
				return new StyleToken(index, null);
		}
	}
}
