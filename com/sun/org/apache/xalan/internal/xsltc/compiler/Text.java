package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Text extends Instruction {
  private String _text;
  
  private boolean _escaping = true;
  
  private boolean _ignore = false;
  
  private boolean _textElement = false;
  
  public Text() { this._textElement = true; }
  
  public Text(String paramString) { this._text = paramString; }
  
  protected String getText() { return this._text; }
  
  protected void setText(String paramString) {
    if (this._text == null) {
      this._text = paramString;
    } else {
      this._text += paramString;
    } 
  }
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("Text");
    indent(paramInt + 4);
    Util.println(this._text);
  }
  
  public void parseContents(Parser paramParser) {
    String str = getAttribute("disable-output-escaping");
    if (str != null && str.equals("yes"))
      this._escaping = false; 
    parseChildren(paramParser);
    if (this._text == null) {
      if (this._textElement) {
        this._text = "";
      } else {
        this._ignore = true;
      } 
    } else if (this._textElement) {
      if (this._text.length() == 0)
        this._ignore = true; 
    } else if (getParent() instanceof LiteralElement) {
      LiteralElement literalElement = (LiteralElement)getParent();
      String str1 = literalElement.getAttribute("xml:space");
      if (str1 == null || !str1.equals("preserve")) {
        int i = this._text.length();
        byte b;
        for (b = 0; b < i; b++) {
          char c = this._text.charAt(b);
          if (!isWhitespace(c))
            break; 
        } 
        if (b == i)
          this._ignore = true; 
      } 
    } else {
      int i = this._text.length();
      byte b;
      for (b = 0; b < i; b++) {
        char c = this._text.charAt(b);
        if (!isWhitespace(c))
          break; 
      } 
      if (b == i)
        this._ignore = true; 
    } 
  }
  
  public void ignore() { this._ignore = true; }
  
  public boolean isIgnore() { return this._ignore; }
  
  public boolean isTextElement() { return this._textElement; }
  
  protected boolean contextDependent() { return false; }
  
  private static boolean isWhitespace(char paramChar) { return (paramChar == ' ' || paramChar == '\t' || paramChar == '\n' || paramChar == '\r'); }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    if (!this._ignore) {
      int i = constantPoolGen.addInterfaceMethodref("com/sun/org/apache/xml/internal/serializer/SerializationHandler", "setEscaping", "(Z)Z");
      if (!this._escaping) {
        instructionList.append(paramMethodGenerator.loadHandler());
        instructionList.append(new PUSH(constantPoolGen, false));
        instructionList.append(new INVOKEINTERFACE(i, 2));
      } 
      instructionList.append(paramMethodGenerator.loadHandler());
      if (!canLoadAsArrayOffsetLength()) {
        int j = constantPoolGen.addInterfaceMethodref("com/sun/org/apache/xml/internal/serializer/SerializationHandler", "characters", "(Ljava/lang/String;)V");
        instructionList.append(new PUSH(constantPoolGen, this._text));
        instructionList.append(new INVOKEINTERFACE(j, 2));
      } else {
        int j = constantPoolGen.addInterfaceMethodref("com/sun/org/apache/xml/internal/serializer/SerializationHandler", "characters", "([CII)V");
        loadAsArrayOffsetLength(paramClassGenerator, paramMethodGenerator);
        instructionList.append(new INVOKEINTERFACE(j, 4));
      } 
      if (!this._escaping) {
        instructionList.append(paramMethodGenerator.loadHandler());
        instructionList.append(SWAP);
        instructionList.append(new INVOKEINTERFACE(i, 2));
        instructionList.append(POP);
      } 
    } 
    translateContents(paramClassGenerator, paramMethodGenerator);
  }
  
  public boolean canLoadAsArrayOffsetLength() { return (this._text.length() <= 21845); }
  
  public void loadAsArrayOffsetLength(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    XSLTC xSLTC = paramClassGenerator.getParser().getXSLTC();
    int i = xSLTC.addCharacterData(this._text);
    int j = this._text.length();
    String str = "_scharData" + (xSLTC.getCharacterDataCount() - 1);
    instructionList.append(new GETSTATIC(constantPoolGen.addFieldref(xSLTC.getClassName(), str, "[C")));
    instructionList.append(new PUSH(constantPoolGen, i));
    instructionList.append(new PUSH(constantPoolGen, this._text.length()));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Text.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */