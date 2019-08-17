package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;

public final class TestGenerator extends MethodGenerator {
  private static int CONTEXT_NODE_INDEX = 1;
  
  private static int CURRENT_NODE_INDEX = 4;
  
  private static int ITERATOR_INDEX = 6;
  
  private Instruction _aloadDom;
  
  private final Instruction _iloadCurrent = new ILOAD(CURRENT_NODE_INDEX);
  
  private final Instruction _iloadContext = new ILOAD(CONTEXT_NODE_INDEX);
  
  private final Instruction _istoreCurrent = new ISTORE(CURRENT_NODE_INDEX);
  
  private final Instruction _istoreContext = new ILOAD(CONTEXT_NODE_INDEX);
  
  private final Instruction _astoreIterator = new ASTORE(ITERATOR_INDEX);
  
  private final Instruction _aloadIterator = new ALOAD(ITERATOR_INDEX);
  
  public TestGenerator(int paramInt, Type paramType, Type[] paramArrayOfType, String[] paramArrayOfString, String paramString1, String paramString2, InstructionList paramInstructionList, ConstantPoolGen paramConstantPoolGen) { super(paramInt, paramType, paramArrayOfType, paramArrayOfString, paramString1, paramString2, paramInstructionList, paramConstantPoolGen); }
  
  public int getHandlerIndex() { return -1; }
  
  public int getIteratorIndex() { return ITERATOR_INDEX; }
  
  public void setDomIndex(int paramInt) { this._aloadDom = new ALOAD(paramInt); }
  
  public Instruction loadDOM() { return this._aloadDom; }
  
  public Instruction loadCurrentNode() { return this._iloadCurrent; }
  
  public Instruction loadContextNode() { return this._iloadContext; }
  
  public Instruction storeContextNode() { return this._istoreContext; }
  
  public Instruction storeCurrentNode() { return this._istoreCurrent; }
  
  public Instruction storeIterator() { return this._astoreIterator; }
  
  public Instruction loadIterator() { return this._aloadIterator; }
  
  public int getLocalIndex(String paramString) { return paramString.equals("current") ? CURRENT_NODE_INDEX : super.getLocalIndex(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\TestGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */