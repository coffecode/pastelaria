package codecoffe.restaurantes.sockets;
import java.io.Serializable;

public class CacheAutentica implements Serializable
{
	public String username, password, nome;
	public int header, level;
	
	public CacheAutentica(String u, String p)
	{
		this.username = u;
		this.password = p;
	}
}
