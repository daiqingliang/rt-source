package com.sun.org.apache.xml.internal.security.c14n.implementations;

public class Canonicalizer20010315ExclOmitComments extends Canonicalizer20010315Excl {
  public Canonicalizer20010315ExclOmitComments() { super(false); }
  
  public final String engineGetURI() { return "http://www.w3.org/2001/10/xml-exc-c14n#"; }
  
  public final boolean engineGetIncludeComments() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\Canonicalizer20010315ExclOmitComments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */