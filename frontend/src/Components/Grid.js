import React ,{useState} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import ChatBot from './ChatBot';
import RecursiveTreeView from './Treeview';
import TabsWrappedLabel from './Tabs';
// import './chatbot.css';

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  paper: {
    height:"100%",
    padding: theme.spacing(1),
    textAlign: 'center',
    color: theme.palette.text.secondary,
  },
}));

export default function CenteredGrid(props) {
  const classes = useStyles();
  const [selectedTab, setSelectedTab] = useState(0);
  const [showGrid , setShowGrid ] = useState('false');
  const [item, setItem] = useState();
  const [selectTree, setSelectTree]=useState(0);
  const [ detail, setDetails]=useState([]);
  let hiddenFlag = 'divHidden';
  showGrid === 'false' ? hiddenFlag = 'divShow' : hiddenFlag = 'divHidden';

  return (
    <div className={classes.root}>
      <div className = {hiddenFlag}>
      <Grid container showGrid= {props.showGrid} >
        <Grid item xs={3}>  
          <RecursiveTreeView setSelectedTab={setSelectedTab} selectTree={selectTree} setItem={setItem}   detail={detail} />
        </Grid>
        <Grid item xs={9}>
        <TabsWrappedLabel selectedTab={selectedTab} setSelectedTab ={setSelectedTab} setSelectTree={setSelectTree} item={item} setItem={setItem} />        </Grid>
        </Grid>
        </div>
        {/* <Grid item xs={4}> */}
          <ChatBot setShowGrid ={setShowGrid}  setDetails={setDetails} selectedTab={selectedTab}/>
        {/* </Grid> */}
      
    </div>
  );
}
