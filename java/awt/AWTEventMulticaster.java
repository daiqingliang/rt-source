package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.EventListener;

public class AWTEventMulticaster implements ComponentListener, ContainerListener, FocusListener, KeyListener, MouseListener, MouseMotionListener, WindowListener, WindowFocusListener, WindowStateListener, ActionListener, ItemListener, AdjustmentListener, TextListener, InputMethodListener, HierarchyListener, HierarchyBoundsListener, MouseWheelListener {
  protected final EventListener a;
  
  protected final EventListener b;
  
  protected AWTEventMulticaster(EventListener paramEventListener1, EventListener paramEventListener2) {
    this.a = paramEventListener1;
    this.b = paramEventListener2;
  }
  
  protected EventListener remove(EventListener paramEventListener) {
    if (paramEventListener == this.a)
      return this.b; 
    if (paramEventListener == this.b)
      return this.a; 
    EventListener eventListener1 = removeInternal(this.a, paramEventListener);
    EventListener eventListener2 = removeInternal(this.b, paramEventListener);
    return (eventListener1 == this.a && eventListener2 == this.b) ? this : addInternal(eventListener1, eventListener2);
  }
  
  public void componentResized(ComponentEvent paramComponentEvent) {
    ((ComponentListener)this.a).componentResized(paramComponentEvent);
    ((ComponentListener)this.b).componentResized(paramComponentEvent);
  }
  
  public void componentMoved(ComponentEvent paramComponentEvent) {
    ((ComponentListener)this.a).componentMoved(paramComponentEvent);
    ((ComponentListener)this.b).componentMoved(paramComponentEvent);
  }
  
  public void componentShown(ComponentEvent paramComponentEvent) {
    ((ComponentListener)this.a).componentShown(paramComponentEvent);
    ((ComponentListener)this.b).componentShown(paramComponentEvent);
  }
  
  public void componentHidden(ComponentEvent paramComponentEvent) {
    ((ComponentListener)this.a).componentHidden(paramComponentEvent);
    ((ComponentListener)this.b).componentHidden(paramComponentEvent);
  }
  
  public void componentAdded(ContainerEvent paramContainerEvent) {
    ((ContainerListener)this.a).componentAdded(paramContainerEvent);
    ((ContainerListener)this.b).componentAdded(paramContainerEvent);
  }
  
  public void componentRemoved(ContainerEvent paramContainerEvent) {
    ((ContainerListener)this.a).componentRemoved(paramContainerEvent);
    ((ContainerListener)this.b).componentRemoved(paramContainerEvent);
  }
  
  public void focusGained(FocusEvent paramFocusEvent) {
    ((FocusListener)this.a).focusGained(paramFocusEvent);
    ((FocusListener)this.b).focusGained(paramFocusEvent);
  }
  
  public void focusLost(FocusEvent paramFocusEvent) {
    ((FocusListener)this.a).focusLost(paramFocusEvent);
    ((FocusListener)this.b).focusLost(paramFocusEvent);
  }
  
  public void keyTyped(KeyEvent paramKeyEvent) {
    ((KeyListener)this.a).keyTyped(paramKeyEvent);
    ((KeyListener)this.b).keyTyped(paramKeyEvent);
  }
  
  public void keyPressed(KeyEvent paramKeyEvent) {
    ((KeyListener)this.a).keyPressed(paramKeyEvent);
    ((KeyListener)this.b).keyPressed(paramKeyEvent);
  }
  
  public void keyReleased(KeyEvent paramKeyEvent) {
    ((KeyListener)this.a).keyReleased(paramKeyEvent);
    ((KeyListener)this.b).keyReleased(paramKeyEvent);
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent) {
    ((MouseListener)this.a).mouseClicked(paramMouseEvent);
    ((MouseListener)this.b).mouseClicked(paramMouseEvent);
  }
  
  public void mousePressed(MouseEvent paramMouseEvent) {
    ((MouseListener)this.a).mousePressed(paramMouseEvent);
    ((MouseListener)this.b).mousePressed(paramMouseEvent);
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent) {
    ((MouseListener)this.a).mouseReleased(paramMouseEvent);
    ((MouseListener)this.b).mouseReleased(paramMouseEvent);
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent) {
    ((MouseListener)this.a).mouseEntered(paramMouseEvent);
    ((MouseListener)this.b).mouseEntered(paramMouseEvent);
  }
  
  public void mouseExited(MouseEvent paramMouseEvent) {
    ((MouseListener)this.a).mouseExited(paramMouseEvent);
    ((MouseListener)this.b).mouseExited(paramMouseEvent);
  }
  
  public void mouseDragged(MouseEvent paramMouseEvent) {
    ((MouseMotionListener)this.a).mouseDragged(paramMouseEvent);
    ((MouseMotionListener)this.b).mouseDragged(paramMouseEvent);
  }
  
  public void mouseMoved(MouseEvent paramMouseEvent) {
    ((MouseMotionListener)this.a).mouseMoved(paramMouseEvent);
    ((MouseMotionListener)this.b).mouseMoved(paramMouseEvent);
  }
  
  public void windowOpened(WindowEvent paramWindowEvent) {
    ((WindowListener)this.a).windowOpened(paramWindowEvent);
    ((WindowListener)this.b).windowOpened(paramWindowEvent);
  }
  
  public void windowClosing(WindowEvent paramWindowEvent) {
    ((WindowListener)this.a).windowClosing(paramWindowEvent);
    ((WindowListener)this.b).windowClosing(paramWindowEvent);
  }
  
  public void windowClosed(WindowEvent paramWindowEvent) {
    ((WindowListener)this.a).windowClosed(paramWindowEvent);
    ((WindowListener)this.b).windowClosed(paramWindowEvent);
  }
  
  public void windowIconified(WindowEvent paramWindowEvent) {
    ((WindowListener)this.a).windowIconified(paramWindowEvent);
    ((WindowListener)this.b).windowIconified(paramWindowEvent);
  }
  
  public void windowDeiconified(WindowEvent paramWindowEvent) {
    ((WindowListener)this.a).windowDeiconified(paramWindowEvent);
    ((WindowListener)this.b).windowDeiconified(paramWindowEvent);
  }
  
  public void windowActivated(WindowEvent paramWindowEvent) {
    ((WindowListener)this.a).windowActivated(paramWindowEvent);
    ((WindowListener)this.b).windowActivated(paramWindowEvent);
  }
  
  public void windowDeactivated(WindowEvent paramWindowEvent) {
    ((WindowListener)this.a).windowDeactivated(paramWindowEvent);
    ((WindowListener)this.b).windowDeactivated(paramWindowEvent);
  }
  
  public void windowStateChanged(WindowEvent paramWindowEvent) {
    ((WindowStateListener)this.a).windowStateChanged(paramWindowEvent);
    ((WindowStateListener)this.b).windowStateChanged(paramWindowEvent);
  }
  
  public void windowGainedFocus(WindowEvent paramWindowEvent) {
    ((WindowFocusListener)this.a).windowGainedFocus(paramWindowEvent);
    ((WindowFocusListener)this.b).windowGainedFocus(paramWindowEvent);
  }
  
  public void windowLostFocus(WindowEvent paramWindowEvent) {
    ((WindowFocusListener)this.a).windowLostFocus(paramWindowEvent);
    ((WindowFocusListener)this.b).windowLostFocus(paramWindowEvent);
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    ((ActionListener)this.a).actionPerformed(paramActionEvent);
    ((ActionListener)this.b).actionPerformed(paramActionEvent);
  }
  
  public void itemStateChanged(ItemEvent paramItemEvent) {
    ((ItemListener)this.a).itemStateChanged(paramItemEvent);
    ((ItemListener)this.b).itemStateChanged(paramItemEvent);
  }
  
  public void adjustmentValueChanged(AdjustmentEvent paramAdjustmentEvent) {
    ((AdjustmentListener)this.a).adjustmentValueChanged(paramAdjustmentEvent);
    ((AdjustmentListener)this.b).adjustmentValueChanged(paramAdjustmentEvent);
  }
  
  public void textValueChanged(TextEvent paramTextEvent) {
    ((TextListener)this.a).textValueChanged(paramTextEvent);
    ((TextListener)this.b).textValueChanged(paramTextEvent);
  }
  
  public void inputMethodTextChanged(InputMethodEvent paramInputMethodEvent) {
    ((InputMethodListener)this.a).inputMethodTextChanged(paramInputMethodEvent);
    ((InputMethodListener)this.b).inputMethodTextChanged(paramInputMethodEvent);
  }
  
  public void caretPositionChanged(InputMethodEvent paramInputMethodEvent) {
    ((InputMethodListener)this.a).caretPositionChanged(paramInputMethodEvent);
    ((InputMethodListener)this.b).caretPositionChanged(paramInputMethodEvent);
  }
  
  public void hierarchyChanged(HierarchyEvent paramHierarchyEvent) {
    ((HierarchyListener)this.a).hierarchyChanged(paramHierarchyEvent);
    ((HierarchyListener)this.b).hierarchyChanged(paramHierarchyEvent);
  }
  
  public void ancestorMoved(HierarchyEvent paramHierarchyEvent) {
    ((HierarchyBoundsListener)this.a).ancestorMoved(paramHierarchyEvent);
    ((HierarchyBoundsListener)this.b).ancestorMoved(paramHierarchyEvent);
  }
  
  public void ancestorResized(HierarchyEvent paramHierarchyEvent) {
    ((HierarchyBoundsListener)this.a).ancestorResized(paramHierarchyEvent);
    ((HierarchyBoundsListener)this.b).ancestorResized(paramHierarchyEvent);
  }
  
  public void mouseWheelMoved(MouseWheelEvent paramMouseWheelEvent) {
    ((MouseWheelListener)this.a).mouseWheelMoved(paramMouseWheelEvent);
    ((MouseWheelListener)this.b).mouseWheelMoved(paramMouseWheelEvent);
  }
  
  public static ComponentListener add(ComponentListener paramComponentListener1, ComponentListener paramComponentListener2) { return (ComponentListener)addInternal(paramComponentListener1, paramComponentListener2); }
  
  public static ContainerListener add(ContainerListener paramContainerListener1, ContainerListener paramContainerListener2) { return (ContainerListener)addInternal(paramContainerListener1, paramContainerListener2); }
  
  public static FocusListener add(FocusListener paramFocusListener1, FocusListener paramFocusListener2) { return (FocusListener)addInternal(paramFocusListener1, paramFocusListener2); }
  
  public static KeyListener add(KeyListener paramKeyListener1, KeyListener paramKeyListener2) { return (KeyListener)addInternal(paramKeyListener1, paramKeyListener2); }
  
  public static MouseListener add(MouseListener paramMouseListener1, MouseListener paramMouseListener2) { return (MouseListener)addInternal(paramMouseListener1, paramMouseListener2); }
  
  public static MouseMotionListener add(MouseMotionListener paramMouseMotionListener1, MouseMotionListener paramMouseMotionListener2) { return (MouseMotionListener)addInternal(paramMouseMotionListener1, paramMouseMotionListener2); }
  
  public static WindowListener add(WindowListener paramWindowListener1, WindowListener paramWindowListener2) { return (WindowListener)addInternal(paramWindowListener1, paramWindowListener2); }
  
  public static WindowStateListener add(WindowStateListener paramWindowStateListener1, WindowStateListener paramWindowStateListener2) { return (WindowStateListener)addInternal(paramWindowStateListener1, paramWindowStateListener2); }
  
  public static WindowFocusListener add(WindowFocusListener paramWindowFocusListener1, WindowFocusListener paramWindowFocusListener2) { return (WindowFocusListener)addInternal(paramWindowFocusListener1, paramWindowFocusListener2); }
  
  public static ActionListener add(ActionListener paramActionListener1, ActionListener paramActionListener2) { return (ActionListener)addInternal(paramActionListener1, paramActionListener2); }
  
  public static ItemListener add(ItemListener paramItemListener1, ItemListener paramItemListener2) { return (ItemListener)addInternal(paramItemListener1, paramItemListener2); }
  
  public static AdjustmentListener add(AdjustmentListener paramAdjustmentListener1, AdjustmentListener paramAdjustmentListener2) { return (AdjustmentListener)addInternal(paramAdjustmentListener1, paramAdjustmentListener2); }
  
  public static TextListener add(TextListener paramTextListener1, TextListener paramTextListener2) { return (TextListener)addInternal(paramTextListener1, paramTextListener2); }
  
  public static InputMethodListener add(InputMethodListener paramInputMethodListener1, InputMethodListener paramInputMethodListener2) { return (InputMethodListener)addInternal(paramInputMethodListener1, paramInputMethodListener2); }
  
  public static HierarchyListener add(HierarchyListener paramHierarchyListener1, HierarchyListener paramHierarchyListener2) { return (HierarchyListener)addInternal(paramHierarchyListener1, paramHierarchyListener2); }
  
  public static HierarchyBoundsListener add(HierarchyBoundsListener paramHierarchyBoundsListener1, HierarchyBoundsListener paramHierarchyBoundsListener2) { return (HierarchyBoundsListener)addInternal(paramHierarchyBoundsListener1, paramHierarchyBoundsListener2); }
  
  public static MouseWheelListener add(MouseWheelListener paramMouseWheelListener1, MouseWheelListener paramMouseWheelListener2) { return (MouseWheelListener)addInternal(paramMouseWheelListener1, paramMouseWheelListener2); }
  
  public static ComponentListener remove(ComponentListener paramComponentListener1, ComponentListener paramComponentListener2) { return (ComponentListener)removeInternal(paramComponentListener1, paramComponentListener2); }
  
  public static ContainerListener remove(ContainerListener paramContainerListener1, ContainerListener paramContainerListener2) { return (ContainerListener)removeInternal(paramContainerListener1, paramContainerListener2); }
  
  public static FocusListener remove(FocusListener paramFocusListener1, FocusListener paramFocusListener2) { return (FocusListener)removeInternal(paramFocusListener1, paramFocusListener2); }
  
  public static KeyListener remove(KeyListener paramKeyListener1, KeyListener paramKeyListener2) { return (KeyListener)removeInternal(paramKeyListener1, paramKeyListener2); }
  
  public static MouseListener remove(MouseListener paramMouseListener1, MouseListener paramMouseListener2) { return (MouseListener)removeInternal(paramMouseListener1, paramMouseListener2); }
  
  public static MouseMotionListener remove(MouseMotionListener paramMouseMotionListener1, MouseMotionListener paramMouseMotionListener2) { return (MouseMotionListener)removeInternal(paramMouseMotionListener1, paramMouseMotionListener2); }
  
  public static WindowListener remove(WindowListener paramWindowListener1, WindowListener paramWindowListener2) { return (WindowListener)removeInternal(paramWindowListener1, paramWindowListener2); }
  
  public static WindowStateListener remove(WindowStateListener paramWindowStateListener1, WindowStateListener paramWindowStateListener2) { return (WindowStateListener)removeInternal(paramWindowStateListener1, paramWindowStateListener2); }
  
  public static WindowFocusListener remove(WindowFocusListener paramWindowFocusListener1, WindowFocusListener paramWindowFocusListener2) { return (WindowFocusListener)removeInternal(paramWindowFocusListener1, paramWindowFocusListener2); }
  
  public static ActionListener remove(ActionListener paramActionListener1, ActionListener paramActionListener2) { return (ActionListener)removeInternal(paramActionListener1, paramActionListener2); }
  
  public static ItemListener remove(ItemListener paramItemListener1, ItemListener paramItemListener2) { return (ItemListener)removeInternal(paramItemListener1, paramItemListener2); }
  
  public static AdjustmentListener remove(AdjustmentListener paramAdjustmentListener1, AdjustmentListener paramAdjustmentListener2) { return (AdjustmentListener)removeInternal(paramAdjustmentListener1, paramAdjustmentListener2); }
  
  public static TextListener remove(TextListener paramTextListener1, TextListener paramTextListener2) { return (TextListener)removeInternal(paramTextListener1, paramTextListener2); }
  
  public static InputMethodListener remove(InputMethodListener paramInputMethodListener1, InputMethodListener paramInputMethodListener2) { return (InputMethodListener)removeInternal(paramInputMethodListener1, paramInputMethodListener2); }
  
  public static HierarchyListener remove(HierarchyListener paramHierarchyListener1, HierarchyListener paramHierarchyListener2) { return (HierarchyListener)removeInternal(paramHierarchyListener1, paramHierarchyListener2); }
  
  public static HierarchyBoundsListener remove(HierarchyBoundsListener paramHierarchyBoundsListener1, HierarchyBoundsListener paramHierarchyBoundsListener2) { return (HierarchyBoundsListener)removeInternal(paramHierarchyBoundsListener1, paramHierarchyBoundsListener2); }
  
  public static MouseWheelListener remove(MouseWheelListener paramMouseWheelListener1, MouseWheelListener paramMouseWheelListener2) { return (MouseWheelListener)removeInternal(paramMouseWheelListener1, paramMouseWheelListener2); }
  
  protected static EventListener addInternal(EventListener paramEventListener1, EventListener paramEventListener2) { return (paramEventListener1 == null) ? paramEventListener2 : ((paramEventListener2 == null) ? paramEventListener1 : new AWTEventMulticaster(paramEventListener1, paramEventListener2)); }
  
  protected static EventListener removeInternal(EventListener paramEventListener1, EventListener paramEventListener2) { return (paramEventListener1 == paramEventListener2 || paramEventListener1 == null) ? null : ((paramEventListener1 instanceof AWTEventMulticaster) ? ((AWTEventMulticaster)paramEventListener1).remove(paramEventListener2) : paramEventListener1); }
  
  protected void saveInternal(ObjectOutputStream paramObjectOutputStream, String paramString) throws IOException {
    if (this.a instanceof AWTEventMulticaster) {
      ((AWTEventMulticaster)this.a).saveInternal(paramObjectOutputStream, paramString);
    } else if (this.a instanceof java.io.Serializable) {
      paramObjectOutputStream.writeObject(paramString);
      paramObjectOutputStream.writeObject(this.a);
    } 
    if (this.b instanceof AWTEventMulticaster) {
      ((AWTEventMulticaster)this.b).saveInternal(paramObjectOutputStream, paramString);
    } else if (this.b instanceof java.io.Serializable) {
      paramObjectOutputStream.writeObject(paramString);
      paramObjectOutputStream.writeObject(this.b);
    } 
  }
  
  protected static void save(ObjectOutputStream paramObjectOutputStream, String paramString, EventListener paramEventListener) throws IOException {
    if (paramEventListener == null)
      return; 
    if (paramEventListener instanceof AWTEventMulticaster) {
      ((AWTEventMulticaster)paramEventListener).saveInternal(paramObjectOutputStream, paramString);
    } else if (paramEventListener instanceof java.io.Serializable) {
      paramObjectOutputStream.writeObject(paramString);
      paramObjectOutputStream.writeObject(paramEventListener);
    } 
  }
  
  private static int getListenerCount(EventListener paramEventListener, Class<?> paramClass) { return (paramEventListener instanceof AWTEventMulticaster) ? ((aWTEventMulticaster = (AWTEventMulticaster)paramEventListener).getListenerCount(aWTEventMulticaster.a, paramClass) + getListenerCount(aWTEventMulticaster.b, paramClass)) : (paramClass.isInstance(paramEventListener) ? 1 : 0); }
  
  private static int populateListenerArray(EventListener[] paramArrayOfEventListener, EventListener paramEventListener, int paramInt) {
    if (paramEventListener instanceof AWTEventMulticaster) {
      AWTEventMulticaster aWTEventMulticaster;
      int i = (aWTEventMulticaster = (AWTEventMulticaster)paramEventListener).populateListenerArray(paramArrayOfEventListener, aWTEventMulticaster.a, paramInt);
      return populateListenerArray(paramArrayOfEventListener, aWTEventMulticaster.b, i);
    } 
    if (paramArrayOfEventListener.getClass().getComponentType().isInstance(paramEventListener)) {
      paramArrayOfEventListener[paramInt] = paramEventListener;
      return paramInt + 1;
    } 
    return paramInt;
  }
  
  public static <T extends EventListener> T[] getListeners(EventListener paramEventListener, Class<T> paramClass) {
    if (paramClass == null)
      throw new NullPointerException("Listener type should not be null"); 
    int i = getListenerCount(paramEventListener, paramClass);
    EventListener[] arrayOfEventListener = (EventListener[])Array.newInstance(paramClass, i);
    populateListenerArray(arrayOfEventListener, paramEventListener, 0);
    return (T[])arrayOfEventListener;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\AWTEventMulticaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */