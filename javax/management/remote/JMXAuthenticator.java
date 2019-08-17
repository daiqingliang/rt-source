package javax.management.remote;

import javax.security.auth.Subject;

public interface JMXAuthenticator {
  Subject authenticate(Object paramObject);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\JMXAuthenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */