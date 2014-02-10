package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;

import javax.swing.*;

import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.Usuario;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.laf.button.WebButton;

import java.awt.event.*;

public class PainelMenu extends JPanel implements MouseListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WebButton vendaRapida, consulta, inicio, produtos, cozinha, clientes;
	
	private PainelMenu()
	{
		setLayout(new FlowLayout(FlowLayout.CENTER, 6, 20));
		setMaximumSize(new Dimension(1920, 80));
		setMinimumSize(new Dimension(980, 80));
		
		inicio = new WebButton("Início");
		inicio.setRolloverShine(true);
		inicio.setPreferredSize(new Dimension(130, 60));
		ImageIcon iconeInicio = new ImageIcon(getClass().getClassLoader().getResource("imgs/inicio.png"));
		inicio.setIcon(iconeInicio);
		inicio.addMouseListener(this);
		add(inicio);		
		
		vendaRapida = new WebButton("Venda Rápida");
		vendaRapida.setRolloverShine(true);
		ImageIcon iconeRapida = new ImageIcon(getClass().getClassLoader().getResource("imgs/vrapida.png"));
		vendaRapida.setIcon(iconeRapida);
		vendaRapida.setPreferredSize(new Dimension(160, 60));
		vendaRapida.addMouseListener(this);
		add(vendaRapida);
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{
			produtos = new WebButton("Produtos");
			produtos.setRolloverShine(true);
			produtos.setPreferredSize(new Dimension(140, 60));
			ImageIcon iconeProdutos = new ImageIcon(getClass().getClassLoader().getResource("imgs/produtos.png"));
			produtos.setIcon(iconeProdutos);
			produtos.addMouseListener(this);
			add(produtos);			
		}
		
		clientes = new WebButton("Clientes");
		clientes.setRolloverShine(true);
		clientes.setPreferredSize(new Dimension(140, 60));
		clientes.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/report_user.png")));
		clientes.addMouseListener(this);
		add(clientes);
		
		cozinha = new WebButton("Cozinha");
		cozinha.setRolloverShine(true);
		cozinha.setPreferredSize(new Dimension(140, 60));
		ImageIcon iconeFuncionarios = new ImageIcon(getClass().getClassLoader().getResource("imgs/chef.png"));
		cozinha.setIcon(iconeFuncionarios);
		cozinha.addMouseListener(this);
		add(cozinha);			
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{			
			consulta = new WebButton("Vendas");
			consulta.setRolloverShine(true);
			consulta.setPreferredSize(new Dimension(130, 60));
			ImageIcon iconeVendas = new ImageIcon(getClass().getClassLoader().getResource("imgs/consultar.png"));
			consulta.setIcon(iconeVendas);
			consulta.addMouseListener(this);
			add(consulta);			
		}
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{
			ActionMap actionMap = getActionMap();
			actionMap.put("botao1", new SpaceAction(0));
			actionMap.put("botao2", new SpaceAction(1));
			actionMap.put("botao3", new SpaceAction(2));
			actionMap.put("botao4", new SpaceAction(3));
			actionMap.put("botao5", new SpaceAction(4));
			actionMap.put("botao6", new SpaceAction(5));
			actionMap.put("botao7", new SpaceAction(6));
			setActionMap(actionMap);
			
			InputMap imap = getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
			imap.put(KeyStroke.getKeyStroke("F1"), "botao1");
			imap.put(KeyStroke.getKeyStroke("F2"), "botao2");
			imap.put(KeyStroke.getKeyStroke("F3"), "botao3"); 
			imap.put(KeyStroke.getKeyStroke("F4"), "botao4"); 
			imap.put(KeyStroke.getKeyStroke("F5"), "botao5");
			imap.put(KeyStroke.getKeyStroke("F6"), "botao6");
			imap.put(KeyStroke.getKeyStroke("ESCAPE"), "botao7");	
		}
		else
		{
			ActionMap actionMap = getActionMap();
			actionMap.put("botao1", new SpaceAction(0));
			actionMap.put("botao2", new SpaceAction(1));
			actionMap.put("botao4", new SpaceAction(3));
			actionMap.put("botao5", new SpaceAction(4));
			actionMap.put("botao7", new SpaceAction(6));
			setActionMap(actionMap);
			
			InputMap imap = getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
			imap.put(KeyStroke.getKeyStroke("F1"), "botao1");
			imap.put(KeyStroke.getKeyStroke("F2"), "botao2");
			imap.put(KeyStroke.getKeyStroke("F3"), "botao4"); 
			imap.put(KeyStroke.getKeyStroke("F4"), "botao5");
			imap.put(KeyStroke.getKeyStroke("ESCAPE"), "botao7");				
		}
	}
	
	private static class MenuSingletonHolder { 
		public static final PainelMenu INSTANCE = new PainelMenu();
	}
 
	public static PainelMenu getInstance() {
		return MenuSingletonHolder.INSTANCE;
	}	
	
	private class SpaceAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int tipo = 0;

		public SpaceAction(int tipo) {
			this.tipo = tipo;
		}		

		@Override
		public void actionPerformed(ActionEvent e) {
			
			switch(this.tipo)
			{
				case 0:
				{
					MenuPrincipal.getInstance().AbrirPrincipal(4);
					break;
				}
				case 1:
				{
					MenuPrincipal.getInstance().AbrirPrincipal(0);
					break;
				}
				case 2:
				{
					MenuPrincipal.getInstance().AbrirPrincipal(1);
					break;
				}
				case 3:
				{
					MenuPrincipal.getInstance().AbrirPrincipal(5);
					break;
				}
				case 4:
				{
					MenuPrincipal.getInstance().AbrirPrincipal(6);
					break;
				}
				case 5:
				{
					MenuPrincipal.getInstance().AbrirPrincipal(3);
					break;
				}
				default:
				{
					int opcao = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja sair?", "Logout", JOptionPane.YES_NO_OPTION);

					if(opcao == JOptionPane.YES_OPTION)
						MenuPrincipal.getInstance().logout();
				}
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getSource() == vendaRapida)
		{
			MenuPrincipal.getInstance().AbrirPrincipal(0);
		}
		else if(e.getSource() == produtos)
		{
			if(Usuario.INSTANCE.getLevel() > 1)
				MenuPrincipal.getInstance().AbrirPrincipal(1);
			else
				JOptionPane.showMessageDialog(null, "Você não tem permissão para ver isso.");
		}
		else if(e.getSource() == consulta)
		{
			if(Usuario.INSTANCE.getLevel() > 1)
				MenuPrincipal.getInstance().AbrirPrincipal(3);
			else
				JOptionPane.showMessageDialog(null, "Você não tem permissão para ver isso.");
		}
		else if(e.getSource() == inicio)
		{
			MenuPrincipal.getInstance().AbrirPrincipal(4);
		}
		else if(e.getSource() == clientes)
		{
			MenuPrincipal.getInstance().AbrirPrincipal(5);
			PainelClientes.getInstance().setCallBack(0);
		}
		else if(e.getSource() == cozinha)
		{
			MenuPrincipal.getInstance().AbrirPrincipal(6);
		}		
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		if(e.getSource() == vendaRapida)
		{
			PainelLegenda.getInstance().AtualizaLegenda("Venda Rápida, direto no balcão.");
		}
		else if(e.getSource() == produtos)
		{
			PainelLegenda.getInstance().AtualizaLegenda("Gerenciamento de produtos/adicionais.");
		}
		else if(e.getSource() == cozinha)
		{
			PainelLegenda.getInstance().AtualizaLegenda("Gerenciamento de pedidos.");
		}
		else if(e.getSource() == consulta)
		{
			PainelLegenda.getInstance().AtualizaLegenda("Consulte as vendas de determinada data.");
		}
		else if(e.getSource() == inicio)
		{
			PainelLegenda.getInstance().AtualizaLegenda("Início do programa (mesas).");
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		PainelLegenda.getInstance().AtualizaLegenda("Desenvolvido por CodeCoffe (C) - 2014");
	}
}