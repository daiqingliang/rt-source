package javax.management.relation;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.ObjectName;

public class MBeanServerNotificationFilter extends NotificationFilterSupport {
  private static final long oldSerialVersionUID = 6001782699077323605L;
  
  private static final long newSerialVersionUID = 2605900539589789736L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("mySelectObjNameList", Vector.class), new ObjectStreamField("myDeselectObjNameList", Vector.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("selectedNames", List.class), new ObjectStreamField("deselectedNames", List.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  private List<ObjectName> selectedNames = new Vector();
  
  private List<ObjectName> deselectedNames = null;
  
  public MBeanServerNotificationFilter() {
    JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "MBeanServerNotificationFilter");
    enableType("JMX.mbean.registered");
    enableType("JMX.mbean.unregistered");
    JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "MBeanServerNotificationFilter");
  }
  
  public void disableAllObjectNames() {
    JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "disableAllObjectNames");
    this.selectedNames = new Vector();
    this.deselectedNames = null;
    JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "disableAllObjectNames");
  }
  
  public void disableObjectName(ObjectName paramObjectName) throws IllegalArgumentException {
    if (paramObjectName == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "disableObjectName", paramObjectName);
    if (this.selectedNames != null && this.selectedNames.size() != 0)
      this.selectedNames.remove(paramObjectName); 
    if (this.deselectedNames != null && !this.deselectedNames.contains(paramObjectName))
      this.deselectedNames.add(paramObjectName); 
    JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "disableObjectName");
  }
  
  public void enableAllObjectNames() {
    JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "enableAllObjectNames");
    this.selectedNames = null;
    this.deselectedNames = new Vector();
    JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "enableAllObjectNames");
  }
  
  public void enableObjectName(ObjectName paramObjectName) throws IllegalArgumentException {
    if (paramObjectName == null) {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    } 
    JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "enableObjectName", paramObjectName);
    if (this.deselectedNames != null && this.deselectedNames.size() != 0)
      this.deselectedNames.remove(paramObjectName); 
    if (this.selectedNames != null && !this.selectedNames.contains(paramObjectName))
      this.selectedNames.add(paramObjectName); 
    JmxProperties.RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(), "enableObjectName");
  }
  
  public Vector<ObjectName> getEnabledObjectNames() { return (this.selectedNames != null) ? new Vector(this.selectedNames) : null; }
  
  public Vector<ObjectName> getDisabledObjectNames() { return (this.deselectedNames != null) ? new Vector(this.deselectedNames) : null; }
  
  public boolean isNotificationEnabled(Notification paramNotification) throws IllegalArgumentException {
    if (paramNotification == null) {
      String str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    } 
    JmxProperties.RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", paramNotification);
    String str = paramNotification.getType();
    Vector vector = getEnabledTypes();
    if (!vector.contains(str)) {
      JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "Type not selected, exiting");
      return false;
    } 
    MBeanServerNotification mBeanServerNotification = (MBeanServerNotification)paramNotification;
    ObjectName objectName = mBeanServerNotification.getMBeanName();
    boolean bool = false;
    if (this.selectedNames != null) {
      if (this.selectedNames.size() == 0) {
        JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "No ObjectNames selected, exiting");
        return false;
      } 
      bool = this.selectedNames.contains(objectName);
      if (!bool) {
        JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName not in selected list, exiting");
        return false;
      } 
    } 
    if (!bool) {
      if (this.deselectedNames == null) {
        JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName not selected, and all names deselected, exiting");
        return false;
      } 
      if (this.deselectedNames.contains(objectName)) {
        JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName explicitly not selected, exiting");
        return false;
      } 
    } 
    JmxProperties.RELATION_LOGGER.logp(Level.FINER, MBeanServerNotificationFilter.class.getName(), "isNotificationEnabled", "ObjectName selected, exiting");
    return true;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
      this.selectedNames = (List)Util.cast(getField.get("mySelectObjNameList", null));
      if (getField.defaulted("mySelectObjNameList"))
        throw new NullPointerException("mySelectObjNameList"); 
      this.deselectedNames = (List)Util.cast(getField.get("myDeselectObjNameList", null));
      if (getField.defaulted("myDeselectObjNameList"))
        throw new NullPointerException("myDeselectObjNameList"); 
    } else {
      paramObjectInputStream.defaultReadObject();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("mySelectObjNameList", this.selectedNames);
      putField.put("myDeselectObjNameList", this.deselectedNames);
      paramObjectOutputStream.writeFields();
    } else {
      paramObjectOutputStream.defaultWriteObject();
    } 
  }
  
  static  {
    try {
      GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(getPropertyAction);
      compat = (str != null && str.equals("1.0"));
    } catch (Exception exception) {}
    if (compat) {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = 6001782699077323605L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 2605900539589789736L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\relation\MBeanServerNotificationFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */