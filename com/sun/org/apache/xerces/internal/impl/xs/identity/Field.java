package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import com.sun.org.apache.xerces.internal.impl.xpath.XPathException;
import com.sun.org.apache.xerces.internal.impl.xs.util.ShortListImpl;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSComplexTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;

public class Field {
  protected XPath fXPath;
  
  protected IdentityConstraint fIdentityConstraint;
  
  public Field(XPath paramXPath, IdentityConstraint paramIdentityConstraint) {
    this.fXPath = paramXPath;
    this.fIdentityConstraint = paramIdentityConstraint;
  }
  
  public XPath getXPath() { return this.fXPath; }
  
  public IdentityConstraint getIdentityConstraint() { return this.fIdentityConstraint; }
  
  public XPathMatcher createMatcher(FieldActivator paramFieldActivator, ValueStore paramValueStore) { return new Matcher(this.fXPath, paramFieldActivator, paramValueStore); }
  
  public String toString() { return this.fXPath.toString(); }
  
  protected class Matcher extends XPathMatcher {
    protected FieldActivator fFieldActivator;
    
    protected ValueStore fStore;
    
    public Matcher(Field.XPath param1XPath, FieldActivator param1FieldActivator, ValueStore param1ValueStore) {
      super(param1XPath);
      this.fFieldActivator = param1FieldActivator;
      this.fStore = param1ValueStore;
    }
    
    protected void matched(Object param1Object, short param1Short, ShortList param1ShortList, boolean param1Boolean) {
      super.matched(param1Object, param1Short, param1ShortList, param1Boolean);
      if (param1Boolean && Field.this.fIdentityConstraint.getCategory() == 1) {
        String str = "KeyMatchesNillable";
        this.fStore.reportError(str, new Object[] { Field.this.fIdentityConstraint.getElementName(), Field.this.fIdentityConstraint.getIdentityConstraintName() });
      } 
      this.fStore.addValue(Field.this, param1Object, convertToPrimitiveKind(param1Short), convertToPrimitiveKind(param1ShortList));
      this.fFieldActivator.setMayMatch(Field.this, Boolean.FALSE);
    }
    
    private short convertToPrimitiveKind(short param1Short) { return (param1Short <= 20) ? param1Short : ((param1Short <= 29) ? 2 : ((param1Short <= 42) ? 4 : param1Short)); }
    
    private ShortList convertToPrimitiveKind(ShortList param1ShortList) {
      if (param1ShortList != null) {
        int i = param1ShortList.getLength();
        byte b;
        for (b = 0; b < i; b++) {
          short s = param1ShortList.item(b);
          if (s != convertToPrimitiveKind(s))
            break; 
        } 
        if (b != i) {
          short[] arrayOfShort = new short[i];
          for (byte b1 = 0; b1 < b; b1++)
            arrayOfShort[b1] = param1ShortList.item(b1); 
          while (b < i) {
            arrayOfShort[b] = convertToPrimitiveKind(param1ShortList.item(b));
            b++;
          } 
          return new ShortListImpl(arrayOfShort, arrayOfShort.length);
        } 
      } 
      return param1ShortList;
    }
    
    protected void handleContent(XSTypeDefinition param1XSTypeDefinition, boolean param1Boolean, Object param1Object, short param1Short, ShortList param1ShortList) {
      if (param1XSTypeDefinition == null || (param1XSTypeDefinition.getTypeCategory() == 15 && ((XSComplexTypeDefinition)param1XSTypeDefinition).getContentType() != 1))
        this.fStore.reportError("cvc-id.3", new Object[] { Field.this.fIdentityConstraint.getName(), Field.this.fIdentityConstraint.getElementName() }); 
      this.fMatchedString = param1Object;
      matched(this.fMatchedString, param1Short, param1ShortList, param1Boolean);
    }
  }
  
  public static class XPath extends XPath {
    public XPath(String param1String, SymbolTable param1SymbolTable, NamespaceContext param1NamespaceContext) throws XPathException {
      super((param1String.trim().startsWith("/") || param1String.trim().startsWith(".")) ? param1String : ("./" + param1String), param1SymbolTable, param1NamespaceContext);
      for (byte b = 0; b < this.fLocationPaths.length; b++) {
        for (byte b1 = 0; b1 < (this.fLocationPaths[b]).steps.length; b1++) {
          XPath.Axis axis = ((this.fLocationPaths[b]).steps[b1]).axis;
          if (axis.type == 2 && b1 < (this.fLocationPaths[b]).steps.length - 1)
            throw new XPathException("c-fields-xpaths"); 
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\identity\Field.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */