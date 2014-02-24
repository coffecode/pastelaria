package codecoffe.restaurantes.primitivas;

import java.io.Serializable;

public class Funcionario implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int level;
	private String usuario, password, nome;
	
	public Funcionario(int level, String usuario, String password, String nome) {
		this.level = level;
		this.usuario = usuario;
		this.password = password;
		this.nome = nome;
	}
	
	public Funcionario() {}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}