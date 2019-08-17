package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;

public interface XMLSchemaDescription extends XMLGrammarDescription {
  public static final short CONTEXT_INCLUDE = 0;
  
  public static final short CONTEXT_REDEFINE = 1;
  
  public static final short CONTEXT_IMPORT = 2;
  
  public static final short CONTEXT_PREPARSE = 3;
  
  public static final short CONTEXT_INSTANCE = 4;
  
  public static final short CONTEXT_ELEMENT = 5;
  
  public static final short CONTEXT_ATTRIBUTE = 6;
  
  public static final short CONTEXT_XSITYPE = 7;
  
  short getContextType();
  
  String getTargetNamespace();
  
  String[] getLocationHints();
  
  QName getTriggeringComponent();
  
  QName getEnclosingElementName();
  
  XMLAttributes getAttributes();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\grammars\XMLSchemaDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */