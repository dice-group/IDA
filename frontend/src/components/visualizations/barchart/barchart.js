import React from "react";
import "./barchart.css";

export default function IDABarChart(props) {
    // Use this to draw the bar graph. Later it should be fetched from props
    const data = {
        xAxisLabel: "X axis label",
        yAxisLabel: "Y axis label",
        items: [{
            x: "label1",
            y: 453
        }, {
            x: "label2",
            y: 693
        }, {
            x: "label3",
            y: 264
        }, {
            x: "label4",
            y: 852
        }, {
            x: "label5",
            y: 726
        }]
    };
    return (
        <h3>
            Here comes the bar chart for the data: <br />
            <pre>
                {
                    JSON.stringify(data, null, 4)
                }
            </pre>
        </h3>
    );
}