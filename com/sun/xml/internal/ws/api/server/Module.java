package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Component;
import java.util.List;

public abstract class Module implements Component {
  @NotNull
  public abstract List<BoundEndpoint> getBoundEndpoints();
  
  @Nullable
  public <S> S getSPI(@NotNull Class<S> paramClass) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\Module.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */