package codecoffe.restaurantes.utilitarios;
public class Usuario {
	
	private static int level;
	private static String nome;
	private static int olhando = -1;
	
	public static void setNome(String texto)
	{
		nome = texto;
	}
	
	public static void setLevel(int lvl)
	{
		level = lvl;
	}
	
	public static void setOlhando(int mesa)
	{
		olhando = mesa;
	}
	
	public static String getNome()
	{
		return nome;
	}
	
	public static int getLevel()
	{
		return level;
	}
	
	public static int getOlhando()
	{
		return olhando;
	}	
}
