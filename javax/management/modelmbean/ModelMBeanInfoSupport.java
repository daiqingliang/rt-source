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
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.RuntimeOperationsException;

public class ModelMBeanInfoSupport extends MBeanInfo implements ModelMBeanInfo {
  private static final long oldSerialVersionUID = -3944083498453227709L;
  
  private static final long newSerialVersionUID = -1935722590756516193L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("modelMBeanDescriptor", Descriptor.class), new ObjectStreamField("mmbAttributes", MBeanAttributeInfo[].class), new ObjectStreamField("mmbConstructors", MBeanConstructorInfo[].class), new ObjectStreamField("mmbNotifications", MBeanNotificationInfo[].class), new ObjectStreamField("mmbOperations", MBeanOperationInfo[].class), new ObjectStreamField("currClass", String.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("modelMBeanDescriptor", Descriptor.class), new ObjectStreamField("modelMBeanAttributes", MBeanAttributeInfo[].class), new ObjectStreamField("modelMBeanConstructors", MBeanConstructorInfo[].class), new ObjectStreamField("modelMBeanNotifications", MBeanNotificationInfo[].class), new ObjectStreamField("modelMBeanOperations", MBeanOperationInfo[].class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  private Descriptor modelMBeanDescriptor = null;
  
  private MBeanAttributeInfo[] modelMBeanAttributes;
  
  private MBeanConstructorInfo[] modelMBeanConstructors;
  
  private MBeanNotificationInfo[] modelMBeanNotifications;
  
  private MBeanOperationInfo[] modelMBeanOperations;
  
  private static final String ATTR = "attribute";
  
  private static final String OPER = "operation";
  
  private static final String NOTF = "notification";
  
  private static final String CONS = "constructor";
  
  private static final String MMB = "mbean";
  
  private static final String ALL = "all";
  
  private static final String currClass = "ModelMBeanInfoSupport";
  
  private static final ModelMBeanAttributeInfo[] NO_ATTRIBUTES;
  
  private static final ModelMBeanConstructorInfo[] NO_CONSTRUCTORS;
  
  private static final ModelMBeanNotificationInfo[] NO_NOTIFICATIONS;
  
  private static final ModelMBeanOperationInfo[] NO_OPERATIONS;
  
  public ModelMBeanInfoSupport(ModelMBeanInfo paramModelMBeanInfo) {
    super(paramModelMBeanInfo.getClassName(), paramModelMBeanInfo.getDescription(), paramModelMBeanInfo.getAttributes(), paramModelMBeanInfo.getConstructors(), paramModelMBeanInfo.getOperations(), paramModelMBeanInfo.getNotifications());
    this.modelMBeanAttributes = paramModelMBeanInfo.getAttributes();
    this.modelMBeanConstructors = paramModelMBeanInfo.getConstructors();
    this.modelMBeanOperations = paramModelMBeanInfo.getOperations();
    this.modelMBeanNotifications = paramModelMBeanInfo.getNotifications();
    try {
      Descriptor descriptor = paramModelMBeanInfo.getMBeanDescriptor();
      this.modelMBeanDescriptor = validDescriptor(descriptor);
    } catch (MBeanException mBeanException) {
      this.modelMBeanDescriptor = validDescriptor(null);
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "ModelMBeanInfo(ModelMBeanInfo)", "Could not get a valid modelMBeanDescriptor, setting a default Descriptor"); 
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "ModelMBeanInfo(ModelMBeanInfo)", "Exit"); 
  }
  
  public ModelMBeanInfoSupport(String paramString1, String paramString2, ModelMBeanAttributeInfo[] paramArrayOfModelMBeanAttributeInfo, ModelMBeanConstructorInfo[] paramArrayOfModelMBeanConstructorInfo, ModelMBeanOperationInfo[] paramArrayOfModelMBeanOperationInfo, ModelMBeanNotificationInfo[] paramArrayOfModelMBeanNotificationInfo) { this(paramString1, paramString2, paramArrayOfModelMBeanAttributeInfo, paramArrayOfModelMBeanConstructorInfo, paramArrayOfModelMBeanOperationInfo, paramArrayOfModelMBeanNotificationInfo, null); }
  
  public ModelMBeanInfoSupport(String paramString1, String paramString2, ModelMBeanAttributeInfo[] paramArrayOfModelMBeanAttributeInfo, ModelMBeanConstructorInfo[] paramArrayOfModelMBeanConstructorInfo, ModelMBeanOperationInfo[] paramArrayOfModelMBeanOperationInfo, ModelMBeanNotificationInfo[] paramArrayOfModelMBeanNotificationInfo, Descriptor paramDescriptor) {
    super(paramString1, paramString2, (paramArrayOfModelMBeanAttributeInfo != null) ? paramArrayOfModelMBeanAttributeInfo : NO_ATTRIBUTES, (paramArrayOfModelMBeanConstructorInfo != null) ? paramArrayOfModelMBeanConstructorInfo : NO_CONSTRUCTORS, (paramArrayOfModelMBeanOperationInfo != null) ? paramArrayOfModelMBeanOperationInfo : NO_OPERATIONS, (paramArrayOfModelMBeanNotificationInfo != null) ? paramArrayOfModelMBeanNotificationInfo : NO_NOTIFICATIONS);
    this.modelMBeanAttributes = paramArrayOfModelMBeanAttributeInfo;
    this.modelMBeanConstructors = paramArrayOfModelMBeanConstructorInfo;
    this.modelMBeanOperations = paramArrayOfModelMBeanOperationInfo;
    this.modelMBeanNotifications = paramArrayOfModelMBeanNotificationInfo;
    this.modelMBeanDescriptor = validDescriptor(paramDescriptor);
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "ModelMBeanInfoSupport(String,String,ModelMBeanAttributeInfo[],ModelMBeanConstructorInfo[],ModelMBeanOperationInfo[],ModelMBeanNotificationInfo[],Descriptor)", "Exit"); 
  }
  
  public Object clone() { return new ModelMBeanInfoSupport(this); }
  
  public Descriptor[] getDescriptors(String paramString) throws MBeanException, RuntimeOperationsException {
    Descriptor[] arrayOfDescriptor;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getDescriptors(String)", "Entry"); 
    if (paramString == null || paramString.equals(""))
      paramString = "all"; 
    if (paramString.equalsIgnoreCase("mbean")) {
      arrayOfDescriptor = new Descriptor[] { this.modelMBeanDescriptor };
    } else if (paramString.equalsIgnoreCase("attribute")) {
      MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = this.modelMBeanAttributes;
      int i = 0;
      if (arrayOfMBeanAttributeInfo != null)
        i = arrayOfMBeanAttributeInfo.length; 
      arrayOfDescriptor = new Descriptor[i];
      for (byte b = 0; b < i; b++)
        arrayOfDescriptor[b] = ((ModelMBeanAttributeInfo)arrayOfMBeanAttributeInfo[b]).getDescriptor(); 
    } else if (paramString.equalsIgnoreCase("operation")) {
      MBeanOperationInfo[] arrayOfMBeanOperationInfo = this.modelMBeanOperations;
      int i = 0;
      if (arrayOfMBeanOperationInfo != null)
        i = arrayOfMBeanOperationInfo.length; 
      arrayOfDescriptor = new Descriptor[i];
      for (byte b = 0; b < i; b++)
        arrayOfDescriptor[b] = ((ModelMBeanOperationInfo)arrayOfMBeanOperationInfo[b]).getDescriptor(); 
    } else if (paramString.equalsIgnoreCase("constructor")) {
      MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = this.modelMBeanConstructors;
      int i = 0;
      if (arrayOfMBeanConstructorInfo != null)
        i = arrayOfMBeanConstructorInfo.length; 
      arrayOfDescriptor = new Descriptor[i];
      for (byte b = 0; b < i; b++)
        arrayOfDescriptor[b] = ((ModelMBeanConstructorInfo)arrayOfMBeanConstructorInfo[b]).getDescriptor(); 
    } else if (paramString.equalsIgnoreCase("notification")) {
      MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = this.modelMBeanNotifications;
      int i = 0;
      if (arrayOfMBeanNotificationInfo != null)
        i = arrayOfMBeanNotificationInfo.length; 
      arrayOfDescriptor = new Descriptor[i];
      for (byte b = 0; b < i; b++)
        arrayOfDescriptor[b] = ((ModelMBeanNotificationInfo)arrayOfMBeanNotificationInfo[b]).getDescriptor(); 
    } else if (paramString.equalsIgnoreCase("all")) {
      MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = this.modelMBeanAttributes;
      int i = 0;
      if (arrayOfMBeanAttributeInfo != null)
        i = arrayOfMBeanAttributeInfo.length; 
      MBeanOperationInfo[] arrayOfMBeanOperationInfo = this.modelMBeanOperations;
      int j = 0;
      if (arrayOfMBeanOperationInfo != null)
        j = arrayOfMBeanOperationInfo.length; 
      MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = this.modelMBeanConstructors;
      int k = 0;
      if (arrayOfMBeanConstructorInfo != null)
        k = arrayOfMBeanConstructorInfo.length; 
      MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = this.modelMBeanNotifications;
      int m = 0;
      if (arrayOfMBeanNotificationInfo != null)
        m = arrayOfMBeanNotificationInfo.length; 
      int n = i + k + j + m + 1;
      arrayOfDescriptor = new Descriptor[n];
      arrayOfDescriptor[n - 1] = this.modelMBeanDescriptor;
      byte b1 = 0;
      byte b2;
      for (b2 = 0; b2 < i; b2++) {
        arrayOfDescriptor[b1] = ((ModelMBeanAttributeInfo)arrayOfMBeanAttributeInfo[b2]).getDescriptor();
        b1++;
      } 
      for (b2 = 0; b2 < k; b2++) {
        arrayOfDescriptor[b1] = ((ModelMBeanConstructorInfo)arrayOfMBeanConstructorInfo[b2]).getDescriptor();
        b1++;
      } 
      for (b2 = 0; b2 < j; b2++) {
        arrayOfDescriptor[b1] = ((ModelMBeanOperationInfo)arrayOfMBeanOperationInfo[b2]).getDescriptor();
        b1++;
      } 
      for (b2 = 0; b2 < m; b2++) {
        arrayOfDescriptor[b1] = ((ModelMBeanNotificationInfo)arrayOfMBeanNotificationInfo[b2]).getDescriptor();
        b1++;
      } 
    } else {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Descriptor Type is invalid");
      throw new RuntimeOperationsException(illegalArgumentException, "Exception occurred trying to find the descriptors of the MBean");
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getDescriptors(String)", "Exit"); 
    return arrayOfDescriptor;
  }
  
  public void setDescriptors(Descriptor[] paramArrayOfDescriptor) throws MBeanException, RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptors(Descriptor[])", "Entry"); 
    if (paramArrayOfDescriptor == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor list is invalid"), "Exception occurred trying to set the descriptors of the MBeanInfo"); 
    if (paramArrayOfDescriptor.length == 0)
      return; 
    for (byte b = 0; b < paramArrayOfDescriptor.length; b++)
      setDescriptor(paramArrayOfDescriptor[b], null); 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptors(Descriptor[])", "Exit"); 
  }
  
  public Descriptor getDescriptor(String paramString) throws MBeanException, RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getDescriptor(String)", "Entry"); 
    return getDescriptor(paramString, null);
  }
  
  public Descriptor getDescriptor(String paramString1, String paramString2) throws MBeanException, RuntimeOperationsException {
    if (paramString1 == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor is invalid"), "Exception occurred trying to set the descriptors of the MBeanInfo"); 
    if ("mbean".equalsIgnoreCase(paramString2))
      return (Descriptor)this.modelMBeanDescriptor.clone(); 
    if ("attribute".equalsIgnoreCase(paramString2) || paramString2 == null) {
      ModelMBeanAttributeInfo modelMBeanAttributeInfo = getAttribute(paramString1);
      if (modelMBeanAttributeInfo != null)
        return modelMBeanAttributeInfo.getDescriptor(); 
      if (paramString2 != null)
        return null; 
    } 
    if ("operation".equalsIgnoreCase(paramString2) || paramString2 == null) {
      ModelMBeanOperationInfo modelMBeanOperationInfo = getOperation(paramString1);
      if (modelMBeanOperationInfo != null)
        return modelMBeanOperationInfo.getDescriptor(); 
      if (paramString2 != null)
        return null; 
    } 
    if ("constructor".equalsIgnoreCase(paramString2) || paramString2 == null) {
      ModelMBeanConstructorInfo modelMBeanConstructorInfo = getConstructor(paramString1);
      if (modelMBeanConstructorInfo != null)
        return modelMBeanConstructorInfo.getDescriptor(); 
      if (paramString2 != null)
        return null; 
    } 
    if ("notification".equalsIgnoreCase(paramString2) || paramString2 == null) {
      ModelMBeanNotificationInfo modelMBeanNotificationInfo = getNotification(paramString1);
      if (modelMBeanNotificationInfo != null)
        return modelMBeanNotificationInfo.getDescriptor(); 
      if (paramString2 != null)
        return null; 
    } 
    if (paramString2 == null)
      return null; 
    throw new RuntimeOperationsException(new IllegalArgumentException("Descriptor Type is invalid"), "Exception occurred trying to find the descriptors of the MBean");
  }
  
  public void setDescriptor(Descriptor paramDescriptor, String paramString) throws MBeanException, RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "Entry"); 
    if (paramDescriptor == null)
      paramDescriptor = new DescriptorSupport(); 
    if (paramString == null || paramString.equals("")) {
      paramString = (String)paramDescriptor.getFieldValue("descriptorType");
      if (paramString == null) {
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "descriptorType null in both String parameter and Descriptor, defaulting to mbean");
        paramString = "mbean";
      } 
    } 
    String str = (String)paramDescriptor.getFieldValue("name");
    if (str == null) {
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "descriptor name null, defaulting to " + getClassName());
      str = getClassName();
    } 
    boolean bool = false;
    if (paramString.equalsIgnoreCase("mbean")) {
      setMBeanDescriptor(paramDescriptor);
      bool = true;
    } else if (paramString.equalsIgnoreCase("attribute")) {
      MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = this.modelMBeanAttributes;
      int i = 0;
      if (arrayOfMBeanAttributeInfo != null)
        i = arrayOfMBeanAttributeInfo.length; 
      for (byte b = 0; b < i; b++) {
        if (str.equals(arrayOfMBeanAttributeInfo[b].getName())) {
          bool = true;
          ModelMBeanAttributeInfo modelMBeanAttributeInfo = (ModelMBeanAttributeInfo)arrayOfMBeanAttributeInfo[b];
          modelMBeanAttributeInfo.setDescriptor(paramDescriptor);
          if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
            StringBuilder stringBuilder = (new StringBuilder()).append("Setting descriptor to ").append(paramDescriptor).append("\t\n local: AttributeInfo descriptor is ").append(modelMBeanAttributeInfo.getDescriptor()).append("\t\n modelMBeanInfo: AttributeInfo descriptor is ").append(getDescriptor(str, "attribute"));
            JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", stringBuilder.toString());
          } 
        } 
      } 
    } else if (paramString.equalsIgnoreCase("operation")) {
      MBeanOperationInfo[] arrayOfMBeanOperationInfo = this.modelMBeanOperations;
      int i = 0;
      if (arrayOfMBeanOperationInfo != null)
        i = arrayOfMBeanOperationInfo.length; 
      for (byte b = 0; b < i; b++) {
        if (str.equals(arrayOfMBeanOperationInfo[b].getName())) {
          bool = true;
          ModelMBeanOperationInfo modelMBeanOperationInfo = (ModelMBeanOperationInfo)arrayOfMBeanOperationInfo[b];
          modelMBeanOperationInfo.setDescriptor(paramDescriptor);
        } 
      } 
    } else if (paramString.equalsIgnoreCase("constructor")) {
      MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = this.modelMBeanConstructors;
      int i = 0;
      if (arrayOfMBeanConstructorInfo != null)
        i = arrayOfMBeanConstructorInfo.length; 
      for (byte b = 0; b < i; b++) {
        if (str.equals(arrayOfMBeanConstructorInfo[b].getName())) {
          bool = true;
          ModelMBeanConstructorInfo modelMBeanConstructorInfo = (ModelMBeanConstructorInfo)arrayOfMBeanConstructorInfo[b];
          modelMBeanConstructorInfo.setDescriptor(paramDescriptor);
        } 
      } 
    } else if (paramString.equalsIgnoreCase("notification")) {
      MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = this.modelMBeanNotifications;
      int i = 0;
      if (arrayOfMBeanNotificationInfo != null)
        i = arrayOfMBeanNotificationInfo.length; 
      for (byte b = 0; b < i; b++) {
        if (str.equals(arrayOfMBeanNotificationInfo[b].getName())) {
          bool = true;
          ModelMBeanNotificationInfo modelMBeanNotificationInfo = (ModelMBeanNotificationInfo)arrayOfMBeanNotificationInfo[b];
          modelMBeanNotificationInfo.setDescriptor(paramDescriptor);
        } 
      } 
    } else {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Invalid descriptor type: " + paramString);
      throw new RuntimeOperationsException(illegalArgumentException, "Exception occurred trying to set the descriptors of the MBean");
    } 
    if (!bool) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Descriptor name is invalid: type=" + paramString + "; name=" + str);
      throw new RuntimeOperationsException(illegalArgumentException, "Exception occurred trying to set the descriptors of the MBean");
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setDescriptor(Descriptor,String)", "Exit"); 
  }
  
  public ModelMBeanAttributeInfo getAttribute(String paramString) throws MBeanException, RuntimeOperationsException {
    ModelMBeanAttributeInfo modelMBeanAttributeInfo = null;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getAttribute(String)", "Entry"); 
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute Name is null"), "Exception occurred trying to get the ModelMBeanAttributeInfo of the MBean"); 
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = this.modelMBeanAttributes;
    int i = 0;
    if (arrayOfMBeanAttributeInfo != null)
      i = arrayOfMBeanAttributeInfo.length; 
    for (byte b = 0; b < i && modelMBeanAttributeInfo == null; b++) {
      if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
        StringBuilder stringBuilder = (new StringBuilder()).append("\t\n this.getAttributes() MBeanAttributeInfo Array ").append(b).append(":").append(((ModelMBeanAttributeInfo)arrayOfMBeanAttributeInfo[b]).getDescriptor()).append("\t\n this.modelMBeanAttributes MBeanAttributeInfo Array ").append(b).append(":").append(((ModelMBeanAttributeInfo)this.modelMBeanAttributes[b]).getDescriptor());
        JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getAttribute(String)", stringBuilder.toString());
      } 
      if (paramString.equals(arrayOfMBeanAttributeInfo[b].getName()))
        modelMBeanAttributeInfo = (ModelMBeanAttributeInfo)arrayOfMBeanAttributeInfo[b].clone(); 
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getAttribute(String)", "Exit"); 
    return modelMBeanAttributeInfo;
  }
  
  public ModelMBeanOperationInfo getOperation(String paramString) throws MBeanException, RuntimeOperationsException {
    ModelMBeanOperationInfo modelMBeanOperationInfo = null;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getOperation(String)", "Entry"); 
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("inName is null"), "Exception occurred trying to get the ModelMBeanOperationInfo of the MBean"); 
    MBeanOperationInfo[] arrayOfMBeanOperationInfo = this.modelMBeanOperations;
    int i = 0;
    if (arrayOfMBeanOperationInfo != null)
      i = arrayOfMBeanOperationInfo.length; 
    for (byte b = 0; b < i && modelMBeanOperationInfo == null; b++) {
      if (paramString.equals(arrayOfMBeanOperationInfo[b].getName()))
        modelMBeanOperationInfo = (ModelMBeanOperationInfo)arrayOfMBeanOperationInfo[b].clone(); 
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getOperation(String)", "Exit"); 
    return modelMBeanOperationInfo;
  }
  
  public ModelMBeanConstructorInfo getConstructor(String paramString) throws MBeanException, RuntimeOperationsException {
    ModelMBeanConstructorInfo modelMBeanConstructorInfo = null;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getConstructor(String)", "Entry"); 
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Constructor name is null"), "Exception occurred trying to get the ModelMBeanConstructorInfo of the MBean"); 
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = this.modelMBeanConstructors;
    int i = 0;
    if (arrayOfMBeanConstructorInfo != null)
      i = arrayOfMBeanConstructorInfo.length; 
    for (byte b = 0; b < i && modelMBeanConstructorInfo == null; b++) {
      if (paramString.equals(arrayOfMBeanConstructorInfo[b].getName()))
        modelMBeanConstructorInfo = (ModelMBeanConstructorInfo)arrayOfMBeanConstructorInfo[b].clone(); 
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getConstructor(String)", "Exit"); 
    return modelMBeanConstructorInfo;
  }
  
  public ModelMBeanNotificationInfo getNotification(String paramString) throws MBeanException, RuntimeOperationsException {
    ModelMBeanNotificationInfo modelMBeanNotificationInfo = null;
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getNotification(String)", "Entry"); 
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Notification name is null"), "Exception occurred trying to get the ModelMBeanNotificationInfo of the MBean"); 
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = this.modelMBeanNotifications;
    int i = 0;
    if (arrayOfMBeanNotificationInfo != null)
      i = arrayOfMBeanNotificationInfo.length; 
    for (byte b = 0; b < i && modelMBeanNotificationInfo == null; b++) {
      if (paramString.equals(arrayOfMBeanNotificationInfo[b].getName()))
        modelMBeanNotificationInfo = (ModelMBeanNotificationInfo)arrayOfMBeanNotificationInfo[b].clone(); 
    } 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getNotification(String)", "Exit"); 
    return modelMBeanNotificationInfo;
  }
  
  public Descriptor getDescriptor() { return getMBeanDescriptorNoException(); }
  
  public Descriptor getMBeanDescriptor() { return getMBeanDescriptorNoException(); }
  
  private Descriptor getMBeanDescriptorNoException() {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getMBeanDescriptorNoException()", "Entry"); 
    if (this.modelMBeanDescriptor == null)
      this.modelMBeanDescriptor = validDescriptor(null); 
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "getMBeanDescriptorNoException()", "Exit, returning: " + this.modelMBeanDescriptor); 
    return (Descriptor)this.modelMBeanDescriptor.clone();
  }
  
  public void setMBeanDescriptor(Descriptor paramDescriptor) throws MBeanException, RuntimeOperationsException {
    if (JmxProperties.MODELMBEAN_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MODELMBEAN_LOGGER.logp(Level.FINER, ModelMBeanInfoSupport.class.getName(), "setMBeanDescriptor(Descriptor)", "Entry"); 
    this.modelMBeanDescriptor = validDescriptor(paramDescriptor);
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
      descriptor.setField("name", getClassName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor name to " + getClassName());
    } 
    if (bool && descriptor.getFieldValue("descriptorType") == null) {
      descriptor.setField("descriptorType", "mbean");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"mbean\"");
    } 
    if (descriptor.getFieldValue("displayName") == null) {
      descriptor.setField("displayName", getClassName());
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + getClassName());
    } 
    if (descriptor.getFieldValue("persistPolicy") == null) {
      descriptor.setField("persistPolicy", "never");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor persistPolicy to \"never\"");
    } 
    if (descriptor.getFieldValue("log") == null) {
      descriptor.setField("log", "F");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor \"log\" field to \"F\"");
    } 
    if (descriptor.getFieldValue("visibility") == null) {
      descriptor.setField("visibility", "1");
      JmxProperties.MODELMBEAN_LOGGER.finer("Defaulting Descriptor visibility to 1");
    } 
    if (!descriptor.isValid())
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The isValid() method of the Descriptor object itself returned false,one or more required fields are invalid. Descriptor:" + descriptor.toString()); 
    if (!((String)descriptor.getFieldValue("descriptorType")).equalsIgnoreCase("mbean"))
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"), "The Descriptor \"descriptorType\" field does not match the object described.  Expected: mbean , was: " + descriptor.getFieldValue("descriptorType")); 
    return descriptor;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
      this.modelMBeanDescriptor = (Descriptor)getField.get("modelMBeanDescriptor", null);
      if (getField.defaulted("modelMBeanDescriptor"))
        throw new NullPointerException("modelMBeanDescriptor"); 
      this.modelMBeanAttributes = (MBeanAttributeInfo[])getField.get("mmbAttributes", null);
      if (getField.defaulted("mmbAttributes"))
        throw new NullPointerException("mmbAttributes"); 
      this.modelMBeanConstructors = (MBeanConstructorInfo[])getField.get("mmbConstructors", null);
      if (getField.defaulted("mmbConstructors"))
        throw new NullPointerException("mmbConstructors"); 
      this.modelMBeanNotifications = (MBeanNotificationInfo[])getField.get("mmbNotifications", null);
      if (getField.defaulted("mmbNotifications"))
        throw new NullPointerException("mmbNotifications"); 
      this.modelMBeanOperations = (MBeanOperationInfo[])getField.get("mmbOperations", null);
      if (getField.defaulted("mmbOperations"))
        throw new NullPointerException("mmbOperations"); 
    } else {
      paramObjectInputStream.defaultReadObject();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("modelMBeanDescriptor", this.modelMBeanDescriptor);
      putField.put("mmbAttributes", this.modelMBeanAttributes);
      putField.put("mmbConstructors", this.modelMBeanConstructors);
      putField.put("mmbNotifications", this.modelMBeanNotifications);
      putField.put("mmbOperations", this.modelMBeanOperations);
      putField.put("currClass", "ModelMBeanInfoSupport");
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
      serialVersionUID = -3944083498453227709L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -1935722590756516193L;
    } 
    NO_ATTRIBUTES = new ModelMBeanAttributeInfo[0];
    NO_CONSTRUCTORS = new ModelMBeanConstructorInfo[0];
    NO_NOTIFICATIONS = new ModelMBeanNotificationInfo[0];
    NO_OPERATIONS = new ModelMBeanOperationInfo[0];
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\modelmbean\ModelMBeanInfoSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */