package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

final class AttributeValueTemplate extends AttributeValue {
  static final int OUT_EXPR = 0;
  
  static final int IN_EXPR = 1;
  
  static final int IN_EXPR_SQUOTES = 2;
  
  static final int IN_EXPR_DQUOTES = 3;
  
  static final String DELIMITER = "￾";
  
  public AttributeValueTemplate(String paramString, Parser paramParser, SyntaxTreeNode paramSyntaxTreeNode) {
    setParent(paramSyntaxTreeNode);
    setParser(paramParser);
    try {
      parseAVTemplate(paramString, paramParser);
    } catch (NoSuchElementException noSuchElementException) {
      reportError(paramSyntaxTreeNode, paramParser, "ATTR_VAL_TEMPLATE_ERR", paramString);
    } 
  }
  
  private void parseAVTemplate(String paramString, Parser paramParser) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, "{}\"'", true);
    String str1 = null;
    String str2 = null;
    StringBuffer stringBuffer = new StringBuffer();
    byte b = 0;
    while (stringTokenizer.hasMoreTokens()) {
      if (str2 != null) {
        str1 = str2;
        str2 = null;
      } else {
        str1 = stringTokenizer.nextToken();
      } 
      if (str1.length() == 1) {
        switch (str1.charAt(0)) {
          case '{':
            switch (b) {
              case false:
                str2 = stringTokenizer.nextToken();
                if (str2.equals("{")) {
                  stringBuffer.append(str2);
                  str2 = null;
                  continue;
                } 
                stringBuffer.append("￾");
                b = 1;
                continue;
              case true:
              case true:
              case true:
                reportError(getParent(), paramParser, "ATTR_VAL_TEMPLATE_ERR", paramString);
                continue;
            } 
            continue;
          case '}':
            switch (b) {
              case false:
                str2 = stringTokenizer.nextToken();
                if (str2.equals("}")) {
                  stringBuffer.append(str2);
                  str2 = null;
                  continue;
                } 
                reportError(getParent(), paramParser, "ATTR_VAL_TEMPLATE_ERR", paramString);
                continue;
              case true:
                stringBuffer.append("￾");
                b = 0;
                continue;
              case true:
              case true:
                stringBuffer.append(str1);
                continue;
            } 
            continue;
          case '\'':
            switch (b) {
              case true:
                b = 2;
                break;
              case true:
                b = 1;
                break;
            } 
            stringBuffer.append(str1);
            continue;
          case '"':
            switch (b) {
              case 1:
                b = 3;
                break;
              case 3:
                b = 1;
                break;
            } 
            stringBuffer.append(str1);
            continue;
        } 
        stringBuffer.append(str1);
        continue;
      } 
      stringBuffer.append(str1);
    } 
    if (b != 0)
      reportError(getParent(), paramParser, "ATTR_VAL_TEMPLATE_ERR", paramString); 
    stringTokenizer = new StringTokenizer(stringBuffer.toString(), "￾", true);
    while (stringTokenizer.hasMoreTokens()) {
      str1 = stringTokenizer.nextToken();
      if (str1.equals("￾")) {
        addElement(paramParser.parseExpression(this, stringTokenizer.nextToken()));
        stringTokenizer.nextToken();
        continue;
      } 
      addElement(new LiteralExpr(str1));
    } 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError {
    List list = getContents();
    int i = list.size();
    for (byte b = 0; b < i; b++) {
      Expression expression = (Expression)list.get(b);
      if (!expression.typeCheck(paramSymbolTable).identicalTo(Type.String))
        list.set(b, new CastExpr(expression, Type.String)); 
    } 
    return this._type = Type.String;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("AVT:[");
    int i = elementCount();
    for (byte b = 0; b < i; b++) {
      stringBuffer.append(elementAt(b).toString());
      if (b < i - 1)
        stringBuffer.append(' '); 
    } 
    return stringBuffer.append(']').toString();
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    if (elementCount() == 1) {
      Expression expression = (Expression)elementAt(0);
      expression.translate(paramClassGenerator, paramMethodGenerator);
    } else {
      ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
      InstructionList instructionList = paramMethodGenerator.getInstructionList();
      int i = constantPoolGen.addMethodref("java.lang.StringBuffer", "<init>", "()V");
      INVOKEVIRTUAL iNVOKEVIRTUAL = new INVOKEVIRTUAL(constantPoolGen.addMethodref("java.lang.StringBuffer", "append", "(Ljava/lang/String;)Ljava/lang/StringBuffer;"));
      int j = constantPoolGen.addMethodref("java.lang.StringBuffer", "toString", "()Ljava/lang/String;");
      instructionList.append(new NEW(constantPoolGen.addClass("java.lang.StringBuffer")));
      instructionList.append(DUP);
      instructionList.append(new INVOKESPECIAL(i));
      Iterator iterator = elements();
      while (iterator.hasNext()) {
        Expression expression = (Expression)iterator.next();
        expression.translate(paramClassGenerator, paramMethodGenerator);
        instructionList.append(iNVOKEVIRTUAL);
      } 
      instructionList.append(new INVOKEVIRTUAL(j));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\AttributeValueTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */