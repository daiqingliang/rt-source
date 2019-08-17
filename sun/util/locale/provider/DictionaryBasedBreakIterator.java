package sun.util.locale.provider;

import java.io.IOException;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Stack;

class DictionaryBasedBreakIterator extends RuleBasedBreakIterator {
  private BreakDictionary dictionary;
  
  private boolean[] categoryFlags;
  
  private int dictionaryCharCount;
  
  private int[] cachedBreakPositions;
  
  private int positionInCache;
  
  DictionaryBasedBreakIterator(String paramString1, String paramString2) throws IOException {
    super(paramString1);
    byte[] arrayOfByte = getAdditionalData();
    if (arrayOfByte != null) {
      prepareCategoryFlags(arrayOfByte);
      setAdditionalData(null);
    } 
    this.dictionary = new BreakDictionary(paramString2);
  }
  
  private void prepareCategoryFlags(byte[] paramArrayOfByte) {
    this.categoryFlags = new boolean[paramArrayOfByte.length];
    for (byte b = 0; b < paramArrayOfByte.length; b++)
      this.categoryFlags[b] = (paramArrayOfByte[b] == 1); 
  }
  
  public void setText(CharacterIterator paramCharacterIterator) {
    super.setText(paramCharacterIterator);
    this.cachedBreakPositions = null;
    this.dictionaryCharCount = 0;
    this.positionInCache = 0;
  }
  
  public int first() {
    this.cachedBreakPositions = null;
    this.dictionaryCharCount = 0;
    this.positionInCache = 0;
    return super.first();
  }
  
  public int last() {
    this.cachedBreakPositions = null;
    this.dictionaryCharCount = 0;
    this.positionInCache = 0;
    return super.last();
  }
  
  public int previous() {
    CharacterIterator characterIterator = getText();
    if (this.cachedBreakPositions != null && this.positionInCache > 0) {
      this.positionInCache--;
      characterIterator.setIndex(this.cachedBreakPositions[this.positionInCache]);
      return this.cachedBreakPositions[this.positionInCache];
    } 
    this.cachedBreakPositions = null;
    int i = super.previous();
    if (this.cachedBreakPositions != null)
      this.positionInCache = this.cachedBreakPositions.length - 2; 
    return i;
  }
  
  public int preceding(int paramInt) {
    CharacterIterator characterIterator = getText();
    checkOffset(paramInt, characterIterator);
    if (this.cachedBreakPositions == null || paramInt <= this.cachedBreakPositions[0] || paramInt > this.cachedBreakPositions[this.cachedBreakPositions.length - 1]) {
      this.cachedBreakPositions = null;
      return super.preceding(paramInt);
    } 
    this.positionInCache = 0;
    while (this.positionInCache < this.cachedBreakPositions.length && paramInt > this.cachedBreakPositions[this.positionInCache])
      this.positionInCache++; 
    this.positionInCache--;
    characterIterator.setIndex(this.cachedBreakPositions[this.positionInCache]);
    return characterIterator.getIndex();
  }
  
  public int following(int paramInt) {
    CharacterIterator characterIterator = getText();
    checkOffset(paramInt, characterIterator);
    if (this.cachedBreakPositions == null || paramInt < this.cachedBreakPositions[0] || paramInt >= this.cachedBreakPositions[this.cachedBreakPositions.length - 1]) {
      this.cachedBreakPositions = null;
      return super.following(paramInt);
    } 
    this.positionInCache = 0;
    while (this.positionInCache < this.cachedBreakPositions.length && paramInt >= this.cachedBreakPositions[this.positionInCache])
      this.positionInCache++; 
    characterIterator.setIndex(this.cachedBreakPositions[this.positionInCache]);
    return characterIterator.getIndex();
  }
  
  protected int handleNext() {
    CharacterIterator characterIterator = getText();
    if (this.cachedBreakPositions == null || this.positionInCache == this.cachedBreakPositions.length - 1) {
      int i = characterIterator.getIndex();
      this.dictionaryCharCount = 0;
      int j = super.handleNext();
      if (this.dictionaryCharCount > 1 && j - i > 1) {
        divideUpDictionaryRange(i, j);
      } else {
        this.cachedBreakPositions = null;
        return j;
      } 
    } 
    if (this.cachedBreakPositions != null) {
      this.positionInCache++;
      characterIterator.setIndex(this.cachedBreakPositions[this.positionInCache]);
      return this.cachedBreakPositions[this.positionInCache];
    } 
    return -9999;
  }
  
  protected int lookupCategory(int paramInt) {
    int i = super.lookupCategory(paramInt);
    if (i != -1 && this.categoryFlags[i])
      this.dictionaryCharCount++; 
    return i;
  }
  
  private void divideUpDictionaryRange(int paramInt1, int paramInt2) {
    CharacterIterator characterIterator = getText();
    characterIterator.setIndex(paramInt1);
    int i = getCurrent();
    int j;
    for (j = lookupCategory(i); j == -1 || !this.categoryFlags[j]; j = lookupCategory(i))
      i = getNext(); 
    Stack stack1 = new Stack();
    Stack stack2 = new Stack();
    ArrayList arrayList = new ArrayList();
    short s = 0;
    int k = characterIterator.getIndex();
    Stack stack3 = null;
    for (i = getCurrent();; i = getNext()) {
      if (this.dictionary.getNextState(s, 0) == -1)
        stack2.push(Integer.valueOf(characterIterator.getIndex())); 
      s = this.dictionary.getNextStateFromCharacter(s, i);
      if (s == -1) {
        stack1.push(Integer.valueOf(characterIterator.getIndex()));
        break;
      } 
      if (s == 0 || characterIterator.getIndex() >= paramInt2) {
        if (characterIterator.getIndex() > k) {
          k = characterIterator.getIndex();
          Stack stack = (Stack)stack1.clone();
          stack3 = stack;
        } 
        while (!stack2.isEmpty() && arrayList.contains(stack2.peek()))
          stack2.pop(); 
        if (stack2.isEmpty()) {
          if (stack3 != null) {
            stack1 = stack3;
            if (k < paramInt2) {
              characterIterator.setIndex(k + 1);
            } else {
              break;
            } 
          } else {
            if ((stack1.size() == 0 || ((Integer)stack1.peek()).intValue() != characterIterator.getIndex()) && characterIterator.getIndex() != paramInt1)
              stack1.push(new Integer(characterIterator.getIndex())); 
            getNext();
            stack1.push(new Integer(characterIterator.getIndex()));
          } 
        } else {
          Integer integer1 = (Integer)stack2.pop();
          Integer integer2 = null;
          while (!stack1.isEmpty() && integer1.intValue() < ((Integer)stack1.peek()).intValue()) {
            integer2 = (Integer)stack1.pop();
            arrayList.add(integer2);
          } 
          stack1.push(integer1);
          characterIterator.setIndex(((Integer)stack1.peek()).intValue());
        } 
        i = getCurrent();
        if (characterIterator.getIndex() >= paramInt2)
          break; 
        continue;
      } 
    } 
    if (!stack1.isEmpty())
      stack1.pop(); 
    stack1.push(Integer.valueOf(paramInt2));
    this.cachedBreakPositions = new int[stack1.size() + 1];
    this.cachedBreakPositions[0] = paramInt1;
    for (byte b = 0; b < stack1.size(); b++)
      this.cachedBreakPositions[b + true] = ((Integer)stack1.elementAt(b)).intValue(); 
    this.positionInCache = 0;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\DictionaryBasedBreakIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */