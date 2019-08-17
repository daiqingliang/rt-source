package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResolverXPointer extends ResourceResolverSpi {
  private static Logger log = Logger.getLogger(ResolverXPointer.class.getName());
  
  private static final String XP = "#xpointer(id(";
  
  private static final int XP_LENGTH = "#xpointer(id(".length();
  
  public boolean engineIsThreadSafe() { return true; }
  
  public XMLSignatureInput engineResolveURI(ResourceResolverContext paramResourceResolverContext) throws ResourceResolverException {
    Element element = null;
    Document document = paramResourceResolverContext.attr.getOwnerElement().getOwnerDocument();
    if (isXPointerSlash(paramResourceResolverContext.uriToResolve)) {
      element = document;
    } else if (isXPointerId(paramResourceResolverContext.uriToResolve)) {
      String str = getXPointerId(paramResourceResolverContext.uriToResolve);
      element = document.getElementById(str);
      if (paramResourceResolverContext.secureValidation) {
        Element element1 = paramResourceResolverContext.attr.getOwnerDocument().getDocumentElement();
        if (!XMLUtils.protectAgainstWrappingAttack(element1, str)) {
          Object[] arrayOfObject = { str };
          throw new ResourceResolverException("signature.Verification.MultipleIDs", arrayOfObject, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
        } 
      } 
      if (element == null) {
        Object[] arrayOfObject = { str };
        throw new ResourceResolverException("signature.Verification.MissingID", arrayOfObject, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
      } 
    } 
    XMLSignatureInput xMLSignatureInput = new XMLSignatureInput(element);
    xMLSignatureInput.setMIMEType("text/xml");
    if (paramResourceResolverContext.baseUri != null && paramResourceResolverContext.baseUri.length() > 0) {
      xMLSignatureInput.setSourceURI(paramResourceResolverContext.baseUri.concat(paramResourceResolverContext.uriToResolve));
    } else {
      xMLSignatureInput.setSourceURI(paramResourceResolverContext.uriToResolve);
    } 
    return xMLSignatureInput;
  }
  
  public boolean engineCanResolveURI(ResourceResolverContext paramResourceResolverContext) { return (paramResourceResolverContext.uriToResolve == null) ? false : ((isXPointerSlash(paramResourceResolverContext.uriToResolve) || isXPointerId(paramResourceResolverContext.uriToResolve))); }
  
  private static boolean isXPointerSlash(String paramString) { return paramString.equals("#xpointer(/)"); }
  
  private static boolean isXPointerId(String paramString) {
    if (paramString.startsWith("#xpointer(id(") && paramString.endsWith("))")) {
      String str = paramString.substring(XP_LENGTH, paramString.length() - 2);
      int i = str.length() - 1;
      if ((str.charAt(0) == '"' && str.charAt(i) == '"') || (str.charAt(0) == '\'' && str.charAt(i) == '\'')) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "Id = " + str.substring(1, i)); 
        return true;
      } 
    } 
    return false;
  }
  
  private static String getXPointerId(String paramString) {
    if (paramString.startsWith("#xpointer(id(") && paramString.endsWith("))")) {
      String str = paramString.substring(XP_LENGTH, paramString.length() - 2);
      int i = str.length() - 1;
      if ((str.charAt(0) == '"' && str.charAt(i) == '"') || (str.charAt(0) == '\'' && str.charAt(i) == '\''))
        return str.substring(1, i); 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\resolver\implementations\ResolverXPointer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */