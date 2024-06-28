// steps to install apache maven v3.9.6
install apache maven v3.9.6 from website
sudo mv ~/Downloads/apache-maven-3.9.6 /opt/
cd /opt
ls
export M2_HOME=/opt/apache-maven-3.9.6
export PATH=$PATH:$M2_HOME/bin
mvn --version

// build application jar file
mvn clean package

// configure node.js script
mkdir my-lambda-function
cd my-lambda-function
npm init -y
npm install
npm install jsonwebtoken

// zip files for lambda function
zip -r my-lambda-function.zip .

// generate JWT token for new user to use in Postman
node generate_JWT_token.js

// update nginx config each time jar file is uploaded
sudo vim /etc/nginx/conf.d/01_nginx_upload_limit.conf
client_max_body_size 25M;
sudo systemctl restart nginx
