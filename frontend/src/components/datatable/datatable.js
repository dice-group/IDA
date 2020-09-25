import React, { useState , useEffect } from "react";
import PropTypes from "prop-types";
import { useTheme } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import TableFooter from "@material-ui/core/TableFooter";
import TablePagination from "@material-ui/core/TablePagination";
import IconButton from "@material-ui/core/IconButton";
import FirstPageIcon from "@material-ui/icons/FirstPage";
import KeyboardArrowLeft from "@material-ui/icons/KeyboardArrowLeft";
import KeyboardArrowRight from "@material-ui/icons/KeyboardArrowRight";
import LastPageIcon from "@material-ui/icons/LastPage";
import "./datatable.css";

function TablePaginationActions(props) {
  const theme = useTheme();
  const { count, page, rowsPerPage, onChangePage } = props;
  //let page = 5;

  const handleFirstPageButtonClick = (event) => {
    onChangePage(event, 0);
  };

  const handleBackButtonClick = (event) => {
    onChangePage(event, page - 1);
  };

  const handleNextButtonClick = (event) => {
    onChangePage(event, page + 1);
  };

  const handleLastPageButtonClick = (event) => {
    onChangePage(event, Math.max(0, Math.ceil(count / rowsPerPage) - 1));
  };

  return (
    <div className={"pagination-root"}>
      <IconButton
        onClick={handleFirstPageButtonClick}
        disabled={page === 0}
        aria-label="first page"
      >
        {theme.direction === "rtl" ? <LastPageIcon /> : <FirstPageIcon />}
      </IconButton>
      <IconButton onClick={handleBackButtonClick} disabled={page === 0} aria-label="previous page">
        {theme.direction === "rtl" ? <KeyboardArrowRight /> : <KeyboardArrowLeft />}
      </IconButton>
      <IconButton
        onClick={handleNextButtonClick}
        disabled={page >= Math.ceil(count / rowsPerPage) - 1}
        aria-label="next page"
      >
        {theme.direction === "rtl" ? <KeyboardArrowLeft /> : <KeyboardArrowRight />}
      </IconButton>
      <IconButton
        onClick={handleLastPageButtonClick}
        disabled={page >= Math.ceil(count / rowsPerPage) - 1}
        aria-label="last page"
      >
        {theme.direction === "rtl" ? <FirstPageIcon /> : <LastPageIcon />}
      </IconButton>
    </div>
  );
}

TablePaginationActions.propTypes = {
  count: PropTypes.number.isRequired,
  onChangePage: PropTypes.func.isRequired,
  page: PropTypes.number.isRequired,
  rowsPerPage: PropTypes.number.isRequired,
};
export default function CustomizedTables(props) {
  const [page, setPage] = useState(0);
  const tableData = props.data;
  const tableId = props.nodeId
  const keysName = props.columns.map((col) => {
    return {
      "key": col.colAttr,
      "label": col.colName
    };
  });
  let seletedItem = tableData;
  useEffect(()=>{
    if (tableId){
    const rowHeight = document.getElementById(tableId).getElementsByClassName("ida-table-row")[0].offsetHeight;
    const numberOfRows = Math.round((window.innerHeight*0.7)/rowHeight);
    const defaultRowsPerPage = props.noPagination ? 0 : numberOfRows;
    setRowsPerPage(defaultRowsPerPage);
  }
  });

  const [rowsPerPage, setRowsPerPage] = useState(5);
  const emptyRows = rowsPerPage - Math.min(rowsPerPage, seletedItem.length - page * rowsPerPage);
  
  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
  };


  return (
    <TableContainer component={Paper}>
      <Table aria-label="ida table" id={tableId}>
        <TableHead >
          <TableRow >
            {keysName.map((row, index) => (
              <TableCell align="left" key={index}>{row["label"]}</TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {(rowsPerPage > 0
            ? seletedItem.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
            : seletedItem
          ).map((row, index) => (
            <TableRow key={index} component="tr" className="ida-table-row">
              {keysName.map((colName, index) => (
                <TableCell align="left"  component="th" scope="row" key={index} >{row[colName["key"]]}</TableCell>
              ))}
            </TableRow>
          ))}

          {emptyRows > 0 && (
            <TableRow style={{ height: 50 * emptyRows }}>
              <TableCell colSpan={6} />
            </TableRow>
          )}
        </TableBody>
        {
          props.noPagination ? null : <TableFooter>
            <TableRow>
              <TablePagination
                rowsPerPageOptions={[rowsPerPage ,rowsPerPage*2, rowsPerPage*3, { label: "All", value: -1 }]}
                colSpan={keysName.length}
                count={seletedItem.length}
                rowsPerPage={rowsPerPage}
                page={page}
                SelectProps={{
                  inputProps: { "aria-label": "rows per page" },
                  native: true,
                }}
                onChangePage={handleChangePage}
                onChangeRowsPerPage={handleChangeRowsPerPage}
                ActionsComponent={TablePaginationActions}
              />
            </TableRow>
          </TableFooter>
        }
      </Table>
    </TableContainer>

  );
}
