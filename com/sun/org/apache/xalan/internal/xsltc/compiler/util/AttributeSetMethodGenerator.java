package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;

public final class AttributeSetMethodGenerator extends MethodGenerator {
  protected static final int CURRENT_INDEX = 4;
  
  private static final int PARAM_START_INDEX = 5;
  
  private static final String[] argNames = new String[4];
  
  private static final Type[] argTypes = new Type[4];
  
  public AttributeSetMethodGenerator(String paramString, ClassGenerator paramClassGenerator) { super(2, Type.VOID, argTypes, argNames, paramString, paramClassGenerator.getClassName(), new InstructionList(), paramClassGenerator.getConstantPool()); }
  
  public int getLocalIndex(String paramString) { return paramString.equals("current") ? 4 : super.getLocalIndex(paramString); }
  
  public Instruction loadParameter(int paramInt) { return new ALOAD(paramInt + 5); }
  
  public Instruction storeParameter(int paramInt) { return new ASTORE(paramInt + 5); }
  
  static  {
    argTypes[0] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    argTypes[1] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    argTypes[2] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
    argTypes[3] = Type.INT;
    argNames[0] = "document";
    argNames[1] = "iterator";
    argNames[2] = "handler";
    argNames[3] = "node";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\AttributeSetMethodGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */