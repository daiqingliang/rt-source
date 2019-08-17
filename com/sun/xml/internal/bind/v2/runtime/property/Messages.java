package com.sun.xml.internal.bind.v2.runtime.property;

import java.text.MessageFormat;
import java.util.ResourceBundle;

static enum Messages {
  UNSUBSTITUTABLE_TYPE, UNEXPECTED_JAVA_TYPE;
  
  private static final ResourceBundle rb;
  
  public String toString() { return format(new Object[0]); }
  
  public String format(Object... paramVarArgs) { return MessageFormat.format(rb.getString(name()), paramVarArgs); }
  
  static  {
    rb = ResourceBundle.getBundle(Messages.class.getName());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */