package codecoffe.restaurantes.interfaceGrafica;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.BevelBorder;

import codecoffe.restaurantes.primitivas.Pedido;
import codecoffe.restaurantes.utilitarios.Bartender;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.extended.label.HotkeyPainter;
import com.alee.laf.StyleConstants;
import com.alee.laf.panel.WebPanel;

public class PainelPedido extends WebPanel
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel labelLocal, labelQuantidade, labelProduto, labelTempo;
	private JPopupMenu popup;
	private JStatusBar statusBar;
	private Pedido pedidoAtt;
	
	public PainelPedido(Pedido p)
	{
		pedidoAtt = p;
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(120, 45));
		setUndecorated(false);
		setMargin(4);
		setRound(StyleConstants.largeRound);
		HotkeyPainter pintura = new HotkeyPainter();
		setPainter(pintura);
		statusBar = new JStatusBar();
		refreshStatus();
		
		if(p.getLocal() > 0)
		{
			labelLocal = new JLabel(" Mesa " + p.getLocal());
			labelLocal.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/mesa_mini_mini.png")));
			labelLocal.setPreferredSize(new Dimension(70, 30));
		}
		else
		{
			labelLocal = new JLabel(" Balcão");
			labelLocal.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/mesa_mini_mini.png")));
			labelLocal.setPreferredSize(new Dimension(70, 30));			
		}
		
		labelQuantidade = new JLabel(" " + p.getProduto().getQuantidade());
		labelQuantidade.setIcon(new ImageIcon(getClass().getClassLoader().getResource("imgs/icon_food.png")));
		labelQuantidade.setPreferredSize(new Dimension(38, 30));
		
		String formataNome = " ";
		formataNome += p.getProduto().getNome();
		
		if(p.getProduto().getTotalAdicionais() > 0)
			formataNome += " com " + p.getProduto().getAllAdicionais();
		
		if(!UtilCoffe.vaziu(p.getObs()))
			formataNome += " (" + p.getObs() + ")";
		
		labelProduto = new JLabel(formataNome + ".");
		labelProduto.setPreferredSize(new Dimension(400, 30));
		
		labelTempo = new JLabel("0 segundos atrás.");
		labelTempo.setPreferredSize(new Dimension(105, 30));
		
		statusBar.addLeftComponent(labelLocal);
		statusBar.addLeftComponent(labelQuantidade);
		statusBar.addLeftComponent(labelProduto);
		statusBar.addRightComponent(labelTempo);
		add(statusBar, BorderLayout.CENTER);
		
		popup = new JPopupMenu();
		ActionListener menuListener = new ActionListener() 
		{
			public void actionPerformed(ActionEvent event) {
				
				if(event.getActionCommand().equals("Status: Normal"))
				{
					pedidoAtt.setStatus(UtilCoffe.PEDIDO_NORMAL);
					pedidoAtt.setHeader(UtilCoffe.PEDIDO_STATUS);
					Bartender.INSTANCE.enviarPedido(pedidoAtt);
				}
				else if(event.getActionCommand().equals("Status: Fazendo"))
				{
					pedidoAtt.setStatus(UtilCoffe.PEDIDO_FAZENDO);
					pedidoAtt.setHeader(UtilCoffe.PEDIDO_STATUS);
					Bartender.INSTANCE.enviarPedido(pedidoAtt);					
				}
				else
				{
					pedidoAtt.setStatus(UtilCoffe.PEDIDO_REMOVER);
					pedidoAtt.setHeader(UtilCoffe.PEDIDO_STATUS);
					Bartender.INSTANCE.enviarPedido(pedidoAtt);					
				}
			}
		 };
		 
		 JMenuItem item;
		 popup.add(item = new JMenuItem("Status: Normal", new ImageIcon("1.gif")));
		 item.setHorizontalTextPosition(JMenuItem.RIGHT);
		 item.addActionListener(menuListener);
		 popup.addSeparator();
		 
		 popup.add(item = new JMenuItem("Status: Fazendo", new ImageIcon("1.gif")));
		 item.setHorizontalTextPosition(JMenuItem.RIGHT);
		 item.addActionListener(menuListener);
		 popup.addSeparator();		 
		 
		 popup.add(item = new JMenuItem("Status: Remover", new ImageIcon("2.gif")));
		 item.setHorizontalTextPosition(JMenuItem.RIGHT);
		 item.addActionListener(menuListener);

		 popup.setLabel("Menu Pedido");
		 popup.setBorder(new BevelBorder(BevelBorder.RAISED));
		 addMouseListener(new MousePopupListener());
		 
		 atualizaTempo();
	}
	
	class MousePopupListener extends MouseAdapter {
	    public void mousePressed(MouseEvent e) {
	      checkPopup(e);
	    }

	    public void mouseClicked(MouseEvent e) {
	      checkPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	      checkPopup(e);
	    }

	    private void checkPopup(MouseEvent e) {
	      if (!e.isPopupTrigger()) {
	        popup.show(PainelPedido.this, e.getX(), e.getY());
	      }
	    }
	}
	
	public void atualizaTempo()
	{
	    long duration = System.currentTimeMillis() - pedidoAtt.getHora().getTime();
	    long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
	    long hours = TimeUnit.MILLISECONDS.toHours(duration);
	    long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
	    
	    if (hours > 0) {
	    	labelTempo.setText(hours + " horas atrás.");
	    }
	    else if (minutes > 0) {
	    	labelTempo.setText(minutes + " minutos atrás.");
	    }
	    else
	    {
	    	labelTempo.setText(seconds + " segundos atrás.");
	    }		
	}
	
	public void setPedido(Pedido ped)
	{
		pedidoAtt = ped;
	}
	
	public Pedido getPedido()
	{
		return pedidoAtt;
	}
	
	public void refreshStatus()
	{
		switch(pedidoAtt.getStatus())
		{
			case 2:
			{
				statusBar.setBackground(new Color(251, 242, 197));
				break;
			}
			case 3:
			{
				statusBar.setBackground(new Color(251, 197, 197));
				break;
			}
			default:
			{
				statusBar.setBackground(new Color(237, 237, 237));	
			}
		}
	}
}