// __tests__/fetch.test.js
import React from "react";
import { render, fireEvent } from "@testing-library/react";
import Home from "../home";

it("should accept the input", () => {
    const mockMsg = jest.fn()
    const { debug, getByLabelText } = render(<Home ChatApp={mockMsg} />)
    const Input = getByLabelText(/chat-input/i)
    fireEvent.change(Input, { target: { placeholder: 'a new chat-input' } })
    debug()
})
