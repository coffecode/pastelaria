import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.event.*;

public class PainelMesas extends JPanel implements MouseListener
{
	private static int qntdmesas;
	private JPanel mesasPainel;
	private static BotaoMesa[] mesas;
	
	PainelMesas(int qntd)
	{
		qntdmesas = qntd;
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
	
		mesas = new BotaoMesa[qntdmesas];

		for(int i = 0; i < qntdmesas; i++)
		{
			String nomeMesa = "Mesa ";
			mesas[i] = new BotaoMesa(nomeMesa + (i+1));
			mesas[i].setPreferredSize(new Dimension(100, 52));
			mesas[i].setHorizontalTextPosition(AbstractButton.CENTER);
			mesas[i].setVerticalTextPosition(AbstractButton.BOTTOM);
			mesas[i].addMouseListener(this);

			Query pega = new Query();
			pega.executaQuery("SELECT mesa_id FROM produtosMesa WHERE mesa_id = "+ (i+1) +";");
			
			if(pega.next()){
				mesas[i].setIcon(new ImageIcon(getClass().getResource("imgs/mesa_ocupada.png")));
				mesas[i].setFont(new Font("Verdana", Font.PLAIN, 12));
				mesas[i].setForeground(new Color(191, 93, 12));
				
				Venda recibo = new Venda();
				Query pega3 = new Query();
				pega3.executaQuery("SELECT * FROM produtosMesa WHERE mesa_id = "+ (i+1) +";");		
				
				while(pega3.next())
				{
					String nomeProduto = pega3.getString("produto");
					String adicionais = pega3.getString("adicionais");
					String[] nome = adicionais.split(", ");
					
					int qntd2 = pega3.getInt("qntd");
					Produto p = new Produto();
					
					Query pega1 = new Query();
					pega1.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + nomeProduto + "';");
					
					if(pega1.next())
					{
						double precoProduto = Double.parseDouble(pega1.getString("preco").replaceAll(",", "."));
						
						p.setNome(nomeProduto);
						p.setPreco(precoProduto);
						
						if(nome.length > 0)
						{
							for(int z = 0 ; z < nome.length ; z++)
							{
								Query pega2 = new Query();
								pega2.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + nome[z] + "' AND `tipo` = 2;");
								
								if(pega2.next())
								{
									double pAdicional = Double.parseDouble(pega2.getString("preco").replaceAll(",", "."));
									
									Adicionais adcional = new Adicionais();
									adcional.nomeAdicional = nome[z];
									adcional.precoAdicional = pAdicional;
									
									p.adicionrAdc(adcional);
								}
								pega2.fechaConexao();
							}
						}
						if(qntd2 > 0)
							for(int z = 0; z < qntd2 ; z++)
								recibo.adicionarProduto(p);

					}	
					pega1.fechaConexao();
					//x++;
				}
				
				pega3.fechaConexao();
				recibo.calculaTotal();
				
				mesas[i].legendaBotao = nomeMesa + (i+1) + " - Total: R$" + String.format("%.2f", recibo.getTotal());// coloque a legenda aqui
			}else{
				mesas[i].setIcon(new ImageIcon(getClass().getResource("imgs/mesa.png")));
				mesas[i].setFont(new Font("Verdana", Font.PLAIN, 12));
				mesas[i].setForeground(Color.BLACK);
				mesas[i].legendaBotao = nomeMesa + (i+1);
			}
			
			pega.fechaConexao();
			
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
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		add(scroll);
	}
	
	private class BotaoMesa extends JButton
	{
		public String legendaBotao;
		
	    public BotaoMesa(String txt) {
	        super(txt);
	    }
	}
	
	static public void refresh()
	{
		for(int i = 0; i < qntdmesas; i++)
		{
			Query pega = new Query();
			pega.executaQuery("SELECT mesa_id FROM produtosMesa WHERE mesa_id = "+ (i+1) +";");
			
			if(pega.next()){
				mesas[i].setIcon(new ImageIcon(mesas[i].getClass().getResource("imgs/mesa_ocupada.png")));
				mesas[i].setFont(new Font("Verdana", Font.PLAIN, 12));
				mesas[i].setForeground(new Color(191, 93, 12));

				Query pega9 = new Query();
				pega9.executaQuery("SELECT mesa_id FROM produtosMesa WHERE mesa_id = "+ (i+1) +";");
				
				if(pega9.next()){
					mesas[i].setIcon(new ImageIcon(mesas[i].getClass().getResource("imgs/mesa_ocupada.png")));
					mesas[i].setFont(new Font("Verdana", Font.PLAIN, 12));
					mesas[i].setForeground(new Color(191, 93, 12));
					
					Venda recibo = new Venda();
					Query pega3 = new Query();
					pega3.executaQuery("SELECT * FROM produtosMesa WHERE mesa_id = "+ (i+1) +";");		
					
					while(pega3.next())
					{
						String nomeProduto = pega3.getString("produto");
						String adicionais = pega3.getString("adicionais");
						String[] nome = adicionais.split(", ");
						
						int qntd2 = pega3.getInt("qntd");
						Produto p = new Produto();
						
						Query pega1 = new Query();
						pega1.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + nomeProduto + "';");
						
						if(pega1.next())
						{
							double precoProduto = Double.parseDouble(pega1.getString("preco").replaceAll(",", "."));
							
							p.setNome(nomeProduto);
							p.setPreco(precoProduto);
							
							if(nome.length > 0)
							{
								for(int z = 0 ; z < nome.length ; z++)
								{
									Query pega2 = new Query();
									pega2.executaQuery("SELECT preco FROM produtos WHERE `nome` = '" + nome[z] + "' AND `tipo` = 2;");
									
									if(pega2.next())
									{
										double pAdicional = Double.parseDouble(pega2.getString("preco").replaceAll(",", "."));
										
										Adicionais adcional = new Adicionais();
										adcional.nomeAdicional = nome[z];
										adcional.precoAdicional = pAdicional;
										
										p.adicionrAdc(adcional);
									}
									pega2.fechaConexao();
								}
							}
							if(qntd2 > 0)
								for(int z = 0; z < qntd2 ; z++)
									recibo.adicionarProduto(p);

						}	
						pega1.fechaConexao();
						//x++;
					}
					
					pega3.fechaConexao();
					recibo.calculaTotal();
					
					mesas[i].legendaBotao = "Mesa " + (i+1) + " - Total: R$" + String.format("%.2f", recibo.getTotal());
				
				pega9.fechaConexao();
			}
				
			}else{
				mesas[i].setIcon(new ImageIcon(mesas[i].getClass().getResource("imgs/mesa.png")));
				mesas[i].setFont(new Font("Verdana", Font.PLAIN, 12));
				mesas[i].setForeground(Color.BLACK);
				mesas[i].legendaBotao = "Mesa " + (i+1);
			}
			
			pega.fechaConexao();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		for(int i = 0; i < qntdmesas; i++)
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
		for(int i = 0; i < qntdmesas; i++)
		{
			if(e.getSource() == mesas[i])
			{
				PainelLegenda.AtualizaLegenda(mesas[i].legendaBotao);
				break;
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		PainelLegenda.AtualizaLegenda("Desenvolvido por CodeCoffe (C) - 2013");
	}
}
