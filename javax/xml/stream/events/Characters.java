package javax.xml.stream.events;

public interface Characters extends XMLEvent {
  String getData();
  
  boolean isWhiteSpace();
  
  boolean isCData();
  
  boolean isIgnorableWhiteSpace();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\stream\events\Characters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */