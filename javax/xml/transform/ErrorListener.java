package javax.xml.transform;

public interface ErrorListener {
  void warning(TransformerException paramTransformerException) throws TransformerException;
  
  void error(TransformerException paramTransformerException) throws TransformerException;
  
  void fatalError(TransformerException paramTransformerException) throws TransformerException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\ErrorListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */