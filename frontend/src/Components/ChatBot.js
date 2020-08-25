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
      pyld:[],
      action:'1004',
      msg: [],
    }
  }
  
  _onMessageWasSent(message,props) {
    this.setState({
      messageList: [...this.state.messageList, message]
    })
    var outerscope = this;
    if (message.data.text.length > 0) {
        let obj = {
            "senderId"  : "01",
            "message" :  message.data.text,
            "timestamp" : "",
            "senderName" : "spoorthi",
            "activeDS" : "",
            "activeTable": ""
        };   
        
        const res = axios.post("http://localhost:8080/chatmessage", obj, {}
        ).then(response => {
            console.log(response);
            //changeCSS = {top:"50%" , right: 0, transform: "translate(-10%, -60%) !important"};
            outerscope._sendMessage(response.data.message);
            this.msg = response.data.uiAction;
            console.log("act",this.msg)
            this.pyld =response.data.payload;
            if( this.msg === 1004) {
              console.log("lets dance")
              // this.props.setShowGrid(true);
              // console.log("props",this.props.setShowGrid);
              this.props.setDetails(this.pyld);
              // console.log("mas",this.pyld)      
            }        
                       
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
 
  render(props) {
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