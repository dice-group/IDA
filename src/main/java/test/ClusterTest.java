package test;


import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;

import weka.filters.Filter;
import weka.associations.Apriori;
import weka.classifiers.meta.AutoWEKAClassifier;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.EM;
import weka.core.Instance;
import weka.core.Instances;

public class ClusterTest  {

    public static void main(String a[])
    {
        
        try {
           
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File("./src/main/resources/test.csv"));
            Instances data = loader.getDataSet();
            System.out.println(data.numAttributes());
           // data.setClassIndex(data.numAttributes() - 1);

            // generate data for clusterer (w/o class)
            
            /*
             * Remove filter = new Remove(); filter.setAttributeIndices("" +
             * (data.classIndex() + 1)); filter.setInputFormat(data); Instances
             * dataClusterer = Filter.useFilter(data, filter);
             */
             
            // train clusterer
            EM clusterer = new EM();
            // set further options for EM, if necessary...
            clusterer.buildClusterer(data);

            // evaluate clusterer
            ClusterEvaluation eval = new ClusterEvaluation();
            eval.setClusterer(clusterer);
            eval.evaluateClusterer(data);
            
           
            

            // print results
            System.out.println(eval.clusterResultsToString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    
        
    }

      
        
    


