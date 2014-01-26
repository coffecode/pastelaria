package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;

import javax.swing.border.EtchedBorder;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import codecoffe.restaurantes.primitivas.Pedido;
import codecoffe.restaurantes.sockets.CacheTodosPedidos;
import codecoffe.restaurantes.utilitarios.Bartender;
import codecoffe.restaurantes.utilitarios.CompararTempo;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.laf.scroll.WebScrollPane;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PainelCozinha extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JPanel pedidosCozinha;
	private static ArrayList<Pedido> todosPedidos;
	private static Timer timer, timer2;
	
	PainelCozinha()
	{
		todosPedidos = new ArrayList<>();
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Pedidos"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		pedidosCozinha = new JPanel();
		pedidosCozinha.setLayout(new GridBagLayout());
		WebScrollPane scroll = new WebScrollPane(pedidosCozinha, false);
		scroll.setPreferredSize(getSize());
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.getVerticalScrollBar().setUnitIncrement(20);		
		add(scroll);
		
		timer = new Timer();
		timer.schedule(new RemindTask(), 0, 10*1000);	// começa imediatamente e repete a cada 10 segundos
		
		if(Configuracao.getModo() == UtilCoffe.SERVER)
		{
			timer2 = new Timer();
			timer2.schedule(new DeleteTask(), 0, 5*1000);	
		}
	}
	
	class DeleteTask extends TimerTask 
	{
        public void run() 
        {
        	for(int i = 0; i < todosPedidos.size(); i++)
        	{
        		if(todosPedidos.get(i).getStatus() == UtilCoffe.PEDIDO_REMOVER)
        		{
        			long duration = System.currentTimeMillis() - todosPedidos.get(i).getUltimaEdicao().getTime();
        			long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        			
        			if(seconds > 15) // 15 segundos de cooldown para deletar
        			{
        				todosPedidos.get(i).setHeader(UtilCoffe.PEDIDO_DELETA);
        				Bartender.enviarPedido(todosPedidos.get(i));      			
        			}
        		}
        		else
        		{
            		long duration = System.currentTimeMillis() - todosPedidos.get(i).getHora().getTime();
            		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);  
            		
            		if(minutes > 120)	// deleta o pedido, nao importa o status
            		{
        				todosPedidos.get(i).setHeader(UtilCoffe.PEDIDO_DELETA);
        				Bartender.enviarPedido(todosPedidos.get(i));          			
            		}        			
        		}
        	}
        }
    }	
	
	class RemindTask extends TimerTask 
	{
        public void run() 
        {
        	for(int i = 0; i < pedidosCozinha.getComponentCount(); i++)
        	{
        		PainelPedido pp = (PainelPedido)pedidosCozinha.getComponent(i);
        		pp.atualizaTempo();
        	}
        }
    }
	
	public static void atualizaTodosPedidos(CacheTodosPedidos tp)
	{
		todosPedidos = tp.getTodosPedidos();
		
		GridBagConstraints gbc = new GridBagConstraints();		
		gbc.insets = new Insets(5,5,5,5);
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx = 1;	// colunas
		int linha = 1;

		Collections.sort(todosPedidos, new CompararTempo());
		
		pedidosCozinha.removeAll();
		for(int i = 0; i < todosPedidos.size(); i++)
		{
			gbc.gridy = linha;
			pedidosCozinha.add(new PainelPedido(todosPedidos.get(i)), gbc);
			linha++;
		}
		
		pedidosCozinha.revalidate();
		pedidosCozinha.repaint();			
	}
	
	public static void atualizaTodosPedidos()
	{
		
	}
	
	public static CacheTodosPedidos getTodosPedidos()
	{
		CacheTodosPedidos tp = new CacheTodosPedidos(todosPedidos);
		return tp;
	}
	
	public static boolean verificaStatusPedido(int local, int qntd, String nome, String adicionais)
	{
		for(int i = 0; i < todosPedidos.size(); i++)
		{
			if(todosPedidos.get(i).getLocal() == local)
			{
				if(todosPedidos.get(i).getProduto().getQuantidade() == qntd &&
						todosPedidos.get(i).getProduto().getNome().equals(nome) &&
						todosPedidos.get(i).getProduto().getAllAdicionais().equals(adicionais)){
					
					if(todosPedidos.get(i).getStatus() == UtilCoffe.PEDIDO_FAZENDO)
						return false;
				}
			}
		}
		
		return true;
	}
		
	public static void atualizaPedido(Pedido p)
	{
		if(p.getHeader() == UtilCoffe.PEDIDO_ADICIONA) // adiciona
		{
    		GridBagConstraints gbc = new GridBagConstraints();		
    		gbc.insets = new Insets(5,5,5,5);
    		gbc.anchor = GridBagConstraints.NORTH;
    		gbc.fill = GridBagConstraints.HORIZONTAL;
    		gbc.weightx = 1.0;
    		gbc.gridx = 1;	// colunas
    		int linha = 1;

			todosPedidos.add(p);
			Collections.sort(todosPedidos, new CompararTempo());
			
			pedidosCozinha.removeAll();
			for(int i = 0; i < todosPedidos.size(); i++)
			{
				gbc.gridy = linha;
				pedidosCozinha.add(new PainelPedido(todosPedidos.get(i)), gbc);
				linha++;
			}
			
    		pedidosCozinha.revalidate();
    		pedidosCozinha.repaint();			
		}
		else if(p.getHeader() == UtilCoffe.PEDIDO_DELETA) // deleta
		{
			for(int i = 0; i < todosPedidos.size(); i++)
			{
				if(todosPedidos.get(i).getLocal() == p.getLocal() && todosPedidos.get(i).getIdLocal() == p.getIdLocal())
				{
					if(todosPedidos.get(i).getHora().compareTo(p.getHora()) == 0 && todosPedidos.get(i).getProduto().getNome().equals(p.getProduto().getNome()))
					{
						if(todosPedidos.get(i).getProduto().getAllAdicionais().equals(p.getProduto().getAllAdicionais()))
						{
							pedidosCozinha.remove(i);
							todosPedidos.remove(i);
							pedidosCozinha.revalidate();
							pedidosCozinha.repaint();
							break;
						}
					}					
				}
			}
		}
		else if(p.getHeader() == UtilCoffe.PEDIDO_STATUS) // edita status
		{
			for(int i = 0; i < todosPedidos.size(); i++)
			{
				if(todosPedidos.get(i).getLocal() == p.getLocal() && todosPedidos.get(i).getIdLocal() == p.getIdLocal())
				{
					if(todosPedidos.get(i).getHora().compareTo(p.getHora()) == 0 && todosPedidos.get(i).getProduto().getNome().equals(p.getProduto().getNome()))
					{
						if(todosPedidos.get(i).getProduto().getAllAdicionais().equals(p.getProduto().getAllAdicionais()))
						{
							todosPedidos.set(i, p);
							PainelPedido pp = (PainelPedido)pedidosCozinha.getComponent(i);
							pp.setPedido(p);
							pp.refreshStatus();
							break;
						}
					}					
				}
			}
		}
	}	
}