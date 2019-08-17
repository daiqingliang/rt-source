package sun.text;

import sun.text.normalizer.NormalizerBase;

public class CollatorUtilities {
  static NormalizerBase.Mode[] legacyModeMap = { NormalizerBase.NONE, NormalizerBase.NFD, NormalizerBase.NFKD };
  
  public static int toLegacyMode(NormalizerBase.Mode paramMode) {
    int i = legacyModeMap.length;
    while (i > 0) {
      if (legacyModeMap[--i] == paramMode)
        break; 
    } 
    return i;
  }
  
  public static NormalizerBase.Mode toNormalizerMode(int paramInt) {
    NormalizerBase.Mode mode;
    try {
      mode = legacyModeMap[paramInt];
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      mode = NormalizerBase.NONE;
    } 
    return mode;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\CollatorUtilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */