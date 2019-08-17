package javax.swing.colorchooser;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DocumentFilter;

final class ValueFormatter extends JFormattedTextField.AbstractFormatter implements FocusListener, Runnable {
  private final DocumentFilter filter = new DocumentFilter() {
      public void remove(FilterBypass param1FilterBypass, int param1Int1, int param1Int2) throws BadLocationException {
        if (ValueFormatter.this.isValid(param1FilterBypass.getDocument().getLength() - param1Int2))
          param1FilterBypass.remove(param1Int1, param1Int2); 
      }
      
      public void replace(FilterBypass param1FilterBypass, int param1Int1, int param1Int2, String param1String, AttributeSet param1AttributeSet) throws BadLocationException {
        if (ValueFormatter.this.isValid(param1FilterBypass.getDocument().getLength() + param1String.length() - param1Int2) && ValueFormatter.this.isValid(param1String))
          param1FilterBypass.replace(param1Int1, param1Int2, param1String.toUpperCase(Locale.ENGLISH), param1AttributeSet); 
      }
      
      public void insertString(FilterBypass param1FilterBypass, int param1Int, String param1String, AttributeSet param1AttributeSet) throws BadLocationException {
        if (ValueFormatter.this.isValid(param1FilterBypass.getDocument().getLength() + param1String.length()) && ValueFormatter.this.isValid(param1String))
          param1FilterBypass.insertString(param1Int, param1String.toUpperCase(Locale.ENGLISH), param1AttributeSet); 
      }
    };
  
  private final int length;
  
  private final int radix;
  
  private JFormattedTextField text;
  
  static void init(int paramInt, boolean paramBoolean, JFormattedTextField paramJFormattedTextField) {
    ValueFormatter valueFormatter = new ValueFormatter(paramInt, paramBoolean);
    paramJFormattedTextField.setColumns(paramInt);
    paramJFormattedTextField.setFormatterFactory(new DefaultFormatterFactory(valueFormatter));
    paramJFormattedTextField.setHorizontalAlignment(4);
    paramJFormattedTextField.setMinimumSize(paramJFormattedTextField.getPreferredSize());
    paramJFormattedTextField.addFocusListener(valueFormatter);
  }
  
  ValueFormatter(int paramInt, boolean paramBoolean) {
    this.length = paramInt;
    this.radix = paramBoolean ? 16 : 10;
  }
  
  public Object stringToValue(String paramString) throws ParseException {
    try {
      return Integer.valueOf(paramString, this.radix);
    } catch (NumberFormatException numberFormatException) {
      ParseException parseException = new ParseException("illegal format", 0);
      parseException.initCause(numberFormatException);
      throw parseException;
    } 
  }
  
  public String valueToString(Object paramObject) throws ParseException {
    if (paramObject instanceof Integer) {
      if (this.radix == 10)
        return paramObject.toString(); 
      int i = ((Integer)paramObject).intValue();
      int j = this.length;
      char[] arrayOfChar = new char[j];
      while (0 < j--) {
        arrayOfChar[j] = Character.forDigit(i & 0xF, this.radix);
        i >>= 4;
      } 
      return (new String(arrayOfChar)).toUpperCase(Locale.ENGLISH);
    } 
    throw new ParseException("illegal object", 0);
  }
  
  protected DocumentFilter getDocumentFilter() { return this.filter; }
  
  public void focusGained(FocusEvent paramFocusEvent) {
    Object object = paramFocusEvent.getSource();
    if (object instanceof JFormattedTextField) {
      this.text = (JFormattedTextField)object;
      SwingUtilities.invokeLater(this);
    } 
  }
  
  public void focusLost(FocusEvent paramFocusEvent) {}
  
  public void run() {
    if (this.text != null)
      this.text.selectAll(); 
  }
  
  private boolean isValid(int paramInt) { return (0 <= paramInt && paramInt <= this.length); }
  
  private boolean isValid(String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (Character.digit(c, this.radix) < 0)
        return false; 
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\ValueFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */