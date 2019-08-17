package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.NamespaceContext2;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class NamespaceContextImpl implements NamespaceContext2 {
  private final XMLSerializer owner;
  
  private String[] prefixes = new String[4];
  
  private String[] nsUris = new String[4];
  
  private int size;
  
  private Element current;
  
  private final Element top;
  
  private NamespacePrefixMapper prefixMapper = defaultNamespacePrefixMapper;
  
  public boolean collectionMode;
  
  private static final NamespacePrefixMapper defaultNamespacePrefixMapper = new NamespacePrefixMapper() {
      public String getPreferredPrefix(String param1String1, String param1String2, boolean param1Boolean) { return param1String1.equals("http://www.w3.org/2001/XMLSchema-instance") ? "xsi" : (param1String1.equals("http://www.w3.org/2001/XMLSchema") ? "xs" : (param1String1.equals("http://www.w3.org/2005/05/xmlmime") ? "xmime" : param1String2)); }
    };
  
  public NamespaceContextImpl(XMLSerializer paramXMLSerializer) {
    this.owner = paramXMLSerializer;
    this.current = this.top = new Element(this, null, null);
    put("http://www.w3.org/XML/1998/namespace", "xml");
  }
  
  public void setPrefixMapper(NamespacePrefixMapper paramNamespacePrefixMapper) {
    if (paramNamespacePrefixMapper == null)
      paramNamespacePrefixMapper = defaultNamespacePrefixMapper; 
    this.prefixMapper = paramNamespacePrefixMapper;
  }
  
  public NamespacePrefixMapper getPrefixMapper() { return this.prefixMapper; }
  
  public void reset() {
    this.current = this.top;
    this.size = 1;
    this.collectionMode = false;
  }
  
  public int declareNsUri(String paramString1, String paramString2, boolean paramBoolean) {
    paramString2 = this.prefixMapper.getPreferredPrefix(paramString1, paramString2, paramBoolean);
    if (paramString1.length() == 0) {
      for (int j = this.size - 1; j >= 0; j--) {
        if (this.nsUris[j].length() == 0)
          return j; 
        if (this.prefixes[j].length() == 0) {
          assert this.current.defaultPrefixIndex == -1 && this.current.oldDefaultNamespaceUriIndex == -1;
          String str = this.nsUris[j];
          String[] arrayOfString = this.owner.nameList.namespaceURIs;
          if (this.current.baseIndex <= j) {
            this.nsUris[j] = "";
            int m = put(str, null);
            for (int n = arrayOfString.length - 1; n >= 0; n--) {
              if (arrayOfString[n].equals(str)) {
                this.owner.knownUri2prefixIndexMap[n] = m;
                break;
              } 
            } 
            if (this.current.elementLocalName != null)
              this.current.setTagName(m, this.current.elementLocalName, this.current.getOuterPeer()); 
            return j;
          } 
          for (int k = arrayOfString.length - 1; k >= 0; k--) {
            if (arrayOfString[k].equals(str)) {
              this.current.defaultPrefixIndex = j;
              this.current.oldDefaultNamespaceUriIndex = k;
              this.owner.knownUri2prefixIndexMap[k] = this.size;
              break;
            } 
          } 
          if (this.current.elementLocalName != null)
            this.current.setTagName(this.size, this.current.elementLocalName, this.current.getOuterPeer()); 
          put(this.nsUris[j], null);
          return put("", "");
        } 
      } 
      return put("", "");
    } 
    for (int i = this.size - 1; i >= 0; i--) {
      String str = this.prefixes[i];
      if (this.nsUris[i].equals(paramString1) && (!paramBoolean || str.length() > 0))
        return i; 
      if (str.equals(paramString2))
        paramString2 = null; 
    } 
    if (paramString2 == null && paramBoolean)
      paramString2 = makeUniquePrefix(); 
    return put(paramString1, paramString2);
  }
  
  public int force(@NotNull String paramString1, @NotNull String paramString2) {
    for (int i = this.size - 1; i >= 0; i--) {
      if (this.prefixes[i].equals(paramString2)) {
        if (this.nsUris[i].equals(paramString1))
          return i; 
        break;
      } 
    } 
    return put(paramString1, paramString2);
  }
  
  public int put(@NotNull String paramString1, @Nullable String paramString2) {
    if (this.size == this.nsUris.length) {
      String[] arrayOfString1 = new String[this.nsUris.length * 2];
      String[] arrayOfString2 = new String[this.prefixes.length * 2];
      System.arraycopy(this.nsUris, 0, arrayOfString1, 0, this.nsUris.length);
      System.arraycopy(this.prefixes, 0, arrayOfString2, 0, this.prefixes.length);
      this.nsUris = arrayOfString1;
      this.prefixes = arrayOfString2;
    } 
    if (paramString2 == null)
      if (this.size == 1) {
        paramString2 = "";
      } else {
        paramString2 = makeUniquePrefix();
      }  
    this.nsUris[this.size] = paramString1;
    this.prefixes[this.size] = paramString2;
    return this.size++;
  }
  
  private String makeUniquePrefix() {
    String str;
    for (str = 5.toString(); getNamespaceURI(str) != null; str = str + '_');
    return str;
  }
  
  public Element getCurrent() { return this.current; }
  
  public int getPrefixIndex(String paramString) {
    for (int i = this.size - 1; i >= 0; i--) {
      if (this.nsUris[i].equals(paramString))
        return i; 
    } 
    throw new IllegalStateException();
  }
  
  public String getPrefix(int paramInt) { return this.prefixes[paramInt]; }
  
  public String getNamespaceURI(int paramInt) { return this.nsUris[paramInt]; }
  
  public String getNamespaceURI(String paramString) {
    for (int i = this.size - 1; i >= 0; i--) {
      if (this.prefixes[i].equals(paramString))
        return this.nsUris[i]; 
    } 
    return null;
  }
  
  public String getPrefix(String paramString) {
    if (this.collectionMode)
      return declareNamespace(paramString, null, false); 
    for (int i = this.size - 1; i >= 0; i--) {
      if (this.nsUris[i].equals(paramString))
        return this.prefixes[i]; 
    } 
    return null;
  }
  
  public Iterator<String> getPrefixes(String paramString) {
    String str = getPrefix(paramString);
    return (str == null) ? Collections.emptySet().iterator() : Collections.singleton(paramString).iterator();
  }
  
  public String declareNamespace(String paramString1, String paramString2, boolean paramBoolean) {
    int i = declareNsUri(paramString1, paramString2, paramBoolean);
    return getPrefix(i);
  }
  
  public int count() { return this.size; }
  
  public final class Element {
    public final NamespaceContextImpl context;
    
    private final Element prev;
    
    private Element next;
    
    private int oldDefaultNamespaceUriIndex;
    
    private int defaultPrefixIndex;
    
    private int baseIndex;
    
    private final int depth;
    
    private int elementNamePrefix;
    
    private String elementLocalName;
    
    private Name elementName;
    
    private Object outerPeer;
    
    private Object innerPeer;
    
    private Element(NamespaceContextImpl param1NamespaceContextImpl1, Element param1Element) {
      this.context = param1NamespaceContextImpl1;
      this.prev = param1Element;
      this.depth = (param1Element == null) ? 0 : (param1Element.depth + 1);
    }
    
    public boolean isRootElement() { return (this.depth == 1); }
    
    public Element push() {
      if (this.next == null)
        this.next = new Element(NamespaceContextImpl.this, this.context, this); 
      this.next.onPushed();
      return this.next;
    }
    
    public Element pop() {
      if (this.oldDefaultNamespaceUriIndex >= 0)
        this.context.owner.knownUri2prefixIndexMap[this.oldDefaultNamespaceUriIndex] = this.defaultPrefixIndex; 
      this.context.size = this.baseIndex;
      this.context.current = this.prev;
      this.outerPeer = this.innerPeer = null;
      return this.prev;
    }
    
    private void onPushed() {
      this.oldDefaultNamespaceUriIndex = this.defaultPrefixIndex = -1;
      this.baseIndex = this.context.size;
      this.context.current = this;
    }
    
    public void setTagName(int param1Int, String param1String, Object param1Object) {
      assert param1String != null;
      this.elementNamePrefix = param1Int;
      this.elementLocalName = param1String;
      this.elementName = null;
      this.outerPeer = param1Object;
    }
    
    public void setTagName(Name param1Name, Object param1Object) {
      assert param1Name != null;
      this.elementName = param1Name;
      this.outerPeer = param1Object;
    }
    
    public void startElement(XmlOutput param1XmlOutput, Object param1Object) throws IOException, XMLStreamException {
      this.innerPeer = param1Object;
      if (this.elementName != null) {
        param1XmlOutput.beginStartTag(this.elementName);
      } else {
        param1XmlOutput.beginStartTag(this.elementNamePrefix, this.elementLocalName);
      } 
    }
    
    public void endElement(XmlOutput param1XmlOutput) throws IOException, SAXException, XMLStreamException {
      if (this.elementName != null) {
        param1XmlOutput.endTag(this.elementName);
        this.elementName = null;
      } else {
        param1XmlOutput.endTag(this.elementNamePrefix, this.elementLocalName);
      } 
    }
    
    public final int count() { return this.context.size - this.baseIndex; }
    
    public final String getPrefix(int param1Int) { return this.context.prefixes[this.baseIndex + param1Int]; }
    
    public final String getNsUri(int param1Int) { return this.context.nsUris[this.baseIndex + param1Int]; }
    
    public int getBase() { return this.baseIndex; }
    
    public Object getOuterPeer() { return this.outerPeer; }
    
    public Object getInnerPeer() { return this.innerPeer; }
    
    public Element getParent() { return this.prev; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\NamespaceContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */