package codecoffe.restaurantes.sockets;
import java.io.Serializable;

import codecoffe.restaurantes.primitivas.Venda;

public class CacheVendaFeita implements Serializable 
{
	public Venda vendaFeita, vendaTotal;	// nem fudendo que vo fazer setter e getter pra tudo isso.
	public CacheMesaHeader vendaMesa;
	public String total, atendente, horario, forma_pagamento, valor_pago, troco, delivery, dezporcento;
	public int ano, mes, dia_mes, dia_semana, fiado_id, caixa, classe;
	public boolean imprimir = false;
	
	public CacheVendaFeita(Venda v)
	{
		this.vendaFeita = v;
	}
	
	public CacheVendaFeita(Venda v, CacheMesaHeader m)
	{
		this.vendaFeita = v;
		this.vendaMesa = m;
	}
	
	public CacheVendaFeita(Venda v, Venda vt, CacheMesaHeader m)
	{
		this.vendaFeita = v;
		this.vendaTotal = vt;
		this.vendaMesa = m;
	}		
}
