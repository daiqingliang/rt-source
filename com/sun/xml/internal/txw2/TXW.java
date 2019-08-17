package com.sun.xml.internal.txw2;

import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.annotation.XmlNamespace;
import com.sun.xml.internal.txw2.output.TXWSerializer;
import com.sun.xml.internal.txw2.output.XmlSerializer;
import javax.xml.namespace.QName;

public abstract class TXW {
  static QName getTagName(Class<?> paramClass) {
    String str1 = "";
    String str2 = "##default";
    XmlElement xmlElement = (XmlElement)paramClass.getAnnotation(XmlElement.class);
    if (xmlElement != null) {
      str1 = xmlElement.value();
      str2 = xmlElement.ns();
    } 
    if (str1.length() == 0) {
      str1 = paramClass.getName();
      int i = str1.lastIndexOf('.');
      if (i >= 0)
        str1 = str1.substring(i + 1); 
      str1 = Character.toLowerCase(str1.charAt(0)) + str1.substring(1);
    } 
    if (str2.equals("##default")) {
      Package package = paramClass.getPackage();
      if (package != null) {
        XmlNamespace xmlNamespace = (XmlNamespace)package.getAnnotation(XmlNamespace.class);
        if (xmlNamespace != null)
          str2 = xmlNamespace.value(); 
      } 
    } 
    if (str2.equals("##default"))
      str2 = ""; 
    return new QName(str2, str1);
  }
  
  public static <T extends TypedXmlWriter> T create(Class<T> paramClass, XmlSerializer paramXmlSerializer) {
    if (paramXmlSerializer instanceof TXWSerializer) {
      TXWSerializer tXWSerializer = (TXWSerializer)paramXmlSerializer;
      return (T)tXWSerializer.txw._element(paramClass);
    } 
    Document document = new Document(paramXmlSerializer);
    QName qName = getTagName(paramClass);
    return (T)(new ContainerElement(document, null, qName.getNamespaceURI(), qName.getLocalPart()))._cast(paramClass);
  }
  
  public static <T extends TypedXmlWriter> T create(QName paramQName, Class<T> paramClass, XmlSerializer paramXmlSerializer) {
    if (paramXmlSerializer instanceof TXWSerializer) {
      TXWSerializer tXWSerializer = (TXWSerializer)paramXmlSerializer;
      return (T)tXWSerializer.txw._element(paramQName, paramClass);
    } 
    return (T)(new ContainerElement(new Document(paramXmlSerializer), null, paramQName.getNamespaceURI(), paramQName.getLocalPart()))._cast(paramClass);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\TXW.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */