package Chat;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class ArquivoSocket extends Thread{
	private Socket socketFile;
	private DataOutputStream saida;
	private DataInputStream entrada;
	private InputStream entradaStream;
	private OutputStream saidaStream;
	private String tipo;
	private String usuario;
	private String meuNome;
	private File arquivo;
	private BufferedInputStream buffArquivo;
	public ArquivoSocket(String ip, int porta, String tipo, String usuario, File arquivo, String meuNome){
		try {
			socketFile = new Socket(ip, porta);
			saida = new DataOutputStream(socketFile.getOutputStream());
			entrada = new DataInputStream(socketFile.getInputStream());
			this.tipo = tipo;
			this.usuario = usuario;
			this.meuNome = meuNome;
			this.arquivo = arquivo;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setDados(){
		
	}
	public void enviar(){
		try {
			saida.writeUTF("CON_FILE_REQUEST|"+meuNome+"|"+usuario+"|"+arquivo.getName());
			String dados = entrada.readUTF();
			StringTokenizer splitMsg = new StringTokenizer(dados);
			switch(splitMsg.nextToken("|")){
			case "CON_RESPOSTA":
				if(splitMsg.nextToken("|").equals("OK")){
					int arqTamanho = (int)Math.ceil(arquivo.length() / 100);
					buffArquivo = new BufferedInputStream(new FileInputStream(arquivo));
					saidaStream = socketFile.getOutputStream();
					byte[] bytArquivo = new byte[1000];
					int tmByt, porcento = 0;
					 while((tmByt = buffArquivo.read(bytArquivo)) > 0){
						 porcento = porcento + tmByt;
						 System.out.println(porcento/arqTamanho);
						 saidaStream.write(bytArquivo, 0, tmByt);
					 }
					 System.out.println("ARQUIVO ENVIADO COM SUCESSO");
					 saidaStream.flush();
					 saidaStream.close();
				}else{
					System.out.println("conexao recusada");
				}
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void receber(){
		try{
			saida.writeUTF("CON_FILE_ACCEPT|"+meuNome+"|"+usuario);
			FileOutputStream fos = new FileOutputStream("C:\\lucas\\"+arquivo.getName());
			entradaStream = socketFile.getInputStream();
			byte[] bytArquivo = new byte[1000];
			int tmByt;
            System.out.println("recebendo arquivo");
            while((tmByt = entradaStream.read(bytArquivo)) > 0){
	           	 System.out.println(tmByt);
	           	 fos.write(bytArquivo, 0, tmByt);
            }
            fos.flush();
            fos.close();
          	saida.writeUTF("ARQUIVO_OK");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		if(tipo.equals("enviar"))
			enviar();
		else if(tipo.equals("receber"))
			receber();
	}
}
