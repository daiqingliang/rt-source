package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

public class DesktopProperty implements UIDefaults.ActiveValue {
  private static boolean updatePending;
  
  private static final ReferenceQueue<DesktopProperty> queue = new ReferenceQueue();
  
  private WeakPCL pcl;
  
  private final String key;
  
  private Object value;
  
  private final Object fallback;
  
  static void flushUnreferencedProperties() {
    WeakPCL weakPCL;
    while ((weakPCL = (WeakPCL)queue.poll()) != null)
      weakPCL.dispose(); 
  }
  
  private static void setUpdatePending(boolean paramBoolean) { updatePending = paramBoolean; }
  
  private static boolean isUpdatePending() { return updatePending; }
  
  private static void updateAllUIs() {
    Class clazz = UIManager.getLookAndFeel().getClass();
    if (clazz.getPackage().equals(DesktopProperty.class.getPackage()))
      XPStyle.invalidateStyle(); 
    Frame[] arrayOfFrame = Frame.getFrames();
    for (Frame frame : arrayOfFrame)
      updateWindowUI(frame); 
  }
  
  private static void updateWindowUI(Window paramWindow) {
    SwingUtilities.updateComponentTreeUI(paramWindow);
    Window[] arrayOfWindow = paramWindow.getOwnedWindows();
    for (Window window : arrayOfWindow)
      updateWindowUI(window); 
  }
  
  public DesktopProperty(String paramString, Object paramObject) {
    this.key = paramString;
    this.fallback = paramObject;
    flushUnreferencedProperties();
  }
  
  public Object createValue(UIDefaults paramUIDefaults) {
    if (this.value == null) {
      this.value = configureValue(getValueFromDesktop());
      if (this.value == null)
        this.value = configureValue(getDefaultValue()); 
    } 
    return this.value;
  }
  
  protected Object getValueFromDesktop() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (this.pcl == null) {
      this.pcl = new WeakPCL(this, getKey(), UIManager.getLookAndFeel());
      toolkit.addPropertyChangeListener(getKey(), this.pcl);
    } 
    return toolkit.getDesktopProperty(getKey());
  }
  
  protected Object getDefaultValue() { return this.fallback; }
  
  public void invalidate(LookAndFeel paramLookAndFeel) { invalidate(); }
  
  public void invalidate() { this.value = null; }
  
  protected void updateUI() {
    if (!isUpdatePending()) {
      setUpdatePending(true);
      Runnable runnable = new Runnable() {
          public void run() {
            DesktopProperty.updateAllUIs();
            DesktopProperty.setUpdatePending(false);
          }
        };
      SwingUtilities.invokeLater(runnable);
    } 
  }
  
  protected Object configureValue(Object paramObject) {
    if (paramObject != null) {
      if (paramObject instanceof Color)
        return new ColorUIResource((Color)paramObject); 
      if (paramObject instanceof Font)
        return new FontUIResource((Font)paramObject); 
      if (paramObject instanceof UIDefaults.LazyValue) {
        paramObject = ((UIDefaults.LazyValue)paramObject).createValue(null);
      } else if (paramObject instanceof UIDefaults.ActiveValue) {
        paramObject = ((UIDefaults.ActiveValue)paramObject).createValue(null);
      } 
    } 
    return paramObject;
  }
  
  protected String getKey() { return this.key; }
  
  private static class WeakPCL extends WeakReference<DesktopProperty> implements PropertyChangeListener {
    private String key;
    
    private LookAndFeel laf;
    
    WeakPCL(DesktopProperty param1DesktopProperty, String param1String, LookAndFeel param1LookAndFeel) {
      super(param1DesktopProperty, queue);
      this.key = param1String;
      this.laf = param1LookAndFeel;
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      DesktopProperty desktopProperty = (DesktopProperty)get();
      if (desktopProperty == null || this.laf != UIManager.getLookAndFeel()) {
        dispose();
      } else {
        desktopProperty.invalidate(this.laf);
        desktopProperty.updateUI();
      } 
    }
    
    void dispose() { Toolkit.getDefaultToolkit().removePropertyChangeListener(this.key, this); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\DesktopProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */