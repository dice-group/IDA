import React, { Fragment, useState } from "react";
import Message from "./Message";
import Progress from "./Progress";
import axios from "axios";
import Button from "@material-ui/core/Button";
import { makeStyles } from "@material-ui/core/styles";
import Modal from "@material-ui/core/Modal";
import Backdrop from "@material-ui/core/Backdrop";
import Fade from "@material-ui/core/Fade";

const useStyles = makeStyles((theme) => ({
    modal: {
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
    },
    paper: {
      backgroundColor: theme.palette.background.paper,
      border: "2px solid #000",
      boxShadow: theme.shadows[5],
      padding: theme.spacing(2, 4, 3),
    },
    root: {
        background: "linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)",
        border: 0,
        borderRadius: 3,
        boxShadow: "0 3px 5px 2px rgba(255, 105, 135, .3)",
        color: "white",
        height: 48,
        padding: "0 30px",
        alignItems: "center",
        width:"-webkit-fill-available",
        
      },
  }));
const FileUpload = () => {
    const [file, setFile] = useState("");
    const [filename, setFilename] = useState("Choose File");
    const [uploadedFile, setUploadedFile] = useState({});
    const [message, setMessage] = useState("");
    const [uploadPercentage, setUploadPercentage] = useState(0);
    const classes = useStyles();
    const [open, setOpen] = React.useState(false);

  const handleOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

    const onChange = (e) => {
      setFile(e.target.files[0]);
      setFilename(e.target.files[0].name);
    };
  
    const onSubmit = async (e) => {
      e.preventDefault();
      const formData = new FormData();
      formData.append("file", file);
  
      try {
        const res = await axios.post("", formData, {
          headers: {
            "Content-Type": "multipart/form-data"
          },
          onUploadProgress: progressEvent =>{
            setUploadPercentage(
              parseInt(
                Math.round((progressEvent.loaded * 100) / progressEvent.total)
              )
            );
  
            // Clear percentage
            setTimeout(() => setUploadPercentage(0), 10000);
          }
        });
        setMessage("File Uploaded");

      } catch (error) {
        if (error.response.status === 500) {
          setMessage("There was a problem with the server");
        }else {
          setMessage(error.response.data.msg);
        }
      }
    };

  return ( 
        <div>
      <Button type="button" onClick={handleOpen}>
       Click  to Upload
      </Button>

        <Modal
            aria-labelledby="transition-modal-title"
            aria-describedby="transition-modal-description"
            className={classes.modal}
            open={open}
            onClose={handleClose}
            closeAfterTransition
            BackdropComponent={Backdrop}
            BackdropProps={{
            timeout: 500,
            }}
        >
        <Fade in={open}>
        <div className={classes.paper}>
        <Fragment>
            {message ? <Message msg={message} /> : null}
            <form onSubmit={onSubmit}>
                <div className="custom-file mb-4">
                {/* <Button>     */}
                <Button className={classes.root} 
                    type="file"
                    // className="custom-file-input"
                    id="customFile"
                    onChange={onChange}     
               >Choose / Drag and Drop</Button>
                </div>
                <br/>
                <Progress percentage={uploadPercentage} />
                <br/>
                <Button  type="submit"
                value="Upload"
                color="blue"
                align ="center"
                className={classes.root}>
                    Upload
                </Button>
            </form>
            {uploadedFile ? (
                <div className="row mt-5">
                <div className="col-md-6 m-auto">
                    <h3 className="text-center">{uploadedFile.fileName}</h3>
                    <img style={{ width: "100%" }} src={uploadedFile.filePath} alt="" />
                </div>
                </div>
            ) : null}
        </Fragment>
        </div>
        </Fade>
      </Modal>
 
    </div>
  );
};

export default FileUpload;
