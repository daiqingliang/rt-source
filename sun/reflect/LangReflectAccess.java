package sun.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface LangReflectAccess {
  Field newField(Class<?> paramClass1, String paramString1, Class<?> paramClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte);
  
  Method newMethod(Class<?> paramClass1, String paramString1, Class<?>[] paramArrayOfClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3);
  
  <T> Constructor<T> newConstructor(Class<T> paramClass, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  
  MethodAccessor getMethodAccessor(Method paramMethod);
  
  void setMethodAccessor(Method paramMethod, MethodAccessor paramMethodAccessor);
  
  ConstructorAccessor getConstructorAccessor(Constructor<?> paramConstructor);
  
  void setConstructorAccessor(Constructor<?> paramConstructor, ConstructorAccessor paramConstructorAccessor);
  
  byte[] getExecutableTypeAnnotationBytes(Executable paramExecutable);
  
  int getConstructorSlot(Constructor<?> paramConstructor);
  
  String getConstructorSignature(Constructor<?> paramConstructor);
  
  byte[] getConstructorAnnotations(Constructor<?> paramConstructor);
  
  byte[] getConstructorParameterAnnotations(Constructor<?> paramConstructor);
  
  Method copyMethod(Method paramMethod);
  
  Field copyField(Field paramField);
  
  <T> Constructor<T> copyConstructor(Constructor<T> paramConstructor);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\LangReflectAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */