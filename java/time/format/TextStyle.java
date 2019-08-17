package java.time.format;

public static enum TextStyle {
  FULL(2, 0),
  FULL_STANDALONE(32770, 0),
  SHORT(1, 1),
  SHORT_STANDALONE(32769, 1),
  NARROW(4, 1),
  NARROW_STANDALONE(32772, 1);
  
  private final int calendarStyle;
  
  private final int zoneNameStyleIndex;
  
  TextStyle(int paramInt1, int paramInt2) {
    this.calendarStyle = paramInt1;
    this.zoneNameStyleIndex = paramInt2;
  }
  
  public boolean isStandalone() { return ((ordinal() & true) == 1); }
  
  public TextStyle asStandalone() { return values()[ordinal() | true]; }
  
  public TextStyle asNormal() { return values()[ordinal() & 0xFFFFFFFE]; }
  
  int toCalendarStyle() { return this.calendarStyle; }
  
  int zoneNameStyleIndex() { return this.zoneNameStyleIndex; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\format\TextStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */