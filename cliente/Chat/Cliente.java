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
import Chat.InfoChat.Arquivo;

public class Cliente implements Runnable{
	public String ipServidor;
	public int porta;
	public String nome;
	public boolean conectado;
	
	private Janela_Chat chatFrame;
	private Socket socket;
	private ArquivoSocket sockFile;
	private DataOutputStream saida;
	private DataInputStream entrada;
	private boolean loop = true;

	public Cliente(String ip, String porta, String nome){
		try{
			this.ipServidor = ip;
			this.porta = Integer.parseInt(porta);
			this.nome = nome;
			socket = new Socket(this.ipServidor, this.porta);
			saida = new DataOutputStream(socket.getOutputStream());
		    entrada = new DataInputStream(socket.getInputStream());
		    saida.writeUTF("CON_CLIENTE|"+nome);
		    String resposta = entrada.readUTF();
		    if(resposta.equals("ACEITO")){
			    conectado = true;
			    JOptionPane.showMessageDialog(null,"Conexão realizada com sucesso!");
		    }else{
		     conectado = false;
		     JOptionPane.showMessageDialog(null,"Nome já existe, por favor usar outro nome.","Recusado",JOptionPane.ERROR_MESSAGE);
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
				String nomeArquivo;
				StringTokenizer splitMsg = new StringTokenizer(dados);
				switch(splitMsg.nextToken("|")){
				case "SINGLE_MSG"://ESTRUTURA [OPCAO] | [EMITENTE] | [MENSAGEM]
					emitente = splitMsg.nextToken("|");
					while(splitMsg.hasMoreTokens()){
						msg = msg + splitMsg.nextToken("|");
					}
					chatFrame.incluirMsg(emitente, emitente, msg);
					break;
				case "GRUPO_MSG"://ESTRUTURA [OPCAO] | [NOME DO GRUPO] | [EMITENTE] | [MENSAGEM]
					nomeGrupo = splitMsg.nextToken("|");
					emitente = splitMsg.nextToken("|");
					while(splitMsg.hasMoreTokens()){
						msg = msg + splitMsg.nextToken("|");
					}
					chatFrame.incluirMsg(nomeGrupo, emitente, msg);
					break;
				case "CON_FILE_SEND"://ESTRUTURA [OPCAO] | [EMITENTE] | [NOME DO ARQUIVO] | [TAMANHO DO ARQUIVO]
					emitente = splitMsg.nextToken("|");
					nomeArquivo = splitMsg.nextToken("|");
					chatFrame.incluirFile(emitente,emitente,nomeArquivo, splitMsg.nextToken("|"));
					break;
				case "CON_FILE_SEND_GRUPO"://ESTRUTURA [OPCAO] | [EMITENTE] |[NOME DO GRUPO]| [NOME DO ARQUIVO] | [TAMANHO DO ARQUIVO]
					emitente = splitMsg.nextToken("|");
					nomeGrupo = splitMsg.nextToken("|");
					nomeArquivo = splitMsg.nextToken("|");
					chatFrame.incluirFile(emitente,nomeGrupo,nomeArquivo, splitMsg.nextToken("|"));
					break;
				case "CLIENTES_ON"://ESTRUTURA [OPCAO] | {(LOOP) [NOME] | [IP] }
					while(splitMsg.hasMoreTokens()){
						String nometemp = splitMsg.nextToken("|");
						String iptemp = splitMsg.nextToken("|");
						chatFrame.addLabelClientON(nometemp,iptemp );
					}
					break;
				case "CLIENTE_OFF"://ESTRUTURA [OPCAO] | [NOME]
					chatFrame.removeLabelClientON(splitMsg.nextToken());
					break;
				case "GRUPOS_ON"://ESTRUTURA [OPCAO] | {(LOOP LISTA DE GRUPOS)[NOME DO GRUPO] | [LISTA DE USUARIOS DELIMITADOS POR ;] }
					while(splitMsg.hasMoreTokens()){
						nomeGrupo = splitMsg.nextToken("|");
						msg = splitMsg.nextToken("|");
						chatFrame.addLabelGrupo(nomeGrupo, msg);
					}
					break;
				case "GRUPO_ADD"://ESTRUTURA [OPCAO] | [NOME DO GRUPO] | [LISTA DE USUARIOS DELIMITADOS POR ;]
					nomeGrupo = splitMsg.nextToken("|");
					msg = splitMsg.nextToken("|");
					chatFrame.addLabelGrupo(nomeGrupo, msg);
					if(!nomeGrupo.equals("Chat All"))
						chatFrame.showNotfication("Grupo Adicionado", "Você foi adicionado ao grupo: "+nomeGrupo);
					break;
				case "USER_SAIU_GRUPO"://ESTRUTURA [OPCAO] | [NOME DO GRUPO] | [USUARIO]
					nomeGrupo = splitMsg.nextToken("|");
					emitente = splitMsg.nextToken("|");
					chatFrame.excUserDoGrupo(nomeGrupo,emitente);
					chatFrame.incluirMsg(nomeGrupo,emitente,"O USUARIO '"+emitente+"' SAIU DO GRUPO\n");
					break;
				case "USER_ENTROU_GRUPO"://ESTRUTURA [OPCAO] | [NOME DO GRUPO] | [USUARIO]
					nomeGrupo = splitMsg.nextToken("|");
					emitente = splitMsg.nextToken("|");
					chatFrame.addUserNoGrupo(nomeGrupo,emitente);
					chatFrame.incluirMsg(nomeGrupo,emitente,"O USUARIO '"+emitente+"' ENTROU NO GRUPO\n");
					break;
				case "ADD_USERGRUPO"://ESTRUTURA [OPCAO] | [RESPOSTA DO SERVIDOR]
					String resposta = splitMsg.nextToken("|");
					if(resposta.equals("ADICIONADO")){
						chatFrame.showNotfication("Usuário Adicionado","Adicionado ao grupo com sucesso");
					}else if(resposta.equals("RECUSADO")){
						chatFrame.showNotfication("Usuário Recusado","Não foi possivel adicionar o usuário pois ele já está no grupo");
					}
					break;

				default:// COMANDO NAO IDENTIFICADO
					JOptionPane.showMessageDialog(null, "Não foi possivel processar o comando", "Falha", JOptionPane.WARNING_MESSAGE);
					break;
				}
			}catch(SocketException e){
				loop = false;
				JOptionPane.showMessageDialog(null, "Você foi desconectado do servidor", "Desconectado", JOptionPane.WARNING_MESSAGE);
				chatFrame.setVisible(false);
				Main.frame.setVisible(true);
			}catch(IOException e) {
				JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	public void enviar(String opt,String destino, String msg){
		try{ ///ESTRUTURA DE ENVIO [OPCAO] | [DESTINO] | [MENSAGEM]
			saida.writeUTF(opt+ "|" +destino+ "|" +msg);
		}catch(SocketException e){
			JOptionPane.showMessageDialog(null, "Não foi possivel enviar a mensagem ao servidor", "Erro", JOptionPane.ERROR_MESSAGE);
		}catch(IOException e) {
		    JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}	
	public void startConexao_file(String tipo, String emitente, String nomeChat, Arquivo file, int tipoChat){
		sockFile = new ArquivoSocket(ipServidor, porta, tipo, nomeChat, nome, file, tipoChat);
		if(tipoChat == 1) sockFile.setEmitente(emitente);
		sockFile.start();
	}
	public void criarGrupo(String grupo, DefaultListModel<String> usuarios){
		try{//ESTRUTURA DE CRICAO DE GRUPO [OPCAO] | [NOME DO GRUPO] | [LISTA DE USUARIOS]
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
	public void sairGrupo(String grupo){
		try {// ESTRUTURA DE DEIXAR GRUPO [OPCAO] | [NOME DO GRUPO]
			saida.writeUTF("SAIR_GRUPO|"+grupo);
			chatFrame.showNotfication("Mensagem", "Você saiu do grupo '"+grupo+"'");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void fechar(){
	   try{// COMANDO PARA FECHAR OS SOCKET E EXCLUIR O CLIENTE DAS LISTAS DO SERVIDOR
		   saida.writeUTF("FECHAR");
		   socket.close();
	   }catch(IOException e) {
		   JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
	   }
	}
}