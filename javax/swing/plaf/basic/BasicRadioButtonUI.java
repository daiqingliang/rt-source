package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import java.util.HashSet;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class BasicRadioButtonUI extends BasicToggleButtonUI {
  private static final Object BASIC_RADIO_BUTTON_UI_KEY = new Object();
  
  protected Icon icon;
  
  private boolean defaults_initialized = false;
  
  private static final String propertyPrefix = "RadioButton.";
  
  private KeyListener keyListener = null;
  
  private static Dimension size = new Dimension();
  
  private static Rectangle viewRect = new Rectangle();
  
  private static Rectangle iconRect = new Rectangle();
  
  private static Rectangle textRect = new Rectangle();
  
  private static Rectangle prefViewRect = new Rectangle();
  
  private static Rectangle prefIconRect = new Rectangle();
  
  private static Rectangle prefTextRect = new Rectangle();
  
  private static Insets prefInsets = new Insets(0, 0, 0, 0);
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    BasicRadioButtonUI basicRadioButtonUI = (BasicRadioButtonUI)appContext.get(BASIC_RADIO_BUTTON_UI_KEY);
    if (basicRadioButtonUI == null) {
      basicRadioButtonUI = new BasicRadioButtonUI();
      appContext.put(BASIC_RADIO_BUTTON_UI_KEY, basicRadioButtonUI);
    } 
    return basicRadioButtonUI;
  }
  
  protected String getPropertyPrefix() { return "RadioButton."; }
  
  protected void installDefaults(AbstractButton paramAbstractButton) {
    super.installDefaults(paramAbstractButton);
    if (!this.defaults_initialized) {
      this.icon = UIManager.getIcon(getPropertyPrefix() + "icon");
      this.defaults_initialized = true;
    } 
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton) {
    super.uninstallDefaults(paramAbstractButton);
    this.defaults_initialized = false;
  }
  
  public Icon getDefaultIcon() { return this.icon; }
  
  protected void installListeners(AbstractButton paramAbstractButton) {
    super.installListeners(paramAbstractButton);
    if (!(paramAbstractButton instanceof JRadioButton))
      return; 
    this.keyListener = createKeyListener();
    paramAbstractButton.addKeyListener(this.keyListener);
    paramAbstractButton.setFocusTraversalKeysEnabled(false);
    paramAbstractButton.getActionMap().put("Previous", new SelectPreviousBtn());
    paramAbstractButton.getActionMap().put("Next", new SelectNextBtn());
    paramAbstractButton.getInputMap(1).put(KeyStroke.getKeyStroke("UP"), "Previous");
    paramAbstractButton.getInputMap(1).put(KeyStroke.getKeyStroke("DOWN"), "Next");
    paramAbstractButton.getInputMap(1).put(KeyStroke.getKeyStroke("LEFT"), "Previous");
    paramAbstractButton.getInputMap(1).put(KeyStroke.getKeyStroke("RIGHT"), "Next");
  }
  
  protected void uninstallListeners(AbstractButton paramAbstractButton) {
    super.uninstallListeners(paramAbstractButton);
    if (!(paramAbstractButton instanceof JRadioButton))
      return; 
    paramAbstractButton.getActionMap().remove("Previous");
    paramAbstractButton.getActionMap().remove("Next");
    paramAbstractButton.getInputMap(1).remove(KeyStroke.getKeyStroke("UP"));
    paramAbstractButton.getInputMap(1).remove(KeyStroke.getKeyStroke("DOWN"));
    paramAbstractButton.getInputMap(1).remove(KeyStroke.getKeyStroke("LEFT"));
    paramAbstractButton.getInputMap(1).remove(KeyStroke.getKeyStroke("RIGHT"));
    if (this.keyListener != null) {
      paramAbstractButton.removeKeyListener(this.keyListener);
      this.keyListener = null;
    } 
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    ButtonModel buttonModel = abstractButton.getModel();
    Font font = paramJComponent.getFont();
    paramGraphics.setFont(font);
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, font);
    Insets insets = paramJComponent.getInsets();
    size = abstractButton.getSize(size);
    viewRect.x = insets.left;
    viewRect.y = insets.top;
    viewRect.width = size.width - insets.right + viewRect.x;
    viewRect.height = size.height - insets.bottom + viewRect.y;
    iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
    textRect.x = textRect.y = textRect.width = textRect.height = 0;
    Icon icon1 = abstractButton.getIcon();
    Object object1 = null;
    Object object2 = null;
    String str = SwingUtilities.layoutCompoundLabel(paramJComponent, fontMetrics, abstractButton.getText(), (icon1 != null) ? icon1 : getDefaultIcon(), abstractButton.getVerticalAlignment(), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalTextPosition(), abstractButton.getHorizontalTextPosition(), viewRect, iconRect, textRect, (abstractButton.getText() == null) ? 0 : abstractButton.getIconTextGap());
    if (paramJComponent.isOpaque()) {
      paramGraphics.setColor(abstractButton.getBackground());
      paramGraphics.fillRect(0, 0, size.width, size.height);
    } 
    if (icon1 != null) {
      if (!buttonModel.isEnabled()) {
        if (buttonModel.isSelected()) {
          icon1 = abstractButton.getDisabledSelectedIcon();
        } else {
          icon1 = abstractButton.getDisabledIcon();
        } 
      } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
        icon1 = abstractButton.getPressedIcon();
        if (icon1 == null)
          icon1 = abstractButton.getSelectedIcon(); 
      } else if (buttonModel.isSelected()) {
        if (abstractButton.isRolloverEnabled() && buttonModel.isRollover()) {
          icon1 = abstractButton.getRolloverSelectedIcon();
          if (icon1 == null)
            icon1 = abstractButton.getSelectedIcon(); 
        } else {
          icon1 = abstractButton.getSelectedIcon();
        } 
      } else if (abstractButton.isRolloverEnabled() && buttonModel.isRollover()) {
        icon1 = abstractButton.getRolloverIcon();
      } 
      if (icon1 == null)
        icon1 = abstractButton.getIcon(); 
      icon1.paintIcon(paramJComponent, paramGraphics, iconRect.x, iconRect.y);
    } else {
      getDefaultIcon().paintIcon(paramJComponent, paramGraphics, iconRect.x, iconRect.y);
    } 
    if (str != null) {
      View view = (View)paramJComponent.getClientProperty("html");
      if (view != null) {
        view.paint(paramGraphics, textRect);
      } else {
        paintText(paramGraphics, abstractButton, textRect, str);
      } 
      if (abstractButton.hasFocus() && abstractButton.isFocusPainted() && textRect.width > 0 && textRect.height > 0)
        paintFocus(paramGraphics, textRect, size); 
    } 
  }
  
  protected void paintFocus(Graphics paramGraphics, Rectangle paramRectangle, Dimension paramDimension) {}
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    if (paramJComponent.getComponentCount() > 0)
      return null; 
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    String str = abstractButton.getText();
    Icon icon1 = abstractButton.getIcon();
    if (icon1 == null)
      icon1 = getDefaultIcon(); 
    Font font = abstractButton.getFont();
    FontMetrics fontMetrics = abstractButton.getFontMetrics(font);
    prefViewRect.x = prefViewRect.y = 0;
    prefViewRect.width = 32767;
    prefViewRect.height = 32767;
    prefIconRect.x = prefIconRect.y = prefIconRect.width = prefIconRect.height = 0;
    prefTextRect.x = prefTextRect.y = prefTextRect.width = prefTextRect.height = 0;
    SwingUtilities.layoutCompoundLabel(paramJComponent, fontMetrics, str, icon1, abstractButton.getVerticalAlignment(), abstractButton.getHorizontalAlignment(), abstractButton.getVerticalTextPosition(), abstractButton.getHorizontalTextPosition(), prefViewRect, prefIconRect, prefTextRect, (str == null) ? 0 : abstractButton.getIconTextGap());
    int i = Math.min(prefIconRect.x, prefTextRect.x);
    int j = Math.max(prefIconRect.x + prefIconRect.width, prefTextRect.x + prefTextRect.width);
    int k = Math.min(prefIconRect.y, prefTextRect.y);
    int m = Math.max(prefIconRect.y + prefIconRect.height, prefTextRect.y + prefTextRect.height);
    int n = j - i;
    int i1 = m - k;
    prefInsets = abstractButton.getInsets(prefInsets);
    n += prefInsets.left + prefInsets.right;
    i1 += prefInsets.top + prefInsets.bottom;
    return new Dimension(n, i1);
  }
  
  private KeyListener createKeyListener() {
    if (this.keyListener == null)
      this.keyListener = new KeyHandler(null); 
    return this.keyListener;
  }
  
  private boolean isValidRadioButtonObj(Object paramObject) { return (paramObject instanceof JRadioButton && ((JRadioButton)paramObject).isVisible() && ((JRadioButton)paramObject).isEnabled()); }
  
  private void selectRadioButton(ActionEvent paramActionEvent, boolean paramBoolean) {
    Object object = paramActionEvent.getSource();
    if (!isValidRadioButtonObj(object))
      return; 
    ButtonGroupInfo buttonGroupInfo = new ButtonGroupInfo((JRadioButton)object);
    buttonGroupInfo.selectNewButton(paramBoolean);
  }
  
  private class ButtonGroupInfo {
    JRadioButton activeBtn = null;
    
    JRadioButton firstBtn = null;
    
    JRadioButton lastBtn = null;
    
    JRadioButton previousBtn = null;
    
    JRadioButton nextBtn = null;
    
    HashSet<JRadioButton> btnsInGroup = null;
    
    boolean srcFound = false;
    
    public ButtonGroupInfo(JRadioButton param1JRadioButton) {
      this.activeBtn = param1JRadioButton;
      this.btnsInGroup = new HashSet();
    }
    
    boolean containsInGroup(Object param1Object) { return this.btnsInGroup.contains(param1Object); }
    
    Component getFocusTransferBaseComponent(boolean param1Boolean) {
      JRadioButton jRadioButton = this.activeBtn;
      Container container = jRadioButton.getFocusCycleRootAncestor();
      if (container != null) {
        FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
        Component component = param1Boolean ? focusTraversalPolicy.getComponentAfter(container, this.activeBtn) : focusTraversalPolicy.getComponentBefore(container, this.activeBtn);
        if (containsInGroup(component))
          jRadioButton = param1Boolean ? this.lastBtn : this.firstBtn; 
      } 
      return jRadioButton;
    }
    
    boolean getButtonGroupInfo() {
      if (this.activeBtn == null)
        return false; 
      this.btnsInGroup.clear();
      ButtonModel buttonModel = this.activeBtn.getModel();
      if (!(buttonModel instanceof DefaultButtonModel))
        return false; 
      DefaultButtonModel defaultButtonModel = (DefaultButtonModel)buttonModel;
      ButtonGroup buttonGroup = defaultButtonModel.getGroup();
      if (buttonGroup == null)
        return false; 
      Enumeration enumeration = buttonGroup.getElements();
      if (enumeration == null)
        return false; 
      while (enumeration.hasMoreElements()) {
        AbstractButton abstractButton = (AbstractButton)enumeration.nextElement();
        if (!BasicRadioButtonUI.this.isValidRadioButtonObj(abstractButton))
          continue; 
        this.btnsInGroup.add((JRadioButton)abstractButton);
        if (null == this.firstBtn)
          this.firstBtn = (JRadioButton)abstractButton; 
        if (this.activeBtn == abstractButton) {
          this.srcFound = true;
        } else if (!this.srcFound) {
          this.previousBtn = (JRadioButton)abstractButton;
        } else if (this.nextBtn == null) {
          this.nextBtn = (JRadioButton)abstractButton;
        } 
        this.lastBtn = (JRadioButton)abstractButton;
      } 
      return true;
    }
    
    void selectNewButton(boolean param1Boolean) {
      if (!getButtonGroupInfo())
        return; 
      if (this.srcFound) {
        JRadioButton jRadioButton = null;
        if (param1Boolean) {
          jRadioButton = (null == this.nextBtn) ? this.firstBtn : this.nextBtn;
        } else {
          jRadioButton = (null == this.previousBtn) ? this.lastBtn : this.previousBtn;
        } 
        if (jRadioButton != null && jRadioButton != this.activeBtn) {
          jRadioButton.requestFocusInWindow();
          jRadioButton.setSelected(true);
        } 
      } 
    }
    
    void jumpToNextComponent(boolean param1Boolean) {
      if (!getButtonGroupInfo())
        if (this.activeBtn != null) {
          this.lastBtn = this.activeBtn;
          this.firstBtn = this.activeBtn;
        } else {
          return;
        }  
      JRadioButton jRadioButton = this.activeBtn;
      Component component = getFocusTransferBaseComponent(param1Boolean);
      if (component != null)
        if (param1Boolean) {
          KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(component);
        } else {
          KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(component);
        }  
    }
  }
  
  private class KeyHandler implements KeyListener {
    private KeyHandler() {}
    
    public void keyPressed(KeyEvent param1KeyEvent) {
      if (param1KeyEvent.getKeyCode() == 9) {
        Object object = param1KeyEvent.getSource();
        if (BasicRadioButtonUI.this.isValidRadioButtonObj(object)) {
          param1KeyEvent.consume();
          BasicRadioButtonUI.ButtonGroupInfo buttonGroupInfo = new BasicRadioButtonUI.ButtonGroupInfo(BasicRadioButtonUI.this, (JRadioButton)object);
          buttonGroupInfo.jumpToNextComponent(!param1KeyEvent.isShiftDown());
        } 
      } 
    }
    
    public void keyReleased(KeyEvent param1KeyEvent) {}
    
    public void keyTyped(KeyEvent param1KeyEvent) {}
  }
  
  private class SelectNextBtn extends AbstractAction {
    public SelectNextBtn() { super("Next"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) { BasicRadioButtonUI.this.selectRadioButton(param1ActionEvent, true); }
  }
  
  private class SelectPreviousBtn extends AbstractAction {
    public SelectPreviousBtn() { super("Previous"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) { BasicRadioButtonUI.this.selectRadioButton(param1ActionEvent, false); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicRadioButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */