package bloom;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;

class LineReader
{
  private String         line;    //  Line waiting to be returned by NEXT.
  private BufferedReader reader;  //  Where to read LINEs from.

//  Constructor. Make a new instance of LINE READER. It reads LINEs from a file
//  whose pathname is PATH.

  public LineReader(String path)
  {
    try
    {
      reader = new BufferedReader(new FileReader(path));
      line = reader.readLine();
    }
    catch (IOException ignore)
    {
      throw new IllegalArgumentException("Can't open '" + path + "'.");
    }
  }

//  HAS NEXT. Test if a LINE is waiting to be returned by NEXT.

  public boolean hasNext()
  {
    return line != null;
  }

//  NEXT. Return the current LINE from PATH and advance to the next LINE, if it
//  exists.

  public String next()
  {
    try
    {
      String temp = line;
      line = reader.readLine();
      return temp;
    }
    catch (IOException ignore)
    {
      throw new IllegalStateException("Can't read any more lines.");
    }
  }

}

class BitArray {
	
	private int[] array;
	private int M;
	
	public BitArray(int M){
		if (M<0){
			throw new IllegalArgumentException("M cannot be negative.");
		}
		else{
			array = new int[M/32 + 1];
			this.M = M;
		}
	}
	
	public boolean get(int n){
		int m = 0x80000000;
		if (n < 0 || n >= M){
			throw new IndexOutOfBoundsException("out of range");
		} 
		else{
			m = m >> n%32;
			if ((array[n/32] & m) == 0){
				return false;
			} 
			else
			{
				return true;
			}
		}
	}
	
	public void set(int n){
		int m = 0x80000000;
		if (n < 0 || n >= M){
			throw new IndexOutOfBoundsException ("out of range");
		} 
		else{
			m = m >> n%32;
			array[n/32] = array[n/32] | m;
		}
	}
}


class BloomFilter {
	
	private BitArray bArray;
	private int M;
	private int counter = 0;
	
	public BloomFilter(int M){
		if (M<0){
			throw new IllegalArgumentException("M cannot be negative.");
		}
		else{
			this.bArray= new BitArray(M);
			this.M = M;
		}
	}
	
	private int h1(String w)  {
		int key;
		key = w.hashCode();
		return Math.abs(key%M);
	}
	
	private int h2(String w) {
		int key = 0;
		  for (int i = 0; i < w.length(); i++) {
		    key = key * 31 + w.charAt(i);
		  }
		return Math.abs(key%M);
	}
	
	private int h3(String w){ // FNV hash
		int key = 0x811c9dc5;
		final int len = w.length();
        for(int i = 0; i < len; i++) {
            key ^= w.charAt(i);
            key *= 0x01000193;
        }
        return Math.abs(key%M);
	}
	
	
	public void add(String w){
		int key, key2, key3;
		key = h1(w);
		key2 = h2(w);
		key3 = h3(w);

		bArray.set(key);
		bArray.set(key2);
		bArray.set(key3);
		counter ++;
	}
	
	
	public boolean isIn(String w){
		int key, key2,key3;
		key = h1(w);
		key2 = h2(w);
		key3 = h3(w);
		
		if (!bArray.get(key) && !bArray.get(key2) && !bArray.get(key3)){
			return false;
		}
		else{
			return true;
		}
	}
	
	public double accuracy(){
		double prob;
		double calc1;
		calc1 = -3*(double)counter/M;
		calc1 = Math.exp(calc1);
		prob = Math.pow(1-calc1,3);
		return prob;
	}
	
}



class Driver {

	public static void main(String[] args) {
		  BloomFilter filter = new BloomFilter(14000);
	      LineReader reader = new LineReader("src/basic.txt");
	      while (reader.hasNext())
	      {
	        filter.add(reader.next());
	      }
	      
	      System.out.println();
	      
	      reader = new LineReader("src/basic.txt");
	      while (reader.hasNext())
	      {
	    	  boolean bool;
	    	  String word = reader.next();
	    	  bool = filter.isIn(word);
	    	  if (!bool){
	    		  System.out.println(word);
	    	  }

	      }
	      System.out.println("False positive report rate: " + filter.accuracy());
	      
	}

}
