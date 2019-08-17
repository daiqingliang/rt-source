package javax.swing;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultButtonModel implements ButtonModel, Serializable {
  protected int stateMask = 0;
  
  protected String actionCommand = null;
  
  protected ButtonGroup group = null;
  
  protected int mnemonic = 0;
  
  protected ChangeEvent changeEvent = null;
  
  protected EventListenerList listenerList = new EventListenerList();
  
  private boolean menuItem = false;
  
  public static final int ARMED = 1;
  
  public static final int SELECTED = 2;
  
  public static final int PRESSED = 4;
  
  public static final int ENABLED = 8;
  
  public static final int ROLLOVER = 16;
  
  public DefaultButtonModel() {
    this.stateMask = 0;
    setEnabled(true);
  }
  
  public void setActionCommand(String paramString) { this.actionCommand = paramString; }
  
  public String getActionCommand() { return this.actionCommand; }
  
  public boolean isArmed() { return ((this.stateMask & true) != 0); }
  
  public boolean isSelected() { return ((this.stateMask & 0x2) != 0); }
  
  public boolean isEnabled() { return ((this.stateMask & 0x8) != 0); }
  
  public boolean isPressed() { return ((this.stateMask & 0x4) != 0); }
  
  public boolean isRollover() { return ((this.stateMask & 0x10) != 0); }
  
  public void setArmed(boolean paramBoolean) {
    if (isMenuItem() && UIManager.getBoolean("MenuItem.disabledAreNavigable")) {
      if (isArmed() == paramBoolean)
        return; 
    } else if (isArmed() == paramBoolean || !isEnabled()) {
      return;
    } 
    if (paramBoolean) {
      this.stateMask |= 0x1;
    } else {
      this.stateMask &= 0xFFFFFFFE;
    } 
    fireStateChanged();
  }
  
  public void setEnabled(boolean paramBoolean) {
    if (isEnabled() == paramBoolean)
      return; 
    if (paramBoolean) {
      this.stateMask |= 0x8;
    } else {
      this.stateMask &= 0xFFFFFFF7;
      this.stateMask &= 0xFFFFFFFE;
      this.stateMask &= 0xFFFFFFFB;
    } 
    fireStateChanged();
  }
  
  public void setSelected(boolean paramBoolean) {
    if (isSelected() == paramBoolean)
      return; 
    if (paramBoolean) {
      this.stateMask |= 0x2;
    } else {
      this.stateMask &= 0xFFFFFFFD;
    } 
    fireItemStateChanged(new ItemEvent(this, 701, this, paramBoolean ? 1 : 2));
    fireStateChanged();
  }
  
  public void setPressed(boolean paramBoolean) {
    if (isPressed() == paramBoolean || !isEnabled())
      return; 
    if (paramBoolean) {
      this.stateMask |= 0x4;
    } else {
      this.stateMask &= 0xFFFFFFFB;
    } 
    if (!isPressed() && isArmed()) {
      int i = 0;
      AWTEvent aWTEvent = EventQueue.getCurrentEvent();
      if (aWTEvent instanceof InputEvent) {
        i = ((InputEvent)aWTEvent).getModifiers();
      } else if (aWTEvent instanceof ActionEvent) {
        i = ((ActionEvent)aWTEvent).getModifiers();
      } 
      fireActionPerformed(new ActionEvent(this, 1001, getActionCommand(), EventQueue.getMostRecentEventTime(), i));
    } 
    fireStateChanged();
  }
  
  public void setRollover(boolean paramBoolean) {
    if (isRollover() == paramBoolean || !isEnabled())
      return; 
    if (paramBoolean) {
      this.stateMask |= 0x10;
    } else {
      this.stateMask &= 0xFFFFFFEF;
    } 
    fireStateChanged();
  }
  
  public void setMnemonic(int paramInt) {
    this.mnemonic = paramInt;
    fireStateChanged();
  }
  
  public int getMnemonic() { return this.mnemonic; }
  
  public void addChangeListener(ChangeListener paramChangeListener) { this.listenerList.add(ChangeListener.class, paramChangeListener); }
  
  public void removeChangeListener(ChangeListener paramChangeListener) { this.listenerList.remove(ChangeListener.class, paramChangeListener); }
  
  public ChangeListener[] getChangeListeners() { return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class); }
  
  protected void fireStateChanged() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class) {
        if (this.changeEvent == null)
          this.changeEvent = new ChangeEvent(this); 
        ((ChangeListener)arrayOfObject[i + 1]).stateChanged(this.changeEvent);
      } 
    } 
  }
  
  public void addActionListener(ActionListener paramActionListener) { this.listenerList.add(ActionListener.class, paramActionListener); }
  
  public void removeActionListener(ActionListener paramActionListener) { this.listenerList.remove(ActionListener.class, paramActionListener); }
  
  public ActionListener[] getActionListeners() { return (ActionListener[])this.listenerList.getListeners(ActionListener.class); }
  
  protected void fireActionPerformed(ActionEvent paramActionEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ActionListener.class)
        ((ActionListener)arrayOfObject[i + 1]).actionPerformed(paramActionEvent); 
    } 
  }
  
  public void addItemListener(ItemListener paramItemListener) { this.listenerList.add(ItemListener.class, paramItemListener); }
  
  public void removeItemListener(ItemListener paramItemListener) { this.listenerList.remove(ItemListener.class, paramItemListener); }
  
  public ItemListener[] getItemListeners() { return (ItemListener[])this.listenerList.getListeners(ItemListener.class); }
  
  protected void fireItemStateChanged(ItemEvent paramItemEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ItemListener.class)
        ((ItemListener)arrayOfObject[i + 1]).itemStateChanged(paramItemEvent); 
    } 
  }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) { return (T[])this.listenerList.getListeners(paramClass); }
  
  public Object[] getSelectedObjects() { return null; }
  
  public void setGroup(ButtonGroup paramButtonGroup) { this.group = paramButtonGroup; }
  
  public ButtonGroup getGroup() { return this.group; }
  
  boolean isMenuItem() { return this.menuItem; }
  
  void setMenuItem(boolean paramBoolean) { this.menuItem = paramBoolean; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DefaultButtonModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */