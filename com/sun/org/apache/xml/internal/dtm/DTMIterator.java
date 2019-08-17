package com.sun.org.apache.xml.internal.dtm;

public interface DTMIterator {
  public static final short FILTER_ACCEPT = 1;
  
  public static final short FILTER_REJECT = 2;
  
  public static final short FILTER_SKIP = 3;
  
  DTM getDTM(int paramInt);
  
  DTMManager getDTMManager();
  
  int getRoot();
  
  void setRoot(int paramInt, Object paramObject);
  
  void reset();
  
  int getWhatToShow();
  
  boolean getExpandEntityReferences();
  
  int nextNode();
  
  int previousNode();
  
  void detach();
  
  void allowDetachToRelease(boolean paramBoolean);
  
  int getCurrentNode();
  
  boolean isFresh();
  
  void setShouldCacheNodes(boolean paramBoolean);
  
  boolean isMutable();
  
  int getCurrentPos();
  
  void runTo(int paramInt);
  
  void setCurrentPos(int paramInt);
  
  int item(int paramInt);
  
  void setItem(int paramInt1, int paramInt2);
  
  int getLength();
  
  DTMIterator cloneWithReset() throws CloneNotSupportedException;
  
  Object clone() throws CloneNotSupportedException;
  
  boolean isDocOrdered();
  
  int getAxis();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\DTMIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */