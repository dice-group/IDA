import * as d3 from 'd3';


export default function tooltip(){
    svg.append("g")
      .selectAll("rect")
      .data(this.graphData.items)
      .enter()
      .append("rect")
      .attr("x", (d) => scaleX(d.x))
      .attr("y", (d) => scaleY(d.y))
      .attr("tooltip-text", (d) => d.y)
      .attr("width", scaleX.bandwidth())
      .attr("height", (d, i) => scaleY(0) - scaleY(d.y))
      .attr("fill", "#4f8bff")
      .attr("class", "tooltip")
      .on("mouseover", function (d) {
        div.transition()
          .duration(200)
          .style("opacity", .9);
        div.html(d.currentTarget.getAttribute("tooltip-text"))
          .style("left", (d.pageX) + "px")
          .style("top", (d.pageY - 28) + "px");
      })
      .on("mouseout", function (d) {
        div.transition()
          .duration(500)
          .style("opacity", 0);
      });
    }