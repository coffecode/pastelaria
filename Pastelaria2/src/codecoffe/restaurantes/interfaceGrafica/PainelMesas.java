package codecoffe.restaurantes.interfaceGrafica;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.primitivas.Produto;
import codecoffe.restaurantes.primitivas.ProdutoVenda;
import codecoffe.restaurantes.primitivas.Venda;
import codecoffe.restaurantes.sockets.CacheTodasMesas;
import codecoffe.restaurantes.utilitarios.Configuracao;
import codecoffe.restaurantes.utilitarios.Usuario;
import codecoffe.restaurantes.utilitarios.UtilCoffe;

import com.alee.laf.button.WebButton;
import com.alee.laf.scroll.WebScrollPane;

import java.awt.event.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PainelMesas extends JPanel
{
	private static final long serialVersionUID = 1L;
	private JPanel mesasPainel;
	private ArrayList<Venda> vendaMesas;
	private TheListener pegaMouse;
	private ImageIcon mesaOcupada, mesaNormal;
	
	private PainelMesas()
	{
		if(Configuracao.INSTANCE.getTipoPrograma() == UtilCoffe.TIPO_MESA) {
			mesaOcupada = new ImageIcon(getClass().getClassLoader().getResource("imgs/mesa_ocupada_mini.png"));
			mesaNormal = new ImageIcon(getClass().getClassLoader().getResource("imgs/mesa_mini.png"));	
		}
		else {
			mesaOcupada = new ImageIcon(getClass().getClassLoader().getResource("imgs/comanda_ocupada_26.png"));
			mesaNormal = new ImageIcon(getClass().getClassLoader().getResource("imgs/comanda_26.png"));
		}
		
		pegaMouse = new TheListener();
		vendaMesas = new ArrayList<>();
		
		mesasPainel = new JPanel();
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), Configuracao.INSTANCE.getTipoNome() + "s"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		mesasPainel.setLayout(new GridBagLayout());
		
		WebScrollPane scroll = new WebScrollPane(mesasPainel, false);
		scroll.setPreferredSize(getSize());
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		add(scroll);
		
		if(Configuracao.INSTANCE.getModo() == UtilCoffe.SERVER)
			gerarMesas();
	}
	
	private static class MesasSingletonHolder { 
		public static final PainelMesas INSTANCE = new PainelMesas();
	}
 
	public static PainelMesas getInstance() {
		return MesasSingletonHolder.INSTANCE;
	}	
	
	public void gerarMesas()
	{
		SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
		GridBagConstraints gbc = new GridBagConstraints();
		int colunas = 0;
		int linhas  = 0;
		int quebra_linhas = 0;
	
		Query pega = new Query();
		
		gbc.fill 			= GridBagConstraints.BOTH;
		gbc.weightx 		= 1.0;
		gbc.weighty 		= 1.0;
		gbc.insets 			= new Insets(7,7,7,7);  //top padding
		
		for(int i = 0; i < Configuracao.INSTANCE.getMesas(); i++)
		{
			String nomeMesa;
			if(Configuracao.INSTANCE.getTipoPrograma() == UtilCoffe.TIPO_COMANDA)
				nomeMesa = "Com ";
			else 
				nomeMesa = Configuracao.INSTANCE.getTipoNome() + " ";
			
			BotaoMesa mesa = new BotaoMesa(nomeMesa + (i+1));
			mesa.idMesa = i;
			mesa.setFont(new Font("Verdana", Font.PLAIN, 12));
			mesa.setPreferredSize(new Dimension(100, 60));
			mesa.setHorizontalTextPosition(AbstractButton.CENTER);
			mesa.setVerticalTextPosition(AbstractButton.BOTTOM);
			mesa.setRolloverDecoratedOnly(true);
			mesa.addMouseListener(pegaMouse);
			
			try {
				Venda vd = new Venda();
				ProdutoVenda p = null;
				Date data = null;
				pega.executaQuery("SELECT * FROM mesas WHERE mesas_id = " + i +";");
				
				while(pega.next())
				{
					p = new ProdutoVenda();
					p.setIdUnico(pega.getInt("produto"));
					p.setQuantidade(pega.getInt("quantidade"), 0);
					p.setPagos(pega.getInt("pago"));
					p.setComentario(pega.getString("comentario"));
					
					if(data == null) {
						try {
							data = formataData.parse(pega.getString("data"));
						} catch (ParseException e) {
							e.printStackTrace();
							data = new Date();
						}
					}
					else {
						try {
							if(data.after(formataData.parse(pega.getString("data"))))
							{
								data = formataData.parse(pega.getString("data"));
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					
					String[] adcArray = pega.getString("adicionais").split("\\s+");
					Query pega1 = new Query();
					pega1.executaQuery("SELECT * FROM produtos_new WHERE id = " + p.getIdUnico());
					
					if(pega1.next())
					{
						p.setNome(pega1.getString("nome"));
						p.setPreco(UtilCoffe.precoToDouble(pega1.getString("preco")));
						p.setCodigo(pega1.getInt("codigo"));
						p.setReferencia(pega1.getString("referencia"));
						
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
										
										p.adicionrAdc(adicional);
									}
									pega2.fechaConexao();	
								}
							}	
						}
						
						p.calcularPreco();
						vd.adicionarProduto(p);
					}
					
					pega1.fechaConexao();			
				}
				
				if(vd.getQuantidadeProdutos() > 0)
				{
					vd.calculaTotal();
					mesa.setIcon(mesaOcupada);
					mesa.setForeground(new Color(191, 93, 12));
					mesa.legendaBotao = Configuracao.INSTANCE.getTipoNome() + " " + (i+1) + " - total: R$" + String.format("%.2f", vd.getTotal());
					mesa.setToolTipText("R$" + String.format("%.2f", vd.getTotal()));
				}
				else
				{
					mesa.setIcon(mesaNormal);
					mesa.setForeground(Color.BLACK);
					mesa.legendaBotao = Configuracao.INSTANCE.getTipoNome() + " " + (i+1);
					mesa.setToolTipText(null);
				}
				
				gbc.gridx = colunas;
				gbc.gridy = linhas;
				
				if(data != null)
					vd.setData(data);
				else
					vd.setData(new Date());
				
				mesasPainel.add(mesa, gbc);
				vendaMesas.add(vd);
				
				quebra_linhas++;
				colunas++;
				
				if(quebra_linhas >= 7)
				{
					colunas = 0;
					linhas++;
					quebra_linhas = 0;
				}
			} catch (NumberFormatException | ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				new PainelErro(e);
				System.exit(0);
			}			
		}
		
		mesasPainel.revalidate();
		mesasPainel.repaint();
	}
	
	public CacheTodasMesas getTodasMesas()
	{
		CacheTodasMesas tm = new CacheTodasMesas(vendaMesas);
		return tm;
	}
	
	public void atualizarTodasMesas(CacheTodasMesas tm)
	{
		vendaMesas = tm.getTodasMesas();
		mesasPainel.removeAll();
		
		GridBagConstraints gbc = new GridBagConstraints();
		int colunas = 0;
		int linhas  = 0;
		int quebra_linhas = 0;
		
		gbc.fill 			= GridBagConstraints.BOTH;
		gbc.weightx 		= 1.0;
		gbc.weighty 		= 1.0;
		gbc.insets = new Insets(7,7,7,7);  //top padding
		
		for(int i = 0; i < vendaMesas.size(); i++)
		{	
			BotaoMesa mesa = new BotaoMesa(Configuracao.INSTANCE.getTipoNome() + " " + (i+1));
			mesa.idMesa = i;
			mesa.setFont(new Font("Verdana", Font.PLAIN, 12));
			mesa.setPreferredSize(new Dimension(100, 60));
			mesa.setHorizontalTextPosition(AbstractButton.CENTER);
			mesa.setVerticalTextPosition(AbstractButton.BOTTOM);
			mesa.setRolloverDecoratedOnly(true);
			mesa.addMouseListener(pegaMouse);
			
			if(vendaMesas.get(i).getQuantidadeProdutos() > 0)
			{
				vendaMesas.get(i).calculaTotal();
				mesa.setIcon(mesaOcupada);
				mesa.setForeground(new Color(191, 93, 12));
				mesa.legendaBotao = Configuracao.INSTANCE.getTipoNome() + " " + (i+1) + " - total: R$" + String.format("%.2f", vendaMesas.get(i).getTotal());
				mesa.setToolTipText("R$" + String.format("%.2f", vendaMesas.get(i).getTotal()));				
			}
			else
			{
				mesa.setIcon(mesaNormal);
				mesa.setForeground(Color.BLACK);
				mesa.legendaBotao = Configuracao.INSTANCE.getTipoNome() + " " + (i+1);
				mesa.setToolTipText(null);
			}
			
			gbc.gridx = colunas;
			gbc.gridy = linhas;			
			
			mesasPainel.add(mesa, gbc);
			
			quebra_linhas++;
			colunas++;
			
			if(quebra_linhas >= 7)
			{
				colunas = 0;
				linhas++;
				quebra_linhas = 0;
			}
		}
		
		mesasPainel.revalidate();
		mesasPainel.repaint();		
	}
	
	private class TheListener implements MouseListener
	{
		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			BotaoMesa x = (BotaoMesa)e.getSource();
			PainelPrincipal.getInstance().AbrirMesa(x.idMesa, vendaMesas.get(x.idMesa), true);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			BotaoMesa x = (BotaoMesa)e.getSource();
			PainelLegenda.getInstance().AtualizaLegenda(x.legendaBotao);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			PainelLegenda.getInstance().AtualizaLegenda("Desenvolvido por CodeCoffe (C) - 2014");
		}
	}
	
	private class BotaoMesa extends WebButton
	{
		private static final long serialVersionUID = 1L;
		public String legendaBotao;
		public int idMesa;
		
	    public BotaoMesa(String txt) {
	        super(txt);
	    }
	}
	
	public Venda getVendaMesa(int mesa)
	{
		return vendaMesas.get(mesa);
	}
	
	public void atualizaMesa(int mesa, Venda v)
	{
		vendaMesas.set(mesa, v);
		if(vendaMesas.get(mesa).getQuantidadeProdutos() > 0)
		{
			vendaMesas.get(mesa).calculaTotal();
			((BotaoMesa) mesasPainel.getComponent(mesa)).setIcon(mesaOcupada);
			((BotaoMesa) mesasPainel.getComponent(mesa)).setForeground(new Color(191, 93, 12));
			((BotaoMesa) mesasPainel.getComponent(mesa)).legendaBotao = Configuracao.INSTANCE.getTipoNome() + " " + (mesa+1) 
							+ " - total: R$" + String.format("%.2f", vendaMesas.get(mesa).getTotal());
			((BotaoMesa) mesasPainel.getComponent(mesa)).setToolTipText("R$" + String.format("%.2f", vendaMesas.get(mesa).getTotal()));
		}
		else
		{
			((BotaoMesa) mesasPainel.getComponent(mesa)).setIcon(mesaNormal);
			((BotaoMesa) mesasPainel.getComponent(mesa)).setForeground(Color.BLACK);
			((BotaoMesa) mesasPainel.getComponent(mesa)).legendaBotao = Configuracao.INSTANCE.getTipoNome() + " " + (mesa+1);
			((BotaoMesa) mesasPainel.getComponent(mesa)).setToolTipText(null);
		}
	}	
	
	public void atualizaMesaCache(int mesa, Venda v)
	{
		vendaMesas.set(mesa, v);
		if(vendaMesas.get(mesa).getQuantidadeProdutos() > 0)
		{
			vendaMesas.get(mesa).calculaTotal();			
			((BotaoMesa) mesasPainel.getComponent(mesa)).setIcon(mesaOcupada);
			((BotaoMesa) mesasPainel.getComponent(mesa)).setForeground(new Color(191, 93, 12));
			((BotaoMesa) mesasPainel.getComponent(mesa)).legendaBotao = Configuracao.INSTANCE.getTipoNome() + " " + (mesa+1) 
						+ " - total: R$" + String.format("%.2f", vendaMesas.get(mesa).getTotal());
			((BotaoMesa) mesasPainel.getComponent(mesa)).setToolTipText("R$" + String.format("%.2f", vendaMesas.get(mesa).getTotal()));
		}
		else
		{
			((BotaoMesa) mesasPainel.getComponent(mesa)).setIcon(mesaNormal);
			((BotaoMesa) mesasPainel.getComponent(mesa)).setForeground(Color.BLACK);
			((BotaoMesa) mesasPainel.getComponent(mesa)).legendaBotao = Configuracao.INSTANCE.getTipoNome() + " " + (mesa+1);
			((BotaoMesa) mesasPainel.getComponent(mesa)).setToolTipText(null);
		}
		
		if(Usuario.INSTANCE.getOlhando() == mesa)
			PainelPrincipal.getInstance().AbrirMesa(mesa, vendaMesas.get(mesa), true);
	}
	
	public void verMesa(int mesaid) {
		PainelPrincipal.getInstance().AbrirMesa(mesaid, vendaMesas.get(mesaid), false);
	}
}
