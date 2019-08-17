package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DUP;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.bcel.internal.generic.IFLT;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import com.sun.org.apache.bcel.internal.generic.TargetLostException;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.util.InstructionFinder;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NamedMethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

final class Mode implements Constants {
  private final QName _name;
  
  private final Stylesheet _stylesheet;
  
  private final String _methodName;
  
  private Vector _templates;
  
  private Vector _childNodeGroup = null;
  
  private TestSeq _childNodeTestSeq = null;
  
  private Vector _attribNodeGroup = null;
  
  private TestSeq _attribNodeTestSeq = null;
  
  private Vector _idxGroup = null;
  
  private TestSeq _idxTestSeq = null;
  
  private Vector[] _patternGroups;
  
  private TestSeq[] _testSeq;
  
  private Map<Template, Object> _neededTemplates = new HashMap();
  
  private Map<Template, Mode> _namedTemplates = new HashMap();
  
  private Map<Template, InstructionHandle> _templateIHs = new HashMap();
  
  private Map<Template, InstructionList> _templateILs = new HashMap();
  
  private LocationPathPattern _rootPattern = null;
  
  private Map<Integer, Integer> _importLevels = null;
  
  private Map<String, Key> _keys = null;
  
  private int _currentIndex;
  
  public Mode(QName paramQName, Stylesheet paramStylesheet, String paramString) {
    this._name = paramQName;
    this._stylesheet = paramStylesheet;
    this._methodName = "applyTemplates" + paramString;
    this._templates = new Vector();
    this._patternGroups = new Vector[32];
  }
  
  public String functionName() { return this._methodName; }
  
  public String functionName(int paramInt1, int paramInt2) {
    if (this._importLevels == null)
      this._importLevels = new HashMap(); 
    this._importLevels.put(Integer.valueOf(paramInt2), Integer.valueOf(paramInt1));
    return this._methodName + '_' + paramInt2;
  }
  
  private String getClassName() { return this._stylesheet.getClassName(); }
  
  public Stylesheet getStylesheet() { return this._stylesheet; }
  
  public void addTemplate(Template paramTemplate) { this._templates.addElement(paramTemplate); }
  
  private Vector quicksort(Vector paramVector, int paramInt1, int paramInt2) {
    if (paramInt1 < paramInt2) {
      int i = partition(paramVector, paramInt1, paramInt2);
      quicksort(paramVector, paramInt1, i);
      quicksort(paramVector, i + 1, paramInt2);
    } 
    return paramVector;
  }
  
  private int partition(Vector paramVector, int paramInt1, int paramInt2) {
    Template template = (Template)paramVector.elementAt(paramInt1);
    int i = paramInt1 - 1;
    int j = paramInt2 + 1;
    while (true) {
      if (template.compareTo((Template)paramVector.elementAt(--j)) > 0)
        continue; 
      while (template.compareTo((Template)paramVector.elementAt(++i)) < 0);
      if (i < j) {
        paramVector.set(j, paramVector.set(i, paramVector.elementAt(j)));
        continue;
      } 
      break;
    } 
    return j;
  }
  
  public void processPatterns(Map<String, Key> paramMap) {
    this._keys = paramMap;
    this._templates = quicksort(this._templates, 0, this._templates.size() - 1);
    Enumeration enumeration = this._templates.elements();
    while (enumeration.hasMoreElements()) {
      Template template = (Template)enumeration.nextElement();
      if (template.isNamed() && !template.disabled())
        this._namedTemplates.put(template, this); 
      Pattern pattern = template.getPattern();
      if (pattern != null)
        flattenAlternative(pattern, template, paramMap); 
    } 
    prepareTestSequences();
  }
  
  private void flattenAlternative(Pattern paramPattern, Template paramTemplate, Map<String, Key> paramMap) {
    if (paramPattern instanceof IdKeyPattern) {
      IdKeyPattern idKeyPattern = (IdKeyPattern)paramPattern;
      idKeyPattern.setTemplate(paramTemplate);
      if (this._idxGroup == null)
        this._idxGroup = new Vector(); 
      this._idxGroup.add(paramPattern);
    } else if (paramPattern instanceof AlternativePattern) {
      AlternativePattern alternativePattern = (AlternativePattern)paramPattern;
      flattenAlternative(alternativePattern.getLeft(), paramTemplate, paramMap);
      flattenAlternative(alternativePattern.getRight(), paramTemplate, paramMap);
    } else if (paramPattern instanceof LocationPathPattern) {
      LocationPathPattern locationPathPattern = (LocationPathPattern)paramPattern;
      locationPathPattern.setTemplate(paramTemplate);
      addPatternToGroup(locationPathPattern);
    } 
  }
  
  private void addPatternToGroup(LocationPathPattern paramLocationPathPattern) {
    if (paramLocationPathPattern instanceof IdKeyPattern) {
      addPattern(-1, paramLocationPathPattern);
    } else {
      StepPattern stepPattern = paramLocationPathPattern.getKernelPattern();
      if (stepPattern != null) {
        addPattern(stepPattern.getNodeType(), paramLocationPathPattern);
      } else if (this._rootPattern == null || paramLocationPathPattern.noSmallerThan(this._rootPattern)) {
        this._rootPattern = paramLocationPathPattern;
      } 
    } 
  }
  
  private void addPattern(int paramInt, LocationPathPattern paramLocationPathPattern) {
    Vector vector;
    int i = this._patternGroups.length;
    if (paramInt >= i) {
      vector = new Vector[paramInt * 2];
      System.arraycopy(this._patternGroups, 0, vector, 0, i);
      this._patternGroups = vector;
    } 
    if (paramInt == -1) {
      if (paramLocationPathPattern.getAxis() == 2) {
        vector = (this._attribNodeGroup == null) ? (this._attribNodeGroup = new Vector(2)) : this._attribNodeGroup;
      } else {
        vector = (this._childNodeGroup == null) ? (this._childNodeGroup = new Vector(2)) : this._childNodeGroup;
      } 
    } else {
      vector = (this._patternGroups[paramInt] == null) ? (this._patternGroups[paramInt] = new Vector(2)) : this._patternGroups[paramInt];
    } 
    if (vector.size() == 0) {
      vector.addElement(paramLocationPathPattern);
    } else {
      boolean bool = false;
      for (byte b = 0; b < vector.size(); b++) {
        LocationPathPattern locationPathPattern = (LocationPathPattern)vector.elementAt(b);
        if (paramLocationPathPattern.noSmallerThan(locationPathPattern)) {
          bool = true;
          vector.insertElementAt(paramLocationPathPattern, b);
          break;
        } 
      } 
      if (!bool)
        vector.addElement(paramLocationPathPattern); 
    } 
  }
  
  private void completeTestSequences(int paramInt, Vector paramVector) {
    if (paramVector != null)
      if (this._patternGroups[paramInt] == null) {
        this._patternGroups[paramInt] = paramVector;
      } else {
        int i = paramVector.size();
        for (byte b = 0; b < i; b++)
          addPattern(paramInt, (LocationPathPattern)paramVector.elementAt(b)); 
      }  
  }
  
  private void prepareTestSequences() {
    Vector vector1 = this._patternGroups[1];
    Vector vector2 = this._patternGroups[2];
    completeTestSequences(3, this._childNodeGroup);
    completeTestSequences(1, this._childNodeGroup);
    completeTestSequences(7, this._childNodeGroup);
    completeTestSequences(8, this._childNodeGroup);
    completeTestSequences(2, this._attribNodeGroup);
    Vector vector3 = this._stylesheet.getXSLTC().getNamesIndex();
    if (vector1 != null || vector2 != null || this._childNodeGroup != null || this._attribNodeGroup != null) {
      int j = this._patternGroups.length;
      for (byte b1 = 14; b1 < j; b1++) {
        if (this._patternGroups[b1] != null) {
          String str = (String)vector3.elementAt(b1 - 14);
          if (isAttributeName(str)) {
            completeTestSequences(b1, vector2);
            completeTestSequences(b1, this._attribNodeGroup);
          } else {
            completeTestSequences(b1, vector1);
            completeTestSequences(b1, this._childNodeGroup);
          } 
        } 
      } 
    } 
    this._testSeq = new TestSeq[14 + vector3.size()];
    int i = this._patternGroups.length;
    for (byte b = 0; b < i; b++) {
      Vector vector = this._patternGroups[b];
      if (vector != null) {
        TestSeq testSeq = new TestSeq(vector, b, this);
        testSeq.reduce();
        this._testSeq[b] = testSeq;
        testSeq.findTemplates(this._neededTemplates);
      } 
    } 
    if (this._childNodeGroup != null && this._childNodeGroup.size() > 0) {
      this._childNodeTestSeq = new TestSeq(this._childNodeGroup, -1, this);
      this._childNodeTestSeq.reduce();
      this._childNodeTestSeq.findTemplates(this._neededTemplates);
    } 
    if (this._idxGroup != null && this._idxGroup.size() > 0) {
      this._idxTestSeq = new TestSeq(this._idxGroup, this);
      this._idxTestSeq.reduce();
      this._idxTestSeq.findTemplates(this._neededTemplates);
    } 
    if (this._rootPattern != null)
      this._neededTemplates.put(this._rootPattern.getTemplate(), this); 
  }
  
  private void compileNamedTemplate(Template paramTemplate, ClassGenerator paramClassGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = new InstructionList();
    String str = Util.escape(paramTemplate.getName().toString());
    int i = 0;
    if (paramTemplate.isSimpleNamedTemplate()) {
      Vector vector = paramTemplate.getParameters();
      i = vector.size();
    } 
    Type[] arrayOfType = new Type[4 + i];
    String[] arrayOfString = new String[4 + i];
    arrayOfType[0] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    arrayOfType[1] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    arrayOfType[2] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
    arrayOfType[3] = Type.INT;
    arrayOfString[0] = "document";
    arrayOfString[1] = "iterator";
    arrayOfString[2] = "handler";
    arrayOfString[3] = "node";
    for (byte b = 4; b < 4 + i; b++) {
      arrayOfType[b] = Util.getJCRefType("Ljava/lang/Object;");
      arrayOfString[b] = "param" + String.valueOf(b - 4);
    } 
    NamedMethodGenerator namedMethodGenerator = new NamedMethodGenerator(1, Type.VOID, arrayOfType, arrayOfString, str, getClassName(), instructionList, constantPoolGen);
    instructionList.append(paramTemplate.compile(paramClassGenerator, namedMethodGenerator));
    instructionList.append(RETURN);
    paramClassGenerator.addMethod(namedMethodGenerator);
  }
  
  private void compileTemplates(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, InstructionHandle paramInstructionHandle) {
    Set set = this._namedTemplates.keySet();
    for (Template template : set)
      compileNamedTemplate(template, paramClassGenerator); 
    set = this._neededTemplates.keySet();
    for (Template template : set) {
      if (template.hasContents()) {
        InstructionList instructionList = template.compile(paramClassGenerator, paramMethodGenerator);
        instructionList.append(new GOTO_W(paramInstructionHandle));
        this._templateILs.put(template, instructionList);
        this._templateIHs.put(template, instructionList.getStart());
        continue;
      } 
      this._templateIHs.put(template, paramInstructionHandle);
    } 
  }
  
  private void appendTemplateCode(InstructionList paramInstructionList) {
    for (Template template : this._neededTemplates.keySet()) {
      InstructionList instructionList = (InstructionList)this._templateILs.get(template);
      if (instructionList != null)
        paramInstructionList.append(instructionList); 
    } 
  }
  
  private void appendTestSequences(InstructionList paramInstructionList) {
    int i = this._testSeq.length;
    for (byte b = 0; b < i; b++) {
      TestSeq testSeq = this._testSeq[b];
      if (testSeq != null) {
        InstructionList instructionList = testSeq.getInstructionList();
        if (instructionList != null)
          paramInstructionList.append(instructionList); 
      } 
    } 
  }
  
  public static void compileGetChildren(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, int paramInt) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getChildren", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(new ILOAD(paramInt));
    instructionList.append(new INVOKEINTERFACE(i, 2));
  }
  
  private InstructionList compileDefaultRecursion(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, InstructionHandle paramInstructionHandle) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = new InstructionList();
    String str = paramClassGenerator.getApplyTemplatesSig();
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getChildren", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    int j = constantPoolGen.addMethodref(getClassName(), functionName(), str);
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(new ILOAD(this._currentIndex));
    instructionList.append(new INVOKEINTERFACE(i, 2));
    instructionList.append(paramMethodGenerator.loadHandler());
    instructionList.append(new INVOKEVIRTUAL(j));
    instructionList.append(new GOTO_W(paramInstructionHandle));
    return instructionList;
  }
  
  private InstructionList compileDefaultText(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, InstructionHandle paramInstructionHandle) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = new InstructionList();
    int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "characters", "(ILcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(new ILOAD(this._currentIndex));
    instructionList.append(paramMethodGenerator.loadHandler());
    instructionList.append(new INVOKEINTERFACE(i, 3));
    instructionList.append(new GOTO_W(paramInstructionHandle));
    return instructionList;
  }
  
  private InstructionList compileNamespaces(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2, boolean paramBoolean, InstructionHandle paramInstructionHandle) {
    XSLTC xSLTC = paramClassGenerator.getParser().getXSLTC();
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    Vector vector1 = xSLTC.getNamespaceIndex();
    Vector vector2 = xSLTC.getNamesIndex();
    int i = vector1.size() + 1;
    int j = vector2.size();
    InstructionList instructionList = new InstructionList();
    int[] arrayOfInt = new int[i];
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[arrayOfInt.length];
    if (i > 0) {
      boolean bool = false;
      int k;
      for (k = 0; k < i; k++) {
        arrayOfInstructionHandle[k] = paramInstructionHandle;
        arrayOfInt[k] = k;
      } 
      for (k = 14; k < 14 + j; k++) {
        if (paramArrayOfBoolean1[k] && paramArrayOfBoolean2[k] == paramBoolean) {
          String str1 = (String)vector2.elementAt(k - 14);
          String str2 = str1.substring(0, str1.lastIndexOf(':'));
          int m = xSLTC.registerNamespace(str2);
          if (k < this._testSeq.length && this._testSeq[k] != null) {
            arrayOfInstructionHandle[m] = this._testSeq[k].compile(paramClassGenerator, paramMethodGenerator, paramInstructionHandle);
            bool = true;
          } 
        } 
      } 
      if (!bool)
        return null; 
      k = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNamespaceType", "(I)I");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(new ILOAD(this._currentIndex));
      instructionList.append(new INVOKEINTERFACE(k, 2));
      instructionList.append(new SWITCH(arrayOfInt, arrayOfInstructionHandle, paramInstructionHandle));
      return instructionList;
    } 
    return null;
  }
  
  public void compileApplyTemplates(ClassGenerator paramClassGenerator) {
    XSLTC xSLTC = paramClassGenerator.getParser().getXSLTC();
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    Vector vector = xSLTC.getNamesIndex();
    Type[] arrayOfType = new Type[3];
    arrayOfType[0] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    arrayOfType[1] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    arrayOfType[2] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
    String[] arrayOfString = new String[3];
    arrayOfString[0] = "document";
    arrayOfString[1] = "iterator";
    arrayOfString[2] = "handler";
    InstructionList instructionList1 = new InstructionList();
    MethodGenerator methodGenerator = new MethodGenerator(17, Type.VOID, arrayOfType, arrayOfString, functionName(), getClassName(), instructionList1, paramClassGenerator.getConstantPool());
    methodGenerator.addException("com.sun.org.apache.xalan.internal.xsltc.TransletException");
    instructionList1.append(NOP);
    LocalVariableGen localVariableGen = methodGenerator.addLocalVariable2("current", Type.INT, null);
    this._currentIndex = localVariableGen.getIndex();
    InstructionList instructionList2 = new InstructionList();
    instructionList2.append(NOP);
    InstructionList instructionList3 = new InstructionList();
    instructionList3.append(methodGenerator.loadIterator());
    instructionList3.append(methodGenerator.nextNode());
    instructionList3.append(DUP);
    instructionList3.append(new ISTORE(this._currentIndex));
    BranchHandle branchHandle1 = instructionList3.append(new IFLT(null));
    BranchHandle branchHandle2 = instructionList3.append(new GOTO_W(null));
    branchHandle1.setTarget(instructionList3.append(RETURN));
    InstructionHandle instructionHandle1 = instructionList3.getStart();
    localVariableGen.setStart(instructionList1.append(new GOTO_W(instructionHandle1)));
    localVariableGen.setEnd(branchHandle2);
    InstructionList instructionList4 = compileDefaultRecursion(paramClassGenerator, methodGenerator, instructionHandle1);
    InstructionHandle instructionHandle2 = instructionList4.getStart();
    InstructionList instructionList5 = compileDefaultText(paramClassGenerator, methodGenerator, instructionHandle1);
    InstructionHandle instructionHandle3 = instructionList5.getStart();
    int[] arrayOfInt = new int[14 + vector.size()];
    for (byte b1 = 0; b1 < arrayOfInt.length; b1++)
      arrayOfInt[b1] = b1; 
    boolean[] arrayOfBoolean1 = new boolean[arrayOfInt.length];
    boolean[] arrayOfBoolean2 = new boolean[arrayOfInt.length];
    for (byte b2 = 0; b2 < vector.size(); b2++) {
      String str = (String)vector.elementAt(b2);
      arrayOfBoolean1[b2 + 14] = isAttributeName(str);
      arrayOfBoolean2[b2 + 14] = isNamespaceName(str);
    } 
    compileTemplates(paramClassGenerator, methodGenerator, instructionHandle1);
    TestSeq testSeq1 = this._testSeq[1];
    InstructionHandle instructionHandle4 = instructionHandle2;
    if (testSeq1 != null)
      instructionHandle4 = testSeq1.compile(paramClassGenerator, methodGenerator, instructionHandle2); 
    TestSeq testSeq2 = this._testSeq[2];
    InstructionHandle instructionHandle5 = instructionHandle3;
    if (testSeq2 != null)
      instructionHandle5 = testSeq2.compile(paramClassGenerator, methodGenerator, instructionHandle5); 
    InstructionList instructionList6 = null;
    if (this._idxTestSeq != null) {
      branchHandle2.setTarget(this._idxTestSeq.compile(paramClassGenerator, methodGenerator, instructionList2.getStart()));
      instructionList6 = this._idxTestSeq.getInstructionList();
    } else {
      branchHandle2.setTarget(instructionList2.getStart());
    } 
    if (this._childNodeTestSeq != null) {
      double d1 = this._childNodeTestSeq.getPriority();
      int j = this._childNodeTestSeq.getPosition();
      double d2 = -1.7976931348623157E308D;
      int k = Integer.MIN_VALUE;
      if (testSeq1 != null) {
        d2 = testSeq1.getPriority();
        k = testSeq1.getPosition();
      } 
      if (d2 == NaND || d2 < d1 || (d2 == d1 && k < j))
        instructionHandle4 = this._childNodeTestSeq.compile(paramClassGenerator, methodGenerator, instructionHandle1); 
      TestSeq testSeq = this._testSeq[3];
      double d3 = -1.7976931348623157E308D;
      int m = Integer.MIN_VALUE;
      if (testSeq != null) {
        d3 = testSeq.getPriority();
        m = testSeq.getPosition();
      } 
      if (d3 == NaND || d3 < d1 || (d3 == d1 && m < j)) {
        instructionHandle3 = this._childNodeTestSeq.compile(paramClassGenerator, methodGenerator, instructionHandle1);
        this._testSeq[3] = this._childNodeTestSeq;
      } 
    } 
    InstructionHandle instructionHandle6 = instructionHandle4;
    InstructionList instructionList7 = compileNamespaces(paramClassGenerator, methodGenerator, arrayOfBoolean2, arrayOfBoolean1, false, instructionHandle4);
    if (instructionList7 != null)
      instructionHandle6 = instructionList7.getStart(); 
    InstructionHandle instructionHandle7 = instructionHandle5;
    InstructionList instructionList8 = compileNamespaces(paramClassGenerator, methodGenerator, arrayOfBoolean2, arrayOfBoolean1, true, instructionHandle5);
    if (instructionList8 != null)
      instructionHandle7 = instructionList8.getStart(); 
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[arrayOfInt.length];
    for (byte b3 = 14; b3 < arrayOfInstructionHandle.length; b3++) {
      TestSeq testSeq = this._testSeq[b3];
      if (arrayOfBoolean2[b3]) {
        if (arrayOfBoolean1[b3]) {
          arrayOfInstructionHandle[b3] = instructionHandle7;
        } else {
          arrayOfInstructionHandle[b3] = instructionHandle6;
        } 
      } else if (testSeq != null) {
        if (arrayOfBoolean1[b3]) {
          arrayOfInstructionHandle[b3] = testSeq.compile(paramClassGenerator, methodGenerator, instructionHandle7);
        } else {
          arrayOfInstructionHandle[b3] = testSeq.compile(paramClassGenerator, methodGenerator, instructionHandle6);
        } 
      } else {
        arrayOfInstructionHandle[b3] = instructionHandle1;
      } 
    } 
    arrayOfInstructionHandle[0] = (this._rootPattern != null) ? getTemplateInstructionHandle(this._rootPattern.getTemplate()) : instructionHandle2;
    arrayOfInstructionHandle[9] = (this._rootPattern != null) ? getTemplateInstructionHandle(this._rootPattern.getTemplate()) : instructionHandle2;
    arrayOfInstructionHandle[3] = (this._testSeq[3] != null) ? this._testSeq[3].compile(paramClassGenerator, methodGenerator, instructionHandle3) : instructionHandle3;
    arrayOfInstructionHandle[13] = instructionHandle1;
    arrayOfInstructionHandle[1] = instructionHandle6;
    arrayOfInstructionHandle[2] = instructionHandle7;
    InstructionHandle instructionHandle8 = instructionHandle1;
    if (this._childNodeTestSeq != null)
      instructionHandle8 = instructionHandle4; 
    if (this._testSeq[7] != null) {
      arrayOfInstructionHandle[7] = this._testSeq[7].compile(paramClassGenerator, methodGenerator, instructionHandle8);
    } else {
      arrayOfInstructionHandle[7] = instructionHandle8;
    } 
    InstructionHandle instructionHandle9 = instructionHandle1;
    if (this._childNodeTestSeq != null)
      instructionHandle9 = instructionHandle4; 
    arrayOfInstructionHandle[8] = (this._testSeq[8] != null) ? this._testSeq[8].compile(paramClassGenerator, methodGenerator, instructionHandle9) : instructionHandle9;
    arrayOfInstructionHandle[4] = instructionHandle1;
    arrayOfInstructionHandle[11] = instructionHandle1;
    arrayOfInstructionHandle[10] = instructionHandle1;
    arrayOfInstructionHandle[6] = instructionHandle1;
    arrayOfInstructionHandle[5] = instructionHandle1;
    arrayOfInstructionHandle[12] = instructionHandle1;
    int i;
    for (i = 14; i < arrayOfInstructionHandle.length; i++) {
      TestSeq testSeq = this._testSeq[i];
      if (testSeq == null || arrayOfBoolean2[i]) {
        if (arrayOfBoolean1[i]) {
          arrayOfInstructionHandle[i] = instructionHandle7;
        } else {
          arrayOfInstructionHandle[i] = instructionHandle6;
        } 
      } else if (arrayOfBoolean1[i]) {
        arrayOfInstructionHandle[i] = testSeq.compile(paramClassGenerator, methodGenerator, instructionHandle7);
      } else {
        arrayOfInstructionHandle[i] = testSeq.compile(paramClassGenerator, methodGenerator, instructionHandle6);
      } 
    } 
    if (instructionList6 != null)
      instructionList2.insert(instructionList6); 
    i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
    instructionList2.append(methodGenerator.loadDOM());
    instructionList2.append(new ILOAD(this._currentIndex));
    instructionList2.append(new INVOKEINTERFACE(i, 2));
    InstructionHandle instructionHandle10 = instructionList2.append(new SWITCH(arrayOfInt, arrayOfInstructionHandle, instructionHandle1));
    appendTestSequences(instructionList2);
    appendTemplateCode(instructionList2);
    if (instructionList7 != null)
      instructionList2.append(instructionList7); 
    if (instructionList8 != null)
      instructionList2.append(instructionList8); 
    instructionList2.append(instructionList4);
    instructionList2.append(instructionList5);
    instructionList1.append(instructionList2);
    instructionList1.append(instructionList3);
    peepHoleOptimization(methodGenerator);
    paramClassGenerator.addMethod(methodGenerator);
    if (this._importLevels != null)
      for (Map.Entry entry : this._importLevels.entrySet())
        compileApplyImports(paramClassGenerator, ((Integer)entry.getValue()).intValue(), ((Integer)entry.getKey()).intValue());  
  }
  
  private void compileTemplateCalls(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, InstructionHandle paramInstructionHandle, int paramInt1, int paramInt2) {
    for (Template template : this._neededTemplates.keySet()) {
      int i = template.getImportPrecedence();
      if (i >= paramInt1 && i < paramInt2) {
        if (template.hasContents()) {
          InstructionList instructionList = template.compile(paramClassGenerator, paramMethodGenerator);
          instructionList.append(new GOTO_W(paramInstructionHandle));
          this._templateILs.put(template, instructionList);
          this._templateIHs.put(template, instructionList.getStart());
          continue;
        } 
        this._templateIHs.put(template, paramInstructionHandle);
      } 
    } 
  }
  
  public void compileApplyImports(ClassGenerator paramClassGenerator, int paramInt1, int paramInt2) {
    XSLTC xSLTC = paramClassGenerator.getParser().getXSLTC();
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    Vector vector1 = xSLTC.getNamesIndex();
    this._namedTemplates = new HashMap();
    this._neededTemplates = new HashMap();
    this._templateIHs = new HashMap();
    this._templateILs = new HashMap();
    this._patternGroups = new Vector[32];
    this._rootPattern = null;
    Vector vector2 = this._templates;
    this._templates = new Vector();
    Enumeration enumeration = vector2.elements();
    while (enumeration.hasMoreElements()) {
      Template template = (Template)enumeration.nextElement();
      int j = template.getImportPrecedence();
      if (j >= paramInt1 && j < paramInt2)
        addTemplate(template); 
    } 
    processPatterns(this._keys);
    Type[] arrayOfType = new Type[4];
    arrayOfType[0] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    arrayOfType[1] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    arrayOfType[2] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
    arrayOfType[3] = Type.INT;
    String[] arrayOfString = new String[4];
    arrayOfString[0] = "document";
    arrayOfString[1] = "iterator";
    arrayOfString[2] = "handler";
    arrayOfString[3] = "node";
    InstructionList instructionList1 = new InstructionList();
    MethodGenerator methodGenerator = new MethodGenerator(17, Type.VOID, arrayOfType, arrayOfString, functionName() + '_' + paramInt2, getClassName(), instructionList1, paramClassGenerator.getConstantPool());
    methodGenerator.addException("com.sun.org.apache.xalan.internal.xsltc.TransletException");
    LocalVariableGen localVariableGen = methodGenerator.addLocalVariable2("current", Type.INT, null);
    this._currentIndex = localVariableGen.getIndex();
    instructionList1.append(new ILOAD(methodGenerator.getLocalIndex("node")));
    localVariableGen.setStart(instructionList1.append(new ISTORE(this._currentIndex)));
    InstructionList instructionList2 = new InstructionList();
    instructionList2.append(NOP);
    InstructionList instructionList3 = new InstructionList();
    instructionList3.append(RETURN);
    InstructionHandle instructionHandle1 = instructionList3.getStart();
    InstructionList instructionList4 = compileDefaultRecursion(paramClassGenerator, methodGenerator, instructionHandle1);
    InstructionHandle instructionHandle2 = instructionList4.getStart();
    InstructionList instructionList5 = compileDefaultText(paramClassGenerator, methodGenerator, instructionHandle1);
    InstructionHandle instructionHandle3 = instructionList5.getStart();
    int[] arrayOfInt = new int[14 + vector1.size()];
    for (byte b1 = 0; b1 < arrayOfInt.length; b1++)
      arrayOfInt[b1] = b1; 
    boolean[] arrayOfBoolean1 = new boolean[arrayOfInt.length];
    boolean[] arrayOfBoolean2 = new boolean[arrayOfInt.length];
    for (byte b2 = 0; b2 < vector1.size(); b2++) {
      String str = (String)vector1.elementAt(b2);
      arrayOfBoolean1[b2 + 14] = isAttributeName(str);
      arrayOfBoolean2[b2 + 14] = isNamespaceName(str);
    } 
    compileTemplateCalls(paramClassGenerator, methodGenerator, instructionHandle1, paramInt1, paramInt2);
    TestSeq testSeq1 = this._testSeq[1];
    InstructionHandle instructionHandle4 = instructionHandle2;
    if (testSeq1 != null)
      instructionHandle4 = testSeq1.compile(paramClassGenerator, methodGenerator, instructionHandle1); 
    TestSeq testSeq2 = this._testSeq[2];
    InstructionHandle instructionHandle5 = instructionHandle1;
    if (testSeq2 != null)
      instructionHandle5 = testSeq2.compile(paramClassGenerator, methodGenerator, instructionHandle5); 
    InstructionList instructionList6 = null;
    if (this._idxTestSeq != null)
      instructionList6 = this._idxTestSeq.getInstructionList(); 
    if (this._childNodeTestSeq != null) {
      double d1 = this._childNodeTestSeq.getPriority();
      int j = this._childNodeTestSeq.getPosition();
      double d2 = -1.7976931348623157E308D;
      int k = Integer.MIN_VALUE;
      if (testSeq1 != null) {
        d2 = testSeq1.getPriority();
        k = testSeq1.getPosition();
      } 
      if (d2 == NaND || d2 < d1 || (d2 == d1 && k < j))
        instructionHandle4 = this._childNodeTestSeq.compile(paramClassGenerator, methodGenerator, instructionHandle1); 
      TestSeq testSeq = this._testSeq[3];
      double d3 = -1.7976931348623157E308D;
      int m = Integer.MIN_VALUE;
      if (testSeq != null) {
        d3 = testSeq.getPriority();
        m = testSeq.getPosition();
      } 
      if (d3 == NaND || d3 < d1 || (d3 == d1 && m < j)) {
        instructionHandle3 = this._childNodeTestSeq.compile(paramClassGenerator, methodGenerator, instructionHandle1);
        this._testSeq[3] = this._childNodeTestSeq;
      } 
    } 
    InstructionHandle instructionHandle6 = instructionHandle4;
    InstructionList instructionList7 = compileNamespaces(paramClassGenerator, methodGenerator, arrayOfBoolean2, arrayOfBoolean1, false, instructionHandle4);
    if (instructionList7 != null)
      instructionHandle6 = instructionList7.getStart(); 
    InstructionList instructionList8 = compileNamespaces(paramClassGenerator, methodGenerator, arrayOfBoolean2, arrayOfBoolean1, true, instructionHandle5);
    InstructionHandle instructionHandle7 = instructionHandle5;
    if (instructionList8 != null)
      instructionHandle7 = instructionList8.getStart(); 
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[arrayOfInt.length];
    for (byte b3 = 14; b3 < arrayOfInstructionHandle.length; b3++) {
      TestSeq testSeq = this._testSeq[b3];
      if (arrayOfBoolean2[b3]) {
        if (arrayOfBoolean1[b3]) {
          arrayOfInstructionHandle[b3] = instructionHandle7;
        } else {
          arrayOfInstructionHandle[b3] = instructionHandle6;
        } 
      } else if (testSeq != null) {
        if (arrayOfBoolean1[b3]) {
          arrayOfInstructionHandle[b3] = testSeq.compile(paramClassGenerator, methodGenerator, instructionHandle7);
        } else {
          arrayOfInstructionHandle[b3] = testSeq.compile(paramClassGenerator, methodGenerator, instructionHandle6);
        } 
      } else {
        arrayOfInstructionHandle[b3] = instructionHandle1;
      } 
    } 
    arrayOfInstructionHandle[0] = (this._rootPattern != null) ? getTemplateInstructionHandle(this._rootPattern.getTemplate()) : instructionHandle2;
    arrayOfInstructionHandle[9] = (this._rootPattern != null) ? getTemplateInstructionHandle(this._rootPattern.getTemplate()) : instructionHandle2;
    arrayOfInstructionHandle[3] = (this._testSeq[3] != null) ? this._testSeq[3].compile(paramClassGenerator, methodGenerator, instructionHandle3) : instructionHandle3;
    arrayOfInstructionHandle[13] = instructionHandle1;
    arrayOfInstructionHandle[1] = instructionHandle6;
    arrayOfInstructionHandle[2] = instructionHandle7;
    InstructionHandle instructionHandle8 = instructionHandle1;
    if (this._childNodeTestSeq != null)
      instructionHandle8 = instructionHandle4; 
    if (this._testSeq[7] != null) {
      arrayOfInstructionHandle[7] = this._testSeq[7].compile(paramClassGenerator, methodGenerator, instructionHandle8);
    } else {
      arrayOfInstructionHandle[7] = instructionHandle8;
    } 
    InstructionHandle instructionHandle9 = instructionHandle1;
    if (this._childNodeTestSeq != null)
      instructionHandle9 = instructionHandle4; 
    arrayOfInstructionHandle[8] = (this._testSeq[8] != null) ? this._testSeq[8].compile(paramClassGenerator, methodGenerator, instructionHandle9) : instructionHandle9;
    arrayOfInstructionHandle[4] = instructionHandle1;
    arrayOfInstructionHandle[11] = instructionHandle1;
    arrayOfInstructionHandle[10] = instructionHandle1;
    arrayOfInstructionHandle[6] = instructionHandle1;
    arrayOfInstructionHandle[5] = instructionHandle1;
    arrayOfInstructionHandle[12] = instructionHandle1;
    int i;
    for (i = 14; i < arrayOfInstructionHandle.length; i++) {
      TestSeq testSeq = this._testSeq[i];
      if (testSeq == null || arrayOfBoolean2[i]) {
        if (arrayOfBoolean1[i]) {
          arrayOfInstructionHandle[i] = instructionHandle7;
        } else {
          arrayOfInstructionHandle[i] = instructionHandle6;
        } 
      } else if (arrayOfBoolean1[i]) {
        arrayOfInstructionHandle[i] = testSeq.compile(paramClassGenerator, methodGenerator, instructionHandle7);
      } else {
        arrayOfInstructionHandle[i] = testSeq.compile(paramClassGenerator, methodGenerator, instructionHandle6);
      } 
    } 
    if (instructionList6 != null)
      instructionList2.insert(instructionList6); 
    i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
    instructionList2.append(methodGenerator.loadDOM());
    instructionList2.append(new ILOAD(this._currentIndex));
    instructionList2.append(new INVOKEINTERFACE(i, 2));
    InstructionHandle instructionHandle10 = instructionList2.append(new SWITCH(arrayOfInt, arrayOfInstructionHandle, instructionHandle1));
    appendTestSequences(instructionList2);
    appendTemplateCode(instructionList2);
    if (instructionList7 != null)
      instructionList2.append(instructionList7); 
    if (instructionList8 != null)
      instructionList2.append(instructionList8); 
    instructionList2.append(instructionList4);
    instructionList2.append(instructionList5);
    instructionList1.append(instructionList2);
    localVariableGen.setEnd(instructionList2.getEnd());
    instructionList1.append(instructionList3);
    peepHoleOptimization(methodGenerator);
    paramClassGenerator.addMethod(methodGenerator);
    this._templates = vector2;
  }
  
  private void peepHoleOptimization(MethodGenerator paramMethodGenerator) {
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    InstructionFinder instructionFinder = new InstructionFinder(instructionList);
    String str = "loadinstruction pop";
    Iterator iterator = instructionFinder.search(str);
    while (iterator.hasNext()) {
      InstructionHandle[] arrayOfInstructionHandle = (InstructionHandle[])iterator.next();
      try {
        if (!arrayOfInstructionHandle[0].hasTargeters() && !arrayOfInstructionHandle[1].hasTargeters())
          instructionList.delete(arrayOfInstructionHandle[0], arrayOfInstructionHandle[1]); 
      } catch (TargetLostException targetLostException) {}
    } 
    str = "iload iload swap istore";
    iterator = instructionFinder.search(str);
    while (iterator.hasNext()) {
      InstructionHandle[] arrayOfInstructionHandle = (InstructionHandle[])iterator.next();
      try {
        ILOAD iLOAD1 = (ILOAD)arrayOfInstructionHandle[0].getInstruction();
        ILOAD iLOAD2 = (ILOAD)arrayOfInstructionHandle[1].getInstruction();
        ISTORE iSTORE = (ISTORE)arrayOfInstructionHandle[3].getInstruction();
        if (!arrayOfInstructionHandle[1].hasTargeters() && !arrayOfInstructionHandle[2].hasTargeters() && !arrayOfInstructionHandle[3].hasTargeters() && iLOAD1.getIndex() == iLOAD2.getIndex() && iLOAD2.getIndex() == iSTORE.getIndex())
          instructionList.delete(arrayOfInstructionHandle[1], arrayOfInstructionHandle[3]); 
      } catch (TargetLostException targetLostException) {}
    } 
    str = "loadinstruction loadinstruction swap";
    iterator = instructionFinder.search(str);
    while (iterator.hasNext()) {
      InstructionHandle[] arrayOfInstructionHandle = (InstructionHandle[])iterator.next();
      try {
        if (!arrayOfInstructionHandle[0].hasTargeters() && !arrayOfInstructionHandle[1].hasTargeters() && !arrayOfInstructionHandle[2].hasTargeters()) {
          Instruction instruction = arrayOfInstructionHandle[1].getInstruction();
          instructionList.insert(arrayOfInstructionHandle[0], instruction);
          instructionList.delete(arrayOfInstructionHandle[1], arrayOfInstructionHandle[2]);
        } 
      } catch (TargetLostException targetLostException) {}
    } 
    str = "aload aload";
    iterator = instructionFinder.search(str);
    while (iterator.hasNext()) {
      InstructionHandle[] arrayOfInstructionHandle = (InstructionHandle[])iterator.next();
      try {
        if (!arrayOfInstructionHandle[1].hasTargeters()) {
          ALOAD aLOAD1 = (ALOAD)arrayOfInstructionHandle[0].getInstruction();
          ALOAD aLOAD2 = (ALOAD)arrayOfInstructionHandle[1].getInstruction();
          if (aLOAD1.getIndex() == aLOAD2.getIndex()) {
            instructionList.insert(arrayOfInstructionHandle[1], new DUP());
            instructionList.delete(arrayOfInstructionHandle[1]);
          } 
        } 
      } catch (TargetLostException targetLostException) {}
    } 
  }
  
  public InstructionHandle getTemplateInstructionHandle(Template paramTemplate) { return (InstructionHandle)this._templateIHs.get(paramTemplate); }
  
  private static boolean isAttributeName(String paramString) {
    int i = paramString.lastIndexOf(':') + 1;
    return (paramString.charAt(i) == '@');
  }
  
  private static boolean isNamespaceName(String paramString) {
    int i = paramString.lastIndexOf(':');
    return (i > -1 && paramString.charAt(paramString.length() - 1) == '*');
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Mode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */