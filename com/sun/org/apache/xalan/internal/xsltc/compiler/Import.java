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

final class Import extends TopLevelElement {
  private Stylesheet _imported = null;
  
  public Stylesheet getImportedStylesheet() { return this._imported; }
  
  public void parseContents(Parser paramParser) {
    XSLTC xSLTC = paramParser.getXSLTC();
    stylesheet = paramParser.getCurrentStylesheet();
    try {
      SyntaxTreeNode syntaxTreeNode;
      String str1 = getAttribute("href");
      if (stylesheet.checkForLoop(str1)) {
        ErrorMsg errorMsg = new ErrorMsg("CIRCULAR_INCLUDE_ERR", str1, this);
        paramParser.reportError(2, errorMsg);
        return;
      } 
      InputSource inputSource = null;
      XMLReader xMLReader = null;
      String str2 = stylesheet.getSystemId();
      SourceLoader sourceLoader = stylesheet.getSourceLoader();
      if (sourceLoader != null) {
        inputSource = sourceLoader.loadSource(str1, str2, xSLTC);
        if (inputSource != null) {
          str1 = inputSource.getSystemId();
          xMLReader = xSLTC.getXMLReader();
        } else if (paramParser.errorsFound()) {
          return;
        } 
      } 
      if (inputSource == null) {
        str1 = SystemIDResolver.getAbsoluteURI(str1, str2);
        syntaxTreeNode = SecuritySupport.checkAccess(str1, (String)xSLTC.getProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet"), "all");
        if (syntaxTreeNode != null) {
          ErrorMsg errorMsg = new ErrorMsg("ACCESSING_XSLT_TARGET_ERR", SecuritySupport.sanitizePath(str1), syntaxTreeNode, this);
          paramParser.reportError(2, errorMsg);
          return;
        } 
        inputSource = new InputSource(str1);
      } 
      if (inputSource == null) {
        syntaxTreeNode = new ErrorMsg("FILE_NOT_FOUND_ERR", str1, this);
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
      this._imported = paramParser.makeStylesheet(syntaxTreeNode);
      if (this._imported == null)
        return; 
      this._imported.setSourceLoader(sourceLoader);
      this._imported.setSystemId(str1);
      this._imported.setParentStylesheet(stylesheet);
      this._imported.setImportingStylesheet(stylesheet);
      this._imported.setTemplateInlining(stylesheet.getTemplateInlining());
      int i = paramParser.getCurrentImportPrecedence();
      int j = paramParser.getNextImportPrecedence();
      this._imported.setImportPrecedence(i);
      stylesheet.setImportPrecedence(j);
      paramParser.setCurrentStylesheet(this._imported);
      this._imported.parseContents(paramParser);
      Iterator iterator = this._imported.elements();
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Import.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */