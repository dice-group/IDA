import os
from flask import Flask, request
from werkzeug.utils import secure_filename
from flask_cors import CORS, cross_origin
import pandas as pd
import json
import os
import uuid
import shutil

app = Flask(__name__)
cors = CORS(app)

app.config['CORS_HEADERS'] = 'Content-Type'
app.config['TEMP_FOLDER'] = './temp-uploads'
app.config['MAX_CONTENT_LENGTH'] = 5000000  # Maximum fi


@app.route('/', methods=['POST'])
@cross_origin()
def upload_file():
	if request.method == 'POST':
		response = {}
		status_code = 200

		udsi = uuid.uuid4().hex[:6].upper()  # Uniqye dataset identifier
		dsdirpath = os.path.join(app.config.get('TEMP_FOLDER'), udsi)
		os.mkdir(dsdirpath)

		files = request.files.to_dict()  # convert multidict to dict

		files_meta_data = []

		for file in files:
			file_name = secure_filename(files[file].filename)
			files[file].save(os.path.join(dsdirpath, file_name))

			try:
				ds = pd.read_csv(os.path.join(dsdirpath, file_name))
				cols = [col for col in ds.columns]
				no_rows, no_cols = ds.shape

				file_cols_md = []
				for index, col_name in enumerate(cols):
					d_dt = str(ds[col_name].dtype)  # detected data type
					data_type = "string"
					if d_dt in ['int16', 'int32', 'int64', 'float16', 'float32', 'float64']:
						data_type = "numeric"

					file_cols_md.append(
						{"colIndex": index + 1, "colName": col_name, "colDesc": col_name, "colType": data_type,
						 "colAttr": col_name, "isUnique": pd.Series(ds[col_name]).is_unique})

				files_meta_data.append(
					{
						"fileName": file_name,
						"displayName": file_name,
						"fileDesc": "",
						"rowCount": no_rows,
						"colCount": no_cols,
						"fileColMd": file_cols_md
					}
				)
			except pd.errors.ParserError as e:
				print(e)
				shutil.rmtree(dsdirpath)
				status_code = 400
				response = {"message": "Encountered corrupt file! Kindly make sure your files contains csv content"}
				break

		if status_code == 200:
			meta_data = {
				"dsName": "",
				"dsDesc": "",
				"filesMd": files_meta_data
			}
			response = {"metadata": meta_data, "message": "dataset to uploaded successfully", "udsi": udsi}
			status_code = 200

		return response, status_code


@app.route('/savemetadata', methods=['POST'])
@cross_origin()
def save_metadata():
	if request.method == 'POST':
		metadata = request.json["metadata"]
		udsi = request.json["udsi"]
		dsdirpath = os.path.join(app.config.get('TEMP_FOLDER'), udsi)
		metadata_file = open(os.path.join(dsdirpath, 'dsmd.json'), 'w')
		metadata_file.write(json.dumps(metadata))
		return 'ok', 200


@app.route('/delete', methods=['POST'])
@cross_origin()
def delete():
	if request.method == 'POST':
		udsi = request.json["udsi"]
		dsdirpath = os.path.join(app.config.get('TEMP_FOLDER'), udsi)
		shutil.rmtree(dsdirpath)
		return 'ok', 200
