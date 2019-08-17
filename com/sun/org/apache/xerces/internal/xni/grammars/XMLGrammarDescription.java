package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

public interface XMLGrammarDescription extends XMLResourceIdentifier {
  public static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  
  public static final String XML_DTD = "http://www.w3.org/TR/REC-xml";
  
  String getGrammarType();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\grammars\XMLGrammarDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */