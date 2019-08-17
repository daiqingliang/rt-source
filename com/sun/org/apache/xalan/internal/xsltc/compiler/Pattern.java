package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

public abstract class Pattern extends Expression {
  public abstract Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError;
  
  public abstract void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator);
  
  public abstract double getPriority();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Pattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */