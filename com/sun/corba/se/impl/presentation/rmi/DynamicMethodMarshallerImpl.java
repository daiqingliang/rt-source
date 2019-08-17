package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class DynamicMethodMarshallerImpl implements DynamicMethodMarshaller {
  Method method;
  
  ExceptionHandler ehandler;
  
  boolean hasArguments = true;
  
  boolean hasVoidResult = true;
  
  boolean needsArgumentCopy;
  
  boolean needsResultCopy;
  
  ReaderWriter[] argRWs = null;
  
  ReaderWriter resultRW = null;
  
  private static ReaderWriter booleanRW = new ReaderWriterBase("boolean") {
      public Object read(InputStream param1InputStream) {
        boolean bool = param1InputStream.read_boolean();
        return new Boolean(bool);
      }
      
      public void write(OutputStream param1OutputStream, Object param1Object) {
        Boolean bool = (Boolean)param1Object;
        param1OutputStream.write_boolean(bool.booleanValue());
      }
    };
  
  private static ReaderWriter byteRW = new ReaderWriterBase("byte") {
      public Object read(InputStream param1InputStream) {
        byte b = param1InputStream.read_octet();
        return new Byte(b);
      }
      
      public void write(OutputStream param1OutputStream, Object param1Object) {
        Byte byte = (Byte)param1Object;
        param1OutputStream.write_octet(byte.byteValue());
      }
    };
  
  private static ReaderWriter charRW = new ReaderWriterBase("char") {
      public Object read(InputStream param1InputStream) {
        char c = param1InputStream.read_wchar();
        return new Character(c);
      }
      
      public void write(OutputStream param1OutputStream, Object param1Object) {
        Character character = (Character)param1Object;
        param1OutputStream.write_wchar(character.charValue());
      }
    };
  
  private static ReaderWriter shortRW = new ReaderWriterBase("short") {
      public Object read(InputStream param1InputStream) {
        short s = param1InputStream.read_short();
        return new Short(s);
      }
      
      public void write(OutputStream param1OutputStream, Object param1Object) {
        Short short = (Short)param1Object;
        param1OutputStream.write_short(short.shortValue());
      }
    };
  
  private static ReaderWriter intRW = new ReaderWriterBase("int") {
      public Object read(InputStream param1InputStream) {
        int i = param1InputStream.read_long();
        return new Integer(i);
      }
      
      public void write(OutputStream param1OutputStream, Object param1Object) {
        Integer integer = (Integer)param1Object;
        param1OutputStream.write_long(integer.intValue());
      }
    };
  
  private static ReaderWriter longRW = new ReaderWriterBase("long") {
      public Object read(InputStream param1InputStream) {
        long l = param1InputStream.read_longlong();
        return new Long(l);
      }
      
      public void write(OutputStream param1OutputStream, Object param1Object) {
        Long long = (Long)param1Object;
        param1OutputStream.write_longlong(long.longValue());
      }
    };
  
  private static ReaderWriter floatRW = new ReaderWriterBase("float") {
      public Object read(InputStream param1InputStream) {
        float f = param1InputStream.read_float();
        return new Float(f);
      }
      
      public void write(OutputStream param1OutputStream, Object param1Object) {
        Float float = (Float)param1Object;
        param1OutputStream.write_float(float.floatValue());
      }
    };
  
  private static ReaderWriter doubleRW = new ReaderWriterBase("double") {
      public Object read(InputStream param1InputStream) {
        double d = param1InputStream.read_double();
        return new Double(d);
      }
      
      public void write(OutputStream param1OutputStream, Object param1Object) {
        Double double = (Double)param1Object;
        param1OutputStream.write_double(double.doubleValue());
      }
    };
  
  private static ReaderWriter corbaObjectRW = new ReaderWriterBase("org.omg.CORBA.Object") {
      public Object read(InputStream param1InputStream) { return param1InputStream.read_Object(); }
      
      public void write(OutputStream param1OutputStream, Object param1Object) { param1OutputStream.write_Object((Object)param1Object); }
    };
  
  private static ReaderWriter anyRW = new ReaderWriterBase("any") {
      public Object read(InputStream param1InputStream) { return Util.readAny(param1InputStream); }
      
      public void write(OutputStream param1OutputStream, Object param1Object) { Util.writeAny(param1OutputStream, param1Object); }
    };
  
  private static ReaderWriter abstractInterfaceRW = new ReaderWriterBase("abstract_interface") {
      public Object read(InputStream param1InputStream) { return param1InputStream.read_abstract_interface(); }
      
      public void write(OutputStream param1OutputStream, Object param1Object) { Util.writeAbstractObject(param1OutputStream, param1Object); }
    };
  
  private static boolean isAnyClass(Class paramClass) { return (paramClass.equals(Object.class) || paramClass.equals(Serializable.class) || paramClass.equals(java.io.Externalizable.class)); }
  
  private static boolean isAbstractInterface(Class paramClass) { return org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(paramClass) ? paramClass.isInterface() : ((paramClass.isInterface() && allMethodsThrowRemoteException(paramClass)) ? 1 : 0); }
  
  private static boolean allMethodsThrowRemoteException(Class paramClass) {
    Method[] arrayOfMethod = paramClass.getMethods();
    for (byte b = 0; b < arrayOfMethod.length; b++) {
      Method method1 = arrayOfMethod[b];
      if (method1.getDeclaringClass() != Object.class && !throwsRemote(method1))
        return false; 
    } 
    return true;
  }
  
  private static boolean throwsRemote(Method paramMethod) {
    Class[] arrayOfClass = paramMethod.getExceptionTypes();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      Class clazz = arrayOfClass[b];
      if (RemoteException.class.isAssignableFrom(clazz))
        return true; 
    } 
    return false;
  }
  
  public static ReaderWriter makeReaderWriter(final Class cls) { return paramClass.equals(boolean.class) ? booleanRW : (paramClass.equals(byte.class) ? byteRW : (paramClass.equals(char.class) ? charRW : (paramClass.equals(short.class) ? shortRW : (paramClass.equals(int.class) ? intRW : (paramClass.equals(long.class) ? longRW : (paramClass.equals(float.class) ? floatRW : (paramClass.equals(double.class) ? doubleRW : (java.rmi.Remote.class.isAssignableFrom(paramClass) ? new ReaderWriterBase("remote(" + paramClass.getName() + ")") {
        public Object read(InputStream param1InputStream) { return PortableRemoteObject.narrow(param1InputStream.read_Object(), cls); }
        
        public void write(OutputStream param1OutputStream, Object param1Object) { Util.writeRemoteObject(param1OutputStream, param1Object); }
      } : (paramClass.equals(Object.class) ? corbaObjectRW : (Object.class.isAssignableFrom(paramClass) ? new ReaderWriterBase("org.omg.CORBA.Object(" + paramClass.getName() + ")") {
        public Object read(InputStream param1InputStream) { return param1InputStream.read_Object(cls); }
        
        public void write(OutputStream param1OutputStream, Object param1Object) { param1OutputStream.write_Object((Object)param1Object); }
      } : (isAnyClass(paramClass) ? anyRW : (isAbstractInterface(paramClass) ? abstractInterfaceRW : new ReaderWriterBase("value(" + paramClass.getName() + ")") {
        public Object read(InputStream param1InputStream) { return param1InputStream.read_value(cls); }
        
        public void write(OutputStream param1OutputStream, Object param1Object) { param1OutputStream.write_value((Serializable)param1Object, cls); }
      })))))))))))); }
  
  public DynamicMethodMarshallerImpl(Method paramMethod) {
    this.method = paramMethod;
    this.ehandler = new ExceptionHandlerImpl(paramMethod.getExceptionTypes());
    this.needsArgumentCopy = false;
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    this.hasArguments = (arrayOfClass.length > 0);
    if (this.hasArguments) {
      this.argRWs = new ReaderWriter[arrayOfClass.length];
      for (byte b = 0; b < arrayOfClass.length; b++) {
        if (!arrayOfClass[b].isPrimitive())
          this.needsArgumentCopy = true; 
        this.argRWs[b] = makeReaderWriter(arrayOfClass[b]);
      } 
    } 
    Class clazz = paramMethod.getReturnType();
    this.needsResultCopy = false;
    this.hasVoidResult = clazz.equals(void.class);
    if (!this.hasVoidResult) {
      this.needsResultCopy = !clazz.isPrimitive();
      this.resultRW = makeReaderWriter(clazz);
    } 
  }
  
  public Method getMethod() { return this.method; }
  
  public Object[] copyArguments(Object[] paramArrayOfObject, ORB paramORB) throws RemoteException { return this.needsArgumentCopy ? Util.copyObjects(paramArrayOfObject, paramORB) : paramArrayOfObject; }
  
  public Object[] readArguments(InputStream paramInputStream) {
    Object[] arrayOfObject = null;
    if (this.hasArguments) {
      arrayOfObject = new Object[this.argRWs.length];
      for (byte b = 0; b < this.argRWs.length; b++)
        arrayOfObject[b] = this.argRWs[b].read(paramInputStream); 
    } 
    return arrayOfObject;
  }
  
  public void writeArguments(OutputStream paramOutputStream, Object[] paramArrayOfObject) {
    if (this.hasArguments) {
      if (paramArrayOfObject.length != this.argRWs.length)
        throw new IllegalArgumentException("Expected " + this.argRWs.length + " arguments, but got " + paramArrayOfObject.length + " arguments."); 
      for (byte b = 0; b < this.argRWs.length; b++)
        this.argRWs[b].write(paramOutputStream, paramArrayOfObject[b]); 
    } 
  }
  
  public Object copyResult(Object paramObject, ORB paramORB) throws RemoteException { return this.needsResultCopy ? Util.copyObject(paramObject, paramORB) : paramObject; }
  
  public Object readResult(InputStream paramInputStream) { return this.hasVoidResult ? null : this.resultRW.read(paramInputStream); }
  
  public void writeResult(OutputStream paramOutputStream, Object paramObject) {
    if (!this.hasVoidResult)
      this.resultRW.write(paramOutputStream, paramObject); 
  }
  
  public boolean isDeclaredException(Throwable paramThrowable) { return this.ehandler.isDeclaredException(paramThrowable.getClass()); }
  
  public void writeException(OutputStream paramOutputStream, Exception paramException) { this.ehandler.writeException(paramOutputStream, paramException); }
  
  public Exception readException(ApplicationException paramApplicationException) { return this.ehandler.readException(paramApplicationException); }
  
  public static interface ReaderWriter {
    Object read(InputStream param1InputStream);
    
    void write(OutputStream param1OutputStream, Object param1Object);
  }
  
  static abstract class ReaderWriterBase implements ReaderWriter {
    String name;
    
    public ReaderWriterBase(String param1String) { this.name = param1String; }
    
    public String toString() { return "ReaderWriter[" + this.name + "]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\DynamicMethodMarshallerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */