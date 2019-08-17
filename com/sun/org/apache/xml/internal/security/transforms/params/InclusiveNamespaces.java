package com.sun.org.apache.xml.internal.security.transforms.params;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.transforms.TransformParam;
import com.sun.org.apache.xml.internal.security.utils.ElementProxy;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class InclusiveNamespaces extends ElementProxy implements TransformParam {
  public static final String _TAG_EC_INCLUSIVENAMESPACES = "InclusiveNamespaces";
  
  public static final String _ATT_EC_PREFIXLIST = "PrefixList";
  
  public static final String ExclusiveCanonicalizationNamespace = "http://www.w3.org/2001/10/xml-exc-c14n#";
  
  public InclusiveNamespaces(Document paramDocument, String paramString) { this(paramDocument, prefixStr2Set(paramString)); }
  
  public InclusiveNamespaces(Document paramDocument, Set<String> paramSet) {
    super(paramDocument);
    SortedSet sortedSet = null;
    if (paramSet instanceof SortedSet) {
      sortedSet = (SortedSet)paramSet;
    } else {
      sortedSet = new TreeSet(paramSet);
    } 
    StringBuilder stringBuilder = new StringBuilder();
    for (String str : sortedSet) {
      if (str.equals("xmlns")) {
        stringBuilder.append("#default ");
        continue;
      } 
      stringBuilder.append(str + " ");
    } 
    this.constructionElement.setAttributeNS(null, "PrefixList", stringBuilder.toString().trim());
  }
  
  public InclusiveNamespaces(Element paramElement, String paramString) throws XMLSecurityException { super(paramElement, paramString); }
  
  public String getInclusiveNamespaces() { return this.constructionElement.getAttributeNS(null, "PrefixList"); }
  
  public static SortedSet<String> prefixStr2Set(String paramString) {
    TreeSet treeSet = new TreeSet();
    if (paramString == null || paramString.length() == 0)
      return treeSet; 
    String[] arrayOfString = paramString.split("\\s");
    for (String str : arrayOfString) {
      if (str.equals("#default")) {
        treeSet.add("xmlns");
      } else {
        treeSet.add(str);
      } 
    } 
    return treeSet;
  }
  
  public String getBaseNamespace() { return "http://www.w3.org/2001/10/xml-exc-c14n#"; }
  
  public String getBaseLocalName() { return "InclusiveNamespaces"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\params\InclusiveNamespaces.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */