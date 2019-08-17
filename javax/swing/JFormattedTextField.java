package javax.swing;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputMethodEvent;
import java.awt.im.InputContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.JTextComponent;
import javax.swing.text.NavigationFilter;
import javax.swing.text.NumberFormatter;
import javax.swing.text.TextAction;

public class JFormattedTextField extends JTextField {
  private static final String uiClassID = "FormattedTextFieldUI";
  
  private static final Action[] defaultActions = { new CommitAction(), new CancelAction() };
  
  public static final int COMMIT = 0;
  
  public static final int COMMIT_OR_REVERT = 1;
  
  public static final int REVERT = 2;
  
  public static final int PERSIST = 3;
  
  private AbstractFormatterFactory factory;
  
  private AbstractFormatter format;
  
  private Object value;
  
  private boolean editValid;
  
  private int focusLostBehavior;
  
  private boolean edited;
  
  private DocumentListener documentListener;
  
  private Object mask;
  
  private ActionMap textFormatterActionMap;
  
  private boolean composedTextExists = false;
  
  private FocusLostHandler focusLostHandler;
  
  public JFormattedTextField() {
    enableEvents(4L);
    setFocusLostBehavior(1);
  }
  
  public JFormattedTextField(Object paramObject) {
    this();
    setValue(paramObject);
  }
  
  public JFormattedTextField(Format paramFormat) {
    this();
    setFormatterFactory(getDefaultFormatterFactory(paramFormat));
  }
  
  public JFormattedTextField(AbstractFormatter paramAbstractFormatter) { this(new DefaultFormatterFactory(paramAbstractFormatter)); }
  
  public JFormattedTextField(AbstractFormatterFactory paramAbstractFormatterFactory) {
    this();
    setFormatterFactory(paramAbstractFormatterFactory);
  }
  
  public JFormattedTextField(AbstractFormatterFactory paramAbstractFormatterFactory, Object paramObject) {
    this(paramObject);
    setFormatterFactory(paramAbstractFormatterFactory);
  }
  
  public void setFocusLostBehavior(int paramInt) {
    if (paramInt != 0 && paramInt != 1 && paramInt != 3 && paramInt != 2)
      throw new IllegalArgumentException("setFocusLostBehavior must be one of: JFormattedTextField.COMMIT, JFormattedTextField.COMMIT_OR_REVERT, JFormattedTextField.PERSIST or JFormattedTextField.REVERT"); 
    this.focusLostBehavior = paramInt;
  }
  
  public int getFocusLostBehavior() { return this.focusLostBehavior; }
  
  public void setFormatterFactory(AbstractFormatterFactory paramAbstractFormatterFactory) {
    AbstractFormatterFactory abstractFormatterFactory = this.factory;
    this.factory = paramAbstractFormatterFactory;
    firePropertyChange("formatterFactory", abstractFormatterFactory, paramAbstractFormatterFactory);
    setValue(getValue(), true, false);
  }
  
  public AbstractFormatterFactory getFormatterFactory() { return this.factory; }
  
  protected void setFormatter(AbstractFormatter paramAbstractFormatter) {
    AbstractFormatter abstractFormatter = this.format;
    if (abstractFormatter != null)
      abstractFormatter.uninstall(); 
    setEditValid(true);
    this.format = paramAbstractFormatter;
    if (paramAbstractFormatter != null)
      paramAbstractFormatter.install(this); 
    setEdited(false);
    firePropertyChange("textFormatter", abstractFormatter, paramAbstractFormatter);
  }
  
  public AbstractFormatter getFormatter() { return this.format; }
  
  public void setValue(Object paramObject) {
    if (paramObject != null && getFormatterFactory() == null)
      setFormatterFactory(getDefaultFormatterFactory(paramObject)); 
    setValue(paramObject, true, true);
  }
  
  public Object getValue() { return this.value; }
  
  public void commitEdit() {
    AbstractFormatter abstractFormatter = getFormatter();
    if (abstractFormatter != null)
      setValue(abstractFormatter.stringToValue(getText()), false, true); 
  }
  
  private void setEditValid(boolean paramBoolean) {
    if (paramBoolean != this.editValid) {
      this.editValid = paramBoolean;
      firePropertyChange("editValid", Boolean.valueOf(!paramBoolean), Boolean.valueOf(paramBoolean));
    } 
  }
  
  public boolean isEditValid() { return this.editValid; }
  
  protected void invalidEdit() { UIManager.getLookAndFeel().provideErrorFeedback(this); }
  
  protected void processInputMethodEvent(InputMethodEvent paramInputMethodEvent) {
    AttributedCharacterIterator attributedCharacterIterator = paramInputMethodEvent.getText();
    int i = paramInputMethodEvent.getCommittedCharacterCount();
    if (attributedCharacterIterator != null) {
      int j = attributedCharacterIterator.getBeginIndex();
      int k = attributedCharacterIterator.getEndIndex();
      this.composedTextExists = (k - j > i);
    } else {
      this.composedTextExists = false;
    } 
    super.processInputMethodEvent(paramInputMethodEvent);
  }
  
  protected void processFocusEvent(FocusEvent paramFocusEvent) {
    super.processFocusEvent(paramFocusEvent);
    if (paramFocusEvent.isTemporary())
      return; 
    if (isEdited() && paramFocusEvent.getID() == 1005) {
      InputContext inputContext = getInputContext();
      if (this.focusLostHandler == null)
        this.focusLostHandler = new FocusLostHandler(null); 
      if (inputContext != null && this.composedTextExists) {
        inputContext.endComposition();
        EventQueue.invokeLater(this.focusLostHandler);
      } else {
        this.focusLostHandler.run();
      } 
    } else if (!isEdited()) {
      setValue(getValue(), true, true);
    } 
  }
  
  public Action[] getActions() { return TextAction.augmentList(super.getActions(), defaultActions); }
  
  public String getUIClassID() { return "FormattedTextFieldUI"; }
  
  public void setDocument(Document paramDocument) {
    if (this.documentListener != null && getDocument() != null)
      getDocument().removeDocumentListener(this.documentListener); 
    super.setDocument(paramDocument);
    if (this.documentListener == null)
      this.documentListener = new DocumentHandler(null); 
    paramDocument.addDocumentListener(this.documentListener);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("FormattedTextFieldUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  private void setFormatterActions(Action[] paramArrayOfAction) {
    if (paramArrayOfAction == null) {
      if (this.textFormatterActionMap != null)
        this.textFormatterActionMap.clear(); 
    } else {
      if (this.textFormatterActionMap == null) {
        ActionMap actionMap = getActionMap();
        this.textFormatterActionMap = new ActionMap();
        while (actionMap != null) {
          ActionMap actionMap1 = actionMap.getParent();
          if (actionMap1 instanceof javax.swing.plaf.UIResource || actionMap1 == null) {
            actionMap.setParent(this.textFormatterActionMap);
            this.textFormatterActionMap.setParent(actionMap1);
            break;
          } 
          actionMap = actionMap1;
        } 
      } 
      for (int i = paramArrayOfAction.length - 1; i >= 0; i--) {
        Object object = paramArrayOfAction[i].getValue("Name");
        if (object != null)
          this.textFormatterActionMap.put(object, paramArrayOfAction[i]); 
      } 
    } 
  }
  
  private void setValue(Object paramObject, boolean paramBoolean1, boolean paramBoolean2) {
    Object object = this.value;
    this.value = paramObject;
    if (paramBoolean1) {
      AbstractFormatter abstractFormatter;
      AbstractFormatterFactory abstractFormatterFactory = getFormatterFactory();
      if (abstractFormatterFactory != null) {
        abstractFormatter = abstractFormatterFactory.getFormatter(this);
      } else {
        abstractFormatter = null;
      } 
      setFormatter(abstractFormatter);
    } else {
      setEditValid(true);
    } 
    setEdited(false);
    if (paramBoolean2)
      firePropertyChange("value", object, paramObject); 
  }
  
  private void setEdited(boolean paramBoolean) { this.edited = paramBoolean; }
  
  private boolean isEdited() { return this.edited; }
  
  private AbstractFormatterFactory getDefaultFormatterFactory(Object paramObject) {
    if (paramObject instanceof DateFormat)
      return new DefaultFormatterFactory(new DateFormatter((DateFormat)paramObject)); 
    if (paramObject instanceof NumberFormat)
      return new DefaultFormatterFactory(new NumberFormatter((NumberFormat)paramObject)); 
    if (paramObject instanceof Format)
      return new DefaultFormatterFactory(new InternationalFormatter((Format)paramObject)); 
    if (paramObject instanceof java.util.Date)
      return new DefaultFormatterFactory(new DateFormatter()); 
    if (paramObject instanceof Number) {
      NumberFormatter numberFormatter1 = new NumberFormatter();
      ((NumberFormatter)numberFormatter1).setValueClass(paramObject.getClass());
      NumberFormatter numberFormatter2 = new NumberFormatter(new DecimalFormat("#.#"));
      ((NumberFormatter)numberFormatter2).setValueClass(paramObject.getClass());
      return new DefaultFormatterFactory(numberFormatter1, numberFormatter1, numberFormatter2);
    } 
    return new DefaultFormatterFactory(new DefaultFormatter());
  }
  
  public static abstract class AbstractFormatter implements Serializable {
    private JFormattedTextField ftf;
    
    public void install(JFormattedTextField param1JFormattedTextField) {
      if (this.ftf != null)
        uninstall(); 
      this.ftf = param1JFormattedTextField;
      if (param1JFormattedTextField != null) {
        try {
          param1JFormattedTextField.setText(valueToString(param1JFormattedTextField.getValue()));
        } catch (ParseException parseException) {
          param1JFormattedTextField.setText("");
          setEditValid(false);
        } 
        installDocumentFilter(getDocumentFilter());
        param1JFormattedTextField.setNavigationFilter(getNavigationFilter());
        param1JFormattedTextField.setFormatterActions(getActions());
      } 
    }
    
    public void uninstall() {
      if (this.ftf != null) {
        installDocumentFilter(null);
        this.ftf.setNavigationFilter(null);
        this.ftf.setFormatterActions(null);
      } 
    }
    
    public abstract Object stringToValue(String param1String) throws ParseException;
    
    public abstract String valueToString(Object param1Object) throws ParseException;
    
    protected JFormattedTextField getFormattedTextField() { return this.ftf; }
    
    protected void invalidEdit() {
      JFormattedTextField jFormattedTextField = getFormattedTextField();
      if (jFormattedTextField != null)
        jFormattedTextField.invalidEdit(); 
    }
    
    protected void setEditValid(boolean param1Boolean) {
      JFormattedTextField jFormattedTextField = getFormattedTextField();
      if (jFormattedTextField != null)
        jFormattedTextField.setEditValid(param1Boolean); 
    }
    
    protected Action[] getActions() { return null; }
    
    protected DocumentFilter getDocumentFilter() { return null; }
    
    protected NavigationFilter getNavigationFilter() { return null; }
    
    protected Object clone() {
      AbstractFormatter abstractFormatter = (AbstractFormatter)super.clone();
      abstractFormatter.ftf = null;
      return abstractFormatter;
    }
    
    private void installDocumentFilter(DocumentFilter param1DocumentFilter) {
      JFormattedTextField jFormattedTextField = getFormattedTextField();
      if (jFormattedTextField != null) {
        Document document = jFormattedTextField.getDocument();
        if (document instanceof AbstractDocument)
          ((AbstractDocument)document).setDocumentFilter(param1DocumentFilter); 
        document.putProperty(DocumentFilter.class, null);
      } 
    }
  }
  
  public static abstract class AbstractFormatterFactory {
    public abstract JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField param1JFormattedTextField);
  }
  
  private static class CancelAction extends TextAction {
    public CancelAction() { super("reset-field-edit"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getFocusedComponent();
      if (jTextComponent instanceof JFormattedTextField) {
        JFormattedTextField jFormattedTextField = (JFormattedTextField)jTextComponent;
        jFormattedTextField.setValue(jFormattedTextField.getValue());
      } 
    }
    
    public boolean isEnabled() {
      JTextComponent jTextComponent = getFocusedComponent();
      return (jTextComponent instanceof JFormattedTextField) ? (!!jFormattedTextField.isEdited()) : super.isEnabled();
    }
  }
  
  static class CommitAction extends JTextField.NotifyAction {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getFocusedComponent();
      if (jTextComponent instanceof JFormattedTextField)
        try {
          ((JFormattedTextField)jTextComponent).commitEdit();
        } catch (ParseException parseException) {
          ((JFormattedTextField)jTextComponent).invalidEdit();
          return;
        }  
      super.actionPerformed(param1ActionEvent);
    }
    
    public boolean isEnabled() {
      JTextComponent jTextComponent = getFocusedComponent();
      return (jTextComponent instanceof JFormattedTextField) ? (!!jFormattedTextField.isEdited()) : super.isEnabled();
    }
  }
  
  private class DocumentHandler implements DocumentListener, Serializable {
    private DocumentHandler() {}
    
    public void insertUpdate(DocumentEvent param1DocumentEvent) { JFormattedTextField.this.setEdited(true); }
    
    public void removeUpdate(DocumentEvent param1DocumentEvent) { JFormattedTextField.this.setEdited(true); }
    
    public void changedUpdate(DocumentEvent param1DocumentEvent) {}
  }
  
  private class FocusLostHandler implements Runnable, Serializable {
    private FocusLostHandler() {}
    
    public void run() {
      int i = JFormattedTextField.this.getFocusLostBehavior();
      if (i == 0 || i == 1) {
        try {
          JFormattedTextField.this.commitEdit();
          JFormattedTextField.this.setValue(JFormattedTextField.this.getValue(), true, true);
        } catch (ParseException parseException) {
          JFormattedTextField.this;
          if (i == 1)
            JFormattedTextField.this.setValue(JFormattedTextField.this.getValue(), true, true); 
        } 
      } else if (i == 2) {
        JFormattedTextField.this.setValue(JFormattedTextField.this.getValue(), true, true);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JFormattedTextField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */