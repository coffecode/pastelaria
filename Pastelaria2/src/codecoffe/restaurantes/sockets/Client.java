package codecoffe.restaurantes.sockets;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import codecoffe.restaurantes.interfaceGrafica.Login;
import codecoffe.restaurantes.interfaceGrafica.MenuPrincipal;
import codecoffe.restaurantes.interfaceGrafica.PainelCozinha;
import codecoffe.restaurantes.interfaceGrafica.PainelMesas;
import codecoffe.restaurantes.interfaceGrafica.PainelVendaMesa;
import codecoffe.restaurantes.interfaceGrafica.PainelVendaRapida;
import codecoffe.restaurantes.interfaceGrafica.VendaMesaAdicionaisCampo;
import codecoffe.restaurantes.interfaceGrafica.VendaMesaProdutoCampo;
import codecoffe.restaurantes.interfaceGrafica.VendaRapidaAdicionaisCampo;
import codecoffe.restaurantes.interfaceGrafica.VendaRapidaProdutoCampo;
import codecoffe.restaurantes.primitivas.Pedido;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.managers.notification.NotificationManager;

public class Client implements Runnable
{
	private static ObjectInputStream sInput;
	private static ObjectOutputStream sOutput;
	private static Socket socket;
	private static InetAddress hostname;
	private static int port;
	private static boolean clienteConectado;
	private static Date ultimoPing;
	private Timer timerPing;
	
	public Client(InetAddress host, int porta)
	{
		hostname = host;
		port = porta;
		System.out.println("Iniciando modo cliente. host: " + host);
	}

	@Override
	public void run() {
		try {
			socket = new Socket(hostname, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Conexao aceita: " + socket.getInetAddress() + ":" + socket.getPort());
		Client.clienteConectado = true;
		
		try {
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			
			System.out.println("Input/Output criados.");
		} catch (IOException e) {
			System.out.println("Error ao criar imput e output cliente: " + e);
		}
		
		try {
			sOutput.reset();
			sOutput.writeObject("" + UtilCoffe.VERSAO);
		} catch (IOException e) {
			System.out.println("Error ao mandar versao: " + e);
		}
		
		new ListenFromServer().start();
		
		timerPing = new Timer();
		timerPing.schedule(new PingPeriodico(), 30*1000, 5*1000);
		
    	System.out.println("Enviando pedido de configuracao atualizada.");
    	Client.enviarObjeto("UPDATE CONFIGURACAO");  		
	}
	
	class PingPeriodico extends TimerTask 
	{
        public void run() 
        {
        	long duration = System.currentTimeMillis() - ultimoPing.getTime();
        	long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        	
        	if(seconds > 10)	// ja faz 10 segundos desde a última resposta
        	{
        		enviarObjeto(new String("ping!"));	// testando a conexão
        		ultimoPing = new Date();
        	}
        }
	}
	
	public static void enviarObjeto(Object objeto)
	{
		try {
			sOutput.reset();
			sOutput.writeObject(objeto);
		} catch (IOException e) {
			System.out.println("Error ao mandar objeto: " + e);
		}
	}
	
	public static void disconnect()
	{
		clienteConectado = false;
		
		if(Client.sInput != null)
			try {
				sInput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		if(sOutput != null)
			try {
				sOutput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		if(socket != null)
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	class ListenFromServer extends Thread
	{
		public void run()
		{
			while(clienteConectado)
			{
				try {
					Object dataRecebida = sInput.readUnshared();
					ultimoPing = new Date();
					
					if(dataRecebida != null)
					{						
						if(dataRecebida instanceof CacheTodosProdutos)
						{
							System.out.println("Recebendo produtos atualizados.");
							CacheTodosProdutos produtosAtualizados = (CacheTodosProdutos)dataRecebida;
							
							PainelVendaMesa.atualizaProdutos(produtosAtualizados);
							PainelVendaRapida.atualizaProdutos(produtosAtualizados);
							VendaRapidaProdutoCampo.AtualizaProdutos(produtosAtualizados);
							VendaRapidaAdicionaisCampo.AtualizaProdutos(produtosAtualizados);
							
							VendaMesaProdutoCampo.AtualizaProdutos(produtosAtualizados);
							VendaMesaAdicionaisCampo.AtualizaProdutos(produtosAtualizados);							
						}
						else if(dataRecebida instanceof CacheTodasMesas)
						{
							System.out.println("Atualizacao de todas as mesas recebida.");
							CacheTodasMesas tm = (CacheTodasMesas)dataRecebida;
							MenuPrincipal.atualizarTodasMesas(tm);
						}
						else if(dataRecebida instanceof CacheMesaHeader)
						{
							NotificationManager.setLocation(2);
							NotificationManager.showNotification(MenuPrincipal.getJanela(), "Atualizacao de mesa recebida.").setDisplayTime(2000);
							
							System.out.println("Atualizacao de mesa recebida.");
							CacheMesaHeader mh = (CacheMesaHeader)dataRecebida;
							PainelMesas.atualizaMesaCache(mh.getMesaId(), mh.getMesaVenda());
						}
						else if(dataRecebida instanceof CacheTodosPedidos)
						{
							System.out.println("Todos pedidos do servidor recebido.");
							CacheTodosPedidos tp = (CacheTodosPedidos)dataRecebida;
							PainelCozinha.atualizaTodosPedidos(tp);
						}						
						else if(dataRecebida instanceof Pedido)
						{
							System.out.println("Pedido do servidor recebido.");
							Pedido ped = (Pedido)dataRecebida;
							PainelCozinha.atualizaPedido(ped);
						}
						else if(dataRecebida instanceof CacheAviso)
						{
							System.out.println("Aviso do servidor recebido.");
							CacheAviso aviso = (CacheAviso)dataRecebida;
							if(aviso.getClasse() == UtilCoffe.VENDA_RAPIDA)	// Painel Venda Rapida
							{
								if(aviso.getTipo() == 1) // Venda Realizada com Sucesso
								{
									PainelVendaRapida.receberAviso(aviso);
								}
							}
							else
							{
								if(aviso.getTipo() == 1) // Venda Realizada com Sucesso
								{
									PainelVendaMesa.receberAviso(aviso);
								}								
							}
						}
						else if(dataRecebida instanceof CacheAutentica)
						{
							CacheAutentica cc = (CacheAutentica)dataRecebida;
							Login.autentica(cc);
						}
						else if(dataRecebida instanceof CacheConfiguracoes)
						{
							System.out.println("Recebendo configurações atualizadas.");
							CacheConfiguracoes configAtualizada = (CacheConfiguracoes)dataRecebida;
							new Configuracao(configAtualizada);
							new MenuPrincipal(UtilCoffe.CLIENT, hostname, port);
						}						
					}
				} catch (ClassNotFoundException | IOException e) {
					System.out.println("Fechando conexao com o servidor.");
					break;
				}
			}
		}
	}
}
