package com.sun.xml.internal.ws.api.server;

import java.util.concurrent.Executor;

public class ThreadLocalContainerResolver extends ContainerResolver {
  private ThreadLocal<Container> containerThreadLocal = new ThreadLocal<Container>() {
      protected Container initialValue() { return Container.NONE; }
    };
  
  public Container getContainer() { return (Container)this.containerThreadLocal.get(); }
  
  public Container enterContainer(Container paramContainer) {
    Container container = (Container)this.containerThreadLocal.get();
    this.containerThreadLocal.set(paramContainer);
    return container;
  }
  
  public void exitContainer(Container paramContainer) { this.containerThreadLocal.set(paramContainer); }
  
  public Executor wrapExecutor(final Container container, final Executor ex) { return (paramExecutor == null) ? null : new Executor() {
        public void execute(final Runnable command) { ex.execute(new Runnable() {
                public void run() {
                  container = ThreadLocalContainerResolver.null.this.this$0.enterContainer(container);
                  try {
                    command.run();
                  } finally {
                    ThreadLocalContainerResolver.null.this.this$0.exitContainer(container);
                  } 
                }
              }); }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\ThreadLocalContainerResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */