package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.Instruction;

class OutlineableChunkStart extends MarkerInstruction {
  public static final Instruction OUTLINEABLECHUNKSTART = new OutlineableChunkStart();
  
  public String getName() { return OutlineableChunkStart.class.getName(); }
  
  public String toString() { return getName(); }
  
  public String toString(boolean paramBoolean) { return getName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\OutlineableChunkStart.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */