package sun.management;

import com.sun.management.DiagnosticCommandMBean;
import java.lang.reflect.Constructor;
import java.security.Permission;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;

class DiagnosticCommandImpl extends NotificationEmitterSupport implements DiagnosticCommandMBean {
  private final VMManagement jvm;
  
  private static final String strClassName = "".getClass().getName();
  
  private static final String strArrayClassName = String[].class.getName();
  
  private final boolean isSupported;
  
  private static final String notifName = "javax.management.Notification";
  
  private static final String[] diagFramNotifTypes = { "jmx.mbean.info.changed" };
  
  private MBeanNotificationInfo[] notifInfo = null;
  
  private static long seqNumber = 0L;
  
  public Object getAttribute(String paramString) throws AttributeNotFoundException, MBeanException, ReflectionException { throw new AttributeNotFoundException(paramString); }
  
  public void setAttribute(Attribute paramAttribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException { throw new AttributeNotFoundException(paramAttribute.getName()); }
  
  public AttributeList getAttributes(String[] paramArrayOfString) { return new AttributeList(); }
  
  public AttributeList setAttributes(AttributeList paramAttributeList) { return new AttributeList(); }
  
  DiagnosticCommandImpl(VMManagement paramVMManagement) {
    this.jvm = paramVMManagement;
    this.isSupported = paramVMManagement.isRemoteDiagnosticCommandsSupported();
  }
  
  public MBeanInfo getMBeanInfo() {
    Map map;
    TreeSet treeSet = new TreeSet(new OperationInfoComparator(null));
    if (!this.isSupported) {
      map = Collections.EMPTY_MAP;
    } else {
      try {
        String[] arrayOfString = getDiagnosticCommands();
        DiagnosticCommandInfo[] arrayOfDiagnosticCommandInfo = getDiagnosticCommandInfo(arrayOfString);
        MBeanParameterInfo[] arrayOfMBeanParameterInfo = { new MBeanParameterInfo("arguments", strArrayClassName, "Array of Diagnostic Commands Arguments and Options") };
        map = new HashMap();
        for (byte b = 0; b < arrayOfString.length; b++) {
          String str = transform(arrayOfString[b]);
          try {
            Wrapper wrapper = new Wrapper(str, arrayOfString[b], arrayOfDiagnosticCommandInfo[b]);
            map.put(str, wrapper);
            treeSet.add(new MBeanOperationInfo(wrapper.name, wrapper.info.getDescription(), (wrapper.info.getArgumentsInfo() == null || wrapper.info.getArgumentsInfo().isEmpty()) ? null : arrayOfMBeanParameterInfo, strClassName, 2, commandDescriptor(wrapper)));
          } catch (InstantiationException instantiationException) {}
        } 
      } catch (IllegalArgumentException|UnsupportedOperationException illegalArgumentException) {
        map = Collections.EMPTY_MAP;
      } 
    } 
    this.wrappers = Collections.unmodifiableMap(map);
    HashMap hashMap = new HashMap();
    hashMap.put("immutableInfo", "false");
    hashMap.put("interfaceClassName", "com.sun.management.DiagnosticCommandMBean");
    hashMap.put("mxbean", "false");
    ImmutableDescriptor immutableDescriptor = new ImmutableDescriptor(hashMap);
    return new MBeanInfo(getClass().getName(), "Diagnostic Commands", null, null, (MBeanOperationInfo[])treeSet.toArray(new MBeanOperationInfo[treeSet.size()]), getNotificationInfo(), immutableDescriptor);
  }
  
  public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws MBeanException, ReflectionException {
    if (!this.isSupported)
      throw new UnsupportedOperationException(); 
    if (this.wrappers == null)
      getMBeanInfo(); 
    Wrapper wrapper = (Wrapper)this.wrappers.get(paramString);
    if (wrapper != null) {
      if (wrapper.info.getArgumentsInfo().isEmpty() && (paramArrayOfObject == null || paramArrayOfObject.length == 0) && (paramArrayOfString == null || paramArrayOfString.length == 0))
        return wrapper.execute(null); 
      if (paramArrayOfObject != null && paramArrayOfObject.length == 1 && paramArrayOfString != null && paramArrayOfString.length == 1 && paramArrayOfString[false] != null && paramArrayOfString[0].compareTo(strArrayClassName) == 0)
        return wrapper.execute((String[])paramArrayOfObject[0]); 
    } 
    throw new ReflectionException(new NoSuchMethodException(paramString));
  }
  
  private static String transform(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    boolean bool1 = true;
    boolean bool2 = false;
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c == '.' || c == '_') {
        bool1 = false;
        bool2 = true;
      } else if (bool2) {
        bool2 = false;
        stringBuilder.append(Character.toUpperCase(c));
      } else if (bool1) {
        stringBuilder.append(Character.toLowerCase(c));
      } else {
        stringBuilder.append(c);
      } 
    } 
    return stringBuilder.toString();
  }
  
  private Descriptor commandDescriptor(Wrapper paramWrapper) throws IllegalArgumentException {
    HashMap hashMap = new HashMap();
    hashMap.put("dcmd.name", paramWrapper.info.getName());
    hashMap.put("dcmd.description", paramWrapper.info.getDescription());
    hashMap.put("dcmd.vmImpact", paramWrapper.info.getImpact());
    hashMap.put("dcmd.permissionClass", paramWrapper.info.getPermissionClass());
    hashMap.put("dcmd.permissionName", paramWrapper.info.getPermissionName());
    hashMap.put("dcmd.permissionAction", paramWrapper.info.getPermissionAction());
    hashMap.put("dcmd.enabled", Boolean.valueOf(paramWrapper.info.isEnabled()));
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("help ");
    stringBuilder.append(paramWrapper.info.getName());
    hashMap.put("dcmd.help", executeDiagnosticCommand(stringBuilder.toString()));
    if (paramWrapper.info.getArgumentsInfo() != null && !paramWrapper.info.getArgumentsInfo().isEmpty()) {
      HashMap hashMap1 = new HashMap();
      for (DiagnosticCommandArgumentInfo diagnosticCommandArgumentInfo : paramWrapper.info.getArgumentsInfo()) {
        HashMap hashMap2 = new HashMap();
        hashMap2.put("dcmd.arg.name", diagnosticCommandArgumentInfo.getName());
        hashMap2.put("dcmd.arg.type", diagnosticCommandArgumentInfo.getType());
        hashMap2.put("dcmd.arg.description", diagnosticCommandArgumentInfo.getDescription());
        hashMap2.put("dcmd.arg.isMandatory", Boolean.valueOf(diagnosticCommandArgumentInfo.isMandatory()));
        hashMap2.put("dcmd.arg.isMultiple", Boolean.valueOf(diagnosticCommandArgumentInfo.isMultiple()));
        boolean bool = diagnosticCommandArgumentInfo.isOption();
        hashMap2.put("dcmd.arg.isOption", Boolean.valueOf(bool));
        if (!bool) {
          hashMap2.put("dcmd.arg.position", Integer.valueOf(diagnosticCommandArgumentInfo.getPosition()));
        } else {
          hashMap2.put("dcmd.arg.position", Integer.valueOf(-1));
        } 
        hashMap1.put(diagnosticCommandArgumentInfo.getName(), new ImmutableDescriptor(hashMap2));
      } 
      hashMap.put("dcmd.arguments", new ImmutableDescriptor(hashMap1));
    } 
    return new ImmutableDescriptor(hashMap);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo() {
    synchronized (this) {
      if (this.notifInfo == null) {
        this.notifInfo = new MBeanNotificationInfo[1];
        this.notifInfo[0] = new MBeanNotificationInfo(diagFramNotifTypes, "javax.management.Notification", "Diagnostic Framework Notification");
      } 
    } 
    return (MBeanNotificationInfo[])this.notifInfo.clone();
  }
  
  private static long getNextSeqNumber() { return ++seqNumber; }
  
  private void createDiagnosticFrameworkNotification() {
    if (!hasListeners())
      return; 
    ObjectName objectName = null;
    try {
      objectName = ObjectName.getInstance("com.sun.management:type=DiagnosticCommand");
    } catch (MalformedObjectNameException malformedObjectNameException) {}
    Notification notification = new Notification("jmx.mbean.info.changed", objectName, getNextSeqNumber());
    notification.setUserData(getMBeanInfo());
    sendNotification(notification);
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) {
    boolean bool1 = hasListeners();
    super.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    boolean bool2 = hasListeners();
    if (!bool1 && bool2)
      setNotificationEnabled(true); 
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException {
    boolean bool1 = hasListeners();
    super.removeNotificationListener(paramNotificationListener);
    boolean bool2 = hasListeners();
    if (bool1 && !bool2)
      setNotificationEnabled(false); 
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) {
    boolean bool1 = hasListeners();
    super.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    boolean bool2 = hasListeners();
    if (bool1 && !bool2)
      setNotificationEnabled(false); 
  }
  
  private native void setNotificationEnabled(boolean paramBoolean);
  
  private native String[] getDiagnosticCommands();
  
  private native DiagnosticCommandInfo[] getDiagnosticCommandInfo(String[] paramArrayOfString);
  
  private native String executeDiagnosticCommand(String paramString);
  
  private static class OperationInfoComparator extends Object implements Comparator<MBeanOperationInfo> {
    private OperationInfoComparator() {}
    
    public int compare(MBeanOperationInfo param1MBeanOperationInfo1, MBeanOperationInfo param1MBeanOperationInfo2) { return param1MBeanOperationInfo1.getName().compareTo(param1MBeanOperationInfo2.getName()); }
  }
  
  private class Wrapper {
    String name;
    
    String cmd;
    
    DiagnosticCommandInfo info;
    
    Permission permission;
    
    Wrapper(String param1String1, String param1String2, DiagnosticCommandInfo param1DiagnosticCommandInfo) throws InstantiationException {
      this.name = param1String1;
      this.cmd = param1String2;
      this.info = param1DiagnosticCommandInfo;
      this.permission = null;
      InstantiationException instantiationException = null;
      if (param1DiagnosticCommandInfo.getPermissionClass() != null) {
        try {
          Class clazz = Class.forName(param1DiagnosticCommandInfo.getPermissionClass());
          if (param1DiagnosticCommandInfo.getPermissionAction() == null)
            try {
              Constructor constructor = clazz.getConstructor(new Class[] { String.class });
              this.permission = (Permission)constructor.newInstance(new Object[] { param1DiagnosticCommandInfo.getPermissionName() });
            } catch (InstantiationException|IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException|NoSuchMethodException|SecurityException instantiationException1) {
              instantiationException = instantiationException1;
            }  
          if (this.permission == null)
            try {
              Constructor constructor = clazz.getConstructor(new Class[] { String.class, String.class });
              this.permission = (Permission)constructor.newInstance(new Object[] { param1DiagnosticCommandInfo.getPermissionName(), param1DiagnosticCommandInfo.getPermissionAction() });
            } catch (InstantiationException|IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException|NoSuchMethodException|SecurityException instantiationException1) {
              instantiationException = instantiationException1;
            }  
        } catch (ClassNotFoundException classNotFoundException) {}
        if (this.permission == null) {
          InstantiationException instantiationException1 = new InstantiationException("Unable to instantiate required permission");
          instantiationException1.initCause(instantiationException);
        } 
      } 
    }
    
    public String execute(String[] param1ArrayOfString) {
      if (this.permission != null) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null)
          securityManager.checkPermission(this.permission); 
      } 
      if (param1ArrayOfString == null)
        return DiagnosticCommandImpl.this.executeDiagnosticCommand(this.cmd); 
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(this.cmd);
      for (byte b = 0; b < param1ArrayOfString.length; b++) {
        if (param1ArrayOfString[b] == null)
          throw new IllegalArgumentException("Invalid null argument"); 
        stringBuilder.append(" ");
        stringBuilder.append(param1ArrayOfString[b]);
      } 
      return DiagnosticCommandImpl.this.executeDiagnosticCommand(stringBuilder.toString());
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\DiagnosticCommandImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */