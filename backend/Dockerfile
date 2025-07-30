FROM python:3
WORKDIR /app
COPY requirements.txt ./
RUN pip install -r requirements.txt
COPY . .
EXPOSE 4000
CMD ["flask", "run", "--host=192.168.0.100", "--port=4000"]
