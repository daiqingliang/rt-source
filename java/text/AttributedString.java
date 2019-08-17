package java.text;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class AttributedString {
  private static final int ARRAY_SIZE_INCREMENT = 10;
  
  String text;
  
  int runArraySize;
  
  int runCount;
  
  int[] runStarts;
  
  Vector<AttributedCharacterIterator.Attribute>[] runAttributes;
  
  Vector<Object>[] runAttributeValues;
  
  AttributedString(AttributedCharacterIterator[] paramArrayOfAttributedCharacterIterator) {
    if (paramArrayOfAttributedCharacterIterator == null)
      throw new NullPointerException("Iterators must not be null"); 
    if (paramArrayOfAttributedCharacterIterator.length == 0) {
      this.text = "";
    } else {
      StringBuffer stringBuffer = new StringBuffer();
      int i;
      for (i = 0; i < paramArrayOfAttributedCharacterIterator.length; i++)
        appendContents(stringBuffer, paramArrayOfAttributedCharacterIterator[i]); 
      this.text = stringBuffer.toString();
      if (this.text.length() > 0) {
        i = 0;
        Map map = null;
        for (byte b = 0; b < paramArrayOfAttributedCharacterIterator.length; b++) {
          AttributedCharacterIterator attributedCharacterIterator = paramArrayOfAttributedCharacterIterator[b];
          int j = attributedCharacterIterator.getBeginIndex();
          int k = attributedCharacterIterator.getEndIndex();
          int m;
          for (m = j; m < k; m = attributedCharacterIterator.getRunLimit()) {
            attributedCharacterIterator.setIndex(m);
            Map map1 = attributedCharacterIterator.getAttributes();
            if (mapsDiffer(map, map1))
              setAttributes(map1, m - j + i); 
            map = map1;
          } 
          i += k - j;
        } 
      } 
    } 
  }
  
  public AttributedString(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.text = paramString;
  }
  
  public AttributedString(String paramString, Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap) {
    if (paramString == null || paramMap == null)
      throw new NullPointerException(); 
    this.text = paramString;
    if (paramString.length() == 0) {
      if (paramMap.isEmpty())
        return; 
      throw new IllegalArgumentException("Can't add attribute to 0-length text");
    } 
    int i = paramMap.size();
    if (i > 0) {
      createRunAttributeDataVectors();
      Vector vector1 = new Vector(i);
      Vector vector2 = new Vector(i);
      this.runAttributes[0] = vector1;
      this.runAttributeValues[0] = vector2;
      for (Map.Entry entry : paramMap.entrySet()) {
        vector1.addElement(entry.getKey());
        vector2.addElement(entry.getValue());
      } 
    } 
  }
  
  public AttributedString(AttributedCharacterIterator paramAttributedCharacterIterator) { this(paramAttributedCharacterIterator, paramAttributedCharacterIterator.getBeginIndex(), paramAttributedCharacterIterator.getEndIndex(), null); }
  
  public AttributedString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2) { this(paramAttributedCharacterIterator, paramInt1, paramInt2, null); }
  
  public AttributedString(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt1, int paramInt2, AttributedCharacterIterator.Attribute[] paramArrayOfAttribute) {
    if (paramAttributedCharacterIterator == null)
      throw new NullPointerException(); 
    int i = paramAttributedCharacterIterator.getBeginIndex();
    int j = paramAttributedCharacterIterator.getEndIndex();
    if (paramInt1 < i || paramInt2 > j || paramInt1 > paramInt2)
      throw new IllegalArgumentException("Invalid substring range"); 
    StringBuffer stringBuffer = new StringBuffer();
    paramAttributedCharacterIterator.setIndex(paramInt1);
    char c;
    for (c = paramAttributedCharacterIterator.current(); paramAttributedCharacterIterator.getIndex() < paramInt2; c = paramAttributedCharacterIterator.next())
      stringBuffer.append(c); 
    this.text = stringBuffer.toString();
    if (paramInt1 == paramInt2)
      return; 
    HashSet hashSet = new HashSet();
    if (paramArrayOfAttribute == null) {
      hashSet.addAll(paramAttributedCharacterIterator.getAllAttributeKeys());
    } else {
      for (byte b = 0; b < paramArrayOfAttribute.length; b++)
        hashSet.add(paramArrayOfAttribute[b]); 
      hashSet.retainAll(paramAttributedCharacterIterator.getAllAttributeKeys());
    } 
    if (hashSet.isEmpty())
      return; 
    for (AttributedCharacterIterator.Attribute attribute : hashSet) {
      paramAttributedCharacterIterator.setIndex(i);
      while (paramAttributedCharacterIterator.getIndex() < paramInt2) {
        int k = paramAttributedCharacterIterator.getRunStart(attribute);
        int m = paramAttributedCharacterIterator.getRunLimit(attribute);
        Object object = paramAttributedCharacterIterator.getAttribute(attribute);
        if (object != null)
          if (object instanceof Annotation) {
            if (k >= paramInt1 && m <= paramInt2) {
              addAttribute(attribute, object, k - paramInt1, m - paramInt1);
            } else if (m > paramInt2) {
              break;
            } 
          } else {
            if (k >= paramInt2)
              break; 
            if (m > paramInt1) {
              if (k < paramInt1)
                k = paramInt1; 
              if (m > paramInt2)
                m = paramInt2; 
              if (k != m)
                addAttribute(attribute, object, k - paramInt1, m - paramInt1); 
            } 
          }  
        paramAttributedCharacterIterator.setIndex(m);
      } 
    } 
  }
  
  public void addAttribute(AttributedCharacterIterator.Attribute paramAttribute, Object paramObject) {
    if (paramAttribute == null)
      throw new NullPointerException(); 
    int i = length();
    if (i == 0)
      throw new IllegalArgumentException("Can't add attribute to 0-length text"); 
    addAttributeImpl(paramAttribute, paramObject, 0, i);
  }
  
  public void addAttribute(AttributedCharacterIterator.Attribute paramAttribute, Object paramObject, int paramInt1, int paramInt2) {
    if (paramAttribute == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 > length() || paramInt1 >= paramInt2)
      throw new IllegalArgumentException("Invalid substring range"); 
    addAttributeImpl(paramAttribute, paramObject, paramInt1, paramInt2);
  }
  
  public void addAttributes(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, int paramInt1, int paramInt2) {
    if (paramMap == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 > length() || paramInt1 > paramInt2)
      throw new IllegalArgumentException("Invalid substring range"); 
    if (paramInt1 == paramInt2) {
      if (paramMap.isEmpty())
        return; 
      throw new IllegalArgumentException("Can't add attribute to 0-length text");
    } 
    if (this.runCount == 0)
      createRunAttributeDataVectors(); 
    int i = ensureRunBreak(paramInt1);
    int j = ensureRunBreak(paramInt2);
    for (Map.Entry entry : paramMap.entrySet())
      addAttributeRunData((AttributedCharacterIterator.Attribute)entry.getKey(), entry.getValue(), i, j); 
  }
  
  private void addAttributeImpl(AttributedCharacterIterator.Attribute paramAttribute, Object paramObject, int paramInt1, int paramInt2) {
    if (this.runCount == 0)
      createRunAttributeDataVectors(); 
    int i = ensureRunBreak(paramInt1);
    int j = ensureRunBreak(paramInt2);
    addAttributeRunData(paramAttribute, paramObject, i, j);
  }
  
  private final void createRunAttributeDataVectors() {
    int[] arrayOfInt = new int[10];
    Vector[] arrayOfVector1 = (Vector[])new Vector[10];
    Vector[] arrayOfVector2 = (Vector[])new Vector[10];
    this.runStarts = arrayOfInt;
    this.runAttributes = arrayOfVector1;
    this.runAttributeValues = arrayOfVector2;
    this.runArraySize = 10;
    this.runCount = 1;
  }
  
  private final int ensureRunBreak(int paramInt) { return ensureRunBreak(paramInt, true); }
  
  private final int ensureRunBreak(int paramInt, boolean paramBoolean) {
    if (paramInt == length())
      return this.runCount; 
    byte b;
    for (b = 0; b < this.runCount && this.runStarts[b] < paramInt; b++);
    if (b < this.runCount && this.runStarts[b] == paramInt)
      return b; 
    if (this.runCount == this.runArraySize) {
      int j = this.runArraySize + 10;
      int[] arrayOfInt = new int[j];
      Vector[] arrayOfVector1 = (Vector[])new Vector[j];
      Vector[] arrayOfVector2 = (Vector[])new Vector[j];
      for (byte b1 = 0; b1 < this.runArraySize; b1++) {
        arrayOfInt[b1] = this.runStarts[b1];
        arrayOfVector1[b1] = this.runAttributes[b1];
        arrayOfVector2[b1] = this.runAttributeValues[b1];
      } 
      this.runStarts = arrayOfInt;
      this.runAttributes = arrayOfVector1;
      this.runAttributeValues = arrayOfVector2;
      this.runArraySize = j;
    } 
    Vector vector1 = null;
    Vector vector2 = null;
    if (paramBoolean) {
      Vector vector3 = this.runAttributes[b - 1];
      Vector vector4 = this.runAttributeValues[b - 1];
      if (vector3 != null)
        vector1 = new Vector(vector3); 
      if (vector4 != null)
        vector2 = new Vector(vector4); 
    } 
    this.runCount++;
    for (int i = this.runCount - 1; i > b; i--) {
      this.runStarts[i] = this.runStarts[i - 1];
      this.runAttributes[i] = this.runAttributes[i - 1];
      this.runAttributeValues[i] = this.runAttributeValues[i - 1];
    } 
    this.runStarts[b] = paramInt;
    this.runAttributes[b] = vector1;
    this.runAttributeValues[b] = vector2;
    return b;
  }
  
  private void addAttributeRunData(AttributedCharacterIterator.Attribute paramAttribute, Object paramObject, int paramInt1, int paramInt2) {
    for (int i = paramInt1; i < paramInt2; i++) {
      int j = -1;
      if (this.runAttributes[i] == null) {
        Vector vector1 = new Vector();
        Vector vector2 = new Vector();
        this.runAttributes[i] = vector1;
        this.runAttributeValues[i] = vector2;
      } else {
        j = this.runAttributes[i].indexOf(paramAttribute);
      } 
      if (j == -1) {
        int k = this.runAttributes[i].size();
        this.runAttributes[i].addElement(paramAttribute);
        try {
          this.runAttributeValues[i].addElement(paramObject);
        } catch (Exception exception) {
          this.runAttributes[i].setSize(k);
          this.runAttributeValues[i].setSize(k);
        } 
      } else {
        this.runAttributeValues[i].set(j, paramObject);
      } 
    } 
  }
  
  public AttributedCharacterIterator getIterator() { return getIterator(null, 0, length()); }
  
  public AttributedCharacterIterator getIterator(AttributedCharacterIterator.Attribute[] paramArrayOfAttribute) { return getIterator(paramArrayOfAttribute, 0, length()); }
  
  public AttributedCharacterIterator getIterator(AttributedCharacterIterator.Attribute[] paramArrayOfAttribute, int paramInt1, int paramInt2) { return new AttributedStringIterator(paramArrayOfAttribute, paramInt1, paramInt2); }
  
  int length() { return this.text.length(); }
  
  private char charAt(int paramInt) { return this.text.charAt(paramInt); }
  
  private Object getAttribute(AttributedCharacterIterator.Attribute paramAttribute, int paramInt) {
    Vector vector1 = this.runAttributes[paramInt];
    Vector vector2 = this.runAttributeValues[paramInt];
    if (vector1 == null)
      return null; 
    int i = vector1.indexOf(paramAttribute);
    return (i != -1) ? vector2.elementAt(i) : null;
  }
  
  private Object getAttributeCheckRange(AttributedCharacterIterator.Attribute paramAttribute, int paramInt1, int paramInt2, int paramInt3) {
    Object object = getAttribute(paramAttribute, paramInt1);
    if (object instanceof Annotation) {
      if (paramInt2 > 0) {
        int j = paramInt1;
        int k;
        for (k = this.runStarts[j]; k >= paramInt2 && valuesMatch(object, getAttribute(paramAttribute, j - 1)); k = this.runStarts[--j]);
        if (k < paramInt2)
          return null; 
      } 
      int i = length();
      if (paramInt3 < i) {
        int j = paramInt1;
        int k;
        for (k = (j < this.runCount - 1) ? this.runStarts[j + 1] : i; k <= paramInt3 && valuesMatch(object, getAttribute(paramAttribute, j + 1)); k = (++j < this.runCount - 1) ? this.runStarts[j + 1] : i);
        if (k > paramInt3)
          return null; 
      } 
    } 
    return object;
  }
  
  private boolean attributeValuesMatch(Set<? extends AttributedCharacterIterator.Attribute> paramSet, int paramInt1, int paramInt2) {
    for (AttributedCharacterIterator.Attribute attribute : paramSet) {
      if (!valuesMatch(getAttribute(attribute, paramInt1), getAttribute(attribute, paramInt2)))
        return false; 
    } 
    return true;
  }
  
  private static final boolean valuesMatch(Object paramObject1, Object paramObject2) { return (paramObject1 == null) ? ((paramObject2 == null)) : paramObject1.equals(paramObject2); }
  
  private final void appendContents(StringBuffer paramStringBuffer, CharacterIterator paramCharacterIterator) {
    int i = paramCharacterIterator.getBeginIndex();
    int j = paramCharacterIterator.getEndIndex();
    while (i < j) {
      paramCharacterIterator.setIndex(i++);
      paramStringBuffer.append(paramCharacterIterator.current());
    } 
  }
  
  private void setAttributes(Map<AttributedCharacterIterator.Attribute, Object> paramMap, int paramInt) {
    if (this.runCount == 0)
      createRunAttributeDataVectors(); 
    int i = ensureRunBreak(paramInt, false);
    int j;
    if (paramMap != null && (j = paramMap.size()) > 0) {
      Vector vector1 = new Vector(j);
      Vector vector2 = new Vector(j);
      for (Map.Entry entry : paramMap.entrySet()) {
        vector1.add(entry.getKey());
        vector2.add(entry.getValue());
      } 
      this.runAttributes[i] = vector1;
      this.runAttributeValues[i] = vector2;
    } 
  }
  
  private static <K, V> boolean mapsDiffer(Map<K, V> paramMap1, Map<K, V> paramMap2) { return (paramMap1 == null) ? ((paramMap2 != null && paramMap2.size() > 0)) : (!paramMap1.equals(paramMap2)); }
  
  private final class AttributeMap extends AbstractMap<AttributedCharacterIterator.Attribute, Object> {
    int runIndex;
    
    int beginIndex;
    
    int endIndex;
    
    AttributeMap(int param1Int1, int param1Int2, int param1Int3) {
      this.runIndex = param1Int1;
      this.beginIndex = param1Int2;
      this.endIndex = param1Int3;
    }
    
    public Set<Map.Entry<AttributedCharacterIterator.Attribute, Object>> entrySet() {
      HashSet hashSet = new HashSet();
      synchronized (AttributedString.this) {
        int i = AttributedString.this.runAttributes[this.runIndex].size();
        for (byte b = 0; b < i; b++) {
          AttributedCharacterIterator.Attribute attribute = (AttributedCharacterIterator.Attribute)AttributedString.this.runAttributes[this.runIndex].get(b);
          Object object = AttributedString.this.runAttributeValues[this.runIndex].get(b);
          if (object instanceof Annotation) {
            object = AttributedString.this.getAttributeCheckRange(attribute, this.runIndex, this.beginIndex, this.endIndex);
            if (object == null)
              continue; 
          } 
          AttributeEntry attributeEntry = new AttributeEntry(attribute, object);
          hashSet.add(attributeEntry);
          continue;
        } 
      } 
      return hashSet;
    }
    
    public Object get(Object param1Object) { return AttributedString.this.getAttributeCheckRange((AttributedCharacterIterator.Attribute)param1Object, this.runIndex, this.beginIndex, this.endIndex); }
  }
  
  private final class AttributedStringIterator implements AttributedCharacterIterator {
    private int beginIndex;
    
    private int endIndex;
    
    private AttributedCharacterIterator.Attribute[] relevantAttributes;
    
    private int currentIndex;
    
    private int currentRunIndex;
    
    private int currentRunStart;
    
    private int currentRunLimit;
    
    AttributedStringIterator(AttributedCharacterIterator.Attribute[] param1ArrayOfAttribute, int param1Int1, int param1Int2) {
      if (param1Int1 < 0 || param1Int1 > param1Int2 || param1Int2 > this$0.length())
        throw new IllegalArgumentException("Invalid substring range"); 
      this.beginIndex = param1Int1;
      this.endIndex = param1Int2;
      this.currentIndex = param1Int1;
      updateRunInfo();
      if (param1ArrayOfAttribute != null)
        this.relevantAttributes = (Attribute[])param1ArrayOfAttribute.clone(); 
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof AttributedStringIterator))
        return false; 
      AttributedStringIterator attributedStringIterator = (AttributedStringIterator)param1Object;
      return (AttributedString.this != attributedStringIterator.getString()) ? false : (!(this.currentIndex != attributedStringIterator.currentIndex || this.beginIndex != attributedStringIterator.beginIndex || this.endIndex != attributedStringIterator.endIndex));
    }
    
    public int hashCode() { return AttributedString.this.text.hashCode() ^ this.currentIndex ^ this.beginIndex ^ this.endIndex; }
    
    public Object clone() {
      try {
        return (AttributedStringIterator)super.clone();
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        throw new InternalError(cloneNotSupportedException);
      } 
    }
    
    public char first() { return internalSetIndex(this.beginIndex); }
    
    public char last() { return (this.endIndex == this.beginIndex) ? internalSetIndex(this.endIndex) : internalSetIndex(this.endIndex - 1); }
    
    public char current() { return (this.currentIndex == this.endIndex) ? Character.MAX_VALUE : AttributedString.this.charAt(this.currentIndex); }
    
    public char next() { return (this.currentIndex < this.endIndex) ? internalSetIndex(this.currentIndex + 1) : 65535; }
    
    public char previous() { return (this.currentIndex > this.beginIndex) ? internalSetIndex(this.currentIndex - 1) : 65535; }
    
    public char setIndex(int param1Int) {
      if (param1Int < this.beginIndex || param1Int > this.endIndex)
        throw new IllegalArgumentException("Invalid index"); 
      return internalSetIndex(param1Int);
    }
    
    public int getBeginIndex() { return this.beginIndex; }
    
    public int getEndIndex() { return this.endIndex; }
    
    public int getIndex() { return this.currentIndex; }
    
    public int getRunStart() { return this.currentRunStart; }
    
    public int getRunStart(AttributedCharacterIterator.Attribute param1Attribute) {
      if (this.currentRunStart == this.beginIndex || this.currentRunIndex == -1)
        return this.currentRunStart; 
      Object object = getAttribute(param1Attribute);
      int i = this.currentRunStart;
      int j = this.currentRunIndex;
      while (i > this.beginIndex && AttributedString.valuesMatch(object, AttributedString.this.getAttribute(param1Attribute, j - 1)))
        i = AttributedString.this.runStarts[--j]; 
      if (i < this.beginIndex)
        i = this.beginIndex; 
      return i;
    }
    
    public int getRunStart(Set<? extends AttributedCharacterIterator.Attribute> param1Set) {
      if (this.currentRunStart == this.beginIndex || this.currentRunIndex == -1)
        return this.currentRunStart; 
      int i = this.currentRunStart;
      int j = this.currentRunIndex;
      while (i > this.beginIndex && AttributedString.this.attributeValuesMatch(param1Set, this.currentRunIndex, j - 1))
        i = AttributedString.this.runStarts[--j]; 
      if (i < this.beginIndex)
        i = this.beginIndex; 
      return i;
    }
    
    public int getRunLimit() { return this.currentRunLimit; }
    
    public int getRunLimit(AttributedCharacterIterator.Attribute param1Attribute) {
      if (this.currentRunLimit == this.endIndex || this.currentRunIndex == -1)
        return this.currentRunLimit; 
      Object object = getAttribute(param1Attribute);
      int i = this.currentRunLimit;
      int j = this.currentRunIndex;
      while (i < this.endIndex && AttributedString.valuesMatch(object, AttributedString.this.getAttribute(param1Attribute, j + 1)))
        i = (++j < AttributedString.this.runCount - 1) ? AttributedString.this.runStarts[j + 1] : this.endIndex; 
      if (i > this.endIndex)
        i = this.endIndex; 
      return i;
    }
    
    public int getRunLimit(Set<? extends AttributedCharacterIterator.Attribute> param1Set) {
      if (this.currentRunLimit == this.endIndex || this.currentRunIndex == -1)
        return this.currentRunLimit; 
      int i = this.currentRunLimit;
      int j = this.currentRunIndex;
      while (i < this.endIndex && AttributedString.this.attributeValuesMatch(param1Set, this.currentRunIndex, j + 1))
        i = (++j < AttributedString.this.runCount - 1) ? AttributedString.this.runStarts[j + 1] : this.endIndex; 
      if (i > this.endIndex)
        i = this.endIndex; 
      return i;
    }
    
    public Map<AttributedCharacterIterator.Attribute, Object> getAttributes() { return (AttributedString.this.runAttributes == null || this.currentRunIndex == -1 || AttributedString.this.runAttributes[this.currentRunIndex] == null) ? new Hashtable() : new AttributedString.AttributeMap(AttributedString.this, this.currentRunIndex, this.beginIndex, this.endIndex); }
    
    public Set<AttributedCharacterIterator.Attribute> getAllAttributeKeys() {
      if (AttributedString.this.runAttributes == null)
        return new HashSet(); 
      synchronized (AttributedString.this) {
        HashSet hashSet = new HashSet();
        for (byte b = 0; b < AttributedString.this.runCount; b++) {
          if (AttributedString.this.runStarts[b] < this.endIndex && (b == AttributedString.this.runCount - 1 || AttributedString.this.runStarts[b + true] > this.beginIndex)) {
            Vector vector = AttributedString.this.runAttributes[b];
            if (vector != null) {
              int i = vector.size();
              while (i-- > 0)
                hashSet.add(vector.get(i)); 
            } 
          } 
        } 
        return hashSet;
      } 
    }
    
    public Object getAttribute(AttributedCharacterIterator.Attribute param1Attribute) {
      int i = this.currentRunIndex;
      return (i < 0) ? null : AttributedString.this.getAttributeCheckRange(param1Attribute, i, this.beginIndex, this.endIndex);
    }
    
    private AttributedString getString() { return AttributedString.this; }
    
    private char internalSetIndex(int param1Int) {
      this.currentIndex = param1Int;
      if (param1Int < this.currentRunStart || param1Int >= this.currentRunLimit)
        updateRunInfo(); 
      return (this.currentIndex == this.endIndex) ? Character.MAX_VALUE : AttributedString.this.charAt(param1Int);
    }
    
    private void updateRunInfo() {
      if (this.currentIndex == this.endIndex) {
        this.currentRunStart = this.currentRunLimit = this.endIndex;
        this.currentRunIndex = -1;
      } else {
        synchronized (AttributedString.this) {
          byte b;
          for (b = -1; b < AttributedString.this.runCount - 1 && AttributedString.this.runStarts[b + 1] <= this.currentIndex; b++);
          this.currentRunIndex = b;
          if (b >= 0) {
            this.currentRunStart = AttributedString.this.runStarts[b];
            if (this.currentRunStart < this.beginIndex)
              this.currentRunStart = this.beginIndex; 
          } else {
            this.currentRunStart = this.beginIndex;
          } 
          if (b < AttributedString.this.runCount - 1) {
            this.currentRunLimit = AttributedString.this.runStarts[b + 1];
            if (this.currentRunLimit > this.endIndex)
              this.currentRunLimit = this.endIndex; 
          } else {
            this.currentRunLimit = this.endIndex;
          } 
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\AttributedString.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */