package javax.lang.model.element;

public static enum NestingKind {
  TOP_LEVEL, MEMBER, LOCAL, ANONYMOUS;
  
  public boolean isNested() { return (this != TOP_LEVEL); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\element\NestingKind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */