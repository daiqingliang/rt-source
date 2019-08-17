package java.awt.font;

import java.awt.Font;

public interface MultipleMaster {
  int getNumDesignAxes();
  
  float[] getDesignAxisRanges();
  
  float[] getDesignAxisDefaults();
  
  String[] getDesignAxisNames();
  
  Font deriveMMFont(float[] paramArrayOfFloat);
  
  Font deriveMMFont(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\MultipleMaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */