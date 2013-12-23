import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

public class PainelStatus extends JPanel implements Runnable, MouseListener {

    protected TimerThread timerThread;
    static private JLabel leftLabel;
    private JButton bSair;
    static private String nomeFun = "André Alves";
    
    public PainelStatus(String nomeFuncionario)
    {
    	run();
    	setNome(nomeFuncionario);
    }
    
    static public void setNome(String nomeFuncionario)
    {
    	leftLabel.setText("Olá, " + nomeFuncionario + ".");
    	nomeFun = nomeFuncionario;
    }
    
    static public String pegaNome()
    {
    	return nomeFun;
    }
    
    @Override
    public void run() {

        setLayout(new BorderLayout());
		this.setMaximumSize(new Dimension(800, 40));
		this.setMinimumSize(new Dimension(800, 40));
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "*"));

        JStatusBar statusBar = new JStatusBar();
        leftLabel = new JLabel("-");
        leftLabel.setFont(new Font("sans-serif", Font.PLAIN, 12));
        statusBar.setLeftComponent(leftLabel);
        
        ImageIcon iconeSair = new ImageIcon("imgs/sair.png");
        bSair = new JButton(iconeSair);
        bSair.addMouseListener(this);
        bSair.setBorder(BorderFactory.createEmptyBorder());
        bSair.setContentAreaFilled(false);        
        statusBar.setLeftComponent(bSair);

        final JLabel dateLabel = new JLabel();
        dateLabel.setHorizontalAlignment(JLabel.CENTER);
        statusBar.addRightComponent(dateLabel);

        final JLabel timeLabel = new JLabel();
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        statusBar.addRightComponent(timeLabel);

        add(statusBar, BorderLayout.SOUTH);

        timerThread = new TimerThread(dateLabel, timeLabel);
        timerThread.start();
    }

    public class TimerThread extends Thread {

        protected boolean isRunning;

        protected JLabel dateLabel;
        protected JLabel timeLabel;

        protected SimpleDateFormat dateFormat = 
                new SimpleDateFormat("EEE, d MMM yyyy");
        protected SimpleDateFormat timeFormat =
                new SimpleDateFormat("h:mm a");

        public TimerThread(JLabel dateLabel, JLabel timeLabel) {
            this.dateLabel = dateLabel;
            this.timeLabel = timeLabel;
            this.isRunning = true;
        }

        @Override
        public void run() {
            while (isRunning) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Calendar currentCalendar = Calendar.getInstance();
                        Date currentTime = currentCalendar.getTime();
                        dateLabel.setText(dateFormat.format(currentTime));
                        timeLabel.setText(timeFormat.format(currentTime));
                        
                        dateLabel.setFont(new Font("sans-serif", Font.PLAIN, 12));
                        timeLabel.setFont(new Font("sans-serif", Font.PLAIN, 12));
                    }
                });

                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                }
            }
        }

        public void setRunning(boolean isRunning) {
            this.isRunning = isRunning;
        }
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == bSair)
		{
			int opcao = JOptionPane.showConfirmDialog(null, "Você tem certeza que quer sair?", "Logout",JOptionPane.YES_NO_OPTION);
			
			if(opcao == JOptionPane.YES_OPTION)
			{
				MenuPrincipal.DeletarPrincipal();
				MenuLogin LoginNovo = new MenuLogin();
				LoginNovo.setVisible(true);	
			}	
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}