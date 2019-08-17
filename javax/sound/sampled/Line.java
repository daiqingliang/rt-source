package javax.sound.sampled;

public interface Line extends AutoCloseable {
  Info getLineInfo();
  
  void open() throws LineUnavailableException;
  
  void close() throws LineUnavailableException;
  
  boolean isOpen();
  
  Control[] getControls();
  
  boolean isControlSupported(Control.Type paramType);
  
  Control getControl(Control.Type paramType);
  
  void addLineListener(LineListener paramLineListener);
  
  void removeLineListener(LineListener paramLineListener);
  
  public static class Info {
    private final Class lineClass;
    
    public Info(Class<?> param1Class) {
      if (param1Class == null) {
        this.lineClass = Line.class;
      } else {
        this.lineClass = param1Class;
      } 
    }
    
    public Class<?> getLineClass() { return this.lineClass; }
    
    public boolean matches(Info param1Info) { return !getClass().isInstance(param1Info) ? false : (!!getLineClass().isAssignableFrom(param1Info.getLineClass())); }
    
    public String toString() {
      String str3;
      String str1 = "javax.sound.sampled.";
      String str2 = new String(getLineClass().toString());
      int i = str2.indexOf(str1);
      if (i != -1) {
        str3 = str2.substring(0, i) + str2.substring(i + str1.length(), str2.length());
      } else {
        str3 = str2;
      } 
      return str3;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\Line.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */