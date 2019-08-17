package java.text;

public class RuleBasedCollator extends Collator {
  static final int CHARINDEX = 1879048192;
  
  static final int EXPANDCHARINDEX = 2113929216;
  
  static final int CONTRACTCHARINDEX = 2130706432;
  
  static final int UNMAPPED = -1;
  
  private static final int COLLATIONKEYOFFSET = 1;
  
  private RBCollationTables tables = null;
  
  private StringBuffer primResult = null;
  
  private StringBuffer secResult = null;
  
  private StringBuffer terResult = null;
  
  private CollationElementIterator sourceCursor = null;
  
  private CollationElementIterator targetCursor = null;
  
  public RuleBasedCollator(String paramString) throws ParseException { this(paramString, 1); }
  
  RuleBasedCollator(String paramString, int paramInt) throws ParseException {
    setStrength(2);
    setDecomposition(paramInt);
    this.tables = new RBCollationTables(paramString, paramInt);
  }
  
  private RuleBasedCollator(RuleBasedCollator paramRuleBasedCollator) {
    setStrength(paramRuleBasedCollator.getStrength());
    setDecomposition(paramRuleBasedCollator.getDecomposition());
    this.tables = paramRuleBasedCollator.tables;
  }
  
  public String getRules() { return this.tables.getRules(); }
  
  public CollationElementIterator getCollationElementIterator(String paramString) { return new CollationElementIterator(paramString, this); }
  
  public CollationElementIterator getCollationElementIterator(CharacterIterator paramCharacterIterator) { return new CollationElementIterator(paramCharacterIterator, this); }
  
  public int compare(String paramString1, String paramString2) {
    if (paramString1 == null || paramString2 == null)
      throw new NullPointerException(); 
    byte b = 0;
    if (this.sourceCursor == null) {
      this.sourceCursor = getCollationElementIterator(paramString1);
    } else {
      this.sourceCursor.setText(paramString1);
    } 
    if (this.targetCursor == null) {
      this.targetCursor = getCollationElementIterator(paramString2);
    } else {
      this.targetCursor.setText(paramString2);
    } 
    int i = 0;
    int j = 0;
    byte b1 = (getStrength() >= 1) ? 1 : 0;
    byte b2 = b1;
    boolean bool1 = (getStrength() >= 2) ? 1 : 0;
    boolean bool2 = true;
    boolean bool3 = true;
    while (true) {
      if (bool2) {
        i = this.sourceCursor.next();
      } else {
        bool2 = true;
      } 
      if (bool3) {
        j = this.targetCursor.next();
      } else {
        bool3 = true;
      } 
      if (i == -1 || j == -1)
        break; 
      int k = CollationElementIterator.primaryOrder(i);
      int m = CollationElementIterator.primaryOrder(j);
      if (i == j) {
        if (this.tables.isFrenchSec() && k != 0 && !b2) {
          b2 = b1;
          bool1 = false;
        } 
        continue;
      } 
      if (k != m) {
        if (i == 0) {
          bool3 = false;
          continue;
        } 
        if (j == 0) {
          bool2 = false;
          continue;
        } 
        if (k == 0) {
          if (b2 != 0) {
            b = 1;
            b2 = 0;
          } 
          bool3 = false;
          continue;
        } 
        if (m == 0) {
          if (b2 != 0) {
            b = -1;
            b2 = 0;
          } 
          bool2 = false;
          continue;
        } 
        return (k < m) ? -1 : 1;
      } 
      if (b2 != 0) {
        short s1 = CollationElementIterator.secondaryOrder(i);
        short s2 = CollationElementIterator.secondaryOrder(j);
        if (s1 != s2) {
          b = (s1 < s2) ? -1 : 1;
          b2 = 0;
          continue;
        } 
        if (bool1) {
          short s3 = CollationElementIterator.tertiaryOrder(i);
          short s4 = CollationElementIterator.tertiaryOrder(j);
          if (s3 != s4) {
            b = (s3 < s4) ? -1 : 1;
            bool1 = false;
          } 
        } 
      } 
    } 
    if (i != -1) {
      do {
        if (CollationElementIterator.primaryOrder(i) != 0)
          return 1; 
        if (CollationElementIterator.secondaryOrder(i) == 0 || b2 == 0)
          continue; 
        b = 1;
        b2 = 0;
      } while ((i = this.sourceCursor.next()) != -1);
    } else if (j != -1) {
      do {
        if (CollationElementIterator.primaryOrder(j) != 0)
          return -1; 
        if (CollationElementIterator.secondaryOrder(j) == 0 || b2 == 0)
          continue; 
        b = -1;
        b2 = 0;
      } while ((j = this.targetCursor.next()) != -1);
    } 
    if (b == 0 && getStrength() == 3) {
      Normalizer.Form form;
      int k = getDecomposition();
      if (k == 1) {
        form = Normalizer.Form.NFD;
      } else if (k == 2) {
        form = Normalizer.Form.NFKD;
      } else {
        return paramString1.compareTo(paramString2);
      } 
      String str1 = Normalizer.normalize(paramString1, form);
      String str2 = Normalizer.normalize(paramString2, form);
      return str1.compareTo(str2);
    } 
    return b;
  }
  
  public CollationKey getCollationKey(String paramString) {
    if (paramString == null)
      return null; 
    if (this.primResult == null) {
      this.primResult = new StringBuffer();
      this.secResult = new StringBuffer();
      this.terResult = new StringBuffer();
    } else {
      this.primResult.setLength(0);
      this.secResult.setLength(0);
      this.terResult.setLength(0);
    } 
    int i = 0;
    boolean bool1 = (getStrength() >= 1) ? 1 : 0;
    boolean bool2 = (getStrength() >= 2) ? 1 : 0;
    short s1 = -1;
    short s2 = -1;
    int j = 0;
    if (this.sourceCursor == null) {
      this.sourceCursor = getCollationElementIterator(paramString);
    } else {
      this.sourceCursor.setText(paramString);
    } 
    while ((i = this.sourceCursor.next()) != -1) {
      s1 = CollationElementIterator.secondaryOrder(i);
      s2 = CollationElementIterator.tertiaryOrder(i);
      if (!CollationElementIterator.isIgnorable(i)) {
        this.primResult.append((char)(CollationElementIterator.primaryOrder(i) + 1));
        if (bool1) {
          if (this.tables.isFrenchSec() && j < this.secResult.length())
            RBCollationTables.reverse(this.secResult, j, this.secResult.length()); 
          this.secResult.append((char)(s1 + 1));
          j = this.secResult.length();
        } 
        if (bool2)
          this.terResult.append((char)(s2 + 1)); 
        continue;
      } 
      if (bool1 && s1 != 0)
        this.secResult.append((char)(s1 + this.tables.getMaxSecOrder() + 1)); 
      if (bool2 && s2 != 0)
        this.terResult.append((char)(s2 + this.tables.getMaxTerOrder() + 1)); 
    } 
    if (this.tables.isFrenchSec()) {
      if (j < this.secResult.length())
        RBCollationTables.reverse(this.secResult, j, this.secResult.length()); 
      RBCollationTables.reverse(this.secResult, 0, this.secResult.length());
    } 
    this.primResult.append(false);
    this.secResult.append(false);
    this.secResult.append(this.terResult.toString());
    this.primResult.append(this.secResult.toString());
    if (getStrength() == 3) {
      this.primResult.append(false);
      int k = getDecomposition();
      if (k == 1) {
        this.primResult.append(Normalizer.normalize(paramString, Normalizer.Form.NFD));
      } else if (k == 2) {
        this.primResult.append(Normalizer.normalize(paramString, Normalizer.Form.NFKD));
      } else {
        this.primResult.append(paramString);
      } 
    } 
    return new RuleBasedCollationKey(paramString, this.primResult.toString());
  }
  
  public Object clone() {
    if (getClass() == RuleBasedCollator.class)
      return new RuleBasedCollator(this); 
    RuleBasedCollator ruleBasedCollator = (RuleBasedCollator)super.clone();
    ruleBasedCollator.primResult = null;
    ruleBasedCollator.secResult = null;
    ruleBasedCollator.terResult = null;
    ruleBasedCollator.sourceCursor = null;
    ruleBasedCollator.targetCursor = null;
    return ruleBasedCollator;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!super.equals(paramObject))
      return false; 
    RuleBasedCollator ruleBasedCollator = (RuleBasedCollator)paramObject;
    return getRules().equals(ruleBasedCollator.getRules());
  }
  
  public int hashCode() { return getRules().hashCode(); }
  
  RBCollationTables getTables() { return this.tables; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\RuleBasedCollator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */