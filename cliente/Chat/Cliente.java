package Chat;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
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
			 saida = new DataOutputStream(socket.getOutputStream());
		     entrada = new DataInputStream(socket.getInputStream());
		     enviar("a","a", nome);
		     String status = entrada.readUTF();
		     if(status.equals("ACEITO")){
			     conectado = true;
			     JOptionPane.showMessageDialog(null,"Conexão realizada com sucesso!");
		     }else{
		    	 conectado = false;
		    	 JOptionPane.showMessageDialog(null,"Nome já existe, por favor usar outro nome." + status);
		     }
		    
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
					String msg = "";
					String emitente;
					String nomeGrupo;
					StringTokenizer splitMsg = new StringTokenizer(dados);
						switch(splitMsg.nextToken("|")){
						case "SINGLE_MSG":
							emitente = splitMsg.nextToken("|");
							while(splitMsg.hasMoreTokens()){
								msg = msg + splitMsg.nextToken("|");
							}
							chatFrame.incluirMsg(emitente, emitente +": "+msg+"\n");
							break;
						case "GRUPO_MSG":// Arquitetura [Opção] | [nome do Grupo] | [Emitente] | [Mensagem]
							nomeGrupo = splitMsg.nextToken("|");
							emitente = splitMsg.nextToken("|");
							while(splitMsg.hasMoreTokens()){
								msg = msg + splitMsg.nextToken("|");
							}
							chatFrame.incluirMsg(nomeGrupo, emitente +": "+msg+"\n");
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
						case "GRUPOS_ON":
							while(splitMsg.hasMoreTokens()){
								chatFrame.addLabelGrupo(splitMsg.nextToken());
							}
							break;
						case "GRUPO_ADD":
							chatFrame.addLabelGrupo(splitMsg.nextToken("|"));
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
	   
	   public void enviar(String opt,String destino, String msg){
		  try{
			  /// Arquitetura [Opção] | [Destino] | [Mensagem]
			saida.writeUTF(opt+ "|" +destino+ "|" +msg);
		  }catch(SocketException e){
			  JOptionPane.showMessageDialog(null, "Não foi possivel enviar a mensagem ao servidor", "Erro", JOptionPane.ERROR_MESSAGE);
		  }catch(IOException e) {
			  JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		  }
	   }	   
	   
	   public void criarGrupo(String grupo, DefaultListModel<String> usuarios){
		   try{
			   String env = "CRIAR_GRUPO|"+grupo+"|"+nome;
			   for(int x=0;usuarios.size() > x;x++){
				   env = env + "|" + usuarios.get(x);
			   }
			   saida.writeUTF(env);
		   }catch(SocketException e){
				  JOptionPane.showMessageDialog(null, "Não foi possivel enviar a mensagem ao servidor", "Erro", JOptionPane.ERROR_MESSAGE);
		   }catch(IOException e) {
				  JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		   }
	   }
	   
	   public void setChat(Janela_Chat frame){
		   this.chatFrame = frame;
	   }
	   
	   public void fechar(){
		   try{
			   saida.writeUTF("FECHAR");
			   socket.close();
		   }catch(IOException e) {
			   JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		   }
	   }
}