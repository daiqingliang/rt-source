package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.SunToolkit;

public class JLayeredPane extends JComponent implements Accessible {
  public static final Integer DEFAULT_LAYER = new Integer(0);
  
  public static final Integer PALETTE_LAYER = new Integer(100);
  
  public static final Integer MODAL_LAYER = new Integer(200);
  
  public static final Integer POPUP_LAYER = new Integer(300);
  
  public static final Integer DRAG_LAYER = new Integer(400);
  
  public static final Integer FRAME_CONTENT_LAYER = new Integer(-30000);
  
  public static final String LAYER_PROPERTY = "layeredContainerLayer";
  
  private Hashtable<Component, Integer> componentToLayer;
  
  private boolean optimizedDrawingPossible = true;
  
  public JLayeredPane() { setLayout(null); }
  
  private void validateOptimizedDrawing() {
    boolean bool = false;
    synchronized (getTreeLock()) {
      Component[] arrayOfComponent = getComponents();
      int i = arrayOfComponent.length;
      byte b = 0;
      while (b < i) {
        Component component = arrayOfComponent[b];
        Integer integer = null;
        if ((!SunToolkit.isInstanceOf(component, "javax.swing.JInternalFrame") && (!(component instanceof JComponent) || (integer = (Integer)((JComponent)component).getClientProperty("layeredContainerLayer")) == null)) || (integer != null && integer.equals(FRAME_CONTENT_LAYER))) {
          b++;
          continue;
        } 
        bool = true;
      } 
    } 
    if (bool) {
      this.optimizedDrawingPossible = false;
    } else {
      this.optimizedDrawingPossible = true;
    } 
  }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt) {
    int i;
    if (paramObject instanceof Integer) {
      i = ((Integer)paramObject).intValue();
      setLayer(paramComponent, i);
    } else {
      i = getLayer(paramComponent);
    } 
    int j = insertIndexForLayer(i, paramInt);
    super.addImpl(paramComponent, paramObject, j);
    paramComponent.validate();
    paramComponent.repaint();
    validateOptimizedDrawing();
  }
  
  public void remove(int paramInt) {
    Component component = getComponent(paramInt);
    super.remove(paramInt);
    if (component != null && !(component instanceof JComponent))
      getComponentToLayer().remove(component); 
    validateOptimizedDrawing();
  }
  
  public void removeAll() {
    Component[] arrayOfComponent = getComponents();
    Hashtable hashtable = getComponentToLayer();
    for (int i = arrayOfComponent.length - 1; i >= 0; i--) {
      Component component = arrayOfComponent[i];
      if (component != null && !(component instanceof JComponent))
        hashtable.remove(component); 
    } 
    super.removeAll();
  }
  
  public boolean isOptimizedDrawingEnabled() { return this.optimizedDrawingPossible; }
  
  public static void putLayer(JComponent paramJComponent, int paramInt) {
    Integer integer = new Integer(paramInt);
    paramJComponent.putClientProperty("layeredContainerLayer", integer);
  }
  
  public static int getLayer(JComponent paramJComponent) {
    Integer integer;
    return ((integer = (Integer)paramJComponent.getClientProperty("layeredContainerLayer")) != null) ? integer.intValue() : DEFAULT_LAYER.intValue();
  }
  
  public static JLayeredPane getLayeredPaneAbove(Component paramComponent) {
    if (paramComponent == null)
      return null; 
    Container container;
    for (container = paramComponent.getParent(); container != null && !(container instanceof JLayeredPane); container = container.getParent());
    return (JLayeredPane)container;
  }
  
  public void setLayer(Component paramComponent, int paramInt) { setLayer(paramComponent, paramInt, -1); }
  
  public void setLayer(Component paramComponent, int paramInt1, int paramInt2) {
    Integer integer = getObjectForLayer(paramInt1);
    if (paramInt1 == getLayer(paramComponent) && paramInt2 == getPosition(paramComponent)) {
      repaint(paramComponent.getBounds());
      return;
    } 
    if (paramComponent instanceof JComponent) {
      ((JComponent)paramComponent).putClientProperty("layeredContainerLayer", integer);
    } else {
      getComponentToLayer().put(paramComponent, integer);
    } 
    if (paramComponent.getParent() == null || paramComponent.getParent() != this) {
      repaint(paramComponent.getBounds());
      return;
    } 
    int i = insertIndexForLayer(paramComponent, paramInt1, paramInt2);
    setComponentZOrder(paramComponent, i);
    repaint(paramComponent.getBounds());
  }
  
  public int getLayer(Component paramComponent) {
    Integer integer;
    if (paramComponent instanceof JComponent) {
      integer = (Integer)((JComponent)paramComponent).getClientProperty("layeredContainerLayer");
    } else {
      integer = (Integer)getComponentToLayer().get(paramComponent);
    } 
    return (integer == null) ? DEFAULT_LAYER.intValue() : integer.intValue();
  }
  
  public int getIndexOf(Component paramComponent) {
    int i = getComponentCount();
    for (byte b = 0; b < i; b++) {
      if (paramComponent == getComponent(b))
        return b; 
    } 
    return -1;
  }
  
  public void moveToFront(Component paramComponent) { setPosition(paramComponent, 0); }
  
  public void moveToBack(Component paramComponent) { setPosition(paramComponent, -1); }
  
  public void setPosition(Component paramComponent, int paramInt) { setLayer(paramComponent, getLayer(paramComponent), paramInt); }
  
  public int getPosition(Component paramComponent) {
    byte b = 0;
    getComponentCount();
    int k = getIndexOf(paramComponent);
    if (k == -1)
      return -1; 
    int j = getLayer(paramComponent);
    for (int i = k - 1; i >= 0; i--) {
      int m = getLayer(getComponent(i));
      if (m == j) {
        b++;
      } else {
        return b;
      } 
    } 
    return b;
  }
  
  public int highestLayer() { return (getComponentCount() > 0) ? getLayer(getComponent(0)) : 0; }
  
  public int lowestLayer() {
    int i = getComponentCount();
    return (i > 0) ? getLayer(getComponent(i - 1)) : 0;
  }
  
  public int getComponentCountInLayer(int paramInt) {
    byte b2 = 0;
    int i = getComponentCount();
    for (byte b1 = 0; b1 < i; b1++) {
      int j = getLayer(getComponent(b1));
      if (j == paramInt) {
        b2++;
      } else if (b2 > 0 || j < paramInt) {
        break;
      } 
    } 
    return b2;
  }
  
  public Component[] getComponentsInLayer(int paramInt) {
    byte b2 = 0;
    Component[] arrayOfComponent = new Component[getComponentCountInLayer(paramInt)];
    int i = getComponentCount();
    for (byte b1 = 0; b1 < i; b1++) {
      int j = getLayer(getComponent(b1));
      if (j == paramInt) {
        arrayOfComponent[b2++] = getComponent(b1);
      } else if (b2 > 0 || j < paramInt) {
        break;
      } 
    } 
    return arrayOfComponent;
  }
  
  public void paint(Graphics paramGraphics) {
    if (isOpaque()) {
      Rectangle rectangle = paramGraphics.getClipBounds();
      Color color = getBackground();
      if (color == null)
        color = Color.lightGray; 
      paramGraphics.setColor(color);
      if (rectangle != null) {
        paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } else {
        paramGraphics.fillRect(0, 0, getWidth(), getHeight());
      } 
    } 
    super.paint(paramGraphics);
  }
  
  protected Hashtable<Component, Integer> getComponentToLayer() {
    if (this.componentToLayer == null)
      this.componentToLayer = new Hashtable(4); 
    return this.componentToLayer;
  }
  
  protected Integer getObjectForLayer(int paramInt) {
    switch (paramInt) {
      case 0:
        return DEFAULT_LAYER;
      case 100:
        return PALETTE_LAYER;
      case 200:
        return MODAL_LAYER;
      case 300:
        return POPUP_LAYER;
      case 400:
        return DRAG_LAYER;
    } 
    return new Integer(paramInt);
  }
  
  protected int insertIndexForLayer(int paramInt1, int paramInt2) { return insertIndexForLayer(null, paramInt1, paramInt2); }
  
  private int insertIndexForLayer(Component paramComponent, int paramInt1, int paramInt2) {
    int j = -1;
    int k = -1;
    int m = getComponentCount();
    ArrayList arrayList = new ArrayList(m);
    for (byte b1 = 0; b1 < m; b1++) {
      if (getComponent(b1) != paramComponent)
        arrayList.add(getComponent(b1)); 
    } 
    int i = arrayList.size();
    for (byte b = 0; b < i; b++) {
      int n = getLayer((Component)arrayList.get(b));
      if (j == -1 && n == paramInt1)
        j = b; 
      if (n < paramInt1) {
        if (b == 0) {
          j = 0;
          k = 0;
          break;
        } 
        k = b;
        break;
      } 
    } 
    if (j == -1 && k == -1)
      return i; 
    if (j != -1 && k == -1)
      k = i; 
    if (k != -1 && j == -1)
      j = k; 
    return (paramInt2 == -1) ? k : ((paramInt2 > -1 && j + paramInt2 <= k) ? (j + paramInt2) : k);
  }
  
  protected String paramString() {
    String str = this.optimizedDrawingPossible ? "true" : "false";
    return super.paramString() + ",optimizedDrawingPossible=" + str;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJLayeredPane(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJLayeredPane extends JComponent.AccessibleJComponent {
    protected AccessibleJLayeredPane() { super(JLayeredPane.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.LAYERED_PANE; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JLayeredPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */