package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public interface Component {
  @Nullable
  <S> S getSPI(@NotNull Class<S> paramClass);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\Component.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */