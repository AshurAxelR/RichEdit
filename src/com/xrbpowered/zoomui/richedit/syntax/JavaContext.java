package com.xrbpowered.zoomui.richedit.syntax;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

import com.xrbpowered.zoomui.richedit.StyleToken;
import com.xrbpowered.zoomui.richedit.TokeniserContext;
import com.xrbpowered.zoomui.richedit.StyleToken.Style;

public class JavaContext extends TokeniserContext {

	public static Style comment = new Style(new Color(0x007755));
	public static Style string = new Style(new Color(0x0000ff));
	public static Style keyword = new Style(new Color(0x770055), null, Font.BOLD);
	public static Style number = new Style(new Color(0x777777));
	public static Style identifier = new Style(null);
	public static Style todo = new Style(new Color(0x7799bb), null, Font.BOLD);

	public TokeniserContext multilineCommentContext = new TokeniserContext(new Pattern[] {
			Pattern.compile("\\*\\/"),
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

	public TokeniserContext commentContext = new TokeniserContext.SingleLine(new Pattern[] {
			Pattern.compile("(TODO)|(FIXME)"),
			Pattern.compile(".")
		}) {
		@Override
		protected StyleToken evaluateToken(int index, int match) {
			switch(match) {
				case 0:
					return new StyleToken(index, todo, this);
				default:
					return new StyleToken(index, comment, this);
			}
		}
	};

	public static final HashSet<String> KEYWORD_MAP = new HashSet<>();
	static {
		KEYWORD_MAP.addAll(Arrays.asList(new String[] {
			"abstract", "continue", "for", "new", "switch",
			"assert", "default", "goto", "package", "synchronized",
			"boolean", "do", "if", "private", "this",
			"break", "double", "implements", "protected", "throw",
			"byte", "else", "import", "public", "throws",
			"case", "enum", "instanceof", "return", "transient",
			"catch", "extends", "int", "short", "try",
			"char", "final", "interface", "static", "void",
			"class", "finally", "long", "strictfp", "volatile",
			"const", "float", "native", "super", "while",
			"true", "false", "null"
		}));
	}
	
	public JavaContext() {
		super(new Pattern[] {
				Pattern.compile("\\s+"),
				Pattern.compile("\\/\\/"),
				Pattern.compile("\\/\\*"),
				Pattern.compile("\\\"((\\\\\\\")|.)*?\\\""),
				Pattern.compile("\\\'((\\\\\\\')|.)*?\\\'"),
				Pattern.compile("[A-Za-z][A-Za-z0-9_]+"),
				Pattern.compile("0x[0-9a-fA-F]+"),
				Pattern.compile("\\-?\\d*\\.?\\d+[FfLl]?"),
				Pattern.compile(".")
			});
	}
	@Override
	protected StyleToken evaluateToken(int index, int match) {
		switch(match) {
			case 1:
				return new StyleToken(index, comment, commentContext);
			case 2:
				return new StyleToken(index, comment, multilineCommentContext);
			case 3:
			case 4:
				return new StyleToken(index, string);
			case 5: {
				if(KEYWORD_MAP.contains(raw(match)))
						return new StyleToken(index, keyword);
				return new StyleToken(index, identifier);
			}
			case 6:
			case 7:
				return new StyleToken(index, number);
			default:
				return new StyleToken(index, null);
		}
	}

}
