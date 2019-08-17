package sun.nio.ch;

import sun.misc.Cleaner;

public interface DirectBuffer {
  long address();
  
  Object attachment();
  
  Cleaner cleaner();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\DirectBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */