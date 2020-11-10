import React, { Component } from 'react';
import { withStyles } from "@material-ui/core/styles";
import * as d3 from 'd3';


const styles = theme => ({
  root: {
    border: "1px solid black", 
    overflow:"hidden"
  },
});

class IDABubbleGraph extends Component {
  margin = {
        top: 20,
        right: 0,
        bottom: 100,
        left: 60
      };
  height = 700 
  width = 1000 
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
    if (data && data.length) {
        const pack = data => d3.pack()
            .size([this.width - 2, this.height - 2])
            .padding(3)
            (d3.hierarchy({ children: data })
            .sum(d => d.size))
        const root = pack(data);
        const svg = d3.select("#" + this.containerId)
            .append("svg")
            .attr("width", this.width)
            .attr("height", this.height)
        const entry = svg.selectAll("g")
            .data(root.leaves())
            .join("g")
            .attr("transform", d => `translate(${d.x + 1},${d.y + 1})`); 
        entry.append("circle")
            .attr("id", (d, i) => "clip" + i)
            .attr("r", d => d.r)
            .attr("fill", d => "#4f8bff");
        entry.append("text")
            .attr("dy", ".2em")
            .style("text-anchor", "middle")
            .text((d) => d.data.label)
            .attr("font-family", "sans-serif")
            .attr("font-size", (d) => d.r / 5)
            .attr("fill", "white");
        entry.append("title")
            .text(d => (d.data.description + ':  ' + d.value));
        console.log(d3)
        const zoom = d3.zoom()
          .scaleExtent([0.1, 10])
          .on('zoom', function(event) { svg.attr('transform', event.transform); });
        svg.call(zoom);

    }
  }
  render() {
    const { classes } = this.props;
        return <div className={classes.root} id={this.containerId} ></div >;

  }

}
export default  withStyles(styles, { withTheme: true })(IDABubbleGraph);