package java.nio.file;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public interface DirectoryStream<T> extends Closeable, Iterable<T> {
  Iterator<T> iterator();
  
  @FunctionalInterface
  public static interface Filter<T> {
    boolean accept(T param1T) throws IOException;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\DirectoryStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */