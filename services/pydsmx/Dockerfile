FROM python:3.7.2-stretch

WORKDIR /app
COPY pydsmx /app

RUN mkdir -p ./temp-uploads

RUN pip install -r requirements.txt

CMD ["uwsgi", "app.ini"]
