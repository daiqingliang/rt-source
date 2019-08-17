package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

final class Choose extends Instruction {
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("Choose");
    indent(paramInt + 4);
    displayContents(paramInt + 4);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    Vector vector1 = new Vector();
    Otherwise otherwise = null;
    Iterator iterator = elements();
    ErrorMsg errorMsg = null;
    int i = getLineNumber();
    while (iterator.hasNext()) {
      SyntaxTreeNode syntaxTreeNode = (SyntaxTreeNode)iterator.next();
      if (syntaxTreeNode instanceof When) {
        vector1.addElement(syntaxTreeNode);
        continue;
      } 
      if (syntaxTreeNode instanceof Otherwise) {
        if (otherwise == null) {
          otherwise = (Otherwise)syntaxTreeNode;
          continue;
        } 
        errorMsg = new ErrorMsg("MULTIPLE_OTHERWISE_ERR", this);
        getParser().reportError(3, errorMsg);
        continue;
      } 
      if (syntaxTreeNode instanceof Text) {
        ((Text)syntaxTreeNode).ignore();
        continue;
      } 
      errorMsg = new ErrorMsg("WHEN_ELEMENT_ERR", this);
      getParser().reportError(3, errorMsg);
    } 
    if (vector1.size() == 0) {
      errorMsg = new ErrorMsg("MISSING_WHEN_ERR", this);
      getParser().reportError(3, errorMsg);
      return;
    } 
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    BranchHandle branchHandle = null;
    Vector vector2 = new Vector();
    InstructionHandle instructionHandle = null;
    Enumeration enumeration1 = vector1.elements();
    while (enumeration1.hasMoreElements()) {
      When when = (When)enumeration1.nextElement();
      Expression expression = when.getTest();
      InstructionHandle instructionHandle1 = instructionList.getEnd();
      if (branchHandle != null)
        branchHandle.setTarget(instructionList.append(NOP)); 
      expression.translateDesynthesized(paramClassGenerator, paramMethodGenerator);
      if (expression instanceof FunctionCall) {
        FunctionCall functionCall = (FunctionCall)expression;
        try {
          Type type = functionCall.typeCheck(getParser().getSymbolTable());
          if (type != Type.Boolean)
            expression._falseList.add(instructionList.append(new IFEQ(null))); 
        } catch (TypeCheckError typeCheckError) {}
      } 
      instructionHandle1 = instructionList.getEnd();
      if (!when.ignore())
        when.translateContents(paramClassGenerator, paramMethodGenerator); 
      vector2.addElement(instructionList.append(new GOTO(null)));
      if (enumeration1.hasMoreElements() || otherwise != null) {
        branchHandle = instructionList.append(new GOTO(null));
        expression.backPatchFalseList(branchHandle);
      } else {
        expression.backPatchFalseList(instructionHandle = instructionList.append(NOP));
      } 
      expression.backPatchTrueList(instructionHandle1.getNext());
    } 
    if (otherwise != null) {
      branchHandle.setTarget(instructionList.append(NOP));
      otherwise.translateContents(paramClassGenerator, paramMethodGenerator);
      instructionHandle = instructionList.append(NOP);
    } 
    Enumeration enumeration2 = vector2.elements();
    while (enumeration2.hasMoreElements()) {
      BranchHandle branchHandle1 = (BranchHandle)enumeration2.nextElement();
      branchHandle1.setTarget(instructionHandle);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Choose.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */