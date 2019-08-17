package com.sun.org.apache.bcel.internal.classfile;

import java.util.Stack;

public class DescendingVisitor implements Visitor {
  private JavaClass clazz;
  
  private Visitor visitor;
  
  private Stack stack = new Stack();
  
  public Object predecessor() { return predecessor(0); }
  
  public Object predecessor(int paramInt) {
    int i = this.stack.size();
    return (i < 2 || paramInt < 0) ? null : this.stack.elementAt(i - paramInt + 2);
  }
  
  public Object current() { return this.stack.peek(); }
  
  public DescendingVisitor(JavaClass paramJavaClass, Visitor paramVisitor) {
    this.clazz = paramJavaClass;
    this.visitor = paramVisitor;
  }
  
  public void visit() { this.clazz.accept(this); }
  
  public void visitJavaClass(JavaClass paramJavaClass) {
    this.stack.push(paramJavaClass);
    paramJavaClass.accept(this.visitor);
    Field[] arrayOfField = paramJavaClass.getFields();
    for (byte b1 = 0; b1 < arrayOfField.length; b1++)
      arrayOfField[b1].accept(this); 
    Method[] arrayOfMethod = paramJavaClass.getMethods();
    for (byte b2 = 0; b2 < arrayOfMethod.length; b2++)
      arrayOfMethod[b2].accept(this); 
    Attribute[] arrayOfAttribute = paramJavaClass.getAttributes();
    for (byte b3 = 0; b3 < arrayOfAttribute.length; b3++)
      arrayOfAttribute[b3].accept(this); 
    paramJavaClass.getConstantPool().accept(this);
    this.stack.pop();
  }
  
  public void visitField(Field paramField) {
    this.stack.push(paramField);
    paramField.accept(this.visitor);
    Attribute[] arrayOfAttribute = paramField.getAttributes();
    for (byte b = 0; b < arrayOfAttribute.length; b++)
      arrayOfAttribute[b].accept(this); 
    this.stack.pop();
  }
  
  public void visitConstantValue(ConstantValue paramConstantValue) {
    this.stack.push(paramConstantValue);
    paramConstantValue.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitMethod(Method paramMethod) {
    this.stack.push(paramMethod);
    paramMethod.accept(this.visitor);
    Attribute[] arrayOfAttribute = paramMethod.getAttributes();
    for (byte b = 0; b < arrayOfAttribute.length; b++)
      arrayOfAttribute[b].accept(this); 
    this.stack.pop();
  }
  
  public void visitExceptionTable(ExceptionTable paramExceptionTable) {
    this.stack.push(paramExceptionTable);
    paramExceptionTable.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitCode(Code paramCode) {
    this.stack.push(paramCode);
    paramCode.accept(this.visitor);
    CodeException[] arrayOfCodeException = paramCode.getExceptionTable();
    for (byte b1 = 0; b1 < arrayOfCodeException.length; b1++)
      arrayOfCodeException[b1].accept(this); 
    Attribute[] arrayOfAttribute = paramCode.getAttributes();
    for (byte b2 = 0; b2 < arrayOfAttribute.length; b2++)
      arrayOfAttribute[b2].accept(this); 
    this.stack.pop();
  }
  
  public void visitCodeException(CodeException paramCodeException) {
    this.stack.push(paramCodeException);
    paramCodeException.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitLineNumberTable(LineNumberTable paramLineNumberTable) {
    this.stack.push(paramLineNumberTable);
    paramLineNumberTable.accept(this.visitor);
    LineNumber[] arrayOfLineNumber = paramLineNumberTable.getLineNumberTable();
    for (byte b = 0; b < arrayOfLineNumber.length; b++)
      arrayOfLineNumber[b].accept(this); 
    this.stack.pop();
  }
  
  public void visitLineNumber(LineNumber paramLineNumber) {
    this.stack.push(paramLineNumber);
    paramLineNumber.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitLocalVariableTable(LocalVariableTable paramLocalVariableTable) {
    this.stack.push(paramLocalVariableTable);
    paramLocalVariableTable.accept(this.visitor);
    LocalVariable[] arrayOfLocalVariable = paramLocalVariableTable.getLocalVariableTable();
    for (byte b = 0; b < arrayOfLocalVariable.length; b++)
      arrayOfLocalVariable[b].accept(this); 
    this.stack.pop();
  }
  
  public void visitLocalVariableTypeTable(LocalVariableTypeTable paramLocalVariableTypeTable) {
    this.stack.push(paramLocalVariableTypeTable);
    paramLocalVariableTypeTable.accept(this.visitor);
    LocalVariable[] arrayOfLocalVariable = paramLocalVariableTypeTable.getLocalVariableTypeTable();
    for (byte b = 0; b < arrayOfLocalVariable.length; b++)
      arrayOfLocalVariable[b].accept(this); 
    this.stack.pop();
  }
  
  public void visitStackMap(StackMap paramStackMap) {
    this.stack.push(paramStackMap);
    paramStackMap.accept(this.visitor);
    StackMapEntry[] arrayOfStackMapEntry = paramStackMap.getStackMap();
    for (byte b = 0; b < arrayOfStackMapEntry.length; b++)
      arrayOfStackMapEntry[b].accept(this); 
    this.stack.pop();
  }
  
  public void visitStackMapEntry(StackMapEntry paramStackMapEntry) {
    this.stack.push(paramStackMapEntry);
    paramStackMapEntry.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitLocalVariable(LocalVariable paramLocalVariable) {
    this.stack.push(paramLocalVariable);
    paramLocalVariable.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitConstantPool(ConstantPool paramConstantPool) {
    this.stack.push(paramConstantPool);
    paramConstantPool.accept(this.visitor);
    Constant[] arrayOfConstant = paramConstantPool.getConstantPool();
    for (byte b = 1; b < arrayOfConstant.length; b++) {
      if (arrayOfConstant[b] != null)
        arrayOfConstant[b].accept(this); 
    } 
    this.stack.pop();
  }
  
  public void visitConstantClass(ConstantClass paramConstantClass) {
    this.stack.push(paramConstantClass);
    paramConstantClass.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitConstantDouble(ConstantDouble paramConstantDouble) {
    this.stack.push(paramConstantDouble);
    paramConstantDouble.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitConstantFieldref(ConstantFieldref paramConstantFieldref) {
    this.stack.push(paramConstantFieldref);
    paramConstantFieldref.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitConstantFloat(ConstantFloat paramConstantFloat) {
    this.stack.push(paramConstantFloat);
    paramConstantFloat.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitConstantInteger(ConstantInteger paramConstantInteger) {
    this.stack.push(paramConstantInteger);
    paramConstantInteger.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref paramConstantInterfaceMethodref) {
    this.stack.push(paramConstantInterfaceMethodref);
    paramConstantInterfaceMethodref.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitConstantLong(ConstantLong paramConstantLong) {
    this.stack.push(paramConstantLong);
    paramConstantLong.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitConstantMethodref(ConstantMethodref paramConstantMethodref) {
    this.stack.push(paramConstantMethodref);
    paramConstantMethodref.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitConstantNameAndType(ConstantNameAndType paramConstantNameAndType) {
    this.stack.push(paramConstantNameAndType);
    paramConstantNameAndType.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitConstantString(ConstantString paramConstantString) {
    this.stack.push(paramConstantString);
    paramConstantString.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitConstantUtf8(ConstantUtf8 paramConstantUtf8) {
    this.stack.push(paramConstantUtf8);
    paramConstantUtf8.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitInnerClasses(InnerClasses paramInnerClasses) {
    this.stack.push(paramInnerClasses);
    paramInnerClasses.accept(this.visitor);
    InnerClass[] arrayOfInnerClass = paramInnerClasses.getInnerClasses();
    for (byte b = 0; b < arrayOfInnerClass.length; b++)
      arrayOfInnerClass[b].accept(this); 
    this.stack.pop();
  }
  
  public void visitInnerClass(InnerClass paramInnerClass) {
    this.stack.push(paramInnerClass);
    paramInnerClass.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitDeprecated(Deprecated paramDeprecated) {
    this.stack.push(paramDeprecated);
    paramDeprecated.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitSignature(Signature paramSignature) {
    this.stack.push(paramSignature);
    paramSignature.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitSourceFile(SourceFile paramSourceFile) {
    this.stack.push(paramSourceFile);
    paramSourceFile.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitSynthetic(Synthetic paramSynthetic) {
    this.stack.push(paramSynthetic);
    paramSynthetic.accept(this.visitor);
    this.stack.pop();
  }
  
  public void visitUnknown(Unknown paramUnknown) {
    this.stack.push(paramUnknown);
    paramUnknown.accept(this.visitor);
    this.stack.pop();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\DescendingVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */