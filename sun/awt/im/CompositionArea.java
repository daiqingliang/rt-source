package sun.awt.im;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public final class CompositionArea extends JPanel implements InputMethodListener {
  private CompositionAreaHandler handler;
  
  private TextLayout composedTextLayout;
  
  private TextHitInfo caret = null;
  
  private JFrame compositionWindow;
  
  private static final int TEXT_ORIGIN_X = 5;
  
  private static final int TEXT_ORIGIN_Y = 15;
  
  private static final int PASSIVE_WIDTH = 480;
  
  private static final int WIDTH_MARGIN = 10;
  
  private static final int HEIGHT_MARGIN = 3;
  
  private static final long serialVersionUID = -1057247068746557444L;
  
  CompositionArea() {
    String str = Toolkit.getProperty("AWT.CompositionWindowTitle", "Input Window");
    this.compositionWindow = (JFrame)InputMethodContext.createInputMethodWindow(str, null, true);
    setOpaque(true);
    setBorder(LineBorder.createGrayLineBorder());
    setForeground(Color.black);
    setBackground(Color.white);
    enableInputMethods(true);
    enableEvents(8L);
    this.compositionWindow.getContentPane().add(this);
    this.compositionWindow.addWindowListener(new FrameWindowAdapter());
    addInputMethodListener(this);
    this.compositionWindow.enableInputMethods(false);
    this.compositionWindow.pack();
    Dimension dimension1 = this.compositionWindow.getSize();
    Dimension dimension2 = getToolkit().getScreenSize();
    this.compositionWindow.setLocation(dimension2.width - dimension1.width - 20, dimension2.height - dimension1.height - 100);
    this.compositionWindow.setVisible(false);
  }
  
  void setHandlerInfo(CompositionAreaHandler paramCompositionAreaHandler, InputContext paramInputContext) {
    this.handler = paramCompositionAreaHandler;
    ((InputMethodWindow)this.compositionWindow).setInputContext(paramInputContext);
  }
  
  public InputMethodRequests getInputMethodRequests() { return this.handler; }
  
  private Rectangle getCaretRectangle(TextHitInfo paramTextHitInfo) {
    int i = 0;
    TextLayout textLayout = this.composedTextLayout;
    if (textLayout != null)
      i = Math.round(textLayout.getCaretInfo(paramTextHitInfo)[0]); 
    graphics = getGraphics();
    FontMetrics fontMetrics = null;
    try {
      fontMetrics = graphics.getFontMetrics();
    } finally {
      graphics.dispose();
    } 
    return new Rectangle(5 + i, 15 - fontMetrics.getAscent(), 0, fontMetrics.getAscent() + fontMetrics.getDescent());
  }
  
  public void paint(Graphics paramGraphics) {
    super.paint(paramGraphics);
    paramGraphics.setColor(getForeground());
    TextLayout textLayout = this.composedTextLayout;
    if (textLayout != null)
      textLayout.draw((Graphics2D)paramGraphics, 5.0F, 15.0F); 
    if (this.caret != null) {
      Rectangle rectangle = getCaretRectangle(this.caret);
      paramGraphics.setXORMode(getBackground());
      paramGraphics.fillRect(rectangle.x, rectangle.y, 1, rectangle.height);
      paramGraphics.setPaintMode();
    } 
  }
  
  void setCompositionAreaVisible(boolean paramBoolean) { this.compositionWindow.setVisible(paramBoolean); }
  
  boolean isCompositionAreaVisible() { return this.compositionWindow.isVisible(); }
  
  public void inputMethodTextChanged(InputMethodEvent paramInputMethodEvent) { this.handler.inputMethodTextChanged(paramInputMethodEvent); }
  
  public void caretPositionChanged(InputMethodEvent paramInputMethodEvent) { this.handler.caretPositionChanged(paramInputMethodEvent); }
  
  void setText(AttributedCharacterIterator paramAttributedCharacterIterator, TextHitInfo paramTextHitInfo) {
    this.composedTextLayout = null;
    if (paramAttributedCharacterIterator == null) {
      this.compositionWindow.setVisible(false);
      this.caret = null;
    } else {
      if (!this.compositionWindow.isVisible())
        this.compositionWindow.setVisible(true); 
      graphics = getGraphics();
      if (graphics == null)
        return; 
      try {
        updateWindowLocation();
        FontRenderContext fontRenderContext = ((Graphics2D)graphics).getFontRenderContext();
        this.composedTextLayout = new TextLayout(paramAttributedCharacterIterator, fontRenderContext);
        Rectangle2D rectangle2D1 = this.composedTextLayout.getBounds();
        this.caret = paramTextHitInfo;
        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle2D rectangle2D2 = fontMetrics.getMaxCharBounds(graphics);
        int i = (int)rectangle2D2.getHeight() + 3;
        int j = i + (this.compositionWindow.getInsets()).top + (this.compositionWindow.getInsets()).bottom;
        InputMethodRequests inputMethodRequests = this.handler.getClientInputMethodRequests();
        int k = (inputMethodRequests == null) ? 480 : ((int)rectangle2D1.getWidth() + 10);
        int m = k + (this.compositionWindow.getInsets()).left + (this.compositionWindow.getInsets()).right;
        setPreferredSize(new Dimension(k, i));
        this.compositionWindow.setSize(new Dimension(m, j));
        paint(graphics);
      } finally {
        graphics.dispose();
      } 
    } 
  }
  
  void setCaret(TextHitInfo paramTextHitInfo) {
    this.caret = paramTextHitInfo;
    if (this.compositionWindow.isVisible()) {
      graphics = getGraphics();
      try {
        paint(graphics);
      } finally {
        graphics.dispose();
      } 
    } 
  }
  
  void updateWindowLocation() {
    InputMethodRequests inputMethodRequests = this.handler.getClientInputMethodRequests();
    if (inputMethodRequests == null)
      return; 
    Point point = new Point();
    Rectangle rectangle = inputMethodRequests.getTextLocation(null);
    Dimension dimension1 = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension dimension2 = this.compositionWindow.getSize();
    if (rectangle.x + dimension2.width > dimension1.width) {
      point.x = dimension1.width - dimension2.width;
    } else {
      point.x = rectangle.x;
    } 
    if (rectangle.y + rectangle.height + 2 + dimension2.height > dimension1.height) {
      point.y = rectangle.y - 2 - dimension2.height;
    } else {
      point.y = rectangle.y + rectangle.height + 2;
    } 
    this.compositionWindow.setLocation(point);
  }
  
  Rectangle getTextLocation(TextHitInfo paramTextHitInfo) {
    Rectangle rectangle = getCaretRectangle(paramTextHitInfo);
    Point point = getLocationOnScreen();
    rectangle.translate(point.x, point.y);
    return rectangle;
  }
  
  TextHitInfo getLocationOffset(int paramInt1, int paramInt2) {
    TextLayout textLayout = this.composedTextLayout;
    if (textLayout == null)
      return null; 
    Point point = getLocationOnScreen();
    paramInt1 -= point.x + 5;
    paramInt2 -= point.y + 15;
    return textLayout.getBounds().contains(paramInt1, paramInt2) ? textLayout.hitTestChar(paramInt1, paramInt2) : null;
  }
  
  void setCompositionAreaUndecorated(boolean paramBoolean) {
    if (this.compositionWindow.isDisplayable())
      this.compositionWindow.removeNotify(); 
    this.compositionWindow.setUndecorated(paramBoolean);
    this.compositionWindow.pack();
  }
  
  class FrameWindowAdapter extends WindowAdapter {
    public void windowActivated(WindowEvent param1WindowEvent) { CompositionArea.this.requestFocus(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\im\CompositionArea.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */