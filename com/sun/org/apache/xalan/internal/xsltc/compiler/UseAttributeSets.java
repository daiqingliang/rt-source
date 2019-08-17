package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.StringTokenizer;
import java.util.Vector;

final class UseAttributeSets extends Instruction {
  private static final String ATTR_SET_NOT_FOUND = "";
  
  private final Vector _sets = new Vector(2);
  
  public UseAttributeSets(String paramString, Parser paramParser) {
    setParser(paramParser);
    addAttributeSets(paramString);
  }
  
  public void addAttributeSets(String paramString) {
    if (paramString != null && !paramString.equals("")) {
      StringTokenizer stringTokenizer = new StringTokenizer(paramString);
      while (stringTokenizer.hasMoreTokens()) {
        QName qName = getParser().getQNameIgnoreDefaultNs(stringTokenizer.nextToken());
        this._sets.add(qName);
      } 
    } 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError { return Type.Void; }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    SymbolTable symbolTable = getParser().getSymbolTable();
    for (byte b = 0; b < this._sets.size(); b++) {
      QName qName = (QName)this._sets.elementAt(b);
      AttributeSet attributeSet = symbolTable.lookupAttributeSet(qName);
      if (attributeSet != null) {
        String str = attributeSet.getMethodName();
        instructionList.append(paramClassGenerator.loadTranslet());
        instructionList.append(paramMethodGenerator.loadDOM());
        instructionList.append(paramMethodGenerator.loadIterator());
        instructionList.append(paramMethodGenerator.loadHandler());
        instructionList.append(paramMethodGenerator.loadCurrentNode());
        int i = constantPoolGen.addMethodref(paramClassGenerator.getClassName(), str, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V");
        instructionList.append(new INVOKESPECIAL(i));
      } else {
        Parser parser = getParser();
        String str = qName.toString();
        reportError(this, parser, "ATTRIBSET_UNDEF_ERR", str);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\UseAttributeSets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */