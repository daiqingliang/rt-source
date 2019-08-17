package com.sun.org.apache.xerces.internal.impl.validation;

import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.ArrayList;
import java.util.Locale;

public class ValidationState implements ValidationContext {
  private boolean fExtraChecking = true;
  
  private boolean fFacetChecking = true;
  
  private boolean fNormalize = true;
  
  private boolean fNamespaces = true;
  
  private EntityState fEntityState = null;
  
  private NamespaceContext fNamespaceContext = null;
  
  private SymbolTable fSymbolTable = null;
  
  private Locale fLocale = null;
  
  private ArrayList<String> fIdList;
  
  private ArrayList<String> fIdRefList;
  
  public void setExtraChecking(boolean paramBoolean) { this.fExtraChecking = paramBoolean; }
  
  public void setFacetChecking(boolean paramBoolean) { this.fFacetChecking = paramBoolean; }
  
  public void setNormalizationRequired(boolean paramBoolean) { this.fNormalize = paramBoolean; }
  
  public void setUsingNamespaces(boolean paramBoolean) { this.fNamespaces = paramBoolean; }
  
  public void setEntityState(EntityState paramEntityState) { this.fEntityState = paramEntityState; }
  
  public void setNamespaceSupport(NamespaceContext paramNamespaceContext) { this.fNamespaceContext = paramNamespaceContext; }
  
  public void setSymbolTable(SymbolTable paramSymbolTable) { this.fSymbolTable = paramSymbolTable; }
  
  public String checkIDRefID() {
    if (this.fIdList == null && this.fIdRefList != null)
      return (String)this.fIdRefList.get(0); 
    if (this.fIdRefList != null)
      for (byte b = 0; b < this.fIdRefList.size(); b++) {
        String str = (String)this.fIdRefList.get(b);
        if (!this.fIdList.contains(str))
          return str; 
      }  
    return null;
  }
  
  public void reset() {
    this.fExtraChecking = true;
    this.fFacetChecking = true;
    this.fNamespaces = true;
    this.fIdList = null;
    this.fIdRefList = null;
    this.fEntityState = null;
    this.fNamespaceContext = null;
    this.fSymbolTable = null;
  }
  
  public void resetIDTables() {
    this.fIdList = null;
    this.fIdRefList = null;
  }
  
  public boolean needExtraChecking() { return this.fExtraChecking; }
  
  public boolean needFacetChecking() { return this.fFacetChecking; }
  
  public boolean needToNormalize() { return this.fNormalize; }
  
  public boolean useNamespaces() { return this.fNamespaces; }
  
  public boolean isEntityDeclared(String paramString) { return (this.fEntityState != null) ? this.fEntityState.isEntityDeclared(getSymbol(paramString)) : 0; }
  
  public boolean isEntityUnparsed(String paramString) { return (this.fEntityState != null) ? this.fEntityState.isEntityUnparsed(getSymbol(paramString)) : 0; }
  
  public boolean isIdDeclared(String paramString) { return (this.fIdList == null) ? false : this.fIdList.contains(paramString); }
  
  public void addId(String paramString) {
    if (this.fIdList == null)
      this.fIdList = new ArrayList(); 
    this.fIdList.add(paramString);
  }
  
  public void addIdRef(String paramString) {
    if (this.fIdRefList == null)
      this.fIdRefList = new ArrayList(); 
    this.fIdRefList.add(paramString);
  }
  
  public String getSymbol(String paramString) { return (this.fSymbolTable != null) ? this.fSymbolTable.addSymbol(paramString) : paramString.intern(); }
  
  public String getURI(String paramString) { return (this.fNamespaceContext != null) ? this.fNamespaceContext.getURI(paramString) : null; }
  
  public void setLocale(Locale paramLocale) { this.fLocale = paramLocale; }
  
  public Locale getLocale() { return this.fLocale; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\validation\ValidationState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */