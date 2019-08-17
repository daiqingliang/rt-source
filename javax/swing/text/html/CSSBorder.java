package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.AbstractBorder;
import javax.swing.text.AttributeSet;

class CSSBorder extends AbstractBorder {
  static final int COLOR = 0;
  
  static final int STYLE = 1;
  
  static final int WIDTH = 2;
  
  static final int TOP = 0;
  
  static final int RIGHT = 1;
  
  static final int BOTTOM = 2;
  
  static final int LEFT = 3;
  
  static final CSS.Attribute[][] ATTRIBUTES = { { CSS.Attribute.BORDER_TOP_COLOR, CSS.Attribute.BORDER_RIGHT_COLOR, CSS.Attribute.BORDER_BOTTOM_COLOR, CSS.Attribute.BORDER_LEFT_COLOR }, { CSS.Attribute.BORDER_TOP_STYLE, CSS.Attribute.BORDER_RIGHT_STYLE, CSS.Attribute.BORDER_BOTTOM_STYLE, CSS.Attribute.BORDER_LEFT_STYLE }, { CSS.Attribute.BORDER_TOP_WIDTH, CSS.Attribute.BORDER_RIGHT_WIDTH, CSS.Attribute.BORDER_BOTTOM_WIDTH, CSS.Attribute.BORDER_LEFT_WIDTH } };
  
  static final CSS.CssValue[] PARSERS = { new CSS.ColorValue(), new CSS.BorderStyle(), new CSS.BorderWidthValue(null, 0) };
  
  static final Object[] DEFAULTS = { CSS.Attribute.BORDER_COLOR, PARSERS[1].parseCssValue(CSS.Attribute.BORDER_STYLE.getDefaultValue()), PARSERS[2].parseCssValue(CSS.Attribute.BORDER_WIDTH.getDefaultValue()) };
  
  final AttributeSet attrs;
  
  static Map<CSS.Value, BorderPainter> borderPainters = new HashMap();
  
  CSSBorder(AttributeSet paramAttributeSet) { this.attrs = paramAttributeSet; }
  
  private Color getBorderColor(int paramInt) {
    CSS.ColorValue colorValue;
    Object object = this.attrs.getAttribute(ATTRIBUTES[0][paramInt]);
    if (object instanceof CSS.ColorValue) {
      colorValue = (CSS.ColorValue)object;
    } else {
      colorValue = (CSS.ColorValue)this.attrs.getAttribute(CSS.Attribute.COLOR);
      if (colorValue == null)
        colorValue = (CSS.ColorValue)PARSERS[0].parseCssValue(CSS.Attribute.COLOR.getDefaultValue()); 
    } 
    return colorValue.getValue();
  }
  
  private int getBorderWidth(int paramInt) {
    int i = 0;
    CSS.BorderStyle borderStyle = (CSS.BorderStyle)this.attrs.getAttribute(ATTRIBUTES[1][paramInt]);
    if (borderStyle != null && borderStyle.getValue() != CSS.Value.NONE) {
      CSS.LengthValue lengthValue = (CSS.LengthValue)this.attrs.getAttribute(ATTRIBUTES[2][paramInt]);
      if (lengthValue == null)
        lengthValue = (CSS.LengthValue)DEFAULTS[2]; 
      i = (int)lengthValue.getValue(true);
    } 
    return i;
  }
  
  private int[] getWidths() {
    int[] arrayOfInt = new int[4];
    for (byte b = 0; b < arrayOfInt.length; b++)
      arrayOfInt[b] = getBorderWidth(b); 
    return arrayOfInt;
  }
  
  private CSS.Value getBorderStyle(int paramInt) {
    CSS.BorderStyle borderStyle = (CSS.BorderStyle)this.attrs.getAttribute(ATTRIBUTES[1][paramInt]);
    if (borderStyle == null)
      borderStyle = (CSS.BorderStyle)DEFAULTS[1]; 
    return borderStyle.getValue();
  }
  
  private Polygon getBorderShape(int paramInt) {
    Polygon polygon = null;
    int[] arrayOfInt = getWidths();
    if (arrayOfInt[paramInt] != 0) {
      polygon = new Polygon(new int[4], new int[4], 0);
      polygon.addPoint(0, 0);
      polygon.addPoint(-arrayOfInt[(paramInt + 3) % 4], -arrayOfInt[paramInt]);
      polygon.addPoint(arrayOfInt[(paramInt + 1) % 4], -arrayOfInt[paramInt]);
      polygon.addPoint(0, 0);
    } 
    return polygon;
  }
  
  private BorderPainter getBorderPainter(int paramInt) {
    CSS.Value value = getBorderStyle(paramInt);
    return (BorderPainter)borderPainters.get(value);
  }
  
  static Color getAdjustedColor(Color paramColor, double paramDouble) {
    double d1 = 1.0D - Math.min(Math.abs(paramDouble), 1.0D);
    double d2 = (paramDouble > 0.0D) ? (255.0D * (1.0D - d1)) : 0.0D;
    return new Color((int)(paramColor.getRed() * d1 + d2), (int)(paramColor.getGreen() * d1 + d2), (int)(paramColor.getBlue() * d1 + d2));
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
    int[] arrayOfInt = getWidths();
    paramInsets.set(arrayOfInt[0], arrayOfInt[3], arrayOfInt[2], arrayOfInt[1]);
    return paramInsets;
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (!(paramGraphics instanceof Graphics2D))
      return; 
    Graphics2D graphics2D = (Graphics2D)paramGraphics.create();
    int[] arrayOfInt = getWidths();
    int i = paramInt1 + arrayOfInt[3];
    int j = paramInt2 + arrayOfInt[0];
    int k = paramInt3 - arrayOfInt[1] + arrayOfInt[3];
    int m = paramInt4 - arrayOfInt[0] + arrayOfInt[2];
    int[][] arrayOfInt1 = { { i, j }, { i + k, j }, { i + k, j + m }, { i, j + m } };
    for (byte b = 0; b < 4; b++) {
      CSS.Value value = getBorderStyle(b);
      Polygon polygon = getBorderShape(b);
      if (value != CSS.Value.NONE && polygon != null) {
        int n = (b % 2 == 0) ? k : m;
        polygon.xpoints[2] = polygon.xpoints[2] + n;
        polygon.xpoints[3] = polygon.xpoints[3] + n;
        Color color = getBorderColor(b);
        BorderPainter borderPainter = getBorderPainter(b);
        double d = b * Math.PI / 2.0D;
        graphics2D.setClip(paramGraphics.getClip());
        graphics2D.translate(arrayOfInt1[b][0], arrayOfInt1[b][1]);
        graphics2D.rotate(d);
        graphics2D.clip(polygon);
        borderPainter.paint(polygon, graphics2D, color, b);
        graphics2D.rotate(-d);
        graphics2D.translate(-arrayOfInt1[b][0], -arrayOfInt1[b][1]);
      } 
    } 
    graphics2D.dispose();
  }
  
  static void registerBorderPainter(CSS.Value paramValue, BorderPainter paramBorderPainter) { borderPainters.put(paramValue, paramBorderPainter); }
  
  static  {
    registerBorderPainter(CSS.Value.NONE, new NullPainter());
    registerBorderPainter(CSS.Value.HIDDEN, new NullPainter());
    registerBorderPainter(CSS.Value.SOLID, new SolidPainter());
    registerBorderPainter(CSS.Value.DOUBLE, new DoublePainter());
    registerBorderPainter(CSS.Value.DOTTED, new DottedDashedPainter(1));
    registerBorderPainter(CSS.Value.DASHED, new DottedDashedPainter(3));
    registerBorderPainter(CSS.Value.GROOVE, new GrooveRidgePainter(CSS.Value.GROOVE));
    registerBorderPainter(CSS.Value.RIDGE, new GrooveRidgePainter(CSS.Value.RIDGE));
    registerBorderPainter(CSS.Value.INSET, new InsetOutsetPainter(CSS.Value.INSET));
    registerBorderPainter(CSS.Value.OUTSET, new InsetOutsetPainter(CSS.Value.OUTSET));
  }
  
  static interface BorderPainter {
    void paint(Polygon param1Polygon, Graphics param1Graphics, Color param1Color, int param1Int);
  }
  
  static class DottedDashedPainter extends StrokePainter {
    final int factor;
    
    DottedDashedPainter(int param1Int) { this.factor = param1Int; }
    
    public void paint(Polygon param1Polygon, Graphics param1Graphics, Color param1Color, int param1Int) {
      Rectangle rectangle = param1Polygon.getBounds();
      int i = rectangle.height * this.factor;
      int[] arrayOfInt = { i, i };
      Color[] arrayOfColor = { param1Color, null };
      paintStrokes(rectangle, param1Graphics, 0, arrayOfInt, arrayOfColor);
    }
  }
  
  static class DoublePainter extends StrokePainter {
    public void paint(Polygon param1Polygon, Graphics param1Graphics, Color param1Color, int param1Int) {
      Rectangle rectangle = param1Polygon.getBounds();
      int i = Math.max(rectangle.height / 3, 1);
      int[] arrayOfInt = { i, i };
      Color[] arrayOfColor = { param1Color, null };
      paintStrokes(rectangle, param1Graphics, 1, arrayOfInt, arrayOfColor);
    }
  }
  
  static class GrooveRidgePainter extends ShadowLightPainter {
    final CSS.Value type;
    
    GrooveRidgePainter(CSS.Value param1Value) { this.type = param1Value; }
    
    public void paint(Polygon param1Polygon, Graphics param1Graphics, Color param1Color, int param1Int) {
      Rectangle rectangle = param1Polygon.getBounds();
      int i = Math.max(rectangle.height / 2, 1);
      int[] arrayOfInt = { i, i };
      new Color[2][0] = getShadowColor(param1Color);
      new Color[2][1] = getLightColor(param1Color);
      new Color[2][0] = getLightColor(param1Color);
      new Color[2][1] = getShadowColor(param1Color);
      Color[] arrayOfColor = ((((param1Int + 1) % 4 < 2) ? 1 : 0) == ((this.type == CSS.Value.GROOVE) ? 1 : 0)) ? new Color[2] : new Color[2];
      paintStrokes(rectangle, param1Graphics, 1, arrayOfInt, arrayOfColor);
    }
  }
  
  static class InsetOutsetPainter extends ShadowLightPainter {
    CSS.Value type;
    
    InsetOutsetPainter(CSS.Value param1Value) { this.type = param1Value; }
    
    public void paint(Polygon param1Polygon, Graphics param1Graphics, Color param1Color, int param1Int) {
      param1Graphics.setColor(((((param1Int + 1) % 4 < 2) ? 1 : 0) == ((this.type == CSS.Value.INSET) ? 1 : 0)) ? getShadowColor(param1Color) : getLightColor(param1Color));
      param1Graphics.fillPolygon(param1Polygon);
    }
  }
  
  static class NullPainter implements BorderPainter {
    public void paint(Polygon param1Polygon, Graphics param1Graphics, Color param1Color, int param1Int) {}
  }
  
  static abstract class ShadowLightPainter extends StrokePainter {
    static Color getShadowColor(Color param1Color) { return CSSBorder.getAdjustedColor(param1Color, -0.3D); }
    
    static Color getLightColor(Color param1Color) { return CSSBorder.getAdjustedColor(param1Color, 0.7D); }
  }
  
  static class SolidPainter implements BorderPainter {
    public void paint(Polygon param1Polygon, Graphics param1Graphics, Color param1Color, int param1Int) {
      param1Graphics.setColor(param1Color);
      param1Graphics.fillPolygon(param1Polygon);
    }
  }
  
  static abstract class StrokePainter implements BorderPainter {
    void paintStrokes(Rectangle param1Rectangle, Graphics param1Graphics, int param1Int, int[] param1ArrayOfInt, Color[] param1ArrayOfColor) {
      boolean bool = (param1Int == 0) ? 1 : 0;
      int i = 0;
      int j = bool ? param1Rectangle.width : param1Rectangle.height;
      while (i < j) {
        for (byte b = 0; b < param1ArrayOfInt.length && i < j; b++) {
          int k = param1ArrayOfInt[b];
          Color color = param1ArrayOfColor[b];
          if (color != null) {
            int m = param1Rectangle.x + (bool ? i : 0);
            int n = param1Rectangle.y + (bool ? 0 : i);
            int i1 = bool ? k : param1Rectangle.width;
            int i2 = bool ? param1Rectangle.height : k;
            param1Graphics.setColor(color);
            param1Graphics.fillRect(m, n, i1, i2);
          } 
          i += k;
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\CSSBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */