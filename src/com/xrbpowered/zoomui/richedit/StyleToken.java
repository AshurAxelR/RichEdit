package com.xrbpowered.zoomui.richedit;

import java.awt.Color;
import java.awt.Font;

public class StyleToken {

	public static class Style {
		public Color fg;
		public Color bg = null;
		public int font = Font.PLAIN;

		public Style(Color fg) {
			this.fg = fg;
		}

		public Style(Color fg, Color bg) {
			this.fg = fg;
			this.bg = bg;
		}
		
		public Style(Color fg, Color bg, int font) {
			this.fg = fg;
			this.bg = bg;
			this.font = font;
		}
	}
	
	public int start;
	public Style style;
	public TokeniserContext nextContext;
	
	public StyleToken(int start, Style style) {
		this(start, style, null);
	}

	public StyleToken(int start, Style style, TokeniserContext nextContext) {
		this.start = start;
		this.style = style;
		this.nextContext = nextContext;
	}

	public Color getBg(Color def, Color over) {
		return over!=null ? over : (style==null || style.bg==null) ? def : style.bg;
	}
	
	public Color getFg(Color def, Color over) {
		return over!=null ? over : (style==null || style.fg==null) ? def : style.fg;
	}
	
	public int getFont() {
		return style==null ? Font.PLAIN : style.font;
	}
}
