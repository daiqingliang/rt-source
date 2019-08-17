package com.sun.xml.internal.ws.policy.sourcemodel.wspolicy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public static enum NamespaceVersion {
  v1_2("http://schemas.xmlsoap.org/ws/2004/09/policy", "wsp1_2", new XmlToken[] { 
      XmlToken.Policy, XmlToken.ExactlyOne, XmlToken.All, XmlToken.PolicyReference, XmlToken.UsingPolicy, XmlToken.Name, XmlToken.Optional, XmlToken.Ignorable, XmlToken.PolicyUris, XmlToken.Uri, 
      XmlToken.Digest, XmlToken.DigestAlgorithm }),
  v1_5("http://www.w3.org/ns/ws-policy", "wsp", new XmlToken[] { 
      XmlToken.Policy, XmlToken.ExactlyOne, XmlToken.All, XmlToken.PolicyReference, XmlToken.UsingPolicy, XmlToken.Name, XmlToken.Optional, XmlToken.Ignorable, XmlToken.PolicyUris, XmlToken.Uri, 
      XmlToken.Digest, XmlToken.DigestAlgorithm });
  
  private final String nsUri;
  
  private final String defaultNsPrefix;
  
  private final Map<XmlToken, QName> tokenToQNameCache;
  
  public static NamespaceVersion resolveVersion(String paramString) {
    for (NamespaceVersion namespaceVersion : values()) {
      if (namespaceVersion.toString().equalsIgnoreCase(paramString))
        return namespaceVersion; 
    } 
    return null;
  }
  
  public static NamespaceVersion resolveVersion(QName paramQName) { return resolveVersion(paramQName.getNamespaceURI()); }
  
  public static NamespaceVersion getLatestVersion() { return v1_5; }
  
  public static XmlToken resolveAsToken(QName paramQName) {
    NamespaceVersion namespaceVersion = resolveVersion(paramQName);
    if (namespaceVersion != null) {
      XmlToken xmlToken = XmlToken.resolveToken(paramQName.getLocalPart());
      if (namespaceVersion.tokenToQNameCache.containsKey(xmlToken))
        return xmlToken; 
    } 
    return XmlToken.UNKNOWN;
  }
  
  NamespaceVersion(String paramString1, XmlToken[] paramArrayOfXmlToken1, XmlToken... paramVarArgs1) {
    this.nsUri = paramString1;
    this.defaultNsPrefix = paramArrayOfXmlToken1;
    HashMap hashMap = new HashMap();
    for (XmlToken xmlToken : paramVarArgs1)
      hashMap.put(xmlToken, new QName(this.nsUri, xmlToken.toString())); 
    this.tokenToQNameCache = Collections.unmodifiableMap(hashMap);
  }
  
  public String getDefaultNamespacePrefix() { return this.defaultNsPrefix; }
  
  public QName asQName(XmlToken paramXmlToken) throws IllegalArgumentException { return (QName)this.tokenToQNameCache.get(paramXmlToken); }
  
  public String toString() { return this.nsUri; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\wspolicy\NamespaceVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */