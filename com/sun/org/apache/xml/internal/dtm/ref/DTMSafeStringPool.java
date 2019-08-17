package com.sun.org.apache.xml.internal.dtm.ref;

public class DTMSafeStringPool extends DTMStringPool {
  public void removeAllElements() { super.removeAllElements(); }
  
  public String indexToString(int paramInt) throws ArrayIndexOutOfBoundsException { return super.indexToString(paramInt); }
  
  public int stringToIndex(String paramString) { return super.stringToIndex(paramString); }
  
  public static void _main(String[] paramArrayOfString) {
    String[] arrayOfString = { 
        "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", 
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen", 
        "Twenty", "Twenty-One", "Twenty-Two", "Twenty-Three", "Twenty-Four", "Twenty-Five", "Twenty-Six", "Twenty-Seven", "Twenty-Eight", "Twenty-Nine", 
        "Thirty", "Thirty-One", "Thirty-Two", "Thirty-Three", "Thirty-Four", "Thirty-Five", "Thirty-Six", "Thirty-Seven", "Thirty-Eight", "Thirty-Nine" };
    DTMSafeStringPool dTMSafeStringPool = new DTMSafeStringPool();
    System.out.println("If no complaints are printed below, we passed initial test.");
    for (byte b = 0; b <= 1; b++) {
      byte b1;
      for (b1 = 0; b1 < arrayOfString.length; b1++) {
        int i = dTMSafeStringPool.stringToIndex(arrayOfString[b1]);
        if (i != b1)
          System.out.println("\tMismatch populating pool: assigned " + i + " for create " + b1); 
      } 
      for (b1 = 0; b1 < arrayOfString.length; b1++) {
        int i = dTMSafeStringPool.stringToIndex(arrayOfString[b1]);
        if (i != b1)
          System.out.println("\tMismatch in stringToIndex: returned " + i + " for lookup " + b1); 
      } 
      for (b1 = 0; b1 < arrayOfString.length; b1++) {
        String str = dTMSafeStringPool.indexToString(b1);
        if (!arrayOfString[b1].equals(str))
          System.out.println("\tMismatch in indexToString: returned" + str + " for lookup " + b1); 
      } 
      dTMSafeStringPool.removeAllElements();
      System.out.println("\nPass " + b + " complete\n");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMSafeStringPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */