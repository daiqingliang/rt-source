package javax.swing.text;

import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;

class GlyphPainter1 extends GlyphView.GlyphPainter {
  FontMetrics metrics;
  
  public float getSpan(GlyphView paramGlyphView, int paramInt1, int paramInt2, TabExpander paramTabExpander, float paramFloat) {
    sync(paramGlyphView);
    Segment segment = paramGlyphView.getText(paramInt1, paramInt2);
    int[] arrayOfInt = getJustificationData(paramGlyphView);
    int i = Utilities.getTabbedTextWidth(paramGlyphView, segment, this.metrics, (int)paramFloat, paramTabExpander, paramInt1, arrayOfInt);
    SegmentCache.releaseSharedSegment(segment);
    return i;
  }
  
  public float getHeight(GlyphView paramGlyphView) {
    sync(paramGlyphView);
    return this.metrics.getHeight();
  }
  
  public float getAscent(GlyphView paramGlyphView) {
    sync(paramGlyphView);
    return this.metrics.getAscent();
  }
  
  public float getDescent(GlyphView paramGlyphView) {
    sync(paramGlyphView);
    return this.metrics.getDescent();
  }
  
  public void paint(GlyphView paramGlyphView, Graphics paramGraphics, Shape paramShape, int paramInt1, int paramInt2) {
    sync(paramGlyphView);
    TabExpander tabExpander = paramGlyphView.getTabExpander();
    Rectangle rectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    int i = rectangle.x;
    int j = paramGlyphView.getStartOffset();
    int[] arrayOfInt = getJustificationData(paramGlyphView);
    if (j != paramInt1) {
      Segment segment1 = paramGlyphView.getText(j, paramInt1);
      int m = Utilities.getTabbedTextWidth(paramGlyphView, segment1, this.metrics, i, tabExpander, j, arrayOfInt);
      i += m;
      SegmentCache.releaseSharedSegment(segment1);
    } 
    int k = rectangle.y + this.metrics.getHeight() - this.metrics.getDescent();
    Segment segment = paramGlyphView.getText(paramInt1, paramInt2);
    paramGraphics.setFont(this.metrics.getFont());
    Utilities.drawTabbedText(paramGlyphView, segment, i, k, paramGraphics, tabExpander, paramInt1, arrayOfInt);
    SegmentCache.releaseSharedSegment(segment);
  }
  
  public Shape modelToView(GlyphView paramGlyphView, int paramInt, Position.Bias paramBias, Shape paramShape) throws BadLocationException {
    sync(paramGlyphView);
    Rectangle rectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    int i = paramGlyphView.getStartOffset();
    int j = paramGlyphView.getEndOffset();
    TabExpander tabExpander = paramGlyphView.getTabExpander();
    if (paramInt == j)
      return new Rectangle(rectangle.x + rectangle.width, rectangle.y, 0, this.metrics.getHeight()); 
    if (paramInt >= i && paramInt <= j) {
      Segment segment = paramGlyphView.getText(i, paramInt);
      int[] arrayOfInt = getJustificationData(paramGlyphView);
      int k = Utilities.getTabbedTextWidth(paramGlyphView, segment, this.metrics, rectangle.x, tabExpander, i, arrayOfInt);
      SegmentCache.releaseSharedSegment(segment);
      return new Rectangle(rectangle.x + k, rectangle.y, 0, this.metrics.getHeight());
    } 
    throw new BadLocationException("modelToView - can't convert", j);
  }
  
  public int viewToModel(GlyphView paramGlyphView, float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias) {
    sync(paramGlyphView);
    Rectangle rectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    int i = paramGlyphView.getStartOffset();
    int j = paramGlyphView.getEndOffset();
    TabExpander tabExpander = paramGlyphView.getTabExpander();
    Segment segment = paramGlyphView.getText(i, j);
    int[] arrayOfInt = getJustificationData(paramGlyphView);
    int k = Utilities.getTabbedTextOffset(paramGlyphView, segment, this.metrics, rectangle.x, (int)paramFloat1, tabExpander, i, arrayOfInt);
    SegmentCache.releaseSharedSegment(segment);
    int m = i + k;
    if (m == j)
      m--; 
    paramArrayOfBias[0] = Position.Bias.Forward;
    return m;
  }
  
  public int getBoundedPosition(GlyphView paramGlyphView, int paramInt, float paramFloat1, float paramFloat2) {
    sync(paramGlyphView);
    TabExpander tabExpander = paramGlyphView.getTabExpander();
    Segment segment = paramGlyphView.getText(paramInt, paramGlyphView.getEndOffset());
    int[] arrayOfInt = getJustificationData(paramGlyphView);
    int i = Utilities.getTabbedTextOffset(paramGlyphView, segment, this.metrics, (int)paramFloat1, (int)(paramFloat1 + paramFloat2), tabExpander, paramInt, false, arrayOfInt);
    SegmentCache.releaseSharedSegment(segment);
    return paramInt + i;
  }
  
  void sync(GlyphView paramGlyphView) {
    Font font = paramGlyphView.getFont();
    if (this.metrics == null || !font.equals(this.metrics.getFont())) {
      Container container = paramGlyphView.getContainer();
      this.metrics = (container != null) ? container.getFontMetrics(font) : Toolkit.getDefaultToolkit().getFontMetrics(font);
    } 
  }
  
  private int[] getJustificationData(GlyphView paramGlyphView) {
    View view = paramGlyphView.getParent();
    int[] arrayOfInt = null;
    if (view instanceof ParagraphView.Row) {
      ParagraphView.Row row = (ParagraphView.Row)view;
      arrayOfInt = row.justificationData;
    } 
    return arrayOfInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\GlyphPainter1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */