import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

public class Starter 
{
	private static JFrame splash;
	private JPanel inicio;
	private static JProgressBar progressBar;
	private static JLabel statusProgress;
	
	public Starter()
	{		
		splash = new JFrame();
		inicio = new JPanel();
		inicio.setLayout(null);
		
		UIManager.put("ProgressBar.foreground", new Color(100, 100, 100));
		
		JLabel splashImage = new JLabel(new ImageIcon(getClass().getResource("imgs/splashscreen.png")));
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
		splash.setIconImage(new ImageIcon(getClass().getResource("imgs/icone_programa.png")).getImage());
		splash.setResizable(false);
		splash.setVisible(true);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				    Process process = Runtime.getRuntime().exec(command);
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
							// TODO Auto-generated catch block
							e.printStackTrace();
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}			    
				} 
				catch (IOException e)
				{
				    e.printStackTrace();
				}					
			}
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		progressBar.setValue(20);
		statusProgress.setText("Carregando programa...");
		
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		MenuPrincipal principal = new MenuPrincipal();
	}
	
	static public void setarProgresso(String texto, int valor)
	{
		progressBar.setValue(valor);
		statusProgress.setText(texto);
	}
	
	static public void loadingFinalizado()
	{
		splash.dispose();
	}	
	
	public static void main(String[] args) {
		new Starter();
	}
}
