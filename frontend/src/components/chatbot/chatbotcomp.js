import React from "react";
import axios from "axios";
import "./chatbotcomp.css";
import { IDA_CONSTANTS } from "../constants";
import IDAChatbotActionHandler from "../action-handler";
import IDALinearProgress from "../progress/progress";
import { Grid } from "@material-ui/core";
import CloseIcon from "@material-ui/icons/Close";

export default class ChatApp extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            title: "IDA chatbot",
            iterator: -1,
            hideProgress: true,
            hideBot: false,
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
            closeImg: { cursor: "pointer", float: "right", marginTop: "5px", width: "40px", height: "40px", display: "inline-flex" }
        };
    }


    _sendMessage(text) {
        this.setState({
            messages: [...this.state.messages, {
                sender: text.sender,
                type: "text",
                key: text.key,
                message: text
            }]
        });
    }

    componentDidUpdate(_prevProps, _prevState) {
        const msgs = document.getElementById("chat-area-msgs");
        msgs.scrollTop = msgs.scrollHeight;
    }

    messageSend = (e) => {
        let msgs = [...this.state.messages],
            user_msgs = msgs.filter(v => v.sender === "user");

        /**
         * Section to manage new message from the user
         */
        if (e.keyCode === 13 && e.target.value !== "") {
            let msg = {
                sender: "user",
                message: e.target.value,
                key: Math.random(),
                timestamp: Date.now(),
                senderName: "user",
                activeDS: this.props.activeDS,
                activeTable: this.props.activeTable
            }
            msgs = [...msgs, msg];
            this.setState({
                messages: msgs,
                hideProgress: false,
                iterator: msgs.filter(v => v.sender === "user").length - 1
            });
            e.target.value = ""

            axios.post(IDA_CONSTANTS.API_BASE + "/chatmessage", msg, { withCredentials: true, },)
                .then(response => {
                    this._sendMessage(response.data.message);
                    const actionCode = response.data.uiAction;
                    const payload = response.data.payload;
                    IDAChatbotActionHandler(this.props, actionCode, payload);
                })
                .catch(err => {
                    console.log(err);
                    if (err.response && err.response.status && err.response.status === IDA_CONSTANTS.GATEWAY_TIMEOUT_STATUS) {
                        this._sendMessage(IDA_CONSTANTS.TIMEOUT_MESSAGE);
                    } else {
                        this._sendMessage(IDA_CONSTANTS.ERROR_MESSAGE);
                    }
                }).finally(() => {
                    this.setState({
                        hideProgress: true
                    });
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

    handlebutton = () => {
        this.props.setIsChatbotOpen(!this.props.isChatbotOpen);
    }

    render() {
        return (
            <div className={`chatbox-container ${this.props.detail.length ? "with-data" : "no-data"} ${this.props.isChatbotOpen ? "" : "hidden"}`}>
                {/* <Draggable handle=".chatbox-title"> */}
                <div className="chatbox">
                    <Grid className="chatbox-title" container direction="row" alignItems="center">
                        <Grid item xs={10} className="pl-3">
                            <span className="chat-window-title">
                                {this.state.title}
                            </span>
                        </Grid>
                        <Grid item xs={2}>
                            <CloseIcon onClick={this.handlebutton.bind(this)} className="chatbot-close" />
                        </Grid>
                    </Grid>
                    <div className="chatbox-chat-area">
                        <div className="chat-area-msgs" id="chat-area-msgs">
                            {
                                this.state.messages.map((val, i) => {
                                    if (val.sender === "user") {
                                        return (
                                            <div className="user" key={i}>
                                                <div className="msg" key={Math.random()}>{val.message}</div>
                                                <div className="time">{new Date(val.timestamp).toLocaleTimeString()}</div>
                                            </div>
                                        )
                                    } else {
                                        return (
                                            <div className="agent" key={Math.random()}>
                                                <div className="msg" key={Math.random()} dangerouslySetInnerHTML={{ __html: val.message }} />
                                                <div className="agent-pic" key={Math.random()} />
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
                {/* </Draggable> */}
            </div>

        )
    }
}

