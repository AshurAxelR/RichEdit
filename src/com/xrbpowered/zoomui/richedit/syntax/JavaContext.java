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
		add("\\/\\/", comment,  new TokeniserContext() {{
			nextLineContext = JavaContext.this;
			add("(TODO)|(FIXME)", todo);
			add(".", comment);
		}});
		add("\\/\\*", comment, new TokeniserContext() {{
			add("\\*\\/", comment, JavaContext.this);
			add("(TODO)|(FIXME)", todo);
			add(".", comment);
		}});
		add("\\\"((\\\\\\\")|.)*?\\\"", string);
		add("\\\'((\\\\\\\')|.)*?\\\'", string);
		add("[A-Za-z][A-Za-z0-9_]+", new StyleTokenProvider() {
			@Override
			public StyleToken evaluateToken(int index, int match) {
				return new StyleToken(index,
						keywords.contains(raw(match)) ? keyword : identifier,
						JavaContext.this);
			}
		});
		add("0x[0-9a-fA-F]+", number);
		add("\\-?\\d*\\.?\\d+([Ee][\\+\\-]?\\d+)?[FfLl]?", number);
		addPlain(".");
	}

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
