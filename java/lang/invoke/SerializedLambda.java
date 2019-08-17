package java.lang.invoke;

import java.io.Serializable;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;

public final class SerializedLambda implements Serializable {
  private static final long serialVersionUID = 8025925345765570181L;
  
  private final Class<?> capturingClass;
  
  private final String functionalInterfaceClass;
  
  private final String functionalInterfaceMethodName;
  
  private final String functionalInterfaceMethodSignature;
  
  private final String implClass;
  
  private final String implMethodName;
  
  private final String implMethodSignature;
  
  private final int implMethodKind;
  
  private final String instantiatedMethodType;
  
  private final Object[] capturedArgs;
  
  public SerializedLambda(Class<?> paramClass, String paramString1, String paramString2, String paramString3, int paramInt, String paramString4, String paramString5, String paramString6, String paramString7, Object[] paramArrayOfObject) {
    this.capturingClass = paramClass;
    this.functionalInterfaceClass = paramString1;
    this.functionalInterfaceMethodName = paramString2;
    this.functionalInterfaceMethodSignature = paramString3;
    this.implMethodKind = paramInt;
    this.implClass = paramString4;
    this.implMethodName = paramString5;
    this.implMethodSignature = paramString6;
    this.instantiatedMethodType = paramString7;
    this.capturedArgs = (Object[])((Object[])Objects.requireNonNull(paramArrayOfObject)).clone();
  }
  
  public String getCapturingClass() { return this.capturingClass.getName().replace('.', '/'); }
  
  public String getFunctionalInterfaceClass() { return this.functionalInterfaceClass; }
  
  public String getFunctionalInterfaceMethodName() { return this.functionalInterfaceMethodName; }
  
  public String getFunctionalInterfaceMethodSignature() { return this.functionalInterfaceMethodSignature; }
  
  public String getImplClass() { return this.implClass; }
  
  public String getImplMethodName() { return this.implMethodName; }
  
  public String getImplMethodSignature() { return this.implMethodSignature; }
  
  public int getImplMethodKind() { return this.implMethodKind; }
  
  public final String getInstantiatedMethodType() { return this.instantiatedMethodType; }
  
  public int getCapturedArgCount() { return this.capturedArgs.length; }
  
  public Object getCapturedArg(int paramInt) { return this.capturedArgs[paramInt]; }
  
  private Object readResolve() throws ReflectiveOperationException {
    try {
      Method method = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
              Method method = SerializedLambda.this.capturingClass.getDeclaredMethod("$deserializeLambda$", new Class[] { SerializedLambda.class });
              method.setAccessible(true);
              return method;
            }
          });
      return method.invoke(null, new Object[] { this });
    } catch (PrivilegedActionException privilegedActionException) {
      Exception exception = privilegedActionException.getException();
      if (exception instanceof ReflectiveOperationException)
        throw (ReflectiveOperationException)exception; 
      if (exception instanceof RuntimeException)
        throw (RuntimeException)exception; 
      throw new RuntimeException("Exception in SerializedLambda.readResolve", privilegedActionException);
    } 
  }
  
  public String toString() {
    String str;
    return (str = MethodHandleInfo.referenceKindToString(this.implMethodKind)).format("SerializedLambda[%s=%s, %s=%s.%s:%s, %s=%s %s.%s:%s, %s=%s, %s=%d]", new Object[] { 
          "capturingClass", this.capturingClass, "functionalInterfaceMethod", this.functionalInterfaceClass, this.functionalInterfaceMethodName, this.functionalInterfaceMethodSignature, "implementation", str, this.implClass, this.implMethodName, 
          this.implMethodSignature, "instantiatedMethodType", this.instantiatedMethodType, "numCaptured", Integer.valueOf(this.capturedArgs.length) });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\SerializedLambda.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */