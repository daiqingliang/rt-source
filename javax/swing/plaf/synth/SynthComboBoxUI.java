package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

public class SynthComboBoxUI extends BasicComboBoxUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  private boolean useListColors;
  
  Insets popupInsets;
  
  private boolean buttonWhenNotEditable;
  
  private boolean pressedWhenPopupVisible;
  
  private ButtonHandler buttonHandler;
  
  private EditorFocusHandler editorFocusHandler;
  
  private boolean forceOpaque = false;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthComboBoxUI(); }
  
  public void installUI(JComponent paramJComponent) {
    this.buttonHandler = new ButtonHandler(null);
    super.installUI(paramJComponent);
  }
  
  protected void installDefaults() { updateStyle(this.comboBox); }
  
  private void updateStyle(JComboBox paramJComboBox) {
    SynthStyle synthStyle = this.style;
    SynthContext synthContext = getContext(paramJComboBox, 1);
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      this.padding = (Insets)this.style.get(synthContext, "ComboBox.padding");
      this.popupInsets = (Insets)this.style.get(synthContext, "ComboBox.popupInsets");
      this.useListColors = this.style.getBoolean(synthContext, "ComboBox.rendererUseListColors", true);
      this.buttonWhenNotEditable = this.style.getBoolean(synthContext, "ComboBox.buttonWhenNotEditable", false);
      this.pressedWhenPopupVisible = this.style.getBoolean(synthContext, "ComboBox.pressedWhenPopupVisible", false);
      this.squareButton = this.style.getBoolean(synthContext, "ComboBox.squareButton", true);
      if (synthStyle != null) {
        uninstallKeyboardActions();
        installKeyboardActions();
      } 
      this.forceOpaque = this.style.getBoolean(synthContext, "ComboBox.forceOpaque", false);
    } 
    synthContext.dispose();
    if (this.listBox != null)
      SynthLookAndFeel.updateStyles(this.listBox); 
  }
  
  protected void installListeners() {
    this.comboBox.addPropertyChangeListener(this);
    this.comboBox.addMouseListener(this.buttonHandler);
    this.editorFocusHandler = new EditorFocusHandler(this.comboBox, null);
    super.installListeners();
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    if (this.popup instanceof SynthComboPopup)
      ((SynthComboPopup)this.popup).removePopupMenuListener(this.buttonHandler); 
    super.uninstallUI(paramJComponent);
    this.buttonHandler = null;
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this.comboBox, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  protected void uninstallListeners() {
    this.editorFocusHandler.unregister();
    this.comboBox.removePropertyChangeListener(this);
    this.comboBox.removeMouseListener(this.buttonHandler);
    this.buttonHandler.pressed = false;
    this.buttonHandler.over = false;
    super.uninstallListeners();
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) {
    if (!(paramJComponent instanceof JComboBox))
      return SynthLookAndFeel.getComponentState(paramJComponent); 
    JComboBox jComboBox = (JComboBox)paramJComponent;
    if (shouldActLikeButton()) {
      char c = '\001';
      if (!paramJComponent.isEnabled())
        c = '\b'; 
      if (this.buttonHandler.isPressed())
        c |= 0x4; 
      if (this.buttonHandler.isRollover())
        c |= 0x2; 
      if (jComboBox.isFocusOwner())
        c |= 0x100; 
      return c;
    } 
    int i = SynthLookAndFeel.getComponentState(paramJComponent);
    if (jComboBox.isEditable() && jComboBox.getEditor().getEditorComponent().isFocusOwner())
      i |= 0x100; 
    return i;
  }
  
  protected ComboPopup createPopup() {
    SynthComboPopup synthComboPopup = new SynthComboPopup(this.comboBox);
    synthComboPopup.addPopupMenuListener(this.buttonHandler);
    return synthComboPopup;
  }
  
  protected ListCellRenderer createRenderer() { return new SynthComboBoxRenderer(); }
  
  protected ComboBoxEditor createEditor() { return new SynthComboBoxEditor(null); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle(this.comboBox); 
  }
  
  protected JButton createArrowButton() {
    SynthArrowButton synthArrowButton = new SynthArrowButton(5);
    synthArrowButton.setName("ComboBox.arrowButton");
    synthArrowButton.setModel(this.buttonHandler);
    return synthArrowButton;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintComboBoxBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    this.hasFocus = this.comboBox.hasFocus();
    if (!this.comboBox.isEditable()) {
      Rectangle rectangle = rectangleForCurrentValue();
      paintCurrentValue(paramGraphics, rectangle, this.hasFocus);
    } 
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintComboBoxBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void paintCurrentValue(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean) {
    ListCellRenderer listCellRenderer = this.comboBox.getRenderer();
    Component component = listCellRenderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
    boolean bool = false;
    if (component instanceof javax.swing.JPanel)
      bool = true; 
    if (component instanceof UIResource)
      component.setName("ComboBox.renderer"); 
    boolean bool1 = (this.forceOpaque && component instanceof JComponent) ? 1 : 0;
    if (bool1)
      ((JComponent)component).setOpaque(false); 
    int i = paramRectangle.x;
    int j = paramRectangle.y;
    int k = paramRectangle.width;
    int m = paramRectangle.height;
    if (this.padding != null) {
      i = paramRectangle.x + this.padding.left;
      j = paramRectangle.y + this.padding.top;
      k = paramRectangle.width - this.padding.left + this.padding.right;
      m = paramRectangle.height - this.padding.top + this.padding.bottom;
    } 
    this.currentValuePane.paintComponent(paramGraphics, component, this.comboBox, i, j, k, m, bool);
    if (bool1)
      ((JComponent)component).setOpaque(true); 
  }
  
  private boolean shouldActLikeButton() { return (this.buttonWhenNotEditable && !this.comboBox.isEditable()); }
  
  protected Dimension getDefaultSize() {
    SynthComboBoxRenderer synthComboBoxRenderer = new SynthComboBoxRenderer();
    Dimension dimension = getSizeForComponent(synthComboBoxRenderer.getListCellRendererComponent(this.listBox, " ", -1, false, false));
    return new Dimension(dimension.width, dimension.height);
  }
  
  private final class ButtonHandler extends DefaultButtonModel implements MouseListener, PopupMenuListener {
    private boolean over;
    
    private boolean pressed;
    
    private ButtonHandler() {}
    
    private void updatePressed(boolean param1Boolean) {
      this.pressed = (param1Boolean && isEnabled());
      if (SynthComboBoxUI.this.shouldActLikeButton())
        SynthComboBoxUI.this.comboBox.repaint(); 
    }
    
    private void updateOver(boolean param1Boolean) {
      boolean bool1 = isRollover();
      this.over = (param1Boolean && isEnabled());
      boolean bool2 = isRollover();
      if (SynthComboBoxUI.this.shouldActLikeButton() && bool1 != bool2)
        SynthComboBoxUI.this.comboBox.repaint(); 
    }
    
    public boolean isPressed() {
      boolean bool = SynthComboBoxUI.this.shouldActLikeButton() ? this.pressed : super.isPressed();
      return (bool || (SynthComboBoxUI.this.pressedWhenPopupVisible && SynthComboBoxUI.this.comboBox.isPopupVisible()));
    }
    
    public boolean isArmed() {
      boolean bool = (SynthComboBoxUI.this.shouldActLikeButton() || (SynthComboBoxUI.this.pressedWhenPopupVisible && SynthComboBoxUI.this.comboBox.isPopupVisible())) ? 1 : 0;
      return bool ? isPressed() : super.isArmed();
    }
    
    public boolean isRollover() { return SynthComboBoxUI.this.shouldActLikeButton() ? this.over : super.isRollover(); }
    
    public void setPressed(boolean param1Boolean) {
      super.setPressed(param1Boolean);
      updatePressed(param1Boolean);
    }
    
    public void setRollover(boolean param1Boolean) {
      super.setRollover(param1Boolean);
      updateOver(param1Boolean);
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) { updateOver(true); }
    
    public void mouseExited(MouseEvent param1MouseEvent) { updateOver(false); }
    
    public void mousePressed(MouseEvent param1MouseEvent) { updatePressed(true); }
    
    public void mouseReleased(MouseEvent param1MouseEvent) { updatePressed(false); }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void popupMenuCanceled(PopupMenuEvent param1PopupMenuEvent) {
      if (SynthComboBoxUI.this.shouldActLikeButton() || SynthComboBoxUI.this.pressedWhenPopupVisible)
        SynthComboBoxUI.this.comboBox.repaint(); 
    }
    
    public void popupMenuWillBecomeVisible(PopupMenuEvent param1PopupMenuEvent) {}
    
    public void popupMenuWillBecomeInvisible(PopupMenuEvent param1PopupMenuEvent) {}
  }
  
  private static class EditorFocusHandler implements FocusListener, PropertyChangeListener {
    private JComboBox comboBox;
    
    private ComboBoxEditor editor = null;
    
    private Component editorComponent = null;
    
    private EditorFocusHandler(JComboBox param1JComboBox) {
      this.comboBox = param1JComboBox;
      this.editor = param1JComboBox.getEditor();
      if (this.editor != null) {
        this.editorComponent = this.editor.getEditorComponent();
        if (this.editorComponent != null)
          this.editorComponent.addFocusListener(this); 
      } 
      param1JComboBox.addPropertyChangeListener("editor", this);
    }
    
    public void unregister() {
      this.comboBox.removePropertyChangeListener(this);
      if (this.editorComponent != null)
        this.editorComponent.removeFocusListener(this); 
    }
    
    public void focusGained(FocusEvent param1FocusEvent) { this.comboBox.repaint(); }
    
    public void focusLost(FocusEvent param1FocusEvent) { this.comboBox.repaint(); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      ComboBoxEditor comboBoxEditor = this.comboBox.getEditor();
      if (this.editor != comboBoxEditor) {
        if (this.editorComponent != null)
          this.editorComponent.removeFocusListener(this); 
        this.editor = comboBoxEditor;
        if (this.editor != null) {
          this.editorComponent = this.editor.getEditorComponent();
          if (this.editorComponent != null)
            this.editorComponent.addFocusListener(this); 
        } 
      } 
    }
  }
  
  private static class SynthComboBoxEditor extends BasicComboBoxEditor.UIResource {
    private SynthComboBoxEditor() {}
    
    public JTextField createEditorComponent() {
      JTextField jTextField = new JTextField("", 9);
      jTextField.setName("ComboBox.textField");
      return jTextField;
    }
  }
  
  private class SynthComboBoxRenderer extends JLabel implements ListCellRenderer<Object>, UIResource {
    public SynthComboBoxRenderer() { setText(" "); }
    
    public String getName() {
      String str = super.getName();
      return (str == null) ? "ComboBox.renderer" : str;
    }
    
    public Component getListCellRendererComponent(JList<?> param1JList, Object param1Object, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      setName("ComboBox.listRenderer");
      SynthLookAndFeel.resetSelectedUI();
      if (param1Boolean1) {
        setBackground(param1JList.getSelectionBackground());
        setForeground(param1JList.getSelectionForeground());
        if (!SynthComboBoxUI.this.useListColors)
          SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(getUI(), SynthLabelUI.class), param1Boolean1, param1Boolean2, param1JList.isEnabled(), false); 
      } else {
        setBackground(param1JList.getBackground());
        setForeground(param1JList.getForeground());
      } 
      setFont(param1JList.getFont());
      if (param1Object instanceof Icon) {
        setIcon((Icon)param1Object);
        setText("");
      } else {
        String str = (param1Object == null) ? " " : param1Object.toString();
        if ("".equals(str))
          str = " "; 
        setText(str);
      } 
      if (SynthComboBoxUI.this.comboBox != null) {
        setEnabled(SynthComboBoxUI.this.comboBox.isEnabled());
        setComponentOrientation(SynthComboBoxUI.this.comboBox.getComponentOrientation());
      } 
      return this;
    }
    
    public void paint(Graphics param1Graphics) {
      super.paint(param1Graphics);
      SynthLookAndFeel.resetSelectedUI();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthComboBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */