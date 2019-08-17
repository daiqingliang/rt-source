package com.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.functions.FuncExtFunction;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.ArrayList;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;
import jdk.xml.internal.JdkXmlFeatures;

public class JAXPExtensionsProvider implements ExtensionsProvider {
  private final XPathFunctionResolver resolver;
  
  private boolean extensionInvocationDisabled = false;
  
  public JAXPExtensionsProvider(XPathFunctionResolver paramXPathFunctionResolver) {
    this.resolver = paramXPathFunctionResolver;
    this.extensionInvocationDisabled = false;
  }
  
  public JAXPExtensionsProvider(XPathFunctionResolver paramXPathFunctionResolver, boolean paramBoolean, JdkXmlFeatures paramJdkXmlFeatures) {
    this.resolver = paramXPathFunctionResolver;
    if (paramBoolean && !paramJdkXmlFeatures.getFeature(JdkXmlFeatures.XmlFeature.ENABLE_EXTENSION_FUNCTION))
      this.extensionInvocationDisabled = true; 
  }
  
  public boolean functionAvailable(String paramString1, String paramString2) throws TransformerException {
    try {
      if (paramString2 == null) {
        String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "Function Name" });
        throw new NullPointerException(str);
      } 
      QName qName = new QName(paramString1, paramString2);
      XPathFunction xPathFunction = this.resolver.resolveFunction(qName, 0);
      return !(xPathFunction == null);
    } catch (Exception exception) {
      return false;
    } 
  }
  
  public boolean elementAvailable(String paramString1, String paramString2) throws TransformerException { return false; }
  
  public Object extFunction(String paramString1, String paramString2, Vector paramVector, Object paramObject) throws TransformerException {
    try {
      if (paramString2 == null) {
        String str = XSLMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "Function Name" });
        throw new NullPointerException(str);
      } 
      QName qName = new QName(paramString1, paramString2);
      if (this.extensionInvocationDisabled) {
        String str = XSLMessages.createXPATHMessage("ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED", new Object[] { qName.toString() });
        throw new XPathFunctionException(str);
      } 
      int i = paramVector.size();
      XPathFunction xPathFunction = this.resolver.resolveFunction(qName, i);
      ArrayList arrayList = new ArrayList(i);
      for (byte b = 0; b < i; b++) {
        Object object = paramVector.elementAt(b);
        if (object instanceof XNodeSet) {
          arrayList.add(b, ((XNodeSet)object).nodelist());
        } else if (object instanceof XObject) {
          Object object1 = ((XObject)object).object();
          arrayList.add(b, object1);
        } else {
          arrayList.add(b, object);
        } 
      } 
      return xPathFunction.evaluate(arrayList);
    } catch (XPathFunctionException xPathFunctionException) {
      throw new WrappedRuntimeException(xPathFunctionException);
    } catch (Exception exception) {
      throw new TransformerException(exception);
    } 
  }
  
  public Object extFunction(FuncExtFunction paramFuncExtFunction, Vector paramVector) throws TransformerException {
    try {
      String str1 = paramFuncExtFunction.getNamespace();
      String str2 = paramFuncExtFunction.getFunctionName();
      int i = paramFuncExtFunction.getArgCount();
      QName qName = new QName(str1, str2);
      if (this.extensionInvocationDisabled) {
        String str = XSLMessages.createXPATHMessage("ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED", new Object[] { qName.toString() });
        throw new XPathFunctionException(str);
      } 
      XPathFunction xPathFunction = this.resolver.resolveFunction(qName, i);
      ArrayList arrayList = new ArrayList(i);
      for (byte b = 0; b < i; b++) {
        Object object = paramVector.elementAt(b);
        if (object instanceof XNodeSet) {
          arrayList.add(b, ((XNodeSet)object).nodelist());
        } else if (object instanceof XObject) {
          Object object1 = ((XObject)object).object();
          arrayList.add(b, object1);
        } else {
          arrayList.add(b, object);
        } 
      } 
      return xPathFunction.evaluate(arrayList);
    } catch (XPathFunctionException xPathFunctionException) {
      throw new WrappedRuntimeException(xPathFunctionException);
    } catch (Exception exception) {
      throw new TransformerException(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\jaxp\JAXPExtensionsProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */