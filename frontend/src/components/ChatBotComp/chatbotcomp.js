import React from "react";
// import ReactDOM from "react-dom";
// import axios from "axios";
import "./chatbotcomp.css";
// import { IDA_CONSTANTS } from "../constants";
// import IDAChatbotActionHandler from "./../action-handler";
export default class ChatApp extends React.Component {
    constructor(props){
        super(props)
            this.state = {
                title: 'IDA chatbot',
                messages: [
                    { sender: 'user', message: 'Hi', key: 1 },
                    { sender: 'ida', message: 'Hi', key: 1 }
                 ],
            }
}


  
    richTextFormatter(text) {

        text = text.replace(/[&<>"']/g, function ($0) {
            return "&" + { "&": "amp", "<": "lt", ">": "gt", '"': "quot", "'": "#39" }[$0] + ";";
        });

        const richText = text
            .replace(/\*\*(.+?)\*\*/g, '<b>$1</b>')
            .replace(/^###(.*)$/gm, '<h3>$1</h3>')
            .replace(/__(.+?)__/g, '<i>$1</i>')
            .replace(/~~(.+?)~~/g, '<s>$1</s>')
            .replace(
                /(ftp|http|https):\/\/[^ "\n]+/g,
                '<a href="$&" target="_blank">$&</a>'
            )
            .replace(/\n/g, ' <br> ');

        return richText;
    }

    componentDidUpdate(_prevProps, _prevState) {
        const msgs = document.getElementById('chat-area-msgs');
        msgs.scrollTop = msgs.scrollHeight;
    }

    messageSend = (e) => {
        if (e.keyCode === 13 && e.target.value !== '') {
            let msg = { sender: 'user', message: e.target.value, key: Math.random() }
            let msgs = [...this.state.messages, msg]
            this.setState({
                messages: msgs
            })
            e.target.value = ''
        }
    }

    render() {
        return (
            // <div className={this.props.detail.length ? "" : "no-data"}>
            <div className ="chatbox" >
                <div className ="chatbox-title">{this.state.title}</div>
                <div className="chatbox-chat-area">
                    <div className="chat-area-msgs clearfix" id='chat-area-msgs'>
                        {
                            this.state.messages.map(val => {
                                if (val.sender === 'user') {
                                    return <div className="user" key={Math.random()}  dangerouslySetInnerHTML={{
                                        __html: this.richTextFormatter(val.message)   
                                    }}></div>
                                } else {
                                    return (
                                        <div className="agent">
                                            <div className="msg" key={Math.random()}>{val.message}</div>
                                            <div className="agent-pic" key={Math.random()}></div>
                                        </div>
                                    )
                                }
                            })
                        }
                    </div>

                    <div className="chat-area-input">
                        <input type="text" placeholder="Enter your message .." onKeyUp={this.messageSend} />
                    </div>
                </div>
            </div>
        )
    }
}

