import * as d3 from "d3";
import React, { Component } from "react";

const data= {
    xAxisLabel: "X axis label",
    yAxisLabel: "Y axis label",
    items: [{
        x: "label1",
        y: 453
    }, 
    {
      x: "label2",
      y: 693
    }, 
    {
      x: "label3",
      y: 264
    }, 
    {
      x: "label4",
      y: 852
    }, 
    {
      x: "label5",
      y: 726
    },{
        x: "label6",
        y: 900
    }]
  };
const height = 500;
const width = 500;
const  margin = ({top: 20, right: 0, bottom: 30, left: 40});
class BarChart extends Component{
    state = {
        data,
        height,
        width,
        margin
        }
      
    componentDidMount() {
        this.drawChart();
      }
      drawChart(){
            let svg = d3.create("svg")
                .attr("viewBox", [0, 0, this.state.width, this.state.height]);
            console.log("data",this.state)
            if(this.state.data.items){
                const x = d3.scaleBand()
                    .domain(this.state.data.items.map(d => d.x))
                    .range([this.state.margin.left, this.state.width - this.state.margin.right])
                    .padding(0.1)

                const y = d3.scaleLinear()
                    .domain([0, d3.max(this.state.data.items, d => d.value)]).nice()
                    .range([this.state.height - this.state.margin.bottom, this.state.margin.top])

                const bar = svg.append("g")
                    .attr("fill", "steelblue")
                    .selectAll("rect")
                    .data(this.state.data.items)
                    .join("rect")
                    .style("mix-blend-mode", "multiply")
                    .attr("x", d => x(d.x))
                    .attr("y", d => y(d.y))
                    .attr("height", d => y(0) - y(d.y))
                    .attr("width", x.bandwidth());

                const xAxis = g => g
                    .attr("transform", `translate(0,${this.state.height - this.state.margin.bottom})`)
                    .call(d3.axisBottom(x).tickSizeOuter(0))

                const yAxis = g => g
                    .attr("transform", `translate(${this.state.margin.left},0)`)
                    .call(d3.axisLeft(y))
                    .call(g => g.select(".domain").remove())

            
                // d3 = require("d3@6")
                svg = d3.select("chart")
                    .attr("viewBox", [0, 0, this.state.width, this.state.height]);
                
    
    
                const gx = svg.append("g")
                    .call(xAxis);
                // eslint-disable-next-line
                const gy = svg.append("g")
                    .call(yAxis);
                if (window.UndefinedVariable){
                    return Object.assign(svg.node(), {
                        update(order) {
                        x.domain(this.state.data.items.sort(order).map(d => d.x));

                        const t = svg.transition()
                            .duration(750);

                        bar.data(this.state.data.items, d => d.x)
                            .order()
                            .transition(t)
                            .delay((d, i) => i * 20)
                            .attr("x", d => x(d.x));

                        gx.transition(t)
                            .call(xAxis)
                            .selectAll(".tick")
                            .delay((d, i) => i * 20);
                        }
                    });
                }
                }
            }
render(){
    return  <div id={"root"}>
               Graph should be displayed here!!
                <svg/>
             </div>
  }
}
export default BarChart;