package javax.management;

import java.util.Vector;

public class AttributeChangeNotificationFilter implements NotificationFilter {
  private static final long serialVersionUID = -6347317584796410029L;
  
  private Vector<String> enabledAttributes = new Vector();
  
  public boolean isNotificationEnabled(Notification paramNotification) {
    String str1 = paramNotification.getType();
    if (str1 == null || !str1.equals("jmx.attribute.change") || !(paramNotification instanceof AttributeChangeNotification))
      return false; 
    String str2 = ((AttributeChangeNotification)paramNotification).getAttributeName();
    return this.enabledAttributes.contains(str2);
  }
  
  public void enableAttribute(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new IllegalArgumentException("The name cannot be null."); 
    if (!this.enabledAttributes.contains(paramString))
      this.enabledAttributes.addElement(paramString); 
  }
  
  public void disableAttribute(String paramString) throws IllegalArgumentException { this.enabledAttributes.removeElement(paramString); }
  
  public void disableAllAttributes() { this.enabledAttributes.removeAllElements(); }
  
  public Vector<String> getEnabledAttributes() { return this.enabledAttributes; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\AttributeChangeNotificationFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */