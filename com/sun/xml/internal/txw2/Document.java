package com.sun.xml.internal.txw2;

import com.sun.xml.internal.txw2.output.XmlSerializer;
import java.util.HashMap;
import java.util.Map;

public final class Document {
  private final XmlSerializer out;
  
  private boolean started = false;
  
  private Content current = null;
  
  private final Map<Class, DatatypeWriter> datatypeWriters = new HashMap();
  
  private int iota = 1;
  
  private final NamespaceSupport inscopeNamespace = new NamespaceSupport();
  
  private NamespaceDecl activeNamespaces;
  
  private final ContentVisitor visitor = new ContentVisitor() {
      public void onStartDocument() { throw new IllegalStateException(); }
      
      public void onEndDocument() { Document.this.out.endDocument(); }
      
      public void onEndTag() {
        Document.this.out.endTag();
        Document.this.inscopeNamespace.popContext();
        Document.this.activeNamespaces = null;
      }
      
      public void onPcdata(StringBuilder param1StringBuilder) {
        if (Document.this.activeNamespaces != null)
          param1StringBuilder = Document.this.fixPrefix(param1StringBuilder); 
        Document.this.out.text(param1StringBuilder);
      }
      
      public void onCdata(StringBuilder param1StringBuilder) {
        if (Document.this.activeNamespaces != null)
          param1StringBuilder = Document.this.fixPrefix(param1StringBuilder); 
        Document.this.out.cdata(param1StringBuilder);
      }
      
      public void onComment(StringBuilder param1StringBuilder) {
        if (Document.this.activeNamespaces != null)
          param1StringBuilder = Document.this.fixPrefix(param1StringBuilder); 
        Document.this.out.comment(param1StringBuilder);
      }
      
      public void onStartTag(String param1String1, String param1String2, Attribute param1Attribute, NamespaceDecl param1NamespaceDecl) {
        assert param1String1 != null;
        assert param1String2 != null;
        Document.this.activeNamespaces = param1NamespaceDecl;
        if (!Document.this.started) {
          Document.this.started = true;
          Document.this.out.startDocument();
        } 
        Document.this.inscopeNamespace.pushContext();
        NamespaceDecl namespaceDecl;
        for (namespaceDecl = param1NamespaceDecl; namespaceDecl != null; namespaceDecl = namespaceDecl.next) {
          namespaceDecl.declared = false;
          if (namespaceDecl.prefix != null) {
            String str = Document.this.inscopeNamespace.getURI(namespaceDecl.prefix);
            if (str == null || !str.equals(namespaceDecl.uri)) {
              Document.this.inscopeNamespace.declarePrefix(namespaceDecl.prefix, namespaceDecl.uri);
              namespaceDecl.declared = true;
            } 
          } 
        } 
        for (namespaceDecl = param1NamespaceDecl; namespaceDecl != null; namespaceDecl = namespaceDecl.next) {
          if (namespaceDecl.prefix == null)
            if (Document.this.inscopeNamespace.getURI("").equals(namespaceDecl.uri)) {
              namespaceDecl.prefix = "";
            } else {
              String str = Document.this.inscopeNamespace.getPrefix(namespaceDecl.uri);
              if (str == null) {
                while (Document.this.inscopeNamespace.getURI(str = Document.this.newPrefix()) != null);
                namespaceDecl.declared = true;
                Document.this.inscopeNamespace.declarePrefix(str, namespaceDecl.uri);
              } 
              namespaceDecl.prefix = str;
            }  
        } 
        assert param1NamespaceDecl.uri.equals(param1String1);
        assert param1NamespaceDecl.prefix != null : "a prefix must have been all allocated";
        Document.this.out.beginStartTag(param1String1, param1String2, param1NamespaceDecl.prefix);
        for (namespaceDecl = param1NamespaceDecl; namespaceDecl != null; namespaceDecl = namespaceDecl.next) {
          if (namespaceDecl.declared)
            Document.this.out.writeXmlns(namespaceDecl.prefix, namespaceDecl.uri); 
        } 
        for (Attribute attribute = param1Attribute; attribute != null; attribute = attribute.next) {
          String str;
          if (attribute.nsUri.length() == 0) {
            str = "";
          } else {
            str = Document.this.inscopeNamespace.getPrefix(attribute.nsUri);
          } 
          Document.this.out.writeAttribute(attribute.nsUri, attribute.localName, str, Document.this.fixPrefix(attribute.value));
        } 
        Document.this.out.endStartTag(param1String1, param1String2, param1NamespaceDecl.prefix);
      }
    };
  
  private final StringBuilder prefixSeed = new StringBuilder("ns");
  
  private int prefixIota = 0;
  
  static final char MAGIC = '\000';
  
  Document(XmlSerializer paramXmlSerializer) {
    this.out = paramXmlSerializer;
    for (DatatypeWriter datatypeWriter : DatatypeWriter.BUILTIN)
      this.datatypeWriters.put(datatypeWriter.getType(), datatypeWriter); 
  }
  
  void flush() { this.out.flush(); }
  
  void setFirstContent(Content paramContent) {
    assert this.current == null;
    this.current = new StartDocument();
    this.current.setNext(this, paramContent);
  }
  
  public void addDatatypeWriter(DatatypeWriter<?> paramDatatypeWriter) { this.datatypeWriters.put(paramDatatypeWriter.getType(), paramDatatypeWriter); }
  
  void run() {
    while (true) {
      Content content = this.current.getNext();
      if (content == null || !content.isReadyToCommit())
        return; 
      content.accept(this.visitor);
      content.written();
      this.current = content;
    } 
  }
  
  void writeValue(Object paramObject, NamespaceResolver paramNamespaceResolver, StringBuilder paramStringBuilder) {
    if (paramObject == null)
      throw new IllegalArgumentException("argument contains null"); 
    if (paramObject instanceof Object[]) {
      for (Object object : (Object[])paramObject)
        writeValue(object, paramNamespaceResolver, paramStringBuilder); 
      return;
    } 
    if (paramObject instanceof Iterable) {
      for (Object object : (Iterable)paramObject)
        writeValue(object, paramNamespaceResolver, paramStringBuilder); 
      return;
    } 
    if (paramStringBuilder.length() > 0)
      paramStringBuilder.append(' '); 
    for (Class clazz = paramObject.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
      DatatypeWriter datatypeWriter = (DatatypeWriter)this.datatypeWriters.get(clazz);
      if (datatypeWriter != null) {
        datatypeWriter.print(paramObject, paramNamespaceResolver, paramStringBuilder);
        return;
      } 
    } 
    paramStringBuilder.append(paramObject);
  }
  
  private String newPrefix() {
    this.prefixSeed.setLength(2);
    this.prefixSeed.append(++this.prefixIota);
    return this.prefixSeed.toString();
  }
  
  private StringBuilder fixPrefix(StringBuilder paramStringBuilder) {
    assert this.activeNamespaces != null;
    int i = paramStringBuilder.length();
    byte b;
    for (b = 0; b < i && paramStringBuilder.charAt(b) != '\000'; b++);
    if (b == i)
      return paramStringBuilder; 
    while (b < i) {
      char c = paramStringBuilder.charAt(b + 1);
      NamespaceDecl namespaceDecl;
      for (namespaceDecl = this.activeNamespaces; namespaceDecl != null && namespaceDecl.uniqueId != c; namespaceDecl = namespaceDecl.next);
      if (namespaceDecl == null)
        throw new IllegalStateException("Unexpected use of prefixes " + paramStringBuilder); 
      int j = 2;
      String str = namespaceDecl.prefix;
      if (str.length() == 0) {
        if (paramStringBuilder.length() <= b + 2 || paramStringBuilder.charAt(b + 2) != ':')
          throw new IllegalStateException("Unexpected use of prefixes " + paramStringBuilder); 
        j = 3;
      } 
      paramStringBuilder.replace(b, b + j, str);
      i += str.length() - j;
      while (b < i && paramStringBuilder.charAt(b) != '\000')
        b++; 
    } 
    return paramStringBuilder;
  }
  
  char assignNewId() { return (char)this.iota++; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\Document.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */