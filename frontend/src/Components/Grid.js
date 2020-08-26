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
  const [item, setItem] = useState();
  const [selectTree, setSelectTree]=useState(0);
  const [ detail, setDetails]=useState([]);
  const [loaded, setLoaded] = useState('false');
  let hiddenFlag = 'divHidden';
  const loadTab = (loaded)=>
  {
    if(loaded === 'true'){
    return <TabsWrappedLabel loaded={loaded} selectedTab={selectedTab} selectTree={selectTree} setSelectedTab ={setSelectedTab} setSelectTree={setSelectTree} item={item} setItem={setItem} />       
  }
}
  return (
    <div className={classes.root}>
      <div className = {hiddenFlag}>
      <Grid container showGrid= {props.showGrid} >
        <Grid item xs={3}>  
          <RecursiveTreeView loaded={loaded} setSelectedTab={setSelectedTab} selectTree={selectTree} setItem={setItem}   detail={detail} />
        </Grid>
        <Grid item xs={9}>
            {loadTab(loaded)}
         </Grid>
        </Grid>
        </div>
          <ChatBot   setDetails={setDetails} selectedTab={selectedTab} setLoaded={setLoaded}/>      
    </div>
  );
}
