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

import codecoffe.restaurantes.interfaceGrafica.Loading;
import codecoffe.restaurantes.interfaceGrafica.Login;
import codecoffe.restaurantes.interfaceGrafica.PainelPrincipal;
import codecoffe.restaurantes.interfaceGrafica.PainelClientes;
import codecoffe.restaurantes.interfaceGrafica.PainelCozinha;
import codecoffe.restaurantes.interfaceGrafica.PainelDisconnect;
import codecoffe.restaurantes.interfaceGrafica.PainelErro;
import codecoffe.restaurantes.interfaceGrafica.PainelMesas;
import codecoffe.restaurantes.interfaceGrafica.PainelVendaMesa;
import codecoffe.restaurantes.interfaceGrafica.PainelVendaRapida;
import codecoffe.restaurantes.primitivas.Pedido;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.managers.notification.NotificationManager;
import com.alee.utils.ThreadUtils;

public class Client implements Runnable
{
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private Socket socket;
	private InetAddress hostname;
	private int port;
	private boolean clienteConectado;
	private boolean finalizarPrograma = false;
	private boolean reLoad = false;
	private Date ultimoPing;
	private Timer timerPing;
	
	private Client() {}
	
	private static class ClientSingletonHolder { 
		public static final Client INSTANCE = new Client();
	}
 
	public static Client getInstance() {
		return ClientSingletonHolder.INSTANCE;
	}
	
	public void atualizaConexao(InetAddress host, int porta) throws IOException
	{
		hostname = host;
		port = porta;
		socket = new Socket(hostname, port);
	}

	@Override
	public void run()
	{
		conexaoEstabelecida();
	}
	
	private void conexaoEstabelecida()
	{
		System.out.println("Conexao aceita: " + socket.getInetAddress() + ":" + socket.getPort());
		clienteConectado = true;
		
		try {
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			
			System.out.println("Input/Output criados.");
		} catch (IOException e) {
			System.out.println("Error ao criar imput e output cliente: " + e);
		}
		
		enviarObjeto("" + UtilCoffe.VERSAO);
		new ListenFromServer().start();
		
		timerPing = new Timer();
		timerPing.schedule(new PingPeriodico(), 30*1000, 3*1000);
		
    	System.out.println("Enviando pedido de configuracao atualizada.");
    	enviarObjeto("UPDATE CONFIGURACAO");  		
	}
	
	private void conexaoEstabelecida(int reconexao)
	{
		System.out.println("Conexao aceita: " + socket.getInetAddress() + ":" + socket.getPort());
		
		try {
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			clienteConectado = true;
			System.out.println("Input/Output criados.");
		} catch (IOException e) {
			System.out.println("Error ao criar imput e output cliente: " + e);
		}
		
		enviarObjeto("" + UtilCoffe.VERSAO);
		
    	System.out.println("Enviando pedido de configuracao atualizada.");
    	enviarObjeto("UPDATE CONFIGURACAO");  		
	}	
	
	public void clientReconnect()
	{		
		if(!reLoad)
		{
			reLoad = true;
			PainelPrincipal.getInstance().setEnabled(false);
			disconnect();
			
			new Thread ( new Runnable ()
	        {
	            @Override
	            public void run ()
	            {
	            	boolean sucesso = false;
	            	PainelDisconnect dcPanel = new PainelDisconnect();
	            	dcPanel.setVisible(true);
	            	
	            	while(!sucesso)
	            	{
	            		ThreadUtils.sleepSafely(3000);
	            		
	            		try 
	            		{
	            			System.out.println("Tentando reconectar com o servidor.");
	            			socket = new Socket(hostname, port);
	            			sucesso = true;
	            			conexaoEstabelecida(1);
	            			dcPanel.dispose();
	            		} 
	            		catch (IOException e) 
	            		{
	            			
	            		}            		
	            	}
	            }
	        } ).start ();				
		}	
	}
	
	public void finalizaPrograma(int motivo)
	{
		clienteConectado = false;
		
		if(motivo == 1)
		{
			JOptionPane.showMessageDialog(null, "O programa principal fechou todas conexões.");
		}
		else if(motivo == 2)
		{
			JOptionPane.showMessageDialog(null, "A versão desse programa difere da versão do Principal.");
		}
		
		finalizarPrograma = true;
		System.exit(0);
	}
	
	class PingPeriodico extends TimerTask 
	{
        public void run() 
        {
        	if(clienteConectado)
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
	}
	
	public void enviarObjeto(Object objeto)
	{
		try {
			sOutput.reset();
			sOutput.writeObject(objeto);
		} catch (IOException e) {
			
			if(e.getMessage().contains("Connection reset"))
			{
				System.out.println("Conexão perdida com o servidor.");
				clientReconnect();
			}
			else	// erro desconhecido
			{
				e.printStackTrace();
				new PainelErro(e);
				finalizarPrograma = true;
				clienteConectado = false;
				System.exit(0);
			}
		}
	}
	
	public void disconnect()
	{
		clienteConectado = false;
		
		if(sInput != null)
		{
			try {
				sInput.close();
			} catch (IOException e) {}
		}
		
		if(sOutput != null)
		{
			try {
				sOutput.close();
			} catch (IOException e) {}
		}
		
		if(socket != null)
		{
			try {
				socket.close();
			} catch (IOException e) {}
		}
	}
	
	class ListenFromServer extends Thread
	{
		public void run()
		{
			while(!finalizarPrograma)
			{
				if(clienteConectado)
				{
					try {
						Object dataRecebida = sInput.readUnshared();
						ultimoPing = new Date();
						
						if(dataRecebida != null)
						{	
							if(dataRecebida instanceof String)
							{
								String decodifica = (String)dataRecebida;
								if(decodifica.equals("BYE"))
								{
									finalizaPrograma(1);
								}
								else if(decodifica.equals("WRONG VERSION"))
								{
									finalizaPrograma(2);
								}
							}
							else if(dataRecebida instanceof CacheTodosProdutos)
							{
								System.out.println("Recebendo produtos atualizados.");
								CacheTodosProdutos produtosAtualizados = (CacheTodosProdutos)dataRecebida;
								
								PainelVendaMesa.getInstance().atualizaProdutos(produtosAtualizados);
								PainelVendaRapida.getInstance().atualizaProdutos(produtosAtualizados);	
								PainelCozinha.getInstance().atualizaProdutos(produtosAtualizados);		
							}
							else if(dataRecebida instanceof CacheTodasMesas)
							{
								System.out.println("Recebendo atualização de todas as mesas do servidor.");
								CacheTodasMesas tm = (CacheTodasMesas)dataRecebida;
								PainelPrincipal.getInstance().atualizarTodasMesas(tm);
							}
							else if(dataRecebida instanceof CacheMesaHeader)
							{								
								System.out.println("Recebendo atualização de mesa do servidor.");
								CacheMesaHeader mh = (CacheMesaHeader)dataRecebida;
								PainelMesas.getInstance().atualizaMesaCache(mh.getMesaId(), mh.getMesaVenda());
								
								if(mh.getHeader() == UtilCoffe.MESA_ERROR)
								{
									NotificationManager.setLocation(2);
									NotificationManager.showNotification(PainelPrincipal.getInstance().getJanela(), 
									"Houve um erro e não foi possível atualizar a mesa.").setDisplayTime(2000);							
								}
							}
							else if(dataRecebida instanceof CacheTodosPedidos)
							{
								System.out.println("Recebendo todos os pedidos do servidor.");
								CacheTodosPedidos tp = (CacheTodosPedidos)dataRecebida;
								PainelCozinha.getInstance().atualizaTodosPedidos(tp);
							}
							else if(dataRecebida instanceof CacheTodosFuncionarios)
							{
								System.out.println("Recebendo todos os funcionários do servidor.");
								CacheTodosFuncionarios cf = (CacheTodosFuncionarios)dataRecebida;
								PainelVendaRapida.getInstance().atualizaFuncionarios(cf);
								PainelVendaMesa.getInstance().atualizaFuncionarios(cf);
							}	
							else if(dataRecebida instanceof Pedido)
							{
								System.out.println("Recebendo pedido do servidor.");
								Pedido ped = (Pedido)dataRecebida;
								PainelCozinha.getInstance().atualizaPedido(ped);
							}
							else if(dataRecebida instanceof CacheAviso)
							{
								System.out.println("Recebendo aviso do servidor.");
								CacheAviso aviso = (CacheAviso)dataRecebida;
								if(aviso.getClasse() == UtilCoffe.CLASSE_CLIENTES)
									PainelClientes.getInstance().receberAviso(aviso);
								else if(aviso.getClasse() == UtilCoffe.CLASSE_VENDA_RAPIDA)
									PainelVendaRapida.getInstance().receberAviso(aviso);
								else
									PainelVendaMesa.getInstance().receberAviso(aviso);
							}
							else if(dataRecebida instanceof CacheClientes)
							{
								System.out.println("Recebendo clientes atualizado.");
								CacheClientes clientesAtualizado = (CacheClientes)dataRecebida;
								
								if(clientesAtualizado.getHeader() == UtilCoffe.CLIENTE_ADICIONAR)
								{
									PainelClientes.getInstance().adicionarCliente(clientesAtualizado);
								}
								else if(clientesAtualizado.getHeader() == UtilCoffe.CLIENTE_EDITAR)
								{
									PainelClientes.getInstance().editarClientes(clientesAtualizado);
								}
								else if(clientesAtualizado.getHeader() == UtilCoffe.CLIENTE_REMOVER)
								{
									PainelClientes.getInstance().removerClientes(clientesAtualizado);
								}
								else
								{
									PainelClientes.getInstance().atualizarClientes(clientesAtualizado);
								}
							}							
							else if(dataRecebida instanceof CacheAutentica)
							{
								CacheAutentica cc = (CacheAutentica)dataRecebida;
								Login.getInstance().autentica(cc);
							}
							else if(dataRecebida instanceof CacheConfiguracoes)
							{
								System.out.println("Recebendo configurações atualizadas.");
								CacheConfiguracoes configAtualizada = (CacheConfiguracoes)dataRecebida;								
								Configuracao.INSTANCE.atualizarConfiguracao(configAtualizada);
								
								if(!reLoad)
									new Loading(UtilCoffe.CLIENT);
								else
								{
							    	System.out.println("Enviando pedido da lista de produtos atualizado.");
							    	enviarObjeto("UPDATE PRODUTOS");
							    	
							    	System.out.println("Enviando pedido da lista de mesas atualizada.");
							    	enviarObjeto("UPDATE MESAS");        	
							    	
							    	System.out.println("Enviando pedido da lista de pedidos atualizada.");
							    	enviarObjeto("UPDATE PEDIDOS");
							    	
							    	System.out.println("Enviando pedido da lista de clientes atualizada.");
							    	enviarObjeto("UPDATE CLIENTES");
							    	
							    	System.out.println("Enviando pedido da lista de funcionários atualizada.");
							    	enviarObjeto("UPDATE FUNCIONARIOS");
							    	
							    	PainelPrincipal.getInstance().setEnabled(true);
							    	reLoad = false;
								}
							}						
						}
					} catch (ClassNotFoundException | IOException e) {
						if(e.getMessage().contains("Connection reset"))
						{
							System.out.println("Conexão perdida com o servidor.");
							clienteConectado = false;
							clientReconnect();
						}
						else if(e.getMessage().toLowerCase().contains("socket closed"))
						{
							// de boa
						}
						else	// erro desconhecido.
						{
							System.err.println(e.getMessage());
							e.printStackTrace();
							new PainelErro(e);
							finalizarPrograma = true;
							clienteConectado = false;
							System.exit(0);
						}
					}					
				}
			}
		}
	}
}
