package com.sun.org.apache.bcel.internal.classfile;

public interface Visitor {
  void visitCode(Code paramCode);
  
  void visitCodeException(CodeException paramCodeException);
  
  void visitConstantClass(ConstantClass paramConstantClass);
  
  void visitConstantDouble(ConstantDouble paramConstantDouble);
  
  void visitConstantFieldref(ConstantFieldref paramConstantFieldref);
  
  void visitConstantFloat(ConstantFloat paramConstantFloat);
  
  void visitConstantInteger(ConstantInteger paramConstantInteger);
  
  void visitConstantInterfaceMethodref(ConstantInterfaceMethodref paramConstantInterfaceMethodref);
  
  void visitConstantLong(ConstantLong paramConstantLong);
  
  void visitConstantMethodref(ConstantMethodref paramConstantMethodref);
  
  void visitConstantNameAndType(ConstantNameAndType paramConstantNameAndType);
  
  void visitConstantPool(ConstantPool paramConstantPool);
  
  void visitConstantString(ConstantString paramConstantString);
  
  void visitConstantUtf8(ConstantUtf8 paramConstantUtf8);
  
  void visitConstantValue(ConstantValue paramConstantValue);
  
  void visitDeprecated(Deprecated paramDeprecated);
  
  void visitExceptionTable(ExceptionTable paramExceptionTable);
  
  void visitField(Field paramField);
  
  void visitInnerClass(InnerClass paramInnerClass);
  
  void visitInnerClasses(InnerClasses paramInnerClasses);
  
  void visitJavaClass(JavaClass paramJavaClass);
  
  void visitLineNumber(LineNumber paramLineNumber);
  
  void visitLineNumberTable(LineNumberTable paramLineNumberTable);
  
  void visitLocalVariable(LocalVariable paramLocalVariable);
  
  void visitLocalVariableTable(LocalVariableTable paramLocalVariableTable);
  
  void visitLocalVariableTypeTable(LocalVariableTypeTable paramLocalVariableTypeTable);
  
  void visitMethod(Method paramMethod);
  
  void visitSignature(Signature paramSignature);
  
  void visitSourceFile(SourceFile paramSourceFile);
  
  void visitSynthetic(Synthetic paramSynthetic);
  
  void visitUnknown(Unknown paramUnknown);
  
  void visitStackMap(StackMap paramStackMap);
  
  void visitStackMapEntry(StackMapEntry paramStackMapEntry);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Visitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */