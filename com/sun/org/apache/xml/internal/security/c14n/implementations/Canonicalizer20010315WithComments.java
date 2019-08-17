package com.sun.org.apache.xml.internal.security.c14n.implementations;

public class Canonicalizer20010315WithComments extends Canonicalizer20010315 {
  public Canonicalizer20010315WithComments() { super(true); }
  
  public final String engineGetURI() { return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments"; }
  
  public final boolean engineGetIncludeComments() { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\implementations\Canonicalizer20010315WithComments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */