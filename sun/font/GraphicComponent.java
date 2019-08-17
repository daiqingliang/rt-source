package sun.font;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GraphicAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

public final class GraphicComponent implements TextLineComponent, Decoration.Label {
  public static final float GRAPHIC_LEADING = 2.0F;
  
  private GraphicAttribute graphic;
  
  private int graphicCount;
  
  private int[] charsLtoV;
  
  private byte[] levels;
  
  private Rectangle2D visualBounds = null;
  
  private float graphicAdvance;
  
  private AffineTransform baseTx;
  
  private CoreMetrics cm;
  
  private Decoration decorator;
  
  public GraphicComponent(GraphicAttribute paramGraphicAttribute, Decoration paramDecoration, int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2, AffineTransform paramAffineTransform) {
    if (paramInt2 <= paramInt1)
      throw new IllegalArgumentException("0 or negative length in GraphicComponent"); 
    this.graphic = paramGraphicAttribute;
    this.graphicAdvance = paramGraphicAttribute.getAdvance();
    this.decorator = paramDecoration;
    this.cm = createCoreMetrics(paramGraphicAttribute);
    this.baseTx = paramAffineTransform;
    initLocalOrdering(paramArrayOfInt, paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private GraphicComponent(GraphicComponent paramGraphicComponent, int paramInt1, int paramInt2, int paramInt3) {
    this.graphic = paramGraphicComponent.graphic;
    this.graphicAdvance = paramGraphicComponent.graphicAdvance;
    this.decorator = paramGraphicComponent.decorator;
    this.cm = paramGraphicComponent.cm;
    this.baseTx = paramGraphicComponent.baseTx;
    int[] arrayOfInt = null;
    byte[] arrayOfByte = null;
    if (paramInt3 == 2) {
      arrayOfInt = paramGraphicComponent.charsLtoV;
      arrayOfByte = paramGraphicComponent.levels;
    } else if (paramInt3 == 0 || paramInt3 == 1) {
      paramInt2 -= paramInt1;
      paramInt1 = 0;
      if (paramInt3 == 1) {
        arrayOfInt = new int[paramInt2];
        arrayOfByte = new byte[paramInt2];
        for (int i = 0; i < paramInt2; i++) {
          arrayOfInt[i] = paramInt2 - i - 1;
          arrayOfByte[i] = 1;
        } 
      } 
    } else {
      throw new IllegalArgumentException("Invalid direction flag");
    } 
    initLocalOrdering(arrayOfInt, arrayOfByte, paramInt1, paramInt2);
  }
  
  private void initLocalOrdering(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    this.graphicCount = paramInt2 - paramInt1;
    if (paramArrayOfInt == null || paramArrayOfInt.length == this.graphicCount) {
      this.charsLtoV = paramArrayOfInt;
    } else {
      this.charsLtoV = BidiUtils.createNormalizedMap(paramArrayOfInt, paramArrayOfByte, paramInt1, paramInt2);
    } 
    if (paramArrayOfByte == null || paramArrayOfByte.length == this.graphicCount) {
      this.levels = paramArrayOfByte;
    } else {
      this.levels = new byte[this.graphicCount];
      System.arraycopy(paramArrayOfByte, paramInt1, this.levels, 0, this.graphicCount);
    } 
  }
  
  public boolean isSimple() { return false; }
  
  public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2) { throw new InternalError("do not call if isSimple returns false"); }
  
  public Rectangle2D handleGetVisualBounds() {
    Rectangle2D rectangle2D = this.graphic.getBounds();
    float f = (float)rectangle2D.getWidth() + this.graphicAdvance * (this.graphicCount - 1);
    return new Rectangle2D.Float((float)rectangle2D.getX(), (float)rectangle2D.getY(), f, (float)rectangle2D.getHeight());
  }
  
  public CoreMetrics getCoreMetrics() { return this.cm; }
  
  public static CoreMetrics createCoreMetrics(GraphicAttribute paramGraphicAttribute) { return new CoreMetrics(paramGraphicAttribute.getAscent(), paramGraphicAttribute.getDescent(), 2.0F, paramGraphicAttribute.getAscent() + paramGraphicAttribute.getDescent() + 2.0F, paramGraphicAttribute.getAlignment(), new float[] { 0.0F, -paramGraphicAttribute.getAscent() / 2.0F, -paramGraphicAttribute.getAscent() }, -paramGraphicAttribute.getAscent() / 2.0F, paramGraphicAttribute.getAscent() / 12.0F, paramGraphicAttribute.getDescent() / 3.0F, paramGraphicAttribute.getAscent() / 12.0F, 0.0F, 0.0F); }
  
  public float getItalicAngle() { return 0.0F; }
  
  public Rectangle2D getVisualBounds() {
    if (this.visualBounds == null)
      this.visualBounds = this.decorator.getVisualBounds(this); 
    Rectangle2D.Float float = new Rectangle2D.Float();
    float.setRect(this.visualBounds);
    return float;
  }
  
  public Shape handleGetOutline(float paramFloat1, float paramFloat2) {
    double[] arrayOfDouble = { 1.0D, 0.0D, 0.0D, 1.0D, paramFloat1, paramFloat2 };
    if (this.graphicCount == 1) {
      AffineTransform affineTransform = new AffineTransform(arrayOfDouble);
      return this.graphic.getOutline(affineTransform);
    } 
    GeneralPath generalPath = new GeneralPath();
    for (byte b = 0; b < this.graphicCount; b++) {
      AffineTransform affineTransform = new AffineTransform(arrayOfDouble);
      generalPath.append(this.graphic.getOutline(affineTransform), false);
      arrayOfDouble[4] = arrayOfDouble[4] + this.graphicAdvance;
    } 
    return generalPath;
  }
  
  public AffineTransform getBaselineTransform() { return this.baseTx; }
  
  public Shape getOutline(float paramFloat1, float paramFloat2) { return this.decorator.getOutline(this, paramFloat1, paramFloat2); }
  
  public void handleDraw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2) {
    for (byte b = 0; b < this.graphicCount; b++) {
      this.graphic.draw(paramGraphics2D, paramFloat1, paramFloat2);
      paramFloat1 += this.graphicAdvance;
    } 
  }
  
  public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2) { this.decorator.drawTextAndDecorations(this, paramGraphics2D, paramFloat1, paramFloat2); }
  
  public Rectangle2D getCharVisualBounds(int paramInt) { return this.decorator.getCharVisualBounds(this, paramInt); }
  
  public int getNumCharacters() { return this.graphicCount; }
  
  public float getCharX(int paramInt) {
    int i = (this.charsLtoV == null) ? paramInt : this.charsLtoV[paramInt];
    return this.graphicAdvance * i;
  }
  
  public float getCharY(int paramInt) { return 0.0F; }
  
  public float getCharAdvance(int paramInt) { return this.graphicAdvance; }
  
  public boolean caretAtOffsetIsValid(int paramInt) { return true; }
  
  public Rectangle2D handleGetCharVisualBounds(int paramInt) {
    Rectangle2D rectangle2D = this.graphic.getBounds();
    Rectangle2D.Float float = new Rectangle2D.Float();
    float.setRect(rectangle2D);
    float.x += this.graphicAdvance * paramInt;
    return float;
  }
  
  public int getLineBreakIndex(int paramInt, float paramFloat) {
    int i = (int)(paramFloat / this.graphicAdvance);
    if (i > this.graphicCount - paramInt)
      i = this.graphicCount - paramInt; 
    return i;
  }
  
  public float getAdvanceBetween(int paramInt1, int paramInt2) { return this.graphicAdvance * (paramInt2 - paramInt1); }
  
  public Rectangle2D getLogicalBounds() {
    float f1 = 0.0F;
    float f2 = -this.cm.ascent;
    float f3 = this.graphicAdvance * this.graphicCount;
    float f4 = this.cm.descent - f2;
    return new Rectangle2D.Float(f1, f2, f3, f4);
  }
  
  public float getAdvance() { return this.graphicAdvance * this.graphicCount; }
  
  public Rectangle2D getItalicBounds() { return getLogicalBounds(); }
  
  public TextLineComponent getSubset(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt1 < 0 || paramInt2 > this.graphicCount || paramInt1 >= paramInt2)
      throw new IllegalArgumentException("Invalid range.  start=" + paramInt1 + "; limit=" + paramInt2); 
    return (paramInt1 == 0 && paramInt2 == this.graphicCount && paramInt3 == 2) ? this : new GraphicComponent(this, paramInt1, paramInt2, paramInt3);
  }
  
  public String toString() { return "[graphic=" + this.graphic + ":count=" + getNumCharacters() + "]"; }
  
  public int getNumJustificationInfos() { return 0; }
  
  public void getJustificationInfos(GlyphJustificationInfo[] paramArrayOfGlyphJustificationInfo, int paramInt1, int paramInt2, int paramInt3) {}
  
  public TextLineComponent applyJustificationDeltas(float[] paramArrayOfFloat, int paramInt, boolean[] paramArrayOfBoolean) { return this; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\GraphicComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */