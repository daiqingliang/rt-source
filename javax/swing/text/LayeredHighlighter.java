package javax.swing.text;

import java.awt.Graphics;
import java.awt.Shape;

public abstract class LayeredHighlighter implements Highlighter {
  public abstract void paintLayeredHighlights(Graphics paramGraphics, int paramInt1, int paramInt2, Shape paramShape, JTextComponent paramJTextComponent, View paramView);
  
  public static abstract class LayerPainter implements Highlighter.HighlightPainter {
    public abstract Shape paintLayer(Graphics param1Graphics, int param1Int1, int param1Int2, Shape param1Shape, JTextComponent param1JTextComponent, View param1View);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\LayeredHighlighter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */