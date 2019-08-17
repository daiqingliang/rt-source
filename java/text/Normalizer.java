package java.text;

import sun.text.normalizer.NormalizerBase;

public final class Normalizer {
  public static String normalize(CharSequence paramCharSequence, Form paramForm) { return NormalizerBase.normalize(paramCharSequence.toString(), paramForm); }
  
  public static boolean isNormalized(CharSequence paramCharSequence, Form paramForm) { return NormalizerBase.isNormalized(paramCharSequence.toString(), paramForm); }
  
  public enum Form {
    NFD, NFC, NFKD, NFKC;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\Normalizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */