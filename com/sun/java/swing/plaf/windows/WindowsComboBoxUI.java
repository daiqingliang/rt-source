package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import sun.swing.DefaultLookup;
import sun.swing.StringUIClientPropertyKey;

public class WindowsComboBoxUI extends BasicComboBoxUI {
  private static final MouseListener rolloverListener = new MouseAdapter() {
      private void handleRollover(MouseEvent param1MouseEvent, boolean param1Boolean) {
        JComboBox jComboBox = getComboBox(param1MouseEvent);
        WindowsComboBoxUI windowsComboBoxUI = getWindowsComboBoxUI(param1MouseEvent);
        if (jComboBox == null || windowsComboBoxUI == null)
          return; 
        if (!jComboBox.isEditable()) {
          ButtonModel buttonModel = null;
          if (windowsComboBoxUI.arrowButton != null)
            buttonModel = windowsComboBoxUI.arrowButton.getModel(); 
          if (buttonModel != null)
            buttonModel.setRollover(param1Boolean); 
        } 
        windowsComboBoxUI.isRollover = param1Boolean;
        jComboBox.repaint();
      }
      
      public void mouseEntered(MouseEvent param1MouseEvent) { handleRollover(param1MouseEvent, true); }
      
      public void mouseExited(MouseEvent param1MouseEvent) { handleRollover(param1MouseEvent, false); }
      
      private JComboBox getComboBox(MouseEvent param1MouseEvent) {
        Object object = param1MouseEvent.getSource();
        JComboBox jComboBox = null;
        if (object instanceof JComboBox) {
          jComboBox = (JComboBox)object;
        } else if (object instanceof WindowsComboBoxUI.XPComboBoxButton) {
          jComboBox = (((WindowsComboBoxUI.XPComboBoxButton)object).getWindowsComboBoxUI()).comboBox;
        } 
        return jComboBox;
      }
      
      private WindowsComboBoxUI getWindowsComboBoxUI(MouseEvent param1MouseEvent) {
        JComboBox jComboBox = getComboBox(param1MouseEvent);
        WindowsComboBoxUI windowsComboBoxUI = null;
        if (jComboBox != null && jComboBox.getUI() instanceof WindowsComboBoxUI)
          windowsComboBoxUI = (WindowsComboBoxUI)jComboBox.getUI(); 
        return windowsComboBoxUI;
      }
    };
  
  private boolean isRollover = false;
  
  private static final PropertyChangeListener componentOrientationListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
        String str = param1PropertyChangeEvent.getPropertyName();
        Object object = null;
        if ("componentOrientation" == str && object = param1PropertyChangeEvent.getSource() instanceof JComboBox && ((JComboBox)object).getUI() instanceof WindowsComboBoxUI) {
          JComboBox jComboBox = (JComboBox)object;
          WindowsComboBoxUI windowsComboBoxUI;
          if (windowsComboBoxUI.arrowButton instanceof WindowsComboBoxUI.XPComboBoxButton)
            ((WindowsComboBoxUI.XPComboBoxButton)windowsComboBoxUI.arrowButton).setPart((jComboBox.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT) ? TMSchema.Part.CP_DROPDOWNBUTTONLEFT : TMSchema.Part.CP_DROPDOWNBUTTONRIGHT); 
        } 
      }
    };
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsComboBoxUI(); }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    this.isRollover = false;
    this.comboBox.setRequestFocusEnabled(true);
    if (XPStyle.getXP() != null && this.arrowButton != null) {
      this.comboBox.addMouseListener(rolloverListener);
      this.arrowButton.addMouseListener(rolloverListener);
    } 
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    this.comboBox.removeMouseListener(rolloverListener);
    if (this.arrowButton != null)
      this.arrowButton.removeMouseListener(rolloverListener); 
    super.uninstallUI(paramJComponent);
  }
  
  protected void installListeners() {
    super.installListeners();
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null && xPStyle.isSkinDefined(this.comboBox, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT))
      this.comboBox.addPropertyChangeListener("componentOrientation", componentOrientationListener); 
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.comboBox.removePropertyChangeListener("componentOrientation", componentOrientationListener);
  }
  
  protected void configureEditor() {
    super.configureEditor();
    if (XPStyle.getXP() != null)
      this.editor.addMouseListener(rolloverListener); 
  }
  
  protected void unconfigureEditor() {
    super.unconfigureEditor();
    this.editor.removeMouseListener(rolloverListener);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    if (XPStyle.getXP() != null)
      paintXPComboBoxBackground(paramGraphics, paramJComponent); 
    super.paint(paramGraphics, paramJComponent);
  }
  
  TMSchema.State getXPComboBoxState(JComponent paramJComponent) {
    TMSchema.State state = TMSchema.State.NORMAL;
    if (!paramJComponent.isEnabled()) {
      state = TMSchema.State.DISABLED;
    } else if (isPopupVisible(this.comboBox)) {
      state = TMSchema.State.PRESSED;
    } else if (this.isRollover) {
      state = TMSchema.State.HOT;
    } 
    return state;
  }
  
  private void paintXPComboBoxBackground(Graphics paramGraphics, JComponent paramJComponent) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle == null)
      return; 
    TMSchema.State state = getXPComboBoxState(paramJComponent);
    XPStyle.Skin skin = null;
    if (!this.comboBox.isEditable() && xPStyle.isSkinDefined(paramJComponent, TMSchema.Part.CP_READONLY))
      skin = xPStyle.getSkin(paramJComponent, TMSchema.Part.CP_READONLY); 
    if (skin == null)
      skin = xPStyle.getSkin(paramJComponent, TMSchema.Part.CP_COMBOBOX); 
    skin.paintSkin(paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), state);
  }
  
  public void paintCurrentValue(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      paramRectangle.x += 2;
      paramRectangle.y += 2;
      paramRectangle.width -= 4;
      paramRectangle.height -= 4;
    } else {
      paramRectangle.x++;
      paramRectangle.y++;
      paramRectangle.width -= 2;
      paramRectangle.height -= 2;
    } 
    if (!this.comboBox.isEditable() && xPStyle != null && xPStyle.isSkinDefined(this.comboBox, TMSchema.Part.CP_READONLY)) {
      Component component;
      ListCellRenderer listCellRenderer = this.comboBox.getRenderer();
      if (paramBoolean && !isPopupVisible(this.comboBox)) {
        component = listCellRenderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, true, false);
      } else {
        component = listCellRenderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
      } 
      component.setFont(this.comboBox.getFont());
      if (this.comboBox.isEnabled()) {
        component.setForeground(this.comboBox.getForeground());
        component.setBackground(this.comboBox.getBackground());
      } else {
        component.setForeground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledForeground", null));
        component.setBackground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledBackground", null));
      } 
      boolean bool = false;
      if (component instanceof javax.swing.JPanel)
        bool = true; 
      this.currentValuePane.paintComponent(paramGraphics, component, this.comboBox, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, bool);
    } else {
      super.paintCurrentValue(paramGraphics, paramRectangle, paramBoolean);
    } 
  }
  
  public void paintCurrentValueBackground(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean) {
    if (XPStyle.getXP() == null)
      super.paintCurrentValueBackground(paramGraphics, paramRectangle, paramBoolean); 
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    Dimension dimension = super.getMinimumSize(paramJComponent);
    if (XPStyle.getXP() != null) {
      dimension.width += 5;
    } else {
      dimension.width += 4;
    } 
    dimension.height += 2;
    return dimension;
  }
  
  protected LayoutManager createLayoutManager() { return new BasicComboBoxUI.ComboBoxLayoutManager() {
        public void layoutContainer(Container param1Container) {
          super.layoutContainer(param1Container);
          if (XPStyle.getXP() != null && WindowsComboBoxUI.this.arrowButton != null) {
            Dimension dimension = param1Container.getSize();
            Insets insets = WindowsComboBoxUI.this.getInsets();
            int i = (this.this$0.arrowButton.getPreferredSize()).width;
            WindowsComboBoxUI.this.arrowButton.setBounds(WindowsGraphicsUtils.isLeftToRight((JComboBox)param1Container) ? (dimension.width - insets.right - i) : insets.left, insets.top, i, dimension.height - insets.top - insets.bottom);
          } 
        }
      }; }
  
  protected void installKeyboardActions() { super.installKeyboardActions(); }
  
  protected ComboPopup createPopup() { return super.createPopup(); }
  
  protected ComboBoxEditor createEditor() { return new WindowsComboBoxEditor(); }
  
  protected ListCellRenderer createRenderer() {
    XPStyle xPStyle = XPStyle.getXP();
    return (xPStyle != null && xPStyle.isSkinDefined(this.comboBox, TMSchema.Part.CP_READONLY)) ? new WindowsComboBoxRenderer(null) : super.createRenderer();
  }
  
  protected JButton createArrowButton() {
    XPStyle xPStyle = XPStyle.getXP();
    return (xPStyle != null) ? new XPComboBoxButton(this, xPStyle) : super.createArrowButton();
  }
  
  public static class WindowsComboBoxEditor extends BasicComboBoxEditor.UIResource {
    protected JTextField createEditorComponent() {
      JTextField jTextField = super.createEditorComponent();
      Border border = (Border)UIManager.get("ComboBox.editorBorder");
      if (border != null)
        jTextField.setBorder(border); 
      jTextField.setOpaque(false);
      return jTextField;
    }
    
    public void setItem(Object param1Object) {
      super.setItem(param1Object);
      Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      if (component == this.editor || component == this.editor.getParent())
        this.editor.selectAll(); 
    }
  }
  
  private static class WindowsComboBoxRenderer extends BasicComboBoxRenderer.UIResource {
    private static final Object BORDER_KEY = new StringUIClientPropertyKey("BORDER_KEY");
    
    private static final Border NULL_BORDER = new EmptyBorder(0, 0, 0, 0);
    
    private WindowsComboBoxRenderer() {}
    
    public Component getListCellRendererComponent(JList param1JList, Object param1Object, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      Component component = super.getListCellRendererComponent(param1JList, param1Object, param1Int, param1Boolean1, param1Boolean2);
      if (component instanceof JComponent) {
        JComponent jComponent = (JComponent)component;
        if (param1Int == -1 && param1Boolean1) {
          Border border = jComponent.getBorder();
          WindowsBorders.DashedBorder dashedBorder = new WindowsBorders.DashedBorder(param1JList.getForeground());
          jComponent.setBorder(dashedBorder);
          if (jComponent.getClientProperty(BORDER_KEY) == null)
            jComponent.putClientProperty(BORDER_KEY, (border == null) ? NULL_BORDER : border); 
        } else if (jComponent.getBorder() instanceof WindowsBorders.DashedBorder) {
          Object object = jComponent.getClientProperty(BORDER_KEY);
          if (object instanceof Border)
            jComponent.setBorder((object == NULL_BORDER) ? null : (Border)object); 
          jComponent.putClientProperty(BORDER_KEY, null);
        } 
        if (param1Int == -1) {
          jComponent.setOpaque(false);
          jComponent.setForeground(param1JList.getForeground());
        } else {
          jComponent.setOpaque(true);
        } 
      } 
      return component;
    }
  }
  
  @Deprecated
  protected class WindowsComboPopup extends BasicComboPopup {
    public WindowsComboPopup(JComboBox param1JComboBox) { super(param1JComboBox); }
    
    protected KeyListener createKeyListener() { return new InvocationKeyHandler(); }
    
    protected class InvocationKeyHandler extends BasicComboPopup.InvocationKeyHandler {
      protected InvocationKeyHandler() { super(WindowsComboBoxUI.WindowsComboPopup.this); }
    }
  }
  
  private class XPComboBoxButton extends XPStyle.GlyphButton {
    public XPComboBoxButton(WindowsComboBoxUI this$0, XPStyle param1XPStyle) {
      super(null, !param1XPStyle.isSkinDefined(this$0.comboBox, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT) ? TMSchema.Part.CP_DROPDOWNBUTTON : ((this$0.comboBox.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT) ? TMSchema.Part.CP_DROPDOWNBUTTONLEFT : TMSchema.Part.CP_DROPDOWNBUTTONRIGHT));
      setRequestFocusEnabled(false);
    }
    
    protected TMSchema.State getState() {
      TMSchema.State state = super.getState();
      XPStyle xPStyle = XPStyle.getXP();
      if (state != TMSchema.State.DISABLED && this.this$0.comboBox != null && !this.this$0.comboBox.isEditable() && xPStyle != null && xPStyle.isSkinDefined(this.this$0.comboBox, TMSchema.Part.CP_DROPDOWNBUTTONRIGHT))
        state = TMSchema.State.NORMAL; 
      return state;
    }
    
    public Dimension getPreferredSize() { return new Dimension(17, 21); }
    
    void setPart(TMSchema.Part param1Part) { setPart(this.this$0.comboBox, param1Part); }
    
    WindowsComboBoxUI getWindowsComboBoxUI() { return this.this$0; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsComboBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */