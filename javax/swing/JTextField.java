package javax.swing;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.TextAction;

public class JTextField extends JTextComponent implements SwingConstants {
  private Action action;
  
  private PropertyChangeListener actionPropertyChangeListener;
  
  public static final String notifyAction = "notify-field-accept";
  
  private BoundedRangeModel visibility;
  
  private int horizontalAlignment = 10;
  
  private int columns;
  
  private int columnWidth;
  
  private String command;
  
  private static final Action[] defaultActions = { new NotifyAction() };
  
  private static final String uiClassID = "TextFieldUI";
  
  public JTextField() { this(null, null, 0); }
  
  public JTextField(String paramString) { this(null, paramString, 0); }
  
  public JTextField(int paramInt) { this(null, null, paramInt); }
  
  public JTextField(String paramString, int paramInt) { this(null, paramString, paramInt); }
  
  public JTextField(Document paramDocument, String paramString, int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("columns less than zero."); 
    this.visibility = new DefaultBoundedRangeModel();
    this.visibility.addChangeListener(new ScrollRepainter());
    this.columns = paramInt;
    if (paramDocument == null)
      paramDocument = createDefaultModel(); 
    setDocument(paramDocument);
    if (paramString != null)
      setText(paramString); 
  }
  
  public String getUIClassID() { return "TextFieldUI"; }
  
  public void setDocument(Document paramDocument) {
    if (paramDocument != null)
      paramDocument.putProperty("filterNewlines", Boolean.TRUE); 
    super.setDocument(paramDocument);
  }
  
  public boolean isValidateRoot() { return !(SwingUtilities.getUnwrappedParent(this) instanceof JViewport); }
  
  public int getHorizontalAlignment() { return this.horizontalAlignment; }
  
  public void setHorizontalAlignment(int paramInt) {
    if (paramInt == this.horizontalAlignment)
      return; 
    int i = this.horizontalAlignment;
    if (paramInt == 2 || paramInt == 0 || paramInt == 4 || paramInt == 10 || paramInt == 11) {
      this.horizontalAlignment = paramInt;
    } else {
      throw new IllegalArgumentException("horizontalAlignment");
    } 
    firePropertyChange("horizontalAlignment", i, this.horizontalAlignment);
    invalidate();
    repaint();
  }
  
  protected Document createDefaultModel() { return new PlainDocument(); }
  
  public int getColumns() { return this.columns; }
  
  public void setColumns(int paramInt) {
    int i = this.columns;
    if (paramInt < 0)
      throw new IllegalArgumentException("columns less than zero."); 
    if (paramInt != i) {
      this.columns = paramInt;
      invalidate();
    } 
  }
  
  protected int getColumnWidth() {
    if (this.columnWidth == 0) {
      FontMetrics fontMetrics = getFontMetrics(getFont());
      this.columnWidth = fontMetrics.charWidth('m');
    } 
    return this.columnWidth;
  }
  
  public Dimension getPreferredSize() {
    Dimension dimension = super.getPreferredSize();
    if (this.columns != 0) {
      Insets insets = getInsets();
      dimension.width = this.columns * getColumnWidth() + insets.left + insets.right;
    } 
    return dimension;
  }
  
  public void setFont(Font paramFont) {
    super.setFont(paramFont);
    this.columnWidth = 0;
  }
  
  public void addActionListener(ActionListener paramActionListener) { this.listenerList.add(ActionListener.class, paramActionListener); }
  
  public void removeActionListener(ActionListener paramActionListener) {
    if (paramActionListener != null && getAction() == paramActionListener) {
      setAction(null);
    } else {
      this.listenerList.remove(ActionListener.class, paramActionListener);
    } 
  }
  
  public ActionListener[] getActionListeners() { return (ActionListener[])this.listenerList.getListeners(ActionListener.class); }
  
  protected void fireActionPerformed() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    int i = 0;
    AWTEvent aWTEvent = EventQueue.getCurrentEvent();
    if (aWTEvent instanceof InputEvent) {
      i = ((InputEvent)aWTEvent).getModifiers();
    } else if (aWTEvent instanceof ActionEvent) {
      i = ((ActionEvent)aWTEvent).getModifiers();
    } 
    ActionEvent actionEvent = new ActionEvent(this, 1001, (this.command != null) ? this.command : getText(), EventQueue.getMostRecentEventTime(), i);
    for (int j = arrayOfObject.length - 2; j >= 0; j -= 2) {
      if (arrayOfObject[j] == ActionListener.class)
        ((ActionListener)arrayOfObject[j + 1]).actionPerformed(actionEvent); 
    } 
  }
  
  public void setActionCommand(String paramString) { this.command = paramString; }
  
  public void setAction(Action paramAction) {
    Action action1 = getAction();
    if (this.action == null || !this.action.equals(paramAction)) {
      this.action = paramAction;
      if (action1 != null) {
        removeActionListener(action1);
        action1.removePropertyChangeListener(this.actionPropertyChangeListener);
        this.actionPropertyChangeListener = null;
      } 
      configurePropertiesFromAction(this.action);
      if (this.action != null) {
        if (!isListener(ActionListener.class, this.action))
          addActionListener(this.action); 
        this.actionPropertyChangeListener = createActionPropertyChangeListener(this.action);
        this.action.addPropertyChangeListener(this.actionPropertyChangeListener);
      } 
      firePropertyChange("action", action1, this.action);
    } 
  }
  
  private boolean isListener(Class paramClass, ActionListener paramActionListener) {
    boolean bool = false;
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == paramClass && arrayOfObject[i + true] == paramActionListener)
        bool = true; 
    } 
    return bool;
  }
  
  public Action getAction() { return this.action; }
  
  protected void configurePropertiesFromAction(Action paramAction) {
    AbstractAction.setEnabledFromAction(this, paramAction);
    AbstractAction.setToolTipTextFromAction(this, paramAction);
    setActionCommandFromAction(paramAction);
  }
  
  protected void actionPropertyChanged(Action paramAction, String paramString) {
    if (paramString == "ActionCommandKey") {
      setActionCommandFromAction(paramAction);
    } else if (paramString == "enabled") {
      AbstractAction.setEnabledFromAction(this, paramAction);
    } else if (paramString == "ShortDescription") {
      AbstractAction.setToolTipTextFromAction(this, paramAction);
    } 
  }
  
  private void setActionCommandFromAction(Action paramAction) { setActionCommand((paramAction == null) ? null : (String)paramAction.getValue("ActionCommandKey")); }
  
  protected PropertyChangeListener createActionPropertyChangeListener(Action paramAction) { return new TextFieldActionPropertyChangeListener(this, paramAction); }
  
  public Action[] getActions() { return TextAction.augmentList(super.getActions(), defaultActions); }
  
  public void postActionEvent() { fireActionPerformed(); }
  
  public BoundedRangeModel getHorizontalVisibility() { return this.visibility; }
  
  public int getScrollOffset() { return this.visibility.getValue(); }
  
  public void setScrollOffset(int paramInt) { this.visibility.setValue(paramInt); }
  
  public void scrollRectToVisible(Rectangle paramRectangle) {
    Insets insets = getInsets();
    int i = paramRectangle.x + this.visibility.getValue() - insets.left;
    int j = i + paramRectangle.width;
    if (i < this.visibility.getValue()) {
      this.visibility.setValue(i);
    } else if (j > this.visibility.getValue() + this.visibility.getExtent()) {
      this.visibility.setValue(j - this.visibility.getExtent());
    } 
  }
  
  boolean hasActionListener() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ActionListener.class)
        return true; 
    } 
    return false;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("TextFieldUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    String str1;
    if (this.horizontalAlignment == 2) {
      str1 = "LEFT";
    } else if (this.horizontalAlignment == 0) {
      str1 = "CENTER";
    } else if (this.horizontalAlignment == 4) {
      str1 = "RIGHT";
    } else if (this.horizontalAlignment == 10) {
      str1 = "LEADING";
    } else if (this.horizontalAlignment == 11) {
      str1 = "TRAILING";
    } else {
      str1 = "";
    } 
    String str2 = (this.command != null) ? this.command : "";
    return super.paramString() + ",columns=" + this.columns + ",columnWidth=" + this.columnWidth + ",command=" + str2 + ",horizontalAlignment=" + str1;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJTextField(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJTextField extends JTextComponent.AccessibleJTextComponent {
    protected AccessibleJTextField() { super(JTextField.this); }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      accessibleStateSet.add(AccessibleState.SINGLE_LINE);
      return accessibleStateSet;
    }
  }
  
  static class NotifyAction extends TextAction {
    NotifyAction() { super("notify-field-accept"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getFocusedComponent();
      if (jTextComponent instanceof JTextField) {
        JTextField jTextField = (JTextField)jTextComponent;
        jTextField.postActionEvent();
      } 
    }
    
    public boolean isEnabled() {
      JTextComponent jTextComponent = getFocusedComponent();
      return (jTextComponent instanceof JTextField) ? ((JTextField)jTextComponent).hasActionListener() : 0;
    }
  }
  
  class ScrollRepainter implements ChangeListener, Serializable {
    public void stateChanged(ChangeEvent param1ChangeEvent) { JTextField.this.repaint(); }
  }
  
  private static class TextFieldActionPropertyChangeListener extends ActionPropertyChangeListener<JTextField> {
    TextFieldActionPropertyChangeListener(JTextField param1JTextField, Action param1Action) { super(param1JTextField, param1Action); }
    
    protected void actionPropertyChanged(JTextField param1JTextField, Action param1Action, PropertyChangeEvent param1PropertyChangeEvent) {
      if (AbstractAction.shouldReconfigure(param1PropertyChangeEvent)) {
        param1JTextField.configurePropertiesFromAction(param1Action);
      } else {
        param1JTextField.actionPropertyChanged(param1Action, param1PropertyChangeEvent.getPropertyName());
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JTextField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */