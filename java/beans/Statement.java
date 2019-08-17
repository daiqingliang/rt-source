package java.beans;

import com.sun.beans.finder.ClassFinder;
import com.sun.beans.finder.ConstructorFinder;
import com.sun.beans.finder.MethodFinder;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import sun.reflect.misc.MethodUtil;

public class Statement {
  private static Object[] emptyArray = new Object[0];
  
  static ExceptionListener defaultExceptionListener = new ExceptionListener() {
      public void exceptionThrown(Exception param1Exception) {
        System.err.println(param1Exception);
        System.err.println("Continuing ...");
      }
    };
  
  private final AccessControlContext acc = AccessController.getContext();
  
  private final Object target;
  
  private final String methodName;
  
  private final Object[] arguments;
  
  ClassLoader loader;
  
  @ConstructorProperties({"target", "methodName", "arguments"})
  public Statement(Object paramObject, String paramString, Object[] paramArrayOfObject) {
    this.target = paramObject;
    this.methodName = paramString;
    this.arguments = (paramArrayOfObject == null) ? emptyArray : (Object[])paramArrayOfObject.clone();
  }
  
  public Object getTarget() { return this.target; }
  
  public String getMethodName() { return this.methodName; }
  
  public Object[] getArguments() { return (Object[])this.arguments.clone(); }
  
  public void execute() throws Exception { invoke(); }
  
  Object invoke() {
    AccessControlContext accessControlContext = this.acc;
    if (accessControlContext == null && System.getSecurityManager() != null)
      throw new SecurityException("AccessControlContext is not set"); 
    try {
      return AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
            public Object run() { return Statement.this.invokeInternal(); }
          },  accessControlContext);
    } catch (PrivilegedActionException privilegedActionException) {
      throw privilegedActionException.getException();
    } 
  }
  
  private Object invokeInternal() {
    Object object = getTarget();
    String str = getMethodName();
    if (object == null || str == null)
      throw new NullPointerException(((object == null) ? "target" : "methodName") + " should not be null"); 
    Object[] arrayOfObject = getArguments();
    if (arrayOfObject == null)
      arrayOfObject = emptyArray; 
    if (object == Class.class && str.equals("forName"))
      return ClassFinder.resolveClass((String)arrayOfObject[0], this.loader); 
    Class[] arrayOfClass = new Class[arrayOfObject.length];
    for (byte b = 0; b < arrayOfObject.length; b++)
      arrayOfClass[b] = (arrayOfObject[b] == null) ? null : arrayOfObject[b].getClass(); 
    Method method = null;
    if (object instanceof Class) {
      Method method1;
      if (str.equals("new"))
        str = "newInstance"; 
      if (str.equals("newInstance") && ((Class)object).isArray()) {
        Object object1 = Array.newInstance(((Class)object).getComponentType(), arrayOfObject.length);
        for (byte b1 = 0; b1 < arrayOfObject.length; b1++)
          Array.set(object1, b1, arrayOfObject[b1]); 
        return object1;
      } 
      if (str.equals("newInstance") && arrayOfObject.length != 0) {
        if (object == Character.class && arrayOfObject.length == 1 && arrayOfClass[false] == String.class)
          return new Character(((String)arrayOfObject[0]).charAt(0)); 
        try {
          method = ConstructorFinder.findConstructor((Class)object, arrayOfClass);
        } catch (NoSuchMethodException noSuchMethodException) {
          method = null;
        } 
      } 
      if (method == null && object != Class.class)
        method1 = getMethod((Class)object, str, arrayOfClass); 
      if (method1 == null)
        method1 = getMethod(Class.class, str, arrayOfClass); 
    } else {
      if (object.getClass().isArray() && (str.equals("set") || str.equals("get"))) {
        int i = ((Integer)arrayOfObject[0]).intValue();
        if (str.equals("get"))
          return Array.get(object, i); 
        Array.set(object, i, arrayOfObject[1]);
        return null;
      } 
      method = getMethod(object.getClass(), str, arrayOfClass);
    } 
    if (method != null)
      try {
        return (method instanceof Method) ? MethodUtil.invoke((Method)method, object, arrayOfObject) : ((Constructor)method).newInstance(arrayOfObject);
      } catch (IllegalAccessException illegalAccessException) {
        throw new Exception("Statement cannot invoke: " + str + " on " + object.getClass(), illegalAccessException);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getTargetException();
        if (throwable instanceof Exception)
          throw (Exception)throwable; 
        throw invocationTargetException;
      }  
    throw new NoSuchMethodException(toString());
  }
  
  String instanceName(Object paramObject) { return (paramObject == null) ? "null" : ((paramObject.getClass() == String.class) ? ("\"" + (String)paramObject + "\"") : NameGenerator.unqualifiedClassName(paramObject.getClass())); }
  
  public String toString() {
    Object object = getTarget();
    String str = getMethodName();
    Object[] arrayOfObject = getArguments();
    if (arrayOfObject == null)
      arrayOfObject = emptyArray; 
    StringBuffer stringBuffer = new StringBuffer(instanceName(object) + "." + str + "(");
    int i = arrayOfObject.length;
    for (byte b = 0; b < i; b++) {
      stringBuffer.append(instanceName(arrayOfObject[b]));
      if (b != i - 1)
        stringBuffer.append(", "); 
    } 
    stringBuffer.append(");");
    return stringBuffer.toString();
  }
  
  static Method getMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs) {
    try {
      return MethodFinder.findMethod(paramClass, paramString, paramVarArgs);
    } catch (NoSuchMethodException noSuchMethodException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\Statement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */