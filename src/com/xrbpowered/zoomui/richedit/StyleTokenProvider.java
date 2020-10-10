package com.xrbpowered.zoomui.richedit;

import com.xrbpowered.zoomui.richedit.StyleToken.Style;

public interface StyleTokenProvider {

	public StyleToken evaluateToken(int index, int match);

	public static StyleTokenProvider token(final Style style, final TokeniserContext nextContext) {
		return new StyleTokenProvider() {
			@Override
			public StyleToken evaluateToken(int index, int match) {
				return new StyleToken(index, style, nextContext);
			}
		};
	}

}
