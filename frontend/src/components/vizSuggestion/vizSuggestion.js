import React, { Component } from "react";
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Accordion from '@material-ui/core/Accordion';
import AccordionSummary from '@material-ui/core/AccordionSummary';
import AccordionDetails from '@material-ui/core/AccordionDetails';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import Typography from '@material-ui/core/Typography';

import "./vizSuggestion.css";

export default class IDAVisualizationSuggestion extends Component {

  suggestionData = {};
  nodeId = "";
  tableName = "";

  constructor(props) {
    super(props);
    this.suggestionData = props.data;
    this.nodeId = props.nodeId;
    this.tableName = props.tableName;
  }

  render() {
    return <>
      <Typography varient="h5" className="mb-2 p-3">
        Suggested parameters for available visualizations for the <b>{this.tableName}</b> table are listed below.
        You can render these visualizations in new tabs by asking the chatbot to draw them (For Eg: <i>"draw a bar chart"</i>).
      </Typography>
      <div className="m-2">
        {
          Object.keys(this.suggestionData).map(
            (vizName, i) => (
              <Accordion key={i} defaultExpanded={i === 0}>
                <AccordionSummary expandIcon={<ExpandMoreIcon />} id={this.nodeId + "-" + vizName}>
                  <Typography varient="h5">{vizName}</Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <List>
                    {
                      Object.keys(this.suggestionData[`${vizName}`]).map(
                        (param, j) => (
                          <ListItem key={j}>
                            <ListItemText primary={param} secondary={this.suggestionData[`${vizName}`][`${param}`]} />
                          </ListItem>
                        )
                      )
                    }
                  </List>
                </AccordionDetails>
              </Accordion>
            )
          )
        }
      </div>
    </>
  }
}