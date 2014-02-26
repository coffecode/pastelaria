package codecoffe.restaurantes.interfaceGrafica;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.primitivas.Pedido;
import codecoffe.restaurantes.primitivas.Produto;
import codecoffe.restaurantes.primitivas.ProdutoVenda;
import codecoffe.restaurantes.sockets.CacheTodosPedidos;
import codecoffe.restaurantes.sockets.Server;
import codecoffe.restaurantes.utilitarios.Bartender;
import codecoffe.restaurantes.utilitarios.CompararTempo;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.laf.scroll.WebScrollPane;
import com.alee.managers.notification.NotificationManager;

public class PainelCozinha extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel pedidosCozinha;
	private ArrayList<Pedido> todosPedidos;
	private Timer timer, timer2;
	private WebScrollPane scroll;
	private boolean flag_refresh, flag_musica;
	private URL musica;
	
	private PainelCozinha()
	{
		flag_refresh = false;
		flag_musica = false;
		musica = getClass().getClassLoader().getResource("imgs/novo_pedido.wav");
		todosPedidos = new ArrayList<Pedido>();
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Pedidos"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		pedidosCozinha = new JPanel();
		pedidosCozinha.setLayout(new GridBagLayout());
		scroll = new WebScrollPane(pedidosCozinha, false);
		scroll.setPreferredSize(getSize());
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		//scroll.getVerticalScrollBar().setPreferredSize(new Dimension(50, 200));
		
		add(scroll);
		
		timer = new Timer();
		timer.schedule(new UpdateTask(), 0, 8*1000);

		timer2 = new Timer();
		timer2.schedule(new VerificaTask(), 0, 5*1000);
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		{
			atualizaTodosPedidos();
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
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
		        	for(int i = 0; i < todosPedidos.size(); i++)
		        	{
		        		if(todosPedidos.get(i).getHeader() == UtilCoffe.PEDIDO_ADICIONA)
		        		{
		        			long duration = System.currentTimeMillis() - todosPedidos.get(i).getUltimaEdicao().getTime();
		        			long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		        			
		        			if(seconds > 5) // demorar um pouco antes de adicionar na lista
		        			{
		        				flag_refresh = true;
		        				todosPedidos.get(i).setHeader(UtilCoffe.PEDIDO_STATUS);
		        				todosPedidos.get(i).setStatus(UtilCoffe.PEDIDO_NOVO);
		        			}
		        		}
		        		else if(todosPedidos.get(i).getStatus() == UtilCoffe.PEDIDO_NOVO)
		        		{
		        			long duration = System.currentTimeMillis() - todosPedidos.get(i).getUltimaEdicao().getTime();
		        			long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		        			
		        			if(seconds > 110) // fica uns 2 minutos com status de novo
		        			{
		        				todosPedidos.get(i).setHeader(UtilCoffe.PEDIDO_STATUS);
		        				todosPedidos.get(i).setStatus(UtilCoffe.PEDIDO_NORMAL);
								PainelPedido pp = (PainelPedido)pedidosCozinha.getComponent(i);
								pp.setPedido(todosPedidos.get(i));
								pp.refreshStatus();
		        			}        			
		        		}
		        		else if((todosPedidos.get(i).getStatus() == UtilCoffe.PEDIDO_REMOVER || todosPedidos.get(i).getStatus() == UtilCoffe.PEDIDO_DELETADO) 
		        				&& Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		        		{
		        			long duration = System.currentTimeMillis() - todosPedidos.get(i).getUltimaEdicao().getTime();
		        			long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		        			
		        			if(seconds > 10) // 10 segundos de cooldown para deletar
		        			{
		        				todosPedidos.get(i).setHeader(UtilCoffe.PEDIDO_DELETA);
		        				Bartender.INSTANCE.enviarPedido(todosPedidos.get(i));      			
		        			}
		        		}
		        		else if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
		        		{
		            		long duration = System.currentTimeMillis() - todosPedidos.get(i).getHora().getTime();
		            		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);  
		            		
		            		if(minutes > Configuracao.INSTANCE.getIntervaloPedido())	// deleta o pedido, nao importa o status
		            		{
		        				todosPedidos.get(i).setHeader(UtilCoffe.PEDIDO_DELETA);
		        				Bartender.INSTANCE.enviarPedido(todosPedidos.get(i));          			
		            		}
		        		}
		        	}	
				}
			});
        }
    }
	
	class UpdateTask extends TimerTask 
	{
		public void run() 
		{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for(int i = 0; i < pedidosCozinha.getComponentCount(); i++)
					{
						PainelPedido pp = (PainelPedido)pedidosCozinha.getComponent(i);
						pp.atualizaTempo();
					}

					if(flag_refresh)
					{
						//new ScrollSetter(scroll.getVerticalScrollBar(), scroll.getVerticalScrollBar().getValue()).start(); 
						
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
							if(todosPedidos.get(x).getHeader() != UtilCoffe.PEDIDO_ADICIONA)
							{
								gbc.gridy = linha;
								pedidosCozinha.add(new PainelPedido(todosPedidos.get(x)), gbc);
								linha++;
							}
						}

						pedidosCozinha.revalidate();
						pedidosCozinha.repaint();
						
						if(flag_musica && Configuracao.INSTANCE.isSomCozinha() && isVisible())
						{
							try {
								Clip clip = AudioSystem.getClip();
								AudioInputStream ais = AudioSystem.getAudioInputStream(musica);
								clip.open(ais);
								clip.start();
							} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
								e.printStackTrace();
							}
						}
						
						flag_refresh = false;
						flag_musica = false;
					}
				}
			});
		}
	}
	
	public void atualizaTodosPedidos()
	{
		todosPedidos.clear();
		
		try {
			Query pega = new Query();
			pega.executaQuery("SELECT * FROM pedidos");
			
			SimpleDateFormat formataData = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
			
			while(pega.next())
			{
				ProdutoVenda produto = new ProdutoVenda();
				produto.setIdUnico(pega.getInt("produto"));
				produto.setQuantidade(pega.getInt("quantidade"), 0);
				produto.setComentario(pega.getString("observacao"));
				
				Query pega1 = new Query();
				pega1.executaQuery("SELECT * FROM produtos_new WHERE id = " + produto.getIdUnico());
				
				if(pega1.next())
				{
					produto.setNome(pega1.getString("nome"));
					produto.setReferencia(pega1.getString("referencia"));
					produto.setCodigo(pega1.getInt("codigo"));
					produto.setPreco(0);
					
					if(!UtilCoffe.vaziu(pega.getString("adicionais")))
					{
						String[] adcArray = pega.getString("adicionais").split("\\s+");
						
						if(adcArray.length > 0)
						{
							for(int x = 0; x < adcArray.length; x++)
							{
								if(UtilCoffe.isNumeric(adcArray[x]) && !UtilCoffe.vaziu(adcArray[x]))
								{
									Query pega2 = new Query();
									pega2.executaQuery("SELECT * FROM produtos_new WHERE id = " + Integer.parseInt(adcArray[x]));
									if(pega2.next())
									{
										Produto adicional = new Produto(pega2.getString("nome"), pega2.getString("referencia"), 
												UtilCoffe.precoToDouble(pega2.getString("preco")), pega2.getInt("id"), pega2.getInt("codigo"));
										
										produto.adicionrAdc(adicional);
									}
									pega2.fechaConexao();	
								}
							}	
						}
					}
					
					Date date = formataData.parse(pega.getString("data"));
					
	        		long duration = System.currentTimeMillis() - date.getTime();
	        		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);  
	        		
	        		if(minutes > 120)	// deleta o pedidos antigos...
	        		{
	        			Query deleta = new Query();
	        			deleta.executaUpdate("DELETE FROM pedidos WHERE `id` = "+ pega.getInt("id") +";");
	        			deleta.fechaConexao();
	        		}
	        		else
	        		{
	    				todosPedidos.add(new Pedido(produto, date, pega.getString("atendido"), 
	    						pega.getInt("local"), pega.getInt("status"), pega.getInt("id")));	
	        		}
				}
				else	// esse produto não existe mais, então flw!
				{
        			Query deleta = new Query();
        			deleta.executaUpdate("DELETE FROM pedidos WHERE `id` = "+ pega.getInt("id") +";");
        			deleta.fechaConexao();
				}
				
				pega1.fechaConexao();
			}
			
			pega.fechaConexao();
			flag_refresh = true;
		} catch (ClassNotFoundException | SQLException | ParseException e) {
			e.printStackTrace();
			new PainelErro(e);
			System.exit(0);
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
	
	public int verificaStatusPedido(int local, ProdutoVenda produto)
	{
		int produtos_fazendo = 0;
		for(int i = 0; i < todosPedidos.size(); i++)
		{
			if(todosPedidos.get(i).getLocal() == local)
			{
				if(todosPedidos.get(i).getProduto().compareTo(produto))
				{	
					if(todosPedidos.get(i).getStatus() == UtilCoffe.PEDIDO_FAZENDO)
						produtos_fazendo += todosPedidos.get(i).getProduto().getQuantidade();
				}
			}
		}
		return produtos_fazendo;
	}
	
	public void buscarPedidos(Pedido p)
	{
		ArrayList<Pedido> listaNormal = new ArrayList<Pedido>();
		ArrayList<Pedido> listaFazendo = new ArrayList<Pedido>();
		
		for(int i = 0; i < todosPedidos.size(); i++)
		{
			if(todosPedidos.get(i).getLocal() == p.getLocal())
			{
				if(todosPedidos.get(i).getProduto().compareTo(p.getProduto())) 
				{
					if(todosPedidos.get(i).getStatus() != UtilCoffe.PEDIDO_DELETADO && todosPedidos.get(i).getStatus() != UtilCoffe.PEDIDO_REMOVER)
					{
						if(todosPedidos.get(i).getStatus() == UtilCoffe.PEDIDO_FAZENDO)
							listaFazendo.add(todosPedidos.get(i));
						else
							listaNormal.add(todosPedidos.get(i));	
					}
				}
			}
		}
		
		int quantidade = p.getProduto().getQuantidade();
		if(quantidade < 0)
			quantidade = 1;
		
		for(int i = 0; i < listaNormal.size(); i++)
		{
			if(listaNormal.get(i).getProduto().getQuantidade() - quantidade > 0)
			{
				if(listaNormal.get(i).getHeader() == UtilCoffe.PEDIDO_ADICIONA || listaNormal.get(i).getHeader() == UtilCoffe.PEDIDO_EDITADO)
				{
					listaNormal.get(i).setHeader(UtilCoffe.PEDIDO_EDITADO);
					listaNormal.get(i).setStatus(UtilCoffe.PEDIDO_NOVO);
					listaNormal.get(i).getProduto().setQuantidade(quantidade, 2);
				}
				else
				{
					listaNormal.get(i).setHeader(UtilCoffe.PEDIDO_STATUS);
					listaNormal.get(i).setStatus(UtilCoffe.PEDIDO_EDITAR);
					listaNormal.get(i).getProduto().setQuantidade(quantidade, 2);					
				}

				atualizaPedido(listaNormal.get(i));
				quantidade = 0;
			}
			else
			{
				if(listaNormal.get(i).getHeader() == UtilCoffe.PEDIDO_ADICIONA || listaNormal.get(i).getHeader() == UtilCoffe.PEDIDO_EDITADO)
				{
					listaNormal.get(i).setHeader(UtilCoffe.PEDIDO_DELETA);
				}
				else
				{
					listaNormal.get(i).setHeader(UtilCoffe.PEDIDO_STATUS);
					listaNormal.get(i).setStatus(UtilCoffe.PEDIDO_DELETADO);				
				}

				atualizaPedido(listaNormal.get(i));
				quantidade -= listaNormal.get(i).getProduto().getQuantidade();
			}
			
			if(quantidade <= 0)
				break;
		}
		
		if(quantidade > 0)
		{
			for(int i = 0; i < listaFazendo.size(); i++)
			{
				if(listaFazendo.get(i).getProduto().getQuantidade() - quantidade > 0)
				{
					listaFazendo.get(i).setHeader(UtilCoffe.PEDIDO_STATUS);
					listaFazendo.get(i).setStatus(UtilCoffe.PEDIDO_EDITAR);
					atualizaPedido(listaFazendo.get(i));
					quantidade = 0;
				}
				else
				{
					listaFazendo.get(i).setHeader(UtilCoffe.PEDIDO_STATUS);
					listaFazendo.get(i).setStatus(UtilCoffe.PEDIDO_DELETADO);
					atualizaPedido(listaFazendo.get(i));
					quantidade -= listaFazendo.get(i).getProduto().getQuantidade();
				}
				
				if(quantidade <= 0)
					break;
			}			
		}
	}
		
	public void atualizaPedido(Pedido p)
	{		
		/* tive que criar o objeto denovo... 
		 * se nao ele passava  referencia no array list */
		ProdutoVenda pNovo = UtilCoffe.cloneProdutoVenda(p.getProduto());
		
		if(p.getHeader() == UtilCoffe.PEDIDO_ADICIONA) // adiciona
		{
			flag_musica = true;
			if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
			{
				int idPedido = 0;
				try {
					SimpleDateFormat formataData = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
					
					Query envia = new Query();
					envia.executaUpdate("INSERT INTO pedidos(local, status, observacao, atendido, data, produto, adicionais, quantidade)"
							+ " VALUES(" + p.getLocal() + ", "
							+ UtilCoffe.PEDIDO_NORMAL + ", '" 
							+ pNovo.getComentario() + "', '" 
							+ p.getAtendido() + "', '" 
							+ formataData.format(p.getHora()) + "', " 
							+ pNovo.getIdUnico() + ", '" 
							+ pNovo.getAllAdicionaisId() + "', " 
							+ pNovo.getQuantidade() + ");");
					
					envia.executaQuery("SELECT id FROM pedidos ORDER BY id DESC limit 0, 1");
					
					if(envia.next())
					{
						idPedido = envia.getInt("id");
					}
					
					envia.fechaConexao();
					
					if(idPedido > 0)
					{
			    		Pedido pedNovo = p;
			    		pedNovo.setProduto(pNovo);
			    		pedNovo.setIdUnico(idPedido);
						todosPedidos.add(pedNovo);
						System.out.println("Pedido recebido na cozinha. #" + pedNovo.getIdUnico());
						Server.getInstance().enviaTodos(pedNovo);
					}
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					new PainelErro(e);
				}
			}
			else
			{
	    		Pedido pedNovo = p;
	    		pedNovo.setProduto(pNovo);
				todosPedidos.add(pedNovo);
				System.out.println("Pedido recebido na cozinha.");	
			}
		}
		else if(p.getHeader() == UtilCoffe.PEDIDO_EDITADO)
		{
			for(int i = 0; i < todosPedidos.size(); i++)
			{
				if(todosPedidos.get(i).getIdUnico() == p.getIdUnico())
				{
					todosPedidos.get(i).getProduto().setQuantidade(pNovo.getQuantidade(), 0);
					flag_refresh = true;
					break;
				}
			}
		}
		else if(p.getHeader() == UtilCoffe.PEDIDO_DELETA) // deleta
		{
			for(int i = 0; i < todosPedidos.size(); i++)
			{
				if(todosPedidos.get(i).getIdUnico() == p.getIdUnico())
				{
					if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
					{
	        			try {
	        				Query deleta = new Query();
							deleta.executaUpdate("DELETE FROM pedidos WHERE `id` = "+ todosPedidos.get(i).getIdUnico() +";");
							deleta.fechaConexao();
						} catch (ClassNotFoundException | SQLException e) {
							e.printStackTrace();
						}	
					}
					
					todosPedidos.remove(i);
					
					if(i < pedidosCozinha.getComponentCount())
					{
						pedidosCozinha.remove(i);
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								pedidosCozinha.revalidate();
								pedidosCozinha.repaint();
							}
						});						
					}
					
					if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
						Server.getInstance().enviaTodos(p);
					
					break;				
				}
			}	
		}
		else if(p.getHeader() == UtilCoffe.PEDIDO_STATUS) // edita status
		{
			if(p.getIdUnico() == 0)
			{
				buscarPedidos(p);
			}
			else
			{
				for(int i = 0; i < todosPedidos.size(); i++)
				{
					if(todosPedidos.get(i).getIdUnico() == p.getIdUnico())
					{						
						if(p.getStatus() == UtilCoffe.PEDIDO_EDITAR)
							todosPedidos.get(i).getProduto().setQuantidade(pNovo.getQuantidade(), 0);
						
						if(p.getStatus() == UtilCoffe.PEDIDO_DELETADO || p.getStatus() == UtilCoffe.PEDIDO_EDITAR)
							avisaPedido(p, p.getStatus());
						
						todosPedidos.get(i).setUltimaEdicao(new Date());
						todosPedidos.get(i).setHeader(p.getHeader());
						todosPedidos.get(i).setStatus(p.getStatus());
						
						final int idAgora = i;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								
								if(idAgora < pedidosCozinha.getComponentCount())
								{
									PainelPedido pp = (PainelPedido)pedidosCozinha.getComponent(idAgora);
									pp.setPedido(todosPedidos.get(idAgora));
									pp.refreshStatus();
									pp.refreshQuantidade();	
								}	
							}
						});
						
						if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
						{
							Server.getInstance().enviaTodos(p);
							
		        			try {
		        				Query atualiza = new Query();
		        				atualiza.executaUpdate("UPDATE pedidos SET `status` = " + todosPedidos.get(i).getStatus() 
		        						+ ", `quantidade` = " + todosPedidos.get(i).getProduto().getQuantidade() 
		        						+ " WHERE `id` = " + todosPedidos.get(i).getIdUnico() + ";");
		        				atualiza.fechaConexao();
							} catch (ClassNotFoundException | SQLException e) {
								e.printStackTrace();
							}
						}
						
						break;
					}
				}
			}
		}
	}
	
	public void avisaPedido(Pedido p, int tipo) 
	{
		if(isVisible())
		{
			NotificationManager.setLocation(2);
			String local = "";
			
			if(p.getLocal() == 0) {
				local = "Balcão";
			}
			else {
				local = Configuracao.INSTANCE.getTipoNome() + " " + p.getLocal();
			}
			
			switch(tipo)
			{
				case UtilCoffe.PEDIDO_DELETADO:
				{
					NotificationManager.showNotification(this, "Pedido Deletado (" + local + " - " 
								+ p.getProduto().getNome() + ")", new ImageIcon(getClass().getClassLoader().getResource("imgs/notifications_cancel.png"))).setDisplayTime(5000);
					break;
				}
				case UtilCoffe.PEDIDO_EDITAR:
				{
					NotificationManager.showNotification(this, "Pedido Alterado (" + local + " - " 
							+ p.getProduto().getNome() + ")", new ImageIcon(getClass().getClassLoader().getResource("imgs/notifications_warning.png"))).setDisplayTime(5000);
					break;		
				}
			}
		}
	}
	
	/*class ScrollSetter extends Thread{
		JScrollBar scrollBar;
		int value;

		public ScrollSetter(JScrollBar scroll, int value){
			this.scrollBar = scroll;
			this.value = value;
		}

		public void run(){
			try{
				Thread.sleep(100);
				scrollBar.getModel().setValue(value);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}*/
}