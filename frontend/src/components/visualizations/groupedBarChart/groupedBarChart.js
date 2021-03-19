import React, { Component } from "react";
import * as d3 from "d3";
import Grid from "@material-ui/core/Grid";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Chip from "@material-ui/core/Chip";
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
    datalabel = [];
    constructor(props) {
        super();
        this.data = props.data;
        this.containerId = props.nodeId;
        Object.keys(this.data.groupedBarChartData).forEach((k) => {
            let obj = {
                groupLabel: k.length > 12 ? k.substring(0, 7) + "..." : k,
                originalGroupLabel: k
            };
            this.data.groupedBarChartData[`${k}`].forEach((e) => {
                Object.defineProperty(obj, e.x, {
                    value: e.y
                });
            });
            this.graphData.push(obj);
            this.tooltip = document.createElement("div");
            this.tooltip.setAttribute("class", "tooltip");
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
            .domain(this.graphData.map((d) => d.groupLabel))
            .rangeRound([this.margin.left, this.width - this.margin.right])
            .paddingInner(0.1);

        const x1 = d3.scaleBand()
            .domain(keys)
            .rangeRound([0, x0.bandwidth()])
            .padding(0.05);

        const y = d3.scaleLinear()
            .domain([0, d3.max(this.graphData, (d) => d3.max(keys, (key) => d[`${key}`]))]).nice()
            .rangeRound([this.height - this.margin.bottom, this.margin.top]);

        const color = d3.scaleOrdinal(this.data.xAxisLabels, d3.schemeCategory10);

        const group = svg.append("g")
            .attr("name", "group")
            .selectAll("g")
            .data(this.graphData)
            .join("g")
            .attr("transform", (d) => `translate(${x0(d.groupLabel)},0)`);

        group
            .selectAll("rect")
            .data((d) => keys.map((key) => ({ key, groupLabel: d.groupLabel, originalGroupLabel: d.originalGroupLabel, value: d[`${key}`] })))
            .join("rect")
            .attr("x", (d) => x1(d.key))
            .attr("y", (d) => y(d.value))
            .attr("width", x1.bandwidth())
            .attr("height", (d) => y(0) - y(d.value))
            .attr("fill", (d) => this.colorFunction(d.key))
            .attr("tooltip-text", (d) => (d.originalGroupLabel + "\n" + d.key + ": " + d.value))
            .attr("name", (d) => "rect" + (d.key))
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
        const xAxis2 = (g) => (g)
            .attr("transform", `translate(0,${this.height - this.margin.bottom})`)
            .call(d3.axisBottom(x1).tickSizeOuter(0))
            .selectAll("text")
            .attr("x", -15)
            .attr("y", -5)
            .attr("transform", "rotate(-90)")
            .style("text-anchor", "end");

        var xval = 0;
        var yval = 0;
        group._groups[0].map((g) => {
            var barcount = Math.max(g.childNodes.length) - 1;
            xval = g.childNodes[0].attributes[0].value;
            yval = +g.childNodes[${barcount}].attributes[0].value + +15;
        });
		
        group
            .append("g")
            .call(xAxis2)
            .append("line")
            .style("stroke", "steelblue")
            .style("stroke-width", 1.5)
            .attr("x1", xval)
            .attr("y1", 76)
            .attr("x2", yval)
            .attr("y2", 76);

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


        //delhi, kerala etc
        const xAxis = (g) => (g)
            .attr("transform", `translate(0,${this.height - this.margin.bottom})`)
            .call(d3.axisBottom(x0).tickSize(0))
            .selectAll("text")
            .attr("x", 25)
            .attr("y", 80)
            .style("dominant-baseline", "text-before-edge")
            .style("text-anchor", "end");


        const yAxis = (g) => (g)
            .attr("transform", `translate(${this.margin.left},0)`)
            .call(d3.axisLeft(y).tickSizeOuter(0));

        svg.append("g")
            .call(xAxis);

        svg.append("g")
            .call(yAxis);

    }

    render() {
        return <>
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

                                            < ListItemText primary={label} />
                                        </ListItem>
                                    ))
                                }
                            </List>
                        </div>
                    </Grid>
                </Hidden>
            </Grid>
        </>;
    }
}
