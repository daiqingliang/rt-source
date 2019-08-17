package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.Instruction;

class OutlineableChunkEnd extends MarkerInstruction {
  public static final Instruction OUTLINEABLECHUNKEND = new OutlineableChunkEnd();
  
  public String getName() { return OutlineableChunkEnd.class.getName(); }
  
  public String toString() { return getName(); }
  
  public String toString(boolean paramBoolean) { return getName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\OutlineableChunkEnd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */