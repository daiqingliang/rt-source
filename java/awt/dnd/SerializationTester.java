package java.awt.dnd;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

final class SerializationTester {
  private static ObjectOutputStream stream;
  
  static boolean test(Object paramObject) {
    if (!(paramObject instanceof java.io.Serializable))
      return false; 
    try {
      stream.writeObject(paramObject);
    } catch (IOException iOException) {
      return false;
    } finally {
      try {
        stream.reset();
      } catch (IOException iOException) {}
    } 
    return true;
  }
  
  static  {
    try {
      stream = new ObjectOutputStream(new OutputStream() {
            public void write(int param1Int) {}
          });
    } catch (IOException iOException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\SerializationTester.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */