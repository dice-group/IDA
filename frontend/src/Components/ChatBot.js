import React, {Component} from 'react'
import axios from 'axios';
import {Launcher} from 'react-chat-window'
import './chatbot.css';
import CustomizedTables from './Datatable'
import Treeview from './Treeview'

export default class Demo extends Component {
 
  constructor() {
    super();
    this.state = {
      messageList: [],
      changeCSS:{}
    }
  }
 
  _onMessageWasSent(message) {
    this.setState({
      messageList: [...this.state.messageList, message]
    })
    // let changeCSS = {top:'50%' , transform: 'translate(-50%, -50%)'}

    var outerscope = this;
    if (message.data.text.length > 0) {
        let obj = {
            "senderId"  : "01",
            "message" :  message.data.text,
            "timestamp" : "",
            "senderName" : "spoorthi"
        };
        console.log(obj,message.data.text);
        if(message.data.text === "upload dataset") {
        
        }
        const res = axios.post('http://localhost:8090/chatmessage', obj, {}
        ).then(response => {
            console.log('success');
            console.log(response);
            console.log("this is testing");
            //changeCSS = {top:'50%' , right: 0, transform: 'translate(-10%, -60%) !important'};
            outerscope._sendMessage(response.data.message)
            
      }, function (err) {
            console.log('error');
            console.log(err.status);
      });
    }
  }
 
  _sendMessage(text) {
      this.setState({
        messageList: [...this.state.messageList, {
          author: 'them',
          type: 'text',
          data: { text }
        }]
      })
  }
 
  render() {
    let changeCSS = {top:'50%' , transform: 'translate(-50%, -50%)'}

    return (
    <div style={{changeCSS}} >
      <Launcher
        agentProfile={{
          teamName: 'IDA-ChatBot',
          imageUrl: '',
          className:{changeCSS}
          
        }}
        onMessageWasSent={this._onMessageWasSent.bind(this)}
        messageList={this.state.messageList}
      />
    
    <div>
        {/* <Treeview/>
        <CustomizedTables/>  */}
    </div>
    </div>
    )
   
  }
}