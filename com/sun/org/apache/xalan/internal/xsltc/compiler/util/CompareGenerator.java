package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.Type;

public final class CompareGenerator extends MethodGenerator {
  private static int DOM_INDEX = 1;
  
  private static int CURRENT_INDEX = 2;
  
  private static int LEVEL_INDEX = 3;
  
  private static int TRANSLET_INDEX = 4;
  
  private static int LAST_INDEX = 5;
  
  private int ITERATOR_INDEX = 6;
  
  private final Instruction _iloadCurrent = new ILOAD(CURRENT_INDEX);
  
  private final Instruction _istoreCurrent = new ISTORE(CURRENT_INDEX);
  
  private final Instruction _aloadDom = new ALOAD(DOM_INDEX);
  
  private final Instruction _iloadLast = new ILOAD(LAST_INDEX);
  
  private final Instruction _aloadIterator;
  
  private final Instruction _astoreIterator;
  
  public CompareGenerator(int paramInt, Type paramType, Type[] paramArrayOfType, String[] paramArrayOfString, String paramString1, String paramString2, InstructionList paramInstructionList, ConstantPoolGen paramConstantPoolGen) {
    super(paramInt, paramType, paramArrayOfType, paramArrayOfString, paramString1, paramString2, paramInstructionList, paramConstantPoolGen);
    LocalVariableGen localVariableGen = addLocalVariable("iterator", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    this.ITERATOR_INDEX = localVariableGen.getIndex();
    this._aloadIterator = new ALOAD(this.ITERATOR_INDEX);
    this._astoreIterator = new ASTORE(this.ITERATOR_INDEX);
    paramInstructionList.append(new ACONST_NULL());
    paramInstructionList.append(storeIterator());
  }
  
  public Instruction loadLastNode() { return this._iloadLast; }
  
  public Instruction loadCurrentNode() { return this._iloadCurrent; }
  
  public Instruction storeCurrentNode() { return this._istoreCurrent; }
  
  public Instruction loadDOM() { return this._aloadDom; }
  
  public int getHandlerIndex() { return -1; }
  
  public int getIteratorIndex() { return -1; }
  
  public Instruction storeIterator() { return this._astoreIterator; }
  
  public Instruction loadIterator() { return this._aloadIterator; }
  
  public int getLocalIndex(String paramString) { return paramString.equals("current") ? CURRENT_INDEX : super.getLocalIndex(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\CompareGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */