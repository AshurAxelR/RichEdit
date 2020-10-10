package com.xrbpowered.zoomui.richedit.syntax;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.HashSet;

import com.xrbpowered.zoomui.richedit.InterruptibleContext;
import com.xrbpowered.zoomui.richedit.StyleToken;
import com.xrbpowered.zoomui.richedit.StyleToken.Style;
import com.xrbpowered.zoomui.richedit.StyleTokenProvider;
import com.xrbpowered.zoomui.richedit.TokeniserContext;

public class JavascriptContext extends InterruptibleContext {

	public static Style comment = new Style(new Color(0x007744));
	public static Style string = new Style(new Color(0x777777));
	public static Style regex = new Style(new Color(0x7700ff));
	public static Style keyword = new Style(new Color(0x770055), null, Font.BOLD);
	public static Style number = new Style(new Color(0x000099));
	public static Style identifier = new Style(null);
	public static Style todo = new Style(new Color(0x7799bb), null, Font.BOLD);

	private static class StringContext extends InterruptibleContext {
		public StringContext(InterruptionRules inter, String delim, TokeniserContext next) {
			super(inter);
			nextLineContext = next;
			add(delim, string, next);
			add("\\\\[tbnrfv\\\\\\\"\\\\']", string);
			add(".", string);
		}
	}
	
	public JavascriptContext() {
		this((InterruptionRules)null);
	}

	public JavascriptContext(InterruptionRules inter) {
		super(inter);
		addPlain("\\s+");
		add("\\/\\/", comment, new InterruptibleContext(inter) {{
			nextLineContext = JavascriptContext.this;
			add("(TODO)|(FIXME)", todo);
			add(".", comment);
		}});
		add("\\/\\*", comment, new InterruptibleContext(inter) {{
			add("\\*\\/", comment, JavascriptContext.this);
			add("(TODO)|(FIXME)", todo);
			add(".", comment);
		}});
		add("\\\"", string, new StringContext(inter, "\\\"", this));
		add("\\\'", string, new StringContext(inter, "\\\'", this));
		add("\\/((\\\\\\/)|.)*?\\/", regex);
		add("[A-Za-z][A-Za-z0-9_]+", new StyleTokenProvider() {
			@Override
			public StyleToken evaluateToken(int index, int match) {
				return new StyleToken(index,
						keywords.contains(raw(match)) ? keyword : identifier,
						JavascriptContext.this);
			}
		});
		add("0x[0-9a-fA-F]+", number);
		add("\\-?\\d*\\.?\\d+([Ee][\\+\\-]?\\d+)?", number);
		addPlain(".");
	}

	private static final HashSet<String> keywords = new HashSet<>();
	static {
		keywords.addAll(Arrays.asList(new String[] {
			"abstract", "arguments", "await", "boolean",
			"break", "byte", "case", "catch",
			"char", "class", "const", "continue",
			"debugger", "default", "delete", "do",
			"double", "else", "enum", "eval",
			"export", "extends", "false", "final",
			"finally", "float", "for", "function",
			"goto", "if", "implements", "import",
			"in", "instanceof", "int", "interface",
			"let", "long", "native", "new",
			"null", "package", "private", "protected",
			"public", "return", "short", "static",
			"super", "switch", "synchronized", "this",
			"throw", "throws", "transient", "true",
			"try", "typeof", "var", "void",
			"volatile", "while", "with", "yield"
		}));
	}
	
}
