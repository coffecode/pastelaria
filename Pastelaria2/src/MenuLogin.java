import java.awt.*;

//import com.jtattoo.plaf.smart.SmartLookAndFeel;
import javax.swing.*;

//import javax.swing.UIManager.*;
import java.awt.event.*;

public class MenuLogin extends JFrame implements ActionListener
{
	private JLabel LabelUsername, LabelPassword, Creditos;
	private JTextField CampoUsername;
	private JPasswordField CampoPassword;
	private JButton Confere;
	private JPanel Painel;
	
	static public String logado = "";
	
	public MenuLogin()
	{
		
		/*try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		setTitle("Login");
		JPanel login = new JPanel();
		login.setLayout(null);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		LabelUsername = new JLabel("Usu·rio:");
		LabelUsername.setBounds(15,20,70,30); // Coluna, Linha, Largura, Altura!
		login.add(LabelUsername);
		
		CampoUsername = new JTextField();
		CampoUsername.setBounds(95,20,120,30);
		login.add(CampoUsername);
		
		LabelPassword = new JLabel("Senha:");
		LabelPassword.setBounds(15,60,70,30); // Coluna, Linha, Largura, Altura
		login.add(LabelPassword);
		
		CampoPassword = new JPasswordField();
		CampoPassword.setBounds(95,60,120,30);
		login.add(CampoPassword);
		
		Confere = new JButton("Entrar");
		Confere.setBounds(100,110,85,40);
		login.add(Confere);
		
		Creditos = new JLabel("Desenvolvido por CodeCoffe - 2013");
		Creditos.setFont(new Font("Verdana", Font.PLAIN, 10));
		Creditos.setBounds(15, 165, 200, 15);
		login.add(Creditos);
		
		add(login);
		
		setSize(280,220);							// Largura, Altura
		setLocationRelativeTo(null);				// Abre no centro da tela
		
		Confere.addActionListener(this);
		getRootPane().setDefaultButton(Confere);	// Ao apertar ENTER acione o bot√£o
		setResizable(false);
	}
	
	public static void main(String[] args)
	{
		MenuLogin login = new MenuLogin();
		login.setVisible(true);					// Mostrando o Frame
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
					
					MenuPrincipal principal = new MenuPrincipal(teste.getInt("level"), teste.getString("nome"));
					teste.fechaConexao();
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