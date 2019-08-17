package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import java.util.Iterator;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

final class Include extends TopLevelElement {
  private Stylesheet _included = null;
  
  public Stylesheet getIncludedStylesheet() { return this._included; }
  
  public void parseContents(Parser paramParser) {
    XSLTC xSLTC = paramParser.getXSLTC();
    stylesheet = paramParser.getCurrentStylesheet();
    String str = getAttribute("href");
    try {
      SyntaxTreeNode syntaxTreeNode;
      if (stylesheet.checkForLoop(str)) {
        ErrorMsg errorMsg = new ErrorMsg("CIRCULAR_INCLUDE_ERR", str, this);
        paramParser.reportError(2, errorMsg);
        return;
      } 
      InputSource inputSource = null;
      XMLReader xMLReader = null;
      String str1 = stylesheet.getSystemId();
      SourceLoader sourceLoader = stylesheet.getSourceLoader();
      if (sourceLoader != null) {
        inputSource = sourceLoader.loadSource(str, str1, xSLTC);
        if (inputSource != null) {
          str = inputSource.getSystemId();
          xMLReader = xSLTC.getXMLReader();
        } else if (paramParser.errorsFound()) {
          return;
        } 
      } 
      if (inputSource == null) {
        str = SystemIDResolver.getAbsoluteURI(str, str1);
        syntaxTreeNode = SecuritySupport.checkAccess(str, (String)xSLTC.getProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet"), "all");
        if (syntaxTreeNode != null) {
          ErrorMsg errorMsg = new ErrorMsg("ACCESSING_XSLT_TARGET_ERR", SecuritySupport.sanitizePath(str), syntaxTreeNode, this);
          paramParser.reportError(2, errorMsg);
          return;
        } 
        inputSource = new InputSource(str);
      } 
      if (inputSource == null) {
        syntaxTreeNode = new ErrorMsg("FILE_NOT_FOUND_ERR", str, this);
        paramParser.reportError(2, syntaxTreeNode);
        return;
      } 
      if (xMLReader != null) {
        syntaxTreeNode = paramParser.parse(xMLReader, inputSource);
      } else {
        syntaxTreeNode = paramParser.parse(inputSource);
      } 
      if (syntaxTreeNode == null)
        return; 
      this._included = paramParser.makeStylesheet(syntaxTreeNode);
      if (this._included == null)
        return; 
      this._included.setSourceLoader(sourceLoader);
      this._included.setSystemId(str);
      this._included.setParentStylesheet(stylesheet);
      this._included.setIncludingStylesheet(stylesheet);
      this._included.setTemplateInlining(stylesheet.getTemplateInlining());
      int i = stylesheet.getImportPrecedence();
      this._included.setImportPrecedence(i);
      paramParser.setCurrentStylesheet(this._included);
      this._included.parseContents(paramParser);
      Iterator iterator = this._included.elements();
      Stylesheet stylesheet1 = paramParser.getTopLevelStylesheet();
      while (iterator.hasNext()) {
        SyntaxTreeNode syntaxTreeNode1 = (SyntaxTreeNode)iterator.next();
        if (syntaxTreeNode1 instanceof TopLevelElement) {
          if (syntaxTreeNode1 instanceof Variable) {
            stylesheet1.addVariable((Variable)syntaxTreeNode1);
            continue;
          } 
          if (syntaxTreeNode1 instanceof Param) {
            stylesheet1.addParam((Param)syntaxTreeNode1);
            continue;
          } 
          stylesheet1.addElement((TopLevelElement)syntaxTreeNode1);
        } 
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } finally {
      paramParser.setCurrentStylesheet(stylesheet);
    } 
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError { return Type.Void; }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Include.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */