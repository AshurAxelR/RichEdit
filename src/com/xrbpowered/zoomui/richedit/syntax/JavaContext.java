package com.xrbpowered.zoomui.richedit.syntax;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.HashSet;

import com.xrbpowered.zoomui.richedit.StyleToken;
import com.xrbpowered.zoomui.richedit.StyleToken.Style;
import com.xrbpowered.zoomui.richedit.StyleTokenProvider;
import com.xrbpowered.zoomui.richedit.TokeniserContext;

public class JavaContext extends TokeniserContext {

	public static Style comment = new Style(new Color(0x007744));
	public static Style string = new Style(new Color(0x0000ff));
	public static Style keyword = new Style(new Color(0x770055), null, Font.BOLD);
	public static Style number = new Style(new Color(0x777777));
	public static Style identifier = new Style(null);
	public static Style todo = new Style(new Color(0x7799bb), null, Font.BOLD);

	public JavaContext() {
		addPlain("\\s+");
		add("\\/\\/", comment, commentContext);
		add("\\/\\*", comment, multilineCommentContext);
		add("\\\"((\\\\\\\")|.)*?\\\"", string);
		add("\\\'((\\\\\\\')|.)*?\\\'", string);
		add("[A-Za-z][A-Za-z0-9_]+", new StyleTokenProvider() {
			@Override
			public StyleToken evaluateToken(int index, int match) {
				if(keywords.contains(raw(match)))
					return new StyleToken(index, keyword);
				return new StyleToken(index, identifier);
			}
		});
		add("0x[0-9a-fA-F]+", number);
		add("\\-?\\d*\\.?\\d+[FfLl]?", number);
		addPlain(".");
	}

	private static TokeniserContext multilineCommentContext = new TokeniserContext() {{
		add("\\*\\/", comment);
		add("(TODO)|(FIXME)", todo, this);
		add(".", comment, this);
	}};

	private static TokeniserContext commentContext = new TokeniserContext.SingleLine() {{
		add("(TODO)|(FIXME)", todo, this);
		add(".", comment, this);
	}};

	private static final HashSet<String> keywords = new HashSet<>();
	static {
		keywords.addAll(Arrays.asList(new String[] {
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
	
}
