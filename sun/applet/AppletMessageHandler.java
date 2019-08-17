package sun.applet;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

class AppletMessageHandler {
  private static ResourceBundle rb;
  
  private String baseKey = null;
  
  AppletMessageHandler(String paramString) { this.baseKey = paramString; }
  
  String getMessage(String paramString) { return rb.getString(getQualifiedKey(paramString)); }
  
  String getMessage(String paramString, Object paramObject) {
    String str = rb.getString(getQualifiedKey(paramString));
    MessageFormat messageFormat = new MessageFormat(str);
    Object[] arrayOfObject = new Object[1];
    if (paramObject == null)
      paramObject = "null"; 
    arrayOfObject[0] = paramObject;
    return messageFormat.format(arrayOfObject);
  }
  
  String getMessage(String paramString, Object paramObject1, Object paramObject2) {
    String str = rb.getString(getQualifiedKey(paramString));
    MessageFormat messageFormat = new MessageFormat(str);
    Object[] arrayOfObject = new Object[2];
    if (paramObject1 == null)
      paramObject1 = "null"; 
    if (paramObject2 == null)
      paramObject2 = "null"; 
    arrayOfObject[0] = paramObject1;
    arrayOfObject[1] = paramObject2;
    return messageFormat.format(arrayOfObject);
  }
  
  String getMessage(String paramString, Object paramObject1, Object paramObject2, Object paramObject3) {
    String str = rb.getString(getQualifiedKey(paramString));
    MessageFormat messageFormat = new MessageFormat(str);
    Object[] arrayOfObject = new Object[3];
    if (paramObject1 == null)
      paramObject1 = "null"; 
    if (paramObject2 == null)
      paramObject2 = "null"; 
    if (paramObject3 == null)
      paramObject3 = "null"; 
    arrayOfObject[0] = paramObject1;
    arrayOfObject[1] = paramObject2;
    arrayOfObject[2] = paramObject3;
    return messageFormat.format(arrayOfObject);
  }
  
  String getMessage(String paramString, Object[] paramArrayOfObject) {
    String str = rb.getString(getQualifiedKey(paramString));
    MessageFormat messageFormat = new MessageFormat(str);
    return messageFormat.format(paramArrayOfObject);
  }
  
  String getQualifiedKey(String paramString) { return this.baseKey + "." + paramString; }
  
  static  {
    try {
      rb = ResourceBundle.getBundle("sun.applet.resources.MsgAppletViewer");
    } catch (MissingResourceException missingResourceException) {
      System.out.println(missingResourceException.getMessage());
      System.exit(1);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletMessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */