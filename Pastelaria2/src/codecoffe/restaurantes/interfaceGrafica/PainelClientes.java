package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.utilitarios.DiarioLog;
import codecoffe.restaurantes.utilitarios.Usuario;
import codecoffe.restaurantes.utilitarios.UtilCoffe;
import codecoffe.restaurantes.utilitarios.buscaCEP;

import com.alee.extended.image.WebImage;
import com.alee.extended.painter.DashedBorderPainter;
import com.alee.laf.button.WebButton;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.managers.tooltip.TooltipWay;

import java.awt.event.*;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PainelClientes extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel painelClientes,verClientes;
	private JLabel labelNome, labelApelido, labelTelefone, labelCPF, labelCEP, labelEndereco, labelNumero, labelBairro, labelComplemento, labelDivida, labelLoad;
	private JTextField campoNome, campoApelido, campoTelefone, campoCPF, campoEndereco, campoNumero, campoBairro, campoComplemento;
	private WebTextField campoBusca, campoCEP;
	private JTabbedPane divisaoPainel;
	private ArrayList<Integer> clientesID = new ArrayList<>();
	private int clienteIDSelecionado;
	private DefaultListModel<String> modeloLista;
	private WebButton bSalvarCliente, bNovoCliente, bDeletarCliente, bDivida, bVenda;
	private JTable tabelaUltimasVendas;
	private DefaultTableModel tabela;
	private WebList jlist;
	private JScrollPane scrolltabela;
	private boolean flag_aciona;
	private int callBack = 0;
	
	@SuppressWarnings("unchecked")
	private PainelClientes()
	{		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		divisaoPainel = new JTabbedPane();			
		
		painelClientes = new JPanel();
		painelClientes.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;	
		flag_aciona = true;
		
		labelNome = new JLabel("Nome:");
		labelApelido = new JLabel("Apelido:");
		labelTelefone = new JLabel("Tel:");
		labelCPF = new JLabel("CPF:");
		labelCEP = new JLabel("CEP:");
		labelEndereco = new JLabel("Endereço:");
		labelNumero = new JLabel("Número:");
		labelBairro = new JLabel("Bairro:");
		labelComplemento = new JLabel("Complemento:");
		labelDivida = new JLabel("Dívida:");
		
		campoBusca = new WebTextField("");
		campoBusca.setToolTipText("Digite qualquer informação do cliente.");
		campoBusca.setInputPrompt("Buscar clientes...");
		campoBusca.setPreferredSize(new Dimension(150, 30));
		campoBusca.setTrailingComponent(new WebImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/buscar.png"))));
		
		campoBusca.addKeyListener(new KeyAdapter()
        {
        	public void keyPressed(KeyEvent e)
        	{
        		int code = e.getKeyCode();
        		if(code==KeyEvent.VK_ENTER)
        		{
        			if(!UtilCoffe.vaziu(campoBusca.getText()))
        				buscarCliente(campoBusca.getText());
        			else
        				buscarCliente("allclients");
        		}
            }
        });		
		
		campoNome = new JTextField("");
		campoNome.setPreferredSize(new Dimension(210, 30));
		
		campoApelido = new JTextField("");
		campoApelido.setPreferredSize(new Dimension(130, 30));
		
		campoTelefone = new JTextField("");
		campoTelefone.setPreferredSize(new Dimension(130, 30));
	
		campoCPF = new JTextField("");
		campoCPF.setPreferredSize(new Dimension(140, 30));		
		
		campoCEP = new WebTextField("");
		campoCEP.setPreferredSize(new Dimension(100, 30));
		campoCEP.setTrailingComponent(new WebImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/buscar.png"))));
		campoCEP.addKeyListener(new KeyAdapter()
        {
        	public void keyPressed(KeyEvent e)
        	{
        		int code = e.getKeyCode();
        		if(code==KeyEvent.VK_ENTER)
        		{
        			if(!UtilCoffe.vaziu(campoCEP.getText()))
        			{
        				labelLoad.setVisible(true);
        				
        				buscarCEP buscar = new buscarCEP();
        				Thread iniciaBuscaCep = new Thread(buscar);
        				iniciaBuscaCep.start();
        			}
        			else
        			{
        				JOptionPane.showMessageDialog(null, "Digite o CEP, exemplo: 13040050");
        			}
        		}
            }
        });				
		
		campoEndereco = new JTextField("");
		campoEndereco.setPreferredSize(new Dimension(300, 30));
		
		campoNumero = new JTextField("");
		campoNumero.setPreferredSize(new Dimension(70, 30));
		
		campoBairro = new JTextField("");
		campoBairro.setPreferredSize(new Dimension(120, 30));		
		
		campoComplemento = new JTextField("");
		campoComplemento.setPreferredSize(new Dimension(300, 30));
		
		verClientes = new JPanel();
		verClientes.setLayout(new BoxLayout(verClientes, BoxLayout.Y_AXIS));
		verClientes.setMinimumSize(new Dimension(320, 360));
		verClientes.setMaximumSize(new Dimension(320, 360));
		
		modeloLista = new DefaultListModel();
		try {
			Query pega = new Query();
			pega.executaQuery("SELECT fiador_id, nome from fiados ORDER BY nome");

			while(pega.next())
			{
				clientesID.add(pega.getInt("fiador_id"));
				modeloLista.addElement(pega.getString("nome"));
			}
			
			pega.fechaConexao();
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
			new PainelErro(e1);
			System.exit(0);
		}
		
		jlist = new WebList(modeloLista);
		jlist.setBackground(new Color(205, 205, 205));
		
		jlist.setCellRenderer(new DefaultListCellRenderer() {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		        label.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/Usuario.png")));
		        
		        if(index % 2 == 0)
		        {
		        	label.setBackground(new Color(206, 220, 249));
		        	label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(205, 205, 205)));
		        }
		        else
		        {
		        	label.setBackground(Color.WHITE);
		        	label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,new Color(205, 205, 205)));
		        }
		        
		        
		        if(isSelected)
		        {
		        	label.setForeground(Color.BLACK);
		        	label.setFont(new Font("Verdana", Font.BOLD, 11));
		        	label.setPreferredSize(new Dimension(300, 30));
		        }
		        else
		        {
		        	label.setForeground(Color.BLACK);
		        	label.setFont(new Font("Verdana", Font.PLAIN, 11));
		        	label.setPreferredSize(new Dimension(300, 30));
		        }
		        
		        return label;
		    }
		});
		
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlist.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent le) {
				int idx = jlist.getSelectedIndex();
		        if (idx != -1)
		        {
		        	if(flag_aciona)
		        	{
		        		receberCliente(clientesID.get(idx));
		        		flag_aciona = false;
		        	}
		        	else
		        	{
		        		flag_aciona = true;
		        	}
		        	/*if(jlist.getValueIsAdjusting())
		        		receberCliente(clientesID.get(idx));*/
		        }
		    }
		});
		
		verClientes.add(jlist);
		JScrollPane scrollClientes = new JScrollPane(verClientes, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollClientes.setMinimumSize(new Dimension(310,320));
		scrollClientes.setMaximumSize(new Dimension(310,320));
		scrollClientes.setPreferredSize(new Dimension(310,320));
		WebPanel verClientes1 = new WebPanel();	
		
        DashedBorderPainter bp4 = new DashedBorderPainter ( new float[]{ 3f, 3f } );
        bp4.setWidth ( 2 );
        bp4.setColor ( new Color ( 205, 205, 205 ) );
        verClientes1.setPainter (bp4);
		
		verClientes1.add(scrollClientes);	
		
		gbc.insets = new Insets(3,3,3,50);
		gbc.gridwidth = 3;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 1;	// linhas		
		painelClientes.add(campoBusca, gbc);		
		
		gbc.insets = new Insets(3,3,3,50);
		gbc.gridheight = 8;
		gbc.gridwidth = 3;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 2;	// linhas		
		painelClientes.add(verClientes1, gbc);
		
		gbc.insets = new Insets(3,3,3,3);
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.gridx = 4;	// colunas
		gbc.gridy = 1;	// linhas		
		painelClientes.add(labelNome, gbc);
		
		gbc.gridwidth = 3;
		gbc.gridx = 5;	// colunas
		gbc.gridy = 1;	// linhas		
		painelClientes.add(campoNome, gbc);
		
		gbc.gridwidth = 1;
		gbc.gridx = 8;	// colunas
		gbc.gridy = 1;	// linhas		
		painelClientes.add(labelApelido, gbc);
		
		gbc.gridx = 9;	// colunas
		gbc.gridy = 1;	// linhas		
		painelClientes.add(campoApelido, gbc);
		
		gbc.gridx = 4;	// colunas
		gbc.gridy = 2;	// linhas		
		painelClientes.add(labelCPF, gbc);
		
		gbc.gridwidth = 2;
		gbc.gridx = 5;	// colunas
		gbc.gridy = 2;	// linhas		
		painelClientes.add(campoCPF, gbc);
		
		gbc.gridwidth = 1;
		gbc.gridx = 8;	// colunas
		gbc.gridy = 2;	// linhas		
		painelClientes.add(labelTelefone, gbc);
		
		gbc.gridx = 9;	// colunas
		gbc.gridy = 2;	// linhas		
		painelClientes.add(campoTelefone, gbc);
		
		gbc.gridx = 4;	// colunas
		gbc.gridy = 3;	// linhas		
		painelClientes.add(labelCEP, gbc);
		
		gbc.gridx = 5;	// colunas
		gbc.gridy = 3;	// linhas		
		painelClientes.add(campoCEP, gbc);
		
		labelLoad = new JLabel();
		labelLoad.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/loadcep.gif")));
		labelLoad.setVisible(false);
		
		gbc.gridx = 6;	// colunas
		gbc.gridy = 3;	// linhas		
		painelClientes.add(labelLoad, gbc);		
		
		gbc.gridx = 4;	// colunas
		gbc.gridy = 4;	// linhas		
		painelClientes.add(labelEndereco, gbc);
		
		gbc.gridwidth = 5;
		gbc.gridx = 5;	// colunas
		gbc.gridy = 4;	// linhas		
		painelClientes.add(campoEndereco, gbc);			
		
		gbc.gridwidth = 1;
		gbc.gridx = 4;	// colunas
		gbc.gridy = 5;	// linhas		
		painelClientes.add(labelNumero, gbc);
		
		gbc.gridx = 5;	// colunas
		gbc.gridy = 5;	// linhas		
		painelClientes.add(campoNumero, gbc);
		
		gbc.gridx = 8;	// colunas
		gbc.gridy = 5;	// linhas		
		painelClientes.add(labelBairro, gbc);
		
		gbc.gridx = 9;	// colunas
		gbc.gridy = 5;	// linhas		
		painelClientes.add(campoBairro, gbc);		
		
		gbc.gridx = 4;	// colunas
		gbc.gridy = 6;	// linhas		
		painelClientes.add(labelComplemento, gbc);
		
		gbc.gridwidth = 5;
		gbc.gridx = 5;	// colunas
		gbc.gridy = 6;	// linhas		
		painelClientes.add(campoComplemento, gbc);	
		
		tabela = new DefaultTableModel() {

		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		       if(column == 4)
		    	   return true;
		       
		       return false;
		    }
		};
		
		tabela.addColumn("ID");
		tabela.addColumn("Data");
		tabela.addColumn("Total");
		tabela.addColumn("Status");
		
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
		tabelaUltimasVendas.getColumnModel().getColumn(0).setMinWidth(0);
		tabelaUltimasVendas.getColumnModel().getColumn(0).setMaxWidth(0);
		tabelaUltimasVendas.getColumnModel().getColumn(1).setMinWidth(100);
		tabelaUltimasVendas.getColumnModel().getColumn(1).setMaxWidth(200);
		tabelaUltimasVendas.getColumnModel().getColumn(2).setMinWidth(100);
		tabelaUltimasVendas.getColumnModel().getColumn(2).setMaxWidth(200);		
		tabelaUltimasVendas.getColumnModel().getColumn(3).setMinWidth(100);
		tabelaUltimasVendas.getColumnModel().getColumn(3).setMaxWidth(200);		
		tabelaUltimasVendas.setRowHeight(25);
		tabelaUltimasVendas.getTableHeader().setReorderingAllowed(false);
		tabelaUltimasVendas.getColumn("Total").setCellRenderer(new JLabelRenderer());
		tabelaUltimasVendas.getColumn("Data").setCellRenderer(new CustomRenderer());
		tabelaUltimasVendas.getColumn("Status").setCellRenderer(new CustomRenderer());
		tabelaUltimasVendas.setPreferredScrollableViewportSize(new Dimension(350, 100));
		
		scrolltabela = new JScrollPane(tabelaUltimasVendas, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		gbc.gridwidth = 6;
		gbc.gridheight = 1;
		gbc.gridx = 4;	// colunas
		gbc.gridy = 8;	// linhas		
		painelClientes.add(scrolltabela, gbc);
		
		bVenda = new WebButton("Venda");
		bVenda.setRolloverShine(true);
		bVenda.setPreferredSize(new Dimension(90, 35));
		bVenda.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/plus2.png")));
		bVenda.addActionListener(this);
		
		gbc.gridwidth = 1;
		gbc.gridx = 4;	// colunas
		gbc.gridy = 10;	// linhas		
		painelClientes.add(bVenda, gbc);		
		
		gbc.gridx = 8;	// colunas
		gbc.gridy = 10;	// linhas		
		painelClientes.add(labelDivida, gbc);
		
		bDivida = new WebButton("0,00");
		bDivida.setRolloverShine(true);
		bDivida.setPreferredSize(new Dimension(70, 30));
		bDivida.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/fiados1.png")));
		bDivida.addActionListener(this);
		
		gbc.gridx = 9;	// colunas
		gbc.gridy = 10;	// linhas		
		painelClientes.add(bDivida, gbc);		
		
		JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		
		bSalvarCliente = new WebButton("");
		bSalvarCliente.setToolTipText("Salvar cliente.");
		bSalvarCliente.setPreferredSize(new Dimension(32, 32));
		bSalvarCliente.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/salvar.png")));
		bSalvarCliente.setUndecorated(true);
		bSalvarCliente.addActionListener(this);
		
		bNovoCliente = new WebButton("");
		bNovoCliente.setToolTipText("Novo cliente.");
		bNovoCliente.setPreferredSize(new Dimension(32, 32));
		bNovoCliente.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/newcliente.png")));
		bNovoCliente.setUndecorated(true);
		bNovoCliente.addActionListener(this);
		
		bDeletarCliente = new WebButton("");
		bDeletarCliente.setToolTipText("Deletar cliente.");
		bDeletarCliente.setPreferredSize(new Dimension(32, 32));
		bDeletarCliente.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/deletecliente.png")));
		bDeletarCliente.setUndecorated(true);
		bDeletarCliente.addActionListener(this);
		
		painelBotoes.add(bSalvarCliente);
		painelBotoes.add(bNovoCliente);
		painelBotoes.add(bDeletarCliente);
		
		gbc.insets = new Insets(0,0,0,50);
		gbc.gridwidth = 3;
		gbc.gridx = 1;	// colunas
		gbc.gridy = 10;	// linhas		
		painelClientes.add(painelBotoes, gbc);
		
		divisaoPainel.addTab("Clientes", new ImageIcon(getClass().getClassLoader().getResource("imgs/report_user_mini.png")), painelClientes, "Gerenciar Clientes.");			
		add(divisaoPainel);
		setarAtivado(false);
		callBack = 0;
	}
	
	private static class ClientesSingletonHolder { 
		public static final PainelClientes INSTANCE = new PainelClientes();
	}
 
	public static PainelClientes getInstance() {
		return ClientesSingletonHolder.INSTANCE;
	}	
	
	public void setCallBack(int menu)
	{
		callBack = menu;
	}
	
	class buscarCEP implements Runnable {
		  public void run () {
			  
			  	buscaCEP pegaDados = new buscaCEP();
			  
				try {
					String resultadoEnd = pegaDados.getEndereco(campoCEP.getText());
					String resultadoBairro = pegaDados.getBairro(campoCEP.getText());
					
					if(!resultadoEnd.equals(campoCEP.getText()))
					{
						campoEndereco.setText(resultadoEnd);
						campoBairro.setText(resultadoBairro);
					}
					
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e1.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
				} finally {
					labelLoad.setVisible(false);
				}
		  }
	}	
	
	/*private class Teste extends JPanel
	{
		Teste()
		{
			setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Teste"));
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setPreferredSize(new Dimension(400, 400));
			
			JLabel lol = new JLabel("finalmente");
			add(lol);
		}
	}*/
	
	private void setarAtivado(boolean set)
	{		
		labelNome.setEnabled(set);
		labelApelido.setEnabled(set);
		labelTelefone.setEnabled(set);
		labelCPF.setEnabled(set);
		labelCEP.setEnabled(set);
		labelEndereco.setEnabled(set);
		labelNumero.setEnabled(set);
		labelBairro.setEnabled(set);
		labelComplemento.setEnabled(set);
		labelDivida.setEnabled(set);
		
		campoNome.setEnabled(set);
		campoApelido.setEnabled(set);
		campoTelefone.setEnabled(set);
		campoCPF.setEnabled(set);
		campoCEP.setEnabled(set);
		campoEndereco.setEnabled(set);
		campoNumero.setEnabled(set);
		campoBairro.setEnabled(set);
		campoComplemento.setEnabled(set);
		bDivida.setEnabled(set);
		bVenda.setEnabled(set);
		scrolltabela.setEnabled(set);
		
		if(!set)
		{
			clienteIDSelecionado = 0;
			
			campoNome.setText("");
			campoApelido.setText("");
			campoTelefone.setText("");
			campoCPF.setText("");
			campoCEP.setText("");
			campoEndereco.setText("");
			campoNumero.setText("");
			campoBairro.setText("");
			campoComplemento.setText("");
			bDivida.setText("0,00");
			tabela.setRowCount(0);		
		}
	}
	
	private void buscarCliente(String arg)
	{
		setarAtivado(false);
		clientesID.clear();
		modeloLista.removeAllElements();
		
		try {
			if(arg.equals("allclients"))
			{
				Query pega = new Query();
				pega.executaQuery("SELECT fiador_id, nome from fiados ORDER BY nome");

				while(pega.next())
				{
					clientesID.add(pega.getInt("fiador_id"));
					modeloLista.addElement(pega.getString("nome"));
				}
				
				pega.fechaConexao();			
			}
			else
			{
				Query pega = new Query();
				String formatador = "SELECT fiador_id, nome from fiados WHERE ";
				
				formatador += "nome LIKE '%" + arg + "%' OR ";
				formatador += "apelido LIKE '%" + arg + "%' OR ";
				formatador += "telefone LIKE '%" + arg + "%' OR ";
				formatador += "cpf LIKE '%" + arg + "%' OR ";
				formatador += "cep LIKE '%" + arg + "%' OR ";
				formatador += "endereco LIKE '%" + arg + "%' OR ";
				formatador += "numero LIKE '%" + arg + "%' OR ";
				formatador += "bairro LIKE '%" + arg + "%' OR ";
				formatador += "complemento LIKE '%" + arg + "%' ORDER BY nome;";
				
				pega.executaQuery(formatador);
				int numAchados = 0;

				while(pega.next())
				{
					clientesID.add(pega.getInt("fiador_id"));
					modeloLista.addElement(pega.getString("nome"));
					numAchados++;
				}
				
				pega.fechaConexao();
				
				if(numAchados == 1)
				{
					jlist.setSelectedIndex(0);
					receberCliente(clientesID.get(0));
				}
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			new PainelErro(e);
		}
	}
	
	private void receberCliente(int id)
	{
		if(id > 0)
		{
			clienteIDSelecionado = id;
			
			try {
				Query pega = new Query();
				pega.executaQuery("SELECT * from fiados WHERE fiador_id = " + id);

				while(pega.next())
				{
					setarAtivado(true);
					
					campoNome.setText(pega.getString("nome"));
					campoApelido.setText(pega.getString("apelido"));
					campoCPF.setText(pega.getString("cpf"));
					campoTelefone.setText(pega.getString("telefone"));
					campoCEP.setText(pega.getString("cep"));
					campoEndereco.setText(pega.getString("endereco"));
					campoNumero.setText(pega.getString("numero"));
					campoBairro.setText(pega.getString("bairro"));
					campoComplemento.setText(pega.getString("complemento"));
					
					tabela.setNumRows(0);
					double totalDivida = 0.0;
					Query pega2 = new Query();
					pega2.executaQuery("SELECT * FROM vendas WHERE fiado_id = " + clienteIDSelecionado + " ORDER BY vendas_id DESC limit 0, 10");
					
					while(pega2.next())
					{
						Vector<Serializable> linha = new Vector<Serializable>();		
						
						linha.add(pega2.getInt("vendas_id"));
						linha.add(pega2.getString("horario"));
						linha.add(pega2.getString("total"));
						
						if((Double.parseDouble(pega2.getString("total").replaceAll(",", ".")) > Double.parseDouble(pega2.getString("valor_pago").replaceAll(",", "."))))
						{
							if(pega2.getString("forma_pagamento").equals("Fiado"))
							{
								linha.add("Não Pago");
								totalDivida += (Double.parseDouble(pega2.getString("total").replaceAll(",", ".")) - Double.parseDouble(pega2.getString("valor_pago").replaceAll(",", ".")));
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
						
						tabela.addRow(linha);
					}
					
					bDivida.setText(UtilCoffe.doubleToPreco(totalDivida));					
				}
				
				pega.fechaConexao();
			} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				new PainelErro(e);
			}			
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
			  
			  	String formataTip = "<html>";
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
					new PainelErro(e);
					formataTip = "Erro ao receber banco de dados.";
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

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == bVenda)
		{
			if(clienteIDSelecionado == 0)
			{
				JOptionPane.showMessageDialog(null, "Escolha um cliente antes!");
			}
			else
			{
				if(callBack > 0)	// retorna para o painel mesas;
				{
					PainelMesas.getInstance().verMesa(callBack-1);
					PainelVendaMesa.getInstance().setFiado(modeloLista.getElementAt(jlist.getSelectedIndex()), clienteIDSelecionado);
					callBack = 0;
				}
				else
				{
					PainelVendaRapida.getInstance().setFiado(modeloLista.getElementAt(jlist.getSelectedIndex()), clienteIDSelecionado, campoTelefone.getText(), 
							campoEndereco.getText(), campoNumero.getText(), campoComplemento.getText());
					MenuPrincipal.getInstance().AbrirPrincipal(0);
				}
			}
		}
		else if(e.getSource() == bDeletarCliente)
		{
			if(clienteIDSelecionado > 0)
			{
				if(UtilCoffe.precoToDouble(bDivida.getText()) <= 0)
				{
		    		  int opcao = JOptionPane.showConfirmDialog(null, "Deletar o cliente: " + campoNome.getText() + ".\n\nVocê tem certeza?\n\n", "Deletar Cliente", JOptionPane.YES_NO_OPTION);
		    		  
		    		  if(opcao == JOptionPane.YES_OPTION)
		    		  {
		    			   try {
							DiarioLog.add(Usuario.INSTANCE.getNome(), "Deletou o cliente " + campoNome.getText() + ". Telefone: " + campoTelefone.getText() + ".", 5);
							   Query envia = new Query();
							   String formatacao = "DELETE FROM fiados WHERE `fiador_id` = " + clienteIDSelecionado + ";";  
							   envia.executaUpdate(formatacao);
							   envia.fechaConexao();
							   buscarCliente("allclients");
							   NotificationManager.setLocation(2);
							   NotificationManager.showNotification(this, "Cliente Deletado!").setDisplayTime(2000);
							   campoBusca.requestFocus();
						} catch (ClassNotFoundException | SQLException e1) {
							e1.printStackTrace();
							new PainelErro(e1);
						}		
		    		  }					
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Não pode deletar cliente com dívida aberta!");
				}
			}
		}
		else if(e.getSource() == bSalvarCliente)
		{
			if(clienteIDSelecionado > 0)
			{
				if(UtilCoffe.vaziu(campoNome.getText()))
				{
					JOptionPane.showMessageDialog(null, "O campo nome é de preenchimento obrigatório!");
				}
				else
				{
					Query manda = new Query();
					String formata = "UPDATE fiados SET ";
					
					formata += "`nome` = '" + campoNome.getText() + "', ";
					formata += "`apelido` = '" + campoApelido.getText() + "', ";
					formata += "`telefone` = '" + campoTelefone.getText() + "', ";
					formata += "`cpf` = '" + campoCPF.getText() + "', ";
					formata += "`cep` = '" + campoCEP.getText() + "', ";
					formata += "`endereco` = '" + campoEndereco.getText() + "', ";
					formata += "`numero` = '" + campoNumero.getText() + "', ";
					formata += "`bairro` = '" + campoBairro.getText() + "', ";
					formata += "`complemento` = '" + campoComplemento.getText() + "' WHERE `fiador_id` = " + clienteIDSelecionado;
					
					try {
						manda.executaUpdate(formata);				
						manda.fechaConexao();

						modeloLista.set(jlist.getSelectedIndex(), campoNome.getText());
						
						NotificationManager.setLocation(2);
						NotificationManager.showNotification(this, "Cliente Salvado!").setDisplayTime(2000);
						DiarioLog.add(Usuario.INSTANCE.getNome(), "Atualizou o cliente: " + campoNome.getText() + ". Telefone: " + campoTelefone.getText() + ".", 5);
					} catch (ClassNotFoundException | SQLException e1) {
						e1.printStackTrace();
						new PainelErro(e1);
					}
				}
			}
		}		
		else if(e.getSource() == bNovoCliente)
		{
			try {
				int novoID = 0;
				Query envia = new Query();
				envia.executaUpdate("INSERT INTO fiados(nome) VALUES('Novo Cliente');");
				envia.executaQuery("SELECT fiador_id FROM fiados ORDER BY fiador_id DESC limit 0, 1");
				
				if(envia.next())
					novoID = envia.getInt("fiador_id");
				
				envia.fechaConexao();
				
				clientesID.add(0, novoID);
				modeloLista.add(0, "Novo Cliente");
				jlist.setSelectedIndex(0);
				receberCliente(novoID);
				
				campoNome.requestFocus();
				TooltipManager.showOneTimeTooltip ( bSalvarCliente, null, "Lembre-se de salvar!", TooltipWay.up );
				DiarioLog.add(Usuario.INSTANCE.getNome(), "Cadastrou um novo cliente.", 5);
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
				new PainelErro(e1);
			}
		}
		else if(e.getSource() == bDivida)
		{
		  String bla = "Escreva o valor a ser deduzido da dívida.\n";
   		  bla += "Importante:\n\n1- Não é possível aumentar a dívida, apenas reduzí-la.\n";
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
   			  
   			  if(resposta > 0 && clienteIDSelecionado > 0)
   			  {
   				  	try {
						DiarioLog.add(Usuario.INSTANCE.getNome(), "Reduziu R$" + pegaResposta + " da dívida do " + campoNome.getText() + " (Telefone: " + campoTelefone.getText() + " ) de R$" + bDivida.getText() + ".", 6);
						Query pega = new Query();
						pega.executaQuery("SELECT * FROM vendas WHERE `fiado_id` = " + clienteIDSelecionado + "");
							
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
										manda.executaUpdate("UPDATE vendas SET `valor_pago` = '" + pega.getString("total") + "' WHERE `vendas_id` = " + pega.getInt("vendas_id"));
										manda.fechaConexao();
									}
								}
							}
						
						pega.fechaConexao();
						receberCliente(clienteIDSelecionado);
					} catch (NumberFormatException | ClassNotFoundException | SQLException e1) {
						e1.printStackTrace();
						new PainelErro(e1);
					}
   			  }
   		  }
		}
	}
}