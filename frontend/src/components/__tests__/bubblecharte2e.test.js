import React from 'react'
import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { render, screen } from '@testing-library/react'
import '@testing-library/jest-dom/extend-expect'
import IDABubbleGraph from "../visualizations/bubblechart/bubblechart";
import { MOCK_COMPONENT } from "../mockData";

const server = setupServer(
    rest.get("http://localhost:8080", (req, res, ctx) => {
        return res(ctx.json({ message: 'hello there' }))
    })
)

beforeAll(() => server.listen())
afterEach(() => server.resetHandlers())
afterAll(() => server.close())

test('loads', async () => {
    render(<IDABubbleGraph url="http://localhost:8080/chatmessage" data={MOCK_COMPONENT} />)
    expect(screen.getByRole('dialog'))
})

test('handles server error', async () => {
    server.use(
        rest.get('http://localhost:8080/chatmessage', (req, res, ctx) => {
            return res(ctx.status(500))
        })
    )
    render(<IDABubbleGraph url="http://localhost:8080/chatmessage" data={MOCK_COMPONENT} />)
    expect(screen.getByRole('dialog'))
})