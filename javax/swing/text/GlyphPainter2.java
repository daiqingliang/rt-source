package javax.swing.text;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

class GlyphPainter2 extends GlyphView.GlyphPainter {
  TextLayout layout;
  
  public GlyphPainter2(TextLayout paramTextLayout) { this.layout = paramTextLayout; }
  
  public GlyphView.GlyphPainter getPainter(GlyphView paramGlyphView, int paramInt1, int paramInt2) { return null; }
  
  public float getSpan(GlyphView paramGlyphView, int paramInt1, int paramInt2, TabExpander paramTabExpander, float paramFloat) {
    if (paramInt1 == paramGlyphView.getStartOffset() && paramInt2 == paramGlyphView.getEndOffset())
      return this.layout.getAdvance(); 
    int i = paramGlyphView.getStartOffset();
    int j = paramInt1 - i;
    int k = paramInt2 - i;
    TextHitInfo textHitInfo1;
    TextHitInfo textHitInfo2 = (textHitInfo1 = TextHitInfo.afterOffset(j)).beforeOffset(k);
    float[] arrayOfFloat = this.layout.getCaretInfo(textHitInfo1);
    float f1 = arrayOfFloat[0];
    arrayOfFloat = this.layout.getCaretInfo(textHitInfo2);
    float f2 = arrayOfFloat[0];
    return (f2 > f1) ? (f2 - f1) : (f1 - f2);
  }
  
  public float getHeight(GlyphView paramGlyphView) { return this.layout.getAscent() + this.layout.getDescent() + this.layout.getLeading(); }
  
  public float getAscent(GlyphView paramGlyphView) { return this.layout.getAscent(); }
  
  public float getDescent(GlyphView paramGlyphView) { return this.layout.getDescent(); }
  
  public void paint(GlyphView paramGlyphView, Graphics paramGraphics, Shape paramShape, int paramInt1, int paramInt2) {
    if (paramGraphics instanceof Graphics2D) {
      Rectangle2D rectangle2D = paramShape.getBounds2D();
      Graphics2D graphics2D = (Graphics2D)paramGraphics;
      float f1 = (float)rectangle2D.getY() + this.layout.getAscent() + this.layout.getLeading();
      float f2 = (float)rectangle2D.getX();
      if (paramInt1 > paramGlyphView.getStartOffset() || paramInt2 < paramGlyphView.getEndOffset()) {
        try {
          Shape shape1 = paramGlyphView.modelToView(paramInt1, Position.Bias.Forward, paramInt2, Position.Bias.Backward, paramShape);
          Shape shape2 = paramGraphics.getClip();
          graphics2D.clip(shape1);
          this.layout.draw(graphics2D, f2, f1);
          paramGraphics.setClip(shape2);
        } catch (BadLocationException badLocationException) {}
      } else {
        this.layout.draw(graphics2D, f2, f1);
      } 
    } 
  }
  
  public Shape modelToView(GlyphView paramGlyphView, int paramInt, Position.Bias paramBias, Shape paramShape) throws BadLocationException {
    int i = paramInt - paramGlyphView.getStartOffset();
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    TextHitInfo textHitInfo = (paramBias == Position.Bias.Forward) ? TextHitInfo.afterOffset(i) : TextHitInfo.beforeOffset(i);
    float[] arrayOfFloat = this.layout.getCaretInfo(textHitInfo);
    rectangle2D.setRect(rectangle2D.getX() + arrayOfFloat[0], rectangle2D.getY(), 1.0D, rectangle2D.getHeight());
    return rectangle2D;
  }
  
  public int viewToModel(GlyphView paramGlyphView, float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias) {
    Rectangle2D rectangle2D = (paramShape instanceof Rectangle2D) ? (Rectangle2D)paramShape : paramShape.getBounds2D();
    TextHitInfo textHitInfo = this.layout.hitTestChar(paramFloat1 - (float)rectangle2D.getX(), 0.0F);
    int i = textHitInfo.getInsertionIndex();
    if (i == paramGlyphView.getEndOffset())
      i--; 
    paramArrayOfBias[0] = textHitInfo.isLeadingEdge() ? Position.Bias.Forward : Position.Bias.Backward;
    return i + paramGlyphView.getStartOffset();
  }
  
  public int getBoundedPosition(GlyphView paramGlyphView, int paramInt, float paramFloat1, float paramFloat2) {
    TextHitInfo textHitInfo;
    if (paramFloat2 < 0.0F)
      throw new IllegalArgumentException("Length must be >= 0."); 
    if (this.layout.isLeftToRight()) {
      textHitInfo = this.layout.hitTestChar(paramFloat2, 0.0F);
    } else {
      textHitInfo = this.layout.hitTestChar(this.layout.getAdvance() - paramFloat2, 0.0F);
    } 
    return paramGlyphView.getStartOffset() + textHitInfo.getCharIndex();
  }
  
  public int getNextVisualPositionFrom(GlyphView paramGlyphView, int paramInt1, Position.Bias paramBias, Shape paramShape, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException {
    TextHitInfo textHitInfo2;
    TextHitInfo textHitInfo1;
    boolean bool;
    Document document = paramGlyphView.getDocument();
    int i = paramGlyphView.getStartOffset();
    int j = paramGlyphView.getEndOffset();
    switch (paramInt2) {
      case 1:
      case 5:
        return paramInt1;
      case 3:
        bool = AbstractDocument.isLeftToRight(document, i, j);
        if (i == document.getLength()) {
          if (paramInt1 == -1) {
            paramArrayOfBias[0] = Position.Bias.Forward;
            return i;
          } 
          return -1;
        } 
        if (paramInt1 == -1) {
          if (bool) {
            paramArrayOfBias[0] = Position.Bias.Forward;
            return i;
          } 
          Segment segment = paramGlyphView.getText(j - 1, j);
          char c = segment.array[segment.offset];
          SegmentCache.releaseSharedSegment(segment);
          if (c == '\n') {
            paramArrayOfBias[0] = Position.Bias.Forward;
            return j - 1;
          } 
          paramArrayOfBias[0] = Position.Bias.Backward;
          return j;
        } 
        if (paramBias == Position.Bias.Forward) {
          textHitInfo1 = TextHitInfo.afterOffset(paramInt1 - i);
        } else {
          textHitInfo1 = TextHitInfo.beforeOffset(paramInt1 - i);
        } 
        textHitInfo2 = this.layout.getNextRightHit(textHitInfo1);
        if (textHitInfo2 == null)
          return -1; 
        if (bool != this.layout.isLeftToRight())
          textHitInfo2 = this.layout.getVisualOtherHit(textHitInfo2); 
        paramInt1 = textHitInfo2.getInsertionIndex() + i;
        if (paramInt1 == j) {
          Segment segment = paramGlyphView.getText(j - 1, j);
          char c = segment.array[segment.offset];
          SegmentCache.releaseSharedSegment(segment);
          if (c == '\n')
            return -1; 
          paramArrayOfBias[0] = Position.Bias.Backward;
        } else {
          paramArrayOfBias[0] = Position.Bias.Forward;
        } 
        return paramInt1;
      case 7:
        bool = AbstractDocument.isLeftToRight(document, i, j);
        if (i == document.getLength()) {
          if (paramInt1 == -1) {
            paramArrayOfBias[0] = Position.Bias.Forward;
            return i;
          } 
          return -1;
        } 
        if (paramInt1 == -1) {
          if (bool) {
            Segment segment = paramGlyphView.getText(j - 1, j);
            char c = segment.array[segment.offset];
            SegmentCache.releaseSharedSegment(segment);
            if (c == '\n' || Character.isSpaceChar(c)) {
              paramArrayOfBias[0] = Position.Bias.Forward;
              return j - 1;
            } 
            paramArrayOfBias[0] = Position.Bias.Backward;
            return j;
          } 
          paramArrayOfBias[0] = Position.Bias.Forward;
          return i;
        } 
        if (paramBias == Position.Bias.Forward) {
          textHitInfo1 = TextHitInfo.afterOffset(paramInt1 - i);
        } else {
          textHitInfo1 = TextHitInfo.beforeOffset(paramInt1 - i);
        } 
        textHitInfo2 = this.layout.getNextLeftHit(textHitInfo1);
        if (textHitInfo2 == null)
          return -1; 
        if (bool != this.layout.isLeftToRight())
          textHitInfo2 = this.layout.getVisualOtherHit(textHitInfo2); 
        paramInt1 = textHitInfo2.getInsertionIndex() + i;
        if (paramInt1 == j) {
          Segment segment = paramGlyphView.getText(j - 1, j);
          char c = segment.array[segment.offset];
          SegmentCache.releaseSharedSegment(segment);
          if (c == '\n')
            return -1; 
          paramArrayOfBias[0] = Position.Bias.Backward;
        } else {
          paramArrayOfBias[0] = Position.Bias.Forward;
        } 
        return paramInt1;
    } 
    throw new IllegalArgumentException("Bad direction: " + paramInt2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\GlyphPainter2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */