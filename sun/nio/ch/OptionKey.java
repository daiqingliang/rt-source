package sun.nio.ch;

class OptionKey {
  private int level;
  
  private int name;
  
  OptionKey(int paramInt1, int paramInt2) {
    this.level = paramInt1;
    this.name = paramInt2;
  }
  
  int level() { return this.level; }
  
  int name() { return this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\OptionKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */