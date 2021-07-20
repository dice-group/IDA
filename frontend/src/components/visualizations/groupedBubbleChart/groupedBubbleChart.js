import React, { Component } from "react";
import * as d3 from "d3";

import "./groupedBubbleChart.css";
import uid from "./../../utils/uid";

export default class IDAGroupedBubbleChart extends Component {
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

    constructor(props) {
        super();
        this.data = props.data;
        this.containerId = props.nodeId;
        this.graphData = {
            name: "Total",
            children: []
        };
        Object.keys(this.data.groupedBubbleChartData).forEach((k) => {
            this.graphData.children.push({
                name: k,
                children: this.data.groupedBubbleChartData[`${k}`].map((e) => ({ name: e.label, value: e.size }))
            });
        });
    }

    componentDidMount() {
        this.height = window.innerHeight * 0.77;
        this.drawGraph();
    }

    drawGraph() {
        const pack = (data) => d3.pack()
            .size([this.width - 2, this.height - 2])
            .padding(30)
            (d3.hierarchy(data)
                .sum((d) => d.value)
                .sort((a, b) => b.value - a.value));
        const format = d3.format(",d");
        const color = d3.scaleOrdinal([3, 0], ["#b2df8a", "#4f8bff", "#EFEFEF"]);

        const root = pack(this.graphData);

        const svg = d3.select("#" + this.containerId)
            .append("svg")
            .attr("height", this.height)
            .attr("width", "100%");

        const shadow = uid("shadow");

        svg.append("filter")
            .attr("id", shadow.id)
            .append("feDropShadow")
            .attr("flood-opacity", 0.3)
            .attr("dx", 0)
            .attr("dy", 1);

        const node = svg.selectAll("g")
            .data(d3.group(root.descendants(), (d) => d.height))
            .join("g")
            .attr("filter", shadow)
            .selectAll("g")
            .data((d) => d[1])
            .join("g")
            .attr("transform", (d) => `translate(${d.x + 1},${d.y + 1})`);

        node.append("circle")
            .attr("r", (d) => d.r)
            .attr("fill", (d) => color(d.height));
        node.append("text")
            .attr("dy", (d) => d.children ? (-d.r + (d.r / 3)) : ".2em")
            .style("text-anchor", "middle")
            .text((d) => d.data.name.trim())
            .attr("font-family", "sans-serif")
            .attr("font-size", (d) => Math.min(d.r / 7, 12) + "px")
            .attr("fill", (d) => d.children ? "#000" : "#FFF")
            .style("visibility", (d) => d.parent ? "visible" : "hidden");

        const leaf = node.filter((d) => !d.children);

        leaf.select("circle")
            .attr("id", (d) => (d.leafUid = uid("leaf")).id);

        node.append("title")
            .text((d) => `${d.ancestors().map((d) => d.data.name.trim()).reverse().join("-")}:\n${format(d.value)}`);

        const zoom = d3.zoom()
            .scaleExtent([0.1, Infinity])
            .on("zoom", (event) => {
                svg.attr("transform", event.transform);
            });
        svg.call(zoom);
    }

    render() {
        return <>
            <div className="grouped-bubblechart-tab-container">
                <div className="grouped-bubblechart-container" id={this.containerId}></div>
            </div>
        </>;
    }
}