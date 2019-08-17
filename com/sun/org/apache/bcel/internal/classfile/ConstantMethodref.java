package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataInputStream;
import java.io.IOException;

public final class ConstantMethodref extends ConstantCP {
  public ConstantMethodref(ConstantMethodref paramConstantMethodref) { super((byte)10, paramConstantMethodref.getClassIndex(), paramConstantMethodref.getNameAndTypeIndex()); }
  
  ConstantMethodref(DataInputStream paramDataInputStream) throws IOException { super((byte)10, paramDataInputStream); }
  
  public ConstantMethodref(int paramInt1, int paramInt2) { super((byte)10, paramInt1, paramInt2); }
  
  public void accept(Visitor paramVisitor) { paramVisitor.visitConstantMethodref(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\ConstantMethodref.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */