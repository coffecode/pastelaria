import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import java.awt.Color;
import java.awt.event.*;
import java.io.Serializable;
import java.util.Vector;

public class PainelVendas extends JPanel implements ActionListener, TableModelListener
{
	private JTable tabelaFiados;
	
	public PainelVendas()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(800,600));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setMinimumSize(new Dimension(790, 470));
		tabbedPane.setMaximumSize(new Dimension(790, 470));
		
		JPanel visualizarFiado = new JPanel();
		visualizarFiado.setLayout(new BoxLayout(visualizarFiado, BoxLayout.Y_AXIS));
		visualizarFiado.setPreferredSize(new Dimension(700, 200));
		
		JPanel painelTabela = new JPanel();
		painelTabela.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Dívidas em aberto"));
		painelTabela.setMinimumSize(new Dimension(800, 200));		// Horizontal , Vertical
		painelTabela.setMaximumSize(new Dimension(800, 200));		
		
		DefaultTableModel tabela = new DefaultTableModel() {

		    @Override
		    public boolean isCellEditable(int row, int column) {
		       if(column == 0 || column == 1 || column == 2 || column == 3 || column == 5)
		    	   return true;
		       
		       return false;
		    }
		};
		
		tabela.addColumn("Dívida");
		tabela.addColumn("Nome");
		tabela.addColumn("Apelido");
		tabela.addColumn("Telefone");
		tabela.addColumn("CPF");
		tabela.addColumn("Deletar");
		
		Query pega = new Query();
		pega.executaQuery("SELECT * FROM fiados ORDER BY nome");
		
		while(pega.next())
		{
			Vector<Serializable> linha = new Vector<Serializable>();
			
			double totalDivida = 0.0;
			Query pega2 = new Query();
			pega2.executaQuery("SELECT * FROM vendas WHERE `fiado_id` = " + pega.getInt("fiador_id") + "");
			
			while(pega2.next())
			{
				totalDivida += (Double.parseDouble(pega2.getString("total").replaceAll(",", ".")) - Double.parseDouble(pega2.getString("valor_pago").replaceAll(",", ".")));
			}
			
			String pegaPreco = String.format("%.2f", totalDivida);
			pegaPreco.replaceAll(",", ".");
			
			linha.add(pegaPreco);
			
			linha.add(pega.getString("nome"));
			linha.add(pega.getString("apelido"));
			linha.add(pega.getString("telefone"));
			linha.add(pega.getString("cpf"));			
			
			linha.add("");
			
			if(totalDivida > 0)
			{
				tabela.addRow(linha);
			}
		}
		
		pega.fechaConexao();
		
		tabelaFiados = new JTable() {
		    Color alternate = new Color(141, 182, 205);
		    
		    @Override
		    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		        Component stamp = super.prepareRenderer(renderer, row, column);
		        if (row % 2 == 0 && column != 5 && column != 0)
		            stamp.setBackground(alternate);
		        else
		            stamp.setBackground(this.getBackground());
		        return stamp;
		    }				
		};		
		
		tabelaFiados.setModel(tabela);
		
		
		tabelaFiados.getColumnModel().getColumn(0).setMinWidth(120);
		tabelaFiados.getColumnModel().getColumn(0).setMaxWidth(120);			
		
		tabelaFiados.getColumnModel().getColumn(1).setMinWidth(230);
		tabelaFiados.getColumnModel().getColumn(1).setMaxWidth(230);
		tabelaFiados.getColumnModel().getColumn(4).setMinWidth(140);
		tabelaFiados.getColumnModel().getColumn(4).setMaxWidth(140);	
		
		tabelaFiados.getColumnModel().getColumn(5).setMinWidth(60);
		tabelaFiados.getColumnModel().getColumn(5).setMaxWidth(60);		
		
		tabelaFiados.setRowHeight(30);
		
		DefaultTableCellRenderer centraliza = new DefaultTableCellRenderer();
		centraliza.setHorizontalAlignment( JLabel.CENTER );
		
		tabelaFiados.getColumn("Nome").setCellRenderer(centraliza);
		tabelaFiados.getColumn("Apelido").setCellRenderer(centraliza);
		tabelaFiados.getColumn("Telefone").setCellRenderer(centraliza);
		tabelaFiados.getColumn("CPF").setCellRenderer(centraliza);
		tabelaFiados.getColumn("Dívida").setCellRenderer(centraliza);
		tabelaFiados.getColumn("Dívida").setCellRenderer(new ButtonRenderer());
		tabelaFiados.getColumn("Dívida").setCellEditor(new ButtonEditor(new JCheckBox()));		
		
		tabelaFiados.getColumn("Deletar").setCellRenderer(centraliza);
		tabelaFiados.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
		tabelaFiados.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));
		
		tabelaFiados.getModel().addTableModelListener(this);
		tabelaFiados.setPreferredScrollableViewportSize(new Dimension(750, 140));
		
		JScrollPane scrolltabela = new JScrollPane(tabelaFiados);
		painelTabela.add(scrolltabela);
		visualizarFiado.add(painelTabela);
		
		UltimasVendas painelUltimas = new UltimasVendas();
		
		ImageIcon iconeUltimas = new ImageIcon("imgs/fiado24.png");
		tabbedPane.addTab("Últimas Vendas", iconeUltimas, painelUltimas, "Últimas vendas realizadas.");		

		ImageIcon iconeFinalizar = new ImageIcon("imgs/fiado24.png");
		tabbedPane.addTab("Fiados", iconeFinalizar, visualizarFiado, "Todas as dívidas em aberto.");

		add(tabbedPane);
		ToolTipManager.sharedInstance().setDismissDelay(40000);
	}
	
	class ButtonRenderer extends JButton implements TableCellRenderer {
		
		  public ButtonRenderer() {
		    setOpaque(true);
		  }		  

		  public Component getTableCellRendererComponent(JTable table, Object value,
		      boolean isSelected, boolean hasFocus, int row, int column) {
			  
			  setHorizontalTextPosition(AbstractButton.LEFT);
			  
			  if(column == 5)
				  setIcon(new ImageIcon("imgs/delete.png"));
			  else
				  setIcon(new ImageIcon("imgs/fiados1.png"));
			  
			  	String pegaCPF = (String) table.getValueAt(row,4);
			  	String formataTip = "<html>";
				Query pega = new Query();
				pega.executaQuery("SELECT fiador_id FROM fiados WHERE `cpf` = '" + pegaCPF + "'");
				
				if(pega.next())
				{				
					pega.executaQuery("SELECT * FROM vendas WHERE `fiado_id` = " + pega.getInt("fiador_id") + "");
					
					while(pega.next())
					{
						if((Double.parseDouble(pega.getString("total").replaceAll(",", ".")) > Double.parseDouble(pega.getString("valor_pago").replaceAll(",", "."))))
						{
							formataTip += "<b>Venda #" + pega.getInt("vendas_id") + "</b>  (<i>" + pega.getString("horario") +")</i><br>";
							
							Query pega2 = new Query();
							pega2.executaQuery("SELECT * FROM vendas_produtos WHERE `id_link` = " + pega.getInt("vendas_id"));
							
							while(pega2.next())
							{
								formataTip += pega2.getInt("quantidade_produto") + "x .......... <b>" + pega2.getString("nome_produto") + "</b>";
								
								if(!"".equals(pega2.getString("adicionais_produto").trim()))
								{
									formataTip += " com " + pega2.getString("adicionais_produto");
								}
								
								formataTip += " - R$" +  pega2.getString("preco_produto") + "<br>";
							}
							
							formataTip += "Total: " + pega.getString("total") + " | Pago: " +  pega.getString("valor_pago");
							
							formataTip += "<br><br>";
							pega2.fechaConexao();
						}
					}					
				}
			
			pega.fechaConexao();
			formataTip += "</html>";
			
			setToolTipText(formataTip);
			  
		    if (isSelected) {
		    		setForeground(table.getSelectionForeground());
		    		setBackground(table.getSelectionBackground());
		    } else {
			      setForeground(table.getSelectionForeground());
			      setBackground(table.getSelectionBackground());
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
			  
			button.setHorizontalTextPosition(AbstractButton.LEFT);
		    label = (value == null) ? "" : value.toString();
		    button.setText(label);
		    
			  if(column == 5)
				  button.setIcon(new ImageIcon("imgs/delete.png"));
			  else
				  button.setIcon(new ImageIcon("imgs/fiados1.png"));		    
		    
		    isPushed = true;
		    return button;
		  }

		  public Object getCellEditorValue() {
		    if (isPushed) {
		      if(tabelaFiados.getSelectedRowCount() == 1)
		      {
		    	  if(tabelaFiados.getSelectedColumn() == 0)
		    	  {
		    		  String bla = "Escreva o valor a ser deduzido da dívida.\n";
		    		  bla += "Importante:\n\n 1- Não é possível aumentar a dívida, apenas reduzí-la\n";
		    		  bla += "2 - Não é possível desfazer essa ação.\n3- Se o valor digitado for maior ou igual que o da dívida, a mesma será quitada.\n\n";
		    		  String pegaResposta = JOptionPane.showInputDialog(null, bla, "Reduzir Dívida", JOptionPane.QUESTION_MESSAGE);
		    		  
		    		  if(pegaResposta == null)
		    			  pegaResposta = "";
		    		  
		    		  if(!"".equals(pegaResposta.trim()))
		    		  {
		    			  pegaResposta = pegaResposta.replaceAll("[^0-9.,]+","");
		    			  pegaResposta = pegaResposta.replaceAll(",",".");
		    			  
		    			  double resposta = Double.parseDouble(pegaResposta);
		    			  double deduzindo = resposta;
		    			  
		    			  if(resposta > 0)
		    			  {
		    				  String pegaCPF = (String) tabelaFiados.getValueAt(tabelaFiados.getSelectedRow(), 4);
		    				  
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
		    			  }
		    		  }
		    	  }
		    	  else	// deletar
		    	  {
		    		  int opcao = JOptionPane.showConfirmDialog(null, "Essa opção irá quitar a dívida.\n\nVocê tem certeza?\n\n", "Quitar Dívida", JOptionPane.YES_NO_OPTION);
		    		  
		    		  if(opcao == JOptionPane.YES_OPTION)
		    		  {
		    			  String pegaCPF = (String) tabelaFiados.getValueAt(tabelaFiados.getSelectedRow(), 4);
		    				  
		    			Query pega = new Query();
		    			pega.executaQuery("SELECT fiador_id FROM fiados WHERE `cpf` = '" + pegaCPF + "'");
		    					
		    			if(pega.next())
		    			{		
		    				pega.executaQuery("SELECT * FROM vendas WHERE `fiado_id` = " + pega.getInt("fiador_id") + "");
		    						
		    				while(pega.next())
		    				{
		    					if((Double.parseDouble(pega.getString("total").replaceAll(",", ".")) > Double.parseDouble(pega.getString("valor_pago").replaceAll(",", "."))))
		    					{		    								
		    						Query manda = new Query();
		    								
	    							manda.executaUpdate("UPDATE vendas SET `valor_pago` = '" + pega.getString("total") + "' WHERE `vendas_id` = " + pega.getInt("vendas_id"));
	    							manda.fechaConexao();
		    					}
		    				}
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
	
	public void actionPerformed(ActionEvent e)
	{
		/*if(e.getSource() == procuraCPF)
		{
			
		}*/
	}

	@Override
	public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        if(column == 1 || column == 2 || column == 3){
        	String tipo;
        	if(column==1){
        		tipo = "nome";
        	}else if(column == 2){
        		tipo = "apelido";
        	}
        	else
        	{
        		tipo = "telefone";
        	}
        	
	        TableModel model = (TableModel)e.getSource();
	        String data = (String) model.getValueAt(row, column);
	        String pega = (String) model.getValueAt(row, 4);
	        
		    String formatacao;
		    Query envia = new Query();
		    formatacao = "UPDATE fiados SET " + tipo + " = '" + data + "' WHERE cpf = '" + pega + "'";
		    envia.executaUpdate(formatacao);
        }
	}
}