package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import com.sun.org.apache.xerces.internal.impl.xpath.XPathException;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public class Selector {
  protected final XPath fXPath;
  
  protected final IdentityConstraint fIdentityConstraint;
  
  protected IdentityConstraint fIDConstraint;
  
  public Selector(XPath paramXPath, IdentityConstraint paramIdentityConstraint) {
    this.fXPath = paramXPath;
    this.fIdentityConstraint = paramIdentityConstraint;
  }
  
  public XPath getXPath() { return this.fXPath; }
  
  public IdentityConstraint getIDConstraint() { return this.fIdentityConstraint; }
  
  public XPathMatcher createMatcher(FieldActivator paramFieldActivator, int paramInt) { return new Matcher(this.fXPath, paramFieldActivator, paramInt); }
  
  public String toString() { return this.fXPath.toString(); }
  
  public class Matcher extends XPathMatcher {
    protected final FieldActivator fFieldActivator;
    
    protected final int fInitialDepth;
    
    protected int fElementDepth;
    
    protected int fMatchedDepth;
    
    public Matcher(Selector.XPath param1XPath, FieldActivator param1FieldActivator, int param1Int) {
      super(param1XPath);
      this.fFieldActivator = param1FieldActivator;
      this.fInitialDepth = param1Int;
    }
    
    public void startDocumentFragment() {
      super.startDocumentFragment();
      this.fElementDepth = 0;
      this.fMatchedDepth = -1;
    }
    
    public void startElement(QName param1QName, XMLAttributes param1XMLAttributes) {
      super.startElement(param1QName, param1XMLAttributes);
      this.fElementDepth++;
      if (isMatched()) {
        this.fMatchedDepth = this.fElementDepth;
        this.fFieldActivator.startValueScopeFor(Selector.this.fIdentityConstraint, this.fInitialDepth);
        int i = Selector.this.fIdentityConstraint.getFieldCount();
        for (byte b = 0; b < i; b++) {
          Field field = Selector.this.fIdentityConstraint.getFieldAt(b);
          XPathMatcher xPathMatcher = this.fFieldActivator.activateField(field, this.fInitialDepth);
          xPathMatcher.startElement(param1QName, param1XMLAttributes);
        } 
      } 
    }
    
    public void endElement(QName param1QName, XSTypeDefinition param1XSTypeDefinition, boolean param1Boolean, Object param1Object, short param1Short, ShortList param1ShortList) {
      super.endElement(param1QName, param1XSTypeDefinition, param1Boolean, param1Object, param1Short, param1ShortList);
      if (this.fElementDepth-- == this.fMatchedDepth) {
        this.fMatchedDepth = -1;
        this.fFieldActivator.endValueScopeFor(Selector.this.fIdentityConstraint, this.fInitialDepth);
      } 
    }
    
    public IdentityConstraint getIdentityConstraint() { return Selector.this.fIdentityConstraint; }
    
    public int getInitialDepth() { return this.fInitialDepth; }
  }
  
  public static class XPath extends XPath {
    public XPath(String param1String, SymbolTable param1SymbolTable, NamespaceContext param1NamespaceContext) throws XPathException {
      super(normalize(param1String), param1SymbolTable, param1NamespaceContext);
      for (byte b = 0; b < this.fLocationPaths.length; b++) {
        XPath.Axis axis = ((this.fLocationPaths[b]).steps[(this.fLocationPaths[b]).steps.length - 1]).axis;
        if (axis.type == 2)
          throw new XPathException("c-selector-xpath"); 
      } 
    }
    
    private static String normalize(String param1String) {
      StringBuffer stringBuffer = new StringBuffer(param1String.length() + 5);
      int i = -1;
      while (true) {
        if (!XMLChar.trim(param1String).startsWith("/") && !XMLChar.trim(param1String).startsWith("."))
          stringBuffer.append("./"); 
        i = param1String.indexOf('|');
        if (i == -1) {
          stringBuffer.append(param1String);
          break;
        } 
        stringBuffer.append(param1String.substring(0, i + 1));
        param1String = param1String.substring(i + 1, param1String.length());
      } 
      return stringBuffer.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\identity\Selector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */