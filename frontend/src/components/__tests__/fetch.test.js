// __tests__/fetch.test.js
import React from "react";
import { render, fireEvent } from "@testing-library/react";
import Home from "../home";

<<<<<<< HEAD
it('should accept the input', () => {
    const mockMsg = "hello Ida"
    const { debug, getByLabelText } = render(<Home ChatApp={mockMsg} />)
    const Input = getByLabelText(/chat-input/i)
    fireEvent.change(Input, { target: { placeholder: 'a new chat-input' } })
    debug()
})
=======
it("should accept the input", () => {
//     const mockMsg = jest.fn();
	const mockMsg ="Hello Ida";
    const { debug, getByLabelText } = render(<Home ChatApp={mockMsg} />);
    const Input = getByLabelText(/chat-input/i);
    fireEvent.change(Input, { target: { placeholder: "a new chat-input" } });
    debug();
});
>>>>>>> 83e50755da531d4df9ac959473b6c87bd8224f94
