import ChatBot from 'react-simple-chatbot';
import React from 'react';
import FileUpload from './newcomponent';
import { ThemeProvider } from 'styled-components';

const theme = {
  background: '#linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)',
  fontFamily: 'Helvetica Neue',
  headerBgColor: '#EF6C00',
  headerFontColor: '#fff',
  headerFontSize: '15px',
  botBubbleColor: '#EF6C00',
  botFontColor: '#fff',
  userBubbleColor: '#fff',
  userFontColor: '#4a4a4a',
};

export default function App(){

  return(
    <ThemeProvider theme={theme}>
    <ChatBot 
      floating="true"
      headerTitle = 'IDA'
      steps={[
        {
          id: 'Greet',
          message: 'Hello, Welcome to IDA',
          trigger: 'intro',
        },
        {
          id: 'intro',
          message: 'I am your service agent Bot, What would you like to do ?',
          trigger : 'option1',
        },
        {
          id: 'option1',
          options: [
            { value: 1, label: ' Know more about chatbot', trigger: 'about' },
            { value: 2, label: ' Loading a dataset', trigger: 'load' },
            { value: 3, label: ' Access the existing dataset', trigger: 'reload' },
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
    />
    </ThemeProvider>
    );
}

