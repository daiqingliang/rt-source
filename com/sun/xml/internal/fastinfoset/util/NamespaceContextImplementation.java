package com.sun.xml.internal.fastinfoset.util;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

public final class NamespaceContextImplementation implements NamespaceContext {
  private static int DEFAULT_SIZE = 8;
  
  private String[] prefixes = new String[DEFAULT_SIZE];
  
  private String[] namespaceURIs = new String[DEFAULT_SIZE];
  
  private int namespacePosition;
  
  private int[] contexts = new int[DEFAULT_SIZE];
  
  private int contextPosition;
  
  private int currentContext;
  
  public NamespaceContextImplementation() {
    this.prefixes[0] = "xml";
    this.namespaceURIs[0] = "http://www.w3.org/XML/1998/namespace";
    this.prefixes[1] = "xmlns";
    this.namespaceURIs[1] = "http://www.w3.org/2000/xmlns/";
    this.currentContext = this.namespacePosition = 2;
  }
  
  public String getNamespaceURI(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException(); 
    for (int i = this.namespacePosition - 1; i >= 0; i--) {
      String str = this.prefixes[i];
      if (str.equals(paramString))
        return this.namespaceURIs[i]; 
    } 
    return "";
  }
  
  public String getPrefix(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException(); 
    for (int i = this.namespacePosition - 1; i >= 0; i--) {
      String str = this.namespaceURIs[i];
      if (str.equals(paramString)) {
        String str1 = this.prefixes[i];
        boolean bool = false;
        for (int j = i + 1; j < this.namespacePosition; j++) {
          if (str1.equals(this.prefixes[j])) {
            bool = true;
            break;
          } 
        } 
        if (!bool)
          return str1; 
      } 
    } 
    return null;
  }
  
  public String getNonDefaultPrefix(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException(); 
    for (int i = this.namespacePosition - 1; i >= 0; i--) {
      String str = this.namespaceURIs[i];
      if (str.equals(paramString) && this.prefixes[i].length() > 0) {
        String str1 = this.prefixes[i];
        while (++i < this.namespacePosition) {
          if (str1.equals(this.prefixes[i]))
            return null; 
          i++;
        } 
        return str1;
      } 
    } 
    return null;
  }
  
  public Iterator getPrefixes(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException(); 
    ArrayList arrayList = new ArrayList();
    for (int i = this.namespacePosition - 1; i >= 0; i--) {
      String str = this.namespaceURIs[i];
      if (str.equals(paramString)) {
        String str1 = this.prefixes[i];
        int j = i + 1;
        while (true) {
          if (j < this.namespacePosition) {
            if (str1.equals(this.prefixes[j]))
              break; 
            j++;
            continue;
          } 
          arrayList.add(str1);
          break;
        } 
      } 
    } 
    return arrayList.iterator();
  }
  
  public String getPrefix(int paramInt) { return this.prefixes[paramInt]; }
  
  public String getNamespaceURI(int paramInt) { return this.namespaceURIs[paramInt]; }
  
  public int getCurrentContextStartIndex() { return this.currentContext; }
  
  public int getCurrentContextEndIndex() { return this.namespacePosition; }
  
  public boolean isCurrentContextEmpty() { return (this.currentContext == this.namespacePosition); }
  
  public void declarePrefix(String paramString1, String paramString2) {
    paramString1 = paramString1.intern();
    paramString2 = paramString2.intern();
    if (paramString1 == "xml" || paramString1 == "xmlns")
      return; 
    for (int i = this.currentContext; i < this.namespacePosition; i++) {
      String str = this.prefixes[i];
      if (str == paramString1) {
        this.prefixes[i] = paramString1;
        this.namespaceURIs[i] = paramString2;
        return;
      } 
    } 
    if (this.namespacePosition == this.namespaceURIs.length)
      resizeNamespaces(); 
    this.prefixes[this.namespacePosition] = paramString1;
    this.namespaceURIs[this.namespacePosition++] = paramString2;
  }
  
  private void resizeNamespaces() {
    int i = this.namespaceURIs.length * 3 / 2 + 1;
    String[] arrayOfString1 = new String[i];
    System.arraycopy(this.prefixes, 0, arrayOfString1, 0, this.prefixes.length);
    this.prefixes = arrayOfString1;
    String[] arrayOfString2 = new String[i];
    System.arraycopy(this.namespaceURIs, 0, arrayOfString2, 0, this.namespaceURIs.length);
    this.namespaceURIs = arrayOfString2;
  }
  
  public void pushContext() {
    if (this.contextPosition == this.contexts.length)
      resizeContexts(); 
    this.contexts[this.contextPosition++] = this.currentContext = this.namespacePosition;
  }
  
  private void resizeContexts() {
    int[] arrayOfInt = new int[this.contexts.length * 3 / 2 + 1];
    System.arraycopy(this.contexts, 0, arrayOfInt, 0, this.contexts.length);
    this.contexts = arrayOfInt;
  }
  
  public void popContext() {
    if (this.contextPosition > 0)
      this.namespacePosition = this.currentContext = this.contexts[--this.contextPosition]; 
  }
  
  public void reset() { this.currentContext = this.namespacePosition = 2; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfose\\util\NamespaceContextImplementation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */