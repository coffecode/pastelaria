import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class Query
{
    private Connection con = null;
    private PreparedStatement st = null;
    private ResultSet resultado = null;
	
	public void executaQuery(String query)
	{		
        try {
    		Class.forName("com.mysql.jdbc.Driver");
    		this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pastelaria", "root", "puc4321");	// URL, user, senha
    		
    		this.st = this.con.prepareStatement(query);
    		this.resultado = this.st.executeQuery();

        } catch (SQLException ex) {
            //Logger lgr = Logger.getLogger(Query.class.getName());
            //lgr.log(Level.SEVERE, ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + ex.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void executaUpdate(String query)
	{		
        try {
    		Class.forName("com.mysql.jdbc.Driver");
    		this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pastelaria", "root", "puc4321");	// URL, user, senha
    		this.st = this.con.prepareStatement(query);

    		this.st.executeUpdate();

        } catch (SQLException ex) {
            //Logger lgr = Logger.getLogger(Query.class.getName());
            //lgr.log(Level.SEVERE, ex.getMessage(), ex);
        	JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + ex.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
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
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return false;
	}
	
	public String getString(String coluna)
	{
		try {
			return this.resultado.getString(coluna);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	public int getInt(String coluna)
	{
		try {
			return this.resultado.getInt(coluna);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}
	
	public int getInt(int coluna)
	{
		try {
			return this.resultado.getInt(coluna);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
			return 0;
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
            //Logger lgr = Logger.getLogger(Query.class.getName());
            //lgr.log(Level.WARNING, ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + ex.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
        }		
	}
}