package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.utilitarios.DiarioLog;
import codecoffe.restaurantes.utilitarios.Usuario;

import com.alee.laf.scroll.WebScrollPane;

import java.awt.Color;
import java.awt.event.*;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Vector;

public class UltimasVendas extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable tabelaUltimasVendas;
	private DefaultTableModel tabela;
	
	public UltimasVendas()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel painelTabela = new JPanel(new BorderLayout()){
		
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        Graphics2D g2d = (Graphics2D) g;
		        Color color1 = getBackground();
		        Color color2 = new Color(207, 220, 249);
		        int w = getWidth();
		        int h = getHeight();
		        GradientPaint gp = new GradientPaint(
		            0, 0, color1, 0, h, color2);
		        g2d.setPaint(gp);
		        g2d.fillRect(0, 0, w, h);
		    }			
		};

		tabela = new DefaultTableModel() {

		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		       if(column == 6)
		    	   return true;
		       
		       return false;
		    }
		};
		
		tabela.addColumn("ID");
		tabela.addColumn("Data");
		tabela.addColumn("Forma de Pagamento");
		tabela.addColumn("Total");
		tabela.addColumn("Status");
		tabela.addColumn("Atendente");
		tabela.addColumn("Deletar");
		
		try {
			Query pega = new Query();
			pega.executaQuery("SELECT * FROM vendas ORDER BY vendas_id DESC limit 0, 25");
			
			while(pega.next())
			{
				Vector<Serializable> linha = new Vector<Serializable>();
					
				linha.add(pega.getInt("vendas_id"));
				linha.add(pega.getString("horario"));
				linha.add(pega.getString("forma_pagamento"));
				linha.add(pega.getString("total"));
				
				if((Double.parseDouble(pega.getString("total").replaceAll(",", ".")) > Double.parseDouble(pega.getString("valor_pago").replaceAll(",", "."))))
				{
					if(pega.getString("forma_pagamento").equals("Fiado"))
					{
						linha.add("Não Pago");
					}
					else
					{
						linha.add("Pago");
					}
				}
				else
				{
					linha.add("Pago");
				}
				
				linha.add(pega.getString("atendente"));
				linha.add("");
				tabela.addRow(linha);
			}
			
			pega.fechaConexao();
		} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			new PainelErro(e);
		}
		
		tabelaUltimasVendas = new JTable() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			Color alternate = new Color(206, 220, 249);
		    
		    @Override
		    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		        Component stamp = super.prepareRenderer(renderer, row, column);
		        if (row % 2 == 0 && column != 6)
		            stamp.setBackground(alternate);
		        else
		            stamp.setBackground(this.getBackground());
		        return stamp;
		    }    
		};
		
		tabelaUltimasVendas.setModel(tabela);
		tabelaUltimasVendas.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
		tabelaUltimasVendas.getColumnModel().getColumn(0).setMinWidth(50);
		tabelaUltimasVendas.getColumnModel().getColumn(0).setMaxWidth(100);
		tabelaUltimasVendas.getColumnModel().getColumn(1).setMinWidth(170);
		tabelaUltimasVendas.getColumnModel().getColumn(1).setMaxWidth(440);
		tabelaUltimasVendas.getColumnModel().getColumn(2).setMinWidth(150);
		tabelaUltimasVendas.getColumnModel().getColumn(2).setMaxWidth(400);		
		tabelaUltimasVendas.getColumnModel().getColumn(3).setMinWidth(110);
		tabelaUltimasVendas.getColumnModel().getColumn(3).setMaxWidth(320);	
		tabelaUltimasVendas.getColumnModel().getColumn(4).setMinWidth(99);
		tabelaUltimasVendas.getColumnModel().getColumn(4).setMaxWidth(198);
		tabelaUltimasVendas.getColumnModel().getColumn(5).setMinWidth(150);
		tabelaUltimasVendas.getColumnModel().getColumn(5).setMaxWidth(400);			
		tabelaUltimasVendas.getColumnModel().getColumn(6).setMinWidth(60);
		tabelaUltimasVendas.getColumnModel().getColumn(6).setMaxWidth(60);		
		tabelaUltimasVendas.setRowHeight(30);
		tabelaUltimasVendas.getTableHeader().setReorderingAllowed(false);
		
		tabelaUltimasVendas.getColumn("Total").setCellRenderer(new JLabelRenderer());	
		tabelaUltimasVendas.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
		tabelaUltimasVendas.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));
		tabelaUltimasVendas.getColumn("ID").setCellRenderer(new CustomRenderer());
		tabelaUltimasVendas.getColumn("Data").setCellRenderer(new CustomRenderer());
		tabelaUltimasVendas.getColumn("Atendente").setCellRenderer(new CustomRenderer());
		tabelaUltimasVendas.getColumn("Forma de Pagamento").setCellRenderer(new CustomRenderer());
		tabelaUltimasVendas.getColumn("Status").setCellRenderer(new CustomRenderer());	
		
		WebScrollPane scrolltabela = new WebScrollPane(tabelaUltimasVendas, true);
		
		//scrolltabela.setMinimumSize(new Dimension(1020, 435));		// Horizontal , Vertical
		//scrolltabela.setMaximumSize(new Dimension(1920, 800));		
		
		painelTabela.add(scrolltabela, BorderLayout.CENTER);
		add(painelTabela);

		ToolTipManager.sharedInstance().setDismissDelay(40000);
	}
	
	class CustomRenderer extends DefaultTableCellRenderer 
	{
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	    {
	        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        if(isSelected)
	        {
	        	setHorizontalAlignment( JLabel.CENTER );
	        	c.setForeground(new Color(72, 61, 139));
	        	return c;
	        }
	        else
	        {
	        	setHorizontalAlignment( JLabel.CENTER );
	        	c.setForeground(Color.BLACK);
	        	return c;
	        }
	    }
	}	
	
	public void refresh()
	{
		tabela.setNumRows(0);
		
		try {
			Query pega = new Query();
			pega.executaQuery("SELECT * FROM vendas ORDER BY vendas_id DESC limit 0, 25");
			
			while(pega.next())
			{
				Vector<Serializable> linha = new Vector<Serializable>();
					
				linha.add(pega.getInt("vendas_id"));
				linha.add(pega.getString("horario"));
				linha.add(pega.getString("forma_pagamento"));
				linha.add(pega.getString("total"));
				
				if((Double.parseDouble(pega.getString("total").replaceAll(",", ".")) > Double.parseDouble(pega.getString("valor_pago").replaceAll(",", "."))))
				{
					if(pega.getString("forma_pagamento").equals("Fiado"))
					{
						linha.add("Não Pago");
					}
					else
					{
						linha.add("Pago");
					}
				}
				else
				{
					linha.add("Pago");
				}
				
				linha.add(pega.getString("atendente"));
				linha.add("");
				tabela.addRow(linha);
			}
			
			pega.fechaConexao();
		} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			new PainelErro(e);
		}
	}	
	
	class JLabelRenderer implements TableCellRenderer {
		
		private JLabel label;
		
		  public JLabelRenderer() {
			  label = new JLabel();
			  label.setForeground(Color.BLACK);
			  label.setOpaque(true);
		  }  

		  @SuppressWarnings("finally")
		public Component getTableCellRendererComponent(JTable table, Object value,
		      boolean isSelected, boolean hasFocus, int row, int column) {	  
			  
			  label.setHorizontalTextPosition(AbstractButton.LEFT);
			  label.setHorizontalAlignment( JLabel.CENTER );
			  label.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/documento.png")));
			  label.setForeground(Color.BLACK);
			  
			  	String formataTip;
			  	formataTip = "<html>";
			  	
				try {
					formataTip += "<b>Venda #" + table.getValueAt(row,0) + "</b>  (<i>" + table.getValueAt(row,1) +")</i><br>";
					Query pega = new Query();
					pega.executaQuery("SELECT * FROM vendas_produtos WHERE `id_link` = " + table.getValueAt(row,0) + "");
					
					while(pega.next())
					{
						formataTip += pega.getInt("quantidade_produto") + "x .......... <b>" + pega.getString("nome_produto") + "</b>";
						
						if(!"".equals(pega.getString("adicionais_produto").trim()))
						{
							formataTip += " com " + pega.getString("adicionais_produto");
						}
						
						formataTip += " - R$" +  pega.getString("preco_produto") + "<br>";
					}

					pega.fechaConexao();
					formataTip += "</html>";
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					formataTip = "Erro ao receber informação do banco de dados.";
				} finally {
					label.setToolTipText(formataTip);
					  
				    if (isSelected) {
				    	label.setForeground(new Color(72, 61, 139));
				    	label.setBackground(table.getSelectionBackground());
				    } else {
				    	label.setForeground(Color.BLACK);
				    	label.setBackground(table.getSelectionBackground());
				    }
				    
				    label.setText((value == null) ? "" : value.toString());
				    return label;
				  }					
			}
	}
	
	class ButtonRenderer implements TableCellRenderer {
		
		private JButton button;
		
		  public ButtonRenderer() {
		  }  

		  public Component getTableCellRendererComponent(JTable table, Object value,
		      boolean isSelected, boolean hasFocus, int row, int column) {
			  
			  button = new JButton();
			  button.setHorizontalTextPosition(AbstractButton.LEFT);
			  
		    if (isSelected) {
		    	button.setForeground(table.getSelectionForeground());
		    	button.setBackground(table.getSelectionBackground());
		    } else {
		    	button.setForeground(table.getSelectionForeground());
		    	button.setBackground(table.getSelectionBackground());
		    }
		    
		    button.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/delete.png")));
		    
			button.setText((value == null) ? "" : value.toString());
		    return button;
		  }
		}

		/**
		 * @version 1.0 11/09/98
		 */

		class ButtonEditor extends DefaultCellEditor {
		  /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

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
			  
			button.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/delete.png")));
			  
			button.setHorizontalTextPosition(AbstractButton.LEFT);
		    label = (value == null) ? "" : value.toString();
		    button.setText(label);  
		    
		    isPushed = true;
		    return button;
		  }

		  public Object getCellEditorValue() {
		    if (isPushed) {
		      if(tabelaUltimasVendas.getSelectedRowCount() == 1)
		      {
		    	  if(tabelaUltimasVendas.getSelectedColumn() == 4)
		    	  {
		    		  String bla = "Escreva o valor a ser deduzido da dívida.\n";
		    		  bla += "Importante:\n\n 1- Não é possível aumentar a dívida, apenas reduzí-la\n";
		    		  bla += "2 - Não é possível desfazer essa ação.\n3- Se o valor digitado for maior ou igual que o da dívida, a mesma será quitada.\n\n";
		    		  String pegaResposta = JOptionPane.showInputDialog(null, bla, "Reduzir Dívida", JOptionPane.QUESTION_MESSAGE);
		    		  
		    		  if(!"".equals(pegaResposta.trim()))
		    		  {
		    			  pegaResposta = pegaResposta.replaceAll("[^0-9.,]+","");
		    			  pegaResposta = pegaResposta.replaceAll(",",".");
		    			  
		    			  double resposta = Double.parseDouble(pegaResposta);
		    			  double deduzindo = resposta;
		    			  
		    			  if(resposta > 0)
		    			  {
		    				  	try {
									String pegaCPF = (String) tabelaUltimasVendas.getValueAt(tabelaUltimasVendas.getSelectedRow(), 3);
			  
									Query pega = new Query();
									pega.executaQuery("SELECT fiador_id FROM fiados WHERE `cpf` = '" + pegaCPF + "'");
									
									if(pega.next())
									{		
										pega.executaQuery("SELECT * FROM vendas WHERE `fiado_id` = " + pega.getInt("fiador_id") + "");
										
										while(pega.next())
										{
											if((Double.parseDouble(pega.getString("total").replaceAll(",", ".")) > Double.parseDouble(pega.getString("valor_pago").replaceAll(",", "."))))
											{
												double conta = Double.parseDouble(pega.getString("total").replaceAll(",", ".")) - Double.parseDouble(pega.getString("valor_pago").replaceAll(",", "."));
												
												Query manda = new Query();
												
												if(conta >= deduzindo)
												{
													String atualizado = String.format("%.2f", (deduzindo + Double.parseDouble(pega.getString("valor_pago").replaceAll(",", "."))));
													manda.executaUpdate("UPDATE vendas SET `valor_pago` = '" + atualizado + "' WHERE `vendas_id` = " + pega.getInt("vendas_id"));
													manda.fechaConexao();
													break;
												}
												else
												{
													deduzindo = (deduzindo + Double.parseDouble(pega.getString("valor_pago").replaceAll(",", "."))) - Double.parseDouble(pega.getString("total").replaceAll(",", "."));
													
													manda.executaUpdate("UPDATE vendas SET `valor_pago` = '" + pega.getString("valor_pago") + "' WHERE `vendas_id` = " + pega.getInt("vendas_id"));
													manda.fechaConexao();
												}
											}
										}
									}
								} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
									e.printStackTrace();
									new PainelErro(e);
								}
		    			  }
		    		  }
		    	  }
		    	  else	// deletar
		    	  {
		    		  int opcao = JOptionPane.showConfirmDialog(null, "Essa opção irá deletar a venda.\n\nVocê tem certeza?\n\n", "Deletar Venda", JOptionPane.YES_NO_OPTION);
		    		  
		    		  if(opcao == JOptionPane.YES_OPTION)
		    		  { 
		    			try {
							Query pega = new Query();
							pega.executaUpdate("DELETE FROM vendas WHERE `vendas_id` = " + tabelaUltimasVendas.getValueAt(tabelaUltimasVendas.getSelectedRow(), 0));
							pega.executaUpdate("DELETE FROM vendas_produtos WHERE `id_link` = " + tabelaUltimasVendas.getValueAt(tabelaUltimasVendas.getSelectedRow(), 0));
							DiarioLog.add(Usuario.INSTANCE.getNome(), "Deletou a venda #" + tabelaUltimasVendas.getValueAt(tabelaUltimasVendas.getSelectedRow(), 0) + " de valor R$" + tabelaUltimasVendas.getValueAt(tabelaUltimasVendas.getSelectedRow(), 5) + ".", 7);
							pega.fechaConexao();
							
							SwingUtilities.invokeLater(new Runnable() {  
								public void run() {  
									tabela.removeRow(tabelaUltimasVendas.getSelectedRow());
								}  
							});
						} catch (ClassNotFoundException | SQLException e) {
							e.printStackTrace();
							new PainelErro(e);
						}		    			
		    		 }		    		  
		    	  }
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
}