package codecoffe.restaurantes.utilitarios;

import java.text.Normalizer;

import codecoffe.restaurantes.primitivas.Produto;
import codecoffe.restaurantes.primitivas.ProdutoVenda;

public abstract class UtilCoffe
{
	public static final String VERSAO = "v2.00";
	
	public static final int SERVER = 1;
	public static final int CLIENT = 2;
	
	public static final int PEDIDO_NORMAL = 1;
	public static final int PEDIDO_FAZENDO = 2;
	public static final int PEDIDO_REMOVER = 3;
	public static final int PEDIDO_NOVO = 4;
	public static final int PEDIDO_DELETADO = 5;
	public static final int PEDIDO_EDITAR = 6;
	
	public static final int PEDIDO_ADICIONA = 1;
	public static final int PEDIDO_DELETA = 2;
	public static final int PEDIDO_STATUS = 3;
	public static final int PEDIDO_EDITADO = 4;
	
	public static final int CLASSE_VENDA_MESA = 1;
	public static final int CLASSE_VENDA_RAPIDA = 2;
	public static final int CLASSE_CLIENTES = 3;
	
	public static final int MESA_ADICIONAR = 1;
	public static final int MESA_ATUALIZAR = 2;
	public static final int MESA_ATUALIZAR2 = 3;
	public static final int MESA_DELETAR = 4;
	public static final int MESA_LIMPAR = 5;
	public static final int MESA_ERROR = 6;
	
	public static final int CLIENTE_ADICIONAR = 1;
	public static final int CLIENTE_EDITAR = 2;
	public static final int CLIENTE_REMOVER = 3;
	public static final int CLIENTE_ATUALIZAR = 4;
	
	public static String limpaNumero(String campo)
	{
		String limpeza = campo.replaceAll("[^0-9]+","");
		if(!"".equals(limpeza.trim()))
		{
			return limpeza;
		}
		return "";
	}
	
	public static String limpaNumeroDecimal(String campo)
	{
		String limpeza = campo.replaceAll("[^0-9],.+","");
		if(!"".equals(limpeza.trim()))
		{
			return limpeza;
		}
		return "";
	}
	
	public static boolean vaziu(String texto)
	{
		if(texto == null)
			return true;
		
		if("".equals(texto.trim()))
			return true;
		
		return false;
	}
	
	public static double precoToDouble(String preco)
	{
		double doub = Double.parseDouble(preco.replaceAll(",", "."));
		return doub;
	}
	
	public static String doubleToPreco(double doub)
	{
		String pegaPreco = String.format("%.2f", doub);
		pegaPreco.replaceAll(",", ".");
		return pegaPreco;
	}
	
	public static String removeAcentos(String str) 
	{
		str = Normalizer.normalize(str, Normalizer.Form.NFD);
		str = str.replaceAll("[^\\p{ASCII}]", "");
		return str;
	}
	
	public static boolean isNumeric(String str)
	{
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}
	
	public static ProdutoVenda cloneProdutoVenda(ProdutoVenda p)
	{
		ProdutoVenda prod = new ProdutoVenda(p.getNome(), p.getReferencia(), p.getPreco(), 
				p.getIdUnico(), p.getCodigo(), p.getAdicionaisList(), p.getQuantidade(), 
				p.getPagos(), p.getTotalProduto(), p.getComentario());
		
		prod.calcularPreco();
		return prod;
	}
	
	public static Produto cloneProduto(Produto p)
	{
		return new Produto(p.getNome(), p.getReferencia(), p.getPreco(), 
				p.getIdUnico(), p.getCodigo());
	}
	
	/*public static void printaMemoria()
	{
		Runtime runtime = Runtime.getRuntime();
	    NumberFormat format = NumberFormat.getInstance();

	    StringBuilder sb = new StringBuilder();
	    long maxMemory = runtime.maxMemory();
	    long allocatedMemory = runtime.totalMemory();
	    long freeMemory = runtime.freeMemory();
	    
	    sb.append("##################################################################################\n");
	    sb.append("free memory: " + format.format(freeMemory / 1024) + "\n");
	    sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
	    sb.append("max memory: " + format.format(maxMemory / 1024) + "\n");
	    sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");
	    sb.append("##################################################################################\n");
	    
	    System.out.println(sb);
	}*/
}