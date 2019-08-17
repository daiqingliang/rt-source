package java.lang;

import java.io.IOException;

public interface Appendable {
  Appendable append(CharSequence paramCharSequence) throws IOException;
  
  Appendable append(CharSequence paramCharSequence, int paramInt1, int paramInt2) throws IOException;
  
  Appendable append(char paramChar) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Appendable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */