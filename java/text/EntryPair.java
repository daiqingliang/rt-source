package java.text;

final class EntryPair {
  public String entryName;
  
  public int value;
  
  public boolean fwd;
  
  public EntryPair(String paramString, int paramInt) { this(paramString, paramInt, true); }
  
  public EntryPair(String paramString, int paramInt, boolean paramBoolean) {
    this.entryName = paramString;
    this.value = paramInt;
    this.fwd = paramBoolean;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\EntryPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */