package javax.xml.stream.events;

public interface EntityDeclaration extends XMLEvent {
  String getPublicId();
  
  String getSystemId();
  
  String getName();
  
  String getNotationName();
  
  String getReplacementText();
  
  String getBaseURI();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\stream\events\EntityDeclaration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */