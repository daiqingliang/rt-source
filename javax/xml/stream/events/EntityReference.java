package javax.xml.stream.events;

public interface EntityReference extends XMLEvent {
  EntityDeclaration getDeclaration();
  
  String getName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\stream\events\EntityReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */