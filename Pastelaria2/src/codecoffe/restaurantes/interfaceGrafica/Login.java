package codecoffe.restaurantes.interfaceGrafica;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.sockets.CacheAutentica;
import codecoffe.restaurantes.sockets.Client;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.DiarioLog;
import codecoffe.restaurantes.utilitarios.Usuario;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyManager;

public class Login extends WebDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static JLabel LabelUsername, LabelPassword, Creditos, logo;
	static JTextField CampoUsername, CampoPassword;
	static JButton Confere;
	public Login()
	{
		setIconImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/icone_programa.png")).getImage());
		setDefaultCloseOperation(WebDialog.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{	
				if(Configuracao.getModo() > 1)
				{
					Client.enviarObjeto("ADEUS");
					Client.disconnect();						
				}
				
				System.exit(0);
			}
		});				
		
		setResizable(false);
		setTitle("Login");
		setPreferredSize(new Dimension(470, 230));

		WebPanel login = new WebPanel();
		login.setLayout(null);
		login.setMargin(15, 30, 15, 30);
		login.setOpaque(false);
		
		LabelUsername = new JLabel("Usuário:");
		LabelUsername.setFont(new Font("Verdana", Font.BOLD, 12));
		LabelUsername.setBounds(250,40,70,30); // Coluna, Linha, Largura, Altura!
		login.add(LabelUsername);
		
		CampoUsername = new JTextField();
		CampoUsername.setHorizontalAlignment(SwingConstants.CENTER);
		CampoUsername.setBounds(330,40,120,30);
		login.add(CampoUsername);
		
		LabelPassword = new JLabel("Senha:");
		LabelPassword.setFont(new Font("Verdana", Font.BOLD, 12));
		LabelPassword.setBounds(250,80,70,30); // Coluna, Linha, Largura, Altura
		login.add(LabelPassword);
		
		CampoPassword = new JPasswordField();
		CampoPassword.setHorizontalAlignment(SwingConstants.CENTER);
		CampoPassword.setBounds(330,80,120,30);
		login.add(CampoPassword);
		
		Confere = new JButton("Entrar ");
		
		Confere.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/login.png")));
		Confere.setFont(new Font("Helvetica", Font.BOLD, 16));
		Confere.setHorizontalTextPosition(AbstractButton.LEFT);			
		Confere.setBounds(330,130,120,40);
		login.add(Confere);
		
		Creditos = new JLabel(UtilCoffe.VERSAO);
		Creditos.setFont(new Font("Verdana", Font.PLAIN, 10));
		Creditos.setBounds(430, 180, 200, 15);
		login.add(Creditos);
		
		logo = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("imgs/logo_login.png")));
		logo.setBounds(5,5,224,189);
		login.add(logo);			
		
		add(login);		

		ActionListener listener = new ActionListener ()
		{
			@Override
			public void actionPerformed ( ActionEvent e )
			{
				if(e.getSource() == Confere)
				{
					if(Configuracao.getModo() == UtilCoffe.SERVER)
						autentica(CampoUsername.getText(), CampoPassword.getText());
					else
						Client.enviarObjeto(new CacheAutentica(CampoUsername.getText(), CampoPassword.getText()));
				}
            }
		};
		
		Confere.addActionListener(listener);
		HotkeyManager.registerHotkey (this, Confere, Hotkey.ENTER);			
	}
	
	public void autentica(String username, String password)
	{
		String formatacao;
		Query teste = new Query();
		formatacao = "SELECT password, level, nome FROM funcionarios WHERE username = '" + username + "';";
		
		try {
			teste.executaQuery(formatacao);
			if(teste.next())
			{
				if(teste.getString("password").equals(password))
				{
					CampoUsername.setText("");
					CampoPassword.setText("");
					Usuario.setNome(teste.getString("nome"));
					Usuario.setLevel(teste.getInt("level"));
					PainelStatus.setNome(Usuario.getNome());
					MenuPrincipal.setarVisible(true);

					teste.fechaConexao();
					DiarioLog.add(Usuario.getNome(), "Fez login no sistema.", 8);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Senha incorreta!");
					teste.fechaConexao();
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Usuário não encontrado!");
				teste.fechaConexao();
			}				
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			new PainelErro(e);
			System.exit(0);
		}	
	}
	
	public static void autentica(CacheAutentica ca)
	{		
		switch(ca.header)
		{
			case 1:
			{
				CampoUsername.setText("");
				CampoPassword.setText("");				
				Usuario.setNome(ca.nome);
				Usuario.setLevel(ca.level);
				PainelStatus.setNome(Usuario.getNome());
				MenuPrincipal.setarVisible(true);				
				break;
			}
			case 2:
			{
				JOptionPane.showMessageDialog(null, "Usuário não encontrado!");
				break;
			}
			case 3:
			{
				JOptionPane.showMessageDialog(null, "Senha incorreta!");
				break;
			}
			default:
			{
				JOptionPane.showMessageDialog(null, "Não pode entrar com o mesmo login do Principal!");
			}
		}
	}
}
