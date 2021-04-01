import json
import os
import uuid
import shutil
import re

from flask import Flask, request
from werkzeug.utils import secure_filename
from flask_cors import CORS, cross_origin
import requests
import pandas as pd

app = Flask(__name__)
cors = CORS(app)

app.config['CORS_HEADERS'] = 'Content-Type'
app.config['TEMP_FOLDER'] = './temp-uploads'
app.config['DS_FOLDER'] = '/Users/maqbool/Documents/ida-ds'
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

					# Pandas tends to classify most of column types as object
					# here we further trying to categorize them into numeric and date
					if d_dt == 'object':
						data_type = "string"
						try:
							# Checking if its numeric
							pd.to_numeric(ds[col_name].str.replace(',', '.'))
							data_type = 'numeric'
						except:
							# It was not numeric column
							try:
								# Checking if its Date
								pd.to_datetime(ds[col_name])
								data_type = 'date'
							except:
								# it is not even date
								pass
					elif d_dt in ['int16', 'int32', 'int64', 'float16', 'float32', 'float64']:
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
			except (pd.errors.ParserError, UnicodeDecodeError) as e:
				print(e)
				shutil.rmtree(dsdirpath)
				status_code = 400
				response = {"message": file_name + " does not contain CSVs! Kindly make sure your files contains csv content"}
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
		udsi = request.json["udsi"]

		metadata = request.json["metadata"]
		dsName = metadata["dsName"].lower().strip()

		if re.search(r"(^[A-Za-z0-9\-_]+$)", dsName):
			resp = requests.post("http://localhost:3030/ds/query", data={
				"query": "SELECT ?subject ?predicate ?object WHERE {   ?subject ?predicate '" + dsName + "' }"})
			isNameUnique = False if len(resp.json()["results"]["bindings"]) > 0 else True

			if isNameUnique:
				resp = requests.post("http://localhost:3030/ds/update", data={
					"update": "PREFIX ab: <https://www.upb.de/ida/datasets/> INSERT DATA { <https://www.upb.de/ida/datasets/" + dsName
							  + ">  ab:names '" + dsName + "' ; ab:paths '/docs/dowloads'. }"})

				if resp.status_code == 200:
					dsdirpath = os.path.join(app.config.get('TEMP_FOLDER'), udsi)
					with open(os.path.join(dsdirpath, 'dsmd.json'), 'w') as  metadata_file:
						metadata_file.write(json.dumps(metadata))

					# Moving dataset folder from temp directory to dataset directory
					os.rename(dsdirpath, os.path.join(app.config.get('DS_FOLDER'), dsName))

					return {"message":  "Successfully uploaded dataset"}, 200
			else:
				return {'message': 'dataset with name ' + dsName + ' already exists!'}, 409
		else:
			return {'message': 'dataset name should be alpha numeric with only dashes and underscore'}, 401


@app.route('/delete', methods=['POST'])
@cross_origin()
def delete():
	if request.method == 'POST':
		udsi = request.json["udsi"]
		dsdirpath = os.path.join(app.config.get('TEMP_FOLDER'), udsi)
		shutil.rmtree(dsdirpath)
		return 'ok', 200
