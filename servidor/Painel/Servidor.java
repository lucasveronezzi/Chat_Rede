package Painel;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitorInputStream;

public class Servidor implements Runnable{
	private ServerSocket servidor;
	private Janela_Servidor terminal;
	private List<Cliente> client = new ArrayList<Cliente>();
	private List<Grupo> grupo = new ArrayList<Grupo>();
	private List<Arquivo> arquivos = new ArrayList<Arquivo>();
	
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
				String nomeTemp = validar(socket);
				switch(nomeTemp){
				case "RECUSADO":
					socket.close();
					break;
				case "ARQUIVO":
					arquivos.get(arquivos.size()-1).setSocket(socket);
					arquivos.get(arquivos.size()-1).start();
					break;
				case "ACCEPT_FILE":
					break;
				default:
					client.add(new Cliente(socket,nomeTemp));
					client.get(client.size()-1).start();
					break;
				 }
			 }catch(IOException e) {
				 terminal.addErroTerminal("[ERRO]: "+e.getMessage());
			 }
		}
	}
	 public String validar(Socket socketValidar){
		 try {
			DataInputStream entrada = new DataInputStream(socketValidar.getInputStream());
			DataOutputStream saida = new DataOutputStream(socketValidar.getOutputStream());
			String dados =  entrada.readUTF();
			StringTokenizer splitMsg = new StringTokenizer(dados);
			String opt = splitMsg.nextToken("|");
			String nome = splitMsg.nextToken("|");
			if(grupo.get(0).clientes.contains(nome)){
				if(opt.equals("CON_FILE_REQUEST")){
					String destino = splitMsg.nextToken("|");
					String arquivoNome = splitMsg.nextToken("|");
					String arquivoTamanho = splitMsg.nextToken("|");
					arquivos.add(new Arquivo(destino, nome, arquivoNome, arquivoTamanho));
					return "ARQUIVO";
				}else 
					if(opt.equals("CON_FILE_ACCEPT")){
						String emitente = splitMsg.nextToken("|");
						String nomeFile = splitMsg.nextToken("|");
						for(int x=0;arquivos.size() >x;x++){
							if(arquivos.get(x).emitente.equals(emitente) && arquivos.get(x).nomeArquivo.equals(nomeFile)){
								arquivos.get(x).socketDestino = socketValidar;
								arquivos.get(x).enviarFile();
								break;
							}
						}
						return "ACCEPT_FILE";
					}
				saida.writeUTF("RECUSADO");
			 	terminal.addMsgTerminal("[Servidor]:Cliente("+socketValidar.getInetAddress().getHostAddress()+") recusado, nome "+nome+" já existe\n");
				return "RECUSADO";
			}else 
				if(opt.equals("CON_CLIENTE")){
					grupo.get(0).addCliente(nome);
					return nome;
				}
				saida.writeUTF("RECUSADO");
				terminal.addMsgTerminal("[Servidor]:Cliente("+socketValidar.getInetAddress().getHostAddress()+") recusado, nome "+nome+" já existe\n");
				return "RECUSADO";
		} catch (IOException e) {
			terminal.addErroTerminal("[ERRO]: "+e.getMessage());
			return "RECUSADO";
		}
	 }
 
	 public void removerClient(String nome){
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
					terminal.addMsgTerminal("[REMOVER]:\""+temp.nome+"\" foi removido do servidor\n");
				} catch (IOException e) {
					terminal.addErroTerminal("[ERRO]: Erro ao remover o cliente:\n"+e.getMessage()+"\n");
				}
			 }
			try {
				if(x<client.size())
					client.get(x).saida.writeUTF("CLIENTE_OFF|"+nome);
			}catch(IOException e) {
				terminal.addErroTerminal("[ERRO]: "+e.getMessage());
			}
		 }
	 }
	 
	 public class Cliente extends Thread {
		 private String nome;
		 private Socket socket;
		 private String ip;
		 private boolean loop = true;
		 private DataInputStream entrada;
		 private DataOutputStream saida;
		
		public Cliente(Socket socket, String nome){
			try {
			 		this.socket = socket;
			 		this.nome = nome;
			 		ip = socket.getInetAddress().getHostAddress();
			 		saida = new DataOutputStream(socket.getOutputStream());
					entrada = new DataInputStream(socket.getInputStream());
					saida.writeUTF("ACEITO");
					enviarGrupos(this);
					enviarClientesON(this);
					terminal.addMsgTerminal("[Servidor]: Nova conexão com o cliente \"" +  nome +"\"("+ip+")\n");
					DateFormat dateFormat = new SimpleDateFormat("[HH:mm] dd/MM/yy");
					Date date = new Date();
					terminal.addCliente(nome, ip, socket.getPort(), dateFormat.format(date));
			 }catch(IOException e) {
				 terminal.addErroTerminal("[ERRO]: "+e.getMessage());
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
					 String grupoR;
					 StringTokenizer splitMsg = new StringTokenizer(dados);
					 switch(splitMsg.nextToken("|")){
					 	case "SINGLE_MSG"://ESTRUTURA [OPCAO] | [DESTINO] | [MENSAGEM]
					 		destino = splitMsg.nextToken("|");
					 		while(splitMsg.hasMoreTokens()){
					 			msg = msg + splitMsg.nextToken("|");
					 			if(splitMsg.hasMoreTokens())
					 				msg = msg+"|";
					 		}
					 		terminal.addMsgTerminal("[Mensagem]: De: \"" +nome+ "\" Para: \""+destino+"\"\nMsg: " + msg + "\n");
					 		msg = "SINGLE_MSG|"+this.nome+"|"+msg;
					 		redirecionarMSG(destino, msg);
					 		break;
					 	case "GROUP_MSG"://ESTRUTURA [OPCAO] |  [NOME DO GRUPO] | [MENSAGEM]
					 		grupoR = splitMsg.nextToken("|");
					 		while(splitMsg.hasMoreTokens()){
					 			msg = msg + splitMsg.nextToken("|");
					 		}
					 		for(Grupo grupoT : grupo){
					 			if(grupoT.nome.equals(grupoR)){
					 				terminal.addMsgTerminal("[Mensagem Grupo]: De: \"" +nome+ "\" Para: \""+grupoR+"\"\nMsg: " + msg + "\n");
					 				for(int x=0;grupoT.clientes.size() > x;x++){
					 					destino = grupoT.getCliente(x);
					 					if(!destino.equals(this.nome)){
						 					msgEnviar = "GRUPO_MSG|"+grupoT.nome+"|"+this.nome+"|"+msg;
						 					redirecionarMSG(destino,msgEnviar);
					 					}
					 				}
					 			}
					 		}
					 		break;
					 	case "CRIAR_GRUPO"://ESTRUTURA [OPCAO] | [NOME DO GRUPO] | [LISTA DE USUARIOS]
					 		grupoR = splitMsg.nextToken("|");
					 		int x = grupo.size();
					 		grupo.add(x,new Grupo(grupoR));
					 		while(splitMsg.hasMoreTokens()){
					 			String nomeClient = splitMsg.nextToken("|");
					 			grupo.get(x).addCliente(nomeClient);
					 			msg = msg+nomeClient+";";
					 		}
					 		enviarGrupo(grupoR,grupo.get(x).clientes, msg);
					 		terminal.addMsgTerminal("[Servidor]: Grupo'"+grupoR+"' adicionar com sucesso\n");
					 		break;
					 	case "SAIR_GRUPO"://ESTRUTURA [OPCAO] |  [NOME DO GRUPO]
					 		grupoR = splitMsg.nextToken("|");
					 		for(Grupo grupoT : grupo){
					 			if(grupoT.nome.equals(grupoR)){
					 				grupoT.clientes.remove(nome);
					 				for(int i=0;grupoT.clientes.size() > i;i++){
					 					msgEnviar = "USER_SAIU_GRUPO|"+grupoT.nome+"|"+nome;
					 					redirecionarMSG(grupoT.clientes.get(i),msgEnviar);
					 				}
					 			}
					 		}
					 		break;
					 	case "ADD_USER_GRUPO"://ESTRUTURA [OPCAO] | [NOME DO GRUPO] | [USUARIO]
					 		grupoR = splitMsg.nextToken("|");
					 		destino = splitMsg.nextToken("|");
					 		for(Grupo grupoT : grupo){
					 			if(grupoT.nome.equals(grupoR)){
					 				if(!grupoT.clientes.contains(destino)){
						 				grupoT.clientes.add(destino);
						 				redirecionarMSG(nome,"ADD_USERGRUPO|ADICIONADO");
						 				String usuariosGrupo = "";
						 				for(int i=0;grupoT.clientes.size() > i;i++){
						 					if(!grupoT.clientes.get(i).equals(destino)){
							 					usuariosGrupo = usuariosGrupo+grupoT.clientes.get(i)+";";
							 					msgEnviar = "USER_ENTROU_GRUPO|"+grupoT.nome+"|"+destino;
							 					redirecionarMSG(grupoT.clientes.get(i),msgEnviar);
						 					}
						 				}
						 				msgEnviar = "GRUPO_ADD|"+grupoT.nome+"|"+usuariosGrupo;
								 		redirecionarMSG(destino,msgEnviar);
						 				break;
					 				}else{
					 					redirecionarMSG(nome,"ADD_USERGRUPO|RECUSADO");
					 				}
					 			}
					 		}
					 		break;
					 	case "FECHAR"://COMANDO PARA REMOVER O CLIENTE E FECHAR SUAS CONEXOES
					 		terminal.addMsgTerminal("[Servidor]: O Cliente \""+nome+"\"("+ip+") desconectou!\n");
					 		removerClient(nome);					 		
					 		break;
					 	default:
						 	terminal.addMsgTerminal("[Servidor]: Não foi possivel identificar o comando\n");
						 	break;
					 }
				 }
			}catch(IOException e) {
				removerClient(nome);
				terminal.addErroTerminal("[ERRO]: "+e.getMessage());
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
						terminal.addErroTerminal("[ERRO] Não foi possivel enviar a mensagem!Erro:\n"+e.getMessage());
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
				 terminal.addErroTerminal("[ERRO]: "+e.getMessage());
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
						terminal.addErroTerminal("[ERRO]: Erro ao enviar o status do cliente '"+destino.nome+"' para"+client.get(x).nome);
					}
					 msg1 = msg1+"|"+client.get(x).nome+ "|"+client.get(x).ip;
				 }
			 }
			 try {
				destino.saida.writeUTF(msg1);
			}catch(IOException e){
				terminal.addErroTerminal("[ERRO]: Erro ao enviar lista de clientes para: "+destino.nome+"\n"+e.getMessage()+"\n");
			}
		 }
		 
		 public void enviarGrupos(Cliente destino){
			 String msg = "GRUPOS_ON|";
			 for(int x=0;grupo.size()>x;x++){
				 if(grupo.get(x).clientes.contains(destino.nome)){
					 msg = msg+grupo.get(x).nome+"|";
					 for(int i=0;grupo.get(x).clientes.size() > i;i++){
						 msg = msg + grupo.get(x).clientes.get(i)+";";
					 }
					 msg = msg + "|";
				 }
			 }
			 try {
				destino.saida.writeUTF(msg);
			} catch (IOException e) {
				terminal.addErroTerminal("[ERRO]: "+e.getMessage());
			}
		 }
		 
		 public void enviarGrupo(String grupo, List<String> clientes, String msgClient){
			 String msg = "GRUPO_ADD|"+grupo+"|"+msgClient;
			 for(int x=0;client.size() > x;x++){
				 if(clientes.contains(client.get(x).nome)){
					 try {
						client.get(x).saida.writeUTF(msg);
					} catch (IOException e) {
						terminal.addErroTerminal("[ERRO]: "+e.getMessage());
					}
				 }
			 }
		 }
	 }
	 
	 public void finalizar_arquivo(Socket sk ){
		for(int x=0;arquivos.size() > x;x++){
			if(arquivos.get(x).socket.equals(sk)){
				Arquivo temp = arquivos.get(x);
				try {
					arquivos.remove(x);
					temp.socketDestino.close();
				} catch (IOException e) {
					terminal.addErroTerminal("[ERRO]: "+e.getMessage());
				}
			}
		}
	 }

	 class Arquivo extends Thread{
		 private String destino;
		 private String emitente;
		 private String nomeArquivo;
		 private int tamanho;
		 private Socket socket;
		 private Socket socketDestino;
		 private ByteArrayOutputStream arquivoTemp;
		 
		 public Arquivo(String destino, String emitente, String nomeArquivo, String tamanho){
			 this.destino = destino;
			 this.emitente = emitente;
			 this.nomeArquivo = nomeArquivo;
			 this.tamanho = Integer.parseInt(tamanho);
		 }
		 
		 public void setSocket(Socket socket){
			 this.socket = socket;
		 }
		 
		 public void recebeFile(){
			 try{
					InputStream entradaArquivo = socket.getInputStream();
	                byte[] bytArquivo = new byte[1000];
	                arquivoTemp = new ByteArrayOutputStream();
	                int tmByt;
	                System.out.println("recebendo arquivo");
	                while((tmByt = entradaArquivo.read(bytArquivo)) > 0){
	                	 arquivoTemp.write(bytArquivo, 0, tmByt);
	                }
	                socket.close();
	                for(int x=0; client.size() > x;x++){
	   				 if(client.get(x).nome.equals(emitente))
	   					 client.get(x).redirecionarMSG(destino,"CON_FILE_SEND|"+emitente+"|"+nomeArquivo+"|"+tamanho);
	                }
				}catch(IOException e) {
					finalizar_arquivo(socket);
					terminal.addErroTerminal("[ERRO]: "+e.getMessage());
				}
		 }
		 public void enviarFile(){
			 try {
				OutputStream saidaArquivo = socketDestino.getOutputStream();
				System.out.println("Enviar arquivo");
				arquivoTemp.writeTo(saidaArquivo);
				finalizar_arquivo(socket);
			 } catch (IOException e) {
				 finalizar_arquivo(socket);
				 terminal.addErroTerminal("[ERRO]: "+e.getMessage());
			}
		 }
		 @Override
		 public void run(){
			 recebeFile();
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