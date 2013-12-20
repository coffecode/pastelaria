import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.event.*;

public class PainelVenda extends JPanel implements MouseListener
{
	private int qntdmesas;
	private JPanel mesasPainel;
	private JButton[] mesas;
	
	PainelVenda()
	{
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Venda na Mesa"));
		setLayout(new FlowLayout(FlowLayout.CENTER, 6, 5));
		setMinimumSize(new Dimension(800, 500));		// Horizontal , Vertical
		setMaximumSize(new Dimension(800, 500));
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e)	{

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}

