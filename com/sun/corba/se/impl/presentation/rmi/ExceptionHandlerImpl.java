package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.lang.reflect.Method;
import java.rmi.UnexpectedException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class ExceptionHandlerImpl implements ExceptionHandler {
  private ExceptionRW[] rws;
  
  private final ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.presentation");
  
  public ExceptionHandlerImpl(Class[] paramArrayOfClass) {
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < paramArrayOfClass.length; b2++) {
      Class clazz = paramArrayOfClass[b2];
      if (!java.rmi.RemoteException.class.isAssignableFrom(clazz))
        b1++; 
    } 
    this.rws = new ExceptionRW[b1];
    b2 = 0;
    for (byte b3 = 0; b3 < paramArrayOfClass.length; b3++) {
      Class clazz = paramArrayOfClass[b3];
      if (!java.rmi.RemoteException.class.isAssignableFrom(clazz)) {
        ExceptionRWRMIImpl exceptionRWRMIImpl = null;
        if (org.omg.CORBA.UserException.class.isAssignableFrom(clazz)) {
          exceptionRWRMIImpl = new ExceptionRWIDLImpl(clazz);
        } else {
          exceptionRWRMIImpl = new ExceptionRWRMIImpl(clazz);
        } 
        this.rws[b2++] = exceptionRWRMIImpl;
      } 
    } 
  }
  
  private int findDeclaredException(Class paramClass) {
    for (byte b = 0; b < this.rws.length; b++) {
      Class clazz = this.rws[b].getExceptionClass();
      if (clazz.isAssignableFrom(paramClass))
        return b; 
    } 
    return -1;
  }
  
  private int findDeclaredException(String paramString) {
    for (byte b = 0; b < this.rws.length; b++) {
      if (this.rws[b] == null)
        return -1; 
      String str = this.rws[b].getId();
      if (paramString.equals(str))
        return b; 
    } 
    return -1;
  }
  
  public boolean isDeclaredException(Class paramClass) { return (findDeclaredException(paramClass) >= 0); }
  
  public void writeException(OutputStream paramOutputStream, Exception paramException) {
    int i = findDeclaredException(paramException.getClass());
    if (i < 0)
      throw this.wrapper.writeUndeclaredException(paramException, paramException.getClass().getName()); 
    this.rws[i].write(paramOutputStream, paramException);
  }
  
  public Exception readException(ApplicationException paramApplicationException) {
    InputStream inputStream = (InputStream)paramApplicationException.getInputStream();
    String str = paramApplicationException.getId();
    int i = findDeclaredException(str);
    if (i < 0) {
      str = inputStream.read_string();
      UnexpectedException unexpectedException = new UnexpectedException(str);
      unexpectedException.initCause(paramApplicationException);
      return unexpectedException;
    } 
    return this.rws[i].read(inputStream);
  }
  
  public ExceptionRW getRMIExceptionRW(Class paramClass) { return new ExceptionRWRMIImpl(paramClass); }
  
  public static interface ExceptionRW {
    Class getExceptionClass();
    
    String getId();
    
    void write(OutputStream param1OutputStream, Exception param1Exception);
    
    Exception read(InputStream param1InputStream);
  }
  
  public abstract class ExceptionRWBase implements ExceptionRW {
    private Class cls;
    
    private String id;
    
    public ExceptionRWBase(Class param1Class) { this.cls = param1Class; }
    
    public Class getExceptionClass() { return this.cls; }
    
    public String getId() { return this.id; }
    
    void setId(String param1String) { this.id = param1String; }
  }
  
  public class ExceptionRWIDLImpl extends ExceptionRWBase {
    private Method readMethod;
    
    private Method writeMethod;
    
    public ExceptionRWIDLImpl(Class param1Class) {
      super(ExceptionHandlerImpl.this, param1Class);
      String str = param1Class.getName() + "Helper";
      ClassLoader classLoader = param1Class.getClassLoader();
      try {
        clazz = Class.forName(str, true, classLoader);
        Method method = clazz.getDeclaredMethod("id", (Class[])null);
        setId((String)method.invoke(null, (Object[])null));
      } catch (Exception exception) {
        throw this$0.wrapper.badHelperIdMethod(exception, str);
      } 
      try {
        Class[] arrayOfClass = { org.omg.CORBA.portable.OutputStream.class, param1Class };
        this.writeMethod = clazz.getDeclaredMethod("write", arrayOfClass);
      } catch (Exception exception) {
        throw this$0.wrapper.badHelperWriteMethod(exception, str);
      } 
      try {
        Class[] arrayOfClass = { org.omg.CORBA.portable.InputStream.class };
        this.readMethod = clazz.getDeclaredMethod("read", arrayOfClass);
      } catch (Exception exception) {
        throw this$0.wrapper.badHelperReadMethod(exception, str);
      } 
    }
    
    public void write(OutputStream param1OutputStream, Exception param1Exception) {
      try {
        Object[] arrayOfObject = { param1OutputStream, param1Exception };
        this.writeMethod.invoke(null, arrayOfObject);
      } catch (Exception exception) {
        throw ExceptionHandlerImpl.this.wrapper.badHelperWriteMethod(exception, this.writeMethod.getDeclaringClass().getName());
      } 
    }
    
    public Exception read(InputStream param1InputStream) {
      try {
        Object[] arrayOfObject = { param1InputStream };
        return (Exception)this.readMethod.invoke(null, arrayOfObject);
      } catch (Exception exception) {
        throw ExceptionHandlerImpl.this.wrapper.badHelperReadMethod(exception, this.readMethod.getDeclaringClass().getName());
      } 
    }
  }
  
  public class ExceptionRWRMIImpl extends ExceptionRWBase {
    public ExceptionRWRMIImpl(Class param1Class) {
      super(ExceptionHandlerImpl.this, param1Class);
      setId(IDLNameTranslatorImpl.getExceptionId(param1Class));
    }
    
    public void write(OutputStream param1OutputStream, Exception param1Exception) {
      param1OutputStream.write_string(getId());
      param1OutputStream.write_value(param1Exception, getExceptionClass());
    }
    
    public Exception read(InputStream param1InputStream) {
      param1InputStream.read_string();
      return (Exception)param1InputStream.read_value(getExceptionClass());
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\ExceptionHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */