package java.lang.invoke;

import java.lang.invoke.TypeConvertingMethodAdapter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import sun.invoke.util.BytecodeDescriptor;
import sun.invoke.util.Wrapper;

class TypeConvertingMethodAdapter extends MethodVisitor {
  private static final int NUM_WRAPPERS = Wrapper.values().length;
  
  private static final String NAME_OBJECT = "java/lang/Object";
  
  private static final String WRAPPER_PREFIX = "Ljava/lang/";
  
  private static final String NAME_BOX_METHOD = "valueOf";
  
  private static final int[][] wideningOpcodes = new int[NUM_WRAPPERS][NUM_WRAPPERS];
  
  private static final Wrapper[] FROM_WRAPPER_NAME = new Wrapper[16];
  
  private static final Wrapper[] FROM_TYPE_SORT = new Wrapper[16];
  
  TypeConvertingMethodAdapter(MethodVisitor paramMethodVisitor) { super(327680, paramMethodVisitor); }
  
  private static void initWidening(Wrapper paramWrapper, int paramInt, Wrapper... paramVarArgs) {
    for (Wrapper wrapper : paramVarArgs)
      wideningOpcodes[wrapper.ordinal()][paramWrapper.ordinal()] = paramInt; 
  }
  
  private static int hashWrapperName(String paramString) { return (paramString.length() < 3) ? 0 : (('\003' * paramString.charAt(1) + paramString.charAt(2)) % '\020'); }
  
  private Wrapper wrapperOrNullFromDescriptor(String paramString) {
    if (!paramString.startsWith("Ljava/lang/"))
      return null; 
    String str = paramString.substring("Ljava/lang/".length(), paramString.length() - 1);
    Wrapper wrapper = FROM_WRAPPER_NAME[hashWrapperName(str)];
    return (wrapper == null || wrapper.wrapperSimpleName().equals(str)) ? wrapper : null;
  }
  
  private static String wrapperName(Wrapper paramWrapper) { return "java/lang/" + paramWrapper.wrapperSimpleName(); }
  
  private static String unboxMethod(Wrapper paramWrapper) { return paramWrapper.primitiveSimpleName() + "Value"; }
  
  private static String boxingDescriptor(Wrapper paramWrapper) { return String.format("(%s)L%s;", new Object[] { Character.valueOf(paramWrapper.basicTypeChar()), wrapperName(paramWrapper) }); }
  
  private static String unboxingDescriptor(Wrapper paramWrapper) { return "()" + paramWrapper.basicTypeChar(); }
  
  void boxIfTypePrimitive(Type paramType) {
    Wrapper wrapper = FROM_TYPE_SORT[paramType.getSort()];
    if (wrapper != null)
      box(wrapper); 
  }
  
  void widen(Wrapper paramWrapper1, Wrapper paramWrapper2) {
    if (paramWrapper1 != paramWrapper2) {
      int i = wideningOpcodes[paramWrapper1.ordinal()][paramWrapper2.ordinal()];
      if (i != 0)
        visitInsn(i); 
    } 
  }
  
  void box(Wrapper paramWrapper) { visitMethodInsn(184, wrapperName(paramWrapper), "valueOf", boxingDescriptor(paramWrapper), false); }
  
  void unbox(String paramString, Wrapper paramWrapper) { visitMethodInsn(182, paramString, unboxMethod(paramWrapper), unboxingDescriptor(paramWrapper), false); }
  
  private String descriptorToName(String paramString) {
    int i = paramString.length() - 1;
    return (paramString.charAt(0) == 'L' && paramString.charAt(i) == ';') ? paramString.substring(1, i) : paramString;
  }
  
  void cast(String paramString1, String paramString2) {
    String str1 = descriptorToName(paramString1);
    String str2 = descriptorToName(paramString2);
    if (!str2.equals(str1) && !str2.equals("java/lang/Object"))
      visitTypeInsn(192, str2); 
  }
  
  private boolean isPrimitive(Wrapper paramWrapper) { return (paramWrapper != Wrapper.OBJECT); }
  
  private Wrapper toWrapper(String paramString) {
    char c = paramString.charAt(0);
    if (c == '[' || c == '(')
      c = 'L'; 
    return Wrapper.forBasicType(c);
  }
  
  void convertType(Class<?> paramClass1, Class<?> paramClass2, Class<?> paramClass3) {
    if (paramClass1.equals(paramClass2) && paramClass1.equals(paramClass3))
      return; 
    if (paramClass1 == void.class || paramClass2 == void.class)
      return; 
    if (paramClass1.isPrimitive()) {
      Wrapper wrapper = Wrapper.forPrimitiveType(paramClass1);
      if (paramClass2.isPrimitive()) {
        widen(wrapper, Wrapper.forPrimitiveType(paramClass2));
      } else {
        String str = BytecodeDescriptor.unparse(paramClass2);
        Wrapper wrapper1 = wrapperOrNullFromDescriptor(str);
        if (wrapper1 != null) {
          widen(wrapper, wrapper1);
          box(wrapper1);
        } else {
          box(wrapper);
          cast(wrapperName(wrapper), str);
        } 
      } 
    } else {
      String str2;
      String str1 = BytecodeDescriptor.unparse(paramClass1);
      if (paramClass3.isPrimitive()) {
        str2 = str1;
      } else {
        str2 = BytecodeDescriptor.unparse(paramClass3);
        cast(str1, str2);
      } 
      String str3 = BytecodeDescriptor.unparse(paramClass2);
      if (paramClass2.isPrimitive()) {
        Wrapper wrapper1 = toWrapper(str3);
        Wrapper wrapper2 = wrapperOrNullFromDescriptor(str2);
        if (wrapper2 != null) {
          if (wrapper2.isSigned() || wrapper2.isFloating()) {
            unbox(wrapperName(wrapper2), wrapper1);
          } else {
            unbox(wrapperName(wrapper2), wrapper2);
            widen(wrapper2, wrapper1);
          } 
        } else {
          String str;
          if (wrapper1.isSigned() || wrapper1.isFloating()) {
            str = "java/lang/Number";
          } else {
            str = wrapperName(wrapper1);
          } 
          cast(str2, str);
          unbox(str, wrapper1);
        } 
      } else {
        cast(str2, str3);
      } 
    } 
  }
  
  void iconst(int paramInt) {
    if (paramInt >= -1 && paramInt <= 5) {
      this.mv.visitInsn(3 + paramInt);
    } else if (paramInt >= -128 && paramInt <= 127) {
      this.mv.visitIntInsn(16, paramInt);
    } else if (paramInt >= -32768 && paramInt <= 32767) {
      this.mv.visitIntInsn(17, paramInt);
    } else {
      this.mv.visitLdcInsn(Integer.valueOf(paramInt));
    } 
  }
  
  static  {
    for (Wrapper wrapper : Wrapper.values()) {
      if (wrapper.basicTypeChar() != 'L') {
        int i = hashWrapperName(wrapper.wrapperSimpleName());
        assert FROM_WRAPPER_NAME[i] == null;
        FROM_WRAPPER_NAME[i] = wrapper;
      } 
    } 
    for (byte b = 0; b < NUM_WRAPPERS; b++) {
      for (byte b1 = 0; b1 < NUM_WRAPPERS; b1++)
        wideningOpcodes[b][b1] = 0; 
    } 
    initWidening(Wrapper.LONG, 133, new Wrapper[] { Wrapper.BYTE, Wrapper.SHORT, Wrapper.INT, Wrapper.CHAR });
    initWidening(Wrapper.LONG, 140, new Wrapper[] { Wrapper.FLOAT });
    initWidening(Wrapper.FLOAT, 134, new Wrapper[] { Wrapper.BYTE, Wrapper.SHORT, Wrapper.INT, Wrapper.CHAR });
    initWidening(Wrapper.FLOAT, 137, new Wrapper[] { Wrapper.LONG });
    initWidening(Wrapper.DOUBLE, 135, new Wrapper[] { Wrapper.BYTE, Wrapper.SHORT, Wrapper.INT, Wrapper.CHAR });
    initWidening(Wrapper.DOUBLE, 141, new Wrapper[] { Wrapper.FLOAT });
    initWidening(Wrapper.DOUBLE, 138, new Wrapper[] { Wrapper.LONG });
    FROM_TYPE_SORT[3] = Wrapper.BYTE;
    FROM_TYPE_SORT[4] = Wrapper.SHORT;
    FROM_TYPE_SORT[5] = Wrapper.INT;
    FROM_TYPE_SORT[7] = Wrapper.LONG;
    FROM_TYPE_SORT[2] = Wrapper.CHAR;
    FROM_TYPE_SORT[6] = Wrapper.FLOAT;
    FROM_TYPE_SORT[8] = Wrapper.DOUBLE;
    FROM_TYPE_SORT[1] = Wrapper.BOOLEAN;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\TypeConvertingMethodAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */