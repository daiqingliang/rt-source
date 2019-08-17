package sun.rmi.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.StubNotFoundException;
import java.rmi.server.LogStream;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.rmi.server.Skeleton;
import java.rmi.server.SkeletonNotFoundException;
import java.security.AccessController;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import sun.rmi.runtime.Log;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

public final class Util {
  static final int logLevel = LogStream.parseLevel((String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.server.logLevel")));
  
  public static final Log serverRefLog = Log.getLog("sun.rmi.server.ref", "transport", logLevel);
  
  private static final boolean ignoreStubClasses = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("java.rmi.server.ignoreStubClasses"))).booleanValue();
  
  private static final Map<Class<?>, Void> withoutStubs = Collections.synchronizedMap(new WeakHashMap(11));
  
  private static final Class<?>[] stubConsParamTypes = { RemoteRef.class };
  
  public static Remote createProxy(Class<?> paramClass, RemoteRef paramRemoteRef, boolean paramBoolean) throws StubNotFoundException {
    Class clazz;
    try {
      clazz = getRemoteClass(paramClass);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new StubNotFoundException("object does not implement a remote interface: " + paramClass.getName());
    } 
    if (paramBoolean || (!ignoreStubClasses && stubClassExists(clazz)))
      return createStub(clazz, paramRemoteRef); 
    final ClassLoader loader = paramClass.getClassLoader();
    final Class[] interfaces = getRemoteInterfaces(paramClass);
    final RemoteObjectInvocationHandler handler = new RemoteObjectInvocationHandler(paramRemoteRef);
    try {
      return (Remote)AccessController.doPrivileged(new PrivilegedAction<Remote>() {
            public Remote run() { return (Remote)Proxy.newProxyInstance(loader, interfaces, handler); }
          });
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new StubNotFoundException("unable to create proxy", illegalArgumentException);
    } 
  }
  
  private static boolean stubClassExists(Class<?> paramClass) {
    if (!withoutStubs.containsKey(paramClass))
      try {
        Class.forName(paramClass.getName() + "_Stub", false, paramClass.getClassLoader());
        return true;
      } catch (ClassNotFoundException classNotFoundException) {
        withoutStubs.put(paramClass, null);
      }  
    return false;
  }
  
  private static Class<?> getRemoteClass(Class<?> paramClass) throws ClassNotFoundException {
    while (paramClass != null) {
      Class[] arrayOfClass = paramClass.getInterfaces();
      for (int i = arrayOfClass.length - 1; i >= 0; i--) {
        if (Remote.class.isAssignableFrom(arrayOfClass[i]))
          return paramClass; 
      } 
      paramClass = paramClass.getSuperclass();
    } 
    throw new ClassNotFoundException("class does not implement java.rmi.Remote");
  }
  
  private static Class<?>[] getRemoteInterfaces(Class<?> paramClass) {
    ArrayList arrayList = new ArrayList();
    getRemoteInterfaces(arrayList, paramClass);
    return (Class[])arrayList.toArray(new Class[arrayList.size()]);
  }
  
  private static void getRemoteInterfaces(ArrayList<Class<?>> paramArrayList, Class<?> paramClass) {
    Class clazz = paramClass.getSuperclass();
    if (clazz != null)
      getRemoteInterfaces(paramArrayList, clazz); 
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      Class clazz1 = arrayOfClass[b];
      if (Remote.class.isAssignableFrom(clazz1) && !paramArrayList.contains(clazz1)) {
        Method[] arrayOfMethod = clazz1.getMethods();
        for (byte b1 = 0; b1 < arrayOfMethod.length; b1++)
          checkMethod(arrayOfMethod[b1]); 
        paramArrayList.add(clazz1);
      } 
    } 
  }
  
  private static void checkMethod(Method paramMethod) {
    Class[] arrayOfClass = paramMethod.getExceptionTypes();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      if (arrayOfClass[b].isAssignableFrom(java.rmi.RemoteException.class))
        return; 
    } 
    throw new IllegalArgumentException("illegal remote method encountered: " + paramMethod);
  }
  
  private static RemoteStub createStub(Class<?> paramClass, RemoteRef paramRemoteRef) throws StubNotFoundException {
    String str = paramClass.getName() + "_Stub";
    try {
      Class clazz = Class.forName(str, false, paramClass.getClassLoader());
      Constructor constructor = clazz.getConstructor(stubConsParamTypes);
      return (RemoteStub)constructor.newInstance(new Object[] { paramRemoteRef });
    } catch (ClassNotFoundException classNotFoundException) {
      throw new StubNotFoundException("Stub class not found: " + str, classNotFoundException);
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new StubNotFoundException("Stub class missing constructor: " + str, noSuchMethodException);
    } catch (InstantiationException instantiationException) {
      throw new StubNotFoundException("Can't create instance of stub class: " + str, instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new StubNotFoundException("Stub class constructor not public: " + str, illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      throw new StubNotFoundException("Exception creating instance of stub class: " + str, invocationTargetException);
    } catch (ClassCastException classCastException) {
      throw new StubNotFoundException("Stub class not instance of RemoteStub: " + str, classCastException);
    } 
  }
  
  static Skeleton createSkeleton(Remote paramRemote) throws SkeletonNotFoundException {
    Class clazz;
    try {
      clazz = getRemoteClass(paramRemote.getClass());
    } catch (ClassNotFoundException classNotFoundException) {
      throw new SkeletonNotFoundException("object does not implement a remote interface: " + paramRemote.getClass().getName());
    } 
    String str = clazz.getName() + "_Skel";
    try {
      Class clazz1 = Class.forName(str, false, clazz.getClassLoader());
      return (Skeleton)clazz1.newInstance();
    } catch (ClassNotFoundException classNotFoundException) {
      throw new SkeletonNotFoundException("Skeleton class not found: " + str, classNotFoundException);
    } catch (InstantiationException instantiationException) {
      throw new SkeletonNotFoundException("Can't create skeleton: " + str, instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new SkeletonNotFoundException("No public constructor: " + str, illegalAccessException);
    } catch (ClassCastException classCastException) {
      throw new SkeletonNotFoundException("Skeleton not of correct class: " + str, classCastException);
    } 
  }
  
  public static long computeMethodHash(Method paramMethod) {
    long l = 0L;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(127);
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA");
      DataOutputStream dataOutputStream = new DataOutputStream(new DigestOutputStream(byteArrayOutputStream, messageDigest));
      String str = getMethodNameAndDescriptor(paramMethod);
      if (serverRefLog.isLoggable(Log.VERBOSE))
        serverRefLog.log(Log.VERBOSE, "string used for method hash: \"" + str + "\""); 
      dataOutputStream.writeUTF(str);
      dataOutputStream.flush();
      byte[] arrayOfByte = messageDigest.digest();
      for (byte b = 0; b < Math.min(8, arrayOfByte.length); b++)
        l += ((arrayOfByte[b] & 0xFF) << b * 8); 
    } catch (IOException iOException) {
      l = -1L;
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new SecurityException(noSuchAlgorithmException.getMessage());
    } 
    return l;
  }
  
  private static String getMethodNameAndDescriptor(Method paramMethod) {
    StringBuffer stringBuffer = new StringBuffer(paramMethod.getName());
    stringBuffer.append('(');
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    for (byte b = 0; b < arrayOfClass.length; b++)
      stringBuffer.append(getTypeDescriptor(arrayOfClass[b])); 
    stringBuffer.append(')');
    Class clazz = paramMethod.getReturnType();
    if (clazz == void.class) {
      stringBuffer.append('V');
    } else {
      stringBuffer.append(getTypeDescriptor(clazz));
    } 
    return stringBuffer.toString();
  }
  
  private static String getTypeDescriptor(Class<?> paramClass) {
    if (paramClass.isPrimitive()) {
      if (paramClass == int.class)
        return "I"; 
      if (paramClass == boolean.class)
        return "Z"; 
      if (paramClass == byte.class)
        return "B"; 
      if (paramClass == char.class)
        return "C"; 
      if (paramClass == short.class)
        return "S"; 
      if (paramClass == long.class)
        return "J"; 
      if (paramClass == float.class)
        return "F"; 
      if (paramClass == double.class)
        return "D"; 
      if (paramClass == void.class)
        return "V"; 
      throw new Error("unrecognized primitive type: " + paramClass);
    } 
    return paramClass.isArray() ? paramClass.getName().replace('.', '/') : ("L" + paramClass.getName().replace('.', '/') + ";");
  }
  
  public static String getUnqualifiedName(Class<?> paramClass) {
    String str = paramClass.getName();
    return str.substring(str.lastIndexOf('.') + 1);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */