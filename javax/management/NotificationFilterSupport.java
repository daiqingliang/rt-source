package javax.management;

import java.util.List;
import java.util.Vector;

public class NotificationFilterSupport implements NotificationFilter {
  private static final long serialVersionUID = 6579080007561786969L;
  
  private List<String> enabledTypes = new Vector();
  
  public boolean isNotificationEnabled(Notification paramNotification) {
    String str = paramNotification.getType();
    if (str == null)
      return false; 
    try {
      for (String str1 : this.enabledTypes) {
        if (str.startsWith(str1))
          return true; 
      } 
    } catch (NullPointerException nullPointerException) {
      return false;
    } 
    return false;
  }
  
  public void enableType(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new IllegalArgumentException("The prefix cannot be null."); 
    if (!this.enabledTypes.contains(paramString))
      this.enabledTypes.add(paramString); 
  }
  
  public void disableType(String paramString) throws IllegalArgumentException { this.enabledTypes.remove(paramString); }
  
  public void disableAllTypes() { this.enabledTypes.clear(); }
  
  public Vector<String> getEnabledTypes() { return (Vector)this.enabledTypes; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\NotificationFilterSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */