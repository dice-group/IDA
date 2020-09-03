
import React from 'react';
import * as d3 from 'd3';

class BarChart extends React.Component {
    componentDidMount() {
        this.drawChart();
    }

    drawChart() {
    const data = this.props.data;
    const svg = d3.select("body").append("svg")
        .attr("width", this.props.width)
        .attr("height", this.props.height)
        .style("margin-left", 100)
        .style("margin-top", 100);

    const h = this.props.height;
    console.log(this.props.width)
    console.log(this.props.height)
    svg.selectAll("rect")
        .data(data.items)
        .enter()
        .append("rect")
        .attr("x", (d, i) => i * 75)
        .attr("y", (d, i) => h - 0.5 * d.y)
        .attr("width", 65)
        .attr("height", (d, i) => d.y *1)
        .attr("fill", "green")

    // svg.selectAll("text")
    //     .data(data.items)
    //     .enter()
    //     .append("text")
    //     .text((d) => d.x)
    //     .attr("x", (d, i) => i * 75)
    //     .attr("y", (d, i) => h - (d.y))
    svg.selectAll("text")
        .data(data.items)
        .enter()
        .append("text")
        .text((d) => d.x)
        .attr("x", (d, i) => i * 70)
        .attr("y", (d, i) => h - (10 * d.y) - 3)

    }

    render(){
        return  <div id={"#" + this.props.id}></div>
    }

}

export default BarChart;
