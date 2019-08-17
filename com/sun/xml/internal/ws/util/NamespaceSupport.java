package com.sun.xml.internal.ws.util;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;

public final class NamespaceSupport {
  public static final String XMLNS = "http://www.w3.org/XML/1998/namespace";
  
  private static final Iterable<String> EMPTY_ENUMERATION = new ArrayList();
  
  private Context[] contexts;
  
  private Context currentContext;
  
  private int contextPos;
  
  public NamespaceSupport() { reset(); }
  
  public NamespaceSupport(NamespaceSupport paramNamespaceSupport) {
    this.contexts = new Context[paramNamespaceSupport.contexts.length];
    this.currentContext = null;
    this.contextPos = paramNamespaceSupport.contextPos;
    Context context = null;
    for (byte b = 0; b < paramNamespaceSupport.contexts.length; b++) {
      Context context1 = paramNamespaceSupport.contexts[b];
      if (context1 == null) {
        this.contexts[b] = null;
      } else {
        Context context2 = new Context(context1, context);
        this.contexts[b] = context2;
        if (paramNamespaceSupport.currentContext == context1)
          this.currentContext = context2; 
        context = context2;
      } 
    } 
  }
  
  public void reset() {
    this.contexts = new Context[32];
    this.contextPos = 0;
    this.contexts[this.contextPos] = this.currentContext = new Context();
    this.currentContext.declarePrefix("xml", "http://www.w3.org/XML/1998/namespace");
  }
  
  public void pushContext() {
    int i = this.contexts.length;
    this.contextPos++;
    if (this.contextPos >= i) {
      Context[] arrayOfContext = new Context[i * 2];
      System.arraycopy(this.contexts, 0, arrayOfContext, 0, i);
      this.contexts = arrayOfContext;
    } 
    this.currentContext = this.contexts[this.contextPos];
    if (this.currentContext == null)
      this.contexts[this.contextPos] = this.currentContext = new Context(); 
    if (this.contextPos > 0)
      this.currentContext.setParent(this.contexts[this.contextPos - 1]); 
  }
  
  public void popContext() {
    this.contextPos--;
    if (this.contextPos < 0)
      throw new EmptyStackException(); 
    this.currentContext = this.contexts[this.contextPos];
  }
  
  public void slideContextUp() {
    this.contextPos--;
    this.currentContext = this.contexts[this.contextPos];
  }
  
  public void slideContextDown() {
    this.contextPos++;
    if (this.contexts[this.contextPos] == null)
      this.contexts[this.contextPos] = this.contexts[this.contextPos - 1]; 
    this.currentContext = this.contexts[this.contextPos];
  }
  
  public boolean declarePrefix(String paramString1, String paramString2) {
    if ((paramString1.equals("xml") && !paramString2.equals("http://www.w3.org/XML/1998/namespace")) || paramString1.equals("xmlns"))
      return false; 
    this.currentContext.declarePrefix(paramString1, paramString2);
    return true;
  }
  
  public String[] processName(String paramString, String[] paramArrayOfString, boolean paramBoolean) {
    String[] arrayOfString = this.currentContext.processName(paramString, paramBoolean);
    if (arrayOfString == null)
      return null; 
    paramArrayOfString[0] = arrayOfString[0];
    paramArrayOfString[1] = arrayOfString[1];
    paramArrayOfString[2] = arrayOfString[2];
    return paramArrayOfString;
  }
  
  public String getURI(String paramString) { return this.currentContext.getURI(paramString); }
  
  public Iterable<String> getPrefixes() { return this.currentContext.getPrefixes(); }
  
  public String getPrefix(String paramString) { return this.currentContext.getPrefix(paramString); }
  
  public Iterator getPrefixes(String paramString) {
    ArrayList arrayList = new ArrayList();
    for (String str : getPrefixes()) {
      if (paramString.equals(getURI(str)))
        arrayList.add(str); 
    } 
    return arrayList.iterator();
  }
  
  public Iterable<String> getDeclaredPrefixes() { return this.currentContext.getDeclaredPrefixes(); }
  
  static final class Context {
    HashMap prefixTable;
    
    HashMap uriTable;
    
    HashMap elementNameTable;
    
    HashMap attributeNameTable;
    
    String defaultNS = null;
    
    private ArrayList declarations = null;
    
    private boolean tablesDirty = false;
    
    private Context parent = null;
    
    Context() { copyTables(); }
    
    Context(Context param1Context1, Context param1Context2) {
      if (param1Context1 == null) {
        copyTables();
        return;
      } 
      if (param1Context2 != null && !param1Context1.tablesDirty) {
        this.prefixTable = (param1Context1.prefixTable == param1Context1.parent.prefixTable) ? param1Context2.prefixTable : (HashMap)param1Context1.prefixTable.clone();
        this.uriTable = (param1Context1.uriTable == param1Context1.parent.uriTable) ? param1Context2.uriTable : (HashMap)param1Context1.uriTable.clone();
        this.elementNameTable = (param1Context1.elementNameTable == param1Context1.parent.elementNameTable) ? param1Context2.elementNameTable : (HashMap)param1Context1.elementNameTable.clone();
        this.attributeNameTable = (param1Context1.attributeNameTable == param1Context1.parent.attributeNameTable) ? param1Context2.attributeNameTable : (HashMap)param1Context1.attributeNameTable.clone();
        this.defaultNS = (param1Context1.defaultNS == param1Context1.parent.defaultNS) ? param1Context2.defaultNS : param1Context1.defaultNS;
      } else {
        this.prefixTable = (HashMap)param1Context1.prefixTable.clone();
        this.uriTable = (HashMap)param1Context1.uriTable.clone();
        this.elementNameTable = (HashMap)param1Context1.elementNameTable.clone();
        this.attributeNameTable = (HashMap)param1Context1.attributeNameTable.clone();
        this.defaultNS = param1Context1.defaultNS;
      } 
      this.tablesDirty = param1Context1.tablesDirty;
      this.parent = param1Context2;
      this.declarations = (param1Context1.declarations == null) ? null : (ArrayList)param1Context1.declarations.clone();
    }
    
    void setParent(Context param1Context) {
      this.parent = param1Context;
      this.declarations = null;
      this.prefixTable = param1Context.prefixTable;
      this.uriTable = param1Context.uriTable;
      this.elementNameTable = param1Context.elementNameTable;
      this.attributeNameTable = param1Context.attributeNameTable;
      this.defaultNS = param1Context.defaultNS;
      this.tablesDirty = false;
    }
    
    void declarePrefix(String param1String1, String param1String2) {
      if (!this.tablesDirty)
        copyTables(); 
      if (this.declarations == null)
        this.declarations = new ArrayList(); 
      param1String1 = param1String1.intern();
      param1String2 = param1String2.intern();
      if ("".equals(param1String1)) {
        if ("".equals(param1String2)) {
          this.defaultNS = null;
        } else {
          this.defaultNS = param1String2;
        } 
      } else {
        this.prefixTable.put(param1String1, param1String2);
        this.uriTable.put(param1String2, param1String1);
      } 
      this.declarations.add(param1String1);
    }
    
    String[] processName(String param1String, boolean param1Boolean) {
      HashMap hashMap;
      if (param1Boolean) {
        hashMap = this.elementNameTable;
      } else {
        hashMap = this.attributeNameTable;
      } 
      String[] arrayOfString = (String[])hashMap.get(param1String);
      if (arrayOfString != null)
        return arrayOfString; 
      arrayOfString = new String[3];
      int i = param1String.indexOf(':');
      if (i == -1) {
        if (param1Boolean || this.defaultNS == null) {
          arrayOfString[0] = "";
        } else {
          arrayOfString[0] = this.defaultNS;
        } 
        arrayOfString[1] = param1String.intern();
        arrayOfString[2] = arrayOfString[1];
      } else {
        String str3;
        String str1 = param1String.substring(0, i);
        String str2 = param1String.substring(i + 1);
        if ("".equals(str1)) {
          str3 = this.defaultNS;
        } else {
          str3 = (String)this.prefixTable.get(str1);
        } 
        if (str3 == null)
          return null; 
        arrayOfString[0] = str3;
        arrayOfString[1] = str2.intern();
        arrayOfString[2] = param1String.intern();
      } 
      hashMap.put(arrayOfString[2], arrayOfString);
      this.tablesDirty = true;
      return arrayOfString;
    }
    
    String getURI(String param1String) { return "".equals(param1String) ? this.defaultNS : ((this.prefixTable == null) ? null : (String)this.prefixTable.get(param1String)); }
    
    String getPrefix(String param1String) { return (this.uriTable == null) ? null : (String)this.uriTable.get(param1String); }
    
    Iterable<String> getDeclaredPrefixes() { return (this.declarations == null) ? EMPTY_ENUMERATION : this.declarations; }
    
    Iterable<String> getPrefixes() { return (this.prefixTable == null) ? EMPTY_ENUMERATION : this.prefixTable.keySet(); }
    
    private void copyTables() {
      if (this.prefixTable != null) {
        this.prefixTable = (HashMap)this.prefixTable.clone();
      } else {
        this.prefixTable = new HashMap();
      } 
      if (this.uriTable != null) {
        this.uriTable = (HashMap)this.uriTable.clone();
      } else {
        this.uriTable = new HashMap();
      } 
      this.elementNameTable = new HashMap();
      this.attributeNameTable = new HashMap();
      this.tablesDirty = true;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\NamespaceSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */