package com.sun.xml.internal.ws.streaming;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderUtil {
  public static void close(XMLStreamReader paramXMLStreamReader) {
    try {
      paramXMLStreamReader.close();
    } catch (XMLStreamException xMLStreamException) {
      throw wrapException(xMLStreamException);
    } 
  }
  
  public static void readRest(XMLStreamReader paramXMLStreamReader) {
    try {
      while (paramXMLStreamReader.getEventType() != 8)
        paramXMLStreamReader.next(); 
    } catch (XMLStreamException xMLStreamException) {
      throw wrapException(xMLStreamException);
    } 
  }
  
  public static int next(XMLStreamReader paramXMLStreamReader) {
    try {
      int i;
      for (i = paramXMLStreamReader.next(); i != 8; i = paramXMLStreamReader.next()) {
        switch (i) {
          case 1:
          case 2:
          case 3:
          case 4:
          case 12:
            return i;
        } 
      } 
      return i;
    } catch (XMLStreamException xMLStreamException) {
      throw wrapException(xMLStreamException);
    } 
  }
  
  public static int nextElementContent(XMLStreamReader paramXMLStreamReader) {
    int i = nextContent(paramXMLStreamReader);
    if (i == 4)
      throw new XMLStreamReaderException("xmlreader.unexpectedCharacterContent", new Object[] { paramXMLStreamReader.getText() }); 
    return i;
  }
  
  public static void toNextTag(XMLStreamReader paramXMLStreamReader, QName paramQName) {
    if (paramXMLStreamReader.getEventType() != 1 && paramXMLStreamReader.getEventType() != 2)
      nextElementContent(paramXMLStreamReader); 
    if (paramXMLStreamReader.getEventType() == 2 && paramQName.equals(paramXMLStreamReader.getName()))
      nextElementContent(paramXMLStreamReader); 
  }
  
  public static String nextWhiteSpaceContent(XMLStreamReader paramXMLStreamReader) {
    next(paramXMLStreamReader);
    return currentWhiteSpaceContent(paramXMLStreamReader);
  }
  
  public static String currentWhiteSpaceContent(XMLStreamReader paramXMLStreamReader) {
    StringBuilder stringBuilder = null;
    while (true) {
      switch (paramXMLStreamReader.getEventType()) {
        case 1:
        case 2:
        case 8:
          return (stringBuilder == null) ? null : stringBuilder.toString();
        case 4:
          if (paramXMLStreamReader.isWhiteSpace()) {
            if (stringBuilder == null)
              stringBuilder = new StringBuilder(); 
            stringBuilder.append(paramXMLStreamReader.getText());
            break;
          } 
          throw new XMLStreamReaderException("xmlreader.unexpectedCharacterContent", new Object[] { paramXMLStreamReader.getText() });
      } 
      next(paramXMLStreamReader);
    } 
  }
  
  public static int nextContent(XMLStreamReader paramXMLStreamReader) {
    while (true) {
      int i = next(paramXMLStreamReader);
      switch (i) {
        case 1:
        case 2:
        case 8:
          return i;
        case 4:
          if (!paramXMLStreamReader.isWhiteSpace())
            break; 
      } 
    } 
    return 4;
  }
  
  public static void skipElement(XMLStreamReader paramXMLStreamReader) {
    assert paramXMLStreamReader.getEventType() == 1;
    skipTags(paramXMLStreamReader, true);
    assert paramXMLStreamReader.getEventType() == 2;
  }
  
  public static void skipSiblings(XMLStreamReader paramXMLStreamReader, QName paramQName) {
    skipTags(paramXMLStreamReader, paramXMLStreamReader.getName().equals(paramQName));
    assert paramXMLStreamReader.getEventType() == 2;
  }
  
  private static void skipTags(XMLStreamReader paramXMLStreamReader, boolean paramBoolean) {
    try {
      byte b = 0;
      int i;
      while ((i = paramXMLStreamReader.next()) != 8) {
        if (i == 1) {
          b++;
          continue;
        } 
        if (i == 2) {
          if (b == 0 && paramBoolean)
            return; 
          b--;
        } 
      } 
    } catch (XMLStreamException xMLStreamException) {
      throw wrapException(xMLStreamException);
    } 
  }
  
  public static String getElementText(XMLStreamReader paramXMLStreamReader) {
    try {
      return paramXMLStreamReader.getElementText();
    } catch (XMLStreamException xMLStreamException) {
      throw wrapException(xMLStreamException);
    } 
  }
  
  public static QName getElementQName(XMLStreamReader paramXMLStreamReader) {
    try {
      String str1 = paramXMLStreamReader.getElementText().trim();
      String str2 = str1.substring(0, str1.indexOf(':'));
      String str3 = paramXMLStreamReader.getNamespaceContext().getNamespaceURI(str2);
      if (str3 == null)
        str3 = ""; 
      String str4 = str1.substring(str1.indexOf(':') + 1, str1.length());
      return new QName(str3, str4);
    } catch (XMLStreamException xMLStreamException) {
      throw wrapException(xMLStreamException);
    } 
  }
  
  public static Attributes getAttributes(XMLStreamReader paramXMLStreamReader) { return (paramXMLStreamReader.getEventType() == 1 || paramXMLStreamReader.getEventType() == 10) ? new AttributesImpl(paramXMLStreamReader) : null; }
  
  public static void verifyReaderState(XMLStreamReader paramXMLStreamReader, int paramInt) {
    int i = paramXMLStreamReader.getEventType();
    if (i != paramInt)
      throw new XMLStreamReaderException("xmlreader.unexpectedState", new Object[] { getStateName(paramInt), getStateName(i) }); 
  }
  
  public static void verifyTag(XMLStreamReader paramXMLStreamReader, String paramString1, String paramString2) {
    if (!paramString2.equals(paramXMLStreamReader.getLocalName()) || !paramString1.equals(paramXMLStreamReader.getNamespaceURI()))
      throw new XMLStreamReaderException("xmlreader.unexpectedState.tag", new Object[] { "{" + paramString1 + "}" + paramString2, "{" + paramXMLStreamReader.getNamespaceURI() + "}" + paramXMLStreamReader.getLocalName() }); 
  }
  
  public static void verifyTag(XMLStreamReader paramXMLStreamReader, QName paramQName) { verifyTag(paramXMLStreamReader, paramQName.getNamespaceURI(), paramQName.getLocalPart()); }
  
  public static String getStateName(XMLStreamReader paramXMLStreamReader) { return getStateName(paramXMLStreamReader.getEventType()); }
  
  public static String getStateName(int paramInt) {
    switch (paramInt) {
      case 10:
        return "ATTRIBUTE";
      case 12:
        return "CDATA";
      case 4:
        return "CHARACTERS";
      case 5:
        return "COMMENT";
      case 11:
        return "DTD";
      case 8:
        return "END_DOCUMENT";
      case 2:
        return "END_ELEMENT";
      case 15:
        return "ENTITY_DECLARATION";
      case 9:
        return "ENTITY_REFERENCE";
      case 13:
        return "NAMESPACE";
      case 14:
        return "NOTATION_DECLARATION";
      case 3:
        return "PROCESSING_INSTRUCTION";
      case 6:
        return "SPACE";
      case 7:
        return "START_DOCUMENT";
      case 1:
        return "START_ELEMENT";
    } 
    return "UNKNOWN";
  }
  
  private static XMLStreamReaderException wrapException(XMLStreamException paramXMLStreamException) { return new XMLStreamReaderException("xmlreader.ioException", new Object[] { paramXMLStreamException }); }
  
  public static class AttributesImpl implements Attributes {
    static final String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
    
    AttributeInfo[] atInfos;
    
    public AttributesImpl(XMLStreamReader param1XMLStreamReader) {
      if (param1XMLStreamReader == null) {
        this.atInfos = new AttributeInfo[0];
      } else {
        byte b1 = 0;
        int i = param1XMLStreamReader.getNamespaceCount();
        int j = param1XMLStreamReader.getAttributeCount();
        this.atInfos = new AttributeInfo[i + j];
        byte b2;
        for (b2 = 0; b2 < i; b2++) {
          String str = param1XMLStreamReader.getNamespacePrefix(b2);
          if (str == null)
            str = ""; 
          this.atInfos[b1++] = new AttributeInfo(new QName("http://www.w3.org/2000/xmlns/", str, "xmlns"), param1XMLStreamReader.getNamespaceURI(b2));
        } 
        for (b2 = 0; b2 < j; b2++)
          this.atInfos[b1++] = new AttributeInfo(param1XMLStreamReader.getAttributeName(b2), param1XMLStreamReader.getAttributeValue(b2)); 
      } 
    }
    
    public int getLength() { return this.atInfos.length; }
    
    public String getLocalName(int param1Int) { return (param1Int >= 0 && param1Int < this.atInfos.length) ? this.atInfos[param1Int].getLocalName() : null; }
    
    public QName getName(int param1Int) { return (param1Int >= 0 && param1Int < this.atInfos.length) ? this.atInfos[param1Int].getName() : null; }
    
    public String getPrefix(int param1Int) { return (param1Int >= 0 && param1Int < this.atInfos.length) ? this.atInfos[param1Int].getName().getPrefix() : null; }
    
    public String getURI(int param1Int) { return (param1Int >= 0 && param1Int < this.atInfos.length) ? this.atInfos[param1Int].getName().getNamespaceURI() : null; }
    
    public String getValue(int param1Int) { return (param1Int >= 0 && param1Int < this.atInfos.length) ? this.atInfos[param1Int].getValue() : null; }
    
    public String getValue(QName param1QName) {
      int i = getIndex(param1QName);
      return (i != -1) ? this.atInfos[i].getValue() : null;
    }
    
    public String getValue(String param1String) {
      int i = getIndex(param1String);
      return (i != -1) ? this.atInfos[i].getValue() : null;
    }
    
    public String getValue(String param1String1, String param1String2) {
      int i = getIndex(param1String1, param1String2);
      return (i != -1) ? this.atInfos[i].getValue() : null;
    }
    
    public boolean isNamespaceDeclaration(int param1Int) { return (param1Int >= 0 && param1Int < this.atInfos.length) ? this.atInfos[param1Int].isNamespaceDeclaration() : 0; }
    
    public int getIndex(QName param1QName) {
      for (byte b = 0; b < this.atInfos.length; b++) {
        if (this.atInfos[b].getName().equals(param1QName))
          return b; 
      } 
      return -1;
    }
    
    public int getIndex(String param1String) {
      for (byte b = 0; b < this.atInfos.length; b++) {
        if (this.atInfos[b].getName().getLocalPart().equals(param1String))
          return b; 
      } 
      return -1;
    }
    
    public int getIndex(String param1String1, String param1String2) {
      for (byte b = 0; b < this.atInfos.length; b++) {
        QName qName = this.atInfos[b].getName();
        if (qName.getNamespaceURI().equals(param1String1) && qName.getLocalPart().equals(param1String2))
          return b; 
      } 
      return -1;
    }
    
    static class AttributeInfo {
      private QName name;
      
      private String value;
      
      public AttributeInfo(QName param2QName, String param2String) {
        this.name = param2QName;
        if (param2String == null) {
          this.value = "";
        } else {
          this.value = param2String;
        } 
      }
      
      QName getName() { return this.name; }
      
      String getValue() { return this.value; }
      
      String getLocalName() { return isNamespaceDeclaration() ? (this.name.getLocalPart().equals("") ? "xmlns" : ("xmlns:" + this.name.getLocalPart())) : this.name.getLocalPart(); }
      
      boolean isNamespaceDeclaration() { return (this.name.getNamespaceURI() == "http://www.w3.org/2000/xmlns/"); }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\streaming\XMLStreamReaderUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */