package javax.sql.rowset.spi;

import java.io.Writer;
import java.sql.SQLException;
import javax.sql.RowSetWriter;
import javax.sql.rowset.WebRowSet;

public interface XmlWriter extends RowSetWriter {
  void writeXML(WebRowSet paramWebRowSet, Writer paramWriter) throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\spi\XmlWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */