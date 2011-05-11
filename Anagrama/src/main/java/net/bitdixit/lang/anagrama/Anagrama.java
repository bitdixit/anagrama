package net.bitdixit.lang.anagrama;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Hashtable;

public class Anagrama
{
	public interface FoundCallback{ public void onWord(String word); public boolean onQueryContinue();  }
	
	static public String DUMMY="";
	static public Hashtable<String,String> m_words;

	private BigInteger m_speed; 
	
	public void calcSpeed()
	{
		long t = System.currentTimeMillis();
		permute("         ", new FoundCallback() {
			public void onWord(String word) {
			}
			public boolean onQueryContinue() {
				return true;
			}
		});
		long speed=(long)(1000*(362880L/(System.currentTimeMillis()-t)));
		m_speed=new BigInteger(""+speed);
	}
	
	Hashtable<String,String> foundWords=new Hashtable<String, String>();
	
	public void word(String permutatedWord, FoundCallback foundCallback )
	{
		String words=permutatedWord.replace("  ", " ");
		String[] wordbreak=words.split(" ");
		String build="";
		for (String w : wordbreak)
		{
			if (!m_words.containsKey(w)) return;
			String nw = m_words.get(w);
			if (build.length()>0) build+=" ";
			build+=nw;
		}
		if (!foundWords.containsKey(build))
		{
			foundWords.put(build, DUMMY);
			foundCallback.onWord(build);
		}
	}
	
    public void permute(String word, FoundCallback foundCallback)
    {
    	int[] indices;
    	char[] elements = word.toCharArray();
    	PermutationGenerator x = new PermutationGenerator (elements.length);
    	StringBuffer permutation;
    	while (x.hasMore ()) {
    	  if (!foundCallback.onQueryContinue()) return;
    	  permutation = new StringBuffer ();
    	  indices = x.getNext ();
    	  for (int i = 0; i < indices.length; i++) {
    	    permutation.append (elements[indices[i]]);
    	  }
    	  word(permutation.toString (),foundCallback);
    	}
	}
    
    public String getEstistimedTime(String word)
    {
    	BigInteger permutations=PermutationGenerator.getFactorial(word.length());
    	BigInteger res=permutations.divide(m_speed);
    	
    	if (res.compareTo(new BigInteger(""+60)) < 0 ) return res.toString()+"s";
    	if (res.compareTo(new BigInteger(""+60*60)) < 0 ) return res.divide(new BigInteger(""+60)).toString()+"m";
    	if (res.compareTo(new BigInteger(""+24*60*60)) < 0 ) return res.divide(new BigInteger(""+60*60)).toString()+"h";
    	if (res.compareTo(new BigInteger(""+(24*60*60*365L))) < 0 ) return res.divide(new BigInteger(""+60*60*24)).toString()+"d";
    	return res.divide(new BigInteger(""+60*60*24)).toString()+" anys";
    }
    
	public void adddictword(String word)
	{
		String pl=word.toLowerCase();
		pl=pl.replace('ˆ', 'a');
		pl=pl.replace('', 'e');
		pl=pl.replace('Ž', 'e');					
		pl=pl.replace('’', 'i');
		pl=pl.replace('˜', 'o');
		pl=pl.replace('—', 'o');
		pl=pl.replace('œ', 'u');					
		pl=pl.replace("lál", "ll");					
		m_words.put(pl,word);	
	}
	public void loaddict(String dictfile) throws Exception
	{
		InputStream is = Anagrama.class.getResourceAsStream(dictfile);
		if (is==null) throw new IOException(dictfile+" not found as resource");
		m_words = new Hashtable<String, String>(60000);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is,"UTF8"));
		while (true)
		 {
			 String line=bufferedReader.readLine();
			 if (line==null) return;
			 try
			 {
				String[] tok=line.split("/",2);
				String word=tok[0];
				if (word.length()>=3) adddictword(word);
			 } catch (Exception e)
			 {
				 throw e;
			 }
		 }
	}
	public Anagrama() 
	{
		try { loaddict("/catala.dicc");	} catch (Exception e) { e.printStackTrace(); }
		calcSpeed();
	}
}