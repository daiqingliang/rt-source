package javax.swing;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.swing.plaf.ButtonUI;

public class JToggleButton extends AbstractButton implements Accessible {
  private static final String uiClassID = "ToggleButtonUI";
  
  public JToggleButton() { this(null, null, false); }
  
  public JToggleButton(Icon paramIcon) { this(null, paramIcon, false); }
  
  public JToggleButton(Icon paramIcon, boolean paramBoolean) { this(null, paramIcon, paramBoolean); }
  
  public JToggleButton(String paramString) { this(paramString, null, false); }
  
  public JToggleButton(String paramString, boolean paramBoolean) { this(paramString, null, paramBoolean); }
  
  public JToggleButton(Action paramAction) {
    this();
    setAction(paramAction);
  }
  
  public JToggleButton(String paramString, Icon paramIcon) { this(paramString, paramIcon, false); }
  
  public JToggleButton(String paramString, Icon paramIcon, boolean paramBoolean) {
    setModel(new ToggleButtonModel());
    this.model.setSelected(paramBoolean);
    init(paramString, paramIcon);
  }
  
  public void updateUI() { setUI((ButtonUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "ToggleButtonUI"; }
  
  boolean shouldUpdateSelectedStateFromAction() { return true; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ToggleButtonUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() { return super.paramString(); }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJToggleButton(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJToggleButton extends AbstractButton.AccessibleAbstractButton implements ItemListener {
    public AccessibleJToggleButton() {
      super(JToggleButton.this);
      this$0.addItemListener(this);
    }
    
    public void itemStateChanged(ItemEvent param1ItemEvent) {
      JToggleButton jToggleButton;
      if (JToggleButton.this.accessibleContext != null)
        if (jToggleButton.isSelected()) {
          JToggleButton.this.accessibleContext.firePropertyChange("AccessibleState", null, AccessibleState.CHECKED);
        } else {
          JToggleButton.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.CHECKED, null);
        }  
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.TOGGLE_BUTTON; }
  }
  
  public static class ToggleButtonModel extends DefaultButtonModel {
    public boolean isSelected() { return ((this.stateMask & 0x2) != 0); }
    
    public void setSelected(boolean param1Boolean) {
      ButtonGroup buttonGroup = getGroup();
      if (buttonGroup != null) {
        buttonGroup.setSelected(this, param1Boolean);
        param1Boolean = buttonGroup.isSelected(this);
      } 
      if (isSelected() == param1Boolean)
        return; 
      if (param1Boolean) {
        this.stateMask |= 0x2;
      } else {
        this.stateMask &= 0xFFFFFFFD;
      } 
      fireStateChanged();
      fireItemStateChanged(new ItemEvent(this, 701, this, isSelected() ? 1 : 2));
    }
    
    public void setPressed(boolean param1Boolean) {
      if (isPressed() == param1Boolean || !isEnabled())
        return; 
      if (!param1Boolean && isArmed())
        setSelected(!isSelected()); 
      if (param1Boolean) {
        this.stateMask |= 0x4;
      } else {
        this.stateMask &= 0xFFFFFFFB;
      } 
      fireStateChanged();
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
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JToggleButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */