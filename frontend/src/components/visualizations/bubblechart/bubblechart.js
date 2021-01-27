import React, { Component } from 'react';
import * as d3 from "d3";

import "./bubblechart.css";

export default class IDABubbleGraph extends Component {
  margin = {
    top: 20,
    right: 0,
    bottom: 100,
    left: 60
  };
  height = 700;
  width = 1000;
  graphData = {};
  containerId = "";
  tooltip = null;
  constructor(props) {
    super(props);
    this.containerId = props.nodeId;
    this.graphData = props.data;
    this.tooltip = document.createElement('div');
    this.tooltip.setAttribute('class', 'tooltip');
    document.body.appendChild(this.tooltip);
  }

  componentDidMount() {
    this.graphData && this.graphData.items && this.drawGraph();
    var rects = document.querySelectorAll('[data-foo]');
    console.log(rects)
    rects.forEach(ele => {
      ele.addEventListener("mouseover", (event) => {
        this.tooltip.style.display = "block";
        this.tooltip.style.position = "absolute";
        this.tooltip.style.top = event.clientY + "px";
        this.tooltip.style.left = event.clientX + "px";
        // add the text node to the newly created div
        this.tooltip.innerText = event.srcElement.getAttribute("data-foo");
      });
      ele.addEventListener("mouseout", () => {
        setTimeout(() => {
          this.tooltip.style.display = "none";
        }, 2000)
      });
    });
  }

  drawGraph() {
    const data = this.graphData.items;
    if (data && data.length) {
      const pack = data => d3.pack()
        .size([this.width - 2, this.height - 2])
        .padding(3)
        (d3.hierarchy({ children: data })
          .sum(d => d.size))
      const root = pack(data);
      const svg = d3.select("#" + this.containerId)
        .append("svg")
        .attr("height", this.height)
        .attr("width", "100%");

      // Top level group to facilitate zoom and drag functionality
      const top_group = svg.append("g");

      const entry = top_group.selectAll("g")
        .data(root.leaves())
        .join("g")
        .attr("transform", d => `translate(${d.x + 1},${d.y + 1})`);

      entry.append("circle")
        .attr("id", (d, i) => "clip" + i)
        .attr("r", d => d.r)
        .attr("fill", d => "#4f8bff")
        .attr("data-foo", d => (d.data.description + ':  ' + d.value))

      entry.append("text")
        .attr("dy", ".2em")
        .style("text-anchor", "middle")
        .text((d) => d.data.label)
        .attr("font-family", "sans-serif")
        .attr("font-size", (d) => d.r / 5)
        .attr("fill", "white");

      const zoom = d3.zoom()
        .scaleExtent([0.1, Infinity])
        .on('zoom', (event) => {
          // we want to drag all the bubbles so we put transform on top_group
          top_group.attr('transform', event.transform);
        });
      svg.call(zoom);
    }
  }
  render() {
    return <div className="tab-container">
      <div className="bubblechart-container" role="dialog" id={this.containerId}></div>
    </div>;
  }

}
