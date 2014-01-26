package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;

import javax.swing.*;

import java.awt.Font;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.utilitarios.Usuario;

import com.alee.extended.date.WebDateField;
import com.alee.laf.button.WebButton;
import com.alee.laf.scroll.WebScrollPane;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Color;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

public class ConsultarDiario extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JLabel labelDataInicio, labelDataFim, labelProgresso;
	private static JTable tabelaUltimasVendas;
	private static DefaultTableModel tabela;
	private static WebDateField campoInicio, campoFim;
	private static Date dataInicio, dataFim;
	private static WebButton botaoPesquisar, botaoExportar;
	private static JPanel painelBotoes, painelImportar;
	private static com.itextpdf.text.Font catFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 18, com.itextpdf.text.Font.BOLD);
	private static com.itextpdf.text.Font paFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 16, com.itextpdf.text.Font.BOLD);
	private static com.itextpdf.text.Font subFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
	private Document document;
	private Paragraph escrever;
	private Timer t;
	private Query pega3;
	private int index, totalLinhas;
	private JProgressBar progressBar;
	private JFileChooser chooser;
	private String nomeArquivo, dataDia;
	
	public ConsultarDiario()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));	
		
		painelBotoes = new JPanel(new GridBagLayout());
		painelBotoes.setMinimumSize(new Dimension(975, 200));
		painelBotoes.setMaximumSize(new Dimension(1920, 300));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(20,4,4,4);  //top padding
		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		labelDataInicio = new JLabel("Data inicial");
		labelDataInicio.setFont(new Font("Helvetica", Font.BOLD, 16));
		
		labelDataFim = new JLabel("Data final");
		labelDataFim.setFont(new Font("Helvetica", Font.BOLD, 16));		
		
		campoInicio = new WebDateField(new Date());
		campoInicio.setHorizontalAlignment(SwingConstants.CENTER);
		campoInicio.setFont(new Font("Verdana", Font.BOLD, 12));
		campoInicio.setPreferredSize(new Dimension(130, 35));
		campoInicio.setEditable(false);
		
		campoFim = new WebDateField(new Date());
		campoFim.setHorizontalAlignment(SwingConstants.CENTER);
		campoFim.setFont(new Font("Verdana", Font.BOLD, 12));
		campoFim.setPreferredSize(new Dimension(130, 35));
		campoFim.setEditable(false);
		
		botaoPesquisar = new WebButton("Consultar");
		botaoPesquisar.setRolloverShine(true);
		botaoPesquisar.setPreferredSize(new Dimension(160, 70));
		botaoPesquisar.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/pesquisa.png")));
		botaoPesquisar.setFont(new Font("Helvetica", Font.BOLD, 16));
		botaoPesquisar.setHorizontalTextPosition(AbstractButton.LEFT);
		botaoPesquisar.addActionListener(this);
		
		dataInicio = new Date();
		dataFim = new Date();
		
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas
		
		painelBotoes.add(labelDataInicio, gbc);
		
		gbc.insets = new Insets(4,4,20,4);  //top padding
		gbc.gridy = 2;	// linhas		
		
		painelBotoes.add(campoInicio, gbc);
		
		gbc.insets = new Insets(20,60,4,4);  //top padding
		
		gbc.gridx = 3;	// colunas
		gbc.gridy = 1;	// linhas
		
		painelBotoes.add(labelDataFim, gbc);
		
		gbc.insets = new Insets(4,60,20,4);  //top padding
		
		gbc.gridy = 2;	// linhas		
		
		painelBotoes.add(campoFim, gbc);
		
		gbc.gridy = 1;	// linhas
		gbc.gridx = 5;	// colunas
		gbc.insets = new Insets(20,60,20,4);  //top padding
		gbc.gridheight = 2;
		
		painelBotoes.add(botaoPesquisar, gbc);	
		
		JPanel painelTabela = new JPanel(new BorderLayout());
		
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
		tabela.addColumn("Atendente");
		tabela.addColumn("Ação");
		tabela.addColumn("Tipo");
		
		GregorianCalendar now = new GregorianCalendar();
		
		try {
			Query pega = new Query();
			pega.executaQuery("SELECT * FROM diario WHERE data = '"+ now.get(GregorianCalendar.YEAR) + "-"
			+ (now.get(GregorianCalendar.MONTH)+1) + "-" + now.get(GregorianCalendar.DAY_OF_MONTH) + "'");
			
			while(pega.next())
			{	
				Vector<Serializable> linha = new Vector<Serializable>();
				linha.add(pega.getInt("diario_id"));
				linha.add(pega.getString("horario"));
				linha.add(pega.getString("atendente"));
				linha.add(pega.getString("acao"));
				linha.add(pega.getInt("tipo"));
				tabela.addRow(linha);
			}
			
			pega.fechaConexao();
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			new PainelErro(e);
		}
		finally
		{
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
			tabelaUltimasVendas.getColumnModel().getColumn(0).setMinWidth(0);
			tabelaUltimasVendas.getColumnModel().getColumn(0).setMaxWidth(0);
			
			tabelaUltimasVendas.getColumnModel().getColumn(1).setMinWidth(150);
			tabelaUltimasVendas.getColumnModel().getColumn(1).setMaxWidth(350);
			
			tabelaUltimasVendas.getColumnModel().getColumn(2).setMinWidth(150);
			tabelaUltimasVendas.getColumnModel().getColumn(2).setMaxWidth(350);		
			
			tabelaUltimasVendas.getColumnModel().getColumn(3).setMinWidth(400);
			tabelaUltimasVendas.getColumnModel().getColumn(3).setMaxWidth(1400);
			
			tabelaUltimasVendas.getColumnModel().getColumn(4).setMinWidth(0);
			tabelaUltimasVendas.getColumnModel().getColumn(4).setMaxWidth(0);		
			
			tabelaUltimasVendas.setRowHeight(30);
			tabelaUltimasVendas.getTableHeader().setReorderingAllowed(false);
			
			tabelaUltimasVendas.getColumn("Data").setCellRenderer(new CustomRenderer());
			tabelaUltimasVendas.getColumn("Atendente").setCellRenderer(new CustomRenderer());
			tabelaUltimasVendas.getColumn("Ação").setCellRenderer(new CustomRenderer());
			tabelaUltimasVendas.setPreferredScrollableViewportSize(new Dimension(800, 170));
			
			WebScrollPane scrolltabela = new WebScrollPane(tabelaUltimasVendas, true);
			painelTabela.add(scrolltabela, BorderLayout.CENTER);
			
			painelImportar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 14))  {
				
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
			
			painelImportar.setMinimumSize(new Dimension(975, 100));
			painelImportar.setMaximumSize(new Dimension(1920, 100));
			
			botaoExportar = new WebButton("Exportar para PDF ");
			botaoExportar.setRolloverShine(true);
			botaoExportar.setPreferredSize(new Dimension(230, 70));
			botaoExportar.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/export_pdf.png")));
			botaoExportar.setFont(new Font("Helvetica", Font.BOLD, 16));
			botaoExportar.setHorizontalTextPosition(AbstractButton.LEFT);
			botaoExportar.addActionListener(this);
			
			painelImportar.add(botaoExportar);
			
			add(painelBotoes);
			add(painelTabela);
			add(painelImportar);

			ToolTipManager.sharedInstance().setDismissDelay(40000);			
		}
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
	        	if(column != 3)
	        	{
	        		setHorizontalAlignment( JLabel.CENTER );
	        	}

	        	c.setForeground(new Color(72, 61, 139));
	        	return c;
	        }
	        else
	        {
	        	if(column != 3)
	        	{
	        		setHorizontalAlignment( JLabel.CENTER );
	        	}
	        	
	        	c.setForeground(Color.BLACK);
	        	return c;
	        }
	    }
	}		
	
	static public void refresh()
	{	
		try {
			campoInicio.setText("");
			campoFim.setText("");
			tabela.setNumRows(0);
			
			GregorianCalendar now = new GregorianCalendar();
			
			Query pega = new Query();
			pega.executaQuery("SELECT * FROM diario WHERE data = '"+ now.get(GregorianCalendar.YEAR) + "-"
			+ (now.get(GregorianCalendar.MONTH)+1) + "-" + now.get(GregorianCalendar.DAY_OF_MONTH) + "'");
			
			while(pega.next())
			{	
				Vector<Serializable> linha = new Vector<Serializable>();
				linha.add(pega.getInt("diario_id"));
				linha.add(pega.getString("horario"));
				linha.add(pega.getString("atendente"));
				linha.add(pega.getString("acao"));
				linha.add(pega.getInt("tipo"));
				tabela.addRow(linha);
			}
			
			pega.fechaConexao();			

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			new PainelErro(e);
		}
	}
	
	private static void addEmptyLine(Paragraph paragraph, int number) {
	    for (int i = 0; i < number; i++) {
	      paragraph.add(new Paragraph(" "));
	    }
	  }

		@SuppressWarnings("deprecation")
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if(e.getSource() == botaoPesquisar)
			{
				dataInicio = campoInicio.getDate();
				dataFim = campoFim.getDate();
				
				if(dataInicio != null && dataFim != null)
				{
					try {
						tabela.setNumRows(0);
						Query pega = new Query();						
						
						pega.executaQuery("SELECT * FROM diario WHERE data BETWEEN ('"+ (dataInicio.getYear()+1900) + "-"
						+ (dataInicio.getMonth()+1) + "-" + dataInicio.getDate() + "') "
						+ "AND ('" + (dataFim.getYear()+1900) + "-"
						+ (dataFim.getMonth()+1) + "-" + dataFim.getDate() + "') ORDER BY data;");
						
						while(pega.next())
						{	
							Vector<Serializable> linha = new Vector<Serializable>();
							linha.add(pega.getInt("diario_id"));
							linha.add(pega.getString("horario"));
							linha.add(pega.getString("atendente"));
							linha.add(pega.getString("acao"));
							linha.add(pega.getInt("tipo"));
							tabela.addRow(linha);
						}
						
					} catch (ClassNotFoundException | SQLException e1) {
						e1.printStackTrace();
						new PainelErro(e1);
					}
				}
			}
			
			if(e.getSource() == botaoExportar)
			{
				dataInicio = campoInicio.getDate();
				dataFim = campoFim.getDate();

				if(dataInicio != null && dataFim != null)
				{
					nomeArquivo = "/Diario_" + dataInicio.getDate() + "-" + (dataInicio.getMonth()+1) + "-" +
										 (dataInicio.getYear()+1900) + "__" + dataFim.getDate() + "-" + 
										 (dataFim.getMonth()+1) + "-" + (dataFim.getYear()+1900) + ".pdf";
					
					chooser = new JFileChooser(); 
				    chooser.setCurrentDirectory(new java.io.File("."));
				    chooser.setDialogTitle("Selecione a pasta para salvar");
				    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				    chooser.setAcceptAllFileFilterUsed(false);	    
				    
				    if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				    {
						document = new Document();
						try {
							PdfWriter.getInstance(document, new FileOutputStream(chooser.getCurrentDirectory() + "/" + chooser.getSelectedFile().getName() + nomeArquivo));
						} catch (FileNotFoundException | DocumentException e1) {
							JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e1.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
						}
						
						document.open();
						document.addTitle("Diário do Sistema " + campoInicio.getText() + " - " + campoFim.getText());
					    document.addSubject("diário");
					    document.addKeywords("diário, codecoffe, sistema");
					    document.addAuthor("CodeCoffe");
					    document.addCreator("CodeCoffe");
					    
					    Paragraph preface = new Paragraph();
					    addEmptyLine(preface, 1);
					    preface.add(new Paragraph("Diário do Sistema " + campoInicio.getText() + " - " + campoFim.getText(), catFont));
					    addEmptyLine(preface, 1);
					    preface.add(new Paragraph("Relatório gerado por: " + Usuario.getNome() + ", " + new Date(), paFont));
					    try {
							document.add(preface);
						} catch (DocumentException e1) {
							JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e1.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
						}
					    document.newPage();					    
						
				    	pega3 = new Query();
				    	try {
							pega3.executaQuery("SELECT count(*) FROM diario WHERE data BETWEEN ('"+ (dataInicio.getYear()+1900) + "-"
							+ (dataInicio.getMonth()+1) + "-" + dataInicio.getDate() + "') "
							+ "AND ('" + (dataFim.getYear()+1900) + "-"
							+ (dataFim.getMonth()+1) + "-" + dataFim.getDate() + "');");
						} catch (ClassNotFoundException | SQLException e1) {
							document.close();
							pega3.fechaConexao();
							e1.printStackTrace();
							new PainelErro(e1);
						}
						
						if(pega3.next())
							totalLinhas = pega3.getInt(1);
				    	
				    	JFrame salvando = new JFrame();
				    	salvando.setTitle("Exportando Diário para PDF");
				    	salvando.setSize(436, 186);
				    	salvando.setLayout(null);
				    	salvando.setLocationRelativeTo(null);
				    	salvando.setResizable(false);
				    	
				    	labelProgresso = new JLabel();
				    	labelProgresso.setFont(new Font("Helvetica", Font.BOLD, 16));
				    	labelProgresso.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/export_pdf.png")));
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

						try {
							pega3.executaQuery("SELECT * FROM diario WHERE data BETWEEN ('"+ (dataInicio.getYear()+1900) + "-"
							+ (dataInicio.getMonth()+1) + "-" + dataInicio.getDate() + "') "
							+ "AND ('" + (dataFim.getYear()+1900) + "-"
							+ (dataFim.getMonth()+1) + "-" + dataFim.getDate() + "') ORDER BY data;");
							
						} catch (ClassNotFoundException | SQLException e1) {
							document.close();
							pega3.fechaConexao();
							e1.printStackTrace();
							new PainelErro(e1);
						}
						
						index = 1;
						dataDia = null;
						
				    	 t = new Timer(30, new ActionListener()
				    	 {
				    		 public void actionPerformed(ActionEvent ae)
				    		 {				    			 	
									if(pega3.next())
									{
										if(dataDia == null)
										{
											String[] splited = pega3.getString("horario").split("\\s+");
											dataDia = splited[0];
											//escrever = new Paragraph();
											//escrever.add(new Paragraph(dataDia, paFont));
											
											Anchor anchor = new Anchor(" Dia: " + dataDia, catFont);
										    anchor.setName(" Dia: " + dataDia);

										    // Second parameter is the number of the chapter
										    Chapter catPart = new Chapter(new Paragraph(anchor), 1);											
											
											try {
												document.add(catPart);
											} catch (DocumentException e) {
												document.close();
												new PainelErro(e);
											}
										}
										
										String[] splited = pega3.getString("horario").split("\\s+");
										
										if(!dataDia.equals(splited[0]))
										{
											dataDia = splited[0];
											//escrever = new Paragraph();
											//escrever.add(new Paragraph(dataDia, paFont));
											
											Anchor anchor = new Anchor(" Dia: " + dataDia, catFont);
										    anchor.setName(" Dia: " + dataDia);

										    // Second parameter is the number of the chapter
										    Chapter catPart = new Chapter(new Paragraph(anchor), 1);											
											
											try {
												document.add(catPart);
											} catch (DocumentException e) {
												document.close();
												new PainelErro(e);
											}
										}

										escrever = new Paragraph();
										
										labelProgresso.setText("Exportando linha " + index + " de " + totalLinhas + ".");
										progressBar.setValue(index);
										
										escrever.add(new Paragraph("(" + splited[2] + ") " + pega3.getString("atendente") + " - " + pega3.getString("acao"), subFont));									    
									    
									    try {
											document.add(escrever);
										} catch (DocumentException e) {
											document.close();
											new PainelErro(e);
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