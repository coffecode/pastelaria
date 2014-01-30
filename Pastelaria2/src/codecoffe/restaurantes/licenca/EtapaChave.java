package codecoffe.restaurantes.licenca;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import com.alee.extended.image.WebImage;
import com.alee.laf.text.WebTextField;
import net.miginfocom.swing.MigLayout;

public class EtapaChave extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WebTextField campoChave, campoComputador;
	private JLabel respostaServer;
	private JComboBox<String> ondeInstalar;
	
	public EtapaChave()
	{
		setLayout(new MigLayout());
		add(new JLabel("<html>Método de Ativação por Chave.<br><br>"
				+ "É preciso estar conectado na internet e possuir a chave (recebida na compra desse programa). "
				+ "Caso contrário, escolha outro método de ativação.</html>"), "wrap");
		
		String[] ondeString = {"Nesse computador", "Outro computador" };
		ondeInstalar = new JComboBox<String>(ondeString);
		ondeInstalar.setPreferredSize(new Dimension(140, 30));
		
		campoComputador = new WebTextField();
		campoComputador.setPreferredSize(new Dimension(180, 35));
		campoComputador.setHorizontalAlignment(SwingConstants.CENTER);
		campoComputador.setInputPrompt("Código do computador");
		campoComputador.setEnabled(false);
		
		campoChave = new WebTextField();
		campoChave.setPreferredHeight(45);
		campoChave.setHorizontalAlignment(SwingConstants.CENTER);
		campoChave.setFont(new Font("Helvetica", Font.BOLD, 16));
		campoChave.setInputPrompt("Digite a chave de ativação e aperte ENTER");
		campoChave.setTrailingComponent(new WebImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/chave.png"))));
		campoChave.addKeyListener(new KeyAdapter()
        {
        	public void keyPressed(KeyEvent e)
        	{
        		int code = e.getKeyCode();
        		if(code==KeyEvent.VK_ENTER)
        		{
        			// ação.
        		}
            }
        });
		
		respostaServer = new JLabel("");
		respostaServer.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/loadcep.gif")));
		
		add(new JLabel("Onde ativar"), "wrap, gaptop 20, gapleft 30, gapright 30, align center");
		add(ondeInstalar, "gaptop 5, align center, split 2");
		add(campoComputador, "wrap");
		add(new JLabel("Chave de Ativação"), "wrap, gaptop 30, gapleft 30, gapright 30, align center");
		add(campoChave, "wrap, grow, gaptop 5, gapleft 30, gapright 30");
		add(respostaServer, "wrap, gaptop 15, align center");
		
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			
			}
			campoComputador.setText(sb.toString());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e){
			e.printStackTrace();
		}		
	}
}

