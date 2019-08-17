package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.DesktopPaneUI;

public class JDesktopPane extends JLayeredPane implements Accessible {
  private static final String uiClassID = "DesktopPaneUI";
  
  DesktopManager desktopManager;
  
  private JInternalFrame selectedFrame = null;
  
  public static final int LIVE_DRAG_MODE = 0;
  
  public static final int OUTLINE_DRAG_MODE = 1;
  
  private int dragMode = 0;
  
  private boolean dragModeSet = false;
  
  private List<JInternalFrame> framesCache;
  
  private boolean componentOrderCheckingEnabled = true;
  
  private boolean componentOrderChanged = false;
  
  public JDesktopPane() {
    setUIProperty("opaque", Boolean.TRUE);
    setFocusCycleRoot(true);
    setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
          public Component getDefaultComponent(Container param1Container) {
            JInternalFrame[] arrayOfJInternalFrame = JDesktopPane.this.getAllFrames();
            Component component = null;
            for (JInternalFrame jInternalFrame : arrayOfJInternalFrame) {
              component = jInternalFrame.getFocusTraversalPolicy().getDefaultComponent(jInternalFrame);
              if (component != null)
                break; 
            } 
            return component;
          }
        });
    updateUI();
  }
  
  public DesktopPaneUI getUI() { return (DesktopPaneUI)this.ui; }
  
  public void setUI(DesktopPaneUI paramDesktopPaneUI) { setUI(paramDesktopPaneUI); }
  
  public void setDragMode(int paramInt) {
    int i = this.dragMode;
    this.dragMode = paramInt;
    firePropertyChange("dragMode", i, this.dragMode);
    this.dragModeSet = true;
  }
  
  public int getDragMode() { return this.dragMode; }
  
  public DesktopManager getDesktopManager() { return this.desktopManager; }
  
  public void setDesktopManager(DesktopManager paramDesktopManager) {
    DesktopManager desktopManager1 = this.desktopManager;
    this.desktopManager = paramDesktopManager;
    firePropertyChange("desktopManager", desktopManager1, this.desktopManager);
  }
  
  public void updateUI() { setUI((DesktopPaneUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "DesktopPaneUI"; }
  
  public JInternalFrame[] getAllFrames() { return (JInternalFrame[])getAllFrames(this).toArray(new JInternalFrame[0]); }
  
  private static Collection<JInternalFrame> getAllFrames(Container paramContainer) {
    LinkedHashSet linkedHashSet = new LinkedHashSet();
    int i = paramContainer.getComponentCount();
    for (byte b = 0; b < i; b++) {
      Component component = paramContainer.getComponent(b);
      if (component instanceof JInternalFrame) {
        linkedHashSet.add((JInternalFrame)component);
      } else if (component instanceof JInternalFrame.JDesktopIcon) {
        JInternalFrame jInternalFrame = ((JInternalFrame.JDesktopIcon)component).getInternalFrame();
        if (jInternalFrame != null)
          linkedHashSet.add(jInternalFrame); 
      } else if (component instanceof Container) {
        linkedHashSet.addAll(getAllFrames((Container)component));
      } 
    } 
    return linkedHashSet;
  }
  
  public JInternalFrame getSelectedFrame() { return this.selectedFrame; }
  
  public void setSelectedFrame(JInternalFrame paramJInternalFrame) { this.selectedFrame = paramJInternalFrame; }
  
  public JInternalFrame[] getAllFramesInLayer(int paramInt) {
    Collection collection = getAllFrames(this);
    Iterator iterator = collection.iterator();
    while (iterator.hasNext()) {
      if (((JInternalFrame)iterator.next()).getLayer() != paramInt)
        iterator.remove(); 
    } 
    return (JInternalFrame[])collection.toArray(new JInternalFrame[0]);
  }
  
  private List<JInternalFrame> getFrames() {
    TreeSet treeSet = new TreeSet();
    for (byte b = 0; b < getComponentCount(); b++) {
      Component component = getComponent(b);
      if (component instanceof JInternalFrame) {
        treeSet.add(new ComponentPosition((JInternalFrame)component, getLayer(component), b));
      } else if (component instanceof JInternalFrame.JDesktopIcon) {
        component = ((JInternalFrame.JDesktopIcon)component).getInternalFrame();
        treeSet.add(new ComponentPosition((JInternalFrame)component, getLayer(component), b));
      } 
    } 
    ArrayList arrayList = new ArrayList(treeSet.size());
    Iterator iterator = treeSet.iterator();
    while (iterator.hasNext()) {
      ComponentPosition componentPosition;
      arrayList.add(componentPosition.component);
    } 
    return arrayList;
  }
  
  private JInternalFrame getNextFrame(JInternalFrame paramJInternalFrame, boolean paramBoolean) {
    verifyFramesCache();
    if (paramJInternalFrame == null)
      return getTopInternalFrame(); 
    int i = this.framesCache.indexOf(paramJInternalFrame);
    if (i == -1 || this.framesCache.size() == 1)
      return null; 
    if (paramBoolean) {
      if (++i == this.framesCache.size())
        i = 0; 
    } else if (--i == -1) {
      i = this.framesCache.size() - 1;
    } 
    return (JInternalFrame)this.framesCache.get(i);
  }
  
  JInternalFrame getNextFrame(JInternalFrame paramJInternalFrame) { return getNextFrame(paramJInternalFrame, true); }
  
  private JInternalFrame getTopInternalFrame() { return (this.framesCache.size() == 0) ? null : (JInternalFrame)this.framesCache.get(0); }
  
  private void updateFramesCache() { this.framesCache = getFrames(); }
  
  private void verifyFramesCache() {
    if (this.componentOrderChanged) {
      this.componentOrderChanged = false;
      updateFramesCache();
    } 
  }
  
  public void remove(Component paramComponent) {
    super.remove(paramComponent);
    updateFramesCache();
  }
  
  public JInternalFrame selectFrame(boolean paramBoolean) {
    JInternalFrame jInternalFrame1 = getSelectedFrame();
    JInternalFrame jInternalFrame2 = getNextFrame(jInternalFrame1, paramBoolean);
    if (jInternalFrame2 == null)
      return null; 
    setComponentOrderCheckingEnabled(false);
    if (paramBoolean && jInternalFrame1 != null)
      jInternalFrame1.moveToBack(); 
    try {
      jInternalFrame2.setSelected(true);
    } catch (PropertyVetoException propertyVetoException) {}
    setComponentOrderCheckingEnabled(true);
    return jInternalFrame2;
  }
  
  void setComponentOrderCheckingEnabled(boolean paramBoolean) { this.componentOrderCheckingEnabled = paramBoolean; }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt) {
    super.addImpl(paramComponent, paramObject, paramInt);
    if (this.componentOrderCheckingEnabled && (paramComponent instanceof JInternalFrame || paramComponent instanceof JInternalFrame.JDesktopIcon))
      this.componentOrderChanged = true; 
  }
  
  public void remove(int paramInt) {
    if (this.componentOrderCheckingEnabled) {
      Component component = getComponent(paramInt);
      if (component instanceof JInternalFrame || component instanceof JInternalFrame.JDesktopIcon)
        this.componentOrderChanged = true; 
    } 
    super.remove(paramInt);
  }
  
  public void removeAll() {
    if (this.componentOrderCheckingEnabled) {
      int i = getComponentCount();
      for (byte b = 0; b < i; b++) {
        Component component = getComponent(b);
        if (component instanceof JInternalFrame || component instanceof JInternalFrame.JDesktopIcon) {
          this.componentOrderChanged = true;
          break;
        } 
      } 
    } 
    super.removeAll();
  }
  
  public void setComponentZOrder(Component paramComponent, int paramInt) {
    super.setComponentZOrder(paramComponent, paramInt);
    if (this.componentOrderCheckingEnabled && (paramComponent instanceof JInternalFrame || paramComponent instanceof JInternalFrame.JDesktopIcon))
      this.componentOrderChanged = true; 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("DesktopPaneUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  void setUIProperty(String paramString, Object paramObject) {
    if (paramString == "dragMode") {
      if (!this.dragModeSet) {
        setDragMode(((Integer)paramObject).intValue());
        this.dragModeSet = false;
      } 
    } else {
      super.setUIProperty(paramString, paramObject);
    } 
  }
  
  protected String paramString() {
    String str = (this.desktopManager != null) ? this.desktopManager.toString() : "";
    return super.paramString() + ",desktopManager=" + str;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJDesktopPane(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJDesktopPane extends JComponent.AccessibleJComponent {
    protected AccessibleJDesktopPane() { super(JDesktopPane.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.DESKTOP_PANE; }
  }
  
  private static class ComponentPosition extends Object implements Comparable<ComponentPosition> {
    private final JInternalFrame component;
    
    private final int layer;
    
    private final int zOrder;
    
    ComponentPosition(JInternalFrame param1JInternalFrame, int param1Int1, int param1Int2) {
      this.component = param1JInternalFrame;
      this.layer = param1Int1;
      this.zOrder = param1Int2;
    }
    
    public int compareTo(ComponentPosition param1ComponentPosition) {
      int i = param1ComponentPosition.layer - this.layer;
      return (i == 0) ? (this.zOrder - param1ComponentPosition.zOrder) : i;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JDesktopPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */