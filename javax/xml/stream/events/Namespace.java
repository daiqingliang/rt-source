package javax.xml.stream.events;

public interface Namespace extends Attribute {
  String getPrefix();
  
  String getNamespaceURI();
  
  boolean isDefaultNamespaceDeclaration();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\stream\events\Namespace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */