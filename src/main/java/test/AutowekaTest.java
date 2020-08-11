package test;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.AutoWEKAClassifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class AutowekaTest {

    public static void main(String ar[])
    {
        
        DataSource source;
        try {
            source = new DataSource("./src/main/resources/iris.arff");
            String[] args = {"-t", "./src/main/resources/iris.arff", "-seed", "1", "-no-cv", "-timeLimit", "1"};
            String out = Evaluation.evaluateModel(new AutoWEKAClassifier(), args);
            System.out.println(out);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
      
        
    }
}
