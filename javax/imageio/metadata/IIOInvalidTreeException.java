package javax.imageio.metadata;

import javax.imageio.IIOException;
import org.w3c.dom.Node;

public class IIOInvalidTreeException extends IIOException {
  protected Node offendingNode = null;
  
  public IIOInvalidTreeException(String paramString, Node paramNode) {
    super(paramString);
    this.offendingNode = paramNode;
  }
  
  public IIOInvalidTreeException(String paramString, Throwable paramThrowable, Node paramNode) {
    super(paramString, paramThrowable);
    this.offendingNode = paramNode;
  }
  
  public Node getOffendingNode() { return this.offendingNode; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\metadata\IIOInvalidTreeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */