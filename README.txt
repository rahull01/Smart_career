# Smart Career Navigator

### How to run:
1. Import project in IntelliJ as Maven Project
2. Create MySQL DB: `smartcareer`
3. Change DB credentials in `application.properties`
4. Run `SmartCareerNavigatorApplication.java`

API Test:
POST http://localhost:8080/api/users/register

{
  "name": "Rahul",
  "email": "rahul@gmail.com",
  "password": "123456"
}
