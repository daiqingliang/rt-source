package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.NOP;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.TABLESWITCH;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.CompareGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSortRecordFactGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSortRecordGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.ArrayList;
import java.util.Vector;

final class Sort extends Instruction implements Closure {
  private Expression _select;
  
  private AttributeValue _order;
  
  private AttributeValue _caseOrder;
  
  private AttributeValue _dataType;
  
  private String _lang;
  
  private String _className = null;
  
  private ArrayList<VariableRefBase> _closureVars = null;
  
  private boolean _needsSortRecordFactory = false;
  
  public boolean inInnerClass() { return (this._className != null); }
  
  public Closure getParentClosure() { return null; }
  
  public String getInnerClassName() { return this._className; }
  
  public void addVariable(VariableRefBase paramVariableRefBase) {
    if (this._closureVars == null)
      this._closureVars = new ArrayList(); 
    if (!this._closureVars.contains(paramVariableRefBase)) {
      this._closureVars.add(paramVariableRefBase);
      this._needsSortRecordFactory = true;
    } 
  }
  
  private void setInnerClassName(String paramString) { this._className = paramString; }
  
  public void parseContents(Parser paramParser) {
    SyntaxTreeNode syntaxTreeNode = getParent();
    if (!(syntaxTreeNode instanceof ApplyTemplates) && !(syntaxTreeNode instanceof ForEach)) {
      reportError(this, paramParser, "STRAY_SORT_ERR", null);
      return;
    } 
    this._select = paramParser.parseExpression(this, "select", "string(.)");
    String str = getAttribute("order");
    if (str.length() == 0)
      str = "ascending"; 
    this._order = AttributeValue.create(this, str, paramParser);
    str = getAttribute("data-type");
    if (str.length() == 0)
      try {
        Type type = this._select.typeCheck(paramParser.getSymbolTable());
        if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType) {
          str = "number";
        } else {
          str = "text";
        } 
      } catch (TypeCheckError typeCheckError) {
        str = "text";
      }  
    this._dataType = AttributeValue.create(this, str, paramParser);
    this._lang = getAttribute("lang");
    str = getAttribute("case-order");
    this._caseOrder = AttributeValue.create(this, str, paramParser);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type = this._select.typeCheck(paramSymbolTable);
    if (!(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType))
      this._select = new CastExpr(this._select, Type.String); 
    this._order.typeCheck(paramSymbolTable);
    this._caseOrder.typeCheck(paramSymbolTable);
    this._dataType.typeCheck(paramSymbolTable);
    return Type.Void;
  }
  
  public void translateSortType(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { this._dataType.translate(paramClassGenerator, paramMethodGenerator); }
  
  public void translateSortOrder(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { this._order.translate(paramClassGenerator, paramMethodGenerator); }
  
  public void translateCaseOrder(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { this._caseOrder.translate(paramClassGenerator, paramMethodGenerator); }
  
  public void translateLang(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(new PUSH(constantPoolGen, this._lang));
  }
  
  public void translateSelect(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { this._select.translate(paramClassGenerator, paramMethodGenerator); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
  
  public static void translateSortIterator(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Expression paramExpression, Vector<Sort> paramVector) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.SortingIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory;)V");
    LocalVariableGen localVariableGen1 = paramMethodGenerator.addLocalVariable("sort_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    LocalVariableGen localVariableGen2 = paramMethodGenerator.addLocalVariable("sort_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory;"), null, null);
    if (paramExpression == null) {
      int j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(new PUSH(constantPoolGen, 3));
      instructionList.append(new INVOKEINTERFACE(j, 2));
    } else {
      paramExpression.translate(paramClassGenerator, paramMethodGenerator);
    } 
    localVariableGen1.setStart(instructionList.append(new ASTORE(localVariableGen1.getIndex())));
    compileSortRecordFactory(paramVector, paramClassGenerator, paramMethodGenerator);
    localVariableGen2.setStart(instructionList.append(new ASTORE(localVariableGen2.getIndex())));
    instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.SortingIterator")));
    instructionList.append(DUP);
    localVariableGen1.setEnd(instructionList.append(new ALOAD(localVariableGen1.getIndex())));
    localVariableGen2.setEnd(instructionList.append(new ALOAD(localVariableGen2.getIndex())));
    instructionList.append(new INVOKESPECIAL(i));
  }
  
  public static void compileSortRecordFactory(Vector<Sort> paramVector, ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    String str1 = compileSortRecord(paramVector, paramClassGenerator, paramMethodGenerator);
    boolean bool = false;
    int i = paramVector.size();
    for (byte b1 = 0; b1 < i; b1++) {
      Sort sort = (Sort)paramVector.elementAt(b1);
      bool |= sort._needsSortRecordFactory;
    } 
    String str2 = "com/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory";
    if (bool)
      str2 = compileSortRecordFactory(paramVector, paramClassGenerator, paramMethodGenerator, str1); 
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    LocalVariableGen localVariableGen1 = paramMethodGenerator.addLocalVariable("sort_order_tmp", Util.getJCRefType("[Ljava/lang/String;"), null, null);
    instructionList.append(new PUSH(constantPoolGen, i));
    instructionList.append(new ANEWARRAY(constantPoolGen.addClass("java.lang.String")));
    for (byte b2 = 0; b2 < i; b2++) {
      Sort sort = (Sort)paramVector.elementAt(b2);
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, b2));
      sort.translateSortOrder(paramClassGenerator, paramMethodGenerator);
      instructionList.append(AASTORE);
    } 
    localVariableGen1.setStart(instructionList.append(new ASTORE(localVariableGen1.getIndex())));
    LocalVariableGen localVariableGen2 = paramMethodGenerator.addLocalVariable("sort_type_tmp", Util.getJCRefType("[Ljava/lang/String;"), null, null);
    instructionList.append(new PUSH(constantPoolGen, i));
    instructionList.append(new ANEWARRAY(constantPoolGen.addClass("java.lang.String")));
    for (byte b3 = 0; b3 < i; b3++) {
      Sort sort = (Sort)paramVector.elementAt(b3);
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, b3));
      sort.translateSortType(paramClassGenerator, paramMethodGenerator);
      instructionList.append(AASTORE);
    } 
    localVariableGen2.setStart(instructionList.append(new ASTORE(localVariableGen2.getIndex())));
    LocalVariableGen localVariableGen3 = paramMethodGenerator.addLocalVariable("sort_lang_tmp", Util.getJCRefType("[Ljava/lang/String;"), null, null);
    instructionList.append(new PUSH(constantPoolGen, i));
    instructionList.append(new ANEWARRAY(constantPoolGen.addClass("java.lang.String")));
    for (byte b4 = 0; b4 < i; b4++) {
      Sort sort = (Sort)paramVector.elementAt(b4);
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, b4));
      sort.translateLang(paramClassGenerator, paramMethodGenerator);
      instructionList.append(AASTORE);
    } 
    localVariableGen3.setStart(instructionList.append(new ASTORE(localVariableGen3.getIndex())));
    LocalVariableGen localVariableGen4 = paramMethodGenerator.addLocalVariable("sort_case_order_tmp", Util.getJCRefType("[Ljava/lang/String;"), null, null);
    instructionList.append(new PUSH(constantPoolGen, i));
    instructionList.append(new ANEWARRAY(constantPoolGen.addClass("java.lang.String")));
    for (byte b5 = 0; b5 < i; b5++) {
      Sort sort = (Sort)paramVector.elementAt(b5);
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, b5));
      sort.translateCaseOrder(paramClassGenerator, paramMethodGenerator);
      instructionList.append(AASTORE);
    } 
    localVariableGen4.setStart(instructionList.append(new ASTORE(localVariableGen4.getIndex())));
    instructionList.append(new NEW(constantPoolGen.addClass(str2)));
    instructionList.append(DUP);
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(new PUSH(constantPoolGen, str1));
    instructionList.append(paramClassGenerator.loadTranslet());
    localVariableGen1.setEnd(instructionList.append(new ALOAD(localVariableGen1.getIndex())));
    localVariableGen2.setEnd(instructionList.append(new ALOAD(localVariableGen2.getIndex())));
    localVariableGen3.setEnd(instructionList.append(new ALOAD(localVariableGen3.getIndex())));
    localVariableGen4.setEnd(instructionList.append(new ALOAD(localVariableGen4.getIndex())));
    instructionList.append(new INVOKESPECIAL(constantPoolGen.addMethodref(str2, "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;)V")));
    ArrayList arrayList = new ArrayList();
    for (byte b6 = 0; b6 < i; b6++) {
      Sort sort = (Sort)paramVector.get(b6);
      boolean bool1 = (sort._closureVars == null) ? 0 : sort._closureVars.size();
      for (byte b = 0; b < bool1; b++) {
        VariableRefBase variableRefBase = (VariableRefBase)sort._closureVars.get(b);
        if (!arrayList.contains(variableRefBase)) {
          VariableBase variableBase = variableRefBase.getVariable();
          instructionList.append(DUP);
          instructionList.append(variableBase.loadInstruction());
          instructionList.append(new PUTFIELD(constantPoolGen.addFieldref(str2, variableBase.getEscapedName(), variableBase.getType().toSignature())));
          arrayList.add(variableRefBase);
        } 
      } 
    } 
  }
  
  public static String compileSortRecordFactory(Vector<Sort> paramVector, ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, String paramString) {
    XSLTC xSLTC = ((Sort)paramVector.firstElement()).getXSLTC();
    String str = xSLTC.getHelperClassName();
    NodeSortRecordFactGenerator nodeSortRecordFactGenerator = new NodeSortRecordFactGenerator(str, "com/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory", str + ".java", 49, new String[0], paramClassGenerator.getStylesheet());
    ConstantPoolGen constantPoolGen = nodeSortRecordFactGenerator.getConstantPool();
    int i = paramVector.size();
    ArrayList arrayList = new ArrayList();
    for (byte b1 = 0; b1 < i; b1++) {
      Sort sort = (Sort)paramVector.get(b1);
      boolean bool = (sort._closureVars == null) ? 0 : sort._closureVars.size();
      for (byte b = 0; b < bool; b++) {
        VariableRefBase variableRefBase = (VariableRefBase)sort._closureVars.get(b);
        if (!arrayList.contains(variableRefBase)) {
          VariableBase variableBase = variableRefBase.getVariable();
          nodeSortRecordFactGenerator.addField(new Field(1, constantPoolGen.addUtf8(variableBase.getEscapedName()), constantPoolGen.addUtf8(variableBase.getType().toSignature()), null, constantPoolGen.getConstantPool()));
          arrayList.add(variableRefBase);
        } 
      } 
    } 
    Type[] arrayOfType = new Type[7];
    arrayOfType[0] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    arrayOfType[1] = Util.getJCRefType("Ljava/lang/String;");
    arrayOfType[2] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/Translet;");
    arrayOfType[3] = Util.getJCRefType("[Ljava/lang/String;");
    arrayOfType[4] = Util.getJCRefType("[Ljava/lang/String;");
    arrayOfType[5] = Util.getJCRefType("[Ljava/lang/String;");
    arrayOfType[6] = Util.getJCRefType("[Ljava/lang/String;");
    String[] arrayOfString = new String[7];
    arrayOfString[0] = "document";
    arrayOfString[1] = "className";
    arrayOfString[2] = "translet";
    arrayOfString[3] = "order";
    arrayOfString[4] = "type";
    arrayOfString[5] = "lang";
    arrayOfString[6] = "case_order";
    InstructionList instructionList = new InstructionList();
    MethodGenerator methodGenerator1 = new MethodGenerator(1, Type.VOID, arrayOfType, arrayOfString, "<init>", str, instructionList, constantPoolGen);
    instructionList.append(ALOAD_0);
    instructionList.append(ALOAD_1);
    instructionList.append(ALOAD_2);
    instructionList.append(new ALOAD(3));
    instructionList.append(new ALOAD(4));
    instructionList.append(new ALOAD(5));
    instructionList.append(new ALOAD(6));
    instructionList.append(new ALOAD(7));
    instructionList.append(new INVOKESPECIAL(constantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory", "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;)V")));
    instructionList.append(RETURN);
    instructionList = new InstructionList();
    MethodGenerator methodGenerator2 = new MethodGenerator(1, Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecord;"), new Type[] { Type.INT, Type.INT }, new String[] { "node", "last" }, "makeNodeSortRecord", str, instructionList, constantPoolGen);
    instructionList.append(ALOAD_0);
    instructionList.append(ILOAD_1);
    instructionList.append(ILOAD_2);
    instructionList.append(new INVOKESPECIAL(constantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory", "makeNodeSortRecord", "(II)Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecord;")));
    instructionList.append(DUP);
    instructionList.append(new CHECKCAST(constantPoolGen.addClass(paramString)));
    int j = arrayList.size();
    for (byte b2 = 0; b2 < j; b2++) {
      VariableRefBase variableRefBase = (VariableRefBase)arrayList.get(b2);
      VariableBase variableBase = variableRefBase.getVariable();
      Type type = variableBase.getType();
      instructionList.append(DUP);
      instructionList.append(ALOAD_0);
      instructionList.append(new GETFIELD(constantPoolGen.addFieldref(str, variableBase.getEscapedName(), type.toSignature())));
      instructionList.append(new PUTFIELD(constantPoolGen.addFieldref(paramString, variableBase.getEscapedName(), type.toSignature())));
    } 
    instructionList.append(POP);
    instructionList.append(ARETURN);
    methodGenerator1.setMaxLocals();
    methodGenerator1.setMaxStack();
    nodeSortRecordFactGenerator.addMethod(methodGenerator1);
    methodGenerator2.setMaxLocals();
    methodGenerator2.setMaxStack();
    nodeSortRecordFactGenerator.addMethod(methodGenerator2);
    xSLTC.dumpClass(nodeSortRecordFactGenerator.getJavaClass());
    return str;
  }
  
  private static String compileSortRecord(Vector<Sort> paramVector, ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    XSLTC xSLTC = ((Sort)paramVector.firstElement()).getXSLTC();
    String str = xSLTC.getHelperClassName();
    NodeSortRecordGenerator nodeSortRecordGenerator = new NodeSortRecordGenerator(str, "com.sun.org.apache.xalan.internal.xsltc.dom.NodeSortRecord", "sort$0.java", 49, new String[0], paramClassGenerator.getStylesheet());
    ConstantPoolGen constantPoolGen = nodeSortRecordGenerator.getConstantPool();
    int i = paramVector.size();
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < i; b++) {
      Sort sort = (Sort)paramVector.get(b);
      sort.setInnerClassName(str);
      boolean bool = (sort._closureVars == null) ? 0 : sort._closureVars.size();
      for (byte b1 = 0; b1 < bool; b1++) {
        VariableRefBase variableRefBase = (VariableRefBase)sort._closureVars.get(b1);
        if (!arrayList.contains(variableRefBase)) {
          VariableBase variableBase = variableRefBase.getVariable();
          nodeSortRecordGenerator.addField(new Field(1, constantPoolGen.addUtf8(variableBase.getEscapedName()), constantPoolGen.addUtf8(variableBase.getType().toSignature()), null, constantPoolGen.getConstantPool()));
          arrayList.add(variableRefBase);
        } 
      } 
    } 
    MethodGenerator methodGenerator1 = compileInit(nodeSortRecordGenerator, constantPoolGen, str);
    MethodGenerator methodGenerator2 = compileExtract(paramVector, nodeSortRecordGenerator, constantPoolGen, str);
    nodeSortRecordGenerator.addMethod(methodGenerator1);
    nodeSortRecordGenerator.addMethod(methodGenerator2);
    xSLTC.dumpClass(nodeSortRecordGenerator.getJavaClass());
    return str;
  }
  
  private static MethodGenerator compileInit(NodeSortRecordGenerator paramNodeSortRecordGenerator, ConstantPoolGen paramConstantPoolGen, String paramString) {
    InstructionList instructionList = new InstructionList();
    MethodGenerator methodGenerator = new MethodGenerator(1, Type.VOID, null, null, "<init>", paramString, instructionList, paramConstantPoolGen);
    instructionList.append(ALOAD_0);
    instructionList.append(new INVOKESPECIAL(paramConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeSortRecord", "<init>", "()V")));
    instructionList.append(RETURN);
    return methodGenerator;
  }
  
  private static MethodGenerator compileExtract(Vector<Sort> paramVector, NodeSortRecordGenerator paramNodeSortRecordGenerator, ConstantPoolGen paramConstantPoolGen, String paramString) {
    InstructionList instructionList = new InstructionList();
    CompareGenerator compareGenerator = new CompareGenerator(17, Type.STRING, new Type[] { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), Type.INT, Type.INT, Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;"), Type.INT }, new String[] { "dom", "current", "level", "translet", "last" }, "extractValueFromDOM", paramString, instructionList, paramConstantPoolGen);
    int i = paramVector.size();
    int[] arrayOfInt = new int[i];
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[i];
    InstructionHandle instructionHandle = null;
    if (i > 1) {
      instructionList.append(new ILOAD(compareGenerator.getLocalIndex("level")));
      instructionHandle = instructionList.append(new NOP());
    } 
    for (byte b = 0; b < i; b++) {
      arrayOfInt[b] = b;
      Sort sort = (Sort)paramVector.elementAt(b);
      arrayOfInstructionHandle[b] = instructionList.append(NOP);
      sort.translateSelect(paramNodeSortRecordGenerator, compareGenerator);
      instructionList.append(ARETURN);
    } 
    if (i > 1) {
      InstructionHandle instructionHandle1 = instructionList.append(new PUSH(paramConstantPoolGen, ""));
      instructionList.insert(instructionHandle, new TABLESWITCH(arrayOfInt, arrayOfInstructionHandle, instructionHandle1));
      instructionList.append(ARETURN);
    } 
    return compareGenerator;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Sort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */