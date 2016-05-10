package Chat;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
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
	private String usuario;
	private String meuNome;
	private Arquivo obArquivo;
	private BufferedInputStream buffArquivo;
	private ImageIcon iconOpenFile = new ImageIcon("C:\\Chat\\img\\icon-open-file.png");
	public ArquivoSocket(String ip, int porta, String tipo, String usuario, String meuNome,Arquivo file ){
		try {
			socketFile = new Socket(ip, porta);
			saida = new DataOutputStream(socketFile.getOutputStream());
			this.tipo = tipo;
			this.usuario = usuario;
			this.meuNome = meuNome;
			this.obArquivo = file;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void enviar(){
		try {
			saida.writeUTF("CON_FILE_REQUEST|"+meuNome+"|"+usuario+"|"+obArquivo.arquivo.getName()+"|"+obArquivo.arquivo.length());
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
			saida.writeUTF("CON_FILE_ACCEPT|"+meuNome+"|"+usuario+"|"+obArquivo.arquivo.getName());
			FileOutputStream fos = new FileOutputStream(obArquivo.arquivo.getAbsolutePath());
			entradaStream = socketFile.getInputStream();
			int tmPorcento = (int)Math.ceil(obArquivo.tamanho / 100);
			if(tmPorcento == 0) tmPorcento = 1;
			byte[] bytArquivo = new byte[1000];
			int tmByt;
			int tmLido = 0;
            System.out.println("recebendo arquivo");
            while((tmByt = entradaStream.read(bytArquivo)) > 0){
            	tmLido = tmLido + tmByt;
            	obArquivo.barraProgresso.setValue(tmLido/tmPorcento);
	            fos.write(bytArquivo, 0, tmByt);
            }
            obArquivo.barraProgresso.setValue(100);
            obArquivo.barraProgresso.setString("Concluido");
            obArquivo.button.setIcon(iconOpenFile);
            obArquivo.button.addActionListener(actionOpenFile);
            obArquivo.button.setToolTipText("Abrir Arquivo");
            fos.flush();
            fos.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
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
				desktop.edit(obArquivo.arquivo);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e, "Erro", JOptionPane.ERROR_MESSAGE);
			}
		}
	};
}