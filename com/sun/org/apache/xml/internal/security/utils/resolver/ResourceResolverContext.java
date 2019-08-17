package com.sun.org.apache.xml.internal.security.utils.resolver;

import org.w3c.dom.Attr;

public class ResourceResolverContext {
  public final String uriToResolve;
  
  public final boolean secureValidation;
  
  public final String baseUri;
  
  public final Attr attr;
  
  public ResourceResolverContext(Attr paramAttr, String paramString, boolean paramBoolean) {
    this.attr = paramAttr;
    this.baseUri = paramString;
    this.secureValidation = paramBoolean;
    this.uriToResolve = (paramAttr != null) ? paramAttr.getValue() : null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\resolver\ResourceResolverContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */