package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.classfile.CodeException;
import com.sun.org.apache.bcel.internal.classfile.ExceptionTable;
import com.sun.org.apache.bcel.internal.classfile.LineNumber;
import com.sun.org.apache.bcel.internal.classfile.LineNumberTable;
import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import com.sun.org.apache.bcel.internal.classfile.LocalVariableTable;
import com.sun.org.apache.bcel.internal.classfile.LocalVariableTypeTable;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;

public class MethodGen extends FieldGenOrMethodGen {
  private String class_name;
  
  private Type[] arg_types;
  
  private String[] arg_names;
  
  private int max_locals;
  
  private int max_stack;
  
  private InstructionList il;
  
  private boolean strip_attributes;
  
  private ArrayList variable_vec = new ArrayList();
  
  private ArrayList type_vec = new ArrayList();
  
  private ArrayList line_number_vec = new ArrayList();
  
  private ArrayList exception_vec = new ArrayList();
  
  private ArrayList throws_vec = new ArrayList();
  
  private ArrayList code_attrs_vec = new ArrayList();
  
  private ArrayList observers;
  
  public MethodGen(int paramInt, Type paramType, Type[] paramArrayOfType, String[] paramArrayOfString, String paramString1, String paramString2, InstructionList paramInstructionList, ConstantPoolGen paramConstantPoolGen) {
    setAccessFlags(paramInt);
    setType(paramType);
    setArgumentTypes(paramArrayOfType);
    setArgumentNames(paramArrayOfString);
    setName(paramString1);
    setClassName(paramString2);
    setInstructionList(paramInstructionList);
    setConstantPool(paramConstantPoolGen);
    boolean bool = (isAbstract() || isNative()) ? 1 : 0;
    InstructionHandle instructionHandle1 = null;
    InstructionHandle instructionHandle2 = null;
    if (!bool) {
      instructionHandle1 = paramInstructionList.getStart();
      instructionHandle2 = paramInstructionList.getEnd();
      if (!isStatic() && paramString2 != null)
        addLocalVariable("this", new ObjectType(paramString2), instructionHandle1, instructionHandle2); 
    } 
    if (paramArrayOfType != null) {
      int i = paramArrayOfType.length;
      byte b;
      for (b = 0; b < i; b++) {
        if (Type.VOID == paramArrayOfType[b])
          throw new ClassGenException("'void' is an illegal argument type for a method"); 
      } 
      if (paramArrayOfString != null) {
        if (i != paramArrayOfString.length)
          throw new ClassGenException("Mismatch in argument array lengths: " + i + " vs. " + paramArrayOfString.length); 
      } else {
        paramArrayOfString = new String[i];
        for (b = 0; b < i; b++)
          paramArrayOfString[b] = "arg" + b; 
        setArgumentNames(paramArrayOfString);
      } 
      if (!bool)
        for (b = 0; b < i; b++)
          addLocalVariable(paramArrayOfString[b], paramArrayOfType[b], instructionHandle1, instructionHandle2);  
    } 
  }
  
  public MethodGen(Method paramMethod, String paramString, ConstantPoolGen paramConstantPoolGen) {
    this(paramMethod.getAccessFlags(), Type.getReturnType(paramMethod.getSignature()), Type.getArgumentTypes(paramMethod.getSignature()), null, paramMethod.getName(), paramString, ((paramMethod.getAccessFlags() & 0x500) == 0) ? new InstructionList(paramMethod.getCode().getCode()) : null, paramConstantPoolGen);
    Attribute[] arrayOfAttribute = paramMethod.getAttributes();
    for (byte b = 0; b < arrayOfAttribute.length; b++) {
      Attribute attribute = arrayOfAttribute[b];
      if (attribute instanceof Code) {
        Code code = (Code)attribute;
        setMaxStack(code.getMaxStack());
        setMaxLocals(code.getMaxLocals());
        CodeException[] arrayOfCodeException = code.getExceptionTable();
        if (arrayOfCodeException != null)
          for (byte b2 = 0; b2 < arrayOfCodeException.length; b2++) {
            InstructionHandle instructionHandle;
            CodeException codeException = arrayOfCodeException[b2];
            int i = codeException.getCatchType();
            ObjectType objectType = null;
            if (i > 0) {
              String str = paramMethod.getConstantPool().getConstantString(i, (byte)7);
              objectType = new ObjectType(str);
            } 
            int j = codeException.getEndPC();
            int k = paramMethod.getCode().getCode().length;
            if (k == j) {
              instructionHandle = this.il.getEnd();
            } else {
              instructionHandle = this.il.findHandle(j);
              instructionHandle = instructionHandle.getPrev();
            } 
            addExceptionHandler(this.il.findHandle(codeException.getStartPC()), instructionHandle, this.il.findHandle(codeException.getHandlerPC()), objectType);
          }  
        Attribute[] arrayOfAttribute1 = code.getAttributes();
        for (byte b1 = 0; b1 < arrayOfAttribute1.length; b1++) {
          attribute = arrayOfAttribute1[b1];
          if (attribute instanceof LineNumberTable) {
            LineNumber[] arrayOfLineNumber = ((LineNumberTable)attribute).getLineNumberTable();
            for (byte b2 = 0; b2 < arrayOfLineNumber.length; b2++) {
              LineNumber lineNumber = arrayOfLineNumber[b2];
              addLineNumber(this.il.findHandle(lineNumber.getStartPC()), lineNumber.getLineNumber());
            } 
          } else if (attribute instanceof LocalVariableTable) {
            LocalVariable[] arrayOfLocalVariable = ((LocalVariableTable)attribute).getLocalVariableTable();
            removeLocalVariables();
            for (byte b2 = 0; b2 < arrayOfLocalVariable.length; b2++) {
              LocalVariable localVariable = arrayOfLocalVariable[b2];
              InstructionHandle instructionHandle1 = this.il.findHandle(localVariable.getStartPC());
              InstructionHandle instructionHandle2 = this.il.findHandle(localVariable.getStartPC() + localVariable.getLength());
              if (null == instructionHandle1)
                instructionHandle1 = this.il.getStart(); 
              if (null == instructionHandle2)
                instructionHandle2 = this.il.getEnd(); 
              addLocalVariable(localVariable.getName(), Type.getType(localVariable.getSignature()), localVariable.getIndex(), instructionHandle1, instructionHandle2);
            } 
          } else if (attribute instanceof LocalVariableTypeTable) {
            LocalVariable[] arrayOfLocalVariable = ((LocalVariableTypeTable)attribute).getLocalVariableTypeTable();
            removeLocalVariableTypes();
            for (byte b2 = 0; b2 < arrayOfLocalVariable.length; b2++) {
              LocalVariable localVariable = arrayOfLocalVariable[b2];
              InstructionHandle instructionHandle1 = this.il.findHandle(localVariable.getStartPC());
              InstructionHandle instructionHandle2 = this.il.findHandle(localVariable.getStartPC() + localVariable.getLength());
              if (null == instructionHandle1)
                instructionHandle1 = this.il.getStart(); 
              if (null == instructionHandle2)
                instructionHandle2 = this.il.getEnd(); 
              addLocalVariableType(localVariable.getName(), Type.getType(localVariable.getSignature()), localVariable.getIndex(), instructionHandle1, instructionHandle2);
            } 
          } else {
            addCodeAttribute(attribute);
          } 
        } 
      } else if (attribute instanceof ExceptionTable) {
        String[] arrayOfString = ((ExceptionTable)attribute).getExceptionNames();
        for (byte b1 = 0; b1 < arrayOfString.length; b1++)
          addException(arrayOfString[b1]); 
      } else {
        addAttribute(attribute);
      } 
    } 
  }
  
  public LocalVariableGen addLocalVariable(String paramString, Type paramType, int paramInt, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) {
    byte b = paramType.getType();
    if (b != 16) {
      int i = paramType.getSize();
      if (paramInt + i > this.max_locals)
        this.max_locals = paramInt + i; 
      LocalVariableGen localVariableGen = new LocalVariableGen(paramInt, paramString, paramType, paramInstructionHandle1, paramInstructionHandle2);
      int j;
      if ((j = this.variable_vec.indexOf(localVariableGen)) >= 0) {
        this.variable_vec.set(j, localVariableGen);
      } else {
        this.variable_vec.add(localVariableGen);
      } 
      return localVariableGen;
    } 
    throw new IllegalArgumentException("Can not use " + paramType + " as type for local variable");
  }
  
  public LocalVariableGen addLocalVariable(String paramString, Type paramType, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) { return addLocalVariable(paramString, paramType, this.max_locals, paramInstructionHandle1, paramInstructionHandle2); }
  
  public void removeLocalVariable(LocalVariableGen paramLocalVariableGen) { this.variable_vec.remove(paramLocalVariableGen); }
  
  public void removeLocalVariables() { this.variable_vec.clear(); }
  
  private static final void sort(LocalVariableGen[] paramArrayOfLocalVariableGen, int paramInt1, int paramInt2) {
    int i = paramInt1;
    int j = paramInt2;
    int k = paramArrayOfLocalVariableGen[(paramInt1 + paramInt2) / 2].getIndex();
    do {
      while (paramArrayOfLocalVariableGen[i].getIndex() < k)
        i++; 
      while (k < paramArrayOfLocalVariableGen[j].getIndex())
        j--; 
      if (i > j)
        continue; 
      LocalVariableGen localVariableGen = paramArrayOfLocalVariableGen[i];
      paramArrayOfLocalVariableGen[i] = paramArrayOfLocalVariableGen[j];
      paramArrayOfLocalVariableGen[j] = localVariableGen;
      i++;
      j--;
    } while (i <= j);
    if (paramInt1 < j)
      sort(paramArrayOfLocalVariableGen, paramInt1, j); 
    if (i < paramInt2)
      sort(paramArrayOfLocalVariableGen, i, paramInt2); 
  }
  
  public LocalVariableGen[] getLocalVariables() {
    int i = this.variable_vec.size();
    LocalVariableGen[] arrayOfLocalVariableGen = new LocalVariableGen[i];
    this.variable_vec.toArray(arrayOfLocalVariableGen);
    for (byte b = 0; b < i; b++) {
      if (arrayOfLocalVariableGen[b].getStart() == null)
        arrayOfLocalVariableGen[b].setStart(this.il.getStart()); 
      if (arrayOfLocalVariableGen[b].getEnd() == null)
        arrayOfLocalVariableGen[b].setEnd(this.il.getEnd()); 
    } 
    if (i > 1)
      sort(arrayOfLocalVariableGen, 0, i - 1); 
    return arrayOfLocalVariableGen;
  }
  
  private LocalVariableGen[] getLocalVariableTypes() {
    int i = this.type_vec.size();
    LocalVariableGen[] arrayOfLocalVariableGen = new LocalVariableGen[i];
    this.type_vec.toArray(arrayOfLocalVariableGen);
    for (byte b = 0; b < i; b++) {
      if (arrayOfLocalVariableGen[b].getStart() == null)
        arrayOfLocalVariableGen[b].setStart(this.il.getStart()); 
      if (arrayOfLocalVariableGen[b].getEnd() == null)
        arrayOfLocalVariableGen[b].setEnd(this.il.getEnd()); 
    } 
    if (i > 1)
      sort(arrayOfLocalVariableGen, 0, i - 1); 
    return arrayOfLocalVariableGen;
  }
  
  public LocalVariableTable getLocalVariableTable(ConstantPoolGen paramConstantPoolGen) {
    LocalVariableGen[] arrayOfLocalVariableGen = getLocalVariables();
    int i = arrayOfLocalVariableGen.length;
    LocalVariable[] arrayOfLocalVariable = new LocalVariable[i];
    for (byte b = 0; b < i; b++)
      arrayOfLocalVariable[b] = arrayOfLocalVariableGen[b].getLocalVariable(paramConstantPoolGen); 
    return new LocalVariableTable(paramConstantPoolGen.addUtf8("LocalVariableTable"), 2 + arrayOfLocalVariable.length * 10, arrayOfLocalVariable, paramConstantPoolGen.getConstantPool());
  }
  
  public LocalVariableTypeTable getLocalVariableTypeTable(ConstantPoolGen paramConstantPoolGen) {
    LocalVariableGen[] arrayOfLocalVariableGen = getLocalVariableTypes();
    int i = arrayOfLocalVariableGen.length;
    LocalVariable[] arrayOfLocalVariable = new LocalVariable[i];
    for (byte b = 0; b < i; b++)
      arrayOfLocalVariable[b] = arrayOfLocalVariableGen[b].getLocalVariable(paramConstantPoolGen); 
    return new LocalVariableTypeTable(paramConstantPoolGen.addUtf8("LocalVariableTypeTable"), 2 + arrayOfLocalVariable.length * 10, arrayOfLocalVariable, paramConstantPoolGen.getConstantPool());
  }
  
  private LocalVariableGen addLocalVariableType(String paramString, Type paramType, int paramInt, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) {
    byte b = paramType.getType();
    if (b != 16) {
      int i = paramType.getSize();
      if (paramInt + i > this.max_locals)
        this.max_locals = paramInt + i; 
      LocalVariableGen localVariableGen = new LocalVariableGen(paramInt, paramString, paramType, paramInstructionHandle1, paramInstructionHandle2);
      int j;
      if ((j = this.type_vec.indexOf(localVariableGen)) >= 0) {
        this.type_vec.set(j, localVariableGen);
      } else {
        this.type_vec.add(localVariableGen);
      } 
      return localVariableGen;
    } 
    throw new IllegalArgumentException("Can not use " + paramType + " as type for local variable");
  }
  
  private void removeLocalVariableTypes() { this.type_vec.clear(); }
  
  public LineNumberGen addLineNumber(InstructionHandle paramInstructionHandle, int paramInt) {
    LineNumberGen lineNumberGen = new LineNumberGen(paramInstructionHandle, paramInt);
    this.line_number_vec.add(lineNumberGen);
    return lineNumberGen;
  }
  
  public void removeLineNumber(LineNumberGen paramLineNumberGen) { this.line_number_vec.remove(paramLineNumberGen); }
  
  public void removeLineNumbers() { this.line_number_vec.clear(); }
  
  public LineNumberGen[] getLineNumbers() {
    LineNumberGen[] arrayOfLineNumberGen = new LineNumberGen[this.line_number_vec.size()];
    this.line_number_vec.toArray(arrayOfLineNumberGen);
    return arrayOfLineNumberGen;
  }
  
  public LineNumberTable getLineNumberTable(ConstantPoolGen paramConstantPoolGen) {
    int i = this.line_number_vec.size();
    LineNumber[] arrayOfLineNumber = new LineNumber[i];
    try {
      for (byte b = 0; b < i; b++)
        arrayOfLineNumber[b] = ((LineNumberGen)this.line_number_vec.get(b)).getLineNumber(); 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
    return new LineNumberTable(paramConstantPoolGen.addUtf8("LineNumberTable"), 2 + arrayOfLineNumber.length * 4, arrayOfLineNumber, paramConstantPoolGen.getConstantPool());
  }
  
  public CodeExceptionGen addExceptionHandler(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2, InstructionHandle paramInstructionHandle3, ObjectType paramObjectType) {
    if (paramInstructionHandle1 == null || paramInstructionHandle2 == null || paramInstructionHandle3 == null)
      throw new ClassGenException("Exception handler target is null instruction"); 
    CodeExceptionGen codeExceptionGen = new CodeExceptionGen(paramInstructionHandle1, paramInstructionHandle2, paramInstructionHandle3, paramObjectType);
    this.exception_vec.add(codeExceptionGen);
    return codeExceptionGen;
  }
  
  public void removeExceptionHandler(CodeExceptionGen paramCodeExceptionGen) { this.exception_vec.remove(paramCodeExceptionGen); }
  
  public void removeExceptionHandlers() { this.exception_vec.clear(); }
  
  public CodeExceptionGen[] getExceptionHandlers() {
    CodeExceptionGen[] arrayOfCodeExceptionGen = new CodeExceptionGen[this.exception_vec.size()];
    this.exception_vec.toArray(arrayOfCodeExceptionGen);
    return arrayOfCodeExceptionGen;
  }
  
  private CodeException[] getCodeExceptions() {
    int i = this.exception_vec.size();
    CodeException[] arrayOfCodeException = new CodeException[i];
    try {
      for (byte b = 0; b < i; b++) {
        CodeExceptionGen codeExceptionGen = (CodeExceptionGen)this.exception_vec.get(b);
        arrayOfCodeException[b] = codeExceptionGen.getCodeException(this.cp);
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
    return arrayOfCodeException;
  }
  
  public void addException(String paramString) { this.throws_vec.add(paramString); }
  
  public void removeException(String paramString) { this.throws_vec.remove(paramString); }
  
  public void removeExceptions() { this.throws_vec.clear(); }
  
  public String[] getExceptions() {
    String[] arrayOfString = new String[this.throws_vec.size()];
    this.throws_vec.toArray(arrayOfString);
    return arrayOfString;
  }
  
  private ExceptionTable getExceptionTable(ConstantPoolGen paramConstantPoolGen) {
    int i = this.throws_vec.size();
    int[] arrayOfInt = new int[i];
    try {
      for (byte b = 0; b < i; b++)
        arrayOfInt[b] = paramConstantPoolGen.addClass((String)this.throws_vec.get(b)); 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {}
    return new ExceptionTable(paramConstantPoolGen.addUtf8("Exceptions"), 2 + 2 * i, arrayOfInt, paramConstantPoolGen.getConstantPool());
  }
  
  public void addCodeAttribute(Attribute paramAttribute) { this.code_attrs_vec.add(paramAttribute); }
  
  public void removeCodeAttribute(Attribute paramAttribute) { this.code_attrs_vec.remove(paramAttribute); }
  
  public void removeCodeAttributes() { this.code_attrs_vec.clear(); }
  
  public Attribute[] getCodeAttributes() {
    Attribute[] arrayOfAttribute = new Attribute[this.code_attrs_vec.size()];
    this.code_attrs_vec.toArray(arrayOfAttribute);
    return arrayOfAttribute;
  }
  
  public Method getMethod() {
    String str = getSignature();
    int i = this.cp.addUtf8(this.name);
    int j = this.cp.addUtf8(str);
    byte[] arrayOfByte = null;
    if (this.il != null)
      arrayOfByte = this.il.getByteCode(); 
    LineNumberTable lineNumberTable = null;
    LocalVariableTable localVariableTable = null;
    LocalVariableTypeTable localVariableTypeTable = null;
    if (this.variable_vec.size() > 0 && !this.strip_attributes)
      addCodeAttribute(localVariableTable = getLocalVariableTable(this.cp)); 
    if (this.type_vec.size() > 0 && !this.strip_attributes)
      addCodeAttribute(localVariableTypeTable = getLocalVariableTypeTable(this.cp)); 
    if (this.line_number_vec.size() > 0 && !this.strip_attributes)
      addCodeAttribute(lineNumberTable = getLineNumberTable(this.cp)); 
    Attribute[] arrayOfAttribute = getCodeAttributes();
    int k = 0;
    for (byte b = 0; b < arrayOfAttribute.length; b++)
      k += arrayOfAttribute[b].getLength() + 6; 
    CodeException[] arrayOfCodeException = getCodeExceptions();
    int m = arrayOfCodeException.length * 8;
    Code code = null;
    if (this.il != null && !isAbstract()) {
      Attribute[] arrayOfAttribute1 = getAttributes();
      for (byte b1 = 0; b1 < arrayOfAttribute1.length; b1++) {
        Attribute attribute = arrayOfAttribute1[b1];
        if (attribute instanceof Code)
          removeAttribute(attribute); 
      } 
      code = new Code(this.cp.addUtf8("Code"), 8 + arrayOfByte.length + 2 + m + 2 + k, this.max_stack, this.max_locals, arrayOfByte, arrayOfCodeException, arrayOfAttribute, this.cp.getConstantPool());
      addAttribute(code);
    } 
    ExceptionTable exceptionTable = null;
    if (this.throws_vec.size() > 0)
      addAttribute(exceptionTable = getExceptionTable(this.cp)); 
    Method method = new Method(this.access_flags, i, j, getAttributes(), this.cp.getConstantPool());
    if (localVariableTable != null)
      removeCodeAttribute(localVariableTable); 
    if (localVariableTypeTable != null)
      removeCodeAttribute(localVariableTypeTable); 
    if (lineNumberTable != null)
      removeCodeAttribute(lineNumberTable); 
    if (code != null)
      removeAttribute(code); 
    if (exceptionTable != null)
      removeAttribute(exceptionTable); 
    return method;
  }
  
  public void removeNOPs() {
    if (this.il != null)
      for (InstructionHandle instructionHandle = this.il.getStart(); instructionHandle != null; instructionHandle = instructionHandle1) {
        InstructionHandle instructionHandle1 = instructionHandle.next;
        if (instructionHandle1 != null && instructionHandle.getInstruction() instanceof NOP)
          try {
            this.il.delete(instructionHandle);
          } catch (TargetLostException targetLostException) {
            InstructionHandle[] arrayOfInstructionHandle = targetLostException.getTargets();
            for (byte b = 0; b < arrayOfInstructionHandle.length; b++) {
              InstructionTargeter[] arrayOfInstructionTargeter = arrayOfInstructionHandle[b].getTargeters();
              for (byte b1 = 0; b1 < arrayOfInstructionTargeter.length; b1++)
                arrayOfInstructionTargeter[b1].updateTarget(arrayOfInstructionHandle[b], instructionHandle1); 
            } 
          }  
      }  
  }
  
  public void setMaxLocals(int paramInt) { this.max_locals = paramInt; }
  
  public int getMaxLocals() { return this.max_locals; }
  
  public void setMaxStack(int paramInt) { this.max_stack = paramInt; }
  
  public int getMaxStack() { return this.max_stack; }
  
  public String getClassName() { return this.class_name; }
  
  public void setClassName(String paramString) { this.class_name = paramString; }
  
  public void setReturnType(Type paramType) { setType(paramType); }
  
  public Type getReturnType() { return getType(); }
  
  public void setArgumentTypes(Type[] paramArrayOfType) { this.arg_types = paramArrayOfType; }
  
  public Type[] getArgumentTypes() { return (Type[])this.arg_types.clone(); }
  
  public void setArgumentType(int paramInt, Type paramType) { this.arg_types[paramInt] = paramType; }
  
  public Type getArgumentType(int paramInt) { return this.arg_types[paramInt]; }
  
  public void setArgumentNames(String[] paramArrayOfString) { this.arg_names = paramArrayOfString; }
  
  public String[] getArgumentNames() { return (String[])this.arg_names.clone(); }
  
  public void setArgumentName(int paramInt, String paramString) { this.arg_names[paramInt] = paramString; }
  
  public String getArgumentName(int paramInt) { return this.arg_names[paramInt]; }
  
  public InstructionList getInstructionList() { return this.il; }
  
  public void setInstructionList(InstructionList paramInstructionList) { this.il = paramInstructionList; }
  
  public String getSignature() { return Type.getMethodSignature(this.type, this.arg_types); }
  
  public void setMaxStack() {
    if (this.il != null) {
      this.max_stack = getMaxStack(this.cp, this.il, getExceptionHandlers());
    } else {
      this.max_stack = 0;
    } 
  }
  
  public void setMaxLocals() {
    if (this.il != null) {
      int i = isStatic() ? 0 : 1;
      if (this.arg_types != null)
        for (byte b = 0; b < this.arg_types.length; b++)
          i += this.arg_types[b].getSize();  
      for (InstructionHandle instructionHandle = this.il.getStart(); instructionHandle != null; instructionHandle = instructionHandle.getNext()) {
        Instruction instruction = instructionHandle.getInstruction();
        if (instruction instanceof LocalVariableInstruction || instruction instanceof RET || instruction instanceof IINC) {
          int j = ((IndexedInstruction)instruction).getIndex() + ((TypedInstruction)instruction).getType(this.cp).getSize();
          if (j > i)
            i = j; 
        } 
      } 
      this.max_locals = i;
    } else {
      this.max_locals = 0;
    } 
  }
  
  public void stripAttributes(boolean paramBoolean) { this.strip_attributes = paramBoolean; }
  
  public static int getMaxStack(ConstantPoolGen paramConstantPoolGen, InstructionList paramInstructionList, CodeExceptionGen[] paramArrayOfCodeExceptionGen) {
    BranchStack branchStack = new BranchStack();
    int i;
    for (i = 0; i < paramArrayOfCodeExceptionGen.length; i++) {
      InstructionHandle instructionHandle1 = paramArrayOfCodeExceptionGen[i].getHandlerPC();
      if (instructionHandle1 != null)
        branchStack.push(instructionHandle1, 1); 
    } 
    i = 0;
    int j = 0;
    InstructionHandle instructionHandle = paramInstructionList.getStart();
    while (instructionHandle != null) {
      Instruction instruction = instructionHandle.getInstruction();
      short s = instruction.getOpcode();
      int k = instruction.produceStack(paramConstantPoolGen) - instruction.consumeStack(paramConstantPoolGen);
      i += k;
      if (i > j)
        j = i; 
      if (instruction instanceof BranchInstruction) {
        BranchInstruction branchInstruction = (BranchInstruction)instruction;
        if (instruction instanceof Select) {
          Select select = (Select)branchInstruction;
          InstructionHandle[] arrayOfInstructionHandle = select.getTargets();
          for (byte b = 0; b < arrayOfInstructionHandle.length; b++)
            branchStack.push(arrayOfInstructionHandle[b], i); 
          instructionHandle = null;
        } else if (!(branchInstruction instanceof IfInstruction)) {
          if (s == 168 || s == 201)
            branchStack.push(instructionHandle.getNext(), i - 1); 
          instructionHandle = null;
        } 
        branchStack.push(branchInstruction.getTarget(), i);
      } else if (s == 191 || s == 169 || (s >= 172 && s <= 177)) {
        instructionHandle = null;
      } 
      if (instructionHandle != null)
        instructionHandle = instructionHandle.getNext(); 
      if (instructionHandle == null) {
        BranchTarget branchTarget = branchStack.pop();
        if (branchTarget != null) {
          instructionHandle = branchTarget.target;
          i = branchTarget.stackDepth;
        } 
      } 
    } 
    return j;
  }
  
  public void addObserver(MethodObserver paramMethodObserver) {
    if (this.observers == null)
      this.observers = new ArrayList(); 
    this.observers.add(paramMethodObserver);
  }
  
  public void removeObserver(MethodObserver paramMethodObserver) {
    if (this.observers != null)
      this.observers.remove(paramMethodObserver); 
  }
  
  public void update() {
    if (this.observers != null) {
      Iterator iterator = this.observers.iterator();
      while (iterator.hasNext())
        ((MethodObserver)iterator.next()).notify(this); 
    } 
  }
  
  public final String toString() {
    String str1 = Utility.accessToString(this.access_flags);
    String str2 = Type.getMethodSignature(this.type, this.arg_types);
    str2 = Utility.methodSignatureToString(str2, this.name, str1, true, getLocalVariableTable(this.cp));
    StringBuffer stringBuffer = new StringBuffer(str2);
    if (this.throws_vec.size() > 0) {
      Iterator iterator = this.throws_vec.iterator();
      while (iterator.hasNext())
        stringBuffer.append("\n\t\tthrows " + iterator.next()); 
    } 
    return stringBuffer.toString();
  }
  
  public MethodGen copy(String paramString, ConstantPoolGen paramConstantPoolGen) {
    Method method = ((MethodGen)clone()).getMethod();
    MethodGen methodGen = new MethodGen(method, paramString, this.cp);
    if (this.cp != paramConstantPoolGen) {
      methodGen.setConstantPool(paramConstantPoolGen);
      methodGen.getInstructionList().replaceConstantPool(this.cp, paramConstantPoolGen);
    } 
    return methodGen;
  }
  
  static final class BranchStack {
    Stack branchTargets = new Stack();
    
    Hashtable visitedTargets = new Hashtable();
    
    public void push(InstructionHandle param1InstructionHandle, int param1Int) {
      if (visited(param1InstructionHandle))
        return; 
      this.branchTargets.push(visit(param1InstructionHandle, param1Int));
    }
    
    public MethodGen.BranchTarget pop() { return !this.branchTargets.empty() ? (MethodGen.BranchTarget)this.branchTargets.pop() : null; }
    
    private final MethodGen.BranchTarget visit(InstructionHandle param1InstructionHandle, int param1Int) {
      MethodGen.BranchTarget branchTarget = new MethodGen.BranchTarget(param1InstructionHandle, param1Int);
      this.visitedTargets.put(param1InstructionHandle, branchTarget);
      return branchTarget;
    }
    
    private final boolean visited(InstructionHandle param1InstructionHandle) { return (this.visitedTargets.get(param1InstructionHandle) != null); }
  }
  
  static final class BranchTarget {
    InstructionHandle target;
    
    int stackDepth;
    
    BranchTarget(InstructionHandle param1InstructionHandle, int param1Int) {
      this.target = param1InstructionHandle;
      this.stackDepth = param1Int;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\MethodGen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */