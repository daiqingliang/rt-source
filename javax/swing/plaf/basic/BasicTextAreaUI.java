package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BoxView;
import javax.swing.text.CompositeView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.GlyphView;
import javax.swing.text.JTextComponent;
import javax.swing.text.ParagraphView;
import javax.swing.text.PlainView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;

public class BasicTextAreaUI extends BasicTextUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicTextAreaUI(); }
  
  protected String getPropertyPrefix() { return "TextArea"; }
  
  protected void installDefaults() { super.installDefaults(); }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    super.propertyChange(paramPropertyChangeEvent);
    if (paramPropertyChangeEvent.getPropertyName().equals("lineWrap") || paramPropertyChangeEvent.getPropertyName().equals("wrapStyleWord") || paramPropertyChangeEvent.getPropertyName().equals("tabSize")) {
      modelChanged();
    } else if ("editable".equals(paramPropertyChangeEvent.getPropertyName())) {
      updateFocusTraversalKeys();
    } 
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return super.getPreferredSize(paramJComponent); }
  
  public Dimension getMinimumSize(JComponent paramJComponent) { return super.getMinimumSize(paramJComponent); }
  
  public View create(Element paramElement) {
    Document document = paramElement.getDocument();
    Object object = document.getProperty("i18n");
    if (object != null && object.equals(Boolean.TRUE))
      return createI18N(paramElement); 
    JTextComponent jTextComponent = getComponent();
    if (jTextComponent instanceof JTextArea) {
      PlainView plainView;
      JTextArea jTextArea = (JTextArea)jTextComponent;
      if (jTextArea.getLineWrap()) {
        plainView = new WrappedPlainView(paramElement, jTextArea.getWrapStyleWord());
      } else {
        plainView = new PlainView(paramElement);
      } 
      return plainView;
    } 
    return null;
  }
  
  View createI18N(Element paramElement) {
    String str = paramElement.getName();
    if (str != null) {
      if (str.equals("content"))
        return new PlainParagraph(paramElement); 
      if (str.equals("paragraph"))
        return new BoxView(paramElement, 1); 
    } 
    return null;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    Object object = ((JTextComponent)paramJComponent).getDocument().getProperty("i18n");
    Insets insets = paramJComponent.getInsets();
    if (Boolean.TRUE.equals(object)) {
      View view = getRootView((JTextComponent)paramJComponent);
      if (view.getViewCount() > 0) {
        paramInt2 = paramInt2 - insets.top - insets.bottom;
        int i = insets.top;
        int j = BasicHTML.getBaseline(view.getView(0), paramInt1 - insets.left - insets.right, paramInt2);
        return (j < 0) ? -1 : (i + j);
      } 
      return -1;
    } 
    FontMetrics fontMetrics = paramJComponent.getFontMetrics(paramJComponent.getFont());
    return insets.top + fontMetrics.getAscent();
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
  }
  
  static class PlainParagraph extends ParagraphView {
    PlainParagraph(Element param1Element) {
      super(param1Element);
      this.layoutPool = new LogicalView(param1Element);
      this.layoutPool.setParent(this);
    }
    
    public void setParent(View param1View) {
      super.setParent(param1View);
      if (param1View != null)
        setPropertiesFromAttributes(); 
    }
    
    protected void setPropertiesFromAttributes() {
      Container container = getContainer();
      if (container != null && !container.getComponentOrientation().isLeftToRight()) {
        setJustification(2);
      } else {
        setJustification(0);
      } 
    }
    
    public int getFlowSpan(int param1Int) {
      Container container = getContainer();
      if (container instanceof JTextArea) {
        JTextArea jTextArea = (JTextArea)container;
        if (!jTextArea.getLineWrap())
          return Integer.MAX_VALUE; 
      } 
      return super.getFlowSpan(param1Int);
    }
    
    protected SizeRequirements calculateMinorAxisRequirements(int param1Int, SizeRequirements param1SizeRequirements) {
      SizeRequirements sizeRequirements = super.calculateMinorAxisRequirements(param1Int, param1SizeRequirements);
      Container container = getContainer();
      if (container instanceof JTextArea) {
        JTextArea jTextArea = (JTextArea)container;
        if (!jTextArea.getLineWrap()) {
          sizeRequirements.minimum = sizeRequirements.preferred;
        } else {
          sizeRequirements.minimum = 0;
          sizeRequirements.preferred = getWidth();
          if (sizeRequirements.preferred == Integer.MAX_VALUE)
            sizeRequirements.preferred = 100; 
        } 
      } 
      return sizeRequirements;
    }
    
    public void setSize(float param1Float1, float param1Float2) {
      if ((int)param1Float1 != getWidth())
        preferenceChanged(null, true, true); 
      super.setSize(param1Float1, param1Float2);
    }
    
    static class LogicalView extends CompositeView {
      LogicalView(Element param2Element) { super(param2Element); }
      
      protected int getViewIndexAtPosition(int param2Int) {
        Element element = getElement();
        return (element.getElementCount() > 0) ? element.getElementIndex(param2Int) : 0;
      }
      
      protected boolean updateChildren(DocumentEvent.ElementChange param2ElementChange, DocumentEvent param2DocumentEvent, ViewFactory param2ViewFactory) { return false; }
      
      protected void loadChildren(ViewFactory param2ViewFactory) {
        Element element = getElement();
        if (element.getElementCount() > 0) {
          super.loadChildren(param2ViewFactory);
        } else {
          GlyphView glyphView = new GlyphView(element);
          append(glyphView);
        } 
      }
      
      public float getPreferredSpan(int param2Int) {
        if (getViewCount() != 1)
          throw new Error("One child view is assumed."); 
        View view = getView(0);
        return view.getPreferredSpan(param2Int);
      }
      
      protected void forwardUpdateToView(View param2View, DocumentEvent param2DocumentEvent, Shape param2Shape, ViewFactory param2ViewFactory) {
        param2View.setParent(this);
        super.forwardUpdateToView(param2View, param2DocumentEvent, param2Shape, param2ViewFactory);
      }
      
      public void paint(Graphics param2Graphics, Shape param2Shape) {}
      
      protected boolean isBefore(int param2Int1, int param2Int2, Rectangle param2Rectangle) { return false; }
      
      protected boolean isAfter(int param2Int1, int param2Int2, Rectangle param2Rectangle) { return false; }
      
      protected View getViewAtPoint(int param2Int1, int param2Int2, Rectangle param2Rectangle) { return null; }
      
      protected void childAllocation(int param2Int, Rectangle param2Rectangle) {}
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicTextAreaUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */