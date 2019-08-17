package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleEditableText;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SpinnerUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DocumentFilter;
import javax.swing.text.NumberFormatter;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

public class JSpinner extends JComponent implements Accessible {
  private static final String uiClassID = "SpinnerUI";
  
  private static final Action DISABLED_ACTION = new DisabledAction(null);
  
  private SpinnerModel model;
  
  private JComponent editor;
  
  private ChangeListener modelListener;
  
  private ChangeEvent changeEvent;
  
  private boolean editorExplicitlySet = false;
  
  public JSpinner(SpinnerModel paramSpinnerModel) {
    if (paramSpinnerModel == null)
      throw new NullPointerException("model cannot be null"); 
    this.model = paramSpinnerModel;
    this.editor = createEditor(paramSpinnerModel);
    setUIProperty("opaque", Boolean.valueOf(true));
    updateUI();
  }
  
  public JSpinner() { this(new SpinnerNumberModel()); }
  
  public SpinnerUI getUI() { return (SpinnerUI)this.ui; }
  
  public void setUI(SpinnerUI paramSpinnerUI) { setUI(paramSpinnerUI); }
  
  public String getUIClassID() { return "SpinnerUI"; }
  
  public void updateUI() {
    setUI((SpinnerUI)UIManager.getUI(this));
    invalidate();
  }
  
  protected JComponent createEditor(SpinnerModel paramSpinnerModel) { return (paramSpinnerModel instanceof SpinnerDateModel) ? new DateEditor(this) : ((paramSpinnerModel instanceof SpinnerListModel) ? new ListEditor(this) : ((paramSpinnerModel instanceof SpinnerNumberModel) ? new NumberEditor(this) : new DefaultEditor(this))); }
  
  public void setModel(SpinnerModel paramSpinnerModel) {
    if (paramSpinnerModel == null)
      throw new IllegalArgumentException("null model"); 
    if (!paramSpinnerModel.equals(this.model)) {
      SpinnerModel spinnerModel = this.model;
      this.model = paramSpinnerModel;
      if (this.modelListener != null) {
        spinnerModel.removeChangeListener(this.modelListener);
        this.model.addChangeListener(this.modelListener);
      } 
      firePropertyChange("model", spinnerModel, paramSpinnerModel);
      if (!this.editorExplicitlySet) {
        setEditor(createEditor(paramSpinnerModel));
        this.editorExplicitlySet = false;
      } 
      repaint();
      revalidate();
    } 
  }
  
  public SpinnerModel getModel() { return this.model; }
  
  public Object getValue() { return getModel().getValue(); }
  
  public void setValue(Object paramObject) { getModel().setValue(paramObject); }
  
  public Object getNextValue() { return getModel().getNextValue(); }
  
  public void addChangeListener(ChangeListener paramChangeListener) {
    if (this.modelListener == null) {
      this.modelListener = new ModelListener(null);
      getModel().addChangeListener(this.modelListener);
    } 
    this.listenerList.add(ChangeListener.class, paramChangeListener);
  }
  
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
  
  public Object getPreviousValue() { return getModel().getPreviousValue(); }
  
  public void setEditor(JComponent paramJComponent) {
    if (paramJComponent == null)
      throw new IllegalArgumentException("null editor"); 
    if (!paramJComponent.equals(this.editor)) {
      JComponent jComponent = this.editor;
      this.editor = paramJComponent;
      if (jComponent instanceof DefaultEditor)
        ((DefaultEditor)jComponent).dismiss(this); 
      this.editorExplicitlySet = true;
      firePropertyChange("editor", jComponent, paramJComponent);
      revalidate();
      repaint();
    } 
  }
  
  public JComponent getEditor() { return this.editor; }
  
  public void commitEdit() {
    JComponent jComponent = getEditor();
    if (jComponent instanceof DefaultEditor)
      ((DefaultEditor)jComponent).commitEdit(); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("SpinnerUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJSpinner(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJSpinner extends JComponent.AccessibleJComponent implements AccessibleValue, AccessibleAction, AccessibleText, AccessibleEditableText, ChangeListener {
    private Object oldModelValue = null;
    
    protected AccessibleJSpinner() {
      super(JSpinner.this);
      this.oldModelValue = this$0.model.getValue();
      this$0.addChangeListener(this);
    }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      if (param1ChangeEvent == null)
        throw new NullPointerException(); 
      Object object = JSpinner.this.model.getValue();
      firePropertyChange("AccessibleValue", this.oldModelValue, object);
      firePropertyChange("AccessibleText", null, Integer.valueOf(0));
      this.oldModelValue = object;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.SPIN_BOX; }
    
    public int getAccessibleChildrenCount() { return (JSpinner.this.editor.getAccessibleContext() != null) ? 1 : 0; }
    
    public Accessible getAccessibleChild(int param1Int) { return (param1Int != 0) ? null : ((JSpinner.this.editor.getAccessibleContext() != null) ? (Accessible)JSpinner.this.editor : null); }
    
    public AccessibleAction getAccessibleAction() { return this; }
    
    public AccessibleText getAccessibleText() { return this; }
    
    private AccessibleContext getEditorAccessibleContext() {
      if (JSpinner.this.editor instanceof JSpinner.DefaultEditor) {
        JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor)JSpinner.this.editor).getTextField();
        if (jFormattedTextField != null)
          return jFormattedTextField.getAccessibleContext(); 
      } else if (JSpinner.this.editor instanceof Accessible) {
        return JSpinner.this.editor.getAccessibleContext();
      } 
      return null;
    }
    
    private AccessibleText getEditorAccessibleText() {
      AccessibleContext accessibleContext = getEditorAccessibleContext();
      return (accessibleContext != null) ? accessibleContext.getAccessibleText() : null;
    }
    
    private AccessibleEditableText getEditorAccessibleEditableText() {
      AccessibleText accessibleText = getEditorAccessibleText();
      return (accessibleText instanceof AccessibleEditableText) ? (AccessibleEditableText)accessibleText : null;
    }
    
    public AccessibleValue getAccessibleValue() { return this; }
    
    public Number getCurrentAccessibleValue() {
      Object object = JSpinner.this.model.getValue();
      return (object instanceof Number) ? (Number)object : null;
    }
    
    public boolean setCurrentAccessibleValue(Number param1Number) {
      try {
        JSpinner.this.model.setValue(param1Number);
        return true;
      } catch (IllegalArgumentException illegalArgumentException) {
        return false;
      } 
    }
    
    public Number getMinimumAccessibleValue() {
      if (JSpinner.this.model instanceof SpinnerNumberModel) {
        SpinnerNumberModel spinnerNumberModel = (SpinnerNumberModel)JSpinner.this.model;
        Comparable comparable = spinnerNumberModel.getMinimum();
        if (comparable instanceof Number)
          return (Number)comparable; 
      } 
      return null;
    }
    
    public Number getMaximumAccessibleValue() {
      if (JSpinner.this.model instanceof SpinnerNumberModel) {
        SpinnerNumberModel spinnerNumberModel = (SpinnerNumberModel)JSpinner.this.model;
        Comparable comparable = spinnerNumberModel.getMaximum();
        if (comparable instanceof Number)
          return (Number)comparable; 
      } 
      return null;
    }
    
    public int getAccessibleActionCount() { return 2; }
    
    public String getAccessibleActionDescription(int param1Int) { return (param1Int == 0) ? AccessibleAction.INCREMENT : ((param1Int == 1) ? AccessibleAction.DECREMENT : null); }
    
    public boolean doAccessibleAction(int param1Int) {
      Object object;
      if (param1Int < 0 || param1Int > 1)
        return false; 
      if (param1Int == 0) {
        object = JSpinner.this.getNextValue();
      } else {
        object = JSpinner.this.getPreviousValue();
      } 
      try {
        JSpinner.this.model.setValue(object);
        return true;
      } catch (IllegalArgumentException illegalArgumentException) {
        return false;
      } 
    }
    
    private boolean sameWindowAncestor(Component param1Component1, Component param1Component2) { return (param1Component1 == null || param1Component2 == null) ? false : ((SwingUtilities.getWindowAncestor(param1Component1) == SwingUtilities.getWindowAncestor(param1Component2))); }
    
    public int getIndexAtPoint(Point param1Point) {
      AccessibleText accessibleText = getEditorAccessibleText();
      if (accessibleText != null && sameWindowAncestor(JSpinner.this, JSpinner.this.editor)) {
        Point point = SwingUtilities.convertPoint(JSpinner.this, param1Point, JSpinner.this.editor);
        if (point != null)
          return accessibleText.getIndexAtPoint(point); 
      } 
      return -1;
    }
    
    public Rectangle getCharacterBounds(int param1Int) {
      AccessibleText accessibleText = getEditorAccessibleText();
      if (accessibleText != null) {
        Rectangle rectangle = accessibleText.getCharacterBounds(param1Int);
        if (rectangle != null && sameWindowAncestor(JSpinner.this, JSpinner.this.editor))
          return SwingUtilities.convertRectangle(JSpinner.this.editor, rectangle, JSpinner.this); 
      } 
      return null;
    }
    
    public int getCharCount() {
      AccessibleText accessibleText = getEditorAccessibleText();
      return (accessibleText != null) ? accessibleText.getCharCount() : -1;
    }
    
    public int getCaretPosition() {
      AccessibleText accessibleText = getEditorAccessibleText();
      return (accessibleText != null) ? accessibleText.getCaretPosition() : -1;
    }
    
    public String getAtIndex(int param1Int1, int param1Int2) {
      AccessibleText accessibleText = getEditorAccessibleText();
      return (accessibleText != null) ? accessibleText.getAtIndex(param1Int1, param1Int2) : null;
    }
    
    public String getAfterIndex(int param1Int1, int param1Int2) {
      AccessibleText accessibleText = getEditorAccessibleText();
      return (accessibleText != null) ? accessibleText.getAfterIndex(param1Int1, param1Int2) : null;
    }
    
    public String getBeforeIndex(int param1Int1, int param1Int2) {
      AccessibleText accessibleText = getEditorAccessibleText();
      return (accessibleText != null) ? accessibleText.getBeforeIndex(param1Int1, param1Int2) : null;
    }
    
    public AttributeSet getCharacterAttribute(int param1Int) {
      AccessibleText accessibleText = getEditorAccessibleText();
      return (accessibleText != null) ? accessibleText.getCharacterAttribute(param1Int) : null;
    }
    
    public int getSelectionStart() {
      AccessibleText accessibleText = getEditorAccessibleText();
      return (accessibleText != null) ? accessibleText.getSelectionStart() : -1;
    }
    
    public int getSelectionEnd() {
      AccessibleText accessibleText = getEditorAccessibleText();
      return (accessibleText != null) ? accessibleText.getSelectionEnd() : -1;
    }
    
    public String getSelectedText() {
      AccessibleText accessibleText = getEditorAccessibleText();
      return (accessibleText != null) ? accessibleText.getSelectedText() : null;
    }
    
    public void setTextContents(String param1String) {
      AccessibleEditableText accessibleEditableText = getEditorAccessibleEditableText();
      if (accessibleEditableText != null)
        accessibleEditableText.setTextContents(param1String); 
    }
    
    public void insertTextAtIndex(int param1Int, String param1String) {
      AccessibleEditableText accessibleEditableText = getEditorAccessibleEditableText();
      if (accessibleEditableText != null)
        accessibleEditableText.insertTextAtIndex(param1Int, param1String); 
    }
    
    public String getTextRange(int param1Int1, int param1Int2) {
      AccessibleEditableText accessibleEditableText = getEditorAccessibleEditableText();
      return (accessibleEditableText != null) ? accessibleEditableText.getTextRange(param1Int1, param1Int2) : null;
    }
    
    public void delete(int param1Int1, int param1Int2) {
      AccessibleEditableText accessibleEditableText = getEditorAccessibleEditableText();
      if (accessibleEditableText != null)
        accessibleEditableText.delete(param1Int1, param1Int2); 
    }
    
    public void cut(int param1Int1, int param1Int2) {
      AccessibleEditableText accessibleEditableText = getEditorAccessibleEditableText();
      if (accessibleEditableText != null)
        accessibleEditableText.cut(param1Int1, param1Int2); 
    }
    
    public void paste(int param1Int) {
      AccessibleEditableText accessibleEditableText = getEditorAccessibleEditableText();
      if (accessibleEditableText != null)
        accessibleEditableText.paste(param1Int); 
    }
    
    public void replaceText(int param1Int1, int param1Int2, String param1String) {
      AccessibleEditableText accessibleEditableText = getEditorAccessibleEditableText();
      if (accessibleEditableText != null)
        accessibleEditableText.replaceText(param1Int1, param1Int2, param1String); 
    }
    
    public void selectText(int param1Int1, int param1Int2) {
      AccessibleEditableText accessibleEditableText = getEditorAccessibleEditableText();
      if (accessibleEditableText != null)
        accessibleEditableText.selectText(param1Int1, param1Int2); 
    }
    
    public void setAttributes(int param1Int1, int param1Int2, AttributeSet param1AttributeSet) {
      AccessibleEditableText accessibleEditableText = getEditorAccessibleEditableText();
      if (accessibleEditableText != null)
        accessibleEditableText.setAttributes(param1Int1, param1Int2, param1AttributeSet); 
    }
  }
  
  public static class DateEditor extends DefaultEditor {
    private static String getDefaultPattern(Locale param1Locale) {
      LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter(java.text.spi.DateFormatProvider.class, param1Locale);
      LocaleResources localeResources = localeProviderAdapter.getLocaleResources(param1Locale);
      if (localeResources == null)
        localeResources = LocaleProviderAdapter.forJRE().getLocaleResources(param1Locale); 
      return localeResources.getDateTimePattern(3, 3, null);
    }
    
    public DateEditor(JSpinner param1JSpinner) { this(param1JSpinner, getDefaultPattern(param1JSpinner.getLocale())); }
    
    public DateEditor(JSpinner param1JSpinner, String param1String) { this(param1JSpinner, new SimpleDateFormat(param1String, param1JSpinner.getLocale())); }
    
    private DateEditor(JSpinner param1JSpinner, DateFormat param1DateFormat) {
      super(param1JSpinner);
      if (!(param1JSpinner.getModel() instanceof SpinnerDateModel))
        throw new IllegalArgumentException("model not a SpinnerDateModel"); 
      SpinnerDateModel spinnerDateModel = (SpinnerDateModel)param1JSpinner.getModel();
      JSpinner.DateEditorFormatter dateEditorFormatter = new JSpinner.DateEditorFormatter(spinnerDateModel, param1DateFormat);
      DefaultFormatterFactory defaultFormatterFactory = new DefaultFormatterFactory(dateEditorFormatter);
      JFormattedTextField jFormattedTextField = getTextField();
      jFormattedTextField.setEditable(true);
      jFormattedTextField.setFormatterFactory(defaultFormatterFactory);
      try {
        String str1 = dateEditorFormatter.valueToString(spinnerDateModel.getStart());
        String str2 = dateEditorFormatter.valueToString(spinnerDateModel.getEnd());
        jFormattedTextField.setColumns(Math.max(str1.length(), str2.length()));
      } catch (ParseException parseException) {}
    }
    
    public SimpleDateFormat getFormat() { return (SimpleDateFormat)((DateFormatter)getTextField().getFormatter()).getFormat(); }
    
    public SpinnerDateModel getModel() { return (SpinnerDateModel)getSpinner().getModel(); }
  }
  
  private static class DateEditorFormatter extends DateFormatter {
    private final SpinnerDateModel model;
    
    DateEditorFormatter(SpinnerDateModel param1SpinnerDateModel, DateFormat param1DateFormat) {
      super(param1DateFormat);
      this.model = param1SpinnerDateModel;
    }
    
    public void setMinimum(Comparable param1Comparable) { this.model.setStart(param1Comparable); }
    
    public Comparable getMinimum() { return this.model.getStart(); }
    
    public void setMaximum(Comparable param1Comparable) { this.model.setEnd(param1Comparable); }
    
    public Comparable getMaximum() { return this.model.getEnd(); }
  }
  
  public static class DefaultEditor extends JPanel implements ChangeListener, PropertyChangeListener, LayoutManager {
    public DefaultEditor(JSpinner param1JSpinner) {
      super(null);
      JFormattedTextField jFormattedTextField = new JFormattedTextField();
      jFormattedTextField.setName("Spinner.formattedTextField");
      jFormattedTextField.setValue(param1JSpinner.getValue());
      jFormattedTextField.addPropertyChangeListener(this);
      jFormattedTextField.setEditable(false);
      jFormattedTextField.setInheritsPopupMenu(true);
      String str = param1JSpinner.getToolTipText();
      if (str != null)
        jFormattedTextField.setToolTipText(str); 
      add(jFormattedTextField);
      setLayout(this);
      param1JSpinner.addChangeListener(this);
      ActionMap actionMap = jFormattedTextField.getActionMap();
      if (actionMap != null) {
        actionMap.put("increment", DISABLED_ACTION);
        actionMap.put("decrement", DISABLED_ACTION);
      } 
    }
    
    public void dismiss(JSpinner param1JSpinner) { param1JSpinner.removeChangeListener(this); }
    
    public JSpinner getSpinner() {
      DefaultEditor defaultEditor = this;
      while (defaultEditor != null) {
        if (defaultEditor instanceof JSpinner)
          return (JSpinner)defaultEditor; 
        Container container = defaultEditor.getParent();
      } 
      return null;
    }
    
    public JFormattedTextField getTextField() { return (JFormattedTextField)getComponent(0); }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      JSpinner jSpinner = (JSpinner)param1ChangeEvent.getSource();
      getTextField().setValue(jSpinner.getValue());
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      JSpinner jSpinner = getSpinner();
      if (jSpinner == null)
        return; 
      Object object = param1PropertyChangeEvent.getSource();
      String str = param1PropertyChangeEvent.getPropertyName();
      if (object instanceof JFormattedTextField && "value".equals(str)) {
        Object object1 = jSpinner.getValue();
        try {
          jSpinner.setValue(getTextField().getValue());
        } catch (IllegalArgumentException illegalArgumentException) {
          try {
            ((JFormattedTextField)object).setValue(object1);
          } catch (IllegalArgumentException illegalArgumentException1) {}
        } 
      } 
    }
    
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void removeLayoutComponent(Component param1Component) {}
    
    private Dimension insetSize(Container param1Container) {
      Insets insets = param1Container.getInsets();
      int i = insets.left + insets.right;
      int j = insets.top + insets.bottom;
      return new Dimension(i, j);
    }
    
    public Dimension preferredLayoutSize(Container param1Container) {
      Dimension dimension = insetSize(param1Container);
      if (param1Container.getComponentCount() > 0) {
        Dimension dimension1 = getComponent(0).getPreferredSize();
        dimension.width += dimension1.width;
        dimension.height += dimension1.height;
      } 
      return dimension;
    }
    
    public Dimension minimumLayoutSize(Container param1Container) {
      Dimension dimension = insetSize(param1Container);
      if (param1Container.getComponentCount() > 0) {
        Dimension dimension1 = getComponent(0).getMinimumSize();
        dimension.width += dimension1.width;
        dimension.height += dimension1.height;
      } 
      return dimension;
    }
    
    public void layoutContainer(Container param1Container) {
      if (param1Container.getComponentCount() > 0) {
        Insets insets = param1Container.getInsets();
        int i = param1Container.getWidth() - insets.left + insets.right;
        int j = param1Container.getHeight() - insets.top + insets.bottom;
        getComponent(0).setBounds(insets.left, insets.top, i, j);
      } 
    }
    
    public void commitEdit() {
      JFormattedTextField jFormattedTextField = getTextField();
      jFormattedTextField.commitEdit();
    }
    
    public int getBaseline(int param1Int1, int param1Int2) {
      super.getBaseline(param1Int1, param1Int2);
      Insets insets = getInsets();
      param1Int1 = param1Int1 - insets.left - insets.right;
      param1Int2 = param1Int2 - insets.top - insets.bottom;
      int i = getComponent(0).getBaseline(param1Int1, param1Int2);
      return (i >= 0) ? (i + insets.top) : -1;
    }
    
    public Component.BaselineResizeBehavior getBaselineResizeBehavior() { return getComponent(0).getBaselineResizeBehavior(); }
  }
  
  private static class DisabledAction implements Action {
    private DisabledAction() {}
    
    public Object getValue(String param1String) { return null; }
    
    public void putValue(String param1String, Object param1Object) {}
    
    public void setEnabled(boolean param1Boolean) {}
    
    public boolean isEnabled() { return false; }
    
    public void addPropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {}
    
    public void removePropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {}
    
    public void actionPerformed(ActionEvent param1ActionEvent) {}
  }
  
  public static class ListEditor extends DefaultEditor {
    public ListEditor(JSpinner param1JSpinner) {
      super(param1JSpinner);
      if (!(param1JSpinner.getModel() instanceof SpinnerListModel))
        throw new IllegalArgumentException("model not a SpinnerListModel"); 
      getTextField().setEditable(true);
      getTextField().setFormatterFactory(new DefaultFormatterFactory(new ListFormatter(this, null)));
    }
    
    public SpinnerListModel getModel() { return (SpinnerListModel)getSpinner().getModel(); }
    
    private class ListFormatter extends JFormattedTextField.AbstractFormatter {
      private DocumentFilter filter;
      
      private ListFormatter() {}
      
      public String valueToString(Object param2Object) throws ParseException { return (param2Object == null) ? "" : param2Object.toString(); }
      
      public Object stringToValue(String param2String) { return param2String; }
      
      protected DocumentFilter getDocumentFilter() {
        if (this.filter == null)
          this.filter = new Filter(null); 
        return this.filter;
      }
      
      private class Filter extends DocumentFilter {
        private Filter() {}
        
        public void replace(DocumentFilter.FilterBypass param3FilterBypass, int param3Int1, int param3Int2, String param3String, AttributeSet param3AttributeSet) throws BadLocationException {
          if (param3String != null && param3Int1 + param3Int2 == param3FilterBypass.getDocument().getLength()) {
            Object object = JSpinner.ListEditor.ListFormatter.this.this$0.getModel().findNextMatch(param3FilterBypass.getDocument().getText(0, param3Int1) + param3String);
            String str = (object != null) ? object.toString() : null;
            if (str != null) {
              param3FilterBypass.remove(0, param3Int1 + param3Int2);
              param3FilterBypass.insertString(0, str, null);
              JSpinner.ListEditor.ListFormatter.this.getFormattedTextField().select(param3Int1 + param3String.length(), str.length());
              return;
            } 
          } 
          super.replace(param3FilterBypass, param3Int1, param3Int2, param3String, param3AttributeSet);
        }
        
        public void insertString(DocumentFilter.FilterBypass param3FilterBypass, int param3Int, String param3String, AttributeSet param3AttributeSet) throws BadLocationException { replace(param3FilterBypass, param3Int, 0, param3String, param3AttributeSet); }
      }
    }
  }
  
  private class ModelListener implements ChangeListener, Serializable {
    private ModelListener() {}
    
    public void stateChanged(ChangeEvent param1ChangeEvent) { JSpinner.this.fireStateChanged(); }
  }
  
  public static class NumberEditor extends DefaultEditor {
    private static String getDefaultPattern(Locale param1Locale) {
      LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter(java.text.spi.NumberFormatProvider.class, param1Locale);
      LocaleResources localeResources = localeProviderAdapter.getLocaleResources(param1Locale);
      if (localeResources == null)
        localeResources = LocaleProviderAdapter.forJRE().getLocaleResources(param1Locale); 
      String[] arrayOfString = localeResources.getNumberPatterns();
      return arrayOfString[0];
    }
    
    public NumberEditor(JSpinner param1JSpinner) { this(param1JSpinner, getDefaultPattern(param1JSpinner.getLocale())); }
    
    public NumberEditor(JSpinner param1JSpinner, String param1String) { this(param1JSpinner, new DecimalFormat(param1String)); }
    
    private NumberEditor(JSpinner param1JSpinner, DecimalFormat param1DecimalFormat) {
      super(param1JSpinner);
      if (!(param1JSpinner.getModel() instanceof SpinnerNumberModel))
        throw new IllegalArgumentException("model not a SpinnerNumberModel"); 
      SpinnerNumberModel spinnerNumberModel = (SpinnerNumberModel)param1JSpinner.getModel();
      JSpinner.NumberEditorFormatter numberEditorFormatter = new JSpinner.NumberEditorFormatter(spinnerNumberModel, param1DecimalFormat);
      DefaultFormatterFactory defaultFormatterFactory = new DefaultFormatterFactory(numberEditorFormatter);
      JFormattedTextField jFormattedTextField = getTextField();
      jFormattedTextField.setEditable(true);
      jFormattedTextField.setFormatterFactory(defaultFormatterFactory);
      jFormattedTextField.setHorizontalAlignment(4);
      try {
        String str1 = numberEditorFormatter.valueToString(spinnerNumberModel.getMinimum());
        String str2 = numberEditorFormatter.valueToString(spinnerNumberModel.getMaximum());
        jFormattedTextField.setColumns(Math.max(str1.length(), str2.length()));
      } catch (ParseException parseException) {}
    }
    
    public DecimalFormat getFormat() { return (DecimalFormat)((NumberFormatter)getTextField().getFormatter()).getFormat(); }
    
    public SpinnerNumberModel getModel() { return (SpinnerNumberModel)getSpinner().getModel(); }
  }
  
  private static class NumberEditorFormatter extends NumberFormatter {
    private final SpinnerNumberModel model;
    
    NumberEditorFormatter(SpinnerNumberModel param1SpinnerNumberModel, NumberFormat param1NumberFormat) {
      super(param1NumberFormat);
      this.model = param1SpinnerNumberModel;
      setValueClass(param1SpinnerNumberModel.getValue().getClass());
    }
    
    public void setMinimum(Comparable param1Comparable) { this.model.setMinimum(param1Comparable); }
    
    public Comparable getMinimum() { return this.model.getMinimum(); }
    
    public void setMaximum(Comparable param1Comparable) { this.model.setMaximum(param1Comparable); }
    
    public Comparable getMaximum() { return this.model.getMaximum(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JSpinner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */