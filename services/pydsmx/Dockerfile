FROM python:3.7.2-stretch

WORKDIR /app
COPY pydsmx /app

RUN pip install -r requirements.txt
RUN mkdir -p temp-uploads
RUN mkdir -p datasets

CMD ["uwsgi", "app.ini"]
