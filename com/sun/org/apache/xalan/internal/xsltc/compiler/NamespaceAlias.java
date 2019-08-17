package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

final class NamespaceAlias extends TopLevelElement {
  private String sPrefix;
  
  private String rPrefix;
  
  public void parseContents(Parser paramParser) {
    this.sPrefix = getAttribute("stylesheet-prefix");
    this.rPrefix = getAttribute("result-prefix");
    paramParser.getSymbolTable().addPrefixAlias(this.sPrefix, this.rPrefix);
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError { return Type.Void; }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\NamespaceAlias.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */