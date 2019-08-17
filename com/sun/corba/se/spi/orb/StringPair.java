package com.sun.corba.se.spi.orb;

public class StringPair {
  private String first;
  
  private String second;
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof StringPair))
      return false; 
    StringPair stringPair = (StringPair)paramObject;
    return (this.first.equals(stringPair.first) && this.second.equals(stringPair.second));
  }
  
  public int hashCode() { return this.first.hashCode() ^ this.second.hashCode(); }
  
  public StringPair(String paramString1, String paramString2) {
    this.first = paramString1;
    this.second = paramString2;
  }
  
  public String getFirst() { return this.first; }
  
  public String getSecond() { return this.second; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orb\StringPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */