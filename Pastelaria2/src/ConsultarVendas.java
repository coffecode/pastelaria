import java.awt.*;

import javax.swing.*;

import java.awt.Font;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Color;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.Vector;

public class ConsultarVendas extends JPanel implements HandleDataSelcionada, ActionListener, FocusListener
{
	private JLabel imagemCalendario1, imagemCalendario2, labelDataInicio, labelDataFim, labelProgresso;
	private JTable tabelaUltimasVendas;
	private static DefaultTableModel tabela;
	private static JTextField campoInicio;
	private static JTextField campoFim;
	private DateChooserPanel selecionaInicio, selecionaFim;
	private GregorianCalendar dataInicio, dataFim;
	private JButton botaoPesquisar, botaoExportar, botaoExportar2;
	private JPanel painelBotoes, painelImportar;
	
	private static com.itextpdf.text.Font catFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 18, com.itextpdf.text.Font.BOLD, BaseColor.BLUE);
	private static com.itextpdf.text.Font catFont2 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 18, com.itextpdf.text.Font.BOLD);
	private static com.itextpdf.text.Font catFont3 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
	private static com.itextpdf.text.Font paFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 16, com.itextpdf.text.Font.BOLD);
	private static com.itextpdf.text.Font vendaFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
	private static com.itextpdf.text.Font vendaFontRed = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD, BaseColor.RED);
	private static com.itextpdf.text.Font subFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.ITALIC);
	private static com.itextpdf.text.Font vendaFontFiado = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.NORMAL);
	private static com.itextpdf.text.Font fontDivisao = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD, BaseColor.BLUE);
	
	private Document document;
	private Paragraph escrever;	
	private static boolean dataSelecionada1;
	private static boolean dataSelecionada2;
	private Timer t;
	private Query pega3;
	private int index, totalLinhas, totalVendas, totalFiados;
	private JProgressBar progressBar;
	private HSSFSheet sheet;
	private JFileChooser chooser;
	private HSSFWorkbook wb;
	private String nomeArquivo, dataDia;
	
 	double totalTudo;
 	double totalPago;
 	double totalTroco;	
	
	public ConsultarVendas()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(800, 435));
		
		painelBotoes = new JPanel(new GridBagLayout());
		painelBotoes.setMinimumSize(new Dimension(800, 200));
		painelBotoes.setMaximumSize(new Dimension(800, 200));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4,4,4,4);  //top padding
		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		dataSelecionada1 = false;
		dataSelecionada2 = false;
		
		labelDataInicio = new JLabel("Data inicial");
		labelDataInicio.setFont(new Font("Helvetica", Font.BOLD, 16));
		
		labelDataFim = new JLabel("Data final");
		labelDataFim.setFont(new Font("Helvetica", Font.BOLD, 16));		
		
		campoInicio = new JTextField();
		campoInicio.setHorizontalAlignment(SwingConstants.CENTER);
		campoInicio.setFont(new Font("Verdana", Font.BOLD, 12));
		//campoInicio.setBackground(new Color(141, 182, 205));
		campoInicio.setPreferredSize(new Dimension(120, 25));
		campoInicio.addFocusListener(this);
		
		campoFim = new JTextField();
		campoFim.setHorizontalAlignment(SwingConstants.CENTER);
		campoFim.setFont(new Font("Verdana", Font.BOLD, 12));
		//campoFim.setBackground(new Color(141, 182, 205));
		campoFim.setPreferredSize(new Dimension(120, 25));
		campoFim.addFocusListener(this);
		
		botaoPesquisar = new JButton("Consultar");
		botaoPesquisar.setPreferredSize(new Dimension(140, 60));
		botaoPesquisar.setIcon(new ImageIcon(getClass().getResource("imgs/pesquisa.png")));
		botaoPesquisar.setFont(new Font("Helvetica", Font.BOLD, 16));
		botaoPesquisar.setHorizontalTextPosition(AbstractButton.LEFT);
		botaoPesquisar.addActionListener(this);
		
		imagemCalendario1 = new JLabel(new ImageIcon(getClass().getResource("imgs/calendar.png")));
		imagemCalendario2 = new JLabel(new ImageIcon(getClass().getResource("imgs/calendar.png")));
		
		dataInicio = new GregorianCalendar();
		dataFim = new GregorianCalendar();
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas
		
		painelBotoes.add(labelDataInicio, gbc);
		
		gbc.gridy = 2;	// linhas		
		
		painelBotoes.add(campoInicio, gbc);
		
		gbc.gridx = 2;	// colunas
		
		painelBotoes.add(imagemCalendario1, gbc);
		
		gbc.insets = new Insets(4,60,4,4);  //top padding
		
		gbc.gridx = 3;	// colunas
		gbc.gridy = 1;	// linhas
		
		painelBotoes.add(labelDataFim, gbc);
		
		gbc.gridy = 2;	// linhas		
		
		painelBotoes.add(campoFim, gbc);
		
		gbc.insets = new Insets(4,4,4,4);  //top padding
		
		gbc.gridx = 4;	// colunas
		
		painelBotoes.add(imagemCalendario2, gbc);
		
		gbc.gridy = 1;	// linhas
		gbc.gridx = 5;	// colunas
		gbc.insets = new Insets(4,60,4,4);  //top padding
		gbc.gridheight = 2;
		
		painelBotoes.add(botaoPesquisar, gbc);
		
		JPanel painelTabela = new JPanel();
		painelTabela.setMinimumSize(new Dimension(800, 300));		// Horizontal , Vertical
		painelTabela.setMaximumSize(new Dimension(800, 300));		
		
		tabela = new DefaultTableModel() {

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
		
		tabelaUltimasVendas = new JTable() {
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
		tabelaUltimasVendas.getColumnModel().getColumn(0).setMinWidth(40);
		tabelaUltimasVendas.getColumnModel().getColumn(0).setMaxWidth(40);
		
		tabelaUltimasVendas.getColumnModel().getColumn(1).setMinWidth(150);
		tabelaUltimasVendas.getColumnModel().getColumn(1).setMaxWidth(150);
		
		tabelaUltimasVendas.getColumnModel().getColumn(2).setMinWidth(150);
		tabelaUltimasVendas.getColumnModel().getColumn(2).setMaxWidth(150);		
		
		tabelaUltimasVendas.getColumnModel().getColumn(3).setMinWidth(110);
		tabelaUltimasVendas.getColumnModel().getColumn(3).setMaxWidth(110);	
		
		tabelaUltimasVendas.getColumnModel().getColumn(4).setMinWidth(99);
		tabelaUltimasVendas.getColumnModel().getColumn(4).setMaxWidth(99);
		
		tabelaUltimasVendas.getColumnModel().getColumn(5).setMinWidth(140);
		tabelaUltimasVendas.getColumnModel().getColumn(5).setMaxWidth(140);			
		
		tabelaUltimasVendas.getColumnModel().getColumn(6).setMinWidth(60);
		tabelaUltimasVendas.getColumnModel().getColumn(6).setMaxWidth(60);		
		
		tabelaUltimasVendas.setRowHeight(30);
		
		DefaultTableCellRenderer centraliza = new DefaultTableCellRenderer();
		centraliza.setHorizontalAlignment( JLabel.CENTER );
		
		tabelaUltimasVendas.getColumn("ID").setCellRenderer(centraliza);
		tabelaUltimasVendas.getColumn("Data").setCellRenderer(centraliza);
		tabelaUltimasVendas.getColumn("Atendente").setCellRenderer(centraliza);
		tabelaUltimasVendas.getColumn("Forma de Pagamento").setCellRenderer(centraliza);
		tabelaUltimasVendas.getColumn("Status").setCellRenderer(centraliza);
		tabelaUltimasVendas.getColumn("Total").setCellRenderer(centraliza);		
		tabelaUltimasVendas.getColumn("Total").setCellRenderer(new JLabelRenderer());	
		
		tabelaUltimasVendas.getColumn("Deletar").setCellRenderer(centraliza);
		tabelaUltimasVendas.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
		tabelaUltimasVendas.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));
		tabelaUltimasVendas.setPreferredScrollableViewportSize(new Dimension(750, 180));
		
		JScrollPane scrolltabela = new JScrollPane(tabelaUltimasVendas);
		painelTabela.add(scrolltabela);
		
		painelImportar = new JPanel(new FlowLayout(FlowLayout.CENTER, 45, 0))  {
			
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
		
		painelImportar.setMinimumSize(new Dimension(800, 105));
		painelImportar.setMaximumSize(new Dimension(800, 105));
		
		botaoExportar = new JButton("Exportar para Excel ");
		botaoExportar.setPreferredSize(new Dimension(220, 60));
		botaoExportar.setIcon(new ImageIcon(getClass().getResource("imgs/export_excel.png")));
		botaoExportar.setFont(new Font("Helvetica", Font.BOLD, 16));
		botaoExportar.setHorizontalTextPosition(AbstractButton.LEFT);
		botaoExportar.addActionListener(this);
		
		botaoExportar2 = new JButton("Exportar para PDF ");
		botaoExportar2.setPreferredSize(new Dimension(220, 60));
		botaoExportar2.setIcon(new ImageIcon(getClass().getResource("imgs/export_pdf.png")));
		botaoExportar2.setFont(new Font("Helvetica", Font.BOLD, 16));
		botaoExportar2.setHorizontalTextPosition(AbstractButton.LEFT);
		botaoExportar2.addActionListener(this);
		botaoExportar2.setToolTipText("(Recomendado) - Cria um relatório completo em PDF.");
		
		painelImportar.add(botaoExportar);
		painelImportar.add(botaoExportar2);
		
		add(painelBotoes);
		add(painelTabela);
		add(painelImportar);

		ToolTipManager.sharedInstance().setDismissDelay(40000);
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		if(e.getSource() == campoInicio)
		{
			botaoPesquisar.requestFocus();
			selecionaInicio = new DateChooserPanel();
			selecionaInicio.addEventListener(this);
		}
		
		if(e.getSource() == campoFim)
		{
			botaoPesquisar.requestFocus();
			selecionaFim = new DateChooserPanel();
			selecionaFim.addEventListener(this);
		}		
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	static public void refresh()
	{
		campoInicio.setText("");
		campoFim.setText("");
		tabela.setNumRows(0);
		dataSelecionada1 = false;
		dataSelecionada2 = false;
	}
	
	class JLabelRenderer implements TableCellRenderer {
		
		private JLabel label;
		
		  public JLabelRenderer() {
			  label = new JLabel();
			  label.setOpaque(true);
		  }  

		  public Component getTableCellRendererComponent(JTable table, Object value,
		      boolean isSelected, boolean hasFocus, int row, int column) {	  
			  
			  label.setHorizontalTextPosition(AbstractButton.LEFT);
			  label.setHorizontalAlignment( JLabel.CENTER );
			  label.setIcon(new ImageIcon(getClass().getResource("imgs/documento.png")));
			  
			  	String formataTip = "<html>";
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
			
			label.setToolTipText(formataTip);
			  
		    if (isSelected) {
		    	label.setForeground(table.getSelectionForeground());
		    	label.setBackground(table.getSelectionBackground());
		    } else {
		    	label.setForeground(table.getSelectionForeground());
		    	label.setBackground(table.getSelectionBackground());
		    }		    
		    
		    label.setText((value == null) ? "" : value.toString());
		    return label;
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
		    
		    button.setIcon(new ImageIcon(getClass().getResource("imgs/delete.png")));
		    
			button.setText((value == null) ? "" : value.toString());
		    return button;
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
			  
			button.setIcon(new ImageIcon(getClass().getResource("imgs/delete.png")));
			  
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
		    			  }
		    		  }
		    	  }
		    	  else	// deletar
		    	  {
		    		  int opcao = JOptionPane.showConfirmDialog(null, "Essa opção irá deletar a venda.\n\nVocê tem certeza?\n\n", "Deletar Venda", JOptionPane.YES_NO_OPTION);
		    		  
		    		  if(opcao == JOptionPane.YES_OPTION)
		    		  { 
		    			Query pega = new Query();
		    			pega.executaUpdate("DELETE FROM vendas WHERE `vendas_id` = " + tabelaUltimasVendas.getValueAt(tabelaUltimasVendas.getSelectedRow(), 0));
		    			pega.executaUpdate("DELETE FROM vendas_produtos WHERE `id_link` = " + tabelaUltimasVendas.getValueAt(tabelaUltimasVendas.getSelectedRow(), 0));
		    			DiarioLog.add("Deletou a venda #" + tabelaUltimasVendas.getValueAt(tabelaUltimasVendas.getSelectedRow(), 0) + " de valor R$" + tabelaUltimasVendas.getValueAt(tabelaUltimasVendas.getSelectedRow(), 5) + ".", 7);	
		    			pega.fechaConexao();
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
		
		private static void addEmptyLine(Paragraph paragraph, int number) {
		    for (int i = 0; i < number; i++) {
		      paragraph.add(new Paragraph(" "));
		    }
		  }			

		@Override
		public void handleMyEventClassEvent(EventObject e, GregorianCalendar c) {
			// TODO Auto-generated method stub
			if(e.getSource() == selecionaInicio)
			{
				dataSelecionada1 = true;
				dataInicio = c;
				campoInicio.setText(c.get(GregorianCalendar.DAY_OF_MONTH) + "/" + (c.get(GregorianCalendar.MONTH)+1) + "/" + c.get(GregorianCalendar.YEAR));
			}
			
			if(e.getSource() == selecionaFim)
			{
				dataSelecionada2 = true;
				dataFim = c;
				campoFim.setText(c.get(GregorianCalendar.DAY_OF_MONTH) + "/" + (c.get(GregorianCalendar.MONTH)+1) + "/" + c.get(GregorianCalendar.YEAR));
			}			
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if(e.getSource() == botaoPesquisar)
			{
				if(dataSelecionada1 && dataSelecionada2)
				{
					tabela.setNumRows(0);
					
					Query pega = new Query();
					pega.executaQuery("SELECT * FROM vendas WHERE data BETWEEN ('"+ dataInicio.get(GregorianCalendar.YEAR) + "-"
					+ (dataInicio.get(GregorianCalendar.MONTH)+1) + "-" + dataInicio.get(GregorianCalendar.DAY_OF_MONTH) + "') "
					+ "AND ('" + dataFim.get(GregorianCalendar.YEAR) + "-"
					+ (dataFim.get(GregorianCalendar.MONTH)+1) + "-" + dataFim.get(GregorianCalendar.DAY_OF_MONTH) + "') ORDER BY data;");
					
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
				}
			}
			
			if(e.getSource() == botaoExportar)
			{
				if(dataSelecionada1 && dataSelecionada2)
				{
					nomeArquivo = "/Vendas_" + dataInicio.get(GregorianCalendar.DAY_OF_MONTH) + "-" + (dataInicio.get(GregorianCalendar.MONTH)+1) + "-" +
										 dataInicio.get(GregorianCalendar.YEAR) + "__" + dataFim.get(GregorianCalendar.DAY_OF_MONTH) + "-" + 
										 (dataFim.get(GregorianCalendar.MONTH)+1) + "-" + dataFim.get(GregorianCalendar.YEAR) + ".xls";
					
					chooser = new JFileChooser(); 
				    chooser.setCurrentDirectory(new java.io.File("."));
				    chooser.setDialogTitle("Selecione a pasta para salvar");
				    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				    chooser.setAcceptAllFileFilterUsed(false);
				    
				    UIManager.put("ProgressBar.foreground", new Color(50, 205, 50));			    
				    
				    if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				    {
				    	pega3 = new Query();
				    	pega3.executaQuery("SELECT count(*) FROM vendas WHERE data BETWEEN ('"+ dataInicio.get(GregorianCalendar.YEAR) + "-"
						+ (dataInicio.get(GregorianCalendar.MONTH)+1) + "-" + dataInicio.get(GregorianCalendar.DAY_OF_MONTH) + "') "
						+ "AND ('" + dataFim.get(GregorianCalendar.YEAR) + "-"
						+ (dataFim.get(GregorianCalendar.MONTH)+1) + "-" + dataFim.get(GregorianCalendar.DAY_OF_MONTH) + "');");
						
						if(pega3.next())
							totalLinhas = pega3.getInt(1);
				    	
				    	JFrame salvando = new JFrame();
				    	salvando.setTitle("Exportando Vendas para Excel");
				    	salvando.setSize(436, 186);
				    	salvando.setLayout(null);
				    	salvando.setLocationRelativeTo(null);
				    	salvando.setResizable(false);
				    	
				    	labelProgresso = new JLabel();
				    	labelProgresso.setFont(new Font("Helvetica", Font.BOLD, 16));
				    	labelProgresso.setIcon(new ImageIcon(getClass().getResource("imgs/export_excel.png")));
				    	labelProgresso.setHorizontalTextPosition(AbstractButton.LEFT);
				    	labelProgresso.setBounds(15, 10, 480, 40);				    	
				    	
				    	progressBar = new JProgressBar(0, totalLinhas);
				    	progressBar.setValue(0);
				    	progressBar.setStringPainted(true);
				    	progressBar.setBounds(15, 51, 400, 50); // Coluna, Linha, Largura, Altura
				    	
				    	JTextField campoSalvando = new JTextField();
				    	campoSalvando.setFont(new Font("Verdana", Font.PLAIN, 10));
				    	campoSalvando.setEditable(false);
				    	campoSalvando.setText(chooser.getCurrentDirectory() + "/" + chooser.getSelectedFile().getName() + nomeArquivo);
				    	campoSalvando.setBounds(15, 110, 400, 30);
				    	
				    	salvando.add(campoSalvando);
				    	salvando.add(labelProgresso);
				    	salvando.add(progressBar);
				    	salvando.setVisible(true);			    	
				    	
						wb = new HSSFWorkbook();
						sheet = wb.createSheet("Tabela de Vendas");				
						HSSFRow rowhead = sheet.createRow(0);
						
						CellStyle cs = wb.createCellStyle();
						CellStyle cs2 = wb.createCellStyle();
						org.apache.poi.ss.usermodel.Font f = wb.createFont();
						org.apache.poi.ss.usermodel.Font f2 = wb.createFont();
						cs.setAlignment(CellStyle.ALIGN_CENTER);
						cs2.setAlignment(CellStyle.ALIGN_CENTER);
						
						f.setColor(HSSFColor.BLACK.index);
						f2.setColor(HSSFColor.BLUE.index);
						
						f.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
						f2.setBoldweight(org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD);
						
						cs.setFont(f);
						cs2.setFont(f2);	
						
						sheet.setDefaultColumnStyle(6,cs);
						sheet.setDefaultColumnStyle(7,cs);				
						sheet.setDefaultColumnStyle(0,cs);
						sheet.setDefaultColumnStyle(5,cs2);
						
						rowhead.createCell(0).setCellValue("VENDA ID");
						rowhead.createCell(1).setCellValue("DATA");
						rowhead.createCell(2).setCellValue("DIA SEMANA");
						rowhead.createCell(3).setCellValue("ATENDENTE");
						rowhead.createCell(4).setCellValue("FORMA DE PAGAMENTO");
						rowhead.createCell(5).setCellValue("TOTAL R$");
						rowhead.createCell(6).setCellValue("VALOR_PAGO R$");
						rowhead.createCell(7).setCellValue("TROCO R$");
						
						index = 1;
	    			 	totalTudo = 0.0;
	    			 	totalPago = 0.0;
	    			 	totalTroco = 0.0;						

						pega3.executaQuery("SELECT * FROM vendas WHERE data BETWEEN ('"+ dataInicio.get(GregorianCalendar.YEAR) + "-"
						+ (dataInicio.get(GregorianCalendar.MONTH)+1) + "-" + dataInicio.get(GregorianCalendar.DAY_OF_MONTH) + "') "
						+ "AND ('" + dataFim.get(GregorianCalendar.YEAR) + "-"
						+ (dataFim.get(GregorianCalendar.MONTH)+1) + "-" + dataFim.get(GregorianCalendar.DAY_OF_MONTH) + "') ORDER BY data;");
						
				    	 t = new Timer(50, new ActionListener()
				    	 {
				    		 public void actionPerformed(ActionEvent ae)
				    		 {				    			 	
									if(pega3.next())
									{
										labelProgresso.setText("Exportando venda " + index + " de " + totalLinhas + ".");
										progressBar.setValue(index);							
										
										HSSFRow row = sheet.createRow(index);
										row.createCell(0).setCellValue(pega3.getInt("vendas_id"));
										row.createCell(1).setCellValue(pega3.getString("horario"));
										
										switch(pega3.getInt("dia_semana"))
										{
											case 1:
											{
												row.createCell(2).setCellValue("Domingo");
												break;
											}
											case 2:
											{
												row.createCell(2).setCellValue("Segunda-feira");
												break;
											}
											case 3:
											{
												row.createCell(2).setCellValue("Terça-feira");
												break;
											}
											case 4:
											{
												row.createCell(2).setCellValue("Quarta-feira");
												break;
											}	
											case 5:
											{
												row.createCell(2).setCellValue("Quinta-feira");
												break;
											}
											case 6:
											{
												row.createCell(2).setCellValue("Sexta-feira");
												break;
											}
											case 7:
											{
												row.createCell(2).setCellValue("Sábado");
											}										
										}
										
										row.createCell(3).setCellValue(pega3.getString("atendente"));
										row.createCell(4).setCellValue(pega3.getString("forma_pagamento"));
										row.createCell(5).setCellValue(pega3.getString("total").replaceAll(",", "."));
										
										totalTudo += Double.parseDouble(pega3.getString("total").replaceAll(",", "."));
										
										if((Double.parseDouble(pega3.getString("total").replaceAll(",", ".")) > Double.parseDouble(pega3.getString("valor_pago").replaceAll(",", "."))))
										{
											if(pega3.getString("forma_pagamento").equals("Fiado"))
											{
												row.createCell(6).setCellValue(pega3.getString("valor_pago").replaceAll(",", "."));
												totalPago += Double.parseDouble(pega3.getString("valor_pago").replaceAll(",", "."));
											}
											else
											{
												row.createCell(6).setCellValue(pega3.getString("valor_pago").replaceAll(",", "."));
												totalPago += Double.parseDouble(pega3.getString("total").replaceAll(",", "."));
											}
										}
										else
										{
											row.createCell(6).setCellValue(pega3.getString("valor_pago").replaceAll(",", "."));
											totalPago += Double.parseDouble(pega3.getString("valor_pago").replaceAll(",", "."));
										}
										
										row.createCell(7).setCellValue(pega3.getString("troco").replaceAll(",", "."));
										totalTroco += Double.parseDouble(pega3.getString("troco").replaceAll(",", "."));
										index++;
									}
									else
									{
								    	pega3.fechaConexao();
								    	
										String resultado = String.format("%.2f", totalTudo);
										String resultado2 = String.format("%.2f", totalPago);
										String resultado3 = String.format("%.2f", totalTroco);
										resultado.replaceAll(",", ".");
										resultado2.replaceAll(",", ".");	
										resultado3.replaceAll(",", ".");
										
										sheet.createRow(index+1);
										HSSFRow row = sheet.createRow(index+2);
										
										row.createCell(4).setCellValue("TOTAL: ");
										row.createCell(5).setCellValue(resultado);
										row.createCell(6).setCellValue(resultado2);
										row.createCell(7).setCellValue(resultado3);
										
										sheet.autoSizeColumn(0);
										sheet.autoSizeColumn(1);
										sheet.autoSizeColumn(2);
										sheet.autoSizeColumn(3);
										sheet.autoSizeColumn(4);
										sheet.autoSizeColumn(5);
										sheet.autoSizeColumn(6);
										sheet.autoSizeColumn(7);
										sheet.autoSizeColumn(8);
										
										try {
											FileOutputStream fileOut = new FileOutputStream(chooser.getCurrentDirectory() + "/" + chooser.getSelectedFile().getName() + nomeArquivo);
											wb.write(fileOut);
											fileOut.close();
											labelProgresso.setText("Finalizado.");
										} catch (IOException ex) {
											// TODO Auto-generated catch block
											ex.printStackTrace();
										}
										
										t.stop();
									}
				    		 }
				    	});
				    	 
				    	 t.start();			    	
				    }						
				}				
			}
			if(e.getSource() == botaoExportar2)
			{
				if(dataSelecionada1 && dataSelecionada2)
				{
					nomeArquivo = "/Vendas_" + dataInicio.get(GregorianCalendar.DAY_OF_MONTH) + "-" + (dataInicio.get(GregorianCalendar.MONTH)+1) + "-" +
										 dataInicio.get(GregorianCalendar.YEAR) + "__" + dataFim.get(GregorianCalendar.DAY_OF_MONTH) + "-" + 
										 (dataFim.get(GregorianCalendar.MONTH)+1) + "-" + dataFim.get(GregorianCalendar.YEAR) + ".pdf";
					
					chooser = new JFileChooser(); 
				    chooser.setCurrentDirectory(new java.io.File("."));
				    chooser.setDialogTitle("Selecione a pasta para salvar");
				    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				    chooser.setAcceptAllFileFilterUsed(false);
				    
				    UIManager.put("ProgressBar.foreground", new Color(255, 72, 77));			    
				    
				    if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				    {
				    	pega3 = new Query();
						document = new Document();
						
						try {
							PdfWriter.getInstance(document, new FileOutputStream(chooser.getCurrentDirectory() + "/" + chooser.getSelectedFile().getName() + nomeArquivo));
						} catch (FileNotFoundException | DocumentException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						index = 1;
						dataDia = null;
	    			 	totalTudo = 0.0;
	    			 	totalPago = 0.0;
	    			 	totalVendas = 0;
	    			 	totalFiados = 0;						
						
						pega3.executaQuery("SELECT * FROM vendas WHERE data BETWEEN ('"+ dataInicio.get(GregorianCalendar.YEAR) + "-"
						+ (dataInicio.get(GregorianCalendar.MONTH)+1) + "-" + dataInicio.get(GregorianCalendar.DAY_OF_MONTH) + "') "
						+ "AND ('" + dataFim.get(GregorianCalendar.YEAR) + "-"
						+ (dataFim.get(GregorianCalendar.MONTH)+1) + "-" + dataFim.get(GregorianCalendar.DAY_OF_MONTH) + "') ORDER BY data;");
	    			 	
	    			 	while(pega3.next())
	    			 	{
	    			 		totalVendas++;
	    			 		totalTudo += Double.parseDouble(pega3.getString("total").replaceAll(",", "."));
	    			 		totalPago += Double.parseDouble(pega3.getString("valor_pago").replaceAll(",", "."));
	    			 		
	    			 		if(pega3.getString("forma_pagamento").equals("Fiado"))
	    			 			totalFiados++;
	    			 	}
	    			 	
	    			 	totalLinhas = totalVendas;
	    			 	
						document.open();
						document.addTitle("Vendas do Sistema " + campoInicio.getText() + " - " + campoFim.getText());
					    document.addSubject("vendas");
					    document.addKeywords("diário, codecoffe, sistema");
					    document.addAuthor("CodeCoffe");
					    document.addCreator("CodeCoffe");
					    
						String resultado = String.format("%.2f", totalTudo);
						String resultado2 = String.format("%.2f", totalPago);
						resultado.replaceAll(",", ".");
						resultado2.replaceAll(",", ".");					    
					    
					    Paragraph preface = new Paragraph();
					    addEmptyLine(preface, 1);
					    preface.add(new Paragraph("Vendas do Sistema " + campoInicio.getText() + " - " + campoFim.getText(), catFont2));
					    addEmptyLine(preface, 1);
					    preface.add(new Paragraph("Relatório gerado por: " + PainelStatus.pegaNome() + ", " + new Date(), paFont));
					    addEmptyLine(preface, 15);
					    preface.add(new Paragraph("Total de Vendas: " + totalVendas, catFont3));
					    preface.add(new Paragraph("Total de Fiados: " + totalFiados, catFont3));
					    addEmptyLine(preface, 1);
					    preface.add(new Paragraph("Total: R$" + resultado, catFont3));
					    preface.add(new Paragraph("Total Pago: R$" + resultado2, catFont3));
					    addEmptyLine(preface, 12);
					    preface.add(new Paragraph("Sistema de Caixa by CodeCoffe (C) - 2014", paFont));
					    
					    try {
							document.add(preface);
						} catch (DocumentException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					    
					    document.newPage();
					    
					    Paragraph divisaoProdutos1 = new Paragraph();
					    divisaoProdutos1.add(new Paragraph("Produtos Vendidos no Período:", fontDivisao));
					    addEmptyLine(divisaoProdutos1, 1);
						try {
							document.add(divisaoProdutos1);
						} catch (DocumentException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}						    
					    
						pega3.executaQuery("SELECT * FROM produtos WHERE tipo = 1 ORDER BY nome");
						
						while(pega3.next())
						{
							int qntVendas = 0;
							escrever = new Paragraph();
							Query pega4 = new Query();
							
							pega4.executaQuery("SELECT * FROM vendas_produtos WHERE nome_produto = '" + pega3.getString("nome") + "' AND data BETWEEN ('"+ dataInicio.get(GregorianCalendar.YEAR) + "-"
									+ (dataInicio.get(GregorianCalendar.MONTH)+1) + "-" + dataInicio.get(GregorianCalendar.DAY_OF_MONTH) + "') "
									+ "AND ('" + dataFim.get(GregorianCalendar.YEAR) + "-"
									+ (dataFim.get(GregorianCalendar.MONTH)+1) + "-" + dataFim.get(GregorianCalendar.DAY_OF_MONTH) + "') ORDER BY data;");
							
							while(pega4.next())
							{
								qntVendas += pega4.getInt("quantidade_produto");
							}
							
							pega4.fechaConexao();
							escrever.add(new Paragraph(pega3.getString("nome") + ": " + qntVendas + " vezes.", catFont3));
							try {
								document.add(escrever);
							} catch (DocumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

					    Paragraph divisaoProdutos = new Paragraph();
					    addEmptyLine(divisaoProdutos, 2);
					    divisaoProdutos.add(new Paragraph("Adicionais Vendidos no Período:", fontDivisao));
					    addEmptyLine(divisaoProdutos, 1);
						try {
							document.add(divisaoProdutos);
						} catch (DocumentException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}					    
					    
						pega3.executaQuery("SELECT * FROM produtos WHERE tipo = 2 ORDER BY nome");
						
						while(pega3.next())
						{
							int qntVendas = 0;
							escrever = new Paragraph();
							Query pega4 = new Query();
							
							pega4.executaQuery("SELECT * FROM vendas_produtos WHERE adicionais_produto LIKE '%" + pega3.getString("nome") + "%' AND data BETWEEN ('"+ dataInicio.get(GregorianCalendar.YEAR) + "-"
									+ (dataInicio.get(GregorianCalendar.MONTH)+1) + "-" + dataInicio.get(GregorianCalendar.DAY_OF_MONTH) + "') "
									+ "AND ('" + dataFim.get(GregorianCalendar.YEAR) + "-"
									+ (dataFim.get(GregorianCalendar.MONTH)+1) + "-" + dataFim.get(GregorianCalendar.DAY_OF_MONTH) + "') ORDER BY data;");
							
							while(pega4.next())
							{
								int pos = -1;  
								int contagem = 0;  
								while (true) {  
								    pos = pega4.getString("adicionais_produto").indexOf(pega3.getString("nome"), pos + 1);   
								    if (pos < 0) break;  
								    contagem++;  
								}
								
								qntVendas += (pega4.getInt("quantidade_produto") * contagem);
							}
							
							pega4.fechaConexao();
							escrever.add(new Paragraph(pega3.getString("nome") + ": " + qntVendas + " vezes.", catFont3));
							try {
								document.add(escrever);
							} catch (DocumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}						
						
						document.newPage();
				    	
				    	JFrame salvando = new JFrame();
				    	salvando.setTitle("Exportando Vendas para PDF");
				    	salvando.setSize(436, 186);
				    	salvando.setLayout(null);
				    	salvando.setLocationRelativeTo(null);
				    	salvando.setResizable(false);
				    	
				    	labelProgresso = new JLabel();
				    	labelProgresso.setFont(new Font("Helvetica", Font.BOLD, 16));
				    	labelProgresso.setIcon(new ImageIcon(getClass().getResource("imgs/export_pdf.png")));
				    	labelProgresso.setHorizontalTextPosition(AbstractButton.LEFT);
				    	labelProgresso.setBounds(15, 10, 480, 40);				    	
				    	
				    	progressBar = new JProgressBar(0, totalLinhas);
				    	progressBar.setValue(0);
				    	progressBar.setStringPainted(true);
				    	progressBar.setBounds(15, 51, 400, 50); // Coluna, Linha, Largura, Altura
				    	
				    	JTextField campoSalvando = new JTextField();
				    	campoSalvando.setFont(new Font("Verdana", Font.PLAIN, 10));
				    	campoSalvando.setEditable(false);
				    	campoSalvando.setText(chooser.getCurrentDirectory() + "/" + chooser.getSelectedFile().getName() + nomeArquivo);
				    	campoSalvando.setBounds(15, 110, 400, 30);
				    	
				    	salvando.add(campoSalvando);
				    	salvando.add(labelProgresso);
				    	salvando.add(progressBar);
				    	salvando.setVisible(true);				
	    			 	
						pega3.executaQuery("SELECT * FROM vendas WHERE data BETWEEN ('"+ dataInicio.get(GregorianCalendar.YEAR) + "-"
						+ (dataInicio.get(GregorianCalendar.MONTH)+1) + "-" + dataInicio.get(GregorianCalendar.DAY_OF_MONTH) + "') "
						+ "AND ('" + dataFim.get(GregorianCalendar.YEAR) + "-"
						+ (dataFim.get(GregorianCalendar.MONTH)+1) + "-" + dataFim.get(GregorianCalendar.DAY_OF_MONTH) + "') ORDER BY data;");	    			 	
						
				    	 t = new Timer(50, new ActionListener()
				    	 {
				    		 public void actionPerformed(ActionEvent ae)
				    		 {				    			 	
									if(pega3.next())
									{
										if(dataDia == null)
										{							
											String[] splited = pega3.getString("horario").split("\\s+");
											dataDia = splited[0];
											escrever = new Paragraph();
											addEmptyLine(escrever, 1);
											
											switch(pega3.getInt("dia_semana"))
											{
												case 1:
												{
													escrever.add(new Paragraph(dataDia + " - Domingo", catFont));
													break;
												}
												case 2:
												{
													escrever.add(new Paragraph(dataDia + " - Segunda-feira", catFont));
													break;
												}
												case 3:
												{
													escrever.add(new Paragraph(dataDia + " - Terça-feira", catFont));
													break;
												}
												case 4:
												{
													escrever.add(new Paragraph(dataDia + " - Quarta-feira", catFont));
													break;
												}	
												case 5:
												{
													escrever.add(new Paragraph(dataDia + " - Quinta-feira", catFont));
													break;
												}
												case 6:
												{
													escrever.add(new Paragraph(dataDia + " - Sexta-feira", catFont));
													break;
												}
												case 7:
												{
													escrever.add(new Paragraph(dataDia + " - Sábado", catFont));
												}										
											}																						
											
											try {
												document.add(escrever);
											} catch (DocumentException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
										
										String[] splited = pega3.getString("horario").split("\\s+");
										
										if(!dataDia.equals(splited[0]))
										{
											dataDia = splited[0];
											escrever = new Paragraph();
											addEmptyLine(escrever, 1);
											
											switch(pega3.getInt("dia_semana"))
											{
												case 1:
												{
													escrever.add(new Paragraph(dataDia + " - Domingo", catFont));
													break;
												}
												case 2:
												{
													escrever.add(new Paragraph(dataDia + " - Segunda-feira", catFont));
													break;
												}
												case 3:
												{
													escrever.add(new Paragraph(dataDia + " - Terça-feira", catFont));
													break;
												}
												case 4:
												{
													escrever.add(new Paragraph(dataDia + " - Quarta-feira", catFont));
													break;
												}	
												case 5:
												{
													escrever.add(new Paragraph(dataDia + " - Quinta-feira", catFont));
													break;
												}
												case 6:
												{
													escrever.add(new Paragraph(dataDia + " - Sexta-feira", catFont));
													break;
												}
												case 7:
												{
													escrever.add(new Paragraph(dataDia + " - Sábado", catFont));
												}										
											}												
											
											try {
												document.add(escrever);
											} catch (DocumentException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}

										escrever = new Paragraph();
										
										labelProgresso.setText("Exportando venda " + index + " de " + totalLinhas + ".");
										progressBar.setValue(index);
										
										if((Double.parseDouble(pega3.getString("total").replaceAll(",", ".")) > Double.parseDouble(pega3.getString("valor_pago").replaceAll(",", "."))))
										{
											if(pega3.getString("forma_pagamento").equals("Fiado"))
											{
												escrever.add(new Paragraph(("(" + splited[2] + ") " + pega3.getString("atendente") + " - " + pega3.getString("forma_pagamento") + " - Total: R$" + pega3.getString("total") + " - " + "Valor Pago: R$" + pega3.getString("valor_pago")).replaceAll("\\s+", "  "), vendaFontRed));
											}
											else
											{
												escrever.add(new Paragraph(("(" + splited[2] + ") " + pega3.getString("atendente") + " - " + pega3.getString("forma_pagamento") + " - Total: R$" + pega3.getString("total") + " - " + "Valor Pago: R$" + pega3.getString("total")).replaceAll("\\s+", "  "), vendaFont));
											}
										}
										else
										{
											escrever.add(new Paragraph(("(" + splited[2] + ") " + pega3.getString("atendente") + " - " + pega3.getString("forma_pagamento") + " - Total: R$" + pega3.getString("total") + " - " + "Valor Pago: R$" + pega3.getString("valor_pago")).replaceAll("\\s+", "  "), vendaFont));
										}
										
										if(pega3.getString("forma_pagamento").equals("Fiado"))
										{
											Query pega4 = new Query();
											pega4.executaQuery("SELECT * FROM fiados WHERE `fiador_id` = " + pega3.getInt("fiado_id"));
											
											if(pega4.next())
											{
												escrever.add(new Paragraph(("Fiado para: " + pega4.getString("nome") + " (" + pega4.getString("apelido") + ") - TEL: " + pega4.getString("telefone") + " - CPF: " + pega4.getString("cpf")).replaceAll("\\s+", "  "), vendaFontFiado));
											}
											
											pega4.fechaConexao();
										}
										
										Query pega = new Query();
										pega.executaQuery("SELECT * FROM vendas_produtos WHERE `id_link` = " + pega3.getInt("vendas_id"));
										
										while(pega.next())
										{	
											if(!"".equals(pega.getString("adicionais_produto").trim()))
											{
												escrever.add(new Paragraph(("   " + pega.getInt("quantidade_produto") + "x...... " + pega.getString("nome_produto") + " com " + pega.getString("adicionais_produto") + " [R$" +  pega.getString("preco_produto") + "]").replaceAll("\\s+", "  "), subFont));
											}
											else
											{
												escrever.add(new Paragraph(("   " + pega.getInt("quantidade_produto") + "x...... " + pega.getString("nome_produto") + " [R$" +  pega.getString("preco_produto") + "]").replaceAll("\\s+", "  "), subFont));
											}
										}
										
										addEmptyLine(escrever, 1);
										pega.fechaConexao();
									    
									    try {
											document.add(escrever);
										} catch (DocumentException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										index++;
									}
									else
									{
								    	pega3.fechaConexao();
										document.close();
										labelProgresso.setText("Finalizado.");
										t.stop();
									}
				    		 }
				    	});
				    	 
				    	 t.start();			    	
				    }						
				}				
			}			
		}
	}