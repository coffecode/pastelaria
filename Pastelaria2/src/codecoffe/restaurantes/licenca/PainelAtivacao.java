package codecoffe.restaurantes.licenca;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.alee.laf.button.WebButton;

public class PainelAtivacao 
{
	private JFrame frameAtivacao;
	private JPanel painelAtivacao, painelMeio, painelEtapas, painelImagem, painelFooter;
	private Etapa1 painelEtapa1;
	private EtapaChave painelEtapaChave;
	private EtapaCodigo painelEtapaCodigo;
	private JLabel bInicio;
	
	public PainelAtivacao()
	{
		frameAtivacao = new JFrame("CodeCoffe - Ativação de Licença");
		frameAtivacao.setSize(650, 420);
		frameAtivacao.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameAtivacao.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("imgs/icone_programa.png")).getImage());
		frameAtivacao.setResizable(false);
		frameAtivacao.setLocationRelativeTo(null);
		
		painelAtivacao = new JPanel();
		painelAtivacao.setLayout(new BoxLayout(painelAtivacao, BoxLayout.X_AXIS));
		painelImagem = new JPanel(new BorderLayout());
		painelImagem.setPreferredSize(new Dimension(186, 420));
		
		painelMeio = new JPanel();
		painelMeio.setLayout(new BoxLayout(painelMeio, BoxLayout.Y_AXIS));
		painelMeio.setPreferredSize(new Dimension(464, 420));
		
		painelEtapas = new JPanel(new CardLayout());
		painelEtapas.setPreferredSize(new Dimension(464, 390));
		
		painelEtapa1 = new Etapa1();
		painelEtapas.add(painelEtapa1, "Etapa 1");
		
		painelEtapaChave = new EtapaChave();
		painelEtapas.add(painelEtapaChave, "Etapa Chave");
		
		painelEtapaCodigo = new EtapaCodigo();
		painelEtapas.add(painelEtapaCodigo, "Etapa Codigo");
		
		painelMeio.add(painelEtapas);
		
		MouseListener lis = new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				CardLayout cardLayout = (CardLayout) painelEtapas.getLayout();
				cardLayout.show(painelEtapas, "Etapa 1");
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		};
		
		painelFooter = new JPanel(new BorderLayout());		
		bInicio = new JLabel("Início");
		bInicio.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/icon_food.png")));
		bInicio.setBorder(new EmptyBorder(6, 6, 6, 6));
		bInicio.addMouseListener(lis);
		painelFooter.add(bInicio, BorderLayout.EAST);
		painelMeio.add(painelFooter);
		
		painelImagem.add(new JLabel(new ImageIcon(getClass().getClassLoader().getResource("imgs/ativacao.png"))));
		painelAtivacao.add(painelImagem);
		painelAtivacao.add(painelMeio);
		frameAtivacao.add(painelAtivacao);
		
		frameAtivacao.setVisible(true);
	}
	
	public static void main(String[] args) {
		new PainelAtivacao();
	}
	
	private class Etapa1 extends JPanel implements ActionListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private WebButton bCodigo, bLicenca;
		
		public Etapa1()
		{
			setLayout(new MigLayout());
			add(new JLabel("<html>Bem-vindo ao processo de ativação da licença.<br><br>"
					+ "Esse processo é iniciado sempre que o programa não encontrar uma licença de uso para esse computador. "
					+ "Recomenda-se ao cliente que leia o manual rápido de instalação do programa.<br><br>"
					+ "Agradecemos muito pela preferência, em casos de dificuldade por favor entre em contato conosco.<br><br><br>"
					+ "<b>Escolha o método de ativação</b>:</html>"), "wrap");
			
			bCodigo = new WebButton("Chave de Ativação");
			bCodigo.setPreferredHeight(50);
			bCodigo.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/chave.png")));		
			bCodigo.setToolTipText("É necessário conexão com a internet.");
			bCodigo.setRolloverShine(true);
			bCodigo.addActionListener(this);
			
			bLicenca = new WebButton("Código de Licença");
			bLicenca.setPreferredHeight(50);
			bLicenca.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/codigo.png")));
			bLicenca.setToolTipText("Não é necessário conexão com a internet.");
			bLicenca.setRolloverShine(true);
			bLicenca.addActionListener(this);
			
			add(bCodigo, "wrap, grow, gaptop 30");
			add(bLicenca, "grow, gaptop 20");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == bCodigo)
			{
				CardLayout cardLayout = (CardLayout) painelEtapas.getLayout();
				cardLayout.show(painelEtapas, "Etapa Chave");				
			}
			else if(e.getSource() == bLicenca)
			{
				CardLayout cardLayout = (CardLayout) painelEtapas.getLayout();
				cardLayout.show(painelEtapas, "Etapa Codigo");					
			}
		}
	}	
}
