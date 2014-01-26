package codecoffe.restaurantes.utilitarios;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import codecoffe.restaurantes.interfaceGrafica.PainelErro;
import codecoffe.restaurantes.mysql.Query;
import codecoffe.restaurantes.sockets.CacheConfiguracoes;

public class Configuracao {
	
	private static int qntdMesas, modo, backupAutoIntervalo;
	private static double taxaEntrega;
	private static String nomeRest, caminhoBackupAuto;
	private static Date ultimoBackup;
	private static boolean dezPorcento, reciboFim, backupAuto;
	
	public Configuracao()
	{
		modo = UtilCoffe.SERVER;
		
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
	
	public Configuracao(CacheConfiguracoes cc)
	{
		modo = UtilCoffe.CLIENT;
		nomeRest = cc.getNome();
		dezPorcento = cc.getDezPorcento();
		reciboFim = cc.getReciboFim();
		taxaEntrega = cc.getTaxa();
	}	
	
	public static CacheConfiguracoes gerarCache()
	{
		CacheConfiguracoes cc = new CacheConfiguracoes(nomeRest, dezPorcento, reciboFim, taxaEntrega);
		return cc;
	}
	
	public static void setBackupAutoCaminho(String caminho)
	{
		caminhoBackupAuto = caminho;
	}
	
	public static void setUltimoBackup(Date quando)
	{
		ultimoBackup = quando;
	}
	
	public static void setBackupAuto(boolean set)
	{
		backupAuto = set;
	}
	
	public static void setBackupAutoIntervalo(int intervalo)
	{
		backupAutoIntervalo = intervalo;
	}
	
	public static void setRestaurante(String texto)
	{
		nomeRest = texto;
	}
	
	public static void setMesas(int qtd)
	{
		qntdMesas = qtd;
	}
	
	public static void setReciboFim(boolean set)
	{
		reciboFim = set;
	}
	
	public static void setDezPorcento(boolean set)
	{
		dezPorcento = set;
	}
	
	public static void setTaxaEntrega(double quantia)
	{
		taxaEntrega = quantia;
	}
	
	public static String getRestaurante()
	{
		return nomeRest;
	}
	
	public static int getMesas()
	{
		return qntdMesas;
	}
	
	public static boolean getReciboFim()
	{
		return reciboFim;
	}
	
	public static boolean getDezPorcento()
	{
		return dezPorcento;
	}
	
	public static double getTaxaEntrega()
	{
		return taxaEntrega;
	}
	
	public static int getModo()
	{
		return modo;
	}
	
	public static String getBackupAutoCaminho()
	{
		return caminhoBackupAuto;
	}
	
	public static Date getUltimoBackup()
	{
		return ultimoBackup;
	}
	
	public static boolean getBackupAuto()
	{
		return backupAuto;
	}
	
	public static int getBackupAutoIntervalo()
	{
		return backupAutoIntervalo;
	}	
}