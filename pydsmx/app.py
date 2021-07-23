import json
import os
import time
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

app.config['TEMP_FOLDER'] = 'temp-uploads'
app.config['DS_FOLDER'] = 'datasets'
# IDA supports "dd/MM/yyyy", "dd MMM", "MMM YYYY", "dd/MM/yyyy HH:mm:ss", "dd-MMM-yyyy", "MMMM-yyyy", "YYYY"
app.config['DATE_FORMAT'] = 'dd/MM/yyyy'
app.config['MAX_CONTENT_LENGTH'] = 5000000  # Maximum file size 5mb

# http://localhost:8080 for not using Docker
API_URL = os.getenv('API_URL')


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
						except Exception:
							# It was not numeric column
							try:
								# Checking if its Date
								pd.to_datetime(ds[col_name])
								data_type = 'date'
							except Exception:
								# it is not even date
								print("Neither a number nor a date")
					elif d_dt in ['int16', 'int32', 'int64', 'float16', 'float32', 'float64']:
						data_type = "numeric"

					cl = col_name
					if col_name.startswith("Unnamed:"):
						col_name = "column" + str(index + 1)

					col_name = col_name.lower().strip()
					row = {"colIndex": index + 1, "colName": col_name, "colDesc": col_name, "colType": data_type,
						   "colAttr": col_name, "isUnique": pd.Series(ds[cl]).is_unique}

					if data_type == 'date':
						row["dataFormat"] = app.config.get('DATE_FORMAT')

					file_cols_md.append(row)

				file_name = file_name.lower().strip()
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
				response = {
					"message": file_name + " does not contain CSVs! Kindly make sure your files contains csv content"}
				break

		if status_code == 200:
			meta_data = {
				"dsName": "",
				"dsDesc": "",
				"filesMd": files_meta_data
			}
			response = {"metadata": meta_data, "message": "Dataset to uploaded successfully", "udsi": udsi}
			status_code = 200

		return response, status_code


def get_dataset(path):
	data = []
	for file_name in os.listdir(path):
		if file_name.endswith(".csv"):
			file_path = os.path.join(path, file_name)
			file_data = []
			with open(file_path, "r") as file:
				columns = file.readline().strip().split(",")
				line = file.readline().strip()
				while line != "":
					line = line.split(",")
					row_data = {}
					for i in range(len(columns)):
						row_data[columns[i]] = line[i] or ""
					file_data.append(row_data)
					line = file.readline().strip()
				data.append({
					"name": file_name,
					"data": file_data
				})
	return data


@app.route('/savemetadata', methods=['POST'])
@cross_origin()
def save_metadata():
	if request.method == 'POST':
		udsi = request.json["udsi"]

		metadata = request.json["metadata"]
		metadata["dsName"] = metadata["dsName"].lower().strip()
		dsName = metadata["dsName"]

		if re.search(r"(^[A-Za-z0-9\-_]+$)", dsName):

			resp = requests.get(API_URL + "/datasetexist", params={'dsName': dsName})
			isNameUnique = True if resp.text == 'false' else False

			if isNameUnique:
				resp = requests.post(API_URL + "/adddataset", params={'dsName': dsName})
				success = True if resp.text == 'true' else False
				if success:
					dsdirpath = os.path.join(app.config.get('TEMP_FOLDER'), udsi)

					# Extracting entities from Metadata
					entity_columns = {}
					date_columns = {}

					for i in metadata.get('filesMd'):
						for j in i.get('fileColMd'):
							colAttr = j.get('colAttr').lower().strip()
							colName = j.get('colName').lower().strip()

							if not colName:
								colName = colAttr
								j["colName"] = colName

							entity_columns[colAttr] = [colName, colAttr]
							if j.get('colType') == 'date':
								date_columns[colAttr] = [colName, colAttr]

					# Updating entities
					resp_entds = requests.post(API_URL + "/addentities", headers={'Content-type': 'application/json'},
											   json={"entityId": "dataset_name", "entityList": {dsName: [dsName]}})
					resp_entcol = requests.post(API_URL + "/addentities", headers={'Content-type': 'application/json'},
												json={"entityId": "column_name", "entityList": entity_columns})
					resp_entdate = requests.post(API_URL + "/addentities", headers={'Content-type': 'application/json'},
												 json={"entityId": "date_columns", "entityList": date_columns})

					if resp_entds.status_code == 200 and resp_entcol.status_code == 200 and resp_entdate.status_code == 200:
						with open(os.path.join(dsdirpath, 'dsmd.json'), 'w') as metadata_file:
							metadata_file.write(json.dumps(metadata))

						# Moving dataset folder from temp directory to dataset directory
						shutil.move(dsdirpath, os.path.join(app.config.get('DS_FOLDER'), dsName))
						response_data = {
							"errorCode": 0,
							"message": "'" + dsName + "' dataset has been successfully uploaded and is now available for analysis. Please close this modal to proceed",
							"uiAction": 1004,
							"timestamp": time.time(),
							"payload": {
								"activeDS": "",
								"activeTable": "",
								"dsData": get_dataset(os.path.join(app.config.get('DS_FOLDER'), dsName)),
								"dsMd": metadata
							},
							"success": True
						}
						return json.dumps(response_data), 200
					else:
						return {"message": "[Dialogflow] Error occurred while updating entities", "success": False}, 500
			else:
				return {'message': 'dataset with name ' + dsName + ' already exists!', "success": False}, 409
		else:
			return {'message': 'dataset name should be alpha numeric with only dashes and underscore', "success": False}, 401


@app.route('/delete', methods=['POST'])
@cross_origin()
def delete():
	if request.method == 'POST':
		udsi = request.json["udsi"]
		dsdirpath = os.path.join(app.config.get('TEMP_FOLDER'), udsi)
		shutil.rmtree(dsdirpath)
		return 'ok', 200


@app.route('/test', methods=['GET'])
@cross_origin()
def test():
	return "Pydsmx is up and running.", 200


if __name__ == '__main__':
	app.run(debug=False)
