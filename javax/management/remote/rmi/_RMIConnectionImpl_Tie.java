package javax.management.remote.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
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
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.security.auth.Subject;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.ObjectImpl;
import org.omg.CORBA_2_3.portable.OutputStream;

public class _RMIConnectionImpl_Tie extends ObjectImpl implements Tie {
  private static final String[] _type_ids = { "RMI:javax.management.remote.rmi.RMIConnection:0000000000000000" };
  
  static Class array$Ljava$lang$String;
  
  static Class array$Ljavax$management$ObjectName;
  
  static Class array$Ljava$rmi$MarshalledObject;
  
  static Class array$Ljavax$security$auth$Subject;
  
  static Class array$Ljava$lang$Integer;
  
  public String[] _ids() { return (String[])_type_ids.clone(); }
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler) throws SystemException {
    try {
      RMIConnectionImpl rMIConnectionImpl = this.target;
      if (rMIConnectionImpl == null)
        throw new IOException(); 
      InputStream inputStream = (InputStream)paramInputStream;
      switch (paramString.charAt(3)) {
        case 'A':
          if (paramString.equals("getAttribute")) {
            Object object;
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            String str = (String)inputStream.read_value(String.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              object = rMIConnectionImpl.getAttribute(objectName, str, subject);
            } catch (MBeanException mBeanException) {
              String str1 = "IDL:javax/management/MBeanEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(mBeanException, MBeanException.class);
              return outputStream1;
            } catch (AttributeNotFoundException attributeNotFoundException) {
              String str1 = "IDL:javax/management/AttributeNotFoundEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(attributeNotFoundException, AttributeNotFoundException.class);
              return outputStream1;
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str1 = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream1;
            } catch (ReflectionException reflectionException) {
              String str1 = "IDL:javax/management/ReflectionEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(reflectionException, ReflectionException.class);
              return outputStream1;
            } catch (IOException iOException) {
              String str1 = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = paramResponseHandler.createReply();
            Util.writeAny(outputStream, object);
            return outputStream;
          } 
          if (paramString.equals("getAttributes")) {
            AttributeList attributeList;
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            String[] arrayOfString = (String[])inputStream.read_value((array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              attributeList = rMIConnectionImpl.getAttributes(objectName, arrayOfString, subject);
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream1;
            } catch (ReflectionException reflectionException) {
              String str = "IDL:javax/management/ReflectionEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(reflectionException, ReflectionException.class);
              return outputStream1;
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(attributeList, AttributeList.class);
            return outputStream;
          } 
          if (paramString.equals("setAttribute")) {
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            MarshalledObject marshalledObject = (MarshalledObject)inputStream.read_value(MarshalledObject.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              rMIConnectionImpl.setAttribute(objectName, marshalledObject, subject);
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream;
            } catch (AttributeNotFoundException attributeNotFoundException) {
              String str = "IDL:javax/management/AttributeNotFoundEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(attributeNotFoundException, AttributeNotFoundException.class);
              return outputStream;
            } catch (InvalidAttributeValueException invalidAttributeValueException) {
              String str = "IDL:javax/management/InvalidAttributeValueEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(invalidAttributeValueException, InvalidAttributeValueException.class);
              return outputStream;
            } catch (MBeanException mBeanException) {
              String str = "IDL:javax/management/MBeanEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(mBeanException, MBeanException.class);
              return outputStream;
            } catch (ReflectionException reflectionException) {
              String str = "IDL:javax/management/ReflectionEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(reflectionException, ReflectionException.class);
              return outputStream;
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(iOException, IOException.class);
              return outputStream;
            } 
            return paramResponseHandler.createReply();
          } 
          if (paramString.equals("setAttributes")) {
            AttributeList attributeList;
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            MarshalledObject marshalledObject = (MarshalledObject)inputStream.read_value(MarshalledObject.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              attributeList = rMIConnectionImpl.setAttributes(objectName, marshalledObject, subject);
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream1;
            } catch (ReflectionException reflectionException) {
              String str = "IDL:javax/management/ReflectionEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(reflectionException, ReflectionException.class);
              return outputStream1;
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(attributeList, AttributeList.class);
            return outputStream;
          } 
        case 'C':
          if (paramString.equals("getConnectionId")) {
            String str;
            try {
              str = rMIConnectionImpl.getConnectionId();
            } catch (IOException iOException) {
              String str1 = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(str, String.class);
            return outputStream;
          } 
        case 'D':
          if (paramString.equals("getDefaultDomain")) {
            String str;
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              str = rMIConnectionImpl.getDefaultDomain(subject);
            } catch (IOException iOException) {
              String str1 = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(str, String.class);
            return outputStream;
          } 
          if (paramString.equals("getDomains")) {
            String[] arrayOfString;
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              arrayOfString = rMIConnectionImpl.getDomains(subject);
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(cast_array(arrayOfString), (array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
            return outputStream;
          } 
        case 'M':
          if (paramString.equals("getMBeanCount")) {
            Integer integer;
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              integer = rMIConnectionImpl.getMBeanCount(subject);
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(integer, Integer.class);
            return outputStream;
          } 
          if (paramString.equals("getMBeanInfo")) {
            MBeanInfo mBeanInfo;
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              mBeanInfo = rMIConnectionImpl.getMBeanInfo(objectName, subject);
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream1;
            } catch (IntrospectionException introspectionException) {
              String str = "IDL:javax/management/IntrospectionEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(introspectionException, IntrospectionException.class);
              return outputStream1;
            } catch (ReflectionException reflectionException) {
              String str = "IDL:javax/management/ReflectionEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(reflectionException, ReflectionException.class);
              return outputStream1;
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(mBeanInfo, MBeanInfo.class);
            return outputStream;
          } 
        case 'N':
          if (paramString.equals("addNotificationListener")) {
            ObjectName objectName1 = (ObjectName)inputStream.read_value(ObjectName.class);
            ObjectName objectName2 = (ObjectName)inputStream.read_value(ObjectName.class);
            MarshalledObject marshalledObject1 = (MarshalledObject)inputStream.read_value(MarshalledObject.class);
            MarshalledObject marshalledObject2 = (MarshalledObject)inputStream.read_value(MarshalledObject.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              rMIConnectionImpl.addNotificationListener(objectName1, objectName2, marshalledObject1, marshalledObject2, subject);
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream;
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(iOException, IOException.class);
              return outputStream;
            } 
            return paramResponseHandler.createReply();
          } 
          if (paramString.equals("addNotificationListeners")) {
            Integer[] arrayOfInteger;
            ObjectName[] arrayOfObjectName = (ObjectName[])inputStream.read_value((array$Ljavax$management$ObjectName != null) ? array$Ljavax$management$ObjectName : (array$Ljavax$management$ObjectName = class$("[Ljavax.management.ObjectName;")));
            MarshalledObject[] arrayOfMarshalledObject = (MarshalledObject[])inputStream.read_value((array$Ljava$rmi$MarshalledObject != null) ? array$Ljava$rmi$MarshalledObject : (array$Ljava$rmi$MarshalledObject = class$("[Ljava.rmi.MarshalledObject;")));
            Subject[] arrayOfSubject = (Subject[])inputStream.read_value((array$Ljavax$security$auth$Subject != null) ? array$Ljavax$security$auth$Subject : (array$Ljavax$security$auth$Subject = class$("[Ljavax.security.auth.Subject;")));
            try {
              arrayOfInteger = rMIConnectionImpl.addNotificationListeners(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject);
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream1;
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(cast_array(arrayOfInteger), (array$Ljava$lang$Integer != null) ? array$Ljava$lang$Integer : (array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")));
            return outputStream;
          } 
        case 'O':
          if (paramString.equals("getObjectInstance")) {
            ObjectInstance objectInstance;
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              objectInstance = rMIConnectionImpl.getObjectInstance(objectName, subject);
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream1;
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(objectInstance, ObjectInstance.class);
            return outputStream;
          } 
        case 'a':
          if (paramString.equals("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_security_auth_Subject")) {
            ObjectInstance objectInstance;
            String str = (String)inputStream.read_value(String.class);
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              objectInstance = rMIConnectionImpl.createMBean(str, objectName, subject);
            } catch (ReflectionException reflectionException) {
              String str1 = "IDL:javax/management/ReflectionEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(reflectionException, ReflectionException.class);
              return outputStream1;
            } catch (InstanceAlreadyExistsException instanceAlreadyExistsException) {
              String str1 = "IDL:javax/management/InstanceAlreadyExistsEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(instanceAlreadyExistsException, InstanceAlreadyExistsException.class);
              return outputStream1;
            } catch (MBeanException mBeanException) {
              String str1 = "IDL:javax/management/MBeanEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(mBeanException, MBeanException.class);
              return outputStream1;
            } catch (NotCompliantMBeanException notCompliantMBeanException) {
              String str1 = "IDL:javax/management/NotCompliantMBeanEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(notCompliantMBeanException, NotCompliantMBeanException.class);
              return outputStream1;
            } catch (IOException iOException) {
              String str1 = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(objectInstance, ObjectInstance.class);
            return outputStream;
          } 
          if (paramString.equals("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject")) {
            ObjectInstance objectInstance;
            String str = (String)inputStream.read_value(String.class);
            ObjectName objectName1 = (ObjectName)inputStream.read_value(ObjectName.class);
            ObjectName objectName2 = (ObjectName)inputStream.read_value(ObjectName.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              objectInstance = rMIConnectionImpl.createMBean(str, objectName1, objectName2, subject);
            } catch (ReflectionException reflectionException) {
              String str1 = "IDL:javax/management/ReflectionEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(reflectionException, ReflectionException.class);
              return outputStream1;
            } catch (InstanceAlreadyExistsException instanceAlreadyExistsException) {
              String str1 = "IDL:javax/management/InstanceAlreadyExistsEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(instanceAlreadyExistsException, InstanceAlreadyExistsException.class);
              return outputStream1;
            } catch (MBeanException mBeanException) {
              String str1 = "IDL:javax/management/MBeanEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(mBeanException, MBeanException.class);
              return outputStream1;
            } catch (NotCompliantMBeanException notCompliantMBeanException) {
              String str1 = "IDL:javax/management/NotCompliantMBeanEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(notCompliantMBeanException, NotCompliantMBeanException.class);
              return outputStream1;
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str1 = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream1;
            } catch (IOException iOException) {
              String str1 = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(objectInstance, ObjectInstance.class);
            return outputStream;
          } 
          if (paramString.equals("createMBean__CORBA_WStringValue__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject")) {
            ObjectInstance objectInstance;
            String str = (String)inputStream.read_value(String.class);
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            MarshalledObject marshalledObject = (MarshalledObject)inputStream.read_value(MarshalledObject.class);
            String[] arrayOfString = (String[])inputStream.read_value((array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              objectInstance = rMIConnectionImpl.createMBean(str, objectName, marshalledObject, arrayOfString, subject);
            } catch (ReflectionException reflectionException) {
              String str1 = "IDL:javax/management/ReflectionEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(reflectionException, ReflectionException.class);
              return outputStream1;
            } catch (InstanceAlreadyExistsException instanceAlreadyExistsException) {
              String str1 = "IDL:javax/management/InstanceAlreadyExistsEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(instanceAlreadyExistsException, InstanceAlreadyExistsException.class);
              return outputStream1;
            } catch (MBeanException mBeanException) {
              String str1 = "IDL:javax/management/MBeanEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(mBeanException, MBeanException.class);
              return outputStream1;
            } catch (NotCompliantMBeanException notCompliantMBeanException) {
              String str1 = "IDL:javax/management/NotCompliantMBeanEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(notCompliantMBeanException, NotCompliantMBeanException.class);
              return outputStream1;
            } catch (IOException iOException) {
              String str1 = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(objectInstance, ObjectInstance.class);
            return outputStream;
          } 
          if (paramString.equals("createMBean__CORBA_WStringValue__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__org_omg_boxedRMI_CORBA_seq1_WStringValue__javax_security_auth_Subject")) {
            ObjectInstance objectInstance;
            String str = (String)inputStream.read_value(String.class);
            ObjectName objectName1 = (ObjectName)inputStream.read_value(ObjectName.class);
            ObjectName objectName2 = (ObjectName)inputStream.read_value(ObjectName.class);
            MarshalledObject marshalledObject = (MarshalledObject)inputStream.read_value(MarshalledObject.class);
            String[] arrayOfString = (String[])inputStream.read_value((array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              objectInstance = rMIConnectionImpl.createMBean(str, objectName1, objectName2, marshalledObject, arrayOfString, subject);
            } catch (ReflectionException reflectionException) {
              String str1 = "IDL:javax/management/ReflectionEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(reflectionException, ReflectionException.class);
              return outputStream1;
            } catch (InstanceAlreadyExistsException instanceAlreadyExistsException) {
              String str1 = "IDL:javax/management/InstanceAlreadyExistsEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(instanceAlreadyExistsException, InstanceAlreadyExistsException.class);
              return outputStream1;
            } catch (MBeanException mBeanException) {
              String str1 = "IDL:javax/management/MBeanEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(mBeanException, MBeanException.class);
              return outputStream1;
            } catch (NotCompliantMBeanException notCompliantMBeanException) {
              String str1 = "IDL:javax/management/NotCompliantMBeanEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(notCompliantMBeanException, NotCompliantMBeanException.class);
              return outputStream1;
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str1 = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream1;
            } catch (IOException iOException) {
              String str1 = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(objectInstance, ObjectInstance.class);
            return outputStream;
          } 
        case 'c':
          if (paramString.equals("fetchNotifications")) {
            NotificationResult notificationResult;
            long l1 = inputStream.read_longlong();
            int i = inputStream.read_long();
            long l2 = inputStream.read_longlong();
            try {
              notificationResult = rMIConnectionImpl.fetchNotifications(l1, i, l2);
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(notificationResult, NotificationResult.class);
            return outputStream;
          } 
        case 'e':
          if (paramString.equals("unregisterMBean")) {
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              rMIConnectionImpl.unregisterMBean(objectName, subject);
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream;
            } catch (MBeanRegistrationException mBeanRegistrationException) {
              String str = "IDL:javax/management/MBeanRegistrationEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(mBeanRegistrationException, MBeanRegistrationException.class);
              return outputStream;
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(iOException, IOException.class);
              return outputStream;
            } 
            return paramResponseHandler.createReply();
          } 
          if (paramString.equals("isRegistered")) {
            boolean bool;
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              bool = rMIConnectionImpl.isRegistered(objectName, subject);
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = paramResponseHandler.createReply();
            outputStream.write_boolean(bool);
            return outputStream;
          } 
        case 'n':
          if (paramString.equals("isInstanceOf")) {
            boolean bool;
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            String str = (String)inputStream.read_value(String.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              bool = rMIConnectionImpl.isInstanceOf(objectName, str, subject);
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str1 = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream1;
            } catch (IOException iOException) {
              String str1 = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = paramResponseHandler.createReply();
            outputStream.write_boolean(bool);
            return outputStream;
          } 
        case 'o':
          if (paramString.equals("invoke")) {
            Object object;
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            String str = (String)inputStream.read_value(String.class);
            MarshalledObject marshalledObject = (MarshalledObject)inputStream.read_value(MarshalledObject.class);
            String[] arrayOfString = (String[])inputStream.read_value((array$Ljava$lang$String != null) ? array$Ljava$lang$String : (array$Ljava$lang$String = class$("[Ljava.lang.String;")));
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              object = rMIConnectionImpl.invoke(objectName, str, marshalledObject, arrayOfString, subject);
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str1 = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream1;
            } catch (MBeanException mBeanException) {
              String str1 = "IDL:javax/management/MBeanEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(mBeanException, MBeanException.class);
              return outputStream1;
            } catch (ReflectionException reflectionException) {
              String str1 = "IDL:javax/management/ReflectionEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(reflectionException, ReflectionException.class);
              return outputStream1;
            } catch (IOException iOException) {
              String str1 = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str1);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = paramResponseHandler.createReply();
            Util.writeAny(outputStream, object);
            return outputStream;
          } 
          if (paramString.equals("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__javax_security_auth_Subject")) {
            ObjectName objectName1 = (ObjectName)inputStream.read_value(ObjectName.class);
            ObjectName objectName2 = (ObjectName)inputStream.read_value(ObjectName.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              rMIConnectionImpl.removeNotificationListener(objectName1, objectName2, subject);
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream;
            } catch (ListenerNotFoundException listenerNotFoundException) {
              String str = "IDL:javax/management/ListenerNotFoundEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(listenerNotFoundException, ListenerNotFoundException.class);
              return outputStream;
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(iOException, IOException.class);
              return outputStream;
            } 
            return paramResponseHandler.createReply();
          } 
          if (paramString.equals("removeNotificationListener__javax_management_ObjectName__javax_management_ObjectName__java_rmi_MarshalledObject__java_rmi_MarshalledObject__javax_security_auth_Subject")) {
            ObjectName objectName1 = (ObjectName)inputStream.read_value(ObjectName.class);
            ObjectName objectName2 = (ObjectName)inputStream.read_value(ObjectName.class);
            MarshalledObject marshalledObject1 = (MarshalledObject)inputStream.read_value(MarshalledObject.class);
            MarshalledObject marshalledObject2 = (MarshalledObject)inputStream.read_value(MarshalledObject.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              rMIConnectionImpl.removeNotificationListener(objectName1, objectName2, marshalledObject1, marshalledObject2, subject);
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream;
            } catch (ListenerNotFoundException listenerNotFoundException) {
              String str = "IDL:javax/management/ListenerNotFoundEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(listenerNotFoundException, ListenerNotFoundException.class);
              return outputStream;
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(iOException, IOException.class);
              return outputStream;
            } 
            return paramResponseHandler.createReply();
          } 
          if (paramString.equals("removeNotificationListeners")) {
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            Integer[] arrayOfInteger = (Integer[])inputStream.read_value((array$Ljava$lang$Integer != null) ? array$Ljava$lang$Integer : (array$Ljava$lang$Integer = class$("[Ljava.lang.Integer;")));
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              rMIConnectionImpl.removeNotificationListeners(objectName, arrayOfInteger, subject);
            } catch (InstanceNotFoundException instanceNotFoundException) {
              String str = "IDL:javax/management/InstanceNotFoundEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(instanceNotFoundException, InstanceNotFoundException.class);
              return outputStream;
            } catch (ListenerNotFoundException listenerNotFoundException) {
              String str = "IDL:javax/management/ListenerNotFoundEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(listenerNotFoundException, ListenerNotFoundException.class);
              return outputStream;
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(iOException, IOException.class);
              return outputStream;
            } 
            return paramResponseHandler.createReply();
          } 
        case 'r':
          if (paramString.equals("queryMBeans")) {
            Set set;
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            MarshalledObject marshalledObject = (MarshalledObject)inputStream.read_value(MarshalledObject.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              set = rMIConnectionImpl.queryMBeans(objectName, marshalledObject, subject);
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value((Serializable)set, Set.class);
            return outputStream;
          } 
          if (paramString.equals("queryNames")) {
            Set set;
            ObjectName objectName = (ObjectName)inputStream.read_value(ObjectName.class);
            MarshalledObject marshalledObject = (MarshalledObject)inputStream.read_value(MarshalledObject.class);
            Subject subject = (Subject)inputStream.read_value(Subject.class);
            try {
              set = rMIConnectionImpl.queryNames(objectName, marshalledObject, subject);
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value((Serializable)set, Set.class);
            return outputStream;
          } 
        case 's':
          if (paramString.equals("close")) {
            try {
              rMIConnectionImpl.close();
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream.write_string(str);
              outputStream.write_value(iOException, IOException.class);
              return outputStream;
            } 
            return paramResponseHandler.createReply();
          } 
          break;
      } 
      throw new BAD_OPERATION();
    } catch (SystemException systemException) {
      throw systemException;
    } catch (Throwable throwable) {
      throw new UnknownException(throwable);
    } 
  }
  
  private Serializable cast_array(Object paramObject) { return (Serializable)paramObject; }
  
  public void deactivate() {
    _orb().disconnect(this);
    _set_delegate(null);
    this.target = null;
  }
  
  public Remote getTarget() { return this.target; }
  
  public ORB orb() { return _orb(); }
  
  public void orb(ORB paramORB) { paramORB.connect(this); }
  
  public void setTarget(Remote paramRemote) { this.target = (RMIConnectionImpl)paramRemote; }
  
  public Object thisObject() { return this; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\_RMIConnectionImpl_Tie.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.0.7
 */