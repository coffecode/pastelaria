package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;

import javax.swing.border.EtchedBorder;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import codecoffe.restaurantes.primitivas.Pedido;
import codecoffe.restaurantes.primitivas.Produto;
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
	private JPanel pedidosCozinha;
	private ArrayList<Pedido> todosPedidos;
	private Timer timer, timer2;
	private boolean flag_refresh = false;
	
	private PainelCozinha()
	{
		todosPedidos = new ArrayList<Pedido>();
		
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
		timer.schedule(new UpdateTask(), 0, 8*1000);	// começa imediatamente e repete a cada 10 segundos
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{
			timer2 = new Timer();
			timer2.schedule(new VerificaTask(), 0, 5*1000);	
		}
	}
	
	private static class CozinhaSingletonHolder { 
		public static final PainelCozinha INSTANCE = new PainelCozinha();
	}
 
	public static PainelCozinha getInstance() {
		return CozinhaSingletonHolder.INSTANCE;
	}	
	
	class VerificaTask extends TimerTask 
	{
        public void run() 
        {
        	for(int i = 0; i < todosPedidos.size(); i++)
        	{
        		if(todosPedidos.get(i).getHeader() == UtilCoffe.PEDIDO_NOVO)
        		{
        			long duration = System.currentTimeMillis() - todosPedidos.get(i).getUltimaEdicao().getTime();
        			long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        			
        			if(seconds > 6) // demorar um pouco antes de adicionar na lista
        			{
        				todosPedidos.get(i).setHeader(UtilCoffe.PEDIDO_NOVO2);
        				Bartender.INSTANCE.enviarPedido(todosPedidos.get(i));
        			}
        		}
        		else if(todosPedidos.get(i).getHeader() == UtilCoffe.PEDIDO_NOVO2)
        		{
        			long duration = System.currentTimeMillis() - todosPedidos.get(i).getUltimaEdicao().getTime();
        			long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        			
        			if(seconds > 110) // fica uns 2 minutos com status de novo
        			{
        				todosPedidos.get(i).setHeader(UtilCoffe.PEDIDO_STATUS);
        				todosPedidos.get(i).setStatus(UtilCoffe.PEDIDO_NORMAL);
        				Bartender.INSTANCE.enviarPedido(todosPedidos.get(i));
        			}        			
        		}
        		else if(todosPedidos.get(i).getStatus() == UtilCoffe.PEDIDO_REMOVER)
        		{
        			long duration = System.currentTimeMillis() - todosPedidos.get(i).getUltimaEdicao().getTime();
        			long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        			
        			if(seconds > 10) // 10 segundos de cooldown para deletar
        			{
        				todosPedidos.get(i).setHeader(UtilCoffe.PEDIDO_DELETA);
        				Bartender.INSTANCE.enviarPedido(todosPedidos.get(i));      			
        			}
        		}
        		else
        		{
            		long duration = System.currentTimeMillis() - todosPedidos.get(i).getHora().getTime();
            		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);  
            		
            		if(minutes > 120)	// deleta o pedido, nao importa o status
            		{
        				todosPedidos.get(i).setHeader(UtilCoffe.PEDIDO_DELETA);
        				Bartender.INSTANCE.enviarPedido(todosPedidos.get(i));          			
            		}        			
        		}
        	}
        }
    }	
	
	class UpdateTask extends TimerTask 
	{
        public void run() 
        {
        	for(int i = 0; i < pedidosCozinha.getComponentCount(); i++)
        	{
        		PainelPedido pp = (PainelPedido)pedidosCozinha.getComponent(i);
        		pp.atualizaTempo();
        	}
        	
        	if(flag_refresh)
        	{
        		GridBagConstraints gbc = new GridBagConstraints();		
        		gbc.insets = new Insets(5,5,5,5);
        		gbc.anchor = GridBagConstraints.NORTH;
        		gbc.fill = GridBagConstraints.HORIZONTAL;
        		gbc.weightx = 1.0;
        		gbc.gridx = 1;	// colunas
        		int linha = 1;
    			
    			Collections.sort(todosPedidos, new CompararTempo());
    			pedidosCozinha.removeAll();
    			for(int x = 0; x < todosPedidos.size(); x++)
    			{
    				if(todosPedidos.get(x).getHeader() != UtilCoffe.PEDIDO_NOVO)
    				{
        				gbc.gridy = linha;
        				pedidosCozinha.add(new PainelPedido(todosPedidos.get(x)), gbc);
        				linha++;	
    				}
    			}
    			
        		pedidosCozinha.revalidate();
        		pedidosCozinha.repaint();
        		Toolkit.getDefaultToolkit().beep();
        		flag_refresh = false;
        	}
        }
    }
	
	public void atualizaTodosPedidos(CacheTodosPedidos tp)
	{
		todosPedidos = tp.getTodosPedidos();
		flag_refresh = true;
	}
	
	public CacheTodosPedidos getTodosPedidos()
	{
		CacheTodosPedidos tp = new CacheTodosPedidos(todosPedidos);
		return tp;
	}
	
	public int verificaStatusPedido(int local, int qntd, String nome, String adicionais)
	{
		int produtos_fazendo = 0;
		for(int i = 0; i < todosPedidos.size(); i++)
		{
			if(todosPedidos.get(i).getLocal() == local)
			{
				if(todosPedidos.get(i).getProduto().getNome().equals(nome) && todosPedidos.get(i).getProduto().getAllAdicionais().equals(adicionais))
				{	
					if(todosPedidos.get(i).getStatus() == UtilCoffe.PEDIDO_FAZENDO)
						produtos_fazendo += todosPedidos.get(i).getProduto().getQuantidade();
				}
			}
		}
		
		return produtos_fazendo;
	}
		
	public void atualizaPedido(Pedido p)
	{
		/* tive que criar o objeto denovo... 
		 * se nao ele passava  referencia no array list
		 * bug filho da puta!*/
		
		Produto pNovo = new Produto();
		pNovo.setNome(p.getProduto().getNome());
		pNovo.setPagos(p.getProduto().getPagos());
		pNovo.setPreco(p.getProduto().getPreco());
		pNovo.setQuantidade(p.getProduto().getQuantidade(), 0);
		pNovo.setAdicionaisList(p.getProduto().getAdicionaisList());
		pNovo.calcularPreco();
		
		if(p.getHeader() == UtilCoffe.PEDIDO_ADICIONA) // adiciona
		{    		
    		Pedido pedNovo = p;
    		pedNovo.setProduto(pNovo);
    		pedNovo.setHeader(UtilCoffe.PEDIDO_NOVO);
			todosPedidos.add(pedNovo);
			
			System.out.println("Pedido recebido na cozinha.");
		}
		else if(p.getHeader() == UtilCoffe.PEDIDO_DELETA) // deleta
		{
			for(int i = 0; i < todosPedidos.size(); i++)
			{
				if(todosPedidos.get(i).getLocal() == p.getLocal())
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
				if(todosPedidos.get(i).getLocal() == p.getLocal())
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
		else if(p.getHeader() == UtilCoffe.PEDIDO_NOVO2)
		{
			for(int i = 0; i < todosPedidos.size(); i++)
			{
				if(todosPedidos.get(i).getLocal() == p.getLocal())
				{
					if(todosPedidos.get(i).getHora().compareTo(p.getHora()) == 0 && todosPedidos.get(i).getProduto().getNome().equals(p.getProduto().getNome()))
					{
						if(todosPedidos.get(i).getProduto().getAllAdicionais().equals(p.getProduto().getAllAdicionais()))
						{
					 		Pedido pedNovo = p;
				    		pedNovo.setProduto(pNovo);
				    		
							todosPedidos.set(i, pedNovo);
							flag_refresh = true;
							break;
						}
					}					
				}
			}
		}
	}	
}