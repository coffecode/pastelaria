import java.awt.*;

import javax.swing.*;

import java.awt.Font;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

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
import java.util.Date;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.Vector;

public class ConsultarDiario extends JPanel implements HandleDataSelcionada, ActionListener, FocusListener
{
	private JLabel imagemCalendario1, imagemCalendario2, labelDataInicio, labelDataFim, labelProgresso;
	private JTable tabelaUltimasVendas;
	private static DefaultTableModel tabela;
	private static JTextField campoInicio;
	private static JTextField campoFim;
	private DateChooserPanel selecionaInicio, selecionaFim;
	private GregorianCalendar dataInicio, dataFim;
	private JButton botaoPesquisar, botaoExportar;
	private JPanel painelBotoes, painelImportar;
	
	private static com.itextpdf.text.Font catFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 18, com.itextpdf.text.Font.BOLD);
	private static com.itextpdf.text.Font paFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 16, com.itextpdf.text.Font.BOLD);
	private static com.itextpdf.text.Font subFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
	
	private Document document;
	private Paragraph escrever;
	private static boolean dataSelecionada1;
	private static boolean dataSelecionada2;
	private Timer t;
	private Query pega3;
	private int index, totalLinhas;
	private JProgressBar progressBar;
	private JFileChooser chooser;
	private String nomeArquivo, dataDia;
	
 	double totalTudo;
 	double totalPago;
 	double totalTroco;	
	
	public ConsultarDiario()
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
		tabela.addColumn("Atendente");
		tabela.addColumn("Ação");
		tabela.addColumn("Tipo");
		
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
		tabelaUltimasVendas.getColumnModel().getColumn(0).setMinWidth(0);
		tabelaUltimasVendas.getColumnModel().getColumn(0).setMaxWidth(0);
		
		tabelaUltimasVendas.getColumnModel().getColumn(1).setMinWidth(150);
		tabelaUltimasVendas.getColumnModel().getColumn(1).setMaxWidth(150);
		
		tabelaUltimasVendas.getColumnModel().getColumn(2).setMinWidth(140);
		tabelaUltimasVendas.getColumnModel().getColumn(2).setMaxWidth(140);		
		
		tabelaUltimasVendas.getColumnModel().getColumn(3).setMinWidth(400);
		tabelaUltimasVendas.getColumnModel().getColumn(3).setMaxWidth(470);
		
		tabelaUltimasVendas.getColumnModel().getColumn(4).setMinWidth(0);
		tabelaUltimasVendas.getColumnModel().getColumn(4).setMaxWidth(0);		
		
		tabelaUltimasVendas.setRowHeight(28);
		
		DefaultTableCellRenderer centraliza = new DefaultTableCellRenderer();
		centraliza.setHorizontalAlignment( JLabel.CENTER );
		
		tabelaUltimasVendas.getColumn("Data").setCellRenderer(centraliza);
		tabelaUltimasVendas.getColumn("Atendente").setCellRenderer(centraliza);
		tabelaUltimasVendas.setPreferredScrollableViewportSize(new Dimension(750, 180));
		
		JScrollPane scrolltabela = new JScrollPane(tabelaUltimasVendas);
		painelTabela.add(scrolltabela);
		
		painelImportar = new JPanel(new FlowLayout()) {
			
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
		
		botaoExportar = new JButton("Exportar para PDF ");
		botaoExportar.setPreferredSize(new Dimension(220, 60));
		botaoExportar.setIcon(new ImageIcon(getClass().getResource("imgs/export_pdf.png")));
		botaoExportar.setFont(new Font("Helvetica", Font.BOLD, 16));
		botaoExportar.setHorizontalTextPosition(AbstractButton.LEFT);
		botaoExportar.addActionListener(this);
		
		painelImportar.add(botaoExportar);
		
		add(painelBotoes);
		add(painelTabela);
		add(painelImportar);

		ToolTipManager.sharedInstance().setDismissDelay(40000);
	}
	
	static public void refresh()
	{
		campoInicio.setText("");
		campoFim.setText("");
		tabela.setNumRows(0);
		dataSelecionada1 = false;
		dataSelecionada2 = false;
		
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
					pega.executaQuery("SELECT * FROM diario WHERE data BETWEEN ('"+ dataInicio.get(GregorianCalendar.YEAR) + "-"
					+ (dataInicio.get(GregorianCalendar.MONTH)+1) + "-" + dataInicio.get(GregorianCalendar.DAY_OF_MONTH) + "') "
					+ "AND ('" + dataFim.get(GregorianCalendar.YEAR) + "-"
					+ (dataFim.get(GregorianCalendar.MONTH)+1) + "-" + dataFim.get(GregorianCalendar.DAY_OF_MONTH) + "') ORDER BY data;");
					
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
				}
			}
			
			if(e.getSource() == botaoExportar)
			{
				if(dataSelecionada1 && dataSelecionada2)
				{
					nomeArquivo = "/Diario_" + dataInicio.get(GregorianCalendar.DAY_OF_MONTH) + "-" + (dataInicio.get(GregorianCalendar.MONTH)+1) + "-" +
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
						document = new Document();
						try {
							PdfWriter.getInstance(document, new FileOutputStream(chooser.getCurrentDirectory() + "/" + chooser.getSelectedFile().getName() + nomeArquivo));
						} catch (FileNotFoundException | DocumentException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
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
					    preface.add(new Paragraph("Relatório gerado por: " + PainelStatus.pegaNome() + ", " + new Date(), paFont));
					    try {
							document.add(preface);
						} catch (DocumentException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					    document.newPage();					    
						
				    	pega3 = new Query();
				    	pega3.executaQuery("SELECT count(*) FROM diario WHERE data BETWEEN ('"+ dataInicio.get(GregorianCalendar.YEAR) + "-"
						+ (dataInicio.get(GregorianCalendar.MONTH)+1) + "-" + dataInicio.get(GregorianCalendar.DAY_OF_MONTH) + "') "
						+ "AND ('" + dataFim.get(GregorianCalendar.YEAR) + "-"
						+ (dataFim.get(GregorianCalendar.MONTH)+1) + "-" + dataFim.get(GregorianCalendar.DAY_OF_MONTH) + "');");
						
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

						pega3.executaQuery("SELECT * FROM diario WHERE data BETWEEN ('"+ dataInicio.get(GregorianCalendar.YEAR) + "-"
						+ (dataInicio.get(GregorianCalendar.MONTH)+1) + "-" + dataInicio.get(GregorianCalendar.DAY_OF_MONTH) + "') "
						+ "AND ('" + dataFim.get(GregorianCalendar.YEAR) + "-"
						+ (dataFim.get(GregorianCalendar.MONTH)+1) + "-" + dataFim.get(GregorianCalendar.DAY_OF_MONTH) + "') ORDER BY data;");
						
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
												// TODO Auto-generated catch block
												e.printStackTrace();
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
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}

										escrever = new Paragraph();
										
										labelProgresso.setText("Exportando linha " + index + " de " + totalLinhas + ".");
										progressBar.setValue(index);
										
										escrever.add(new Paragraph("(" + splited[2] + ") " + pega3.getString("atendente") + " - " + pega3.getString("acao"), subFont));									    
									    
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