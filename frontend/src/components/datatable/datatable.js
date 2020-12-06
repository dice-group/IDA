import React, { useState, useEffect, Component } from "react";
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

export default class CustomizedTables extends Component {
  tableData = null;
  tableId = "";
  keysName = [];
  seletedItem = null;
  emptyRows = 0;
  noPagination;

  constructor(props) {
    super(props);
    this.state = {
      page: 0,
      rowsPerPage: props.noPagination ? 0 : 5,
      rowsPerPageList: [],
    };
    this.noPagination = props.noPagination;
    this.tableData = props.data;
    this.tableId = props.nodeId;
    this.keysName = props.columns.map((col) => {
      return {
        "key": col.colAttr,
        "label": col.colName
      };
    });
    this.seletedItem = this.tableData;
    this.emptyRows = this.state.rowsPerPage - Math.min(this.state.rowsPerPage, this.seletedItem.length - this.state.page * this.state.rowsPerPage);
  }

  componentDidMount() {
    if (this.tableId && document.getElementById(this.tableId)) {
      const rowHeight = document.getElementById(this.tableId).getElementsByClassName("ida-table-row")[0].offsetHeight;
      if (rowHeight) {
        const defaultRowsPerPage = Math.floor((window.innerHeight * 0.65) / rowHeight);
        this.setState({
          rowsPerPage: defaultRowsPerPage,
          rowsPerPageList: [defaultRowsPerPage, defaultRowsPerPage * 2, defaultRowsPerPage * 3, defaultRowsPerPage * 4, defaultRowsPerPage * 5]
        });
      }
    }
  }

  handleChangePage(event, newPage) {
    this.setState({
      page: newPage
    });
  };

  handleChangeRowsPerPage = (event) => {
    this.setState({
      rowsPerPage: parseInt(event.target.value, 10)
    });
  };

  render() {
    return (
      <TableContainer component={Paper}>
        <Table aria-label="ida table" id={this.tableId}>
          <TableHead >
            <TableRow >
              {this.keysName.map((row, index) => (
                <TableCell align="left" key={index}>{row["label"]}</TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {(this.state.rowsPerPage > 0
              ? this.seletedItem.slice(this.state.page * this.state.rowsPerPage, this.state.page * this.state.rowsPerPage + this.state.rowsPerPage)
              : this.seletedItem
            ).map((row, index) => (
              <TableRow key={index} component="tr" className="ida-table-row">
                {this.keysName.map((colName, index) => (
                  <TableCell align="left" component="th" scope="row" key={index} >{row[colName["key"]]}</TableCell>
                ))}
              </TableRow>
            ))}

            {this.emptyRows > 0 && (
              <TableRow style={{ height: 50 * this.emptyRows }}>
                <TableCell colSpan={6} />
              </TableRow>
            )}
          </TableBody>
          {
            this.noPagination ? null : <TableFooter>
              <TableRow>
                <TablePagination
                  rowsPerPageOptions={this.state.rowsPerPageList}
                  colSpan={this.keysName.length}
                  count={this.seletedItem.length}
                  rowsPerPage={this.state.rowsPerPage}
                  page={this.state.page}
                  SelectProps={{
                    inputProps: { "aria-label": "rows per page" },
                    native: true,
                  }}
                  onChangePage={this.handleChangePage.bind(this)}
                  onChangeRowsPerPage={this.handleChangeRowsPerPage.bind(this)}
                  ActionsComponent={TablePaginationActions}
                />
              </TableRow>
            </TableFooter>
          }
        </Table>
      </TableContainer>
    );
  }
}
