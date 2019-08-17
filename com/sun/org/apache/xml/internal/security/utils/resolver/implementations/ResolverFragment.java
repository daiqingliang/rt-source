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

public class ResolverFragment extends ResourceResolverSpi {
  private static Logger log = Logger.getLogger(ResolverFragment.class.getName());
  
  public boolean engineIsThreadSafe() { return true; }
  
  public XMLSignatureInput engineResolveURI(ResourceResolverContext paramResourceResolverContext) throws ResourceResolverException {
    Document document = paramResourceResolverContext.attr.getOwnerElement().getOwnerDocument();
    Element element = null;
    if (paramResourceResolverContext.uriToResolve.equals("")) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "ResolverFragment with empty URI (means complete document)"); 
      element = document;
    } else {
      String str = paramResourceResolverContext.uriToResolve.substring(1);
      element = document.getElementById(str);
      if (element == null) {
        Object[] arrayOfObject = { str };
        throw new ResourceResolverException("signature.Verification.MissingID", arrayOfObject, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
      } 
      if (paramResourceResolverContext.secureValidation) {
        Element element1 = paramResourceResolverContext.attr.getOwnerDocument().getDocumentElement();
        if (!XMLUtils.protectAgainstWrappingAttack(element1, str)) {
          Object[] arrayOfObject = { str };
          throw new ResourceResolverException("signature.Verification.MultipleIDs", arrayOfObject, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
        } 
      } 
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Try to catch an Element with ID " + str + " and Element was " + element); 
    } 
    XMLSignatureInput xMLSignatureInput = new XMLSignatureInput(element);
    xMLSignatureInput.setExcludeComments(true);
    xMLSignatureInput.setMIMEType("text/xml");
    if (paramResourceResolverContext.baseUri != null && paramResourceResolverContext.baseUri.length() > 0) {
      xMLSignatureInput.setSourceURI(paramResourceResolverContext.baseUri.concat(paramResourceResolverContext.uriToResolve));
    } else {
      xMLSignatureInput.setSourceURI(paramResourceResolverContext.uriToResolve);
    } 
    return xMLSignatureInput;
  }
  
  public boolean engineCanResolveURI(ResourceResolverContext paramResourceResolverContext) {
    if (paramResourceResolverContext.uriToResolve == null) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Quick fail for null uri"); 
      return false;
    } 
    if (paramResourceResolverContext.uriToResolve.equals("") || (paramResourceResolverContext.uriToResolve.charAt(0) == '#' && !paramResourceResolverContext.uriToResolve.startsWith("#xpointer("))) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "State I can resolve reference: \"" + paramResourceResolverContext.uriToResolve + "\""); 
      return true;
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Do not seem to be able to resolve reference: \"" + paramResourceResolverContext.uriToResolve + "\""); 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\resolver\implementations\ResolverFragment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */