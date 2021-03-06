package Chat;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import Chat.InfoChat.Arquivo;

public class ArquivoSocket extends Thread{
	private Socket socketFile;
	private DataOutputStream saida;
	private InputStream entradaStream;
	private OutputStream saidaStream;
	private String tipo;
	private String chatNome;
	private String meuNome;
	private String emitente;
	private Arquivo obArquivo;
	private BufferedInputStream buffArquivo;
	private int tipoChat;
	private ImageIcon iconOpenFile = new ImageIcon("C:\\Chat\\img\\icon-open-file.png");
	private ImageIcon iconXFile = new ImageIcon("C:\\Chat\\img\\icon-xFile.png");
	public ArquivoSocket(String ip, int porta, String tipo, String chatNome, String meuNome,Arquivo file, int tipoChat ){
		try {
			socketFile = new Socket(ip, porta);
			saida = new DataOutputStream(socketFile.getOutputStream());
			this.tipo = tipo;
			this.chatNome = chatNome;
			this.meuNome = meuNome;
			this.obArquivo = file;
			this.tipoChat = tipoChat;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void setEmitente(String emitente){
		this.emitente = emitente;
	}
	public void enviar(){
		try {
			saida.writeUTF("CON_FILE_REQUEST|"+meuNome+"|"+chatNome+"|"+tipoChat+"|"+obArquivo.arquivo.getName()+"|"+obArquivo.arquivo.length());
			int tmPorcento = (int)Math.ceil(obArquivo.arquivo.length() / 100);
			if(tmPorcento == 0) tmPorcento = 1;
			buffArquivo = new BufferedInputStream(new FileInputStream(obArquivo.arquivo));
			saidaStream = socketFile.getOutputStream();
			byte[] bytArquivo = new byte[1000];
			int tmByt; 
			int tmLido = 0;
			 while((tmByt = buffArquivo.read(bytArquivo)) > 0){
				 tmLido = tmLido + tmByt;
				 obArquivo.barraProgresso.setValue(tmLido/tmPorcento);
				 saidaStream.write(bytArquivo, 0, tmByt);
			 }
			 obArquivo.barraProgresso.setValue(100);
			 obArquivo.barraProgresso.setString("Concluido");
			 obArquivo.button.setIcon(iconOpenFile);
			 obArquivo.button.addActionListener(actionOpenFile);
			 obArquivo.button.setToolTipText("Abrir Arquivo");
			 saidaStream.flush();
			 saidaStream.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void receber(){
		try{
			if(tipoChat == 0)
				saida.writeUTF("CON_FILE_ACCEPT|"+meuNome+"|"+chatNome+"|"+obArquivo.arquivo.getName());
			else if(tipoChat == 1)
				saida.writeUTF("CON_FILE_ACCEPT_GRUPO|"+meuNome+"|"+chatNome+"|"+emitente+"|"+obArquivo.arquivo.getName());
				if(!socketFile.isClosed()){
					FileOutputStream fos = new FileOutputStream(obArquivo.arquivo.getAbsolutePath());
					entradaStream = socketFile.getInputStream();
					int tmPorcento = (int)Math.ceil(obArquivo.tamanho / 100);
					if(tmPorcento == 0) tmPorcento = 1;
					byte[] bytArquivo = new byte[1000];
					int tmByt;
					int tmLido = 0;
		            while((tmByt = entradaStream.read(bytArquivo)) > 0){
		            	tmLido = tmLido + tmByt;
		            	obArquivo.barraProgresso.setValue(tmLido/tmPorcento);
			            fos.write(bytArquivo, 0, tmByt);
		            }
		            fos.flush();
		            fos.close();
		            if(tmLido > 0){
			            obArquivo.barraProgresso.setValue(100);
			            obArquivo.barraProgresso.setString("Concluido");
			            obArquivo.button.setIcon(iconOpenFile);
			            obArquivo.button.addActionListener(actionOpenFile);
			            obArquivo.button.setToolTipText("Abrir Arquivo");
		            }else{
		            	obArquivo.arquivo.delete();
		            	obArquivo.barraProgresso.setValue(0);
				        obArquivo.barraProgresso.setString("Excluido");
				        obArquivo.button.setIcon(iconXFile);
				        obArquivo.button.setToolTipText("Arquivo excluido");
		            }
				}
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
	private ActionListener actionOpenFile = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Desktop desktop = Desktop.getDesktop();
			try {
				if(obArquivo.arquivo.exists())
					desktop.edit(obArquivo.arquivo);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
			}
		}
	};
}