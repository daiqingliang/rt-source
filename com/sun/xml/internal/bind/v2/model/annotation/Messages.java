package com.sun.xml.internal.bind.v2.model.annotation;

import java.text.MessageFormat;
import java.util.ResourceBundle;

static enum Messages {
  DUPLICATE_ANNOTATIONS, CLASS_NOT_FOUND;
  
  private static final ResourceBundle rb;
  
  public String toString() { return format(new Object[0]); }
  
  public String format(Object... paramVarArgs) { return MessageFormat.format(rb.getString(name()), paramVarArgs); }
  
  static  {
    rb = ResourceBundle.getBundle(Messages.class.getName());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */