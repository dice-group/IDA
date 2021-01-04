import React from "react";
import { render } from "@testing-library/react";
import { MOCK_COMPONENT } from "../mockData";
import IDABubbleChart from "../visualizations/bubblechart/bubblechart";
import "@testing-library/jest-dom";

it("it should pass visualisation inputs", () => {
    const mockData = MOCK_COMPONENT.MOCKDATA;
    const { debug } = render(<IDABubbleChart data={mockData} />);
    debug();
});  