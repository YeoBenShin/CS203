docker build -t tariffy-frontend .
docker run -p 3000:3000 --env-file .env --rm tariffy-frontend