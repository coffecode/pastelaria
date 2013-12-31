import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;

public class PainelProdutos extends JPanel implements ActionListener, TableModelListener{

	private DefaultTableModel tabela, tabelaAdc;
	private JTable tabelaProdutos, tabelaAdicionais;
	private JLabel adicionarNome, adicionarPreco;
	private JTextField campoNome, campoPreco;
	private JComboBox campoTipo;
	private JButton adicionar;
	private JPanel addPainel;
	
	PainelProdutos()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setMinimumSize(new Dimension(800, 480));		// Horizontal , Vertical
		setMaximumSize(new Dimension(800, 480));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setMinimumSize(new Dimension(786, 480));
		tabbedPane.setMaximumSize(new Dimension(786, 480));		
        
		//instance table model
		tabela = new DefaultTableModel() {
			@Override
		    public boolean isCellEditable(int row, int column) {
		       if(column == 1 || column == 2 || column == 4 || column ==5)
		    	   return true;
		       
		       return false;
		    }
		};
		
		tabela.addColumn("ID");
		tabela.addColumn("Nome");
		tabela.addColumn("Preço");
		tabela.addColumn("Vendido no Mês");
		tabela.addColumn("Tipo");
		tabela.addColumn("Deletar");
		
		Query pega = new Query();
		pega.executaQuery("SELECT * FROM produtos WHERE tipo = 1 ORDER BY nome ");
		int linhas = 0;
		int quantidade = 0;
		Calendar c = Calendar.getInstance();
		
		while(pega.next())
		{
			Query pega2 = new Query();
			pega2.executaQuery("SELECT quantidade_produto FROM vendas_produtos WHERE mes = "+ c.get(Calendar.MONTH) +" AND ano = "+ c.get(Calendar.YEAR) +" AND nome_produto = '" + pega.getString("nome") + "'");		
			
			while(pega2.next())
				quantidade += pega2.getInt("quantidade_produto");
			
			Object[] linha = {pega.getInt("produtos_id"), pega.getString("nome"), pega.getString("preco"), quantidade + " vezes", "Produto", "Deletar"};
			tabela.addRow(linha);
			linhas++;
			quantidade = 0;
			pega2.fechaConexao();
		}
		
		tabelaProdutos = new JTable() {
		    Color alternate = new Color(141, 182, 205);
		    
		    @Override
		    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		        Component stamp = super.prepareRenderer(renderer, row, column);
		        if (row % 2 == 0 && column != 4 && column!= 5)
		            stamp.setBackground(alternate);
		        else
		            stamp.setBackground(this.getBackground());
		        return stamp;
		    }				
		};
		
		tabelaProdutos.setModel(tabela);
		tabelaProdutos.getColumnModel().getColumn(0).setMinWidth(0);
		tabelaProdutos.getColumnModel().getColumn(0).setMaxWidth(0);
		tabelaProdutos.getColumnModel().getColumn(1).setMinWidth(300);
		tabelaProdutos.getColumnModel().getColumn(1).setMaxWidth(300);		
		tabelaProdutos.getColumnModel().getColumn(2).setMinWidth(100);
		tabelaProdutos.getColumnModel().getColumn(2).setMaxWidth(100);
		tabelaProdutos.getColumnModel().getColumn(3).setMinWidth(150);
		tabelaProdutos.getColumnModel().getColumn(3).setMaxWidth(150);
		tabelaProdutos.getColumnModel().getColumn(5).setMinWidth(60);
		tabelaProdutos.getColumnModel().getColumn(5).setMaxWidth(60);		
		
		tabelaProdutos.setRowHeight(28);
		
		DefaultTableCellRenderer centraliza = new DefaultTableCellRenderer();
		centraliza.setHorizontalAlignment( JLabel.CENTER );
		
		tabelaProdutos.getColumn("Nome").setCellRenderer(centraliza);
		tabelaProdutos.getColumn("Preço").setCellRenderer(centraliza);
		tabelaProdutos.getColumn("Tipo").setCellRenderer(centraliza);
		tabelaProdutos.getColumn("Vendido no Mês").setCellRenderer(centraliza);
		tabelaProdutos.getColumn("Deletar").setCellRenderer(centraliza);
		tabelaProdutos.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
		tabelaProdutos.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));		
		
		String[] tiposProduto = { "Adicional", "Produto" };
		
		tabelaProdutos.getColumn("Tipo").setCellEditor(new MyComboBoxEditor(tiposProduto));
		MyComboBoxRenderer combo = new MyComboBoxRenderer(tiposProduto);
		((JLabel)combo.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		tabelaProdutos.getColumn("Tipo").setCellRenderer(combo);
		tabelaProdutos.getModel().addTableModelListener(this);
		
		if(linhas > 8)
			tabelaProdutos.setPreferredScrollableViewportSize(new Dimension(600, 112));
		else
			tabelaProdutos.setPreferredScrollableViewportSize(new Dimension(600, linhas*16));
		
		JScrollPane scrolltabela = new JScrollPane(tabelaProdutos);
		
		ImageIcon iconeUltimas = new ImageIcon("imgs/fiado24.png");
		tabbedPane.addTab("Produtos", iconeUltimas, scrolltabela, "Gerenciar Produtos da loja.");
		
		//instance table model
		tabelaAdc = new DefaultTableModel() {
			@Override
		    public boolean isCellEditable(int row, int column) {
		       if(column == 2 || column == 4 || column ==5)
		    	   return true;
		       
		       return false;
		    }
		};
		
		tabelaAdc.addColumn("ID");
		tabelaAdc.addColumn("Nome");
		tabelaAdc.addColumn("Preço");
		tabelaAdc.addColumn("Vendido no Mês");
		tabelaAdc.addColumn("Tipo");
		tabelaAdc.addColumn("Deletar");
		
		pega.executaQuery("SELECT * FROM produtos WHERE tipo = 2 ORDER BY nome ");
		int linhas2 = 0;
		quantidade = 0;
		
		while(pega.next())
		{
			Query pega2 = new Query();
			pega2.executaQuery("SELECT quantidade_produto, adicionais_produto FROM vendas_produtos WHERE mes = "+ c.get(Calendar.MONTH) +" AND ano = "+ c.get(Calendar.YEAR) +" AND adicionais_produto LIKE '%" + pega.getString("nome") + "%'");		
			
			while(pega2.next())
			{ 
				int pos = -1;  
				int contagem = 0;  
				while (true) {  
				    pos = pega2.getString("adicionais_produto").indexOf (pega.getString("nome"), pos + 1);   
				    if (pos < 0) break;  
				    contagem++;  
				}
				quantidade += (pega2.getInt("quantidade_produto") * contagem);
			}
			
			Object[] linha = {pega.getInt("produtos_id"), pega.getString("nome"), pega.getString("preco"), quantidade + " vezes", "Adicional", "Deletar"};
			tabelaAdc.addRow(linha);
			linhas2++;
			quantidade = 0;
			pega2.fechaConexao();
		}
		
		pega.fechaConexao();
		
		tabelaAdicionais = new JTable() {
		    Color alternate = new Color(141, 182, 205);
		    
		    @Override
		    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		        Component stamp = super.prepareRenderer(renderer, row, column);
		        if (row % 2 == 0 && column != 4 && column!= 5)
		            stamp.setBackground(alternate);
		        else
		            stamp.setBackground(this.getBackground());
		        return stamp;
		    }				
		};
		
		tabelaAdicionais.setModel(tabelaAdc);
		tabelaAdicionais.getColumnModel().getColumn(0).setMinWidth(0);
		tabelaAdicionais.getColumnModel().getColumn(0).setMaxWidth(0);
		tabelaAdicionais.getColumnModel().getColumn(1).setMinWidth(300);
		tabelaAdicionais.getColumnModel().getColumn(1).setMaxWidth(300);		
		tabelaAdicionais.getColumnModel().getColumn(2).setMinWidth(100);
		tabelaAdicionais.getColumnModel().getColumn(2).setMaxWidth(100);
		tabelaAdicionais.getColumnModel().getColumn(3).setMinWidth(150);
		tabelaAdicionais.getColumnModel().getColumn(3).setMaxWidth(150);
		tabelaAdicionais.getColumnModel().getColumn(5).setMinWidth(60);
		tabelaAdicionais.getColumnModel().getColumn(5).setMaxWidth(60);		
		
		tabelaAdicionais.setRowHeight(28);
		
		tabelaAdicionais.getColumn("Nome").setCellRenderer(centraliza);
		tabelaAdicionais.getColumn("Preço").setCellRenderer(centraliza);
		tabelaAdicionais.getColumn("Tipo").setCellRenderer(centraliza);
		tabelaAdicionais.getColumn("Vendido no Mês").setCellRenderer(centraliza);
		tabelaAdicionais.getColumn("Deletar").setCellRenderer(centraliza);
		tabelaAdicionais.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
		tabelaAdicionais.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));		
		
		tabelaAdicionais.getColumn("Tipo").setCellEditor(new MyComboBoxEditor(tiposProduto));
		MyComboBoxRenderer combo2 = new MyComboBoxRenderer(tiposProduto);
		((JLabel)combo2.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		tabelaAdicionais.getColumn("Tipo").setCellRenderer(combo2);
		tabelaAdicionais.getModel().addTableModelListener(this);
		
		if(linhas2 > 8)
			tabelaAdicionais.setPreferredScrollableViewportSize(new Dimension(600, 112));
		else
			tabelaAdicionais.setPreferredScrollableViewportSize(new Dimension(600, linhas2*16));
		
		JScrollPane scrolltabelaAdc = new JScrollPane(tabelaAdicionais);		
		tabbedPane.addTab("Adicionais", iconeUltimas, scrolltabelaAdc, "Gerenciar Adicionais da loja.");
		
		add(tabbedPane);
		
		addPainel = new JPanel();
		addPainel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Adicionar"));
		addPainel.setLayout(new GridBagLayout());
		addPainel.setMinimumSize(new Dimension(790, 200));
		addPainel.setMaximumSize(new Dimension(790, 200));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);  //top padding
		
		campoTipo= new JComboBox(tiposProduto);
		campoTipo.setSelectedIndex(0);
		campoTipo.setPreferredSize(new Dimension(150, 30));
		
		adicionarNome = new JLabel("Nome: ");
		adicionarPreco = new JLabel("Preço: ");
		
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
				String limpeza = campoPreco.getText().replaceAll("[^0-9.,]+","");
				
				if(!"".equals(limpeza.trim()))
				{
					limpeza = limpeza.replaceAll(",", ".");
					double limpeza2 = Double.parseDouble(limpeza);
					
					if(limpeza2 > 0)
					{
						String resultado = String.format("%.2f", limpeza2);
						
						String formatacao;
						Query envia = new Query();
						formatacao = "INSERT INTO produtos(nome, preco, tipo) VALUES('"
						+ campoNome.getText() +
						"', '" + resultado + "', "+tipo+");";
						
						envia.executaUpdate(formatacao);
						
						int idProdutoNovo = 9999999;
						
						envia.executaQuery("SELECT produtos_id FROM produtos ORDER BY produtos_id DESC limit 0, 1");
						if(envia.next())
						{
							idProdutoNovo = envia.getInt("produtos_id");
						}
						
						envia.fechaConexao();
						if(tipo == 1)
						{
							boolean flag = false;
							
							Object[] linha = {idProdutoNovo, campoNome.getText(), resultado, "0 vezes", "Produto", "Deletar"};
							
							for(int i = 0; i < tabela.getRowCount() ; i++)
							{
								if(campoNome.getText().compareTo((String) tabela.getValueAt(i, 1)) <= 0)
								{
									tabela.insertRow(i, linha);
									flag = true;
									break;
								}
							}

							if(!flag)
								tabela.addRow(linha);							
						}
						else
						{
							boolean flag = false;
							
							Object[] linha = {idProdutoNovo, campoNome.getText(), resultado, "0 vezes", "Adicional", "Deletar"};
							
							for(int i = 0; i < tabelaAdc.getRowCount() ; i++)
							{
								if(campoNome.getText().compareTo((String) tabelaAdc.getValueAt(i, 1)) <= 0)
								{
									tabelaAdc.insertRow(i, linha);
									flag = true;
									break;
								}
							}

							if(!flag)
								tabelaAdc.addRow(linha);								
						}
						
						campoNome.setText("");
						campoPreco.setText("");
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Preço Inválido!");
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Preço Inválido!");
				}
			}
		}
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
		    button.setIcon(new ImageIcon("imgs/delete.png"));
		    isPushed = true;
		    return button;
		  }
		  
		  public Object getCellEditorValue() {
		    if (isPushed) {
		      if(tabelaProdutos.getSelectedRowCount() ==1)
		      {
			       String formatacao;
			       Query envia = new Query();
			       formatacao = "DELETE FROM produtos WHERE `produtos_id` = " + tabelaProdutos.getValueAt(tabelaProdutos.getSelectedRow(), 0) + ";";
			       envia.executaUpdate(formatacao);
			       envia.fechaConexao();
			       
				      SwingUtilities.invokeLater(new Runnable() {  
				    	  public void run() {  
				    		  tabela.removeRow(tabelaProdutos.getSelectedRow());
				    	  }  
				      });
		       }
		      else if(tabelaAdicionais.getSelectedRowCount() ==1)
		      {
			       String formatacao;
			       Query envia = new Query();
			       formatacao = "DELETE FROM produtos WHERE `produtos_id` = " + tabelaAdicionais.getValueAt(tabelaAdicionais.getSelectedRow(), 0) + ";";
			       envia.executaUpdate(formatacao);
			       envia.fechaConexao();
			       
				      SwingUtilities.invokeLater(new Runnable() {  
				    	  public void run() {  
				    		  tabelaAdc.removeRow(tabelaAdicionais.getSelectedRow());
				    	  }  
				      });		    	  
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
			
	        final int row = e.getFirstRow();
	        int column = e.getColumn();
	        
	        if((TableModel)e.getSource() == tabela)
	        {
	        	if(column == 1)
	        	{
				    String formatacao;
				    Query envia = new Query();
				    formatacao = "UPDATE produtos SET nome = '" + (String) tabela.getValueAt(row, column) + "' WHERE produtos_id = " + tabela.getValueAt(row, 0);
				    envia.executaUpdate(formatacao);
				    envia.fechaConexao();
	        	}
	        	else if(column == 2)
	        	{
	        		String pega = (String) tabela.getValueAt(row, column);
					String limpeza = pega.replaceAll("[^0-9.,]+","");
					
					if(!"".equals(limpeza.trim()))
					{
						limpeza = limpeza.replaceAll(",", ".");
						double limpeza2 = Double.parseDouble(limpeza);
						
						if(limpeza2 > 0)
						{
							String resultado = String.format("%.2f", limpeza2);
						    String formatacao;
						    Query envia = new Query();
						    formatacao = "UPDATE produtos SET preco = '" + resultado + "' WHERE produtos_id = " + tabela.getValueAt(row, 0);
						    envia.executaUpdate(formatacao);
						    envia.fechaConexao();
						}
					}
	        	}
	        	else if(column == 4)
	        	{
			        String data = (String) tabela.getValueAt(row, column);
			        int tipo =0;
			        if(data.equals("Adicional")){
			        	tipo = 2;
			        }else{
			        	tipo = 1;
			        }
			        
			        String formatacao;
				    Query envia = new Query();
				    formatacao = "UPDATE produtos SET tipo = " + tipo + " WHERE produtos_id = " + tabela.getValueAt(row, 0);
				    envia.executaUpdate(formatacao);
				    envia.fechaConexao();
				    
				    if(tipo == 2)
				    {					    
						boolean flag = false;
						
						Object[] linha = {tabela.getValueAt(row, 0), (String) tabela.getValueAt(row, 1), (String) tabela.getValueAt(row, 2), 
						(String) tabela.getValueAt(row, 3), "Adicional", ""};
						
						String nomeP = (String) tabela.getValueAt(row, 1);
						
						for(int i = 0; i < tabelaAdc.getRowCount() ; i++)
						{
							if(nomeP.compareTo((String) tabelaAdc.getValueAt(i, 1)) <= 0)
							{
								tabelaAdc.insertRow(i, linha);
								flag = true;
								break;
							}
						}

						if(!flag)
							tabelaAdc.addRow(linha);
						
					    SwingUtilities.invokeLater(new Runnable() {  
					    	  public void run() {  
					    		  tabela.removeRow(row);
					    	  }  
					      });						
				    }
	        	}
	        }
	        if((TableModel)e.getSource() == tabelaAdc)
	        {
	        	if(column == 1)
	        	{
				    String formatacao;
				    Query envia = new Query();
				    formatacao = "UPDATE produtos SET nome = '" + (String) tabelaAdc.getValueAt(row, column) + "' WHERE produtos_id = " + tabelaAdc.getValueAt(row, 0);
				    envia.executaUpdate(formatacao);
				    envia.fechaConexao();
	        	}
	        	else if(column == 2)
	        	{
	        		String pega = (String) tabelaAdc.getValueAt(row, column);
					String limpeza = pega.replaceAll("[^0-9.,]+","");
					
					if(!"".equals(limpeza.trim()))
					{
						limpeza = limpeza.replaceAll(",", ".");
						double limpeza2 = Double.parseDouble(limpeza);
						
						if(limpeza2 > 0)
						{
							String resultado = String.format("%.2f", limpeza2);
						    String formatacao;
						    Query envia = new Query();
						    formatacao = "UPDATE produtos SET preco = '" + resultado + "' WHERE produtos_id = " + tabelaAdc.getValueAt(row, 0);
						    envia.executaUpdate(formatacao);
						    envia.fechaConexao();
						}
					}
	        	}
	        	else if(column == 4)
	        	{
			        String data = (String) tabelaAdc.getValueAt(row, column);
			        int tipo =0;
			        if(data.equals("Adicional")){
			        	tipo = 2;
			        }else{
			        	tipo = 1;
			        }
			        
			        String formatacao;
				    Query envia = new Query();
				    formatacao = "UPDATE produtos SET tipo = " + tipo + " WHERE produtos_id = " + tabelaAdc.getValueAt(row, 0);
				    envia.executaUpdate(formatacao);
				    envia.fechaConexao();
				    
				    if(tipo == 1)
				    {					    
						boolean flag = false;
						
						Object[] linha = {tabelaAdc.getValueAt(row, 0), (String) tabelaAdc.getValueAt(row, 1), (String) tabelaAdc.getValueAt(row, 2), 
						(String) tabelaAdc.getValueAt(row, 3), "Produto", ""};
						
						String nomeP = (String) tabelaAdc.getValueAt(row, 1);
						
						for(int i = 0; i < tabela.getRowCount() ; i++)
						{
							if(nomeP.compareTo((String) tabela.getValueAt(i, 1)) <= 0)
							{
								tabela.insertRow(i, linha);
								flag = true;
								break;
							}
						}

						if(!flag)
							tabela.addRow(linha);
						
					    SwingUtilities.invokeLater(new Runnable() {  
					    	  public void run() {  
					    		  tabelaAdc.removeRow(row);
					    	  }  
					      });						
				    }				    
	        	}	        	
	        }
	    }
}
