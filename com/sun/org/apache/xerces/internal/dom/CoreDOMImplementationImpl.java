package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.parsers.DOMParserImpl;
import com.sun.org.apache.xerces.internal.parsers.DTDConfiguration;
import com.sun.org.apache.xerces.internal.parsers.XIncludeAwareParserConfiguration;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xml.internal.serialize.DOMSerializerImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

public class CoreDOMImplementationImpl implements DOMImplementation, DOMImplementationLS {
  private static final int SIZE = 2;
  
  private RevalidationHandler[] validators = new RevalidationHandler[2];
  
  private RevalidationHandler[] dtdValidators = new RevalidationHandler[2];
  
  private int freeValidatorIndex = -1;
  
  private int freeDTDValidatorIndex = -1;
  
  private int currentSize = 2;
  
  private int docAndDoctypeCounter = 0;
  
  static CoreDOMImplementationImpl singleton = new CoreDOMImplementationImpl();
  
  public static DOMImplementation getDOMImplementation() { return singleton; }
  
  public boolean hasFeature(String paramString1, String paramString2) {
    boolean bool = (paramString2 == null || paramString2.length() == 0) ? 1 : 0;
    if (paramString1.equalsIgnoreCase("+XPath") && (bool || paramString2.equals("3.0"))) {
      try {
        Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
        Class[] arrayOfClass = clazz.getInterfaces();
        for (byte b = 0; b < arrayOfClass.length; b++) {
          if (arrayOfClass[b].getName().equals("org.w3c.dom.xpath.XPathEvaluator"))
            return true; 
        } 
      } catch (Exception exception) {
        return false;
      } 
      return true;
    } 
    if (paramString1.startsWith("+"))
      paramString1 = paramString1.substring(1); 
    return ((paramString1.equalsIgnoreCase("Core") && (bool || paramString2.equals("1.0") || paramString2.equals("2.0") || paramString2.equals("3.0"))) || (paramString1.equalsIgnoreCase("XML") && (bool || paramString2.equals("1.0") || paramString2.equals("2.0") || paramString2.equals("3.0"))) || (paramString1.equalsIgnoreCase("LS") && (bool || paramString2.equals("3.0"))));
  }
  
  public DocumentType createDocumentType(String paramString1, String paramString2, String paramString3) {
    checkQName(paramString1);
    return new DocumentTypeImpl(null, paramString1, paramString2, paramString3);
  }
  
  final void checkQName(String paramString) {
    int i = paramString.indexOf(':');
    int j = paramString.lastIndexOf(':');
    int k = paramString.length();
    if (i == 0 || i == k - 1 || j != i) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
      throw new DOMException((short)14, str);
    } 
    int m = 0;
    if (i > 0) {
      if (!XMLChar.isNCNameStart(paramString.charAt(m))) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
        throw new DOMException((short)5, str);
      } 
      for (byte b = 1; b < i; b++) {
        if (!XMLChar.isNCName(paramString.charAt(b))) {
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
          throw new DOMException((short)5, str);
        } 
      } 
      m = i + 1;
    } 
    if (!XMLChar.isNCNameStart(paramString.charAt(m))) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    } 
    for (int n = m + 1; n < k; n++) {
      if (!XMLChar.isNCName(paramString.charAt(n))) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
        throw new DOMException((short)5, str);
      } 
    } 
  }
  
  public Document createDocument(String paramString1, String paramString2, DocumentType paramDocumentType) throws DOMException {
    if (paramDocumentType != null && paramDocumentType.getOwnerDocument() != null) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
      throw new DOMException((short)4, str);
    } 
    CoreDocumentImpl coreDocumentImpl = new CoreDocumentImpl(paramDocumentType);
    Element element = coreDocumentImpl.createElementNS(paramString1, paramString2);
    coreDocumentImpl.appendChild(element);
    return coreDocumentImpl;
  }
  
  public Object getFeature(String paramString1, String paramString2) {
    if (singleton.hasFeature(paramString1, paramString2))
      if (paramString1.equalsIgnoreCase("+XPath")) {
        try {
          Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
          Class[] arrayOfClass = clazz.getInterfaces();
          for (byte b = 0; b < arrayOfClass.length; b++) {
            if (arrayOfClass[b].getName().equals("org.w3c.dom.xpath.XPathEvaluator"))
              return clazz.newInstance(); 
          } 
        } catch (Exception exception) {
          return null;
        } 
      } else {
        return singleton;
      }  
    return null;
  }
  
  public LSParser createLSParser(short paramShort, String paramString) throws DOMException {
    if (paramShort != 1 || (paramString != null && !"http://www.w3.org/2001/XMLSchema".equals(paramString) && !"http://www.w3.org/TR/REC-xml".equals(paramString))) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, str);
    } 
    return (paramString != null && paramString.equals("http://www.w3.org/TR/REC-xml")) ? new DOMParserImpl(new DTDConfiguration(), paramString) : new DOMParserImpl(new XIncludeAwareParserConfiguration(), paramString);
  }
  
  public LSSerializer createLSSerializer() { return new DOMSerializerImpl(); }
  
  public LSInput createLSInput() { return new DOMInputImpl(); }
  
  RevalidationHandler getValidator(String paramString) {
    if (paramString == "http://www.w3.org/2001/XMLSchema") {
      if (this.freeValidatorIndex < 0)
        return (RevalidationHandler)ObjectFactory.newInstance("com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator", ObjectFactory.findClassLoader(), true); 
      RevalidationHandler revalidationHandler = this.validators[this.freeValidatorIndex];
      this.validators[this.freeValidatorIndex--] = null;
      return revalidationHandler;
    } 
    if (paramString == "http://www.w3.org/TR/REC-xml") {
      if (this.freeDTDValidatorIndex < 0)
        return (RevalidationHandler)ObjectFactory.newInstance("com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator", ObjectFactory.findClassLoader(), true); 
      RevalidationHandler revalidationHandler = this.dtdValidators[this.freeDTDValidatorIndex];
      this.dtdValidators[this.freeDTDValidatorIndex--] = null;
      return revalidationHandler;
    } 
    return null;
  }
  
  void releaseValidator(String paramString, RevalidationHandler paramRevalidationHandler) {
    if (paramString == "http://www.w3.org/2001/XMLSchema") {
      this.freeValidatorIndex++;
      if (this.validators.length == this.freeValidatorIndex) {
        this.currentSize += 2;
        RevalidationHandler[] arrayOfRevalidationHandler = new RevalidationHandler[this.currentSize];
        System.arraycopy(this.validators, 0, arrayOfRevalidationHandler, 0, this.validators.length);
        this.validators = arrayOfRevalidationHandler;
      } 
      this.validators[this.freeValidatorIndex] = paramRevalidationHandler;
    } else if (paramString == "http://www.w3.org/TR/REC-xml") {
      this.freeDTDValidatorIndex++;
      if (this.dtdValidators.length == this.freeDTDValidatorIndex) {
        this.currentSize += 2;
        RevalidationHandler[] arrayOfRevalidationHandler = new RevalidationHandler[this.currentSize];
        System.arraycopy(this.dtdValidators, 0, arrayOfRevalidationHandler, 0, this.dtdValidators.length);
        this.dtdValidators = arrayOfRevalidationHandler;
      } 
      this.dtdValidators[this.freeDTDValidatorIndex] = paramRevalidationHandler;
    } 
  }
  
  protected int assignDocumentNumber() { return ++this.docAndDoctypeCounter; }
  
  protected int assignDocTypeNumber() { return ++this.docAndDoctypeCounter; }
  
  public LSOutput createLSOutput() { return new DOMOutputImpl(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\CoreDOMImplementationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */