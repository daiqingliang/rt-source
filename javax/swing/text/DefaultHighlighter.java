package javax.swing.text;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TextUI;

public class DefaultHighlighter extends LayeredHighlighter {
  private static final Highlighter.Highlight[] noHighlights = new Highlighter.Highlight[0];
  
  private Vector<HighlightInfo> highlights = new Vector();
  
  private JTextComponent component;
  
  private boolean drawsLayeredHighlights = true;
  
  private SafeDamager safeDamager = new SafeDamager();
  
  public static final LayeredHighlighter.LayerPainter DefaultPainter = new DefaultHighlightPainter(null);
  
  public void paint(Graphics paramGraphics) {
    int i = this.highlights.size();
    for (byte b = 0; b < i; b++) {
      HighlightInfo highlightInfo = (HighlightInfo)this.highlights.elementAt(b);
      if (!(highlightInfo instanceof LayeredHighlightInfo)) {
        Rectangle rectangle = this.component.getBounds();
        Insets insets = this.component.getInsets();
        rectangle.x = insets.left;
        rectangle.y = insets.top;
        rectangle.width -= insets.left + insets.right;
        rectangle.height -= insets.top + insets.bottom;
        while (b < i) {
          highlightInfo = (HighlightInfo)this.highlights.elementAt(b);
          if (!(highlightInfo instanceof LayeredHighlightInfo)) {
            Highlighter.HighlightPainter highlightPainter = highlightInfo.getPainter();
            highlightPainter.paint(paramGraphics, highlightInfo.getStartOffset(), highlightInfo.getEndOffset(), rectangle, this.component);
          } 
          b++;
        } 
      } 
    } 
  }
  
  public void install(JTextComponent paramJTextComponent) {
    this.component = paramJTextComponent;
    removeAllHighlights();
  }
  
  public void deinstall(JTextComponent paramJTextComponent) { this.component = null; }
  
  public Object addHighlight(int paramInt1, int paramInt2, Highlighter.HighlightPainter paramHighlightPainter) throws BadLocationException {
    if (paramInt1 < 0)
      throw new BadLocationException("Invalid start offset", paramInt1); 
    if (paramInt2 < paramInt1)
      throw new BadLocationException("Invalid end offset", paramInt2); 
    Document document = this.component.getDocument();
    LayeredHighlightInfo layeredHighlightInfo = (getDrawsLayeredHighlights() && paramHighlightPainter instanceof LayeredHighlighter.LayerPainter) ? new LayeredHighlightInfo() : new HighlightInfo();
    layeredHighlightInfo.painter = paramHighlightPainter;
    layeredHighlightInfo.p0 = document.createPosition(paramInt1);
    layeredHighlightInfo.p1 = document.createPosition(paramInt2);
    this.highlights.addElement(layeredHighlightInfo);
    safeDamageRange(paramInt1, paramInt2);
    return layeredHighlightInfo;
  }
  
  public void removeHighlight(Object paramObject) {
    if (paramObject instanceof LayeredHighlightInfo) {
      LayeredHighlightInfo layeredHighlightInfo = (LayeredHighlightInfo)paramObject;
      if (layeredHighlightInfo.width > 0 && layeredHighlightInfo.height > 0)
        this.component.repaint(layeredHighlightInfo.x, layeredHighlightInfo.y, layeredHighlightInfo.width, layeredHighlightInfo.height); 
    } else {
      HighlightInfo highlightInfo = (HighlightInfo)paramObject;
      safeDamageRange(highlightInfo.p0, highlightInfo.p1);
    } 
    this.highlights.removeElement(paramObject);
  }
  
  public void removeAllHighlights() {
    TextUI textUI = this.component.getUI();
    if (getDrawsLayeredHighlights()) {
      int i = this.highlights.size();
      if (i != 0) {
        int j = 0;
        int k = 0;
        int m = 0;
        int n = 0;
        int i1 = -1;
        int i2 = -1;
        for (b = 0; b < i; b++) {
          HighlightInfo highlightInfo = (HighlightInfo)this.highlights.elementAt(b);
          if (highlightInfo instanceof LayeredHighlightInfo) {
            LayeredHighlightInfo layeredHighlightInfo = (LayeredHighlightInfo)highlightInfo;
            j = Math.min(j, layeredHighlightInfo.x);
            k = Math.min(k, layeredHighlightInfo.y);
            m = Math.max(m, layeredHighlightInfo.x + layeredHighlightInfo.width);
            n = Math.max(n, layeredHighlightInfo.y + layeredHighlightInfo.height);
          } else if (i1 == -1) {
            i1 = highlightInfo.p0.getOffset();
            i2 = highlightInfo.p1.getOffset();
          } else {
            i1 = Math.min(i1, highlightInfo.p0.getOffset());
            i2 = Math.max(i2, highlightInfo.p1.getOffset());
          } 
        } 
        if (j != m && k != n)
          this.component.repaint(j, k, m - j, n - k); 
        if (i1 != -1)
          try {
            safeDamageRange(i1, i2);
          } catch (BadLocationException b) {
            BadLocationException badLocationException;
          }  
        this.highlights.removeAllElements();
      } 
    } else if (textUI != null) {
      int i = this.highlights.size();
      if (i != 0) {
        int j = Integer.MAX_VALUE;
        int k = 0;
        for (b = 0; b < i; b++) {
          HighlightInfo highlightInfo = (HighlightInfo)this.highlights.elementAt(b);
          j = Math.min(j, highlightInfo.p0.getOffset());
          k = Math.max(k, highlightInfo.p1.getOffset());
        } 
        try {
          safeDamageRange(j, k);
        } catch (BadLocationException b) {
          BadLocationException badLocationException;
        } 
        this.highlights.removeAllElements();
      } 
    } 
  }
  
  public void changeHighlight(Object paramObject, int paramInt1, int paramInt2) throws BadLocationException {
    if (paramInt1 < 0)
      throw new BadLocationException("Invalid beginning of the range", paramInt1); 
    if (paramInt2 < paramInt1)
      throw new BadLocationException("Invalid end of the range", paramInt2); 
    Document document = this.component.getDocument();
    if (paramObject instanceof LayeredHighlightInfo) {
      LayeredHighlightInfo layeredHighlightInfo = (LayeredHighlightInfo)paramObject;
      if (layeredHighlightInfo.width > 0 && layeredHighlightInfo.height > 0)
        this.component.repaint(layeredHighlightInfo.x, layeredHighlightInfo.y, layeredHighlightInfo.width, layeredHighlightInfo.height); 
      layeredHighlightInfo.width = layeredHighlightInfo.height = 0;
      layeredHighlightInfo.p0 = document.createPosition(paramInt1);
      layeredHighlightInfo.p1 = document.createPosition(paramInt2);
      safeDamageRange(Math.min(paramInt1, paramInt2), Math.max(paramInt1, paramInt2));
    } else {
      HighlightInfo highlightInfo = (HighlightInfo)paramObject;
      int i = highlightInfo.p0.getOffset();
      int j = highlightInfo.p1.getOffset();
      if (paramInt1 == i) {
        safeDamageRange(Math.min(j, paramInt2), Math.max(j, paramInt2));
      } else if (paramInt2 == j) {
        safeDamageRange(Math.min(paramInt1, i), Math.max(paramInt1, i));
      } else {
        safeDamageRange(i, j);
        safeDamageRange(paramInt1, paramInt2);
      } 
      highlightInfo.p0 = document.createPosition(paramInt1);
      highlightInfo.p1 = document.createPosition(paramInt2);
    } 
  }
  
  public Highlighter.Highlight[] getHighlights() {
    int i = this.highlights.size();
    if (i == 0)
      return noHighlights; 
    Highlighter.Highlight[] arrayOfHighlight = new Highlighter.Highlight[i];
    this.highlights.copyInto(arrayOfHighlight);
    return arrayOfHighlight;
  }
  
  public void paintLayeredHighlights(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent, View paramView) {
    for (int i = this.highlights.size() - 1; i >= 0; i--) {
      HighlightInfo highlightInfo = (HighlightInfo)this.highlights.elementAt(i);
      if (highlightInfo instanceof LayeredHighlightInfo) {
        LayeredHighlightInfo layeredHighlightInfo = (LayeredHighlightInfo)highlightInfo;
        int j = layeredHighlightInfo.getStartOffset();
        int k = layeredHighlightInfo.getEndOffset();
        if ((paramInt1 < j && paramInt2 > j) || (paramInt1 >= j && paramInt1 < k))
          layeredHighlightInfo.paintLayeredHighlights(paramGraphics, paramInt1, paramInt2, paramShape, paramJTextComponent, paramView); 
      } 
    } 
  }
  
  private void safeDamageRange(Position paramPosition1, Position paramPosition2) { this.safeDamager.damageRange(paramPosition1, paramPosition2); }
  
  private void safeDamageRange(int paramInt1, int paramInt2) throws BadLocationException {
    Document document = this.component.getDocument();
    safeDamageRange(document.createPosition(paramInt1), document.createPosition(paramInt2));
  }
  
  public void setDrawsLayeredHighlights(boolean paramBoolean) { this.drawsLayeredHighlights = paramBoolean; }
  
  public boolean getDrawsLayeredHighlights() { return this.drawsLayeredHighlights; }
  
  public static class DefaultHighlightPainter extends LayeredHighlighter.LayerPainter {
    private Color color;
    
    public DefaultHighlightPainter(Color param1Color) { this.color = param1Color; }
    
    public Color getColor() { return this.color; }
    
    public void paint(Graphics param1Graphics, int param1Int1, int param1Int2, Shape param1Shape, JTextComponent param1JTextComponent) {
      Rectangle rectangle = param1Shape.getBounds();
      try {
        TextUI textUI = param1JTextComponent.getUI();
        Rectangle rectangle1 = textUI.modelToView(param1JTextComponent, param1Int1);
        Rectangle rectangle2 = textUI.modelToView(param1JTextComponent, param1Int2);
        Color color1 = getColor();
        if (color1 == null) {
          param1Graphics.setColor(param1JTextComponent.getSelectionColor());
        } else {
          param1Graphics.setColor(color1);
        } 
        if (rectangle1.y == rectangle2.y) {
          Rectangle rectangle3 = rectangle1.union(rectangle2);
          param1Graphics.fillRect(rectangle3.x, rectangle3.y, rectangle3.width, rectangle3.height);
        } else {
          int i = rectangle.x + rectangle.width - rectangle1.x;
          param1Graphics.fillRect(rectangle1.x, rectangle1.y, i, rectangle1.height);
          if (rectangle1.y + rectangle1.height != rectangle2.y)
            param1Graphics.fillRect(rectangle.x, rectangle1.y + rectangle1.height, rectangle.width, rectangle2.y - rectangle1.y + rectangle1.height); 
          param1Graphics.fillRect(rectangle.x, rectangle2.y, rectangle2.x - rectangle.x, rectangle2.height);
        } 
      } catch (BadLocationException badLocationException) {}
    }
    
    public Shape paintLayer(Graphics param1Graphics, int param1Int1, int param1Int2, Shape param1Shape, JTextComponent param1JTextComponent, View param1View) {
      Shape shape;
      Color color1 = getColor();
      if (color1 == null) {
        param1Graphics.setColor(param1JTextComponent.getSelectionColor());
      } else {
        param1Graphics.setColor(color1);
      } 
      if (param1Int1 == param1View.getStartOffset() && param1Int2 == param1View.getEndOffset()) {
        if (param1Shape instanceof Rectangle) {
          shape = (Rectangle)param1Shape;
        } else {
          shape = param1Shape.getBounds();
        } 
      } else {
        try {
          Shape shape1 = param1View.modelToView(param1Int1, Position.Bias.Forward, param1Int2, Position.Bias.Backward, param1Shape);
          shape = (shape1 instanceof Rectangle) ? (Rectangle)shape1 : shape1.getBounds();
        } catch (BadLocationException badLocationException) {
          shape = null;
        } 
      } 
      if (shape != null) {
        shape.width = Math.max(shape.width, 1);
        param1Graphics.fillRect(shape.x, shape.y, shape.width, shape.height);
      } 
      return shape;
    }
  }
  
  class HighlightInfo implements Highlighter.Highlight {
    Position p0;
    
    Position p1;
    
    Highlighter.HighlightPainter painter;
    
    public int getStartOffset() { return this.p0.getOffset(); }
    
    public int getEndOffset() { return this.p1.getOffset(); }
    
    public Highlighter.HighlightPainter getPainter() { return this.painter; }
  }
  
  class LayeredHighlightInfo extends HighlightInfo {
    int x;
    
    int y;
    
    int width;
    
    int height;
    
    LayeredHighlightInfo() { super(DefaultHighlighter.this); }
    
    void union(Shape param1Shape) {
      Rectangle rectangle;
      if (param1Shape == null)
        return; 
      if (param1Shape instanceof Rectangle) {
        rectangle = (Rectangle)param1Shape;
      } else {
        rectangle = param1Shape.getBounds();
      } 
      if (this.width == 0 || this.height == 0) {
        this.x = rectangle.x;
        this.y = rectangle.y;
        this.width = rectangle.width;
        this.height = rectangle.height;
      } else {
        this.width = Math.max(this.x + this.width, rectangle.x + rectangle.width);
        this.height = Math.max(this.y + this.height, rectangle.y + rectangle.height);
        this.x = Math.min(this.x, rectangle.x);
        this.width -= this.x;
        this.y = Math.min(this.y, rectangle.y);
        this.height -= this.y;
      } 
    }
    
    void paintLayeredHighlights(Graphics param1Graphics, int param1Int1, int param1Int2, Shape param1Shape, JTextComponent param1JTextComponent, View param1View) {
      int i = getStartOffset();
      int j = getEndOffset();
      param1Int1 = Math.max(i, param1Int1);
      param1Int2 = Math.min(j, param1Int2);
      union(((LayeredHighlighter.LayerPainter)this.painter).paintLayer(param1Graphics, param1Int1, param1Int2, param1Shape, param1JTextComponent, param1View));
    }
  }
  
  class SafeDamager implements Runnable {
    private Vector<Position> p0 = new Vector(10);
    
    private Vector<Position> p1 = new Vector(10);
    
    private Document lastDoc = null;
    
    public void run() {
      if (DefaultHighlighter.this.component != null) {
        TextUI textUI = DefaultHighlighter.this.component.getUI();
        if (textUI != null && this.lastDoc == DefaultHighlighter.this.component.getDocument()) {
          int i = this.p0.size();
          for (byte b = 0; b < i; b++)
            textUI.damageRange(DefaultHighlighter.this.component, ((Position)this.p0.get(b)).getOffset(), ((Position)this.p1.get(b)).getOffset()); 
        } 
      } 
      this.p0.clear();
      this.p1.clear();
      this.lastDoc = null;
    }
    
    public void damageRange(Position param1Position1, Position param1Position2) {
      if (DefaultHighlighter.this.component == null) {
        this.p0.clear();
        this.lastDoc = null;
        return;
      } 
      boolean bool = this.p0.isEmpty();
      Document document = DefaultHighlighter.this.component.getDocument();
      if (document != this.lastDoc) {
        if (!this.p0.isEmpty()) {
          this.p0.clear();
          this.p1.clear();
        } 
        this.lastDoc = document;
      } 
      this.p0.add(param1Position1);
      this.p1.add(param1Position2);
      if (bool)
        SwingUtilities.invokeLater(this); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\DefaultHighlighter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */