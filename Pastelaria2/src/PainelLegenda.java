import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

public class PainelLegenda extends JPanel
{
	private static JLabel leg;
	
	PainelLegenda()
	{
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Legenda"));
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		setMaximumSize(new Dimension(800, 45));
		setMinimumSize(new Dimension(800, 45));
		
		leg = new JLabel("Desenvolvido por CodeCoffe (C) - 2013");
		leg.setFont(new Font("sans-serif", Font.BOLD, 12));
		
		add(leg);
	}
	
	static public void AtualizaLegenda(String texto)
	{
		leg.setText(texto);
	}
}