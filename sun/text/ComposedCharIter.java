package sun.text;

import sun.text.normalizer.NormalizerImpl;

public final class ComposedCharIter {
  public static final int DONE = -1;
  
  private static int[] chars;
  
  private static String[] decomps;
  
  private static int decompNum;
  
  private int curChar = -1;
  
  public int next() { return (this.curChar == decompNum - 1) ? -1 : chars[++this.curChar]; }
  
  public String decomposition() { return decomps[this.curChar]; }
  
  static  {
    char c = 'ߐ';
    chars = new int[c];
    decomps = new String[c];
    decompNum = NormalizerImpl.getDecompose(chars, decomps);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\ComposedCharIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */