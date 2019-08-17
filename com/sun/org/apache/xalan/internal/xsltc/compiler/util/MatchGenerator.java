package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;

public final class MatchGenerator extends MethodGenerator {
  private static int CURRENT_INDEX = 1;
  
  private int _iteratorIndex = -1;
  
  private final Instruction _iloadCurrent = new ILOAD(CURRENT_INDEX);
  
  private final Instruction _istoreCurrent = new ISTORE(CURRENT_INDEX);
  
  private Instruction _aloadDom;
  
  public MatchGenerator(int paramInt, Type paramType, Type[] paramArrayOfType, String[] paramArrayOfString, String paramString1, String paramString2, InstructionList paramInstructionList, ConstantPoolGen paramConstantPoolGen) { super(paramInt, paramType, paramArrayOfType, paramArrayOfString, paramString1, paramString2, paramInstructionList, paramConstantPoolGen); }
  
  public Instruction loadCurrentNode() { return this._iloadCurrent; }
  
  public Instruction storeCurrentNode() { return this._istoreCurrent; }
  
  public int getHandlerIndex() { return -1; }
  
  public Instruction loadDOM() { return this._aloadDom; }
  
  public void setDomIndex(int paramInt) { this._aloadDom = new ALOAD(paramInt); }
  
  public int getIteratorIndex() { return this._iteratorIndex; }
  
  public void setIteratorIndex(int paramInt) { this._iteratorIndex = paramInt; }
  
  public int getLocalIndex(String paramString) { return paramString.equals("current") ? CURRENT_INDEX : super.getLocalIndex(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\MatchGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */