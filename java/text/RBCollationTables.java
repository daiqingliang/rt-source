package java.text;

import java.util.Vector;
import sun.text.IntHashtable;
import sun.text.UCompactIntArray;

final class RBCollationTables {
  static final int EXPANDCHARINDEX = 2113929216;
  
  static final int CONTRACTCHARINDEX = 2130706432;
  
  static final int UNMAPPED = -1;
  
  static final int PRIMARYORDERMASK = -65536;
  
  static final int SECONDARYORDERMASK = 65280;
  
  static final int TERTIARYORDERMASK = 255;
  
  static final int PRIMARYDIFFERENCEONLY = -65536;
  
  static final int SECONDARYDIFFERENCEONLY = -256;
  
  static final int PRIMARYORDERSHIFT = 16;
  
  static final int SECONDARYORDERSHIFT = 8;
  
  private String rules = null;
  
  private boolean frenchSec = false;
  
  private boolean seAsianSwapping = false;
  
  private UCompactIntArray mapping = null;
  
  private Vector<Vector<EntryPair>> contractTable = null;
  
  private Vector<int[]> expandTable = null;
  
  private IntHashtable contractFlags = null;
  
  private short maxSecOrder = 0;
  
  private short maxTerOrder = 0;
  
  public RBCollationTables(String paramString, int paramInt) throws ParseException {
    this.rules = paramString;
    RBTableBuilder rBTableBuilder = new RBTableBuilder(new BuildAPI(this, null));
    rBTableBuilder.build(paramString, paramInt);
  }
  
  public String getRules() { return this.rules; }
  
  public boolean isFrenchSec() { return this.frenchSec; }
  
  public boolean isSEAsianSwapping() { return this.seAsianSwapping; }
  
  Vector<EntryPair> getContractValues(int paramInt) {
    int i = this.mapping.elementAt(paramInt);
    return getContractValuesImpl(i - 2130706432);
  }
  
  private Vector<EntryPair> getContractValuesImpl(int paramInt) { return (paramInt >= 0) ? (Vector)this.contractTable.elementAt(paramInt) : null; }
  
  boolean usedInContractSeq(int paramInt) { return (this.contractFlags.get(paramInt) == 1); }
  
  int getMaxExpansion(int paramInt) {
    int i = 1;
    if (this.expandTable != null)
      for (byte b = 0; b < this.expandTable.size(); b++) {
        int[] arrayOfInt = (int[])this.expandTable.elementAt(b);
        int j = arrayOfInt.length;
        if (j > i && arrayOfInt[j - 1] == paramInt)
          i = j; 
      }  
    return i;
  }
  
  final int[] getExpandValueList(int paramInt) { return (int[])this.expandTable.elementAt(paramInt - 2113929216); }
  
  int getUnicodeOrder(int paramInt) { return this.mapping.elementAt(paramInt); }
  
  short getMaxSecOrder() { return this.maxSecOrder; }
  
  short getMaxTerOrder() { return this.maxTerOrder; }
  
  static void reverse(StringBuffer paramStringBuffer, int paramInt1, int paramInt2) {
    int i = paramInt1;
    for (int j = paramInt2 - 1; i < j; j--) {
      char c = paramStringBuffer.charAt(i);
      paramStringBuffer.setCharAt(i, paramStringBuffer.charAt(j));
      paramStringBuffer.setCharAt(j, c);
      i++;
    } 
  }
  
  static final int getEntry(Vector<EntryPair> paramVector, String paramString, boolean paramBoolean) {
    for (byte b = 0; b < paramVector.size(); b++) {
      EntryPair entryPair = (EntryPair)paramVector.elementAt(b);
      if (entryPair.fwd == paramBoolean && entryPair.entryName.equals(paramString))
        return b; 
    } 
    return -1;
  }
  
  final class BuildAPI {
    private BuildAPI() {}
    
    void fillInTables(boolean param1Boolean1, boolean param1Boolean2, UCompactIntArray param1UCompactIntArray, Vector<Vector<EntryPair>> param1Vector1, Vector<int[]> param1Vector2, IntHashtable param1IntHashtable, short param1Short1, short param1Short2) {
      RBCollationTables.this.frenchSec = param1Boolean1;
      RBCollationTables.this.seAsianSwapping = param1Boolean2;
      RBCollationTables.this.mapping = param1UCompactIntArray;
      RBCollationTables.this.contractTable = param1Vector1;
      RBCollationTables.this.expandTable = param1Vector2;
      RBCollationTables.this.contractFlags = param1IntHashtable;
      RBCollationTables.this.maxSecOrder = param1Short1;
      RBCollationTables.this.maxTerOrder = param1Short2;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\RBCollationTables.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */