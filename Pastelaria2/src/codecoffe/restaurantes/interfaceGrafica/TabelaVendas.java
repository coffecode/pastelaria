package codecoffe.restaurantes.interfaceGrafica;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import com.alee.extended.date.WebDateField;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;

public class TabelaVendas extends WebPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable tabela;
	private DefaultTableModel tabelaModel;
	private WebDateField dataInicial, dataFinal;
	private WebButton pesquisar, exportarPDF, exportarExcel, opcoesTabela;
	private JComboBox<String> paginacao;
	
	public TabelaVendas()
	{
		setLayout(new MigLayout("fill", "15[]15[]20[]15", "10[]10[]"));
		
		tabela = new JTable();
		tabelaModel = new DefaultTableModel() {
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
		
		tabelaModel.addColumn("ID");
		tabelaModel.addColumn("Data");
		tabelaModel.addColumn("Pagamento");
		tabelaModel.addColumn("Total");
		tabelaModel.addColumn("Status");
		tabelaModel.addColumn("Atendente");
		tabelaModel.addColumn("Deletar");
		
		tabela.setModel(tabelaModel);
		
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
		
		String[] paginas = {"Página 1", "Página 2"};
		paginacao = new JComboBox<String>(paginas);
		paginacao.setMinimumSize(new Dimension(120, 30));
		
		add(new JLabel("Início: "), "gapleft 10px, split 5");
		add(dataInicial, "gapleft 10px");
		add(new JLabel("Fim: "), "gapleft 20px");
		add(dataFinal, "gapleft 10px");
		add(pesquisar, "gapleft 30px");
		add(opcoesTabela, "align 100%, gapleft 30px, split 3, span");
		add(exportarPDF, "align 100%");
		add(exportarExcel, "align 100%, wrap");
		add(scrolltabela, "grow, pushy, span, wrap");
		add(paginacao, "align 100%, span");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
}
