package java.text;

final class RuleBasedCollationKey extends CollationKey {
  private String key = null;
  
  public int compareTo(CollationKey paramCollationKey) {
    int i = this.key.compareTo(((RuleBasedCollationKey)paramCollationKey).key);
    return (i <= -1) ? -1 : ((i >= 1) ? 1 : 0);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || !getClass().equals(paramObject.getClass()))
      return false; 
    RuleBasedCollationKey ruleBasedCollationKey = (RuleBasedCollationKey)paramObject;
    return this.key.equals(ruleBasedCollationKey.key);
  }
  
  public int hashCode() { return this.key.hashCode(); }
  
  public byte[] toByteArray() {
    char[] arrayOfChar = this.key.toCharArray();
    byte[] arrayOfByte = new byte[2 * arrayOfChar.length];
    byte b1 = 0;
    for (byte b2 = 0; b2 < arrayOfChar.length; b2++) {
      arrayOfByte[b1++] = (byte)(arrayOfChar[b2] >>> '\b');
      arrayOfByte[b1++] = (byte)(arrayOfChar[b2] & 0xFF);
    } 
    return arrayOfByte;
  }
  
  RuleBasedCollationKey(String paramString1, String paramString2) {
    super(paramString1);
    this.key = paramString2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\RuleBasedCollationKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */