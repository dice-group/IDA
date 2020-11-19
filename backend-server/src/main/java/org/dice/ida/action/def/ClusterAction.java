package org.dice.ida.action.def;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.util.DialogFlowUtil;
import org.dice.ida.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.clusterers.RandomizableClusterer;
import weka.clusterers.SimpleKMeans;

import java.util.*;

/**
 * Class to handle the Clustering implementation
 *
 * @author  Sourabh Poddar
 */
@Component
public class ClusterAction implements Action {

	@Autowired
	private DialogFlowUtil dialogFlowUtil;
	private String clusterMethod;
	private String fullIntentName;
	private SimpleKMeans clusterer;
	private String paramtertoChange;
	private String paramValue;
	private Map<String, Object> paramMap;
	private StringBuilder textMsg;

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse) {
		try {
			textMsg = new StringBuilder(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
			if (ValidatorUtil.preActionValidation(chatMessageResponse))
			{
				this.paramMap = paramMap;
				fullIntentName = paramMap.get(IDAConst.FULL_INTENT_NAME).toString();
				clusterMethod = getClusterMethod(fullIntentName);
				String parameterChangeChoice = getParameterChangeChoice(fullIntentName);
				paramtertoChange = getParameterToChange(fullIntentName);
				if(!clusterMethod.isEmpty()&&parameterChangeChoice.isEmpty())
				{
					clusterer = getClusterer(clusterMethod);
					textMsg = new StringBuilder("Okay!! Here is the list of default parameter and our suggested parameters\n");
					textMsg.append(getDefaultParmeterList() ) ;
					textMsg.append("\nWould you like to change any parameter?"  );
				}
				else if(!paramtertoChange.isEmpty() )
				{

					getnsetNewParamValue();




				}
				if(parameterChangeChoice.equals("no"))
				{
					textMsg = new StringBuilder("Here is your clustered data\n");
				}

				chatMessageResponse.setMessage(textMsg.toString());
				chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getnsetNewParamValue() throws Exception {
		switch (clusterMethod)
		{
			case "Kmeans":
				switch (paramtertoChange)
				{
					case "NoC":
						paramValue = paramMap.get(IDAConst.NUMBER_OF_CLUSTER).toString();
						if(!paramValue.isEmpty())
						{
							clusterer.setNumClusters(Integer.parseInt(paramValue.split(":")[1].substring(1,2)));
							textMsg = new StringBuilder("Value Changed!! Would you like to change another parameter");
							//TODO : set context of "Clustering kmeans follow up"
						}
				}

		}
	}
	private String getParameterToChange(String fullIntentName)
	{
		return fullIntentName.contains(" - ") && fullIntentName.split(" - ").length>3 ? fullIntentName.split(" - ")[3] : "";
	}
	private String getParameterChangeChoice(String fullIntentName)
	{
		return fullIntentName.contains(" - ") && fullIntentName.split(" - ").length>2 ? fullIntentName.split(" - ")[2] : "";
	}

	private String getDefaultParmeterList()
	{

		String[] optionList = clusterer.getOptions();
		StringBuilder options = new StringBuilder();
		for(int i=0;i<optionList.length;i+=2)
		{
			options.append("Parameter :"+optionList[i]+"\n");
			options.append("Current Value : "+optionList[i+1] +"\n");

		}
		return options.toString();
	}
	private SimpleKMeans getClusterer(String clusterMethod)
	{
		switch (clusterMethod)
		{
			case "Kmeans":
				return new SimpleKMeans();

		}
		return null;
	}
	private String getClusterMethod(String fullIntentName)
	{
		return fullIntentName.contains(" - ")  ? fullIntentName.split(" - ")[1] : "";
	}
}
