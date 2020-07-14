import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

public class code {
    private static DecimalFormat df2 = new DecimalFormat("#.##");

	public static void main(String args[]) throws IOException {
		int peering_col = -1, k=3;
		String input="usa_presidents.tsv";

		//write to file in 'output folder'
		inferUsefulNegations(input, peering_col, k);
		
	}

	private static void inferUsefulNegations(String input, int peering_col, int k) throws IOException {
		
		//loading data
		System.out.println("Loading data...");
		HashMap<String, Set<String>> ST=new HashMap<String, Set<String>>();
		HashMap<String, HashMap<String, Set<String>>> E=new HashMap<String, HashMap<String, Set<String>>>();
		Set<String> table_as_triples=new HashSet<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(input));
		
		String[] features = null;
		
		String line;
		if ((line = br.readLine()) != null)
		{
			line=line.trim();
			features=line.split("\t");
		}
		
		while ((line = br.readLine()) != null)
		{
			String parts[]=line.split("\t");
			String entity=parts[0].trim();
			if(entity.length()==0) continue;

			HashMap<String, Set<String>> st=null;
			if(E.get(entity)==null)
				st = new HashMap<String, Set<String>>();
			else
				st = E.get(entity);


			for(int i=1; i<parts.length; i++)
			{
				if(parts[i].trim().length()==0 || parts[i].trim().equals("null")) continue;
				
				if(st.get(features[i].trim())==null) {
					Set<String> set= new HashSet<String>();
					String comma[]=parts[i].split(",");
					for(int j=0; j<comma.length; j++)
						set.add(comma[j].trim());
					st.put(features[i].trim(), set);
				}
				else {
					Set<String> set= st.get(features[i].trim());
					String comma[]=parts[i].split(",");
					for(int j=0; j<comma.length; j++)
						set.add(comma[j].trim());
					st.put(features[i].trim(), set);
				}
				
				E.put(entity, st);
				
				
				Set<String> e=null;
				String comma[]=parts[i].split(",");
				for(int j=0; j<comma.length; j++)
				{
					if(ST.get(features[i].trim()+"; "+comma[j].trim())==null)
					{
						e=new HashSet<String>();
						e.add(entity);
					}
					else
					{
						e=ST.get(features[i].trim()+"; "+comma[j].trim());
						e.add(entity);
					}
					ST.put(features[i].trim()+"; "+comma[j].trim(), e);
					table_as_triples.add(entity+features[i].trim()+comma[j].trim());
				}
			}			
		}
		br.close();
		
		//output file
		BufferedWriter wr = new BufferedWriter(new FileWriter("output\\negations_"+peering_col+"_"+k+"_"+input, true));	
				
		//inferring for every entity
		System.out.println("Inferring useful negations...");
		for(String e: E.keySet()) {
			Set<String> peers = getpeers(e, peering_col, E, ST, features);

			HashMap<String, Double> candidates = new HashMap<String, Double>();
			for(String p: peers)
			{
				if(p.equals(e)) continue;
				HashMap<String, Set<String>> st = E.get(p);
				
				for (Entry<String, Set<String>> entry : st.entrySet())
				{
					String x = entry.getKey();
					
					for(String y: entry.getValue()) {

						if(table_as_triples.contains(e+x+y)) {
							continue;
						}
						if(candidates.get(x+"; "+y)==null)
							candidates.put(x+"; "+y, 1.0);
						else
							candidates.put(x+"; "+y, candidates.get(x+"; "+y)+1.0);	
					}
				}
			}
			
			writetopk(e, candidates, k, wr, peers);
		}
		wr.close();
		System.out.println("Check the 'output' folder!");
	}

	private static void writetopk(String e, HashMap<String, Double> candidates, int k, BufferedWriter wr, Set<String> peers) throws IOException {
		
		wr.write(e+"\t");
		
		String results="[";
		for(int i=1; i<=k; i++)
		{
			String key = null;
			
			try { key=Collections.max(candidates.entrySet(), Map.Entry.comparingByValue()).getKey(); }
			catch (NoSuchElementException exception) {continue;}
			Double score=(candidates.get(key))/(1.0*peers.size());
			results+=i+": ¬("+key+")="+df2.format(score)+" ";
			candidates.put(key, -1.0);
		}
		results=results.trim()+"]";
		wr.write(results+"\n");
	}

	private static Set<String> getpeers(String e, int peering_col, HashMap<String, HashMap<String, Set<String>>> E, HashMap<String, Set<String>> ST, String [] features) {
		
		if(peering_col < 0 || peering_col >= features.length)
			return E.keySet();
		else
		{
			String peering_feature = features[peering_col];

			if(E.get(e)==null)
				return E.keySet();
			else if (E.get(e).get(peering_feature)==null)
				return E.keySet();
			else 
			{
				Set<String> values = E.get(e).get(peering_feature);
				Set<String> peers=new HashSet<String>();
				for(String v: values)
				{
					peers.addAll(ST.get(peering_feature+"; "+v));
				}
				
				if(peers==null || peers.size()<3)
					return E.keySet();
				
				return peers;
			}
		}
	}
}
