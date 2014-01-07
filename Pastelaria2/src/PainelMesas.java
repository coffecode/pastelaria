import java.awt.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.event.*;

public class PainelMesas extends JPanel implements MouseListener
{
	private int qntdmesas;
	private JPanel mesasPainel;
	private JButton[] mesas;
	
	PainelMesas(int qntd)
	{
		this.qntdmesas = qntd;
		mesasPainel = new JPanel();
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Mesas"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setMinimumSize(new Dimension(800, 430));
		setMaximumSize(new Dimension(800, 430));		
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		mesasPainel.setLayout(new GridBagLayout());
		mesasPainel.setMinimumSize(new Dimension(800, 390));
		mesasPainel.setMaximumSize(new Dimension(800, 390));
		
		int colunas = 0;
		int linhas  = 0;
		int quebra_linhas = 0;
		
		mesas = new JButton[this.qntdmesas];
		ImageIcon[] iconeMesas = new ImageIcon[this.qntdmesas];
		for(int i = 0; i < this.qntdmesas; i++)
		{
			String nomeMesa = "Mesa ";
			mesas[i] = new JButton(nomeMesa + (i+1));
			mesas[i].setPreferredSize(new Dimension(100, 52));
			iconeMesas[i] = new ImageIcon("imgs/mesa.png");
			mesas[i].setIcon(iconeMesas[i]);
			mesas[i].setHorizontalTextPosition(AbstractButton.CENTER);
			mesas[i].setVerticalTextPosition(AbstractButton.BOTTOM);
			mesas[i].addMouseListener(this);

			Query pega = new Query();
			pega.executaQuery("SELECT * FROM produtosMesa WHERE mesa_id = "+ (i+1) +";");
			System.out.print(""+(i+1)+"==");
			if(pega.next()){
				//MESA SENDO USADA
				System.out.print("USADA");
			}else{
				//MESA FECHADA
				System.out.print("LIVRE");
			}
			
			
			gbc.gridx = colunas;
			gbc.gridy = linhas;
			gbc.insets = new Insets(10,10,0,0);  //top padding
			
			mesasPainel.add(mesas[i], gbc);
			
			quebra_linhas++;
			colunas++;
			
			if(quebra_linhas >= 6)
			{
				colunas = 0;
				linhas++;
				quebra_linhas = 0;
			}
		}
		
		JScrollPane scroll = new JScrollPane(mesasPainel);
		scroll.setMinimumSize(new Dimension(800, 390));
		scroll.setMaximumSize(new Dimension(800, 390));
		scroll.setBorder(BorderFactory.createEmptyBorder());
		add(scroll);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		for(int i = 0; i < this.qntdmesas; i++)
		{
			if(e.getSource() == mesas[i])
			{
				MenuPrincipal.AbrirPrincipal(i+5, true);
				break;
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		for(int i = 0; i < this.qntdmesas; i++)
		{
			if(e.getSource() == mesas[i])
			{
				PainelLegenda.AtualizaLegenda("Mesa " + (i+1));
				break;
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		PainelLegenda.AtualizaLegenda("Desenvolvido por CodeCoffe (C) - 2013");
	}
}
