package java.text;

public interface CharacterIterator extends Cloneable {
  public static final char DONE = '￿';
  
  char first();
  
  char last();
  
  char current();
  
  char next();
  
  char previous();
  
  char setIndex(int paramInt);
  
  int getBeginIndex();
  
  int getEndIndex();
  
  int getIndex();
  
  Object clone();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\CharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */