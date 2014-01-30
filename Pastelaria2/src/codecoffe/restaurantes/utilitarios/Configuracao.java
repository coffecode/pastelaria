package codecoffe.restaurantes.utilitarios;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import codecoffe.restaurantes.interfaceGrafica.PainelErro;
import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.sockets.CacheConfiguracoes;

public enum Configuracao {
	INSTANCE;
	
	private int qntdMesas, modo, backupAutoIntervalo;
	private double taxaEntrega;
	private String nomeRest, caminhoBackupAuto;
	private Date ultimoBackup;
	private boolean dezPorcento, reciboFim, backupAuto;
	
	public void atualizarConfiguracao()
	{		
		try {
			Query pega = new Query();
			pega.executaQuery("SELECT * FROM opcoes");
			if(pega.next())
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy__HH_mm");
				
				nomeRest = pega.getString("restaurante");
				qntdMesas = pega.getInt("mesas");
				
				try {
					ultimoBackup = formatter.parse(pega.getString("ultimobackup"));
				} catch (ParseException e) {
					e.printStackTrace();
					new PainelErro(e);
					ultimoBackup = new Date();
				}
				finally
				{
					backupAutoIntervalo = pega.getInt("intervalobackupauto");
					caminhoBackupAuto = pega.getString("caminhobackupauto");
					
					if(pega.getInt("backupauto") == 1)
						backupAuto = true;
					else
						backupAuto = false;			
					
					if(pega.getInt("dezporcento") == 1)
						dezPorcento = true;
					else
						dezPorcento = false;
					
					if(pega.getInt("recibofim") == 1)
						reciboFim = true;
					else
						reciboFim = false;
					
					taxaEntrega = UtilCoffe.precoToDouble(pega.getString("taxaentrega"));
					
					pega.fechaConexao();
				}
			}
			else	// não carregou nada do banco? quitar
			{
				System.exit(0);
			}
			
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
			new PainelErro(e1);
			System.exit(0);
		}		
	}
	
	public void atualizarConfiguracao(CacheConfiguracoes cc)
	{
		nomeRest = cc.getNome();
		dezPorcento = cc.getDezPorcento();
		reciboFim = cc.getReciboFim();
		taxaEntrega = cc.getTaxa();		
	}
	
	public CacheConfiguracoes gerarCache()
	{
		CacheConfiguracoes cc = new CacheConfiguracoes(nomeRest, dezPorcento, reciboFim, taxaEntrega);
		return cc;
	}
	
	public void setModo(int md)
	{
		modo = md;
	}
	
	public void setBackupAutoCaminho(String caminho)
	{
		caminhoBackupAuto = caminho;
	}
	
	public void setUltimoBackup(Date quando)
	{
		ultimoBackup = quando;
	}
	
	public void setBackupAuto(boolean set)
	{
		backupAuto = set;
	}
	
	public void setBackupAutoIntervalo(int intervalo)
	{
		backupAutoIntervalo = intervalo;
	}
	
	public void setRestaurante(String texto)
	{
		nomeRest = texto;
	}
	
	public void setMesas(int qtd)
	{
		qntdMesas = qtd;
	}
	
	public void setReciboFim(boolean set)
	{
		reciboFim = set;
	}
	
	public void setDezPorcento(boolean set)
	{
		dezPorcento = set;
	}
	
	public void setTaxaEntrega(double quantia)
	{
		taxaEntrega = quantia;
	}
	
	public String getRestaurante()
	{
		return nomeRest;
	}
	
	public int getMesas()
	{
		return qntdMesas;
	}
	
	public boolean getReciboFim()
	{
		return reciboFim;
	}
	
	public boolean getDezPorcento()
	{
		return dezPorcento;
	}
	
	public double getTaxaEntrega()
	{
		return taxaEntrega;
	}
	
	public int getModo()
	{
		return modo;
	}
	
	public String getBackupAutoCaminho()
	{
		return caminhoBackupAuto;
	}
	
	public Date getUltimoBackup()
	{
		return ultimoBackup;
	}
	
	public boolean getBackupAuto()
	{
		return backupAuto;
	}
	
	public int getBackupAutoIntervalo()
	{
		return backupAutoIntervalo;
	}	
}