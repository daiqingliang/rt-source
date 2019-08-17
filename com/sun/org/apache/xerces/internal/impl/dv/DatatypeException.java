package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DatatypeException extends Exception {
  static final long serialVersionUID = 1940805832730465578L;
  
  protected String key;
  
  protected Object[] args;
  
  public DatatypeException(String paramString, Object[] paramArrayOfObject) {
    super(paramString);
    this.key = paramString;
    this.args = paramArrayOfObject;
  }
  
  public String getKey() { return this.key; }
  
  public Object[] getArgs() { return this.args; }
  
  public String getMessage() {
    ResourceBundle resourceBundle = null;
    resourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages");
    if (resourceBundle == null)
      throw new MissingResourceException("Property file not found!", "com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages", this.key); 
    String str = resourceBundle.getString(this.key);
    if (str == null) {
      str = resourceBundle.getString("BadMessageKey");
      throw new MissingResourceException(str, "com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages", this.key);
    } 
    if (this.args != null)
      try {
        str = MessageFormat.format(str, this.args);
      } catch (Exception exception) {
        str = resourceBundle.getString("FormatFailed");
        str = str + " " + resourceBundle.getString(this.key);
      }  
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\DatatypeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */