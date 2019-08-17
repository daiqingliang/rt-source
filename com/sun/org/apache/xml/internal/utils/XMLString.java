package com.sun.org.apache.xml.internal.utils;

import java.util.Locale;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public interface XMLString {
  void dispatchCharactersEvents(ContentHandler paramContentHandler) throws SAXException;
  
  void dispatchAsComment(LexicalHandler paramLexicalHandler) throws SAXException;
  
  XMLString fixWhiteSpace(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3);
  
  int length();
  
  char charAt(int paramInt);
  
  void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3);
  
  boolean equals(XMLString paramXMLString);
  
  boolean equals(String paramString);
  
  boolean equals(Object paramObject);
  
  boolean equalsIgnoreCase(String paramString);
  
  int compareTo(XMLString paramXMLString);
  
  int compareToIgnoreCase(XMLString paramXMLString);
  
  boolean startsWith(String paramString, int paramInt);
  
  boolean startsWith(XMLString paramXMLString, int paramInt);
  
  boolean startsWith(String paramString);
  
  boolean startsWith(XMLString paramXMLString);
  
  boolean endsWith(String paramString);
  
  int hashCode();
  
  int indexOf(int paramInt);
  
  int indexOf(int paramInt1, int paramInt2);
  
  int lastIndexOf(int paramInt);
  
  int lastIndexOf(int paramInt1, int paramInt2);
  
  int indexOf(String paramString);
  
  int indexOf(XMLString paramXMLString);
  
  int indexOf(String paramString, int paramInt);
  
  int lastIndexOf(String paramString);
  
  int lastIndexOf(String paramString, int paramInt);
  
  XMLString substring(int paramInt);
  
  XMLString substring(int paramInt1, int paramInt2);
  
  XMLString concat(String paramString);
  
  XMLString toLowerCase(Locale paramLocale);
  
  XMLString toLowerCase();
  
  XMLString toUpperCase(Locale paramLocale);
  
  XMLString toUpperCase();
  
  XMLString trim();
  
  String toString();
  
  boolean hasString();
  
  double toDouble();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\XMLString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */