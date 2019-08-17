package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationSource;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSchemaTypes;
import javax.xml.namespace.QName;

final class Util {
  static <T, C, F, M> QName calcSchemaType(AnnotationReader<T, C, F, M> paramAnnotationReader, AnnotationSource paramAnnotationSource, C paramC, T paramT, Locatable paramLocatable) {
    XmlSchemaType xmlSchemaType = (XmlSchemaType)paramAnnotationSource.readAnnotation(XmlSchemaType.class);
    if (xmlSchemaType != null)
      return new QName(xmlSchemaType.namespace(), xmlSchemaType.name()); 
    XmlSchemaTypes xmlSchemaTypes = (XmlSchemaTypes)paramAnnotationReader.getPackageAnnotation(XmlSchemaTypes.class, paramC, paramLocatable);
    XmlSchemaType[] arrayOfXmlSchemaType = null;
    if (xmlSchemaTypes != null) {
      arrayOfXmlSchemaType = xmlSchemaTypes.value();
    } else {
      xmlSchemaType = (XmlSchemaType)paramAnnotationReader.getPackageAnnotation(XmlSchemaType.class, paramC, paramLocatable);
      if (xmlSchemaType != null) {
        arrayOfXmlSchemaType = new XmlSchemaType[1];
        arrayOfXmlSchemaType[0] = xmlSchemaType;
      } 
    } 
    if (arrayOfXmlSchemaType != null)
      for (XmlSchemaType xmlSchemaType1 : arrayOfXmlSchemaType) {
        if (paramAnnotationReader.getClassValue(xmlSchemaType1, "type").equals(paramT))
          return new QName(xmlSchemaType1.namespace(), xmlSchemaType1.name()); 
      }  
    return null;
  }
  
  static MimeType calcExpectedMediaType(AnnotationSource paramAnnotationSource, ModelBuilder paramModelBuilder) {
    XmlMimeType xmlMimeType = (XmlMimeType)paramAnnotationSource.readAnnotation(XmlMimeType.class);
    if (xmlMimeType == null)
      return null; 
    try {
      return new MimeType(xmlMimeType.value());
    } catch (MimeTypeParseException mimeTypeParseException) {
      paramModelBuilder.reportError(new IllegalAnnotationException(Messages.ILLEGAL_MIME_TYPE.format(new Object[] { xmlMimeType.value(), mimeTypeParseException.getMessage() }, ), xmlMimeType));
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */