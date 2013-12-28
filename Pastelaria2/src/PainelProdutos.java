import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import java.awt.event.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.BorderLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;



public class PainelProdutos extends JPanel implements MouseListener, ActionListener, TableModelListener{

	private DefaultTableModel tabela;
	private JTable tabelaProdutos;
	private JLabel adicionarNome, adicionarPreco;
	private JTextField campoNome, campoPreco;
	private JComboBox campoTipo;
	private JButton adicionar;
	private JPanel addPainel;
	
	
	PainelProdutos()
	{
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Gerenciar Produtos"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setMinimumSize(new Dimension(800, 480));		// Horizontal , Vertical
		setMaximumSize(new Dimension(800, 480));
		
		
		
        
        
		//instance table model
		tabela = new DefaultTableModel() {


			@Override
		    public boolean isCellEditable(int row, int column) {
		       if(column == 3 || column == 1 || column ==2)
		    	   return true;
		       
		       return false;
		    }
		};
		
		tabela.addColumn("Nome");
		tabela.addColumn("Preco");
		tabela.addColumn("Tipo");
		tabela.addColumn("Deletar");
		
		
		
		Query pega = new Query();
		pega.executaQuery("SELECT * FROM produtos ORDER BY nome ");
		int linhas = 0;
		
		
		while(pega.next())
		{
			Vector<Serializable> linha = new Vector<Serializable>();
				
			linha.add(pega.getString("nome"));
			
			String pegaPreco;
			pegaPreco = pega.getString("preco");
			pegaPreco.replaceAll(".", ",");
			
			linha.add(pegaPreco);
				
			
			
			

			if(pega.getInt("tipo") < 2)
				linha.add("Produto");
			else
				linha.add("Adicional");
			linha.add("Deletar");

			tabela.addRow(linha);
			linhas++;
		}
		
		
		tabelaProdutos = new JTable() {
		    Color alternate = new Color(141, 182, 205);
		    
		    @Override
		    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		        Component stamp = super.prepareRenderer(renderer, row, column);
		        if (row % 2 == 0 && column != 3 && column!=2)
		            stamp.setBackground(alternate);
		        else
		            stamp.setBackground(this.getBackground());
		        return stamp;
		    }				
		};
		
		tabelaProdutos.setModel(tabela);
		tabelaProdutos.getColumnModel().getColumn(0).setMinWidth(240);
		tabelaProdutos.getColumnModel().getColumn(0).setMaxWidth(240);
		tabelaProdutos.getColumnModel().getColumn(1).setMinWidth(240);
		tabelaProdutos.getColumnModel().getColumn(1).setMaxWidth(240);
		tabelaProdutos.getColumnModel().getColumn(2).setMinWidth(220);
		tabelaProdutos.getColumnModel().getColumn(2).setMaxWidth(220);
		tabelaProdutos.getColumnModel().getColumn(3).setMinWidth(82);
		tabelaProdutos.getColumnModel().getColumn(3).setMaxWidth(82);		
		
		tabelaProdutos.setRowHeight(30);
		
		DefaultTableCellRenderer centraliza = new DefaultTableCellRenderer();
		centraliza.setHorizontalAlignment( JLabel.CENTER );
		
		tabelaProdutos.getColumn("Nome").setCellRenderer(centraliza);
		tabelaProdutos.getColumn("Preco").setCellRenderer(centraliza);
		tabelaProdutos.getColumn("Tipo").setCellRenderer(centraliza);
		tabelaProdutos.getColumn("Deletar").setCellRenderer(centraliza);
		tabelaProdutos.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
		tabelaProdutos.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));		
		
		String[] tiposProduto = { "Adicional", "Produto" };
		
		tabelaProdutos.getColumn("Tipo").setCellEditor(new MyComboBoxEditor(tiposProduto));
		tabelaProdutos.getColumn("Tipo").setCellRenderer(new MyComboBoxRenderer(tiposProduto));
		
		tabelaProdutos.getModel().addTableModelListener(this);
		
		if(linhas > 8)
			tabelaProdutos.setPreferredScrollableViewportSize(new Dimension(700, 112));
		else
			tabelaProdutos.setPreferredScrollableViewportSize(new Dimension(700, linhas*16));
		
		JScrollPane scrolltabela = new JScrollPane(tabelaProdutos);
		add(scrolltabela);
		
		addPainel = new JPanel();
		addPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Adicionar"));
		addPainel.setLayout(new GridBagLayout());
		addPainel.setMinimumSize(new Dimension(800, 200));
		addPainel.setMaximumSize(new Dimension(800, 200));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);  //top padding
		
		
		campoTipo= new JComboBox(tiposProduto);
		campoTipo.setSelectedIndex(0);
		campoTipo.setPreferredSize(new Dimension(150, 30));
		
		adicionarNome = new JLabel("Nome: ");
		adicionarPreco = new JLabel("Pre�o: ");
		
		campoNome = new JTextField();
		campoNome.setPreferredSize(new Dimension(150, 30));
		campoPreco = new JTextField();
		campoPreco.setPreferredSize(new Dimension(150, 30));
	
		ImageIcon iconePlus = new ImageIcon("imgs/plus2.png");
		adicionar = new JButton("Adicionar");
		adicionar.setIcon(iconePlus);
		adicionar.setPreferredSize(new Dimension(150, 30));
		adicionar.addActionListener(this);
		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;;
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas
		
		addPainel.add(adicionarNome, gbc);
		
		gbc.gridx = 2;	// colunas
		addPainel.add(campoNome, gbc);
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 2;	// linhas		
		
		addPainel.add(adicionarPreco, gbc);
		
		gbc.gridx = 2;	// colunas
		
		addPainel.add(campoPreco, gbc);
		
		gbc.gridx = 3;
		gbc.gridy = 1;
		
		addPainel.add(campoTipo, gbc);
		
		gbc.gridx = 2;	// colunas
		gbc.gridy = 3;	// linhas			
		
		addPainel.add(adicionar, gbc);
		
		add(addPainel);
	}
	
	class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
		  public MyComboBoxRenderer(String[] items) {
		    super(items);
		  }

		  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		      boolean hasFocus, int row, int column) {
		    if (isSelected) {
		      setForeground(table.getSelectionForeground());
		      super.setBackground(table.getSelectionBackground());
		    } else {
		      setForeground(table.getForeground());
		      setBackground(table.getBackground());
		    }
		    setSelectedItem(value);
		    return this;
		  }
		}


	class MyComboBoxEditor extends DefaultCellEditor {
		  public MyComboBoxEditor(String[] items) {
		    super(new JComboBox(items));
		  }
		}
	@Override
	public void actionPerformed(ActionEvent e) {
	
		if(e.getSource() == adicionar)
		{
			int tipo = 0;
			
			if(campoTipo.getSelectedItem() == "Adicional")
				tipo = 2;
			else
				tipo = 1;	
			
			if("".equals(campoNome.getText().trim()) || "".equals(campoPreco.getText().trim()))
			{
				JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
			}
			else
			{
				String formatacao;
				Query envia = new Query();
				formatacao = "INSERT INTO produtos(nome, preco, tipo) VALUES('"
				+ campoNome.getText() +
				"', " + campoPreco.getText() + ", "+tipo+");";
				
				envia.executaUpdate(formatacao);
				
				MenuPrincipal.AbrirPrincipal(1, false);
			}
		}
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
			  
			  setIcon(new ImageIcon("imgs/delete.png"));
			  
		    if (isSelected) {
		    		setForeground(table.getSelectionForeground());
		    		setBackground(table.getSelectionBackground());
		    } else {
			      setForeground(table.getSelectionForeground());
			      setBackground(table.getSelectionBackground());
		    }
		    //setText((value == null) ? "" : value.toString());
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
		    button.setIcon(new ImageIcon("imgs/delete.png"));
		    isPushed = true;
		    return button;
		  }
		
		  
		  
		  public Object getCellEditorValue() {
		    if (isPushed) {
		      if(tabelaProdutos.getSelectedRowCount() ==1)	//verifico se somente uma linha está selecionada  
		      {
		    	   String pega = (String) tabelaProdutos.getValueAt(tabelaProdutos.getSelectedRow(), 0);
			       String formatacao;
			       Query envia = new Query();
			       formatacao = "DELETE FROM produtos WHERE `nome` = '" + pega + "';";
			       envia.executaUpdate(formatacao);
			       MenuPrincipal.AbrirPrincipal(1, false);
		       }
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
		public void tableChanged(TableModelEvent e) {
			
	        int row = e.getFirstRow();
	        int column = e.getColumn();
	        
	        if(column == 1){
		        TableModel model = (TableModel)e.getSource();
		        String data = (String) model.getValueAt(row, column);	// data possui o conte�do atualizado.
		        
		        String pega = (String) model.getValueAt(row, 0);
		        
			    String formatacao;
			    Query envia = new Query();
			    formatacao = "UPDATE produtos SET preco =  " + data + " WHERE nome = '"+pega+"' " ;
			    envia.executaUpdate(formatacao);
			    MenuPrincipal.AbrirPrincipal(1, false);
	        }else if(column == 2){
	        	
	        	
	        	TableModel model = (TableModel)e.getSource();
		        String data = (String) model.getValueAt(row, column);	// data possui o conte�do atualizado
		        String pega = (String) model.getValueAt(row, 0);
		        int tipo =0;
		        if(data.equals("Adicional")){
		        	tipo =2;
		        }else{
		        	tipo =1;
		        }
		        String formatacao;
			    Query envia = new Query();
			    formatacao = "UPDATE produtos SET tipo =  " + tipo + " WHERE nome = '"+pega+"' " ;
			    envia.executaUpdate(formatacao);
			    MenuPrincipal.AbrirPrincipal(1, false);
	        }
	    }	
}
