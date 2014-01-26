package codecoffe.restaurantes.utilitarios;
public abstract class UtilCoffe
{
	public static final String VERSAO = "v1.50";
	
	public static final int SERVER = 1;
	public static final int CLIENT = 2;
	
	public static final int PEDIDO_NORMAL = 1;
	public static final int PEDIDO_FAZENDO = 2;
	public static final int PEDIDO_REMOVER = 3;
	
	public static final int PEDIDO_ADICIONA = 1;
	public static final int PEDIDO_DELETA = 2;
	public static final int PEDIDO_STATUS = 3;
	
	public static final int VENDA_MESA = 1;
	public static final int VENDA_RAPIDA = 2;
	
	public static final int MESA_ADICIONAR = 1;
	public static final int MESA_ATUALIZAR = 2;
	public static final int MESA_ATUALIZAR2 = 3;
	public static final int MESA_DELETAR = 4;
	public static final int MESA_LIMPAR = 5;
	
	public static String limpaNumero(String campo)
	{
		String limpeza = campo.replaceAll("[^0-9]+","");
		if(!"".equals(limpeza.trim()))
		{
			return limpeza;
		}
		return "";
	}
	
	public static boolean vaziu(String texto)
	{
		if("".equals(texto.trim()) || texto == null)
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
}