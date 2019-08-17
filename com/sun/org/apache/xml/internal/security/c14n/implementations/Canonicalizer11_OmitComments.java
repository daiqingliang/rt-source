package com.sun.org.apache.xml.internal.security.c14n.implementations;

public class Canonicalizer11_OmitComments extends Canonicalizer11 {
  public Canonicalizer11_OmitComments() { super(false); }
  
  public final String engineGetURI() { return "http://www.w3.org/2006/12/xml-c14n11"; }
  
  public final boolean engineGetIncludeComments() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\Canonicalizer11_OmitComments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */