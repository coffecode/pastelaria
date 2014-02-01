package codecoffe.restaurantes.utilitarios;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.JOptionPane;

import codecoffe.restaurantes.interfaceGrafica.PainelClientes;
import codecoffe.restaurantes.interfaceGrafica.PainelCozinha;
import codecoffe.restaurantes.interfaceGrafica.PainelErro;
import codecoffe.restaurantes.interfaceGrafica.PainelMesas;
import codecoffe.restaurantes.interfaceGrafica.PainelVendaMesa;
import codecoffe.restaurantes.interfaceGrafica.PainelVendaRapida;
import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.primitivas.Pedido;
import codecoffe.restaurantes.sockets.CacheAviso;
import codecoffe.restaurantes.sockets.CacheClientes;
import codecoffe.restaurantes.sockets.CacheMesaHeader;
import codecoffe.restaurantes.sockets.CacheVendaFeita;
import codecoffe.restaurantes.sockets.Client;
import codecoffe.restaurantes.sockets.Server;

/*CLASSES
 * 1 = Painel Venda Mesa
 * 2 = Painel Venda Rapida
 * 
 * 
 * 
 * 
 */

public enum Bartender
{
	INSTANCE;
	
	public boolean enviarCliente(CacheClientes cc)
	{
		if(Configuracao.INSTANCE.getModo() > UtilCoffe.SERVER)
		{
			System.out.println("Enviando cliente para o Servidor.");
			Client.getInstance().enviarObjeto(cc);			
		}
		else
		{
			if(cc.getHeader() == UtilCoffe.CLIENTE_ADICIONAR)
			{
				try {
					Query envia = new Query();
					envia.executaUpdate("INSERT INTO fiados(nome) VALUES('Novo Cliente');");
					envia.executaQuery("SELECT fiador_id FROM fiados ORDER BY fiador_id DESC limit 0, 1");
					
					if(envia.next())
						cc.getListaClientes().get(0).setIdUnico(envia.getInt("fiador_id"));
					
					envia.fechaConexao();
					DiarioLog.add(cc.getAtendente(), "Cadastrou um novo cliente.", 5);	
					
					PainelClientes.getInstance().adicionarCliente(cc);
					Server.getInstance().enviaTodos(new CacheClientes(cc.getListaClientes().get(0), UtilCoffe.CLIENTE_ADICIONAR, cc.getAtendente()));
					return true;
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					new PainelErro(e);
					return false;
				}
			}
			else if(cc.getHeader() == UtilCoffe.CLIENTE_REMOVER)
			{
				try {
					Query envia = new Query();
					String formatacao = "DELETE FROM fiados WHERE `fiador_id` = " + cc.getListaClientes().get(0).getIdUnico() + ";";  
					envia.executaUpdate(formatacao);
					envia.fechaConexao();
					
					DiarioLog.add(cc.getAtendente(), "Deletou o cliente " + cc.getListaClientes().get(0).getNome() 
							+ ". Telefone: " + cc.getListaClientes().get(0).getTelefone() + ".", 5);
					
					PainelClientes.getInstance().removerClientes(cc);
					Server.getInstance().enviaTodos(new CacheClientes(cc.getListaClientes().get(0), UtilCoffe.CLIENTE_REMOVER, cc.getAtendente()));
					return true;
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
					new PainelErro(e1);
					return false;
				}	
			}
			else if(cc.getHeader() == UtilCoffe.CLIENTE_EDITAR)
			{				
				Query manda = new Query();
				String formata = "UPDATE fiados SET ";

				formata += "`nome` = '" + cc.getListaClientes().get(0).getNome() + "', ";
				formata += "`apelido` = '" + cc.getListaClientes().get(0).getApelido() + "', ";
				formata += "`telefone` = '" + cc.getListaClientes().get(0).getTelefone() + "', ";
				formata += "`cpf` = '" + cc.getListaClientes().get(0).getCpf() + "', ";
				formata += "`cep` = '" + cc.getListaClientes().get(0).getCep() + "', ";
				formata += "`endereco` = '" + cc.getListaClientes().get(0).getEndereco() + "', ";
				formata += "`numero` = '" + cc.getListaClientes().get(0).getNumero() + "', ";
				formata += "`bairro` = '" + cc.getListaClientes().get(0).getBairro() + "', ";
				formata += "`complemento` = '" + cc.getListaClientes().get(0).getComplemento() 
						+ "' WHERE `fiador_id` = " + cc.getListaClientes().get(0).getIdUnico();
				try {
					manda.executaUpdate(formata);				
					manda.fechaConexao();
					DiarioLog.add(cc.getAtendente(), "Atualizou o cliente: " + cc.getListaClientes().get(0).getNome() 
							+ ". Telefone: " + cc.getListaClientes().get(0).getTelefone() + ".", 5);
					
					PainelClientes.getInstance().editarClientes(cc);
					Server.getInstance().enviaTodos(new CacheClientes(cc.getListaClientes().get(0), UtilCoffe.CLIENTE_EDITAR, cc.getAtendente()));
					return true;
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
					new PainelErro(e1);
					return false;
				}
			}
		}
		return false;
	}
	
	public void enviarMesa(CacheMesaHeader m, int usuario)
	{
		if(Configuracao.INSTANCE.getModo() > UtilCoffe.SERVER)
		{
			System.out.println("Enviando mesa para o Servidor.");
			Client.getInstance().enviarObjeto(m);
		}
		else
		{
			if(m.getHeader() == UtilCoffe.MESA_ADICIONAR)
			{
				System.out.println("Atualizando mesa no banco de dados e clientes. (1)");
				try {
					String formatacao;
					Query envia = new Query();
					formatacao = "INSERT INTO mesas(mesas_id, produto, quantidade, pago, adicionais) VALUES("
					+ m.getMesaId() + ", '" + m.getProdutoMesa().getNome() + "', " + m.getHeaderExtra() + ", 0, '" + m.getProdutoMesa().getAllAdicionais() + "');";
					envia.executaUpdate(formatacao);
					envia.fechaConexao();
					
					if(usuario != 0)
						PainelMesas.getInstance().atualizaMesaCache(m.getMesaId(), m.getMesaVenda());
					
					Server.getInstance().enviaTodos(m, usuario);
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					
					Server.getInstance().enviaObjeto(new CacheMesaHeader(m.getMesaId(), 
							PainelMesas.getInstance().getVendaMesa(m.getMesaId()), UtilCoffe.MESA_ERROR), usuario);
					
					new PainelErro(e);
				}
			}
			else if(m.getHeader() == UtilCoffe.MESA_ATUALIZAR)
			{
				System.out.println("Atualizando mesa no banco de dados e clientes. (2)");
				try {
					String formatacao;
					Query envia = new Query();
					formatacao = "UPDATE mesas SET `quantidade` = (`quantidade` + " + m.getHeaderExtra() + ") WHERE `mesas_id` = " + m.getMesaId()
					+ " AND `produto` = '" + m.getProdutoMesa().getNome() + "' AND `adicionais` = '" + m.getProdutoMesa().getAllAdicionais() + "';";						
					envia.executaUpdate(formatacao);
					envia.fechaConexao();
					
					if(usuario != 0)
						PainelMesas.getInstance().atualizaMesaCache(m.getMesaId(), m.getMesaVenda());
					
					Server.getInstance().enviaTodos(m, usuario);
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					
					Server.getInstance().enviaObjeto(new CacheMesaHeader(m.getMesaId(), 
							PainelMesas.getInstance().getVendaMesa(m.getMesaId()), UtilCoffe.MESA_ERROR), usuario);
					
					new PainelErro(e);
				}
			}
			else if(m.getHeader() == UtilCoffe.MESA_ATUALIZAR2)
			{
				System.out.println("Atualizando mesa no banco de dados e clientes. (3)");
				if(usuario != 0)
					PainelMesas.getInstance().atualizaMesaCache(m.getMesaId(), m.getMesaVenda());
				
				Server.getInstance().enviaTodos(m, usuario);
			}
			else if(m.getHeader() == UtilCoffe.MESA_LIMPAR || m.getHeader() == UtilCoffe.MESA_DELETAR)
			{
				System.out.println("Atualizando mesa no banco de dados e clientes. (4)");
				boolean termina = false;
				try {
					Query pega = new Query();
					if(m.getHeader() == UtilCoffe.MESA_DELETAR)
					      pega.executaUpdate("DELETE FROM mesas WHERE `produto` = '" + m.getProdutoMesa().getNome() 
					    		  + "' AND `adicionais` = '" 
					    		  + m.getProdutoMesa().getAllAdicionais() 
					    		  + "' AND `mesas_id` = " + m.getMesaId() + ";");
					
					pega.executaQuery("SELECT * FROM mesas WHERE `quantidade` != `pago` AND `mesas_id` = "+ m.getMesaId() +";");
					if(!pega.next())
					{
						pega.executaUpdate("DELETE FROM mesas WHERE `mesas_id` = "+ m.getMesaId() +";");
						m.getMesaVenda().clear();
						termina = true;
					}
					
					pega.fechaConexao();
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					
					Server.getInstance().enviaObjeto(new CacheMesaHeader(m.getMesaId(), 
							PainelMesas.getInstance().getVendaMesa(m.getMesaId()), UtilCoffe.MESA_ERROR), usuario);
					
					new PainelErro(e);
				} finally {
					if(m.getHeader() == UtilCoffe.MESA_LIMPAR)
					{
						if(termina)
						{
							PainelMesas.getInstance().atualizaMesaCache(m.getMesaId(), m.getMesaVenda());
							Server.getInstance().enviaTodos(m);	
						}					
					}
					else if(m.getHeader() == UtilCoffe.MESA_DELETAR)
					{
						if(!termina)
						{
							if(usuario != 0)
								PainelMesas.getInstance().atualizaMesaCache(m.getMesaId(), m.getMesaVenda());
							
							Server.getInstance().enviaTodos(m, usuario);	
						}
						else
						{
							PainelMesas.getInstance().atualizaMesaCache(m.getMesaId(), m.getMesaVenda());
							Server.getInstance().enviaTodos(m);
						}	
					}
				}
			}
		}
	}	
	
	public void enviarPedido(Pedido p)
	{
		if(Configuracao.INSTANCE.getModo() > UtilCoffe.SERVER)
		{
			System.out.println("Enviando pedido para o Servidor.");
			Client.getInstance().enviarObjeto(p);
		}
		else
		{
			System.out.println("Pedido enviado para Cozinha.");
			
			if(p.getHeader() == UtilCoffe.PEDIDO_ADICIONA)
				p.setHora(new Date());
			
			p.setUltimaEdicao(new Date());
			PainelCozinha.getInstance().atualizaPedido(p);
			Server.getInstance().enviaTodos(p);
		}
	}
	
	public boolean criarImpressao(CacheVendaFeita v)
	{
		if(Configuracao.INSTANCE.getModo() > UtilCoffe.SERVER)
		{
			System.out.println("Enviando impress�o para o Servidor.");
			Client.getInstance().enviarObjeto(v);
			return false;
		}
		else
		{
		      try{
		          File arquivo = new File("codecoffe/recibo.txt");
		            
	              FileWriter arquivoTxt = new FileWriter(arquivo, false);
	              PrintWriter linhasTxt = new PrintWriter(arquivoTxt);
	              
	              String pegaPreco = "";
	              
		          linhasTxt.println("===========================================");
		          linhasTxt.println(String.format("              %s              ", Configuracao.INSTANCE.getRestaurante()));
		          linhasTxt.println("===========================================");
		          linhasTxt.println("*********** NAO TEM VALOR FISCAL **********");
		          linhasTxt.println("===========================================");
	              linhasTxt.println("PRODUTO              QTDE  VALOR UN.  VALOR");
	              
					for(int i = 0; i < v.vendaFeita.getQuantidadeProdutos(); i++)
					{
						linhasTxt.print(String.format("%-20.20s", v.vendaFeita.getProduto(i).getNome()));
						linhasTxt.print(String.format("%3s     ", v.vendaFeita.getProduto(i).getQuantidade()));
												
						pegaPreco = String.format("%.2f", v.vendaFeita.getProduto(i).getPreco());
						pegaPreco.replaceAll(",", ".");							
						
						linhasTxt.print(String.format("%5s    ", pegaPreco));
						
						pegaPreco = String.format("%.2f", (v.vendaFeita.getProduto(i).getPreco()*v.vendaFeita.getProduto(i).getQuantidade()));
						pegaPreco.replaceAll(",", ".");							
						
						linhasTxt.print(String.format("%6s    ", pegaPreco));
						linhasTxt.println();
						
						for(int j = 0; j < v.vendaFeita.getProduto(i).getTotalAdicionais(); j++)
						{
							linhasTxt.print(String.format("%-20.20s", "+" + v.vendaFeita.getProduto(i).getAdicional(j).nomeAdicional));
							linhasTxt.print(String.format("%3s     ", v.vendaFeita.getProduto(i).getQuantidade()));
							
							pegaPreco = String.format("%.2f", v.vendaFeita.getProduto(i).getAdicional(j).precoAdicional);
							pegaPreco.replaceAll(",", ".");							
							
							linhasTxt.print(String.format("%5s    ", pegaPreco));
							
							pegaPreco = String.format("%.2f", (v.vendaFeita.getProduto(i).getAdicional(j).precoAdicional*v.vendaFeita.getProduto(i).getQuantidade()));
							pegaPreco.replaceAll(",", ".");							
							
							linhasTxt.print(String.format("%6s    ", pegaPreco));
							linhasTxt.println();
						}
					}            
	              
	              linhasTxt.println("===========================================");
	              linhasTxt.println("   INFORMACOES PARA FECHAMENTO DE CONTA    ");
	              linhasTxt.println("===========================================");
	              
	              linhasTxt.print(String.format("%-18.18s", "Atendido por: "));
	              linhasTxt.println(v.atendente);
	              
	              Locale locale = new Locale("pt","BR"); 
	              GregorianCalendar calendar = new GregorianCalendar(); 
	              SimpleDateFormat formatador = new SimpleDateFormat("EEE, dd'/'MM'/'yyyy' - 'HH':'mm", locale);		                
	              
	              linhasTxt.print(String.format("%-18.18s", "Data: "));
	              linhasTxt.println(formatador.format(calendar.getTime()));
	              
	              if(UtilCoffe.precoToDouble(v.delivery) > 0 && v.fiado_id > 0)
	              {
	            	  try {
						linhasTxt.println();
						  Query pegaCliente = new Query();
						  pegaCliente.executaQuery("SELECT nome, telefone, endereco, numero, bairro, complemento FROM fiados WHERE fiador_id = " + v.fiado_id);
						  
						  if(pegaCliente.next())
						  {
							  linhasTxt.println(pegaCliente.getString("nome") + " - TEL: " + pegaCliente.getString("telefone"));
							  linhasTxt.println(pegaCliente.getString("endereco") + " - " + pegaCliente.getString("numero"));
							  linhasTxt.println(pegaCliente.getString("complemento"));      				
						  }
						 
						  pegaCliente.fechaConexao();
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
						new PainelErro(e);
					}
	              }              
		            
	              linhasTxt.println("===========================================");
	              
	              if(UtilCoffe.precoToDouble(v.delivery) > 0)
	            	  linhasTxt.println("Taxa de Entrega                  R$" + v.delivery + "\n");              
	              
	              linhasTxt.println("                     ----------------------");
	              linhasTxt.println("Total                            R$" + v.total);
	              linhasTxt.println("===========================================");
	              linhasTxt.println("       OBRIGADO E VOLTE SEMPRE!	          ");
	              linhasTxt.println("       POWERED BY CodeCoffe V1.5    		  ");
	              
	              int i = 0;
	              while(i < 10){
	                  i++;
	                  linhasTxt.println();
	              }
	              
	              arquivoTxt.close();
	              linhasTxt.close();
	              return true;
		      }
		      catch(IOException error)
		      {
		    	  JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + error.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
		          return false;
		      }			
		}
	}
	
	public int enviarVenda(CacheVendaFeita v, int usuario)
	{
		if(Configuracao.INSTANCE.getModo() > 1)
		{
			System.out.println("Enviando venda para o Servidor.");
			Client.getInstance().enviarObjeto(v);
			return 0;
		}
		else
		{
			if(v.classe == UtilCoffe.CLASSE_VENDA_RAPIDA)
			{
				System.out.println("Enviando venda r�pida para o Banco de Dados.");
				int venda_id = 0;
				
				try {
					String formatacao;
					Query envia = new Query();
					formatacao = "INSERT INTO vendas(total, atendente, ano, mes, dia_mes, dia_semana, horario, forma_pagamento, valor_pago, troco, fiado_id, caixa, delivery, dezporcento, data) VALUES('"
					+ v.total +
					"', '" + v.atendente +
					"', " + v.ano + ", "
					+ v.mes + ", "
					+ v.dia_mes + ", "
					+ v.dia_semana +
					", '" + v.horario + "', '" + v.forma_pagamento + "', '" + v.valor_pago + "', '" + v.troco + 
					"', " + v.fiado_id + ", 0, '" + v.delivery + "', '" + v.dezporcento + "', CURDATE());";
					envia.executaUpdate(formatacao);
					
					Query pega = new Query();
					pega.executaQuery("SELECT vendas_id FROM vendas ORDER BY vendas_id DESC");
					
					if(pega.next())
					{
						venda_id = pega.getInt("vendas_id");
						String pegaPreco = "";
						
						for(int i = 0; i < v.vendaFeita.getQuantidadeProdutos(); i++)
						{
							pegaPreco = String.format("%.2f", (v.vendaFeita.getProduto(i).getTotalProduto() * v.vendaFeita.getProduto(i).getQuantidade()));
							pegaPreco.replaceAll(",", ".");						
							
							formatacao = "INSERT INTO vendas_produtos(id_link, nome_produto, adicionais_produto, preco_produto, quantidade_produto, dia, mes, ano, data) VALUES('"
									+ venda_id +
									"', '" + v.vendaFeita.getProduto(i).getNome() +
									"', '" + v.vendaFeita.getProduto(i).getAllAdicionais() + "', '" + pegaPreco + "', '" + v.vendaFeita.getProduto(i).getQuantidade() +
									"', " + v.dia_mes + ", " + v.mes + ", " + v.ano + ", CURDATE());";
									envia.executaUpdate(formatacao);						
						}
					}
					
					envia.fechaConexao();
					
					if(venda_id > 0)
					{
						if(v.atendente.equals(Usuario.INSTANCE.getNome()))
						{
							CacheAviso aviso = new CacheAviso(1, v.classe, "A venda foi conclu�da com sucesso!", "Venda #" + venda_id);
							PainelVendaRapida.getInstance().receberAviso(aviso);			
						}
						
						DiarioLog.add(v.atendente, "Adicionou a Venda #" + venda_id + " de R$" + v.total + " (fiado).", 1);
						return venda_id;
					}
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					new PainelErro(e);
					return 0;
				}
				
				return 0;				
			}
			else	// venda feita na mesa tem frescuras a mais
			{
				System.out.println("Enviando venda mesa para o Banco de Dados.");
				int venda_id = 0;
				
				try {
					String formatacao;
					Query envia = new Query();
					formatacao = "INSERT INTO vendas(total, atendente, ano, mes, dia_mes, dia_semana, horario, forma_pagamento, valor_pago, troco, fiado_id, caixa, delivery, dezporcento, data) VALUES('"
					+ UtilCoffe.doubleToPreco((UtilCoffe.precoToDouble(v.total)-UtilCoffe.precoToDouble(v.dezporcento))) +
					"', '" + Usuario.INSTANCE.getNome() +
					"', " + v.ano + ", "
					+ v.mes + ", "
					+ v.dia_mes + ", "
					+ v.dia_semana +
					", '" + v.horario + "', '" + v.forma_pagamento+ "', '" + v.valor_pago + "', '" + v.troco
					+ "', " + v.fiado_id + ", " + (v.vendaMesa.getMesaId()+1) + ", '0,00', '" + v.dezporcento + "', CURDATE());";
					envia.executaUpdate(formatacao);				
					
					Query pega = new Query();
					pega.executaQuery("SELECT vendas_id FROM vendas ORDER BY vendas_id DESC limit 0, 1");
					
					if(pega.next())
					{
						venda_id = pega.getInt("vendas_id");
						String pegaPreco = "";
						
						for(int i = 0; i < v.vendaFeita.getQuantidadeProdutos(); i++)
						{
							formatacao = "UPDATE mesas SET `pago` = (`pago` + " + v.vendaFeita.getProduto(i).getQuantidade() + ") WHERE `mesas_id` = " + v.vendaMesa.getMesaId()
							+ " AND `produto` = '" + v.vendaFeita.getProduto(i).getNome() + "' AND `adicionais` = '" + v.vendaFeita.getProduto(i).getAllAdicionais() + "';";
							envia.executaUpdate(formatacao);
							
							pegaPreco = String.format("%.2f", (v.vendaFeita.getProduto(i).getPreco() * v.vendaFeita.getProduto(i).getQuantidade()));
							pegaPreco.replaceAll(",", ".");						
							
							formatacao = "INSERT INTO vendas_produtos(id_link, nome_produto, adicionais_produto, preco_produto, quantidade_produto, dia, mes, ano, data) VALUES('"
									+ venda_id +
									"', '" + v.vendaFeita.getProduto(i).getNome() +
									"', '" + v.vendaFeita.getProduto(i).getAllAdicionais() + "', '" + pegaPreco + "', '" + v.vendaFeita.getProduto(i).getQuantidade() + 
									"', " + v.dia_mes + ", " + v.mes + ", " + v.ano + ", CURDATE());";
									envia.executaUpdate(formatacao);						
						}
					}
					
					envia.fechaConexao();
					pega.fechaConexao();
					
					if(venda_id > 0)
					{
						if(v.atendente.equals(Usuario.INSTANCE.getNome()))
						{
							CacheAviso aviso = new CacheAviso(1, v.classe, "A venda foi conclu�da com sucesso!", "Venda #" + venda_id);
							PainelVendaMesa.getInstance().receberAviso(aviso);			
						}
						
						DiarioLog.add(v.atendente, "Adicionou a Venda #" + venda_id + " de R$" + v.total + " (fiado).", 1);
						enviarMesa(v.vendaMesa, usuario);
						return venda_id;
					}
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
					
					Server.getInstance().enviaObjeto(new CacheMesaHeader(v.vendaMesa.getMesaId(), 
							PainelMesas.getInstance().getVendaMesa(v.vendaMesa.getMesaId()), UtilCoffe.MESA_ERROR), usuario);
					
					new PainelErro(e);
					return 0;
				}				
				
				return 0;
			}
		}
	}
}
