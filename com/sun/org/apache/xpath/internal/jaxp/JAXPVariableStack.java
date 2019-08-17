package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathVariableResolver;

public class JAXPVariableStack extends VariableStack {
  private final XPathVariableResolver resolver;
  
  public JAXPVariableStack(XPathVariableResolver paramXPathVariableResolver) { this.resolver = paramXPathVariableResolver; }
  
  public XObject getVariableOrParam(XPathContext paramXPathContext, QName paramQName) throws TransformerException, IllegalArgumentException {
    if (paramQName == null) {
      String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "Variable qname" });
      throw new IllegalArgumentException(str);
    } 
    QName qName = new QName(paramQName.getNamespace(), paramQName.getLocalPart());
    Object object = this.resolver.resolveVariable(qName);
    if (object == null) {
      String str = XSLMessages.createXPATHMessage("ER_RESOLVE_VARIABLE_RETURNS_NULL", new Object[] { qName.toString() });
      throw new TransformerException(str);
    } 
    return XObject.create(object, paramXPathContext);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\jaxp\JAXPVariableStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */