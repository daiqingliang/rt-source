package java.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import sun.reflect.ConstructorAccessor;
import sun.reflect.LangReflectAccess;
import sun.reflect.MethodAccessor;

class ReflectAccess implements LangReflectAccess {
  public Field newField(Class<?> paramClass1, String paramString1, Class<?> paramClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte) { return new Field(paramClass1, paramString1, paramClass2, paramInt1, paramInt2, paramString2, paramArrayOfByte); }
  
  public Method newMethod(Class<?> paramClass1, String paramString1, Class<?>[] paramArrayOfClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) { return new Method(paramClass1, paramString1, paramArrayOfClass1, paramClass2, paramArrayOfClass2, paramInt1, paramInt2, paramString2, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3); }
  
  public <T> Constructor<T> newConstructor(Class<T> paramClass, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) { return new Constructor(paramClass, paramArrayOfClass1, paramArrayOfClass2, paramInt1, paramInt2, paramString, paramArrayOfByte1, paramArrayOfByte2); }
  
  public MethodAccessor getMethodAccessor(Method paramMethod) { return paramMethod.getMethodAccessor(); }
  
  public void setMethodAccessor(Method paramMethod, MethodAccessor paramMethodAccessor) { paramMethod.setMethodAccessor(paramMethodAccessor); }
  
  public ConstructorAccessor getConstructorAccessor(Constructor<?> paramConstructor) { return paramConstructor.getConstructorAccessor(); }
  
  public void setConstructorAccessor(Constructor<?> paramConstructor, ConstructorAccessor paramConstructorAccessor) { paramConstructor.setConstructorAccessor(paramConstructorAccessor); }
  
  public int getConstructorSlot(Constructor<?> paramConstructor) { return paramConstructor.getSlot(); }
  
  public String getConstructorSignature(Constructor<?> paramConstructor) { return paramConstructor.getSignature(); }
  
  public byte[] getConstructorAnnotations(Constructor<?> paramConstructor) { return paramConstructor.getRawAnnotations(); }
  
  public byte[] getConstructorParameterAnnotations(Constructor<?> paramConstructor) { return paramConstructor.getRawParameterAnnotations(); }
  
  public byte[] getExecutableTypeAnnotationBytes(Executable paramExecutable) { return paramExecutable.getTypeAnnotationBytes(); }
  
  public Method copyMethod(Method paramMethod) { return paramMethod.copy(); }
  
  public Field copyField(Field paramField) { return paramField.copy(); }
  
  public <T> Constructor<T> copyConstructor(Constructor<T> paramConstructor) { return paramConstructor.copy(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\ReflectAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */