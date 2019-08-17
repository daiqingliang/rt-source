package javax.naming.event;

public interface NamespaceChangeListener extends NamingListener {
  void objectAdded(NamingEvent paramNamingEvent);
  
  void objectRemoved(NamingEvent paramNamingEvent);
  
  void objectRenamed(NamingEvent paramNamingEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\event\NamespaceChangeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */