package com.sun.org.apache.xerces.internal.util;

public final class ShadowedSymbolTable extends SymbolTable {
  protected SymbolTable fSymbolTable;
  
  public ShadowedSymbolTable(SymbolTable paramSymbolTable) { this.fSymbolTable = paramSymbolTable; }
  
  public String addSymbol(String paramString) { return this.fSymbolTable.containsSymbol(paramString) ? this.fSymbolTable.addSymbol(paramString) : super.addSymbol(paramString); }
  
  public String addSymbol(char[] paramArrayOfChar, int paramInt1, int paramInt2) { return this.fSymbolTable.containsSymbol(paramArrayOfChar, paramInt1, paramInt2) ? this.fSymbolTable.addSymbol(paramArrayOfChar, paramInt1, paramInt2) : super.addSymbol(paramArrayOfChar, paramInt1, paramInt2); }
  
  public int hash(String paramString) { return this.fSymbolTable.hash(paramString); }
  
  public int hash(char[] paramArrayOfChar, int paramInt1, int paramInt2) { return this.fSymbolTable.hash(paramArrayOfChar, paramInt1, paramInt2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\ShadowedSymbolTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */