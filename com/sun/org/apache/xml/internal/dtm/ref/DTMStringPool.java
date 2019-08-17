package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.utils.IntVector;
import java.util.Vector;

public class DTMStringPool {
  Vector m_intToString = new Vector();
  
  static final int HASHPRIME = 101;
  
  int[] m_hashStart = new int[101];
  
  IntVector m_hashChain;
  
  public static final int NULL = -1;
  
  public DTMStringPool(int paramInt) {
    this.m_hashChain = new IntVector(paramInt);
    removeAllElements();
    stringToIndex("");
  }
  
  public DTMStringPool() { this(512); }
  
  public void removeAllElements() {
    this.m_intToString.removeAllElements();
    for (byte b = 0; b < 101; b++)
      this.m_hashStart[b] = -1; 
    this.m_hashChain.removeAllElements();
  }
  
  public String indexToString(int paramInt) throws ArrayIndexOutOfBoundsException { return (paramInt == -1) ? null : (String)this.m_intToString.elementAt(paramInt); }
  
  public int stringToIndex(String paramString) {
    if (paramString == null)
      return -1; 
    int i = paramString.hashCode() % 101;
    if (i < 0)
      i = -i; 
    int j = this.m_hashStart[i];
    int k;
    for (k = j; k != -1; k = this.m_hashChain.elementAt(k)) {
      if (this.m_intToString.elementAt(k).equals(paramString))
        return k; 
      j = k;
    } 
    int m = this.m_intToString.size();
    this.m_intToString.addElement(paramString);
    this.m_hashChain.addElement(-1);
    if (j == -1) {
      this.m_hashStart[i] = m;
    } else {
      this.m_hashChain.setElementAt(m, j);
    } 
    return m;
  }
  
  public static void _main(String[] paramArrayOfString) {
    String[] arrayOfString = { 
        "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", 
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen", 
        "Twenty", "Twenty-One", "Twenty-Two", "Twenty-Three", "Twenty-Four", "Twenty-Five", "Twenty-Six", "Twenty-Seven", "Twenty-Eight", "Twenty-Nine", 
        "Thirty", "Thirty-One", "Thirty-Two", "Thirty-Three", "Thirty-Four", "Thirty-Five", "Thirty-Six", "Thirty-Seven", "Thirty-Eight", "Thirty-Nine" };
    DTMStringPool dTMStringPool = new DTMStringPool();
    System.out.println("If no complaints are printed below, we passed initial test.");
    for (byte b = 0; b <= 1; b++) {
      byte b1;
      for (b1 = 0; b1 < arrayOfString.length; b1++) {
        int i = dTMStringPool.stringToIndex(arrayOfString[b1]);
        if (i != b1)
          System.out.println("\tMismatch populating pool: assigned " + i + " for create " + b1); 
      } 
      for (b1 = 0; b1 < arrayOfString.length; b1++) {
        int i = dTMStringPool.stringToIndex(arrayOfString[b1]);
        if (i != b1)
          System.out.println("\tMismatch in stringToIndex: returned " + i + " for lookup " + b1); 
      } 
      for (b1 = 0; b1 < arrayOfString.length; b1++) {
        String str = dTMStringPool.indexToString(b1);
        if (!arrayOfString[b1].equals(str))
          System.out.println("\tMismatch in indexToString: returned" + str + " for lookup " + b1); 
      } 
      dTMStringPool.removeAllElements();
      System.out.println("\nPass " + b + " complete\n");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMStringPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */