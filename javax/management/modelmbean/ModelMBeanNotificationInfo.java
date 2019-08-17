package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.logging.Level;
import javax.management.Descriptor;
import javax.management.DescriptorAccess;
import javax.management.MBeanNotificationInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanNotificationInfo extends MBeanNotificationInfo implements DescriptorAccess {
  private static final long oldSerialVersionUID = -5211564525059047097L;
  
  private static final long newSerialVersionUID = -7445681389570207141L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("notificationDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("notificationDescriptor", Descriptor.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  private Descriptor notificationDescriptor;
  
  private static final String currClass = "ModelMBeanNotificationInfo";
  
  public ModelMBeanNotificationInfo(String[] paramArrayOfString, String paramString1, String paramString2) { this(paramArrayOfString, paramString1, paramString2, null); }
  
  public ModelMBeanNotificationInfo(String[] paramArrayOfString, String paramString1, String paramString2, Descriptor paramDescriptor) {
    super(paramArrayOfString, paramString1, paramString2);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "ModelMBeanNotificationInfo", "Entry"); 
    this.notificationDescriptor = validDescriptor(paramDescriptor);
  }
  
  public ModelMBeanNotificationInfo(ModelMBeanNotificationInfo paramModelMBeanNotificationInfo) { this(paramModelMBeanNotificationInfo.getNotifTypes(), paramModelMBeanNotificationInfo.getName(), paramModelMBeanNotificationInfo.getDescription(), paramModelMBeanNotificationInfo.getDescriptor()); }
  
  public Object clone() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "clone()", "Entry"); 
    return new ModelMBeanNotificationInfo(this);
  }
  
  public Descriptor getDescriptor() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "getDescriptor()", "Entry"); 
    if (this.notificationDescriptor == null) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "getDescriptor()", "Descriptor value is null, setting descriptor to default values"); 
      this.notificationDescriptor = validDescriptor(null);
    } 
    return (Descriptor)this.notificationDescriptor.clone();
  }
  
  public void setDescriptor(Descriptor paramDescriptor) {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "setDescriptor(Descriptor)", "Entry"); 
    this.notificationDescriptor = validDescriptor(paramDescriptor);
  }
  
  public String toString() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanNotificationInfo.class.getName(), "toString()", "Entry"); 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("ModelMBeanNotificationInfo: ").append(getName());
    stringBuilder.append(" ; Description: ").append(getDescription());
    stringBuilder.append(" ; Descriptor: ").append(getDescriptor());
    stringBuilder.append(" ; Types: ");
    String[] arrayOfString = getNotifTypes();
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (b)
        stringBuilder.append(", "); 
      stringBuilder.append(arrayOfString[b]);
    } 
    return stringBuilder.toString();
  }
  
  private Descriptor validDescriptor(Descriptor paramDescriptor) throws RuntimeOperationsException {
    Descriptor descriptor;
    boolean bool = (paramDescriptor == null) ? 1 : 0;
    if (bool) {
      descriptor = new DescriptorSupport();
      JmxProperties.MODELMBEAN_LOGGER.finer("Null Descriptor, creating new.");
    } else {
      descriptor = (Descriptor)paramDescriptor.clone();
    } 
    if (bool && descriptor.getFieldValue("name") == null) {
      descriptor.setField("name", getName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor name to " + getName());
    } 
    if (bool && descriptor.getFieldValue("descriptorType") == null) {
      descriptor.setField("descriptorType", "notification");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"notification\"");
    } 
    if (descriptor.getFieldValue("displayName") == null) {
      descriptor.setField("displayName", getName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + getName());
    } 
    if (descriptor.getFieldValue("severity") == null) {
      descriptor.setField("severity", "6");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor severity field to 6");
    } 
    if (!descriptor.isValid())
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + descriptor.toString()); 
    if (!getName().equalsIgnoreCase((String)descriptor.getFieldValue("name")))
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + getName() + " , was: " + descriptor.getFieldValue("name")); 
    if (!"notification".equalsIgnoreCase((String)descriptor.getFieldValue("descriptorType")))
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"notification\" , was: " + descriptor.getFieldValue("descriptorType")); 
    return descriptor;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException { paramObjectInputStream.defaultReadObject(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("notificationDescriptor", this.notificationDescriptor);
      putField.put("currClass", "ModelMBeanNotificationInfo");
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
      serialVersionUID = -5211564525059047097L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -7445681389570207141L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\modelmbean\ModelMBeanNotificationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */