package Chat;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

public class Cliente implements Runnable{
	private DataOutputStream saida;
	private DataInputStream entrada;
	private Socket socket;
	private Janela_Chat chatFrame;
	public boolean conectado;
	private boolean loop = true;
	public String ipServidor;
	public int porta;
	public String nome;
	   public Cliente(String ip, String porta, String nome){
		 try{
			 this.ipServidor = ip;
			 this.porta = Integer.parseInt(porta);
			 this.nome = nome;
			 socket = new Socket(this.ipServidor, this.porta);
		     conectado = true;
		     JOptionPane.showMessageDialog(null,"Conexão realizada com sucesso!");
		     saida = new DataOutputStream(socket.getOutputStream());
		     entrada = new DataInputStream(socket.getInputStream());
		     Enviar("INICIAR","servidor", nome);
		 }catch(ConnectException e){
			 conectado = false;
			  JOptionPane.showMessageDialog(null, "Não foi possivel conectar no servidor \nVerifique se o IP e a porta estão corretas.", "Erro", JOptionPane.ERROR_MESSAGE);
		  }catch(Exception e){
			  conectado = false;
			  JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		  }
	   }
	   @Override
	   public void run() {
			 while(loop){
				 try{
					String dados =  entrada.readUTF();
					StringTokenizer splitMsg = new StringTokenizer(dados);
						switch(splitMsg.nextToken("|")){
						case "SINGLE_MSG":
							String msg = "";
							String emitente = splitMsg.nextToken("|");
							while(splitMsg.hasMoreTokens()){
								msg = msg + splitMsg.nextToken("|");
							}
							chatFrame.incluirMsg(emitente, emitente +": "+msg+"\n");
							break;
						case "CLIENTES_ON":
							while(splitMsg.hasMoreTokens()){
								String nometemp = splitMsg.nextToken("|");
								String iptemp = splitMsg.nextToken("|");
								chatFrame.addLabelClientON(nometemp,iptemp );
							}
							break;
						case "CLIENTE_OFF":
							chatFrame.removeLabelClientON(splitMsg.nextToken());
							break;
						default:
							System.out.println("[Servidor]: Não foi possivel identificar o comando\n");
							break;
						 }
				 }catch(SocketException e){
					 loop = false;
					 JOptionPane.showMessageDialog(null, "Você foi desconectado do servidor", "Desconectado", JOptionPane.WARNING_MESSAGE);
					 chatFrame.setVisible(false);
					 Main.frame.setVisible(true);
				 }catch(IOException e) {
					e.printStackTrace();
				 }
			}
		}
	   
	   public void Enviar(String opt,String destino, String msg){
		  try{
			  /// Arquitetura [Opção] | [Destino] | [Mensagem]
			saida.writeUTF(opt+ "|" +destino+ "|" +msg);
		  }catch(SocketException e){
			  JOptionPane.showMessageDialog(null, "Não foi possivel enviar a mensagem ao servidor", "Erro", JOptionPane.ERROR_MESSAGE);
		  }catch(IOException e) {
			  JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		  }
	   }	   
	   
	   public void SetChat(Janela_Chat frame){
		   this.chatFrame = frame;
	   }
	   
	   public void Fechar(){
		   try{
			   saida.writeUTF("FECHAR");
			   socket.close();
		   }catch(IOException e) {
			   JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		   }
	   }
}