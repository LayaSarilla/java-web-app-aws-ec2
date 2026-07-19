# Java Web Application Deployment on AWS EC2

A REST API built with **Java + Spring Boot**, full **CRUD** functionality,
backed by **MySQL**, designed to be deployed on an **AWS EC2 Linux instance**
and tested with **Postman**.

## Features

- Full CRUD REST API for `Employee` resources
- Input validation (`@Valid`, Bean Validation)
- Centralized exception handling (404 on not found, 400 on validation errors, 409 on duplicate email)
- MySQL persistence via Spring Data JPA / Hibernate
- Ready-made Postman collection (`postman_collection.json`)
- AWS EC2 deployment steps (below), matching a Security-Group + EC2 + MySQL/RDS setup

## Tech Stack

- Java 17
- Spring Boot 3.2 (Web, Data JPA, Validation)
- MySQL 8
- Maven
- AWS EC2, Security Groups, (optionally) RDS

## Project Structure

```
java-web-app-aws-ec2/
├── pom.xml
├── postman_collection.json
├── src/main/java/com/laya/webapp/
│   ├── EmployeeWebappApplication.java   # Main class
│   ├── model/Employee.java              # JPA entity
│   ├── repository/EmployeeRepository.java
│   ├── service/EmployeeService.java     # Business logic
│   ├── controller/EmployeeController.java  # CRUD REST endpoints
│   ├── controller/HealthController.java
│   └── exception/                       # Global error handling
├── src/main/resources/application.properties
└── src/test/java/.../EmployeeWebappApplicationTests.java
```

## API Endpoints

| Method | Endpoint              | Description          |
|--------|-----------------------|-----------------------|
| GET    | `/api/health`         | Health check          |
| POST   | `/api/employees`      | Create an employee    |
| GET    | `/api/employees`      | List all employees    |
| GET    | `/api/employees/{id}` | Get one employee      |
| PUT    | `/api/employees/{id}` | Update an employee    |
| DELETE | `/api/employees/{id}` | Delete an employee    |

### Sample request/response

`POST /api/employees`
```json
{
  "name": "Laya Sarilla",
  "email": "laya@example.com",
  "department": "Engineering",
  "salary": 65000
}
```
Response `201 Created`
```json
{
  "id": 1,
  "name": "Laya Sarilla",
  "email": "laya@example.com",
  "department": "Engineering",
  "salary": 65000.0
}
```

## Run locally

1. Create the database (or let Hibernate auto-create it):
   ```sql
   CREATE DATABASE employee_db;
   ```
2. Update `src/main/resources/application.properties` with your MySQL
   username/password if different from `root` / `Root@1234`.
3. Build and run:
   ```bash
   mvn clean package
   java -jar target/employee-webapp.jar
   ```
4. Test with curl:
   ```bash
   curl -X POST http://localhost:8080/api/employees \
     -H "Content-Type: application/json" \
     -d '{"name":"Laya Sarilla","email":"laya@example.com","department":"Engineering","salary":65000}'

   curl http://localhost:8080/api/employees
   ```
   Or import `postman_collection.json` into Postman.

> **Note on this build:** the code in this repo was written and reviewed
> for correctness, and follows the exact same JDBC/Spring pattern verified
> working in the companion Student Management System project. I was not
> able to run `mvn package` inside this sandboxed environment because it
> has no outbound access to Maven Central (only a small allow-list of
> package registries is reachable here), so I couldn't capture a live
> `curl`/Postman transcript for this one. Running the four commands above
> on your own machine (or directly on the EC2 instance) will build and
> execute it — happy to help debug if anything comes up.

## Deploying on AWS EC2

1. **Launch an EC2 instance** (Amazon Linux 2023 or Ubuntu), open inbound
   port `8080` (and `22` for SSH) in the instance's **Security Group**.
2. **Install Java & MySQL** on the instance:
   ```bash
   sudo yum install -y java-17-amazon-corretto mysql-server   # Amazon Linux
   # or
   sudo apt install -y openjdk-17-jdk mysql-server            # Ubuntu
   ```
3. **Set up the database** (or use AWS RDS instead — see below).
4. **Copy the built jar to EC2**:
   ```bash
   scp -i your-key.pem target/employee-webapp.jar ec2-user@<ec2-public-ip>:~/
   ```
5. **Run it** on the instance:
   ```bash
   java -jar employee-webapp.jar
   ```
6. **Test from your machine**:
   ```bash
   curl http://<ec2-public-ip>:8080/api/health
   ```

### Using AWS RDS instead of local MySQL

Update `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://<rds-endpoint>:3306/employee_db?useSSL=false&serverTimezone=UTC
```
Make sure the RDS security group allows inbound MySQL (3306) from the
EC2 instance's security group.

### Optional: S3 for file storage / IAM roles

For endpoints that handle file uploads, attach an IAM role to the EC2
instance with `s3:PutObject`/`s3:GetObject` permissions on your bucket,
and use the AWS SDK for Java (`software.amazon.awssdk:s3`) to upload from
the controller/service layer.

## Pushing to GitHub

```bash
cd java-web-app-aws-ec2
git init
git add .
git commit -m "Initial commit: Employee REST API (Spring Boot + MySQL) for AWS EC2"
git branch -M main
git remote add origin https://github.com/<your-username>/java-web-app-aws-ec2.git
git push -u origin main
```

## Author

Laya Sarilla
