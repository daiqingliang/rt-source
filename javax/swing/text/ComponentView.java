package javax.swing.text;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import javax.swing.SwingUtilities;

public class ComponentView extends View {
  private Component createdC;
  
  private Invalidator c;
  
  public ComponentView(Element paramElement) { super(paramElement); }
  
  protected Component createComponent() {
    AttributeSet attributeSet = getElement().getAttributes();
    return StyleConstants.getComponent(attributeSet);
  }
  
  public final Component getComponent() { return this.createdC; }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    if (this.c != null) {
      Rectangle rectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
      this.c.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } 
  }
  
  public float getPreferredSpan(int paramInt) {
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("Invalid axis: " + paramInt); 
    if (this.c != null) {
      Dimension dimension = this.c.getPreferredSize();
      return (paramInt == 0) ? dimension.width : dimension.height;
    } 
    return 0.0F;
  }
  
  public float getMinimumSpan(int paramInt) {
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("Invalid axis: " + paramInt); 
    if (this.c != null) {
      Dimension dimension = this.c.getMinimumSize();
      return (paramInt == 0) ? dimension.width : dimension.height;
    } 
    return 0.0F;
  }
  
  public float getMaximumSpan(int paramInt) {
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("Invalid axis: " + paramInt); 
    if (this.c != null) {
      Dimension dimension = this.c.getMaximumSize();
      return (paramInt == 0) ? dimension.width : dimension.height;
    } 
    return 0.0F;
  }
  
  public float getAlignment(int paramInt) {
    if (this.c != null)
      switch (paramInt) {
        case 0:
          return this.c.getAlignmentX();
        case 1:
          return this.c.getAlignmentY();
      }  
    return super.getAlignment(paramInt);
  }
  
  public void setParent(View paramView) {
    super.setParent(paramView);
    if (SwingUtilities.isEventDispatchThread()) {
      setComponentParent();
    } else {
      Runnable runnable = new Runnable() {
          public void run() {
            document = ComponentView.this.getDocument();
            try {
              if (document instanceof AbstractDocument)
                ((AbstractDocument)document).readLock(); 
              ComponentView.this.setComponentParent();
              Container container = ComponentView.this.getContainer();
              if (container != null) {
                ComponentView.this.preferenceChanged(null, true, true);
                container.repaint();
              } 
            } finally {
              if (document instanceof AbstractDocument)
                ((AbstractDocument)document).readUnlock(); 
            } 
          }
        };
      SwingUtilities.invokeLater(runnable);
    } 
  }
  
  void setComponentParent() {
    View view = getParent();
    if (view != null) {
      Container container = getContainer();
      if (container != null) {
        if (this.c == null) {
          Component component = createComponent();
          if (component != null) {
            this.createdC = component;
            this.c = new Invalidator(component);
          } 
        } 
        if (this.c != null && this.c.getParent() == null) {
          container.add(this.c, this);
          container.addPropertyChangeListener("enabled", this.c);
        } 
      } 
    } else if (this.c != null) {
      Container container = this.c.getParent();
      if (container != null) {
        container.remove(this.c);
        container.removePropertyChangeListener("enabled", this.c);
      } 
    } 
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias) throws BadLocationException {
    int i = getStartOffset();
    int j = getEndOffset();
    if (paramInt >= i && paramInt <= j) {
      Rectangle rectangle = paramShape.getBounds();
      if (paramInt == j)
        rectangle.x += rectangle.width; 
      rectangle.width = 0;
      return rectangle;
    } 
    throw new BadLocationException(paramInt + " not in range " + i + "," + j, paramInt);
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias) {
    Rectangle rectangle = (Rectangle)paramShape;
    if (paramFloat1 < (rectangle.x + rectangle.width / 2)) {
      paramArrayOfBias[0] = Position.Bias.Forward;
      return getStartOffset();
    } 
    paramArrayOfBias[0] = Position.Bias.Backward;
    return getEndOffset();
  }
  
  class Invalidator extends Container implements PropertyChangeListener {
    Dimension min;
    
    Dimension pref;
    
    Dimension max;
    
    float yalign;
    
    float xalign;
    
    Invalidator(Component param1Component) {
      setLayout(null);
      add(param1Component);
      cacheChildSizes();
    }
    
    public void invalidate() {
      super.invalidate();
      if (getParent() != null)
        ComponentView.this.preferenceChanged(null, true, true); 
    }
    
    public void doLayout() { cacheChildSizes(); }
    
    public void setBounds(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      super.setBounds(param1Int1, param1Int2, param1Int3, param1Int4);
      if (getComponentCount() > 0)
        getComponent(0).setSize(param1Int3, param1Int4); 
      cacheChildSizes();
    }
    
    public void validateIfNecessary() {
      if (!isValid())
        validate(); 
    }
    
    private void cacheChildSizes() {
      if (getComponentCount() > 0) {
        Component component = getComponent(0);
        this.min = component.getMinimumSize();
        this.pref = component.getPreferredSize();
        this.max = component.getMaximumSize();
        this.yalign = component.getAlignmentY();
        this.xalign = component.getAlignmentX();
      } else {
        this.min = this.pref = this.max = new Dimension(0, 0);
      } 
    }
    
    public void setVisible(boolean param1Boolean) {
      super.setVisible(param1Boolean);
      if (getComponentCount() > 0)
        getComponent(0).setVisible(param1Boolean); 
    }
    
    public boolean isShowing() { return true; }
    
    public Dimension getMinimumSize() {
      validateIfNecessary();
      return this.min;
    }
    
    public Dimension getPreferredSize() {
      validateIfNecessary();
      return this.pref;
    }
    
    public Dimension getMaximumSize() {
      validateIfNecessary();
      return this.max;
    }
    
    public float getAlignmentX() {
      validateIfNecessary();
      return this.xalign;
    }
    
    public float getAlignmentY() {
      validateIfNecessary();
      return this.yalign;
    }
    
    public Set<AWTKeyStroke> getFocusTraversalKeys(int param1Int) { return KeyboardFocusManager.getCurrentKeyboardFocusManager().getDefaultFocusTraversalKeys(param1Int); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      Boolean bool = (Boolean)param1PropertyChangeEvent.getNewValue();
      if (getComponentCount() > 0)
        getComponent(0).setEnabled(bool.booleanValue()); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\ComponentView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */