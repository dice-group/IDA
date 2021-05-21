# Fuseki 
Fuseki in IDA is used to  store RDF(data is stored in Subject object and predicate format) data and we can query using Sparqle.

*For example*: Details of visualization in RDF.

    [{visualization:bar_chart rdf:type owl:NamedIndividual ,ivoc:Visualization ;
        ivoop:hasInformation information:BarChart ;
        ivoop:hasParam parameter:x_axis ,parameter:y_axis ;
        rdfs:label "Bar Chart"@en .}]

    [{information:BarChart rdf:type owl:NamedIndividual ,ivoc:Information ;
        ivoop:hasReference reference:BarChart ;
        dc:description "A bar chart or bar graph is a chart that presents grouped data with rectangular bars with lengths 
        proportional to the values that they represent."@en ;
        rdfs:label "Bar Chart Information"@en .}]

