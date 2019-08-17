package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFNULL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;

public final class ObjectType extends Type {
  private String _javaClassName = "java.lang.Object";
  
  private Class _clazz = Object.class;
  
  protected ObjectType(String paramString) {
    this._javaClassName = paramString;
    try {
      this._clazz = ObjectFactory.findProviderClass(paramString, true);
    } catch (ClassNotFoundException classNotFoundException) {
      this._clazz = null;
    } 
  }
  
  protected ObjectType(Class paramClass) {
    this._clazz = paramClass;
    this._javaClassName = paramClass.getName();
  }
  
  public int hashCode() { return Object.class.hashCode(); }
  
  public boolean equals(Object paramObject) { return paramObject instanceof ObjectType; }
  
  public String getJavaClassName() { return this._javaClassName; }
  
  public Class getJavaClass() { return this._clazz; }
  
  public String toString() { return this._javaClassName; }
  
  public boolean identicalTo(Type paramType) { return (this == paramType); }
  
  public String toSignature() {
    StringBuffer stringBuffer = new StringBuffer("L");
    stringBuffer.append(this._javaClassName.replace('.', '/')).append(';');
    return stringBuffer.toString();
  }
  
  public Type toJCType() { return Util.getJCRefType(toSignature()); }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType) {
    if (paramType == Type.String) {
      translateTo(paramClassGenerator, paramMethodGenerator, (StringType)paramType);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramType.toString());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, StringType paramStringType) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(DUP);
    BranchHandle branchHandle1 = instructionList.append(new IFNULL(null));
    instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref(this._javaClassName, "toString", "()Ljava/lang/String;")));
    BranchHandle branchHandle2 = instructionList.append(new GOTO(null));
    branchHandle1.setTarget(instructionList.append(POP));
    instructionList.append(new PUSH(constantPoolGen, ""));
    branchHandle2.setTarget(instructionList.append(NOP));
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) {
    if (paramClass.isAssignableFrom(this._clazz)) {
      paramMethodGenerator.getInstructionList().append(NOP);
    } else {
      ErrorMsg errorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getClass().toString());
      paramClassGenerator.getParser().reportError(2, errorMsg);
    } 
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass) { paramMethodGenerator.getInstructionList().append(NOP); }
  
  public Instruction LOAD(int paramInt) { return new ALOAD(paramInt); }
  
  public Instruction STORE(int paramInt) { return new ASTORE(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\ObjectType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */