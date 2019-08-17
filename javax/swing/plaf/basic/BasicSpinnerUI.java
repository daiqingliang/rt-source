package javax.swing.plaf.basic;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.LookAndFeel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SpinnerUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.InternationalFormatter;
import sun.swing.DefaultLookup;

public class BasicSpinnerUI extends SpinnerUI {
  protected JSpinner spinner;
  
  private Handler handler;
  
  private static final ArrowButtonHandler nextButtonHandler = new ArrowButtonHandler("increment", true);
  
  private static final ArrowButtonHandler previousButtonHandler = new ArrowButtonHandler("decrement", false);
  
  private PropertyChangeListener propertyChangeListener;
  
  private static final Dimension zeroSize = new Dimension(0, 0);
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicSpinnerUI(); }
  
  private void maybeAdd(Component paramComponent, String paramString) {
    if (paramComponent != null)
      this.spinner.add(paramComponent, paramString); 
  }
  
  public void installUI(JComponent paramJComponent) {
    this.spinner = (JSpinner)paramJComponent;
    installDefaults();
    installListeners();
    maybeAdd(createNextButton(), "Next");
    maybeAdd(createPreviousButton(), "Previous");
    maybeAdd(createEditor(), "Editor");
    updateEnabledState();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallDefaults();
    uninstallListeners();
    this.spinner = null;
    paramJComponent.removeAll();
  }
  
  protected void installListeners() {
    this.propertyChangeListener = createPropertyChangeListener();
    this.spinner.addPropertyChangeListener(this.propertyChangeListener);
    if (DefaultLookup.getBoolean(this.spinner, this, "Spinner.disableOnBoundaryValues", false))
      this.spinner.addChangeListener(getHandler()); 
    JComponent jComponent = this.spinner.getEditor();
    if (jComponent != null && jComponent instanceof JSpinner.DefaultEditor) {
      JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)jComponent).getTextField();
      if (jFormattedTextField != null) {
        jFormattedTextField.addFocusListener(nextButtonHandler);
        jFormattedTextField.addFocusListener(previousButtonHandler);
      } 
    } 
  }
  
  protected void uninstallListeners() {
    this.spinner.removePropertyChangeListener(this.propertyChangeListener);
    this.spinner.removeChangeListener(this.handler);
    JComponent jComponent = this.spinner.getEditor();
    removeEditorBorderListener(jComponent);
    if (jComponent instanceof JSpinner.DefaultEditor) {
      JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)jComponent).getTextField();
      if (jFormattedTextField != null) {
        jFormattedTextField.removeFocusListener(nextButtonHandler);
        jFormattedTextField.removeFocusListener(previousButtonHandler);
      } 
    } 
    this.propertyChangeListener = null;
    this.handler = null;
  }
  
  protected void installDefaults() {
    this.spinner.setLayout(createLayout());
    LookAndFeel.installBorder(this.spinner, "Spinner.border");
    LookAndFeel.installColorsAndFont(this.spinner, "Spinner.background", "Spinner.foreground", "Spinner.font");
    LookAndFeel.installProperty(this.spinner, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults() { this.spinner.setLayout(null); }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected void installNextButtonListeners(Component paramComponent) { installButtonListeners(paramComponent, nextButtonHandler); }
  
  protected void installPreviousButtonListeners(Component paramComponent) { installButtonListeners(paramComponent, previousButtonHandler); }
  
  private void installButtonListeners(Component paramComponent, ArrowButtonHandler paramArrowButtonHandler) {
    if (paramComponent instanceof JButton)
      ((JButton)paramComponent).addActionListener(paramArrowButtonHandler); 
    paramComponent.addMouseListener(paramArrowButtonHandler);
  }
  
  protected LayoutManager createLayout() { return getHandler(); }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  protected Component createPreviousButton() {
    Component component = createArrowButton(5);
    component.setName("Spinner.previousButton");
    installPreviousButtonListeners(component);
    return component;
  }
  
  protected Component createNextButton() {
    Component component = createArrowButton(1);
    component.setName("Spinner.nextButton");
    installNextButtonListeners(component);
    return component;
  }
  
  private Component createArrowButton(int paramInt) {
    BasicArrowButton basicArrowButton = new BasicArrowButton(paramInt);
    Border border = UIManager.getBorder("Spinner.arrowButtonBorder");
    if (border instanceof UIResource) {
      basicArrowButton.setBorder(new CompoundBorder(border, null));
    } else {
      basicArrowButton.setBorder(border);
    } 
    basicArrowButton.setInheritsPopupMenu(true);
    return basicArrowButton;
  }
  
  protected JComponent createEditor() {
    JComponent jComponent = this.spinner.getEditor();
    maybeRemoveEditorBorder(jComponent);
    installEditorBorderListener(jComponent);
    jComponent.setInheritsPopupMenu(true);
    updateEditorAlignment(jComponent);
    return jComponent;
  }
  
  protected void replaceEditor(JComponent paramJComponent1, JComponent paramJComponent2) {
    this.spinner.remove(paramJComponent1);
    maybeRemoveEditorBorder(paramJComponent2);
    installEditorBorderListener(paramJComponent2);
    paramJComponent2.setInheritsPopupMenu(true);
    this.spinner.add(paramJComponent2, "Editor");
  }
  
  private void updateEditorAlignment(JComponent paramJComponent) {
    if (paramJComponent instanceof JSpinner.DefaultEditor) {
      int i = UIManager.getInt("Spinner.editorAlignment");
      JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)paramJComponent).getTextField();
      jFormattedTextField.setHorizontalAlignment(i);
    } 
  }
  
  private void maybeRemoveEditorBorder(JComponent paramJComponent) {
    if (!UIManager.getBoolean("Spinner.editorBorderPainted")) {
      if (paramJComponent instanceof javax.swing.JPanel && paramJComponent.getBorder() == null && paramJComponent.getComponentCount() > 0)
        paramJComponent = (JComponent)paramJComponent.getComponent(0); 
      if (paramJComponent != null && paramJComponent.getBorder() instanceof UIResource)
        paramJComponent.setBorder(null); 
    } 
  }
  
  private void installEditorBorderListener(JComponent paramJComponent) {
    if (!UIManager.getBoolean("Spinner.editorBorderPainted")) {
      if (paramJComponent instanceof javax.swing.JPanel && paramJComponent.getBorder() == null && paramJComponent.getComponentCount() > 0)
        paramJComponent = (JComponent)paramJComponent.getComponent(0); 
      if (paramJComponent != null && (paramJComponent.getBorder() == null || paramJComponent.getBorder() instanceof UIResource))
        paramJComponent.addPropertyChangeListener(getHandler()); 
    } 
  }
  
  private void removeEditorBorderListener(JComponent paramJComponent) {
    if (!UIManager.getBoolean("Spinner.editorBorderPainted")) {
      if (paramJComponent instanceof javax.swing.JPanel && paramJComponent.getComponentCount() > 0)
        paramJComponent = (JComponent)paramJComponent.getComponent(0); 
      if (paramJComponent != null)
        paramJComponent.removePropertyChangeListener(getHandler()); 
    } 
  }
  
  private void updateEnabledState() { updateEnabledState(this.spinner, this.spinner.isEnabled()); }
  
  private void updateEnabledState(Container paramContainer, boolean paramBoolean) {
    for (int i = paramContainer.getComponentCount() - 1; i >= 0; i--) {
      Component component = paramContainer.getComponent(i);
      if (DefaultLookup.getBoolean(this.spinner, this, "Spinner.disableOnBoundaryValues", false)) {
        SpinnerModel spinnerModel = this.spinner.getModel();
        if (component.getName() == "Spinner.nextButton" && spinnerModel.getNextValue() == null) {
          component.setEnabled(false);
        } else if (component.getName() == "Spinner.previousButton" && spinnerModel.getPreviousValue() == null) {
          component.setEnabled(false);
        } else {
          component.setEnabled(paramBoolean);
        } 
      } else {
        component.setEnabled(paramBoolean);
      } 
      if (component instanceof Container)
        updateEnabledState((Container)component, paramBoolean); 
    } 
  }
  
  protected void installKeyboardActions() {
    InputMap inputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(this.spinner, 1, inputMap);
    LazyActionMap.installLazyActionMap(this.spinner, BasicSpinnerUI.class, "Spinner.actionMap");
  }
  
  private InputMap getInputMap(int paramInt) { return (paramInt == 1) ? (InputMap)DefaultLookup.get(this.spinner, this, "Spinner.ancestorInputMap") : null; }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put("increment", nextButtonHandler);
    paramLazyActionMap.put("decrement", previousButtonHandler);
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    JComponent jComponent = this.spinner.getEditor();
    Insets insets = this.spinner.getInsets();
    paramInt1 = paramInt1 - insets.left - insets.right;
    paramInt2 = paramInt2 - insets.top - insets.bottom;
    if (paramInt1 >= 0 && paramInt2 >= 0) {
      int i = jComponent.getBaseline(paramInt1, paramInt2);
      if (i >= 0)
        return insets.top + i; 
    } 
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    return this.spinner.getEditor().getBaselineResizeBehavior();
  }
  
  private static class ArrowButtonHandler extends AbstractAction implements FocusListener, MouseListener, UIResource {
    final Timer autoRepeatTimer;
    
    final boolean isNext;
    
    JSpinner spinner = null;
    
    JButton arrowButton = null;
    
    ArrowButtonHandler(String param1String, boolean param1Boolean) {
      super(param1String);
      this.isNext = param1Boolean;
      this.autoRepeatTimer = new Timer(60, this);
      this.autoRepeatTimer.setInitialDelay(300);
    }
    
    private JSpinner eventToSpinner(AWTEvent param1AWTEvent) {
      Object object;
      for (object = param1AWTEvent.getSource(); object instanceof Component && !(object instanceof JSpinner); object = ((Component)object).getParent());
      return (object instanceof JSpinner) ? (JSpinner)object : null;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JSpinner jSpinner = this.spinner;
      if (!(param1ActionEvent.getSource() instanceof Timer)) {
        jSpinner = eventToSpinner(param1ActionEvent);
        if (param1ActionEvent.getSource() instanceof JButton)
          this.arrowButton = (JButton)param1ActionEvent.getSource(); 
      } else if (this.arrowButton != null && !this.arrowButton.getModel().isPressed() && this.autoRepeatTimer.isRunning()) {
        this.autoRepeatTimer.stop();
        jSpinner = null;
        this.arrowButton = null;
      } 
      if (jSpinner != null)
        try {
          int i = getCalendarField(jSpinner);
          jSpinner.commitEdit();
          if (i != -1)
            ((SpinnerDateModel)jSpinner.getModel()).setCalendarField(i); 
          Object object = this.isNext ? jSpinner.getNextValue() : jSpinner.getPreviousValue();
          if (object != null) {
            jSpinner.setValue(object);
            select(jSpinner);
          } 
        } catch (IllegalArgumentException illegalArgumentException) {
          UIManager.getLookAndFeel().provideErrorFeedback(jSpinner);
        } catch (ParseException parseException) {
          UIManager.getLookAndFeel().provideErrorFeedback(jSpinner);
        }  
    }
    
    private void select(JSpinner param1JSpinner) {
      JComponent jComponent = param1JSpinner.getEditor();
      if (jComponent instanceof JSpinner.DateEditor) {
        JSpinner.DateEditor dateEditor = (JSpinner.DateEditor)jComponent;
        JFormattedTextField jFormattedTextField = dateEditor.getTextField();
        SimpleDateFormat simpleDateFormat = dateEditor.getFormat();
        Object object;
        if (simpleDateFormat != null && (object = param1JSpinner.getValue()) != null) {
          SpinnerDateModel spinnerDateModel = dateEditor.getModel();
          DateFormat.Field field = DateFormat.Field.ofCalendarField(spinnerDateModel.getCalendarField());
          if (field != null)
            try {
              AttributedCharacterIterator attributedCharacterIterator = simpleDateFormat.formatToCharacterIterator(object);
              if (!select(jFormattedTextField, attributedCharacterIterator, field) && field == DateFormat.Field.HOUR0)
                select(jFormattedTextField, attributedCharacterIterator, DateFormat.Field.HOUR1); 
            } catch (IllegalArgumentException illegalArgumentException) {} 
        } 
      } 
    }
    
    private boolean select(JFormattedTextField param1JFormattedTextField, AttributedCharacterIterator param1AttributedCharacterIterator, DateFormat.Field param1Field) {
      int i = param1JFormattedTextField.getDocument().getLength();
      param1AttributedCharacterIterator.first();
      do {
        Map map = param1AttributedCharacterIterator.getAttributes();
        if (map != null && map.containsKey(param1Field)) {
          int j = param1AttributedCharacterIterator.getRunStart(param1Field);
          int k = param1AttributedCharacterIterator.getRunLimit(param1Field);
          if (j != -1 && k != -1 && j <= i && k <= i)
            param1JFormattedTextField.select(j, k); 
          return true;
        } 
      } while (param1AttributedCharacterIterator.next() != Character.MAX_VALUE);
      return false;
    }
    
    private int getCalendarField(JSpinner param1JSpinner) {
      JComponent jComponent = param1JSpinner.getEditor();
      if (jComponent instanceof JSpinner.DateEditor) {
        JSpinner.DateEditor dateEditor = (JSpinner.DateEditor)jComponent;
        JFormattedTextField jFormattedTextField = dateEditor.getTextField();
        int i = jFormattedTextField.getSelectionStart();
        JFormattedTextField.AbstractFormatter abstractFormatter = jFormattedTextField.getFormatter();
        if (abstractFormatter instanceof InternationalFormatter) {
          Format.Field[] arrayOfField = ((InternationalFormatter)abstractFormatter).getFields(i);
          for (byte b = 0; b < arrayOfField.length; b++) {
            if (arrayOfField[b] instanceof DateFormat.Field) {
              int j;
              if (arrayOfField[b] == DateFormat.Field.HOUR1) {
                j = 10;
              } else {
                j = ((DateFormat.Field)arrayOfField[b]).getCalendarField();
              } 
              if (j != -1)
                return j; 
            } 
          } 
        } 
      } 
      return -1;
    }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (SwingUtilities.isLeftMouseButton(param1MouseEvent) && param1MouseEvent.getComponent().isEnabled()) {
        this.spinner = eventToSpinner(param1MouseEvent);
        this.autoRepeatTimer.start();
        focusSpinnerIfNecessary();
      } 
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      this.autoRepeatTimer.stop();
      this.arrowButton = null;
      this.spinner = null;
    }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void mouseEntered(MouseEvent param1MouseEvent) {
      if (this.spinner != null && !this.autoRepeatTimer.isRunning() && this.spinner == eventToSpinner(param1MouseEvent))
        this.autoRepeatTimer.start(); 
    }
    
    public void mouseExited(MouseEvent param1MouseEvent) {
      if (this.autoRepeatTimer.isRunning())
        this.autoRepeatTimer.stop(); 
    }
    
    private void focusSpinnerIfNecessary() {
      Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      if (this.spinner.isRequestFocusEnabled() && (component == null || !SwingUtilities.isDescendingFrom(component, this.spinner))) {
        Container container = this.spinner;
        if (!container.isFocusCycleRoot())
          container = container.getFocusCycleRootAncestor(); 
        if (container != null) {
          FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
          Component component1 = focusTraversalPolicy.getComponentAfter(container, this.spinner);
          if (component1 != null && SwingUtilities.isDescendingFrom(component1, this.spinner))
            component1.requestFocus(); 
        } 
      } 
    }
    
    public void focusGained(FocusEvent param1FocusEvent) {}
    
    public void focusLost(FocusEvent param1FocusEvent) {
      if (this.spinner == eventToSpinner(param1FocusEvent)) {
        if (this.autoRepeatTimer.isRunning())
          this.autoRepeatTimer.stop(); 
        this.spinner = null;
        if (this.arrowButton != null) {
          ButtonModel buttonModel = this.arrowButton.getModel();
          buttonModel.setPressed(false);
          buttonModel.setArmed(false);
          this.arrowButton = null;
        } 
      } 
    }
  }
  
  private static class Handler implements LayoutManager, PropertyChangeListener, ChangeListener {
    private Component nextButton = null;
    
    private Component previousButton = null;
    
    private Component editor = null;
    
    private Handler() {}
    
    public void addLayoutComponent(String param1String, Component param1Component) {
      if ("Next".equals(param1String)) {
        this.nextButton = param1Component;
      } else if ("Previous".equals(param1String)) {
        this.previousButton = param1Component;
      } else if ("Editor".equals(param1String)) {
        this.editor = param1Component;
      } 
    }
    
    public void removeLayoutComponent(Component param1Component) {
      if (param1Component == this.nextButton) {
        this.nextButton = null;
      } else if (param1Component == this.previousButton) {
        this.previousButton = null;
      } else if (param1Component == this.editor) {
        this.editor = null;
      } 
    }
    
    private Dimension preferredSize(Component param1Component) { return (param1Component == null) ? zeroSize : param1Component.getPreferredSize(); }
    
    public Dimension preferredLayoutSize(Container param1Container) {
      Dimension dimension1 = preferredSize(this.nextButton);
      Dimension dimension2 = preferredSize(this.previousButton);
      Dimension dimension3 = preferredSize(this.editor);
      dimension3.height = (dimension3.height + 1) / 2 * 2;
      Dimension dimension4 = new Dimension(dimension3.width, dimension3.height);
      dimension4.width += Math.max(dimension1.width, dimension2.width);
      Insets insets = param1Container.getInsets();
      dimension4.width += insets.left + insets.right;
      dimension4.height += insets.top + insets.bottom;
      return dimension4;
    }
    
    public Dimension minimumLayoutSize(Container param1Container) { return preferredLayoutSize(param1Container); }
    
    private void setBounds(Component param1Component, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (param1Component != null)
        param1Component.setBounds(param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void layoutContainer(Container param1Container) {
      int i2;
      int i1;
      int n;
      int i = param1Container.getWidth();
      int j = param1Container.getHeight();
      Insets insets1 = param1Container.getInsets();
      if (this.nextButton == null && this.previousButton == null) {
        setBounds(this.editor, insets1.left, insets1.top, i - insets1.left - insets1.right, j - insets1.top - insets1.bottom);
        return;
      } 
      Dimension dimension1 = preferredSize(this.nextButton);
      Dimension dimension2 = preferredSize(this.previousButton);
      int k = Math.max(dimension1.width, dimension2.width);
      int m = j - insets1.top + insets1.bottom;
      Insets insets2 = UIManager.getInsets("Spinner.arrowButtonInsets");
      if (insets2 == null)
        insets2 = insets1; 
      if (param1Container.getComponentOrientation().isLeftToRight()) {
        n = insets1.left;
        i1 = i - insets1.left - k - insets2.right;
        i2 = i - k - insets2.right;
      } else {
        i2 = insets2.left;
        n = i2 + k;
        i1 = i - insets2.left - k - insets1.right;
      } 
      int i3 = insets2.top;
      int i4 = j / 2 + j % 2 - i3;
      int i5 = insets2.top + i4;
      int i6 = j - i5 - insets2.bottom;
      setBounds(this.editor, n, insets1.top, i1, m);
      setBounds(this.nextButton, i2, i3, k, i4);
      setBounds(this.previousButton, i2, i5, k, i6);
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (param1PropertyChangeEvent.getSource() instanceof JSpinner) {
        JSpinner jSpinner = (JSpinner)param1PropertyChangeEvent.getSource();
        SpinnerUI spinnerUI = jSpinner.getUI();
        if (spinnerUI instanceof BasicSpinnerUI) {
          BasicSpinnerUI basicSpinnerUI = (BasicSpinnerUI)spinnerUI;
          if ("editor".equals(str)) {
            JComponent jComponent1 = (JComponent)param1PropertyChangeEvent.getOldValue();
            JComponent jComponent2 = (JComponent)param1PropertyChangeEvent.getNewValue();
            basicSpinnerUI.replaceEditor(jComponent1, jComponent2);
            basicSpinnerUI.updateEnabledState();
            if (jComponent1 instanceof JSpinner.DefaultEditor) {
              JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)jComponent1).getTextField();
              if (jFormattedTextField != null) {
                jFormattedTextField.removeFocusListener(nextButtonHandler);
                jFormattedTextField.removeFocusListener(previousButtonHandler);
              } 
            } 
            if (jComponent2 instanceof JSpinner.DefaultEditor) {
              JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)jComponent2).getTextField();
              if (jFormattedTextField != null) {
                if (jFormattedTextField.getFont() instanceof UIResource)
                  jFormattedTextField.setFont(jSpinner.getFont()); 
                jFormattedTextField.addFocusListener(nextButtonHandler);
                jFormattedTextField.addFocusListener(previousButtonHandler);
              } 
            } 
          } else if ("enabled".equals(str) || "model".equals(str)) {
            basicSpinnerUI.updateEnabledState();
          } else if ("font".equals(str)) {
            JComponent jComponent = jSpinner.getEditor();
            if (jComponent != null && jComponent instanceof JSpinner.DefaultEditor) {
              JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)jComponent).getTextField();
              if (jFormattedTextField != null && jFormattedTextField.getFont() instanceof UIResource)
                jFormattedTextField.setFont(jSpinner.getFont()); 
            } 
          } else if ("ToolTipText".equals(str)) {
            updateToolTipTextForChildren(jSpinner);
          } 
        } 
      } else if (param1PropertyChangeEvent.getSource() instanceof JComponent) {
        JComponent jComponent = (JComponent)param1PropertyChangeEvent.getSource();
        if (jComponent.getParent() instanceof javax.swing.JPanel && jComponent.getParent().getParent() instanceof JSpinner && "border".equals(str)) {
          JSpinner jSpinner = (JSpinner)jComponent.getParent().getParent();
          SpinnerUI spinnerUI = jSpinner.getUI();
          if (spinnerUI instanceof BasicSpinnerUI) {
            BasicSpinnerUI basicSpinnerUI;
            basicSpinnerUI.maybeRemoveEditorBorder(jComponent);
          } 
        } 
      } 
    }
    
    private void updateToolTipTextForChildren(JComponent param1JComponent) {
      String str = param1JComponent.getToolTipText();
      Component[] arrayOfComponent = param1JComponent.getComponents();
      for (byte b = 0; b < arrayOfComponent.length; b++) {
        if (arrayOfComponent[b] instanceof JSpinner.DefaultEditor) {
          JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)arrayOfComponent[b]).getTextField();
          if (jFormattedTextField != null)
            jFormattedTextField.setToolTipText(str); 
        } else if (arrayOfComponent[b] instanceof JComponent) {
          ((JComponent)arrayOfComponent[b]).setToolTipText(param1JComponent.getToolTipText());
        } 
      } 
    }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      if (param1ChangeEvent.getSource() instanceof JSpinner) {
        JSpinner jSpinner = (JSpinner)param1ChangeEvent.getSource();
        SpinnerUI spinnerUI = jSpinner.getUI();
        if (DefaultLookup.getBoolean(jSpinner, spinnerUI, "Spinner.disableOnBoundaryValues", false) && spinnerUI instanceof BasicSpinnerUI) {
          BasicSpinnerUI basicSpinnerUI;
          basicSpinnerUI.updateEnabledState();
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicSpinnerUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */