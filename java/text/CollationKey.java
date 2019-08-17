package java.text;

public abstract class CollationKey extends Object implements Comparable<CollationKey> {
  private final String source;
  
  public abstract int compareTo(CollationKey paramCollationKey);
  
  public String getSourceString() { return this.source; }
  
  public abstract byte[] toByteArray();
  
  protected CollationKey(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.source = paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\CollationKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */