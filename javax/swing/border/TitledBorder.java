package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.beans.ConstructorProperties;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class TitledBorder extends AbstractBorder {
  protected String title;
  
  protected Border border;
  
  protected int titlePosition;
  
  protected int titleJustification;
  
  protected Font titleFont;
  
  protected Color titleColor;
  
  private final JLabel label;
  
  public static final int DEFAULT_POSITION = 0;
  
  public static final int ABOVE_TOP = 1;
  
  public static final int TOP = 2;
  
  public static final int BELOW_TOP = 3;
  
  public static final int ABOVE_BOTTOM = 4;
  
  public static final int BOTTOM = 5;
  
  public static final int BELOW_BOTTOM = 6;
  
  public static final int DEFAULT_JUSTIFICATION = 0;
  
  public static final int LEFT = 1;
  
  public static final int CENTER = 2;
  
  public static final int RIGHT = 3;
  
  public static final int LEADING = 4;
  
  public static final int TRAILING = 5;
  
  protected static final int EDGE_SPACING = 2;
  
  protected static final int TEXT_SPACING = 2;
  
  protected static final int TEXT_INSET_H = 5;
  
  public TitledBorder(String paramString) { this(null, paramString, 4, 0, null, null); }
  
  public TitledBorder(Border paramBorder) { this(paramBorder, "", 4, 0, null, null); }
  
  public TitledBorder(Border paramBorder, String paramString) { this(paramBorder, paramString, 4, 0, null, null); }
  
  public TitledBorder(Border paramBorder, String paramString, int paramInt1, int paramInt2) { this(paramBorder, paramString, paramInt1, paramInt2, null, null); }
  
  public TitledBorder(Border paramBorder, String paramString, int paramInt1, int paramInt2, Font paramFont) { this(paramBorder, paramString, paramInt1, paramInt2, paramFont, null); }
  
  @ConstructorProperties({"border", "title", "titleJustification", "titlePosition", "titleFont", "titleColor"})
  public TitledBorder(Border paramBorder, String paramString, int paramInt1, int paramInt2, Font paramFont, Color paramColor) {
    this.title = paramString;
    this.border = paramBorder;
    this.titleFont = paramFont;
    this.titleColor = paramColor;
    setTitleJustification(paramInt1);
    setTitlePosition(paramInt2);
    this.label = new JLabel();
    this.label.setOpaque(false);
    this.label.putClientProperty("html", null);
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Border border1 = getBorder();
    String str = getTitle();
    if (str != null && !str.isEmpty()) {
      int i = (border1 instanceof TitledBorder) ? 0 : 2;
      JLabel jLabel = getLabel(paramComponent);
      Dimension dimension = jLabel.getPreferredSize();
      Insets insets = getBorderInsets(border1, paramComponent, new Insets(0, 0, 0, 0));
      int j = paramInt1 + i;
      int k = paramInt2 + i;
      int m = paramInt3 - i - i;
      int n = paramInt4 - i - i;
      int i1 = paramInt2;
      int i2 = dimension.height;
      int i3 = getPosition();
      switch (i3) {
        case 1:
          insets.left = 0;
          insets.right = 0;
          k += i2 - i;
          n -= i2 - i;
          break;
        case 2:
          insets.top = i + insets.top / 2 - i2 / 2;
          if (insets.top < i) {
            k -= insets.top;
            n += insets.top;
            break;
          } 
          i1 += insets.top;
          break;
        case 3:
          i1 += insets.top + i;
          break;
        case 4:
          i1 += paramInt4 - i2 - insets.bottom - i;
          break;
        case 5:
          i1 += paramInt4 - i2;
          insets.bottom = i + (insets.bottom - i2) / 2;
          if (insets.bottom < i) {
            n += insets.bottom;
            break;
          } 
          i1 -= insets.bottom;
          break;
        case 6:
          insets.left = 0;
          insets.right = 0;
          i1 += paramInt4 - i2;
          n -= i2 - i;
          break;
      } 
      insets.left += i + 5;
      insets.right += i + 5;
      int i4 = paramInt1;
      int i5 = paramInt3 - insets.left - insets.right;
      if (i5 > dimension.width)
        i5 = dimension.width; 
      switch (getJustification(paramComponent)) {
        case 1:
          i4 += insets.left;
          break;
        case 3:
          i4 += paramInt3 - insets.right - i5;
          break;
        case 2:
          i4 += (paramInt3 - i5) / 2;
          break;
      } 
      if (border1 != null)
        if (i3 != 2 && i3 != 5) {
          border1.paintBorder(paramComponent, paramGraphics, j, k, m, n);
        } else {
          Graphics graphics = paramGraphics.create();
          if (graphics instanceof Graphics2D) {
            Graphics2D graphics2D = (Graphics2D)graphics;
            Path2D.Float float = new Path2D.Float();
            float.append(new Rectangle(j, k, m, i1 - k), false);
            float.append(new Rectangle(j, i1, i4 - j - 2, i2), false);
            float.append(new Rectangle(i4 + i5 + 2, i1, j - i4 + m - i5 - 2, i2), false);
            float.append(new Rectangle(j, i1 + i2, m, k - i1 + n - i2), false);
            graphics2D.clip(float);
          } 
          border1.paintBorder(paramComponent, graphics, j, k, m, n);
          graphics.dispose();
        }  
      paramGraphics.translate(i4, i1);
      jLabel.setSize(i5, i2);
      jLabel.paint(paramGraphics);
      paramGraphics.translate(-i4, -i1);
    } else if (border1 != null) {
      border1.paintBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    } 
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets) {
    Border border1 = getBorder();
    paramInsets = getBorderInsets(border1, paramComponent, paramInsets);
    String str = getTitle();
    if (str != null && !str.isEmpty()) {
      int i = (border1 instanceof TitledBorder) ? 0 : 2;
      JLabel jLabel = getLabel(paramComponent);
      Dimension dimension = jLabel.getPreferredSize();
      switch (getPosition()) {
        case 1:
          paramInsets.top += dimension.height - i;
          break;
        case 2:
          if (paramInsets.top < dimension.height)
            paramInsets.top = dimension.height - i; 
          break;
        case 3:
          paramInsets.top += dimension.height;
          break;
        case 4:
          paramInsets.bottom += dimension.height;
          break;
        case 5:
          if (paramInsets.bottom < dimension.height)
            paramInsets.bottom = dimension.height - i; 
          break;
        case 6:
          paramInsets.bottom += dimension.height - i;
          break;
      } 
      paramInsets.top += i + 2;
      paramInsets.left += i + 2;
      paramInsets.right += i + 2;
      paramInsets.bottom += i + 2;
    } 
    return paramInsets;
  }
  
  public boolean isBorderOpaque() { return false; }
  
  public String getTitle() { return this.title; }
  
  public Border getBorder() { return (this.border != null) ? this.border : UIManager.getBorder("TitledBorder.border"); }
  
  public int getTitlePosition() { return this.titlePosition; }
  
  public int getTitleJustification() { return this.titleJustification; }
  
  public Font getTitleFont() { return (this.titleFont == null) ? UIManager.getFont("TitledBorder.font") : this.titleFont; }
  
  public Color getTitleColor() { return (this.titleColor == null) ? UIManager.getColor("TitledBorder.titleColor") : this.titleColor; }
  
  public void setTitle(String paramString) { this.title = paramString; }
  
  public void setBorder(Border paramBorder) { this.border = paramBorder; }
  
  public void setTitlePosition(int paramInt) {
    switch (paramInt) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
        this.titlePosition = paramInt;
        return;
    } 
    throw new IllegalArgumentException(paramInt + " is not a valid title position.");
  }
  
  public void setTitleJustification(int paramInt) {
    switch (paramInt) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
        this.titleJustification = paramInt;
        return;
    } 
    throw new IllegalArgumentException(paramInt + " is not a valid title justification.");
  }
  
  public void setTitleFont(Font paramFont) { this.titleFont = paramFont; }
  
  public void setTitleColor(Color paramColor) { this.titleColor = paramColor; }
  
  public Dimension getMinimumSize(Component paramComponent) {
    Insets insets = getBorderInsets(paramComponent);
    Dimension dimension = new Dimension(insets.right + insets.left, insets.top + insets.bottom);
    String str = getTitle();
    if (str != null && !str.isEmpty()) {
      JLabel jLabel = getLabel(paramComponent);
      Dimension dimension1 = jLabel.getPreferredSize();
      int i = getPosition();
      if (i != 1 && i != 6) {
        dimension.width += dimension1.width;
      } else if (dimension.width < dimension1.width) {
        dimension.width += dimension1.width;
      } 
    } 
    return dimension;
  }
  
  public int getBaseline(Component paramComponent, int paramInt1, int paramInt2) {
    if (paramComponent == null)
      throw new NullPointerException("Must supply non-null component"); 
    if (paramInt1 < 0)
      throw new IllegalArgumentException("Width must be >= 0"); 
    if (paramInt2 < 0)
      throw new IllegalArgumentException("Height must be >= 0"); 
    Border border1 = getBorder();
    String str = getTitle();
    if (str != null && !str.isEmpty()) {
      int i = (border1 instanceof TitledBorder) ? 0 : 2;
      JLabel jLabel = getLabel(paramComponent);
      Dimension dimension = jLabel.getPreferredSize();
      Insets insets = getBorderInsets(border1, paramComponent, new Insets(0, 0, 0, 0));
      int j = jLabel.getBaseline(dimension.width, dimension.height);
      switch (getPosition()) {
        case 1:
          return j;
        case 2:
          insets.top = i + (insets.top - dimension.height) / 2;
          return (insets.top < i) ? j : (j + insets.top);
        case 3:
          return j + insets.top + i;
        case 4:
          return j + paramInt2 - dimension.height - insets.bottom - i;
        case 5:
          insets.bottom = i + (insets.bottom - dimension.height) / 2;
          return (insets.bottom < i) ? (j + paramInt2 - dimension.height) : (j + paramInt2 - dimension.height + insets.bottom);
        case 6:
          return j + paramInt2 - dimension.height;
      } 
    } 
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(Component paramComponent) {
    super.getBaselineResizeBehavior(paramComponent);
    switch (getPosition()) {
      case 1:
      case 2:
      case 3:
        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
      case 4:
      case 5:
      case 6:
        return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
    } 
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  private int getPosition() {
    int i = getTitlePosition();
    if (i != 0)
      return i; 
    Object object = UIManager.get("TitledBorder.position");
    if (object instanceof Integer) {
      int j = ((Integer)object).intValue();
      if (0 < j && j <= 6)
        return j; 
    } else if (object instanceof String) {
      String str = (String)object;
      if (str.equalsIgnoreCase("ABOVE_TOP"))
        return 1; 
      if (str.equalsIgnoreCase("TOP"))
        return 2; 
      if (str.equalsIgnoreCase("BELOW_TOP"))
        return 3; 
      if (str.equalsIgnoreCase("ABOVE_BOTTOM"))
        return 4; 
      if (str.equalsIgnoreCase("BOTTOM"))
        return 5; 
      if (str.equalsIgnoreCase("BELOW_BOTTOM"))
        return 6; 
    } 
    return 2;
  }
  
  private int getJustification(Component paramComponent) {
    int i = getTitleJustification();
    return (i == 4 || i == 0) ? (paramComponent.getComponentOrientation().isLeftToRight() ? 1 : 3) : ((i == 5) ? (paramComponent.getComponentOrientation().isLeftToRight() ? 3 : 1) : i);
  }
  
  protected Font getFont(Component paramComponent) {
    Font font = getTitleFont();
    if (font != null)
      return font; 
    if (paramComponent != null) {
      font = paramComponent.getFont();
      if (font != null)
        return font; 
    } 
    return new Font("Dialog", 0, 12);
  }
  
  private Color getColor(Component paramComponent) {
    Color color = getTitleColor();
    return (color != null) ? color : ((paramComponent != null) ? paramComponent.getForeground() : null);
  }
  
  private JLabel getLabel(Component paramComponent) {
    this.label.setText(getTitle());
    this.label.setFont(getFont(paramComponent));
    this.label.setForeground(getColor(paramComponent));
    this.label.setComponentOrientation(paramComponent.getComponentOrientation());
    this.label.setEnabled(paramComponent.isEnabled());
    return this.label;
  }
  
  private static Insets getBorderInsets(Border paramBorder, Component paramComponent, Insets paramInsets) {
    if (paramBorder == null) {
      paramInsets.set(0, 0, 0, 0);
    } else if (paramBorder instanceof AbstractBorder) {
      AbstractBorder abstractBorder = (AbstractBorder)paramBorder;
      paramInsets = abstractBorder.getBorderInsets(paramComponent, paramInsets);
    } else {
      Insets insets = paramBorder.getBorderInsets(paramComponent);
      paramInsets.set(insets.top, insets.left, insets.bottom, insets.right);
    } 
    return paramInsets;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\border\TitledBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */