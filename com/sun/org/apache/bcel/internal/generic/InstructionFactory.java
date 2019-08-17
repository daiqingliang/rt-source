package com.sun.org.apache.bcel.internal.generic;

import java.io.Serializable;

public class InstructionFactory implements InstructionConstants, Serializable {
  protected ClassGen cg;
  
  protected ConstantPoolGen cp;
  
  private static MethodObject[] append_mos = { 
      new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.STRING }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.OBJECT }, 1), null, null, new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.BOOLEAN }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.CHAR }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.FLOAT }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.DOUBLE }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.INT }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.INT }, 1), 
      new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.INT }, 1), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[] { Type.LONG }, 1) };
  
  public InstructionFactory(ClassGen paramClassGen, ConstantPoolGen paramConstantPoolGen) {
    this.cg = paramClassGen;
    this.cp = paramConstantPoolGen;
  }
  
  public InstructionFactory(ClassGen paramClassGen) { this(paramClassGen, paramClassGen.getConstantPool()); }
  
  public InstructionFactory(ConstantPoolGen paramConstantPoolGen) { this(null, paramConstantPoolGen); }
  
  public InvokeInstruction createInvoke(String paramString1, String paramString2, Type paramType, Type[] paramArrayOfType, short paramShort) {
    int i;
    int j = 0;
    String str = Type.getMethodSignature(paramType, paramArrayOfType);
    for (byte b = 0; b < paramArrayOfType.length; b++)
      j += paramArrayOfType[b].getSize(); 
    if (paramShort == 185) {
      i = this.cp.addInterfaceMethodref(paramString1, paramString2, str);
    } else {
      i = this.cp.addMethodref(paramString1, paramString2, str);
    } 
    switch (paramShort) {
      case 183:
        return new INVOKESPECIAL(i);
      case 182:
        return new INVOKEVIRTUAL(i);
      case 184:
        return new INVOKESTATIC(i);
      case 185:
        return new INVOKEINTERFACE(i, j + 1);
    } 
    throw new RuntimeException("Oops: Unknown invoke kind:" + paramShort);
  }
  
  public InstructionList createPrintln(String paramString) {
    InstructionList instructionList = new InstructionList();
    int i = this.cp.addFieldref("java.lang.System", "out", "Ljava/io/PrintStream;");
    int j = this.cp.addMethodref("java.io.PrintStream", "println", "(Ljava/lang/String;)V");
    instructionList.append(new GETSTATIC(i));
    instructionList.append(new PUSH(this.cp, paramString));
    instructionList.append(new INVOKEVIRTUAL(j));
    return instructionList;
  }
  
  public Instruction createConstant(Object paramObject) {
    PUSH pUSH;
    if (paramObject instanceof Number) {
      pUSH = new PUSH(this.cp, (Number)paramObject);
    } else if (paramObject instanceof String) {
      pUSH = new PUSH(this.cp, (String)paramObject);
    } else if (paramObject instanceof Boolean) {
      pUSH = new PUSH(this.cp, (Boolean)paramObject);
    } else if (paramObject instanceof Character) {
      pUSH = new PUSH(this.cp, (Character)paramObject);
    } else {
      throw new ClassGenException("Illegal type: " + paramObject.getClass());
    } 
    return pUSH.getInstruction();
  }
  
  private InvokeInstruction createInvoke(MethodObject paramMethodObject, short paramShort) { return createInvoke(paramMethodObject.class_name, paramMethodObject.name, paramMethodObject.result_type, paramMethodObject.arg_types, paramShort); }
  
  private static final boolean isString(Type paramType) { return (paramType instanceof ObjectType && ((ObjectType)paramType).getClassName().equals("java.lang.String")); }
  
  public Instruction createAppend(Type paramType) {
    byte b = paramType.getType();
    if (isString(paramType))
      return createInvoke(append_mos[0], (short)182); 
    switch (b) {
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
        return createInvoke(append_mos[b], (short)182);
      case 13:
      case 14:
        return createInvoke(append_mos[1], (short)182);
    } 
    throw new RuntimeException("Oops: No append for this type? " + paramType);
  }
  
  public FieldInstruction createFieldAccess(String paramString1, String paramString2, Type paramType, short paramShort) {
    String str = paramType.getSignature();
    int i = this.cp.addFieldref(paramString1, paramString2, str);
    switch (paramShort) {
      case 180:
        return new GETFIELD(i);
      case 181:
        return new PUTFIELD(i);
      case 178:
        return new GETSTATIC(i);
      case 179:
        return new PUTSTATIC(i);
    } 
    throw new RuntimeException("Oops: Unknown getfield kind:" + paramShort);
  }
  
  public static Instruction createThis() { return new ALOAD(0); }
  
  public static ReturnInstruction createReturn(Type paramType) {
    switch (paramType.getType()) {
      case 13:
      case 14:
        return ARETURN;
      case 4:
      case 5:
      case 8:
      case 9:
      case 10:
        return IRETURN;
      case 6:
        return FRETURN;
      case 7:
        return DRETURN;
      case 11:
        return LRETURN;
      case 12:
        return RETURN;
    } 
    throw new RuntimeException("Invalid type: " + paramType);
  }
  
  private static final ArithmeticInstruction createBinaryIntOp(char paramChar, String paramString) {
    switch (paramChar) {
      case '-':
        return ISUB;
      case '+':
        return IADD;
      case '%':
        return IREM;
      case '*':
        return IMUL;
      case '/':
        return IDIV;
      case '&':
        return IAND;
      case '|':
        return IOR;
      case '^':
        return IXOR;
      case '<':
        return ISHL;
      case '>':
        return paramString.equals(">>>") ? IUSHR : ISHR;
    } 
    throw new RuntimeException("Invalid operand " + paramString);
  }
  
  private static final ArithmeticInstruction createBinaryLongOp(char paramChar, String paramString) {
    switch (paramChar) {
      case '-':
        return LSUB;
      case '+':
        return LADD;
      case '%':
        return LREM;
      case '*':
        return LMUL;
      case '/':
        return LDIV;
      case '&':
        return LAND;
      case '|':
        return LOR;
      case '^':
        return LXOR;
      case '<':
        return LSHL;
      case '>':
        return paramString.equals(">>>") ? LUSHR : LSHR;
    } 
    throw new RuntimeException("Invalid operand " + paramString);
  }
  
  private static final ArithmeticInstruction createBinaryFloatOp(char paramChar) {
    switch (paramChar) {
      case '-':
        return FSUB;
      case '+':
        return FADD;
      case '*':
        return FMUL;
      case '/':
        return FDIV;
    } 
    throw new RuntimeException("Invalid operand " + paramChar);
  }
  
  private static final ArithmeticInstruction createBinaryDoubleOp(char paramChar) {
    switch (paramChar) {
      case '-':
        return DSUB;
      case '+':
        return DADD;
      case '*':
        return DMUL;
      case '/':
        return DDIV;
    } 
    throw new RuntimeException("Invalid operand " + paramChar);
  }
  
  public static ArithmeticInstruction createBinaryOperation(String paramString, Type paramType) {
    char c = paramString.toCharArray()[0];
    switch (paramType.getType()) {
      case 5:
      case 8:
      case 9:
      case 10:
        return createBinaryIntOp(c, paramString);
      case 11:
        return createBinaryLongOp(c, paramString);
      case 6:
        return createBinaryFloatOp(c);
      case 7:
        return createBinaryDoubleOp(c);
    } 
    throw new RuntimeException("Invalid type " + paramType);
  }
  
  public static StackInstruction createPop(int paramInt) { return (paramInt == 2) ? POP2 : POP; }
  
  public static StackInstruction createDup(int paramInt) { return (paramInt == 2) ? DUP2 : DUP; }
  
  public static StackInstruction createDup_2(int paramInt) { return (paramInt == 2) ? DUP2_X2 : DUP_X2; }
  
  public static StackInstruction createDup_1(int paramInt) { return (paramInt == 2) ? DUP2_X1 : DUP_X1; }
  
  public static LocalVariableInstruction createStore(Type paramType, int paramInt) {
    switch (paramType.getType()) {
      case 4:
      case 5:
      case 8:
      case 9:
      case 10:
        return new ISTORE(paramInt);
      case 6:
        return new FSTORE(paramInt);
      case 7:
        return new DSTORE(paramInt);
      case 11:
        return new LSTORE(paramInt);
      case 13:
      case 14:
        return new ASTORE(paramInt);
    } 
    throw new RuntimeException("Invalid type " + paramType);
  }
  
  public static LocalVariableInstruction createLoad(Type paramType, int paramInt) {
    switch (paramType.getType()) {
      case 4:
      case 5:
      case 8:
      case 9:
      case 10:
        return new ILOAD(paramInt);
      case 6:
        return new FLOAD(paramInt);
      case 7:
        return new DLOAD(paramInt);
      case 11:
        return new LLOAD(paramInt);
      case 13:
      case 14:
        return new ALOAD(paramInt);
    } 
    throw new RuntimeException("Invalid type " + paramType);
  }
  
  public static ArrayInstruction createArrayLoad(Type paramType) {
    switch (paramType.getType()) {
      case 4:
      case 8:
        return BALOAD;
      case 5:
        return CALOAD;
      case 9:
        return SALOAD;
      case 10:
        return IALOAD;
      case 6:
        return FALOAD;
      case 7:
        return DALOAD;
      case 11:
        return LALOAD;
      case 13:
      case 14:
        return AALOAD;
    } 
    throw new RuntimeException("Invalid type " + paramType);
  }
  
  public static ArrayInstruction createArrayStore(Type paramType) {
    switch (paramType.getType()) {
      case 4:
      case 8:
        return BASTORE;
      case 5:
        return CASTORE;
      case 9:
        return SASTORE;
      case 10:
        return IASTORE;
      case 6:
        return FASTORE;
      case 7:
        return DASTORE;
      case 11:
        return LASTORE;
      case 13:
      case 14:
        return AASTORE;
    } 
    throw new RuntimeException("Invalid type " + paramType);
  }
  
  public Instruction createCast(Type paramType1, Type paramType2) {
    if (paramType1 instanceof BasicType && paramType2 instanceof BasicType) {
      byte b1 = paramType2.getType();
      byte b2 = paramType1.getType();
      if (b1 == 11 && (b2 == 5 || b2 == 8 || b2 == 9))
        b2 = 10; 
      String[] arrayOfString = { "C", "F", "D", "B", "S", "I", "L" };
      String str = "com.sun.org.apache.bcel.internal.generic." + arrayOfString[b2 - 5] + "2" + arrayOfString[b1 - 5];
      Instruction instruction = null;
      try {
        instruction = (Instruction)Class.forName(str).newInstance();
      } catch (Exception exception) {
        throw new RuntimeException("Could not find instruction: " + str);
      } 
      return instruction;
    } 
    if (paramType1 instanceof ReferenceType && paramType2 instanceof ReferenceType)
      return (paramType2 instanceof ArrayType) ? new CHECKCAST(this.cp.addArrayClass((ArrayType)paramType2)) : new CHECKCAST(this.cp.addClass(((ObjectType)paramType2).getClassName())); 
    throw new RuntimeException("Can not cast " + paramType1 + " to " + paramType2);
  }
  
  public GETFIELD createGetField(String paramString1, String paramString2, Type paramType) { return new GETFIELD(this.cp.addFieldref(paramString1, paramString2, paramType.getSignature())); }
  
  public GETSTATIC createGetStatic(String paramString1, String paramString2, Type paramType) { return new GETSTATIC(this.cp.addFieldref(paramString1, paramString2, paramType.getSignature())); }
  
  public PUTFIELD createPutField(String paramString1, String paramString2, Type paramType) { return new PUTFIELD(this.cp.addFieldref(paramString1, paramString2, paramType.getSignature())); }
  
  public PUTSTATIC createPutStatic(String paramString1, String paramString2, Type paramType) { return new PUTSTATIC(this.cp.addFieldref(paramString1, paramString2, paramType.getSignature())); }
  
  public CHECKCAST createCheckCast(ReferenceType paramReferenceType) { return (paramReferenceType instanceof ArrayType) ? new CHECKCAST(this.cp.addArrayClass((ArrayType)paramReferenceType)) : new CHECKCAST(this.cp.addClass((ObjectType)paramReferenceType)); }
  
  public INSTANCEOF createInstanceOf(ReferenceType paramReferenceType) { return (paramReferenceType instanceof ArrayType) ? new INSTANCEOF(this.cp.addArrayClass((ArrayType)paramReferenceType)) : new INSTANCEOF(this.cp.addClass((ObjectType)paramReferenceType)); }
  
  public NEW createNew(ObjectType paramObjectType) { return new NEW(this.cp.addClass(paramObjectType)); }
  
  public NEW createNew(String paramString) { return createNew(new ObjectType(paramString)); }
  
  public Instruction createNewArray(Type paramType, short paramShort) {
    ArrayType arrayType;
    if (paramShort == 1)
      return (paramType instanceof ObjectType) ? new ANEWARRAY(this.cp.addClass((ObjectType)paramType)) : ((paramType instanceof ArrayType) ? new ANEWARRAY(this.cp.addArrayClass((ArrayType)paramType)) : new NEWARRAY(((BasicType)paramType).getType())); 
    if (paramType instanceof ArrayType) {
      arrayType = (ArrayType)paramType;
    } else {
      arrayType = new ArrayType(paramType, paramShort);
    } 
    return new MULTIANEWARRAY(this.cp.addArrayClass(arrayType), paramShort);
  }
  
  public static Instruction createNull(Type paramType) {
    switch (paramType.getType()) {
      case 13:
      case 14:
        return ACONST_NULL;
      case 4:
      case 5:
      case 8:
      case 9:
      case 10:
        return ICONST_0;
      case 6:
        return FCONST_0;
      case 7:
        return DCONST_0;
      case 11:
        return LCONST_0;
      case 12:
        return NOP;
    } 
    throw new RuntimeException("Invalid type: " + paramType);
  }
  
  public static BranchInstruction createBranchInstruction(short paramShort, InstructionHandle paramInstructionHandle) {
    switch (paramShort) {
      case 153:
        return new IFEQ(paramInstructionHandle);
      case 154:
        return new IFNE(paramInstructionHandle);
      case 155:
        return new IFLT(paramInstructionHandle);
      case 156:
        return new IFGE(paramInstructionHandle);
      case 157:
        return new IFGT(paramInstructionHandle);
      case 158:
        return new IFLE(paramInstructionHandle);
      case 159:
        return new IF_ICMPEQ(paramInstructionHandle);
      case 160:
        return new IF_ICMPNE(paramInstructionHandle);
      case 161:
        return new IF_ICMPLT(paramInstructionHandle);
      case 162:
        return new IF_ICMPGE(paramInstructionHandle);
      case 163:
        return new IF_ICMPGT(paramInstructionHandle);
      case 164:
        return new IF_ICMPLE(paramInstructionHandle);
      case 165:
        return new IF_ACMPEQ(paramInstructionHandle);
      case 166:
        return new IF_ACMPNE(paramInstructionHandle);
      case 167:
        return new GOTO(paramInstructionHandle);
      case 168:
        return new JSR(paramInstructionHandle);
      case 198:
        return new IFNULL(paramInstructionHandle);
      case 199:
        return new IFNONNULL(paramInstructionHandle);
      case 200:
        return new GOTO_W(paramInstructionHandle);
      case 201:
        return new JSR_W(paramInstructionHandle);
    } 
    throw new RuntimeException("Invalid opcode: " + paramShort);
  }
  
  public void setClassGen(ClassGen paramClassGen) { this.cg = paramClassGen; }
  
  public ClassGen getClassGen() { return this.cg; }
  
  public void setConstantPool(ConstantPoolGen paramConstantPoolGen) { this.cp = paramConstantPoolGen; }
  
  public ConstantPoolGen getConstantPool() { return this.cp; }
  
  private static class MethodObject {
    Type[] arg_types;
    
    Type result_type;
    
    String[] arg_names;
    
    String class_name;
    
    String name;
    
    int access;
    
    MethodObject(String param1String1, String param1String2, Type param1Type, Type[] param1ArrayOfType, int param1Int) {
      this.class_name = param1String1;
      this.name = param1String2;
      this.result_type = param1Type;
      this.arg_types = param1ArrayOfType;
      this.access = param1Int;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\InstructionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */