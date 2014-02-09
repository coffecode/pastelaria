package codecoffe.restaurantes.sockets;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import codecoffe.restaurantes.interfaceGrafica.PainelClientes;
import codecoffe.restaurantes.interfaceGrafica.PainelCozinha;
import codecoffe.restaurantes.interfaceGrafica.PainelErro;
import codecoffe.restaurantes.interfaceGrafica.PainelMesas;
import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.primitivas.Pedido;
import codecoffe.restaurantes.utilitarios.Bartender;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.DiarioLog;
import codecoffe.restaurantes.utilitarios.Usuario;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

public class Server implements Runnable
{
	private int port;
	private boolean procurandoConexoes;
	private int uniqueId = 1;
	private ArrayList<ClienteThread> listaClientes;
	
	private Server() {}
	
	private static class ServerSingletonHolder { 
		public static final Server INSTANCE = new Server();
	}
 
	public static Server getInstance() {
		return ServerSingletonHolder.INSTANCE;
	}
	
	public void atualizaConexao(int porta)
	{
		port = porta;
		procurandoConexoes = true;
		listaClientes = new ArrayList<ClienteThread>();
		System.out.println("Server TCP iniciado na porta: " + porta);
	}
	
	synchronized void remove(int id)
	{
		for(int i = 0; i < listaClientes.size(); ++i)
		{
			ClienteThread ct = listaClientes.get(i);
			if(ct.id == id)
			{
				listaClientes.remove(i);
				return;
			}
		}
	}
	
	public void terminate()
	{
		enviaTodos("BYE");
		procurandoConexoes = false;
	}

	@Override
	public void run() 
	{
		try {
			// criando o servidor na porta marcada
			ServerSocket serverSocket = new ServerSocket(port);
			
			while(procurandoConexoes)	//fica procurando conexao para aceitar.
			{
				Socket socket = serverSocket.accept();
				ClienteThread cliente = new ClienteThread(socket);		// Cria uma Thread para esse cliente.
				listaClientes.add(cliente);								// Adiciona na lista de clientes.
				cliente.start();										// Começa a conexão com esse cara.
			}
			
			for(int i = 0; i < listaClientes.size(); ++i)
			{
				ClienteThread tc = listaClientes.get(i);
				try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
				}catch(IOException e) {}	// erro não interessa.
			}
			
			serverSocket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			terminate();
			new PainelErro(e);
			System.exit(0);
		}
	}
	
	public synchronized void enviaTodos(Object ob) 
	{
		for(int i = listaClientes.size(); --i >= 0;) {
			ClienteThread ct = listaClientes.get(i);
			if(!ct.enviarObjeto(ob)) {
				listaClientes.remove(i);
			}
		}
	}
	
	public synchronized void enviaTodos(Object ob, int ex) 
	{
		for(int i = listaClientes.size(); --i >= 0;) {
			ClienteThread ct = listaClientes.get(i);
			if(ct.id != ex)
			{
				if(!ct.enviarObjeto(ob)) {
					listaClientes.remove(i);
				}	
			}
		}
	}
	
	public void enviaObjeto(Object ob, int cliente)
	{
		for(int i = listaClientes.size(); --i >= 0;) {
			ClienteThread ct = listaClientes.get(i);
			if(ct.id == cliente)
			{
				if(!ct.enviarObjeto(ob)) {
					listaClientes.remove(i);
				}
				break;
			}
		}		
	}
	
	class ClienteThread extends Thread
	{
		int id;
		boolean clienteConectado = true;
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;

		ClienteThread(Socket socket)
		{
			this.id = ++uniqueId;
			this.socket = socket;
			
			try {
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				
				Object data = sInput.readUnshared();
				
				if(data instanceof String)
				{
					if(!data.toString().equals(UtilCoffe.VERSAO))
					{
						sOutput.reset();
						sOutput.writeObject("WRONG VERSION");						
						this.clienteConectado = false;	// kicka, versão diferente do servidor.
					}
				}
				else
					this.clienteConectado = false;	// kicka pois não informou versão.
				
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				new PainelErro(e);
				this.clienteConectado = false;	// kicka pois não informou versão.
			}
		}
		
		public void run()		// fica rodando sempre
		{			
			while(this.clienteConectado)
			{
				try {
					Object dataRecebida = sInput.readUnshared();
					
					if(dataRecebida instanceof String)	// é uma string
					{
						String decodifica = (String)dataRecebida;
						if(decodifica.equals("UPDATE PRODUTOS"))
						{
							CacheTodosProdutos haha = new CacheTodosProdutos();
							haha.atualizarProdutos();
							sOutput.reset();
							sOutput.writeObject(haha);
						}
						else if(decodifica.equals("UPDATE MESAS"))
						{
							sOutput.reset();
							sOutput.writeObject(PainelMesas.getInstance().getTodasMesas());
						}						
						else if(decodifica.equals("UPDATE PEDIDOS"))
						{
							sOutput.reset();
							sOutput.writeObject(PainelCozinha.getInstance().getTodosPedidos());
						}
						else if(decodifica.equals("UPDATE CLIENTES"))
						{
							CacheClientes cc = PainelClientes.getInstance().getTodosClientes();
							cc.setHeader(UtilCoffe.CLIENTE_ATUALIZAR);
							
							sOutput.reset();
							sOutput.writeObject(cc);
						}
						else if(decodifica.equals("UPDATE CONFIGURACAO"))
						{
							System.out.println("Enviando configuracao");
							
							sOutput.reset();
							sOutput.writeObject(Configuracao.INSTANCE.gerarCache());
						}						
						else if(decodifica.equals("ADEUS"))
						{
							this.clienteConectado = false;
						}
						else if(decodifica.contains(";QUIT"))
						{
							String nome = "";
							
							for(int i = 0; i < decodifica.length(); i++)
							{
								if(decodifica.charAt(i) == ';')
									break;
								
								nome += decodifica.charAt(i);
							}
							
							if(!UtilCoffe.vaziu(nome))
								DiarioLog.add(nome, "Saiu do sistema.", 9);
						}							
					}
					else if(dataRecebida instanceof CacheMesaHeader)	// é alguma atualização de mesa
					{
						CacheMesaHeader mh = (CacheMesaHeader)dataRecebida;
						Bartender.INSTANCE.enviarMesa(mh, this.id);
					}
					else if(dataRecebida instanceof CacheClientes)	// é alguma atualização de clientes
					{
						CacheClientes cc = (CacheClientes)dataRecebida;
						if(Bartender.INSTANCE.enviarCliente(cc))
						{
							sOutput.reset();
							sOutput.writeObject(new CacheAviso(cc.getHeader(), UtilCoffe.CLASSE_CLIENTES));							
						}
					}
					else if(dataRecebida instanceof Pedido)	// é um pedido
					{
						Pedido ped = (Pedido)dataRecebida;
						Bartender.INSTANCE.enviarPedido(ped);
					}
					else if(dataRecebida instanceof CacheVendaFeita)	// é uma venda realizada
					{
						CacheVendaFeita vendaFeita = (CacheVendaFeita)dataRecebida;
						
						if(vendaFeita.imprimir)
						{
							Bartender.INSTANCE.criarImpressao(vendaFeita);
						}
						else
						{
							int sucesso = Bartender.INSTANCE.enviarVenda(vendaFeita, this.id);
							if(sucesso > 0)
							{
								sOutput.reset();
								sOutput.writeObject(new CacheAviso(1, vendaFeita.classe, "A venda foi concluída com sucesso!", "Venda #" + sucesso));
							}
							else
							{
								sOutput.reset();
								sOutput.writeObject(new CacheAviso(2, vendaFeita.classe, "Não foi possível concluir a venda", "Erro"));
							}
						}
					}
					else if(dataRecebida instanceof CacheAutentica)
					{
						CacheAutentica autentica = (CacheAutentica)dataRecebida;
						
						Query teste = new Query();
						
						try {
							String formatacao;
							formatacao = "SELECT password, level, nome FROM funcionarios WHERE username = '" + autentica.username + "';";							
							teste.executaQuery(formatacao);
							
							if(teste.next())
							{
								if(teste.getString("password").equals(autentica.password))
								{
									if(teste.getString("nome").equals(Usuario.INSTANCE.getNome()))
									{
										autentica.header = 4;
										sOutput.reset();
										sOutput.writeObject(autentica);									
									}
									else
									{
										autentica.header = 1;
										autentica.nome = teste.getString("nome");
										autentica.level = teste.getInt("level");
										DiarioLog.add(teste.getString("nome"), "Fez login no sistema.", 8);
										
										sOutput.reset();
										sOutput.writeObject(autentica);
									}
									
									teste.fechaConexao();
								}
								else
								{
									teste.fechaConexao();
									autentica.header = 3;
									sOutput.reset();
									sOutput.writeObject(autentica);
								}
							}
							else
							{
								autentica.header = 2;
								sOutput.reset();
								sOutput.writeObject(autentica);
							}							
						} catch (SQLException e) {
							e.printStackTrace();
							new PainelErro(e);
							autentica.header = 2;
							sOutput.reset();
							sOutput.writeObject(autentica);
							teste.fechaConexao();
						}
					}
					
				} catch (ClassNotFoundException | IOException e) {
					if(e.getMessage().contains("Connection reset") || e.getMessage().toLowerCase().contains("socket closed"))
					{
						System.out.println("Cliente desconectado");
						this.clienteConectado = false;
					}
					else
					{
						e.printStackTrace();
						new PainelErro(e);
						terminate();
						System.exit(0);
					}
				}
			}
			
			remove(id);
			close();
		}
		
		private boolean enviarObjeto(Object ob) {
			if(!socket.isConnected()) {
				close();
				return false;
			}
			
			try {
				sOutput.reset();
				sOutput.writeObject(ob);
				return true;
			}
			catch(IOException e) {
				e.printStackTrace();
				close();
				return false;
			}
		}		
		
		private void close()
		{
			if(sOutput != null)
				try {
					sOutput.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			
			if(sInput != null)
				try {
					sInput.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			
			if(socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
		}
	}
}
