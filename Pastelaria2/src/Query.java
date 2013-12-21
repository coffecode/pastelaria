import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Query
{
    private Connection con = null;
    private PreparedStatement st = null;
    private ResultSet resultado = null;
	
	public void executaQuery(String query)
	{		
        try {
    		Class.forName("com.mysql.jdbc.Driver");
    		this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "testuser", "test623");
    		this.st = this.con.prepareStatement(query);
    		
    		this.resultado = this.st.executeQuery();

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public boolean next()
	{
		try {
			if(this.resultado.next())
			{
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return false;
	}
	
	public String getString(int index)
	{
		try {
			return this.resultado.getString(index);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void fechaConexao()
	{
        try {
            if (this.resultado != null) {
            	this.resultado.close();
            }
            if (this.st != null) {
                this.st.close();
            }
            if (this.con != null) {
                this.con.close();
            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Version.class.getName());
            lgr.log(Level.WARNING, ex.getMessage(), ex);
        }		
	}
}