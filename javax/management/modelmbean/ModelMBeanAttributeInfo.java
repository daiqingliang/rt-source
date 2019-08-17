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
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanAttributeInfo extends MBeanAttributeInfo implements DescriptorAccess {
  private static final long oldSerialVersionUID = 7098036920755973145L;
  
  private static final long newSerialVersionUID = 6181543027787327345L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("attrDescriptor", Descriptor.class), new ObjectStreamField("currClass", String.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("attrDescriptor", Descriptor.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  private Descriptor attrDescriptor = validDescriptor(null);
  
  private static final String currClass = "ModelMBeanAttributeInfo";
  
  public ModelMBeanAttributeInfo(String paramString1, String paramString2, Method paramMethod1, Method paramMethod2) throws IntrospectionException {
    super(paramString1, paramString2, paramMethod1, paramMethod2);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,Method,Method)", "Entry", paramString1); 
    this.attrDescriptor = validDescriptor(null);
  }
  
  public ModelMBeanAttributeInfo(String paramString1, String paramString2, Method paramMethod1, Method paramMethod2, Descriptor paramDescriptor) throws IntrospectionException {
    super(paramString1, paramString2, paramMethod1, paramMethod2);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,Method,Method,Descriptor)", "Entry", paramString1); 
    this.attrDescriptor = validDescriptor(paramDescriptor);
  }
  
  public ModelMBeanAttributeInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    super(paramString1, paramString2, paramString3, paramBoolean1, paramBoolean2, paramBoolean3);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,String,boolean,boolean,boolean)", "Entry", paramString1); 
    this.attrDescriptor = validDescriptor(null);
  }
  
  public ModelMBeanAttributeInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Descriptor paramDescriptor) {
    super(paramString1, paramString2, paramString3, paramBoolean1, paramBoolean2, paramBoolean3);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(String,String,String,boolean,boolean,boolean,Descriptor)", "Entry", paramString1); 
    this.attrDescriptor = validDescriptor(paramDescriptor);
  }
  
  public ModelMBeanAttributeInfo(ModelMBeanAttributeInfo paramModelMBeanAttributeInfo) {
    super(paramModelMBeanAttributeInfo.getName(), paramModelMBeanAttributeInfo.getType(), paramModelMBeanAttributeInfo.getDescription(), paramModelMBeanAttributeInfo.isReadable(), paramModelMBeanAttributeInfo.isWritable(), paramModelMBeanAttributeInfo.isIs());
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "ModelMBeanAttributeInfo(ModelMBeanAttributeInfo)", "Entry"); 
    Descriptor descriptor = paramModelMBeanAttributeInfo.getDescriptor();
    this.attrDescriptor = validDescriptor(descriptor);
  }
  
  public Descriptor getDescriptor() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "getDescriptor()", "Entry"); 
    if (this.attrDescriptor == null)
      this.attrDescriptor = validDescriptor(null); 
    return (Descriptor)this.attrDescriptor.clone();
  }
  
  public void setDescriptor(Descriptor paramDescriptor) { this.attrDescriptor = validDescriptor(paramDescriptor); }
  
  public Object clone() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanAttributeInfo.class.getName(), "clone()", "Entry"); 
    return new ModelMBeanAttributeInfo(this);
  }
  
  public String toString() { return "ModelMBeanAttributeInfo: " + getName() + " ; Description: " + getDescription() + " ; Types: " + getType() + " ; isReadable: " + isReadable() + " ; isWritable: " + isWritable() + " ; Descriptor: " + getDescriptor(); }
  
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
      descriptor.setField("descriptorType", "attribute");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"attribute\"");
    } 
    if (descriptor.getFieldValue("displayName") == null) {
      descriptor.setField("displayName", getName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + getName());
    } 
    if (!descriptor.isValid())
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + descriptor.toString()); 
    if (!getName().equalsIgnoreCase((String)descriptor.getFieldValue("name")))
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"name\" field does not match the object described.  Expected: " + getName() + " , was: " + descriptor.getFieldValue("name")); 
    if (!"attribute".equalsIgnoreCase((String)descriptor.getFieldValue("descriptorType")))
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: \"attribute\" , was: " + descriptor.getFieldValue("descriptorType")); 
    return descriptor;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException { paramObjectInputStream.defaultReadObject(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("attrDescriptor", this.attrDescriptor);
      putField.put("currClass", "ModelMBeanAttributeInfo");
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
      serialVersionUID = 7098036920755973145L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 6181543027787327345L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\modelmbean\ModelMBeanAttributeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */