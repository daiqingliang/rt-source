package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.transforms.implementations.FuncHere;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.FunctionTable;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XalanXPathAPI implements XPathAPI {
  private static Logger log = Logger.getLogger(XalanXPathAPI.class.getName());
  
  private String xpathStr = null;
  
  private XPath xpath = null;
  
  private static FunctionTable funcTable = null;
  
  private static boolean installed;
  
  private XPathContext context;
  
  public NodeList selectNodeList(Node paramNode1, Node paramNode2, String paramString, Node paramNode3) throws TransformerException {
    XObject xObject = eval(paramNode1, paramNode2, paramString, paramNode3);
    return xObject.nodelist();
  }
  
  public boolean evaluate(Node paramNode1, Node paramNode2, String paramString, Node paramNode3) throws TransformerException {
    XObject xObject = eval(paramNode1, paramNode2, paramString, paramNode3);
    return xObject.bool();
  }
  
  public void clear() {
    this.xpathStr = null;
    this.xpath = null;
    this.context = null;
  }
  
  public static boolean isInstalled() { return installed; }
  
  private XObject eval(Node paramNode1, Node paramNode2, String paramString, Node paramNode3) throws TransformerException {
    if (this.context == null) {
      this.context = new XPathContext(paramNode2);
      this.context.setSecureProcessing(true);
    } 
    Element element = (paramNode3.getNodeType() == 9) ? ((Document)paramNode3).getDocumentElement() : paramNode3;
    PrefixResolverDefault prefixResolverDefault = new PrefixResolverDefault(element);
    if (!paramString.equals(this.xpathStr)) {
      if (paramString.indexOf("here()") > 0)
        this.context.reset(); 
      this.xpath = createXPath(paramString, prefixResolverDefault);
      this.xpathStr = paramString;
    } 
    int i = this.context.getDTMHandleFromNode(paramNode1);
    return this.xpath.execute(this.context, i, prefixResolverDefault);
  }
  
  private XPath createXPath(String paramString, PrefixResolver paramPrefixResolver) throws TransformerException {
    XPath xPath = null;
    Class[] arrayOfClass = { String.class, javax.xml.transform.SourceLocator.class, PrefixResolver.class, int.class, javax.xml.transform.ErrorListener.class, FunctionTable.class };
    Object[] arrayOfObject = { paramString, null, paramPrefixResolver, Integer.valueOf(0), null, funcTable };
    try {
      Constructor constructor = XPath.class.getConstructor(arrayOfClass);
      xPath = (XPath)constructor.newInstance(arrayOfObject);
    } catch (Exception exception) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, exception.getMessage(), exception); 
    } 
    if (xPath == null)
      xPath = new XPath(paramString, null, paramPrefixResolver, 0, null); 
    return xPath;
  }
  
  private static void fixupFunctionTable() {
    installed = false;
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Registering Here function"); 
    try {
      Class[] arrayOfClass = { String.class, com.sun.org.apache.xpath.internal.Expression.class };
      Method method = FunctionTable.class.getMethod("installFunction", arrayOfClass);
      if ((method.getModifiers() & 0x8) != 0) {
        Object[] arrayOfObject = { "here", new FuncHere() };
        method.invoke(null, arrayOfObject);
        installed = true;
      } 
    } catch (Exception exception) {
      log.log(Level.FINE, "Error installing function using the static installFunction method", exception);
    } 
    if (!installed)
      try {
        funcTable = new FunctionTable();
        Class[] arrayOfClass = { String.class, Class.class };
        Method method = FunctionTable.class.getMethod("installFunction", arrayOfClass);
        Object[] arrayOfObject = { "here", FuncHere.class };
        method.invoke(funcTable, arrayOfObject);
        installed = true;
      } catch (Exception exception) {
        log.log(Level.FINE, "Error installing function using the static installFunction method", exception);
      }  
    if (log.isLoggable(Level.FINE))
      if (installed) {
        log.log(Level.FINE, "Registered class " + FuncHere.class.getName() + " for XPath function 'here()' function in internal table");
      } else {
        log.log(Level.FINE, "Unable to register class " + FuncHere.class.getName() + " for XPath function 'here()' function in internal table");
      }  
  }
  
  static  {
    fixupFunctionTable();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\XalanXPathAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */