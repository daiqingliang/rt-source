package sun.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class UnsafeFieldAccessorFactory {
  static FieldAccessor newFieldAccessor(Field paramField, boolean paramBoolean) {
    Class clazz = paramField.getType();
    boolean bool1 = Modifier.isStatic(paramField.getModifiers());
    boolean bool2 = Modifier.isFinal(paramField.getModifiers());
    boolean bool3 = Modifier.isVolatile(paramField.getModifiers());
    boolean bool = (bool2 || bool3) ? 1 : 0;
    boolean bool4 = (bool2 && (bool1 || !paramBoolean));
    if (bool1) {
      UnsafeFieldAccessorImpl.unsafe.ensureClassInitialized(paramField.getDeclaringClass());
      return !bool ? ((clazz == boolean.class) ? new UnsafeStaticBooleanFieldAccessorImpl(paramField) : ((clazz == byte.class) ? new UnsafeStaticByteFieldAccessorImpl(paramField) : ((clazz == short.class) ? new UnsafeStaticShortFieldAccessorImpl(paramField) : ((clazz == char.class) ? new UnsafeStaticCharacterFieldAccessorImpl(paramField) : ((clazz == int.class) ? new UnsafeStaticIntegerFieldAccessorImpl(paramField) : ((clazz == long.class) ? new UnsafeStaticLongFieldAccessorImpl(paramField) : ((clazz == float.class) ? new UnsafeStaticFloatFieldAccessorImpl(paramField) : ((clazz == double.class) ? new UnsafeStaticDoubleFieldAccessorImpl(paramField) : new UnsafeStaticObjectFieldAccessorImpl(paramField))))))))) : ((clazz == boolean.class) ? new UnsafeQualifiedStaticBooleanFieldAccessorImpl(paramField, bool4) : ((clazz == byte.class) ? new UnsafeQualifiedStaticByteFieldAccessorImpl(paramField, bool4) : ((clazz == short.class) ? new UnsafeQualifiedStaticShortFieldAccessorImpl(paramField, bool4) : ((clazz == char.class) ? new UnsafeQualifiedStaticCharacterFieldAccessorImpl(paramField, bool4) : ((clazz == int.class) ? new UnsafeQualifiedStaticIntegerFieldAccessorImpl(paramField, bool4) : ((clazz == long.class) ? new UnsafeQualifiedStaticLongFieldAccessorImpl(paramField, bool4) : ((clazz == float.class) ? new UnsafeQualifiedStaticFloatFieldAccessorImpl(paramField, bool4) : ((clazz == double.class) ? new UnsafeQualifiedStaticDoubleFieldAccessorImpl(paramField, bool4) : new UnsafeQualifiedStaticObjectFieldAccessorImpl(paramField, bool4)))))))));
    } 
    return !bool ? ((clazz == boolean.class) ? new UnsafeBooleanFieldAccessorImpl(paramField) : ((clazz == byte.class) ? new UnsafeByteFieldAccessorImpl(paramField) : ((clazz == short.class) ? new UnsafeShortFieldAccessorImpl(paramField) : ((clazz == char.class) ? new UnsafeCharacterFieldAccessorImpl(paramField) : ((clazz == int.class) ? new UnsafeIntegerFieldAccessorImpl(paramField) : ((clazz == long.class) ? new UnsafeLongFieldAccessorImpl(paramField) : ((clazz == float.class) ? new UnsafeFloatFieldAccessorImpl(paramField) : ((clazz == double.class) ? new UnsafeDoubleFieldAccessorImpl(paramField) : new UnsafeObjectFieldAccessorImpl(paramField))))))))) : ((clazz == boolean.class) ? new UnsafeQualifiedBooleanFieldAccessorImpl(paramField, bool4) : ((clazz == byte.class) ? new UnsafeQualifiedByteFieldAccessorImpl(paramField, bool4) : ((clazz == short.class) ? new UnsafeQualifiedShortFieldAccessorImpl(paramField, bool4) : ((clazz == char.class) ? new UnsafeQualifiedCharacterFieldAccessorImpl(paramField, bool4) : ((clazz == int.class) ? new UnsafeQualifiedIntegerFieldAccessorImpl(paramField, bool4) : ((clazz == long.class) ? new UnsafeQualifiedLongFieldAccessorImpl(paramField, bool4) : ((clazz == float.class) ? new UnsafeQualifiedFloatFieldAccessorImpl(paramField, bool4) : ((clazz == double.class) ? new UnsafeQualifiedDoubleFieldAccessorImpl(paramField, bool4) : new UnsafeQualifiedObjectFieldAccessorImpl(paramField, bool4)))))))));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\UnsafeFieldAccessorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */