package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;

public final class NamedMethodGenerator extends MethodGenerator {
  protected static final int CURRENT_INDEX = 4;
  
  private static final int PARAM_START_INDEX = 5;
  
  public NamedMethodGenerator(int paramInt, Type paramType, Type[] paramArrayOfType, String[] paramArrayOfString, String paramString1, String paramString2, InstructionList paramInstructionList, ConstantPoolGen paramConstantPoolGen) { super(paramInt, paramType, paramArrayOfType, paramArrayOfString, paramString1, paramString2, paramInstructionList, paramConstantPoolGen); }
  
  public int getLocalIndex(String paramString) { return paramString.equals("current") ? 4 : super.getLocalIndex(paramString); }
  
  public Instruction loadParameter(int paramInt) { return new ALOAD(paramInt + 5); }
  
  public Instruction storeParameter(int paramInt) { return new ASTORE(paramInt + 5); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\NamedMethodGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */