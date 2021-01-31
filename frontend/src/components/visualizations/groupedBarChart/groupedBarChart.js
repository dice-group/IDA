import React, { Component } from "react";
import * as d3 from "d3";
import Grid from "@material-ui/core/Grid";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Chip from '@material-ui/core/Chip';
import { Hidden } from "@material-ui/core";

import "./groupedBarChart.css";

export default class IDAGroupedBarGraph extends Component {
    margin = {
        top: 20,
        right: 0,
        bottom: 100,
        left: 100
    };
    height = 700;
    width = 1000;
    data = {}
    graphData = [];
    containerId = "";
    originalGraphData = {};
    tooltip = null;
    colorFunction = (label) => null;

    constructor(props) {
        super();
        this.data = props.data;
        this.containerId = props.nodeId;
        Object.keys(this.data.groupedBarChartData).forEach(k => {
            let obj = {
                groupLabel: k.length > 16 ? k.substring(0, 13) + "..." : k,
                originalGroupLabel: k
            };
            this.data.groupedBarChartData[k].forEach(e => {
                obj[e.x] = e.y;
            });
            this.graphData.push(obj);
            this.tooltip = document.createElement('div');
            this.tooltip.setAttribute('class', 'tooltip');
            document.body.appendChild(this.tooltip);
        });
    }

    componentDidMount() {
        this.colorFunction = d3.scaleOrdinal(this.data.xAxisLabels, d3.schemePaired);
        this.drawGraph();
    }

    drawGraph() {
        this.width = Math.max(this.graphData.length * this.data.xAxisLabels.length * 25, this.width);

        const legendSvg = d3.select("#legend-container").append("svg");

        const svg = d3.select("#" + this.containerId)
            .append("svg")
            .attr("height", this.height)
            .attr("width", this.width);
        const groupKey = "groupLabel";
        const keys = this.data.xAxisLabels;

        const x0 = d3.scaleBand()
            .domain(this.graphData.map(d => d[groupKey]))
            .rangeRound([this.margin.left, this.width - this.margin.right])
            .paddingInner(0.1);

        const x1 = d3.scaleBand()
            .domain(keys)
            .rangeRound([0, x0.bandwidth()])
            .padding(0.05);

        const y = d3.scaleLinear()
            .domain([0, d3.max(this.graphData, d => d3.max(keys, key => d[key]))]).nice()
            .rangeRound([this.height - this.margin.bottom, this.margin.top]);

        const color = d3.scaleOrdinal(this.data.xAxisLabels, d3.schemeCategory10);

        svg.append("g")
            .selectAll("g")
            .data(this.graphData)
            .join("g")
            .attr("transform", d => `translate(${x0(d[groupKey])},0)`)
            .selectAll("rect")
            .data(d => keys.map(key => ({ key, groupLabel: d[groupKey], originalGroupLabel: d["originalGroupLabel"], value: d[key] })))
            .join("rect")
            .attr("x", d => x1(d.key))
            .attr("y", d => y(d.value))
            .attr("width", x1.bandwidth())
            .attr("height", d => y(0) - y(d.value))
            .attr("fill", d => this.colorFunction(d.key))
            .attr("tooltip-text", d => (d.originalGroupLabel + "\n" + d.key + ": " + d.value))
            .on("mouseover", (event) => {
                this.tooltip.style.display = "block";
                this.tooltip.style.position = "absolute";
                this.tooltip.style.top = event.clientY + "px";
                this.tooltip.style.left = event.clientX + "px";
                this.tooltip.style.width = "auto";
                this.tooltip.innerText = event.srcElement.getAttribute("tooltip-text");
            })
            .on("mouseout", () => {
                this.tooltip.style.display = "none";
            });

        svg.append("text")
            .attr("transform", "rotate(-90)")
            .attr("x", 0 - (this.height / 2))
            .attr("y", 10)
            .attr("dy", "1em")
            .style("text-anchor", "middle")
            .text(this.data.yAxisLabel);

        d3.select("#" + this.containerId).append("svg")
            .attr("height", 30)
            .attr("width", this.width)
            .append("text")
            .text(this.data.xAxisLabel)
            .attr("text-anchor", "middle")
            .attr("x", 515)
            .attr("y", 15);

        const xAxis = g => g
            .attr("transform", `translate(0,${this.height - this.margin.bottom})`)
            .call(d3.axisBottom(x0).tickSizeOuter(0))
            .selectAll("text")
            .attr("x", -10)
            .attr("y", -5)
            .attr("transform", "rotate(-90)")
            .style("text-anchor", "end");

        const yAxis = g => g
            .attr("transform", `translate(${this.margin.left},0)`)
            .call(d3.axisLeft(y).tickSizeOuter(0));

        svg.append("g")
            .call(xAxis);

        svg.append("g")
            .call(yAxis);

        const legend = legendSvg => {
            const g = legendSvg
                .attr("transform", `translate(100,10)`)
                .attr("text-anchor", "end")
                .attr("font-family", "sans-serif")
                .attr("font-size", 10)
                .selectAll("g")
                .data(color.domain().slice())
                .join("g")
                .attr("transform", (d, i) => `translate(0,${i * 20})`);

            g.append("rect")
                .attr("x", -19)
                .attr("width", 19)
                .attr("height", 19)
                .attr("fill", color);

            g.append("text")
                .attr("x", -24)
                .attr("y", 9.5)
                .attr("dy", "0.35em")
                .text(d => d);
        };

        /* legendSvg.append("g")
            .call(legend); */
    }

    render() {
        return <>
            {/* <div className="legend-container" id="legend-container">
            </div>
            <div className="grouped-bargraph-container" id={this.containerId}>
            </div> */}
            <Grid container>
                <Hidden mdUp>
                    <Grid item xs={12}>
                        <div className="m-2">
                            {
                                this.data.xAxisLabels.map((label) => (
                                    <Chip
                                        key={label}
                                        size="small"
                                        avatar={<span className="legend-item-sm-icon mr-1" style={{ backgroundColor: this.colorFunction(label) }} />}
                                        label={label}
                                        className="mr-2 mt-2"
                                    />
                                ))
                            }
                        </div>
                    </Grid>
                </Hidden>
                <Grid item md={9} className="tab-container">
                    <div className="grouped-bargraph-container" id={this.containerId}></div>
                </Grid>
                <Hidden mdDown>
                    <Grid item md={3}>
                        <div>
                            <List component="nav" dense={true} aria-label="graph legend" className="grouped-bar-chart-legend">
                                {
                                    this.data.xAxisLabels.map((label) => (
                                        <ListItem key={label}>
                                            <ListItemAvatar>
                                                <div className="legend-item-icon" style={{ backgroundColor: this.colorFunction(label) }}></div>
                                            </ListItemAvatar>
                                            <ListItemText primary={label} />
                                        </ListItem>
                                    ))
                                }
                            </List>
                        </div>
                    </Grid>
                </Hidden>
            </Grid>
        </>
    };
}