package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.dtm.Axis;
import java.util.Vector;

final class Step extends RelativeLocationPath {
  private int _axis;
  
  private Vector _predicates;
  
  private boolean _hadPredicates = false;
  
  private int _nodeType;
  
  public Step(int paramInt1, int paramInt2, Vector paramVector) {
    this._axis = paramInt1;
    this._nodeType = paramInt2;
    this._predicates = paramVector;
  }
  
  public void setParser(Parser paramParser) {
    super.setParser(paramParser);
    if (this._predicates != null) {
      int i = this._predicates.size();
      for (byte b = 0; b < i; b++) {
        Predicate predicate = (Predicate)this._predicates.elementAt(b);
        predicate.setParser(paramParser);
        predicate.setParent(this);
      } 
    } 
  }
  
  public int getAxis() { return this._axis; }
  
  public void setAxis(int paramInt) { this._axis = paramInt; }
  
  public int getNodeType() { return this._nodeType; }
  
  public Vector getPredicates() { return this._predicates; }
  
  public void addPredicates(Vector paramVector) {
    if (this._predicates == null) {
      this._predicates = paramVector;
    } else {
      this._predicates.addAll(paramVector);
    } 
  }
  
  private boolean hasParentPattern() {
    SyntaxTreeNode syntaxTreeNode = getParent();
    return (syntaxTreeNode instanceof ParentPattern || syntaxTreeNode instanceof ParentLocationPath || syntaxTreeNode instanceof UnionPathExpr || syntaxTreeNode instanceof FilterParentPath);
  }
  
  private boolean hasParentLocationPath() { return getParent() instanceof ParentLocationPath; }
  
  private boolean hasPredicates() { return (this._predicates != null && this._predicates.size() > 0); }
  
  private boolean isPredicate() {
    Step step = this;
    while (step != null) {
      SyntaxTreeNode syntaxTreeNode = step.getParent();
      if (syntaxTreeNode instanceof Predicate)
        return true; 
    } 
    return false;
  }
  
  public boolean isAbbreviatedDot() { return (this._nodeType == -1 && this._axis == 13); }
  
  public boolean isAbbreviatedDDot() { return (this._nodeType == -1 && this._axis == 10); }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    this._hadPredicates = hasPredicates();
    if (isAbbreviatedDot()) {
      this._type = (hasParentPattern() || hasPredicates() || hasParentLocationPath()) ? Type.NodeSet : Type.Node;
    } else {
      this._type = Type.NodeSet;
    } 
    if (this._predicates != null) {
      int i = this._predicates.size();
      for (byte b = 0; b < i; b++) {
        Expression expression = (Expression)this._predicates.elementAt(b);
        expression.typeCheck(paramSymbolTable);
      } 
    } 
    return this._type;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) { translateStep(paramClassGenerator, paramMethodGenerator, hasPredicates() ? (this._predicates.size() - 1) : -1); }
  
  private void translateStep(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, int paramInt) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (paramInt >= 0) {
      translatePredicates(paramClassGenerator, paramMethodGenerator, paramInt);
    } else {
      int j;
      int i = 0;
      String str = null;
      XSLTC xSLTC = getParser().getXSLTC();
      if (this._nodeType >= 14) {
        Vector vector = xSLTC.getNamesIndex();
        str = (String)vector.elementAt(this._nodeType - 14);
        i = str.lastIndexOf('*');
      } 
      if (this._axis == 2 && this._nodeType != 2 && this._nodeType != -1 && !hasParentPattern() && i == 0) {
        int m = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getTypedAxisIterator", "(II)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        instructionList.append(paramMethodGenerator.loadDOM());
        instructionList.append(new PUSH(constantPoolGen, 2));
        instructionList.append(new PUSH(constantPoolGen, this._nodeType));
        instructionList.append(new INVOKEINTERFACE(m, 3));
        return;
      } 
      SyntaxTreeNode syntaxTreeNode = getParent();
      if (isAbbreviatedDot()) {
        if (this._type == Type.Node) {
          instructionList.append(paramMethodGenerator.loadContextNode());
        } else if (syntaxTreeNode instanceof ParentLocationPath) {
          int m = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator", "<init>", "(I)V");
          instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator")));
          instructionList.append(DUP);
          instructionList.append(paramMethodGenerator.loadContextNode());
          instructionList.append(new INVOKESPECIAL(m));
        } else {
          int m = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
          instructionList.append(paramMethodGenerator.loadDOM());
          instructionList.append(new PUSH(constantPoolGen, this._axis));
          instructionList.append(new INVOKEINTERFACE(m, 2));
        } 
        return;
      } 
      if (syntaxTreeNode instanceof ParentLocationPath && syntaxTreeNode.getParent() instanceof ParentLocationPath && this._nodeType == 1 && !this._hadPredicates)
        this._nodeType = -1; 
      switch (this._nodeType) {
        case 2:
          this._axis = 2;
        case -1:
          j = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
          instructionList.append(paramMethodGenerator.loadDOM());
          instructionList.append(new PUSH(constantPoolGen, this._axis));
          instructionList.append(new INVOKEINTERFACE(j, 2));
          return;
        default:
          if (i > 1) {
            String str1;
            if (this._axis == 2) {
              str1 = str.substring(0, i - 2);
            } else {
              str1 = str.substring(0, i - 1);
            } 
            int m = xSLTC.registerNamespace(str1);
            int n = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNamespaceAxisIterator", "(II)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            instructionList.append(paramMethodGenerator.loadDOM());
            instructionList.append(new PUSH(constantPoolGen, this._axis));
            instructionList.append(new PUSH(constantPoolGen, m));
            instructionList.append(new INVOKEINTERFACE(n, 3));
            return;
          } 
          break;
        case 1:
          break;
      } 
      int k = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getTypedAxisIterator", "(II)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(new PUSH(constantPoolGen, this._axis));
      instructionList.append(new PUSH(constantPoolGen, this._nodeType));
      instructionList.append(new INVOKEINTERFACE(k, 3));
    } 
  }
  
  public void translatePredicates(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, int paramInt) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = 0;
    if (paramInt < 0) {
      translateStep(paramClassGenerator, paramMethodGenerator, paramInt);
    } else {
      Predicate predicate = (Predicate)this._predicates.get(paramInt--);
      if (predicate.isNodeValueTest()) {
        Step step = predicate.getStep();
        instructionList.append(paramMethodGenerator.loadDOM());
        if (step.isAbbreviatedDot()) {
          translateStep(paramClassGenerator, paramMethodGenerator, paramInt);
          instructionList.append(new ICONST(0));
        } else {
          ParentLocationPath parentLocationPath = new ParentLocationPath(this, step);
          step._parent = parentLocationPath;
          try {
            parentLocationPath.typeCheck(getParser().getSymbolTable());
          } catch (TypeCheckError typeCheckError) {}
          translateStep(paramClassGenerator, paramMethodGenerator, paramInt);
          parentLocationPath.translateStep(paramClassGenerator, paramMethodGenerator);
          instructionList.append(new ICONST(1));
        } 
        predicate.translate(paramClassGenerator, paramMethodGenerator);
        i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeValueIterator", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;ILjava/lang/String;Z)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        instructionList.append(new INVOKEINTERFACE(i, 5));
      } else if (predicate.isNthDescendant()) {
        instructionList.append(paramMethodGenerator.loadDOM());
        instructionList.append(new PUSH(constantPoolGen, predicate.getPosType()));
        predicate.translate(paramClassGenerator, paramMethodGenerator);
        instructionList.append(new ICONST(0));
        i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNthDescendant", "(IIZ)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        instructionList.append(new INVOKEINTERFACE(i, 4));
      } else if (predicate.isNthPositionFilter()) {
        i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)V");
        translatePredicates(paramClassGenerator, paramMethodGenerator, paramInt);
        LocalVariableGen localVariableGen1 = paramMethodGenerator.addLocalVariable("step_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        localVariableGen1.setStart(instructionList.append(new ASTORE(localVariableGen1.getIndex())));
        predicate.translate(paramClassGenerator, paramMethodGenerator);
        LocalVariableGen localVariableGen2 = paramMethodGenerator.addLocalVariable("step_tmp2", Util.getJCRefType("I"), null, null);
        localVariableGen2.setStart(instructionList.append(new ISTORE(localVariableGen2.getIndex())));
        instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.NthIterator")));
        instructionList.append(DUP);
        localVariableGen1.setEnd(instructionList.append(new ALOAD(localVariableGen1.getIndex())));
        localVariableGen2.setEnd(instructionList.append(new ILOAD(localVariableGen2.getIndex())));
        instructionList.append(new INVOKESPECIAL(i));
      } else {
        i = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;ILcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;)V");
        translatePredicates(paramClassGenerator, paramMethodGenerator, paramInt);
        LocalVariableGen localVariableGen1 = paramMethodGenerator.addLocalVariable("step_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        localVariableGen1.setStart(instructionList.append(new ASTORE(localVariableGen1.getIndex())));
        predicate.translateFilter(paramClassGenerator, paramMethodGenerator);
        LocalVariableGen localVariableGen2 = paramMethodGenerator.addLocalVariable("step_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/CurrentNodeListFilter;"), null, null);
        localVariableGen2.setStart(instructionList.append(new ASTORE(localVariableGen2.getIndex())));
        instructionList.append(new NEW(constantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListIterator")));
        instructionList.append(DUP);
        localVariableGen1.setEnd(instructionList.append(new ALOAD(localVariableGen1.getIndex())));
        localVariableGen2.setEnd(instructionList.append(new ALOAD(localVariableGen2.getIndex())));
        instructionList.append(paramMethodGenerator.loadCurrentNode());
        instructionList.append(paramClassGenerator.loadTranslet());
        if (paramClassGenerator.isExternal()) {
          String str = paramClassGenerator.getClassName();
          instructionList.append(new CHECKCAST(constantPoolGen.addClass(str)));
        } 
        instructionList.append(new INVOKESPECIAL(i));
      } 
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("step(\"");
    stringBuffer.append(Axis.getNames(this._axis)).append("\", ").append(this._nodeType);
    if (this._predicates != null) {
      int i = this._predicates.size();
      for (byte b = 0; b < i; b++) {
        Predicate predicate = (Predicate)this._predicates.elementAt(b);
        stringBuffer.append(", ").append(predicate.toString());
      } 
    } 
    return stringBuffer.append(')').toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Step.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */