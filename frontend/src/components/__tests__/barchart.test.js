import React from "react";
import { render } from "@testing-library/react";
import IDABarGraph from "../visualizations/barchart/barchart";
import { MOCK_COMPONENT } from "../mockData";
import IDABubbleChart from "../visualizations/bubblechart/bubblechart";
import IDALineChart from "../visualizations/linechart/linechart";
import "@testing-library/jest-dom";

it("it should pass barchart visualisation inputs", () => {
    const mockData = MOCK_COMPONENT.MOCKDATA;
    const { debug } = render(<IDABarGraph data={mockData} />);
    debug();
});  