package com.sun.org.apache.xml.internal.resolver.helpers;

public class Debug {
  protected int debug = 0;
  
  public void setDebug(int paramInt) { this.debug = paramInt; }
  
  public int getDebug() { return this.debug; }
  
  public void message(int paramInt, String paramString) {
    if (this.debug >= paramInt)
      System.out.println(paramString); 
  }
  
  public void message(int paramInt, String paramString1, String paramString2) {
    if (this.debug >= paramInt)
      System.out.println(paramString1 + ": " + paramString2); 
  }
  
  public void message(int paramInt, String paramString1, String paramString2, String paramString3) {
    if (this.debug >= paramInt) {
      System.out.println(paramString1 + ": " + paramString2);
      System.out.println("\t" + paramString3);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\resolver\helpers\Debug.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */