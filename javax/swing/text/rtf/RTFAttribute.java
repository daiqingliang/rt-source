package javax.swing.text.rtf;

import java.io.IOException;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

interface RTFAttribute {
  public static final int D_CHARACTER = 0;
  
  public static final int D_PARAGRAPH = 1;
  
  public static final int D_SECTION = 2;
  
  public static final int D_DOCUMENT = 3;
  
  public static final int D_META = 4;
  
  int domain();
  
  Object swingName();
  
  String rtfName();
  
  boolean set(MutableAttributeSet paramMutableAttributeSet);
  
  boolean set(MutableAttributeSet paramMutableAttributeSet, int paramInt);
  
  boolean setDefault(MutableAttributeSet paramMutableAttributeSet);
  
  boolean write(AttributeSet paramAttributeSet, RTFGenerator paramRTFGenerator, boolean paramBoolean) throws IOException;
  
  boolean writeValue(Object paramObject, RTFGenerator paramRTFGenerator, boolean paramBoolean) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\rtf\RTFAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */