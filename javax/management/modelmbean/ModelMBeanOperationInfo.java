package javax.management.modelmbean;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.logging.Level;
import javax.management.Descriptor;
import javax.management.DescriptorAccess;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanOperationInfo extends MBeanOperationInfo implements DescriptorAccess {
  private static final long oldSerialVersionUID = 9087646304346171239L;
  
  private static final long newSerialVersionUID = 6532732096650090465L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("operationDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("operationDescriptor", Descriptor.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  private Descriptor operationDescriptor = validDescriptor(null);
  
  private static final String currClass = "ModelMBeanOperationInfo";
  
  public ModelMBeanOperationInfo(String paramString, Method paramMethod) {
    super(paramString, paramMethod);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,Method)", "Entry"); 
    this.operationDescriptor = validDescriptor(null);
  }
  
  public ModelMBeanOperationInfo(String paramString, Method paramMethod, Descriptor paramDescriptor) {
    super(paramString, paramMethod);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,Method,Descriptor)", "Entry"); 
    this.operationDescriptor = validDescriptor(paramDescriptor);
  }
  
  public ModelMBeanOperationInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, String paramString3, int paramInt) {
    super(paramString1, paramString2, paramArrayOfMBeanParameterInfo, paramString3, paramInt);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,String,MBeanParameterInfo[],String,int)", "Entry"); 
    this.operationDescriptor = validDescriptor(null);
  }
  
  public ModelMBeanOperationInfo(String paramString1, String paramString2, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, String paramString3, int paramInt, Descriptor paramDescriptor) {
    super(paramString1, paramString2, paramArrayOfMBeanParameterInfo, paramString3, paramInt);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(String,String,MBeanParameterInfo[],String,int,Descriptor)", "Entry"); 
    this.operationDescriptor = validDescriptor(paramDescriptor);
  }
  
  public ModelMBeanOperationInfo(ModelMBeanOperationInfo paramModelMBeanOperationInfo) {
    super(paramModelMBeanOperationInfo.getName(), paramModelMBeanOperationInfo.getDescription(), paramModelMBeanOperationInfo.getSignature(), paramModelMBeanOperationInfo.getReturnType(), paramModelMBeanOperationInfo.getImpact());
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "ModelMBeanOperationInfo(ModelMBeanOperationInfo)", "Entry"); 
    Descriptor descriptor = paramModelMBeanOperationInfo.getDescriptor();
    this.operationDescriptor = validDescriptor(descriptor);
  }
  
  public Object clone() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "clone()", "Entry"); 
    return new ModelMBeanOperationInfo(this);
  }
  
  public Descriptor getDescriptor() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "getDescriptor()", "Entry"); 
    if (this.operationDescriptor == null)
      this.operationDescriptor = validDescriptor(null); 
    return (Descriptor)this.operationDescriptor.clone();
  }
  
  public void setDescriptor(Descriptor paramDescriptor) {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "setDescriptor(Descriptor)", "Entry"); 
    this.operationDescriptor = validDescriptor(paramDescriptor);
  }
  
  public String toString() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanOperationInfo.class.getName(), "toString()", "Entry"); 
    String str = "ModelMBeanOperationInfo: " + getName() + " ; Description: " + getDescription() + " ; Descriptor: " + getDescriptor() + " ; ReturnType: " + getReturnType() + " ; Signature: ";
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
      descriptor.setField("role", "operation");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor role field to \"operation\"");
    } 
    if (!descriptor.isValid())
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + descriptor.toString()); 
    if (!getName().equalsIgnoreCase((String)descriptor.getFieldValue("name")))
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + getName() + " , was: " + descriptor.getFieldValue("name")); 
    if (!"operation".equalsIgnoreCase((String)descriptor.getFieldValue("descriptorType")))
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"operation\" , was: " + descriptor.getFieldValue("descriptorType")); 
    String str = (String)descriptor.getFieldValue("role");
    if (!str.equalsIgnoreCase("operation") && !str.equalsIgnoreCase("setter") && !str.equalsIgnoreCase("getter"))
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"role\" field does not match the object described.  Expected: \"operation\", \"setter\", or \"getter\" , was: " + descriptor.getFieldValue("role")); 
    Object object = descriptor.getFieldValue("targetType");
    if (object != null && !(object instanceof String))
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor field \"targetValue\" is invalid class.  Expected: java.lang.String,  was: " + object.getClass().getName()); 
    return descriptor;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException { paramObjectInputStream.defaultReadObject(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("operationDescriptor", this.operationDescriptor);
      putField.put("currClass", "ModelMBeanOperationInfo");
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
      serialVersionUID = 9087646304346171239L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 6532732096650090465L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\modelmbean\ModelMBeanOperationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */