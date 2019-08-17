package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.text.MessageFormat;
import java.util.ResourceBundle;

static enum Messages {
  UNRESOLVED_IDREF, UNEXPECTED_ELEMENT, UNEXPECTED_TEXT, NOT_A_QNAME, UNRECOGNIZED_TYPE_NAME, UNRECOGNIZED_TYPE_NAME_MAYBE, UNABLE_TO_CREATE_MAP, UNINTERNED_STRINGS, ERRORS_LIMIT_EXCEEDED;
  
  private static final ResourceBundle rb;
  
  public String toString() { return format(new Object[0]); }
  
  public String format(Object... paramVarArgs) { return MessageFormat.format(rb.getString(name()), paramVarArgs); }
  
  static  {
    rb = ResourceBundle.getBundle(Messages.class.getName());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */