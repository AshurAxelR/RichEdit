package com.xrbpowered.zoomui.richedit;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.UIModalWindow;
import com.xrbpowered.zoomui.UIModalWindow.ResultHandler;
import com.xrbpowered.zoomui.richedit.syntax.CssContext;
import com.xrbpowered.zoomui.richedit.syntax.JavaContext;
import com.xrbpowered.zoomui.richedit.syntax.JavascriptContext;
import com.xrbpowered.zoomui.richedit.syntax.PhpContext;
import com.xrbpowered.zoomui.richedit.syntax.XmlContext;
import com.xrbpowered.zoomui.std.file.UIFileBrowser;
import com.xrbpowered.zoomui.std.menu.UIMenu;
import com.xrbpowered.zoomui.std.menu.UIMenuBar;
import com.xrbpowered.zoomui.std.menu.UIMenuItem;
import com.xrbpowered.zoomui.std.menu.UIMenuSeparator;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingPopup;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class RichEditTest {
	
	private static SwingFrame frame;
	private static SwingPopup popup;
	private static UIRichEditArea text;
	private static UIModalWindow<File> openDlg;
	private static UIModalWindow<File> saveDlg;
	
	private static File documentFile = null;
	
	public static byte[] loadBytes(InputStream s) throws IOException {
		DataInputStream in = new DataInputStream(s);
		byte[] bytes = new byte[in.available()];
		in.readFully(bytes);
		in.close();
		return bytes;
	}
	
	public static String loadString(File file) {
		try {
			return new String(loadBytes(new FileInputStream(file)), StandardCharsets.UTF_8);
		} catch(IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static void saveBytes(byte[] bytes, OutputStream s) throws IOException {
		DataOutputStream out = new DataOutputStream(s);
		out.write(bytes);
		out.close();
	}
	
	public static void saveString(String str, File file) {
		try {
			saveBytes(str.getBytes(StandardCharsets.UTF_8), new FileOutputStream(file));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void setDocument(File file) {
		documentFile = file;
		if(documentFile!=null)
			frame.frame.setTitle("RichEdit: "+documentFile.getName());
		else
			frame.frame.setTitle("RichEdit");
	}
	
	private static void createTextArea(UIContainer parent) {
		text = new UIRichEditArea(parent) {
			@Override
			protected UIRichEdit createEditor() {
				return new UIRichEdit(getView()) {
					@Override
					public boolean onMouseUp(float x, float y, Button button, int mods, UIElement initiator) {
						if(button==Button.right) {
							float bx = localToBaseX(x);
							float by = localToBaseY(y);
							editor.checkPushHistory();
							popup.show(frame, bx, by);
							return true;
						}
						else
							return super.onMouseUp(x, y, button, mods, initiator);
					}
				};
			}
			@Override
			protected void paintBorder(GraphAssist g) {
				g.hborder(this, GraphAssist.TOP, colorBorder);
			}
		};
		text.editor.setFont(new Font("Verdana", Font.PLAIN, 10), 10f);
		text.editor.setTokeniser(null);
	}
	
	private static void createOpenDialog() {
		openDlg = SwingWindowFactory.use().createModal("Open file", 840, 480, true, null);
		openDlg.onResult = new ResultHandler<File>() {
			@Override
			public void onResult(File result) {
				setDocument(result);
				text.editor.setText(loadString(result));
				String filename = result.getName().toLowerCase();
				LineTokeniser t;
				if(filename.endsWith(".css"))
					t = new LineTokeniser(new CssContext());
				else if(filename.endsWith(".java"))
					t = new LineTokeniser(new JavaContext());
				else if(filename.endsWith(".js"))
					t = new LineTokeniser(new JavascriptContext());
				else if(filename.endsWith(".php"))
					t = new LineTokeniser(new PhpContext());
				else if(filename.endsWith(".xml") || filename.endsWith(".html") || filename.endsWith(".htm") || filename.endsWith(".svg"))
					t = new LineTokeniser(new XmlContext());
				else
					t = null;
				text.editor.setTokeniser(t);
			}
			@Override
			public void onCancel() {
			}
		};
		new UIFileBrowser(openDlg.getContainer(), openDlg.wrapInResultHandler());
	}
	
	private static void createSaveDialog() {
		saveDlg = SwingWindowFactory.use().createModal("Save file", 840, 480, true, null);
		saveDlg.onResult = new ResultHandler<File>() {
			@Override
			public void onResult(File result) {
				if(result!=null) {
					setDocument(result);
					saveString(text.editor.getText(), documentFile);
				}
			}
			@Override
			public void onCancel() {
			}
		};
		new UIFileBrowser(saveDlg.getContainer(), saveDlg.wrapInResultHandler());
	}
	
	private static void addFileMenuItems(final UIMenu menu) {
		new UIMenuItem(menu, "New") {
			@Override
			public void onAction() {
				text.editor.setText("");
				text.repaint();
			}
		};
		new UIMenuItem(menu, "Open...") {
			@Override
			public void onAction() {
				openDlg.show();
			}
		};
		new UIMenuSeparator(menu);
		new UIMenuItem(menu, "Save"){
			@Override
			public void onAction() {
				if(documentFile==null)
					saveDlg.show();
				else {
					saveString(text.editor.getText(), documentFile);
				}
			}
		};
		new UIMenuItem(menu, "Save As...") {
			@Override
			public void onAction() {
				saveDlg.show();
			}
		};
		new UIMenuSeparator(menu);
		new UIMenuItem(menu, "Exit") {
			@Override
			public void onAction() {
				frame.requestClosing();
			}
		};
	}
	
	private static void addEditMenuItems(final UIMenu menu) {
		new UIMenuItem(menu, "Undo") {
			@Override
			public boolean isEnabled() {
				return text.editor.history.canUndo();
			}
			@Override
			public void onAction() {
				text.editor.history.undo();
				text.repaint();
			}
		};
		new UIMenuItem(menu, "Redo") {
			@Override
			public boolean isEnabled() {
				return text.editor.history.canRedo();
			}
			@Override
			public void onAction() {
				text.editor.history.redo();
				text.repaint();
			}
		};
		new UIMenuSeparator(menu);
		new UIMenuItem(menu, "Cut") {
			@Override
			public boolean isEnabled() {
				return text.editor.hasSelection();
			}
			@Override
			public void onAction() {
				text.editor.cutSelection();
				text.repaint();
			}
		};
		new UIMenuItem(menu, "Copy") {
			@Override
			public boolean isEnabled() {
				return text.editor.hasSelection();
			}
			@Override
			public void onAction() {
				text.editor.copySelection();
				text.repaint();
			}
		};
		new UIMenuItem(menu, "Paste") {
			@Override
			public boolean isEnabled() {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				return clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
			}
			@Override
			public void onAction() {
				text.editor.pasteAtCursor();
				text.repaint();
			}
		};
		new UIMenuSeparator(menu);
		new UIMenuItem(menu, "Delete") {
			@Override
			public boolean isEnabled() {
				return text.editor.hasSelection();
			}
			@Override
			public void onAction() {
				text.editor.deleteSelection();
				text.repaint();
			}
		};
		new UIMenuItem(menu, "Select All") {
			@Override
			public void onAction() {
				text.editor.selectAll();
				text.repaint();
			}
		};
	}
	
	private static void addSyntaxMenuItems(final UIMenu menu) {
		new UIMenuItem(menu, "Plain text") {
			@Override
			public void onAction() {
				text.editor.setTokeniser(null);
				text.editor.resetAllLines();
				text.repaint();
			}
		};
		new UIMenuSeparator(menu);
		new UIMenuItem(menu, "CSS") {
			@Override
			public void onAction() {
				text.editor.setTokeniser(new LineTokeniser(new CssContext()));
				text.editor.resetAllLines();
				text.repaint();
			}
		};
		new UIMenuItem(menu, "Java") {
			@Override
			public void onAction() {
				text.editor.setTokeniser(new LineTokeniser(new JavaContext()));
				text.editor.resetAllLines();
				text.repaint();
			}
		};
		new UIMenuItem(menu, "JavaScript") {
			@Override
			public void onAction() {
				text.editor.setTokeniser(new LineTokeniser(new JavascriptContext()));
				text.editor.resetAllLines();
				text.repaint();
			}
		};
		new UIMenuItem(menu, "PHP") {
			@Override
			public void onAction() {
				text.editor.setTokeniser(new LineTokeniser(new PhpContext()));
				text.editor.resetAllLines();
				text.repaint();
			}
		};
		new UIMenuItem(menu, "XML/HTML") {
			@Override
			public void onAction() {
				text.editor.setTokeniser(new LineTokeniser(new XmlContext()));
				text.editor.resetAllLines();
				text.repaint();
			}
		};
	}
	
	private static void populateMenus(UIMenuBar menuBar) {
		UIMenu fileMenu = menuBar.addMenu("File");
		addFileMenuItems(fileMenu);
		UIMenu editMenu = menuBar.addMenu("Edit");
		addEditMenuItems(editMenu);
		UIMenu syntaxMenu = menuBar.addMenu("Syntax");
		addSyntaxMenuItems(syntaxMenu);

		addEditMenuItems(new UIMenu(popup.getContainer()));
		popup.setClientSizeToContent();
	}
	
	public static void main(String[] args) {
		frame = new SwingFrame(SwingWindowFactory.use(), "RichEdit", 1600, 900, true, false) {
			@Override
			public boolean onClosing() {
				confirmClosing();
				return false;
			}
		};
		
		popup = new SwingPopup(SwingWindowFactory.use());
		popup.getContainer().setClientBorder(1, UIMenu.colorBorder);
		
		UIMenuBar menuBar = new UIMenuBar(frame.getContainer());
		createTextArea(menuBar.content);
		createOpenDialog();
		createSaveDialog();
		populateMenus(menuBar);
		
		frame.show();
	}

}
