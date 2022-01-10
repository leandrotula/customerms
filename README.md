# AgileMonkeys: customerms

Release 0.0.1:
<table border="1">
    <tr>
        <td colspan="2" align="center"><b>Dependencies</b></td>
    </tr>
    <tr>
        <td><b>Framework</b></td>
        <td><b>Version</b></td>
    </tr>
    <tr>
        <td>Spring boot</td>
        <td><b>2.6.2</b></td>
    </tr>
<tr>
        <td>Spring Security</td>
        <td><b>2.6.2</b></td>
    </tr>
<tr>
        <td>Maven</td>
        <td><b>3.6.3</b></td>
    </tr>
<tr>
        <td>Mysql database</td>
        <td><b>8</b></td>
    </tr>
<tr>
        <td>Docker client version</td>
        <td><b>20.10.7</b></td>
    </tr>
<tr>
        <td>Custom Aws Account</td>
        <td><b>IAM user agilemonkeys</b></td>
    </tr>
</table>

- Others
    - This project contains a Dockerfile and a docker-compose file
- Customer API
    - Save Customer will require a multipartFile and a customer body. The multipart body is created using a request param.
    - An error is throw if any of the required fields is missing.
    - It also includes a getAll api, which is paginated, by default is using page number 0 and size of page of 10. It also includes de option of sort by a field.
- Authentication and Authorization
    - A user must be authenticated. Once you successfully login, you will get an access token and a refresh token.
    - This token has 10 min and 30 min of duration.
    - In order to get this token, we should invoke the /v1/api/login?username=username&password=password.
    - This user will have a username, password and a Role.
    - So far this ms is managing ROLE_ADMIN and ROLE_USER.
- MS API
    - customer ms contains a login api, customer api, and user api.
    - login api must be executed for a existent user
    - customer api will be executed for both, ROLE_ADMIN and USER_ADMIN
    - user api will only be executed for a ROLE_ADMIN user. If other authenticated user tries to execute this api, a 403 forhibben should appears.

- customerms Docker setup
    - This project contains a Dockerfile and a docker-compose file with the objective to have the ms up and running.
    - First we must execute mvn clean install to get the latest version of the jar file.
    - After that, we must execute docker-compose build
    - Once we have all this ready, we must execute docker-compose up or docker-compose up -d

- AWS Elastic Beanstalk deployment
  - For this particular test, i use my own aws account, creating a new iam user with admin privileges (not user root).
  - By using this account, i created a S3 bucket, which is gonna be serving as a file repository of images. This is because, as part of the save customer api, we have the option
    of uploading a new image. This image should be present in that bucket. The bucket's name is customerstoragetest.
  - Also, we have a RDS database present. This is a mysql database created in aws.
  - I used Elastic Beanstalk to create ELB, EC2 instances and security groups for me. Inside that service, i passed some environment variables. You will see this in application.properties files.
  - Unfortunately this is not part of a aws code pipeline, so i will deploy the latest version manually.
  - Just to keep in mind the url of the deployed customer ms is <b>Customerms-env.eba-4swe4yuq.us-east-2.elasticbeanstalk.com</b>

- Authentication authorization flow
- first we have to execute the /v1/api/login?username=user&password=password (Params). This has to be a preexistent user with a given password and a role.
- This api will return an access_token and a refresh token.
- Once que have this token, we can execute any other api, by adding an authorization header of 'Bearer ' type.
- This project is gonna contain a postman collection just to help executing this apis.
- By using this collection, if we want to use aws deployed version we just only have to use the given url. If we want to execute the customer ms using docker or the ide, we should
  use something like http://{{IP}}:{{PORT}}

- Database Management
  - Unfortunately, i could not add liquidbase to manage database versioning. Instead, i use ddl hibernate, in update mode. For local test, we can use create or create-drop
  - During the startup of the app, i create a user (admin/test), just to help testing this ms.
  - I could not add model mapper or mapstruct to avoid doing this work manually.

