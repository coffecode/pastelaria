package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.*;

import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.primitivas.Venda;
import codecoffe.restaurantes.sockets.BroadcastServer;
import codecoffe.restaurantes.sockets.CacheTodasMesas;
import codecoffe.restaurantes.sockets.CacheTodosProdutos;
import codecoffe.restaurantes.sockets.Client;
import codecoffe.restaurantes.utilitarios.Bartender;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.DiarioLog;
import codecoffe.restaurantes.utilitarios.Usuario;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyManager;

public class MenuPrincipal 
{
	private JFrame janela;
	private JPanel principalPainel1, componentesCentrais;
	
	/*private Timer t;
	private int loadingTime = 0;*/
	
	private MenuPrincipal()
	{
		janela = new JFrame();
		principalPainel1 = new JPanel();
		
		componentesCentrais = new JPanel(new CardLayout());
		componentesCentrais.setMinimumSize(new Dimension(980, 400));
		componentesCentrais.setMaximumSize(new Dimension(1920, 910));
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{
			Configuracao.INSTANCE.atualizarConfiguracao();
			
			janela.setTitle(Configuracao.INSTANCE.getRestaurante() + " - Sistema de Restaurante (PRINCIPAL) " + UtilCoffe.VERSAO);
			PainelVendaRapida.getInstance();
			PainelMenu.getInstance();
			PainelMesas.getInstance();
			PainelLegenda.getInstance();
			PainelStatus.getInstance();
			PainelProdutos.getInstance();
			PainelFuncionarios.getInstance();
			PainelVendas.getInstance();
			PainelVendaMesa.getInstance();
			PainelClientes.getInstance();
			PainelCozinha.getInstance();
			
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
			
			PainelVendaRapida.getInstance();
			PainelMenu.getInstance();
			PainelMesas.getInstance();
			PainelLegenda.getInstance();
			PainelStatus.getInstance();
			PainelVendaMesa.getInstance();
			PainelClientes.getInstance();
			PainelCozinha.getInstance();			
			
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
		
		Login.getInstance();
		Login.getInstance().pack();
		Login.getInstance().setLocationRelativeTo(null);
		Login.getInstance().setVisible(true);
		
		/*Usuario.INSTANCE.setNome("André Alves " + modo);
		Usuario.INSTANCE.setLevel(3);
		PainelStatus.setNome(Usuario.INSTANCE.getNome());
		setarVisible(true);*/		
	
	/*
	 	t = new Timer(500, new ActionListener()
	 	{
	 		public void actionPerformed(ActionEvent ae)
	 		{	
	 			switch(loadingTime)
	 			{
	 				case 0:
	 				{
	 					Starter.setarProgresso("Carregando menu...", 25);
	 					menuPainel = new PainelMenu();
	 					break;
	 				}
	 				case 1:
	 				{
	 					Starter.setarProgresso("Carregando mesas...", 30);
	 					menuMesas = new PainelMesas(Configuracao.INSTANCE.getMesas());
	 					break;
	 				}
	 				case 2:
	 				{
	 					Starter.setarProgresso("Carregando legenda...", 35);
	 					menuFooter = new PainelLegenda();
	 					break;
	 				}
	 				case 3:
	 				{
	 					Starter.setarProgresso("Carregando barra de status...", 40);
	 					menuStatus = new PainelStatus();
	 					break;
	 				}
	 				case 4:
	 				{
	 					Starter.setarProgresso("Carregando painel de venda rápida...", 50);
	 					menuVendaRapida = new PainelVendaRapida();
	 					break;
	 				}
	 				case 5:
	 				{
	 					Starter.setarProgresso("Carregando produtos & adicionais...", 60);
	 					menuProdutos = new PainelProdutos();
	 					break;
	 				}  
	 				case 6:
	 				{
	 					Starter.setarProgresso("Carregando funcionarios...", 70);
	 					menuFuncionarios = new PainelFuncionarios();
	 					break;
	 				}
	 				case 7:
	 				{
	 					Starter.setarProgresso("Carregando painel de vendas...", 80);
	 					menuVendas = new PainelVendas();
	 					break;
	 				}   	 				
	 				case 8:
	 				{
	 					Starter.setarProgresso("Carregando componentes do sistema...", 95);
	 					new Usuario();
	 					menuVendaMesa2 = new PainelVendaMesa();
	 					break;
	 				}    	 				
	 				case 9:
	 				{
	 					janela.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	 					janela.addWindowListener(new WindowAdapter()
	 					{
	 						public void windowClosing(WindowEvent e)
	 						{
	 							int opcao = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja sair??", "Logout",JOptionPane.YES_NO_OPTION);
	 	    			
	 							if(opcao == JOptionPane.YES_OPTION)
	 								logout();
	 						}
	 					});	
	 			
	 					componentesCentrais = new JPanel(new CardLayout());
	 					componentesCentrais.setMinimumSize(new Dimension(1020, 650));
	 					componentesCentrais.setMaximumSize(new Dimension(1920, 910));
	 			
	 					componentesCentrais.add(menuMesas, "Menu Mesas");
	 					componentesCentrais.add(menuVendaMesa2, "Menu Venda Mesa");
	 					componentesCentrais.add(menuVendaRapida, "Menu Venda Rapida");
	 					componentesCentrais.add(menuProdutos, "Menu Produtos");
	 					componentesCentrais.add(menuFuncionarios, "Menu Funcionarios");
	 					componentesCentrais.add(menuVendas, "Menu Vendas");			
	 			
	 					janela.setMinimumSize(new Dimension(1024, 720));	
	 					janela.setMaximumSize(new Dimension(1920, 1080));	
	 					janela.setLocationRelativeTo(null);
	 					janela.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	 			
	 					principalPainel1.setLayout(new BoxLayout(principalPainel1, BoxLayout.Y_AXIS));
	 					principalPainel1.setMaximumSize(new Dimension(800, 640));		
	 			
	 					principalPainel1.add(menuStatus);
	 					principalPainel1.add(menuPainel);
	 					principalPainel1.add(componentesCentrais);
	 					principalPainel1.add(menuFooter);
	 			
	 					janela.add(principalPainel1);
	 					janela.setResizable(true);
	 					janela.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/icone_programa.png")).getImage());
	 					
	 					Starter.setarProgresso("Carregando login...", 95);
	 					menuLogin = new LoginDialog();
	 				
	 					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e1.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
					}
	 					
	 					Starter.loadingFinalizado();
	 					menuLogin.pack ();
	 					menuLogin.setLocationRelativeTo(null);
	 					menuLogin.setVisible(true);   	 					
	 					break;
	 				}   	 				
	 				default:
	 				{
	 					t.stop();
	 				}
	 			}
	 			
	 			loadingTime++;
	 		}
	 	});
	 	
	 	t.start();*/		
	}
	
	private static class MenuPrincipalSingletonHolder { 
		public static final MenuPrincipal INSTANCE = new MenuPrincipal();
	}
 
	public static MenuPrincipal getInstance() {
		return MenuPrincipalSingletonHolder.INSTANCE;
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
		
		if(modelo == 0)			// Abre o menu venda rapida
		{
			CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
			cardLayout.show(componentesCentrais, "Menu Venda Rapida");
			PainelVendaRapida.getInstance().setaFocusAdd();
		}
		else if(modelo == 1)			// Abre o menu de produtos
		{
			CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
			cardLayout.show(componentesCentrais, "Menu Produtos");
		}
		else if(modelo == 2)			// Abre o menu de funcionarios
		{
			CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
			cardLayout.show(componentesCentrais, "Menu Funcionarios");
		}
		else if(modelo == 3)			// Abre o menu de vendas
		{
			PainelVendas.getInstance().ultimasVendasRefresh();
			CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
			cardLayout.show(componentesCentrais, "Menu Vendas");
		}
		else if(modelo == 4)			// Abre o inicio.
		{
			CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
			cardLayout.show(componentesCentrais, "Menu Mesas");
		}
		else if(modelo == 5)			// Abre o menu de clientes.
		{
			CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
			cardLayout.show(componentesCentrais, "Menu Clientes");
		}
		else if(modelo == 6)			// Abre o menu de cozinha.
		{
			CardLayout cardLayout = (CardLayout) componentesCentrais.getLayout();
			cardLayout.show(componentesCentrais, "Menu Cozinha");
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
        Login.getInstance().setVisible(true);
        janela.setVisible(false);
	}		
}