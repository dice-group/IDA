FROM python:3.7.2-stretch

WORKDIR /app
COPY pydsmx /app

RUN pip install -r requirements.txt
CMD ["mkdir", "temp-uploads", "datasets"]

CMD ["uwsgi", "app.ini"]
