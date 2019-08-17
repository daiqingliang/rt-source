package java.time.format;

public static enum SignStyle {
  NORMAL, ALWAYS, NEVER, NOT_NEGATIVE, EXCEEDS_PAD;
  
  boolean parse(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    switch (ordinal()) {
      case 0:
        return (!paramBoolean1 || !paramBoolean2);
      case 1:
      case 4:
        return true;
    } 
    return (!paramBoolean2 && !paramBoolean3);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\format\SignStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */