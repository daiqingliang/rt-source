package javax.swing.text;

import java.io.Serializable;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import sun.reflect.misc.ReflectUtil;
import sun.swing.SwingUtilities2;

public class DefaultFormatter extends JFormattedTextField.AbstractFormatter implements Cloneable, Serializable {
  private boolean allowsInvalid = true;
  
  private boolean overwriteMode = true;
  
  private boolean commitOnEdit;
  
  private Class<?> valueClass;
  
  private NavigationFilter navigationFilter;
  
  private DocumentFilter documentFilter;
  
  ReplaceHolder replaceHolder;
  
  public void install(JFormattedTextField paramJFormattedTextField) {
    super.install(paramJFormattedTextField);
    positionCursorAtInitialLocation();
  }
  
  public void setCommitsOnValidEdit(boolean paramBoolean) { this.commitOnEdit = paramBoolean; }
  
  public boolean getCommitsOnValidEdit() { return this.commitOnEdit; }
  
  public void setOverwriteMode(boolean paramBoolean) { this.overwriteMode = paramBoolean; }
  
  public boolean getOverwriteMode() { return this.overwriteMode; }
  
  public void setAllowsInvalid(boolean paramBoolean) { this.allowsInvalid = paramBoolean; }
  
  public boolean getAllowsInvalid() { return this.allowsInvalid; }
  
  public void setValueClass(Class<?> paramClass) { this.valueClass = paramClass; }
  
  public Class<?> getValueClass() { return this.valueClass; }
  
  public Object stringToValue(String paramString) throws ParseException {
    Class clazz = getValueClass();
    JFormattedTextField jFormattedTextField = getFormattedTextField();
    if (clazz == null && jFormattedTextField != null) {
      Object object = jFormattedTextField.getValue();
      if (object != null)
        clazz = object.getClass(); 
    } 
    if (clazz != null) {
      Object object;
      try {
        ReflectUtil.checkPackageAccess(clazz);
        SwingUtilities2.checkAccess(clazz.getModifiers());
        object = clazz.getConstructor(new Class[] { String.class });
      } catch (NoSuchMethodException noSuchMethodException) {
        object = null;
      } 
      if (object != null)
        try {
          SwingUtilities2.checkAccess(object.getModifiers());
          return object.newInstance(new Object[] { paramString });
        } catch (Throwable throwable) {
          throw new ParseException("Error creating instance", 0);
        }  
    } 
    return paramString;
  }
  
  public String valueToString(Object paramObject) throws ParseException { return (paramObject == null) ? "" : paramObject.toString(); }
  
  protected DocumentFilter getDocumentFilter() {
    if (this.documentFilter == null)
      this.documentFilter = new DefaultDocumentFilter(null); 
    return this.documentFilter;
  }
  
  protected NavigationFilter getNavigationFilter() {
    if (this.navigationFilter == null)
      this.navigationFilter = new DefaultNavigationFilter(null); 
    return this.navigationFilter;
  }
  
  public Object clone() throws CloneNotSupportedException {
    DefaultFormatter defaultFormatter = (DefaultFormatter)super.clone();
    defaultFormatter.navigationFilter = null;
    defaultFormatter.documentFilter = null;
    defaultFormatter.replaceHolder = null;
    return defaultFormatter;
  }
  
  void positionCursorAtInitialLocation() {
    JFormattedTextField jFormattedTextField = getFormattedTextField();
    if (jFormattedTextField != null)
      jFormattedTextField.setCaretPosition(getInitialVisualPosition()); 
  }
  
  int getInitialVisualPosition() { return getNextNavigatableChar(0, 1); }
  
  boolean isNavigatable(int paramInt) { return true; }
  
  boolean isLegalInsertText(String paramString) { return true; }
  
  private int getNextNavigatableChar(int paramInt1, int paramInt2) {
    int i = getFormattedTextField().getDocument().getLength();
    while (paramInt1 >= 0 && paramInt1 < i) {
      if (isNavigatable(paramInt1))
        return paramInt1; 
      paramInt1 += paramInt2;
    } 
    return paramInt1;
  }
  
  String getReplaceString(int paramInt1, int paramInt2, String paramString) {
    String str1 = getFormattedTextField().getText();
    String str2 = str1.substring(0, paramInt1);
    if (paramString != null)
      str2 = str2 + paramString; 
    if (paramInt1 + paramInt2 < str1.length())
      str2 = str2 + str1.substring(paramInt1 + paramInt2); 
    return str2;
  }
  
  boolean isValidEdit(ReplaceHolder paramReplaceHolder) {
    if (!getAllowsInvalid()) {
      String str = getReplaceString(paramReplaceHolder.offset, paramReplaceHolder.length, paramReplaceHolder.text);
      try {
        paramReplaceHolder.value = stringToValue(str);
        return true;
      } catch (ParseException parseException) {
        return false;
      } 
    } 
    return true;
  }
  
  void commitEdit() {
    JFormattedTextField jFormattedTextField = getFormattedTextField();
    if (jFormattedTextField != null)
      jFormattedTextField.commitEdit(); 
  }
  
  void updateValue() { updateValue(null); }
  
  void updateValue(Object paramObject) {
    try {
      if (paramObject == null) {
        String str = getFormattedTextField().getText();
        paramObject = stringToValue(str);
      } 
      if (getCommitsOnValidEdit())
        commitEdit(); 
      setEditValid(true);
    } catch (ParseException parseException) {
      setEditValid(false);
    } 
  }
  
  int getNextCursorPosition(int paramInt1, int paramInt2) {
    int i = getNextNavigatableChar(paramInt1, paramInt2);
    int j = getFormattedTextField().getDocument().getLength();
    if (!getAllowsInvalid())
      if (paramInt2 == -1 && paramInt1 == i) {
        i = getNextNavigatableChar(i, 1);
        if (i >= j)
          i = paramInt1; 
      } else if (paramInt2 == 1 && i >= j) {
        i = getNextNavigatableChar(j - 1, -1);
        if (i < j)
          i++; 
      }  
    return i;
  }
  
  void repositionCursor(int paramInt1, int paramInt2) { getFormattedTextField().getCaret().setDot(getNextCursorPosition(paramInt1, paramInt2)); }
  
  int getNextVisualPositionFrom(JTextComponent paramJTextComponent, int paramInt1, Position.Bias paramBias, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException {
    int i = paramJTextComponent.getUI().getNextVisualPositionFrom(paramJTextComponent, paramInt1, paramBias, paramInt2, paramArrayOfBias);
    if (i == -1)
      return -1; 
    if (!getAllowsInvalid() && (paramInt2 == 3 || paramInt2 == 7)) {
      int j = -1;
      while (!isNavigatable(i) && i != j) {
        j = i;
        i = paramJTextComponent.getUI().getNextVisualPositionFrom(paramJTextComponent, i, paramBias, paramInt2, paramArrayOfBias);
      } 
      int k = getFormattedTextField().getDocument().getLength();
      if (j == i || i == k) {
        if (i == 0) {
          paramArrayOfBias[0] = Position.Bias.Forward;
          i = getInitialVisualPosition();
        } 
        if (i >= k && k > 0) {
          paramArrayOfBias[0] = Position.Bias.Forward;
          i = getNextNavigatableChar(k - 1, -1) + 1;
        } 
      } 
    } 
    return i;
  }
  
  boolean canReplace(ReplaceHolder paramReplaceHolder) { return isValidEdit(paramReplaceHolder); }
  
  void replace(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet) throws BadLocationException {
    ReplaceHolder replaceHolder1 = getReplaceHolder(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
    replace(replaceHolder1);
  }
  
  boolean replace(ReplaceHolder paramReplaceHolder) {
    boolean bool = true;
    byte b = 1;
    if (paramReplaceHolder.length > 0 && (paramReplaceHolder.text == null || paramReplaceHolder.text.length() == 0) && (getFormattedTextField().getSelectionStart() != paramReplaceHolder.offset || paramReplaceHolder.length > 1))
      b = -1; 
    if (getOverwriteMode() && paramReplaceHolder.text != null && getFormattedTextField().getSelectedText() == null)
      paramReplaceHolder.length = Math.min(Math.max(paramReplaceHolder.length, paramReplaceHolder.text.length()), paramReplaceHolder.fb.getDocument().getLength() - paramReplaceHolder.offset); 
    if ((paramReplaceHolder.text != null && !isLegalInsertText(paramReplaceHolder.text)) || !canReplace(paramReplaceHolder) || (paramReplaceHolder.length == 0 && (paramReplaceHolder.text == null || paramReplaceHolder.text.length() == 0)))
      bool = false; 
    if (bool) {
      int i = paramReplaceHolder.cursorPosition;
      paramReplaceHolder.fb.replace(paramReplaceHolder.offset, paramReplaceHolder.length, paramReplaceHolder.text, paramReplaceHolder.attrs);
      if (i == -1) {
        i = paramReplaceHolder.offset;
        if (b == 1 && paramReplaceHolder.text != null)
          i = paramReplaceHolder.offset + paramReplaceHolder.text.length(); 
      } 
      updateValue(paramReplaceHolder.value);
      repositionCursor(i, b);
      return true;
    } 
    invalidEdit();
    return false;
  }
  
  void setDot(NavigationFilter.FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias) { paramFilterBypass.setDot(paramInt, paramBias); }
  
  void moveDot(NavigationFilter.FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias) { paramFilterBypass.moveDot(paramInt, paramBias); }
  
  ReplaceHolder getReplaceHolder(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet) {
    if (this.replaceHolder == null)
      this.replaceHolder = new ReplaceHolder(); 
    this.replaceHolder.reset(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
    return this.replaceHolder;
  }
  
  private class DefaultDocumentFilter extends DocumentFilter implements Serializable {
    private DefaultDocumentFilter() {}
    
    public void remove(DocumentFilter.FilterBypass param1FilterBypass, int param1Int1, int param1Int2) throws BadLocationException {
      JFormattedTextField jFormattedTextField = DefaultFormatter.this.getFormattedTextField();
      if (jFormattedTextField.composedTextExists()) {
        param1FilterBypass.remove(param1Int1, param1Int2);
      } else {
        DefaultFormatter.this.replace(param1FilterBypass, param1Int1, param1Int2, null, null);
      } 
    }
    
    public void insertString(DocumentFilter.FilterBypass param1FilterBypass, int param1Int, String param1String, AttributeSet param1AttributeSet) throws BadLocationException {
      JFormattedTextField jFormattedTextField = DefaultFormatter.this.getFormattedTextField();
      if (jFormattedTextField.composedTextExists() || Utilities.isComposedTextAttributeDefined(param1AttributeSet)) {
        param1FilterBypass.insertString(param1Int, param1String, param1AttributeSet);
      } else {
        DefaultFormatter.this.replace(param1FilterBypass, param1Int, 0, param1String, param1AttributeSet);
      } 
    }
    
    public void replace(DocumentFilter.FilterBypass param1FilterBypass, int param1Int1, int param1Int2, String param1String, AttributeSet param1AttributeSet) throws BadLocationException {
      JFormattedTextField jFormattedTextField = DefaultFormatter.this.getFormattedTextField();
      if (jFormattedTextField.composedTextExists() || Utilities.isComposedTextAttributeDefined(param1AttributeSet)) {
        param1FilterBypass.replace(param1Int1, param1Int2, param1String, param1AttributeSet);
      } else {
        DefaultFormatter.this.replace(param1FilterBypass, param1Int1, param1Int2, param1String, param1AttributeSet);
      } 
    }
  }
  
  private class DefaultNavigationFilter extends NavigationFilter implements Serializable {
    private DefaultNavigationFilter() {}
    
    public void setDot(NavigationFilter.FilterBypass param1FilterBypass, int param1Int, Position.Bias param1Bias) {
      JFormattedTextField jFormattedTextField = DefaultFormatter.this.getFormattedTextField();
      if (jFormattedTextField.composedTextExists()) {
        param1FilterBypass.setDot(param1Int, param1Bias);
      } else {
        DefaultFormatter.this.setDot(param1FilterBypass, param1Int, param1Bias);
      } 
    }
    
    public void moveDot(NavigationFilter.FilterBypass param1FilterBypass, int param1Int, Position.Bias param1Bias) {
      JFormattedTextField jFormattedTextField = DefaultFormatter.this.getFormattedTextField();
      if (jFormattedTextField.composedTextExists()) {
        param1FilterBypass.moveDot(param1Int, param1Bias);
      } else {
        DefaultFormatter.this.moveDot(param1FilterBypass, param1Int, param1Bias);
      } 
    }
    
    public int getNextVisualPositionFrom(JTextComponent param1JTextComponent, int param1Int1, Position.Bias param1Bias, int param1Int2, Position.Bias[] param1ArrayOfBias) throws BadLocationException { return param1JTextComponent.composedTextExists() ? param1JTextComponent.getUI().getNextVisualPositionFrom(param1JTextComponent, param1Int1, param1Bias, param1Int2, param1ArrayOfBias) : DefaultFormatter.this.getNextVisualPositionFrom(param1JTextComponent, param1Int1, param1Bias, param1Int2, param1ArrayOfBias); }
  }
  
  static class ReplaceHolder {
    DocumentFilter.FilterBypass fb;
    
    int offset;
    
    int length;
    
    String text;
    
    AttributeSet attrs;
    
    Object value;
    
    int cursorPosition;
    
    void reset(DocumentFilter.FilterBypass param1FilterBypass, int param1Int1, int param1Int2, String param1String, AttributeSet param1AttributeSet) throws BadLocationException {
      this.fb = param1FilterBypass;
      this.offset = param1Int1;
      this.length = param1Int2;
      this.text = param1String;
      this.attrs = param1AttributeSet;
      this.value = null;
      this.cursorPosition = -1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\DefaultFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */