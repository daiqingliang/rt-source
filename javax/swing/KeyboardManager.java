package javax.swing;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import sun.awt.EmbeddedFrame;

class KeyboardManager {
  static KeyboardManager currentManager = new KeyboardManager();
  
  Hashtable<Container, Hashtable> containerMap = new Hashtable();
  
  Hashtable<ComponentKeyStrokePair, Container> componentKeyStrokeMap = new Hashtable();
  
  public static KeyboardManager getCurrentManager() { return currentManager; }
  
  public static void setCurrentManager(KeyboardManager paramKeyboardManager) { currentManager = paramKeyboardManager; }
  
  public void registerKeyStroke(KeyStroke paramKeyStroke, JComponent paramJComponent) {
    Container container = getTopAncestor(paramJComponent);
    if (container == null)
      return; 
    Hashtable hashtable = (Hashtable)this.containerMap.get(container);
    if (hashtable == null)
      hashtable = registerNewTopContainer(container); 
    Object object = hashtable.get(paramKeyStroke);
    if (object == null) {
      hashtable.put(paramKeyStroke, paramJComponent);
    } else if (object instanceof Vector) {
      Vector vector = (Vector)object;
      if (!vector.contains(paramJComponent))
        vector.addElement(paramJComponent); 
    } else if (object instanceof JComponent) {
      if (object != paramJComponent) {
        Vector vector = new Vector();
        vector.addElement((JComponent)object);
        vector.addElement(paramJComponent);
        hashtable.put(paramKeyStroke, vector);
      } 
    } else {
      System.out.println("Unexpected condition in registerKeyStroke");
      Thread.dumpStack();
    } 
    this.componentKeyStrokeMap.put(new ComponentKeyStrokePair(paramJComponent, paramKeyStroke), container);
    if (container instanceof EmbeddedFrame)
      ((EmbeddedFrame)container).registerAccelerator(paramKeyStroke); 
  }
  
  private static Container getTopAncestor(JComponent paramJComponent) {
    for (Container container = paramJComponent.getParent(); container != null; container = container.getParent()) {
      if ((container instanceof Window && ((Window)container).isFocusableWindow()) || container instanceof java.applet.Applet || container instanceof JInternalFrame)
        return container; 
    } 
    return null;
  }
  
  public void unregisterKeyStroke(KeyStroke paramKeyStroke, JComponent paramJComponent) {
    ComponentKeyStrokePair componentKeyStrokePair = new ComponentKeyStrokePair(paramJComponent, paramKeyStroke);
    Container container = (Container)this.componentKeyStrokeMap.get(componentKeyStrokePair);
    if (container == null)
      return; 
    Hashtable hashtable = (Hashtable)this.containerMap.get(container);
    if (hashtable == null) {
      Thread.dumpStack();
      return;
    } 
    Object object = hashtable.get(paramKeyStroke);
    if (object == null) {
      Thread.dumpStack();
      return;
    } 
    if (object instanceof JComponent && object == paramJComponent) {
      hashtable.remove(paramKeyStroke);
    } else if (object instanceof Vector) {
      Vector vector = (Vector)object;
      vector.removeElement(paramJComponent);
      if (vector.isEmpty())
        hashtable.remove(paramKeyStroke); 
    } 
    if (hashtable.isEmpty())
      this.containerMap.remove(container); 
    this.componentKeyStrokeMap.remove(componentKeyStrokePair);
    if (container instanceof EmbeddedFrame)
      ((EmbeddedFrame)container).unregisterAccelerator(paramKeyStroke); 
  }
  
  public boolean fireKeyboardAction(KeyEvent paramKeyEvent, boolean paramBoolean, Container paramContainer) {
    KeyStroke keyStroke1;
    if (paramKeyEvent.isConsumed()) {
      System.out.println("Acquired pre-used event!");
      Thread.dumpStack();
    } 
    KeyStroke keyStroke2 = null;
    if (paramKeyEvent.getID() == 400) {
      keyStroke1 = KeyStroke.getKeyStroke(paramKeyEvent.getKeyChar());
    } else {
      if (paramKeyEvent.getKeyCode() != paramKeyEvent.getExtendedKeyCode())
        keyStroke2 = KeyStroke.getKeyStroke(paramKeyEvent.getExtendedKeyCode(), paramKeyEvent.getModifiers(), !paramBoolean); 
      keyStroke1 = KeyStroke.getKeyStroke(paramKeyEvent.getKeyCode(), paramKeyEvent.getModifiers(), !paramBoolean);
    } 
    Hashtable hashtable = (Hashtable)this.containerMap.get(paramContainer);
    if (hashtable != null) {
      Object object = null;
      if (keyStroke2 != null) {
        object = hashtable.get(keyStroke2);
        if (object != null)
          keyStroke1 = keyStroke2; 
      } 
      if (object == null)
        object = hashtable.get(keyStroke1); 
      if (object != null)
        if (object instanceof JComponent) {
          JComponent jComponent = (JComponent)object;
          if (jComponent.isShowing() && jComponent.isEnabled())
            fireBinding(jComponent, keyStroke1, paramKeyEvent, paramBoolean); 
        } else if (object instanceof Vector) {
          Vector vector = (Vector)object;
          for (int i = vector.size() - 1; i >= 0; i--) {
            JComponent jComponent = (JComponent)vector.elementAt(i);
            if (jComponent.isShowing() && jComponent.isEnabled()) {
              fireBinding(jComponent, keyStroke1, paramKeyEvent, paramBoolean);
              if (paramKeyEvent.isConsumed())
                return true; 
            } 
          } 
        } else {
          System.out.println("Unexpected condition in fireKeyboardAction " + object);
          Thread.dumpStack();
        }  
    } 
    if (paramKeyEvent.isConsumed())
      return true; 
    if (hashtable != null) {
      Vector vector = (Vector)hashtable.get(JMenuBar.class);
      if (vector != null) {
        Enumeration enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
          JMenuBar jMenuBar = (JMenuBar)enumeration.nextElement();
          if (jMenuBar.isShowing() && jMenuBar.isEnabled()) {
            boolean bool = (keyStroke2 != null && !keyStroke2.equals(keyStroke1)) ? 1 : 0;
            if (bool)
              fireBinding(jMenuBar, keyStroke2, paramKeyEvent, paramBoolean); 
            if (!bool || !paramKeyEvent.isConsumed())
              fireBinding(jMenuBar, keyStroke1, paramKeyEvent, paramBoolean); 
            if (paramKeyEvent.isConsumed())
              return true; 
          } 
        } 
      } 
    } 
    return paramKeyEvent.isConsumed();
  }
  
  void fireBinding(JComponent paramJComponent, KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, boolean paramBoolean) {
    if (paramJComponent.processKeyBinding(paramKeyStroke, paramKeyEvent, 2, paramBoolean))
      paramKeyEvent.consume(); 
  }
  
  public void registerMenuBar(JMenuBar paramJMenuBar) {
    Container container = getTopAncestor(paramJMenuBar);
    if (container == null)
      return; 
    Hashtable hashtable = (Hashtable)this.containerMap.get(container);
    if (hashtable == null)
      hashtable = registerNewTopContainer(container); 
    Vector vector = (Vector)hashtable.get(JMenuBar.class);
    if (vector == null) {
      vector = new Vector();
      hashtable.put(JMenuBar.class, vector);
    } 
    if (!vector.contains(paramJMenuBar))
      vector.addElement(paramJMenuBar); 
  }
  
  public void unregisterMenuBar(JMenuBar paramJMenuBar) {
    Container container = getTopAncestor(paramJMenuBar);
    if (container == null)
      return; 
    Hashtable hashtable = (Hashtable)this.containerMap.get(container);
    if (hashtable != null) {
      Vector vector = (Vector)hashtable.get(JMenuBar.class);
      if (vector != null) {
        vector.removeElement(paramJMenuBar);
        if (vector.isEmpty()) {
          hashtable.remove(JMenuBar.class);
          if (hashtable.isEmpty())
            this.containerMap.remove(container); 
        } 
      } 
    } 
  }
  
  protected Hashtable registerNewTopContainer(Container paramContainer) {
    Hashtable hashtable = new Hashtable();
    this.containerMap.put(paramContainer, hashtable);
    return hashtable;
  }
  
  class ComponentKeyStrokePair {
    Object component;
    
    Object keyStroke;
    
    public ComponentKeyStrokePair(Object param1Object1, Object param1Object2) {
      this.component = param1Object1;
      this.keyStroke = param1Object2;
    }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof ComponentKeyStrokePair))
        return false; 
      ComponentKeyStrokePair componentKeyStrokePair = (ComponentKeyStrokePair)param1Object;
      return (this.component.equals(componentKeyStrokePair.component) && this.keyStroke.equals(componentKeyStrokePair.keyStroke));
    }
    
    public int hashCode() { return this.component.hashCode() * this.keyStroke.hashCode(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\KeyboardManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */