package javax.swing.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class GapContent extends GapVector implements AbstractDocument.Content, Serializable {
  private static final char[] empty = new char[0];
  
  private MarkVector marks;
  
  private MarkData search;
  
  private int unusedMarks = 0;
  
  private ReferenceQueue<StickyPosition> queue;
  
  static final int GROWTH_SIZE = 524288;
  
  public GapContent() { this(10); }
  
  public GapContent(int paramInt) {
    super(Math.max(paramInt, 2));
    char[] arrayOfChar = new char[1];
    arrayOfChar[0] = '\n';
    replace(0, 0, arrayOfChar, arrayOfChar.length);
    this.marks = new MarkVector();
    this.search = new MarkData(0);
    this.queue = new ReferenceQueue();
  }
  
  protected Object allocateArray(int paramInt) { return new char[paramInt]; }
  
  protected int getArrayLength() {
    char[] arrayOfChar = (char[])getArray();
    return arrayOfChar.length;
  }
  
  public int length() { return getArrayLength() - getGapEnd() - getGapStart(); }
  
  public UndoableEdit insertString(int paramInt, String paramString) throws BadLocationException {
    if (paramInt > length() || paramInt < 0)
      throw new BadLocationException("Invalid insert", length()); 
    char[] arrayOfChar = paramString.toCharArray();
    replace(paramInt, 0, arrayOfChar, arrayOfChar.length);
    return new InsertUndo(paramInt, paramString.length());
  }
  
  public UndoableEdit remove(int paramInt1, int paramInt2) throws BadLocationException {
    if (paramInt1 + paramInt2 >= length())
      throw new BadLocationException("Invalid remove", length() + 1); 
    String str = getString(paramInt1, paramInt2);
    RemoveUndo removeUndo = new RemoveUndo(paramInt1, str);
    replace(paramInt1, paramInt2, empty, 0);
    return removeUndo;
  }
  
  public String getString(int paramInt1, int paramInt2) throws BadLocationException {
    Segment segment = new Segment();
    getChars(paramInt1, paramInt2, segment);
    return new String(segment.array, segment.offset, segment.count);
  }
  
  public void getChars(int paramInt1, int paramInt2, Segment paramSegment) throws BadLocationException {
    int i = paramInt1 + paramInt2;
    if (paramInt1 < 0 || i < 0)
      throw new BadLocationException("Invalid location", -1); 
    if (i > length() || paramInt1 > length())
      throw new BadLocationException("Invalid location", length() + 1); 
    int j = getGapStart();
    int k = getGapEnd();
    char[] arrayOfChar = (char[])getArray();
    if (paramInt1 + paramInt2 <= j) {
      paramSegment.array = arrayOfChar;
      paramSegment.offset = paramInt1;
    } else if (paramInt1 >= j) {
      paramSegment.array = arrayOfChar;
      paramSegment.offset = k + paramInt1 - j;
    } else {
      int m = j - paramInt1;
      if (paramSegment.isPartialReturn()) {
        paramSegment.array = arrayOfChar;
        paramSegment.offset = paramInt1;
        paramSegment.count = m;
        return;
      } 
      paramSegment.array = new char[paramInt2];
      paramSegment.offset = 0;
      System.arraycopy(arrayOfChar, paramInt1, paramSegment.array, 0, m);
      System.arraycopy(arrayOfChar, k, paramSegment.array, m, paramInt2 - m);
    } 
    paramSegment.count = paramInt2;
  }
  
  public Position createPosition(int paramInt) throws BadLocationException {
    while (this.queue.poll() != null)
      this.unusedMarks++; 
    if (this.unusedMarks > Math.max(5, this.marks.size() / 10))
      removeUnusedMarks(); 
    int i = getGapStart();
    int j = getGapEnd();
    int k = (paramInt < i) ? paramInt : (paramInt + j - i);
    this.search.index = k;
    int m = findSortIndex(this.search);
    MarkData markData;
    StickyPosition stickyPosition;
    if (m >= this.marks.size() || (markData = this.marks.elementAt(m)).index != k || (stickyPosition = markData.getPosition()) == null) {
      stickyPosition = new StickyPosition();
      markData = new MarkData(k, stickyPosition, this.queue);
      stickyPosition.setMark(markData);
      this.marks.insertElementAt(markData, m);
    } 
    return stickyPosition;
  }
  
  protected void shiftEnd(int paramInt) {
    int i = getGapEnd();
    super.shiftEnd(paramInt);
    int j = getGapEnd() - i;
    int k = findMarkAdjustIndex(i);
    int m = this.marks.size();
    for (int n = k; n < m; n++) {
      MarkData markData = this.marks.elementAt(n);
      markData.index += j;
    } 
  }
  
  int getNewArraySize(int paramInt) { return (paramInt < 524288) ? super.getNewArraySize(paramInt) : (paramInt + 524288); }
  
  protected void shiftGap(int paramInt) {
    int i = getGapStart();
    int j = paramInt - i;
    int k = getGapEnd();
    int m = k + j;
    int n = k - i;
    super.shiftGap(paramInt);
    if (j > 0) {
      int i1 = findMarkAdjustIndex(i);
      int i2 = this.marks.size();
      for (int i3 = i1; i3 < i2; i3++) {
        MarkData markData = this.marks.elementAt(i3);
        if (markData.index >= m)
          break; 
        markData.index -= n;
      } 
    } else if (j < 0) {
      int i1 = findMarkAdjustIndex(paramInt);
      int i2 = this.marks.size();
      for (int i3 = i1; i3 < i2; i3++) {
        MarkData markData = this.marks.elementAt(i3);
        if (markData.index >= k)
          break; 
        markData.index += n;
      } 
    } 
    resetMarksAtZero();
  }
  
  protected void resetMarksAtZero() {
    if (this.marks != null && getGapStart() == 0) {
      int i = getGapEnd();
      byte b = 0;
      int j = this.marks.size();
      while (b < j) {
        MarkData markData = this.marks.elementAt(b);
        if (markData.index <= i) {
          markData.index = 0;
          b++;
        } 
      } 
    } 
  }
  
  protected void shiftGapStartDown(int paramInt) {
    int i = findMarkAdjustIndex(paramInt);
    int j = this.marks.size();
    int k = getGapStart();
    int m = getGapEnd();
    for (int n = i; n < j; n++) {
      MarkData markData = this.marks.elementAt(n);
      if (markData.index > k)
        break; 
      markData.index = m;
    } 
    super.shiftGapStartDown(paramInt);
    resetMarksAtZero();
  }
  
  protected void shiftGapEndUp(int paramInt) {
    int i = findMarkAdjustIndex(getGapEnd());
    int j = this.marks.size();
    for (int k = i; k < j; k++) {
      MarkData markData = this.marks.elementAt(k);
      if (markData.index >= paramInt)
        break; 
      markData.index = paramInt;
    } 
    super.shiftGapEndUp(paramInt);
    resetMarksAtZero();
  }
  
  final int compare(MarkData paramMarkData1, MarkData paramMarkData2) { return (paramMarkData1.index < paramMarkData2.index) ? -1 : ((paramMarkData1.index > paramMarkData2.index) ? 1 : 0); }
  
  final int findMarkAdjustIndex(int paramInt) {
    this.search.index = Math.max(paramInt, 1);
    int i = findSortIndex(this.search);
    for (int j = i - 1; j >= 0; j--) {
      MarkData markData = this.marks.elementAt(j);
      if (markData.index != this.search.index)
        break; 
      i--;
    } 
    return i;
  }
  
  final int findSortIndex(MarkData paramMarkData) {
    int i = 0;
    int j = this.marks.size() - 1;
    int k = 0;
    if (j == -1)
      return 0; 
    MarkData markData = this.marks.elementAt(j);
    int m = compare(paramMarkData, markData);
    if (m > 0)
      return j + 1; 
    while (i <= j) {
      k = i + (j - i) / 2;
      MarkData markData1 = this.marks.elementAt(k);
      m = compare(paramMarkData, markData1);
      if (m == 0)
        return k; 
      if (m < 0) {
        j = k - 1;
        continue;
      } 
      i = k + 1;
    } 
    return (m < 0) ? k : (k + 1);
  }
  
  final void removeUnusedMarks() {
    int i = this.marks.size();
    MarkVector markVector = new MarkVector(i);
    for (byte b = 0; b < i; b++) {
      MarkData markData = this.marks.elementAt(b);
      if (markData.get() != null)
        markVector.addElement(markData); 
    } 
    this.marks = markVector;
    this.unusedMarks = 0;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    this.marks = new MarkVector();
    this.search = new MarkData(0);
    this.queue = new ReferenceQueue();
  }
  
  protected Vector getPositionsInRange(Vector paramVector, int paramInt1, int paramInt2) {
    int k;
    int j;
    int i = paramInt1 + paramInt2;
    int m = getGapStart();
    int n = getGapEnd();
    if (paramInt1 < m) {
      if (paramInt1 == 0) {
        j = 0;
      } else {
        j = findMarkAdjustIndex(paramInt1);
      } 
      if (i >= m) {
        k = findMarkAdjustIndex(i + n - m + 1);
      } else {
        k = findMarkAdjustIndex(i + 1);
      } 
    } else {
      j = findMarkAdjustIndex(paramInt1 + n - m);
      k = findMarkAdjustIndex(i + n - m + 1);
    } 
    Vector vector = (paramVector == null) ? new Vector(Math.max(1, k - j)) : paramVector;
    for (int i1 = j; i1 < k; i1++)
      vector.addElement(new UndoPosRef(this.marks.elementAt(i1))); 
    return vector;
  }
  
  protected void updateUndoPositions(Vector paramVector, int paramInt1, int paramInt2) {
    int k;
    int i = paramInt1 + paramInt2;
    int j = getGapEnd();
    int m = findMarkAdjustIndex(j + 1);
    if (paramInt1 != 0) {
      k = findMarkAdjustIndex(j);
    } else {
      k = 0;
    } 
    for (int n = paramVector.size() - 1; n >= 0; n--) {
      UndoPosRef undoPosRef = (UndoPosRef)paramVector.elementAt(n);
      undoPosRef.resetLocation(i, j);
    } 
    if (k < m) {
      Object[] arrayOfObject = new Object[m - k];
      byte b = 0;
      if (paramInt1 == 0) {
        int i1;
        for (i1 = k; i1 < m; i1++) {
          MarkData markData = this.marks.elementAt(i1);
          if (markData.index == 0)
            arrayOfObject[b++] = markData; 
        } 
        for (i1 = k; i1 < m; i1++) {
          MarkData markData = this.marks.elementAt(i1);
          if (markData.index != 0)
            arrayOfObject[b++] = markData; 
        } 
      } else {
        int i1;
        for (i1 = k; i1 < m; i1++) {
          MarkData markData = this.marks.elementAt(i1);
          if (markData.index != j)
            arrayOfObject[b++] = markData; 
        } 
        for (i1 = k; i1 < m; i1++) {
          MarkData markData = this.marks.elementAt(i1);
          if (markData.index == j)
            arrayOfObject[b++] = markData; 
        } 
      } 
      this.marks.replaceRange(k, m, arrayOfObject);
    } 
  }
  
  class InsertUndo extends AbstractUndoableEdit {
    protected int offset;
    
    protected int length;
    
    protected String string;
    
    protected Vector posRefs;
    
    protected InsertUndo(int param1Int1, int param1Int2) {
      this.offset = param1Int1;
      this.length = param1Int2;
    }
    
    public void undo() {
      super.undo();
      try {
        this.posRefs = GapContent.this.getPositionsInRange(null, this.offset, this.length);
        this.string = GapContent.this.getString(this.offset, this.length);
        GapContent.this.remove(this.offset, this.length);
      } catch (BadLocationException badLocationException) {
        throw new CannotUndoException();
      } 
    }
    
    public void redo() {
      super.redo();
      try {
        GapContent.this.insertString(this.offset, this.string);
        this.string = null;
        if (this.posRefs != null) {
          GapContent.this.updateUndoPositions(this.posRefs, this.offset, this.length);
          this.posRefs = null;
        } 
      } catch (BadLocationException badLocationException) {
        throw new CannotRedoException();
      } 
    }
  }
  
  final class MarkData extends WeakReference<StickyPosition> {
    int index;
    
    MarkData(int param1Int) {
      super(null);
      this.index = param1Int;
    }
    
    MarkData(int param1Int, GapContent.StickyPosition param1StickyPosition, ReferenceQueue<? super GapContent.StickyPosition> param1ReferenceQueue) {
      super(param1StickyPosition, param1ReferenceQueue);
      this.index = param1Int;
    }
    
    public final int getOffset() {
      int i = GapContent.this.getGapStart();
      int j = GapContent.this.getGapEnd();
      int k = (this.index < i) ? this.index : (this.index - j - i);
      return Math.max(k, 0);
    }
    
    GapContent.StickyPosition getPosition() { return (GapContent.StickyPosition)get(); }
  }
  
  static class MarkVector extends GapVector {
    GapContent.MarkData[] oneMark = new GapContent.MarkData[1];
    
    MarkVector() {}
    
    MarkVector(int param1Int) { super(param1Int); }
    
    protected Object allocateArray(int param1Int) { return new GapContent.MarkData[param1Int]; }
    
    protected int getArrayLength() {
      MarkData[] arrayOfMarkData = (MarkData[])getArray();
      return arrayOfMarkData.length;
    }
    
    public int size() { return getArrayLength() - getGapEnd() - getGapStart(); }
    
    public void insertElementAt(GapContent.MarkData param1MarkData, int param1Int) {
      this.oneMark[0] = param1MarkData;
      replace(param1Int, 0, this.oneMark, 1);
    }
    
    public void addElement(GapContent.MarkData param1MarkData) { insertElementAt(param1MarkData, size()); }
    
    public GapContent.MarkData elementAt(int param1Int) {
      int i = getGapStart();
      int j = getGapEnd();
      MarkData[] arrayOfMarkData = (MarkData[])getArray();
      if (param1Int < i)
        return arrayOfMarkData[param1Int]; 
      param1Int += j - i;
      return arrayOfMarkData[param1Int];
    }
    
    protected void replaceRange(int param1Int1, int param1Int2, Object[] param1ArrayOfObject) {
      int i = getGapStart();
      int j = getGapEnd();
      int k = param1Int1;
      byte b = 0;
      Object[] arrayOfObject = (Object[])getArray();
      if (param1Int1 >= i) {
        k += j - i;
        param1Int2 += j - i;
      } else if (param1Int2 >= i) {
        param1Int2 += j - i;
        while (k < i)
          arrayOfObject[k++] = param1ArrayOfObject[b++]; 
        k = j;
      } else {
        while (k < param1Int2)
          arrayOfObject[k++] = param1ArrayOfObject[b++]; 
      } 
      while (k < param1Int2)
        arrayOfObject[k++] = param1ArrayOfObject[b++]; 
    }
  }
  
  class RemoveUndo extends AbstractUndoableEdit {
    protected int offset;
    
    protected int length;
    
    protected String string;
    
    protected Vector posRefs;
    
    protected RemoveUndo(int param1Int, String param1String) {
      this.offset = param1Int;
      this.string = param1String;
      this.length = param1String.length();
      this.posRefs = this$0.getPositionsInRange(null, param1Int, this.length);
    }
    
    public void undo() {
      super.undo();
      try {
        GapContent.this.insertString(this.offset, this.string);
        if (this.posRefs != null) {
          GapContent.this.updateUndoPositions(this.posRefs, this.offset, this.length);
          this.posRefs = null;
        } 
        this.string = null;
      } catch (BadLocationException badLocationException) {
        throw new CannotUndoException();
      } 
    }
    
    public void redo() {
      super.redo();
      try {
        this.string = GapContent.this.getString(this.offset, this.length);
        this.posRefs = GapContent.this.getPositionsInRange(null, this.offset, this.length);
        GapContent.this.remove(this.offset, this.length);
      } catch (BadLocationException badLocationException) {
        throw new CannotRedoException();
      } 
    }
  }
  
  final class StickyPosition implements Position {
    GapContent.MarkData mark;
    
    void setMark(GapContent.MarkData param1MarkData) { this.mark = param1MarkData; }
    
    public final int getOffset() { return this.mark.getOffset(); }
    
    public String toString() { return Integer.toString(getOffset()); }
  }
  
  final class UndoPosRef {
    protected int undoLocation;
    
    protected GapContent.MarkData rec;
    
    UndoPosRef(GapContent.MarkData param1MarkData) {
      this.rec = param1MarkData;
      this.undoLocation = param1MarkData.getOffset();
    }
    
    protected void resetLocation(int param1Int1, int param1Int2) {
      if (this.undoLocation != param1Int1) {
        this.rec.index = this.undoLocation;
      } else {
        this.rec.index = param1Int2;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\GapContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */