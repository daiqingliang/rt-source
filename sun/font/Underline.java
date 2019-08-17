package sun.font;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.concurrent.ConcurrentHashMap;

abstract class Underline {
  private static final float DEFAULT_THICKNESS = 1.0F;
  
  private static final boolean USE_THICKNESS = true;
  
  private static final boolean IGNORE_THICKNESS = false;
  
  private static final ConcurrentHashMap<Object, Underline> UNDERLINES = new ConcurrentHashMap(6);
  
  private static final Underline[] UNDERLINE_LIST;
  
  abstract void drawUnderline(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  abstract float getLowerDrawLimit(float paramFloat);
  
  abstract Shape getUnderlineShape(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  static Underline getUnderline(Object paramObject) { return (paramObject == null) ? null : (Underline)UNDERLINES.get(paramObject); }
  
  static Underline getUnderline(int paramInt) { return (paramInt < 0) ? null : UNDERLINE_LIST[paramInt]; }
  
  static  {
    Underline[] arrayOfUnderline = new Underline[6];
    arrayOfUnderline[0] = new StandardUnderline(0.0F, 1.0F, null, true);
    UNDERLINES.put(TextAttribute.UNDERLINE_ON, arrayOfUnderline[0]);
    arrayOfUnderline[1] = new StandardUnderline(1.0F, 1.0F, null, false);
    UNDERLINES.put(TextAttribute.UNDERLINE_LOW_ONE_PIXEL, arrayOfUnderline[1]);
    arrayOfUnderline[2] = new StandardUnderline(1.0F, 2.0F, null, false);
    UNDERLINES.put(TextAttribute.UNDERLINE_LOW_TWO_PIXEL, arrayOfUnderline[2]);
    arrayOfUnderline[3] = new StandardUnderline(1.0F, 1.0F, new float[] { 1.0F, 1.0F }, false);
    UNDERLINES.put(TextAttribute.UNDERLINE_LOW_DOTTED, arrayOfUnderline[3]);
    arrayOfUnderline[4] = new IMGrayUnderline();
    UNDERLINES.put(TextAttribute.UNDERLINE_LOW_GRAY, arrayOfUnderline[4]);
    arrayOfUnderline[5] = new StandardUnderline(1.0F, 1.0F, new float[] { 4.0F, 4.0F }, false);
    UNDERLINES.put(TextAttribute.UNDERLINE_LOW_DASHED, arrayOfUnderline[5]);
    UNDERLINE_LIST = arrayOfUnderline;
  }
  
  private static class IMGrayUnderline extends Underline {
    private BasicStroke stroke = new BasicStroke(1.0F, 0, 0, 10.0F, new float[] { 1.0F, 1.0F }, 0.0F);
    
    void drawUnderline(Graphics2D param1Graphics2D, float param1Float1, float param1Float2, float param1Float3, float param1Float4) {
      Stroke stroke1 = param1Graphics2D.getStroke();
      param1Graphics2D.setStroke(this.stroke);
      Line2D.Float float = new Line2D.Float(param1Float2, param1Float4, param1Float3, param1Float4);
      param1Graphics2D.draw(float);
      float.y1++;
      float.y2++;
      float.x1++;
      param1Graphics2D.draw(float);
      param1Graphics2D.setStroke(stroke1);
    }
    
    float getLowerDrawLimit(float param1Float) { return 2.0F; }
    
    Shape getUnderlineShape(float param1Float1, float param1Float2, float param1Float3, float param1Float4) {
      GeneralPath generalPath = new GeneralPath();
      Line2D.Float float = new Line2D.Float(param1Float2, param1Float4, param1Float3, param1Float4);
      generalPath.append(this.stroke.createStrokedShape(float), false);
      float.y1++;
      float.y2++;
      float.x1++;
      generalPath.append(this.stroke.createStrokedShape(float), false);
      return generalPath;
    }
  }
  
  private static final class StandardUnderline extends Underline {
    private float shift;
    
    private float thicknessMultiplier;
    
    private float[] dashPattern;
    
    private boolean useThickness;
    
    private BasicStroke cachedStroke;
    
    StandardUnderline(float param1Float1, float param1Float2, float[] param1ArrayOfFloat, boolean param1Boolean) {
      this.shift = param1Float1;
      this.thicknessMultiplier = param1Float2;
      this.dashPattern = param1ArrayOfFloat;
      this.useThickness = param1Boolean;
      this.cachedStroke = null;
    }
    
    private BasicStroke createStroke(float param1Float) { return (this.dashPattern == null) ? new BasicStroke(param1Float, 0, 0) : new BasicStroke(param1Float, 0, 0, 10.0F, this.dashPattern, 0.0F); }
    
    private float getLineThickness(float param1Float) { return this.useThickness ? (param1Float * this.thicknessMultiplier) : (1.0F * this.thicknessMultiplier); }
    
    private Stroke getStroke(float param1Float) {
      float f = getLineThickness(param1Float);
      BasicStroke basicStroke = this.cachedStroke;
      if (basicStroke == null || basicStroke.getLineWidth() != f) {
        basicStroke = createStroke(f);
        this.cachedStroke = basicStroke;
      } 
      return basicStroke;
    }
    
    void drawUnderline(Graphics2D param1Graphics2D, float param1Float1, float param1Float2, float param1Float3, float param1Float4) {
      Stroke stroke = param1Graphics2D.getStroke();
      param1Graphics2D.setStroke(getStroke(param1Float1));
      param1Graphics2D.draw(new Line2D.Float(param1Float2, param1Float4 + this.shift, param1Float3, param1Float4 + this.shift));
      param1Graphics2D.setStroke(stroke);
    }
    
    float getLowerDrawLimit(float param1Float) { return this.shift + getLineThickness(param1Float); }
    
    Shape getUnderlineShape(float param1Float1, float param1Float2, float param1Float3, float param1Float4) {
      Stroke stroke = getStroke(param1Float1);
      Line2D.Float float = new Line2D.Float(param1Float2, param1Float4 + this.shift, param1Float3, param1Float4 + this.shift);
      return stroke.createStrokedShape(float);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\Underline.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */