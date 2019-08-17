package sun.font;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

public class Decoration {
  private static final int VALUES_MASK = AttributeValues.getMask(new EAttribute[] { EAttribute.EFOREGROUND, EAttribute.EBACKGROUND, EAttribute.ESWAP_COLORS, EAttribute.ESTRIKETHROUGH, EAttribute.EUNDERLINE, EAttribute.EINPUT_METHOD_HIGHLIGHT, EAttribute.EINPUT_METHOD_UNDERLINE });
  
  private static final Decoration PLAIN = new Decoration();
  
  private Decoration() {}
  
  public static Decoration getPlainDecoration() { return PLAIN; }
  
  public static Decoration getDecoration(AttributeValues paramAttributeValues) {
    if (paramAttributeValues == null || !paramAttributeValues.anyDefined(VALUES_MASK))
      return PLAIN; 
    paramAttributeValues = paramAttributeValues.applyIMHighlight();
    return new DecorationImpl(paramAttributeValues.getForeground(), paramAttributeValues.getBackground(), paramAttributeValues.getSwapColors(), paramAttributeValues.getStrikethrough(), Underline.getUnderline(paramAttributeValues.getUnderline()), Underline.getUnderline(paramAttributeValues.getInputMethodUnderline()));
  }
  
  public static Decoration getDecoration(Map paramMap) { return (paramMap == null) ? PLAIN : getDecoration(AttributeValues.fromMap(paramMap)); }
  
  public void drawTextAndDecorations(Label paramLabel, Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2) { paramLabel.handleDraw(paramGraphics2D, paramFloat1, paramFloat2); }
  
  public Rectangle2D getVisualBounds(Label paramLabel) { return paramLabel.handleGetVisualBounds(); }
  
  public Rectangle2D getCharVisualBounds(Label paramLabel, int paramInt) { return paramLabel.handleGetCharVisualBounds(paramInt); }
  
  Shape getOutline(Label paramLabel, float paramFloat1, float paramFloat2) { return paramLabel.handleGetOutline(paramFloat1, paramFloat2); }
  
  private static final class DecorationImpl extends Decoration {
    private Paint fgPaint = null;
    
    private Paint bgPaint = null;
    
    private boolean swapColors = false;
    
    private boolean strikethrough = false;
    
    private Underline stdUnderline = null;
    
    private Underline imUnderline = null;
    
    DecorationImpl(Paint param1Paint1, Paint param1Paint2, boolean param1Boolean1, boolean param1Boolean2, Underline param1Underline1, Underline param1Underline2) {
      super(null);
      this.fgPaint = param1Paint1;
      this.bgPaint = param1Paint2;
      this.swapColors = param1Boolean1;
      this.strikethrough = param1Boolean2;
      this.stdUnderline = param1Underline1;
      this.imUnderline = param1Underline2;
    }
    
    private static boolean areEqual(Object param1Object1, Object param1Object2) { return (param1Object1 == null) ? ((param1Object2 == null)) : param1Object1.equals(param1Object2); }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (param1Object == null)
        return false; 
      DecorationImpl decorationImpl = null;
      try {
        decorationImpl = (DecorationImpl)param1Object;
      } catch (ClassCastException classCastException) {
        return false;
      } 
      return (this.swapColors != decorationImpl.swapColors || this.strikethrough != decorationImpl.strikethrough) ? false : (!areEqual(this.stdUnderline, decorationImpl.stdUnderline) ? false : (!areEqual(this.fgPaint, decorationImpl.fgPaint) ? false : (!areEqual(this.bgPaint, decorationImpl.bgPaint) ? false : areEqual(this.imUnderline, decorationImpl.imUnderline))));
    }
    
    public int hashCode() {
      int i = 1;
      if (this.strikethrough)
        i |= 0x2; 
      if (this.swapColors)
        i |= 0x4; 
      if (this.stdUnderline != null)
        i += this.stdUnderline.hashCode(); 
      return i;
    }
    
    private float getUnderlineMaxY(CoreMetrics param1CoreMetrics) {
      float f = 0.0F;
      if (this.stdUnderline != null) {
        float f1 = param1CoreMetrics.underlineOffset;
        f1 += this.stdUnderline.getLowerDrawLimit(param1CoreMetrics.underlineThickness);
        f = Math.max(f, f1);
      } 
      if (this.imUnderline != null) {
        float f1 = param1CoreMetrics.underlineOffset;
        f1 += this.imUnderline.getLowerDrawLimit(param1CoreMetrics.underlineThickness);
        f = Math.max(f, f1);
      } 
      return f;
    }
    
    private void drawTextAndEmbellishments(Decoration.Label param1Label, Graphics2D param1Graphics2D, float param1Float1, float param1Float2) {
      param1Label.handleDraw(param1Graphics2D, param1Float1, param1Float2);
      if (!this.strikethrough && this.stdUnderline == null && this.imUnderline == null)
        return; 
      float f1 = param1Float1;
      float f2 = f1 + (float)param1Label.getLogicalBounds().getWidth();
      CoreMetrics coreMetrics = param1Label.getCoreMetrics();
      if (this.strikethrough) {
        Stroke stroke = param1Graphics2D.getStroke();
        param1Graphics2D.setStroke(new BasicStroke(coreMetrics.strikethroughThickness, 0, 0));
        float f = param1Float2 + coreMetrics.strikethroughOffset;
        param1Graphics2D.draw(new Line2D.Float(f1, f, f2, f));
        param1Graphics2D.setStroke(stroke);
      } 
      float f3 = coreMetrics.underlineOffset;
      float f4 = coreMetrics.underlineThickness;
      if (this.stdUnderline != null)
        this.stdUnderline.drawUnderline(param1Graphics2D, f4, f1, f2, param1Float2 + f3); 
      if (this.imUnderline != null)
        this.imUnderline.drawUnderline(param1Graphics2D, f4, f1, f2, param1Float2 + f3); 
    }
    
    public void drawTextAndDecorations(Decoration.Label param1Label, Graphics2D param1Graphics2D, float param1Float1, float param1Float2) {
      if (this.fgPaint == null && this.bgPaint == null && !this.swapColors) {
        drawTextAndEmbellishments(param1Label, param1Graphics2D, param1Float1, param1Float2);
      } else {
        Paint paint3;
        Paint paint2;
        Paint paint1 = param1Graphics2D.getPaint();
        if (this.swapColors) {
          paint3 = (this.fgPaint == null) ? paint1 : this.fgPaint;
          if (this.bgPaint == null) {
            if (paint3 instanceof Color) {
              Color color = (Color)paint3;
              int i = 33 * color.getRed() + 53 * color.getGreen() + 14 * color.getBlue();
              paint2 = (i > 18500) ? Color.BLACK : Color.WHITE;
            } else {
              paint2 = Color.WHITE;
            } 
          } else {
            paint2 = this.bgPaint;
          } 
        } else {
          paint2 = (this.fgPaint == null) ? paint1 : this.fgPaint;
          paint3 = this.bgPaint;
        } 
        if (paint3 != null) {
          Rectangle2D rectangle2D = param1Label.getLogicalBounds();
          rectangle2D = new Rectangle2D.Float(param1Float1 + (float)rectangle2D.getX(), param1Float2 + (float)rectangle2D.getY(), (float)rectangle2D.getWidth(), (float)rectangle2D.getHeight());
          param1Graphics2D.setPaint(paint3);
          param1Graphics2D.fill(rectangle2D);
        } 
        param1Graphics2D.setPaint(paint2);
        drawTextAndEmbellishments(param1Label, param1Graphics2D, param1Float1, param1Float2);
        param1Graphics2D.setPaint(paint1);
      } 
    }
    
    public Rectangle2D getVisualBounds(Decoration.Label param1Label) {
      Rectangle2D rectangle2D = param1Label.handleGetVisualBounds();
      if (this.swapColors || this.bgPaint != null || this.strikethrough || this.stdUnderline != null || this.imUnderline != null) {
        float f1 = 0.0F;
        Rectangle2D rectangle2D1 = param1Label.getLogicalBounds();
        float f2 = 0.0F;
        float f3 = 0.0F;
        if (this.swapColors || this.bgPaint != null) {
          f2 = (float)rectangle2D1.getY();
          f3 = f2 + (float)rectangle2D1.getHeight();
        } 
        f3 = Math.max(f3, getUnderlineMaxY(param1Label.getCoreMetrics()));
        Rectangle2D.Float float = new Rectangle2D.Float(f1, f2, (float)rectangle2D1.getWidth(), f3 - f2);
        rectangle2D.add(float);
      } 
      return rectangle2D;
    }
    
    Shape getOutline(Decoration.Label param1Label, float param1Float1, float param1Float2) {
      if (!this.strikethrough && this.stdUnderline == null && this.imUnderline == null)
        return param1Label.handleGetOutline(param1Float1, param1Float2); 
      CoreMetrics coreMetrics = param1Label.getCoreMetrics();
      float f1 = coreMetrics.underlineThickness;
      float f2 = coreMetrics.underlineOffset;
      Rectangle2D rectangle2D = param1Label.getLogicalBounds();
      float f3 = param1Float1;
      float f4 = f3 + (float)rectangle2D.getWidth();
      Area area = null;
      if (this.stdUnderline != null) {
        Shape shape = this.stdUnderline.getUnderlineShape(f1, f3, f4, param1Float2 + f2);
        area = new Area(shape);
      } 
      if (this.strikethrough) {
        BasicStroke basicStroke = new BasicStroke(coreMetrics.strikethroughThickness, 0, 0);
        float f = param1Float2 + coreMetrics.strikethroughOffset;
        Line2D.Float float = new Line2D.Float(f3, f, f4, f);
        Area area1 = new Area(basicStroke.createStrokedShape(float));
        if (area == null) {
          area = area1;
        } else {
          area.add(area1);
        } 
      } 
      if (this.imUnderline != null) {
        Shape shape = this.imUnderline.getUnderlineShape(f1, f3, f4, param1Float2 + f2);
        Area area1 = new Area(shape);
        if (area == null) {
          area = area1;
        } else {
          area.add(area1);
        } 
      } 
      area.add(new Area(param1Label.handleGetOutline(param1Float1, param1Float2)));
      return new GeneralPath(area);
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(super.toString());
      stringBuffer.append("[");
      if (this.fgPaint != null)
        stringBuffer.append("fgPaint: " + this.fgPaint); 
      if (this.bgPaint != null)
        stringBuffer.append(" bgPaint: " + this.bgPaint); 
      if (this.swapColors)
        stringBuffer.append(" swapColors: true"); 
      if (this.strikethrough)
        stringBuffer.append(" strikethrough: true"); 
      if (this.stdUnderline != null)
        stringBuffer.append(" stdUnderline: " + this.stdUnderline); 
      if (this.imUnderline != null)
        stringBuffer.append(" imUnderline: " + this.imUnderline); 
      stringBuffer.append("]");
      return stringBuffer.toString();
    }
  }
  
  public static interface Label {
    CoreMetrics getCoreMetrics();
    
    Rectangle2D getLogicalBounds();
    
    void handleDraw(Graphics2D param1Graphics2D, float param1Float1, float param1Float2);
    
    Rectangle2D handleGetCharVisualBounds(int param1Int);
    
    Rectangle2D handleGetVisualBounds();
    
    Shape handleGetOutline(float param1Float1, float param1Float2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\Decoration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */