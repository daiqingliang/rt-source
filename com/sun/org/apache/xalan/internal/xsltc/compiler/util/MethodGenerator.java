package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.DSTORE;
import com.sun.org.apache.bcel.internal.generic.FLOAD;
import com.sun.org.apache.bcel.internal.generic.FSTORE;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.IfInstruction;
import com.sun.org.apache.bcel.internal.generic.IndexedInstruction;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.InstructionTargeter;
import com.sun.org.apache.bcel.internal.generic.LLOAD;
import com.sun.org.apache.bcel.internal.generic.LSTORE;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.Select;
import com.sun.org.apache.bcel.internal.generic.TargetLostException;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public class MethodGenerator extends MethodGen implements Constants {
  protected static final int INVALID_INDEX = -1;
  
  private static final String START_ELEMENT_SIG = "(Ljava/lang/String;)V";
  
  private static final String END_ELEMENT_SIG = "(Ljava/lang/String;)V";
  
  private InstructionList _mapTypeSub;
  
  private static final int DOM_INDEX = 1;
  
  private static final int ITERATOR_INDEX = 2;
  
  private static final int HANDLER_INDEX = 3;
  
  private static final int MAX_METHOD_SIZE = 65535;
  
  private static final int MAX_BRANCH_TARGET_OFFSET = 32767;
  
  private static final int MIN_BRANCH_TARGET_OFFSET = -32768;
  
  private static final int TARGET_METHOD_SIZE = 60000;
  
  private static final int MINIMUM_OUTLINEABLE_CHUNK_SIZE = 1000;
  
  private Instruction _iloadCurrent;
  
  private Instruction _istoreCurrent;
  
  private final Instruction _astoreHandler = new ASTORE(3);
  
  private final Instruction _aloadHandler = new ALOAD(3);
  
  private final Instruction _astoreIterator = new ASTORE(2);
  
  private final Instruction _aloadIterator = new ALOAD(2);
  
  private final Instruction _aloadDom = new ALOAD(1);
  
  private final Instruction _astoreDom = new ASTORE(1);
  
  private final Instruction _startElement;
  
  private final Instruction _endElement;
  
  private final Instruction _startDocument;
  
  private final Instruction _endDocument;
  
  private final Instruction _attribute;
  
  private final Instruction _uniqueAttribute;
  
  private final Instruction _namespace;
  
  private final Instruction _setStartNode;
  
  private final Instruction _reset;
  
  private final Instruction _nextNode;
  
  private SlotAllocator _slotAllocator;
  
  private boolean _allocatorInit = false;
  
  private LocalVariableRegistry _localVariableRegistry;
  
  private Map<Pattern, InstructionList> _preCompiled = new HashMap();
  
  private int m_totalChunks = 0;
  
  private int m_openChunks = 0;
  
  public MethodGenerator(int paramInt, Type paramType, Type[] paramArrayOfType, String[] paramArrayOfString, String paramString1, String paramString2, InstructionList paramInstructionList, ConstantPoolGen paramConstantPoolGen) {
    super(paramInt, paramType, paramArrayOfType, paramArrayOfString, paramString1, paramString2, paramInstructionList, paramConstantPoolGen);
    int i = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startElement", "(Ljava/lang/String;)V");
    this._startElement = new INVOKEINTERFACE(i, 2);
    int j = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endElement", "(Ljava/lang/String;)V");
    this._endElement = new INVOKEINTERFACE(j, 2);
    int k = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "addAttribute", "(Ljava/lang/String;Ljava/lang/String;)V");
    this._attribute = new INVOKEINTERFACE(k, 3);
    int m = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "addUniqueAttribute", "(Ljava/lang/String;Ljava/lang/String;I)V");
    this._uniqueAttribute = new INVOKEINTERFACE(m, 4);
    int n = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "namespaceAfterStartElement", "(Ljava/lang/String;Ljava/lang/String;)V");
    this._namespace = new INVOKEINTERFACE(n, 3);
    int i1 = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startDocument", "()V");
    this._startDocument = new INVOKEINTERFACE(i1, 1);
    i1 = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endDocument", "()V");
    this._endDocument = new INVOKEINTERFACE(i1, 1);
    i1 = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "setStartNode", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    this._setStartNode = new INVOKEINTERFACE(i1, 2);
    i1 = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "reset", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    this._reset = new INVOKEINTERFACE(i1, 1);
    i1 = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "next", "()I");
    this._nextNode = new INVOKEINTERFACE(i1, 1);
    this._slotAllocator = new SlotAllocator();
    this._slotAllocator.initialize(getLocalVariableRegistry().getLocals(false));
    this._allocatorInit = true;
  }
  
  public LocalVariableGen addLocalVariable(String paramString, Type paramType, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2) {
    LocalVariableGen localVariableGen;
    if (this._allocatorInit) {
      localVariableGen = addLocalVariable2(paramString, paramType, paramInstructionHandle1);
    } else {
      localVariableGen = super.addLocalVariable(paramString, paramType, paramInstructionHandle1, paramInstructionHandle2);
      getLocalVariableRegistry().registerLocalVariable(localVariableGen);
    } 
    return localVariableGen;
  }
  
  public LocalVariableGen addLocalVariable2(String paramString, Type paramType, InstructionHandle paramInstructionHandle) {
    LocalVariableGen localVariableGen = addLocalVariable(paramString, paramType, this._slotAllocator.allocateSlot(paramType), paramInstructionHandle, null);
    getLocalVariableRegistry().registerLocalVariable(localVariableGen);
    return localVariableGen;
  }
  
  private LocalVariableRegistry getLocalVariableRegistry() {
    if (this._localVariableRegistry == null)
      this._localVariableRegistry = new LocalVariableRegistry(); 
    return this._localVariableRegistry;
  }
  
  boolean offsetInLocalVariableGenRange(LocalVariableGen paramLocalVariableGen, int paramInt) {
    InstructionHandle instructionHandle1 = paramLocalVariableGen.getStart();
    InstructionHandle instructionHandle2 = paramLocalVariableGen.getEnd();
    if (instructionHandle1 == null)
      instructionHandle1 = getInstructionList().getStart(); 
    if (instructionHandle2 == null)
      instructionHandle2 = getInstructionList().getEnd(); 
    return (instructionHandle1.getPosition() <= paramInt && instructionHandle2.getPosition() + instructionHandle2.getInstruction().getLength() >= paramInt);
  }
  
  public void removeLocalVariable(LocalVariableGen paramLocalVariableGen) {
    this._slotAllocator.releaseSlot(paramLocalVariableGen);
    getLocalVariableRegistry().removeByNameTracking(paramLocalVariableGen);
    super.removeLocalVariable(paramLocalVariableGen);
  }
  
  public Instruction loadDOM() { return this._aloadDom; }
  
  public Instruction storeDOM() { return this._astoreDom; }
  
  public Instruction storeHandler() { return this._astoreHandler; }
  
  public Instruction loadHandler() { return this._aloadHandler; }
  
  public Instruction storeIterator() { return this._astoreIterator; }
  
  public Instruction loadIterator() { return this._aloadIterator; }
  
  public final Instruction setStartNode() { return this._setStartNode; }
  
  public final Instruction reset() { return this._reset; }
  
  public final Instruction nextNode() { return this._nextNode; }
  
  public final Instruction startElement() { return this._startElement; }
  
  public final Instruction endElement() { return this._endElement; }
  
  public final Instruction startDocument() { return this._startDocument; }
  
  public final Instruction endDocument() { return this._endDocument; }
  
  public final Instruction attribute() { return this._attribute; }
  
  public final Instruction uniqueAttribute() { return this._uniqueAttribute; }
  
  public final Instruction namespace() { return this._namespace; }
  
  public Instruction loadCurrentNode() {
    if (this._iloadCurrent == null) {
      int i = getLocalIndex("current");
      if (i > 0) {
        this._iloadCurrent = new ILOAD(i);
      } else {
        this._iloadCurrent = new ICONST(0);
      } 
    } 
    return this._iloadCurrent;
  }
  
  public Instruction storeCurrentNode() { return (this._istoreCurrent != null) ? this._istoreCurrent : (this._istoreCurrent = new ISTORE(getLocalIndex("current"))); }
  
  public Instruction loadContextNode() { return loadCurrentNode(); }
  
  public Instruction storeContextNode() { return storeCurrentNode(); }
  
  public int getLocalIndex(String paramString) { return getLocalVariable(paramString).getIndex(); }
  
  public LocalVariableGen getLocalVariable(String paramString) { return getLocalVariableRegistry().lookUpByName(paramString); }
  
  public void setMaxLocals() {
    int i = getMaxLocals();
    int j = i;
    LocalVariableGen[] arrayOfLocalVariableGen = getLocalVariables();
    if (arrayOfLocalVariableGen != null && arrayOfLocalVariableGen.length > i)
      i = arrayOfLocalVariableGen.length; 
    if (i < 5)
      i = 5; 
    setMaxLocals(i);
  }
  
  public void addInstructionList(Pattern paramPattern, InstructionList paramInstructionList) { this._preCompiled.put(paramPattern, paramInstructionList); }
  
  public InstructionList getInstructionList(Pattern paramPattern) { return (InstructionList)this._preCompiled.get(paramPattern); }
  
  private ArrayList getCandidateChunks(ClassGenerator paramClassGenerator, int paramInt) {
    InstructionHandle instructionHandle;
    Iterator iterator = getInstructionList().iterator();
    ArrayList arrayList1 = new ArrayList();
    ArrayList arrayList2 = new ArrayList();
    Stack stack = new Stack();
    boolean bool1 = false;
    boolean bool2 = true;
    if (this.m_openChunks != 0) {
      String str = (new ErrorMsg("OUTLINE_ERR_UNBALANCED_MARKERS")).toString();
      throw new InternalError(str);
    } 
    do {
      instructionHandle = iterator.hasNext() ? (InstructionHandle)iterator.next() : null;
      Instruction instruction = (instructionHandle != null) ? instructionHandle.getInstruction() : null;
      if (bool2) {
        bool1 = true;
        arrayList2.add(instructionHandle);
        bool2 = false;
      } 
      if (instruction instanceof OutlineableChunkStart) {
        if (bool1) {
          stack.push(arrayList2);
          arrayList2 = new ArrayList();
        } 
        bool1 = true;
        arrayList2.add(instructionHandle);
      } else if (instructionHandle == null || instruction instanceof OutlineableChunkEnd) {
        ArrayList arrayList = null;
        if (!bool1) {
          arrayList = arrayList2;
          arrayList2 = (ArrayList)stack.pop();
        } 
        InstructionHandle instructionHandle1 = (InstructionHandle)arrayList2.get(arrayList2.size() - 1);
        int i = (instructionHandle != null) ? instructionHandle.getPosition() : paramInt;
        int j = i - instructionHandle1.getPosition();
        if (j <= 60000) {
          arrayList2.add(instructionHandle);
        } else {
          if (!bool1) {
            int k = arrayList.size() / 2;
            if (k > 0) {
              Chunk[] arrayOfChunk = new Chunk[k];
              for (byte b1 = 0; b1 < k; b1++) {
                InstructionHandle instructionHandle2 = (InstructionHandle)arrayList.get(b1 * 2);
                InstructionHandle instructionHandle3 = (InstructionHandle)arrayList.get(b1 * 2 + 1);
                arrayOfChunk[b1] = new Chunk(instructionHandle2, instructionHandle3);
              } 
              ArrayList arrayList3 = mergeAdjacentChunks(arrayOfChunk);
              for (byte b2 = 0; b2 < arrayList3.size(); b2++) {
                Chunk chunk = (Chunk)arrayList3.get(b2);
                int m = chunk.getChunkSize();
                if (m >= 1000 && m <= 60000)
                  arrayList1.add(chunk); 
              } 
            } 
          } 
          arrayList2.remove(arrayList2.size() - 1);
        } 
        bool1 = ((arrayList2.size() & true) == 1) ? 1 : 0;
      } 
    } while (instructionHandle != null);
    return arrayList1;
  }
  
  private ArrayList mergeAdjacentChunks(Chunk[] paramArrayOfChunk) {
    int[] arrayOfInt1 = new int[paramArrayOfChunk.length];
    int[] arrayOfInt2 = new int[paramArrayOfChunk.length];
    boolean[] arrayOfBoolean = new boolean[paramArrayOfChunk.length];
    int i = 0;
    byte b = 0;
    ArrayList arrayList = new ArrayList();
    int j = 0;
    int k;
    for (k = 1; k < paramArrayOfChunk.length; k++) {
      if (!paramArrayOfChunk[k - true].isAdjacentTo(paramArrayOfChunk[k])) {
        byte b1 = k - j;
        if (i < b1)
          i = b1; 
        if (b1 > 1) {
          arrayOfInt2[b] = b1;
          arrayOfInt1[b] = j;
          b++;
        } 
        j = k;
      } 
    } 
    if (paramArrayOfChunk.length - j > 1) {
      k = paramArrayOfChunk.length - j;
      if (i < k)
        i = k; 
      arrayOfInt2[b] = paramArrayOfChunk.length - j;
      arrayOfInt1[b] = j;
      b++;
    } 
    for (k = i; k > 1; k--) {
      for (byte b1 = 0; b1 < b; b1++) {
        int m = arrayOfInt1[b1];
        int n = m + arrayOfInt2[b1] - 1;
        boolean bool = false;
        for (int i1 = m; i1 + k - 1 <= n && !bool; i1++) {
          int i2 = i1 + k - 1;
          int i3 = 0;
          int i4;
          for (i4 = i1; i4 <= i2; i4++)
            i3 += paramArrayOfChunk[i4].getChunkSize(); 
          if (i3 <= 60000) {
            bool = true;
            for (i4 = i1; i4 <= i2; i4++)
              arrayOfBoolean[i4] = true; 
            arrayList.add(new Chunk(paramArrayOfChunk[i1].getChunkStart(), paramArrayOfChunk[i2].getChunkEnd()));
            arrayOfInt2[b1] = arrayOfInt1[b1] - i1;
            i4 = n - i2;
            if (i4 >= 2) {
              arrayOfInt1[b] = i2 + 1;
              arrayOfInt2[b] = i4;
              b++;
            } 
          } 
        } 
      } 
    } 
    for (k = 0; k < paramArrayOfChunk.length; k++) {
      if (!arrayOfBoolean[k])
        arrayList.add(paramArrayOfChunk[k]); 
    } 
    return arrayList;
  }
  
  public Method[] outlineChunks(ClassGenerator paramClassGenerator, int paramInt) {
    boolean bool;
    ArrayList arrayList = new ArrayList();
    int i = paramInt;
    byte b = 0;
    String str = getName();
    if (str.equals("<init>")) {
      str = "$lt$init$gt$";
    } else if (str.equals("<clinit>")) {
      str = "$lt$clinit$gt$";
    } 
    do {
      ArrayList arrayList1 = getCandidateChunks(paramClassGenerator, i);
      Collections.sort(arrayList1);
      bool = false;
      for (int j = arrayList1.size() - 1; j >= 0 && i > 60000; j--) {
        Chunk chunk = (Chunk)arrayList1.get(j);
        arrayList.add(outline(chunk.getChunkStart(), chunk.getChunkEnd(), str + "$outline$" + b, paramClassGenerator));
        b++;
        bool = true;
        InstructionList instructionList = getInstructionList();
        InstructionHandle instructionHandle = instructionList.getEnd();
        instructionList.setPositions();
        i = instructionHandle.getPosition() + instructionHandle.getInstruction().getLength();
      } 
    } while (bool && i > 60000);
    if (i > 65535) {
      String str1 = (new ErrorMsg("OUTLINE_ERR_METHOD_TOO_BIG")).toString();
      throw new InternalError(str1);
    } 
    Method[] arrayOfMethod = new Method[arrayList.size() + 1];
    arrayList.toArray(arrayOfMethod);
    arrayOfMethod[arrayList.size()] = getThisMethod();
    return arrayOfMethod;
  }
  
  private Method outline(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2, String paramString, ClassGenerator paramClassGenerator) {
    InstructionHandle instructionHandle3;
    if (getExceptionHandlers().length != 0) {
      String str = (new ErrorMsg("OUTLINE_ERR_TRY_CATCH")).toString();
      throw new InternalError(str);
    } 
    int i = paramInstructionHandle1.getPosition();
    int j = paramInstructionHandle2.getPosition() + paramInstructionHandle2.getInstruction().getLength();
    ConstantPoolGen constantPoolGen1 = getConstantPool();
    InstructionList instructionList1 = new InstructionList();
    XSLTC xSLTC = paramClassGenerator.getParser().getXSLTC();
    String str1 = xSLTC.getHelperClassName();
    Type[] arrayOfType = { (new ObjectType(str1)).toJCType() };
    String str2 = "copyLocals";
    String[] arrayOfString1 = { "copyLocals" };
    byte b1 = 18;
    boolean bool1 = ((getAccessFlags() & 0x8) != 0) ? 1 : 0;
    if (bool1)
      b1 |= 0x8; 
    MethodGenerator methodGenerator = new MethodGenerator(b1, Type.VOID, arrayOfType, arrayOfString1, paramString, getClassName(), instructionList1, constantPoolGen1);
    ClassGenerator classGenerator = new ClassGenerator(str1, "java.lang.Object", str1 + ".java", 49, null, paramClassGenerator.getStylesheet()) {
        public boolean isExternal() { return true; }
      };
    ConstantPoolGen constantPoolGen2 = classGenerator.getConstantPool();
    classGenerator.addEmptyConstructor(1);
    byte b2 = 0;
    InstructionHandle instructionHandle1 = paramInstructionHandle2.getNext();
    InstructionList instructionList2 = new InstructionList();
    InstructionList instructionList3 = new InstructionList();
    InstructionList instructionList4 = new InstructionList();
    InstructionList instructionList5 = new InstructionList();
    InstructionHandle instructionHandle2 = instructionList2.append(new NEW(constantPoolGen1.addClass(str1)));
    instructionList2.append(InstructionConstants.DUP);
    instructionList2.append(InstructionConstants.DUP);
    instructionList2.append(new INVOKESPECIAL(constantPoolGen1.addMethodref(str1, "<init>", "()V")));
    if (bool1) {
      instructionHandle3 = instructionList3.append(new INVOKESTATIC(constantPoolGen1.addMethodref(paramClassGenerator.getClassName(), paramString, methodGenerator.getSignature())));
    } else {
      instructionList3.append(InstructionConstants.THIS);
      instructionList3.append(InstructionConstants.SWAP);
      instructionHandle3 = instructionList3.append(new INVOKEVIRTUAL(constantPoolGen1.addMethodref(paramClassGenerator.getClassName(), paramString, methodGenerator.getSignature())));
    } 
    boolean bool2 = false;
    InstructionHandle instructionHandle4 = null;
    BranchHandle branchHandle = null;
    HashMap hashMap1 = new HashMap();
    HashMap hashMap2 = new HashMap();
    HashMap hashMap3 = new HashMap();
    HashMap hashMap4 = new HashMap();
    InstructionHandle instructionHandle5;
    for (instructionHandle5 = paramInstructionHandle1; instructionHandle5 != instructionHandle1; instructionHandle5 = instructionHandle5.getNext()) {
      Instruction instruction = instructionHandle5.getInstruction();
      if (instruction instanceof MarkerInstruction) {
        if (instructionHandle5.hasTargeters())
          if (instruction instanceof OutlineableChunkEnd) {
            hashMap1.put(instructionHandle5, branchHandle);
          } else if (!bool2) {
            bool2 = true;
            instructionHandle4 = instructionHandle5;
          }  
      } else {
        InstructionHandle instructionHandle;
        Instruction instruction1 = instruction.copy();
        if (instruction1 instanceof BranchInstruction) {
          branchHandle = instructionList1.append((BranchInstruction)instruction1);
        } else {
          instructionHandle = instructionList1.append(instruction1);
        } 
        if (instruction1 instanceof com.sun.org.apache.bcel.internal.generic.LocalVariableInstruction || instruction1 instanceof com.sun.org.apache.bcel.internal.generic.RET) {
          IndexedInstruction indexedInstruction = (IndexedInstruction)instruction1;
          int k = indexedInstruction.getIndex();
          LocalVariableGen localVariableGen1 = getLocalVariableRegistry().lookupRegisteredLocalVariable(k, instructionHandle5.getPosition());
          LocalVariableGen localVariableGen2 = (LocalVariableGen)hashMap2.get(localVariableGen1);
          if (hashMap2.get(localVariableGen1) == null) {
            boolean bool3 = offsetInLocalVariableGenRange(localVariableGen1, (i != 0) ? (i - 1) : 0);
            boolean bool4 = offsetInLocalVariableGenRange(localVariableGen1, j + 1);
            if (bool3 || bool4) {
              String str3 = localVariableGen1.getName();
              Type type = localVariableGen1.getType();
              localVariableGen2 = methodGenerator.addLocalVariable(str3, type, null, null);
              int m = localVariableGen2.getIndex();
              String str4 = type.getSignature();
              hashMap2.put(localVariableGen1, localVariableGen2);
              String str5 = "field" + ++b2;
              classGenerator.addField(new Field(1, constantPoolGen2.addUtf8(str5), constantPoolGen2.addUtf8(str4), null, constantPoolGen2.getConstantPool()));
              int n = constantPoolGen1.addFieldref(str1, str5, str4);
              if (bool3) {
                instructionList2.append(InstructionConstants.DUP);
                InstructionHandle instructionHandle7 = instructionList2.append(loadLocal(k, type));
                instructionList2.append(new PUTFIELD(n));
                if (!bool4)
                  hashMap4.put(localVariableGen1, instructionHandle7); 
                instructionList4.append(InstructionConstants.ALOAD_1);
                instructionList4.append(new GETFIELD(n));
                instructionList4.append(storeLocal(m, type));
              } 
              if (bool4) {
                instructionList5.append(InstructionConstants.ALOAD_1);
                instructionList5.append(loadLocal(m, type));
                instructionList5.append(new PUTFIELD(n));
                instructionList3.append(InstructionConstants.DUP);
                instructionList3.append(new GETFIELD(n));
                InstructionHandle instructionHandle7 = instructionList3.append(storeLocal(k, type));
                if (!bool3)
                  hashMap3.put(localVariableGen1, instructionHandle7); 
              } 
            } 
          } 
        } 
        if (instructionHandle5.hasTargeters())
          hashMap1.put(instructionHandle5, instructionHandle); 
        if (bool2) {
          do {
            hashMap1.put(instructionHandle4, instructionHandle);
            instructionHandle4 = instructionHandle4.getNext();
          } while (instructionHandle4 != instructionHandle5);
          bool2 = false;
        } 
      } 
    } 
    instructionHandle5 = paramInstructionHandle1;
    InstructionHandle instructionHandle6 = instructionList1.getStart();
    while (instructionHandle6 != null) {
      Instruction instruction1 = instructionHandle5.getInstruction();
      Instruction instruction2 = instructionHandle6.getInstruction();
      if (instruction1 instanceof BranchInstruction) {
        BranchInstruction branchInstruction1 = (BranchInstruction)instruction2;
        BranchInstruction branchInstruction2 = (BranchInstruction)instruction1;
        InstructionHandle instructionHandle7 = branchInstruction2.getTarget();
        InstructionHandle instructionHandle8 = (InstructionHandle)hashMap1.get(instructionHandle7);
        branchInstruction1.setTarget(instructionHandle8);
        if (branchInstruction2 instanceof Select) {
          InstructionHandle[] arrayOfInstructionHandle1 = ((Select)branchInstruction2).getTargets();
          InstructionHandle[] arrayOfInstructionHandle2 = ((Select)branchInstruction1).getTargets();
          for (byte b = 0; b < arrayOfInstructionHandle1.length; b++)
            arrayOfInstructionHandle2[b] = (InstructionHandle)hashMap1.get(arrayOfInstructionHandle1[b]); 
        } 
      } else if (instruction1 instanceof com.sun.org.apache.bcel.internal.generic.LocalVariableInstruction || instruction1 instanceof com.sun.org.apache.bcel.internal.generic.RET) {
        int m;
        IndexedInstruction indexedInstruction = (IndexedInstruction)instruction2;
        int k = indexedInstruction.getIndex();
        LocalVariableGen localVariableGen1 = getLocalVariableRegistry().lookupRegisteredLocalVariable(k, instructionHandle5.getPosition());
        LocalVariableGen localVariableGen2 = (LocalVariableGen)hashMap2.get(localVariableGen1);
        if (localVariableGen2 == null) {
          String str = localVariableGen1.getName();
          Type type = localVariableGen1.getType();
          localVariableGen2 = methodGenerator.addLocalVariable(str, type, null, null);
          m = localVariableGen2.getIndex();
          hashMap2.put(localVariableGen1, localVariableGen2);
          hashMap3.put(localVariableGen1, instructionHandle3);
          hashMap4.put(localVariableGen1, instructionHandle3);
        } else {
          m = localVariableGen2.getIndex();
        } 
        indexedInstruction.setIndex(m);
      } 
      if (instructionHandle5.hasTargeters()) {
        InstructionTargeter[] arrayOfInstructionTargeter = instructionHandle5.getTargeters();
        for (byte b = 0; b < arrayOfInstructionTargeter.length; b++) {
          InstructionTargeter instructionTargeter = arrayOfInstructionTargeter[b];
          if (instructionTargeter instanceof LocalVariableGen && ((LocalVariableGen)instructionTargeter).getEnd() == instructionHandle5) {
            Object object = hashMap2.get(instructionTargeter);
            if (object != null)
              methodGenerator.removeLocalVariable((LocalVariableGen)object); 
          } 
        } 
      } 
      if (!(instruction1 instanceof MarkerInstruction))
        instructionHandle6 = instructionHandle6.getNext(); 
      instructionHandle5 = instructionHandle5.getNext();
    } 
    instructionList3.append(InstructionConstants.POP);
    for (Map.Entry entry : hashMap3.entrySet()) {
      LocalVariableGen localVariableGen = (LocalVariableGen)entry.getKey();
      InstructionHandle instructionHandle = (InstructionHandle)entry.getValue();
      localVariableGen.setStart(instructionHandle);
    } 
    for (Map.Entry entry : hashMap4.entrySet()) {
      LocalVariableGen localVariableGen = (LocalVariableGen)entry.getKey();
      InstructionHandle instructionHandle = (InstructionHandle)entry.getValue();
      localVariableGen.setEnd(instructionHandle);
    } 
    xSLTC.dumpClass(classGenerator.getJavaClass());
    InstructionList instructionList6 = getInstructionList();
    instructionList6.insert(paramInstructionHandle1, instructionList2);
    instructionList6.insert(paramInstructionHandle1, instructionList3);
    instructionList1.insert(instructionList4);
    instructionList1.append(instructionList5);
    instructionList1.append(InstructionConstants.RETURN);
    try {
      instructionList6.delete(paramInstructionHandle1, paramInstructionHandle2);
    } catch (TargetLostException targetLostException) {
      InstructionHandle[] arrayOfInstructionHandle = targetLostException.getTargets();
      for (byte b = 0; b < arrayOfInstructionHandle.length; b++) {
        InstructionHandle instructionHandle = arrayOfInstructionHandle[b];
        InstructionTargeter[] arrayOfInstructionTargeter = instructionHandle.getTargeters();
        for (byte b4 = 0; b4 < arrayOfInstructionTargeter.length; b4++) {
          if (arrayOfInstructionTargeter[b4] instanceof LocalVariableGen) {
            LocalVariableGen localVariableGen = (LocalVariableGen)arrayOfInstructionTargeter[b4];
            if (localVariableGen.getStart() == instructionHandle)
              localVariableGen.setStart(instructionHandle3); 
            if (localVariableGen.getEnd() == instructionHandle)
              localVariableGen.setEnd(instructionHandle3); 
          } else {
            arrayOfInstructionTargeter[b4].updateTarget(instructionHandle, instructionHandle2);
          } 
        } 
      } 
    } 
    String[] arrayOfString2 = getExceptions();
    for (byte b3 = 0; b3 < arrayOfString2.length; b3++)
      methodGenerator.addException(arrayOfString2[b3]); 
    return methodGenerator.getThisMethod();
  }
  
  private static Instruction loadLocal(int paramInt, Type paramType) { return (paramType == Type.BOOLEAN) ? new ILOAD(paramInt) : ((paramType == Type.INT) ? new ILOAD(paramInt) : ((paramType == Type.SHORT) ? new ILOAD(paramInt) : ((paramType == Type.LONG) ? new LLOAD(paramInt) : ((paramType == Type.BYTE) ? new ILOAD(paramInt) : ((paramType == Type.CHAR) ? new ILOAD(paramInt) : ((paramType == Type.FLOAT) ? new FLOAD(paramInt) : ((paramType == Type.DOUBLE) ? new DLOAD(paramInt) : new ALOAD(paramInt)))))))); }
  
  private static Instruction storeLocal(int paramInt, Type paramType) { return (paramType == Type.BOOLEAN) ? new ISTORE(paramInt) : ((paramType == Type.INT) ? new ISTORE(paramInt) : ((paramType == Type.SHORT) ? new ISTORE(paramInt) : ((paramType == Type.LONG) ? new LSTORE(paramInt) : ((paramType == Type.BYTE) ? new ISTORE(paramInt) : ((paramType == Type.CHAR) ? new ISTORE(paramInt) : ((paramType == Type.FLOAT) ? new FSTORE(paramInt) : ((paramType == Type.DOUBLE) ? new DSTORE(paramInt) : new ASTORE(paramInt)))))))); }
  
  public void markChunkStart() {
    getInstructionList().append(OutlineableChunkStart.OUTLINEABLECHUNKSTART);
    this.m_totalChunks++;
    this.m_openChunks++;
  }
  
  public void markChunkEnd() {
    getInstructionList().append(OutlineableChunkEnd.OUTLINEABLECHUNKEND);
    this.m_openChunks--;
    if (this.m_openChunks < 0) {
      String str = (new ErrorMsg("OUTLINE_ERR_UNBALANCED_MARKERS")).toString();
      throw new InternalError(str);
    } 
  }
  
  Method[] getGeneratedMethods(ClassGenerator paramClassGenerator) {
    Method[] arrayOfMethod;
    InstructionList instructionList = getInstructionList();
    InstructionHandle instructionHandle = instructionList.getEnd();
    instructionList.setPositions();
    int i = instructionHandle.getPosition() + instructionHandle.getInstruction().getLength();
    if (i > 32767) {
      boolean bool = widenConditionalBranchTargetOffsets();
      if (bool) {
        instructionList.setPositions();
        instructionHandle = instructionList.getEnd();
        i = instructionHandle.getPosition() + instructionHandle.getInstruction().getLength();
      } 
    } 
    if (i > 65535) {
      arrayOfMethod = outlineChunks(paramClassGenerator, i);
    } else {
      arrayOfMethod = new Method[] { getThisMethod() };
    } 
    return arrayOfMethod;
  }
  
  protected Method getThisMethod() {
    stripAttributes(true);
    setMaxLocals();
    setMaxStack();
    removeNOPs();
    return getMethod();
  }
  
  boolean widenConditionalBranchTargetOffsets() {
    boolean bool = false;
    int i = 0;
    InstructionList instructionList = getInstructionList();
    InstructionHandle instructionHandle;
    for (instructionHandle = instructionList.getStart(); instructionHandle != null; instructionHandle = instructionHandle.getNext()) {
      Instruction instruction = instructionHandle.getInstruction();
      switch (instruction.getOpcode()) {
        case 167:
        case 168:
          i += 2;
          break;
        case 170:
        case 171:
          i += 3;
          break;
        case 153:
        case 154:
        case 155:
        case 156:
        case 157:
        case 158:
        case 159:
        case 160:
        case 161:
        case 162:
        case 163:
        case 164:
        case 165:
        case 166:
        case 198:
        case 199:
          i += 5;
          break;
      } 
    } 
    for (instructionHandle = instructionList.getStart(); instructionHandle != null; instructionHandle = instructionHandle.getNext()) {
      Instruction instruction = instructionHandle.getInstruction();
      if (instruction instanceof IfInstruction) {
        IfInstruction ifInstruction = (IfInstruction)instruction;
        BranchHandle branchHandle = (BranchHandle)instructionHandle;
        InstructionHandle instructionHandle1 = ifInstruction.getTarget();
        int j = instructionHandle1.getPosition() - branchHandle.getPosition();
        if (j - i < -32768 || j + i > 32767) {
          InstructionHandle instructionHandle2 = branchHandle.getNext();
          IfInstruction ifInstruction1 = ifInstruction.negate();
          BranchHandle branchHandle1 = instructionList.append(branchHandle, ifInstruction1);
          BranchHandle branchHandle2 = instructionList.append(branchHandle1, new GOTO(instructionHandle1));
          if (instructionHandle2 == null)
            instructionHandle2 = instructionList.append(branchHandle2, NOP); 
          branchHandle1.updateTarget(instructionHandle1, instructionHandle2);
          if (branchHandle.hasTargeters()) {
            InstructionTargeter[] arrayOfInstructionTargeter = branchHandle.getTargeters();
            for (byte b = 0; b < arrayOfInstructionTargeter.length; b++) {
              InstructionTargeter instructionTargeter = arrayOfInstructionTargeter[b];
              if (instructionTargeter instanceof LocalVariableGen) {
                LocalVariableGen localVariableGen = (LocalVariableGen)instructionTargeter;
                if (localVariableGen.getStart() == branchHandle) {
                  localVariableGen.setStart(branchHandle1);
                } else if (localVariableGen.getEnd() == branchHandle) {
                  localVariableGen.setEnd(branchHandle2);
                } 
              } else {
                instructionTargeter.updateTarget(branchHandle, branchHandle1);
              } 
            } 
          } 
          try {
            instructionList.delete(branchHandle);
          } catch (TargetLostException targetLostException) {
            String str = (new ErrorMsg("OUTLINE_ERR_DELETED_TARGET", targetLostException.getMessage())).toString();
            throw new InternalError(str);
          } 
          instructionHandle = branchHandle2;
          bool = true;
        } 
      } 
    } 
    return bool;
  }
  
  private class Chunk implements Comparable {
    private InstructionHandle m_start;
    
    private InstructionHandle m_end;
    
    private int m_size;
    
    Chunk(InstructionHandle param1InstructionHandle1, InstructionHandle param1InstructionHandle2) {
      this.m_start = param1InstructionHandle1;
      this.m_end = param1InstructionHandle2;
      this.m_size = param1InstructionHandle2.getPosition() - param1InstructionHandle1.getPosition();
    }
    
    boolean isAdjacentTo(Chunk param1Chunk) { return (getChunkEnd().getNext() == param1Chunk.getChunkStart()); }
    
    InstructionHandle getChunkStart() { return this.m_start; }
    
    InstructionHandle getChunkEnd() { return this.m_end; }
    
    int getChunkSize() { return this.m_size; }
    
    public int compareTo(Object param1Object) { return getChunkSize() - ((Chunk)param1Object).getChunkSize(); }
  }
  
  protected class LocalVariableRegistry {
    protected ArrayList _variables = new ArrayList();
    
    protected HashMap _nameToLVGMap = new HashMap();
    
    protected void registerLocalVariable(LocalVariableGen param1LocalVariableGen) {
      int i = param1LocalVariableGen.getIndex();
      int j = this._variables.size();
      if (i >= j) {
        for (int k = j; k < i; k++)
          this._variables.add(null); 
        this._variables.add(param1LocalVariableGen);
      } else {
        Object object = this._variables.get(i);
        if (object != null) {
          if (object instanceof LocalVariableGen) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(object);
            arrayList.add(param1LocalVariableGen);
            this._variables.set(i, arrayList);
          } else {
            ((ArrayList)object).add(param1LocalVariableGen);
          } 
        } else {
          this._variables.set(i, param1LocalVariableGen);
        } 
      } 
      registerByName(param1LocalVariableGen);
    }
    
    protected LocalVariableGen lookupRegisteredLocalVariable(int param1Int1, int param1Int2) {
      Object object = (this._variables != null) ? this._variables.get(param1Int1) : null;
      if (object != null)
        if (object instanceof LocalVariableGen) {
          LocalVariableGen localVariableGen = (LocalVariableGen)object;
          if (MethodGenerator.this.offsetInLocalVariableGenRange(localVariableGen, param1Int2))
            return localVariableGen; 
        } else {
          ArrayList arrayList = (ArrayList)object;
          int i = arrayList.size();
          for (byte b = 0; b < i; b++) {
            LocalVariableGen localVariableGen = (LocalVariableGen)arrayList.get(b);
            if (MethodGenerator.this.offsetInLocalVariableGenRange(localVariableGen, param1Int2))
              return localVariableGen; 
          } 
        }  
      return null;
    }
    
    protected void registerByName(LocalVariableGen param1LocalVariableGen) {
      Object object = this._nameToLVGMap.get(param1LocalVariableGen.getName());
      if (object == null) {
        this._nameToLVGMap.put(param1LocalVariableGen.getName(), param1LocalVariableGen);
      } else {
        ArrayList arrayList;
        if (object instanceof ArrayList) {
          arrayList = (ArrayList)object;
          arrayList.add(param1LocalVariableGen);
        } else {
          arrayList = new ArrayList();
          arrayList.add(object);
          arrayList.add(param1LocalVariableGen);
        } 
        this._nameToLVGMap.put(param1LocalVariableGen.getName(), arrayList);
      } 
    }
    
    protected void removeByNameTracking(LocalVariableGen param1LocalVariableGen) {
      Object object = this._nameToLVGMap.get(param1LocalVariableGen.getName());
      if (object instanceof ArrayList) {
        ArrayList arrayList = (ArrayList)object;
        for (byte b = 0; b < arrayList.size(); b++) {
          if (arrayList.get(b) == param1LocalVariableGen) {
            arrayList.remove(b);
            break;
          } 
        } 
      } else {
        this._nameToLVGMap.remove(param1LocalVariableGen);
      } 
    }
    
    protected LocalVariableGen lookUpByName(String param1String) {
      LocalVariableGen localVariableGen = null;
      Object object = this._nameToLVGMap.get(param1String);
      if (object instanceof ArrayList) {
        ArrayList arrayList = (ArrayList)object;
        for (byte b = 0; b < arrayList.size(); b++) {
          localVariableGen = (LocalVariableGen)arrayList.get(b);
          if (localVariableGen.getName() == param1String)
            break; 
        } 
      } else {
        localVariableGen = (LocalVariableGen)object;
      } 
      return localVariableGen;
    }
    
    protected LocalVariableGen[] getLocals(boolean param1Boolean) {
      LocalVariableGen[] arrayOfLocalVariableGen = null;
      ArrayList arrayList = new ArrayList();
      if (param1Boolean) {
        int i = arrayList.size();
        for (byte b = 0; b < i; b++) {
          Object object = this._variables.get(b);
          if (object != null)
            if (object instanceof ArrayList) {
              ArrayList arrayList1 = (ArrayList)object;
              for (byte b1 = 0; b1 < arrayList1.size(); b1++)
                arrayList.add(arrayList1.get(b)); 
            } else {
              arrayList.add(object);
            }  
        } 
      } else {
        for (Map.Entry entry : this._nameToLVGMap.entrySet()) {
          Object object = entry.getValue();
          if (object != null) {
            if (object instanceof ArrayList) {
              ArrayList arrayList1 = (ArrayList)object;
              for (byte b = 0; b < arrayList1.size(); b++)
                arrayList.add(arrayList1.get(b)); 
              continue;
            } 
            arrayList.add(object);
          } 
        } 
      } 
      arrayOfLocalVariableGen = new LocalVariableGen[arrayList.size()];
      arrayList.toArray(arrayOfLocalVariableGen);
      return arrayOfLocalVariableGen;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\MethodGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */