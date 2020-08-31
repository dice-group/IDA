import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import { Grid, Card, CardContent, Box } from "@material-ui/core";
import CustomizedTables from "./Datatable";

const useStyles = makeStyles({
  idaCard: {
    marginTop: 15,
    marginBottom: 15
  }
});

export default function SpanningTable(props) {
  const classes = useStyles();
  const tableData = props.data || [];
  const keysName = [{
    "colIndex": 1,
    "colName": "Column Index",
    "colAttr": "colIndex"
  }, {
    "colIndex": 2,
    "colName": "Column Name",
    "colAttr": "colName"
  }, {
    "colIndex": 3,
    "colName": "Column Description",
    "colAttr": "colDesc"
  }, {
    "colIndex": 4,
    "colName": "Column Data Type",
    "colAttr": "colType"
  }];
  tableData.forEach((table) => {
    table.metaData = Object.keys(table).filter((k) => k !== "fileColMd" && k !== "metaData");
  });
  return (
    <>
      {
        tableData.map(
          (table, i) => (
            <Card className={classes.idaCard} key={i}>
              <CardContent>
                {table.metaData.map(
                  (tableMdKey, j) => (
                    <Grid container spacing={3} key={j}>
                      <Grid container item xs={12} sm={6} md={4} lg={2}>
                        {tableMdKey}:
                      </Grid>
                      <Grid container item xs={12} sm={6} md={8} lg={10}>
                        {table[tableMdKey]}
                      </Grid>
                    </Grid>
                  )
                )}
                <Box mt={2} mb={2}>
                  <CustomizedTables data={table.fileColMd} columns={keysName} noPagination={true} />
                </Box>
              </CardContent>
            </Card>
          )
        )
      }
    </>
  );
}
