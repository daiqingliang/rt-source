package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InstructionList implements Serializable {
  private InstructionHandle start = null;
  
  private InstructionHandle end = null;
  
  private int length = 0;
  
  private int[] byte_positions;
  
  private ArrayList observers;
  
  public InstructionList() {}
  
  public InstructionList(Instruction paramInstruction) { append(paramInstruction); }
  
  public InstructionList(BranchInstruction paramBranchInstruction) { append(paramBranchInstruction); }
  
  public InstructionList(CompoundInstruction paramCompoundInstruction) { append(paramCompoundInstruction.getInstructionList()); }
  
  public boolean isEmpty() { return (this.start == null); }
  
  public static InstructionHandle findHandle(InstructionHandle[] paramArrayOfInstructionHandle, int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = 0;
    int j = paramInt1 - 1;
    do {
      int k = (i + j) / 2;
      int m = paramArrayOfInt[k];
      if (m == paramInt2)
        return paramArrayOfInstructionHandle[k]; 
      if (paramInt2 < m) {
        j = k - 1;
      } else {
        i = k + 1;
      } 
    } while (i <= j);
    return null;
  }
  
  public InstructionHandle findHandle(int paramInt) {
    InstructionHandle[] arrayOfInstructionHandle = getInstructionHandles();
    return findHandle(arrayOfInstructionHandle, this.byte_positions, this.length, paramInt);
  }
  
  public InstructionList(byte[] paramArrayOfByte) {
    ByteSequence byteSequence = new ByteSequence(paramArrayOfByte);
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[paramArrayOfByte.length];
    int[] arrayOfInt = new int[paramArrayOfByte.length];
    byte b1 = 0;
    try {
      while (byteSequence.available() > 0) {
        InstructionHandle instructionHandle;
        int i = byteSequence.getIndex();
        arrayOfInt[b1] = i;
        Instruction instruction = Instruction.readInstruction(byteSequence);
        if (instruction instanceof BranchInstruction) {
          instructionHandle = append((BranchInstruction)instruction);
        } else {
          instructionHandle = append(instruction);
        } 
        instructionHandle.setPosition(i);
        arrayOfInstructionHandle[b1] = instructionHandle;
        b1++;
      } 
    } catch (IOException iOException) {
      throw new ClassGenException(iOException.toString());
    } 
    this.byte_positions = new int[b1];
    System.arraycopy(arrayOfInt, 0, this.byte_positions, 0, b1);
    for (byte b2 = 0; b2 < b1; b2++) {
      if (arrayOfInstructionHandle[b2] instanceof BranchHandle) {
        BranchInstruction branchInstruction = (BranchInstruction)(arrayOfInstructionHandle[b2]).instruction;
        int i = branchInstruction.position + branchInstruction.getIndex();
        InstructionHandle instructionHandle = findHandle(arrayOfInstructionHandle, arrayOfInt, b1, i);
        if (instructionHandle == null)
          throw new ClassGenException("Couldn't find target for branch: " + branchInstruction); 
        branchInstruction.setTarget(instructionHandle);
        if (branchInstruction instanceof Select) {
          Select select = (Select)branchInstruction;
          int[] arrayOfInt1 = select.getIndices();
          for (byte b = 0; b < arrayOfInt1.length; b++) {
            i = branchInstruction.position + arrayOfInt1[b];
            instructionHandle = findHandle(arrayOfInstructionHandle, arrayOfInt, b1, i);
            if (instructionHandle == null)
              throw new ClassGenException("Couldn't find target for switch: " + branchInstruction); 
            select.setTarget(b, instructionHandle);
          } 
        } 
      } 
    } 
  }
  
  public InstructionHandle append(InstructionHandle paramInstructionHandle, InstructionList paramInstructionList) {
    if (paramInstructionList == null)
      throw new ClassGenException("Appending null InstructionList"); 
    if (paramInstructionList.isEmpty())
      return paramInstructionHandle; 
    InstructionHandle instructionHandle1 = paramInstructionHandle.next;
    InstructionHandle instructionHandle2 = paramInstructionList.start;
    paramInstructionHandle.next = paramInstructionList.start;
    paramInstructionList.start.prev = paramInstructionHandle;
    paramInstructionList.end.next = instructionHandle1;
    if (instructionHandle1 != null) {
      instructionHandle1.prev = paramInstructionList.end;
    } else {
      this.end = paramInstructionList.end;
    } 
    this.length += paramInstructionList.length;
    paramInstructionList.clear();
    return instructionHandle2;
  }
  
  public InstructionHandle append(Instruction paramInstruction, InstructionList paramInstructionList) {
    InstructionHandle instructionHandle;
    if ((instructionHandle = findInstruction2(paramInstruction)) == null)
      throw new ClassGenException("Instruction " + paramInstruction + " is not contained in this list."); 
    return append(instructionHandle, paramInstructionList);
  }
  
  public InstructionHandle append(InstructionList paramInstructionList) {
    if (paramInstructionList == null)
      throw new ClassGenException("Appending null InstructionList"); 
    if (paramInstructionList.isEmpty())
      return null; 
    if (isEmpty()) {
      this.start = paramInstructionList.start;
      this.end = paramInstructionList.end;
      this.length = paramInstructionList.length;
      paramInstructionList.clear();
      return this.start;
    } 
    return append(this.end, paramInstructionList);
  }
  
  private void append(InstructionHandle paramInstructionHandle) {
    if (isEmpty()) {
      this.start = this.end = paramInstructionHandle;
      paramInstructionHandle.next = paramInstructionHandle.prev = null;
    } else {
      this.end.next = paramInstructionHandle;
      paramInstructionHandle.prev = this.end;
      paramInstructionHandle.next = null;
      this.end = paramInstructionHandle;
    } 
    this.length++;
  }
  
  public InstructionHandle append(Instruction paramInstruction) {
    InstructionHandle instructionHandle = InstructionHandle.getInstructionHandle(paramInstruction);
    append(instructionHandle);
    return instructionHandle;
  }
  
  public BranchHandle append(BranchInstruction paramBranchInstruction) {
    BranchHandle branchHandle = BranchHandle.getBranchHandle(paramBranchInstruction);
    append(branchHandle);
    return branchHandle;
  }
  
  public InstructionHandle append(Instruction paramInstruction1, Instruction paramInstruction2) { return append(paramInstruction1, new InstructionList(paramInstruction2)); }
  
  public InstructionHandle append(Instruction paramInstruction, CompoundInstruction paramCompoundInstruction) { return append(paramInstruction, paramCompoundInstruction.getInstructionList()); }
  
  public InstructionHandle append(CompoundInstruction paramCompoundInstruction) { return append(paramCompoundInstruction.getInstructionList()); }
  
  public InstructionHandle append(InstructionHandle paramInstructionHandle, CompoundInstruction paramCompoundInstruction) { return append(paramInstructionHandle, paramCompoundInstruction.getInstructionList()); }
  
  public InstructionHandle append(InstructionHandle paramInstructionHandle, Instruction paramInstruction) { return append(paramInstructionHandle, new InstructionList(paramInstruction)); }
  
  public BranchHandle append(InstructionHandle paramInstructionHandle, BranchInstruction paramBranchInstruction) {
    BranchHandle branchHandle = BranchHandle.getBranchHandle(paramBranchInstruction);
    InstructionList instructionList = new InstructionList();
    instructionList.append(branchHandle);
    append(paramInstructionHandle, instructionList);
    return branchHandle;
  }
  
  public InstructionHandle insert(InstructionHandle paramInstructionHandle, InstructionList paramInstructionList) {
    if (paramInstructionList == null)
      throw new ClassGenException("Inserting null InstructionList"); 
    if (paramInstructionList.isEmpty())
      return paramInstructionHandle; 
    InstructionHandle instructionHandle1 = paramInstructionHandle.prev;
    InstructionHandle instructionHandle2 = paramInstructionList.start;
    paramInstructionHandle.prev = paramInstructionList.end;
    paramInstructionList.end.next = paramInstructionHandle;
    paramInstructionList.start.prev = instructionHandle1;
    if (instructionHandle1 != null) {
      instructionHandle1.next = paramInstructionList.start;
    } else {
      this.start = paramInstructionList.start;
    } 
    this.length += paramInstructionList.length;
    paramInstructionList.clear();
    return instructionHandle2;
  }
  
  public InstructionHandle insert(InstructionList paramInstructionList) {
    if (isEmpty()) {
      append(paramInstructionList);
      return this.start;
    } 
    return insert(this.start, paramInstructionList);
  }
  
  private void insert(InstructionHandle paramInstructionHandle) {
    if (isEmpty()) {
      this.start = this.end = paramInstructionHandle;
      paramInstructionHandle.next = paramInstructionHandle.prev = null;
    } else {
      this.start.prev = paramInstructionHandle;
      paramInstructionHandle.next = this.start;
      paramInstructionHandle.prev = null;
      this.start = paramInstructionHandle;
    } 
    this.length++;
  }
  
  public InstructionHandle insert(Instruction paramInstruction, InstructionList paramInstructionList) {
    InstructionHandle instructionHandle;
    if ((instructionHandle = findInstruction1(paramInstruction)) == null)
      throw new ClassGenException("Instruction " + paramInstruction + " is not contained in this list."); 
    return insert(instructionHandle, paramInstructionList);
  }
  
  public InstructionHandle insert(Instruction paramInstruction) {
    InstructionHandle instructionHandle = InstructionHandle.getInstructionHandle(paramInstruction);
    insert(instructionHandle);
    return instructionHandle;
  }
  
  public BranchHandle insert(BranchInstruction paramBranchInstruction) {
    BranchHandle branchHandle = BranchHandle.getBranchHandle(paramBranchInstruction);
    insert(branchHandle);
    return branchHandle;
  }
  
  public InstructionHandle insert(Instruction paramInstruction1, Instruction paramInstruction2) { return insert(paramInstruction1, new InstructionList(paramInstruction2)); }
  
  public InstructionHandle insert(Instruction paramInstruction, CompoundInstruction paramCompoundInstruction) { return insert(paramInstruction, paramCompoundInstruction.getInstructionList()); }
  
  public InstructionHandle insert(CompoundInstruction paramCompoundInstruction) { return insert(paramCompoundInstruction.getInstructionList()); }
  
  public InstructionHandle insert(InstructionHandle paramInstructionHandle, Instruction paramInstruction) { return insert(paramInstructionHandle, new InstructionList(paramInstruction)); }
  
  public InstructionHandle insert(InstructionHandle paramInstructionHandle, CompoundInstruction paramCompoundInstruction) { return insert(paramInstructionHandle, paramCompoundInstruction.getInstructionList()); }
  
  public BranchHandle insert(InstructionHandle paramInstructionHandle, BranchInstruction paramBranchInstruction) {
    BranchHandle branchHandle = BranchHandle.getBranchHandle(paramBranchInstruction);
    InstructionList instructionList = new InstructionList();
    instructionList.append(branchHandle);
    insert(paramInstructionHandle, instructionList);
    return branchHandle;
  }
  
  public void move(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2, InstructionHandle paramInstructionHandle3) {
    if (paramInstructionHandle1 == null || paramInstructionHandle2 == null)
      throw new ClassGenException("Invalid null handle: From " + paramInstructionHandle1 + " to " + paramInstructionHandle2); 
    if (paramInstructionHandle3 == paramInstructionHandle1 || paramInstructionHandle3 == paramInstructionHandle2)
      throw new ClassGenException("Invalid range: From " + paramInstructionHandle1 + " to " + paramInstructionHandle2 + " contains target " + paramInstructionHandle3); 
    InstructionHandle instructionHandle1;
    for (instructionHandle1 = paramInstructionHandle1; instructionHandle1 != paramInstructionHandle2.next; instructionHandle1 = instructionHandle1.next) {
      if (instructionHandle1 == null)
        throw new ClassGenException("Invalid range: From " + paramInstructionHandle1 + " to " + paramInstructionHandle2); 
      if (instructionHandle1 == paramInstructionHandle3)
        throw new ClassGenException("Invalid range: From " + paramInstructionHandle1 + " to " + paramInstructionHandle2 + " contains target " + paramInstructionHandle3); 
    } 
    instructionHandle1 = paramInstructionHandle1.prev;
    InstructionHandle instructionHandle2 = paramInstructionHandle2.next;
    if (instructionHandle1 != null) {
      instructionHandle1.next = instructionHandle2;
    } else {
      this.start = instructionHandle2;
    } 
    if (instructionHandle2 != null) {
      instructionHandle2.prev = instructionHandle1;
    } else {
      this.end = instructionHandle1;
    } 
    paramInstructionHandle1.prev = paramInstructionHandle2.next = null;
    if (paramInstructionHandle3 == null) {
      paramInstructionHandle2.next = this.start;
      this.start = paramInstructionHandle1;
    } else {
      instructionHandle2 = paramInstructionHandle3.next;
      paramInstructionHandle3.next = paramInstructionHandle1;
      paramInstructionHandle1.prev = paramInstructionHandle3;
      paramInstructionHandle2.next = instructionHandle2;
      if (instructionHandle2 != null)
        instructionHandle2.prev = paramInstructionHandle2; 
    } 
  }
  
  public void move(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) { move(paramInstructionHandle1, paramInstructionHandle1, paramInstructionHandle2); }
  
  private void remove(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) {
    InstructionHandle instructionHandle2;
    InstructionHandle instructionHandle1;
    if (paramInstructionHandle1 == null && paramInstructionHandle2 == null) {
      instructionHandle1 = instructionHandle2 = this.start;
      this.start = this.end = null;
    } else {
      if (paramInstructionHandle1 == null) {
        instructionHandle1 = this.start;
        this.start = paramInstructionHandle2;
      } else {
        instructionHandle1 = paramInstructionHandle1.next;
        paramInstructionHandle1.next = paramInstructionHandle2;
      } 
      if (paramInstructionHandle2 == null) {
        instructionHandle2 = this.end;
        this.end = paramInstructionHandle1;
      } else {
        instructionHandle2 = paramInstructionHandle2.prev;
        paramInstructionHandle2.prev = paramInstructionHandle1;
      } 
    } 
    instructionHandle1.prev = null;
    instructionHandle2.next = null;
    ArrayList arrayList = new ArrayList();
    for (InstructionHandle instructionHandle3 = instructionHandle1; instructionHandle3 != null; instructionHandle3 = instructionHandle3.next)
      instructionHandle3.getInstruction().dispose(); 
    StringBuffer stringBuffer = new StringBuffer("{ ");
    for (InstructionHandle instructionHandle4 = instructionHandle1; instructionHandle4 != null; instructionHandle4 = paramInstructionHandle2) {
      paramInstructionHandle2 = instructionHandle4.next;
      this.length--;
      if (instructionHandle4.hasTargeters()) {
        arrayList.add(instructionHandle4);
        stringBuffer.append(instructionHandle4.toString(true) + " ");
        instructionHandle4.next = instructionHandle4.prev = null;
      } else {
        instructionHandle4.dispose();
      } 
    } 
    stringBuffer.append("}");
    if (!arrayList.isEmpty()) {
      InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[arrayList.size()];
      arrayList.toArray(arrayOfInstructionHandle);
      throw new TargetLostException(arrayOfInstructionHandle, stringBuffer.toString());
    } 
  }
  
  public void delete(InstructionHandle paramInstructionHandle) { remove(paramInstructionHandle.prev, paramInstructionHandle.next); }
  
  public void delete(Instruction paramInstruction) {
    InstructionHandle instructionHandle;
    if ((instructionHandle = findInstruction1(paramInstruction)) == null)
      throw new ClassGenException("Instruction " + paramInstruction + " is not contained in this list."); 
    delete(instructionHandle);
  }
  
  public void delete(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) { remove(paramInstructionHandle1.prev, paramInstructionHandle2.next); }
  
  public void delete(Instruction paramInstruction1, Instruction paramInstruction2) throws TargetLostException {
    InstructionHandle instructionHandle1;
    if ((instructionHandle1 = findInstruction1(paramInstruction1)) == null)
      throw new ClassGenException("Instruction " + paramInstruction1 + " is not contained in this list."); 
    InstructionHandle instructionHandle2;
    if ((instructionHandle2 = findInstruction2(paramInstruction2)) == null)
      throw new ClassGenException("Instruction " + paramInstruction2 + " is not contained in this list."); 
    delete(instructionHandle1, instructionHandle2);
  }
  
  private InstructionHandle findInstruction1(Instruction paramInstruction) {
    for (InstructionHandle instructionHandle = this.start; instructionHandle != null; instructionHandle = instructionHandle.next) {
      if (instructionHandle.instruction == paramInstruction)
        return instructionHandle; 
    } 
    return null;
  }
  
  private InstructionHandle findInstruction2(Instruction paramInstruction) {
    for (InstructionHandle instructionHandle = this.end; instructionHandle != null; instructionHandle = instructionHandle.prev) {
      if (instructionHandle.instruction == paramInstruction)
        return instructionHandle; 
    } 
    return null;
  }
  
  public boolean contains(InstructionHandle paramInstructionHandle) {
    if (paramInstructionHandle == null)
      return false; 
    for (InstructionHandle instructionHandle = this.start; instructionHandle != null; instructionHandle = instructionHandle.next) {
      if (instructionHandle == paramInstructionHandle)
        return true; 
    } 
    return false;
  }
  
  public boolean contains(Instruction paramInstruction) { return (findInstruction1(paramInstruction) != null); }
  
  public void setPositions() { setPositions(false); }
  
  public void setPositions(boolean paramBoolean) {
    byte b1 = 0;
    int i = 0;
    int j = 0;
    byte b2 = 0;
    int[] arrayOfInt = new int[this.length];
    if (paramBoolean)
      for (InstructionHandle instructionHandle1 = this.start; instructionHandle1 != null; instructionHandle1 = instructionHandle1.next) {
        Instruction instruction = instructionHandle1.instruction;
        if (instruction instanceof BranchInstruction) {
          Instruction instruction1 = (((BranchInstruction)instruction).getTarget()).instruction;
          if (!contains(instruction1))
            throw new ClassGenException("Branch target of " + Constants.OPCODE_NAMES[instruction.opcode] + ":" + instruction1 + " not in instruction list"); 
          if (instruction instanceof Select) {
            InstructionHandle[] arrayOfInstructionHandle = ((Select)instruction).getTargets();
            for (byte b = 0; b < arrayOfInstructionHandle.length; b++) {
              instruction1 = (arrayOfInstructionHandle[b]).instruction;
              if (!contains(instruction1))
                throw new ClassGenException("Branch target of " + Constants.OPCODE_NAMES[instruction.opcode] + ":" + instruction1 + " not in instruction list"); 
            } 
          } 
          if (!(instructionHandle1 instanceof BranchHandle))
            throw new ClassGenException("Branch instruction " + Constants.OPCODE_NAMES[instruction.opcode] + ":" + instruction1 + " not contained in BranchHandle."); 
        } 
      }  
    InstructionHandle instructionHandle;
    for (instructionHandle = this.start; instructionHandle != null; instructionHandle = instructionHandle.next) {
      Instruction instruction = instructionHandle.instruction;
      instructionHandle.setPosition(j);
      arrayOfInt[b2++] = j;
      switch (instruction.getOpcode()) {
        case 167:
        case 168:
          b1 += true;
          break;
        case 170:
        case 171:
          b1 += true;
          break;
      } 
      j += instruction.getLength();
    } 
    for (instructionHandle = this.start; instructionHandle != null; instructionHandle = instructionHandle.next)
      i += instructionHandle.updatePosition(i, b1); 
    j = b2 = 0;
    for (instructionHandle = this.start; instructionHandle != null; instructionHandle = instructionHandle.next) {
      Instruction instruction = instructionHandle.instruction;
      instructionHandle.setPosition(j);
      arrayOfInt[b2++] = j;
      j += instruction.getLength();
    } 
    this.byte_positions = new int[b2];
    System.arraycopy(arrayOfInt, 0, this.byte_positions, 0, b2);
  }
  
  public byte[] getByteCode() {
    setPositions();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    try {
      for (InstructionHandle instructionHandle = this.start; instructionHandle != null; instructionHandle = instructionHandle.next) {
        Instruction instruction = instructionHandle.instruction;
        instruction.dump(dataOutputStream);
      } 
    } catch (IOException iOException) {
      System.err.println(iOException);
      return null;
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  public Instruction[] getInstructions() {
    ByteSequence byteSequence = new ByteSequence(getByteCode());
    ArrayList arrayList = new ArrayList();
    try {
      while (byteSequence.available() > 0)
        arrayList.add(Instruction.readInstruction(byteSequence)); 
    } catch (IOException iOException) {
      throw new ClassGenException(iOException.toString());
    } 
    Instruction[] arrayOfInstruction = new Instruction[arrayList.size()];
    arrayList.toArray(arrayOfInstruction);
    return arrayOfInstruction;
  }
  
  public String toString() { return toString(true); }
  
  public String toString(boolean paramBoolean) {
    StringBuffer stringBuffer = new StringBuffer();
    for (InstructionHandle instructionHandle = this.start; instructionHandle != null; instructionHandle = instructionHandle.next)
      stringBuffer.append(instructionHandle.toString(paramBoolean) + "\n"); 
    return stringBuffer.toString();
  }
  
  public Iterator iterator() { return new Iterator() {
        private InstructionHandle ih = InstructionList.this.start;
        
        public Object next() {
          InstructionHandle instructionHandle = this.ih;
          this.ih = this.ih.next;
          return instructionHandle;
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
        
        public boolean hasNext() { return (this.ih != null); }
      }; }
  
  public InstructionHandle[] getInstructionHandles() {
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[this.length];
    InstructionHandle instructionHandle = this.start;
    for (byte b = 0; b < this.length; b++) {
      arrayOfInstructionHandle[b] = instructionHandle;
      instructionHandle = instructionHandle.next;
    } 
    return arrayOfInstructionHandle;
  }
  
  public int[] getInstructionPositions() { return this.byte_positions; }
  
  public InstructionList copy() {
    HashMap hashMap = new HashMap();
    InstructionList instructionList = new InstructionList();
    InstructionHandle instructionHandle1;
    for (instructionHandle1 = this.start; instructionHandle1 != null; instructionHandle1 = instructionHandle1.next) {
      Instruction instruction1 = instructionHandle1.instruction;
      Instruction instruction2 = instruction1.copy();
      if (instruction2 instanceof BranchInstruction) {
        hashMap.put(instructionHandle1, instructionList.append((BranchInstruction)instruction2));
      } else {
        hashMap.put(instructionHandle1, instructionList.append(instruction2));
      } 
    } 
    instructionHandle1 = this.start;
    for (InstructionHandle instructionHandle2 = instructionList.start; instructionHandle1 != null; instructionHandle2 = instructionHandle2.next) {
      Instruction instruction1 = instructionHandle1.instruction;
      Instruction instruction2 = instructionHandle2.instruction;
      if (instruction1 instanceof BranchInstruction) {
        BranchInstruction branchInstruction1 = (BranchInstruction)instruction1;
        BranchInstruction branchInstruction2 = (BranchInstruction)instruction2;
        InstructionHandle instructionHandle = branchInstruction1.getTarget();
        branchInstruction2.setTarget((InstructionHandle)hashMap.get(instructionHandle));
        if (branchInstruction1 instanceof Select) {
          InstructionHandle[] arrayOfInstructionHandle1 = ((Select)branchInstruction1).getTargets();
          InstructionHandle[] arrayOfInstructionHandle2 = ((Select)branchInstruction2).getTargets();
          for (byte b = 0; b < arrayOfInstructionHandle1.length; b++)
            arrayOfInstructionHandle2[b] = (InstructionHandle)hashMap.get(arrayOfInstructionHandle1[b]); 
        } 
      } 
      instructionHandle1 = instructionHandle1.next;
    } 
    return instructionList;
  }
  
  public void replaceConstantPool(ConstantPoolGen paramConstantPoolGen1, ConstantPoolGen paramConstantPoolGen2) {
    for (InstructionHandle instructionHandle = this.start; instructionHandle != null; instructionHandle = instructionHandle.next) {
      Instruction instruction = instructionHandle.instruction;
      if (instruction instanceof CPInstruction) {
        CPInstruction cPInstruction = (CPInstruction)instruction;
        Constant constant = paramConstantPoolGen1.getConstant(cPInstruction.getIndex());
        cPInstruction.setIndex(paramConstantPoolGen2.addConstant(constant, paramConstantPoolGen1));
      } 
    } 
  }
  
  private void clear() {
    this.start = this.end = null;
    this.length = 0;
  }
  
  public void dispose() {
    for (InstructionHandle instructionHandle = this.end; instructionHandle != null; instructionHandle = instructionHandle.prev)
      instructionHandle.dispose(); 
    clear();
  }
  
  public InstructionHandle getStart() { return this.start; }
  
  public InstructionHandle getEnd() { return this.end; }
  
  public int getLength() { return this.length; }
  
  public int size() { return this.length; }
  
  public void redirectBranches(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) {
    for (InstructionHandle instructionHandle = this.start; instructionHandle != null; instructionHandle = instructionHandle.next) {
      Instruction instruction = instructionHandle.getInstruction();
      if (instruction instanceof BranchInstruction) {
        BranchInstruction branchInstruction = (BranchInstruction)instruction;
        InstructionHandle instructionHandle1 = branchInstruction.getTarget();
        if (instructionHandle1 == paramInstructionHandle1)
          branchInstruction.setTarget(paramInstructionHandle2); 
        if (branchInstruction instanceof Select) {
          InstructionHandle[] arrayOfInstructionHandle = ((Select)branchInstruction).getTargets();
          for (byte b = 0; b < arrayOfInstructionHandle.length; b++) {
            if (arrayOfInstructionHandle[b] == paramInstructionHandle1)
              ((Select)branchInstruction).setTarget(b, paramInstructionHandle2); 
          } 
        } 
      } 
    } 
  }
  
  public void redirectLocalVariables(LocalVariableGen[] paramArrayOfLocalVariableGen, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) {
    for (byte b = 0; b < paramArrayOfLocalVariableGen.length; b++) {
      InstructionHandle instructionHandle1 = paramArrayOfLocalVariableGen[b].getStart();
      InstructionHandle instructionHandle2 = paramArrayOfLocalVariableGen[b].getEnd();
      if (instructionHandle1 == paramInstructionHandle1)
        paramArrayOfLocalVariableGen[b].setStart(paramInstructionHandle2); 
      if (instructionHandle2 == paramInstructionHandle1)
        paramArrayOfLocalVariableGen[b].setEnd(paramInstructionHandle2); 
    } 
  }
  
  public void redirectExceptionHandlers(CodeExceptionGen[] paramArrayOfCodeExceptionGen, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) {
    for (byte b = 0; b < paramArrayOfCodeExceptionGen.length; b++) {
      if (paramArrayOfCodeExceptionGen[b].getStartPC() == paramInstructionHandle1)
        paramArrayOfCodeExceptionGen[b].setStartPC(paramInstructionHandle2); 
      if (paramArrayOfCodeExceptionGen[b].getEndPC() == paramInstructionHandle1)
        paramArrayOfCodeExceptionGen[b].setEndPC(paramInstructionHandle2); 
      if (paramArrayOfCodeExceptionGen[b].getHandlerPC() == paramInstructionHandle1)
        paramArrayOfCodeExceptionGen[b].setHandlerPC(paramInstructionHandle2); 
    } 
  }
  
  public void addObserver(InstructionListObserver paramInstructionListObserver) {
    if (this.observers == null)
      this.observers = new ArrayList(); 
    this.observers.add(paramInstructionListObserver);
  }
  
  public void removeObserver(InstructionListObserver paramInstructionListObserver) {
    if (this.observers != null)
      this.observers.remove(paramInstructionListObserver); 
  }
  
  public void update() {
    if (this.observers != null) {
      Iterator iterator = this.observers.iterator();
      while (iterator.hasNext())
        ((InstructionListObserver)iterator.next()).notify(this); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\InstructionList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */