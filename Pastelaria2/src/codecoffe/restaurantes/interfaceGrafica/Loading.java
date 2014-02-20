package codecoffe.restaurantes.interfaceGrafica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.alee.utils.ThreadUtils;

import codecoffe.restaurantes.utilitarios.UtilCoffe;

public class Loading {
	private JFrame splash;
	private JPanel inicio;
	private JProgressBar progressBar;
	private JLabel statusProgress;
	private int modo;
	
	public Loading(int m)
	{
		boolean fast_mod = true;
		
		if(fast_mod)
		{
			PainelPrincipal.getInstance();
			
			if(m == UtilCoffe.CLIENT)
			{
				PainelVendaRapida.getInstance();
				PainelMenu.getInstance();
				PainelMesas.getInstance();
				PainelLegenda.getInstance();
				PainelStatus.getInstance();
				PainelVendaMesa.getInstance();
				PainelClientes.getInstance();
				PainelCozinha.getInstance();
			}
			else
			{
				PainelVendaRapida.getInstance();
				PainelMenu.getInstance();
				PainelMesas.getInstance();
				PainelLegenda.getInstance();
				PainelStatus.getInstance();
				PainelProdutos.getInstance();
				PainelFuncionarios.getInstance();
				PainelVendas.getInstance();
				PainelVendaMesa.getInstance();
				PainelClientes.getInstance();
				PainelCozinha.getInstance();
				PainelConfiguracao.getInstance();
			}			
			
			PainelPrincipal.getInstance().finalizarLoading();
		}
		else
		{
			modo = m;
			splash = new JFrame();
			inicio = new JPanel();
			inicio.setLayout(null);
			
			JLabel splashImage = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("imgs/splashscreen.png")));
			splashImage.setBounds(1, 1, 445, 276); // Coluna, Linha, Largura, Altura!
			
			progressBar = new JProgressBar(0, 100);
	    	progressBar.setValue(0);
	    	progressBar.setStringPainted(true);
	    	progressBar.setBounds(1, 220, 443, 55);
	    	
	    	statusProgress = new JLabel("Preparando inicialização...");
	    	statusProgress.setBounds(10, 184, 443, 55);
	    	
	    	inicio.add(statusProgress);
	    	inicio.add(progressBar);
	    	inicio.add(splashImage);
			splash.add(inicio);
			
			splash.setUndecorated(true);
			splash.setSize(445,276);
			splash.setLocationRelativeTo(null);
			splash.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/icone_programa.png")).getImage());
			splash.setResizable(false);
			splash.setVisible(true);
			
			if(modo == UtilCoffe.SERVER)
				verificarMYSQL();
			else
			{
				iniciarLoading();
			}	
		}
	}
	
	public void verificarMYSQL()
	{
		new Thread(new Runnable()
        {
            @Override
            public void run()
            {
            	ThreadUtils.sleepSafely(500);
            	progressBar.setValue(1);
            	statusProgress.setText("Verificando banco de dados...");

        		try {
            		String line;
            		String pidInfo ="";
        			Process p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
        			BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
        			
        			while ((line = input.readLine()) != null) {
        			    pidInfo+=line; 
        			}
        			input.close();
        			
        			if(pidInfo.contains("mysqld"))
        			{
        				statusProgress.setText("Banco de dados ligado.");
        			}
        			else
        			{
        				statusProgress.setText("Iniciando banco de dados...");
        				boolean ligando = true;
        				
        				while(ligando)
        				{
        					String command = System.getProperty("user.dir") + "\\mysql\\bin\\mysqld.exe";
        					Runtime.getRuntime().exec(command);
        					
							Process p2 = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
							BufferedReader input2 =  new BufferedReader(new InputStreamReader(p2.getInputStream()));
							
							while ((line = input2.readLine()) != null) {
							    pidInfo+=line; 
							}
							input2.close();
							
							if(pidInfo.contains("mysqld"))
							{
								progressBar.setValue(10);
								statusProgress.setText("Banco de dados iniciado.");
								ligando = false;
							}
							else
							{
								statusProgress.setText("Não foi possível ligar o banco de dados, tentando novamente ...");
								ThreadUtils.sleepSafely(1000);
							}
        				}
        			}
        			iniciarLoading();
        			
        		} catch (IOException e) {
        			e.printStackTrace();
        			new PainelErro(e);
        			System.exit(0);
        		}
            }
        }).start();
	}
	
	public void iniciarLoading()
	{
		new Thread(new Runnable()
        {
            @Override
            public void run()
            {
            	ThreadUtils.sleepSafely(400);
            	
            	for(int i = 1; i < 101; i++)
            	{
            		ThreadUtils.sleepSafely(50);
            		
            		if(i == 1)
            		{
            			new Thread(new Runnable()
            	        {
            	            @Override
            	            public void run()
            	            {            	            	
            	            	statusProgress.setText("Carregando menu principal...");
            	            	PainelPrincipal.getInstance();
            	            }
            	        }).start();
            		}
            		else if(i == 10)
            		{
            			new Thread(new Runnable()
            	        {
            	            @Override
            	            public void run()
            	            {
            	            	statusProgress.setText("Carregando venda rápida...");
            	            	PainelVendaRapida.getInstance();
            	            }
            	        }).start();
            		}
            		else if(i == 20)
            		{
            			new Thread(new Runnable()
            	        {
            	            @Override
            	            public void run()
            	            {
            	            	statusProgress.setText("Carregando painel mesas...");
            	            	PainelMenu.getInstance();
            	            }
            	        }).start();
            		}
            		else if(i == 30)
            		{
            			new Thread(new Runnable()
            	        {
            	            @Override
            	            public void run()
            	            {
            	            	statusProgress.setText("Carregando painel footer...");
            	            	PainelLegenda.getInstance();
            	            }
            	        }).start();
            		}
            		else if(i == 35)
            		{
            			new Thread(new Runnable()
            	        {
            	            @Override
            	            public void run()
            	            {
            	            	statusProgress.setText("Carregando painel status...");
            	            	PainelStatus.getInstance();
            	            }
            	        }).start();
            			
            			if(modo == UtilCoffe.CLIENT)
            				i = 65;
            		}
            		else if(i == 40 && modo == UtilCoffe.SERVER)
            		{
            			new Thread(new Runnable()
            	        {
            	            @Override
            	            public void run()
            	            {
            	            	statusProgress.setText("Carregando produtos...");
            	            	PainelProdutos.getInstance();
            	            }
            	        }).start();
            		}
            		else if(i == 50 && modo == UtilCoffe.SERVER)
            		{
            			new Thread(new Runnable()
            	        {
            	            @Override
            	            public void run()
            	            {
            	            	statusProgress.setText("Carregando funcionários...");
            	            	PainelFuncionarios.getInstance();
            	            }
            	        }).start();
            		}
            		else if(i == 60 && modo == UtilCoffe.SERVER)
            		{
            			new Thread(new Runnable()
            	        {
            	            @Override
            	            public void run()
            	            {
            	            	statusProgress.setText("Carregando vendas...");
            	            	PainelVendas.getInstance();
            	            }
            	        }).start();
            		}
            		else if(i == 70)
            		{
            			new Thread(new Runnable()
            	        {
            	            @Override
            	            public void run()
            	            {
            	            	statusProgress.setText("Carregando venda mesas...");
            	            	PainelVendas.getInstance();
            	            }
            	        }).start();
            		}
            		else if(i == 80)
            		{
            			new Thread(new Runnable()
            	        {
            	            @Override
            	            public void run()
            	            {
            	            	statusProgress.setText("Carregando clientes...");
            	            	PainelClientes.getInstance();
            	            }
            	        }).start();
            		}
            		else if(i == 90)
            		{
            			new Thread(new Runnable()
            	        {
            	            @Override
            	            public void run()
            	            {
            	            	statusProgress.setText("Carregando cozinha...");
            	            	PainelCozinha.getInstance();
            	            }
            	        }).start();
            		}
            		else if(i == 95 && modo == UtilCoffe.SERVER)
            		{
            			new Thread(new Runnable()
            	        {
            	            @Override
            	            public void run()
            	            {
            	            	statusProgress.setText("Carregando outros paineis...");
            	            	PainelConfiguracao.getInstance();
            	            }
            	        }).start();
            		}
            		progressBar.setValue(i);
            	}
            	
            	ThreadUtils.sleepSafely(400);
        		PainelPrincipal.getInstance().finalizarLoading();
        		splash.dispose();
        		splash = null;
        		inicio = null;
        		progressBar = null;
        		statusProgress = null;
            }
        }).start();
	}
}