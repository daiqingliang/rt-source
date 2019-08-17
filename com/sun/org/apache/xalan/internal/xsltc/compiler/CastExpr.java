package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPNE;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.SIPUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MultiHashtable;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class CastExpr extends Expression {
  private final Expression _left;
  
  private static final MultiHashtable<Type, Type> InternalTypeMap = new MultiHashtable();
  
  private boolean _typeTest = false;
  
  public CastExpr(Expression paramExpression, Type paramType) throws TypeCheckError {
    this._left = paramExpression;
    this._type = paramType;
    if (this._left instanceof Step && this._type == Type.Boolean) {
      Step step = (Step)this._left;
      if (step.getAxis() == 13 && step.getNodeType() != -1)
        this._typeTest = true; 
    } 
    setParser(paramExpression.getParser());
    setParent(paramExpression.getParent());
    paramExpression.setParent(this);
    typeCheck(paramExpression.getParser().getSymbolTable());
  }
  
  public Expression getExpr() { return this._left; }
  
  public boolean hasPositionCall() { return this._left.hasPositionCall(); }
  
  public boolean hasLastCall() { return this._left.hasLastCall(); }
  
  public String toString() { return "cast(" + this._left + ", " + this._type + ")"; }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    Type type = this._left.getType();
    if (type == null)
      type = this._left.typeCheck(paramSymbolTable); 
    if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType) {
      type = Type.Node;
    } else if (type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.ResultTreeType) {
      type = Type.ResultTree;
    } 
    if (InternalTypeMap.maps(type, this._type) != null)
      return this._type; 
    throw new TypeCheckError(new ErrorMsg("DATA_CONVERSION_ERR", type.toString(), this._type.toString()));
  }
  
  public void translateDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    Type type = this._left.getType();
    if (this._typeTest) {
      ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList instructionList = paramMethodGenerator.getInstructionList();
      int i = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getExpandedTypeID", "(I)I");
      instructionList.append(new SIPUSH((short)((Step)this._left).getNodeType()));
      instructionList.append(paramMethodGenerator.loadDOM());
      instructionList.append(paramMethodGenerator.loadContextNode());
      instructionList.append(new INVOKEINTERFACE(i, 2));
      this._falseList.add(instructionList.append(new IF_ICMPNE(null)));
    } else {
      this._left.translate(paramClassGenerator, paramMethodGenerator);
      if (this._type != type) {
        this._left.startIterator(paramClassGenerator, paramMethodGenerator);
        if (this._type instanceof com.sun.org.apache.xalan.internal.xsltc.compiler.util.BooleanType) {
          FlowList flowList = type.translateToDesynthesized(paramClassGenerator, paramMethodGenerator, this._type);
          if (flowList != null)
            this._falseList.append(flowList); 
        } else {
          type.translateTo(paramClassGenerator, paramMethodGenerator, this._type);
        } 
      } 
    } 
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    Type type = this._left.getType();
    this._left.translate(paramClassGenerator, paramMethodGenerator);
    if (!this._type.identicalTo(type)) {
      this._left.startIterator(paramClassGenerator, paramMethodGenerator);
      type.translateTo(paramClassGenerator, paramMethodGenerator, this._type);
    } 
  }
  
  static  {
    InternalTypeMap.put(Type.Boolean, Type.Boolean);
    InternalTypeMap.put(Type.Boolean, Type.Real);
    InternalTypeMap.put(Type.Boolean, Type.String);
    InternalTypeMap.put(Type.Boolean, Type.Reference);
    InternalTypeMap.put(Type.Boolean, Type.Object);
    InternalTypeMap.put(Type.Real, Type.Real);
    InternalTypeMap.put(Type.Real, Type.Int);
    InternalTypeMap.put(Type.Real, Type.Boolean);
    InternalTypeMap.put(Type.Real, Type.String);
    InternalTypeMap.put(Type.Real, Type.Reference);
    InternalTypeMap.put(Type.Real, Type.Object);
    InternalTypeMap.put(Type.Int, Type.Int);
    InternalTypeMap.put(Type.Int, Type.Real);
    InternalTypeMap.put(Type.Int, Type.Boolean);
    InternalTypeMap.put(Type.Int, Type.String);
    InternalTypeMap.put(Type.Int, Type.Reference);
    InternalTypeMap.put(Type.Int, Type.Object);
    InternalTypeMap.put(Type.String, Type.String);
    InternalTypeMap.put(Type.String, Type.Boolean);
    InternalTypeMap.put(Type.String, Type.Real);
    InternalTypeMap.put(Type.String, Type.Reference);
    InternalTypeMap.put(Type.String, Type.Object);
    InternalTypeMap.put(Type.NodeSet, Type.NodeSet);
    InternalTypeMap.put(Type.NodeSet, Type.Boolean);
    InternalTypeMap.put(Type.NodeSet, Type.Real);
    InternalTypeMap.put(Type.NodeSet, Type.String);
    InternalTypeMap.put(Type.NodeSet, Type.Node);
    InternalTypeMap.put(Type.NodeSet, Type.Reference);
    InternalTypeMap.put(Type.NodeSet, Type.Object);
    InternalTypeMap.put(Type.Node, Type.Node);
    InternalTypeMap.put(Type.Node, Type.Boolean);
    InternalTypeMap.put(Type.Node, Type.Real);
    InternalTypeMap.put(Type.Node, Type.String);
    InternalTypeMap.put(Type.Node, Type.NodeSet);
    InternalTypeMap.put(Type.Node, Type.Reference);
    InternalTypeMap.put(Type.Node, Type.Object);
    InternalTypeMap.put(Type.ResultTree, Type.ResultTree);
    InternalTypeMap.put(Type.ResultTree, Type.Boolean);
    InternalTypeMap.put(Type.ResultTree, Type.Real);
    InternalTypeMap.put(Type.ResultTree, Type.String);
    InternalTypeMap.put(Type.ResultTree, Type.NodeSet);
    InternalTypeMap.put(Type.ResultTree, Type.Reference);
    InternalTypeMap.put(Type.ResultTree, Type.Object);
    InternalTypeMap.put(Type.Reference, Type.Reference);
    InternalTypeMap.put(Type.Reference, Type.Boolean);
    InternalTypeMap.put(Type.Reference, Type.Int);
    InternalTypeMap.put(Type.Reference, Type.Real);
    InternalTypeMap.put(Type.Reference, Type.String);
    InternalTypeMap.put(Type.Reference, Type.Node);
    InternalTypeMap.put(Type.Reference, Type.NodeSet);
    InternalTypeMap.put(Type.Reference, Type.ResultTree);
    InternalTypeMap.put(Type.Reference, Type.Object);
    InternalTypeMap.put(Type.Object, Type.String);
    InternalTypeMap.put(Type.Void, Type.String);
    InternalTypeMap.makeUnmodifiable();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\CastExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */