package DataSetReader;

import java.io.IOException;

public class MainClass {
	public static void main(String ar[])
	{
		DatasetReader r = new DatasetReader();
		try {
			r.FileReader("IRIS.csv");
			r.createMetainfo();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
