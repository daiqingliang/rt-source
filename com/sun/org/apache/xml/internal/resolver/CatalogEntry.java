package com.sun.org.apache.xml.internal.resolver;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CatalogEntry {
  protected static AtomicInteger nextEntry = new AtomicInteger(0);
  
  protected static final Map<String, Integer> entryTypes = new ConcurrentHashMap();
  
  protected static Vector entryArgs = new Vector();
  
  protected int entryType = 0;
  
  protected Vector args = null;
  
  static int addEntryType(String paramString, int paramInt) {
    int i = nextEntry.getAndIncrement();
    entryTypes.put(paramString, Integer.valueOf(i));
    entryArgs.add(i, Integer.valueOf(paramInt));
    return i;
  }
  
  public static int getEntryType(String paramString) throws CatalogException {
    if (!entryTypes.containsKey(paramString))
      throw new CatalogException(3); 
    Integer integer = (Integer)entryTypes.get(paramString);
    if (integer == null)
      throw new CatalogException(3); 
    return integer.intValue();
  }
  
  public static int getEntryArgCount(String paramString) throws CatalogException { return getEntryArgCount(getEntryType(paramString)); }
  
  public static int getEntryArgCount(int paramInt) throws CatalogException {
    try {
      Integer integer = (Integer)entryArgs.get(paramInt);
      return integer.intValue();
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new CatalogException(3);
    } 
  }
  
  public CatalogEntry() {}
  
  public CatalogEntry(String paramString, Vector paramVector) throws CatalogException {
    Integer integer = (Integer)entryTypes.get(paramString);
    if (integer == null)
      throw new CatalogException(3); 
    int i = integer.intValue();
    try {
      Integer integer1 = (Integer)entryArgs.get(i);
      if (integer1.intValue() != paramVector.size())
        throw new CatalogException(2); 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new CatalogException(3);
    } 
    this.entryType = i;
    this.args = paramVector;
  }
  
  public CatalogEntry(int paramInt, Vector paramVector) throws CatalogException {
    try {
      Integer integer = (Integer)entryArgs.get(paramInt);
      if (integer.intValue() != paramVector.size())
        throw new CatalogException(2); 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new CatalogException(3);
    } 
    this.entryType = paramInt;
    this.args = paramVector;
  }
  
  public int getEntryType() { return this.entryType; }
  
  public String getEntryArg(int paramInt) {
    try {
      return (String)this.args.get(paramInt);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      return null;
    } 
  }
  
  public void setEntryArg(int paramInt, String paramString) throws ArrayIndexOutOfBoundsException { this.args.set(paramInt, paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\CatalogEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */