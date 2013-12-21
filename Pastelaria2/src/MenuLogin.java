import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MenuLogin extends JFrame implements ActionListener
{
	private JLabel LabelUsername, LabelPassword, Creditos;
	private JTextField CampoUsername;
	private JPasswordField CampoPassword;
	private JButton Confere;
	private JPanel Painel;
	
	public MenuLogin()
	{
		setTitle("Faça o seu Login");
		JPanel login = new JPanel();
		login.setLayout(null);
		
		LabelUsername = new JLabel("Usuário:");
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
		
		Creditos = new JLabel("Desenvolvido por CodeCoffe (C) - 2013");
		Creditos.setFont(new Font("Verdana", Font.PLAIN, 10));
		Creditos.setBounds(15, 170, 200, 15);
		login.add(Creditos);
		
		add(login);
		
		setSize(280,220);							// Largura, Altura
		setLocationRelativeTo(null);				// Abre no centro da tela
		
		Confere.addActionListener(this);
		getRootPane().setDefaultButton(Confere);	// Ao apertar ENTER acione o botão
	}
	
	public static void main(String[] args)
	{
		MenuLogin Principal = new MenuLogin();
		Principal.setVisible(true);					// Mostrando o Frame
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == Confere) 	// Ve se o botão apertado é aquele que queremos.
		{
			if(CampoUsername.getText().equals(CampoPassword.getText()))
			{					
				Query teste = new Query();
				teste.executaQuery("SELECT VERSION()");
				if(teste.next())
				{
					JOptionPane.showMessageDialog(null, teste.getString(1));
				}
				teste.fechaConexao();
			}
			else
			{
				dispose();		// Fecha o JFrame
			}
		}
	}	
}