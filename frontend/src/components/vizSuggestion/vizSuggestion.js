import React, { Component } from "react";
import Accordion from "@material-ui/core/Accordion";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import LaunchIcon from "@material-ui/icons/Launch";

import "./vizSuggestion.css";

export default class IDAVisualizationSuggestion extends Component {

  suggestionData = {};
  nodeId = "";
  tableName = "";
  vizInfo = {};

  constructor(props) {
    super(props);
    this.suggestionData = props.data.suggestedParams;
    this.vizInfo = props.data.vizInfo;
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
                  <Typography varient="h5"><b>{vizName}</b></Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <Grid container spacing={3}>
                    <Grid item xs={12}>
                      <Typography varient="h6">
                        {this.vizInfo[`${vizName}`].description}
                      For more information you can go to <a target="_blank" href={this.vizInfo[`${vizName}`].link}>{this.vizInfo[`${vizName}`].linkLabel}<LaunchIcon fontSize="small" /></a>
                      </Typography>
                    </Grid>
                    <Grid container item xs={12}>
                      {
                        Object.keys(this.suggestionData[`${vizName}`]).map(
                          (param, j) => (
                            <Grid item xs={6} md={4} lg={2} key={j}>
                              <Typography varient="h5">{param}:</Typography>
                              <b>{this.suggestionData[`${vizName}`][`${param}`]}</b>
                            </Grid>
                          )
                        )
                      }
                    </Grid>
                  </Grid>
                </AccordionDetails>
              </Accordion>
            )
          )
        }
      </div>
    </>
  }
}