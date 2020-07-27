import ChatBot from 'react-simple-chatbot';
import React from 'react';
// import FileUpload from './FileUpload';
import { ThemeProvider } from 'styled-components';
import  { Component } from 'react';
// import PropTypes from 'prop-types';
import SockJsClient from 'react-stomp';

const theme = {
  background: '#linear-gradient(45deg, #a19bc9 30%, #a19bc9 90%)',
  fontFamily: 'Helvetica Neue',
  headerBgColor: '#a19bc9',
  headerFontColor: '#fff',
  headerFontSize: '15px',
  botBubbleColor: '#a19bc9',
  botFontColor: '#fff',
  userBubbleColor: '#fff',
  userFontColor: '#4a4a4a',
};

// const [name, setName] = React.useState('');
// const onChange = event => setName(event.target.name);

export default class App extends Component {
  
    // constructor(props){
    //     super(props);
    //   }
  sendMessage = (steps) => {
    console.log('steps',steps);
    this.clientRef.sendMessage('/ida/msgs', steps);

  }
render(){
    console.log("asd: ", "sfd")
  return(
    <ThemeProvider theme={theme}>
      <SockJsClient url='http://localhost:8090/ida-fb-ws' topics={['/topic/msgs']}      
      onMessage={(steps) => { console.log(steps); }}
      ref={ (client) => { this.clientRef = client }} />
      <ChatBot 
      steps={[
        {
          id: '1',
          message: 'What is your name?',
          trigger: 'name',
        },
        {
          id: 'name',
          user: true,
          trigger:'1'
        },
      ]}/>
    {/* <ChatBot 
      floating="true"
      headerTitle = 'IDA'
      steps={[
        {
          id: 'Greet',
          message: 'Hello',
          trigger: 'intro',
        },
        {
          id: 'intro',
          message: 'Welcome to IDA, How may I assist you?',
          trigger : 'option1',
        },
        {
          id: 'option1',
          
          options: [
            { value: 1, label: ' Know more about chatbot', trigger: 'about' },
            { value: 2, label: ' Loading a dataset', trigger: 'load' },
            { value: 3, label: ' Access the existing dataset',trigger: 'reload' },
          ],
        },
        {
          id: 'about',
          message:'The service Bot will help analyze the dataset, give suggestion and  help users view visualisations',
          trigger :'option1'
        },
        {
          id: 'load',
          message:'Please upload the folder',
          trigger :'em'
        },
        {
          id:'reload',
          message:'Here further we will access the existing dataset',
          trigger:'em'
        },
        {
            id: 'em',
            component: <FileUpload/>,
            asMessage: true,
            trigger: 'option1',

        },

      ]}
    /> */}
    </ThemeProvider>
    );
}
}