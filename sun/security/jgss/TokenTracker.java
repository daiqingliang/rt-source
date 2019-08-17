package sun.security.jgss;

import java.util.LinkedList;
import org.ietf.jgss.MessageProp;

public class TokenTracker {
  static final int MAX_INTERVALS = 5;
  
  private int initNumber;
  
  private int windowStart;
  
  private int expectedNumber;
  
  private int windowStartIndex = 0;
  
  private LinkedList<Entry> list = new LinkedList();
  
  public TokenTracker(int paramInt) {
    this.initNumber = paramInt;
    this.windowStart = paramInt;
    this.expectedNumber = paramInt;
    Entry entry = new Entry(paramInt - 1);
    this.list.add(entry);
  }
  
  private int getIntervalIndex(int paramInt) {
    Entry entry = null;
    int i;
    for (i = this.list.size() - 1; i >= 0; i--) {
      entry = (Entry)this.list.get(i);
      if (entry.compareTo(paramInt) <= 0)
        break; 
    } 
    return i;
  }
  
  public final void getProps(int paramInt, MessageProp paramMessageProp) {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    int i = getIntervalIndex(paramInt);
    Entry entry = null;
    if (i != -1)
      entry = (Entry)this.list.get(i); 
    if (paramInt == this.expectedNumber) {
      this.expectedNumber++;
    } else if (entry != null && entry.contains(paramInt)) {
      bool4 = true;
    } else if (this.expectedNumber >= this.initNumber) {
      if (paramInt > this.expectedNumber) {
        bool1 = true;
      } else if (paramInt >= this.windowStart) {
        bool3 = true;
      } else if (paramInt >= this.initNumber) {
        bool2 = true;
      } else {
        bool1 = true;
      } 
    } else if (paramInt > this.expectedNumber) {
      if (paramInt < this.initNumber) {
        bool1 = true;
      } else if (this.windowStart >= this.initNumber) {
        if (paramInt >= this.windowStart) {
          bool3 = true;
        } else {
          bool2 = true;
        } 
      } else {
        bool2 = true;
      } 
    } else if (this.windowStart > this.expectedNumber) {
      bool3 = true;
    } else if (paramInt < this.windowStart) {
      bool2 = true;
    } else {
      bool3 = true;
    } 
    if (!bool4 && !bool2)
      add(paramInt, i); 
    if (bool1)
      this.expectedNumber = paramInt + 1; 
    paramMessageProp.setSupplementaryStates(bool4, bool2, bool3, bool1, 0, null);
  }
  
  private void add(int paramInt1, int paramInt2) {
    Entry entry1;
    Entry entry2 = null;
    Entry entry3 = null;
    boolean bool1 = false;
    boolean bool2 = false;
    if (paramInt2 != -1) {
      entry2 = (Entry)this.list.get(paramInt2);
      if (paramInt1 == entry2.getEnd() + 1) {
        entry2.setEnd(paramInt1);
        bool1 = true;
      } 
    } 
    int i = paramInt2 + 1;
    if (i < this.list.size()) {
      entry3 = (Entry)this.list.get(i);
      if (paramInt1 == entry3.getStart() - 1) {
        if (!bool1) {
          entry3.setStart(paramInt1);
        } else {
          entry3.setStart(entry2.getStart());
          this.list.remove(paramInt2);
          if (this.windowStartIndex > paramInt2)
            this.windowStartIndex--; 
        } 
        bool2 = true;
      } 
    } 
    if (bool2 || bool1)
      return; 
    if (this.list.size() < 5) {
      entry1 = new Entry(paramInt1);
      if (paramInt2 < this.windowStartIndex)
        this.windowStartIndex++; 
    } else {
      int j = this.windowStartIndex;
      if (this.windowStartIndex == this.list.size() - 1)
        this.windowStartIndex = 0; 
      entry1 = (Entry)this.list.remove(j);
      this.windowStart = ((Entry)this.list.get(this.windowStartIndex)).getStart();
      entry1.setStart(paramInt1);
      entry1.setEnd(paramInt1);
      if (paramInt2 >= j) {
        paramInt2--;
      } else if (j != this.windowStartIndex) {
        if (paramInt2 == -1)
          this.windowStart = paramInt1; 
      } else {
        this.windowStartIndex++;
      } 
    } 
    this.list.add(paramInt2 + 1, entry1);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("TokenTracker: ");
    stringBuffer.append(" initNumber=").append(this.initNumber);
    stringBuffer.append(" windowStart=").append(this.windowStart);
    stringBuffer.append(" expectedNumber=").append(this.expectedNumber);
    stringBuffer.append(" windowStartIndex=").append(this.windowStartIndex);
    stringBuffer.append("\n\tIntervals are: {");
    for (byte b = 0; b < this.list.size(); b++) {
      if (b)
        stringBuffer.append(", "); 
      stringBuffer.append(((Entry)this.list.get(b)).toString());
    } 
    stringBuffer.append('}');
    return stringBuffer.toString();
  }
  
  class Entry {
    private int start;
    
    private int end;
    
    Entry(int param1Int) {
      this.start = param1Int;
      this.end = param1Int;
    }
    
    final int compareTo(int param1Int) { return (this.start > param1Int) ? 1 : ((this.end < param1Int) ? -1 : 0); }
    
    final boolean contains(int param1Int) { return (param1Int >= this.start && param1Int <= this.end); }
    
    final void append(int param1Int) {
      if (param1Int == this.end + 1)
        this.end = param1Int; 
    }
    
    final void setInterval(int param1Int1, int param1Int2) {
      this.start = param1Int1;
      this.end = param1Int2;
    }
    
    final void setEnd(int param1Int) { this.end = param1Int; }
    
    final void setStart(int param1Int) { this.start = param1Int; }
    
    final int getStart() { return this.start; }
    
    final int getEnd() { return this.end; }
    
    public String toString() { return "[" + this.start + ", " + this.end + "]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\TokenTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */