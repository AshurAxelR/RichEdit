package com.xrbpowered.zoomui.richedit.syntax;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.HashSet;

import com.xrbpowered.zoomui.richedit.StyleToken;
import com.xrbpowered.zoomui.richedit.StyleToken.Style;
import com.xrbpowered.zoomui.richedit.StyleTokenProvider;
import com.xrbpowered.zoomui.richedit.TokeniserContext;

public class PhpContext extends XmlContext {
	
	public static Style phpTag = new Style(new Color(0xff0000), null, Font.BOLD);
	public static Style keyword = new Style(new Color(0x770055), null, Font.BOLD);
	public static Style identifier = new Style(null);
	public static Style variable = new Style(new Color(0x000077));
	public static Style stringVariable = new Style(new Color(0x0000ff), null, Font.BOLD);
	public static Style stringExpression = new Style(null);
	public static Style number = new Style(new Color(0x777777));
	public static Style string = new Style(new Color(0x0000ff));
	public static Style stringEscape = new Style(new Color(0x7799ff));
	public static Style comment = new Style(new Color(0x007744));
	public static Style todo = new Style(new Color(0x7799bb), null, Font.BOLD);

	private static class StringContext extends TokeniserContext {
		public StringContext(String delim, boolean single, TokeniserContext next) {
			add(delim, string, next);
			if(single) {
				add("\\\\[\\\\\\']", stringEscape);
			}
			else {
				add("\\\\([tvnrfe\\$\\\\\\\"\\']|([0-7]{1,3})|(x[0-9A-Fa-f]{1,2}))", stringEscape);
				add("\\{\\$.*?\\}", stringExpression);
				add("\\$[A-Za-z_][A-Za-z0-9_]*", stringVariable);
				add("\\$\\{.*?\\}", stringVariable);
			}
			add(".", string);
		}
	}

	private static class PhpInsertContext extends PushContext {
		public PhpInsertContext(TokeniserContext popContext) {
			add("\\?>", phpTag, popContext);
			addPlain("\\s+");
			add("\\/\\/", comment,  new TokeniserContext() {{
				nextLineContext = PhpInsertContext.this;
				add("(TODO)|(FIXME)", todo);
				add(".", comment);
			}});
			add("\\/\\*", comment, new TokeniserContext() {{
				add("\\*\\/", comment, PhpInsertContext.this);
				add("(TODO)|(FIXME)", todo);
				add(".", comment);
			}});
			add("\\\"", string, new StringContext("\\\"", false, this));
			add("\\\'", string, new StringContext("\\\'", true, this));
			add("yield\\s+from", keyword);
			add("\\$[A-Za-z_][A-Za-z0-9_]*", variable);
			add("[A-Za-z_][A-Za-z0-9_]*", new StyleTokenProvider() {
				@Override
				public StyleToken evaluateToken(int index, int match) {
					return new StyleToken(index,
							keywords.contains(raw(match).toLowerCase()) ? keyword : identifier,
							PhpInsertContext.this);
				}
			});
			add("0x[0-9a-fA-F]+", number);
			add("\\-?\\d*\\.?\\d+([Ee][\\+\\-]?\\d+)?[FfLl]?", number);
			addPlain(".");
		}
		
		@Override
		public PushContext push(TokeniserContext popContext) {
			return new PhpInsertContext(popContext);
		}
	}
	
	public PhpContext() {
		super(new InterruptionRules(
			new InterruptionRule("<\\?(\\=|(php))?", phpTag, new PhpInsertContext(null)) 
		));
	}
	
	private static final HashSet<String> keywords = new HashSet<>();
	static {
		keywords.addAll(Arrays.asList(new String[] {
			"abstract", "and", "array", "as",
			"break", "case", "catch", "class",
			"const", "continue", "declare", "default",
			"die", "do", "echo", "else", "elseif",
			"empty", "enddeclare", "endfor", "endforeach", "endif",
			"endswitch", "endwhile", "eval", "exit", "extends",
			"final", "finally", "fn", "for", "foreach",
			"function", "global", "goto", "if", "implements",
			"include", "include_once", "instanceof", "insteadof", "interface",
			"isset", "list", "namespace", "new", "or",
			"print", "private", "protected", "public", "require",
			"require_once", "return", "static", "switch", "throw",
			"trait", "try", "unset", "use", "var",
			"while", "xor", "yield", /* "yield from" */
			
			"true", "false", "null",
			"int", "integer", "bool", "boolean", "float", "double", "real", "string", "object"
		}));
	}
}
