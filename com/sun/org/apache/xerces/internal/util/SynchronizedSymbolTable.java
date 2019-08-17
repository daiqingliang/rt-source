package com.sun.org.apache.xerces.internal.util;

public final class SynchronizedSymbolTable extends SymbolTable {
  protected SymbolTable fSymbolTable;
  
  public SynchronizedSymbolTable(SymbolTable paramSymbolTable) { this.fSymbolTable = paramSymbolTable; }
  
  public SynchronizedSymbolTable() { this.fSymbolTable = new SymbolTable(); }
  
  public SynchronizedSymbolTable(int paramInt) { this.fSymbolTable = new SymbolTable(paramInt); }
  
  public String addSymbol(String paramString) {
    synchronized (this.fSymbolTable) {
      return this.fSymbolTable.addSymbol(paramString);
    } 
  }
  
  public String addSymbol(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    synchronized (this.fSymbolTable) {
      return this.fSymbolTable.addSymbol(paramArrayOfChar, paramInt1, paramInt2);
    } 
  }
  
  public boolean containsSymbol(String paramString) {
    synchronized (this.fSymbolTable) {
      return this.fSymbolTable.containsSymbol(paramString);
    } 
  }
  
  public boolean containsSymbol(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    synchronized (this.fSymbolTable) {
      return this.fSymbolTable.containsSymbol(paramArrayOfChar, paramInt1, paramInt2);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\SynchronizedSymbolTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */