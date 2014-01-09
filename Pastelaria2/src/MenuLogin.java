import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class MenuLogin extends JFrame implements ActionListener
{
	private JLabel LabelUsername, LabelPassword, Creditos;
	private JTextField CampoUsername;
	private JPasswordField CampoPassword;
	private JButton Confere;
	static public String logado;
	
	public MenuLogin()
	{		
		setTitle("Login do Sistema");
		JPanel login = new JPanel();
		login.setLayout(null);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		LabelUsername = new JLabel("Usu·rio:");
		LabelUsername.setFont(new Font("Verdana", Font.BOLD, 12));
		LabelUsername.setBounds(15,20,70,30); // Coluna, Linha, Largura, Altura!
		login.add(LabelUsername);
		
		CampoUsername = new JTextField();
		CampoUsername.setHorizontalAlignment(SwingConstants.CENTER);
		CampoUsername.setBounds(95,20,120,30);
		login.add(CampoUsername);
		
		LabelPassword = new JLabel("Senha:");
		LabelPassword.setFont(new Font("Verdana", Font.BOLD, 12));
		LabelPassword.setBounds(15,60,70,30); // Coluna, Linha, Largura, Altura
		login.add(LabelPassword);
		
		CampoPassword = new JPasswordField();
		CampoPassword.setHorizontalAlignment(SwingConstants.CENTER);
		CampoPassword.setBounds(95,60,120,30);
		login.add(CampoPassword);
		
		Confere = new JButton("Entrar ");
		
		Confere.setIcon(new ImageIcon(getClass().getResource("imgs/login.png")));
		Confere.setFont(new Font("Helvetica", Font.BOLD, 16));
		Confere.setHorizontalTextPosition(AbstractButton.LEFT);			
		Confere.setBounds(95,110,120,40);
		login.add(Confere);
		
		Creditos = new JLabel("Desenvolvido por CodeCoffe - 2013");
		Creditos.setFont(new Font("Verdana", Font.PLAIN, 10));
		Creditos.setBounds(15, 165, 200, 15);
		login.add(Creditos);
		
		add(login);
		
		setSize(260,220);							// Largura, Altura
		setLocationRelativeTo(null);				// Abre no centro da tela
		
		Confere.addActionListener(this);
		getRootPane().setDefaultButton(Confere);
		setIconImage(new ImageIcon(getClass().getResource("imgs/icone_programa.png")).getImage());
		setResizable(false);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == Confere) 	// Ve se o bot√£o apertado √© aquele que queremos.
		{
			String formatacao;
			Query teste = new Query();
			formatacao = "SELECT password, level, nome FROM funcionarios WHERE username = '" + CampoUsername.getText() + "';";
			
			teste.executaQuery(formatacao);
			if(teste.next())
			{
				if(teste.getString("password").equals(CampoPassword.getText()))
				{
					dispose();		// Fecha o JFrame
					
					logado = teste.getString("nome");

					PainelStatus.setNome(teste.getString("nome"));
					PainelMenu.setarLevel(teste.getInt("level"));
					MenuPrincipal.setarVisible(true);

					teste.fechaConexao();
					DiarioLog.add("Fez login no sistema.", 8);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Senha incorreta!");
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Usu·rio n„o encontrado!");
			}
		}
	}	
}