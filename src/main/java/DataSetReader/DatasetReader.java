package DataSetReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class DatasetReader {
	
	private String fileName;
	Instances data;
	public void createMetainfo() throws IOException
	{
		
		
		int index = fileName.indexOf(".");
		Calendar c = Calendar.getInstance(); 
        fileName = fileName.substring(0, index);
        BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/metainfo/" +fileName+ c.get(Calendar.MONTH)+ c.get(Calendar.YEAR)));
        writer.write(data.toSummaryString());
        writer.close();
	}

    public void FileReader(String fileName) throws IOException
    {
    	this.fileName = fileName;
        CSVLoader loader = new CSVLoader();
        //Loads the File
        
        loader.setSource(new File("./src/main/resources/" +fileName));
        //Create an instance of the dataset 
        data = loader.getDataSet();
        //Set class index of dataset
        data.setClassIndex(data.numAttributes() - 1);
    }

}
