import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.event.*;
import java.io.Serializable;
import java.util.Vector;

public class PainelFuncionarios extends JPanel implements MouseListener
{
	private DefaultTableModel tabela;
	private JTable tabelaFuncionarios;
	
	@SuppressWarnings("null")
	PainelFuncionarios()
	{
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Funcionários"));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		setMinimumSize(new Dimension(800, 500));		// Horizontal , Vertical
		setMaximumSize(new Dimension(800, 500));
		
		tabela = new DefaultTableModel();
		tabela.addColumn("Nome");
		tabela.addColumn("Usuário");
		tabela.addColumn("Senha");
		tabela.addColumn("Nível");
		tabela.addColumn("Deletar");
		
		Query pega = new Query();
		pega.executaQuery("SELECT * FROM funcionarios");
		int linhas = 0;
		
		while(pega.next())
		{
			Vector<Serializable> linha = new Vector<Serializable>();
			
			linha.add(pega.getString("nome"));
			linha.add(pega.getString("username"));
			linha.add(pega.getString("password"));
			
			if(pega.getInt("level") < 2)
				linha.add("Funcionário");
			else
				linha.add("Gerente");
			
			linha.add("Deletar");
			
			tabela.addRow(linha);
			linhas++;
		}
		
		tabelaFuncionarios = new JTable();
		
		tabelaFuncionarios.setModel(tabela);
		tabelaFuncionarios.getColumnModel().getColumn(0).setMinWidth(180);
		tabelaFuncionarios.getColumnModel().getColumn(3).setMinWidth(160);
		tabelaFuncionarios.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
		tabelaFuncionarios.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));
		
		if(linhas > 8)
			tabelaFuncionarios.setPreferredScrollableViewportSize(new Dimension(700, 112));
		else
			tabelaFuncionarios.setPreferredScrollableViewportSize(new Dimension(700, linhas*16));
		
		JScrollPane scrolltabela = new JScrollPane(tabelaFuncionarios);
		add(scrolltabela);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e)	{

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
	
	class ButtonRenderer extends JButton implements TableCellRenderer {

		  public ButtonRenderer() {
		    setOpaque(true);
		  }

		  public Component getTableCellRendererComponent(JTable table, Object value,
		      boolean isSelected, boolean hasFocus, int row, int column) {
		    if (isSelected) {
		      setForeground(table.getSelectionForeground());
		      setBackground(table.getSelectionBackground());
		    } else {
		      setForeground(table.getForeground());
		      setBackground(UIManager.getColor("Button.background"));
		    }
		    setText((value == null) ? "" : value.toString());
		    return this;
		  }
		}

		/**
		 * @version 1.0 11/09/98
		 */

		class ButtonEditor extends DefaultCellEditor {
		  protected JButton button;

		  private String label;

		  private boolean isPushed;

		  public ButtonEditor(JCheckBox checkBox) {
		    super(checkBox);
		    button = new JButton();
		    button.setOpaque(true);
		    button.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        fireEditingStopped();
		      }
		    });
		  }

		  public Component getTableCellEditorComponent(JTable table, Object value,
		      boolean isSelected, int row, int column) {
		    if (isSelected) {
		      button.setForeground(table.getSelectionForeground());
		      button.setBackground(table.getSelectionBackground());
		    } else {
		      button.setForeground(table.getForeground());
		      button.setBackground(table.getBackground());
		    }
		    label = (value == null) ? "" : value.toString();
		    button.setText(label);
		    isPushed = true;
		    return button;
		  }

		  public Object getCellEditorValue() {
		    if (isPushed) {
		      // 
		      // 
		      JOptionPane.showMessageDialog(button, label + ": Ouch!");
		      // System.out.println(label + ": Ouch!");
		    }
		    isPushed = false;
		    return new String(label);
		  }

		  public boolean stopCellEditing() {
		    isPushed = false;
		    return super.stopCellEditing();
		  }

		  protected void fireEditingStopped() {
		    super.fireEditingStopped();
		  }
		}	
}