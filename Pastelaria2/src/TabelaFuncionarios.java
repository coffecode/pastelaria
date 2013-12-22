import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import java.awt.Dimension;
import java.awt.GridLayout;

public class TabelaFuncionarios extends JPanel {
  private boolean DEBUG = false;
  private Object[][] conteudo;

  public TabelaFuncionarios() {
    super(new GridLayout(1, 0));
    
    int linhas = 0;
	Query pega = new Query();
	pega.executaQuery("SELECT * FROM funcionarios");
	
	while(pega.next())
	{
		String[] linha = new String[5];		// qntd de colunas da tabela
		
		linha[0] = pega.getString("nome");
		linha[1] = pega.getString("username");
		linha[2] = pega.getString("password");
		
		if(pega.getInt("level") < 2)
			linha[3] = "Funcionário";
		else
			linha[3] = "Gerente";
		
		linha[4] = "X";
		
		conteudo[linhas] = linha;
		linhas++;
	}

    JTable table = new JTable(new TabelaFuncionariosModel());
    table.setPreferredScrollableViewportSize(new Dimension(500, 70));

    //Create the scroll pane and add the table to it.
    JScrollPane scrollPane = new JScrollPane(table);

    //Add the scroll pane to this panel.
    add(scrollPane);
  }

  class TabelaFuncionariosModel extends AbstractTableModel {
    private String[] columnNames = { "Nome", "Usuário", "Senha", "Nível", "Deletar" };

    private Object[][] data = conteudo;

    public int getColumnCount() {
      return columnNames.length;
    }

    public int getRowCount() {
      return data.length;
    }

    public String getColumnName(int col) {
      return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
      return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/ editor for
     * each cell. If we didn't implement this method, then the last column
     * would contain text ("true"/"false"), rather than a check box.
     */
    public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's editable.
     */
    public boolean isCellEditable(int row, int col) {
      //Note that the data/cell address is constant,
      //no matter where the cell appears onscreen.
      if (col < 2) {
        return false;
      } else {
        return true;
      }
    }

    /*
     * Don't need to implement this method unless your table's data can
     * change.
     */
    public void setValueAt(Object value, int row, int col) {
      data[row][col] = value;
      fireTableCellUpdated(row, col);
    }
  }
}
