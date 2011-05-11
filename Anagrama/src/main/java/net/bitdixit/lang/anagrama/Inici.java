package net.bitdixit.lang.anagrama;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class Inici extends JApplet
{
	private static final long serialVersionUID = 1L;
	public static String TRENCA_0 = "Nomès 1 mot";;
	public static String TRENCA_1 = "Màx 2 mots";
	public static String TRENCA_2 = "Màx 3 mots";
	public static String TRENCA_3 = "Màx 4 mots";

	public static String TEXT_CERCA = "Cerca";
	public static String TEXT_PARA = "Para";
	
	
	Anagrama anagrama = null;
	private JTextField textMot;
	private JButton btnCercaPara = new JButton(TEXT_CERCA);
	private JTextArea textAnagrames = new JTextArea();
	private JComboBox comboBox = new JComboBox();

	Search backgroundWork=null;
	private final JLabel labelMotsCercats = new JLabel("");
	private final JScrollPane scrollPane = new JScrollPane();
	
	class Init extends SwingWorker<Void, Object> 
	{
		protected Void doInBackground() throws Exception
		{
			labelMotsCercats.setText("Calculant velocitat...");
			anagrama = new Anagrama();
			labelMotsCercats.setText("");
			btnCercaPara.setEnabled(true);
			return null;
		}		
	}
	
    class Search extends SwingWorker<Void, Object> implements Anagrama.FoundCallback
    {
       long     n=1;
	   boolean process;
	   public Search()
	   {
		   process=true;
	   }
	   
	   public void queryStop()
	   {
		   btnCercaPara.setEnabled(false);
		   process=false;
	   }
	   
       @Override
       public Void doInBackground()
       {
		   btnCercaPara.setText(TEXT_PARA);
		   textAnagrames.setText("");
    	   
    	   anagrama.permute(getWord(), this);
    	   return null;
       }
       @Override
       protected void done() 
       {
		   btnCercaPara.setText(TEXT_CERCA);
		   btnCercaPara.setEnabled(true);
		   //labelMotsCercats.setText("");
       }

		public void onWord(String word)
		{
			textAnagrames.append(word+"\n");
			
		}
	
		public boolean onQueryContinue() {
			n++;
			long cm = n / 100000;
			if (n%100000==0) labelMotsCercats.setText(""+(cm/10)+"."+(cm%10)+" millons permutacions");
			return process;
		}
   }	
	
    public String getWord()
    {
 	   String trenca="";   	   
	   if (comboBox.getSelectedItem().equals(TRENCA_0)) trenca="";
	   else if (comboBox.getSelectedItem().equals(TRENCA_1)) trenca=" ";
	   else if (comboBox.getSelectedItem().equals(TRENCA_2)) trenca="  ";
	   else if (comboBox.getSelectedItem().equals(TRENCA_3)) trenca="   ";
   	   return textMot.getText().trim()+trenca;
    }
    
    public void updateEstimedTime()
    {
		labelMotsCercats.setText("Temps estimat: "+anagrama.getEstistimedTime(getWord()));
    }
    
	/**
	 * Create the applet.
	 */
	public Inici()
	{
		getContentPane().setBackground(new Color(0xCC,0xCC,0xCC));
		getContentPane().setLayout(null);
		
		JLabel label = new JLabel("CERCA ANAGRAMES V0.1");
		label.setBounds(16, 6, 196, 16);
		getContentPane().add(label);
		
		JLabel label_1 = new JLabel("Mot");
		label_1.setBounds(16, 34, 61, 16);
		getContentPane().add(label_1);
		
		textMot = new JTextField();
		textMot.setBounds(46, 28, 184, 28);
		textMot.getDocument().addDocumentListener(new DocumentListener() {			
			public void removeUpdate(DocumentEvent e) {
				updateEstimedTime();
			}
			public void insertUpdate(DocumentEvent e) {
				updateEstimedTime();
			}
			public void changedUpdate(DocumentEvent e) {
				updateEstimedTime();
			}
		});
		getContentPane().add(textMot);
		textMot.setColumns(10);
		
		comboBox.setModel(new DefaultComboBoxModel(new String[] {TRENCA_0, TRENCA_1, TRENCA_2, TRENCA_3}));
		comboBox.setBounds(237, 29, 196, 27);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateEstimedTime();
			}
		});
		getContentPane().add(comboBox);
		
		btnCercaPara.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (btnCercaPara.getText().equals(TEXT_CERCA))
				{
					backgroundWork = new Search();
					backgroundWork.execute();					
				} else
				{
					backgroundWork.queryStop();										
				}
			}
		});
		btnCercaPara.setBounds(337, 241, 96, 29);
		btnCercaPara.setEnabled(false);
		getContentPane().add(btnCercaPara);
		labelMotsCercats.setBounds(16, 246, 251, 16);
		
		getContentPane().add(labelMotsCercats);
		scrollPane.setBounds(16, 62, 417, 176);
		
		getContentPane().add(scrollPane);
		scrollPane.setViewportView(textAnagrames);
		new Init().execute();
	}
}