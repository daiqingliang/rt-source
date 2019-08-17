package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFNONNULL;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MatchGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeCounterGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.ArrayList;

final class Number extends Instruction implements Closure {
  private static final int LEVEL_SINGLE = 0;
  
  private static final int LEVEL_MULTIPLE = 1;
  
  private static final int LEVEL_ANY = 2;
  
  private static final String[] ClassNames = { "com.sun.org.apache.xalan.internal.xsltc.dom.SingleNodeCounter", "com.sun.org.apache.xalan.internal.xsltc.dom.MultipleNodeCounter", "com.sun.org.apache.xalan.internal.xsltc.dom.AnyNodeCounter" };
  
  private static final String[] FieldNames = { "___single_node_counter", "___multiple_node_counter", "___any_node_counter" };
  
  private Pattern _from = null;
  
  private Pattern _count = null;
  
  private Expression _value = null;
  
  private AttributeValueTemplate _lang = null;
  
  private AttributeValueTemplate _format = null;
  
  private AttributeValueTemplate _letterValue = null;
  
  private AttributeValueTemplate _groupingSeparator = null;
  
  private AttributeValueTemplate _groupingSize = null;
  
  private int _level = 0;
  
  private boolean _formatNeeded = false;
  
  private String _className = null;
  
  private ArrayList _closureVars = null;
  
  public boolean inInnerClass() { return (this._className != null); }
  
  public Closure getParentClosure() { return null; }
  
  public String getInnerClassName() { return this._className; }
  
  public void addVariable(VariableRefBase paramVariableRefBase) {
    if (this._closureVars == null)
      this._closureVars = new ArrayList(); 
    if (!this._closureVars.contains(paramVariableRefBase))
      this._closureVars.add(paramVariableRefBase); 
  }
  
  public void parseContents(Parser paramParser) {
    int i = this._attributes.getLength();
    for (byte b = 0; b < i; b++) {
      String str1 = this._attributes.getQName(b);
      String str2 = this._attributes.getValue(b);
      if (str1.equals("value")) {
        this._value = paramParser.parseExpression(this, str1, null);
      } else if (str1.equals("count")) {
        this._count = paramParser.parsePattern(this, str1, null);
      } else if (str1.equals("from")) {
        this._from = paramParser.parsePattern(this, str1, null);
      } else if (str1.equals("level")) {
        if (str2.equals("single")) {
          this._level = 0;
        } else if (str2.equals("multiple")) {
          this._level = 1;
        } else if (str2.equals("any")) {
          this._level = 2;
        } 
      } else if (str1.equals("format")) {
        this._format = new AttributeValueTemplate(str2, paramParser, this);
        this._formatNeeded = true;
      } else if (str1.equals("lang")) {
        this._lang = new AttributeValueTemplate(str2, paramParser, this);
        this._formatNeeded = true;
      } else if (str1.equals("letter-value")) {
        this._letterValue = new AttributeValueTemplate(str2, paramParser, this);
        this._formatNeeded = true;
      } else if (str1.equals("grouping-separator")) {
        this._groupingSeparator = new AttributeValueTemplate(str2, paramParser, this);
        this._formatNeeded = true;
      } else if (str1.equals("grouping-size")) {
        this._groupingSize = new AttributeValueTemplate(str2, paramParser, this);
        this._formatNeeded = true;
      } 
    } 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    if (this._value != null) {
      Type type = this._value.typeCheck(paramSymbolTable);
      if (!(type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType))
        this._value = new CastExpr(this._value, Type.Real); 
    } 
    if (this._count != null)
      this._count.typeCheck(paramSymbolTable); 
    if (this._from != null)
      this._from.typeCheck(paramSymbolTable); 
    if (this._format != null)
      this._format.typeCheck(paramSymbolTable); 
    if (this._lang != null)
      this._lang.typeCheck(paramSymbolTable); 
    if (this._letterValue != null)
      this._letterValue.typeCheck(paramSymbolTable); 
    if (this._groupingSeparator != null)
      this._groupingSeparator.typeCheck(paramSymbolTable); 
    if (this._groupingSize != null)
      this._groupingSize.typeCheck(paramSymbolTable); 
    return Type.Void;
  }
  
  public boolean hasValue() { return (this._value != null); }
  
  public boolean isDefault() { return (this._from == null && this._count == null); }
  
  private void compileDefault(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int[] arrayOfInt = getXSLTC().getNumberFieldIndexes();
    if (arrayOfInt[this._level] == -1) {
      Field field = new Field(2, constantPoolGen.addUtf8(FieldNames[this._level]), constantPoolGen.addUtf8("Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;"), null, constantPoolGen.getConstantPool());
      paramClassGenerator.addField(field);
      arrayOfInt[this._level] = constantPoolGen.addFieldref(paramClassGenerator.getClassName(), FieldNames[this._level], "Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
    } 
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new GETFIELD(arrayOfInt[this._level]));
    BranchHandle branchHandle1 = instructionList.append(new IFNONNULL(null));
    int i = constantPoolGen.addMethodref(ClassNames[this._level], "getDefaultNodeCounter", "(Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(paramMethodGenerator.loadIterator());
    instructionList.append(new INVOKESTATIC(i));
    instructionList.append(DUP);
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(SWAP);
    instructionList.append(new PUTFIELD(arrayOfInt[this._level]));
    BranchHandle branchHandle2 = instructionList.append(new GOTO(null));
    branchHandle1.setTarget(instructionList.append(paramClassGenerator.loadTranslet()));
    instructionList.append(new GETFIELD(arrayOfInt[this._level]));
    branchHandle2.setTarget(instructionList.append(NOP));
  }
  
  private void compileConstructor(ClassGenerator paramClassGenerator) {
    InstructionList instructionList = new InstructionList();
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    MethodGenerator methodGenerator = new MethodGenerator(1, Type.VOID, new Type[] { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/Translet;"), Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), Type.BOOLEAN }, new String[] { "dom", "translet", "iterator", "hasFrom" }, "<init>", this._className, instructionList, constantPoolGen);
    instructionList.append(ALOAD_0);
    instructionList.append(ALOAD_1);
    instructionList.append(ALOAD_2);
    instructionList.append(new ALOAD(3));
    instructionList.append(new ILOAD(4));
    int i = constantPoolGen.addMethodref(ClassNames[this._level], "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Z)V");
    instructionList.append(new INVOKESPECIAL(i));
    instructionList.append(RETURN);
    paramClassGenerator.addMethod(methodGenerator);
  }
  
  private void compileLocals(NodeCounterGenerator paramNodeCounterGenerator, MatchGenerator paramMatchGenerator, InstructionList paramInstructionList) {
    ConstantPoolGen constantPoolGen = paramNodeCounterGenerator.getConstantPool();
    LocalVariableGen localVariableGen = paramMatchGenerator.addLocalVariable("iterator", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    int i = constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "_iterator", "Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    paramInstructionList.append(ALOAD_0);
    paramInstructionList.append(new GETFIELD(i));
    localVariableGen.setStart(paramInstructionList.append(new ASTORE(localVariableGen.getIndex())));
    paramMatchGenerator.setIteratorIndex(localVariableGen.getIndex());
    localVariableGen = paramMatchGenerator.addLocalVariable("translet", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;"), null, null);
    i = constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "_translet", "Lcom/sun/org/apache/xalan/internal/xsltc/Translet;");
    paramInstructionList.append(ALOAD_0);
    paramInstructionList.append(new GETFIELD(i));
    paramInstructionList.append(new CHECKCAST(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet")));
    localVariableGen.setStart(paramInstructionList.append(new ASTORE(localVariableGen.getIndex())));
    paramNodeCounterGenerator.setTransletIndex(localVariableGen.getIndex());
    localVariableGen = paramMatchGenerator.addLocalVariable("document", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), null, null);
    i = constantPoolGen.addFieldref(this._className, "_document", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    paramInstructionList.append(ALOAD_0);
    paramInstructionList.append(new GETFIELD(i));
    localVariableGen.setStart(paramInstructionList.append(new ASTORE(localVariableGen.getIndex())));
    paramMatchGenerator.setDomIndex(localVariableGen.getIndex());
  }
  
  private void compilePatterns(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    this._className = getXSLTC().getHelperClassName();
    NodeCounterGenerator nodeCounterGenerator = new NodeCounterGenerator(this._className, ClassNames[this._level], toString(), 33, null, paramClassGenerator.getStylesheet());
    InstructionList instructionList = null;
    ConstantPoolGen constantPoolGen = nodeCounterGenerator.getConstantPool();
    boolean bool = (this._closureVars == null) ? 0 : this._closureVars.size();
    int i;
    for (i = 0; i < bool; i++) {
      VariableBase variableBase = ((VariableRefBase)this._closureVars.get(i)).getVariable();
      nodeCounterGenerator.addField(new Field(1, constantPoolGen.addUtf8(variableBase.getEscapedName()), constantPoolGen.addUtf8(variableBase.getType().toSignature()), null, constantPoolGen.getConstantPool()));
    } 
    compileConstructor(nodeCounterGenerator);
    if (this._from != null) {
      instructionList = new InstructionList();
      MatchGenerator matchGenerator = new MatchGenerator(17, Type.BOOLEAN, new Type[] { Type.INT }, new String[] { "node" }, "matchesFrom", this._className, instructionList, constantPoolGen);
      compileLocals(nodeCounterGenerator, matchGenerator, instructionList);
      instructionList.append(matchGenerator.loadContextNode());
      this._from.translate(nodeCounterGenerator, matchGenerator);
      this._from.synthesize(nodeCounterGenerator, matchGenerator);
      instructionList.append(IRETURN);
      nodeCounterGenerator.addMethod(matchGenerator);
    } 
    if (this._count != null) {
      instructionList = new InstructionList();
      MatchGenerator matchGenerator = new MatchGenerator(17, Type.BOOLEAN, new Type[] { Type.INT }, new String[] { "node" }, "matchesCount", this._className, instructionList, constantPoolGen);
      compileLocals(nodeCounterGenerator, matchGenerator, instructionList);
      instructionList.append(matchGenerator.loadContextNode());
      this._count.translate(nodeCounterGenerator, matchGenerator);
      this._count.synthesize(nodeCounterGenerator, matchGenerator);
      instructionList.append(IRETURN);
      nodeCounterGenerator.addMethod(matchGenerator);
    } 
    getXSLTC().dumpClass(nodeCounterGenerator.getJavaClass());
    constantPoolGen = paramClassGenerator.getConstantPool();
    instructionList = paramMethodGenerator.getInstructionList();
    i = constantPoolGen.addMethodref(this._className, "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Z)V");
    instructionList.append(new NEW(constantPoolGen.addClass(this._className)));
    instructionList.append(DUP);
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(paramMethodGenerator.loadDOM());
    instructionList.append(paramMethodGenerator.loadIterator());
    instructionList.append((this._from != null) ? ICONST_1 : ICONST_0);
    instructionList.append(new INVOKESPECIAL(i));
    for (byte b = 0; b < bool; b++) {
      VariableRefBase variableRefBase = (VariableRefBase)this._closureVars.get(b);
      VariableBase variableBase = variableRefBase.getVariable();
      Type type = variableBase.getType();
      instructionList.append(DUP);
      instructionList.append(variableBase.loadInstruction());
      instructionList.append(new PUTFIELD(constantPoolGen.addFieldref(this._className, variableBase.getEscapedName(), type.toSignature())));
    } 
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    instructionList.append(paramClassGenerator.loadTranslet());
    if (hasValue()) {
      compileDefault(paramClassGenerator, paramMethodGenerator);
      this._value.translate(paramClassGenerator, paramMethodGenerator);
      instructionList.append(new PUSH(constantPoolGen, 0.5D));
      instructionList.append(DADD);
      int j = constantPoolGen.addMethodref("java.lang.Math", "floor", "(D)D");
      instructionList.append(new INVOKESTATIC(j));
      j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "setValue", "(D)Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
      instructionList.append(new INVOKEVIRTUAL(j));
    } else if (isDefault()) {
      compileDefault(paramClassGenerator, paramMethodGenerator);
    } else {
      compilePatterns(paramClassGenerator, paramMethodGenerator);
    } 
    if (!hasValue()) {
      instructionList.append(paramMethodGenerator.loadContextNode());
      int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "setStartNode", "(I)Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
      instructionList.append(new INVOKEVIRTUAL(j));
    } 
    if (this._formatNeeded) {
      if (this._format != null) {
        this._format.translate(paramClassGenerator, paramMethodGenerator);
      } else {
        instructionList.append(new PUSH(constantPoolGen, "1"));
      } 
      if (this._lang != null) {
        this._lang.translate(paramClassGenerator, paramMethodGenerator);
      } else {
        instructionList.append(new PUSH(constantPoolGen, "en"));
      } 
      if (this._letterValue != null) {
        this._letterValue.translate(paramClassGenerator, paramMethodGenerator);
      } else {
        instructionList.append(new PUSH(constantPoolGen, ""));
      } 
      if (this._groupingSeparator != null) {
        this._groupingSeparator.translate(paramClassGenerator, paramMethodGenerator);
      } else {
        instructionList.append(new PUSH(constantPoolGen, ""));
      } 
      if (this._groupingSize != null) {
        this._groupingSize.translate(paramClassGenerator, paramMethodGenerator);
      } else {
        instructionList.append(new PUSH(constantPoolGen, "0"));
      } 
      int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "getCounter", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
      instructionList.append(new INVOKEVIRTUAL(j));
    } else {
      int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "setDefaultFormatting", "()Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
      instructionList.append(new INVOKEVIRTUAL(j));
      j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "getCounter", "()Ljava/lang/String;");
      instructionList.append(new INVOKEVIRTUAL(j));
    } 
    instructionList.append(paramMethodGenerator.loadHandler());
    int i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "characters", "(Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
    instructionList.append(new INVOKEVIRTUAL(i));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Number.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */