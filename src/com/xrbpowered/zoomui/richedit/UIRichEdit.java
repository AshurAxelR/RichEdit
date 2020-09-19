package com.xrbpowered.zoomui.richedit;

import java.awt.Color;
import java.util.ArrayList;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.base.UIPanView;
import com.xrbpowered.zoomui.base.UITextEditBase;

public class UIRichEdit extends UITextEditBase<UIRichEdit.Line> {
	
	public class Line extends UITextEditBase<Line>.Line {
		public int offs, length;
		public int width = -1;
		
		public ArrayList<StyleToken> tokens = null;
		public TokeniserContext context = null;
		
		public TokeniserContext nextContext() {
			if(tokens!=null) {
				TokeniserContext context = tokens.get(tokens.size()-1).nextContext;
				return context==null ? null : context.nextLineContext();
			}
			else
				return null;
		}
		
		@Override
		public void reset() {
			super.reset();
			tokens = null;
		}
	}
	
	protected LineTokeniser tokeniser = new LineTokeniser(null);
	
	public UIRichEdit(UIPanView parent, boolean singleLine) {
		super(parent, singleLine);
	}
	
	@Override
	protected Line createLine() {
		return (Line) new Line();
	}
	
	public void setTokeniser(LineTokeniser tokeniser) {
		this.tokeniser = (tokeniser==null) ? new LineTokeniser(null) : tokeniser;
	}
	
	@Override
	protected void allocateFonts(GraphAssist g) {
		allocateFonts(g, 4);
	}
	
	@Override
	protected void drawLine(GraphAssist g, int lineIndex, int lineStart, int lineEnd, int y, Color bg, boolean drawCursor, Line line) {
		if(line.tokens==null) {
			TokeniserContext context = (lineIndex>0) ? lines.get(lineIndex-1).nextContext() : line.context;
			line.tokens = tokeniser.processLine(text, lineStart, lineEnd, context);
			if(lineIndex<lines.size()-1) {
				Line nextLine = lines.get(lineIndex+1);
				if(line.nextContext()!=nextLine.context)
					nextLine.tokens = null;
			}
		}
		super.drawLine(g, lineIndex, lineStart, lineEnd, y, bg, drawCursor, line);
	}

	@Override
	protected void drawText(GraphAssist g, DrawLineState ls, int c0, int c1, Color bg, Color fg) {
		ArrayList<StyleToken> tokens = ls.line.tokens;
		StyleToken st = tokens.get(ls.s);
		int col = c0;
		while(col<c1) {
			int cn = c1;
			StyleToken stnext = null;
			if(ls.s+1<tokens.size()) {
				stnext = tokens.get(ls.s+1);
				cn = stnext.start+ls.lineStart;
			}
			//StyleToken stnext = tokens.get(ls.s+1);
			//int cn = stnext.start+ls.lineStart;
			if(cn>=c1) {
				drawText(g, ls, col, c1, st.getBg(colorBackground, bg), st.getFg(colorText, fg), st.getFont());
				return;
			}
			else {
				drawText(g, ls, col, cn, st.getBg(colorBackground, bg), st.getFg(colorText, fg), st.getFont());
				col = cn;
				st = stnext;
				ls.s++;
			}
		}
	}

	@Override
	protected int stringWidth(Line line, int lineStart, int c0, int c1) {
		if(line.tokens==null)
			return stringWidth(c0, c1, 0); // FIXME remove and solve problem with null after modify
		int s = 0;
		StyleToken st = line.tokens.get(s);
		int x = 0;
		int col = c0;
		while(col<c1) {
			int cn = c1;
			StyleToken stnext = null;
			if(s+1<line.tokens.size()) {
				stnext = line.tokens.get(s+1);
				cn = stnext.start+lineStart;
			}
			//StyleToken stnext = tokens.get(s+1);
			//int cn = stnext.start+lineStart;
			if(cn>=c1) {
				x += stringWidth(col, c1, st.getFont());
				return x;
			}
			else {
				x += stringWidth(col, cn, st.getFont());
				col = cn;
				st = stnext;
				s++;
			}
		}
		return x;
	}

}