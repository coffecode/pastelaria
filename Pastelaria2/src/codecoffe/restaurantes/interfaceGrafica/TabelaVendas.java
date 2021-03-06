package codecoffe.restaurantes.interfaceGrafica;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.DiarioLog;
import codecoffe.restaurantes.utilitarios.Usuario;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.extended.date.WebDateField;
import com.alee.extended.window.PopOverDirection;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;

public class TabelaVendas extends WebPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final int vendasPagina = 80;
	private WebPopOver popOver;
	private JPopupMenu popup;
	private JTable tabela;
	private DefaultTableModel tabelaModel;
	private WebDateField dataInicial, dataFinal;
	private WebButton pesquisar, exportarPDF, exportarExcel, opcoesTabela;
	private JComboBox<String> paginacao;
	private boolean configDelivery, configDez;
	private String pesquisaFormat;
	
	private JComboBox<String> filtroCampoPagamento, filtroCampoStatus, filtroLocal;
	private JComboBox<String> filtroCampoDelivery, filtroCampoDezPorcento;
	private WebComboBox filtroCampoFuncionario;
	
	public TabelaVendas()
	{
		configDez = (Configuracao.INSTANCE.getDezPorcento() || Configuracao.INSTANCE.isDezPorcentoRapida());
		if(Configuracao.INSTANCE.getTaxaEntrega() > 0)	configDelivery = true;
		else
			configDelivery = false;
		
		setLayout(new MigLayout("fill", "15[]15[]20[]15", "10[]10[]"));
		
		tabela = new JTable();
		tabelaModel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				if(column == (tabela.getColumnCount()-1)) return true;
				return false;
			}
		};
		
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tabela.getSelectedRowCount() == 1)
				{
					new VisualizarVenda(Integer.parseInt(tabela.getValueAt(tabela.getSelectedRow(), 0).toString()))
					.setLocationRelativeTo(SwingUtilities.getRoot(popup));
				}
			}
		};
		
        popup = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Ver detalhes...");
        menuItem.addActionListener(al);
        popup.add(menuItem);
		
		tabela.addMouseListener(new MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    JTable source = (JTable)e.getSource();
                    int row = source.rowAtPoint( e.getPoint() );
                    int column = source.columnAtPoint( e.getPoint() );

                    if (!source.isRowSelected(row))
                        source.changeSelection(row, column, false, false);

                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
		
		tabelaModel.addColumn("ID");
		tabelaModel.addColumn("Data");
		tabelaModel.addColumn("Pagamento");
		tabelaModel.addColumn("Total");
		tabelaModel.addColumn("Status");
		tabelaModel.addColumn("Atendente");
		
		if(configDelivery)
			tabelaModel.addColumn("Delivery");
		
		if(configDez)
			tabelaModel.addColumn("10%");
		
		tabelaModel.addColumn("Deletar");
		
		tabela.setModel(tabelaModel);
		tabela.setRowHeight(29);
		tabela.getTableHeader().setReorderingAllowed(false);
		tabela.setFocusable(false);
		tabela.setDefaultRenderer(Object.class, new TabelaVendasRenderer());
		
		tabela.getColumn("ID").setPreferredWidth(80);
		tabela.getColumn("Status").setPreferredWidth(70);
		tabela.getColumn("Data").setPreferredWidth(150);
		tabela.getColumn("Pagamento").setPreferredWidth(150);
		tabela.getColumn("Total").setPreferredWidth(120);
		tabela.getColumn("Atendente").setPreferredWidth(200);
		
		if(configDelivery)
			tabela.getColumn("Delivery").setPreferredWidth(70);
		
		if(configDez)
			tabela.getColumn("10%").setPreferredWidth(60);
		
		tabela.getColumn("Deletar").setMinWidth(60);
		tabela.getColumn("Deletar").setMaxWidth(60);
		tabela.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox()));
		
		dataInicial = new WebDateField(new Date());
		dataInicial.setHorizontalAlignment(SwingConstants.CENTER);
		dataInicial.setMinimumSize(new Dimension(130, 35));
		dataInicial.setEditable(false);
		
		dataFinal = new WebDateField(new Date());
		dataFinal.setHorizontalAlignment(SwingConstants.CENTER);
		dataFinal.setMinimumSize(new Dimension(130, 35));
		dataFinal.setEditable(false);
		
		pesquisar = new WebButton("Pesquisar");
		pesquisar.setRolloverShine(true);
		pesquisar.setPreferredSize(new Dimension(120, 35));
		pesquisar.setHorizontalTextPosition(AbstractButton.LEFT);
		pesquisar.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/pesquisa_mini.png")));
		pesquisar.addActionListener(this);
		
		opcoesTabela = new WebButton();
		opcoesTabela.setToolTipText("Filtro");
		opcoesTabela.setRolloverDecoratedOnly(true);
		opcoesTabela.setPreferredSize(new Dimension(32, 32));
		opcoesTabela.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/opcoes_mini.png")));
		opcoesTabela.addActionListener(this);
		
		exportarPDF = new WebButton();
		exportarPDF.setToolTipText("Exportar para PDF");
		exportarPDF.setRolloverDecoratedOnly(true);
		exportarPDF.setPreferredSize(new Dimension(32, 32));
		exportarPDF.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/export_pdf.png")));
		exportarPDF.addActionListener(this);
		
		exportarExcel = new WebButton();
		exportarExcel.setToolTipText("Exportar para Excel");
		exportarExcel.setRolloverDecoratedOnly(true);
		exportarExcel.setPreferredSize(new Dimension(32, 32));
		exportarExcel.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/export_excel.png")));
		exportarExcel.addActionListener(this);
		
		WebScrollPane scrolltabela = new WebScrollPane(tabela, true);
		scrolltabela.getViewport().setBackground(new Color(237, 237, 237));
		scrolltabela.setFocusable(false);
		
		String[] paginas = {"P�gina 1/1"};
		paginacao = new JComboBox<String>(paginas);
		paginacao.setMaximumRowCount(5);
		paginacao.setMinimumSize(new Dimension(120, 30));
		
		add(new JLabel("In�cio: "), "gapleft 10px, split 5");
		add(dataInicial, "gapleft 10px");
		add(new JLabel("Fim: "), "gapleft 20px");
		add(dataFinal, "gapleft 10px");
		add(pesquisar, "gapleft 30px");
		add(opcoesTabela, "align 100%, gapleft 30px, split 3, span");
		add(exportarPDF, "align 100%");
		add(exportarExcel, "align 100%, wrap");
		add(scrolltabela, "grow, pushy, span, wrap");
		add(paginacao, "align 100%, span");
		
		ToolTipManager.sharedInstance().setInitialDelay(0);
		
		paginacao.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if(!UtilCoffe.vaziu(pesquisaFormat))
					{
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								int start = (paginacao.getSelectedIndex() * vendasPagina);
								pesquisarVendas(start, vendasPagina);
							}
						});
					}
				}
			}
		});
		
		/* CRIAR POPUP DE FILTRO */
		
		popOver = new WebPopOver(opcoesTabela);
		popOver.setModal(true);
		popOver.setMargin(10);
		popOver.setMovable(false);
		popOver.setLayout(new MigLayout());
		
		List<String> locais = new ArrayList<String>();
		locais.add("Todos");
		locais.add("Balc�o");
		
		if(Configuracao.INSTANCE.getMesas() > 0)
			for(int i = 0; i < Configuracao.INSTANCE.getMesas(); i++)
				locais.add(Configuracao.INSTANCE.getTipoNome() + " " + (i+1));
		
		filtroLocal = new JComboBox<String>();
		filtroLocal.setModel(new DefaultComboBoxModel<String>());
		filtroLocal.setPreferredSize(new Dimension(120, 30));
		
		for(int i = 0; i < locais.size(); i++)
			filtroLocal.addItem(locais.get(i));
		
		String[] opcoesDelivery = {"Todos", "Apenas Delivery", "Sem Delivery"};
		filtroCampoDelivery = new JComboBox<String>(opcoesDelivery);
		filtroCampoDelivery.setPreferredSize(new Dimension(120, 30));
		
		String[] opcoesTaxa = {"Todos", "Apenas com 10% Opcional", "Sem 10% Opcional"};
		filtroCampoDezPorcento = new JComboBox<String>(opcoesTaxa);
		filtroCampoDezPorcento.setPreferredSize(new Dimension(160, 30));
		
		String[] opcoesPagamento = {"Todos", "Dinheiro", "Cart�o de Cr�dito", "Ticket Refei��o", "Fiado"};
		filtroCampoPagamento = new JComboBox<String>(opcoesPagamento);
		filtroCampoPagamento.setPreferredSize(new Dimension(150, 30));
		
		String[] opcoesStatus = {"Todos", "Pago", "N�o Pago"};
		filtroCampoStatus = new JComboBox<String>(opcoesStatus);
		filtroCampoStatus.setPreferredSize(new Dimension(100, 30));
		
		List<String> opcoesFuncionarios = new ArrayList<String>();
		opcoesFuncionarios.add("Todos");
		
		try {
			Query pega = new Query();
			pega.executaQuery("SELECT nome FROM funcionarios ORDER BY nome");
			
			while(pega.next()) {
				opcoesFuncionarios.add(pega.getString("nome"));
			}
			
			pega.fechaConexao();
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
			new PainelErro(e1);
		}
		
		filtroCampoFuncionario = new WebComboBox(opcoesFuncionarios.toArray());
		filtroCampoFuncionario.setPreferredSize(new Dimension(150, 30));
		
		popOver.add(new JLabel("<html><b>Filtro de Pesquisa</b></html>"), "wrap, span, align center");
		popOver.add(new JLabel("Formas de Pagamento:"), "gaptop 20px");
		popOver.add(filtroCampoPagamento, "gapleft 15px, wrap");
		popOver.add(new JLabel("Status da Venda:"));
		popOver.add(filtroCampoStatus, "gapleft 15px, wrap");
		popOver.add(new JLabel("Atendido por:"));
		popOver.add(filtroCampoFuncionario, "gapleft 15px, wrap");
		popOver.add(new JLabel("Local:"));
		popOver.add(filtroLocal, "gapleft 15px, wrap");
		
		if(configDelivery) {
			popOver.add(new JLabel("Delivery:"));
			popOver.add(filtroCampoDelivery, "gapleft 15px, wrap");
		}
		
		if(configDez) {
			popOver.add(new JLabel("10% Opcional:"));
			popOver.add(filtroCampoDezPorcento, "gapleft 15px, wrap");
		}
		
		JButton close = new JButton("Fechar");
		
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popOver.dispose();
			}
		});
		
		popOver.add(close, "gaptop 15px, span, align right");
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == pesquisar)
		{
			pesquisaFormat = gerarPesquisa();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						Query pega = new Query();
						pega.executaQuery(pesquisaFormat);
						int quantidade = pega.getRowCount();
						pega.fechaConexao();
						
						gerarPaginas((int) Math.ceil(((double) quantidade)/((double) vendasPagina)));
						
						pesquisaFormat += " LIMIT ";
						pesquisarVendas(0, vendasPagina);
					} catch (ClassNotFoundException | SQLException e1) {
						e1.printStackTrace();
						new PainelErro(e1);
					}
				}
			});
		}
		else if(e.getSource() == exportarPDF)
		{
			
		}
		else if(e.getSource() == exportarExcel)
		{
			
		}
		else
		{
			popOver.show((WebButton) e.getSource (), PopOverDirection.left);			
		}
	}
	
	public String gerarPesquisa()
	{		
		SimpleDateFormat formataDataSQL = new SimpleDateFormat("yyyy-M-dd");
		String formatacao = "SELECT * FROM vendas WHERE data BETWEEN ('" 
				+ formataDataSQL.format(dataInicial.getDate()) + "') " 
				+ "AND ('" + formataDataSQL.format(dataFinal.getDate()) + "') ";
		
		if(filtroCampoDelivery.getSelectedIndex() > 0)
		{
			if(filtroCampoDelivery.getSelectedIndex() == 1) {
				formatacao += "AND delivery != '0,00' ";
			}
			else {
				formatacao += "AND delivery == '0,00' ";
			}
		}
		
		if(filtroCampoDezPorcento.getSelectedIndex() > 0)
		{
			if(filtroCampoDezPorcento.getSelectedIndex() == 1) {
				formatacao += "AND dezporcento != '0,00' ";
			}
			else {
				formatacao += "AND dezporcento == '0,00' ";
			}
		}
		
		if(filtroLocal.getSelectedIndex() > 0)
		{
			if(filtroLocal.getSelectedIndex() == 1) {
				formatacao += "AND caixa = 0 ";
			}
			else
			{
				formatacao += "AND caixa = " + 
						Integer.parseInt(UtilCoffe.limpaNumero(filtroLocal.getSelectedItem().toString())) + " ";
			}
		}
		
		if(filtroCampoStatus.getSelectedIndex() > 0)
		{
			if(filtroCampoStatus.getSelectedIndex() == 1) {
				formatacao += "AND CAST(REPLACE(valor_pago, ',', '.') AS DECIMAL(10,6)) >= CAST(REPLACE(total, ',', '.') AS DECIMAL(10,6)) ";
				formatacao += "AND forma_pagamento LIKE 'Fiado' ";
			}
			else {
				formatacao += "AND CAST(REPLACE(valor_pago, ',', '.') AS DECIMAL(10,6)) < CAST(REPLACE(total, ',', '.') AS DECIMAL(10,6)) ";
				formatacao += "AND forma_pagamento LIKE 'Fiado' ";
			}
		}
		
		if(filtroCampoPagamento.getSelectedIndex() > 0) {
			formatacao += "AND forma_pagamento LIKE '" + filtroCampoPagamento.getSelectedItem().toString() + "' ";
		}
		
		if(filtroCampoFuncionario.getSelectedIndex() > 0) {
			formatacao += "AND atendente LIKE '" + filtroCampoFuncionario.getSelectedItem().toString() + "' ";
		}
		
		formatacao += "ORDER BY data";
		return formatacao;	
	}
	
	public void gerarPaginas(int paginas)
	{
		if(paginas > 0)
		{
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) paginacao.getModel();
			model.removeAllElements();
			
			for(int i = 0; i < paginas; i++) {
				model.addElement("P�gina " + (i+1) + "/" + paginas);
			}	
		}
		else
		{
			DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) paginacao.getModel();
			model.removeAllElements();
			model.addElement("P�gina 1/1");
		}
	}
	
	public void pesquisarVendas(int inicio, int fim)
	{
		tabelaModel.setNumRows(0);
		
		try {
			Query pega = new Query();
			pega.executaQuery(pesquisaFormat + inicio + ", " + fim);
			
			while(pega.next())
			{
				Vector<String> linha = new Vector<String>();
						
				linha.add("" + pega.getInt("vendas_id"));
				linha.add(pega.getString("horario"));
				linha.add(pega.getString("forma_pagamento"));
				linha.add(pega.getString("total"));
					
				if(UtilCoffe.precoToDouble(pega.getString("total")) > UtilCoffe.precoToDouble(pega.getString("valor_pago")))
				{
					if(pega.getString("forma_pagamento").equals("Fiado")) {
						linha.add("0");
					}
					else
						linha.add("1");
				}
				else
					linha.add("1");
					
				linha.add(pega.getString("atendente"));
				
				if(configDelivery)
				{
					if(UtilCoffe.precoToDouble(pega.getString("delivery")) > 0) {
						linha.add("1");
					}
					else
						linha.add("0");
				}
				
				if(configDez)
				{
					if(UtilCoffe.precoToDouble(pega.getString("dezporcento")) > 0) {
						linha.add("1");
					}
					else
						linha.add("0");
				}
				
				linha.add("");
				tabelaModel.addRow(linha);
			}
		} catch (NumberFormatException | ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
			new PainelErro(e1);
		}
	}
	
	private class TabelaVendasRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		private Color alternate = new Color(206, 220, 249);
		private ImageIcon iconLabel = new ImageIcon(getClass().getClassLoader().getResource("imgs/documento.png"));
		private ImageIcon iconDelete = new ImageIcon(getClass().getClassLoader().getResource("imgs/delete.png"));
		private ImageIcon iconOK = new ImageIcon(getClass().getClassLoader().getResource("imgs/pago.png"));
		private ImageIcon iconNO = new ImageIcon(getClass().getClassLoader().getResource("imgs/npago.png"));
		private JButton bDeletar = new JButton(iconDelete);

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, 
				boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel cellComponent = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			if((tabelaModel.getColumnCount() - 1) == column) {
				return bDeletar;
			}
			else if(column == 4)
			{
				if(Integer.parseInt(value.toString()) == 1) {
					setIcon(iconOK);
					setText("");
				}
				else {
					setIcon(iconNO);
					setText("");
				}
			}
			else if(column == 6 && configDelivery)
			{
				if(Integer.parseInt(value.toString()) == 1) {
					setIcon(iconOK);
					setText("");
				}
				else {
					setIcon(iconNO);
					setText("");
				}				
			}
			else if(column == 7 && configDez)
			{
				if(Integer.parseInt(value.toString()) == 1) {
					setIcon(iconOK);
					setText("");
				}
				else {
					setIcon(iconNO);
					setText("");
				}				
			}
			else if(column == 3) {
				setIcon(iconLabel);
				if(isSelected) {
					try {
						String formataTip = "<html>";
						formataTip += "<b>Venda #" + table.getValueAt(row,0) + "</b>  (<i>" + table.getValueAt(row,1) +")</i><br>";
						Query pega = new Query();
						pega.executaQuery("SELECT * FROM vendas_produtos WHERE `id_link` = " + table.getValueAt(row,0) + "");

						while(pega.next())
						{
							formataTip += pega.getInt("quantidade_produto") + "x .......... <b>" + pega.getString("nome_produto") + "</b>";
							if(!"".equals(pega.getString("adicionais_produto").trim()))
								formataTip += " com " + pega.getString("adicionais_produto");

							formataTip += " - R$" +  pega.getString("preco_produto") + "<br>";
						}

						pega.fechaConexao();
						formataTip += "</html>";
						setToolTipText(formataTip);
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
						setToolTipText("Erro ao receber banco de dados.");
					}
				}
			}
			else {
				setIcon(null);
				setToolTipText(null);
			}
			
			if(isSelected) {
				cellComponent.setBackground(tabela.getSelectionBackground());
			}
			else if(row % 2 == 0) {
				cellComponent.setBackground(alternate);
			}
			else {
				cellComponent.setBackground(Color.WHITE);
			}
			
			setHorizontalAlignment(JLabel.CENTER);
			return cellComponent;
		}
	}

	private class ButtonEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 1L;
		protected JButton button;
		private boolean isPushed;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton(new ImageIcon(getClass().getClassLoader().getResource("imgs/delete.png")));
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
			isPushed = true;
			return button;
		}

		public Object getCellEditorValue() {
			if (isPushed) {
				if(tabela.getSelectedRowCount() == 1)
				{
					int opcao = JOptionPane.showConfirmDialog(null, "Essa op��o ir� deletar a venda.\n\n"
							+ "Voc� tem certeza?\n\n", "Deletar Venda", JOptionPane.YES_NO_OPTION);

					if(opcao == JOptionPane.YES_OPTION)
					{ 
						try {
							Query pega = new Query();
							pega.executaUpdate("DELETE FROM vendas WHERE `vendas_id` = " + tabela.getValueAt(tabela.getSelectedRow(), 0));
							pega.executaUpdate("DELETE FROM vendas_produtos WHERE `id_link` = " + tabela.getValueAt(tabela.getSelectedRow(), 0));
							DiarioLog.add(Usuario.INSTANCE.getNome(), "Deletou a venda #" + tabela.getValueAt(tabela.getSelectedRow(), 0) 
									+ " de valor R$" + tabela.getValueAt(tabela.getSelectedRow(), 3) + ".", 7);	
							pega.fechaConexao();

							SwingUtilities.invokeLater(new Runnable() {  
								public void run() {  
									tabelaModel.removeRow(tabela.getSelectedRow());
								}  
							});
						} catch (ClassNotFoundException | SQLException e) {
							e.printStackTrace();
							new PainelErro(e);
						}		    			
					}
				}
			}
			isPushed = false;
			return "";
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