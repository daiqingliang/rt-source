package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.utils.XML11Char;

final class DecimalFormatting extends TopLevelElement {
  private static final String DFS_CLASS = "java.text.DecimalFormatSymbols";
  
  private static final String DFS_SIG = "Ljava/text/DecimalFormatSymbols;";
  
  private QName _name = null;
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError { return Type.Void; }
  
  public void parseContents(Parser paramParser) {
    String str = getAttribute("name");
    if (str.length() > 0 && !XML11Char.isXML11ValidQName(str)) {
      ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str, this);
      paramParser.reportError(3, errorMsg);
    } 
    this._name = paramParser.getQNameIgnoreDefaultNs(str);
    if (this._name == null)
      this._name = paramParser.getQNameIgnoreDefaultNs(""); 
    SymbolTable symbolTable = paramParser.getSymbolTable();
    if (symbolTable.getDecimalFormatting(this._name) != null) {
      reportWarning(this, paramParser, "SYMBOLS_REDEF_ERR", this._name.toString());
    } else {
      symbolTable.addDecimalFormatting(this._name, this);
    } 
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "<init>", "(Ljava/util/Locale;)V");
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new PUSH(constantPoolGen, this._name.toString()));
    instructionList.append(new NEW(constantPoolGen.addClass("java.text.DecimalFormatSymbols")));
    instructionList.append(DUP);
    instructionList.append(new GETSTATIC(constantPoolGen.addFieldref("java.util.Locale", "US", "Ljava/util/Locale;")));
    instructionList.append(new INVOKESPECIAL(i));
    String str = getAttribute("NaN");
    if (str == null || str.equals("")) {
      int m = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setNaN", "(Ljava/lang/String;)V");
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, "NaN"));
      instructionList.append(new INVOKEVIRTUAL(m));
    } 
    str = getAttribute("infinity");
    if (str == null || str.equals("")) {
      int m = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setInfinity", "(Ljava/lang/String;)V");
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, "Infinity"));
      instructionList.append(new INVOKEVIRTUAL(m));
    } 
    int j = this._attributes.getLength();
    int k;
    for (k = 0; k < j; k++) {
      String str1 = this._attributes.getQName(k);
      String str2 = this._attributes.getValue(k);
      boolean bool = true;
      int m = 0;
      if (str1.equals("decimal-separator")) {
        m = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setDecimalSeparator", "(C)V");
      } else if (str1.equals("grouping-separator")) {
        m = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setGroupingSeparator", "(C)V");
      } else if (str1.equals("minus-sign")) {
        m = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setMinusSign", "(C)V");
      } else if (str1.equals("percent")) {
        m = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setPercent", "(C)V");
      } else if (str1.equals("per-mille")) {
        m = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setPerMill", "(C)V");
      } else if (str1.equals("zero-digit")) {
        m = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setZeroDigit", "(C)V");
      } else if (str1.equals("digit")) {
        m = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setDigit", "(C)V");
      } else if (str1.equals("pattern-separator")) {
        m = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setPatternSeparator", "(C)V");
      } else if (str1.equals("NaN")) {
        m = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setNaN", "(Ljava/lang/String;)V");
        instructionList.append(DUP);
        instructionList.append(new PUSH(constantPoolGen, str2));
        instructionList.append(new INVOKEVIRTUAL(m));
        bool = false;
      } else if (str1.equals("infinity")) {
        m = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setInfinity", "(Ljava/lang/String;)V");
        instructionList.append(DUP);
        instructionList.append(new PUSH(constantPoolGen, str2));
        instructionList.append(new INVOKEVIRTUAL(m));
        bool = false;
      } else {
        bool = false;
      } 
      if (bool) {
        instructionList.append(DUP);
        instructionList.append(new PUSH(constantPoolGen, str2.charAt(0)));
        instructionList.append(new INVOKEVIRTUAL(m));
      } 
    } 
    k = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addDecimalFormat", "(Ljava/lang/String;Ljava/text/DecimalFormatSymbols;)V");
    instructionList.append(new INVOKEVIRTUAL(k));
  }
  
  public static void translateDefaultDFS(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "<init>", "(Ljava/util/Locale;)V");
    instructionList.append(paramClassGenerator.loadTranslet());
    instructionList.append(new PUSH(constantPoolGen, ""));
    instructionList.append(new NEW(constantPoolGen.addClass("java.text.DecimalFormatSymbols")));
    instructionList.append(DUP);
    instructionList.append(new GETSTATIC(constantPoolGen.addFieldref("java.util.Locale", "US", "Ljava/util/Locale;")));
    instructionList.append(new INVOKESPECIAL(i));
    int j = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setNaN", "(Ljava/lang/String;)V");
    instructionList.append(DUP);
    instructionList.append(new PUSH(constantPoolGen, "NaN"));
    instructionList.append(new INVOKEVIRTUAL(j));
    int k = constantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setInfinity", "(Ljava/lang/String;)V");
    instructionList.append(DUP);
    instructionList.append(new PUSH(constantPoolGen, "Infinity"));
    instructionList.append(new INVOKEVIRTUAL(k));
    int m = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addDecimalFormat", "(Ljava/lang/String;Ljava/text/DecimalFormatSymbols;)V");
    instructionList.append(new INVOKEVIRTUAL(m));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\DecimalFormatting.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */