package javax.xml.xpath;

import java.util.List;

public interface XPathFunction {
  Object evaluate(List paramList) throws XPathFunctionException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\xpath\XPathFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */