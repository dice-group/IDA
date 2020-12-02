import React from "react";
// import ReactDOM from "react-dom";
// import axios from "axios";
import "./chatbotcomp.css";
// import { IDA_CONSTANTS } from "../constants";
// import IDAChatbotActionHandler from "./../action-handler";
export default class ChatApp extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            title: 'IDA chatbot',
            iterator: -1,
            messages: [
                { sender: 'ida', message: 'Hi', key: 1 }
            ],
            inputValue: ''
        }
    }

    componentDidUpdate(_prevProps, _prevState) {
        const msgs = document.getElementById('chat-area-msgs');
        msgs.scrollTop = msgs.scrollHeight;
    }

    messageSend = (e) => {
        let msgs = [...this.state.messages],
            user_msgs = msgs.filter(v => v.sender === 'user');


        /**
         * Section to manage new message from the user
         */
        if (e.keyCode === 13 && e.target.value !== '') {
            let msg = { sender: 'user', message: e.target.value, key: Math.random() }
            msgs = [...msgs, msg];
            this.setState({
                messages: msgs,
                iterator: msgs.filter(v => v.sender === 'user').length - 1
            })
            e.target.value = ''
        }

        /***
         * Section to manage mesaages iteration
         */
        if (this.state.iterator !== -1) {
            // onlu update and iterate values if iterator has been updated i.e. user has send atleast one message
            if (e.keyCode === 38) {
                // up arrow key
                this.setState({
                    iterator: this.state.iterator > 0 ? this.state.iterator - 1 : this.state.iterator,
                    newValue: user_msgs[this.state.iterator].message
                })
            } else if (e.keyCode === 40) {
                // down arrow key
                const iter = user_msgs.length - 1 > this.state.iterator ? this.state.iterator + 1 : this.state.iterator
                this.setState({
                    iterator: iter,
                    newValue: user_msgs[iter].message
                })
            }
        }

    }


    render() {
        return (
            // <div className={this.props.detail.length ? "" : "no-data"}>
            <div className="chatbox" >
                <div className="chatbox-title">{this.state.title}</div>
                <div className="chatbox-chat-area">
                    <div className="chat-area-msgs clearfix" id='chat-area-msgs'>
                        {
                            this.state.messages.map(val => {
                                if (val.sender === 'user') {
                                    return <div className="user">{val.message}</div>
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
                        <input type="text" placeholder="Enter your message .." onKeyUp={this.messageSend} value={this.state.newValue} />
                    </div>
                </div>
            </div>
        )
    }
}

