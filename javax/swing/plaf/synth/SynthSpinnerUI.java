package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SpinnerUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class SynthSpinnerUI extends BasicSpinnerUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  private EditorFocusHandler editorFocusHandler = new EditorFocusHandler(null);
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthSpinnerUI(); }
  
  protected void installListeners() {
    super.installListeners();
    this.spinner.addPropertyChangeListener(this);
    JComponent jComponent = this.spinner.getEditor();
    if (jComponent instanceof JSpinner.DefaultEditor) {
      JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)jComponent).getTextField();
      if (jFormattedTextField != null)
        jFormattedTextField.addFocusListener(this.editorFocusHandler); 
    } 
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.spinner.removePropertyChangeListener(this);
    JComponent jComponent = this.spinner.getEditor();
    if (jComponent instanceof JSpinner.DefaultEditor) {
      JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)jComponent).getTextField();
      if (jFormattedTextField != null)
        jFormattedTextField.removeFocusListener(this.editorFocusHandler); 
    } 
  }
  
  protected void installDefaults() {
    LayoutManager layoutManager = this.spinner.getLayout();
    if (layoutManager == null || layoutManager instanceof UIResource)
      this.spinner.setLayout(createLayout()); 
    updateStyle(this.spinner);
  }
  
  private void updateStyle(JSpinner paramJSpinner) {
    SynthContext synthContext = getContext(paramJSpinner, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle && synthStyle != null)
      installKeyboardActions(); 
    synthContext.dispose();
  }
  
  protected void uninstallDefaults() {
    if (this.spinner.getLayout() instanceof UIResource)
      this.spinner.setLayout(null); 
    SynthContext synthContext = getContext(this.spinner, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  protected LayoutManager createLayout() { return new SpinnerLayout(null); }
  
  protected Component createPreviousButton() {
    SynthArrowButton synthArrowButton = new SynthArrowButton(5);
    synthArrowButton.setName("Spinner.previousButton");
    installPreviousButtonListeners(synthArrowButton);
    return synthArrowButton;
  }
  
  protected Component createNextButton() {
    SynthArrowButton synthArrowButton = new SynthArrowButton(1);
    synthArrowButton.setName("Spinner.nextButton");
    installNextButtonListeners(synthArrowButton);
    return synthArrowButton;
  }
  
  protected JComponent createEditor() {
    JComponent jComponent = this.spinner.getEditor();
    jComponent.setName("Spinner.editor");
    updateEditorAlignment(jComponent);
    return jComponent;
  }
  
  protected void replaceEditor(JComponent paramJComponent1, JComponent paramJComponent2) {
    this.spinner.remove(paramJComponent1);
    this.spinner.add(paramJComponent2, "Editor");
    if (paramJComponent1 instanceof JSpinner.DefaultEditor) {
      JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)paramJComponent1).getTextField();
      if (jFormattedTextField != null)
        jFormattedTextField.removeFocusListener(this.editorFocusHandler); 
    } 
    if (paramJComponent2 instanceof JSpinner.DefaultEditor) {
      JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)paramJComponent2).getTextField();
      if (jFormattedTextField != null)
        jFormattedTextField.addFocusListener(this.editorFocusHandler); 
    } 
  }
  
  private void updateEditorAlignment(JComponent paramJComponent) {
    if (paramJComponent instanceof JSpinner.DefaultEditor) {
      SynthContext synthContext = getContext(this.spinner);
      Integer integer = (Integer)synthContext.getStyle().get(synthContext, "Spinner.editorAlignment");
      JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)paramJComponent).getTextField();
      if (integer != null)
        jFormattedTextField.setHorizontalAlignment(integer.intValue()); 
      jFormattedTextField.putClientProperty("JComponent.sizeVariant", this.spinner.getClientProperty("JComponent.sizeVariant"));
    } 
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintSpinnerBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintSpinnerBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    JSpinner jSpinner = (JSpinner)paramPropertyChangeEvent.getSource();
    SpinnerUI spinnerUI = jSpinner.getUI();
    if (spinnerUI instanceof SynthSpinnerUI) {
      SynthSpinnerUI synthSpinnerUI = (SynthSpinnerUI)spinnerUI;
      if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
        synthSpinnerUI.updateStyle(jSpinner); 
    } 
  }
  
  private class EditorFocusHandler implements FocusListener {
    private EditorFocusHandler() {}
    
    public void focusGained(FocusEvent param1FocusEvent) { SynthSpinnerUI.this.spinner.repaint(); }
    
    public void focusLost(FocusEvent param1FocusEvent) { SynthSpinnerUI.this.spinner.repaint(); }
  }
  
  private static class SpinnerLayout implements LayoutManager, UIResource {
    private Component nextButton = null;
    
    private Component previousButton = null;
    
    private Component editor = null;
    
    private SpinnerLayout() {}
    
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
    
    private Dimension preferredSize(Component param1Component) { return (param1Component == null) ? new Dimension(0, 0) : param1Component.getPreferredSize(); }
    
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
      int i3;
      int i2;
      Insets insets = param1Container.getInsets();
      int i = param1Container.getWidth() - insets.left + insets.right;
      int j = param1Container.getHeight() - insets.top + insets.bottom;
      Dimension dimension1 = preferredSize(this.nextButton);
      Dimension dimension2 = preferredSize(this.previousButton);
      int k = j / 2;
      int m = j - k;
      int n = Math.max(dimension1.width, dimension2.width);
      int i1 = i - n;
      if (param1Container.getComponentOrientation().isLeftToRight()) {
        i2 = insets.left;
        i3 = i2 + i1;
      } else {
        i3 = insets.left;
        i2 = i3 + n;
      } 
      int i4 = insets.top + k;
      setBounds(this.editor, i2, insets.top, i1, j);
      setBounds(this.nextButton, i3, insets.top, n, k);
      setBounds(this.previousButton, i3, i4, n, m);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthSpinnerUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */