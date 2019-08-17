package javax.xml.bind.helpers;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import javax.xml.bind.ValidationEventLocator;
import org.w3c.dom.Node;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public class ValidationEventLocatorImpl implements ValidationEventLocator {
  private URL url = null;
  
  private int offset = -1;
  
  private int lineNumber = -1;
  
  private int columnNumber = -1;
  
  private Object object = null;
  
  private Node node = null;
  
  public ValidationEventLocatorImpl() {}
  
  public ValidationEventLocatorImpl(Locator paramLocator) {
    if (paramLocator == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "loc")); 
    this.url = toURL(paramLocator.getSystemId());
    this.columnNumber = paramLocator.getColumnNumber();
    this.lineNumber = paramLocator.getLineNumber();
  }
  
  public ValidationEventLocatorImpl(SAXParseException paramSAXParseException) {
    if (paramSAXParseException == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "e")); 
    this.url = toURL(paramSAXParseException.getSystemId());
    this.columnNumber = paramSAXParseException.getColumnNumber();
    this.lineNumber = paramSAXParseException.getLineNumber();
  }
  
  public ValidationEventLocatorImpl(Node paramNode) {
    if (paramNode == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "_node")); 
    this.node = paramNode;
  }
  
  public ValidationEventLocatorImpl(Object paramObject) {
    if (paramObject == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "_object")); 
    this.object = paramObject;
  }
  
  private static URL toURL(String paramString) {
    try {
      return new URL(paramString);
    } catch (MalformedURLException malformedURLException) {
      return null;
    } 
  }
  
  public URL getURL() { return this.url; }
  
  public void setURL(URL paramURL) { this.url = paramURL; }
  
  public int getOffset() { return this.offset; }
  
  public void setOffset(int paramInt) { this.offset = paramInt; }
  
  public int getLineNumber() { return this.lineNumber; }
  
  public void setLineNumber(int paramInt) { this.lineNumber = paramInt; }
  
  public int getColumnNumber() { return this.columnNumber; }
  
  public void setColumnNumber(int paramInt) { this.columnNumber = paramInt; }
  
  public Object getObject() { return this.object; }
  
  public void setObject(Object paramObject) { this.object = paramObject; }
  
  public Node getNode() { return this.node; }
  
  public void setNode(Node paramNode) { this.node = paramNode; }
  
  public String toString() { return MessageFormat.format("[node={0},object={1},url={2},line={3},col={4},offset={5}]", new Object[] { getNode(), getObject(), getURL(), String.valueOf(getLineNumber()), String.valueOf(getColumnNumber()), String.valueOf(getOffset()) }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\helpers\ValidationEventLocatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */