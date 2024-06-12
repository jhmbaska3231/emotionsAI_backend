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

// zip files for lambda function
mkdir my-lambda-function
cd my-lambda-function
npm init -y // Run this once at the start to create a package.json file
npm install // Install necessary dependencies (if any)
npm install jsonwebtoken
zip -r my-lambda-function.zip lambdaConfig.js package.json package-lock.json node_modules
zip -r my-lambda-function.zip .

bar-chart dummy values are found under the main method.
