package javax.management.remote.rmi;

import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.SerializablePermission;
import java.rmi.MarshalledObject;
import java.rmi.UnexpectedException;
import java.util.Set;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.NotificationResult;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Util;
import javax.security.auth.Subject;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class _RMIConnection_Stub extends Stub implements RMIConnection {
  private static final String[] _type_ids = { "RMI:javax.management.remote.rmi.RMIConnection:0000000000000000" };
  
  private boolean _instantiated = false;
  
  static Class array$Ljava$lang$String;
  
  static Class array$Ljavax$management$ObjectName;
  
  static Class array$Ljava$rmi$MarshalledObject;
  
  static Class array$Ljavax$security$auth$Subject;
  
  static Class array$Ljava$lang$Integer;
  
  public _RMIConnection_Stub() {
    this(checkPermission());
    this._instantiated = true;
  }
  
  private _RMIConnection_Stub(Void paramVoid) {}
  
  public String[] _ids() { return (String[])_type_ids.clone(); }
  
  public void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject1, MarshalledObject paramMarshalledObject2, Subject paramSubject) throws InstanceNotFoundException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this)) {
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("addNotificationListener", true);
          outputStream.write_value(paramObjectName1, ObjectName.class);
          outputStream.write_value(paramObjectName2, ObjectName.class);
          outputStream.write_value(paramMarshalledObject1, MarshalledObject.class);
          outputStream.write_value(paramMarshalledObject2, MarshalledObject.class);
          outputStream.write_value(paramSubject, Subject.class);
          _invoke(outputStream);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          addNotificationListener(paramObjectName1, paramObjectName2, paramMarshalledObject1, paramMarshalledObject2, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      } 
    } else {
      ServantObject servantObject = _servant_preinvoke("addNotificationListener", RMIConnection.class);
      if (servantObject == null) {
        addNotificationListener(paramObjectName1, paramObjectName2, paramMarshalledObject1, paramMarshalledObject2, paramSubject);
        return;
      } 
      try {
        Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName1, paramObjectName2, paramMarshalledObject1, paramMarshalledObject2, paramSubject }, _orb());
        ObjectName objectName1 = (ObjectName)arrayOfObject[0];
        ObjectName objectName2 = (ObjectName)arrayOfObject[1];
        MarshalledObject marshalledObject1 = (MarshalledObject)arrayOfObject[2];
        MarshalledObject marshalledObject2 = (MarshalledObject)arrayOfObject[3];
        Subject subject = (Subject)arrayOfObject[4];
        ((RMIConnection)servantObject.servant).addNotificationListener(objectName1, objectName2, marshalledObject1, marshalledObject2, subject);
      } catch (Throwable throwable1) {
        Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
        if (throwable2 instanceof InstanceNotFoundException)
          throw (InstanceNotFoundException)throwable2; 
        if (throwable2 instanceof IOException)
          throw (IOException)throwable2; 
        throw Util.wrapException(throwable2);
      } finally {
        _servant_postinvoke(servantObject);
      } 
    } 
  }
  
  public Integer[] addNotificationListeners(ObjectName[] paramArrayOfObjectName, MarshalledObject[] paramArrayOfMarshalledObject, Subject[] paramArrayOfSubject) throws InstanceNotFoundException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("addNotificationListeners", true);
          outputStream.write_value(cast_array(paramArrayOfObjectName), (array$Ljavax$management$ObjectName != null) ? array$Ljavax$management$ObjectName : (array$Ljavax$management$ObjectName = class$("[Ljavax.management.ObjectName;")));
          outputStream.write_value(cast_array(paramArrayOfMarshalledObject), (array$Ljava$rmi$MarshalledObject != null) ? array$Ljava$rmi$MarshalledObject : (array$Ljava$rmi$MarshalledObject = class$("[Ljava.rmi.MarshalledObject;")));
          outputStream.write_value(cast_array(paramArrayOfSubject), (array$Ljavax$security$auth$Subject != null) ? array$Ljavax$security$auth$Subject : (array$Ljavax$security$auth$Subject = class$("[Ljavax.security.auth.Subject;")));
          inputStream = (InputStream)_invoke(outputStream);
          return (Integer[])inputStream.read_value((array$Ljava$lang$Integer != null) ? array$Ljava$lang$Integer : (array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")));
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return addNotificationListeners(paramArrayOfObjectName, paramArrayOfMarshalledObject, paramArrayOfSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("addNotificationListeners", RMIConnection.class);
    if (servantObject == null)
      return addNotificationListeners(paramArrayOfObjectName, paramArrayOfMarshalledObject, paramArrayOfSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramArrayOfObjectName, paramArrayOfMarshalledObject, paramArrayOfSubject }, _orb());
      ObjectName[] arrayOfObjectName = (ObjectName[])arrayOfObject[0];
      MarshalledObject[] arrayOfMarshalledObject = (MarshalledObject[])arrayOfObject[1];
      Subject[] arrayOfSubject = (Subject[])arrayOfObject[2];
      Integer[] arrayOfInteger = ((RMIConnection)servantObject.servant).addNotificationListeners(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject);
      return (Integer[])Util.copyObject(arrayOfInteger, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)throwable2; 
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  private Serializable cast_array(Object paramObject) { return (Serializable)paramObject; }
  
  private static Void checkPermission() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new SerializablePermission("enableSubclassImplementation")); 
    return null;
  }
  
  public void close() {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this)) {
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = _request("close", true);
          _invoke(outputStream);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          close();
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      } 
    } else {
      ServantObject servantObject = _servant_preinvoke("close", RMIConnection.class);
      if (servantObject == null) {
        close();
        return;
      } 
      try {
        ((RMIConnection)servantObject.servant).close();
      } catch (Throwable throwable1) {
        Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
        if (throwable2 instanceof IOException)
          throw (IOException)throwable2; 
        throw Util.wrapException(throwable2);
      } finally {
        _servant_postinvoke(servantObject);
      } 
    } 
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("createMBean__CORBA_WStringValue__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject", true);
          outputStream.write_value(paramString, String.class);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(paramMarshalledObject, MarshalledObject.class);
          outputStream.write_value(cast_array(paramArrayOfString), (array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return (ObjectInstance)inputStream.read_value(ObjectInstance.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/ReflectionEx:1.0"))
            throw (ReflectionException)inputStream.read_value(ReflectionException.class); 
          if (str.equals("IDL:javax/management/InstanceAlreadyExistsEx:1.0"))
            throw (InstanceAlreadyExistsException)inputStream.read_value(InstanceAlreadyExistsException.class); 
          if (str.equals("IDL:javax/management/MBeanRegistrationEx:1.0"))
            throw (MBeanRegistrationException)inputStream.read_value(MBeanRegistrationException.class); 
          if (str.equals("IDL:javax/management/MBeanEx:1.0"))
            throw (MBeanException)inputStream.read_value(MBeanException.class); 
          if (str.equals("IDL:javax/management/NotCompliantMBeanEx:1.0"))
            throw (NotCompliantMBeanException)inputStream.read_value(NotCompliantMBeanException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return createMBean(paramString, paramObjectName, paramMarshalledObject, paramArrayOfString, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("createMBean__CORBA_WStringValue__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject", RMIConnection.class);
    if (servantObject == null)
      return createMBean(paramString, paramObjectName, paramMarshalledObject, paramArrayOfString, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramString, paramObjectName, paramMarshalledObject, paramArrayOfString, paramSubject }, _orb());
      String str = (String)arrayOfObject[0];
      ObjectName objectName = (ObjectName)arrayOfObject[1];
      MarshalledObject marshalledObject = (MarshalledObject)arrayOfObject[2];
      String[] arrayOfString = (String[])arrayOfObject[3];
      Subject subject = (Subject)arrayOfObject[4];
      ObjectInstance objectInstance = ((RMIConnection)servantObject.servant).createMBean(str, objectName, marshalledObject, arrayOfString, subject);
      return (ObjectInstance)Util.copyObject(objectInstance, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof ReflectionException)
        throw (ReflectionException)throwable2; 
      if (throwable2 instanceof InstanceAlreadyExistsException)
        throw (InstanceAlreadyExistsException)throwable2; 
      if (throwable2 instanceof MBeanRegistrationException)
        throw (MBeanRegistrationException)throwable2; 
      if (throwable2 instanceof MBeanException)
        throw (MBeanException)throwable2; 
      if (throwable2 instanceof NotCompliantMBeanException)
        throw (NotCompliantMBeanException)throwable2; 
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject", true);
          outputStream.write_value(paramString, String.class);
          outputStream.write_value(paramObjectName1, ObjectName.class);
          outputStream.write_value(paramObjectName2, ObjectName.class);
          outputStream.write_value(paramMarshalledObject, MarshalledObject.class);
          outputStream.write_value(cast_array(paramArrayOfString), (array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return (ObjectInstance)inputStream.read_value(ObjectInstance.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/ReflectionEx:1.0"))
            throw (ReflectionException)inputStream.read_value(ReflectionException.class); 
          if (str.equals("IDL:javax/management/InstanceAlreadyExistsEx:1.0"))
            throw (InstanceAlreadyExistsException)inputStream.read_value(InstanceAlreadyExistsException.class); 
          if (str.equals("IDL:javax/management/MBeanRegistrationEx:1.0"))
            throw (MBeanRegistrationException)inputStream.read_value(MBeanRegistrationException.class); 
          if (str.equals("IDL:javax/management/MBeanEx:1.0"))
            throw (MBeanException)inputStream.read_value(MBeanException.class); 
          if (str.equals("IDL:javax/management/NotCompliantMBeanEx:1.0"))
            throw (NotCompliantMBeanException)inputStream.read_value(NotCompliantMBeanException.class); 
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return createMBean(paramString, paramObjectName1, paramObjectName2, paramMarshalledObject, paramArrayOfString, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject", RMIConnection.class);
    if (servantObject == null)
      return createMBean(paramString, paramObjectName1, paramObjectName2, paramMarshalledObject, paramArrayOfString, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramString, paramObjectName1, paramObjectName2, paramMarshalledObject, paramArrayOfString, paramSubject }, _orb());
      String str = (String)arrayOfObject[0];
      ObjectName objectName1 = (ObjectName)arrayOfObject[1];
      ObjectName objectName2 = (ObjectName)arrayOfObject[2];
      MarshalledObject marshalledObject = (MarshalledObject)arrayOfObject[3];
      String[] arrayOfString = (String[])arrayOfObject[4];
      Subject subject = (Subject)arrayOfObject[5];
      ObjectInstance objectInstance = ((RMIConnection)servantObject.servant).createMBean(str, objectName1, objectName2, marshalledObject, arrayOfString, subject);
      return (ObjectInstance)Util.copyObject(objectInstance, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof ReflectionException)
        throw (ReflectionException)throwable2; 
      if (throwable2 instanceof InstanceAlreadyExistsException)
        throw (InstanceAlreadyExistsException)throwable2; 
      if (throwable2 instanceof MBeanRegistrationException)
        throw (MBeanRegistrationException)throwable2; 
      if (throwable2 instanceof MBeanException)
        throw (MBeanException)throwable2; 
      if (throwable2 instanceof NotCompliantMBeanException)
        throw (NotCompliantMBeanException)throwable2; 
      if (throwable2 instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)throwable2; 
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Subject paramSubject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject", true);
          outputStream.write_value(paramString, String.class);
          outputStream.write_value(paramObjectName1, ObjectName.class);
          outputStream.write_value(paramObjectName2, ObjectName.class);
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return (ObjectInstance)inputStream.read_value(ObjectInstance.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/ReflectionEx:1.0"))
            throw (ReflectionException)inputStream.read_value(ReflectionException.class); 
          if (str.equals("IDL:javax/management/InstanceAlreadyExistsEx:1.0"))
            throw (InstanceAlreadyExistsException)inputStream.read_value(InstanceAlreadyExistsException.class); 
          if (str.equals("IDL:javax/management/MBeanRegistrationEx:1.0"))
            throw (MBeanRegistrationException)inputStream.read_value(MBeanRegistrationException.class); 
          if (str.equals("IDL:javax/management/MBeanEx:1.0"))
            throw (MBeanException)inputStream.read_value(MBeanException.class); 
          if (str.equals("IDL:javax/management/NotCompliantMBeanEx:1.0"))
            throw (NotCompliantMBeanException)inputStream.read_value(NotCompliantMBeanException.class); 
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return createMBean(paramString, paramObjectName1, paramObjectName2, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject", RMIConnection.class);
    if (servantObject == null)
      return createMBean(paramString, paramObjectName1, paramObjectName2, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramString, paramObjectName1, paramObjectName2, paramSubject }, _orb());
      String str = (String)arrayOfObject[0];
      ObjectName objectName1 = (ObjectName)arrayOfObject[1];
      ObjectName objectName2 = (ObjectName)arrayOfObject[2];
      Subject subject = (Subject)arrayOfObject[3];
      ObjectInstance objectInstance = ((RMIConnection)servantObject.servant).createMBean(str, objectName1, objectName2, subject);
      return (ObjectInstance)Util.copyObject(objectInstance, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof ReflectionException)
        throw (ReflectionException)throwable2; 
      if (throwable2 instanceof InstanceAlreadyExistsException)
        throw (InstanceAlreadyExistsException)throwable2; 
      if (throwable2 instanceof MBeanRegistrationException)
        throw (MBeanRegistrationException)throwable2; 
      if (throwable2 instanceof MBeanException)
        throw (MBeanException)throwable2; 
      if (throwable2 instanceof NotCompliantMBeanException)
        throw (NotCompliantMBeanException)throwable2; 
      if (throwable2 instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)throwable2; 
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Subject paramSubject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_security_auth_Subject", true);
          outputStream.write_value(paramString, String.class);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return (ObjectInstance)inputStream.read_value(ObjectInstance.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/ReflectionEx:1.0"))
            throw (ReflectionException)inputStream.read_value(ReflectionException.class); 
          if (str.equals("IDL:javax/management/InstanceAlreadyExistsEx:1.0"))
            throw (InstanceAlreadyExistsException)inputStream.read_value(InstanceAlreadyExistsException.class); 
          if (str.equals("IDL:javax/management/MBeanRegistrationEx:1.0"))
            throw (MBeanRegistrationException)inputStream.read_value(MBeanRegistrationException.class); 
          if (str.equals("IDL:javax/management/MBeanEx:1.0"))
            throw (MBeanException)inputStream.read_value(MBeanException.class); 
          if (str.equals("IDL:javax/management/NotCompliantMBeanEx:1.0"))
            throw (NotCompliantMBeanException)inputStream.read_value(NotCompliantMBeanException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return createMBean(paramString, paramObjectName, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_security_auth_Subject", RMIConnection.class);
    if (servantObject == null)
      return createMBean(paramString, paramObjectName, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramString, paramObjectName, paramSubject }, _orb());
      String str = (String)arrayOfObject[0];
      ObjectName objectName = (ObjectName)arrayOfObject[1];
      Subject subject = (Subject)arrayOfObject[2];
      ObjectInstance objectInstance = ((RMIConnection)servantObject.servant).createMBean(str, objectName, subject);
      return (ObjectInstance)Util.copyObject(objectInstance, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof ReflectionException)
        throw (ReflectionException)throwable2; 
      if (throwable2 instanceof InstanceAlreadyExistsException)
        throw (InstanceAlreadyExistsException)throwable2; 
      if (throwable2 instanceof MBeanRegistrationException)
        throw (MBeanRegistrationException)throwable2; 
      if (throwable2 instanceof MBeanException)
        throw (MBeanException)throwable2; 
      if (throwable2 instanceof NotCompliantMBeanException)
        throw (NotCompliantMBeanException)throwable2; 
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public NotificationResult fetchNotifications(long paramLong1, int paramInt, long paramLong2) throws IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = _request("fetchNotifications", true);
          outputStream.write_longlong(paramLong1);
          outputStream.write_long(paramInt);
          outputStream.write_longlong(paramLong2);
          inputStream = (InputStream)_invoke(outputStream);
          return (NotificationResult)inputStream.read_value(NotificationResult.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return fetchNotifications(paramLong1, paramInt, paramLong2);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("fetchNotifications", RMIConnection.class);
    if (servantObject == null)
      return fetchNotifications(paramLong1, paramInt, paramLong2); 
    try {
      NotificationResult notificationResult = ((RMIConnection)servantObject.servant).fetchNotifications(paramLong1, paramInt, paramLong2);
      return (NotificationResult)Util.copyObject(notificationResult, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public Object getAttribute(ObjectName paramObjectName, String paramString, Subject paramSubject) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("getAttribute", true);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(paramString, String.class);
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return Util.readAny(inputStream);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/MBeanEx:1.0"))
            throw (MBeanException)inputStream.read_value(MBeanException.class); 
          if (str.equals("IDL:javax/management/AttributeNotFoundEx:1.0"))
            throw (AttributeNotFoundException)inputStream.read_value(AttributeNotFoundException.class); 
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:javax/management/ReflectionEx:1.0"))
            throw (ReflectionException)inputStream.read_value(ReflectionException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return getAttribute(paramObjectName, paramString, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("getAttribute", RMIConnection.class);
    if (servantObject == null)
      return getAttribute(paramObjectName, paramString, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName, paramString, paramSubject }, _orb());
      ObjectName objectName = (ObjectName)arrayOfObject[0];
      String str = (String)arrayOfObject[1];
      Subject subject = (Subject)arrayOfObject[2];
      Object object = ((RMIConnection)servantObject.servant).getAttribute(objectName, str, subject);
      return Util.copyObject(object, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof MBeanException)
        throw (MBeanException)throwable2; 
      if (throwable2 instanceof AttributeNotFoundException)
        throw (AttributeNotFoundException)throwable2; 
      if (throwable2 instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)throwable2; 
      if (throwable2 instanceof ReflectionException)
        throw (ReflectionException)throwable2; 
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString, Subject paramSubject) throws InstanceNotFoundException, ReflectionException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("getAttributes", true);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(cast_array(paramArrayOfString), (array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return (AttributeList)inputStream.read_value(AttributeList.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:javax/management/ReflectionEx:1.0"))
            throw (ReflectionException)inputStream.read_value(ReflectionException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return getAttributes(paramObjectName, paramArrayOfString, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("getAttributes", RMIConnection.class);
    if (servantObject == null)
      return getAttributes(paramObjectName, paramArrayOfString, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName, paramArrayOfString, paramSubject }, _orb());
      ObjectName objectName = (ObjectName)arrayOfObject[0];
      String[] arrayOfString = (String[])arrayOfObject[1];
      Subject subject = (Subject)arrayOfObject[2];
      AttributeList attributeList = ((RMIConnection)servantObject.servant).getAttributes(objectName, arrayOfString, subject);
      return (AttributeList)Util.copyObject(attributeList, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)throwable2; 
      if (throwable2 instanceof ReflectionException)
        throw (ReflectionException)throwable2; 
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public String getConnectionId() throws IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = _request("getConnectionId", true);
          inputStream = (InputStream)_invoke(outputStream);
          return (String)inputStream.read_value(String.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return getConnectionId();
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("getConnectionId", RMIConnection.class);
    if (servantObject == null)
      return getConnectionId(); 
    try {
      return ((RMIConnection)servantObject.servant).getConnectionId();
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public String getDefaultDomain(Subject paramSubject) throws IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("getDefaultDomain", true);
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return (String)inputStream.read_value(String.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return getDefaultDomain(paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("getDefaultDomain", RMIConnection.class);
    if (servantObject == null)
      return getDefaultDomain(paramSubject); 
    try {
      Subject subject = (Subject)Util.copyObject(paramSubject, _orb());
      return ((RMIConnection)servantObject.servant).getDefaultDomain(subject);
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public String[] getDomains(Subject paramSubject) throws IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("getDomains", true);
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return (String[])inputStream.read_value((array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return getDomains(paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("getDomains", RMIConnection.class);
    if (servantObject == null)
      return getDomains(paramSubject); 
    try {
      Subject subject = (Subject)Util.copyObject(paramSubject, _orb());
      String[] arrayOfString = ((RMIConnection)servantObject.servant).getDomains(subject);
      return (String[])Util.copyObject(arrayOfString, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public Integer getMBeanCount(Subject paramSubject) throws IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("getMBeanCount", true);
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return (Integer)inputStream.read_value(Integer.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return getMBeanCount(paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("getMBeanCount", RMIConnection.class);
    if (servantObject == null)
      return getMBeanCount(paramSubject); 
    try {
      Subject subject = (Subject)Util.copyObject(paramSubject, _orb());
      Integer integer = ((RMIConnection)servantObject.servant).getMBeanCount(subject);
      return (Integer)Util.copyObject(integer, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public MBeanInfo getMBeanInfo(ObjectName paramObjectName, Subject paramSubject) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("getMBeanInfo", true);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return (MBeanInfo)inputStream.read_value(MBeanInfo.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:javax/management/IntrospectionEx:1.0"))
            throw (IntrospectionException)inputStream.read_value(IntrospectionException.class); 
          if (str.equals("IDL:javax/management/ReflectionEx:1.0"))
            throw (ReflectionException)inputStream.read_value(ReflectionException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return getMBeanInfo(paramObjectName, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("getMBeanInfo", RMIConnection.class);
    if (servantObject == null)
      return getMBeanInfo(paramObjectName, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName, paramSubject }, _orb());
      ObjectName objectName = (ObjectName)arrayOfObject[0];
      Subject subject = (Subject)arrayOfObject[1];
      MBeanInfo mBeanInfo = ((RMIConnection)servantObject.servant).getMBeanInfo(objectName, subject);
      return (MBeanInfo)Util.copyObject(mBeanInfo, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)throwable2; 
      if (throwable2 instanceof IntrospectionException)
        throw (IntrospectionException)throwable2; 
      if (throwable2 instanceof ReflectionException)
        throw (ReflectionException)throwable2; 
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public ObjectInstance getObjectInstance(ObjectName paramObjectName, Subject paramSubject) throws InstanceNotFoundException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("getObjectInstance", true);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return (ObjectInstance)inputStream.read_value(ObjectInstance.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return getObjectInstance(paramObjectName, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("getObjectInstance", RMIConnection.class);
    if (servantObject == null)
      return getObjectInstance(paramObjectName, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName, paramSubject }, _orb());
      ObjectName objectName = (ObjectName)arrayOfObject[0];
      Subject subject = (Subject)arrayOfObject[1];
      ObjectInstance objectInstance = ((RMIConnection)servantObject.servant).getObjectInstance(objectName, subject);
      return (ObjectInstance)Util.copyObject(objectInstance, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)throwable2; 
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public Object invoke(ObjectName paramObjectName, String paramString, MarshalledObject paramMarshalledObject, String[] paramArrayOfString, Subject paramSubject) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("invoke", true);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(paramString, String.class);
          outputStream.write_value(paramMarshalledObject, MarshalledObject.class);
          outputStream.write_value(cast_array(paramArrayOfString), (array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return Util.readAny(inputStream);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:javax/management/MBeanEx:1.0"))
            throw (MBeanException)inputStream.read_value(MBeanException.class); 
          if (str.equals("IDL:javax/management/ReflectionEx:1.0"))
            throw (ReflectionException)inputStream.read_value(ReflectionException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return invoke(paramObjectName, paramString, paramMarshalledObject, paramArrayOfString, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("invoke", RMIConnection.class);
    if (servantObject == null)
      return invoke(paramObjectName, paramString, paramMarshalledObject, paramArrayOfString, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName, paramString, paramMarshalledObject, paramArrayOfString, paramSubject }, _orb());
      ObjectName objectName = (ObjectName)arrayOfObject[0];
      String str = (String)arrayOfObject[1];
      MarshalledObject marshalledObject = (MarshalledObject)arrayOfObject[2];
      String[] arrayOfString = (String[])arrayOfObject[3];
      Subject subject = (Subject)arrayOfObject[4];
      Object object = ((RMIConnection)servantObject.servant).invoke(objectName, str, marshalledObject, arrayOfString, subject);
      return Util.copyObject(object, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)throwable2; 
      if (throwable2 instanceof MBeanException)
        throw (MBeanException)throwable2; 
      if (throwable2 instanceof ReflectionException)
        throw (ReflectionException)throwable2; 
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public boolean isInstanceOf(ObjectName paramObjectName, String paramString, Subject paramSubject) throws InstanceNotFoundException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("isInstanceOf", true);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(paramString, String.class);
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return inputStream.read_boolean();
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return isInstanceOf(paramObjectName, paramString, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("isInstanceOf", RMIConnection.class);
    if (servantObject == null)
      return isInstanceOf(paramObjectName, paramString, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName, paramString, paramSubject }, _orb());
      ObjectName objectName = (ObjectName)arrayOfObject[0];
      String str = (String)arrayOfObject[1];
      Subject subject = (Subject)arrayOfObject[2];
      return ((RMIConnection)servantObject.servant).isInstanceOf(objectName, str, subject);
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)throwable2; 
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public boolean isRegistered(ObjectName paramObjectName, Subject paramSubject) throws IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("isRegistered", true);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return inputStream.read_boolean();
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return isRegistered(paramObjectName, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("isRegistered", RMIConnection.class);
    if (servantObject == null)
      return isRegistered(paramObjectName, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName, paramSubject }, _orb());
      ObjectName objectName = (ObjectName)arrayOfObject[0];
      Subject subject = (Subject)arrayOfObject[1];
      return ((RMIConnection)servantObject.servant).isRegistered(objectName, subject);
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public Set queryMBeans(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("queryMBeans", true);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(paramMarshalledObject, MarshalledObject.class);
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return (Set)inputStream.read_value(Set.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return queryMBeans(paramObjectName, paramMarshalledObject, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("queryMBeans", RMIConnection.class);
    if (servantObject == null)
      return queryMBeans(paramObjectName, paramMarshalledObject, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName, paramMarshalledObject, paramSubject }, _orb());
      ObjectName objectName = (ObjectName)arrayOfObject[0];
      MarshalledObject marshalledObject = (MarshalledObject)arrayOfObject[1];
      Subject subject = (Subject)arrayOfObject[2];
      Set set = ((RMIConnection)servantObject.servant).queryMBeans(objectName, marshalledObject, subject);
      return (Set)Util.copyObject(set, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public Set queryNames(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("queryNames", true);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(paramMarshalledObject, MarshalledObject.class);
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return (Set)inputStream.read_value(Set.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return queryNames(paramObjectName, paramMarshalledObject, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("queryNames", RMIConnection.class);
    if (servantObject == null)
      return queryNames(paramObjectName, paramMarshalledObject, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName, paramMarshalledObject, paramSubject }, _orb());
      ObjectName objectName = (ObjectName)arrayOfObject[0];
      MarshalledObject marshalledObject = (MarshalledObject)arrayOfObject[1];
      Subject subject = (Subject)arrayOfObject[2];
      Set set = ((RMIConnection)servantObject.servant).queryNames(objectName, marshalledObject, subject);
      return (Set)Util.copyObject(set, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    checkPermission();
    paramObjectInputStream.defaultReadObject();
    this._instantiated = true;
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, MarshalledObject paramMarshalledObject1, MarshalledObject paramMarshalledObject2, Subject paramSubject) throws InstanceNotFoundException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this)) {
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__java_rmi_MarshalledObject__javax_security_auth_Subject", true);
          outputStream.write_value(paramObjectName1, ObjectName.class);
          outputStream.write_value(paramObjectName2, ObjectName.class);
          outputStream.write_value(paramMarshalledObject1, MarshalledObject.class);
          outputStream.write_value(paramMarshalledObject2, MarshalledObject.class);
          outputStream.write_value(paramSubject, Subject.class);
          _invoke(outputStream);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:javax/management/ListenerNotFoundEx:1.0"))
            throw (ListenerNotFoundException)inputStream.read_value(ListenerNotFoundException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          removeNotificationListener(paramObjectName1, paramObjectName2, paramMarshalledObject1, paramMarshalledObject2, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      } 
    } else {
      ServantObject servantObject = _servant_preinvoke("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__java_rmi_MarshalledObject__javax_security_auth_Subject", RMIConnection.class);
      if (servantObject == null) {
        removeNotificationListener(paramObjectName1, paramObjectName2, paramMarshalledObject1, paramMarshalledObject2, paramSubject);
        return;
      } 
      try {
        Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName1, paramObjectName2, paramMarshalledObject1, paramMarshalledObject2, paramSubject }, _orb());
        ObjectName objectName1 = (ObjectName)arrayOfObject[0];
        ObjectName objectName2 = (ObjectName)arrayOfObject[1];
        MarshalledObject marshalledObject1 = (MarshalledObject)arrayOfObject[2];
        MarshalledObject marshalledObject2 = (MarshalledObject)arrayOfObject[3];
        Subject subject = (Subject)arrayOfObject[4];
        ((RMIConnection)servantObject.servant).removeNotificationListener(objectName1, objectName2, marshalledObject1, marshalledObject2, subject);
      } catch (Throwable throwable1) {
        Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
        if (throwable2 instanceof InstanceNotFoundException)
          throw (InstanceNotFoundException)throwable2; 
        if (throwable2 instanceof ListenerNotFoundException)
          throw (ListenerNotFoundException)throwable2; 
        if (throwable2 instanceof IOException)
          throw (IOException)throwable2; 
        throw Util.wrapException(throwable2);
      } finally {
        _servant_postinvoke(servantObject);
      } 
    } 
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, Subject paramSubject) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this)) {
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject", true);
          outputStream.write_value(paramObjectName1, ObjectName.class);
          outputStream.write_value(paramObjectName2, ObjectName.class);
          outputStream.write_value(paramSubject, Subject.class);
          _invoke(outputStream);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:javax/management/ListenerNotFoundEx:1.0"))
            throw (ListenerNotFoundException)inputStream.read_value(ListenerNotFoundException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          removeNotificationListener(paramObjectName1, paramObjectName2, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      } 
    } else {
      ServantObject servantObject = _servant_preinvoke("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject", RMIConnection.class);
      if (servantObject == null) {
        removeNotificationListener(paramObjectName1, paramObjectName2, paramSubject);
        return;
      } 
      try {
        Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName1, paramObjectName2, paramSubject }, _orb());
        ObjectName objectName1 = (ObjectName)arrayOfObject[0];
        ObjectName objectName2 = (ObjectName)arrayOfObject[1];
        Subject subject = (Subject)arrayOfObject[2];
        ((RMIConnection)servantObject.servant).removeNotificationListener(objectName1, objectName2, subject);
      } catch (Throwable throwable1) {
        Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
        if (throwable2 instanceof InstanceNotFoundException)
          throw (InstanceNotFoundException)throwable2; 
        if (throwable2 instanceof ListenerNotFoundException)
          throw (ListenerNotFoundException)throwable2; 
        if (throwable2 instanceof IOException)
          throw (IOException)throwable2; 
        throw Util.wrapException(throwable2);
      } finally {
        _servant_postinvoke(servantObject);
      } 
    } 
  }
  
  public void removeNotificationListeners(ObjectName paramObjectName, Integer[] paramArrayOfInteger, Subject paramSubject) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this)) {
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("removeNotificationListeners", true);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(cast_array(paramArrayOfInteger), (array$Ljava$lang$Integer != null) ? array$Ljava$lang$Integer : (array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")));
          outputStream.write_value(paramSubject, Subject.class);
          _invoke(outputStream);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:javax/management/ListenerNotFoundEx:1.0"))
            throw (ListenerNotFoundException)inputStream.read_value(ListenerNotFoundException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          removeNotificationListeners(paramObjectName, paramArrayOfInteger, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      } 
    } else {
      ServantObject servantObject = _servant_preinvoke("removeNotificationListeners", RMIConnection.class);
      if (servantObject == null) {
        removeNotificationListeners(paramObjectName, paramArrayOfInteger, paramSubject);
        return;
      } 
      try {
        Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName, paramArrayOfInteger, paramSubject }, _orb());
        ObjectName objectName = (ObjectName)arrayOfObject[0];
        Integer[] arrayOfInteger = (Integer[])arrayOfObject[1];
        Subject subject = (Subject)arrayOfObject[2];
        ((RMIConnection)servantObject.servant).removeNotificationListeners(objectName, arrayOfInteger, subject);
      } catch (Throwable throwable1) {
        Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
        if (throwable2 instanceof InstanceNotFoundException)
          throw (InstanceNotFoundException)throwable2; 
        if (throwable2 instanceof ListenerNotFoundException)
          throw (ListenerNotFoundException)throwable2; 
        if (throwable2 instanceof IOException)
          throw (IOException)throwable2; 
        throw Util.wrapException(throwable2);
      } finally {
        _servant_postinvoke(servantObject);
      } 
    } 
  }
  
  public void setAttribute(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this)) {
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("setAttribute", true);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(paramMarshalledObject, MarshalledObject.class);
          outputStream.write_value(paramSubject, Subject.class);
          _invoke(outputStream);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:javax/management/AttributeNotFoundEx:1.0"))
            throw (AttributeNotFoundException)inputStream.read_value(AttributeNotFoundException.class); 
          if (str.equals("IDL:javax/management/InvalidAttributeValueEx:1.0"))
            throw (InvalidAttributeValueException)inputStream.read_value(InvalidAttributeValueException.class); 
          if (str.equals("IDL:javax/management/MBeanEx:1.0"))
            throw (MBeanException)inputStream.read_value(MBeanException.class); 
          if (str.equals("IDL:javax/management/ReflectionEx:1.0"))
            throw (ReflectionException)inputStream.read_value(ReflectionException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          setAttribute(paramObjectName, paramMarshalledObject, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      } 
    } else {
      ServantObject servantObject = _servant_preinvoke("setAttribute", RMIConnection.class);
      if (servantObject == null) {
        setAttribute(paramObjectName, paramMarshalledObject, paramSubject);
        return;
      } 
      try {
        Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName, paramMarshalledObject, paramSubject }, _orb());
        ObjectName objectName = (ObjectName)arrayOfObject[0];
        MarshalledObject marshalledObject = (MarshalledObject)arrayOfObject[1];
        Subject subject = (Subject)arrayOfObject[2];
        ((RMIConnection)servantObject.servant).setAttribute(objectName, marshalledObject, subject);
      } catch (Throwable throwable1) {
        Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
        if (throwable2 instanceof InstanceNotFoundException)
          throw (InstanceNotFoundException)throwable2; 
        if (throwable2 instanceof AttributeNotFoundException)
          throw (AttributeNotFoundException)throwable2; 
        if (throwable2 instanceof InvalidAttributeValueException)
          throw (InvalidAttributeValueException)throwable2; 
        if (throwable2 instanceof MBeanException)
          throw (MBeanException)throwable2; 
        if (throwable2 instanceof ReflectionException)
          throw (ReflectionException)throwable2; 
        if (throwable2 instanceof IOException)
          throw (IOException)throwable2; 
        throw Util.wrapException(throwable2);
      } finally {
        _servant_postinvoke(servantObject);
      } 
    } 
  }
  
  public AttributeList setAttributes(ObjectName paramObjectName, MarshalledObject paramMarshalledObject, Subject paramSubject) throws InstanceNotFoundException, ReflectionException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this))
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("setAttributes", true);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(paramMarshalledObject, MarshalledObject.class);
          outputStream.write_value(paramSubject, Subject.class);
          inputStream = (InputStream)_invoke(outputStream);
          return (AttributeList)inputStream.read_value(AttributeList.class);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:javax/management/ReflectionEx:1.0"))
            throw (ReflectionException)inputStream.read_value(ReflectionException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          return setAttributes(paramObjectName, paramMarshalledObject, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ServantObject servantObject = _servant_preinvoke("setAttributes", RMIConnection.class);
    if (servantObject == null)
      return setAttributes(paramObjectName, paramMarshalledObject, paramSubject); 
    try {
      Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName, paramMarshalledObject, paramSubject }, _orb());
      ObjectName objectName = (ObjectName)arrayOfObject[0];
      MarshalledObject marshalledObject = (MarshalledObject)arrayOfObject[1];
      Subject subject = (Subject)arrayOfObject[2];
      AttributeList attributeList = ((RMIConnection)servantObject.servant).setAttributes(objectName, marshalledObject, subject);
      return (AttributeList)Util.copyObject(attributeList, _orb());
    } catch (Throwable throwable1) {
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
      if (throwable2 instanceof InstanceNotFoundException)
        throw (InstanceNotFoundException)throwable2; 
      if (throwable2 instanceof ReflectionException)
        throw (ReflectionException)throwable2; 
      if (throwable2 instanceof IOException)
        throw (IOException)throwable2; 
      throw Util.wrapException(throwable2);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public void unregisterMBean(ObjectName paramObjectName, Subject paramSubject) throws InstanceNotFoundException, MBeanRegistrationException, IOException {
    if (System.getSecurityManager() != null && !this._instantiated)
      throw new IOError(new IOException("InvalidObject ")); 
    if (!Util.isLocal(this)) {
      try {
        InputStream inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)_request("unregisterMBean", true);
          outputStream.write_value(paramObjectName, ObjectName.class);
          outputStream.write_value(paramSubject, Subject.class);
          _invoke(outputStream);
        } catch (ApplicationException applicationException) {
          inputStream = (InputStream)applicationException.getInputStream();
          String str = inputStream.read_string();
          if (str.equals("IDL:javax/management/InstanceNotFoundEx:1.0"))
            throw (InstanceNotFoundException)inputStream.read_value(InstanceNotFoundException.class); 
          if (str.equals("IDL:javax/management/MBeanRegistrationEx:1.0"))
            throw (MBeanRegistrationException)inputStream.read_value(MBeanRegistrationException.class); 
          if (str.equals("IDL:java/io/IOEx:1.0"))
            throw (IOException)inputStream.read_value(IOException.class); 
          throw new UnexpectedException(str);
        } catch (RemarshalException remarshalException) {
          unregisterMBean(paramObjectName, paramSubject);
        } finally {
          _releaseReply(inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      } 
    } else {
      ServantObject servantObject = _servant_preinvoke("unregisterMBean", RMIConnection.class);
      if (servantObject == null) {
        unregisterMBean(paramObjectName, paramSubject);
        return;
      } 
      try {
        Object[] arrayOfObject = Util.copyObjects(new Object[] { paramObjectName, paramSubject }, _orb());
        ObjectName objectName = (ObjectName)arrayOfObject[0];
        Subject subject = (Subject)arrayOfObject[1];
        ((RMIConnection)servantObject.servant).unregisterMBean(objectName, subject);
      } catch (Throwable throwable1) {
        Throwable throwable2 = (Throwable)Util.copyObject(throwable1, _orb());
        if (throwable2 instanceof InstanceNotFoundException)
          throw (InstanceNotFoundException)throwable2; 
        if (throwable2 instanceof MBeanRegistrationException)
          throw (MBeanRegistrationException)throwable2; 
        if (throwable2 instanceof IOException)
          throw (IOException)throwable2; 
        throw Util.wrapException(throwable2);
      } finally {
        _servant_postinvoke(servantObject);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\_RMIConnection_Stub.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.0.7
 */