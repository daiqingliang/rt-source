package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;

public final class NodeSortRecordGenerator extends ClassGenerator {
  private static final int TRANSLET_INDEX = 4;
  
  private final Instruction _aloadTranslet = new ALOAD(4);
  
  public NodeSortRecordGenerator(String paramString1, String paramString2, String paramString3, int paramInt, String[] paramArrayOfString, Stylesheet paramStylesheet) { super(paramString1, paramString2, paramString3, paramInt, paramArrayOfString, paramStylesheet); }
  
  public Instruction loadTranslet() { return this._aloadTranslet; }
  
  public boolean isExternal() { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\NodeSortRecordGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */