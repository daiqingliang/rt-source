package com.sun.org.apache.xml.internal.serializer;

import java.util.HashMap;
import java.util.Stack;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class NamespaceMappings {
  private int count;
  
  private HashMap m_namespaces = new HashMap();
  
  private Stack m_nodeStack = new Stack();
  
  private static final String EMPTYSTRING = "";
  
  private static final String XML_PREFIX = "xml";
  
  public NamespaceMappings() { initNamespaces(); }
  
  private void initNamespaces() {
    Stack stack;
    this.m_namespaces.put("", stack = new Stack());
    stack.push(new MappingRecord("", "", 0));
    this.m_namespaces.put("xml", stack = new Stack());
    stack.push(new MappingRecord("xml", "http://www.w3.org/XML/1998/namespace", 0));
    this.m_nodeStack.push(new MappingRecord(null, null, -1));
  }
  
  public String lookupNamespace(String paramString) {
    Stack stack = (Stack)this.m_namespaces.get(paramString);
    return (stack != null && !stack.isEmpty()) ? ((MappingRecord)stack.peek()).m_uri : null;
  }
  
  MappingRecord getMappingFromPrefix(String paramString) {
    Stack stack = (Stack)this.m_namespaces.get(paramString);
    return (stack != null && !stack.isEmpty()) ? (MappingRecord)stack.peek() : null;
  }
  
  public String lookupPrefix(String paramString) {
    String str = null;
    for (String str1 : this.m_namespaces.keySet()) {
      String str2 = lookupNamespace(str1);
      if (str2 != null && str2.equals(paramString)) {
        str = str1;
        break;
      } 
    } 
    return str;
  }
  
  MappingRecord getMappingFromURI(String paramString) {
    MappingRecord mappingRecord = null;
    for (String str : this.m_namespaces.keySet()) {
      MappingRecord mappingRecord1 = getMappingFromPrefix(str);
      if (mappingRecord1 != null && mappingRecord1.m_uri.equals(paramString)) {
        mappingRecord = mappingRecord1;
        break;
      } 
    } 
    return mappingRecord;
  }
  
  boolean popNamespace(String paramString) {
    if (paramString.startsWith("xml"))
      return false; 
    Stack stack;
    if ((stack = (Stack)this.m_namespaces.get(paramString)) != null) {
      stack.pop();
      return true;
    } 
    return false;
  }
  
  boolean pushNamespace(String paramString1, String paramString2, int paramInt) {
    if (paramString1.startsWith("xml"))
      return false; 
    Stack stack;
    if ((stack = (Stack)this.m_namespaces.get(paramString1)) == null)
      this.m_namespaces.put(paramString1, stack = new Stack()); 
    if (!stack.empty() && paramString2.equals(((MappingRecord)stack.peek()).m_uri))
      return false; 
    MappingRecord mappingRecord = new MappingRecord(paramString1, paramString2, paramInt);
    stack.push(mappingRecord);
    this.m_nodeStack.push(mappingRecord);
    return true;
  }
  
  void popNamespaces(int paramInt, ContentHandler paramContentHandler) {
    while (true) {
      if (this.m_nodeStack.isEmpty())
        return; 
      MappingRecord mappingRecord = (MappingRecord)this.m_nodeStack.peek();
      int i = mappingRecord.m_declarationDepth;
      if (i < paramInt)
        return; 
      mappingRecord = (MappingRecord)this.m_nodeStack.pop();
      String str = mappingRecord.m_prefix;
      popNamespace(str);
      if (paramContentHandler != null)
        try {
          paramContentHandler.endPrefixMapping(str);
        } catch (SAXException sAXException) {} 
    } 
  }
  
  public String generateNextPrefix() { return "ns" + this.count++; }
  
  public Object clone() throws CloneNotSupportedException {
    NamespaceMappings namespaceMappings = new NamespaceMappings();
    namespaceMappings.m_nodeStack = (Stack)this.m_nodeStack.clone();
    namespaceMappings.m_namespaces = (HashMap)this.m_namespaces.clone();
    namespaceMappings.count = this.count;
    return namespaceMappings;
  }
  
  final void reset() {
    this.count = 0;
    this.m_namespaces.clear();
    this.m_nodeStack.clear();
    initNamespaces();
  }
  
  class MappingRecord {
    final String m_prefix;
    
    final String m_uri;
    
    final int m_declarationDepth;
    
    MappingRecord(String param1String1, String param1String2, int param1Int) {
      this.m_prefix = param1String1;
      this.m_uri = param1String2;
      this.m_declarationDepth = param1Int;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\NamespaceMappings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */