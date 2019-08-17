package java.nio.charset;

import java.lang.ref.WeakReference;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.HashMap;
import java.util.Map;

public class CoderResult {
  private static final int CR_UNDERFLOW = 0;
  
  private static final int CR_OVERFLOW = 1;
  
  private static final int CR_ERROR_MIN = 2;
  
  private static final int CR_MALFORMED = 2;
  
  private static final int CR_UNMAPPABLE = 3;
  
  private static final String[] names = { "UNDERFLOW", "OVERFLOW", "MALFORMED", "UNMAPPABLE" };
  
  private final int type;
  
  private final int length;
  
  public static final CoderResult UNDERFLOW = new CoderResult(0, 0);
  
  public static final CoderResult OVERFLOW = new CoderResult(1, 0);
  
  private static Cache malformedCache = new Cache() {
      public CoderResult create(int param1Int) { return new CoderResult(2, param1Int, null); }
    };
  
  private static Cache unmappableCache = new Cache() {
      public CoderResult create(int param1Int) { return new CoderResult(3, param1Int, null); }
    };
  
  private CoderResult(int paramInt1, int paramInt2) {
    this.type = paramInt1;
    this.length = paramInt2;
  }
  
  public String toString() {
    String str = names[this.type];
    return isError() ? (str + "[" + this.length + "]") : str;
  }
  
  public boolean isUnderflow() { return (this.type == 0); }
  
  public boolean isOverflow() { return (this.type == 1); }
  
  public boolean isError() { return (this.type >= 2); }
  
  public boolean isMalformed() { return (this.type == 2); }
  
  public boolean isUnmappable() { return (this.type == 3); }
  
  public int length() {
    if (!isError())
      throw new UnsupportedOperationException(); 
    return this.length;
  }
  
  public static CoderResult malformedForLength(int paramInt) { return malformedCache.get(paramInt); }
  
  public static CoderResult unmappableForLength(int paramInt) { return unmappableCache.get(paramInt); }
  
  public void throwException() throws CharacterCodingException {
    switch (this.type) {
      case 0:
        throw new BufferUnderflowException();
      case 1:
        throw new BufferOverflowException();
      case 2:
        throw new MalformedInputException(this.length);
      case 3:
        throw new UnmappableCharacterException(this.length);
    } 
    assert false;
  }
  
  private static abstract class Cache {
    private Map<Integer, WeakReference<CoderResult>> cache = null;
    
    private Cache() throws CharacterCodingException {}
    
    protected abstract CoderResult create(int param1Int);
    
    private CoderResult get(int param1Int) {
      if (param1Int <= 0)
        throw new IllegalArgumentException("Non-positive length"); 
      Integer integer = new Integer(param1Int);
      CoderResult coderResult = null;
      if (this.cache == null) {
        this.cache = new HashMap();
      } else {
        WeakReference weakReference;
        if ((weakReference = (WeakReference)this.cache.get(integer)) != null)
          coderResult = (CoderResult)weakReference.get(); 
      } 
      if (coderResult == null) {
        coderResult = create(param1Int);
        this.cache.put(integer, new WeakReference(coderResult));
      } 
      return coderResult;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\charset\CoderResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */