import React from "react";
import axios from "axios";
import "./chatbotcomp.css";
import { IDA_CONSTANTS } from "../constants";
import idaChatbotActionHandler from "../action-handler";
import IDALinearProgress from "../progress/progress";
import { Grid } from "@material-ui/core";
import CloseIcon from "@material-ui/icons/Close";

export default class ChatApp extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            title: "IDA chatbot",
            iterator: -1,
            hideProgress: true,
            hideBot: false,
            messages: [{
                sender: "them",
                type: "text",
                key: 1,
                message: "Hello, Welcome to IDA. How may I help you?",
				timestamp: Date.now()

            }],
            changeCSS: {},
            selectClick: [],
            pyld: [],
            action: "1004",
            msg: [],
            closeImg: { cursor: "pointer", float: "right", marginTop: "5px", width: "40px", height: "40px", display: "inline-flex" }
        };
    }


    showMessage(text, time) {
        this.setState({
            messages: [...this.state.messages, {
                sender: "them",
                type: "text",
                key: Math.random(),
                message: text,
				timestamp: time
            }]
        });
    }

    componentDidUpdate(_prevProps, _prevState) {
        const msgs = document.getElementById("chat-area-msgs");
        msgs.scrollTop = msgs.scrollHeight;
        if (this.props.isChatbotOpen) {
            document.getElementById("chat-input").focus();
        }
    }

    messageSend = (e) => {
        let msgs = [...this.state.messages];
        const userMsgs = this.state.messages.filter((v) => v.sender === "user");

        /**
         * Section to manage new message from the user
         */
        if (e.keyCode === 13 && e.target.value.trim() !== "") {
            let msg = {
                sender: "user",
                message: e.target.value,
                key: Math.random(),
                timestamp: Date.now(),
                senderName: "user",
                activeDS: this.props.activeDS,
                activeTable: this.props.activeTable,
                activeTableData: this.props.activeTableData,
                temporaryData: !!this.props.activeTableData
            };
            msgs = [...msgs, msg];
            this.setState({
                messages: msgs,
                hideProgress: false,
                iterator: msgs.filter((v) => v.sender === "user").length - 1
            });
            e.target.value = "";
            this.processMessage(msg);
        }
        this.state.iterator !== -1 && this.msgIterator(e, userMsgs);
    }

    processMessage = (msg) => {
        axios.post(IDA_CONSTANTS.API_BASE + "/chatmessage", msg, { withCredentials: true, },)
            .then((response) => {
                this.showMessage(response.data.message, response.data.timestamp);
                const actionCode = response.data.uiAction;
                const payload = response.data.payload;
                idaChatbotActionHandler(this.props, actionCode, payload);
            })
            .catch((err) => {
                if (err.response && err.response.status && err.response.status === IDA_CONSTANTS.GATEWAY_TIMEOUT_STATUS) {
                    this.showMessage(IDA_CONSTANTS.TIMEOUT_MESSAGE, Date.now());
                } else {
                    this.showMessage(IDA_CONSTANTS.ERROR_MESSAGE, Date.now());
                }
            }).finally(() => {
                this.setState({
                    hideProgress: true
                });
            });
    }

    msgIterator = (e, userMsgs) => {
        let target = e.target;
        /***
         * Section to manage mesaages iteration
         */
        // only update and iterate values if iterator has been updated i.e. user has send atleast one message
        if (e.keyCode === 38) {
            // up arrow key
            this.setState({
                iterator: this.state.iterator > 0 ? this.state.iterator - 1 : this.state.iterator,
            });
            target.value = userMsgs[this.state.iterator].message;
        } else if (e.keyCode === 40) {
            // down arrow key
            const iter = userMsgs.length - 1 > this.state.iterator ? this.state.iterator + 1 : this.state.iterator;
            this.setState({
                iterator: iter
            }, () => {
                target.value = userMsgs[this.state.iterator].message;
            });
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
                                        );
                                    } else {
                                        return (
                                            <div className="agent" key={Math.random()}>
												<div>
                                                	<div className="msg" key={Math.random()} dangerouslySetInnerHTML={{ __html: val.message }} />
                                                	<div className="time">{new Date(val.timestamp).toLocaleTimeString()}</div>
												</div>
                                                <div className="agent-pic" key={Math.random()} />
                                            </div>
                                        );
                                    }
                                })
                            }
                        </div>
                        <div className="chat-area-input" >
                            <IDALinearProgress hide={this.state.hideProgress} />
                            <input type="text" id="chat-input" placeholder="Enter your message .." onKeyUp={this.messageSend} autoFocus={true} />
                        </div>
                    </div>
                </div>
                {/* </Draggable> */}
            </div>

        );
    }
}

