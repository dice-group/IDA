# Add/Modify

1. Create RDF data for the visualization
    1. Visualization details (params, information page, possible instances) (Create entities).
    2. Suggestion details
2. Dialogflow intents.
    1. Main intent, intent to fetch each parameter
3. Backend logic
    1.  Add the intent to exisiting list of intents(ida.model.Intent.java) and their action handler(ida.action.process.ActionExecutor.java)
    2. Define data model for visualization (Response to UI)
    3. Implement a function to populate the visualization data into model(ida.action.def.VisualizeAction.java)
    4. Create a new UI Action constant() and use it as the action code in the http response
4. UI Logic
    1. Add a new UI constant to exisiting list of constants (IDA_CONSTANTS) and their action handler(idaChatbotActionHandler)  
    2. Create a component for the new visualization (visualization folder).
    3. define the visualization type to navigate to the above created component (ScrollableTabsButtonAuto).