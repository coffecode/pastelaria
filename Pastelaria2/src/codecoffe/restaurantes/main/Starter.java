package codecoffe.restaurantes.main;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EtchedBorder;

import codecoffe.restaurantes.interfaceGrafica.MenuPrincipal;
import codecoffe.restaurantes.interfaceGrafica.PainelDisconnect;
import codecoffe.restaurantes.interfaceGrafica.PainelErro;
import codecoffe.restaurantes.sockets.BroadcastServer;
import codecoffe.restaurantes.sockets.Client;
import codecoffe.restaurantes.sockets.Server;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.progressbar.WebProgressBar;
import com.alee.managers.language.LanguageManager;
import com.alee.utils.ThreadUtils;

public class Starter implements ActionListener
{
	private JFrame seleciona, splash;
	private JPanel selecionaPanel, inicio;
	private JProgressBar progressBar;
	private JLabel descricaoOperacao, statusProgress;
	private WebButton bTerminal, bPrincipal;
	private WebProgressBar verificandoBar;
	private static final int portaConnect = 12345;
	
	public Starter()
	{
		System.setProperty("java.net.preferIPv4Stack", "true");
		LanguageManager.DEFAULT = LanguageManager.PORTUGUESE;
		WebLookAndFeel.install();
		
		seleciona = new JFrame("CodeCoffe " + UtilCoffe.VERSAO);
		seleciona.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		selecionaPanel = new JPanel();
		selecionaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Modo de Operação"));
		selecionaPanel.setLayout(null);
		
		bTerminal = new WebButton("Terminal");
		bTerminal.setRolloverShine(true);
		bTerminal.setBounds(30, 40, 100, 100); // Coluna, Linha, Largura, Altura
		bTerminal.setHorizontalTextPosition(AbstractButton.CENTER);
		bTerminal.setVerticalTextPosition(AbstractButton.BOTTOM);
		bTerminal.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/terminal.png")));
		bTerminal.addActionListener(this);
		selecionaPanel.add(bTerminal);
		
		bPrincipal = new WebButton("Principal");
		bPrincipal.setRolloverShine(true);
		bPrincipal.setBounds(160, 40, 100, 100); // Coluna, Linha, Largura, Altura
		bPrincipal.setHorizontalTextPosition(AbstractButton.CENTER);
		bPrincipal.setVerticalTextPosition(AbstractButton.BOTTOM);
		bPrincipal.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/principal.png")));
		bPrincipal.addActionListener(this);
		selecionaPanel.add(bPrincipal);
		
		verificandoBar = new WebProgressBar();
		verificandoBar.setIndeterminate(true);
        verificandoBar.setStringPainted(true);
        verificandoBar.setString("Verificando conexões...");
        verificandoBar.setBounds(30, 150, 230, 30); // Coluna, Linha, Largura, Altura
        verificandoBar.setVisible(false);
        selecionaPanel.add(verificandoBar);
		
		descricaoOperacao = new JLabel("Escolha o modo de operação do programa.");
		descricaoOperacao.setFont(new Font("Verdana", Font.PLAIN, 10));
		descricaoOperacao.setBounds(10, 150, 250, 50); // Coluna, Linha, Largura, Altura
		selecionaPanel.add(descricaoOperacao);
		
		seleciona.add(selecionaPanel);
		seleciona.setSize(305,230);
		seleciona.setLocationRelativeTo(null);
		seleciona.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/icone_programa.png")).getImage());
		seleciona.setResizable(false);
		seleciona.setVisible(true);
		
		/*splash = new JFrame();
		inicio = new JPanel();
		inicio.setLayout(null);
		
		//UIManager.put("ProgressBar.foreground", new Color(100, 100, 100));
		
		JLabel splashImage = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("imgs/splashscreen.png")));
		splashImage.setBounds(1,1,445,276); // Coluna, Linha, Largura, Altura!
		
    	progressBar = new JProgressBar(0, 100); // progresso
    	progressBar.setValue(0);
    	progressBar.setStringPainted(true);
    	progressBar.setBounds(1, 220, 443, 55); // Coluna, Linha, Largura, Altura
    	
    	statusProgress = new JLabel("Preparando inicialização...");
    	statusProgress.setBounds(10, 184, 443, 55); // Coluna, Linha, Largura, Altura
    	
    	inicio.add(statusProgress);
    	inicio.add(progressBar);
    	inicio.add(splashImage);
		splash.add(inicio);
		
		splash.setUndecorated(true);
		splash.setSize(445,276);							// Largura, Altura
		splash.setLocationRelativeTo(null);				// Abre no centro da tela
		splash.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/icone_programa.png")).getImage());
		splash.setResizable(false);
		splash.setVisible(true);
		
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
		}
		
		progressBar.setValue(2);
		statusProgress.setText("Verificando banco de dados...");
		
		String line;
		String pidInfo ="";

		try {
			Process p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
			BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
			    pidInfo+=line; 
			}
			input.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
		}		

		if(pidInfo.contains("mysqld"))
		{
			progressBar.setValue(10);
			statusProgress.setText("Banco de dados está OK, continuando...");
		}
		else
		{
			statusProgress.setText("Iniciando banco de dados...");
			boolean ligando = true;
			
			while(ligando)
			{
				String command = System.getProperty("user.dir") + "\\mysql-5.6.15-win32\\bin\\mysqld.exe";
				try
				{
				    Runtime.getRuntime().exec(command);
					try {
						Thread.sleep(1000);
						
						try {
							Process p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
							BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
							while ((line = input.readLine()) != null) {
							    pidInfo+=line; 
							}
							input.close();
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
						}	

						if(pidInfo.contains("mysqld"))
						{
							progressBar.setValue(10);
							statusProgress.setText("Banco de dados ligado, continuando...");
							ligando = false;
						}
						else
						{
							statusProgress.setText("Não foi possível ligar o banco de dados, tentando novamente ...");
							Thread.sleep(1000);
						}
					} catch (InterruptedException e) {
						JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
					}			    
				} 
				catch (IOException e)
				{
				    e.printStackTrace();
				}					
			}
		}
		
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
		}		
		
		progressBar.setValue(20);
		statusProgress.setText("Carregando programa...");
		
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + e.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
		}*/
		
		//new MenuPrincipal(2);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == bTerminal)
		{			
			descricaoOperacao.setText("");
			verificandoBar.setVisible(true);
			new CheckServer(2).start();
		}
		else if(e.getSource() == bPrincipal)
		{
			descricaoOperacao.setText("");
			verificandoBar.setVisible(true);
			new CheckServer(1).start();
		}
	}
	
	public void comecarLoading(int modo, InetAddress host, int porta)
	{
		seleciona.dispose();
		try
		{
			if(modo == UtilCoffe.CLIENT)
			{
				Configuracao.INSTANCE.setModo(UtilCoffe.CLIENT);
				
				Client.getInstance().atualizaConexao(host, porta);
	        	new Thread(Client.getInstance()).start();				
			}
			else
			{				
				Configuracao.INSTANCE.setModo(UtilCoffe.SERVER);		
				
				Server.getInstance().atualizaConexao(porta);
	        	new Thread(Server.getInstance()).start();
	        	
	        	BroadcastServer serverUDP = new BroadcastServer(porta);
	        	new Thread(serverUDP).start();
	        	
	        	MenuPrincipal.getInstance();
			}
		}
		catch(Exception ex)
		{
			new PainelErro(ex);
		}
	}
	
	public void setarProgresso(String texto, int valor)
	{
		progressBar.setValue(valor);
		statusProgress.setText(texto);
	}
	
	public void loadingFinalizado()
	{
		splash.dispose();
	}
	
	public static void main(String[] args) {
		new Starter();
	}
	
	class CheckServer extends Thread
	{
		private int operacao;
		private DatagramSocket socket;
		private InetAddress host;
		
		public CheckServer(int modo)
		{
			this.operacao = modo;
		}
		
		public void run()
		{
			boolean flag_procura = false;
			
			try 
			{
				this.socket = new DatagramSocket(portaConnect);
				
				try 
				{
					this.socket.setBroadcast(true);
					this.socket.setSoTimeout(5000);
					
					byte[] buf = new byte[1];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					this.socket.receive(packet);
					
					/*if(packet.getData()[0] == -16)
						JOptionPane.showMessageDialog(null, "pacote autentico! " + packet.getAddress());
					else
						JOptionPane.showMessageDialog(null, "pacote recebido! " + packet.getAddress());*/
					
					this.host = packet.getAddress();
					flag_procura = true;
					
					if(!this.socket.isClosed())
						this.socket.close();
					
					verificandoBar.setVisible(false);	// nem precisa verificar, ctz que é cliente
					comecarLoading(this.operacao, this.host, portaConnect);	// começar o programa aqui
					
				} 
				catch (IOException e) 
				{
					if(e.getMessage().contains("Receive timed out"))	// nao recebeu nenhum sinal
					{
						if(!this.socket.isClosed())
							this.socket.close();					
						
						flag_procura = false;
						
						verificandoBar.setVisible(false);
						
					    if(this.operacao == 1)
					    {
					    	if(flag_procura)
					    		descricaoOperacao.setText("Erro: já existe um principal conectado!");
					    	else
					    		comecarLoading(this.operacao, this.host, portaConnect);	// começar o programa aqui
					    }
					    else
					    {
					    	if(flag_procura)
					    		comecarLoading(this.operacao, this.host, portaConnect);	// começar o programa aqui
					    	else
					    		descricaoOperacao.setText("Erro: nenhum principal conectado!");
					    }					
					}
					else
					{
						e.printStackTrace();
						verificandoBar.setVisible(false);
						descricaoOperacao.setText("Houve um erro e não foi possível verificar.");					
					}
				}				
			} 
			catch (SocketException e1) 
			{
				if(e1.getMessage().contains("Cannot bind"))	// já está ligado o servidor aqui.
				{
					flag_procura = true;
					verificandoBar.setVisible(false);
					
					if(this.operacao == 1)
					{
					   if(flag_procura)
						   descricaoOperacao.setText("Erro: já existe um principal conectado!");
					   else
					    comecarLoading(this.operacao, this.host, portaConnect);	// começar o programa aqui						 
					}
				    else
				    {
				    	if(flag_procura)
							try 
				    		{
								comecarLoading(this.operacao, InetAddress.getLocalHost(), portaConnect);
							} 
				    		catch (UnknownHostException e)
				    		{
				    			descricaoOperacao.setText("Erro: ao obter localhost!");
								e.printStackTrace();
								new PainelErro(e);
								System.exit(0);
							}
						else
				    		descricaoOperacao.setText("Erro: nenhum principal conectado!");
				    }					
				}
				else
				{
					e1.printStackTrace();
					verificandoBar.setVisible(false);
					descricaoOperacao.setText("Houve um erro e não foi possível verificar.");					
				}				
			}
		}
	}
}