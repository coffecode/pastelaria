package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

import codecoffe.restaurantes.primitivas.Venda;
import codecoffe.restaurantes.sockets.CacheTodasMesas;
import codecoffe.restaurantes.sockets.CacheTodosProdutos;
import codecoffe.restaurantes.sockets.Client;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.DiarioLog;
import codecoffe.restaurantes.utilitarios.Usuario;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

public class MenuPrincipal 
{
	private JFrame janela;
	private JPanel principalPainel1, componentesCentrais;
	
	private MenuPrincipal()
	{
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{
			Configuracao.INSTANCE.atualizarConfiguracao();
		}
	}
	
	private static class MenuPrincipalSingletonHolder { 
		public static final MenuPrincipal INSTANCE = new MenuPrincipal();
	}
 
	public static MenuPrincipal getInstance() {
		return MenuPrincipalSingletonHolder.INSTANCE;
	}
	
	public void finalizarLoading()
	{
		janela = new JFrame();
		principalPainel1 = new JPanel();
		
		componentesCentrais = new JPanel(new CardLayout());
		componentesCentrais.setMinimumSize(new Dimension(980, 400));
		componentesCentrais.setMaximumSize(new Dimension(1920, 910));
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{			
			janela.setTitle(Configuracao.INSTANCE.getRestaurante() + " - Sistema de Restaurante (PRINCIPAL) " + UtilCoffe.VERSAO);
			
			componentesCentrais.add(PainelMesas.getInstance(), "Menu Mesas");
			componentesCentrais.add(PainelVendaMesa.getInstance(), "Menu Venda Mesa");
			componentesCentrais.add(PainelVendaRapida.getInstance(), "Menu Venda Rapida");
			componentesCentrais.add(PainelProdutos.getInstance(), "Menu Produtos");
			componentesCentrais.add(PainelFuncionarios.getInstance(), "Menu Funcionarios");
			componentesCentrais.add(PainelVendas.getInstance(), "Menu Vendas");
			componentesCentrais.add(PainelClientes.getInstance(), "Menu Clientes");
			componentesCentrais.add(PainelCozinha.getInstance(), "Menu Cozinha");
			
			PainelClientes.getInstance().atualizarClientes();
			
        	CacheTodosProdutos todosP = new CacheTodosProdutos();
        	todosP.atualizarProdutos();
        	
        	PainelVendaMesa.getInstance().atualizaProdutos(todosP);
			PainelVendaRapida.getInstance().atualizaProdutos(todosP);	
		}
		else
		{
			janela.setTitle(Configuracao.INSTANCE.getRestaurante() + " - Sistema de Restaurante (TERMINAL) " + UtilCoffe.VERSAO);			
			
			componentesCentrais.add(PainelMesas.getInstance(), "Menu Mesas");
			componentesCentrais.add(PainelVendaMesa.getInstance(), "Menu Venda Mesa");
			componentesCentrais.add(PainelVendaRapida.getInstance(), "Menu Venda Rapida");
			componentesCentrais.add(PainelClientes.getInstance(), "Menu Clientes");
			componentesCentrais.add(PainelCozinha.getInstance(), "Menu Cozinha");
			
	    	System.out.println("Enviando pedido da lista de produtos atualizado.");
	    	Client.getInstance().enviarObjeto("UPDATE PRODUTOS");
	    	
	    	System.out.println("Enviando pedido da lista de mesas atualizada.");
	    	Client.getInstance().enviarObjeto("UPDATE MESAS");        	
	    	
	    	System.out.println("Enviando pedido da lista de pedidos atualizada.");
	    	Client.getInstance().enviarObjeto("UPDATE PEDIDOS");
	    	
	    	System.out.println("Enviando pedido da lista de clientes atualizada.");
	    	Client.getInstance().enviarObjeto("UPDATE CLIENTES");    	
		}
		
		janela.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		janela.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				int opcao = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja sair?", "Logout",JOptionPane.YES_NO_OPTION);
		
				if(opcao == JOptionPane.YES_OPTION)
					logout();
			}
		});

		janela.setMinimumSize(new Dimension(980, 650));	
		janela.setMaximumSize(new Dimension(1920, 1080));	
		janela.setLocationRelativeTo(null);
		janela.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		principalPainel1.setLayout(new BoxLayout(principalPainel1, BoxLayout.Y_AXIS));
		principalPainel1.setMaximumSize(new Dimension(980, 400));		

		principalPainel1.add(PainelStatus.getInstance());
		principalPainel1.add(PainelMenu.getInstance());
		principalPainel1.add(componentesCentrais);
		principalPainel1.add(PainelLegenda.getInstance());

		janela.add(principalPainel1);
		janela.setResizable(true);
		janela.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/icone_programa.png")).getImage());
		
		/*Login.getInstance();
		Login.getInstance().pack();
		Login.getInstance().setLocationRelativeTo(null);
		Login.getInstance().setVisible(true);*/
		
		Usuario.INSTANCE.setNome("André Alves " + Configuracao.INSTANCE.getModo());
		Usuario.INSTANCE.setLevel(3);
		PainelStatus.getInstance().setNome(Usuario.INSTANCE.getNome());
		setarVisible(true);
	}
	
	public JFrame getJanela()
	{
		return janela;
	}
	
	public void setarVisible(boolean set)
	{
		janela.setVisible(true);
		
		if(set)
			Login.getInstance().setVisible(false);
	}
	
	public void setEnabled(boolean set)
	{
		janela.setEnabled(set);
	}
	
	public void setarEnter(JButton j)
	{
		janela.getRootPane().setDefaultButton(j);
	}
	
	public void Ativar(boolean set)
	{
		janela.setEnabled(set);
	}
	
	public void atualizarTodasMesas(CacheTodasMesas tm)
	{
		PainelMesas.getInstance().atualizarTodasMesas(tm);
	}
	
	public void AbrirMesa(int mesa, Venda v, boolean refresh)
	{
		Usuario.INSTANCE.setOlhando(mesa);
		
		if(refresh)
			PainelVendaMesa.getInstance().setarMesa(mesa, v);
		
		CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
		cardLayout.show(componentesCentrais, "Menu Venda Mesa");
	}

	public void AbrirPrincipal(int modelo)
	{
		Usuario.INSTANCE.setOlhando(-1);
		
		switch(modelo)
		{
			case 0:			// Abre o menu venda rapida
			{
				CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
				cardLayout.show(componentesCentrais, "Menu Venda Rapida");
				PainelVendaRapida.getInstance().setaFocusAdd();
				break;
			}
			case 1:			// Abre o menu de produtos
			{
				CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
				cardLayout.show(componentesCentrais, "Menu Produtos");				
				break;
			}
			case 2:			// Abre o menu de funcionarios
			{
				CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
				cardLayout.show(componentesCentrais, "Menu Funcionarios");				
				break;
			}
			case 3:			// Abre o menu de vendas
			{
				CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
				cardLayout.show(componentesCentrais, "Menu Vendas");				
				break;
			}
			case 4:			// Abre o menu de mesas (inicio)
			{
				CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
				cardLayout.show(componentesCentrais, "Menu Mesas");				
				break;
			}
			case 5:			// Abre o menu de clientes
			{
				CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
				cardLayout.show(componentesCentrais, "Menu Clientes");				
				break;
			}
			default:			// Abre o menu de cozinha
			{
				CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
				cardLayout.show(componentesCentrais, "Menu Cozinha");				
			}
		}
	}
	
	public void logout()
	{
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{
			DiarioLog.add(Usuario.INSTANCE.getNome(), "Saiu do sistema.", 9);
		}
		else
		{
			Client.getInstance().enviarObjeto(Usuario.INSTANCE.getNome() + ";QUIT");
		}		
		
        Login.getInstance().pack();
        Login.getInstance().setLocationRelativeTo(janela);
        Login.getInstance().setVisible(true);
        janela.setVisible(false);
	}		
}