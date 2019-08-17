package javax.swing.plaf.basic;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentInputMapUIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicButtonListener implements MouseListener, MouseMotionListener, FocusListener, ChangeListener, PropertyChangeListener {
  private long lastPressedTimestamp = -1L;
  
  private boolean shouldDiscardRelease = false;
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("pressed"));
    paramLazyActionMap.put(new Actions("released"));
  }
  
  public BasicButtonListener(AbstractButton paramAbstractButton) {}
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str == "mnemonic") {
      updateMnemonicBinding((AbstractButton)paramPropertyChangeEvent.getSource());
    } else if (str == "contentAreaFilled") {
      checkOpacity((AbstractButton)paramPropertyChangeEvent.getSource());
    } else if (str == "text" || "font" == str || "foreground" == str) {
      AbstractButton abstractButton = (AbstractButton)paramPropertyChangeEvent.getSource();
      BasicHTML.updateRenderer(abstractButton, abstractButton.getText());
    } 
  }
  
  protected void checkOpacity(AbstractButton paramAbstractButton) { paramAbstractButton.setOpaque(paramAbstractButton.isContentAreaFilled()); }
  
  public void installKeyboardActions(JComponent paramJComponent) {
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    updateMnemonicBinding(abstractButton);
    LazyActionMap.installLazyActionMap(paramJComponent, BasicButtonListener.class, "Button.actionMap");
    InputMap inputMap = getInputMap(0, paramJComponent);
    SwingUtilities.replaceUIInputMap(paramJComponent, 0, inputMap);
  }
  
  public void uninstallKeyboardActions(JComponent paramJComponent) {
    SwingUtilities.replaceUIInputMap(paramJComponent, 2, null);
    SwingUtilities.replaceUIInputMap(paramJComponent, 0, null);
    SwingUtilities.replaceUIActionMap(paramJComponent, null);
  }
  
  InputMap getInputMap(int paramInt, JComponent paramJComponent) {
    if (paramInt == 0) {
      BasicButtonUI basicButtonUI = (BasicButtonUI)BasicLookAndFeel.getUIOfType(((AbstractButton)paramJComponent).getUI(), BasicButtonUI.class);
      if (basicButtonUI != null)
        return (InputMap)DefaultLookup.get(paramJComponent, basicButtonUI, basicButtonUI.getPropertyPrefix() + "focusInputMap"); 
    } 
    return null;
  }
  
  void updateMnemonicBinding(AbstractButton paramAbstractButton) {
    int i = paramAbstractButton.getMnemonic();
    if (i != 0) {
      InputMap inputMap = SwingUtilities.getUIInputMap(paramAbstractButton, 2);
      if (inputMap == null) {
        inputMap = new ComponentInputMapUIResource(paramAbstractButton);
        SwingUtilities.replaceUIInputMap(paramAbstractButton, 2, inputMap);
      } 
      inputMap.clear();
      inputMap.put(KeyStroke.getKeyStroke(i, BasicLookAndFeel.getFocusAcceleratorKeyMask(), false), "pressed");
      inputMap.put(KeyStroke.getKeyStroke(i, BasicLookAndFeel.getFocusAcceleratorKeyMask(), true), "released");
      inputMap.put(KeyStroke.getKeyStroke(i, 0, true), "released");
    } else {
      InputMap inputMap = SwingUtilities.getUIInputMap(paramAbstractButton, 2);
      if (inputMap != null)
        inputMap.clear(); 
    } 
  }
  
  public void stateChanged(ChangeEvent paramChangeEvent) {
    AbstractButton abstractButton = (AbstractButton)paramChangeEvent.getSource();
    abstractButton.repaint();
  }
  
  public void focusGained(FocusEvent paramFocusEvent) {
    AbstractButton abstractButton = (AbstractButton)paramFocusEvent.getSource();
    if (abstractButton instanceof JButton && ((JButton)abstractButton).isDefaultCapable()) {
      JRootPane jRootPane = abstractButton.getRootPane();
      if (jRootPane != null) {
        BasicButtonUI basicButtonUI = (BasicButtonUI)BasicLookAndFeel.getUIOfType(abstractButton.getUI(), BasicButtonUI.class);
        if (basicButtonUI != null && DefaultLookup.getBoolean(abstractButton, basicButtonUI, basicButtonUI.getPropertyPrefix() + "defaultButtonFollowsFocus", true)) {
          jRootPane.putClientProperty("temporaryDefaultButton", abstractButton);
          jRootPane.setDefaultButton((JButton)abstractButton);
          jRootPane.putClientProperty("temporaryDefaultButton", null);
        } 
      } 
    } 
    abstractButton.repaint();
  }
  
  public void focusLost(FocusEvent paramFocusEvent) {
    AbstractButton abstractButton = (AbstractButton)paramFocusEvent.getSource();
    JRootPane jRootPane = abstractButton.getRootPane();
    if (jRootPane != null) {
      JButton jButton = (JButton)jRootPane.getClientProperty("initialDefaultButton");
      if (abstractButton != jButton) {
        BasicButtonUI basicButtonUI = (BasicButtonUI)BasicLookAndFeel.getUIOfType(abstractButton.getUI(), BasicButtonUI.class);
        if (basicButtonUI != null && DefaultLookup.getBoolean(abstractButton, basicButtonUI, basicButtonUI.getPropertyPrefix() + "defaultButtonFollowsFocus", true))
          jRootPane.setDefaultButton(jButton); 
      } 
    } 
    ButtonModel buttonModel = abstractButton.getModel();
    buttonModel.setPressed(false);
    buttonModel.setArmed(false);
    abstractButton.repaint();
  }
  
  public void mouseMoved(MouseEvent paramMouseEvent) {}
  
  public void mouseDragged(MouseEvent paramMouseEvent) {}
  
  public void mouseClicked(MouseEvent paramMouseEvent) {}
  
  public void mousePressed(MouseEvent paramMouseEvent) {
    if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
      AbstractButton abstractButton = (AbstractButton)paramMouseEvent.getSource();
      if (abstractButton.contains(paramMouseEvent.getX(), paramMouseEvent.getY())) {
        long l1 = abstractButton.getMultiClickThreshhold();
        long l2 = this.lastPressedTimestamp;
        long l3 = this.lastPressedTimestamp = paramMouseEvent.getWhen();
        if (l2 != -1L && l3 - l2 < l1) {
          this.shouldDiscardRelease = true;
          return;
        } 
        ButtonModel buttonModel = abstractButton.getModel();
        if (!buttonModel.isEnabled())
          return; 
        if (!buttonModel.isArmed())
          buttonModel.setArmed(true); 
        buttonModel.setPressed(true);
        if (!abstractButton.hasFocus() && abstractButton.isRequestFocusEnabled())
          abstractButton.requestFocus(); 
      } 
    } 
  }
  
  public void mouseReleased(MouseEvent paramMouseEvent) {
    if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
      if (this.shouldDiscardRelease) {
        this.shouldDiscardRelease = false;
        return;
      } 
      AbstractButton abstractButton = (AbstractButton)paramMouseEvent.getSource();
      ButtonModel buttonModel = abstractButton.getModel();
      buttonModel.setPressed(false);
      buttonModel.setArmed(false);
    } 
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent) {
    AbstractButton abstractButton = (AbstractButton)paramMouseEvent.getSource();
    ButtonModel buttonModel = abstractButton.getModel();
    if (abstractButton.isRolloverEnabled() && !SwingUtilities.isLeftMouseButton(paramMouseEvent))
      buttonModel.setRollover(true); 
    if (buttonModel.isPressed())
      buttonModel.setArmed(true); 
  }
  
  public void mouseExited(MouseEvent paramMouseEvent) {
    AbstractButton abstractButton = (AbstractButton)paramMouseEvent.getSource();
    ButtonModel buttonModel = abstractButton.getModel();
    if (abstractButton.isRolloverEnabled())
      buttonModel.setRollover(false); 
    buttonModel.setArmed(false);
  }
  
  private static class Actions extends UIAction {
    private static final String PRESS = "pressed";
    
    private static final String RELEASE = "released";
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      AbstractButton abstractButton = (AbstractButton)param1ActionEvent.getSource();
      String str = getName();
      if (str == "pressed") {
        ButtonModel buttonModel = abstractButton.getModel();
        buttonModel.setArmed(true);
        buttonModel.setPressed(true);
        if (!abstractButton.hasFocus())
          abstractButton.requestFocus(); 
      } else if (str == "released") {
        ButtonModel buttonModel = abstractButton.getModel();
        buttonModel.setPressed(false);
        buttonModel.setArmed(false);
      } 
    }
    
    public boolean isEnabled(Object param1Object) { return !(param1Object != null && param1Object instanceof AbstractButton && !((AbstractButton)param1Object).getModel().isEnabled()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */