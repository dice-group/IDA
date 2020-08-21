import React, {Component} from "react";
import axios from "axios";
import {Launcher} from "react-chat-window";



export default class ChatBot extends Component {
 
  constructor(props) {
    super(props);
    this.state = {
      messageList: [],
      changeCSS:{},
      selectClick:[],
    }
  }
  
  _onMessageWasSent(message) {
    this.setState({
      messageList: [...this.state.messageList, message]
    })
    // let changeCSS = {top:"50%" , transform: "translate(-50%, -50%)"}

    var outerscope = this;
    if (message.data.text.length > 0) {
        let obj = {
            "senderId"  : "01",
            "message" :  message.data.text,
            "timestamp" : "",
            "senderName" : "spoorthi"
        };
        // console.log(obj,message.data.text);
         if(message.data.text === "load dataset") {
          this.props.setShowGrid(true);
          console.log("props",this.props.setShowGrid);
        }
        const res = axios.post("http://localhost:8090/chatmessage", obj, {}
        ).then(response => {
            console.log(response);
            //changeCSS = {top:"50%" , right: 0, transform: "translate(-10%, -60%) !important"};
            outerscope._sendMessage(response.data.message);
            // const msg = message.data.text;
                        
      }, function (err) {
            console.log("error");
            console.log(err.status);
      });
      console.log(res)
    }
  }
 
  _sendMessage(text) {
      this.setState({
        messageList: [...this.state.messageList, {
          author: "them",
          type: "text",
          data: { text }
        }]
      })
  }
 
  render() {
    let changeCSS = {top:"50%" , transform: "translate(-50%, -50%)"};

    return (
    <div style={{changeCSS}} >
      <Launcher
        agentProfile={{
          teamName: "IDA-ChatBot",
          imageUrl: "",
          className:{changeCSS}
          
        }}
        onMessageWasSent={this._onMessageWasSent.bind(this)}
        messageList={this.state.messageList}
      />
    </div>
    );
  }
}