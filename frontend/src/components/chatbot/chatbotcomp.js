import React from "react";
import axios from "axios";
import "./chatbotcomp.css";
import { IDA_CONSTANTS } from "../constants";
import IDAChatbotActionHandler from "../action-handler";
import IDALinearProgress from "../progress/progress";
import Draggable from "react-draggable";
import Grid from "@material-ui/core/Grid";
import Box from "@material-ui/core/Box";
import IconButton from "@material-ui/core/IconButton";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";

export default class ChatApp extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            title: 'IDA chatbot',
            iterator: -1,
            hideProgress: true,
            messages: [{
                sender: "them",
                type: "text",
                key: 1,
                message: "Hello, Welcome to IDA. How may I help you?"

            }],
            changeCSS: {},
            selectClick: [],
            pyld: [],
            action: "1004",
            msg: [],

        }
    }
    _sendMessage(text) {
        this.setState({
            messages: [...this.state.messages, {
                sender: text.sender,
                type: "text",
                key: text.key,
                message: text,
            }]
        })
        console.log(this.state.messages)
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
            let msg = {
                sender: 'user',
                message: e.target.value,
                key: Math.random(),
                timestamp: Date.now(),
                senderName: "user",
                activeDS: this.props.activeDS,
                activeTable: this.props.activeTable,
                activeTableData: this.props.activeTableData,
                temporaryData: this.props.activeTableData ? true : false
            }
            msgs = [...msgs, msg];
            this.setState({
                messages: msgs,
                hideProgress: false,
                iterator: msgs.filter(v => v.sender === 'user').length - 1
            });
            e.target.value = ''

            axios.post(IDA_CONSTANTS.API_BASE + "/chatmessage", msg, { withCredentials: true, },)
                .then(response => {
                    this._sendMessage(response.data.message);
                    const actionCode = response.data.uiAction;
                    const payload = response.data.payload;
                    IDAChatbotActionHandler(this.props, actionCode, payload);

                })
                .catch(err => {
                    console.log(err);
                }).finally(() => {
                    this.setState({
                        hideProgress: true
                    })
                });

        }

        /***
         * Section to manage mesaages iteration
         */
        if (this.state.iterator !== -1) {
            // only update and iterate values if iterator has been updated i.e. user has send atleast one message
            if (e.keyCode === 38) {
                // up arrow key
                this.setState({
                    iterator: this.state.iterator > 0 ? this.state.iterator - 1 : this.state.iterator,
                })
                e.target.value = user_msgs[this.state.iterator].message;
            } else if (e.keyCode === 40) {
                // down arrow key
                const iter = user_msgs.length - 1 > this.state.iterator ? this.state.iterator + 1 : this.state.iterator
                this.setState({
                    iterator: iter
                })
                e.target.value = user_msgs[iter].message;
            }
        }
    }

    render() {
        return (
            <div className={this.props.detail.length ? "" : "no-data"}  >
                <Draggable handle=".chatbox-title">
                    <div className="chatbox" >
                        <div className="chatbox-title">{this.state.title}</div>
                        <div className="chatbox-chat-area">
                            <div className="chat-area-msgs clearfix" id='chat-area-msgs'>
                                {
                                    this.state.messages.map(val => {
                                        if (val.sender === 'user') {
                                            return (
                                                <div className="user">
                                                    <div className="msg" key={Math.random()}>{val.message}</div>
                                                    <div className="time">{new Date(val.timestamp).toLocaleTimeString()}</div>
                                                </div>
                                            )
                                        } else {
                                            return (
                                                <div>
                                                    <div className="agent" key={Math.random()}>
                                                        <div className="msg" key={Math.random()} dangerouslySetInnerHTML={{ __html: val.message }} />
                                                        <div className="agent-pic" key={Math.random()} />
                                                    </div>
                                                </div>
                                            )
                                        }
                                    })
                                }
                            </div>

                            <div className="chat-area-input" >
                                <IDALinearProgress hide={this.state.hideProgress} />
                                <input type="text" placeholder="Enter your message .." onKeyUp={this.messageSend} />
                            </div>
                        </div>
                    </div>
                </Draggable>
            </div>

        )
    }
}

