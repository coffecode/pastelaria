package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.alee.extended.layout.ToolbarLayout;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.extended.statusbar.WebStatusLabel;
import com.alee.laf.text.WebTextField;

public class PainelLegenda extends WebStatusBar
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WebStatusLabel leg;
	protected TimerThread timerThread;
	
	private PainelLegenda()
	{
		setLayout(new ToolbarLayout());
		setMaximumSize(new Dimension(1920, 30));
		setMinimumSize(new Dimension(800, 30));
		
		leg = new WebStatusLabel("Desenvolvido por CodeCoffe (C) - 2014");	
		add(leg, ToolbarLayout.START);
        
        final WebTextField dateLabel = new WebTextField();
        dateLabel.setEditable(false);
        dateLabel.setHorizontalAlignment(JLabel.CENTER);
        dateLabel.setBackground(new Color(236, 236, 236));
        add(dateLabel, ToolbarLayout.END);

        timerThread = new TimerThread(dateLabel);
        timerThread.start();
	}
	
	private static class LegendasSingletonHolder { 
		public static final PainelLegenda INSTANCE = new PainelLegenda();
	}
 
	public static PainelLegenda getInstance() {
		return LegendasSingletonHolder.INSTANCE;
	}	
	
    public class TimerThread extends Thread {

        protected boolean isRunning;

        protected WebTextField dateLabel;

        protected SimpleDateFormat dateFormat = 
                new SimpleDateFormat("EEEE, d MMM yyyy  -  h:mm a");

        public TimerThread(WebTextField dateLabel) {
            this.dateLabel = dateLabel;
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
                        dateLabel.setFont(new Font("sans-serif", Font.PLAIN, 12));
                    }
                });

                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                	new PainelErro(e);
                }
            }
        }

        public void setRunning(boolean isRunning) {
            this.isRunning = isRunning;
        }
    }	
	
	public void AtualizaLegenda(String texto)
	{
		leg.setText(texto);
	}
}