package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class ThreadLocalCoders {
  private static final int CACHE_SIZE = 3;
  
  private static Cache decoderCache = new Cache(3) {
      boolean hasName(Object param1Object1, Object param1Object2) { return (param1Object2 instanceof String) ? ((CharsetDecoder)param1Object1).charset().name().equals(param1Object2) : ((param1Object2 instanceof Charset) ? ((CharsetDecoder)param1Object1).charset().equals(param1Object2) : 0); }
      
      Object create(Object param1Object) {
        if (param1Object instanceof String)
          return Charset.forName((String)param1Object).newDecoder(); 
        if (param1Object instanceof Charset)
          return ((Charset)param1Object).newDecoder(); 
        assert false;
        return null;
      }
    };
  
  private static Cache encoderCache = new Cache(3) {
      boolean hasName(Object param1Object1, Object param1Object2) { return (param1Object2 instanceof String) ? ((CharsetEncoder)param1Object1).charset().name().equals(param1Object2) : ((param1Object2 instanceof Charset) ? ((CharsetEncoder)param1Object1).charset().equals(param1Object2) : 0); }
      
      Object create(Object param1Object) {
        if (param1Object instanceof String)
          return Charset.forName((String)param1Object).newEncoder(); 
        if (param1Object instanceof Charset)
          return ((Charset)param1Object).newEncoder(); 
        assert false;
        return null;
      }
    };
  
  public static CharsetDecoder decoderFor(Object paramObject) {
    CharsetDecoder charsetDecoder = (CharsetDecoder)decoderCache.forName(paramObject);
    charsetDecoder.reset();
    return charsetDecoder;
  }
  
  public static CharsetEncoder encoderFor(Object paramObject) {
    CharsetEncoder charsetEncoder = (CharsetEncoder)encoderCache.forName(paramObject);
    charsetEncoder.reset();
    return charsetEncoder;
  }
  
  private static abstract class Cache {
    private ThreadLocal<Object[]> cache = new ThreadLocal();
    
    private final int size;
    
    Cache(int param1Int) { this.size = param1Int; }
    
    abstract Object create(Object param1Object);
    
    private void moveToFront(Object[] param1ArrayOfObject, int param1Int) {
      Object object = param1ArrayOfObject[param1Int];
      for (int i = param1Int; i > 0; i--)
        param1ArrayOfObject[i] = param1ArrayOfObject[i - 1]; 
      param1ArrayOfObject[0] = object;
    }
    
    abstract boolean hasName(Object param1Object1, Object param1Object2);
    
    Object forName(Object param1Object) {
      Object[] arrayOfObject = (Object[])this.cache.get();
      if (arrayOfObject == null) {
        arrayOfObject = new Object[this.size];
        this.cache.set(arrayOfObject);
      } else {
        for (byte b = 0; b < arrayOfObject.length; b++) {
          Object object1 = arrayOfObject[b];
          if (object1 != null && hasName(object1, param1Object)) {
            if (b)
              moveToFront(arrayOfObject, b); 
            return object1;
          } 
        } 
      } 
      Object object = create(param1Object);
      arrayOfObject[arrayOfObject.length - 1] = object;
      moveToFront(arrayOfObject, arrayOfObject.length - 1);
      return object;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\ThreadLocalCoders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */