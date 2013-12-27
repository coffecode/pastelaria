import java.awt.*;

import javax.swing.*;

import java.awt.Color;
import java.awt.event.*;
import java.awt.Graphics;

public class CadastrarFiado extends JFrame implements ActionListener, FocusListener, MouseListener
{
	private JLabel fiadorCPF, fiadorNome, fiadorApelido, fiadorTelefone, legendaCPF, imagem, fiadorConta;
	private JTextField campoCPF, campoNome, campoApelido, campoTelefone;
	private JButton procuraCPF, cancelar, concluir;
	private ImageIcon iconeConsulta;
	
	private String bancoNome;
	private double totalFiado;
	private int bancoFiadorID;
	private boolean cadastrar;
	private int callBack = 0;
	
	static public String logado = "";
	
	public CadastrarFiado()
	{
		setTitle("Cadastro de Cliente Fiado");
		JPanel cadastroFiado = new MeuPainel();
		cadastroFiado.setLayout(null);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		cadastrar = false;
		
		fiadorCPF = new JLabel("CPF:");
		fiadorCPF.setBounds(15,40,70,30); // Coluna, Linha, Largura, Altura!
		cadastroFiado.add(fiadorCPF);
		
		campoCPF = new JTextField();
		campoCPF.setBounds(57,41,125,30);
		campoCPF.addFocusListener(this);
		cadastroFiado.add(campoCPF);
		
        iconeConsulta = new ImageIcon("imgs/consultar2.png");
        procuraCPF = new JButton(iconeConsulta);
        procuraCPF.addMouseListener(this);
        procuraCPF.addActionListener(this);
        procuraCPF.setBorder(BorderFactory.createEmptyBorder());
        procuraCPF.setContentAreaFilled(false);
        procuraCPF.setBounds(195,40,32,32);
        cadastroFiado.add(procuraCPF);   
		
		legendaCPF = new JLabel("Por favor, verifique o CPF do cliente.");
		legendaCPF.setBounds(15,1,500,30);
		cadastroFiado.add(legendaCPF);
		
		fiadorNome = new JLabel("*Nome Completo:");
		fiadorNome.setBounds(15,120,100,30); // Coluna, Linha, Largura, Altura!
		fiadorNome.setEnabled(false);
		cadastroFiado.add(fiadorNome);
		
		campoNome = new JTextField();
		campoNome.setBounds(130,120,170,30);
		campoNome.setEnabled(false);
		campoNome.setHorizontalAlignment(JTextField.CENTER);
		campoNome.addFocusListener(this);
		cadastroFiado.add(campoNome);		
		
		fiadorApelido = new JLabel("Apelido:");
		fiadorApelido.setBounds(15,160,100,30); // Coluna, Linha, Largura, Altura!
		fiadorApelido.setEnabled(false);
		cadastroFiado.add(fiadorApelido);
		
		campoApelido = new JTextField();
		campoApelido.setBounds(130,160,100,30);
		campoApelido.setEnabled(false);
		campoApelido.setHorizontalAlignment(JTextField.CENTER);
		cadastroFiado.add(campoApelido);
		
		fiadorTelefone = new JLabel("*Telefone:");
		fiadorTelefone.setBounds(15,200,100,30); // Coluna, Linha, Largura, Altura!
		fiadorTelefone.setEnabled(false);
		cadastroFiado.add(fiadorTelefone);
		
		campoTelefone = new JTextField();
		campoTelefone.setBounds(130,200,100,30);
		campoTelefone.setEnabled(false);
		campoTelefone.setHorizontalAlignment(JTextField.CENTER);
		campoTelefone.addFocusListener(this);
		cadastroFiado.add(campoTelefone);
		
		imagem = new JLabel(new ImageIcon("imgs/pessoa.png"));
		imagem.setBounds(350,115,128,128);
		imagem.setEnabled(false);
		cadastroFiado.add(imagem);

		fiadorConta = new JLabel("");
		fiadorConta.setBounds(15,270,400,30);
		fiadorConta.setHorizontalTextPosition(AbstractButton.LEFT);
		fiadorConta.addMouseListener(this);
		cadastroFiado.add(fiadorConta);
		
		concluir = new JButton("Cadastrar");
		concluir.setBounds(105,325,130,50);
		ImageIcon iconeConcluir = new ImageIcon("imgs/concluir.png");
		concluir.setIcon(iconeConcluir);
		concluir.setEnabled(false);
		concluir.addMouseListener(this);
		cadastroFiado.add(concluir);
		
		cancelar = new JButton("Cancelar");
		cancelar.setBounds(255,325,130,50);
		ImageIcon iconeCancelar = new ImageIcon("imgs/cancelar.png");
		cancelar.setIcon(iconeCancelar);
		cancelar.addMouseListener(this);
		cadastroFiado.add(cancelar);
		
		add(cadastroFiado);
		
		setSize(500, 420);							// Largura, Altura
		setLocationRelativeTo(null);				// Abre no centro da tela
		
		getRootPane().setDefaultButton(procuraCPF);
		setResizable(false);
		
		MenuPrincipal.Ativar(false);
	}
	
	private class MeuPainel extends JPanel
	{
	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        
	        g.setColor(new Color(196, 194, 183));
	        g.drawLine(0, 90, getWidth(), 90);
	        g.drawLine(0, 260, getWidth(), 260);
	    }			
	}
	
	private void Terminar() {
	    setVisible(false);
	    dispose();
	}	
	
	public void setCallBack(int model)
	{
		callBack = model;
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		//
	}

	@Override
	public void focusLost(FocusEvent e) {
		if(e.getSource() == campoCPF)
		{
			String pegaCPF = campoCPF.getText();
			pegaCPF = pegaCPF.replaceAll("[^0-9]+","");
			
			if(!"".equals(pegaCPF.trim()))
			{
				if(validaCPF(pegaCPF))
				{
					legendaCPF.setText("CPF Válido");
					CadastraFiado();
				}
				else
				{
					legendaCPF.setText("CPF Inválido");
				}				
			}
		}
		
		if(e.getSource() == campoNome || e.getSource() == campoTelefone)
		{
			verificarCampos();
		}
		
	}		
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == concluir)
		{
			if(!("".equals(campoNome.getText().trim())) && !("".equals(campoTelefone.getText().trim())))
			{
				if(cadastrar)
					CadastrarClienteFiado();
				else
					FinalizarCadastro();
			}
		}
		
		if(e.getSource() == cancelar)
		{
			bancoFiadorID = 0;
			FinalizarCadastro();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void CadastrarClienteFiado()
	{
		String formatacao;
		
		String pegaCPF = campoCPF.getText();
		pegaCPF = pegaCPF.replaceAll("[^0-9]+","");		
		
		Query envia = new Query();
		formatacao = "INSERT INTO fiados(nome, apelido, telefone, cpf) VALUES('"
		+ campoNome.getText() +
		"', '" + campoApelido.getText() +
		"', " + Integer.parseInt(campoTelefone.getText()) + ", '" + pegaCPF + "');";
		
		envia.executaUpdate(formatacao);
		
		formatacao = "SELECT fiador_id FROM fiados ORDER BY fiador_id DESC;";
		envia.executaQuery(formatacao);
		if(envia.next())
		{
			bancoFiadorID = envia.getInt("fiador_id");
		}
		
		bancoNome = campoNome.getText();
		
		envia.fechaConexao();
		FinalizarCadastro();
	}
	
	private void FinalizarCadastro()
	{
		if(callBack == 1) // qm chamou foi venda rapida
		{
			MenuPrincipal.Ativar(true);
			PainelVendaRapida.setFiado(bancoNome, bancoFiadorID);
		}
		
		Terminar();
	}
	
	private void verificarCampos()
	{
		if(!("".equals(campoNome.getText().trim())) && !("".equals(campoTelefone.getText().trim())))
		{
			concluir.setEnabled(true);
			getRootPane().setDefaultButton(concluir);
		}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == procuraCPF)
		{
			CadastraFiado();
		}
	}
	
	private void CadastraFiado()
	{
		String pegaCPF = campoCPF.getText();
		pegaCPF = pegaCPF.replaceAll("[^0-9]+","");			
		
		if(!"".equals(pegaCPF.trim()))
		{
			if(validaCPF(pegaCPF))
			{
				String formatacao;
				Query consulta = new Query();
				formatacao = "SELECT fiador_id, nome, telefone, apelido FROM fiados WHERE cpf = '" + pegaCPF + "';";
				
				consulta.executaQuery(formatacao);
				if(consulta.next())
				{
					bancoNome = consulta.getString("nome");
					bancoFiadorID = consulta.getInt("fiador_id");
					
					campoNome.setText(bancoNome);
					campoApelido.setText(consulta.getString("apelido"));
					campoTelefone.setText(consulta.getString("telefone"));
					
					formatacao = "SELECT total, valor_pago FROM vendas WHERE fiado_id = " + bancoFiadorID + ";";
					consulta.executaQuery(formatacao);
					totalFiado = 0.0;
					
					while(consulta.next())
					{
						String formata;
						formata = consulta.getString("total");
						formata = formata.replaceAll(",",".");	
						double pTotal = Double.parseDouble(formata);
						formata = consulta.getString("valor_pago");
						formata = formata.replaceAll(",",".");							
						double pPago = Double.parseDouble(formata);
						
						if(pTotal > pPago)
							totalFiado += (pTotal - pPago);
					}
					
					String pegaPreco;
					pegaPreco = String.format("%.2f", totalFiado);	
					
					fiadorConta.setText("Esse cliente já possui cadastro e tem uma dívida de R$" + pegaPreco + ".");
					concluir.setText("Adicionar");
					concluir.setEnabled(true);
					fiadorCPF.setEnabled(false);
					campoCPF.setEnabled(false);
					procuraCPF.setEnabled(false);
					
					fiadorNome.setEnabled(true);
					campoNome.setEnabled(true);
					campoNome.setEditable(false);
					fiadorApelido.setEnabled(true);
					campoApelido.setEnabled(true);
					campoApelido.setEditable(false);
					fiadorTelefone.setEnabled(true);
					campoTelefone.setEnabled(true);
					campoTelefone.setEnabled(true);
					campoTelefone.setEditable(false);
					imagem.setEnabled(true);
					cadastrar = false;
					
					if(totalFiado > 0)
					{
						fiadorConta.setIcon(iconeConsulta);
						fiadorConta.setForeground(Color.red);
					}
				}
				else
				{
					fiadorConta.setText("Esse cliente ainda não possui cadastro.");
					legendaCPF.setText("Por favor, cadastre o cliente.");
					
					fiadorCPF.setEnabled(false);
					campoCPF.setEnabled(false);
					procuraCPF.setEnabled(false);
					
					fiadorNome.setEnabled(true);
					campoNome.setEnabled(true);
					fiadorApelido.setEnabled(true);
					campoApelido.setEnabled(true);
					fiadorTelefone.setEnabled(true);
					campoTelefone.setEnabled(true);
					campoTelefone.setEnabled(true);
					imagem.setEnabled(true);
					
					cadastrar = true;
				}
				
				consulta.fechaConexao();
			}
			else
			{
				legendaCPF.setText("CPF Inválido.");
			}				
		}		
	}
	
	private boolean validaCPF(String strCpf)
	{
	    int iDigito1Aux = 0, iDigito2Aux = 0, iDigitoCPF;
	    int iDigito1 = 0, iDigito2 = 0, iRestoDivisao = 0;
	    String strDigitoVerificador, strDigitoResultado;
	 
	    if (! strCpf.substring(0,1).equals("")){
	        try{
	            strCpf = strCpf.replace('.',' ');
	            strCpf = strCpf.replace('-',' ');
	            strCpf = strCpf.replaceAll(" ","");
	            for (int iCont = 1; iCont < strCpf.length() -1; iCont++) {
	                iDigitoCPF = Integer.valueOf(strCpf.substring(iCont -1, iCont)).intValue();
	                iDigito1Aux = iDigito1Aux + (11 - iCont) * iDigitoCPF;
	                iDigito2Aux = iDigito2Aux + (12 - iCont) * iDigitoCPF;
	            }
	            iRestoDivisao = (iDigito1Aux % 11);
	            if (iRestoDivisao < 2) {
	                iDigito1 = 0;
	            } else {
	                iDigito1 = 11 - iRestoDivisao;
	            }
	            iDigito2Aux += 2 * iDigito1;
	            iRestoDivisao = (iDigito2Aux % 11);
	            if (iRestoDivisao < 2) {
	                iDigito2 = 0;
	            } else {
	                iDigito2 = 11 - iRestoDivisao;
	            }
	            strDigitoVerificador = strCpf.substring(strCpf.length()-2, strCpf.length());
	            strDigitoResultado = String.valueOf(iDigito1) + String.valueOf(iDigito2);
	            return strDigitoVerificador.equals(strDigitoResultado);
	        } catch (Exception e) {
	            return false;
	        }
	    } else {
	        return false;
	    }
	}
}