package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.serializer.Encodings;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.StringTokenizer;

final class Output extends TopLevelElement {
  private String _version;
  
  private String _method;
  
  private String _encoding;
  
  private boolean _omitHeader = false;
  
  private String _standalone;
  
  private String _doctypePublic;
  
  private String _doctypeSystem;
  
  private String _cdata;
  
  private boolean _indent = false;
  
  private String _mediaType;
  
  private String _indentamount;
  
  private boolean _disabled = false;
  
  private static final String STRING_SIG = "Ljava/lang/String;";
  
  private static final String XML_VERSION = "1.0";
  
  private static final String HTML_VERSION = "4.0";
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("Output " + this._method);
  }
  
  public void disable() { this._disabled = true; }
  
  public boolean enabled() { return !this._disabled; }
  
  public String getCdata() { return this._cdata; }
  
  public String getOutputMethod() { return this._method; }
  
  private void transferAttribute(Output paramOutput, String paramString) {
    if (!hasAttribute(paramString) && paramOutput.hasAttribute(paramString))
      addAttribute(paramString, paramOutput.getAttribute(paramString)); 
  }
  
  public void mergeOutput(Output paramOutput) {
    transferAttribute(paramOutput, "version");
    transferAttribute(paramOutput, "method");
    transferAttribute(paramOutput, "encoding");
    transferAttribute(paramOutput, "doctype-system");
    transferAttribute(paramOutput, "doctype-public");
    transferAttribute(paramOutput, "media-type");
    transferAttribute(paramOutput, "indent");
    transferAttribute(paramOutput, "omit-xml-declaration");
    transferAttribute(paramOutput, "standalone");
    if (paramOutput.hasAttribute("cdata-section-elements"))
      addAttribute("cdata-section-elements", paramOutput.getAttribute("cdata-section-elements") + ' ' + getAttribute("cdata-section-elements")); 
    String str = lookupPrefix("http://xml.apache.org/xalan");
    if (str != null)
      transferAttribute(paramOutput, str + ':' + "indent-amount"); 
    str = lookupPrefix("http://xml.apache.org/xslt");
    if (str != null)
      transferAttribute(paramOutput, str + ':' + "indent-amount"); 
  }
  
  public void parseContents(Parser paramParser) {
    Properties properties = new Properties();
    paramParser.setOutput(this);
    if (this._disabled)
      return; 
    String str = null;
    this._version = getAttribute("version");
    if (this._version.equals("")) {
      this._version = null;
    } else {
      properties.setProperty("version", this._version);
    } 
    this._method = getAttribute("method");
    if (this._method.equals(""))
      this._method = null; 
    if (this._method != null) {
      this._method = this._method.toLowerCase();
      if (this._method.equals("xml") || this._method.equals("html") || this._method.equals("text") || (XML11Char.isXML11ValidQName(this._method) && this._method.indexOf(":") > 0)) {
        properties.setProperty("method", this._method);
      } else {
        reportError(this, paramParser, "INVALID_METHOD_IN_OUTPUT", this._method);
      } 
    } 
    this._encoding = getAttribute("encoding");
    if (this._encoding.equals("")) {
      this._encoding = null;
    } else {
      try {
        String str1 = Encodings.convertMime2JavaEncoding(this._encoding);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(System.out, str1);
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        ErrorMsg errorMsg = new ErrorMsg("UNSUPPORTED_ENCODING", this._encoding, this);
        paramParser.reportError(4, errorMsg);
      } 
      properties.setProperty("encoding", this._encoding);
    } 
    str = getAttribute("omit-xml-declaration");
    if (!str.equals("")) {
      if (str.equals("yes"))
        this._omitHeader = true; 
      properties.setProperty("omit-xml-declaration", str);
    } 
    this._standalone = getAttribute("standalone");
    if (this._standalone.equals("")) {
      this._standalone = null;
    } else {
      properties.setProperty("standalone", this._standalone);
    } 
    this._doctypeSystem = getAttribute("doctype-system");
    if (this._doctypeSystem.equals("")) {
      this._doctypeSystem = null;
    } else {
      properties.setProperty("doctype-system", this._doctypeSystem);
    } 
    this._doctypePublic = getAttribute("doctype-public");
    if (this._doctypePublic.equals("")) {
      this._doctypePublic = null;
    } else {
      properties.setProperty("doctype-public", this._doctypePublic);
    } 
    this._cdata = getAttribute("cdata-section-elements");
    if (this._cdata.equals("")) {
      this._cdata = null;
    } else {
      StringBuffer stringBuffer = new StringBuffer();
      StringTokenizer stringTokenizer = new StringTokenizer(this._cdata);
      while (stringTokenizer.hasMoreTokens()) {
        String str1 = stringTokenizer.nextToken();
        if (!XML11Char.isXML11ValidQName(str1)) {
          ErrorMsg errorMsg = new ErrorMsg("INVALID_QNAME_ERR", str1, this);
          paramParser.reportError(3, errorMsg);
        } 
        stringBuffer.append(paramParser.getQName(str1).toString()).append(' ');
      } 
      this._cdata = stringBuffer.toString();
      properties.setProperty("cdata-section-elements", this._cdata);
    } 
    str = getAttribute("indent");
    if (!str.equals("")) {
      if (str.equals("yes"))
        this._indent = true; 
      properties.setProperty("indent", str);
    } else if (this._method != null && this._method.equals("html")) {
      this._indent = true;
    } 
    this._indentamount = getAttribute(lookupPrefix("http://xml.apache.org/xalan"), "indent-amount");
    if (this._indentamount.equals(""))
      this._indentamount = getAttribute(lookupPrefix("http://xml.apache.org/xslt"), "indent-amount"); 
    if (!this._indentamount.equals(""))
      properties.setProperty("indent_amount", this._indentamount); 
    this._mediaType = getAttribute("media-type");
    if (this._mediaType.equals("")) {
      this._mediaType = null;
    } else {
      properties.setProperty("media-type", this._mediaType);
    } 
    if (this._method != null)
      if (this._method.equals("html")) {
        if (this._version == null)
          this._version = "4.0"; 
        if (this._mediaType == null)
          this._mediaType = "text/html"; 
      } else if (this._method.equals("text") && this._mediaType == null) {
        this._mediaType = "text/plain";
      }  
    paramParser.getCurrentStylesheet().setOutputProperties(properties);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {
    if (this._disabled)
      return; 
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = paramMethodGenerator.getInstructionList();
    int i = 0;
    instructionList.append(paramClassGenerator.loadTranslet());
    if (this._version != null && !this._version.equals("1.0")) {
      i = constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_version", "Ljava/lang/String;");
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, this._version));
      instructionList.append(new PUTFIELD(i));
    } 
    if (this._method != null) {
      i = constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_method", "Ljava/lang/String;");
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, this._method));
      instructionList.append(new PUTFIELD(i));
    } 
    if (this._encoding != null) {
      i = constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_encoding", "Ljava/lang/String;");
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, this._encoding));
      instructionList.append(new PUTFIELD(i));
    } 
    if (this._omitHeader) {
      i = constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_omitHeader", "Z");
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, this._omitHeader));
      instructionList.append(new PUTFIELD(i));
    } 
    if (this._standalone != null) {
      i = constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_standalone", "Ljava/lang/String;");
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, this._standalone));
      instructionList.append(new PUTFIELD(i));
    } 
    i = constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_doctypeSystem", "Ljava/lang/String;");
    instructionList.append(DUP);
    instructionList.append(new PUSH(constantPoolGen, this._doctypeSystem));
    instructionList.append(new PUTFIELD(i));
    i = constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_doctypePublic", "Ljava/lang/String;");
    instructionList.append(DUP);
    instructionList.append(new PUSH(constantPoolGen, this._doctypePublic));
    instructionList.append(new PUTFIELD(i));
    if (this._mediaType != null) {
      i = constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_mediaType", "Ljava/lang/String;");
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, this._mediaType));
      instructionList.append(new PUTFIELD(i));
    } 
    if (this._indent) {
      i = constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_indent", "Z");
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, this._indent));
      instructionList.append(new PUTFIELD(i));
    } 
    if (this._indentamount != null && !this._indentamount.equals("")) {
      i = constantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_indentamount", "I");
      instructionList.append(DUP);
      instructionList.append(new PUSH(constantPoolGen, Integer.parseInt(this._indentamount)));
      instructionList.append(new PUTFIELD(i));
    } 
    if (this._cdata != null) {
      int j = constantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addCdataElement", "(Ljava/lang/String;)V");
      StringTokenizer stringTokenizer = new StringTokenizer(this._cdata);
      while (stringTokenizer.hasMoreTokens()) {
        instructionList.append(DUP);
        instructionList.append(new PUSH(constantPoolGen, stringTokenizer.nextToken()));
        instructionList.append(new INVOKEVIRTUAL(j));
      } 
    } 
    instructionList.append(POP);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Output.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */