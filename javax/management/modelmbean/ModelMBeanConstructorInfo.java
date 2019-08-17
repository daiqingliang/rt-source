package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.util.logging.Level;
import javax.management.Descriptor;
import javax.management.DescriptorAccess;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanParameterInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanConstructorInfo extends MBeanConstructorInfo implements DescriptorAccess {
  private static final long oldSerialVersionUID = -4440125391095574518L;
  
  private static final long newSerialVersionUID = 3862947819818064362L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("consDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("consDescriptor", Descriptor.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  private Descriptor consDescriptor = validDescriptor(null);
  
  private static final String currClass = "ModelMBeanConstructorInfo";
  
  public ModelMBeanConstructorInfo(String paramString, Constructor<?> paramConstructor) {
    super(paramString, paramConstructor);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(String,Constructor)", "Entry"); 
    this.consDescriptor = validDescriptor(null);
  }
  
  public ModelMBeanConstructorInfo(String paramString, Constructor<?> paramConstructor, Descriptor paramDescriptor) {
    super(paramString, paramConstructor);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(String,Constructor,Descriptor)", "Entry"); 
    this.consDescriptor = validDescriptor(paramDescriptor);
  }
  
  public ModelMBeanConstructorInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo) {
    super(paramString1, paramString2, paramArrayOfMBeanParameterInfo);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(String,String,MBeanParameterInfo[])", "Entry"); 
    this.consDescriptor = validDescriptor(null);
  }
  
  public ModelMBeanConstructorInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, Descriptor paramDescriptor) {
    super(paramString1, paramString2, paramArrayOfMBeanParameterInfo);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(String,String,MBeanParameterInfo[],Descriptor)", "Entry"); 
    this.consDescriptor = validDescriptor(paramDescriptor);
  }
  
  ModelMBeanConstructorInfo(ModelMBeanConstructorInfo paramModelMBeanConstructorInfo) {
    super(paramModelMBeanConstructorInfo.getName(), paramModelMBeanConstructorInfo.getDescription(), paramModelMBeanConstructorInfo.getSignature());
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "ModelMBeanConstructorInfo(ModelMBeanConstructorInfo)", "Entry"); 
    this.consDescriptor = validDescriptor(this.consDescriptor);
  }
  
  public Object clone() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "clone()", "Entry"); 
    return new ModelMBeanConstructorInfo(this);
  }
  
  public Descriptor getDescriptor() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "getDescriptor()", "Entry"); 
    if (this.consDescriptor == null)
      this.consDescriptor = validDescriptor(null); 
    return (Descriptor)this.consDescriptor.clone();
  }
  
  public void setDescriptor(Descriptor paramDescriptor) {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "setDescriptor()", "Entry"); 
    this.consDescriptor = validDescriptor(paramDescriptor);
  }
  
  public String toString() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanConstructorInfo.class.getName(), "toString()", "Entry"); 
    String str = "ModelMBeanConstructorInfo: " + getName() + " ; Description: " + getDescription() + " ; Descriptor: " + getDescriptor() + " ; Signature: ";
    MBeanParameterInfo[] arrayOfMBeanParameterInfo = getSignature();
    for (byte b = 0; b < arrayOfMBeanParameterInfo.length; b++)
      str = str.concat(arrayOfMBeanParameterInfo[b].getType() + ", "); 
    return str;
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
      descriptor.setField("descriptorType", "operation");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"operation\"");
    } 
    if (descriptor.getFieldValue("displayName") == null) {
      descriptor.setField("displayName", getName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + getName());
    } 
    if (descriptor.getFieldValue("role") == null) {
      descriptor.setField("role", "constructor");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor role field to \"constructor\"");
    } 
    if (!descriptor.isValid())
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + descriptor.toString()); 
    if (!getName().equalsIgnoreCase((String)descriptor.getFieldValue("name")))
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + getName() + " , was: " + descriptor.getFieldValue("name")); 
    if (!"operation".equalsIgnoreCase((String)descriptor.getFieldValue("descriptorType")))
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"operation\" , was: " + descriptor.getFieldValue("descriptorType")); 
    if (!((String)descriptor.getFieldValue("role")).equalsIgnoreCase("constructor"))
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"role\" field does not match the object described.  Expected: \"constructor\" , was: " + descriptor.getFieldValue("role")); 
    return descriptor;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException { paramObjectInputStream.defaultReadObject(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("consDescriptor", this.consDescriptor);
      putField.put("currClass", "ModelMBeanConstructorInfo");
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
      serialVersionUID = -4440125391095574518L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 3862947819818064362L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\modelmbean\ModelMBeanConstructorInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */