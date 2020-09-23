import React, { Component } from 'react';
// import  {ReactDOM} from "react-dom";
import * as d3 from "d3";
import IDA_Data from "./demo"


export default class IDABubbleGraph extends Component { 
    componentDidMount() {
        this.drawGraph();
    }
    drawGraph() {
        const data = IDA_Data;
        console.log("Data:",data)

        if (data){
        const pack = data => d3.pack()
            .size([width - 2, height - 2])
            .padding(3)
            (d3.hierarchy({children: data})
            .sum(d => d.value))
        const width = 932
        const height = width
        // const format = d3.format(",d")
        const color = d3.scaleOrdinal(data.map(d => d.group), d3.schemeCategory10)
        const root = pack(data);
        const svg = d3.select("svg")
            .attr("viewBox", [0, 0, width, height])
            .attr("font-size", 10)
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
            // .attr("id", d => (d.clipUid = DOM.uid("clip")).id)
            .attr("id", (d, i) => "clip" + i)
            .append("use")
            .attr("xlink:href", d => d.href);
    
        leaf.append("text")
            .attr("id", (d, i) => "clip" + i)
            .selectAll("tspan")
            .data(d => d.data.name.split(/(?=[A-Z][a-z])|\s+/g))
            .join("tspan")
            .attr("x", 1)
            .attr("y", (i, nodes) => `${i - nodes.length / 2 + 0.8}em`)
            .text(d => d);
    
        // leaf.append("text")
        //     .attr("dy", ".2em")
        //     .style("text-anchor", "middle")
        //     .text(function(d) {
        //         return d.data.Name;
        //     })
        //     .attr("font-family", "sans-serif")
        //     .attr("font-size", function(d){
        //         return d.r/5;
        //     })
        //     .attr("fill", "white");

        // leaf.append("text")
        //     .attr("dy", "1.3em")
        //     .style("text-anchor", "middle")
        //     .text(function(d) {
        //         return d.data.Count;
        //     })
        //     .attr("font-family",  "Gill Sans", "Gill Sans MT")
        //     .attr("font-size", function(d){
        //         return d.r/5;
        //     })
        //     .attr("fill", "white");
        // leaf.append("title")
        //     .text(d => `${d.data.title === undefined ? "" : `${d.data.title}`}${format(d.value)}`);
        }
    }
  render() {

    return <div id ="abc">
             <svg/>
        </div>
  }  

  }