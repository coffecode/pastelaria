package codecoffe.restaurantes.utilitarios;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JOptionPane;

import codecoffe.restaurantes.interfaceGrafica.PainelErro;

public class Recibo
{
	@SuppressWarnings("resource")
	static public void imprimir(int mesa)
	{
		try {
			String nomeRecibo = "codecoffe/recibo_";
			nomeRecibo += mesa;
			nomeRecibo += ".txt";

			java.io.InputStream is = new FileInputStream(nomeRecibo);
			Scanner sc = new Scanner(is);
			FileOutputStream fs = new FileOutputStream("LPT1:");
			PrintStream ps = new PrintStream(fs);

			while(sc.hasNextLine()){
				String linhas = sc.nextLine();
				ps.println(linhas);
			}
			fs.close();
		} catch (IOException ex) {
			if(ex.getMessage().toLowerCase().contains("arquivo especificado"))
			{
				JOptionPane.showMessageDialog(null, "Impressora não está instalada ou não foi encontrada na porta LPT1.", "Erro", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				ex.printStackTrace();
				new PainelErro(ex);
			}
		}			
	}
}