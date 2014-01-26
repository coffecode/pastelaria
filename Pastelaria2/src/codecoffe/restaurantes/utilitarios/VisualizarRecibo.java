package codecoffe.restaurantes.utilitarios;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class VisualizarRecibo
{
	@SuppressWarnings("resource")
	static public void imprimirRecibo()
	{
	       try {
	            java.io.InputStream is = new FileInputStream("codecoffe/recibo.txt");
	            Scanner sc = new Scanner(is);
	            FileOutputStream fs = new FileOutputStream("LPT1:");
	            PrintStream ps = new PrintStream(fs);

	            while(sc.hasNextLine()){
	                String linhas = sc.nextLine();
	                ps.println(linhas);
	            }
	            fs.close();
	        } catch (IOException ex) {
	            JOptionPane.showMessageDialog(null, "Ocorreu o seguine erro no sistema:\n" + ex.getMessage(), "Houve um erro ;(", JOptionPane.ERROR_MESSAGE);
	        }			
	}
}