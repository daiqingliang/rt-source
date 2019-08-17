package javax.print;

import java.net.URI;

public interface URIException {
  public static final int URIInaccessible = 1;
  
  public static final int URISchemeNotSupported = 2;
  
  public static final int URIOtherProblem = -1;
  
  URI getUnsupportedURI();
  
  int getReason();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\URIException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */