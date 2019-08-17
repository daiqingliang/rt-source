package javax.swing.text;

import java.io.Serializable;
import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public final class StringContent implements AbstractDocument.Content, Serializable {
  private static final char[] empty = new char[0];
  
  private char[] data;
  
  private int count;
  
  Vector<PosRec> marks;
  
  public StringContent() { this(10); }
  
  public StringContent(int paramInt) {
    if (paramInt < 1)
      paramInt = 1; 
    this.data = new char[paramInt];
    this.data[0] = '\n';
    this.count = 1;
  }
  
  public int length() { return this.count; }
  
  public UndoableEdit insertString(int paramInt, String paramString) throws BadLocationException {
    if (paramInt >= this.count || paramInt < 0)
      throw new BadLocationException("Invalid location", this.count); 
    char[] arrayOfChar = paramString.toCharArray();
    replace(paramInt, 0, arrayOfChar, 0, arrayOfChar.length);
    if (this.marks != null)
      updateMarksForInsert(paramInt, paramString.length()); 
    return new InsertUndo(paramInt, paramString.length());
  }
  
  public UndoableEdit remove(int paramInt1, int paramInt2) throws BadLocationException {
    if (paramInt1 + paramInt2 >= this.count)
      throw new BadLocationException("Invalid range", this.count); 
    String str = getString(paramInt1, paramInt2);
    RemoveUndo removeUndo = new RemoveUndo(paramInt1, str);
    replace(paramInt1, paramInt2, empty, 0, 0);
    if (this.marks != null)
      updateMarksForRemove(paramInt1, paramInt2); 
    return removeUndo;
  }
  
  public String getString(int paramInt1, int paramInt2) throws BadLocationException {
    if (paramInt1 + paramInt2 > this.count)
      throw new BadLocationException("Invalid range", this.count); 
    return new String(this.data, paramInt1, paramInt2);
  }
  
  public void getChars(int paramInt1, int paramInt2, Segment paramSegment) throws BadLocationException {
    if (paramInt1 + paramInt2 > this.count)
      throw new BadLocationException("Invalid location", this.count); 
    paramSegment.array = this.data;
    paramSegment.offset = paramInt1;
    paramSegment.count = paramInt2;
  }
  
  public Position createPosition(int paramInt) throws BadLocationException {
    if (this.marks == null)
      this.marks = new Vector(); 
    return new StickyPosition(paramInt);
  }
  
  void replace(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, int paramInt4) {
    int i = paramInt4 - paramInt2;
    int j = paramInt1 + paramInt2;
    int k = this.count - j;
    int m = j + i;
    if (this.count + i >= this.data.length) {
      int n = Math.max(2 * this.data.length, this.count + i);
      char[] arrayOfChar = new char[n];
      System.arraycopy(this.data, 0, arrayOfChar, 0, paramInt1);
      System.arraycopy(paramArrayOfChar, paramInt3, arrayOfChar, paramInt1, paramInt4);
      System.arraycopy(this.data, j, arrayOfChar, m, k);
      this.data = arrayOfChar;
    } else {
      System.arraycopy(this.data, j, this.data, m, k);
      System.arraycopy(paramArrayOfChar, paramInt3, this.data, paramInt1, paramInt4);
    } 
    this.count += i;
  }
  
  void resize(int paramInt) {
    char[] arrayOfChar = new char[paramInt];
    System.arraycopy(this.data, 0, arrayOfChar, 0, Math.min(paramInt, this.count));
    this.data = arrayOfChar;
  }
  
  void updateMarksForInsert(int paramInt1, int paramInt2) {
    if (paramInt1 == 0)
      paramInt1 = 1; 
    int i = this.marks.size();
    for (byte b = 0; b < i; b++) {
      PosRec posRec = (PosRec)this.marks.elementAt(b);
      if (posRec.unused) {
        this.marks.removeElementAt(b);
        b--;
        i--;
      } else if (posRec.offset >= paramInt1) {
        posRec.offset += paramInt2;
      } 
    } 
  }
  
  void updateMarksForRemove(int paramInt1, int paramInt2) {
    int i = this.marks.size();
    for (byte b = 0; b < i; b++) {
      PosRec posRec = (PosRec)this.marks.elementAt(b);
      if (posRec.unused) {
        this.marks.removeElementAt(b);
        b--;
        i--;
      } else if (posRec.offset >= paramInt1 + paramInt2) {
        posRec.offset -= paramInt2;
      } else if (posRec.offset >= paramInt1) {
        posRec.offset = paramInt1;
      } 
    } 
  }
  
  protected Vector getPositionsInRange(Vector paramVector, int paramInt1, int paramInt2) {
    int i = this.marks.size();
    int j = paramInt1 + paramInt2;
    Vector vector = (paramVector == null) ? new Vector() : paramVector;
    for (byte b = 0; b < i; b++) {
      PosRec posRec = (PosRec)this.marks.elementAt(b);
      if (posRec.unused) {
        this.marks.removeElementAt(b);
        b--;
        i--;
      } else if (posRec.offset >= paramInt1 && posRec.offset <= j) {
        vector.addElement(new UndoPosRef(posRec));
      } 
    } 
    return vector;
  }
  
  protected void updateUndoPositions(Vector paramVector) {
    for (int i = paramVector.size() - 1; i >= 0; i--) {
      UndoPosRef undoPosRef = (UndoPosRef)paramVector.elementAt(i);
      if (undoPosRef.rec.unused) {
        paramVector.removeElementAt(i);
      } else {
        undoPosRef.resetLocation();
      } 
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
        synchronized (StringContent.this) {
          if (StringContent.this.marks != null)
            this.posRefs = StringContent.this.getPositionsInRange(null, this.offset, this.length); 
          this.string = StringContent.this.getString(this.offset, this.length);
          StringContent.this.remove(this.offset, this.length);
        } 
      } catch (BadLocationException badLocationException) {
        throw new CannotUndoException();
      } 
    }
    
    public void redo() {
      super.redo();
      try {
        synchronized (StringContent.this) {
          StringContent.this.insertString(this.offset, this.string);
          this.string = null;
          if (this.posRefs != null) {
            StringContent.this.updateUndoPositions(this.posRefs);
            this.posRefs = null;
          } 
        } 
      } catch (BadLocationException badLocationException) {
        throw new CannotRedoException();
      } 
    }
  }
  
  final class PosRec {
    int offset;
    
    boolean unused;
    
    PosRec(int param1Int) { this.offset = param1Int; }
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
      if (StringContent.this.marks != null)
        this.posRefs = this$0.getPositionsInRange(null, param1Int, this.length); 
    }
    
    public void undo() {
      super.undo();
      try {
        synchronized (StringContent.this) {
          StringContent.this.insertString(this.offset, this.string);
          if (this.posRefs != null) {
            StringContent.this.updateUndoPositions(this.posRefs);
            this.posRefs = null;
          } 
          this.string = null;
        } 
      } catch (BadLocationException badLocationException) {
        throw new CannotUndoException();
      } 
    }
    
    public void redo() {
      super.redo();
      try {
        synchronized (StringContent.this) {
          this.string = StringContent.this.getString(this.offset, this.length);
          if (StringContent.this.marks != null)
            this.posRefs = StringContent.this.getPositionsInRange(null, this.offset, this.length); 
          StringContent.this.remove(this.offset, this.length);
        } 
      } catch (BadLocationException badLocationException) {
        throw new CannotRedoException();
      } 
    }
  }
  
  final class StickyPosition implements Position {
    StringContent.PosRec rec;
    
    StickyPosition(int param1Int) {
      this.rec = new StringContent.PosRec(this$0, param1Int);
      StringContent.this.marks.addElement(this.rec);
    }
    
    public int getOffset() { return this.rec.offset; }
    
    protected void finalize() { this.rec.unused = true; }
    
    public String toString() { return Integer.toString(getOffset()); }
  }
  
  final class UndoPosRef {
    protected int undoLocation;
    
    protected StringContent.PosRec rec;
    
    UndoPosRef(StringContent.PosRec param1PosRec) {
      this.rec = param1PosRec;
      this.undoLocation = param1PosRec.offset;
    }
    
    protected void resetLocation() { this.rec.offset = this.undoLocation; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\StringContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */