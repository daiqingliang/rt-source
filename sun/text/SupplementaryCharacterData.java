package sun.text;

public final class SupplementaryCharacterData implements Cloneable {
  private static final byte IGNORE = -1;
  
  private int[] dataTable;
  
  public SupplementaryCharacterData(int[] paramArrayOfInt) { this.dataTable = paramArrayOfInt; }
  
  public int getValue(int paramInt) {
    int k;
    assert paramInt >= 65536 && paramInt <= 1114111 : "Invalid code point:" + Integer.toHexString(paramInt);
    int i = 0;
    int j = this.dataTable.length - 1;
    while (true) {
      k = (i + j) / 2;
      int n = this.dataTable[k] >> 8;
      int i1 = this.dataTable[k + 1] >> 8;
      if (paramInt < n) {
        j = k;
        continue;
      } 
      if (paramInt > i1 - 1) {
        i = k;
        continue;
      } 
      break;
    } 
    int m = this.dataTable[k] & 0xFF;
    return (m == 255) ? -1 : m;
  }
  
  public int[] getArray() { return this.dataTable; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\SupplementaryCharacterData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */