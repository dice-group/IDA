import React, { useEffect } from 'react';
import { makeStyles,withStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';

const StyledTableCell = withStyles((theme) => ({
    head: {
      backgroundColor: '#4f8bff', // theme.palette.background.default,
      color: theme.palette.common.white,
    },
    body: {
      fontSize: 12,
    },
  }))(TableCell);
  
  const StyledTableRow = withStyles((theme) => ({
    root: {
      "&:nth-of-type(odd)": {
        backgroundColor: theme.palette.action.hover,
      },
    },
  }))(TableRow);

const useStyles = makeStyles({
  table: {
    minWidth: 700,
  },
});

let columnname0 =[];
let  keysName0=[];
let columnname1 =[];
let  keysName1=[];
let columnname2=[];
let  keysName2=[];
// eslint-disable-next-line
let columnList = [];
let seletedItem0 = [];
let seletedItem1 = [];
let seletedItem2 = [];
let data=[];
export default function SpanningTable(props) {
  const classes = useStyles();
    useEffect(()=>{
      debugger;
        if(props.item !== undefined) {
            data = props.item.data;
            // eslint-disable-next-line
            console.log("mufti",data);
            seletedItem0 = data[0].fileColMd;
            seletedItem1 = data[1].fileColMd;
            seletedItem2 = data[2].fileColMd;
            if (props.selectTree==='root'){
                columnList = data[props.selectTree].fileColMd.map(col => col.colName);
                console.log("selected", columnList);
            }
            columnname0 = data[0].fileColMd[0];
            keysName0 = Object.keys(columnname0);
            columnname1 = data[0].fileColMd[1];
            keysName1 = Object.keys(columnname1);
            columnname2 = data[0].fileColMd[2];
            keysName2 = Object.keys(columnname2);
        }
    })
  return (
    <TableContainer component={Paper}>
      <Table className={classes.table} aria-label="spanning table">
        <TableHead>
          <TableRow>
            {keysName0.map((row) => (
                    <StyledTableCell>{row}</StyledTableCell>
                ))}
          </TableRow>
        </TableHead>
        <TableBody>
       
        
           <TableRow>
          <TableCell colSpan={3}> </TableCell>
          </TableRow>
        
        {seletedItem0.map((row) => (
            <StyledTableRow key={row.colIndex}>
                 {keysName0.map((colName) => (
              <StyledTableCell>{row[colName]}</StyledTableCell>
              ))}
            </StyledTableRow>
          ))}
          <TableRow>
                 <TableCell colSpan={3}> </TableCell>
            </TableRow>
        {seletedItem1.map((row) => (
                    <StyledTableRow key={row.colIndex}>
                        {keysName1.map((colName) => (
                    <StyledTableCell>{row[colName]}</StyledTableCell>
                    ))}
                    </StyledTableRow>
                ))}
                <TableRow>
                      <TableCell colSpan={3}></TableCell>
                </TableRow>  
        {seletedItem2.map((row) => (
                    <StyledTableRow key={row.colIndex}>
                        {keysName2.map((colName) => (
                    <StyledTableCell >{row[colName]}</StyledTableCell>
                    ))}
                    </StyledTableRow>
                ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
