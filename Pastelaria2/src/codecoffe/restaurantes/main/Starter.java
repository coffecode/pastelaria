package codecoffe.restaurantes.main;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
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
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.xml.stream.XMLStreamException;

import net.miginfocom.swing.MigLayout;
import codecoffe.restaurantes.interfaceGrafica.MenuPrincipal;
import codecoffe.restaurantes.interfaceGrafica.PainelDisconnect;
import codecoffe.restaurantes.interfaceGrafica.PainelErro;
import codecoffe.restaurantes.sockets.BroadcastServer;
import codecoffe.restaurantes.sockets.Client;
import codecoffe.restaurantes.sockets.Server;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.UtilCoffe;
import codecoffe.restaurantes.xml.LerStarterConfig;
import codecoffe.restaurantes.xml.SalvarStarterConfig;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.progressbar.WebProgressBar;
import com.alee.laf.text.WebTextField;
import com.alee.managers.language.LanguageManager;
import com.alee.utils.ThreadUtils;

public class Starter implements ActionListener
{
	private JFrame seleciona, splash;
	private JPanel inicio;
	private JTabbedPane selecionaPanel;
	private JProgressBar progressBar;
	private JLabel descricaoOperacao, statusProgress;
	private WebTextField campoIP;
	private WebButton bTerminal, bPrincipal;
	private WebProgressBar verificandoBar;
	private JCheckBox modoTouch;
	
	private static final int portaConnect = 27013;
	
	public Starter()
	{
		System.setProperty("java.net.preferIPv4Stack", "true");
		LanguageManager.DEFAULT = LanguageManager.PORTUGUESE;
		WebLookAndFeel.install();
		ToolTipManager.sharedInstance().setInitialDelay(500);
		ToolTipManager.sharedInstance().setDismissDelay(40000);
		
		seleciona = new JFrame("CodeCoffe " + UtilCoffe.VERSAO);
		seleciona.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		selecionaPanel = new JTabbedPane();
		selecionaPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JPanel iniciar = new JPanel(new MigLayout("align center"));
		
		bTerminal = new WebButton("Terminal");
		bTerminal.setRolloverShine(true);
		bTerminal.setHorizontalTextPosition(AbstractButton.CENTER);
		bTerminal.setVerticalTextPosition(AbstractButton.BOTTOM);
		bTerminal.setPreferredSize(new Dimension(100, 100));
		bTerminal.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/terminal.png")));
		bTerminal.addActionListener(this);
		iniciar.add(bTerminal, "gaptop 15, align center, split 2");
		
		bPrincipal = new WebButton("Principal");
		bPrincipal.setRolloverShine(true);
		bPrincipal.setPreferredSize(new Dimension(100, 100));
		bPrincipal.setHorizontalTextPosition(AbstractButton.CENTER);
		bPrincipal.setVerticalTextPosition(AbstractButton.BOTTOM);
		bPrincipal.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/principal.png")));
		bPrincipal.addActionListener(this);
		iniciar.add(bPrincipal, "gaptop 15, gapleft 20, align center, wrap");
		
		verificandoBar = new WebProgressBar();
		verificandoBar.setIndeterminate(true);
        verificandoBar.setStringPainted(true);
        verificandoBar.setString("Verificando conexões...");
        verificandoBar.setPreferredSize(new Dimension(230, 40));
        verificandoBar.setVisible(false);
        iniciar.add(verificandoBar, "gaptop 15, align center, wrap");
		
		descricaoOperacao = new JLabel("Escolha o modo de operação do programa.");
		descricaoOperacao.setFont(new Font("Verdana", Font.PLAIN, 10));
		descricaoOperacao.setPreferredSize(new Dimension(250, 50));
		iniciar.add(descricaoOperacao, "align center");		
		
		JPanel configuracoes = new JPanel(new MigLayout());
		
		modoTouch = new JCheckBox("Modo Touch Screen (Terminal)");
		modoTouch.setEnabled(false);
		configuracoes.add(modoTouch, "gaptop 10, wrap");
		
		configuracoes.add(new JLabel("<html>IP de Conexão (Terminal)</html>"), "gaptop 20, wrap");
		
		campoIP = new WebTextField("");
		campoIP.setInputPrompt("Automático");
		campoIP.setMargin(5, 5, 5, 5);
		campoIP.setPreferredSize(new Dimension(150, 30));
		
		configuracoes.add(campoIP, "split 2");
		
		JLabel help = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("imgs/help.png")));
		help.setToolTipText("<html>Digite o IP do computador que está<br>com o programa principal rodando.<br>"
				+ "O IP é mostrado no canto superior direito do programa.<br>"
				+ "Exemplo: 192.168.1.2<br>"
				+ "Deixe em branco para que o programa tente encontrar automaticamente.<br><br>"
				+ "Só altere essa configuração caso o programa não consiga encontrar<br>o IP automaticamente na rede.</html>");
		configuracoes.add(help, "wrap");
		
		selecionaPanel.addTab("Iniciar", iniciar);
		selecionaPanel.addTab("Configurações", configuracoes);
		seleciona.add(selecionaPanel);
		seleciona.setSize(350,300);
		seleciona.setLocationRelativeTo(null);
		seleciona.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/icone_programa.png")).getImage());
		seleciona.setResizable(false);
		seleciona.setVisible(true);
		
		try {
			LerStarterConfig configXML = new LerStarterConfig();
			List<String> configSalva = configXML.readConfig();
			
			if(configSalva.size() > 0)
			{
				String ipSalvo = configSalva.get(0);
				if(!UtilCoffe.vaziu(ipSalvo))
					if(!ipSalvo.equals("none"))
						campoIP.setText(ipSalvo);
			}
			
		} catch (FileNotFoundException e) {
			try {
				SalvarStarterConfig salvarXML = new SalvarStarterConfig();
				salvarXML.saveConfig("none");
			} catch (Exception e1) {
				e1.printStackTrace();
				new PainelErro(e1);
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
			new PainelErro(e);
		}
		
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
			try {
				SalvarStarterConfig salvarXML = new SalvarStarterConfig();
				if(UtilCoffe.vaziu(campoIP.getText()))
					salvarXML.saveConfig("none");
				else
					salvarXML.saveConfig(campoIP.getText());
			} catch (Exception e1) {
				e1.printStackTrace();
				new PainelErro(e1);
			}			
			
			if(UtilCoffe.vaziu(campoIP.getText()))
			{
				descricaoOperacao.setText("");
				verificandoBar.setString("Verificando conexões...");
				verificandoBar.setVisible(true);
				new CheckServer(2).start();				
			}
			else
			{
				verificandoBar.setString("Conectando em " + campoIP.getText());
				verificandoBar.setVisible(true);
				
				new Thread(new Runnable()
		        {
		            @Override
		            public void run()
		            {
		            	try {
							comecarLoading(UtilCoffe.CLIENT, InetAddress.getByName(campoIP.getText()), portaConnect);
						} catch (UnknownHostException e) {
							//e.printStackTrace();
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									descricaoOperacao.setText("Erro: IP Inválido.");
									verificandoBar.setVisible(false);	
								}
							});
						}
		            }
		        }).start();
			}
		}
		else if(e.getSource() == bPrincipal)
		{
			try {
				SalvarStarterConfig salvarXML = new SalvarStarterConfig();
				if(UtilCoffe.vaziu(campoIP.getText()))
					salvarXML.saveConfig("none");
				else
					salvarXML.saveConfig(campoIP.getText());
			} catch (Exception e1) {
				e1.printStackTrace();
				new PainelErro(e1);
			}
			
			descricaoOperacao.setText("");
			verificandoBar.setString("Iniciando principal...");
			verificandoBar.setVisible(true);
			new CheckServer(1).start();
		}
	}
	
	public void comecarLoading(int modo, InetAddress host, int porta)
	{
		if(modo == UtilCoffe.CLIENT)
		{
			Configuracao.INSTANCE.setModo(UtilCoffe.CLIENT);

			try {
				Client.getInstance().atualizaConexao(host, porta);
				new Thread(Client.getInstance()).start();
				seleciona.dispose();
			} catch (IOException e) {
				if(e.getMessage().toLowerCase().contains("refused") || e.getMessage().toLowerCase().contains("timed out"))
				{
					descricaoOperacao.setText("Não foi possível conectar em: " + host.getHostAddress());
					verificandoBar.setVisible(false);
				}
				else
				{
					e.printStackTrace();
					new PainelErro(e);
				}
			}		
		}
		else
		{
			seleciona.dispose();
			Configuracao.INSTANCE.setModo(UtilCoffe.SERVER);		

			Server.getInstance().atualizaConexao(porta);
			new Thread(Server.getInstance()).start();

			BroadcastServer serverUDP = new BroadcastServer(porta);
			new Thread(serverUDP).start();

			MenuPrincipal.getInstance();
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