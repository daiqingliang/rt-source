package javax.sound.sampled;

public interface Port extends Line {
  public static class Info extends Line.Info {
    public static final Info MICROPHONE = new Info(Port.class, "MICROPHONE", true);
    
    public static final Info LINE_IN = new Info(Port.class, "LINE_IN", true);
    
    public static final Info COMPACT_DISC = new Info(Port.class, "COMPACT_DISC", true);
    
    public static final Info SPEAKER = new Info(Port.class, "SPEAKER", false);
    
    public static final Info HEADPHONE = new Info(Port.class, "HEADPHONE", false);
    
    public static final Info LINE_OUT = new Info(Port.class, "LINE_OUT", false);
    
    private String name;
    
    private boolean isSource;
    
    public Info(Class<?> param1Class, String param1String, boolean param1Boolean) {
      super(param1Class);
      this.name = param1String;
      this.isSource = param1Boolean;
    }
    
    public String getName() { return this.name; }
    
    public boolean isSource() { return this.isSource; }
    
    public boolean matches(Line.Info param1Info) { return !super.matches(param1Info) ? false : (!this.name.equals(((Info)param1Info).getName()) ? false : (!(this.isSource != ((Info)param1Info).isSource()))); }
    
    public final boolean equals(Object param1Object) { return super.equals(param1Object); }
    
    public final int hashCode() { return super.hashCode(); }
    
    public final String toString() { return this.name + ((this.isSource == true) ? " source" : " target") + " port"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\Port.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */