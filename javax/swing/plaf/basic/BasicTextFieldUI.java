package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.FieldView;
import javax.swing.text.GlyphView;
import javax.swing.text.JTextComponent;
import javax.swing.text.ParagraphView;
import javax.swing.text.Position;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class BasicTextFieldUI extends BasicTextUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicTextFieldUI(); }
  
  protected String getPropertyPrefix() { return "TextField"; }
  
  public View create(Element paramElement) {
    Document document = paramElement.getDocument();
    Object object = document.getProperty("i18n");
    if (Boolean.TRUE.equals(object)) {
      String str = paramElement.getName();
      if (str != null) {
        if (str.equals("content"))
          return new GlyphView(paramElement); 
        if (str.equals("paragraph"))
          return new I18nFieldView(paramElement); 
      } 
    } 
    return new FieldView(paramElement);
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    View view = getRootView((JTextComponent)paramJComponent);
    if (view.getViewCount() > 0) {
      Insets insets = paramJComponent.getInsets();
      paramInt2 = paramInt2 - insets.top - insets.bottom;
      if (paramInt2 > 0) {
        int i = insets.top;
        View view1 = view.getView(0);
        int j = (int)view1.getPreferredSpan(1);
        if (paramInt2 != j) {
          int k = paramInt2 - j;
          i += k / 2;
        } 
        if (view1 instanceof I18nFieldView) {
          int k = BasicHTML.getBaseline(view1, paramInt1 - insets.left - insets.right, paramInt2);
          if (k < 0)
            return -1; 
          i += k;
        } else {
          FontMetrics fontMetrics = paramJComponent.getFontMetrics(paramJComponent.getFont());
          i += fontMetrics.getAscent();
        } 
        return i;
      } 
    } 
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.CENTER_OFFSET;
  }
  
  static class I18nFieldView extends ParagraphView {
    I18nFieldView(Element param1Element) { super(param1Element); }
    
    public int getFlowSpan(int param1Int) { return Integer.MAX_VALUE; }
    
    protected void setJustification(int param1Int) {}
    
    static boolean isLeftToRight(Component param1Component) { return param1Component.getComponentOrientation().isLeftToRight(); }
    
    Shape adjustAllocation(Shape param1Shape) {
      if (param1Shape != null) {
        Rectangle rectangle = param1Shape.getBounds();
        int i = (int)getPreferredSpan(1);
        int j = (int)getPreferredSpan(0);
        if (rectangle.height != i) {
          int k = rectangle.height - i;
          rectangle.y += k / 2;
          rectangle.height -= k;
        } 
        Container container = getContainer();
        if (container instanceof JTextField) {
          JTextField jTextField = (JTextField)container;
          BoundedRangeModel boundedRangeModel = jTextField.getHorizontalVisibility();
          int k = Math.max(j, rectangle.width);
          int m = boundedRangeModel.getValue();
          int n = Math.min(k, rectangle.width - 1);
          if (m + n > k)
            m = k - n; 
          boundedRangeModel.setRangeProperties(m, n, boundedRangeModel.getMinimum(), k, false);
          if (j < rectangle.width) {
            int i1 = rectangle.width - 1 - j;
            int i2 = ((JTextField)container).getHorizontalAlignment();
            if (isLeftToRight(container)) {
              if (i2 == 10) {
                i2 = 2;
              } else if (i2 == 11) {
                i2 = 4;
              } 
            } else if (i2 == 10) {
              i2 = 4;
            } else if (i2 == 11) {
              i2 = 2;
            } 
            switch (i2) {
              case 0:
                rectangle.x += i1 / 2;
                rectangle.width -= i1;
                break;
              case 4:
                rectangle.x += i1;
                rectangle.width -= i1;
                break;
            } 
          } else {
            rectangle.width = j;
            rectangle.x -= boundedRangeModel.getValue();
          } 
        } 
        return rectangle;
      } 
      return null;
    }
    
    void updateVisibilityModel() {
      Container container = getContainer();
      if (container instanceof JTextField) {
        JTextField jTextField = (JTextField)container;
        BoundedRangeModel boundedRangeModel = jTextField.getHorizontalVisibility();
        int i = (int)getPreferredSpan(0);
        int j = boundedRangeModel.getExtent();
        int k = Math.max(i, j);
        j = (j == 0) ? k : j;
        int m = k - j;
        int n = boundedRangeModel.getValue();
        if (n + j > k)
          n = k - j; 
        m = Math.max(0, Math.min(m, n));
        boundedRangeModel.setRangeProperties(m, j, 0, k, false);
      } 
    }
    
    public void paint(Graphics param1Graphics, Shape param1Shape) {
      Rectangle rectangle = (Rectangle)param1Shape;
      param1Graphics.clipRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      super.paint(param1Graphics, adjustAllocation(param1Shape));
    }
    
    public int getResizeWeight(int param1Int) { return (param1Int == 0) ? 1 : 0; }
    
    public Shape modelToView(int param1Int, Shape param1Shape, Position.Bias param1Bias) throws BadLocationException { return super.modelToView(param1Int, adjustAllocation(param1Shape), param1Bias); }
    
    public Shape modelToView(int param1Int1, Position.Bias param1Bias1, int param1Int2, Position.Bias param1Bias2, Shape param1Shape) throws BadLocationException { return super.modelToView(param1Int1, param1Bias1, param1Int2, param1Bias2, adjustAllocation(param1Shape)); }
    
    public int viewToModel(float param1Float1, float param1Float2, Shape param1Shape, Position.Bias[] param1ArrayOfBias) { return super.viewToModel(param1Float1, param1Float2, adjustAllocation(param1Shape), param1ArrayOfBias); }
    
    public void insertUpdate(DocumentEvent param1DocumentEvent, Shape param1Shape, ViewFactory param1ViewFactory) {
      super.insertUpdate(param1DocumentEvent, adjustAllocation(param1Shape), param1ViewFactory);
      updateVisibilityModel();
    }
    
    public void removeUpdate(DocumentEvent param1DocumentEvent, Shape param1Shape, ViewFactory param1ViewFactory) {
      super.removeUpdate(param1DocumentEvent, adjustAllocation(param1Shape), param1ViewFactory);
      updateVisibilityModel();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicTextFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */