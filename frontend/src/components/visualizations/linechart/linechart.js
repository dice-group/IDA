import React, { Component } from "react";
import * as d3 from "d3";
import Grid from "@material-ui/core/Grid";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import Checkbox from "@material-ui/core/Checkbox";

import "./linechart.css";

export default class IDALineChart extends Component {
    margin = {
        top: 20,
        right: 10,
        bottom: 30,
        left: 60
    };
    height = 700;
    width = 1000;
    axisLabelOffset = 30;
    graphData = {};
    containerId = "";
    lines;

    constructor(props) {
        super(props);
        this.graphData = props.data;
        this.containerId = props.nodeId;
        this.data = {
            y: this.graphData.yAxisLabel,
            series: this.graphData.lines,
            dates: this.graphData.xAxisLabels
        };
        this.data.dates = this.data.dates.map(d => new Date(d));
        this.state = {
            selectedIndex: this.data.series.map(l => l.label)
        };
    }

    componentDidMount() {
        this.data && this.drawLineChart();
    }

    colorFunction(d) {
        const self = this;
        const color = d3.scaleOrdinal(self.data.series.map(d => d.label), d3.schemeCategory10)
        return color(d);
    }

    drawLineChart() {
        const self = this;
        d3.select("#" + this.containerId).append("svg")
            .attr("height", this.height)
            .attr("width", this.axisLabelOffset)
            .append("text")
            .attr("class", "y label")
            .attr("text-anchor", "end")
            .attr("y", 4)
            .attr("x", this.width / -4)
            .attr("dy", ".75em")
            .attr("transform", "rotate(-90)")
            .text(this.graphData.yAxisLabel);

        const svg = d3.select("#" + this.containerId)
            .append("svg")
            .attr("width", this.width)
            .attr("height", this.height);

        d3.select("#" + this.containerId).append("svg")
            .attr("height", this.axisLabelOffset)
            .attr("width", this.width)
            .append("text")
            .text(this.graphData.xAxisLabel)
            .attr("text-anchor", "middle")
            .attr("x", this.width / 2)
            .attr("y", 25);

        const y = d3.scaleLinear()
            .domain([0, d3.max(this.data.series, d => d3.max(d.lineValues))]).nice()
            .range([this.height - this.margin.bottom, this.margin.top]);

        const x = d3.scaleUtc()
            .domain(d3.extent(this.data.dates, d => new Date(d)))
            .range([this.margin.left, this.width - this.margin.right]);

        svg.append("g")
            .attr("transform", `translate(0,${this.height - this.margin.bottom})`)
            .call(d3.axisBottom(x).tickSizeOuter(0))
            .selectAll("text");

        svg.append("g")
            .attr("transform", `translate(${this.margin.left},0)`)
            .call(d3.axisLeft(y).tickSizeOuter(0));

        const line = d3.line()
            .defined(d => !isNaN(d))
            .x((d, i) => x(this.data.dates[i]))
            .y(d => y(d));

        const color = d3.scaleOrdinal(self.data.series.map(d => d.label), d3.schemeCategory10);

        const path = svg.append("g")
            .attr("fill", "none")
            .attr("stroke-width", 1.5)
            .attr("stroke-linejoin", "round")
            .attr("stroke-linecap", "round")
            .selectAll("path")
            .data(this.data.series)
            .attr("label", d => d.label)
            .join("path")
            .style("mix-blend-mode", "multiply")
            .attr("stroke", d => this.colorFunction(d.label))
            .attr("d", d => line(d.lineValues));

        self.lines = path;

        const div = d3.select("body").append("div")
            .attr("class", "tooltip")
            .style("opacity", 0);

        const moved = (event) => {
            event.preventDefault();
            const xOffset = document.getElementById(self.containerId).offsetLeft + this.axisLabelOffset;
            const yOffset = document.getElementById(self.containerId).offsetTop + this.axisLabelOffset;
            const pointer = d3.pointer(event, this);
            const xm = x.invert(pointer[0] - xOffset);
            const ym = y.invert(pointer[1] - yOffset);
            const i = d3.bisectCenter(this.data.dates, xm);
            const items = this.data.series.filter(d => self.state.selectedIndex.indexOf(d.label) >= 0);
            if (items.length > 0) {
                const s = d3.least(items, d => Math.abs(d.lineValues[i] - ym));
                path.attr("stroke", d => d === s ? color(d.label) : "#eee").filter(d => d === s).raise();
                dot.attr("transform", `translate(${x(this.data.dates[i])},${y(s.lineValues[i])})`);
                div.style("opacity", .9);
                div.html(s.label + " - " + new Date(self.data.dates[i]).toLocaleDateString() + " - " + s.lineValues[i])
                    .style("left", (event.pageX + 20) + "px")
                    .style("top", (event.pageY - 28) + "px");
            }
        }

        const entered = () => {
            path.style("mix-blend-mode", null)
                .attr("stroke", "#eee");
            dot.attr("display", null);
        }

        const left = (event) => {
            path.style("mix-blend-mode", "multiply")
                .attr("stroke", d => color(d.label));
            dot.attr("display", "none");
            div.style("opacity", 0);
        }

        const hover = (svg, path) => {
            if ("ontouchstart" in document) svg
                .style("-webkit-tap-highlight-color", "transparent")
                .on("touchmove", moved)
                .on("touchstart", entered)
                .on("touchend", left)
            else svg
                .on("mousemove", moved)
                .on("mouseenter", entered)
                .on("mouseleave", left);
        }

        svg.call(hover, path);

        const dot = svg.append("g")
            .attr("display", "none");

        dot.append("circle")
            .attr("r", 2.5);

        dot.append("text")
            .attr("font-family", "sans-serif")
            .attr("font-size", 10)
            .attr("text-anchor", "left")
            .attr("y", -8);
    }

    handleListItemClick = (event, index) => {
        const selectedItems = this.state.selectedIndex;
        const itemIndex = selectedItems.indexOf(index);
        if (itemIndex >= 0) {
            selectedItems.splice(itemIndex, 1);
        } else {
            selectedItems.push(index);
        }
        this.setState({
            selectedIndex: selectedItems
        });
        this.lines.attr("display", d => selectedItems.indexOf(d.label) < 0 ? "none" : "block");
    };

    render() {
        return <>
            <Grid container>
                <Grid item xs={9}>
                    <div className="linechart-container" id={this.containerId}></div>
                </Grid>
                <Grid item xs={3}>
                    <div>
                        <List component="nav" dense={true} aria-label="graph legend" className="line-chart-legend">
                            {
                                this.data.series.map((line) => (
                                    <ListItem key={line.label} button onClick={(event) => this.handleListItemClick(event, line.label)}>
                                        <ListItemAvatar>
                                            <div className="legend-item-icon" style={{ backgroundColor: this.colorFunction(line.label) }}></div>
                                        </ListItemAvatar>
                                        <ListItemText primary={line.label} />
                                        <ListItemSecondaryAction>
                                            <Checkbox
                                                edge="end"
                                                onChange={(event) => this.handleListItemClick(event, line.label)}
                                                checked={this.state.selectedIndex.indexOf(line.label) >= 0}
                                                color="default"
                                            />
                                        </ListItemSecondaryAction>
                                    </ListItem>
                                ))
                            }
                        </List>
                    </div>
                </Grid>
            </Grid>
        </>;
    }
}