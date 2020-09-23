import React, { Component } from 'react';
import * as d3 from "d3";


export default class IDABubbleGraph extends Component { 
    margin = {
        top: 20,
        right: 0,
        bottom: 50,
        left: 60
      };
      height = 700;
      width = 1000;
      graphData = {};
      containerId = "";
    constructor(props) {
        super(props);
        this.containerId = props.nodeId;
        this.graphData = props.data;
      }
    
    componentDidMount() {
        this.graphData && this.graphData.items && this.drawGraph();
    }
    drawGraph() {
        const data = this.graphData.items;
        console.log("Data:",data)

        if (data){
        const pack = data => d3.pack()
            .size([this.width - 2, this.height - 2])
            .padding(3)
            (d3.hierarchy({children: data})
            .sum(d => d.size))
        const format = d3.format(",d")
        const color = d3.scaleOrdinal(data.map(d => d.group), d3.schemeCategory10)
        const root = pack(data);
        const svg = d3.select("#" + this.containerId).append("svg")
            .attr("viewBox", [0, 0, this.width, this.height])
            .attr("font-size",28)
            .attr("font-family", "sans-serif")
            .attr("text-anchor", "middle");
    
        const leaf = svg.selectAll("g")
            .data(root.leaves())
            .join("g")
            .attr("transform", d => `translate(${d.x + 1},${d.y + 1})`);
    
        leaf.append("circle")
            .attr("id", (d, i) => "clip" + i)
            .attr("r", d => d.r)
            .attr("fill-opacity", 0.7)
            .attr("fill", d => color(d.data.group));
    
        leaf.append("clipPath")
            .attr("id", (d, i) => "clip" + i)
            .append("use")
            .attr("xlink:href", d => d.href);

    
        leaf.append("text")
            .attr("dy", ".2em")
            .style("text-anchor", "middle")
            .text(function(d) {
                return d.data.label;
                // return d.data.description;
            })
            .attr("font-family", "sans-serif")
            .attr("font-size", function(d){
                return d.r/5;
            })
            .attr("fill", "white");

        leaf.append("title")
            .text(d => `${d.data.description === undefined ? "" : `${d.data.description}`}${format(d.description)}`);
        }
    }
  render() {

    return <div className="bubblechart-container" id={this.containerId
    } ></div >;
  }  

  }