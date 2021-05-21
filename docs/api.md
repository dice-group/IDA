# RESTful API Reference

## Chat Message
 `Response  to the user's message.`
1. *`URL`* 
    /chatmessage
2. *`Method`*
    POST
3. *`URL PARAMS`*

    ***Required***:  Message - chat message from the user.\
    `For Example: load covid19 dataset `

    ***Request Payload***: Payload for the message sent.\
      `For Example : {activeDS: "" activeTable: "" activeTableData: null key: 0.34837326682125935 message: "load  covid19 dataset"}` 
      `{renderSuggestion: false sender: "user" senderName: "user" suggestionParams: null temporaryData: false timestamp: 1621592279208}`

    ***Response payload***: Payload Response for the  chatmessage sent by the user.\
      `For example: { "dsData": [{"data": [] }], "activeTable": "", "label": "covid19", "activeDS": ""}`

## List all entities
 `Name and Columns for dialogflow .`
1. *`URL`* 
    /addentities
2. *`Method`*
    POST
3. *`URL PARAMS`*

    ***Required***:  entity update request.\
    `For Example: During upload dataset in case user wants to update the entity`

    ***Request Payload***: Payload for the message sent.
      `For Example : metadata: {dsDesc: "flare", dsName: "flare", filesMd: [{colCount: 2, displayName: "flare_1.csv",…}]}`
      `fileColMd: [{colAttr: "id", colDesc: "id", colIndex: 1, colName: "id", colType: "string", isUnique: true},…]`
      The entity name colName in fileColMd was changed from `"id"` to `"FLARECOL1"`.
      `fileColMd: [{colAttr: "id", colDesc: "id", colIndex: 1, colName: "FLARECOL1", colType: "string", isUnique: true},…]`

    ***Response payload***: Payload Response for the  chatmessage sent by the user.\
      `For example: {"message": "Successfully uploaded dataset"}`


## Dataset exist ?
 `To check if a dataset exist or not.`
1. *`URL`* 
    /datasetexist
2. *`Method`*
    GET
3. *`URL PARAMS`*

    ***Required***:  Dataset name.\
    `For Example: Covid19`

    ***Request Payload***: Payload for the message sent.
      ` For Example: {"dsDesc": "","dsName": "covid19","filesMd": [],"fileDesc": "","fileName": "flare_1.csv","rowCount": 252}`

    ***Response payload***: Payload Response for the  chatmessage sent by the user.\
      `For Example: {"message": "dataset with name covid19 already exists!"}`

## Add Dataset
 `To add a new dataset.`
1. *`URL`* 
    /adddataset
2. *`Method`*
    POST
3. *`URL PARAMS`*

    ***Required***:  Dataset name.\
    `For Example: IDA1`

    ***Request Payload***: Payload for the message sent.
      `For Example : {activeDS: "" activeTable: "" activeTableData: null key: 0.34837326682125935 message: "add IDA1"}` 
      `{renderSuggestion: false sender: "user" senderName: "user" suggestionParams: null temporaryData: false timestamp: 1621592279208}`

    ***Response payload***: Payload Response for the  chatmessage sent by the user.\
      `For example: {"message": "Sure. I am going to open dataset upload wizard!"}`