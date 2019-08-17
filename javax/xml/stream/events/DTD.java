package javax.xml.stream.events;

import java.util.List;

public interface DTD extends XMLEvent {
  String getDocumentTypeDeclaration();
  
  Object getProcessedDTD();
  
  List getNotations();
  
  List getEntities();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\stream\events\DTD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */