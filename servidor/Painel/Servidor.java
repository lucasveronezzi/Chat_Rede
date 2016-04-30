package Painel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

public class Servidor implements Runnable{
	private ServerSocket servidor;
	private Janela_Servidor terminal;
	private List<Cliente> client = new ArrayList<Cliente>();
	private List<Grupo> grupo = new ArrayList<Grupo>();
	
	public Servidor(int porta,Janela_Servidor terminal){
		 try {
			 	this.terminal = terminal;
				servidor = new ServerSocket(porta);
				terminal.addMsgTerminal("[Servidor]: Porta aberta com sucesso " + porta + "\n");
				grupo.add(new Grupo("Chat All"));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
			} 
	 }
	 @Override
	public void run() {
		 while(true){
			 try{
				 Socket socket = servidor.accept();
				 client.add(new Cliente(socket));
				 client.get(client.size()-1).start();;
			 }catch(IOException e) {
				e.printStackTrace();
			 }
		}
	}
	 
	 public void redirecionarMSG(String nome, String msg){
		 for(int x=0;client.size() > x;x++){
			 if(client.get(x).nome.equals(nome)){
				 Cliente destino = client.get(x);
				 try {
					destino.saida.writeUTF(msg);
					terminal.addMsgTerminal("[Servidor] Mensagem Enviada com sucesso!\n");
				} catch (IOException e) {
					terminal.addMsgTerminal("[Servidor] Não foi possivel enviar a mensagem!Erro:\n"+e.getMessage());
				}
				 break;
			 }
		 }
	 }
	 
	 public void redirecionarTodos(String msg){
		 try{
			 for(int x=0;client.size() > x;x++){
				client.get(x).saida.writeUTF(msg);
			 }
		 }catch(IOException e) {
			e.printStackTrace();
		}
	 }
	
	 public void Teste(){
		 Thread.currentThread().getThreadGroup().list();
		 for(int x=0;client.size() > x;x++){
			 System.out.println(client.get(x).nome);
		 }
	 }
	 
	 public void RemoverClient(String nome){
		 for(int x=0;client.size() > x;x++){
			 if(client.get(x).nome.equals(nome)){
				 Cliente temp = client.get(x);
				 try{
					grupo.get(0).removeCliente(nome);;
					client.remove(x);    
					temp.entrada.close();
					temp.saida.close();
					temp.socket.close();
					temp.loop = false;
					terminal.addMsgTerminal("[Remover]:\""+temp.nome+"\" foi removido do servidor\n");
				} catch (IOException e) {
					terminal.addMsgTerminal("[Erro]: Erro ao remover o cliente:\n"+e.getMessage()+"\n");
				}
			 }
			try {
				if(x<client.size())
					client.get(x).saida.writeUTF("CLIENTE_OFF|"+nome);
			}catch(IOException e) {
				e.printStackTrace();
			}
		 }
	 }
	 
	 
	 
	 public void enviarClientesON(Cliente destino){
		 String msg1 = "CLIENTES_ON";
		 String msg2 = "CLIENTES_ON|"+destino.nome+"|"+destino.ip;
		 for(int x=0;client.size() > x;x++){
			 if(!client.get(x).nome.equals(destino.nome)){
				 try {
					client.get(x).saida.writeUTF(msg2);
				} catch (IOException e) {
					terminal.addMsgTerminal("[Erro]: Erro ao enviar o status do cliente '"+destino.nome+"' para"+client.get(x).nome);
				}
				 msg1 = msg1+"|"+client.get(x).nome+ "|"+client.get(x).ip;
			 }
		 }
		 try {
			destino.saida.writeUTF(msg1);
		}catch(IOException e){
			terminal.addMsgTerminal("[Erro]: Erro ao enviar lista de clientes para: "+destino.nome+"\n"+e.getMessage()+"\n");
		}
	 }
	 
	 public class Cliente extends Thread {
		public String nome;
		public Socket socket;
		public String ip;
		public boolean loop = true;
		private DataInputStream entrada;
		private DataOutputStream saida;
		 
		 public Cliente(Socket socket){
			 try {
			 		this.socket = socket;
			 		ip = socket.getInetAddress().getHostAddress();
			 		saida = new DataOutputStream(socket.getOutputStream());
					entrada = new DataInputStream(socket.getInputStream());
			 }catch(IOException e) {
					e.printStackTrace();
			 }
		 }
		    @Override
		 public void run() {
			 try {
				 while (loop){
					 String dados =  entrada.readUTF();
					 String msg = "";
					 String msgEnviar = "";
					 String destino;
					 StringTokenizer splitMsg = new StringTokenizer(dados);
					 switch(splitMsg.nextToken("|")){
					 	case "INICIAR": /// Arquitetura [Opção] | [cliente]
					 		splitMsg.nextToken("|");
					 		nome = splitMsg.nextToken("|");
					 		grupo.get(0).addCliente(nome);
					 		enviarClientesON(this);
					 		terminal.addMsgTerminal("[Servidor]: Nova conexão com o cliente \"" +  nome +"\"("+ip+")\n");
					 		terminal.addCliente(nome, ip, socket.getPort());
					 		break;
					 	case "SINGLE_MSG": /// Arquitetura [Opção] | [Destino] | [Mensagem]
					 		destino = splitMsg.nextToken("|");
					 		while(splitMsg.hasMoreTokens()){
					 			msg = msg + splitMsg.nextToken("|");
					 			if(splitMsg.hasMoreTokens())
					 				msg = msg+"|";
					 		}
					 		terminal.addMsgTerminal("[Mensagem]: De: \"" +nome+ "\" Para: \""+destino+"\"\nMsg: " + msg + "\n");
					 		/// Arquitetura [Opção] | [Emitente] | [Mensagem]
					 		msg = "SINGLE_MSG|"+this.nome+"|"+msg;
					 		redirecionarMSG(destino, msg);
					 		break;
					 	case "GROUP_MSG": //  Arquitetura [Opção] | [nome do Grupo] | [Mensagem]
					 		String grupoR = splitMsg.nextToken("|");
					 		while(splitMsg.hasMoreTokens()){
					 			msg = msg + splitMsg.nextToken("|");
					 		}
					 		for(Grupo grupoT : grupo){
					 			if(grupoT.nome.equals(grupoR)){
					 				for(int x=0;grupoT.clientes.size() > x;x++){
					 					destino = grupoT.getCliente(x);
					 					if(destino != this.nome){
					 					// Arquitetura [Opção] | [nome do Grupo] | [Emitente] | [Mensagem]
						 					msgEnviar = "GRUPO_MSG|"+grupoT.nome+"|"+this.nome+"|"+msg;
						 					redirecionarMSG(destino,msgEnviar);
					 					}
					 				}
					 			}
					 		}
					 		break;
					 	case "FECHAR":
					 		terminal.addMsgTerminal("[Servidor]: O Cliente \""+nome+"\"("+ip+") desconectou!\n");
					 		RemoverClient(nome);					 		
					 		break;
					 	default:
						 	terminal.addMsgTerminal("[Servidor]: Não foi possivel identificar o comando\n");
						 	break;
					 }
					/// String msgRecebida = s.nextLine(); 
				 }
			}catch(IOException e) {
				e.printStackTrace();
			}
		 }
	 }
	 
	 class Grupo{
		 private String nome;
		 private List<String> clientes = new ArrayList<String>(); 
		 
		 public Grupo(String n){
			 nome = n;
		 }
		 public void addCliente(String nomeCliente){
			 clientes.add(nomeCliente);
		 }
		 public String getCliente(int index){
			 return clientes.get(index);
		 }
		 public void removeCliente(String nomeCliente){
			 clientes.remove(nomeCliente);
		 }
	 }

}